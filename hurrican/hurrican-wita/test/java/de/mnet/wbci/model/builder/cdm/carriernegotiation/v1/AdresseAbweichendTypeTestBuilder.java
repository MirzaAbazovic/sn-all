/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AdresseAbweichendType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class AdresseAbweichendTypeTestBuilder extends AdresseAbweichendTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<AdresseAbweichendType> {
    @Override
    public AdresseAbweichendType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (isEmpty(objectType.getHausnummer())) {
            withHausnummer("5");
        }
        return build();
    }
}
