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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungOhneRNPGeschaeftsfallType;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnBuilder;
import de.mnet.wbci.unmarshal.v1.enities.OnkzRufNrUnmarshaller;

@Component
public class KuendigungOhneRNPGeschaeftsfallUnmarshaller
        extends AbstractKuendigungGeschaeftsfallUnmarshaller<KuendigungOhneRNPGeschaeftsfallType, WbciGeschaeftsfallKueOrn> {

    @Autowired
    private OnkzRufNrUnmarshaller onkzRufNrUnmarshaller;

    @Nullable
    @Override
    public WbciGeschaeftsfallKueOrn apply(@Nullable KuendigungOhneRNPGeschaeftsfallType input) {
        if (input == null) {
            return null;
        }

        WbciGeschaeftsfallKueOrn geschaeftsfall = new WbciGeschaeftsfallKueOrnBuilder()
                .withAnschlussIdentifikation(onkzRufNrUnmarshaller.apply(input.getAnschlussidentifikation()))
                .build();
        super.apply(geschaeftsfall, input);
        return geschaeftsfall;
    }
}
