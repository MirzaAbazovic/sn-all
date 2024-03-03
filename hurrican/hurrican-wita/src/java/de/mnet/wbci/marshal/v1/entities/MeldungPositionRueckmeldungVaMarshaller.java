/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;

/**
 *
 */
@Component
public class MeldungPositionRueckmeldungVaMarshaller extends
        MeldungPositionMarshaller<MeldungPositionRueckmeldungVa, MeldungsPositionRUEMVAType> {

    @Autowired
    private AdresseAbweichendMarshaller adresseAbweichendMarshaller;

    @Nullable
    @Override
    public MeldungsPositionRUEMVAType apply(@Nullable MeldungPositionRueckmeldungVa input) {
        if (input != null) {
            MeldungsPositionRUEMVAType meldungPosition = V1_OBJECT_FACTORY.createMeldungsPositionRUEMVAType();
            meldungPosition.setAdresseAbweichend(adresseAbweichendMarshaller.apply(input.getStandortAbweichend()));

            super.apply(meldungPosition, input);

            return meldungPosition;
        }

        return null;
    }
}
