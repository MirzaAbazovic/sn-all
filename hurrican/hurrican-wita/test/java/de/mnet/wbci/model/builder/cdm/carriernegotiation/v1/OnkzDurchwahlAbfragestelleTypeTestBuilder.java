/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class OnkzDurchwahlAbfragestelleTypeTestBuilder extends OnkzDurchwahlAbfragestelleTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<OnkzDurchwahlAbfragestelleType> {
    @Override
    public OnkzDurchwahlAbfragestelleType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (isEmpty(objectType.getAbfragestelle())) {
            withAbfragestelle("0");
        }
        if (isEmpty(objectType.getONKZ())) {
            withOnkz("89");
        }
        if (isEmpty(objectType.getDurchwahlnummer())) {
            withDurchwahlnummer("1234");
        }
        return build();
    }
}
