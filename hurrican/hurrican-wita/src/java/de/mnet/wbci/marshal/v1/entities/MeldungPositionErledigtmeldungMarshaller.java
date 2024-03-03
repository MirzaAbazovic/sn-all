/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;
import de.mnet.wbci.model.MeldungPositionErledigtmeldung;

/**
 *
 */
@Component
public class MeldungPositionErledigtmeldungMarshaller extends
        MeldungPositionMarshaller<MeldungPositionErledigtmeldung, MeldungsPositionERLMType> {

    @Nullable
    @Override
    public MeldungsPositionERLMType apply(@Nullable MeldungPositionErledigtmeldung input) {
        if (input != null) {
            MeldungsPositionERLMType meldungsPosition = V1_OBJECT_FACTORY.createMeldungsPositionERLMType();
            super.apply(meldungsPosition, input);
            return meldungsPosition;
        }
        return null;
    }
}
