/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2010 13:21:33
 */

package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GewofagWohnung;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.GewofagService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command um Daten speziell fuer die Gewofag-Vertraege zu ermitteln.
 *
 *
 */
public class GetGewofagDatenCommand extends AbstractReportCommand {

    public static final String WOHNUNGSBEZEICHNUNG = "Wohnungsbez";
    public static final String LAGE = "Lage";
    public static final String ETAGE = "Etage";
    public static final String SOLL_ZUSTAND_PORT = "SollZustandPort";
    public static final String IST_ZUSTAND_PORT = "IstZustandPort";
    public static final String DSL_VON = "DslVon";
    public static final String DSL_NACH = "DslNach";

    private Long auftragId = null;
    private Map<String, String> map = null;

    @Override
    public Object execute() throws Exception {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<>();
            readGewofagDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    private void readGewofagDaten() throws FindException, ServiceNotFoundException {
        EndstellenService endstellenService = getCCService(EndstellenService.class);
        List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
        if (endstellen != null) {
            for (Endstelle es : endstellen) {
                if (es.isEndstelleB()) {
                    readDataFromEndstelle(es);
                }
            }
        }
    }

    private void readDataFromEndstelle(Endstelle es) throws ServiceNotFoundException, FindException {
        RangierungsService rangierungsService = getCCService(RangierungsService.class);
        GewofagService gewofagService = getCCService(GewofagService.class);

        Equipment eqDluNew = null;
        Equipment eqDluOld = null;

        Equipment eqDslOld = null;
        Equipment eqDslNew = null;

        Rangierung rangierung1New = rangierungsService.findRangierung(es.getRangierId());
        if (es.getRangierIdAdditional() == null) {
            eqDluNew = rangierungsService.findEquipment(rangierung1New.getEqInId());
        }
        else {
            if (es.getRangierIdAdditional() != null) {
                Rangierung rangierung2New = rangierungsService.findRangierung(es.getRangierIdAdditional());
                eqDluNew = rangierungsService.findEquipment(rangierung2New.getEqInId());
            }
            eqDslNew = rangierungsService.findEquipment(rangierung1New.getEqInId());
        }

        if (rangierung1New.getHistoryFrom() != null) {
            Rangierung rangierung1Old = rangierungsService.findRangierung(rangierung1New.getHistoryFrom());
            if ((rangierung1Old != null)) {
                if (rangierung1Old.getLeitungGesamtId() == null) {
                    eqDluOld = rangierungsService.findEquipment(rangierung1Old.getEqInId());
                }
                else {
                    List<Rangierung> rangierungenHist = rangierungsService.findByLtgGesId(rangierung1Old.getLeitungGesamtId());
                    for (Rangierung rangierung : rangierungenHist) {
                        if (Integer.valueOf(2).equals(rangierung.getLeitungLfdNr())) {
                            eqDslOld = rangierungsService.findEquipment(rangierung1Old.getEqInId());
                            eqDluOld = rangierungsService.findEquipment(rangierung.getEqInId());
                        }
                    }
                }
            }
        }

        map.put(getPropName(SOLL_ZUSTAND_PORT), generateStringFromDluPort(eqDluNew));
        map.put(getPropName(IST_ZUSTAND_PORT), generateStringFromDluPort(eqDluOld));
        map.put(getPropName(DSL_VON), generateStringFromDslPort(eqDslOld));
        map.put(getPropName(DSL_NACH), generateStringFromDslPort(eqDslNew));

        Equipment eqGewo = rangierungsService.findEquipment(rangierung1New.getEqOutId());
        GewofagWohnung wohnung = null;
        if (eqGewo != null) {
            wohnung = gewofagService.findGewofagWohnung(eqGewo);
        }
        map.put(getPropName(WOHNUNGSBEZEICHNUNG), (wohnung != null) ? wohnung.getName() : null);
        map.put(getPropName(LAGE), (wohnung != null) ? wohnung.getLage() : null);
        map.put(getPropName(ETAGE), (wohnung != null) ? wohnung.getEtage() : null);
    }

    private String generateStringFromDluPort(Equipment port) {
        if (port == null) {
            return null;
        }
        String returnValue = "";
        if (port.getHwSchnittstelle() != null) {
            returnValue += port.getHwSchnittstelle() + " - ";
        }
        if (port.getHwSwitch() != null) {
            returnValue += port.getHwSwitch();
        }
        if (port.getHwEQN() != null) {
            returnValue += port.getHwEQN();
        }
        return returnValue;
    }

    private String generateStringFromDslPort(Equipment port) throws ServiceNotFoundException, FindException {
        if (port == null) {
            return null;
        }
        HWService hwService = getCCService(HWService.class);
        String returnValue = "";
        Long hwBaugruppeId = port.getHwBaugruppenId();
        if (hwBaugruppeId != null) {
            HWBaugruppe baugruppe = hwService.findBaugruppe(hwBaugruppeId);
            if (baugruppe != null) {
                Long rackId = baugruppe.getRackId();
                if (rackId != null) {
                    HWRack rack = hwService.findRackById(rackId);
                    if (rack.getManagementBez() != null) {
                        returnValue = returnValue + rack.getManagementBez() + " - ";
                    }
                    else {
                        returnValue = returnValue + rack.getGeraeteBez() + " - ";
                    }
                }
            }
        }

        return returnValue + port.getHwEQN();
    }

    @Override
    public String getPrefix() {
        return GEWOFAG;
    }

    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetGewofagDatenCommand.properties";
    }

    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("AuftragId wurde dem Command-Objekt nicht uebergeben!");
        }
    }

}
