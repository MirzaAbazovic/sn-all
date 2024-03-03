/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import org.apache.commons.collections.CollectionUtils;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class MeldungERLMTypeTestBuilder extends MeldungERLMTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungERLMType> {

    @Override
    public MeldungERLMType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        AbstractMeldungTypeTestBuilder.enrichTestData(objectType, geschaeftsfallEnumType);
        if (CollectionUtils.isEmpty(objectType.getPosition())) {
            withPosition(new MeldungsPositionERLMTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }

        if (objectType.getWechseltermin() == null) {
            withWechselTermin(DateConverterUtils.toXmlGregorianCalendar(DateCalculationHelper.getDateInWorkingDaysFromNow(8).toLocalDate()));
        }

        if (isEmpty(objectType.getStornoIdRef())) {
            withStornoIdRef("DEU.EKP1.S000000013");
        }

        if (isEmpty(objectType.getAenderungsIdRef())) {
            withAenderungsIdRef("DEU.EKP1.T000000012");
        }

        return build();
    }

}
