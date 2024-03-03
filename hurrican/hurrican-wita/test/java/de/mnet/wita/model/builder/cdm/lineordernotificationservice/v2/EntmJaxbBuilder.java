/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.time.*;
import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlagentypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.DokumenttypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypENTMType;

public class EntmJaxbBuilder extends MeldungWithProduktpositionJaxbBuilder<EntmJaxbBuilder> {

    private final Set<AnlageMitTypType> anlagen = new HashSet<>();
    private LocalDate entgelttermin = LocalDate.of(2011, 6, 1);

    public MeldungstypENTMType build() {
        return new MeldungstypENTMTypeBuilder()
                .withMeldungsattribute(
                        new MeldungsattributeENTMTypeBuilder()
                                .withExterneAuftragsnummer(externeAuftragsnummer)
                                .withKundennummer(kundenNummer)
                                .withEntgelttermin(entgelttermin)
                                .withVertragsnummer(vertragsNummer)
                                .withAnlagen(new ArrayList<>(anlagen))
                                .withProduktPositionen(produktpositionen)
                                .build()
                )
                .withPositionen(meldungspositionen)
                .build();
    }

    public EntmJaxbBuilder withEntgelttermin(LocalDate entgelttermin) {
        this.entgelttermin = entgelttermin;
        return this;
    }

    public EntmJaxbBuilder addAnlage(String dateiname, DokumenttypType dateityp, String beschreibung, String inhalt,
            AnlagentypType anlagentyp) {
        return addAnlage(dateiname, dateityp, beschreibung, inhalt.getBytes(), anlagentyp);
    }

    EntmJaxbBuilder addAnlage(String dateiname, DokumenttypType dateityp, String beschreibung, byte[] inhalt,
            AnlagentypType anlagentyp) {
        AnlageMitTypType anlageMitTyp = new AnlageMitTypTypeBuilder()
                .withAnlagentyp(anlagentyp)
                .withDateiname(dateiname)
                .withDateityp(dateityp)
                .withBeschreibung(beschreibung)
                .withInhalt(inhalt)
                .build();
        anlagen.add(anlageMitTyp);
        return this;
    }

    public EntmJaxbBuilder addAnlage(String dateiname, DokumenttypType dateityp, String inhalt,
            AnlagentypType anlagentyp) {
        return addAnlage(dateiname, dateityp, null, inhalt, anlagentyp);
    }

}
