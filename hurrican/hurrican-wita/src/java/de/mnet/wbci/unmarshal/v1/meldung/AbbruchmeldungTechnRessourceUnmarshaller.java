/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMTRType;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceBuilder;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionABBMTRUnmarshaller;

/**
 *
 */
@Component
public class AbbruchmeldungTechnRessourceUnmarshaller extends
        AbstractMeldungUnmarshaller<MeldungABBMTRType, AbbruchmeldungTechnRessource> {

    @Autowired
    private MeldungsPositionABBMTRUnmarshaller meldungsPositionABBMTRUnmarshaller;

    @Nullable
    @Override
    public AbbruchmeldungTechnRessource apply(@Nullable MeldungABBMTRType input) {
        if (input == null) {
            return null;
        }

        AbbruchmeldungTechnRessourceBuilder builder = new AbbruchmeldungTechnRessourceBuilder();

        if (!CollectionUtils.isEmpty(input.getPosition())) {
            for (MeldungsPositionABBMTRType positionABBMTRType : input.getPosition()) {
                builder.addMeldungPosition(meldungsPositionABBMTRUnmarshaller.apply(positionABBMTRType));
            }
        }
        AbbruchmeldungTechnRessource abbruchmeldungTechnRessource = builder.build();
        super.apply(abbruchmeldungTechnRessource, input);

        return abbruchmeldungTechnRessource;
    }
}
