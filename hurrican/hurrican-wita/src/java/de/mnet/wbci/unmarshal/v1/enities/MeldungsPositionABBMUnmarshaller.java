/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;

/**
 *
 */
@Component
public class MeldungsPositionABBMUnmarshaller extends
        AbstractMeldungsPositionUnmarshaller<MeldungsPositionABBMType, MeldungPositionAbbruchmeldung> {

    @Nullable
    @Override
    public MeldungPositionAbbruchmeldung apply(@Nullable MeldungsPositionABBMType input) {
        if (input != null) {
            MeldungPositionAbbruchmeldungBuilder builder = new MeldungPositionAbbruchmeldungBuilder();
            if (!CollectionUtils.isEmpty(input.getRufnummer())) {
                for (String rufnummer : input.getRufnummer()) {
                    builder.withRufnummer(rufnummer);
                }
            }

            MeldungPositionAbbruchmeldung position = builder.build();
            super.apply(position, input);
            return position;
        }
        return null;
    }

}
