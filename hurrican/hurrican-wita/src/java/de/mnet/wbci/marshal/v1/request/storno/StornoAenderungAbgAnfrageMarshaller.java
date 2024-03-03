/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request.storno;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPabgType;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;

@Component
public class StornoAenderungAbgAnfrageMarshaller extends AbstractStornoMitEndkundeStandortMarshaller<StornoAenderungAbgAnfrage, StornoAenderungEKPabgType> {

    @Nullable
    @Override
    public StornoAenderungEKPabgType apply(@Nullable StornoAenderungAbgAnfrage input) {
        StornoAenderungEKPabgType output = V1_OBJECT_FACTORY.createStornoAenderungEKPabgType();

        super.apply(input, output);

        output.setStornogrund(input.getStornoGrund());

        return output;
    }

}
