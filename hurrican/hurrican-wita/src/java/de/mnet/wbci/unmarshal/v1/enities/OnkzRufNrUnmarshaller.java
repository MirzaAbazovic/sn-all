/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrPortierungskennerType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;

/**
 *
 */
@Component
public class OnkzRufNrUnmarshaller implements Function<OnkzRufNrType, RufnummerOnkz> {

    @Nullable
    @Override
    public RufnummerOnkz apply(@Nullable OnkzRufNrType input) {
        if (input != null) {
            RufnummerOnkz output = new RufnummerOnkzBuilder()
                    .withOnkz(input.getONKZ())
                    .withRufnummer(input.getRufnummer())
                    .build();
            if (input instanceof OnkzRufNrPortierungskennerType) {
                output.setPortierungskennungPKIabg(((OnkzRufNrPortierungskennerType) input)
                        .getPortierungskennungPKIabg());
            }
            return output;
        }
        return null;
    }
}
