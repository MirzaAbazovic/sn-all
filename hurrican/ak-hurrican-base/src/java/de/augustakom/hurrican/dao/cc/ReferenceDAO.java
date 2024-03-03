/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2006 08:36:14
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.cc.Reference;


/**
 * DAO-Interface fuer die Verwaltung von Reference-Objekten.
 *
 *
 */
public interface ReferenceDAO extends FindDAO, FindAllDAO, ByExampleDAO {

    /**
     * Ermittelt alle References zum Type 'type'.
     *
     * @param type
     * @param onlyVisible Flag, ob der Parameter 'GUI_VISIBLE' beachtet werden soll.
     * @return
     *
     */
    public List<Reference> findReferences(String type, Boolean onlyVisible);

}


