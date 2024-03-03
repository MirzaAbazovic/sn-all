/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 20.09.13 
 */
package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public class StornoAenderungAufAnfrageTestBuilder<GF extends WbciGeschaeftsfall> extends
        StornoAenderungAufAnfrageBuilder<GF> implements
        WbciTestBuilder<StornoAenderungAufAnfrage<GF>> {

    private boolean withoutProcessedAt;

    @Override
    public StornoAenderungAufAnfrage<GF> buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        StornoTestBuilder.enrich(wbciRequest, wbciCdmVersion, gfTyp);
        if (withoutProcessedAt) {
            wbciRequest.setProcessedAt(null);
        }
        return build();
    }

    @Override
    public StornoAenderungAufAnfrageTestBuilder<GF> withSendAfter(LocalDateTime sendAfter) {
        super.withSendAfter(sendAfter);
        return this;
    }

    public StornoAenderungAufAnfrageTestBuilder<GF> withoutProcessedAt() {
        this.withoutProcessedAt = true;
        return this;
    }

}
