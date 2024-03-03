/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import de.mnet.wita.message.auftrag.Anrede;

public class AnredeConverter {
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
