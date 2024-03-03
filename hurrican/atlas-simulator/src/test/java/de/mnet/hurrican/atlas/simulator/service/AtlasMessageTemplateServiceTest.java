/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.mnet.hurrican.atlas.simulator.service;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.simulator.config.SimulatorConfiguration;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AtlasMessageTemplateServiceTest {

    private AtlasMessageTemplateService testling = new AtlasMessageTemplateService();

    @BeforeClass
    public void setupTest() {
        testling = new AtlasMessageTemplateService();
        SimulatorConfiguration simulatorConfiguration = new SimulatorConfiguration();
        simulatorConfiguration.setTemplatePath("templates");

        testling.setSimulatorConfiguration(simulatorConfiguration);
    }

    @Test
    public void testGetDefaultMessageTemplates() throws Exception {
        Assert.assertEquals(testling.getDefaultMessageTemplates().size(), 3L);
    }

    @Test
    public void testGetFileResource() throws Exception {
        Assert.assertTrue(testling.getFfmFileResource("createOrder").exists());
        Assert.assertFalse(testling.getFfmFileResource("unknown").exists());
    }
}
