/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 14:58:10
 */
package de.mnet.wita.message.builder.meldung;

import de.mnet.wita.message.meldung.ErgebnisMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class ErgebnisMeldungBuilder extends MessageBuilder<ErgebnisMeldung, ErgebnisMeldungBuilder, MeldungsPosition> {

    private String ergebnislink = "https://result.test";

    public ErgebnisMeldungBuilder withErgebnislink(String ergebnislink) {
        this.ergebnislink = ergebnislink;
        return this;
    }

    @Override
    public ErgebnisMeldung build() {
        ErgebnisMeldung result = new ErgebnisMeldung(externeAuftragsnummer, kundennummer, kundennummerBesteller,
                vertragsnummer, ergebnislink);
        addCommonFields(result);
        return result;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        throw new UnsupportedOperationException("must not be used in ERGM.");
    }
}
