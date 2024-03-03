/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import com.google.common.base.Function;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungsPositionType;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;

/**
 *
 */
public abstract class AbstractMeldungsPositionUnmarshaller<MPT extends AbstractMeldungsPositionType, MP extends MeldungPosition>
        implements Function<MPT, MP> {

    /**
     * Base apply method for basic unmarshalling of MeldungPosition types.
     *
     * @param position
     * @param input
     * @return
     */
    public MP apply(MP position, MPT input) {
        position.setMeldungsCode(MeldungsCode.buildFromCode(input.getMeldungscode()));
        position.setMeldungsText(input.getMeldungstext());

        return position;
    }
}
