/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2010 08:36:57
 */
package de.augustakom.common.tools.lang;


/**
 * Tool-Klasse fuer das Arbeiten mit Java enums.
 */
public class EnumTools {

    /**
     * Ermittelt aus dem angegebenen Enum-Typ den Wert zu {@code name}. Evtl. auftretende IllegalArgumentExceptions
     * werden in dieser Methode unterdrueckt und in diesem Fall {@code null} zurück geliefert.
     *
     * @param <T>
     * @param enumType
     * @param name
     * @return
     */
    public static <T extends Enum<T>> T valueOfSilent(Class<T> enumType, String name) {
        try {
            return Enum.valueOf(enumType, name);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

}


