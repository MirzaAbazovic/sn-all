/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;

public class MeldungPositionUebernahmeRessourceMeldungTestBuilder extends
        MeldungPositionUebernahmeRessourceMeldungBuilder
        implements WbciTestBuilder<MeldungPositionUebernahmeRessourceMeldung> {

    @Override
    public MeldungPositionUebernahmeRessourceMeldung buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (meldungsCode == null) {
            meldungsCode = MeldungsCode.AKMTR_CODE;
        }

        if (StringUtils.isEmpty(meldungsText)) {
            meldungsText = meldungsCode.getStandardText();
        }
        return build();
    }

}
