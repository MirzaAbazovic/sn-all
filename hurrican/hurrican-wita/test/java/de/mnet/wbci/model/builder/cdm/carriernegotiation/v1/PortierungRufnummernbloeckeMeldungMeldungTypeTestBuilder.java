/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class PortierungRufnummernbloeckeMeldungMeldungTypeTestBuilder extends PortierungRufnummernbloeckeMeldungTypeBuilder
        implements CarrierNegotiationMeldungTypeTestBuilder<PortierungRufnummernbloeckeMeldungType> {
    @Override
    public PortierungRufnummernbloeckeMeldungType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (objectType.getOnkzDurchwahlAbfragestelle() == null) {
            withOnkzDurchwahlAbfragestelle(new OnkzDurchwahlAbfragestelleTypeTestBuilder().buildValid(
                    geschaeftsfallEnumType));
        }
        if (objectType.getZuPortierenderRufnummernblock() == null
                || objectType.getZuPortierenderRufnummernblock().size() == 0) {
            withRufnummernblockPortierungskenner(new RufnummernblockPortierungskennerMeldungTypeTestBuilder().buildValid(
                    geschaeftsfallEnumType));
        }
        return build();
    }
}
