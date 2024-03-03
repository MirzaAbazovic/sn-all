/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:35:33
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;

/**
 * DAO-Definition fuer Objekte des Typs <code>Abteilung</code>
 *
 *
 */
public interface AbteilungDAO extends FindDAO, ByExampleDAO {

    /**
     * Sucht nach den Abteilungen, die die IDs <code>ids</code> besitzen.
     *
     * @param ids Liste mit den IDs der gesuchten Abteilungen.
     * @return Liste mit Objekten des Typs <code>Abteilung</code>.
     */
    public List<Abteilung> findByIds(Collection<Long> ids);

    /**
     * Liefert alle Niederlassungen, die eine bestimmte Abteilung besitzen.
     *
     * @param abtId Id der Abteilung
     * @return Liste mit gesuchten Niederlassungen
     *
     */
    public List<Niederlassung> findNL4Abteilung(Long abtId);

}
