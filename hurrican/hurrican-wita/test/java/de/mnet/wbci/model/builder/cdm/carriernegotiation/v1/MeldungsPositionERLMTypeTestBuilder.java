/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import org.apache.commons.lang.StringUtils;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionERLMTypeTestBuilder extends MeldungsPositionERLMTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungsPositionERLMType> {

    @Override
    public MeldungsPositionERLMType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (StringUtils.isEmpty(objectType.getMeldungscode())) {
            objectType.setMeldungscode(MeldungsCode.ZWA.getCode());
        }
        if (StringUtils.isEmpty(objectType.getMeldungstext())) {
            objectType.setMeldungstext(MeldungsCode.ZWA.getStandardText());
        }
        return build();
    }
}
