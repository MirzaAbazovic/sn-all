/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request.storno;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPabgType;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;

@Component
public class StornoAufhebungAbgAnfrageMarshaller extends AbstractStornoMitEndkundeStandortMarshaller<StornoAufhebungAbgAnfrage, StornoAufhebungEKPabgType> {

    @Nullable
    @Override
    public StornoAufhebungEKPabgType apply(@Nullable StornoAufhebungAbgAnfrage input) {
        StornoAufhebungEKPabgType output = V1_OBJECT_FACTORY.createStornoAufhebungEKPabgType();

        super.apply(input, output);

        output.setStornogrund(input.getStornoGrund());

        return output;
    }

}
