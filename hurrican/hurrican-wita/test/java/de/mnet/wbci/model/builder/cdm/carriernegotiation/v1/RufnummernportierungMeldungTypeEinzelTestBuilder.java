/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMeldungType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class RufnummernportierungMeldungTypeEinzelTestBuilder extends RufnummernportierungMeldungTypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<RufnummernportierungMeldungType> {
    @Override
    public RufnummernportierungMeldungType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (objectType.getPortierungRufnummern() == null && objectType.getPortierungRufnummernbloecke() == null) {
            withPortierungRufnummern(new PortierungRufnummernMeldungTypeTestBuilder().buildValid(
                    geschaeftsfallEnumType));
        }

        assert !(objectType.getPortierungRufnummern() != null && objectType.getPortierungRufnummernbloecke() != null) : "RufnummerportierungMeldungType could only have a PortierungRufnummernMeldungType OR a PortierungRufnummernbloeckeType";
        return build();
    }
}
