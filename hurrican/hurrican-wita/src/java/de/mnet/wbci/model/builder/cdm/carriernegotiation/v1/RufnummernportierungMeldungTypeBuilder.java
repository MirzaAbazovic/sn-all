/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMeldungType;

/**
 *
 */
public class RufnummernportierungMeldungTypeBuilder extends V1AbstractBasicBuilder<RufnummernportierungMeldungType> {

    public RufnummernportierungMeldungTypeBuilder() {
        objectType = OBJECT_FACTORY.createRufnummernportierungMeldungType();
    }

    public RufnummernportierungMeldungTypeBuilder withPortierungRufnummern(
            PortierungRufnummernMeldungType portierungRufnummern) {
        objectType.setPortierungRufnummern(portierungRufnummern);
        return this;
    }

    public RufnummernportierungMeldungTypeBuilder withPortierungRufnummernbloecke(
            PortierungRufnummernbloeckeMeldungType portierungRufnummernbloecke) {
        objectType.setPortierungRufnummernbloecke(portierungRufnummernbloecke);
        return this;
    }
}
