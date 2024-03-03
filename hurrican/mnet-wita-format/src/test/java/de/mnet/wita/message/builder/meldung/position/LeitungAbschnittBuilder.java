package de.mnet.wita.message.builder.meldung.position;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.builder.AbstractWitaBuilder;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;

/**
 *
 */
public class LeitungAbschnittBuilder extends AbstractWitaBuilder<LeitungsAbschnitt> {

    private Integer laufendeNummer;
    private String leitungsLaenge;
    private String leitungsDurchmesser;

    public LeitungAbschnittBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    public LeitungsAbschnitt buildValid() {
        return build();
    }

    public LeitungsAbschnitt build() {
        LeitungsAbschnitt leitungsAbschnitt = new LeitungsAbschnitt();
        leitungsAbschnitt.setLaufendeNummer(laufendeNummer);
        leitungsAbschnitt.setLeitungsDurchmesser(leitungsDurchmesser);
        leitungsAbschnitt.setLeitungsLaenge(leitungsLaenge);
        return leitungsAbschnitt;
    }

    public LeitungAbschnittBuilder withLeitungsLaenge(String leitungsLaenge) {
        this.leitungsLaenge = leitungsLaenge;
        return this;
    }

    public LeitungAbschnittBuilder withLeitungsDurchmesser(String leitungsDurchmesser) {
        this.leitungsDurchmesser = leitungsDurchmesser;
        return this;
    }

    public LeitungAbschnittBuilder withLaufendeNummer(Integer laufendeNummer) {
        this.laufendeNummer = laufendeNummer;
        return this;
    }

}
