/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request.storno;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPaufType;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;

@Component
public class StornoAenderungAufAnfrageMarshaller extends AbstractStornoMitEndkundeStandortMarshaller<StornoAenderungAufAnfrage, StornoAenderungEKPaufType> {

    @Nullable
    @Override
    public StornoAenderungEKPaufType apply(@Nullable StornoAenderungAufAnfrage input) {
        StornoAenderungEKPaufType output = V1_OBJECT_FACTORY.createStornoAenderungEKPaufType();

        super.apply(input, output);

        return output;
    }

}
