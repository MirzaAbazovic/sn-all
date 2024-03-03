/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 16:46:07
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.model.cc.view.HVTBestellungView;


/**
 * DAO-Interface fuer die Verwaltung von HVT-Bestellungen.
 *
 *
 */
public interface HVTBestellungDAO extends FindDAO, ByExampleDAO, StoreDAO {

    /**
     * Ermittelt eine Liste mit Views ueber alle HVTs mit zugehoerigen (offenen!) HVT-Bestellungen.
     *
     * @return Liste mit Objekten des Typs <code>HVTBestellungView</code>.
     */
    List<HVTBestellungView> findHVTBestellungViews();

    /**
     * Sucht nach allen Equipments, die einem best. UEVT zugeordnet sind. <br> WICHTIG: Die Equipments werden nach
     * Bucht, Leiste und Schnittstelle gruppiert! <br> Diese Methode berechnet noch nicht, wie viele Stifte vorhanden
     * und wie viele Stifte bereits rangiert sind!
     *
     * @param uevtId  ID des UEVTs
     * @param leiste1 Bezeichnung der Leiste-1 (optional!)
     * @return Liste mit Objekten des Typs <code>EquipmentBelegungView</code>.
     */
    List<EquipmentBelegungView> findEQs4Uevt(Long uevtId, String leiste1);

    /**
     * Ermittelt die gesamte Anzahl, sowie die 'kleinste' und die 'groesste' Bezeichnung der Stifte einer best.
     * Bucht/Leiste an einem best. UEVT.
     *
     * @param eqView Instanz von <code>EquipmentBelegungView</code>, in die die Daten geschrieben werden. Ausserdem sind
     *               die benoetigten Filter-Werte in diesem Modell enthalten.
     */
    void loadStifte(EquipmentBelegungView eqView);

    /**
     * Ermittelt die Anzahl der bereits rangierten Stifte einer best. UEVT-Bucht/Leiste.
     *
     * @param hvtIdStandort ID des zugehoerigen HVT-Standorts.
     * @param uevt          Bezeichnung des UEVTs
     * @param rangLeiste1   Bezeichnung der Leiste (1), deren rangierte Stifte ermittelt werden soll.
     * @return Anzahl der bereits rangierten Stifte auf einer best. Leiste eines UEVTs.
     */
    Integer getCountStifteRangiert(Long hvtIdStandort, String uevt, String rangLeiste1);

    /**
     * Ermittelt, wie viele Stifte von einer HVT-Bestellung bereits eingespielt wurden.
     *
     * @param hvtBestellungId ID der HVT-Bestellung
     * @return Anzahl der Stifte, die von der HVT-Bestellung bereits eingespielt wurden.
     */
    int getCuDACount4Bestellung(Long hvtBestellungId);

    /**
     * Ermittelt die hoechste KVZ Doppelader fuer die Kombination HVT-Standort und KVZ Nummer.
     *
     * @param hvtIdStandort
     * @param kvzNummer
     * @return
     */
    String getHighestKvzDA(Long hvtIdStandort, String kvzNummer);

}


