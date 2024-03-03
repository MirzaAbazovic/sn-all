/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2004 13:38:23
 */
package de.augustakom.common.tools.collections;

import java.util.*;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;


/**
 * Hilfsklasse, um die Inhalte einer Collection in eine Map uebertragen.
 *
 *
 */
public class CollectionMapConverter {

    private static final Logger LOGGER = Logger.getLogger(CollectionMapConverter.class);

    /**
     * Speichert alle Items der Collection <code>items</code> in die Map <code>destination</code>. Als Key fuer die Map
     * wird der Wert verwendet, den die Methode <code>keyMethod</code> liefert. Es muss beachtet werden, dass die
     * Methode ein Objekt und keinen Datentyp liefert!!! Ist <code>valueMethod</code> definiert, wird der Map als Value
     * der Rueckgabewert der Methode uebergeben - ansonsten das gesamte Objekt.
     *
     * @param items       Collection, die in eine Map gewandelt werden soll.
     * @param destination Ziel-Map.
     * @param keyMethod   Methoden-Name, um das Key-Objekt zu erhalten.
     * @param valueMethod Methoden-Name, um das Value-Objekt zu erhalten (oder <code>null</code>).
     */
    @SuppressWarnings("unchecked")
    public static <T, V, U> void convert2Map(Iterable<T> items, Map<V, U> destination, String keyMethod,
            String valueMethod) {
        if ((items == null) || (destination == null) || (keyMethod == null) || (keyMethod.trim().length() == 0)) {
            return;
        }

        try {
            for (T next : items) {
                V key = (V) MethodUtils.invokeExactMethod(next, keyMethod, null);
                U value = null;
                if (valueMethod != null) {
                    value = (U) MethodUtils.invokeExactMethod(next, valueMethod, null);
                }
                else {
                    value = (U) next;
                }

                destination.put(key, value);
            }
        }
        catch (Exception e) {
            destination.clear();
            LOGGER.error("Collection konnte nicht in Map konvertiert werden! Grund: " + e.getMessage(), e);
        }
    }

    /**
     * @param items
     * @param keyMethod
     * @param valueMethod
     * @return
     *
     * @see convert2Map(Collection, Map, String, String) Als 'Ziel-Map' wird ein neues HashMap Objekt erstellt und
     * zurueck gegeben.
     */
    public static <T, U, V> Map<U, V> convert2Map(Iterable<T> items, String keyMethod, String valueMethod) {
        Map<U, V> map = new HashMap<U, V>();
        convert2Map(items, map, keyMethod, valueMethod);
        return map;
    }
}


