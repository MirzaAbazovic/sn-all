/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrPortierungskennerType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernMeldungType;

/**
 *
 */
public class PortierungRufnummernMeldungTypeBuilder extends V1AbstractBasicBuilder<PortierungRufnummernMeldungType> {

    public PortierungRufnummernMeldungTypeBuilder() {
        objectType = OBJECT_FACTORY.createPortierungRufnummernMeldungType();
    }

    public PortierungRufnummernMeldungTypeBuilder withOnkzRufNrPortierungskennerList(
            List<OnkzRufNrPortierungskennerType> onkzRufNrTypeList) {
        objectType.getZuPortierendeOnkzRnr().addAll(onkzRufNrTypeList);
        return this;
    }

    public PortierungRufnummernMeldungTypeBuilder withOnkzRufNrPortierungskenner(
            OnkzRufNrPortierungskennerType onkzRufNrType) {
        objectType.getZuPortierendeOnkzRnr().add(onkzRufNrType);
        return this;
    }

}
