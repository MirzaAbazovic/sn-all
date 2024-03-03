/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import org.apache.commons.collections.CollectionUtils;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class MeldungABBMTRTypeTestBuilder extends MeldungABBMTRTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungABBMTRType> {
    @Override
    public MeldungABBMTRType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        AbstractMeldungTypeTestBuilder.enrichTestData(objectType, geschaeftsfallEnumType);
        if (CollectionUtils.isEmpty(objectType.getPosition())) {
            withPosition(new MeldungsPositionABBMTRTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        return build();
    }
}
