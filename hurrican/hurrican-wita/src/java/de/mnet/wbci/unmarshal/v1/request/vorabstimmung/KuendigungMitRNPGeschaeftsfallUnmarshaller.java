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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.unmarshal.v1.enities.RufnummernPortierungUnmarshaller;

@Component
public class KuendigungMitRNPGeschaeftsfallUnmarshaller extends
        AbstractKuendigungGeschaeftsfallUnmarshaller<KuendigungMitRNPGeschaeftsfallType, WbciGeschaeftsfallKueMrn>
        implements Function<KuendigungMitRNPGeschaeftsfallType, WbciGeschaeftsfallKueMrn> {

    @Autowired
    private RufnummernPortierungUnmarshaller rufnummernPortierungUnmarshaller;

    @Nullable
    @Override
    public WbciGeschaeftsfallKueMrn apply(@Nullable KuendigungMitRNPGeschaeftsfallType input) {
        if (input == null) {
            return null;
        }

        WbciGeschaeftsfallKueMrn geschaeftsfall = new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(rufnummernPortierungUnmarshaller.apply(input.getRufnummernPortierung()))
                .build();
        super.apply(geschaeftsfall, input);
        return geschaeftsfall;
    }

}
