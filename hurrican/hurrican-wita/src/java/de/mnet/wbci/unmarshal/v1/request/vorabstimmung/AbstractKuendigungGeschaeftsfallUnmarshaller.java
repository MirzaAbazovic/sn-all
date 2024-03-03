/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.vorabstimmung;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractKuendigungGeschaeftsfallType;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;
import de.mnet.wbci.unmarshal.v1.enities.StandortUnmarshaller;


@Component
public abstract class AbstractKuendigungGeschaeftsfallUnmarshaller<KUET extends AbstractKuendigungGeschaeftsfallType, GFKUE extends WbciGeschaeftsfallKue>
        extends VorabstimmungUnmarshaller<KUET, GFKUE> implements Function<KUET, GFKUE> {

    @Autowired
    private StandortUnmarshaller standortUnmarshaller;

    @Nullable
    @Override
    public GFKUE apply(GFKUE geschaeftsfall, KUET input) {
        super.apply(geschaeftsfall, input);
        geschaeftsfall.setStandort(standortUnmarshaller.apply(input.getStandort()));
        return null;
    }

}
