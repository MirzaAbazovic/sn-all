/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import org.apache.commons.collections.CollectionUtils;

import de.augustakom.hurrican.model.TNB;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class MeldungAKMTRTypeTestBuilder extends MeldungAKMTRTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungAKMTRType> {

    @Override
    public MeldungAKMTRType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        AbstractMeldungTypeTestBuilder.enrichTestData(objectType, geschaeftsfallEnumType);
        if (CollectionUtils.isEmpty(objectType.getPosition())) {
            addPosition(new MeldungsPositionAKMTRTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        if (CollectionUtils.isEmpty(objectType.getUebernahmeLeitung())) {
            addUebernahmeLeitung(new UebernahmeLeitungTypeTestTypeBuilder().buildValid());
        }
        if (isEmpty(objectType.getPortierungskennungPKIauf())) {
            withPortierungskennungPKIauf(TNB.AKOM.tnbKennung);
        }
        return build();
    }

}
