/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.terminverschiebung;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TerminverschiebungType;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageBuilder;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.PersonOderFirmaUnmarshaller;

@Component
public class TerminverschiebungUnmarshaller implements Function<TerminverschiebungType, TerminverschiebungsAnfrage> {

    @Autowired
    private PersonOderFirmaUnmarshaller personOderFirmaUnmarshaller;

    @Autowired
    private CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshaller;

    @Nullable
    @Override
    public TerminverschiebungsAnfrage apply(@Nullable TerminverschiebungType input) {
        TerminverschiebungsAnfrage tvAnfrage = new TerminverschiebungsAnfrageBuilder<>()
                .withVorabstimmungsIdRef(input.getVorabstimmungsIdRef())
                .withAenderungsId(input.getAenderungsId())
                .withTvTermin(DateConverterUtils.toLocalDate(input.getNeuerKundenwunschtermin()))
                .withEndkunde(personOderFirmaUnmarshaller.apply(input.getName()))
                .withAbsender(carrierIdentifikatorUnmarshaller.apply(input.getAbsender()))
                .build();

        return tvAnfrage;
    }
}
