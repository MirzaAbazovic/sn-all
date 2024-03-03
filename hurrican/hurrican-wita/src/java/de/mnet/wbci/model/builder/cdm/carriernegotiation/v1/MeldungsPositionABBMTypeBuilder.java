/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;

/**
 *
 */
public class MeldungsPositionABBMTypeBuilder extends AbstractMeldungsPositionTypeBuilder<MeldungsPositionABBMType> {

    public MeldungsPositionABBMTypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungsPositionABBMType();
    }

    public MeldungsPositionABBMTypeBuilder withRufnummer(String rufnummer) {
        objectType.getRufnummer().add(rufnummer);
        return this;
    }

    public MeldungsPositionABBMTypeBuilder withRufnummern(List<String> rufnummern) {
        objectType.getRufnummer().addAll(rufnummern);
        return this;
    }

}
