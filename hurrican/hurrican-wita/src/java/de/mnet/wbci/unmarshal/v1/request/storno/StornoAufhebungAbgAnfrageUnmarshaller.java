/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.storno;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPabgType;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;

/**
 *
 */
@Component
public class StornoAufhebungAbgAnfrageUnmarshaller extends AbstractStornoMitEndkundeStandortUnmarshaller<StornoAufhebungEKPabgType, StornoAufhebungAbgAnfrage> {

    @Nullable
    @Override
    public StornoAufhebungAbgAnfrage apply(@Nullable StornoAufhebungEKPabgType input) {
        StornoAufhebungAbgAnfrage output = new StornoAufhebungAbgAnfrage();

        super.apply(input, output);

        output.setStornoGrund(input.getStornogrund());

        return output;
    }
}
