/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 11:09:29
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABBMPVType;
import de.mnet.wita.message.meldung.position.Leitung;

public class AbbmPvJaxbBuilder extends MeldungWithProduktpositionJaxbBuilder<AbbmPvJaxbBuilder> {

    private Leitung leitung;

    public MeldungstypABBMPVType build() {
        MeldungstypABBMPVType meldung = new MeldungstypABBMPVTypeBuilder()
                .withMeldungsattribute(new MeldungsattributeABBMPVTypeBuilder()
                        .withKundennummer(kundenNummer)
                        .withVertragsnummer(vertragsNummer)
                        .withTal(new MeldungsattributeTALABBMPVTypeBuilder()
                                .withLeitung(buildLeitung(leitung))
                                .build()
                        )
                        .build()
                )
                .build();
        MeldungstypABBMPVType.Meldungspositionen xmlMeldungspositionen = new MeldungstypABBMPVType.Meldungspositionen();
        xmlMeldungspositionen.getPosition().addAll(meldungspositionen);
        meldung.setMeldungspositionen(xmlMeldungspositionen);
        return meldung;
    }

    public AbbmPvJaxbBuilder withLeitung(Leitung leitung) {
        this.leitung = leitung;
        return this;
    }

}
