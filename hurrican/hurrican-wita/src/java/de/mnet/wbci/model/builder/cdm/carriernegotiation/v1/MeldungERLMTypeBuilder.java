/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;
import javax.xml.datatype.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;

/**
 *
 */
public class MeldungERLMTypeBuilder extends AbstractMeldungTypeBuilder<MeldungERLMType> {

    public MeldungERLMTypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungERLMType();
    }

    public MeldungERLMTypeBuilder withPositionList(List<MeldungsPositionERLMType> meldungsPositionERLMTypeList) {
        objectType.getPosition().addAll(meldungsPositionERLMTypeList);
        return this;
    }

    public MeldungERLMTypeBuilder withPosition(MeldungsPositionERLMType meldungsPositionERLMType) {
        objectType.getPosition().add(meldungsPositionERLMType);
        return this;
    }

    public MeldungERLMTypeBuilder withWechselTermin(XMLGregorianCalendar wechselTermin) {
        objectType.setWechseltermin(wechselTermin);
        return this;
    }

    public MeldungERLMTypeBuilder withAenderungsIdRef(String aenderungsIdRef) {
        objectType.setAenderungsIdRef(aenderungsIdRef);
        return this;
    }

    public MeldungERLMTypeBuilder withStornoIdRef(String stornoIdRef) {
        objectType.setStornoIdRef(stornoIdRef);
        return this;
    }
}
