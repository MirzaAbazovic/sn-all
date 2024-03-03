/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeENTMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMPVType;

public class EntmPvJaxbBuilder extends MeldungWithProduktpositionJaxbBuilder<EntmPvJaxbBuilder> {

    private LocalDate entgelttermin = LocalDate.of(2011, 6, 1);

    public MeldungstypENTMPVType build() {
        return new MeldungstypENTMPVTypeBuilder()
                .withMeldungsattribute(buildMeldungsAttribute())
                .withPositionen(meldungspositionen)
                .build();
    }

    private MeldungsattributeENTMPVType buildMeldungsAttribute() {
        MeldungsattributeENTMPVType meldungsAttribute = new MeldungsattributeENTMPVTypeBuilder()
                .withEntgelttermin(entgelttermin)
                .withKundennummer(kundenNummer)
                .withVertragsnummer(vertragsNummer)
                .build();
        return meldungsAttribute;
    }

    public EntmPvJaxbBuilder withEntgelttermin(LocalDate entgelttermin) {
        this.entgelttermin = entgelttermin;
        return this;
    }

}
