/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.storno;

import javax.annotation.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPabgType;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;

/**
 *
 */
@Component
public class StornoAenderungAbgAnfrageUnmarshaller extends AbstractStornoMitEndkundeStandortUnmarshaller<StornoAenderungEKPabgType, StornoAenderungAbgAnfrage> {

    @Nullable
    @Override
    public StornoAenderungAbgAnfrage apply(@Nullable StornoAenderungEKPabgType input) {
        StornoAenderungAbgAnfrage output = new StornoAenderungAbgAnfrage();
        super.apply(input, output);

        output.setStornoGrund(input.getStornogrund());

        return output;
    }
}
