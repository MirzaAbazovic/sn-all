/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class TechnischeRessourceTypeTestBuilder extends TechnischeRessourceTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<TechnischeRessourceType> {
    @Override
    public TechnischeRessourceType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (isEmpty(objectType.getIdentifizierer())) {
            withIdentifizierer("I12345");
        }
        if (isEmpty(objectType.getLineID())) {
            withLineID(CarrierCode.DTAG + ".12345");
        }
        if (isEmpty(objectType.getKennungTNBabg())) {
            withLineID("D001");
        }
        if (isEmpty(objectType.getVertragsnummer())) {
            withLineID("V123456");
        }
        return super.build();
    }
}
