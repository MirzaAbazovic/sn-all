/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2005 07:26:14
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.consistence.HistoryConsistence;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service, um die Datenbank-Konsistenz der Hurrican-DB zu pruefen.
 *
 *
 */
public interface ConsistenceCheckService extends ICCService {

    /**
     * Ueberprueft die Konsistenz des angegebenen Klassen-Typs (und damit der zugehoerigen Tabelle).
     *
     * @param classType Klassen-Typ/Tabelle, die geprueft werden soll
     * @return Liste mit Objekten fuer die Datensaetze, die nicht korrekt historisiert sind.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<HistoryConsistence> checkHistoryConsistence(Class classType) throws FindException;

    /**
     * Ueberprueft, ob ein IntAccount jeweils nur einem aktiven Auftrag zugeordnet ist.
     *
     * @return Liste mit Objekten des Typs <code>IntAccount</code>, die mehreren aktiven Auftraegen zugeordnet sind.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<IntAccount> findMultipleUsedIntAccounts() throws FindException;

}


