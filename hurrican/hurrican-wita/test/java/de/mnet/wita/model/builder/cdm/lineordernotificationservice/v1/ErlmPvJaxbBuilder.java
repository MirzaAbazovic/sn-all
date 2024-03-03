/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERLMPVType;
import de.mnet.wita.message.meldung.position.Leitung;

public class ErlmPvJaxbBuilder extends MeldungWithProduktpositionJaxbBuilder<ErlmPvJaxbBuilder> {

    private Leitung leitung;
    private String anschlussOnkz;
    private String anschlussRufnummer;
    private final LocalDate erledigungstermin = LocalDate.of(2011, 5, 30);

    public MeldungstypERLMPVType build() {
        return new MeldungstypERLMPVTypeBuilder()
                .withMeldungsattribute(
                        new MeldungsattributeERLMPVTypeBuilder()
                                .withKundennummer(kundenNummer)
                                .withVertragsnummer(vertragsNummer)
                                .withErledigungstermin(erledigungstermin)
                                .withTal(
                                        new MeldungsattributeTALERLMPVTypeBuilder()
                                                .withLeitung(buildLeitung(leitung))
                                                .withAnschluss(buildAnschluss(anschlussOnkz, anschlussRufnummer))
                                                .build()
                                )
                                .build()
                )
                .withPositionen(meldungspositionen)
                .build();
    }

    public ErlmPvJaxbBuilder withLeitung(Leitung leitung) {
        this.leitung = leitung;
        return this;
    }

    public ErlmPvJaxbBuilder withAnschluss(String onkz, String rufnummer) {
        this.anschlussOnkz = onkz;
        this.anschlussRufnummer = rufnummer;
        return this;
    }

}
