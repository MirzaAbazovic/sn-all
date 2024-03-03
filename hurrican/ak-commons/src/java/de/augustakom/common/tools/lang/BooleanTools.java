/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2005 15:18:43
 */
package de.augustakom.common.tools.lang;

/**
 * Hilfsklasse von Boolean-Operationen.
 *
 *
 */
public class BooleanTools {

    public static final String DEFAULT_TRUE_STRING = "true";

    /**
     * Kann mit {@code null} umgehen.
     *
     * @return {@code true}, falls der Parameter TRUE ist, sonst {@code false}
     */
    public static boolean nullToFalse(Boolean b) {
        return (b != null) && b;
    }

    /**
     * Wandelt ein Integer-Objekt in ein Boolean-Objekt um.
     *
     * @param i zu wandelndes Integer-Objekt
     * @return TRUE, wenn i=1, sonst FALSE
     *
     */
    public static Boolean getBoolean(Integer i) {
        if ((i != null) && NumberTools.equal(i, Integer.valueOf(1))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Wandelt das Boolean-Objekt <code>b</code> in einen String um. Umwandlung wie folgt: <ul> <li>Boolean.TRUE --> "1"
     * <li>Boolean.FALSE --> "0" <li>NULL  -->  NULL </ul>
     *
     * @param b
     * @return
     *
     */
    public static String getBooleanAsString(Boolean b) {
        if (b == null) {
            return null;
        }
        return (BooleanTools.nullToFalse(b)) ? "1" : "0";
    }
}


