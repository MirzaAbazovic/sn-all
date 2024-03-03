/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungsPositionType;

/**
 *
 */
public abstract class AbstractMeldungsPositionTypeBuilder<MPOS extends AbstractMeldungsPositionType> extends V1AbstractBasicBuilder<MPOS> {

    public AbstractMeldungsPositionTypeBuilder withMeldungscode(String meldungscode) {
        objectType.setMeldungscode(meldungscode);
        return this;
    }

    public AbstractMeldungsPositionTypeBuilder withMeldungstext(String meldungstext) {
        objectType.setMeldungstext(meldungstext);
        return this;
    }

}
