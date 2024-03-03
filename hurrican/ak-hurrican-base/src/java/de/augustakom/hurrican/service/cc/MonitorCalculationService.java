/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2015
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface fuer die Berechnung von Ressourcen-Belegungen.
 */
public interface MonitorCalculationService extends ICCService {

    /**
     * Erstellt und speichert fuer den angegebenen Standort sowie die Physiktyp-Kombinationen die Rangierungs-Zahlen.
     * @param physikTypCombinations
     * @param hvtStd
     * @throws FindException
     * @throws StoreException
     */
    void createRsmRangCounts4Hvt(List<Object[]> physikTypCombinations, HVTStandort hvtStd) throws FindException, StoreException;

    /**
     * Erstellt eine Uebersicht der UEVTs mit Anzahl der vorhandenen, vorbereiteten und rangierten CuDAs.
     *
     * @throws StoreException wenn beim Erstellen der View ein Fehler auftritt.
     */
    void createUevtCuDAViews() throws StoreException;

    /**
     * Ermittelt den Port-Verbrauch einer bestimmten Physiktyp-Kombination an einem Standort fuer die letzten x Monate.
     * Falls fuer einen Monat schon eine Berechnung besteht, wird diese nicht noch einmal ausgewertet. Ausser, es
     * handelt sich um den aktuellen bzw. vergangenen Monat. Diese werden auf jeden Fall ausgewertet. (Die Beibehaltung
     * der "alten" Port-Verbraeuche reduziert die Laufzeit der Ermittlung erheblich. Ausserdem wird so ueber die Zeit
     * eine umfangreiche Port-Statistik aufgebaut.)
     *
     * @param monthCount     Anzahl Monate, die beruecksichtigt werden sollen
     * @param hvtIdStandort
     * @param physikTypId
     * @param physikTypIdAdd
     * @param kvzNummer
     * @throws FindException
     * @throws StoreException
     * @result generierte {@link RsmPortUsage} Objekte
     */
    public List<RsmPortUsage> calculatePortUsage(int monthCount, Long hvtIdStandort, Long physikTypId,
            Long physikTypIdAdd, String kvzNummer) throws FindException, StoreException;

}
