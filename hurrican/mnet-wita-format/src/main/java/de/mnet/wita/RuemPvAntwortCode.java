/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2011 13:44:47
 */
package de.mnet.wita;

import java.util.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public enum RuemPvAntwortCode {
    // @formatter:off
    OK                                  (null     , false , null, true, false), // Interner Code

    /** @deprecated ab WITA v7 nicht mehr unterstuetzt */
    NEUER_AUSFUEHRUNGSTERMIN            ("NAT"    , true , null, true, true),
    /** @deprecated ab WITA v7 nicht mehr unterstuetzt */
    NEUER_AUSFUEHRUNGSTERMIN_VERTRAGLICH("NAT AVB", false, null, true, true),

    RUFNUMMER_NICHT_GESCHALTET          ("RNG"    , false, null, false, false),
    ANSCHLUSSINHABER_FALSCH             ("AIF"    , false, null, false, false),
    WEITERE_ANSCHLUSSINHABER            ("WAI"    , false, null, false, false),
    TERMIN_UNGUELTIG                    ("SON"    , true,  "Angefragter Termin ist nicht möglich",  false, false),
    SONSTIGES                           ("SON"    , true , null, false, false),
    UNTERSCHRIFT_FEHLT                  ("KUF"    , false, null, false, false),
    MSN_FALSCH                          ("MSNF"   , false, null, false, false),
    AUFTRAG_NICHT_LESBAR                ("ANL"    , false, null, false, false);
    // @formatter:on

    public final String antwortCode;
    public final boolean antwortTextRequired;
    public final String defaultText;
    public final boolean zustimmung;
    private final boolean deprecated;

    private RuemPvAntwortCode(String antwortCode, boolean antwortTextRequired, String defaultText, boolean zustimmung, boolean deprecated) {
        this.antwortCode = antwortCode;
        this.antwortTextRequired = antwortTextRequired;
        this.defaultText = defaultText;
        this.zustimmung = zustimmung;
        this.deprecated = deprecated;
    }

    public static List<RuemPvAntwortCode> getRuemPvAntwortCodes(final boolean zustimmung) {
        List<RuemPvAntwortCode> result = Lists.newArrayList(values());
        CollectionUtils.filter(result, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return (zustimmung == ((RuemPvAntwortCode) object).zustimmung && !((RuemPvAntwortCode) object).deprecated);
            }
        });
        return result;
    }

}
