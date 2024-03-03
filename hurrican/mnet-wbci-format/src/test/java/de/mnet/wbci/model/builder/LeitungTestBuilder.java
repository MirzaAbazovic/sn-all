/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public class LeitungTestBuilder extends LeitungBuilder implements WbciTestBuilder<Leitung> {

    public Leitung buildValidLineId(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        vertragsnummer = null;

        if (lineId == null) {
            lineId = CarrierCode.DTAG.getITUCarrierCode() + ".12345";
        }

        return build();
    }

    public Leitung buildValidVertragsnummer(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        lineId = null;

        if (vertragsnummer == null) {
            vertragsnummer = "V123456";
        }

        return build();
    }

    @Override
    public Leitung buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        return buildValidLineId(wbciCdmVersion, gfTyp);
    }
}
