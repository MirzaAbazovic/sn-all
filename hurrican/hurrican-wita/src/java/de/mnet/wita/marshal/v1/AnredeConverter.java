/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.marshal.v1;

import de.mnet.wita.message.auftrag.Anrede;

public class AnredeConverter {

    /**
     * Converts the anrede enum in wita specific numbers - must not be contained in mnet wita format as it is Telekom
     * specific information subject to changes.
     */
    public static String toWita(Anrede anrede, boolean isFirma) {
        if (isFirma) {
            switch (anrede) {
                case FIRMA:
                    return "4";
                case UNBEKANNT:
                    return "9";
                default:
            }
        }
        else {
            switch (anrede) {
                case FRAU:
                    return "2";
                case HERR:
                    return "1";
                case UNBEKANNT:
                    return "9";
                default:
            }
        }
        throw new RuntimeException("Unknown value for field anrede. Business: " + isFirma + " - Anrede: " + anrede);
    }

}
