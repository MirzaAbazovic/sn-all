/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request.storno;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPaufType;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;

@Component
public class StornoAufhebungAufAnfrageMarshaller extends AbstractStornoAnfrageMarshaller<StornoAufhebungAufAnfrage, StornoAufhebungEKPaufType> {

    @Nullable
    @Override
    public StornoAufhebungEKPaufType apply(@Nullable StornoAufhebungAufAnfrage input) {
        StornoAufhebungEKPaufType output = V1_OBJECT_FACTORY.createStornoAufhebungEKPaufType();

        super.apply(input, output);

        return output;
    }

}
