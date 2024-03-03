/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 12:54:33
 */
package de.mnet.wita.message.common;

public enum Anlagentyp {
    KUENDIGUNGSSCHREIBEN("Kuendigungsschreiben"),
    LAGEPLAN("Lageplan"),
    KUNDENAUFTRAG("Kundenauftrag"),
    PORTIERUNGSANZEIGE("Portierungsanzeige"),
    KUENDIGUNG_ABGEBENDER_PROVIDER("Kuendigung abgebender Provider"),
    LETZTE_TELEKOM_RECHNUNG("letzte Telekom-Rechnung"),
    SONSTIGE("Sonstige");

    public final String value;

    private Anlagentyp(String value) {
        this.value = value;
    }

    public static Anlagentyp from(String value) {
        for (Anlagentyp anlagentyp : Anlagentyp.values()) {
            if (anlagentyp.value.equals(value)) {
                return anlagentyp;
            }
        }
        return SONSTIGE;
    }
}
