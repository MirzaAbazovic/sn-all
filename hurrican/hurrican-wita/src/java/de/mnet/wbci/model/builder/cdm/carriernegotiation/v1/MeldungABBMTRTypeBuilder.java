/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMTRType;

/**
 *
 */
public class MeldungABBMTRTypeBuilder extends AbstractMeldungTypeBuilder<MeldungABBMTRType> {

    public MeldungABBMTRTypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungABBMTRType();
    }

    public MeldungABBMTRTypeBuilder withPositionList(List<MeldungsPositionABBMTRType> meldungsPositionABBMTRTypeList) {
        objectType.getPosition().addAll(meldungsPositionABBMTRTypeList);
        return this;
    }

    public MeldungABBMTRTypeBuilder withPosition(MeldungsPositionABBMTRType meldungsPositionABBMTRType) {
        objectType.getPosition().add(meldungsPositionABBMTRType);
        return this;
    }
}
