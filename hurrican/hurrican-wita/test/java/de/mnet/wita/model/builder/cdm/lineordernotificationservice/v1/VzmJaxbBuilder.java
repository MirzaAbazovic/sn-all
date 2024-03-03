/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypVZMType;

public class VzmJaxbBuilder extends MeldungJaxbBuilder<VzmJaxbBuilder> {

    private LocalDate verzoegerungstermin = LocalDate.of(2011, 5, 30);

    public MeldungstypVZMType build() {
        return new MeldungstypVZMTypeBuilder()
                    .withMeldungsattribute(
                            new MeldungsattributeVZMTypeBuilder()
                                    .withExterneAuftragsnummer(externeAuftragsnummer)
                                    .withKundennummer(kundenNummer)
                                    .withVerzoegerungstermin(verzoegerungstermin)
                                    .withVertragsnummer(vertragsNummer)
                                    .build())
                    .withPositionen(meldungspositionen)
                    .build();
    }

    public VzmJaxbBuilder withVerzoegerungstermin(LocalDate verzoegerungstermin) {
        this.verzoegerungstermin = verzoegerungstermin;
        return this;
    }

}
