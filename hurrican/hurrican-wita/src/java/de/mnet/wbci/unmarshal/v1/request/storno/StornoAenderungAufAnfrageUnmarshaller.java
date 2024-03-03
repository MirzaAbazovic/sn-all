/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.storno;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPaufType;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;

/**
 *
 */
@Component
public class StornoAenderungAufAnfrageUnmarshaller extends AbstractStornoMitEndkundeStandortUnmarshaller<StornoAenderungEKPaufType, StornoAenderungAufAnfrage> {

    @Nullable
    @Override
    public StornoAenderungAufAnfrage apply(@Nullable StornoAenderungEKPaufType input) {
        StornoAenderungAufAnfrage output = new StornoAenderungAufAnfrage();

        super.apply(input, output);

        return output;
    }
}
