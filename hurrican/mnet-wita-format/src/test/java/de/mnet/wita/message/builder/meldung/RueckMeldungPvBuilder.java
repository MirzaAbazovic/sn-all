/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 18:21:38
 */
package de.mnet.wita.message.builder.meldung;

import de.mnet.wita.message.builder.meldung.attribute.AbgebenderProviderBuilder;
import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.message.meldung.position.AbgebenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class RueckMeldungPvBuilder extends
        MessageBuilder<RueckMeldungPv, RueckMeldungPvBuilder, MeldungsPosition> {

    private AbgebenderProvider abgebenderProvider = new AbgebenderProviderBuilder().build();
    private final Leitung leitung = new LeitungBuilder().buildValid();
    private final String anschlussOnkz = "89";
    private final String anschlussRufnummer = "12345";
    private String vorabstimmungsId;

    @Override
    public RueckMeldungPv build() {
        RueckMeldungPv ruemPv = new RueckMeldungPv();
        addCommonFields(ruemPv);

        ruemPv.setExterneAuftragsnummer(externeAuftragsnummer);
        ruemPv.setKundenNummer(kundennummer);
        ruemPv.setKundennummerBesteller(kundennummerBesteller);
        ruemPv.setVertragsNummer(vertragsnummer);
        ruemPv.setAbgebenderProvider(abgebenderProvider);
        ruemPv.getMeldungsPositionen().addAll(getMeldungspositionen());
        ruemPv.setLeitung(leitung);
        ruemPv.setAnschlussOnkz(anschlussOnkz);
        ruemPv.setAnschlussRufnummer(anschlussRufnummer);
        ruemPv.setVorabstimmungsId(vorabstimmungsId);

        return ruemPv;
    }

    public RueckMeldungPvBuilder withAbgebenderProvider(AbgebenderProvider abgebenderProvider) {
        this.abgebenderProvider = abgebenderProvider;
        return this;
    }

    public RueckMeldungPvBuilder withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition(
                RueckMeldungPv.MELDUNGSCODE,
                RueckMeldungPv.MELDUNGSTEXT);
    }

}
