/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2005 10:48:27
 */
package de.augustakom.common.tools.lang;

import java.math.*;
import java.util.*;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Hilfsklasse fuer das Arbeiten mit Maps.
 */
public class MapTools {

    private static final Logger LOGGER = Logger.getLogger(MapTools.class);

    /**
     * Ermittelt das Objekt zu einem Key in der Map. Ueber die Klasse wird definiert, von welcher Art das Objekt sein
     * soll/muss.
     *
     * @param map   Map mit den Werten
     * @param key   Key des gesuchten Werts
     * @param clazz Klassentyp, der gesucht wird.
     * @return Objekt der Klasse <code>clazz</code> oder <code>null</code>.
     */
    public static <V, C> C getObject(Map<String, V> map, String key, Class<C> clazz) {
        if ((map != null) && (key != null) && (clazz != null)) {
            V temp = map.get(key);
            if ((temp != null) && clazz.isAssignableFrom(temp.getClass())) {
                return clazz.cast(temp);
            }
        }
        return null;
    }

    /**
     * @param map
     * @param key
     * @return Integer-Objekt oder <code>null</code>.
     * @see MapTools#getObject(Map, String, Class)
     */
    public static Integer getInteger(Map<String, ?> map, String key) {
        Number numb = getObject(map, key, Number.class);
        return (numb != null) ? Integer.valueOf(numb.intValue()) : null;
    }

    /**
     * @param map
     * @param key
     * @param defaultValue Return-Value, falls fuer den Key kein Eintrag gefunden wird
     * @return
     *
     * @see MapTools#getInteger(Map, String)
     */
    public static Integer getInteger(Map<String, ?> map, String key, Integer defaultValue) {
        Number numb = getObject(map, key, Number.class);
        return (numb != null) ? Integer.valueOf(numb.intValue()) : defaultValue;
    }

    /**
     * @param map
     * @param key
     * @return Short-Objekt oder <code>null</code>.
     * @see MapTools#getObject(Map, String, Class)
     */
    public static Short getShort(Map<String, ?> map, String key) {
        Number numb = getObject(map, key, Number.class);
        return (numb != null) ? Short.valueOf(numb.shortValue()) : null;
    }

    /**
     * @param map
     * @param key
     * @return Long-Objekt oder <code>null</code>.
     * @see MapTools#getObject(Map, String, Class)
     */
    public static Long getLong(Map<String, ?> map, String key) {
        Number numb = getObject(map, key, Number.class);
        return (numb != null) ? Long.valueOf(numb.longValue()) : null;
    }

    /**
     * @param map
     * @param key
     * @param defaultValue Return-Value, falls fuer den Key kein Eintrag gefunden wird
     * @return
     * @see MapTools#getLong(Map, String)
     */
    public static Long getLong(Map<String, ?> map, String key, Long defaultValue) {
        Number numb = getObject(map, key, Number.class);
        return (numb != null) ? Long.valueOf(numb.longValue()) : defaultValue;
    }

    /**
     * @param map
     * @param key
     * @return Float-Objekt oder <code>null</code>.
     * @see MapTools#getObject(Map, String, Class)
     */
    public static Float getFloat(Map<String, ?> map, String key) {
        Number numb = getObject(map, key, Number.class);
        return (numb != null) ? Float.valueOf(numb.floatValue()) : null;
    }

    /**
     * @param map
     * @param key
     * @return BigDecimal-Objekt oder <code>null</code>.
     * @see MapTools#getObject(Map, String, Class)
     */
    public static BigDecimal getBigDecimal(Map<String, ?> map, String key) {
        Number numb = getObject(map, key, Number.class);
        return (numb != null) ? new BigDecimal(numb.doubleValue()) : null;
    }

    /**
     * @param map
     * @param key
     * @return String-Objekt oder <code>null</code>.
     * @see MapTools#getObject(Map, String, Class)
     */
    public static String getString(Map<String, ?> map, String key) {
        return getObject(map, key, String.class);
    }

    /**
     * @param map
     * @param key
     * @return Boolean-Objekt oder <code>null</code>.
     * @see MapTools#getObject(Map, String, Class)
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "method is only used in DAOs; return null necessary to read null values from the database!")
    public static Boolean getBoolean(Map<String, ?> map, String key) {
        Object tmp = getObject(map, key, Object.class);
        if (tmp != null) {
            if (tmp instanceof Boolean) {
                return (Boolean) tmp;
            }
            else if (tmp instanceof Number) {
                Number numb = (Number) tmp;
                if ((numb.intValue() == 1) || (numb.intValue() == -1)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            else if (tmp instanceof String) {
                if ("1".equals(tmp) || "-1".equals(tmp)) {
                    return Boolean.TRUE;
                }
                else if ("0".equals(tmp)) {
                    return Boolean.FALSE;
                }
            }
        }
        return null;
    }

    /**
     * @param map
     * @param key
     * @return Date-Objekt oder <code>null</code>.
     * @see MapTools#getObject(Map, String, Class)
     */
    public static Date getDate(Map<String, ?> map, String key) {
        return getObject(map, key, Date.class);
    }

    /**
     * Fuegt der Map ein Objekt hinzu.
     *
     * @param destination
     * @param toAdd
     * @param keyMethod   Methode, die auf <code>toAdd</code> aufgerufen werden soll, um den Key-Wert fuer die Map zu
     *                    erhalten.
     */
    public static void addObject(Map<Object, Object> destination, Object toAdd, String keyMethod) {
        if ((destination == null) || (toAdd == null) || StringUtils.isBlank(keyMethod)) {
            return;
        }

        try {
            Object key = MethodUtils.invokeExactMethod(toAdd, keyMethod, null);
            if (key != null) {
                destination.put(key, toAdd);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt alle Keys aus der Map und gibt sie in einer Collection zurueck.
     */
    public static <S, T> Collection<S> getKeys(Map<S, T> map) {
        if (map != null) {
            return new ArrayList<>(map.keySet());
        }
        return null;
    }
}


