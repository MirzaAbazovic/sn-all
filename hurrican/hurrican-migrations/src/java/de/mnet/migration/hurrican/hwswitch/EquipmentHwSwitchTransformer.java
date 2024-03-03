/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2015
 */
package de.mnet.migration.hurrican.hwswitch;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.migration.common.result.SourceIdList;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;

/**
 * Migration der Switch Kennung vom Port auf den Auftrag. Select Statement darf nur NICHT DLU Ports selektieren!!!
 * <p/>
 * Switch Ermittlung bis Release 18:
 * <ul>
 * <li>wenn Produkt einen Switch konfiguriert hat => diesen verwenden</li>
 * <li>ansonsten wenn EQ-IN der 2-ten Rangierung vorhanden (DLU Port) und diese einen Switch konfiguriert hat => diesen
 * verwenden</li>
 * <li>ansonsten Switch des EQ-IN der ersten Rangierung verwenden</li>
 * </ul>
 * Migrationsziel: Switch Kennung von allen nicht DLU Ports löschen und auf AuftragTechnik schreiben.
 * <br/><br/>
 * Query zur Ermittlung von VOIP-Auftraegen, die nach der Migration weiterhin keine Switch-Kennung besitzen: <br/>
 * <pre>
 *  select at.AUFTRAG_ID, ad.STATUS_ID, ad.PROD_ID from T_AUFTRAG_TECHNIK at
 *    join T_AUFTRAG_DATEN ad on at.AUFTRAG_ID = ad.AUFTRAG_ID and ad.GUELTIG_BIS > sysdate
 *    join T_AUFTRAG_2_TECH_LS als on at.AUFTRAG_ID = als.AUFTRAG_ID and (als.AKTIV_BIS is null or als.AKTIV_BIS > sysdate)
 *    join T_TECH_LEISTUNG tl on als.TECH_LS_ID = tl.ID
 *    join T_ENDSTELLE e on at.AT_2_ES_ID = e.ES_GRUPPE and e.ES_TYP = 'B'
 *  where tl.TYP = 'VOIP' and at.GUELTIG_BIS > sysdate and ad.STATUS_ID not in (3400, 9800, 10000) and at.HW_SWITCH is null
 * </pre>
 *
 *
 */
public class EquipmentHwSwitchTransformer extends HurricanTransformator<EquipmentHwSwitch> {

    private static final Logger LOG = Logger.getLogger(EquipmentHwSwitchTransformer.class);

    static HurricanMessages messages = new HurricanMessages();

    private static final List<Long> VOIP_PRODUKTE = Arrays.asList(
            480L, // "DSL + VoIP"
            580L  // "SIP-Trunk"
    );

    private static Map<Long, HWSwitch> VOIP_PRODUKTE_SWITCH = new HashMap<>();

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService ccLeistungsService;

    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;

    public static void main(String[] args) {
        HurricanMigrationStarter migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args, "de/mnet/migration/hurrican/hwswitch/equipment-hwswitch-migration.xml").finish();
    }

    @Override
    public TransformationResult transform(EquipmentHwSwitch row) {
        if (row.equipmentId != null) {
            return transformEquipmentRow(row);
        }
        else {
            return transformAuftragOhnePortRow(row);
        }
    }

    /**
     * Transform a row with an equipment (and possibly Rangierung and Auftrag)
     *
     * @param row the row
     * @return
     */
    protected TransformationResult transformEquipmentRow(EquipmentHwSwitch row) {
        SourceIdList sourceIdList = new SourceIdList(new SourceTargetId("equipmentId", row.equipmentId));
        messages.prepare(null, sourceIdList);
        LOG.info(String.format("equipmentId=%d start HwSwitch migration", row.equipmentId));
        try {
            if (row.rangierId != null) {
                copyHwSwitch2AuftragDaten(row);
            }
            deleteSwitchFromEquipment(row);
        }
        catch (FindException | StoreException e) {
            LOG.error(String.format("equipmentId=%d migrate HwSwitch FAILED", row.equipmentId), e);
            messages.ERROR.add(e);
        }

        return messages.evaluate(sourceIdList, null);
    }

    /**
     * Transform a row with an Auftrag without assigned Port (but VOIP Leistung).
     *
     * @param row the row
     * @return
     */
    private TransformationResult transformAuftragOhnePortRow(EquipmentHwSwitch row) {
        SourceIdList sourceIdList = new SourceIdList(new SourceTargetId("auftragId", row.auftragId));
        messages.prepare(null, sourceIdList);
        LOG.info(String.format("auftragId=%d start HwSwitch migration", row.auftragId));
        try {
            AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(row.auftragId);
            if (auftragTechnik != null && auftragTechnik.getHwSwitch() == null) {
                // switch nach neuer Logik ziehen (ist ja kein Port da ...)
                HWSwitch hwSwitch = auftragService.calculateSwitch4VoipAuftrag(row.auftragId).getLeft()
                        .calculatedHwSwitch;
                updateSwitchForAuftrag(auftragTechnik, hwSwitch, false, "auftragId= " + row.auftragId);
            }
        }
        catch (FindException e) {
            LOG.error(String.format("auftragId=%d migrate HwSwitch FAILED", row.auftragId), e);
            messages.ERROR.add(e);
        }

        return messages.evaluate(sourceIdList, null);
    }

    /**
     * Switch Kennung wenn notwendig vom Port auf den Auftrag schreiben.
     *
     * @param row
     * @throws FindException
     */
    private void copyHwSwitch2AuftragDaten(EquipmentHwSwitch row) throws FindException {
        for (Endstelle endstelle : endstellenService.findEndstellenWithRangierId(row.rangierId)) {
            if (endstelle.isEndstelleA()) {
                continue; // nothing to do ...
            }
            AuftragTechnik auftragTechnik = auftragService.findAuftragTechnik4ESGruppe(endstelle.getEndstelleGruppeId());
            if (auftragTechnik != null && auftragTechnik.getHwSwitch() == null) {
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragTechnik.getAuftragId());
                if (auftragDaten == null) {
                    continue; // Schrott Daten ...
                }

                // only if Auftrag has VOIP Leistung!
                if(ccLeistungsService.hasVoipLeistungFromNowOn(auftragTechnik.getAuftragId())) {
                    HWSwitch hwSwitch = getHwSwitchFromProdukt(auftragDaten.getProdId());
                    boolean switchFromEquipment = false;
                    if (hwSwitch == null) {
                        hwSwitch = getAdditionalPortAndIfNotAvailableDefault(endstelle).getHwSwitch();
                        switchFromEquipment = true;
                    }
                    updateSwitchForAuftrag(auftragTechnik, hwSwitch, switchFromEquipment, "equipmentId= " + row.equipmentId);
                }
            }
        }
    }

    private void updateSwitchForAuftrag(AuftragTechnik auftragTechnik, HWSwitch hwSwitch, boolean switchFromEquipment, String idWithLabel4Logging) {
        if (hwSwitch != null) {
            String msg = String.format("saveSwitch4Auftrag (from %s) auftragId=%d, switch=%s",
                    (switchFromEquipment ? "Equipment" : "Produkt"),
                    auftragTechnik.getAuftragId(), hwSwitch.getName());
            messages.INFO.add(msg);
            LOG.info(idWithLabel4Logging + " " + msg);
            auftragService.updateSwitchForAuftrag(auftragTechnik.getAuftragId(), hwSwitch);
        }
    }

    /**
     * "Alte Logik" aus Rel. 18.
     *
     * @param prodId
     * @return
     * @throws FindException
     */
    private HWSwitch getHwSwitchFromProdukt(Long prodId) throws FindException {
        if (!VOIP_PRODUKTE.contains(prodId)) {
            return null;
        }
        if (!VOIP_PRODUKTE_SWITCH.containsKey(prodId)) {
            VOIP_PRODUKTE_SWITCH.put(prodId, produktService.findProdukt(prodId).getHwSwitch());
        }
        return VOIP_PRODUKTE_SWITCH.get(prodId);
    }

    /**
     * Kopie der Switch Ermittlung aus Rel. 18 Auftragservice (getAdditionalPortAndIfNotAvailableDefault).
     * Eine der beiden Equipments MUSS das aktuell zu migrierende Equipment sein (typischerweise über
     * Endstelle.rangierId).
     *
     * @param endstelle
     * @return
     * @throws FindException
     */
    private Equipment getAdditionalPortAndIfNotAvailableDefault(Endstelle endstelle) throws FindException {
        Equipment additionalPort = rangierungsService.findEquipment4Endstelle(endstelle, true, false);
        if (additionalPort != null && additionalPort.getHwSwitch() != null) {
            return additionalPort;
        }
        Equipment defaultPort = rangierungsService.findEquipment4Endstelle(endstelle, false, false);
        if (defaultPort == null) {
            throw new FindException(String.format("EQ_IN auf Endstelle B mit Id '%d' nicht gefunden", endstelle.getId()));
        }
        return defaultPort;
    }

    /**
     * switch von Equipment löschen.
     *
     * @param row
     * @throws FindException
     * @throws StoreException
     */
    private void deleteSwitchFromEquipment(EquipmentHwSwitch row) throws FindException, StoreException {
        Equipment equipment = rangierungsService.findEquipment(row.equipmentId);
        if (equipment == null) {
            return; // kommt nur vor wenn (faelschlicherweise) einem Equipment mehrere Rangierungen zugeordnet sind ...
        }
        if (equipment.getHwSwitch() == null) {
            return; // bereits erledigt
        }
        String msg = String.format("delete switch=%s from Equipment", equipment.getHwSwitch().getName());
        messages.INFO.add(msg);
        LOG.info(String.format("equipmentId=%d %s", row.equipmentId, msg));
        equipment.setHwSwitch(null);
        rangierungsService.saveEquipment(equipment);
    }
}
