/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 13:49:37
 */
package de.augustakom.authentication.service;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKApplication;
import de.augustakom.common.BaseTest;


/**
 * Unit-Test fuer AKApplicationServiceImpl
 *
 *
 */
public class AKApplicationServiceTest extends AbstractAuthenticationTest {

    private static final Logger LOGGER = Logger.getLogger(AKApplicationServiceTest.class);

    /**
     * Test fuer die Methode AKApplicationServiceImpl#findAll
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFindAll() throws Exception {
        List<AKApplication> apps = getApplicationService().findAll();
        Assert.assertNotNull(apps, "List of application objects is null!");
        Assert.assertTrue(!apps.isEmpty(), "List of applications is empty!");
        for (AKApplication app : apps) {
            LOGGER.info(">>> loaded Application: " + app.getId() + " - " + app.getName());
        }
    }

    /**
     * Test fuer die Methode AKApplicationServiceImpl#findByName "Hurrican" sollte immer existieren.
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFindByName() throws Exception {
        AKApplicationService appService = getApplicationService();
        AKApplication app = appService.findByName("Hurrican");

        Assert.assertNotNull(app, "Application with name 'Hurrican' not found!");
        Assert.assertEquals(app.getName(), "Hurrican", "Name of application is not valid");
        Assert.assertEquals(app.getDescription(), "Hurrican - CustomerCare System", "Desc of application is not valid");
    }

}

