/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.RufnummerOnkz;

/**
 *
 */
@Component
public class RufnummerOnkzMarshaller extends AbstractBaseMarshaller implements Function<RufnummerOnkz, OnkzRufNrType> {
    @Nullable
    @Override
    public OnkzRufNrType apply(@Nullable RufnummerOnkz input) {
        if (input == null) {
            return null;
        }
        OnkzRufNrType onkzRufNrType = V1_OBJECT_FACTORY.createOnkzRufNrType();
        onkzRufNrType.setONKZ(input.getOnkz());
        onkzRufNrType.setRufnummer(input.getRufnummer());
        return onkzRufNrType;
    }
}
