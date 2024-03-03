/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 18:21:38
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.meldung.EntgeltMeldungPv;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class EntgeltMeldungPvBuilder extends
        MessageBuilder<EntgeltMeldungPv, EntgeltMeldungPvBuilder, MeldungsPosition> {

    private final LocalDate entgelttermin = LocalDate.of(2011, 6, 1);

    @Override
    public EntgeltMeldungPv build() {
        EntgeltMeldungPv entmPv = new EntgeltMeldungPv();
        addCommonFields(entmPv);

        entmPv.setExterneAuftragsnummer(externeAuftragsnummer);
        entmPv.setKundenNummer(kundennummer);
        entmPv.setVertragsNummer(vertragsnummer);
        entmPv.getMeldungsPositionen().addAll(getMeldungspositionen());
        entmPv.setEntgelttermin(entgelttermin);
        return entmPv;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("0010", "Auftrag ausgeführt.");
    }

}
