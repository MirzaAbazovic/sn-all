/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 18.09.13 
 */
package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionErledigtmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;

public class MeldungPositionErledigtmeldungTestBuilder extends MeldungPositionErledigtmeldungBuilder implements
        WbciTestBuilder<MeldungPositionErledigtmeldung> {

    @Override
    public MeldungPositionErledigtmeldung buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (meldungsCode == null) {
            meldungsCode = MeldungsCode.TV_OK;
        }

        if (StringUtils.isEmpty(meldungsText)) {
            meldungsText = MeldungsCode.TV_OK.getStandardText();
        }
        return build();
    }

}
