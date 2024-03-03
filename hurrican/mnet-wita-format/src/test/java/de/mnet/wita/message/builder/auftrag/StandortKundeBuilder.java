package de.mnet.wita.message.builder.auftrag;

import de.mnet.wita.message.auftrag.StandortKunde;

/**
 *
 */
public class StandortKundeBuilder extends StandortWithPersonBuilder {

    private String gebaeudeteilName;
    private String gebaeudeteilZusatz;
    private String lageTAEDose;

    public StandortKunde build() {
        StandortKunde standortKunde = new StandortKunde();
        super.enrich(standortKunde);
        standortKunde.setGebaeudeteilName(gebaeudeteilName);
        standortKunde.setGebaeudeteilZusatz(gebaeudeteilZusatz);
        standortKunde.setLageTAEDose(lageTAEDose);
        return standortKunde;
    }

    public StandortKundeBuilder withLageTAEDose(String lageTAEDose) {
        this.lageTAEDose = lageTAEDose;
        return this;
    }

}
