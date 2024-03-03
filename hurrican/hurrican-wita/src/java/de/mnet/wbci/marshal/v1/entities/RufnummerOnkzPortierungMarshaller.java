/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrPortierungskennerType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.OnkzRufNrPortierungskennerTypeBuilder;

/**
 *
 */
@Component
public class RufnummerOnkzPortierungMarshaller extends AbstractBaseMarshaller implements Function<RufnummerOnkz, OnkzRufNrPortierungskennerType> {
    @Nullable
    @Override
    public OnkzRufNrPortierungskennerType apply(@Nullable RufnummerOnkz input) {
        if (input != null) {
            return new OnkzRufNrPortierungskennerTypeBuilder()
                    .withPortierungskennungPKIabg(input.getPortierungskennungPKIabg())
                    .withONKZ(input.getOnkz())
                    .withRufnummer(input.getRufnummer())
                    .build();
        }

        return null;
    }
}
