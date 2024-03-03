/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.14
 */
package de.mnet.wita.message.meldung.converter;

import java.util.*;
import org.springframework.util.StringUtils;

import de.mnet.wita.message.meldung.position.MeldungsPosition;

/**
 * Helper class for meldungs codes and text.
 *
 *
 */
public class MeldungsCodeConverter {

    /**
     * Ermittelt die Meldungs-Codes aus den angegebenen Positionen und erstellt aus diesen einen mit
     * Komma separierten String.
     *
     * @param meldungsPositionen
     * @param <MP>
     * @return
     */
    public static <MP extends MeldungsPosition> String meldungsPositionToCodeString(Set<MP> meldungsPositionen) {
        List<String> codes = new ArrayList<>();
        int i = 0;
        for (MP mp : meldungsPositionen) {
            if (StringUtils.hasText(mp.getMeldungsCode())) {
                codes.add(mp.getMeldungsCode());
                i++;
            }
        }
        return StringUtils.arrayToCommaDelimitedString(codes.toArray());
    }

    /**
     * Ermittelt die Meldungs-Codes aus den angegebenen Positionen und erstellt aus diesen einen mit
     * Komma separierten String.
     *
     * @param meldungsPositionen
     * @param <MP>
     * @return
     */
    public static <MP extends MeldungsPosition> String meldungsPositionToTextString(Set<MP> meldungsPositionen) {
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
}
