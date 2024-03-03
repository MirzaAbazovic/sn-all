/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import static org.apache.commons.lang.StringUtils.*;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AdresseAbweichendType;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Strasse;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;

/**
 *
 */
@Component
public class AdresseAbweichendUnmarshaller implements Function<AdresseAbweichendType, Standort> {
    @Nullable
    @Override
    public Standort apply(@Nullable AdresseAbweichendType input) {
        if (input != null) {
            Strasse strasse = null;
            if (isStreetNameOrNumberSet(input)) {
                strasse = new StrasseBuilder()
                        .withHausnummer(input.getHausnummer())
                        .withStrassenname(input.getStrassenname())
                        .build();
            }

            return new StandortBuilder()
                    .withOrt(input.getOrt())
                    .withPostleitzahl(input.getPostleitzahl())
                    .withStrasse(strasse)
                    .build();
        }
        return null;
    }

    private boolean isStreetNameOrNumberSet(AdresseAbweichendType input) {
        return isNotEmpty(input.getStrassenname()) || isNotEmpty(input.getHausnummer());
    }
}
