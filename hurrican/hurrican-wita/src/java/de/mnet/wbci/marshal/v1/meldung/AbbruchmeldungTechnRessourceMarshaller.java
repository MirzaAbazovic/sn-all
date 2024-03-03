/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;
import de.mnet.wbci.marshal.v1.entities.MeldungPositionAbbruchmeldungTechnResourceMarshaller;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;

@Component
public class AbbruchmeldungTechnRessourceMarshaller extends
        AbstractMeldungMarshaller<AbbruchmeldungTechnRessource, MeldungABBMTRType, MeldungPositionAbbruchmeldungTechnRessource, MeldungsPositionABBMType> {

    @Autowired
    private MeldungPositionAbbruchmeldungTechnResourceMarshaller meldungsPositionAbbruchmeldungMarshaller;

    @Nullable
    @Override
    public MeldungABBMTRType apply(@Nullable AbbruchmeldungTechnRessource meldung) {
        if (meldung != null) {
            MeldungABBMTRType meldungABBMType = V1_OBJECT_FACTORY.createMeldungABBMTRType();
            super.apply(meldung, meldungABBMType);
            return meldungABBMType;
        }
        return null;
    }

    @Override
    protected void applyPositionen(Set<MeldungPositionAbbruchmeldungTechnRessource> meldungsPositionen,
            MeldungABBMTRType output) {
        for (MeldungPositionAbbruchmeldungTechnRessource position : meldungsPositionen) {
            output.getPosition().add(meldungsPositionAbbruchmeldungMarshaller.apply(position));
        }
    }
}
