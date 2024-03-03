/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2008 16:42:19
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.query.ResourcenMonitorQuery;
import de.augustakom.hurrican.model.cc.view.RsmRangCountView;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.MonitorException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Definition, um den Ressourcenmonitor zu bedienen.
 *
 *
 */
public interface MonitorService extends ICCService {

    /**
     * Speichert ein RsMonitorRun-Objekt
     *
     * @param run Das zu speichernde Objekt
     * @throws StoreException Falls ein Fehler auftrat
     *
     */
    public void saveRsMonitorRun(RSMonitorRun run) throws StoreException;

    /**
     * Liefert ein RSMonitorRun-Objekt anhand des Monitor-Typs
     *
     * @param type Monitor-Typ des gesuchten Run-Objekts
     * @return Das gesuchte Objekt
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public RSMonitorRun findByMonitorType(Long type) throws FindException;

    /**
     * Funktion startet die Datenermittlung eines Monitors.
     *
     * @param type      Typ des zu startenden Monitors
     * @param sessionId Hurrican-Session-Id des Benutzer
     * @return RsMonitorRun-Objekt des aktuell gestarteten Monitor-Laufs
     * @throws MonitorException Falls ein Fehler auftrat
     *
     */
    public RSMonitorRun startMonitor(Long type, Long sessionId) throws MonitorException;

    /**
     * Funktion sucht nach Konfigurationsdatensatzen fuer einen bestimmten HVT und einen best. Monitor-Typ
     *
     * @param hvtStandortId Id des HVT-Standorts
     * @param monitorType   Id des Monitors
     * @return Liste mit RSMonitorConfig-Objekte
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public List<RSMonitorConfig> findMonitorConfig4HvtType(Long hvtStandortId, Long monitorType)
            throws FindException;

    /**
     * Speichert ein RsMonitorConfig-Objekt
     *
     * @param config    Das zu speichernde Objekt
     * @param sessionId Id der Session um den Benutzer zu identifizieren
     * @throws StoreException Falls ein Fehler auftrat
     *
     */
    public void saveRsMonitorConfig(RSMonitorConfig config, Long sessionId) throws StoreException;

    /**
     * Loescht einen Konfigurationsdatensatz
     *
     * @param id Id des zu loeschenden Datensatzes
     * @throws StoreException Falls ein Fehler auftrat
     *
     */
    public void deleteRsMonitorConfig(Long id) throws StoreException;

    /**
     * Funktion ermittelt die Anzahl der Rangierungen pro HVT fuer jeden Physiktyp. Wird fuer die
     * Rangierungsueberwachung im Ressourcenmonitor benoetigt.
     *
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public void writeRsmRangCount() throws FindException;

    /**
     * Funktion liefert alle verfuegbaren RsmRangCount-Objekte
     *
     * @return Liste mit RsmRangCount-Objekten
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public List<RsmRangCountView> findAllRsmRangCount() throws FindException;

    /**
     * Funktion liefert gefiltert verfuegbare RsmRangCount-Objekte.
     *
     * @param query für Filter
     * @return Liste mit RsmRangCount-Objekten
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public List<RsmRangCountView> findRsmRangCount(ResourcenMonitorQuery query) throws FindException;

    /**
     * Ermittelt die protokollierten Port-Verbraeuche fuer die angegebene Kombination aus Standort und Physiktypen. <br>
     * Es werden dabei nur die Port-Verbraeuche der letzten X Monate ermittelt, wobei X ueber {@link
     * RsmPortUsage#PORT_USAGE_MONTH_COUNT} definiert ist.
     *
     * @param hvtIdStandort
     * @param kvzNummer      (optional)
     * @param physikTypId
     * @param physikTypIdAdd (optional)
     * @return
     * @throws FindException
     */
    public List<RsmPortUsage> findRsmPortUsage(Long hvtIdStandort, String kvzNummer, Long physikTypId, Long physikTypIdAdd)
            throws FindException;

    /**
     * Ermittelt die aktuelle Belegung aller UEVTs. <br> Die ermittelten Daten werden in Tabellen einer embedded-DB
     * (HSQLDB) geschrieben.
     *
     * @throws StoreException wenn beim Ermitteln der Daten ein Fehler auftritt.
     */
    public void detectUevtBelegung() throws StoreException;

    /**
     * Sucht nach allen zuvor ermittelten (ueber Methode createUevtCuDAViews) Uevt-CuDA-Views.
     *
     * @return Liste mit Objekten des Typs <code>UevtCuDAView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<UevtCuDAView> findUevtCuDAViews() throws FindException;

    /**
     * Sucht nach allen zuvor ermittelten (ueber Methode createUevtCuDAViews) Uevt-CuDA-Views.
     *
     * @param query für Filter
     * @return Liste mit Objekten des Typs <code>UevtCuDAView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<UevtCuDAView> findUevtCuDAViews(ResourcenMonitorQuery query) throws FindException;

    /**
     * Ermittelt CuDA-Zahlen eines best. UEVTs. <br> Die Ermittlung erfolgt gruppiert nach <code>RANG_SS_TYPE</code>.
     *
     * @param uevt          Bezeichnung des UEVTs
     * @param cudaPhysik    Art der gesuchten Physik
     * @param hvtIdStandort ID des zug. HVT-Standorts
     * @return Liste mit Objekten des Typs <code>UevtCuDAView</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<UevtCuDAView> findViewsGroupedByRangSSType(String uevt, String cudaPhysik, Long hvtIdStandort)
            throws FindException;

    /**
     * Ermittelt die Belegungs-Details zu einem best. UEVT.
     *
     * @param uevt          Bezeichnung des UEVTs
     * @param hvtIdStandort ID des zug. HVT-Standorts
     * @return Liste mit Objekten des Typs <code>UevtCuDAView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<UevtCuDAView> findViews4UevtBelegung(String uevt, Long hvtIdStandort) throws FindException;

    /**
     * Funktion prueft das Ergebnis eines bestimmten Monitors anhand des definierten Schwellwerts und erzeugt bei
     * unterschrittenen Schwellwerten eine Alarmierungs-Email.
     *
     * @param type Id des Monitor-Typs
     * @throws MonitorException Falls ein Fehler auftrat
     *
     */
    public void monitorAlarmierung(Long type) throws MonitorException;

}
