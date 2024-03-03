package de.mnet.wita.message.builder.auftrag;

import de.mnet.wita.message.auftrag.AbstractStandort;

/**
 *
 */
abstract class AbstractStandortBuilder {

    void enrich(AbstractStandort abstractStandort) {
        String strassenname = "Musterstrasse";
        abstractStandort.setStrassenname(strassenname);
        String hausnummer = "10";
        abstractStandort.setHausnummer(hausnummer);
        String postleitzahl = "80000";
        abstractStandort.setPostleitzahl(postleitzahl);
        String ortsname = "Muenchen";
        abstractStandort.setOrtsname(ortsname);
        String land = "DE";
        abstractStandort.setLand(land);
    }

}
