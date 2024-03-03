/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 11:27:51
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery;


/**
 * DAO-Interface zur Verwaltung von Objekten des Typs <code>PhysikTyp</code>
 *
 *
 */
public interface PhysikTypDAO extends ByExampleDAO, FindDAO, FindAllDAO {

    /**
     * Loescht eine best. Produkt-PhysikTyp-Zuordnungen.
     *
     * @param p2ptId ID der zu loeschenden Zuordnung.
     */
    public void deleteP2PTById(Long p2ptId);

    /**
     * Speichert alle Objekte der Liste.
     *
     * @param toSave Liste mit den zu speichernden Objekten vom Typ <code>Produkt2PhysikTyp</code>.
     */
    public void saveP2PTs(List<Produkt2PhysikTyp> toSave);

    /**
     * Sucht nach Produkt-PhysikTyp-Zuordnungen anhand der im Query eingetragenen Parameter.
     *
     * @param query Query-Objekt mit den Such-Parametern.
     * @return Liste mit Objekten des Typs <code>Produkt2PhysikTyp</code>, die den Such-Parametern entsprechen.
     */
    public List<Produkt2PhysikTyp> findP2PTsByQuery(Produkt2PhysikTypQuery query);

    /**
     * Sucht nach allen Physiktypen, die best. Parent-Physiktypen besitzen.
     *
     * @param parentPhysiktypIds
     * @return
     */
    public List<PhysikTyp> find4ParentPhysiktypen(List<Long> parentPhysiktypIds);

    /**
     * Sucht nach den zugeordneten Physiktypen eines best. Auftrags.
     *
     * @param auftragId
     * @return
     */
    public List<Long> findPhysiktypen4Auftrag(Long auftragId);

    /**
     * Sucht die einem Produkt zugeordneten einfachen Physiktypen. Einfach heisst, Physiktypen, die keinen Additional
     * oder Parent Physiktyp konfiguriert haben.
     */
    public List<Produkt2PhysikTyp> findSimpleP2PTs4Produkt(Long produktId);

    /**
     * Liefert alle Physiktyp-Kombinationen die in der Tabelle t_produkt_2_physiktyp in den Feldern PHYSIKTYP und
     * PHYSIKTYP_ADDITIONAL eingetragen sind
     *
     * @return Alle Kombinationen von Physiktyp und Physiktyp_Additional
     *
     */
    public List<Object[]> findAllPhysiktypKombinationen();

}


