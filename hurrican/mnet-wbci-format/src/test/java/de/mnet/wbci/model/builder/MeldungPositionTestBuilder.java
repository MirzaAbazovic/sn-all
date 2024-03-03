/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.13
 */
package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public abstract class MeldungPositionTestBuilder extends MeldungPositionBuilder {

    public static void enrich(MeldungPositionBuilder builder, WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (builder.meldungsCode == null) {
            builder.meldungsCode = MeldungsCode.ZWA;
        }

        if (StringUtils.isEmpty(builder.meldungsText)) {
            builder.meldungsText = builder.meldungsCode.getStandardText();
        }
    }
}
