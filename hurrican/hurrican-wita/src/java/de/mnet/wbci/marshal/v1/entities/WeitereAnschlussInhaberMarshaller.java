/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.PersonOderFirma;

/**
 *
 */
@Component
public class WeitereAnschlussInhaberMarshaller extends AbstractBaseMarshaller implements
        Function<List<PersonOderFirma>, List<PersonOderFirmaType>> {

    @Autowired
    private PersonOderFirmaMarshaller personOderFirmaMarshaller;

    @Nullable
    @Override
    public List<PersonOderFirmaType> apply(@Nullable List<PersonOderFirma> input) {
        if (input == null) {
            return null;
        }

        List<PersonOderFirmaType> personOderFirmaTypeList = new ArrayList<>();
        for (PersonOderFirma personOderFirma : input) {
            personOderFirmaTypeList.add(personOderFirmaMarshaller.apply(personOderFirma));
        }
        return personOderFirmaTypeList;
    }
}
