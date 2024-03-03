/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrPortierungskennerType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernMeldungType;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;

/**
 *
 */
@Component
public class PortierungRufnummernEinzelnMeldungUnmarshaller implements
        Function<PortierungRufnummernMeldungType, RufnummernportierungEinzeln> {

    @Autowired
    private OnkzRufNrUnmarshaller onkzRufNrUnmarshaller;

    @Nullable
    @Override
    public RufnummernportierungEinzeln apply(@Nullable PortierungRufnummernMeldungType input) {
        if (input != null) {
            RufnummernportierungEinzeln output = new RufnummernportierungEinzelnBuilder()
                    .withAlleRufnummernPortieren(null)
                    .build();

            if (input.getZuPortierendeOnkzRnr() != null && !input.getZuPortierendeOnkzRnr().isEmpty()) {
                for (OnkzRufNrPortierungskennerType onkzRufNrType : input.getZuPortierendeOnkzRnr()) {
                    output.addRufnummerOnkz(onkzRufNrUnmarshaller.apply(onkzRufNrType));
                }
            }

            return output;
        }
        return null;
    }
}
