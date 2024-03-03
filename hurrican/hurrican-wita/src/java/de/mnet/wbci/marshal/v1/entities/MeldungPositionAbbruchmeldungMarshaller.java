/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;
import de.mnet.wbci.model.MeldungPositionAbbmRufnummer;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;

@Component
public class MeldungPositionAbbruchmeldungMarshaller extends
        MeldungPositionMarshaller<MeldungPositionAbbruchmeldung, MeldungsPositionABBMType> {

    @Nullable
    @Override
    public MeldungsPositionABBMType apply(@Nullable MeldungPositionAbbruchmeldung input) {
        if (input != null) {
            MeldungsPositionABBMType meldungPosition = V1_OBJECT_FACTORY.createMeldungsPositionABBMType();

            if (CollectionUtils.isNotEmpty(input.getRufnummern())) {
                for (MeldungPositionAbbmRufnummer rufnummer : input.getRufnummern()) {
                    meldungPosition.getRufnummer().add(rufnummer.getRufnummer());
                }
            }

            super.apply(meldungPosition, input);

            return meldungPosition;
        }

        return null;
    }

}
