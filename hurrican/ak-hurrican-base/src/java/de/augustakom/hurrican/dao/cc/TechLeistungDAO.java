/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2005 14:34:45
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Produkt2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;


/**
 * DAO-Interface fuer die Verwaltung von techn. Leistungs-Objekten.
 *
 *
 */
public interface TechLeistungDAO extends FindAllDAO, FindDAO, StoreDAO, ByExampleDAO {

    /**
     * Sucht nach den technischen Leistungszuordnungen eines Produkts. <br>
     *
     * @param prodId   Produkt-ID
     * @param techLsTyp   (optional) Typ der zu ermittelnden Leistungen
     * @param defaults (optional) Einschraenkung, ob nur nach den Defaults (Boolean.TRUE), den nicht-standard Leistungen
     *                 (Boolean.FALSE) oder allen (null) Zuordnungen zum Produkt gesucht wird.
     * @return Liste mit Objekten des Typs <code>Produkt2TechLeistung</code>.
     *
     */
    public List<Produkt2TechLeistung> findProdukt2TechLs(Long prodId, String techLsTyp, Boolean defaults);

    /**
     * Sucht nach allen techn. Leistungen. Die Sortierung erfolgt nach dem Leistungstyp.
     *
     * @return
     * @boolean onlyActual Flag, ob nur nach gueltigen/aktuellen oder allen Leistungen gesucht werden soll.
     */
    public List<TechLeistung> findTechLeistungen(boolean onlyActual);

    /**
     * Ermittelt alle technischen Leistungen zu einem bestimmten Auftrag.
     *
     * @param auftragId  Auftrags-ID
     * @param lsTyp      (optional) Angabe des gesuchten Leistungstyps (Konstante aus TechLeistung)
     * @param onlyActive Flag, ob nur aktive (true) oder auch bereits gekuendigte (false) Leistungen ermittelt werden
     *                   sollen.
     * @return Liste mit Objekten des Typs <code>TechLeistung</code>
     *
     */
    public List<TechLeistung> findTechLeistungen4Auftrag(Long auftragId, String lsTyp, boolean onlyActive);

    /**
     * Ermittelt alle technischen Leistungen zu bestimmten Auftraegen.
     *
     * @param auftragIds Auftrags-IDs
     * @param lsTyp      (optional) Angabe des gesuchten Leistungstyps (Konstante aus TechLeistung)
     * @param onlyActive Flag, ob nur aktive (true) oder auch bereits gekuendigte (false) Leistungen ermittelt werden
     *                   sollen.
     * @return Map, die Auftrags-ID auf Liste mit Objekten des Typs <code>TechLeistung</code> mapt
     */
    public Map<Long, List<TechLeistung>> findTechLeistungen4Auftraege(List<Long> auftragIds, String lsTyp, boolean onlyActive);

    /**
     * Sucht nach allen aktuellen(!) Leistungs-Konfigurationen fuer eine bestimmte techn. Leistung mit der angegebenen
     * externen Leistungs-No.
     *
     * @param externLeistungNo
     * @return Liste mit Objekten des Typs <code>TechLeistung</code>.
     */
    public List<TechLeistung> findTechLeistungen(Long externLeistungNo);

    /**
     * Ermittelt die technischen Leistungen, die in einem bestimmten Verlauf zugeordnet sind.
     *
     * @param verlaufId Verlaufs-ID
     * @return Liste mit Objekten des Typs <code>TechLeistung</code>.
     */
    public List<TechLeistung> findTechLeistungen4Verlauf(Long verlaufId);

    /**
     * Loescht die Produkt-2-techLeistung mit der angegebenen ID
     *
     * @param id ID des zu loeschenden Datensatzes.
     *
     */
    public void deleteProdukt2TechLeistung(Long id);

}


