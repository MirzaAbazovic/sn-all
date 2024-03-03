/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockPortierungskennerType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class RufnummernblockPortierungskennerMeldungTypeTestBuilder extends RufnummernblockPortierungskennerTypeBuilder
        implements CarrierNegotiationMeldungTypeTestBuilder<RufnummernblockPortierungskennerType> {
    @Override
    public RufnummernblockPortierungskennerType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (isEmpty(objectType.getPortierungskennungPKIabg())) {
            withPortierungskennungPKIabg("D001");
        }
        RufnummernblockTypeTestBuilder.enrichWithTestData(objectType);
        return build();
    }
}
