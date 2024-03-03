/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionABBMTypeTestBuilder extends MeldungsPositionABBMTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungsPositionABBMType> {

    @Override
    public MeldungsPositionABBMType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (StringUtils.isEmpty(objectType.getMeldungscode())) {
            objectType.setMeldungscode(MeldungsCode.RNG.getCode());
        }
        if (StringUtils.isEmpty(objectType.getMeldungstext())) {
            objectType.setMeldungstext(MeldungsCode.RNG.getStandardText());
        }
        if (CollectionUtils.isEmpty(objectType.getRufnummer())) {
            objectType.getRufnummer().add("08912345678");
            objectType.getRufnummer().add("08912345679");
        }
        return build();
    }

}
