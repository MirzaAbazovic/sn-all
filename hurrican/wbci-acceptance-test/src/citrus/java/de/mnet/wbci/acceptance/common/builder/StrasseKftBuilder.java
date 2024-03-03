/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.StrasseBuilder;

/**
 *
 */
public class StrasseKftBuilder extends StrasseBuilder {

    public StrasseKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withHausnummer("2");
        withHausnummernZusatz("a");
        withStrassenname("Emmy-Noether-Straße");
    }
}
