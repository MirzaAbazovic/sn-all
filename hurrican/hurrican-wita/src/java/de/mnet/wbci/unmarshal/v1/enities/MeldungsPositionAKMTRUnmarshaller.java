/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;
import de.mnet.wbci.model.builder.MeldungPositionUebernahmeRessourceMeldungBuilder;

/**
 *
 */
@Component
public class MeldungsPositionAKMTRUnmarshaller extends
        AbstractMeldungsPositionUnmarshaller<MeldungsPositionAKMTRType, MeldungPosition> {

    @Nullable
    @Override
    public MeldungPositionUebernahmeRessourceMeldung apply(@Nullable MeldungsPositionAKMTRType input) {
        if (input != null) {
            MeldungPositionUebernahmeRessourceMeldung position = new MeldungPositionUebernahmeRessourceMeldungBuilder().build();
            super.apply(position, input);
            return position;
        }
        return null;
    }

}
