/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 10:56:44
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Produkt;
import de.augustakom.hurrican.model.cc.ProduktMapping;

/**
 * DAO-Interface fuer Objekte des Typs <code>Produkt</code> und <code>Produkt2Strasse</code>.
 *
 *
 */
public interface ProduktDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Sucht nach allen Produkten.
     *
     * @param onlyActual Flag, ob nach allen (false) oder nur nach aktuellen/gueltigen (true) Produkten gesucht wird.
     * @return Liste mit Objekten des Typs <code>Produkt</code>
     */
    List<Produkt> find(boolean onlyActual);

    /**
     * Ermittelt alle Produkte, die best. Parent-Physiktypen benoetigen.
     *
     * @param parentPhysikTypIds Liste mit den Parent-Physiktyp-IDs.
     * @return Liste mit Objekten des Typs <code>Produkt</code>.
     */
    List<Produkt> findByParentPhysikTypIds(List<Long> parentPhysikTypIds);

    /**
     * Sucht nach allen Produkt2Produkt-Konfigurationen zu einem best. Source-Produkt. Die Sortierung erfolgt
     * aufsteigend nach der ID des Ziel-Produkts.
     *
     * @param prodIdSrc ID des Source-Produkts.
     * @return Liste mit Objekten des Typs <code>Produkt2Produkt</code>.
     */
    List<Produkt2Produkt> findProdukt2Produkte(Long prodIdSrc);

    /**
     * Sucht nach allen Produkt2Produkt-Konfigurationen zu einem best. Destination-Produkt. Die Sortierung erfolgt
     * aufsteigend nach der ID des Ziel-Produkts.
     *
     * @param prodIdDest ID des Destination-Produkts.
     * @return Liste mit Objekten des Typs <code>Produkt2Produkt</code>.
     */
    List<Produkt2Produkt> findProdukt2ProdukteByDest(Long prodIdDest);

    /**
     * Loescht eine best. Produkt2Produkt-Konfiguration.
     *
     * @param p2pId
     */
    void deleteProd2Prod(Long p2pId);

    /**
     * Sucht nach einem bestimmten Produkt mit der ID
     *
     * @param onlyActual Flag, ob nach allen (false) oder nur nach aktuellen/gueltigen (true) Produkten gesucht wird.
     * @param prodId
     * @return Instanz von <code>Produkt</code> oder <code>null</code>.
     */
    Produkt findActualByProdId(boolean onlyActual, Long prodId);

    /**
     * Ermittelt die Produkt-Mappings zum Hurrican-Produkt mit der ID <code>prodId</code> und dem Produkt-Anteilstyp
     * <code>mappingPartType</code>.
     *
     * @param prodId          Hurrican Produkt-ID
     * @param mappingPartType Kennzeichnung fuer den gesuchten Mapping-Part (z.B. 'phone').
     * @return Liste mit Objekten des Typs <code>ProduktMapping</code>.
     *
     */
    List<ProduktMapping> findProduktMappings(Long prodId, String mappingPartType);

    /**
     * Ermittelt eine Liste von (Hurrican) Produkt-IDs, die sich aus den angegebenen {@code produktMappingGroups}
     * ergeben. <br> Die Liste wird dabei nach der Prioritaet der Produkt-Mappings sortiert (NULL=LAST).
     *
     * @param produktMappingGroups
     * @return Liste mit den Hurrican Produkt-IDs zu den angegebenen Mappings; sortiert nach der Prioritaet der
     * Mappings. Dabei werden die hoechst priorisierten Produkt-IDs zuerst ausgegeben.
     */
    List<Long> findProdIdsForProdMappings(List<Long> produktMappingGroups);

    /**
     * Sucht nach allen Produkten mit den angegebenen technischen Leistungen.
     *
     * @param techLeistungTypes Liste mit TechLeistung-Typen.
     * @return Liste von Produkten oder leere Liste
     */
    List<Produkt> findProductsByTechLeistungType(String... techLeistungTypes);

}
