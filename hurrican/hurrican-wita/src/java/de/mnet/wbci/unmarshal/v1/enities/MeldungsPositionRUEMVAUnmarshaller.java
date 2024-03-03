/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaBuilder;

/**
 *
 */
@Component
public class MeldungsPositionRUEMVAUnmarshaller extends
        AbstractMeldungsPositionUnmarshaller<MeldungsPositionRUEMVAType, MeldungPositionRueckmeldungVa> {

    @Autowired
    private AdresseAbweichendUnmarshaller adresseAbweichendUnmarshaller;

    @Nullable
    @Override
    public MeldungPositionRueckmeldungVa apply(MeldungsPositionRUEMVAType input) {
        if (input != null) {
            MeldungPositionRueckmeldungVa position = new MeldungPositionRueckmeldungVaBuilder()
                    .withStandortAbweichend(adresseAbweichendUnmarshaller.apply(input.getAdresseAbweichend()))
                    .build();

            super.apply(position, input);
            return position;
        }
        return null;

    }
}
