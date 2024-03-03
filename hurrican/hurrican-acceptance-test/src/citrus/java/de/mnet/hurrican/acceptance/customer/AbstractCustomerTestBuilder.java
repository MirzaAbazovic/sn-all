/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.04.2015
 */
package de.mnet.hurrican.acceptance.customer;

import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 *
 */
public class AbstractCustomerTestBuilder extends AbstractHurricanTestBuilder {

    protected void simulatorUseCase(SimulatorUseCase useCase) {
        simulatorUseCase(useCase, CustomerTestVersion.V1);
    }
}
