/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.unmarshal.v1;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CancelCarrierChange;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.unmarshal.v1.request.storno.StornoAenderungAbgAnfrageUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.storno.StornoAenderungAufAnfrageUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.storno.StornoAufhebungAbgAnfrageUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.storno.StornoAufhebungAufAnfrageUnmarshaller;

/**
 *
 */
@Component
public class CancelCarrierChangeUnmarshaller implements Function<CancelCarrierChange, StornoAnfrage> {

    @Autowired
    private StornoAufhebungAufAnfrageUnmarshaller stornoAufhebungAufAnfrageUnmarshaller;
    @Autowired
    private StornoAufhebungAbgAnfrageUnmarshaller stornoAufhebungAbgAnfrageUnmarshaller;
    @Autowired
    private StornoAenderungAbgAnfrageUnmarshaller stornoAenderungAbgAnfrageUnmarshaller;
    @Autowired
    private StornoAenderungAufAnfrageUnmarshaller stornoAenderungAufAnfrageUnmarshaller;

    @Nullable
    @Override
    public StornoAnfrage apply(@Nullable CancelCarrierChange input) {
        if (input != null) {
            if (input.getSTRAUFREC() != null) {
                return stornoAufhebungAufAnfrageUnmarshaller.apply(input.getSTRAUFREC());
            }
            else if (input.getSTRAUFDON() != null) {
                return stornoAufhebungAbgAnfrageUnmarshaller.apply(input.getSTRAUFDON());
            }
            else if (input.getSTRAENREC() != null) {
                return stornoAenderungAufAnfrageUnmarshaller.apply(input.getSTRAENREC());
            }
            else if (input.getSTRAENDON() != null) {
                return stornoAenderungAbgAnfrageUnmarshaller.apply(input.getSTRAENDON());
            }
        }

        return null;
    }
}
