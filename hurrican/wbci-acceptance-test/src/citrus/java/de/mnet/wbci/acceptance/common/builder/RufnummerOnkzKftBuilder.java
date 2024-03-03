/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;

/**
 *
 */
public class RufnummerOnkzKftBuilder extends RufnummerOnkzBuilder {
    public RufnummerOnkzKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withOnkz("089");
        withRufnummer("123456789");
        withPortierungKennungPKIabg("D123");
    }
}
