/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.2014
 */
package de.mnet.hurrican.acceptance.ffm;

import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 *
 */
public abstract class AbstractFfmTestBuilder extends AbstractHurricanTestBuilder {


    protected void simulatorUseCase(SimulatorUseCase useCase) {
        simulatorUseCase(useCase, FfmTestVersion.V1);
    }
}
