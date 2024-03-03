/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.07.13
 */
package de.mnet.hurrican.atlas.simulator.wita;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
@ContextConfiguration({ "classpath:simulator-test-context.xml" })
public abstract class AbstractSimulatorBaseTest extends AbstractTestNGSpringContextTests {
}
