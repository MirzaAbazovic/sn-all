/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.wbci.model.Anrede;

@Component
public class AnredeUnmarshaller implements Function<String, Anrede> {

    protected static final String ANREDE_HERR = "1";
    protected static final String ANREDE_FRAU = "2";
    protected static final String ANREDE_FIRMA = "4";
    protected static final String ANREDE_UNKNOWN = "9";

    @Nullable
    @Override
    public Anrede apply(@Nullable String input) {
        if (input == null) {
            return Anrede.UNBEKANNT;
        }

        switch (input) {
            case ANREDE_HERR:
                return Anrede.HERR;
            case ANREDE_FRAU:
                return Anrede.FRAU;
            case ANREDE_FIRMA:
                return Anrede.FIRMA;
            case ANREDE_UNKNOWN:
                return Anrede.UNBEKANNT;
            default:
                throw new IllegalArgumentException(String.format("No mapping exists for Anrede '%s'", input));
        }
    }

}
