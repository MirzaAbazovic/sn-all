/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.06.2011
 */
package de.mnet.wita.message.builder.meldung;

import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class TerminAnforderungsMeldungBuilder extends
        MessageBuilder<TerminAnforderungsMeldung, TerminAnforderungsMeldungBuilder, MeldungsPosition> {

    private boolean mahnTam = false;

    @Override
    public TerminAnforderungsMeldung build() {
        TerminAnforderungsMeldung result = new TerminAnforderungsMeldung(externeAuftragsnummer, kundennummer,
                kundennummerBesteller, vertragsnummer);
        addCommonFields(result);

        result.getMeldungsPositionen().addAll(getMeldungspositionen());
        result.setMahnTam(mahnTam);
        return result;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("OK", "OK");
    }

    public TerminAnforderungsMeldungBuilder withMahnTam(boolean mahnTam) {
        this.mahnTam = mahnTam;
        return this;
    }

}
