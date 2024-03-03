/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2004
 */
package de.augustakom.common.tools.lang;

import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.regex.*;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;

/**
 * Hilfsklasse fuer String-Operationen.
 */
public class StringTools {

    /**
     * Default charset for all byte to String conversions (String and IO)
     */
    public static final Charset CC_DEFAULT_CHARSET = Charsets.ISO_8859_1;

    /**
     * Formatiert den String fuer Ausgaben an der GUI. <br> Platzhalter in <code>toFormat</code> werden durch die
     * Parameter in <code>params</code> ersetzt. <br> Die Platzhalter werden wie folgt angegeben: {x} wobei x mit einer
     * fortlaufenden Nummer (beginnend bei 0) zu ersetzen ist. <br>
     *
     * @param toFormat String, der Platzhalter enthaelt, die ersetzt werden sollen.
     * @param params   Parameter, die an Stelle der Platzhalter angezeigt werden sollen.
     * @param locale   Angabe der Locale (optional)
     * @return String, bei dem die Platzhalter durch die Parameter ersetzt sind.
     * @see java.text.MessageFormat
     */
    public static String formatString(String toFormat, Object[] params,
            Locale locale) {
        String result = toFormat;
        if (toFormat != null) {
            MessageFormat mf;
            if (locale == null) {
                mf = new MessageFormat(toFormat);
            }
            else {
                mf = new MessageFormat(toFormat, locale);
            }

            result = mf.format(params);
        }

        return result;
    }

    /**
     * @see #formatString(String, Object[], Locale)
     */
    public static String formatString(String toFormat, Object[] params) {
        return formatString(toFormat, params, null);
    }

    /**
     * Ueberprueft, ob der String <code>toCheck</code> mit <code>prefix</code> beginnt.
     *
     * @param toCheck zu pruefender String
     * @param prefix  String, der am Anfang von <code>toCheck</code> erwartet wird.
     * @return true wenn <code>toCheck</code> mit <code>prefix</code> beginnt.
     */
    public static boolean startsWith(String toCheck, String prefix) {
        return (toCheck != null) && (prefix != null) && toCheck.startsWith(prefix);
    }

    /**
     * Ueberprueft, ob der String <code>toCheck</code> mit <code>suffix</code> endet.
     *
     * @param toCheck zu pruefender String
     * @param suffix  String, der am Ende von <code>toCheck</code> erwartet wird.
     * @return true wenn <code>toCheck</code> mit <code>suffix</code> endet.
     */
    public static boolean endsWith(String toCheck, String suffix) {
        return (toCheck != null) && (suffix != null) && toCheck.endsWith(suffix);

    }

    /**
     * Entfernt das Zeichen toRemove vom Anfang eines Strings.
     */
    public static String removeStartToEmpty(String str, char toRemove) {
        if (str == null) {
            return null;
        }
        return CharMatcher.is(toRemove).trimLeadingFrom(str);
    }

    /**
     * Ueberprueft, ob der Wert <code>toCheck</code> in dem Array <code>values</code> vor kommt.
     *
     * @param toCheck zu pruefender String
     * @param values  die moeglichen Werte
     * @return true wenn der String 'toCheck' in dem Array gefunden wird.
     */
    public static boolean isIn(String toCheck, String[] values) {
        if ((values != null) && (values.length > 0)) {
            for (String value : values) {
                if (StringUtils.equals(toCheck, value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNotIn(String toCheck, String[] values) {
        return !isIn(toCheck, values);
    }

    /**
     * Erweitert den String <code>toFill</code> um das Zeichen <code>fillChar</code> bis die angegebene Laenge erreicht
     * ist.
     *
     * @param toFill         Basis-String
     * @param expectedLength erwartete Mindestlaenge
     * @param fillChar       Zeichen, mit dem aufgefuellt werden soll
     * @param fillFromStart  Flag, ob die Zeichen an den Anfang (true) oder das Ende (false) angehaengt werden sollen.
     * @return der erweiterte String
     */
    public static String fillToSize(String toFill, int expectedLength, char fillChar, boolean fillFromStart) {
        final Function<Integer, String> repeatChar = n -> StringUtils.repeat(String.valueOf(fillChar), n);
        if (StringUtils.isNotBlank(toFill)) {
            if (toFill.length() < expectedLength) {
                int diff = expectedLength - toFill.length();
                if (fillFromStart) {
                    return repeatChar.apply(diff) + toFill;
                }
                else {
                    return toFill + repeatChar.apply(diff);
                }
            }
            else {
                return toFill;
            }
        }
        else {
            return repeatChar.apply(expectedLength);
        }
    }

    /**
     * @param strings           Array mit den zu verbindenden Strings
     * @param separator         Trennzeichen
     * @param ignoreBlankOrNull durch Angabe von 'true' werden leere Strings und 'nulls' aus dem Array heraus gefiltert
     *                          und erst dann der Join durchgefuehrt.
     * @return zusammengesetzter String
     * @see StringUtils#join(Object[], String)
     */
    public static String join(String[] strings, String separator, boolean ignoreBlankOrNull) {
        if (ignoreBlankOrNull && (strings != null)) {
            List<String> joins = new ArrayList<>();
            for (String s : strings) {
                if (StringUtils.isNotBlank(s)) {
                    joins.add(s);
                }
            }

            return StringUtils.join(joins.toArray(), separator);
        }

        return StringUtils.join(strings, separator);
    }

    /**
     * Ersetzt bestimmte Zeichen im String 's' durch andere Zeichen. In der Map 'replaces' sind als Key die Zeichen
     * angegeben, die ersetzt werden sollen. Als Value ist der neu zu verwendende String angegeben.
     *
     * @param s        String, der zu ersetzende Zeichen erhaelt
     * @param replaces Map mit den zu ersetzenden Zeichen
     * @return String mit den ersetzten Zeichen
     */
    public static String replaceChars(String s, Map<String, String> replaces) {
        if (StringUtils.isNotBlank(s)) {
            if (replaces != null) {
                for (Entry<String, String> repEntry : replaces.entrySet()) {
                    String toReplace = repEntry.getKey();
                    String valueToUse = repEntry.getValue();
                    s = StringUtils.replace(s, toReplace, valueToUse);
                }
                return s;
            }
            else {
                return s;
            }
        }
        else {
            return s;
        }
    }

    /**
     * Ermittelt aus einem String lediglich die Zahlenwerte und gibt diese als Number-Wert zurueck. <br> Bsp.: A12.234
     * --> 12234 1 345 B --> 1345
     *
     * @param s der String, von dem nur die Zahlenwerte ermittelt werden sollen
     * @return Zahlen aus dem String 's'
     */
    public static String getDigitsFromString(String s) {
        if (StringUtils.isNotBlank(s)) {
            return s.replaceAll("\\D", "");
        }
        return null;
    }

    /**
     * Ersetzt die Umlaute Ä/Ö/Ü/ß in dem String durch Ae/Oe/Ue/ss.
     */
    public static String replaceGermanUmlaute(String s) {
        String replaced = StringUtils.replace(s, "Ö", "Oe");
        replaced = StringUtils.replace(replaced, "ö", "oe");
        replaced = StringUtils.replace(replaced, "Ä", "Ae");
        replaced = StringUtils.replace(replaced, "ä", "ae");
        replaced = StringUtils.replace(replaced, "Ü", "Ue");
        replaced = StringUtils.replace(replaced, "ü", "ue");
        replaced = StringUtils.replace(replaced, "ß", "ss");
        return replaced;
    }

    /**
     * Filtert alle Sonderzeichen, die nicht [a-zA-Z0-9] entsprechen aus dem String heraus.
     */
    public static String filterSpecialChars(String s) {
        if (StringUtils.isNotBlank(s)) {
            int size = s.length();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < size; i++) {
                char charToCheck = s.charAt(i);
                if (Pattern.matches("[a-zA-Z0-9_]", Character.valueOf(charToCheck).toString())) {
                    result.append(charToCheck);
                }
            }
            return result.toString();
        }
        return s;
    }


    /**
     * Liefert -1, 1, 1 zurueck falls String s1 lexikografisch kleiner, gleich, groesser als s2 ist. Falls nullsGreater
     * gesetzt ist, werden nulls ans Ende sortiert, falls nicht, an den Anfang.
     *
     * @param s1           Erster String, darf {@code null} sein
     * @param s2           Zweiter String, darf {@code null} sein
     * @param nullsGreater Falls {@code true}, werden {@code null}-Werte als lexikografisch groesser angesehen.
     */
    public static int compare(String s1, String s2, boolean nullsGreater) {
        if ((s1 == null) && (s2 == null)) {
            return 0;
        }
        if ((s1 != null) && (s2 != null)) {
            return s1.compareTo(s2);
        }
        if (s1 == null) { // && s2 != null
            return nullsGreater ? 1 : -1;
        }
        else { // s1 != null && s2 == null
            return nullsGreater ? -1 : 1;
        }
    }
}
