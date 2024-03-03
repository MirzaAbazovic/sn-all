/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import com.google.common.base.Function;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungsPositionType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;

/**
 *
 */
public abstract class MeldungPositionMarshaller<POS extends MeldungPosition, POST extends AbstractMeldungsPositionType>
        extends AbstractBaseMarshaller implements Function<POS, POST> {

    /**
     * Base apply method for basic marshalling of MeldungPosition types.
     *
     * @param position
     * @param input
     * @return
     */
    public POST apply(POST position, POS input) {
        if (input.getMeldungsCode() != null && !input.getMeldungsCode().equals(MeldungsCode.UNKNOWN)) {
            position.setMeldungscode(input.getMeldungsCode().getCode());
            position.setMeldungstext(input.getMeldungsCode().getStandardText());
        }

        return position;
    }
}
