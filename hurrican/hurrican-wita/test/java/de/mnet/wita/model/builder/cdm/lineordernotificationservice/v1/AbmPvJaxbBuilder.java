/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 15:11:02
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;
import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABMPVType;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.position.Leitung;

public class AbmPvJaxbBuilder extends MeldungWithProduktpositionJaxbBuilder<AbmPvJaxbBuilder> {

    private Leitung leitung;
    private LocalDate uebernahmeDatumVerbindlich;
    private String aufnehmenderProvidername;
    private final List<MeldungstypABMPVType.Meldungspositionen.Position> positionen = new ArrayList<>();

    public MeldungstypABMPVType build() {
        return new MeldungstypABMPVTypeBuilder()
                .withMeldungsattribute(
                        new MeldungsattributeABMPVTypeBuilder()
                                .withTal(
                                        new MeldungsattributeTALABMPVTypeBuilder()
                                                .withLeitung(buildLeitung())
                                                .build()
                                )
                                .withAufnehmderProvider(
                                        new AufnehmenderProviderABMTypeBuilder()
                                                .withUebernahmeDatumVerbindlich(uebernahmeDatumVerbindlich)
                                                .withProvidernameAufnehmend(aufnehmenderProvidername)
                                                .build()
                                )
                                .withVertragsnummer(vertragsNummer)
                                .build()
                )
                .withPositionen(positionen)
                .build();
    }

    private AngabenZurLeitungType buildLeitung() {
        if (leitung == null) {
            return null;
        }

        return new AngabenZurLeitungTypeBuilder()
                .withLeitungsbezeichnung(buildLeitungsbezeichnung(leitung.getLeitungsBezeichnung()))
                .withSchleifenwiderstand(leitung.getSchleifenWiderstand())
                .build();
    }

    @Override
    protected LeitungsbezeichnungType buildLeitungsbezeichnung(LeitungsBezeichnung input) {
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

    public AbmPvJaxbBuilder withLeitung(Leitung leitung) {
        this.leitung = leitung;
        return this;
    }

    public AbmPvJaxbBuilder withUebernahmeDatumVerbindlich(LocalDate uebernahmeDatumVerbindlich) {
        this.uebernahmeDatumVerbindlich = uebernahmeDatumVerbindlich;
        return this;
    }

    public AbmPvJaxbBuilder withAufnehmendenProvidernamen(String aufnehmenderProvider) {
        this.aufnehmenderProvidername = aufnehmenderProvider;
        return this;
    }

}
