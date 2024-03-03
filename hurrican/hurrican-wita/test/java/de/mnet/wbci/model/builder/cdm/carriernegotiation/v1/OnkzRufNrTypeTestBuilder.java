/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class OnkzRufNrTypeTestBuilder<ORNT extends OnkzRufNrType> extends OnkzRufNrTypeBuilder<ORNT> implements
        CarrierNegotiationMeldungTypeTestBuilder<ORNT> {
    @Override
    public ORNT buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        enrichTestData(objectType);
        return build();
    }

    protected static <ORN extends OnkzRufNrType> void enrichTestData(ORN objectType) {
        if (isEmpty(objectType.getONKZ())) {
            objectType.setONKZ("89");
        }
        if (isEmpty(objectType.getRufnummer())) {
            objectType.setRufnummer("452000");
        }
    }
}
