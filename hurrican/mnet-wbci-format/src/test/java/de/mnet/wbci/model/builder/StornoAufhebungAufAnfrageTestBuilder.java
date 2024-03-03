/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 20.09.13 
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequestStatus;

public class StornoAufhebungAufAnfrageTestBuilder<GF extends WbciGeschaeftsfall> extends
        StornoAufhebungAufAnfrageBuilder<GF> implements
        WbciTestBuilder<StornoAufhebungAufAnfrage<GF>> {

    @Override
    public StornoAufhebungAufAnfrage<GF> buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        StornoTestBuilder.enrich(wbciRequest, wbciCdmVersion, gfTyp);
        return build();
    }

    @Override
    public StornoAufhebungAufAnfrageTestBuilder<GF> withRequestStatus(WbciRequestStatus requestStatus) {
        super.withRequestStatus(requestStatus);
        return this;
    }

    @Override
    public StornoAufhebungAufAnfrageTestBuilder<GF> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
        return this;
    }
}
