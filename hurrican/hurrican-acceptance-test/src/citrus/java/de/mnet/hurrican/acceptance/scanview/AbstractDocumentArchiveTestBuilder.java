/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.mnet.hurrican.acceptance.scanview;

import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 *
 */
public class AbstractDocumentArchiveTestBuilder extends AbstractHurricanTestBuilder {

    protected void simulatorUseCase(SimulatorUseCase useCase) {
        simulatorUseCase(useCase, DocumentArchiveTestVersion.V1);
    }

}
