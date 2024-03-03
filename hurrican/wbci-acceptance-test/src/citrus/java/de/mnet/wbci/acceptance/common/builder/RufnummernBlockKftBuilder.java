/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;

/**
 *
 */
public class RufnummernBlockKftBuilder extends RufnummernblockBuilder {
    public RufnummernBlockKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withRnrBlockVon("00");
        withRnrBlockBis("99");
    }
}
