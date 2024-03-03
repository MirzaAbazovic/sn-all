/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2004 13:03:57
 */
package de.augustakom.authentication.service;

import java.util.*;

import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Interface fuer einen Role-Service. <br> Ueber einen Role-Service koennen Rollen verwaltet werden.
 *
 *
 */
public interface AKRoleService extends IAuthenticationService {

    /**
     * Liefert eine bestimmte Rolle anhand der ID
     *
     * @param id
     * @return
     * @throws AKAuthenticationException
     *
     */
    public AKRole findById(Long id) throws AKAuthenticationException;

    /**
     * Gibt eine Liste aller angelegten Rollen zurueck.
     *
     * @return Liste mit AKRole-Objekten
     * @throws AKAuthenticationException
     */
    public List<AKRole> findAll() throws AKAuthenticationException;

    /**
     * Gibt eine Liste aller Rollen fuer eine bestimmte Applikation zurueck.
     *
     * @param applicationId ID der Applikation zu der die Rollen gefunden werden sollen.
     * @return Liste mit AKRole-Objekten
     * @throws AKAuthenticationException
     */
    public List<AKRole> findByApplication(Long applicationId) throws AKAuthenticationException;

    /**
     * Erzeugt oder aktualisiert eine Rolle.
     *
     * @param role
     * @throws AKAuthenticationException
     */
    public void save(AKRole role) throws AKAuthenticationException;

    /**
     * Loescht eine Rolle mit der angegebenen ID.
     *
     * @param roleId ID der zu loeschenden Rolle
     * @throws AKAuthenticationException
     */
    public void delete(Long roleId) throws AKAuthenticationException;
}
