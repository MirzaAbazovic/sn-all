/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 14:58:10
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class EntgeltMeldungBuilder extends MessageBuilder<EntgeltMeldung, EntgeltMeldungBuilder, MeldungsPosition> {

    private LocalDate entgelttermin = LocalDate.of(2011, 6, 1);

    @Override
    public EntgeltMeldung build() {
        EntgeltMeldung result = new EntgeltMeldung(externeAuftragsnummer, kundennummer, entgelttermin, vertragsnummer);
        addCommonFields(result);

        result.setKundennummerBesteller(kundennummerBesteller);
        result.getMeldungsPositionen().addAll(getMeldungspositionen());
        result.getProduktPositionen().addAll(getProduktpositionen());
        result.getAnlagen().addAll(anlagen);
        return result;
    }

    public EntgeltMeldungBuilder withEntgelttermin(LocalDate entgelttermin) {
        this.entgelttermin = entgelttermin;
        return this;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("OK", "OK");
    }
}
