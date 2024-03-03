/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;

/**
 *
 */
@Component
public class MeldungPositionUebernahmeRessourceMeldungMarshaller extends
        MeldungPositionMarshaller<MeldungPositionUebernahmeRessourceMeldung, MeldungsPositionAKMTRType> {

    @Nullable
    @Override
    public MeldungsPositionAKMTRType apply(@Nullable MeldungPositionUebernahmeRessourceMeldung input) {
        if (input != null) {
            MeldungsPositionAKMTRType meldungPosition = V1_OBJECT_FACTORY.createMeldungsPositionAKMTRType();
            super.apply(meldungPosition, input);

            return meldungPosition;
        }

        return null;
    }
}
