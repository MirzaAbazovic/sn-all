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

import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Anrede;

/**
 * Class marshalls an {@link Anrede} object to a valid {@link String} value for cdm version 1. See wsdl schema
 * CarrierNegotiationService.wsdl.
 *
 *
 */
@Component
public class AnredeMarshaller extends AbstractBaseMarshaller implements Function<Anrede, String> {
    protected static final String ANREDE_HERR = "1";
    protected static final String ANREDE_FRAU = "2";
    protected static final String ANREDE_FIRMA = "4";
    protected static final String ANREDE_UNKNOWN = "9";

    @Nullable
    @Override
    public String apply(@Nullable Anrede input) {
        if (input == null) {
            return ANREDE_UNKNOWN;
        }
        switch (input) {
            case HERR:
                return ANREDE_HERR;
            case FRAU:
                return ANREDE_FRAU;
            case FIRMA:
                return ANREDE_FIRMA;
            case UNBEKANNT:
                return ANREDE_UNKNOWN;
            default:
                throw new IllegalArgumentException(String.format("No mapping exists for Anrede '%s'", input));
        }
    }
}
