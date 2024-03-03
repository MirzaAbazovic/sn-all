/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2012 10:56:59
 */
package de.augustakom.common;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;

public class AbstractSpringContextTest extends AbstractTestNGSpringContextTests {
    @BeforeSuite(alwaysRun = true)
    public void testSetupLogging() {
        InitializeLog4J.initializeLog4J("log4j-test");
        SLF4JBridgeHandler.install();
    }

}


