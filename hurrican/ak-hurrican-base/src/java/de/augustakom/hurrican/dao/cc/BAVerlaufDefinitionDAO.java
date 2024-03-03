/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 09:04:32
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;
import de.augustakom.hurrican.model.cc.BAVerlaufAG2Produkt;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;


/**
 * DAO-Interface fuer Objekte, die den elektronischen Verlauf definieren.
 *
 *
 */
public interface BAVerlaufDefinitionDAO extends ByExampleDAO, FindAllDAO, FindDAO,
        HistoryUpdateDAO<AbstractCCHistoryModel>, StoreDAO {

    /**
     * Sucht nach allen Mappings zwischen Produkt und BA-Verlauf Aenderungsgruppen fuer ein best. Produkt.
     *
     * @param produktId ID des Produkts
     * @return Liste mit Objekten vom Typ <code>BAVerlaufAG2Produkt</code>
     */
    public List<BAVerlaufAG2Produkt> findBAVAG4Produkt(Long produktId);

    /**
     * Loescht die Zuordnungen zwischen einem Produkt und den BA-Verlauf Aenderungsgruppen.
     *
     * @param produktId ID des Produkts
     */
    public void deleteBAVAG4Produkt(Long produktId);

    /**
     * Speichert eine Liste von Objekten des Typs BAVerlaufAG2Produkt.
     *
     * @param baVerlaufAG2Produkte Liste mit Objekten des Typs BAVerlaufAG2Produkt
     */
    public void saveBAVerlaufAG2Produkte(List<BAVerlaufAG2Produkt> baVerlaufAG2Produkte);

    /**
     * Ermittelt die Bauauftrags-Anlaesse sortiert nach der Positionsnummer.
     *
     * @param onlyAct         (optional) Flag, ob alle (null), nur aktive (true) oder inaktive (false) Verlaufs-Anlaesse
     *                        ermittelt werden sollen.
     * @param onlyAuftragsart (optional) Flag, nach welchen Anlaessen gefiltert werden soll: <ul> <li>null = alle
     *                        Anlaesse <li>TRUE = nur Anlaesse, die eine Auftragsart (z.B. Neuschaltung) darstellen
     *                        <li>FALSE = nur Anlaesse, die keine Auftragsart darstellen. </ul>
     * @return Liste mit Objekten des Typs <code>BAVerlaufAnlass</code>
     *
     */
    public List<BAVerlaufAnlass> findBAVerlaufAnlaesse(Boolean onlyAct, Boolean onlyAuftragsart);

    /**
     * Sucht nach allen Bauauftragsanlaessen, die fuer ein bestimmtes Produkt moeglich sind.
     *
     * @param produktId       ID des Produkts
     * @param onlyAuftragsart
     * @return Liste mit Objekten des Typs <code>BAVerlaufAenderung</code>
     *
     */
    public List<BAVerlaufAnlass> findPossibleBAAnlaesse4Produkt(Long produktId, Boolean onlyAuftragsart);

    /**
     * Ermittelt die Bauauftragskonfigurationen zu dem angegebenen Anlass und Produkt.
     *
     * @param anlass      ID des Bauauftraganlasses
     * @param prodId      Produkt-ID
     * @param forceProdId Flag, ob die Produkt-ID auf jeden Fall im Query beruecksichtigt werden soll (true) oder nur
     *                    dann, wenn dir Produkt-ID <> 'null' (false) ist.
     * @return Liste mit Objekten des Typs <code>BAVerlaufConfig</code>
     *
     */
    public List<BAVerlaufConfig> findBAVerlaufConfigs(Long anlass, Long prodId, boolean forceProdId);

}


