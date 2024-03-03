/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import org.apache.commons.lang.StringUtils;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMTRType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionABBMTRTypeTestBuilder extends MeldungsPositionABBMTRTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungsPositionABBMTRType> {

    @Override
    public MeldungsPositionABBMTRType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (StringUtils.isEmpty(objectType.getMeldungscode())) {
            objectType.setMeldungscode(MeldungsCode.UETN_NM.getCode());
        }
        if (StringUtils.isEmpty(objectType.getMeldungstext())) {
            objectType.setMeldungstext(MeldungsCode.UETN_NM.getStandardText());
        }
        return build();
    }
}
