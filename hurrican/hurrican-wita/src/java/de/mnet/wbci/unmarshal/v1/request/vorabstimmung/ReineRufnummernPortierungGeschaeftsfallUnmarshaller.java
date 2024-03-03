/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.vorabstimmung;

import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ReineRufnummernportierungGeschaeftsfallType;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpBuilder;
import de.mnet.wbci.unmarshal.v1.enities.RufnummernPortierungUnmarshaller;

@Component
public class ReineRufnummernPortierungGeschaeftsfallUnmarshaller
        extends VorabstimmungUnmarshaller<ReineRufnummernportierungGeschaeftsfallType, WbciGeschaeftsfallRrnp> {

    @Autowired
    private RufnummernPortierungUnmarshaller rufnummernPortierungBaseUnmarshaller;

    @Nullable
    @Override
    public WbciGeschaeftsfallRrnp apply(@Nullable ReineRufnummernportierungGeschaeftsfallType input) {
        if (input == null) {
            return null;
        }

        WbciGeschaeftsfallRrnp geschaeftsfall = new WbciGeschaeftsfallRrnpBuilder()
                .withRufnummernportierung(rufnummernPortierungBaseUnmarshaller.apply(input.getRufnummernPortierung()))
                .build();
        super.apply(geschaeftsfall, input);
        return geschaeftsfall;
    }
}
