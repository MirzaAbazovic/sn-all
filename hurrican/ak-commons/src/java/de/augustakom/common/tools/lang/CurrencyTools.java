/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2005 13:38:41
 */
package de.augustakom.common.tools.lang;

import java.math.*;
import java.text.*;
import java.util.*;


/**
 * Util-Klasse zum Arbeiten mit Waehrungen.
 *
 *
 */
public class CurrencyTools {

    /**
     * Gibt den Number-Wert als Waehrungs-String zurueck.
     */
    public static String getAsCurrency(Number value) {
        if (value != null) {
            BigDecimal bd = new BigDecimal(value.doubleValue());
            BigDecimal scaledBD = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return NumberFormat.getCurrencyInstance().format(scaledBD);
        }
        return null;
    }

    /**
     * Gibt den Number-Wert als Waehrungs-String zurueck.
     */
    public static String getAsCurrency(Number value, Locale locale) {
        if (locale == null) {
            return getAsCurrency(value);
        }

        if (value != null) {
            BigDecimal bd = new BigDecimal(value.doubleValue());
            BigDecimal scaledBD = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return NumberFormat.getCurrencyInstance(locale).format(scaledBD);
        }
        return null;
    }

}


