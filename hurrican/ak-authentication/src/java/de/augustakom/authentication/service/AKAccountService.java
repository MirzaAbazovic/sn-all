/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2004 16:27:34
 */
package de.augustakom.authentication.service;

import java.util.*;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Interface fuer einen Account-Service. <br> Ueber einen Account-Service koennen DB-Accounts verwaltet werden.
 *
 *
 */
public interface AKAccountService extends IAuthenticationService {

    /**
     * Gibt eine Liste aller angelegten Accounts zurueck.
     *
     * @return Liste mit AKAccount-Objekten
     * @throws AKAuthenticationException
     */
    public List<AKAccount> findAll() throws AKAuthenticationException;

    /**
     * Sucht nach allen Accounts, die einer best. Datenbank zugeordnet sind.
     *
     * @param dbId ID der Datenbank, deren Account gesucht werden.
     * @return Liste mit AKAccount-Objekten.
     * @throws AKAuthenticationException
     */
    public List<AKAccount> findByDB(Long dbId) throws AKAuthenticationException;

    /**
     * Erzeugt oder aktualisiert einen Account.
     *
     * @param account
     * @throws AKAuthenticationException
     */
    public void save(AKAccount account) throws AKAuthenticationException;

    /**
     * Loescht einen Account mit der angegebenen ID.
     *
     * @param accountId ID des zu loeschenden Accounts
     * @throws AKAuthenticationException
     */
    public void delete(Long accountId) throws AKAuthenticationException;

}
