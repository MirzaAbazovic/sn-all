/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 14:58:10
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class ErledigtMeldungBuilder extends MessageBuilder<ErledigtMeldung, ErledigtMeldungBuilder, MeldungsPosition> {

    private LocalDate erledigungstermin = LocalDate.now();

    @Override
    public ErledigtMeldung build() {
        ErledigtMeldung result = new ErledigtMeldung(externeAuftragsnummer, kundennummer, kundennummerBesteller);
        addCommonFields(result);

        result.setErledigungstermin(erledigungstermin);
        result.setVertragsNummer(vertragsnummer);
        result.getMeldungsPositionen().addAll(getMeldungspositionen());
        if (!produktPositionen.isEmpty()) {
            result.getProduktPositionen().addAll(getProduktpositionen());
        }
        return result;
    }

    public ErledigtMeldungBuilder withErledigungstermin(LocalDate erledigungstermin) {
        this.erledigungstermin = erledigungstermin;
        return this;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("OK", "OK");
    }
}
