/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2005 07:53:46
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.consistence.HistoryConsistence;


/**
 * Interface fuer DAOs, die die Datenbank-Konsistenz pruefen.
 *
 *
 */
public interface ConsistenceCheckDAO {

    /**
     * Prueft die History-Konsistenz fuer die Klasse <code>type</code>.
     *
     * @param type
     * @return
     */
    public List<HistoryConsistence> checkHistoryConsistence(Class type);

    /**
     * Ermittelt alle IntAccounts, die mehreren aktiven Auftraegen zugeordnet sind.
     *
     * @return Liste mit den mehrfach verwendeten Accounts.
     */
    public List<IntAccount> findMultipleUsedIntAccounts();
}


