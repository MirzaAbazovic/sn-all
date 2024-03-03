/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernMeldungType;

/**
 *
 */
public class PortierungRufnummernMeldungTypePortierungTestBuilder extends PortierungRufnummernMeldungTypeTestBuilder {
    @Override
    public PortierungRufnummernMeldungType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (objectType.getZuPortierendeOnkzRnr() == null || objectType.getZuPortierendeOnkzRnr().size() == 0) {
            withOnkzRufNrPortierungskenner(new OnkzRufNrPortierungskennerTypeTestBuilder().buildValid(
                    geschaeftsfallEnumType));
        }
        return build();
    }
}
