/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 18:21:38
 */
package de.mnet.wita.message.builder.meldung;

import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.builder.common.portierung.RufnummernPortierungBuilder;
import de.mnet.wita.message.builder.meldung.attribute.AufnehmenderProviderBuilder;
import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class AnkuendigungsMeldungPvBuilder extends
        MessageBuilder<AnkuendigungsMeldungPv, AnkuendigungsMeldungPvBuilder, MeldungsPosition> {

    private Leitung leitung = new LeitungBuilder().buildValid();
    private final String anschlussOnkz = "89";
    private final String anschlussRufnummer = "12345";

    private AufnehmenderProvider aufnehmenderProvider = new AufnehmenderProviderBuilder().build();
    private Kundenname endkunde = defaultEndkunde();
    private RufnummernPortierung rufnummernPortierung = new RufnummernPortierungBuilder().buildMeldungPortierung(true);
    private String vorabstimmungsId;

    @Override
    public AnkuendigungsMeldungPv build() {
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPv();
        addCommonFields(akmPv);
        akmPv.setKundenNummer(kundennummer);
        akmPv.setVertragsNummer(vertragsnummer);
        akmPv.setLeitung(leitung);
        akmPv.setAnschlussOnkz(anschlussOnkz);
        akmPv.setAnschlussRufnummer(anschlussRufnummer);
        akmPv.getMeldungsPositionen().addAll(getMeldungspositionen());

        akmPv.setId(id);
        akmPv.setExterneAuftragsnummer(externeAuftragsnummer);
        akmPv.setAufnehmenderProvider(aufnehmenderProvider);
        akmPv.setEndkunde(endkunde);
        akmPv.setRufnummernPortierung(rufnummernPortierung);
        akmPv.getAnlagen().addAll(anlagen);
        akmPv.setVorabstimmungsId(vorabstimmungsId);
        return akmPv;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition(
                "0020",
                "Es liegt uns ein Auftrag zum AnbieterProviderwechsel vor. Wir bitten um Antwort. Erhalten wir im Rahmen der Antwortfrist von Ihnen keine andere Weisung, gehen wir davon aus, dass sie Ihren Einzelvertrags mit der Telekom zum angegebenen Termin kündigen.");
    }

    public AnkuendigungsMeldungPvBuilder withAufnehmenderProvider(AufnehmenderProvider aufnehmenderProvider) {
        this.aufnehmenderProvider = aufnehmenderProvider;
        return this;
    }

    public AnkuendigungsMeldungPvBuilder withEndkunde(Kundenname endkunde) {
        this.endkunde = endkunde;
        return this;
    }

    public AnkuendigungsMeldungPvBuilder withLeitung(Leitung leitung) {
        this.leitung = leitung;
        return this;
    }

    public AnkuendigungsMeldungPvBuilder withRufnummernPortierung(RufnummernPortierung rufnummernPortierung) {
        this.rufnummernPortierung = rufnummernPortierung;
        return this;
    }

    public AnkuendigungsMeldungPvBuilder withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

    private Kundenname defaultEndkunde() {
        Firmenname endkunde = new Firmenname();
        endkunde.setAnrede(Anrede.FIRMA);
        endkunde.setErsterTeil("Mnet");
        endkunde.setZweiterTeil("GmbH");
        return endkunde;
    }

}
