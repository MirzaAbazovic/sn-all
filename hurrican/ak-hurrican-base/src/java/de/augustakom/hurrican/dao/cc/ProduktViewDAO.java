/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 13:15:52
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.ProduktGruppe;


/**
 * DAO-Interface fuer View-Operationen auf Produkt-Klassen.
 *
 *
 */
public interface ProduktViewDAO {

    /**
     * Sucht nach allen ProduktGruppen, denen ein Produkt zugeordnet ist, welches in Hurrican neu angelegt werden kann.
     * <br> Dies ist dann der Fall, wenn das Produkt-Flag <code>auftragserstelltung</code> auf <code>true</code> gesetzt
     * ist.
     *
     * @return Liste mit Objekten des Typs <code>ProduktGruppe</code>
     */
    public List<ProduktGruppe> findProduktGruppen4Hurrican();

    /**
     * Sucht nach der ProduktGruppe, die einem best. Auftrag zugeordnet ist.
     *
     * @param ccAuftragId ID des CC-Auftrags.
     * @return Instanz von ProduktGruppe oder <code>null</code>
     */
    public ProduktGruppe findPG4Auftrag(Long ccAuftragId);

}


