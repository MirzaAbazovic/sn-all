/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.storno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractStornoAenderungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPabgType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoType;
import de.mnet.wbci.model.StornoMitEndkundeStandortAnfrage;
import de.mnet.wbci.unmarshal.v1.enities.PersonOderFirmaUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.StandortUnmarshaller;

/**
 *
 */
public abstract class AbstractStornoMitEndkundeStandortUnmarshaller<T extends StornoType, S extends StornoMitEndkundeStandortAnfrage>
        extends AbstractStornoAnfrageUnmarshaller<T, S> {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractStornoMitEndkundeStandortUnmarshaller.class);

    @Autowired
    private PersonOderFirmaUnmarshaller personOderFirmaUnmarshaller;
    @Autowired
    private StandortUnmarshaller standortUnmarshaller;

    protected S apply(T input, S output) {
        super.apply(input, output);

        if (input instanceof StornoAufhebungEKPabgType) {
            ((StornoMitEndkundeStandortAnfrage) output).setEndkunde(personOderFirmaUnmarshaller.apply(((StornoAufhebungEKPabgType) input).getName()));
            ((StornoMitEndkundeStandortAnfrage) output).setStandort(standortUnmarshaller.apply(((StornoAufhebungEKPabgType) input).getStandort()));
        }
        else if (input instanceof AbstractStornoAenderungType) {
            ((StornoMitEndkundeStandortAnfrage) output).setEndkunde(personOderFirmaUnmarshaller.apply(((AbstractStornoAenderungType) input).getName()));
            ((StornoMitEndkundeStandortAnfrage) output).setStandort(standortUnmarshaller.apply(((AbstractStornoAenderungType) input).getStandort()));
        }
        else {
            LOG.warn("Unable to apply endkunde standort to StornoAnfrage of type :" + input);
        }

        return output;
    }
}
