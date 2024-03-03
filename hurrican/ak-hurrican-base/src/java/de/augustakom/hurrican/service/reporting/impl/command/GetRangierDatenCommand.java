/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2007 09:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse, um Rangierungs-Daten zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetRangierDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetRangierDatenCommand.class);

    public static final String ESEQINVERTEILERA = "EsEqInVerteilerA";
    public static final String ESEQINREIHEA = "EsEqInReiheA";
    public static final String ESEQINBUCHTA = "EsEqInBuchtA";
    public static final String ESEQINLEISTE1A = "EsEqInLeiste1A";
    public static final String ESEQINSTIFT1A = "EsEqInStift1A";
    public static final String ESEQINLEISTE2A = "EsEqInLeiste2A";
    public static final String ESEQINSTIFT2A = "EsEqInStift2A";
    public static final String ESEQINUEVTA = "EsEqInUetvA";
    public static final String ESEQINRANGSCHNITTSTELLEA = "EsEqInRangSchnittstelleA";
    public static final String ESEQOUTVERTEILERA = "EsEqOutVerteilerA";
    public static final String ESEQOUTREIHEA = "EsEqOutReiheA";
    public static final String ESEQOUTBUCHTA = "EsEqOutBuchtA";
    public static final String ESEQOUTLEISTE1A = "EsEqOutLeiste1A";
    public static final String ESEQOUTSTIFT1A = "EsEqOutStift1A";
    public static final String ESEQOUTLEISTE2A = "EsEqOutLeiste2A";
    public static final String ESEQOUTSTIFT2A = "EsEqOutStift2A";
    public static final String ESEQOUTUEVTA = "EsEqOutUetvA";
    public static final String ESEQOUTRANGSCHNITTSTELLEA = "EsEqOutRangSchnittstelleA";
    public static final String ESEQINVERTEILERB = "EsEqInVerteilerB";
    public static final String ESEQINREIHEB = "EsEqInReiheB";
    public static final String ESEQINBUCHTB = "EsEqInBuchtB";
    public static final String ESEQINLEISTE1B = "EsEqInLeiste1B";
    public static final String ESEQINSTIFT1B = "EsEqInStift1B";
    public static final String ESEQINLEISTE2B = "EsEqInLeiste2B";
    public static final String ESEQINSTIFT2B = "EsEqInStift2B";
    public static final String ESEQINUEVTB = "EsEqInUetvB";
    public static final String ESEQINRANGSCHNITTSTELLEB = "EsEqInRangSchnittstelleB";
    public static final String ESEQOUTVERTEILERB = "EsEqOutVerteilerB";
    public static final String ESEQOUTREIHEB = "EsEqOutReiheB";
    public static final String ESEQOUTBUCHTB = "EsEqOutBuchtB";
    public static final String ESEQOUTLEISTE1B = "EsEqOutLeiste1B";
    public static final String ESEQOUTSTIFT1B = "EsEqOutStift1B";
    public static final String ESEQOUTLEISTE2B = "EsEqOutLeiste2B";
    public static final String ESEQOUTSTIFT2B = "EsEqOutStift2B";
    public static final String ESEQOUTUEVTB = "EsEqOutUetvB";
    public static final String ESEQOUTRANGSCHNITTSTELLEB = "EsEqOutRangSchnittstelleB";
    public static final String ZWEIDRAHT = "ZweiDraht";
    public static final String VIERDRAHT = "VierDraht";

    private Long auftragId = null;
    private Map<String, Object> map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<String, Object>();

            readRangierDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return RANGIER_DATEN;
    }

    /* Liest die Rangier-Daten eines Auftrags aus. */
    private void readRangierDaten() throws HurricanServiceCommandException {
        Long esRangIdA = null;
        Long esRangIdB = null;
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            RangierungsService rs = getCCService(RangierungsService.class);

            // Ermittle RangierId über Endstellen
            List<Endstelle> endstellen = esSrv.findEndstellen4Auftrag(auftragId);
            if (endstellen != null) {
                for (Endstelle es : endstellen) {
                    if (es.isEndstelleA()) {
                        esRangIdA = es.getRangierId();
                    }
                    else if (es.isEndstelleB()) {
                        esRangIdB = es.getRangierId();
                    }
                }
            }
            // Schreibe Daten in HashMap
            // Rangierungsdaten fuer Endstelle A
            Rangierung rangA = (esRangIdA != null) ? rs.findRangierung(esRangIdA) : null;
            Equipment eqInA = (rangA != null) ? rs.findEquipment(rangA.getEqInId()) : null;
            map.put(getPropName(ESEQINVERTEILERA), (eqInA != null) ? eqInA.getRangVerteiler() : null);
            map.put(getPropName(ESEQINREIHEA), (eqInA != null) ? eqInA.getRangReihe() : null);
            map.put(getPropName(ESEQINBUCHTA), (eqInA != null) ? eqInA.getRangBucht() : null);
            map.put(getPropName(ESEQINLEISTE1A), (eqInA != null) ? eqInA.getRangLeiste1() : null);
            map.put(getPropName(ESEQINSTIFT1A), (eqInA != null) ? StringUtils.right(eqInA.getRangStift1(), 2) : null);
            map.put(getPropName(ESEQINLEISTE2A), (eqInA != null) ? eqInA.getRangLeiste2() : null);
            map.put(getPropName(ESEQINSTIFT2A), (eqInA != null) ? StringUtils.right(eqInA.getRangStift2(), 2) : null);
            map.put(getPropName(ESEQINUEVTA), ((eqInA != null) && (eqInA.getUetv() != null)) ? eqInA.getUetv().name() : null);
            map.put(getPropName(ESEQINRANGSCHNITTSTELLEA), ((eqInA != null) && (eqInA.getRangSchnittstelle() != null)) ? eqInA.getRangSchnittstelle().name() : null);

            Equipment eqOutA = (rangA != null) ? rs.findEquipment(rangA.getEqOutId()) : null;
            map.put(getPropName(ESEQOUTVERTEILERA), (eqOutA != null) ? eqOutA.getRangVerteiler() : null);
            map.put(getPropName(ESEQOUTREIHEA), (eqOutA != null) ? eqOutA.getRangReihe() : null);
            map.put(getPropName(ESEQOUTBUCHTA), (eqOutA != null) ? eqOutA.getRangBucht() : null);
            map.put(getPropName(ESEQOUTLEISTE1A), (eqOutA != null) ? eqOutA.getRangLeiste1() : null);
            map.put(getPropName(ESEQOUTSTIFT1A), (eqOutA != null) ? StringUtils.right(eqOutA.getRangStift1(), 2) : null);
            map.put(getPropName(ESEQOUTLEISTE2A), (eqOutA != null) ? eqOutA.getRangLeiste2() : null);
            map.put(getPropName(ESEQOUTSTIFT2A), (eqOutA != null) ? StringUtils.right(eqOutA.getRangStift2(), 2) : null);
            map.put(getPropName(ESEQOUTUEVTA), ((eqOutA != null) && (eqOutA.getUetv() != null)) ? eqOutA.getUetv().name() : null);
            map.put(getPropName(ESEQOUTRANGSCHNITTSTELLEA), ((eqOutA != null) && (eqOutA.getRangSchnittstelle() != null)) ? eqOutA.getRangSchnittstelle().name() : null);

            // Rangierungsdaten fuer Endstelle B
            Rangierung rangB = (esRangIdB != null) ? rs.findRangierung(esRangIdB) : null;
            Equipment eqInB = (rangB != null) ? rs.findEquipment(rangB.getEqInId()) : null;
            map.put(getPropName(ESEQINVERTEILERB), (eqInB != null) ? eqInB.getRangVerteiler() : null);
            map.put(getPropName(ESEQINREIHEB), (eqInB != null) ? eqInB.getRangReihe() : null);
            map.put(getPropName(ESEQINBUCHTB), (eqInB != null) ? eqInB.getRangBucht() : null);
            map.put(getPropName(ESEQINLEISTE1B), (eqInB != null) ? eqInB.getRangLeiste1() : null);
            map.put(getPropName(ESEQINSTIFT1B), (eqInB != null) ? StringUtils.right(eqInB.getRangStift1(), 2) : null);
            map.put(getPropName(ESEQINLEISTE2B), (eqInB != null) ? eqInB.getRangLeiste2() : null);
            map.put(getPropName(ESEQINSTIFT2B), (eqInB != null) ? StringUtils.right(eqInB.getRangStift2(), 2) : null);
            map.put(getPropName(ESEQINUEVTB), ((eqInB != null) && (eqInB.getUetv() != null)) ? eqInB.getUetv().name() : null);
            map.put(getPropName(ESEQINRANGSCHNITTSTELLEB), ((eqInB != null) && (eqInB.getRangSchnittstelle() != null)) ? eqInB.getRangSchnittstelle().name() : null);

            Equipment eqOutB = (rangB != null) ? rs.findEquipment(rangB.getEqOutId()) : null;
            map.put(getPropName(ESEQOUTVERTEILERB), (eqOutB != null) ? eqOutB.getRangVerteiler() : null);
            map.put(getPropName(ESEQOUTREIHEB), (eqOutB != null) ? eqOutB.getRangReihe() : null);
            map.put(getPropName(ESEQOUTBUCHTB), (eqOutB != null) ? eqOutB.getRangBucht() : null);
            map.put(getPropName(ESEQOUTLEISTE1B), (eqOutB != null) ? eqOutB.getRangLeiste1() : null);
            map.put(getPropName(ESEQOUTSTIFT1B), (eqOutB != null) ? StringUtils.right(eqOutB.getRangStift1(), 2) : null);
            map.put(getPropName(ESEQOUTLEISTE2B), (eqOutB != null) ? eqOutB.getRangLeiste2() : null);
            map.put(getPropName(ESEQOUTSTIFT2B), (eqOutB != null) ? StringUtils.right(eqOutB.getRangStift2(), 2) : null);
            map.put(getPropName(ESEQOUTUEVTB), ((eqOutB != null) && (eqOutB.getUetv() != null)) ? eqOutB.getUetv().name() : null);
            map.put(getPropName(ESEQOUTRANGSCHNITTSTELLEB), ((eqOutB != null) && (eqOutB.getRangSchnittstelle() != null)) ? eqOutB.getRangSchnittstelle().name() : null);

            // Prüfe Zwei-Draht - Vier-Draht
            if ((map.get(getPropName(ESEQOUTSTIFT1B)) != null) && (map.get(getPropName(ESEQOUTLEISTE1B)) != null) &&
                    (map.get(getPropName(ESEQOUTSTIFT2B)) != null) && (map.get(getPropName(ESEQOUTLEISTE2B)) != null)) {
                map.put(getPropName(VIERDRAHT), Boolean.TRUE);
                map.put(getPropName(ZWEIDRAHT), Boolean.FALSE);
            }
            else {
                map.put(getPropName(VIERDRAHT), Boolean.FALSE);
                map.put(getPropName(ZWEIDRAHT), Boolean.TRUE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("AuftragId wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetRangierDatenCommand.properties";
    }
}


