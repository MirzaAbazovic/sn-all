/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2006 08:48:29
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.Account;


/**
 * DAO-Interface fuer Objekte des Typs <code>Account</code>.
 *
 *
 */
public interface AccountDAO {

    /**
     * Ermittelt alle Accounts des Auftrags mit der ID <code>auftragNo</code>.
     *
     * @param auftragNo ID des Auftrags
     * @param account   gesuchter Account (optional)
     * @return Liste mit Objekten des Typs <code>Account</code>.
     *
     */
    public List<Account> findAccounts4Auftrag(Long auftragNo, String account);

    /**
     * Speichert den Account.
     *
     * @param auftragNo   Taifun-Auftragsnummer
     * @param accountName Account-Name
     * @param password    Passwort
     */

    public void saveAccount(Long auftragNo, String accountName, String password);

    /**
     * Aktualisiert das Passwort eines Accounts.
     *
     * @param auftragNo   Taifun-Auftragsnummer
     * @param accountName Account-Name
     * @param password    Passwort
     */
    public void updatePassword(Long auftragNo, String accountName, String password);
}


