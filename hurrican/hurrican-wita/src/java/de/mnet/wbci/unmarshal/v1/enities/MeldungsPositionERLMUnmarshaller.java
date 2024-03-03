/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;
import de.mnet.wbci.model.MeldungPositionErledigtmeldung;

/**
 *
 */
@Component
public class MeldungsPositionERLMUnmarshaller extends
        AbstractMeldungsPositionUnmarshaller<MeldungsPositionERLMType, MeldungPositionErledigtmeldung> {
    @Nullable
    @Override
    public MeldungPositionErledigtmeldung apply(@Nullable MeldungsPositionERLMType input) {
        if (input != null) {
            MeldungPositionErledigtmeldung meldungPosition = new MeldungPositionErledigtmeldung();
            super.apply(meldungPosition, input);

            return meldungPosition;
        }

        return null;
    }
}
