/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Strasse;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;

@Component
public class StandortUnmarshaller implements Function<StandortType, Standort> {

    @Nullable
    @Override
    public Standort apply(@Nullable StandortType input) {
        if (input == null) {
            return null;
        }

        Strasse strasse = (input.getStrasse() == null) ? null : new StrasseBuilder()
                .withStrassenname(input.getStrasse().getStrassenname())
                .withHausnummer(input.getStrasse().getHausnummer())
                .withHausnummernZusatz(input.getStrasse().getHausnummernZusatz())
                .build();

        Standort standort = new StandortBuilder()
                .withOrt(input.getOrt())
                .withPostleitzahl(input.getPostleitzahl())
                .withStrasse(strasse)
                .build();
        return standort;
    }

}
