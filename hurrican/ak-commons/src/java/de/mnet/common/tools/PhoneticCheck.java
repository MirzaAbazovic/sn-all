/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.10.13
 */
package de.mnet.common.tools;

import java.util.*;
import java.util.regex.*;
import org.apache.commons.codec.language.Caverphone2;
import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Nysiis;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

// @formatter:off
/**
 * Kopie der Klasse {@code de.mnet.vento.phonetic.PhoneticCheck} aus dem VENTO Projekt! <br/>
 * In dieser Implementierung ist jedoch die String-Normalisierung (also z.B. das Ersetzen von Umlauten) bereits
 * eingebaut; in Vento ist dies aus dem PhoneticCheck ausgelagert. <br/><br/>
 * Phonetischer Vergleich von Strings. Details siehe die Beschreibung der einzelnen {@link Codec} Varianten.
 * Da alle Codecs relativ grob sind und teilweise unsinnige Ergebnisse liefern, kann das Ergebniss zusaetzlich mittels
 * Levenshtein gefiltert werden. Dabei wird eine maximale Distanz verwendet die sich aus der Laenge des kuerzeren
 * Strings ergibt.
 *
 * @see <a href="http://commons.apache.org/proper/commons-codec/"> Apache Commons codec</a>
 * @see StringUtils#getLevenshteinDistance(String, String)
 */
// @formatter:on
public class PhoneticCheck {

    public static enum Codec {
        /**
         * Kölner Phonetik: phonetische Vergleich für deutsche Wörter (deutscher Soundex).
         */
        COLOGNE,

        /**
         * Erweiterter Soundex, etwas genauer (aber immer noch ungenauer als Metaphone).
         */
        REFINDED_SOUNDEX,

        /**
         * Genauer als Soundex, allerdings auch fuer die englische Sprache optimiert.
         */
        METAPHONE,

        /**
         * Erweiterter und genauere Metaphone Variante.
         */
        DOUBLE_METAPHONE,

        /**
         * Erweiterter und genauere Metaphone Variante (Alternative Berechnung).
         */
        DOUBLE_METAPHONE_ALT,

        /**
         *
         */
        NYSIIS,

        /**
         *
         */
        CAVERPHONE2
    }

    private static final Logger LOGGER = Logger.getLogger(PhoneticCheck.class);

    private static ColognePhonetic colognePhonetic = new ColognePhonetic();
    private static Metaphone metaphone = new Metaphone();
    private static DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
    private static RefinedSoundex refSoundex = new RefinedSoundex();
    private static Nysiis nysiis = new Nysiis();
    private static Caverphone2 caverphone2 = new Caverphone2();

    private Codec codec;
    private boolean filterByLevenshtein;
    private NormalizePattern[] normalizePatterns = null;

    /**
     * @param codec
     * @param filterByLevenshtein wenn true bei positivem phonetischen Vergleich, zusaetzlich mit Levenshtein gefiltert
     */
    public PhoneticCheck(Codec codec, boolean filterByLevenshtein) {
        this.codec = codec;
        this.filterByLevenshtein = filterByLevenshtein;
    }

    /**
     * @param codec
     * @param filterByLevenshtein wenn true bei positivem phonetischen Vergleich, zusaetzlich mit Levenshtein gefiltert
     * @param normalizePatterns   Angabe von {@link NormalizePattern}s, mit denen die zu vergleichenden Strings zuerst
     *                            normalisiert werden sollen.
     */
    public PhoneticCheck(Codec codec, boolean filterByLevenshtein, NormalizePattern... normalizePatterns) {
        this.codec = codec;
        this.filterByLevenshtein = filterByLevenshtein;
        this.normalizePatterns = normalizePatterns;
    }

    /**
     * Prueft, ob die beiden angegebenen Strings 'phonetisch identisch' sind. <br> Die Strings werden vor der
     * phonetischen Pruefung normalisiert, da einige Codecs z.B. mit Umlauten nicht gut zurecht kommen.
     *
     * @return true wenn die beiden String 'phonetisch identisch' sind; sonst false.
     */
    public boolean isPhoneticEqual(String base, String compareTo) {
        String s1 = normalize(base);
        String s2 = normalize(compareTo);

        try {
            boolean phoneticEqual = false;
            switch (codec) {
                case COLOGNE:
                    phoneticEqual = colognePhonetic.isEncodeEqual(s1, s2);
                    break;
                case METAPHONE:
                    phoneticEqual = metaphone.isMetaphoneEqual(s1, s2);
                    break;
                case DOUBLE_METAPHONE:
                    phoneticEqual = doubleMetaphone.isDoubleMetaphoneEqual(s1, s2);
                    break;
                case DOUBLE_METAPHONE_ALT:
                    phoneticEqual = doubleMetaphone.isDoubleMetaphoneEqual(s1, s2, true);
                    break;
                case REFINDED_SOUNDEX:
                    phoneticEqual = refSoundex.difference(s1, s2) >= 4;
                    break;
                case NYSIIS:
                    phoneticEqual = nysiis.encode(s1).equals(nysiis.encode(s2));
                    break;
                case CAVERPHONE2:
                    phoneticEqual = caverphone2.isEncodeEqual(s1, s2);
                    break;
                default:
                    return false;
            }
            boolean levenshteinEqual = true;
            if (phoneticEqual && filterByLevenshtein) {
                int minLen = Math.min(s1.length(), s2.length());
                if (StringUtils.getLevenshteinDistance(s1, s2) > getLevenshteinThreashold(minLen)) {
                    levenshteinEqual = false;
                }
            }
            if (phoneticEqual && levenshteinEqual && LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("s1='%s', s2='%s' %s=%s/%s", s1, s2, codec, (phoneticEqual ? "+" : "-"),
                        (levenshteinEqual ? "+" : "-")));
            }
            return phoneticEqual && levenshteinEqual;
        }
        catch (Exception e) {
            LOGGER.error("encode error for codec/s1/s2=" + codec + "/" + s1 + "/" + s2 + " e=" + e);
            return false;
        }
    }

    /**
     * Berechnet die max. erlaubte Levenshtein Distanz abhaengig von der minimalen Laenge des Strings. Es wird eine
     * logarithmische Funktion verwendet (Laenge <= 8 => threashold = 2; Laenge <= 18 => threashold = 3; Laenge <= 39
     * threashold = 4 ...)
     *
     * @param minLen
     * @return
     */
    private long getLevenshteinThreashold(int minLen) {
        return Math.round(Math.log10(0.5 * minLen * minLen * minLen));
    }

    public Codec getCodec() {
        return codec;
    }

    String normalize(String in) {
        if (StringUtils.isBlank(in)) {
            return "";
        }

        //adding a blank on each side to find patterns at the begingning or at the end.
        String out = " " + in.toLowerCase() + " ";
        for (Map.Entry<Pattern, String> entry : NormalizePattern.STANDARD_PATTERNS.getPatterns().entrySet()) {
            out = entry.getKey().matcher(out).replaceAll(entry.getValue());
        }

        if (normalizePatterns != null) {
            for (NormalizePattern normalizePatternEnum : normalizePatterns) {
                for (Map.Entry<Pattern, String> entry : normalizePatternEnum.getPatterns().entrySet()) {
                    out = entry.getKey().matcher(out).replaceAll(entry.getValue());
                }
            }
        }

        //trim the added blanks
        return out.trim();
    }
}
