/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public class MeldungPositionAbbruchmeldungTechnRessourceTestBuilder extends
        MeldungPositionAbbruchmeldungTechnRessourceBuilder
        implements WbciTestBuilder<MeldungPositionAbbruchmeldungTechnRessource> {

    @Override
    public MeldungPositionAbbruchmeldungTechnRessource buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (meldungsCode == null) {
            meldungsCode = MeldungsCode.UETN_NM;
        }

        if (StringUtils.isEmpty(meldungsText)) {
            meldungsText = MeldungsCode.UETN_NM.getStandardText();
        }
        return build();
    }

    @Override
    public MeldungPositionAbbruchmeldungTechnRessourceTestBuilder withMeldungsCode(MeldungsCode meldungsCode) {
        return (MeldungPositionAbbruchmeldungTechnRessourceTestBuilder) super.withMeldungsCode(meldungsCode);
    }

}
