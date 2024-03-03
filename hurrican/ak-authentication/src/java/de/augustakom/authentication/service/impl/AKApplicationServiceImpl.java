/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 13:45:24
 */
package de.augustakom.authentication.service.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.dao.AKApplicationDAO;
import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.service.AKApplicationService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Implementierung des ApplicationServices. <br>
 *
 *
 */
@AuthenticationTx
public class AKApplicationServiceImpl implements AKApplicationService {

    private static final Logger LOGGER = Logger.getLogger(AKApplicationServiceImpl.class);

    @Resource(name = "de.augustakom.authentication.dao.AKApplicationDAO")
    private AKApplicationDAO applicationDao;

    @Override
    public List<AKApplication> findAll() throws AKAuthenticationException {
        try {
            return applicationDao.findAll();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AKApplication findByName(String name) throws AKAuthenticationException {
        try {
            AKApplication app = applicationDao.findApplicationByName(name);
            return app;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

}
