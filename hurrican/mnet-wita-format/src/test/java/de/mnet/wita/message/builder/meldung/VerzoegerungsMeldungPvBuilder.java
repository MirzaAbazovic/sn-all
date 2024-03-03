/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class VerzoegerungsMeldungPvBuilder extends
        MessageBuilder<VerzoegerungsMeldungPv, VerzoegerungsMeldungPvBuilder, MeldungsPosition> {

    private LocalDate verzoegerungstermin = LocalDate.of(2011, 6, 1);

    @Override
    public VerzoegerungsMeldungPv build() {
        VerzoegerungsMeldungPv result = new VerzoegerungsMeldungPv();
        addCommonFields(result);

        result.setExterneAuftragsnummer(externeAuftragsnummer);
        result.setKundenNummer(kundennummer);
        result.setKundennummerBesteller(kundennummerBesteller);
        result.setVertragsNummer(vertragsnummer);
        result.setVerzoegerungstermin(verzoegerungstermin);

        result.getMeldungsPositionen().addAll(getMeldungspositionen());
        return result;
    }

    public VerzoegerungsMeldungPvBuilder withVerzoegerungstermin(LocalDate verzoegerungstermin) {
        this.verzoegerungstermin = verzoegerungstermin;
        return this;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("OK", "OK");
    }
}
