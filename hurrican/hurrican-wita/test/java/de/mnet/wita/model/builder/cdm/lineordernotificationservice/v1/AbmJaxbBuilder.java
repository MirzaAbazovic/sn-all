/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;
import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnsprechpartnerBaseType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.LeitungsabschnittType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTALABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernportierungMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.SchaltungKupferType;

public class AbmJaxbBuilder extends MeldungWithProduktpositionJaxbBuilder<AbmJaxbBuilder> {

    public LocalDate verbindlicherLiefertermin = LocalDate.now();
    public String leitungsSchluesselZahl = "96X";
    public String onkzA = "089";
    public String onkzB = "089";
    public String ordnungsnummer = "1";
    public String maxBruttoBitrate = "2000";
    public String schleifenWiderstand = "3,4";
    public final List<LeitungsabschnittType> leitungsAbschnitte = new ArrayList<>();
    public String v5Id = "3456633";
    public final List<SchaltungKupferType> schaltungKupfer = new ArrayList<>();
    public final List<String> zeitSchlitz = new ArrayList<>();
    public final AnsprechpartnerBaseType ansprechpartnerTelekom = new AnsprechpartnerBaseType();
    private RufnummernportierungMeldungType rufnummernportierung = null;
    private final List<MeldungstypABMType.Meldungspositionen.Position> positionen = new ArrayList<>();

    public MeldungstypABMType build() {
        MeldungstypABMType meldungstypABMType = new MeldungstypABMTypeBuilder()
                .withMeldungsattribute(buildMeldungsAttribute(verbindlicherLiefertermin))
                .withPositionen(positionen)
                .build();
        return meldungstypABMType;
    }

    public AbmJaxbBuilder addLeitungsabschnitt(String laenge, String durchmesser, Integer lfdNr) {
        leitungsAbschnitte.add(new LeitungsabschnittTypeBuilder()
                .withLeitungslaenge(laenge)
                .withLeitungsdurchmesser(durchmesser)
                .withLfdNrLeitungsabschnitt(lfdNr)
                .build()
        );
        return this;
    }

    public AbmJaxbBuilder addSchaltungKupfer(String uevt, String evs, String doppelader) {
        schaltungKupfer.add(
                new SchaltungKupferTypeBuilder()
                        .withUevt(uevt)
                        .withEvs(evs)
                        .withDoppelader(doppelader)
                        .build()
        );
        return this;
    }

    private MeldungstypABMType.Meldungsattribute buildMeldungsAttribute(LocalDate liefertermin) {
        MeldungsattributeTALABMType meldungsAttribute = new MeldungsattributeTALABMTypeBuilder()
                .withLeitung(new AngabenZurLeitungABMTypeBuilder()
                        .withLeitungsbezeichnung(
                                new LeitungsbezeichnungTypeBuilder()
                                        .withLeitungsschluesselzahl(leitungsSchluesselZahl)
                                        .withOnkzA(onkzA)
                                        .withOnkzB(onkzB)
                                        .withOrdnungsnummer(ordnungsnummer)
                                        .build()
                        )
                        .withMaxBruttoBitrate(maxBruttoBitrate)
                        .withSchleifenwiderstand(schleifenWiderstand)
                        .withLeitungsabschnittType(leitungsAbschnitte)
                        .build())
                .withVertragsnummer(vertragsNummer)
                .withRnrPortierung(rufnummernportierung)
                .withSchaltangaben(
                        new SchaltungIsisOpalMeldungTypeBuilder()
                                .withV5ID(v5Id)
                                .withSchaltung(schaltungKupfer).withZeitschlitz(zeitSchlitz)
                                .build()
                )
                .build();
        return new MeldungsattributeABMTypeBuilder()
                    .withTal(meldungsAttribute)
                    .withKundennummer(kundenNummer)
                    .withExterneAuftragsnummer(externeAuftragsnummer)
                    .withVerbindlicherLiefertermin(liefertermin)
                    .withProduktPositionen(produktpositionen)
                    .build();
    }

    public AbmJaxbBuilder addMeldungsPosition(String meldungsCode, String meldungsText) {
        return addMeldungsPosition(meldungsCode, meldungsText, ansprechpartnerTelekom);
    }

    public AbmJaxbBuilder addRufnummernportierung(String onkz, String dialNumber) {
        rufnummernportierung =
                new RufnummernportierungMeldungTypeBuilder()
                        .withPortierungRufnummern(
                                new PortierungRufnummernMeldungTypeBuilder()
                                        .addZuPortierendeOnkzRnr(
                                                new OnkzRufNrTypeBuilder()
                                                        .withOnkz(onkz)
                                                        .withRufnummer(dialNumber)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();
        return this;
    }

    public AbmJaxbBuilder addRufnummernportierung(String onkz, String dialNumber, String directDial, String blockVon,
            String blockBis) {
        rufnummernportierung =
                new RufnummernportierungMeldungTypeBuilder()
                        .withPortierungRufnummernbloecke(
                                new PortierungRufnummernbloeckeMeldungTypeBuilder()
                                        .withOnkzDurchwahlAbfragestelle(
                                                new OnkzDurchwahlAbfragestelleTypeBuilder()
                                                        .withOnkz(onkz)
                                                        .withDurchwahlnummer(dialNumber)
                                                        .withAbfragestelle(directDial)
                                                        .build())
                                        .addZuPortierenderRufnummernblock(
                                                new RufnummernblockTypeBuilder()
                                                        .withRnrBlockVon(blockVon)
                                                        .withRnrBlockBis(blockBis)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();
        return this;
    }

    private AbmJaxbBuilder addMeldungsPosition(String meldungsCode, String meldungsText,
            AnsprechpartnerBaseType ansprechpart) {
        positionen.add(new MeldungstypABMTypeBuilder.PositionBuilder()
                        .withPositionsattribute(
                                new MeldungspositionsattributeABMTypeBuilder()
                                        .withAnsprechpartner(ansprechpart)
                                        .build())
                        .withMeldungstext(meldungsText)
                        .withMeldungscode(meldungsCode)
                        .build()
        );
        return this;
    }

}
