/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public class MeldungPositionAbbruchmeldungTestBuilder extends MeldungPositionAbbruchmeldungBuilder
        implements WbciTestBuilder<MeldungPositionAbbruchmeldung> {

    @Override
    public MeldungPositionAbbruchmeldung buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (meldungsCode == null) {
            meldungsCode = MeldungsCode.RNG;
        }

        if (StringUtils.isEmpty(meldungsText)) {
            meldungsText = meldungsCode.getStandardText();
        }

        if (rufnummern == null && meldungsCode.equals(MeldungsCode.RNG)) {
            this.withRufnummer("08912345678").withRufnummer("08912345679");
        }

        return build();
    }

    @Override
    public MeldungPositionAbbruchmeldungTestBuilder withMeldungsCode(MeldungsCode meldungsCode) {
        return (MeldungPositionAbbruchmeldungTestBuilder) super.withMeldungsCode(meldungsCode);
    }
}
