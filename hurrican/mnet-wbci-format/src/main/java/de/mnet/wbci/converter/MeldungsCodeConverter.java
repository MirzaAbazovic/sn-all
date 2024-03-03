/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.wbci.converter;


import java.util.*;
import org.springframework.util.StringUtils;

import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;

/**
 * Hilfsklasse fuer WBCI Meldungs-Codes.
 *
 *
 */
public class MeldungsCodeConverter {

    public static final String SEPARATOR = ",";

    /**
     * Erstellt aus den Meldungs-Codes einen mit {@link #SEPARATOR} separierten String.
     *
     * @param meldungsCodes an amount of MeldungCodes
     * @return
     */
    public static String meldungcodesToCodeString(MeldungsCode... meldungsCodes) {
        List<String> codes = new ArrayList<>();
        for (MeldungsCode mc : meldungsCodes) {
            if (mc != null) {
                codes.add(mc.name());
            }
        }
        return StringUtils.arrayToCommaDelimitedString(codes.toArray());
    }

    /**
     * Checks for the presence of any of the {@code meldungsCode}s in the {@code codeString}.
     *
     * @param codeString   a list of {@link MeldungsCode}s, separated by comma
     * @param meldungsCode the codes to be searched for
     * @return true if any of the {@code meldungsCode}s are present in the {@code codeString}, otherwise false.
     */
    public static boolean isMeldungscodeInCodeString(String codeString, MeldungsCode... meldungsCode) {
        if (StringUtils.hasText(codeString)) {
            StringTokenizer stringTokenizer = new StringTokenizer(codeString, SEPARATOR);
            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                for (MeldungsCode code : meldungsCode) {
                    if (code.name().equals(token)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Ermittelt die Meldungs-Codes aus den angegebenen {@link MeldungPosition}en und erstellt aus diesen einen mit
     * {@link #SEPARATOR} separierten String.
     *
     * @param meldungsPositionen
     * @param <MP>
     * @return
     */
    public static <MP extends MeldungPosition> String meldungsPositionToCodeString(Set<MP> meldungsPositionen) {
        List<String> codes = new ArrayList<>();
        int i = 0;
        for (MP mp : meldungsPositionen) {
            if (mp.getMeldungsCode() != null) {
                codes.add(mp.getMeldungsCode().name());
                i++;
            }
        }
        return StringUtils.arrayToCommaDelimitedString(codes.toArray());
    }

    /**
     * Ermittelt die Meldungs-Codes aus den angegebenen {@link MeldungPosition}en .
     *
     * @param meldungsPositionen
     * @param <MP>
     * @return
     */
    public static <MP extends MeldungPosition> Set<MeldungsCode> retrieveMeldungCodes(Set<MP> meldungsPositionen) {
        Set<MeldungsCode> meldungCodes = new HashSet<>();
        for (MP mp : meldungsPositionen) {
            if (mp.getMeldungsCode() != null) {
                meldungCodes.add(mp.getMeldungsCode());
            }
        }
        return meldungCodes;
    }

    /**
     * Ermittelt die Meldungs-Codes aus den angegebenen {@link MeldungPosition}en und erstellt aus diesen einen mit
     * {@link #SEPARATOR} separierten String.
     *
     * @param meldungsPositionen
     * @param <MP>
     * @return
     */
    public static <MP extends MeldungPosition> String meldungsPositionToTextString(Set<MP> meldungsPositionen) {
        List<String> texte = new ArrayList<>();
        int i = 0;
        for (MP mp : meldungsPositionen) {
            if (StringUtils.hasText(mp.getMeldungsText())) {
                texte.add(mp.getMeldungsText());
                i++;
            }
        }
        return StringUtils.arrayToCommaDelimitedString(texte.toArray());
    }

    /**
     * Konvertiert einen mit {@link #SEPARATOR}  separierten String von WBCI Meldungs-Codes in eine entsprechende enum
     * Liste.
     *
     * @param stringValue
     * @return
     */
    public static Set<MeldungsCode> retrieveMeldungsCodes(String stringValue) {
        if (StringUtils.hasText(stringValue)) {
            Set<MeldungsCode> result = new HashSet<>();
            for (String mcodeName : stringValue.split(SEPARATOR)) {
                result.add(MeldungsCode.buildFromName(mcodeName));
            }
            return result;
        }
        return Collections.emptySet();
    }

}
