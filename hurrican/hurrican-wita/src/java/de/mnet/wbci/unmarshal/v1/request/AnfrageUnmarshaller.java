/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.request;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AnfrageType;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;

@Component
public abstract class AnfrageUnmarshaller<AT extends AnfrageType, GF extends WbciGeschaeftsfall>
        implements Function<AT, GF> {

    @Autowired
    private CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshaller;

    @Nullable
    public GF apply(GF geschaeftsfall, AT input) {
        geschaeftsfall.setWbciVersion(WbciVersion.getWbciVersion(String.format("%s", input.getVersion())));
        geschaeftsfall.setAbsender(carrierIdentifikatorUnmarshaller.apply(input.getAbsender()));
        geschaeftsfall.setAbgebenderEKP(carrierIdentifikatorUnmarshaller.apply(input.getEmpfaenger()));
        if (input.getEndkundenvertragspartner() != null) {
            geschaeftsfall.setAufnehmenderEKP(carrierIdentifikatorUnmarshaller.apply(input.getEndkundenvertragspartner().getEKPauf()));
        }

        return geschaeftsfall;
    }

}
