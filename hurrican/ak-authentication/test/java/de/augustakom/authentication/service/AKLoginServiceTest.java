/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 11:09:02
 */
package de.augustakom.authentication.service;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;
import de.augustakom.authentication.service.impl.AKLoginServiceImpl;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.system.SystemInformation;

/**
 * UnitTest fuer den AKLoginService.
 *
 *
 */
public class AKLoginServiceTest extends AbstractAuthenticationTest {

    private static final Logger LOGGER = Logger.getLogger(AKLoginServiceImpl.class);

    /**
     * Test fuer die Methode AKLoginService#login(..)
     */
    @Test(groups = BaseTest.SERVICE)
    public void testLogin() throws AKAuthenticationException, AKPasswordException {
        final String PW = "1#Unit@Test";

        final String user = "UnitTest";

        AKLoginContext login = getLoginService().ldapLogin(user, PW, "Hurrican", "1.0.0-test");
        Assert.assertNotNull(login, "Login failure! Returned AKLoginContext object is null!");
        Assert.assertNotNull(login.getUserSession(), "UserSession not generated!");
        Assert.assertNotNull(login.getUserSession().getSessionId(), "Session-ID of UserSession not generated!");
        Assert.assertTrue(login.getUserSession().getLoginTime().compareTo(new Date()) <= 0,
                "LoginTime of UserSession is invalid. Must be before now!");
        Assert.assertNotNull(login.getUser(), "Object AKUser is null!");
        Assert.assertNotNull(login.getApplication(), "Object AKApplication is null!");
        Assert.assertEquals(login.getApplication().getName(), "Hurrican", "Name of Application is not valid!");
        Assert.assertEquals(login.getUser().getName(), user, "Username is not valid!");
        Assert.assertEquals(login.getUserSession().getHostName(), SystemInformation.getLocalHostName(),
                "Host-Name of usersession is not valid!");
        Assert.assertEquals(login.getUserSession().getIpAddress(), SystemInformation.getLocalHostAddress(),
                "IP of usersession is not valid!");
        Assert.assertNotNull(login.getAccounts(), "List of AKAccount-Objects is null!");
    }

    /**
     * Test fuer die Methode AKLoginService#logout(..)
     */
    @Test(groups = BaseTest.SERVICE)
    public void testLogout() throws AKAuthenticationException, AKPasswordException {
        final String PW = "1#Unit@Test";
        final String user = "UnitTest";

        AKLoginContext login = getLoginService().ldapLogin(user, PW, "Hurrican", null);
        Assert.assertNotNull(login, "Login not successful!");
        Assert.assertNotNull(login.getUserSession(), "UserSession not created!");

        LOGGER.info("Logout with UserSession: " + login.getUserSession().getSessionId());
        getLoginService().logout(login.getUserSession().getSessionId());
    }

}
