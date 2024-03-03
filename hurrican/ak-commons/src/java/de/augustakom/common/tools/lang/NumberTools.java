/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 13:47:01
 */
package de.augustakom.common.tools.lang;

import org.apache.commons.lang.StringUtils;


/**
 * Hilfklassen fuer Zahlen.
 */
public class NumberTools {

    /**
     * Ueberprueft, ob n1 und n2 den gleichen Wert besitzen, kommt mit {@code null} zurecht.
     *
     * @param n1 Integer 1, darf {@code null} sein
     * @param n2 Integer 2, darf {@code null} sein
     * @return true, wenn die beiden Zahlen identisch sind (oder beide Objekte <code>null</code> sind).
     */
    public static boolean equal(Integer n1, Integer n2) {   // NOSONAR ; squid:S1221 no rename! (critical warning since sonar update)
        if (n1 != null) {
            if (n2 == null) {
                return false;
            }

            if (n1.intValue() == n2.intValue()) {
                return true;
            }
        }
        else if (n2 == null) {
            return true;
        }

        return false;
    }

    /**
     * Ueberprueft, ob n1 und n2 ungleich sind, kommt mit {@code null} zurecht.
     *
     * @param n1 Integer 1, darf {@code null} sein
     * @param n2 Integer 2, darf {@code null} sein
     * @return true, wenn die beiden Zahlen ungleich sind.
     */
    public static boolean notEqual(Integer n1, Integer n2) {
        return !equal(n1, n2);
    }

    /**
     * Ueberprueft, ob n1 und n2 den gleichen Wert besitzen, kommt mit {@code null} zurecht.
     *
     * @param n1 Long 1, darf {@code null} sein
     * @param n2 Long 2, darf {@code null} sein
     * @return true, wenn die beiden Zahlen identisch sind (oder beide Objekte <code>null</code> sind).
     */
    public static boolean equal(Long n1, Long n2) {   // NOSONAR ; squid:S1221 no rename! (critical warning since sonar update)
        if (n1 != null) {
            if (n2 == null) {
                return false;
            }

            if (n1.longValue() == n2.longValue()) {
                return true;
            }
        }
        else if (n2 == null) {
            return true;
        }

        return false;
    }

    /**
     * Ueberprueft, ob n1 und n2 ungleich sind, kommt mit {@code null} zurecht.
     *
     * @param n1 Long 1, darf {@code null} sein
     * @param n2 Long 2, darf {@code null} sein
     * @return true, wenn die beiden Zahlen ungleich sind.
     */
    public static boolean notEqual(Long n1, Long n2) {
        return !equal(n1, n2);
    }

    /**
     * @param n1 Short 1, darf {@code null} sein
     * @param n2 Short 2, darf {@code null} sein
     * @return true, wenn die beiden Zahlen identisch sind (oder beide Objekte <code>null</code> sind).
     * @see #equal(java.lang.Integer, java.lang.Integer)
     */
    public static boolean equal(Short n1, Short n2) {   // NOSONAR ; squid:S1221 no rename! (critical warning since sonar update)
        if (n1 != null) {
            if (n2 == null) {
                return false;
            }

            if (n1.intValue() == n2.intValue()) {
                return true;
            }
        }
        else if (n2 == null) {
            return true;
        }

        return false;
    }

    /**
     * Ueberprueft, ob die Zahl <code>toCheck</code> in <code>values</code> enthalten ist.
     *
     * @param toCheck zu pruefende Zahl
     * @param values  zur Verfuegung stehende Zahlen
     * @return true, wenn <code>toCheck</code> in <code>values</code> enthalten ist.
     */
    public static boolean isIn(Number toCheck, Number[] values) {
        if ((toCheck != null) && (values != null) && (values.length > 0)) {
            for (int i = 0; i < values.length; i++) {
                if (toCheck.equals(values[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @see #isIn(Number, Number[]) - Negation!
     */
    public static boolean isNotIn(Number toCheck, Number[] values) {
        return !isIn(toCheck, values);
    }

    /**
     * Konvertiert ein Integer-Objekt in ein Boolean-Objekt. <br> Erzeugt true, wenn das Integer-Objekt die Werte -1
     * oder 1 hat - sonst wird ein Boolean-Objekt mit false-Wert zurueck gegeben.
     *
     * @param toConvert zu konvertierendes Integer-Objekt.
     */
    public static Boolean convertInt2Boolean(Integer toConvert) {
        if (toConvert == null) {
            return Boolean.FALSE;
        }
        else if (isIn(toConvert, new Integer[] { Integer.valueOf(-1), Integer.valueOf(1) })) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * Funktion konvertiert einen String-Wert in einen Long-Wert
     *
     * @throws NumberFormatException
     */
    public static Long convertString2Long(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return Long.valueOf(StringUtils.trim(str));
    }

    /**
     * @throws NumberFormatException
     * @see #convertString2Long(String) Falls der String nicht konvertiert werden kann, wird {@code defaultValue} zurueck
     * gegeben!
     */
    public static Long convertString2Long(String str, Long defaultValue) {
        try {
            if (StringUtils.isBlank(str)) {
                return defaultValue;
            }
            return Long.valueOf(StringUtils.trim(str));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    /**
     * Konvertiert das Objekt <code>toConvert</code> in ein Boolean-Objekt. <br> Voraussetzung: Objekt ist vom Typ
     * Integer
     */
    public static Boolean convert2Boolean(Object toConvert) {
        if (toConvert instanceof Integer) {
            return convertInt2Boolean((Integer) toConvert);
        }
        return Boolean.FALSE;
    }

    public static String convertToString(Number number, String defaultValue) {
        if (number != null) {
            return number.toString();
        }
        return defaultValue;
    }

    /**
     * Summiert die Werte der beiden Integer-Objekte und gibt ein Integer-Objekt mit der Summe zurueck.
     */
    public static Integer add(Integer value, Integer toAdd) {
        if (toAdd != null) {
            if (value != null) {
                return value + toAdd;
            }
            return toAdd;
        }
        return value;
    }

    /**
     * Ueberprueft, ob der Wert von <code>toCheck</code> kleiner als <code>base</code> ist.
     *
     * @param toCheck zu pruefender Wert
     * @param base    Wert, gegen den geprueft wird
     * @return true wenn <code>toCheck</code> kleiner als <code>base</code> ist.
     */
    public static boolean isLess(Number toCheck, Number base) {
        if ((toCheck != null) && (base != null)) {
            return Double.valueOf(toCheck.doubleValue()).compareTo(Double.valueOf(base.doubleValue())) < 0;
        }
        return false;
    }

    /**
     * Ueberprueft, ob der Wert von <code>toCheck</code> kleiner oder gleich dem Wert <code>base</code> ist.
     *
     * @param toCheck zu pruefender Wert
     * @param base    Wert, gegen den geprueft wird
     * @return true wenn <code>toCheck</code> kleiner/gleich <code>base</code> ist.
     */
    public static boolean isLessOrEqual(Number toCheck, Number base) {
        if ((toCheck != null) && (base != null)) {
            return Double.valueOf(toCheck.doubleValue()).compareTo(Double.valueOf(base.doubleValue())) <= 0;
        }
        return false;
    }

    /**
     * Ueberprueft, ob der Wert von <code>toCheck</code> groesser als <code>base</code> ist.
     *
     * @param toCheck zu pruefender Wert
     * @param base    Wert, gegen den geprueft wird
     * @return true wenn <code>toCheck</code> groesser als <code>base</code> ist.
     */
    public static boolean isGreater(Number toCheck, Number base) {
        if ((toCheck != null) && (base != null)) {
            return Double.valueOf(toCheck.doubleValue()).compareTo(Double.valueOf(base.doubleValue())) > 0;
        }
        return false;
    }

    /**
     * Ueberprueft, ob der Wert von <code>toCheck</code> groesser/gleich als <code>base</code> ist.
     *
     * @param toCheck zu pruefender Wert
     * @param base    Wert, gegen den geprueft wird
     * @return true wenn <code>toCheck</code> groesser/gleich als <code>base</code> ist.
     */
    public static boolean isGreaterOrEqual(Number toCheck, Number base) {
        if ((toCheck != null) && (base != null)) {
            return Double.valueOf(toCheck.doubleValue()).compareTo(Double.valueOf(base.doubleValue())) >= 0;
        }
        return false;
    }

    /**
     * Rundet den angegebenen Float-Wert auf die naechsten 100.
     *
     * @param toRound zu rundender Wert
     */
    public static int roundToNextHundred(float toRound) {
        return (int) (Math.ceil(toRound / 100) * 100);
    }
}


