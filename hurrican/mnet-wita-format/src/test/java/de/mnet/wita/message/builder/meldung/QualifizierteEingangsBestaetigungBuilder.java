package de.mnet.wita.message.builder.meldung;

import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;

public class QualifizierteEingangsBestaetigungBuilder
        extends
        MessageBuilder<QualifizierteEingangsBestaetigung, QualifizierteEingangsBestaetigungBuilder, MeldungsPositionWithAnsprechpartner> {

    private final AnsprechpartnerTelekom ansprechpartnerTelekom = new AnsprechpartnerTelekom(Anrede.UNBEKANNT, "vorname",
            "nachname",
            "012/345678");

    @Override
    public QualifizierteEingangsBestaetigung build() {
        QualifizierteEingangsBestaetigung qeb = new QualifizierteEingangsBestaetigung(externeAuftragsnummer,
                kundennummer, kundennummerBesteller);
        addCommonFields(qeb);
        qeb.getMeldungsPositionen().addAll(getMeldungspositionen());
        return qeb;
    }

    @Override
    protected MeldungsPositionWithAnsprechpartner getDefaultMeldungsPosition() {
        return new MeldungsPositionWithAnsprechpartner("OK", "OK", ansprechpartnerTelekom);
    }
}
