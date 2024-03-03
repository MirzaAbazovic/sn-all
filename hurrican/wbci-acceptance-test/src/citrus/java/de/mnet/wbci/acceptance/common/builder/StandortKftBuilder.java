/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.StandortBuilder;

/**
 *
 */
public class StandortKftBuilder extends StandortBuilder {

    public StandortKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withPostleitzahl("80992");
        withOrt("München");
        withStrasse(new StrasseKftBuilder(wbciCdmVersion).build());
    }
}
