/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 11:49:32
 */
package de.augustakom.common.tools.lang;

import java.util.*;
import java.util.stream.*;


/**
 * Hilfsklasse mit Tools fuer Zufallsgeneratoren.
 *
 *
 */
public class RandomTools {

    /* Passwort-Chars fuer das erste und letzte Zeichen eines Passworts */
    private static final char[] LITERAL_CHARS = new char[] {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    /* Passwort-Chars fuer das 2. - 7. Zeichen eines Passworts */
    private static final char[] CHARS_4_PASSWORD = new char[] {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '2', '3', '4', '5', '6', '7', '8', '9' };

    public static <E extends Enum<?>> E randomEnumValueOf(final Class<E> theEnum) {
        final E[] values = theEnum.getEnumConstants();
        final int randomInt = createInteger(values.length);
        return Stream.of(values).filter(e -> e.ordinal() == randomInt).findFirst().get();
    }

    /**
     * Erzeugt ein zufaelliges Password.
     *
     * @param length Angabe der geforderten Passwort-Laenge.
     * @return das generierte Passwort.
     */
    public static String createPassword(int length) {
        Random random = new Random();

        StringBuilder pw = new StringBuilder(length);
        char begin = LITERAL_CHARS[random.nextInt(LITERAL_CHARS.length)];
        pw.append(begin);

        int availableCharLength = CHARS_4_PASSWORD.length;
        int randomLength = length - 2;
        for (int i = 0; i < randomLength; i++) {
            char next = CHARS_4_PASSWORD[random.nextInt(availableCharLength)];
            pw.append(next);
        }

        char end = LITERAL_CHARS[random.nextInt(LITERAL_CHARS.length)];
        pw.append(end);

        return pw.toString();
    }

    /**
     * erzeugt einen zufaelligen Long Wert, der im Bereich zwischen 0 und Long.MAX_VALUE liegt
     */
    public static Long createLong() {
        return createLong(Long.MAX_VALUE);
    }

    /**
     * erzeugt einen zufaelligen Long Wert, der im Bereich zwischen 0 und {@code range} liegt
     */
    public static Long createLong(Long range) {
        return Long.valueOf((long) (Math.random() * ((range != null) ? range.doubleValue() : 1.0)));
    }

    /**
     * erzeugt einen zufaelligen Integer Wert, der im Bereich zwischen 0 und Integer.MAX_VALUE liegt
     */
    public static Integer createInteger() {
        return createInteger(Integer.MAX_VALUE);
    }

    /**
     * erzeugt einen zufaelligen Integer Wert, der im Bereich zwischen 0 und {@code range} liegt
     */
    public static Integer createInteger(Integer range) {
        return Integer.valueOf((int) (Math.random() * ((range != null) ? range.doubleValue() : 1.0)));
    }

    /**
     * erzeugt einen zufaelligen String
     */
    public static String createString() {
        return UUID.randomUUID().toString();
    }
}


