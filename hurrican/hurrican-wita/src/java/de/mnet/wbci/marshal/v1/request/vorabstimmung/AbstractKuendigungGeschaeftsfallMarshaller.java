/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request.vorabstimmung;

import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractKuendigungGeschaeftsfallType;
import de.mnet.wbci.marshal.v1.entities.StandortMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;

/**
 *
 */
public abstract class AbstractKuendigungGeschaeftsfallMarshaller<GFKUE extends WbciGeschaeftsfallKue, KUET extends AbstractKuendigungGeschaeftsfallType>
        extends VorabstimmungMarshaller<GFKUE, KUET> implements Function<GFKUE, KUET> {

    @Autowired
    private StandortMarshaller standortMarshaller;

    public KUET apply(KUET anfrage, GFKUE input) {
        anfrage.setStandort(standortMarshaller.apply(input.getStandort()));

        super.apply(anfrage, input);
        return anfrage;
    }
}
