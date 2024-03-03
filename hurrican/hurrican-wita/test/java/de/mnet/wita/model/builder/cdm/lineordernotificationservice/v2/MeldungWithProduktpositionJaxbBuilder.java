/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 16:45:43
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AktionscodeType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.ProduktpositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.UebertragungsverfahrenType;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.position.Leitung;

public abstract class MeldungWithProduktpositionJaxbBuilder<BUILDER extends MeldungWithProduktpositionJaxbBuilder<BUILDER>>
        extends MeldungJaxbBuilder<BUILDER> {

    final List<ProduktpositionType> produktpositionen = new ArrayList<>();

    public BUILDER addProduktposition(AktionscodeType aktionscode, ProduktBezeichner bezeichner,
            UebertragungsverfahrenType uetv) {
        ProduktpositionType position =
                new ProduktpositionTypeBuilder()
                        .withAktionscode(aktionscode)
                        .withProdukt(
                                new MeldungProduktTypeBuilder()
                                        .withUebertragungsVerfahren(uetv)
                                        .withBezeichner(bezeichner.getProduktName())
                                        .build()
                        )
                        .build();
        produktpositionen.add(position);

        @SuppressWarnings("unchecked")
        BUILDER result = (BUILDER) this;
        return result;
    }

    public List<ProduktpositionType> getProduktpositionen() {
        return ImmutableList.<ProduktpositionType>builder().addAll(produktpositionen).build();
    }

    AnschlussType buildAnschluss(String anschlussOnkz, String anschlussRufnummer) {
        if (StringUtils.isBlank(anschlussOnkz) || StringUtils.isBlank(anschlussRufnummer)) {
            return null;
        }

        return new AnschlussTypeBuilder()
                .withONKZ(anschlussOnkz)
                .withRufnummer(anschlussRufnummer)
                .build();
    }

    AngabenZurLeitungType buildLeitung(Leitung leitung) {
        if (leitung == null) {
            return null;
        }

        return new AngabenZurLeitungTypeBuilder()
                .withLeitungsbezeichnung(buildLeitungsbezeichnung(leitung.getLeitungsBezeichnung()))
                .withSchleifenwiderstand(leitung.getSchleifenWiderstand())
                .build();
    }

    LeitungsbezeichnungType buildLeitungsbezeichnung(LeitungsBezeichnung input) {
        if (input == null) {
            return null;
        }

        return new LeitungsbezeichnungTypeBuilder()
                .withLeitungsschluesselzahl(input.getLeitungsSchluesselZahl())
                .withOnkzA(input.getOnkzKunde())
                .withOnkzB(input.getOnkzKollokation())
                .withOrdnungsnummer(input.getOrdnungsNummer())
                .build();
    }

}
