/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2005 11:25:34
 */
package de.augustakom.authentication.service;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.authentication.AuthenticationSystemReader;
import de.augustakom.authentication.model.AuthenticationSystem;
import de.augustakom.common.BaseTest;


/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AuthenticationSystemReaderTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationSystemReaderTest.class);

    /**
     * Test fuer die Methode AuthenticationSystemReader#getAuthenticationSystems()
     */
    public void testGetAuthenticationSystems() {
        List<AuthenticationSystem> systems = AuthenticationSystemReader.instance().getAuthenticationSystems();
        Assert.assertNotNull(systems, "Es wurden keine Authentication-Systems gefunden!");

        for (AuthenticationSystem authSys : systems) {
            LOGGER.debug("System: " + authSys.getBeanName() + " - " + authSys.getName());
        }
    }

}


