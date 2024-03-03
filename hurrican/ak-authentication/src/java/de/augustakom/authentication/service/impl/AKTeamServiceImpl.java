/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2004 13:06:36
 */
package de.augustakom.authentication.service.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.dao.AKTeamDAO;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.service.AKTeamService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Implementierung eines Team-Services. <br>
 *
 *
 */
@AuthenticationTx
public class AKTeamServiceImpl implements AKTeamService {

    private static final Logger LOGGER = Logger.getLogger(AKTeamServiceImpl.class);

    @Resource(name = "de.augustakom.authentication.dao.AKTeamDAO")
    private AKTeamDAO teamDAO;

    /**
     * {@inheritDoc} *
     */
    @Override
    public List<AKTeam> findAll() throws AKAuthenticationException {
        try {
            return teamDAO.findAll();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public Map<Long, AKTeam> findAllAsMap() throws AKAuthenticationException {
        Map<Long, AKTeam> result = new HashMap<>();
        for (AKTeam team : this.findAll()) {
            result.put(team.getId(), team);
        }
        return result;
    }

}
