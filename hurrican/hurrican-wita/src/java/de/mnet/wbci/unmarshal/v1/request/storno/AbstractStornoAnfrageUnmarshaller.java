/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.storno;

import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoType;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;

/**
 *
 */
public abstract class AbstractStornoAnfrageUnmarshaller<T extends StornoType, S extends StornoAnfrage> implements Function<T, S> {

    @Autowired
    private CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshaller;

    /**
     * Abstract base unmarshall operations for StornoAnfrage types.
     *
     * @param input
     * @param output
     * @return
     */
    protected S apply(T input, S output) {
        output.setVorabstimmungsIdRef(input.getVorabstimmungsIdRef());
        output.setAbsender(carrierIdentifikatorUnmarshaller.apply(input.getAbsender()));
        output.setAenderungsId(input.getStornoId());
        output.setVersion(Long.valueOf(input.getVersion()));

        return output;
    }
}
