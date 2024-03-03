/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMTRType;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceBuilder;

/**
 *
 */
@Component
public class MeldungsPositionABBMTRUnmarshaller extends
        AbstractMeldungsPositionUnmarshaller<MeldungsPositionABBMTRType, MeldungPositionAbbruchmeldungTechnRessource> {
    @Nullable
    @Override
    public MeldungPositionAbbruchmeldungTechnRessource apply(@Nullable MeldungsPositionABBMTRType input) {
        if (input != null) {
            MeldungPositionAbbruchmeldungTechnRessource position = new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                    .build();
            super.apply(position, input);
            return position;
        }
        return null;
    }
}
