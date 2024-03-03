/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 16:45:35
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.temp.CarrierEquipmentDetails;
import de.augustakom.hurrican.model.cc.view.RangierungWithEquipmentView;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * DAO-Interface fuer Objekte des Typs <code>Rangierung</code>.
 *
 *
 */
public interface RangierungDAO extends StoreDAO, FindDAO, FindAllDAO, HistoryUpdateDAO<Rangierung>, ByExampleDAO {

    /**
     * Sucht nach allen <b>freien</b> Rangierungen ueber das Query-Objekt. <br>
     *
     * @param query          Query-Objekt mit den Such-Parametern.
     * @param maxResultCount max. Anzahl der zu liefernden Rangierungen (-1 ohne).
     * @return Instanz von <code>Rangierung</code> oder <code>null</code>.
     */
    public List<Rangierung> findFreiByQuery(RangierungQuery query, int maxResultCount);

    /**
     * Ermittelt alle freien Rangierungen am Standort <code>hvtStdId</code>, deren EQ_OUT Daten den Daten aus
     * <code>eqOutQuery</code> entsprechen.
     *
     * @param hvtStdId   HVT-Standort ID
     * @param eqOutQuery Query-Parameter fuer das Out-Equipment der Rangierung.
     * @return eine Liste aller freien Rangierungen zu den Such-Parametern
     *
     */
    public List<Rangierung> findFrei(Long hvtStdId, CarrierEquipmentDetails eqOutQuery);

    /**
     * Sucht nach allen (aktuellen) Rangierungen mit einer best. Leitung-Gesamt-ID. <br> Die Sortierung erfolgt nach der
     * angegebenen lfd. Nummer.
     *
     * @param ltgGesId
     * @return Liste mit Objekten des Typs <code>Rangierung</code>.
     */
    public List<Rangierung> findByLtgGesId(Integer ltgGesId);

    /**
     * Gibt den hoechsten Wert der Spalte LEITUNG_GESAMT_ID der Tabelle T_RANGIERUNG zurueck. <br> Wird kein Eintrag
     * gefunden wird ein Integer-Objekt mit Wert '0' zurueck gegeben.
     *
     * @return hoechster Integer-Wert aus Spalte LEITUNG_GESAMT_ID
     */
    public Integer getNextLtgGesId();

    /**
     * Sucht den hoechsten Wert fuer LEITUNG_LFD_NR zu einer best. LEITUNG_GESAMT_ID und gibt diesen zurueck. <br> Wird
     * kein Eintrag gefunden wird ein Integer-Objekt mit Wert '0' zurueck gegeben.
     *
     * @param ltgGesId ID der LEITUNG_GESAMT_ID zu der der hoechste Wert von LEITUNG_LFD_NR gesucht wird.
     * @return Integer-Wert
     */
    public Integer getMaxLfdNr(Integer ltgGesId);

    /**
     * Sucht die Rangierung, die zu einem best. Auftrag gehoert. <br> Ueber den Parameter <code>esTyp</code> wird
     * bestimmt, von welchem Endstellen-Typ die Rangierung gesucht wird ('A' oder 'B').
     *
     * @param auftragId ID des CC-Auftrags
     * @param esTyp     Typ der Endstelle von der die Rangierung gesucht wird.
     * @return Instanz von <code>Rangierung</code> oder <code>null</code>.
     */
    public Rangierung findByAuftragAndEsTyp(Long auftragId, String esTyp);

    /**
     * Gibt eine Rangierung frei. Ueber den Parameter <code>rangierID<code> wird die Rangierung ausgewaehlt
     *
     * @param rangierID ID der Rangierung, die freigegeben werden soll.
     * @return die Anzahl von angepassten Daten-Satze
     */
    public int freigabeRangierung(Long rangierID);

    /**
     * Erzeugt alle {@link RangierungWithEquipmentView}s für die übergebenen {@link Equipment}s. Dabei werden die die
     * nicht mehr gültigen {@link Rangierung}en ausgeschlossen. Sollten bei dein übergebenen Ids auch {@link Rangierung}
     * Adds dabei sein, werden diese nicht nur zur zugehörigen {@link Rangierung} angezeigt. (Matching über die
     * Leitungs-Gesamt-Id und Leitung-Lfd-Nr)
     *
     * @param rangierungIds Liste mit RangierungsIds zu denen die {@link Rangierung}en mit zugehörigen {@link
     *                      Rangierung}enAdd geladen werden sollen (falls vorhanden)
     * @return Liste mit {@link RangierungWithEquipmentView}s zu den übergebenen {@link Equipment}s (never {@code null})
     */
    public List<RangierungWithEquipmentView> findRangierungWithEquipmentViews(Set<Long> rangierungIds);


    /**
     * Ermittelt die aktiven(!) Rangierungen zu einer Liste von Equipments.
     *
     * @param eqIds Liste von Equipment IDs fuer die Rangierungen gesucht sind
     * @return Map, die Equipment-ID (sowohl IN als auch OUT) auf Rangierung mappt, evtl. leer, nie {@code null}
     * @throws FindException
     */
    public Map<Long, Rangierung> findForEquipments(List<Long> eqIds);
}
