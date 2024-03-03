/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2008 16:53:21
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.query.ResourcenMonitorQuery;
import de.augustakom.hurrican.model.cc.view.HVTBelegungView;
import de.augustakom.hurrican.model.cc.view.RsmRangCountView;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;

/**
 * DAO-Interface fuer den Ressourcen-Monitor.
 *
 *
 */
public interface MonitorDAO extends FindAllDAO, FindDAO, StoreDAO, ByExampleDAO, DeleteDAO {

    /**
     * Loescht alle RsmRangCount-Objekte
     *
     * @param toStore zu speicherndes Objekt
     *
     */
    public void deleteRsmRangCount();

    /**
     * Ermittelt die gespeicherten Port-Verbraeuche von der angegebenen Kombination aus Standort und Physiktypen. Die
     * Ermittlung ist dabei auf {@code monthCount} beschraenkt. Die Ermittlung der Daten erfolgt absteigend sortiert
     * nach Jahr und Monat.
     *
     * @param hvtIdStandort
     * @param kvzNummer
     * @param physikTypId
     * @param physikTypIdAdd
     * @param monthCount
     * @return
     */
    public List<RsmPortUsage> findPortUsages(Long hvtIdStandort, String kvzNummer, Long physikTypId, Long physikTypIdAdd, int monthCount);

    /**
     * Liefert alle RsmRangCount-Objekte
     *
     * @return Liste mir RsmRangCount-Objekten
     *
     */
    public List<RsmRangCountView> findAllRsmRangCount();

    /**
     * Findet alle RsmRangCount-Objekte welche der query entsprechen.
     *
     * @param query
     * @return Liste der gefundenen RsmRangCountView-Objekte
     *
     */
    public List<RsmRangCountView> findRsmRangCount(ResourcenMonitorQuery query);

    /**
     * Loescht den Inhalt der Tabelle T_RS_CUDA_BELEGUNG.
     */
    public void deleteHVTBelegung();

    /**
     * Loescht den Inhalt der Tabelle T_RS_UEVT_CUDA_VIEW.
     */
    public void deleteUevtView();

    /**
     * Speichert die angegebenen Objekt.
     *
     * @param toStore
     */
    public void store(List<HVTBelegungView> toStore);

    /**
     * Speichert die angegebenen Objekte.
     *
     * @param toStore
     */
    public void storeUevtCuDAViews(List<UevtCuDAView> toStore);

    /**
     * Sucht nach allen UEVT-CuDA-Views.
     *
     * @return
     */
    public List<UevtCuDAView> findUevtCuDAViews();

    /**
     * Sucht nach UEVT-CuDA-Views die dem Filter entsprechen.
     *
     * @param query Filter mit Bedingungen
     * @return
     */
    public List<UevtCuDAView> findUevtCuDAViews(ResourcenMonitorQuery query);

    /**
     * Ermittelt alle HVTBelegungView-Objekte fuer einen bestimmten UEVT. <br> Das Ergebnis ist nach UEVT, CuDA-Physik,
     * RangLeiste1 und RangSSTyp gruppiert.
     *
     * @param uevt     Bezeichnung des UEVTs.
     * @param hvtIdStd ID des HVT-Standorts
     * @return Liste mit Objekten des Typs <code>HVTBelegungView</code>.
     */
    public List<HVTBelegungView> findHVTBelegungGrouped(String uevt, Long hvtIdStd);

    /**
     * Ermittelt CuDA-Zahlen eines best. UEVTs. <br> Die Ermittlung erfolgt gruppiert nach <code>RANG_SS_TYPE</code>.
     *
     * @param uevt          Bezeichnung des UEVTs
     * @param cudaPhysik    Art der gesuchten Physik
     * @param hvtIdStandort ID des zug. HVT-Standorts
     * @return Liste mit Objekten des Typs <code>UevtCuDAView</code>
     */
    public List<UevtCuDAView> findViewsGroupedByRangSSType(String uevt, String cudaPhysik, Long hvtIdStandort);

    /**
     * Funktion liefert alle Rangierungen eines best. Physiktyps zu einem HVT
     *
     * @param hvtIdStandort Id des HVT-Standorts
     * @param physikTypId   Id des gesuchten Physiktyps
     * @return List mit Rangierungs-Objekten
     *
     */
    public List<Rangierung> findAktRangierung4Phsyiktyp(Long hvtIdStandort, Long physikTypId);

    /**
     * Ermittelt die Summe der Zu-/Abgaenge von Physiktypen innerhalb eines bestimmten Monats. Bei "vergangenen" Monaten
     * muss das Inbetriebnahme- bzw. Kuendiungsdatum des Auftrags in dem Monat liegen. Beim aktuellen Monat werden auch
     * kuenftige Realisierungen/Kuendigungen beruecksichtigt.
     *
     * @param begin
     * @param end
     * @param hvtIdStandort
     * @param kvzNummer      (optional)
     * @param physikTypId
     * @param physikTypIdAdd (optional)
     * @param forCancel
     * @param actMonth
     * @return
     */
    public int sumPortUsage(Date begin, Date end, Long hvtIdStandort, String kvzNummer, Long physikTypId, Long physikTypIdAdd,
            boolean forCancel);
}
