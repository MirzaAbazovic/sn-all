/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import org.apache.commons.lang.StringUtils;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionAKMTRTypeTestBuilder extends MeldungsPositionAKMTRTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungsPositionAKMTRType> {

    @Override
    public MeldungsPositionAKMTRType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (StringUtils.isEmpty(objectType.getMeldungscode())) {
            objectType.setMeldungscode(MeldungsCode.AKMTR_CODE.getCode());
        }
        if (StringUtils.isEmpty(objectType.getMeldungstext())) {
            objectType.setMeldungstext(MeldungsCode.AKMTR_CODE.getStandardText());
        }
        return build();
    }

}
