/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.builder.FirmaBuilder;
import de.mnet.wbci.model.builder.PersonBuilder;

@Component
public class PersonOderFirmaUnmarshaller implements Function<PersonOderFirmaType, PersonOderFirma> {

    @Autowired
    private AnredeUnmarshaller anredeUnmarshaller;

    @Nullable
    @Override
    public PersonOderFirma apply(@Nullable PersonOderFirmaType input) {
        if (input == null) {
            return null;
        }

        PersonOderFirma personOderFirma = null;
        if (input.getFirma() != null) {
            personOderFirma = new FirmaBuilder()
                    .withFirmename(input.getFirma().getFirmenname())
                    .withFirmennamenZusatz(input.getFirma().getFirmennameZweiterTeil())
                    .withAnrede(anredeUnmarshaller.apply(input.getFirma().getAnrede()))
                    .build();
        }
        else if (input.getPerson() != null) {
            personOderFirma = new PersonBuilder()
                    .withNachname(input.getPerson().getNachname())
                    .withVorname(input.getPerson().getVorname())
                    .withAnrede(anredeUnmarshaller.apply(input.getPerson().getAnrede()))
                    .build();
        }

        return personOderFirma;
    }

}
