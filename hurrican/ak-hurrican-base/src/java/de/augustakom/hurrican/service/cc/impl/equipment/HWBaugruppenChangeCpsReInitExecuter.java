/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2010 14:17:09
 */

package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Executer-Klasse, um fuer einen DLU Baugruppen-Schwenk die benoetigten CPS-ReInit-Transaktionen auszufuehren. <br><br>
 * Evtl. auftretende Warnungen / Fehlermeldungen bei der Erstellung der CPS-Tx werden protokolliert und koennen aus dem
 * Executer ermittelt werden.
 *
 *
 */
public class HWBaugruppenChangeCpsReInitExecuter extends AbstractHWBaugruppenChangeDluExecuter {
    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeCpsReInitExecuter.class);

    // Prepared values
    private RangierungsService rangierungsService;
    private HWBaugruppenChangeDlu hwBgChangeDlu;
    private CCAuftragService auftragService;
    private Reference hwBgChangeState;
    private CPSService cpsService;
    private HWService hwService;
    private Long sessionId;

    // Misc
    private AKWarnings warnings = new AKWarnings();


    /**
     * Uebergibt dem Executer die notwendigen Modelle u. Services.
     */
    public void configure(HWBaugruppenChangeDlu hwBgChangeDlu,
            RangierungsService rangierungsService,
            CCAuftragService auftragService,
            Reference hwBgChangeState,
            CPSService cpsService,
            HWService hwService,
            Long sessionId) {
        setRangierungsService(rangierungsService);
        setAuftragService(auftragService);
        setHwBgChangeDlu(hwBgChangeDlu);
        setHwBgChangeState(hwBgChangeState);
        setCpsService(cpsService);
        setHwService(hwService);
        setSessionId(sessionId);
    }

    /**
     * Fuehrt die notwendige CPS-ReInit-Tx fuer den Baugruppen-Schwenk durch.
     */
    @Override
    public void execute() throws StoreException {
        try {
            validateValues();
            List<Equipment> dluEquipments = readEquipments4Dlu(hwBgChangeDlu, hwService, rangierungsService);
            List<AuftragDaten> dluAuftragDaten = findAuftragsDaten4Equipments(dluEquipments);
            List<AuftragDaten> reInitAuftragDaten = getReInitAuftragDaten(dluAuftragDaten);
            doReInit(reInitAuftragDaten);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler im CPS-ReInit Executer: " + e.getMessage(), e);
        }
    }

    void validateValues() throws StoreException {
        StringBuilder message = new StringBuilder();
        message.append("Das CPS-ReInit-Kommando kann nicht durchgeführt werden, da ");

        if (hwBgChangeDlu == null) {
            message.append("die notwendigen DLU Daten fehlen!");
            throw new StoreException(message.toString());
        }
        if ((hwBgChangeDlu.getDluRackOld() == null) || (hwBgChangeDlu.getDluRackOld().getId() == null)) {
            message.append("die notwendige DLU Rack ID fehlt!");
            throw new StoreException(message.toString());
        }
        if (!isCpsReInitActionAllowed()) {
            message.append("der Baugruppen Schwenk entweder (noch) nicht ausgeführt oder bereits abgeschlossen ist!");
            throw new StoreException(message.toString());
        }
    }

    /**
     * Pruefung, ob die angegebene CPS-ReInit-Aktion in dem aktuellen Status des Baugruppen-Schwenks erlaubt ist. Der
     * Schwenk muss ausgeführt sein und darf aber noch nicht abgeschlossen sein.
     */
    boolean isCpsReInitActionAllowed() {
        if ((hwBgChangeState == null)
                || NumberTools.notEqual(hwBgChangeState.getId(),
                HWBaugruppenChange.ChangeState.CHANGE_STATE_EXECUTED.refId())) {
            return false;
        }
        return true;
    }


    /**
     * Ermittelt für jedes Equipment einen eventuell aktiven Auftrag (wenn Auftrag in Kündigung muss Kündigungsdatum in
     * Zukunft liegen).
     *
     * @return die Liste der gültigen AuftragDaten
     */
    List<AuftragDaten> findAuftragsDaten4Equipments(List<Equipment> dluEquipments) {
        if (CollectionTools.isEmpty(dluEquipments)) {
            return null;
        }

        List<AuftragDaten> dluAuftragDaten = new ArrayList<AuftragDaten>();
        for (Equipment equipment : dluEquipments) {
            try {
                List<AuftragDaten> eqAuftragDaten = auftragService.findAuftragDatenByEquipment(equipment.getId());
                if (CollectionTools.isNotEmpty(eqAuftragDaten)) {
                    for (AuftragDaten auftragDaten : eqAuftragDaten) {
                        if (!AuftragStatus.isNotValid(auftragDaten.getStatusId())) {
                            if (auftragDaten.isInBetrieb()) {
                                dluAuftragDaten.add(auftragDaten);
                                break;
                            }
                            else if (auftragDaten.isInKuendigung()
                                    && DateTools.isAfter(auftragDaten.getKuendigung(), new Date())) {
                                dluAuftragDaten.add(auftragDaten);
                                break;
                            }
                        }
                    }
                }
            }
            catch (FindException e) {
                warnings.addAKWarning(this, String.format(
                        "Fehler während der Ermittlung der Auftrags Daten für die Equipment/Port ID %s (HW_EQN: %s) " +
                                equipment.getId(), equipment.getHwEQN()
                ));
            }
        }
        return dluAuftragDaten;
    }

    /**
     * Filtert alle AuftragDaten, für die ein ReInit durchgeführt werden soll. Die Kriterien sind wie folgt: <lu>
     * <li>ReInit pro Billing Auftrag <li>Auftrag/Billing Auftrag besitzt eine erfolgreiche(!) CPS-Tx <lu>
     */
    List<AuftragDaten> getReInitAuftragDaten(List<AuftragDaten> dluAuftragDaten) {
        if (CollectionTools.isEmpty(dluAuftragDaten)) {
            return null;
        }

        List<AuftragDaten> reInitAuftragDaten = new ArrayList<AuftragDaten>();
        Set<Long> dluBillingAuftraege = new HashSet<Long>();
        for (AuftragDaten auftragDaten : dluAuftragDaten) {
            if (auftragDaten.getAuftragNoOrig() == null) {
                StringBuilder message = new StringBuilder();
                message.append("Zu dem technischen Auftrag ")
                        .append(auftragDaten.getAuftragId())
                        .append(" existiert kein Billing Auftrag!");
                warnings.addAKWarning(this, message.toString());
            }
            else {
                try {
                    if (!dluBillingAuftraege.contains(auftragDaten.getAuftragNoOrig())) {
                        List<CPSTransaction> successfulCpsTx = cpsService.findSuccessfulCPSTransaction4TechOrder(auftragDaten.getAuftragId());
                        if (CollectionTools.isNotEmpty(successfulCpsTx)) {
                            reInitAuftragDaten.add(auftragDaten);
                            dluBillingAuftraege.add(auftragDaten.getAuftragNoOrig());
                        }
                    }
                }
                catch (FindException e) {
                    StringBuilder message = new StringBuilder();
                    message.append("Fehler bei der Ermittlung erfolgreicher CPS-Txs für die (Hurrican) Auftrags ID ")
                            .append(auftragDaten.getAuftragId());
                    warnings.addAKWarning(this, message.toString());
                }
            }
        }
        return reInitAuftragDaten;
    }

    /**
     * Führt das ReInit für jeden {@code reInitAuftragDaten} Datensatz aus. Fehler werden protokolliert, es wird nicht
     * abgebrochen.
     */
    void doReInit(List<AuftragDaten> reInitAuftragDaten) {
        if (CollectionTools.isEmpty(reInitAuftragDaten)) {
            warnings.addAKWarning(this, "Es sind keine Ports der DLU mit einem CPS-ReInit-Kommando zu modifizieren.");
            return;
        }

        for (AuftragDaten auftragDaten : reInitAuftragDaten) {
            try {
                CPSTransactionResult cpsTxResult = cpsService.doLazyInit(
                        LazyInitMode.reInit, auftragDaten.getAuftragId(), null, sessionId);

                // TODO neben Ermittlung von cpsTxResult auch noch die CPS-Tx neu laden
                //      und den Status pruefen; Evtl. Fehlermeldungen vom CPS werden sonst
                //      nicht nach aussen signalisiert!

                String warnings = (cpsTxResult.getWarnings() != null) ? cpsTxResult.getWarnings().getWarningsAsText() : null;
                if (StringUtils.isNotBlank(warnings)) {
                    throw new StoreException("Das Erstellen der ReInit Transaktion ist fehlgeschlagen: " + warnings);
                }
            }
            catch (StoreException e) {
                LOGGER.error(e.getMessage(), e);
                StringBuilder message = new StringBuilder();
                message.append("Zu dem technischen Auftrag ")
                        .append(auftragDaten.getAuftragId())
                        .append(" konnte keine ReInit-Transaktion versendet werden! ")
                        .append("Subfehler: ")
                        .append(e.getMessage());
                warnings.addAKWarning(this, message.toString());
            }
        }
    }

    public void setHwBgChangeDlu(HWBaugruppenChangeDlu hwBgChangeDlu) {
        this.hwBgChangeDlu = hwBgChangeDlu;
    }

    public void setCpsService(CPSService cpsService) {
        this.cpsService = cpsService;
    }

    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    public void setHwBgChangeState(Reference hwBgChangeState) {
        this.hwBgChangeState = hwBgChangeState;
    }

    /**
     * Gibt die aufgetretenen Warnungen / Fehlermeldungen zurueck.
     *
     * @return
     */
    public String getWarnings() {
        return warnings.getWarningsAsText();
    }

}
