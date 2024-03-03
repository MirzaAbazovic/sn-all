/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.mnet.hurrican.atlas.simulator.builder;

import com.consol.citrus.TestCase;
import com.consol.citrus.context.TestContext;
import org.springframework.context.ApplicationContext;

import de.mnet.hurrican.atlas.simulator.AbstractSimulatorTestBuilder;

/**
 *
 */
public class MockTestBuilder extends AbstractSimulatorTestBuilder {

    /**
     * Default constructor used by mocked test builder objects
     */
    public MockTestBuilder() {
    }

    /**
     * Default constructor with Spring application context used when instantiating test builder instances in unit tests
     *
     * @param applicationContext SpringApplicationContext
     */
    public MockTestBuilder(ApplicationContext applicationContext) {
        setApplicationContext(applicationContext);
    }

    @Override
    public void execute(TestContext context) {
        configure();
        verifyTestCase(getTestCase());
    }

    @Override
    public String getUseCaseName() {
        return "MockTestBuilder";
    }

    protected void verifyTestCase(TestCase testCase) {
    }

    @Override
    protected String getInterfaceName() {
        return "mock";
    }
}
