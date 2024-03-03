/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.2005 13:12:23
 */
package de.augustakom.authentication.service;

import java.util.*;

import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Interface fuer einen DB-Service.
 *
 *
 */
public interface AKDbService extends IAuthenticationService {

    /**
     * Gibt eine Liste aller angelegten Accounts zurueck.
     *
     * @return Liste mit AKDb-Objekten
     * @throws AKAuthenticationException
     */
    public List<AKDb> findAll() throws AKAuthenticationException;

    /**
     * Erzeugt oder aktualisiert eine DB-Definition.
     *
     * @param db
     * @throws AKAuthenticationException
     */
    public void save(AKDb account) throws AKAuthenticationException;

    /**
     * Loescht eine DB-Definition mit der angegebenen ID.
     *
     * @param dbId ID der zu loeschenden DB-Definition
     * @throws AKAuthenticationException
     */
    public void delete(Long dbId) throws AKAuthenticationException;

}


