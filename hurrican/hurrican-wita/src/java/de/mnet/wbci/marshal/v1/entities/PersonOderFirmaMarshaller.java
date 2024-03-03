/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.FirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;

/**
 *
 */
@Component
public class PersonOderFirmaMarshaller extends AbstractBaseMarshaller implements
        Function<PersonOderFirma, PersonOderFirmaType> {

    @Autowired
    private AnredeMarshaller anredeMarshaller;

    @Nullable
    @Override
    public PersonOderFirmaType apply(@Nullable PersonOderFirma input) {
        if (input == null) {
            return null;
        }
        else {
            PersonOderFirmaType personOderFirmaType = V1_OBJECT_FACTORY.createPersonOderFirmaType();

            if (input instanceof Person) {
                PersonType personType = V1_OBJECT_FACTORY.createPersonType();
                personType.setAnrede(anredeMarshaller.apply(input.getAnrede()));
                Person person = (Person) input;
                personType.setNachname(person.getNachname());
                personType.setVorname(person.getVorname());
                personOderFirmaType.setPerson(personType);
            }
            else if (input instanceof Firma) {
                FirmaType firmaType = V1_OBJECT_FACTORY.createFirmaType();
                firmaType.setAnrede(anredeMarshaller.apply(input.getAnrede()));
                Firma firma = (Firma) input;
                firmaType.setFirmenname(firma.getFirmenname());
                firmaType.setFirmennameZweiterTeil(firma.getFirmennamenZusatz());
                personOderFirmaType.setFirma(firmaType);
            }
            return personOderFirmaType;
        }
    }

}
