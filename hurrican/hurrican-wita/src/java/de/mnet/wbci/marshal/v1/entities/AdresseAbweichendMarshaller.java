/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AdresseAbweichendType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.AdresseAbweichendTypeBuilder;

/**
 *
 */
@Component
public class AdresseAbweichendMarshaller extends AbstractBaseMarshaller implements Function<Standort, AdresseAbweichendType> {
    @Nullable
    @Override
    public AdresseAbweichendType apply(@Nullable Standort input) {
        if (input != null) {
            AdresseAbweichendTypeBuilder type = new AdresseAbweichendTypeBuilder();

            if (input.getStrasse() != null && input.getStrasse().getStrassenname() != null) {
                type.withStrassenname(input.getStrasse().getStrassenname());
            }

            if (input.getStrasse() != null && input.getStrasse().getHausnummer() != null) {
                type.withHausnummer(input.getStrasse().getHausnummer());
            }

            if (input.getOrt() != null) {
                type.withOrt(input.getOrt());
            }

            if (input.getPostleitzahl() != null) {
                type.withPostleitzahl(input.getPostleitzahl());
            }

            return type.build();
        }

        return null;
    }
}
