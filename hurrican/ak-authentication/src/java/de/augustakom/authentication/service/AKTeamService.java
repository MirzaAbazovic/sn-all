/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2004 13:03:57
 */
package de.augustakom.authentication.service;

import java.util.*;

import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Interface for the TeamService
 */
public interface AKTeamService extends IAuthenticationService {

    /**
     * @return a list with all available {@link AKTeam}s.
     * @throws de.augustakom.authentication.service.exceptions.AKAuthenticationException
     */
    List<AKTeam> findAll() throws AKAuthenticationException;

    /**
     * @return a HashMap with all available {@link AKTeam}s and there ID as key.
     * @throws de.augustakom.authentication.service.exceptions.AKAuthenticationException
     */
    Map<Long, AKTeam> findAllAsMap() throws AKAuthenticationException;


}
