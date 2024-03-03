/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.LeitungBuilder;

/**
 *
 */
public class LeitungKftBuilder extends LeitungBuilder {

    public LeitungKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withVertragsnummer("V123456");
    }
}
