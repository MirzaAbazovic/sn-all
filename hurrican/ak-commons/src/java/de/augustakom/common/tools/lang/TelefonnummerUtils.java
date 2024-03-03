/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2011 18:22:26
 */
package de.augustakom.common.tools.lang;

import java.util.regex.*;
import org.apache.commons.lang.StringUtils;

/**
 * Hilfsklasse fuer die Konvertierung / Verarbeitung von Rufnummern.
 */
public class TelefonnummerUtils {

    /**
     * aus {@code TelefonnummerValidator#DTAG_PHONE_FORMAT} nur mit Beachtung von zwei- bis dreistelliger
     * Laendervorwahl
     */
    private static final String MNET_PHONE_FORMAT = "((\\+\\d{1,3} |0)\\d{2,5} )?\\d{1,14}";

    /**
     * Standardisierte Fehlermeldung inkl. eines Platzhalters für die ursprüngliche Rufnummer
     */
    private static final String ERROR_MSG_NOT_CONVERTABLE_NUMBER = "Die Telefonnummer \"%s\" konnte nicht in ein "
            + "standardisiertes Format konvertiert werden. Bitte achten Sie darauf, dass die Telefonnummer eine "
            + "zwischen Ländervorwahl und ONKZ (falls vorhanden) sowie eine eindeutige Trennung zwischen ONKZ und "
            + "Nummer enthält (je ein Leerzeichen), wie in 0176 12345678 oder +49 89 45200-3461.";

    public static boolean isMatchingMnetPhoneFormat(String rufnummer) {
        return Pattern.compile(MNET_PHONE_FORMAT).matcher(rufnummer).matches();
    }

    /**
     * Versucht die uebergebene Telefonnnummer in das entsprechende, von der DTAG fuer WITA definierte Format zu
     * ueberfuehren
     *
     * @param number die in das M-net-Format zu konvertierende Telefonnummer ( {@link #isMatchingMnetPhoneFormat(String)}
     * @return die in das DTAG-Format ueberfuehrte Telefonnummer als {@link String}
     * @throws RuntimeException wenn das Ergenis der Konvertierung nicht dem DTAG-Format entspricht
     */
    public static String convertTelefonnummer(String number) {
        if (number == null) {
            return StringUtils.EMPTY;
        }
        if (isMatchingMnetPhoneFormat(number)) {
            return number;
        }

        StringBuilder resultStr = new StringBuilder();
        String dn = extractCountryCode(number, resultStr);

        // Now extract the local area code
        String[] split = extractLocalAreaCode(number, dn);

        removeNoNumericChars(split[0], resultStr);
        resultStr.append(" ");

        // add remainder
        removeNoNumericChars(split[1], resultStr);

        // Finally check for conformity
        String result = resultStr.toString();
        if (!isMatchingMnetPhoneFormat(result)) {
            throw new RuntimeException(String.format(ERROR_MSG_NOT_CONVERTABLE_NUMBER, number));
        }
        return result;
    }

    private static String[] extractLocalAreaCode(String number, String dn) {
        String[] split;// If the string contains a slash, use it as separator
        if (dn.contains("/")) {
            split = StringUtils.split(dn, "/", 2);
        }
        // If the string contains brackets, use it as separator for ONKZ
        else if ((dn.contains("(") && dn.contains(")")) || (dn.contains("[") && dn.contains("]"))) {
            split = splitByBrackets(dn);
        }
        // If the string contains at least one whitespace, use the first as separator
        else if (dn.contains(" ")) {
            split = StringUtils.split(dn, " ", 2);
        }
        // If the string contains a dash, use it as separator
        else if (dn.contains("-")) {
            split = StringUtils.split(dn, "-", 2);
        }
        else {
            throw new RuntimeException(String.format(ERROR_MSG_NOT_CONVERTABLE_NUMBER, number));
        }
        return split;
    }

    private static String extractCountryCode(String numberIn, StringBuilder str) {
        String number = numberIn;
        Matcher m = Pattern.compile("\\+[ ]*\\d{1,3}[ ]*").matcher(number);
        if (m.find()) {
            str.append(m.group().replaceAll(" ", StringUtils.EMPTY)).append(" ");
            number = StringUtils.remove(number, m.group());
        }
        return number.trim();
    }

    private static String[] splitByBrackets(String numberIn) {
        String number = numberIn;
        Matcher m = Pattern.compile("(\\(.+\\)|\\[.+\\])").matcher(number);
        String onkz = "";
        if (m.find()) {
            StringBuilder str = new StringBuilder();
            removeNoNumericChars(m.group(), str);
            onkz = str.toString();
            number = StringUtils.remove(number, m.group());
        }
        return new String[] { onkz, number.trim() };
    }

    private static void removeNoNumericChars(String number, StringBuilder str) {
        char c;
        for (int i = 0; i < number.length(); i++) {
            c = number.charAt(i);
            if (Character.isDigit(c)) {
                str.append(c);
            }
        }
    }
}
