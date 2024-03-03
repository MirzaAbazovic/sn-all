/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.storno;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPaufType;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;

/**
 *
 */
@Component
public class StornoAufhebungAufAnfrageUnmarshaller extends AbstractStornoAnfrageUnmarshaller<StornoAufhebungEKPaufType, StornoAufhebungAufAnfrage> {

    @Nullable
    @Override
    public StornoAufhebungAufAnfrage apply(@Nullable StornoAufhebungEKPaufType input) {
        StornoAufhebungAufAnfrage output = new StornoAufhebungAufAnfrage();

        super.apply(input, output);

        return output;
    }
}
