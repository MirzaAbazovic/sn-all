/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class RufnummernblockTypeTestBuilder extends RufnummernblockTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<RufnummernblockType> {
    @Override
    public RufnummernblockType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        enrichWithTestData(objectType);
        return build();
    }

    public static <RBT extends RufnummernblockType> void enrichWithTestData(RBT objectType) {
        if (isEmpty(objectType.getRnrBlockVon())) {
            objectType.setRnrBlockVon("00");
        }
        if (isEmpty(objectType.getRnrBlockBis())) {
            objectType.setRnrBlockBis("99");
        }
    }
}
