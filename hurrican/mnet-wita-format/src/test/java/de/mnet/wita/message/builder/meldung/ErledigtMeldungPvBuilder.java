/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 18:21:38
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class ErledigtMeldungPvBuilder extends
        MessageBuilder<ErledigtMeldungPv, ErledigtMeldungPvBuilder, MeldungsPosition> {

    private final Leitung leitung = new LeitungBuilder().withLeitungsAbschnittList(null).withMaxBruttoBitrate(null).buildValid();
    private final String anschlussOnkz = null;
    private final String anschlussRufnummer = null;
    private final LocalDate erledigungstermin = LocalDate.now();

    @Override
    public ErledigtMeldungPv build() {
        ErledigtMeldungPv erlmPv = new ErledigtMeldungPv();
        addCommonFields(erlmPv);

        erlmPv.setExterneAuftragsnummer(externeAuftragsnummer);
        erlmPv.setKundenNummer(kundennummer);
        erlmPv.setVertragsNummer(vertragsnummer);
        erlmPv.setLeitung(leitung);
        erlmPv.setAnschlussOnkz(anschlussOnkz);
        erlmPv.setAnschlussRufnummer(anschlussRufnummer);
        erlmPv.getMeldungsPositionen().addAll(getMeldungspositionen());
        erlmPv.setErledigungstermin(erledigungstermin);
        return erlmPv;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("0010", "Auftrag ausgeführt.");
    }

}
