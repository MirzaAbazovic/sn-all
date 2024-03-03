/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UebernahmeLeitungType;

/**
 *
 */
public class MeldungAKMTRTypeBuilder extends AbstractMeldungTypeBuilder<MeldungAKMTRType> {

    public MeldungAKMTRTypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungAKMTRType();
    }

    public MeldungAKMTRTypeBuilder withPositionList(List<MeldungsPositionAKMTRType> meldungsPositionABBMTRTypeList) {
        objectType.getPosition().addAll(meldungsPositionABBMTRTypeList);
        return this;
    }

    public MeldungAKMTRTypeBuilder addPosition(MeldungsPositionAKMTRType meldungsPositionABBMTRType) {
        objectType.getPosition().add(meldungsPositionABBMTRType);
        return this;
    }

    public MeldungAKMTRTypeBuilder addUebernahmeLeitung(UebernahmeLeitungType uebernahmeLeitungType) {
        objectType.getUebernahmeLeitung().add(uebernahmeLeitungType);
        return this;
    }

    public MeldungAKMTRTypeBuilder withPortierungskennungPKIauf(String portierungskennungPKIauf) {
        objectType.setPortierungskennungPKIauf(portierungskennungPKIauf);
        return this;
    }

    public MeldungAKMTRTypeBuilder withSichererHafen(boolean sichererHafen) {
        objectType.setSichererHafen(sichererHafen);
        return this;
    }

    public MeldungAKMTRTypeBuilder withRessourcenuebernahme(boolean ressourcenuebernahme) {
        objectType.setRessourcenuebernahme(ressourcenuebernahme);
        return this;
    }

}
