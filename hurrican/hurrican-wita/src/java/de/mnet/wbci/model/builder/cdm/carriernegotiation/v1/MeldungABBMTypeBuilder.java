/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;
import javax.xml.datatype.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;

/**
 *
 */
public class MeldungABBMTypeBuilder extends AbstractMeldungTypeBuilder<MeldungABBMType> {

    public MeldungABBMTypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungABBMType();
    }

    public MeldungABBMTypeBuilder withPositionList(List<MeldungsPositionABBMType> meldungsPositionABBMTypeList) {
        objectType.getPosition().addAll(meldungsPositionABBMTypeList);
        return this;
    }

    public MeldungABBMTypeBuilder withPosition(MeldungsPositionABBMType meldungsPositionABBMType) {
        objectType.getPosition().add(meldungsPositionABBMType);
        return this;
    }

    public MeldungABBMTypeBuilder withBegruendung(String begruendung) {
        objectType.setBegruendung(begruendung);
        return this;
    }

    public MeldungABBMTypeBuilder withWechselTermin(XMLGregorianCalendar wechselTermin) {
        objectType.setWechseltermin(wechselTermin);
        return this;
    }

    public MeldungABBMTypeBuilder withAenderungsIdRef(String aenderungsIdRef) {
        objectType.setAenderungsIdRef(aenderungsIdRef);
        return this;
    }

    public MeldungABBMTypeBuilder withStornoIdRef(String stornoIdRef) {
        objectType.setStornoIdRef(stornoIdRef);
        return this;
    }

}
