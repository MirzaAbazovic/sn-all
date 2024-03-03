/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.13
 */
package de.mnet.wbci.marshal.v1.request.storno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractStornoAenderungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPabgType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoType;
import de.mnet.wbci.marshal.v1.entities.PersonOderFirmaMarshaller;
import de.mnet.wbci.marshal.v1.entities.StandortMarshaller;
import de.mnet.wbci.model.StornoMitEndkundeStandortAnfrage;

/**
 * Special storno marshaller capable of adding enkunde and standort properties to anfrage type.
 *
 *
 */
public abstract class AbstractStornoMitEndkundeStandortMarshaller<S extends StornoMitEndkundeStandortAnfrage, T extends StornoType>
        extends AbstractStornoAnfrageMarshaller<S, T> {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractStornoMitEndkundeStandortMarshaller.class);

    @Autowired
    private PersonOderFirmaMarshaller personOderFirmaMarshaller;
    @Autowired
    private StandortMarshaller standortMarshaller;

    protected T apply(S input, T output) {
        super.apply(input, output);

        if (output instanceof StornoAufhebungEKPabgType) {
            ((StornoAufhebungEKPabgType) output).setName(personOderFirmaMarshaller.apply(input.getEndkunde()));
            ((StornoAufhebungEKPabgType) output).setStandort(standortMarshaller.apply(input.getStandort()));
        }
        else if (output instanceof AbstractStornoAenderungType) {
            ((AbstractStornoAenderungType) output).setName(personOderFirmaMarshaller.apply(input.getEndkunde()));
            ((AbstractStornoAenderungType) output).setStandort(standortMarshaller.apply(input.getStandort()));
        }
        else {
            LOG.warn("Unable to apply endkunde standort to StornoAnfrage of type :" + input);
        }

        return output;
    }
}
