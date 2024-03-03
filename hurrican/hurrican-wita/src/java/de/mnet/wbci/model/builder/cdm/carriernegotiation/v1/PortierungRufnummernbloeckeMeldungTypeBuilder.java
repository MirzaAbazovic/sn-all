/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockPortierungskennerType;

/**
 *
 */
public class PortierungRufnummernbloeckeMeldungTypeBuilder extends
        V1AbstractBasicBuilder<PortierungRufnummernbloeckeMeldungType> {

    public PortierungRufnummernbloeckeMeldungTypeBuilder() {
        objectType = OBJECT_FACTORY.createPortierungRufnummernbloeckeMeldungType();
    }

    public PortierungRufnummernbloeckeMeldungTypeBuilder withOnkzDurchwahlAbfragestelle(
            OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelle) {
        objectType.setOnkzDurchwahlAbfragestelle(onkzDurchwahlAbfragestelle);
        return this;
    }

    public PortierungRufnummernbloeckeMeldungTypeBuilder withRufnummernblockPortierungskenner(
            RufnummernblockPortierungskennerType rufnummernblockPortierungskennerType) {
        objectType.getZuPortierenderRufnummernblock().add(rufnummernblockPortierungskennerType);
        return this;
    }

    public PortierungRufnummernbloeckeMeldungTypeBuilder withRufnummernblockPortierungskennerList(
            List<RufnummernblockPortierungskennerType> rufnummernblockPortierungskennerList) {
        objectType.getZuPortierenderRufnummernblock().addAll(rufnummernblockPortierungskennerList);
        return this;
    }

}
