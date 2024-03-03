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

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse, um HVT-Daten zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetHvtDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetHvtDatenCommand.class);

    public static final String HVTASBA = "HvtAsbA";
    public static final String KVZNUMMERA = "KvzNummerA";
    public static final String KVZSCHALTNUMMERA = "KvzSchaltnummerA";
    public static final String HVTONKZA = "HvtONKZA";
    public static final String HVTSTRASSEA = "HvtStrasseA";
    public static final String HVTHAUSNRA = "HvtHausNrA";
    public static final String HVTORTA = "HvtOrtA";
    public static final String HVTPLZA = "HvtPLZA";
    public static final String CKNAMEA = "CkNameA";
    public static final String CKSTRASSEA = "CkStrasseA";
    public static final String CKPLZA = "CkPlzA";
    public static final String CKORTA = "CkOrtA";
    public static final String CKKUNDENRA = "CkKundenNrA";
    public static final String CKPORTIERUNGSKENNUNGA = "CkPortierungskennungA";
    public static final String HVTASB = "HvtAsb";
    public static final String HVTONKZ = "HvtONKZ";
    public static final String HVTSTRASSE = "HvtStrasse";
    public static final String HVTHAUSNR = "HvtHausNr";
    public static final String HVTORT = "HvtOrt";
    public static final String HVTPLZ = "HvtPLZ";
    public static final String KVZNUMMERB = "KvzNummer";
    public static final String KVZSCHALTNUMMERB = "KvzSchaltnummer";
    public static final String CKNAME = "CkName";
    public static final String CKSTRASSE = "CkStrasse";
    public static final String CKPLZ = "CkPlz";
    public static final String CKORT = "CkOrt";
    public static final String CKKUNDENR = "CkKundenNr";
    public static final String CKPORTIERUNGSKENNUNG = "CkPortierungskennung";

    protected Long auftragId;
    protected Map<String, Object> map;

    protected EndstellenService endstellenService;
    protected RangierungsService rangierungsService;
    protected HVTService hvtService;
    protected CarrierService carrierService;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<String, Object>();
            initServices();
            readHvtDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    private void initServices() throws ServiceNotFoundException {
        endstellenService = getCCService(EndstellenService.class);
        rangierungsService = getCCService(RangierungsService.class);
        hvtService = getCCService(HVTService.class);
        carrierService = getCCService(CarrierService.class);
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return HVT_DATEN;
    }

    /* Ermittelt die HVT-Daten der Endstellen (A+B) des Auftrags und schreibt diese in das reportView-Objekt */
    protected void readHvtDaten() throws HurricanServiceCommandException {
        try {
            Long esHvtStdIdA = null;
            Long esHvtStdIdB = null;
            UEVT uevtEsA = null;
            Equipment equipmentEsA = null;
            UEVT uevtEsB = null;
            Equipment equipmentEsB = null;

            // Ermittle RangierId über Endstellen
            List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
            if (endstellen != null) {
                for (Endstelle endstelle : endstellen) {
                    if (endstelle.isEndstelleA()) {
                        esHvtStdIdA = endstelle.getHvtIdStandort();
                        equipmentEsA = rangierungsService.findEquipment4Endstelle(endstelle, false, true);
                        if ((equipmentEsA != null) && StringUtils.isNotBlank(equipmentEsA.getKvzNummer())) {
                            uevtEsA = hvtService.findUEVT(
                                    equipmentEsA.getHvtIdStandort(), equipmentEsA.getRangVerteiler());
                        }
                    }
                    else if (endstelle.isEndstelleB()) {
                        esHvtStdIdB = endstelle.getHvtIdStandort();
                        equipmentEsB = rangierungsService.findEquipment4Endstelle(endstelle, false, true);
                        if ((equipmentEsB != null) && StringUtils.isNotBlank(equipmentEsB.getKvzNummer())) {
                            uevtEsB = hvtService.findUEVT(
                                    equipmentEsB.getHvtIdStandort(), equipmentEsB.getRangVerteiler());
                        }
                    }
                }
            }

            HVTStandort hvts_a = (esHvtStdIdA != null) ? hvtService.findHVTStandort(esHvtStdIdA) : null;
            HVTStandort hvts_b = (esHvtStdIdB != null) ? hvtService.findHVTStandort(esHvtStdIdB) : null;

            // HVT-Daten Endstelle A
            map.put(getPropName(HVTASBA), (hvts_a != null) ? hvts_a.getAsb() : null);
            map.put(getPropName(KVZNUMMERA), (equipmentEsA != null) ? equipmentEsA.getKvzNummer() : null);
            map.put(getPropName(KVZSCHALTNUMMERA), (uevtEsA != null) ? uevtEsA.getUevt() : null);

            HVTGruppe hvtg = (hvts_a != null) ? hvtService.findHVTGruppeById(hvts_a.getHvtGruppeId()) : null;
            map.put(getPropName(HVTONKZA), (hvtg != null) ? hvtg.getOnkz() : null);
            map.put(getPropName(HVTSTRASSEA), (hvtg != null) ? hvtg.getStrasse() : null);
            map.put(getPropName(HVTHAUSNRA), (hvtg != null) ? hvtg.getHausNr() : null);
            map.put(getPropName(HVTORTA), (hvtg != null) ? hvtg.getOrt() : null);
            map.put(getPropName(HVTPLZA), (hvtg != null) ? hvtg.getPlz() : null);

            CarrierKennung ckA = getCarrierKennung((hvts_a != null) ? hvts_a.getCarrierKennungId() : null);
            map.put(getPropName(CKNAMEA), (ckA != null) ? ckA.getName() : null);
            map.put(getPropName(CKSTRASSEA), (ckA != null) ? ckA.getStrasse() : null);
            map.put(getPropName(CKPLZA), (ckA != null) ? ckA.getPlz() : null);
            map.put(getPropName(CKORTA), (ckA != null) ? ckA.getOrt() : null);
            map.put(getPropName(CKKUNDENRA), (ckA != null) ? ckA.getKundenNr() : null);
            map.put(getPropName(CKPORTIERUNGSKENNUNGA), (ckA != null) ? ckA.getPortierungsKennung() : null);

            // HVT-Daten Endstelle B
            map.put(getPropName(HVTASB), (hvts_b != null) ? hvts_b.getAsb() : null);
            map.put(getPropName(KVZNUMMERB), (equipmentEsB != null) ? equipmentEsB.getKvzNummer() : null);
            map.put(getPropName(KVZSCHALTNUMMERB), (uevtEsB != null) ? uevtEsB.getUevt() : null);

            hvtg = (hvts_b != null) ? hvtService.findHVTGruppeById(hvts_b.getHvtGruppeId()) : null;
            map.put(getPropName(HVTONKZ), (hvtg != null) ? hvtg.getOnkz() : null);
            map.put(getPropName(HVTSTRASSE), (hvtg != null) ? hvtg.getStrasse() : null);
            map.put(getPropName(HVTHAUSNR), (hvtg != null) ? hvtg.getHausNr() : null);
            map.put(getPropName(HVTORT), (hvtg != null) ? hvtg.getOrt() : null);
            map.put(getPropName(HVTPLZ), (hvtg != null) ? hvtg.getPlz() : null);

            CarrierKennung ckB = getCarrierKennung((hvts_b != null) ? hvts_b.getCarrierKennungId() : null);
            map.put(getPropName(CKNAME), (ckB != null) ? ckB.getName() : null);
            map.put(getPropName(CKSTRASSE), (ckB != null) ? ckB.getStrasse() : null);
            map.put(getPropName(CKPLZ), (ckB != null) ? ckB.getPlz() : null);
            map.put(getPropName(CKORT), (ckB != null) ? ckB.getOrt() : null);
            map.put(getPropName(CKKUNDENR), (ckB != null) ? ckB.getKundenNr() : null);
            map.put(getPropName(CKPORTIERUNGSKENNUNG), (ckB != null) ? ckB.getPortierungsKennung() : null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ermittelt die Carrier-Kennung mit einer bestimmten ID. */
    private CarrierKennung getCarrierKennung(Long ckId) throws HurricanServiceCommandException {
        try {
            return carrierService.findCarrierKennung(ckId);
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
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetHvtDatenCommand.properties";
    }
}


