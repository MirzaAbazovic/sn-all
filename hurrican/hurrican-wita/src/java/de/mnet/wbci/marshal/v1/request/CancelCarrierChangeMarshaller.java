/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request;

import javax.annotation.*;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CancelCarrierChange;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.marshal.v1.request.storno.StornoAenderungAbgAnfrageMarshaller;
import de.mnet.wbci.marshal.v1.request.storno.StornoAenderungAufAnfrageMarshaller;
import de.mnet.wbci.marshal.v1.request.storno.StornoAufhebungAbgAnfrageMarshaller;
import de.mnet.wbci.marshal.v1.request.storno.StornoAufhebungAufAnfrageMarshaller;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;

@Component
public class CancelCarrierChangeMarshaller extends AbstractBaseMarshaller implements
        Function<StornoAnfrage, CancelCarrierChange> {

    private static final Logger LOG = LoggerFactory.getLogger(CancelCarrierChangeMarshaller.class);

    @Autowired
    private StornoAenderungAbgAnfrageMarshaller stornoAenderungAbgAnfrageMarshaller;

    @Autowired
    private StornoAenderungAufAnfrageMarshaller stornoAenderungAufAnfrageMarshaller;

    @Autowired
    private StornoAufhebungAbgAnfrageMarshaller stornoAufhebungAbgAnfrageMarshaller;

    @Autowired
    private StornoAufhebungAufAnfrageMarshaller stornoAufhebungAufAnfrageMarshaller;

    @Nullable
    @Override
    public CancelCarrierChange apply(@Nullable StornoAnfrage input) {
        if (input == null) {
            return null;
        }

        CancelCarrierChange rescheduleCarrierChange = V1_OBJECT_FACTORY.createCancelCarrierChange();
        if (input instanceof StornoAenderungAbgAnfrage) {
            rescheduleCarrierChange.setSTRAENDON(stornoAenderungAbgAnfrageMarshaller
                    .apply((StornoAenderungAbgAnfrage) input));
        }
        else if (input instanceof StornoAenderungAufAnfrage) {
            rescheduleCarrierChange.setSTRAENREC(stornoAenderungAufAnfrageMarshaller
                    .apply((StornoAenderungAufAnfrage) input));
        }
        else if (input instanceof StornoAufhebungAbgAnfrage) {
            rescheduleCarrierChange.setSTRAUFDON(stornoAufhebungAbgAnfrageMarshaller
                    .apply((StornoAufhebungAbgAnfrage) input));
        }
        else if (input instanceof StornoAufhebungAufAnfrage) {
            rescheduleCarrierChange.setSTRAUFREC(stornoAufhebungAufAnfrageMarshaller
                    .apply((StornoAufhebungAufAnfrage) input));
        }
        else {
            LOG.warn("No marshaller found for StornoAnfrage type:" + input);
        }

        return rescheduleCarrierChange;
    }

}
