/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2007 09:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.exmodules.tal.IBoxedWitaConfigService;

/**
 * Command-Klasse, um HVT-Daten zu einer bestimmten Endstelle eines Auftrags zu sammeln und diese in einer HashMap zu
 * speichern.
 *
 *
 */
public abstract class AbstractHvtDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractHvtDatenCommand.class);

    public static final String HVTASB = "HvtAsb";
    public static final String HVTONKZ = "HvtONKZ";
    public static final String HVTSTRASSE = "HvtStrasse";
    public static final String HVTHAUSNR = "HvtHausNr";
    public static final String HVTORT = "HvtOrt";
    public static final String HVTPLZ = "HvtPLZ";
    public static final String CKNAME = "CkName";
    public static final String CKSTRASSE = "CkStrasse";
    public static final String CKPLZ = "CkPlz";
    public static final String CKORT = "CkOrt";
    public static final String CKKUNDENR = "CkKundenNr";
    public static final String CKPORTIERUNGSKENNUNG = "CkPortierungskennung";
    public static final String SCHALTAUFTRAGORT = "SchaltauftragOrt";

    private Long auftragId = null;
    private Map<String, Object> reportMap = null;

    @Resource
    private IBoxedWitaConfigService boxedWitaConfigService;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            reportMap = new HashMap<String, Object>();

            readHvtDaten();
            return reportMap;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * Subtypen müssen festlegen welchen Endstellentyp diese lesen wollen.
     *
     * @return
     */
    protected abstract String getEndstelleTyp();

    /**
     * Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde.
     */
    private void checkValues() throws HurricanServiceCommandException {
        if (StringUtils.isEmpty(getEndstelleTyp())) {
            throw new HurricanServiceCommandException("methode getEndstellenTyp nicht richtig überschrieben");
        }
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("AuftragId wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * Ermittelt die HVT-Daten der Endstellen (A+B) des Auftrags und schreibt diese in das reportView-Objekt
     */
    protected void readHvtDaten() throws HurricanServiceCommandException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);

            // Ermittle RangierId über Endstellen
            List<Endstelle> endstellen = esSrv.findEndstellen4Auftrag(auftragId);
            if (endstellen != null) {
                for (Endstelle endstelle : endstellen) {
                    if (endstelle.isEndstelleOfType(getEndstelleTyp())) {
                        reportMap.putAll(readHvtDaten4Std(endstelle));
                        return;
                    }
                }
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return HVT_DATEN;
    }

    /**
     * Ermittelt die HVT-Daten der zu einem Hvt-Standort
     */
    protected Map<String, Object> readHvtDaten4Std(Endstelle endstelle) throws Exception {
        Long hvtStdId = endstelle.getHvtIdStandort();
        Map<String, Object> map = new HashMap<String, Object>();
        if (hvtStdId == null) {
            return map;
        }
        HVTService hs = getCCService(HVTService.class);
        HVTStandort hvtStandort = hs.findHVTStandort(hvtStdId);
        if (hvtStandort == null) {
            return map;
        }

        // Standort fuer Ermittlung der CarrierKennung ist auf jeden Fall der aktuelle Standort
        // Grund: unterschiedliche Kundennummer bei DTAG zwischen HVT und FTTC (KVZ) moeglich!)
        CarrierKennung ckB = getCarrierKennung(hvtStandort.getCarrierKennungId());
        map.put(getPropName(CKNAME), (ckB != null) ? ckB.getName() : null);
        map.put(getPropName(CKSTRASSE), (ckB != null) ? ckB.getStrasse() : null);
        map.put(getPropName(CKPLZ), (ckB != null) ? ckB.getPlz() : null);
        map.put(getPropName(CKORT), (ckB != null) ? ckB.getOrt() : null);
        map.put(getPropName(CKKUNDENR), (ckB != null) ? ckB.getKundenNr() : null);
        map.put(getPropName(CKPORTIERUNGSKENNUNG), (ckB != null) ? ckB.getPortierungsKennung() : null);

        boolean useKvzAdresse = false;
        if (hvtStandort.isFttc()) {
            // wenn KVZ TAL über WITA aktiv ist, muss die KvzAdresse der DTAG verwendet werden
            // ansonsten die Adresse des übergeordneten HVT
            if (boxedWitaConfigService.fttcKvzBereitstellungAllowed()) {
                useKvzAdresse = true;
            }
            else {
                hvtStandort = hs.findHVTStandort(hvtStandort.getBetriebsraumId());
                if (hvtStandort == null) {
                    throw new FindException(
                            "Für den MFG-/KVZ-Standort des Auftrags muss ein HVT-Standort zugeordnet werden.");
                }
            }
        }

        map.put(getPropName(HVTASB), hvtStandort.getDTAGAsb());
        Integer asb = hvtStandort.getAsb();
        map.put(getPropName(SCHALTAUFTRAGORT), null);
        if (Integer.valueOf(987).equals(asb)) {
            map.put(getPropName(SCHALTAUFTRAGORT), "Unterer Anger");
        }
        else if (Integer.valueOf(988).equals(asb)) {
            map.put(getPropName(SCHALTAUFTRAGORT), "MTZ");
        }

        HVTGruppe hvtg = hs.findHVTGruppeById(hvtStandort.getHvtGruppeId());

        if (useKvzAdresse) {
            RangierungsService rangierungsService = getCCService(RangierungsService.class);
            Equipment eqOut = rangierungsService.findEquipment4Endstelle(endstelle, false, true);
            String kvzNummer = eqOut.getKvzNummer();
            KvzAdresse kvzAdresse = hs.findKvzAdresse(hvtStandort.getId(), kvzNummer);

            map.put(getPropName(HVTSTRASSE), kvzAdresse.getStrasse());
            map.put(getPropName(HVTHAUSNR), kvzAdresse.getHausNr());
            map.put(getPropName(HVTORT), kvzAdresse.getOrt());
            map.put(getPropName(HVTPLZ), kvzAdresse.getPlz());
        }
        else {
            map.put(getPropName(HVTSTRASSE), (hvtg != null) ? hvtg.getStrasse() : null);
            map.put(getPropName(HVTHAUSNR), (hvtg != null) ? hvtg.getHausNr() : null);
            map.put(getPropName(HVTORT), (hvtg != null) ? hvtg.getOrt() : null);
            map.put(getPropName(HVTPLZ), (hvtg != null) ? hvtg.getPlz() : null);
        }

        map.put(getPropName(HVTONKZ), (hvtg != null) ? hvtg.getOnkz() : null);

        return map;
    }

    /* Ermittelt die Carrier-Kennung mit einer bestimmten ID. */
    private CarrierKennung getCarrierKennung(Long ckId) throws Exception {
        CarrierService cs = getCCService(CarrierService.class);
        CarrierKennung ck = cs.findCarrierKennung(ckId);
        return ck;
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetHvtEsDatenCommand.properties";
    }
}
