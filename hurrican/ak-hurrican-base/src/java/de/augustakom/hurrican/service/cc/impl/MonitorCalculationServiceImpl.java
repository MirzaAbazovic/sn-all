/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.MonitorDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.MonitorCalculationService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Service-Implementierung fuer die Ressourcen-Belegung.
 * Die Methoden waren urspruenglich im {@link de.augustakom.hurrican.service.cc.impl.MonitorServiceImpl} enthalten.
 * Durch die Umstellung auf Hibernate4 und das dabei geaenderte Session-Handling gab es jedoch Probleme bei der
 * Multi-Threading Verarbeitung der Ressourcen-Berechnung. Dies konnte nur umgangen werden, in dem die Thread einen
 * weiteren Service aufrufen.
 */
@CcTxRequired
public class MonitorCalculationServiceImpl extends DefaultCCService implements MonitorCalculationService {

    private static final Logger LOGGER = Logger.getLogger(MonitorCalculationServiceImpl.class);

    @Resource(name = "monitorDAO")
    private MonitorDAO monitorDAO = null;
    @Resource(name = "equipmentDAO")
    private EquipmentDAO equipmentDAO = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;


    @Override
    public void createUevtCuDAViews() throws StoreException {
        try {
            monitorDAO.deleteUevtView();
            List<UevtCuDAView> views = equipmentDAO.createUevtCuDAViews();
            monitorDAO.storeUevtCuDAViews(views);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die UEVT-View konnte nicht erstellt werden!", e);
        }
    }

    @Override
    public void createRsmRangCounts4Hvt(List<Object[]> physikTypCombinations, HVTStandort hvtStd) throws FindException, StoreException {
        for (Object[] combination : physikTypCombinations) {
            // Suche alle Rangierungen des aktuellen Physiktyps
            List<Rangierung> activeRangierungen = monitorDAO.findAktRangierung4Phsyiktyp(hvtStd.getId(), (Long) combination[0]);
            if (CollectionTools.isNotEmpty(activeRangierungen)) {
                if (hvtStd.isFttc()) {
                    Map<String, List<Rangierung>> rangierungenSplittetByKvz = splitRangierungenByKvzNummer(activeRangierungen);
                    Iterator<Map.Entry<String, List<Rangierung>>> entrySetIterator = rangierungenSplittetByKvz.entrySet().iterator();
                    while (entrySetIterator.hasNext()) {
                        Map.Entry<String, List<Rangierung>> entry = entrySetIterator.next();
                        String kvzNummer = entry.getKey();
                        List<Rangierung> activeRangierungen4Kvz = entry.getValue();
                        createRsmRangCount(hvtStd, combination, activeRangierungen4Kvz, kvzNummer);
                    }
                }
                else {
                    createRsmRangCount(hvtStd, combination, activeRangierungen, null);
                }
            }
        }
    }


    /**
     * Ermittelt von den angegebenen Rangierungen jeweils die KVZ-Nummer und erstellt pro KVZ-Nummer eine eigene/neue
     * Liste. <br> Die erstellen Listen werden in einer Map zurueck gegeben. Als Map-Key wird die KVZ-Nummer verwendet.
     */
    Map<String, List<Rangierung>> splitRangierungenByKvzNummer(List<Rangierung> activeRangierungen) {
        Map<String, List<Rangierung>> splittedMap = new HashMap<String, List<Rangierung>>();
        for (Rangierung rangierung : activeRangierungen) {
            if (rangierung.getEqOutId() != null) {
                Equipment carrierEquipment = equipmentDAO.findById(rangierung.getEqOutId(), Equipment.class);
                String kvzNummer = carrierEquipment.getKvzNummer() != null ? carrierEquipment.getKvzNummer() : "";

                List<Rangierung> rangierungen4Kvz = splittedMap.get(kvzNummer);
                if (rangierungen4Kvz == null) {
                    rangierungen4Kvz = new ArrayList<>();
                    splittedMap.put(kvzNummer, rangierungen4Kvz);
                }
                rangierungen4Kvz.add(rangierung);
            }
        }
        return splittedMap;
    }

    private void createRsmRangCount(HVTStandort hvtStd, Object[] combination, List<Rangierung> activeRangierungen, String kvzNummer)
            throws FindException, StoreException {
        // Initialisiere Datensatz
        RsmRangCount rsmRangCount = new RsmRangCount();
        rsmRangCount.setHvtStandortId(hvtStd.getId());
        rsmRangCount.setKvzNummer(kvzNummer);
        rsmRangCount.setPhysiktyp((Long) combination[0]);
        rsmRangCount.setPhysiktypAdd((Long) combination[1]);
        rsmRangCount.initCounter();

        // Rangierungen auswerten
        analyseRangierungsState(rsmRangCount, activeRangierungen, combination, rangierungsService);

        // Port-Verbrauch der letzten Monate ermitteln
        List<RsmPortUsage> usageOfLastMonths = calculatePortUsage(
                RsmPortUsage.PORT_USAGE_MONTH_COUNT,
                rsmRangCount.getHvtStandortId(),
                rsmRangCount.getPhysiktyp(),
                rsmRangCount.getPhysiktypAdd(),
                rsmRangCount.getKvzNummer());
        calculatePortReach(rsmRangCount, usageOfLastMonths);

        // Speicher Datensatz
        monitorDAO.store(rsmRangCount);
    }

    @CcTxRequired
    public List<RsmPortUsage> calculatePortUsage(int monthCount, Long hvtIdStandort, Long physikTypId, Long physikTypIdAdd, String kvzNummer)
            throws FindException, StoreException {
        try {
            Date actMonthStart = DateUtils.truncate(new Date(), Calendar.MONTH);

            LOGGER.debug(String.format("load port usages for HVT-Id/KVZ %s/%s", hvtIdStandort, kvzNummer));
            List<RsmPortUsage> existingPortUsages = monitorDAO.findPortUsages(hvtIdStandort, kvzNummer, physikTypId, physikTypIdAdd, monthCount);
            List<RsmPortUsage> result = new ArrayList<RsmPortUsage>();
            for (int i = 0; i < monthCount; i++) {
                // aktuellen und vergangenen Monat auf jeden Fall auswerten!
                boolean forceCalculation = i <= 1 ? true : false;

                // Start- und End-Datum berechnen
                Date start = DateTools.changeDate(actMonthStart, Calendar.MONTH, -i);

                GregorianCalendar endCal = new GregorianCalendar();
                endCal.setTime(start);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date end = endCal.getTime();

                int year = endCal.get(Calendar.YEAR);
                int month = endCal.get(Calendar.MONTH) + 1;

                RsmPortUsage portUsage = getPortUsageOfMonth(existingPortUsages, year, month);
                if (portUsage == null) {
                    LOGGER.debug(String.format("create port usage for HVT-Id/KVZ %s/%s", hvtIdStandort, kvzNummer));
                    portUsage = new RsmPortUsage();
                    portUsage.setYear(year);
                    portUsage.setMonth(month);
                    portUsage.setHvtIdStandort(hvtIdStandort);
                    portUsage.setKvzNummer(kvzNummer);
                    portUsage.setPhysikTypId(physikTypId);
                    portUsage.setPhysikTypIdAdditional(physikTypIdAdd);
                }

                if (portUsage.getDiffCount() == null || forceCalculation) {
                    // Anzahl Auftraege ermitteln mit Zugang im angegebenen Zeitraum
                    int portsUsed = monitorDAO.sumPortUsage(start, end, hvtIdStandort, kvzNummer, physikTypId, physikTypIdAdd, false);
                    portUsage.setPortsUsed(portsUsed);

                    // Anzahl Auftraege ermitteln mit Kuendigung im angegebenen Zeitraum
                    int portsCancelled = monitorDAO.sumPortUsage(start, end, hvtIdStandort, kvzNummer, physikTypId, physikTypIdAdd, true);
                    portUsage.setPortsCancelled(portsCancelled);

                    portUsage.setDiffCount(portsUsed - portsCancelled);

                    monitorDAO.store(portUsage);
                }

                result.add(portUsage);
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Ermittlung des Port-Verbrauchs: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt aus der Liste der angegebenen RsmPortUsage Objekte das Objekt, das dem angegebenen Jahr+Monat
     * entspricht.
     */
    RsmPortUsage getPortUsageOfMonth(List<RsmPortUsage> existingPortUsages, int year, int month) {
        if (CollectionTools.isNotEmpty(existingPortUsages)) {
            for (RsmPortUsage rsmPortUsage : existingPortUsages) {
                if (NumberTools.equal(rsmPortUsage.getYear(), Integer.valueOf(year))
                        && NumberTools.equal(rsmPortUsage.getMonth(), Integer.valueOf(month))) {
                    return rsmPortUsage;
                }
            }
        }
        return null;
    }


    /**
     * Ermittelt aus den vergangenen Monaten den durchschnittlichen Port-Verbrauch und berechnet aus den noch freien
     * Rangierungen die voraussichtliche Restlaufzeit.
     *
     * @param rsmRangCount
     * @param usageOfLastMonths
     */
    void calculatePortReach(RsmRangCount rsmRangCount, List<RsmPortUsage> usageOfLastMonths) {
        if (CollectionTools.isNotEmpty(usageOfLastMonths)) {
            float monthCount = usageOfLastMonths.size();
            float diffCount = 0;
            for (RsmPortUsage usage : usageOfLastMonths) {
                diffCount += usage.getDiffCount();
            }

            float averageUsage = diffCount / monthCount;
            rsmRangCount.setAverageUsage(averageUsage);

            // Berechnung der voraussichtlichen Reichweite (in Tagen):
            // (30 / averageUsage) * Anzahl freier Ports (frei u. im Aufbau)
            float portReach = 30 / averageUsage * rsmRangCount.getPortCountFree();

            if (portReach < 0 || NumberTools.equal(Integer.valueOf((int) portReach), Integer.MAX_VALUE)) {
                // negativer Durchschnitt oder "unendliche" Reichweite -->> -1
                rsmRangCount.setPortReach(Integer.valueOf(-1));
            }
            else {
                rsmRangCount.setPortReach(Integer.valueOf((int) portReach));
            }
        }
    }


    /* Analysiert die Rangierung und ordnet sie einem der Counter zu. */
    void analyseRangierungsState(RsmRangCount rsmRangCount, List<Rangierung> activeRangierungen,
            Object[] combination, RangierungsService rangierungsService) throws FindException {
        for (Rangierung rangierung : activeRangierungen) {
            // 2. Physiktyp vorhanden
            if (combination[1] != null) {
                // Rangierung besitzt keine LeitungGesamtId -> Abbruch
                if (rangierung.getLeitungGesamtId() == null) {
                    continue;
                }
                analyse4CombiRangierung(rsmRangCount, combination[1], rangierungsService.findByLtgGesId(rangierung.getLeitungGesamtId()), rangierung);
            }
            else {
                if (rangierung.getLeitungGesamtId() != null && rangierungsService.findByLtgGesId(rangierung.getLeitungGesamtId()).size() > 1) {
                    // in diesem Fall handelt es sich um eine einzelne Rangierung, die zwar eine LeitungGesamtId
                    // besitzt, aber keine weitere Rangierung zugeordnet ist
                    continue;
                }
                analyse4SingleRangierung(rsmRangCount, rangierung);
            }
        }
    }

    private void analyse4SingleRangierung(RsmRangCount rsmRangCount, Rangierung rangierung) {
        rsmRangCount.incVorhanden();

        // Rangierung mit passenden Physiktyp und aktuellem Gueltigkeitsdatum vorhanden
        // Rangierung ist belegt
        if (NumberTools.isGreater(rangierung.getEsId(), Integer.valueOf(0))) {
            rsmRangCount.incBelegt();
        }
        // Rangierung freigegeben
        else if (rangierung.getFreigegeben() == Rangierung.Freigegeben.freigegeben) {
            // Rangierung ist keiner ES zugeordnet
            if (rangierung.getEsId() == null) {
                rsmRangCount.incFrei();
            }
            // Rangierung ist freigabebereit
            else if (NumberTools.equal(rangierung.getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE)) {
                rsmRangCount.incFreigabebereit();
                rsmRangCount.incBelegt();
            }
        }
        // Rangierung defekt
        else if (rangierung.getFreigegeben() == Rangierung.Freigegeben.defekt) {
            rsmRangCount.incDefekt();

        }
        // Rangierung im Aufbau
        else if (rangierung.getFreigegeben() == Rangierung.Freigegeben.in_Aufbau) {
            rsmRangCount.incImAufbau();
        }
    }

    private void analyse4CombiRangierung(RsmRangCount rsmRangCount, Object o, List<Rangierung> byLtgGesId, Rangierung rangierung) throws FindException {
        // Pruefe 2. Physiktyp
        Rangierung rangAdditional = null;
        List<Rangierung> rangierungen = byLtgGesId;
        if (CollectionTools.isNotEmpty(rangierungen)) {
            for (Rangierung rangAdditionalCheck : rangierungen) {
                if (!NumberTools.equal(rangAdditionalCheck.getId(), rangierung.getId())) {
                    rangAdditional = rangAdditionalCheck;
                    break;
                }
            }
        }

        if (rangAdditional != null && NumberTools.equal(rangAdditional.getPhysikTypId(), (Long) o)) {
            // Rangierung mit passenden Physiktypen und aktuellem Gueltigkeitsdatum vorhanden
            rsmRangCount.incVorhanden();

            // Rangierung ist belegt
            if (NumberTools.isGreater(rangierung.getEsId(), Integer.valueOf(0))
                    || NumberTools.isGreater(rangAdditional.getEsId(), Integer.valueOf(0))) {
                rsmRangCount.incBelegt();
                return;
            }

            // Rangierung freigegeben
            if (rangierung.getFreigegeben() == Rangierung.Freigegeben.freigegeben
                    && rangAdditional.getFreigegeben() == Rangierung.Freigegeben.freigegeben) {
                // Rangierung ist frei
                if (rangierung.getEsId() == null && rangAdditional.getEsId() == null) {
                    rsmRangCount.incFrei();
                }
                // Rangierung ist freigabebereit
                else if (NumberTools.equal(rangierung.getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE)
                        && NumberTools.equal(rangAdditional.getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE)) {
                    rsmRangCount.incFreigabebereit();
                    rsmRangCount.incBelegt();
                }
            }
            // Rangierung defekt
            else if (rangierung.getFreigegeben() == Rangierung.Freigegeben.defekt
                    || rangAdditional.getFreigegeben() == Rangierung.Freigegeben.defekt) {
                rsmRangCount.incDefekt();
            }
            // Rangierung im Aufbau
            else if (rangierung.getFreigegeben() == Rangierung.Freigegeben.in_Aufbau
                    || rangAdditional.getFreigegeben() == Rangierung.Freigegeben.in_Aufbau) {
                rsmRangCount.incImAufbau();
            }
        }
    }
}
