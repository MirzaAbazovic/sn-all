/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 13:36:51
 */
package de.augustakom.authentication.service;

import java.util.*;

import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Interface fuer einen ApplicationService. <br> Ueber den ApplicationService kann nach Applikationen gesucht werden,
 * die im Authentication-Service verwaltet werden.
 *
 *
 */
public interface AKApplicationService extends IAuthenticationService {

    /**
     * Gibt eine Liste aller Applikationen zurueck.
     *
     * @return Liste mit AKApplication-Objekten
     * @throws AKAuthenticationException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<AKApplication> findAll() throws AKAuthenticationException;

    /**
     * Sucht eine Applikation ueber ihren Namen.
     *
     * @param name Name der gesuchten Applikation.
     * @return AKApplication-Objekt oder <code>null</code>
     * @throws AKAuthenticationException wenn bei der Abfrage ein Fehler auftritt.
     */
    public AKApplication findByName(String name) throws AKAuthenticationException;
}
