/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMTRType;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;

@Component
public class MeldungPositionAbbruchmeldungTechnResourceMarshaller extends
        MeldungPositionMarshaller<MeldungPositionAbbruchmeldungTechnRessource, MeldungsPositionABBMTRType> {

    @Nullable
    @Override
    public MeldungsPositionABBMTRType apply(@Nullable MeldungPositionAbbruchmeldungTechnRessource input) {
        if (input != null) {
            MeldungsPositionABBMTRType meldungPosition = V1_OBJECT_FACTORY.createMeldungsPositionABBMTRType();
            super.apply(meldungPosition, input);

            return meldungPosition;
        }
        return null;
    }

}
