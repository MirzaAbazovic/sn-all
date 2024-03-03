/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 16:31:56
 */
package de.augustakom.common.tools.lang;

import java.io.*;
import java.math.*;
import java.util.*;

/**
 * Hilfsklassen fuer Operationen auf Objekt-Ebene und fuer Array-Operationen.
 */
public class ObjectTools {

    /**
     * Erstellt eine exakte Kopie eines Objekts und gibt diese zurueck.
     *
     * @param obj zu kopierendes Objekt
     * @return exakte Kopie
     * @throws Exception wenn beim Kopieren ein Fehler auftritt.
     */
    public static Object makeDeepCopy(java.lang.Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object copy = ois.readObject();
        ois.close();

        return copy;
    }

    /**
     * @see ObjectTools#getObjectAtIndex(Object[], int, Object, Class)
     */
    public static Object getObjectAtIndex(Object[] values, int index, Object defaultValue) {
        return getObjectAtIndex(values, index, defaultValue, Object.class);
    }

    /**
     * Gibt das Objekt am Index <code>index</code> zurueck. <br> Sollte das angegebene Array kleiner als 'index' sein,
     * wird <code>defaultValue</code> zurueck gegeben. <br>
     *
     * @param expectedClass Angabe des erwarteten Objekttyps am angegebenen Index. Sollte der Typ nicht passen, wird
     *                      <code>defaultValue</code> zurueck geliefert.
     */
    public static Object getObjectAtIndex(Object[] values, int index, Object defaultValue, Class<?> expectedClass) {
        if ((values != null) && (values.length > index)) {
            Object value = values[index];
            if (expectedClass.isInstance(value)) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * Ermittelt aus dem Object[] <code>values</code> den Wert am Index <code>index</code>.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObjectSilent(Object[] values, int index, Class<T> clazz) {
        if ((values != null) && (values.length > index) && values[index] != null) {
            return (clazz.isAssignableFrom(values[index].getClass())) ? (T) values[index] : null;
        }
        return null;
    }

    /**
     * Ermittelt aus dem Object[] <code>values</code> den Wert am Index <code>index</code>. Ist dieser vom Typ String,
     * wird der Wert zurueck gegeben - sonst <code>null</code>.
     */
    public static String getStringSilent(Object[] values, int index) {
        if ((values != null) && (values.length > index)) {
            if (values[index] instanceof String) {
                return (String) values[index];
            } else if (values[index] instanceof Character) {
                return values[index].toString();
            }
        }
        return null;
    }

    /**
     * @see ObjectTools#getStringSilent(Object[], int)
     */
    public static Integer getIntegerSilent(Object[] values, int index) {
        if ((values != null) && (values.length > index)) {
            if (values[index] instanceof BigDecimal) {
                return ((BigDecimal) values[index]).intValue();
            } else {
                return (values[index] instanceof Integer) ? (Integer) values[index] : null;
            }
        }
        return null;
    }

    /**
     * @see ObjectTools#getStringSilent(Object[], int)
     */
    public static Long getLongSilent(Object[] values, int index) {
        if ((values != null) && (values.length > index)) {
            if (values[index] instanceof BigDecimal) {
                return ((BigDecimal) values[index]).longValue();
            } else {
                return (values[index] instanceof Long) ? (Long) values[index] : null;
            }
        }
        return null;
    }

    /**
     * @see ObjectTools#getStringSilent(Object[], int)
     */
    public static Date getDateSilent(Object[] values, int index) {
        if ((values != null) && (values.length > index)) {
            return (values[index] instanceof Date) ? (Date) values[index] : null;
        }
        return null;
    }

    /**
     * @see ObjectTools#getStringSilent(Object[], int)
     */
    public static Float getFloatSilent(Object[] values, int index) {
        if ((values != null) && (values.length > index)) {
            return (values[index] instanceof Float) ? (Float) values[index] : null;
        }
        return null;
    }

    /**
     * @see ObjectTools#getStringSilent(Object[], int)
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "method is only used in DAOs; return null necessary to read null values from the database!")
    public static Boolean getBooleanSilent(Object[] values, int index) {
        if ((values != null) && (values.length > index)) {
            if (values[index] instanceof Number) {
                return getaBooleanSilentOfNumber(values[index]);
            }

           if (values[index] instanceof Character) {
               return getBooleanSilentOfCharacter(values[index]);
           }

           if (values[index] instanceof Boolean) {
                return (Boolean) values[index];
           }
        }
        return null;
    }

    private static Boolean getBooleanSilentOfCharacter(Object value) {
        if ("1".equals(value.toString())){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    private static Boolean getaBooleanSilentOfNumber(Object value) {
        int x = ((Number) value).intValue();
        if ((x == 1) || (x == -1)) {
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }

    /**
     * @see ObjectTools#getStringSilent(Object[], int)
     */
    public static boolean getBooleanTypeSilent(Object[] values, int index) {
        if ((values != null) && (values.length > index)) {
            if (values[index] instanceof Number) {
                int x = ((Number) values[index]).intValue();
                if ((x == 1) || (x == -1)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else if (values[index] instanceof Boolean) {
                return ((Boolean) values[index]).booleanValue();
            }
        }
        return false;
    }

    /**
     * Ueberprueft, ob es sich bei dem Objekt <code>toCheck</code> um eine Instanz von <code>expected</code> handelt.
     */
    public static boolean isInstanceOf(Object toCheck, Class<?> expected) {
        return (expected != null) ? expected.isInstance(toCheck) : (toCheck == null);
    }

    /** Gibt t zurück wenn es nicht null ist, ansonsten wird ifNull zurückgegeben. */
    public static <T> T getOrElse(T t, T ifNull) {
        return (t != null) ? t : ifNull;
    }
}
