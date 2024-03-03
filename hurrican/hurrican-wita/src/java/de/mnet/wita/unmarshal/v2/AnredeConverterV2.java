/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.wita.message.auftrag.Anrede;

@SuppressWarnings("Duplicates")
@Component
public class AnredeConverterV2 {
    public static Anrede toMwf(String anrede) {
        if ("2".equals(anrede)) {
            return Anrede.FRAU;
        }
        if ("1".equals(anrede)) {
            return Anrede.HERR;
        }
        if ("4".equals(anrede)) {
            return Anrede.FIRMA;
        }
        if ("9".equals(anrede)) {
            return Anrede.UNBEKANNT;
        }
        throw new RuntimeException("Unknown value for field anrede");
    }
}
