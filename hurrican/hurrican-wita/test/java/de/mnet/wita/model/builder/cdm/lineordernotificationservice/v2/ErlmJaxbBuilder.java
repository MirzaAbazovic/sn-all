/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.time.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypERLMType;

public class ErlmJaxbBuilder extends MeldungJaxbBuilder<ErlmJaxbBuilder> {

    private LocalDate erledigungstermin = LocalDate.of(2011, 5, 30);

    public MeldungstypERLMType build() {
        return new MeldungstypERLMTypeBuilder()
                .withMeldungsattribute(
                        new MeldungsattributeERLMTypeBuilder()
                                .withExterneAuftragsnummer(externeAuftragsnummer)
                                .withKundennummer(kundenNummer)
                                .withErledigungstermin(erledigungstermin)
                                .withVertragsnummer(vertragsNummer)
                                .withKundennummerBesteller(kundennummerBesteller)
                                .build()
                )
                .withPositionen(meldungspositionen)
                .build();
    }

    public ErlmJaxbBuilder withErledigungstermin(LocalDate erledigungstermin) {
        this.erledigungstermin = erledigungstermin;
        return this;
    }

}
