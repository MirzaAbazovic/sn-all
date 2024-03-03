/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.06.2011
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class VerzoegerungsMeldungBuilder extends
        MessageBuilder<VerzoegerungsMeldung, VerzoegerungsMeldungBuilder, MeldungsPosition> {

    private LocalDate verzoegerungstermin = LocalDate.of(2011, 6, 1);

    @Override
    public VerzoegerungsMeldung build() {
        VerzoegerungsMeldung result = new VerzoegerungsMeldung(externeAuftragsnummer, kundennummer,
                kundennummerBesteller, vertragsnummer, verzoegerungstermin);
        addCommonFields(result);

        result.getMeldungsPositionen().addAll(getMeldungspositionen());
        return result;
    }

    public VerzoegerungsMeldungBuilder withVerzoegerungstermin(LocalDate verzoegerungstermin) {
        this.verzoegerungstermin = verzoegerungstermin;
        return this;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("OK", "OK");
    }
}
