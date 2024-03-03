/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 09:32:56
 */
package de.mnet.wita.unmarshall.v2;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AktionscodeType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.LeitungsabschnittType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.SchaltungKupferType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.UebertragungsverfahrenType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.AbmSchaltangaben;
import de.mnet.wita.message.meldung.position.AbmSchaltung;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.AbmJaxbBuilder;
import de.mnet.wita.unmarshal.v2.AbmUnmarshallerV2;
import de.mnet.wita.unmarshal.v2.AnredeConverterV2;

@SuppressWarnings("Duplicates")
@Test(groups = BaseTest.UNIT)
public class AbmUnmarshallerTest extends BaseTest {

    private static final String EXT_AUFTRAGSNUMMER = "1223425";
    private final AbmUnmarshallerV2 abmReader = new AbmUnmarshallerV2();

    public void extAuftragsnummerShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder().withExterneAuftragsnummer(EXT_AUFTRAGSNUMMER);

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        assertThat(unmarshalled.getExterneAuftragsnummer(), equalTo(EXT_AUFTRAGSNUMMER));
    }

    public void aktionscodeShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder().addProduktposition(AktionscodeType.A,
                ProduktBezeichner.HVT_2H, UebertragungsverfahrenType.H_13);

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        assertThat(unmarshalled.getProduktPositionen().get(0).getAktionsCode(), equalTo(AktionsCode.AENDERUNG));
    }

    public void uebertragungsVerfahrenShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder().addProduktposition(AktionscodeType.A,
                ProduktBezeichner.HVT_2H, UebertragungsverfahrenType.H_20);

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        assertThat(unmarshalled.getProduktPositionen().get(0).getUebertragungsVerfahren(),
                equalTo(Uebertragungsverfahren.H20));
    }

    public void produktBezeichnerShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder().addProduktposition(AktionscodeType.A,
                ProduktBezeichner.HVT_2H, UebertragungsverfahrenType.H_13);

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        assertThat(unmarshalled.getProduktPositionen().get(0).getProduktBezeichner(), equalTo(ProduktBezeichner.HVT_2H));
    }

    public void kundenNummerShouldBeRead() {
        String kundenNummer = "M326586785";
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder().withKundenNummer(kundenNummer);

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        assertThat(unmarshalled.getKundenNummer(), equalTo(kundenNummer));
    }

    public void verbLieferterminShouldBeRead() {
        LocalDate liefertermin = LocalDate.of(2012, 1, 12);
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder();
        abmBuilder.verbindlicherLiefertermin = liefertermin;

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        assertThat(unmarshalled.getVerbindlicherLiefertermin(), equalTo(liefertermin));
    }

    public void vertragsNummerShouldBeRead() {
        String vertragsNummer = "124676789678";
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder().withVertragsNummer(vertragsNummer);

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void leitungsBezeichnungShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder();
        abmBuilder.leitungsSchluesselZahl = "96X";
        abmBuilder.onkzA = "089";
        abmBuilder.onkzB = "090";
        abmBuilder.ordnungsnummer = "5";

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        LeitungsBezeichnung leitungsBezeichnung = unmarshalled.getLeitung().getLeitungsBezeichnung();
        assertThat(leitungsBezeichnung.getLeitungsSchluesselZahl(), equalTo(abmBuilder.leitungsSchluesselZahl));
        assertThat(leitungsBezeichnung.getOnkzKunde(), equalTo(abmBuilder.onkzA));
        assertThat(leitungsBezeichnung.getOnkzKollokation(), equalTo(abmBuilder.onkzB));
        assertThat(leitungsBezeichnung.getOrdnungsNummer(), equalTo(abmBuilder.ordnungsnummer));
    }

    public void leitungsBezeichnungShouldPreserveLeadingZeros() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder();
        abmBuilder.leitungsSchluesselZahl = "0001";
        abmBuilder.onkzA = "0002";
        abmBuilder.onkzB = "0003";
        abmBuilder.ordnungsnummer = "0004";

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        LeitungsBezeichnung leitungsBezeichnung = unmarshalled.getLeitung().getLeitungsBezeichnung();
        assertThat(leitungsBezeichnung.getLeitungsSchluesselZahl(), equalTo(abmBuilder.leitungsSchluesselZahl));
        assertThat(leitungsBezeichnung.getOnkzKunde(), equalTo(abmBuilder.onkzA));
        assertThat(leitungsBezeichnung.getOnkzKollokation(), equalTo(abmBuilder.onkzB));
        assertThat(leitungsBezeichnung.getOrdnungsNummer(), equalTo(abmBuilder.ordnungsnummer));
    }

    public void leitungsShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder();
        abmBuilder.maxBruttoBitrate = "1000";
        abmBuilder.schleifenWiderstand = "23";

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        Leitung leitung = unmarshalled.getLeitung();
        assertThat(leitung.getMaxBruttoBitrate(), equalTo(abmBuilder.maxBruttoBitrate));
        assertThat(leitung.getSchleifenWiderstand(), equalTo(abmBuilder.schleifenWiderstand));
    }

    public void schaltangabenShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder();
        abmBuilder.v5Id = "235635478";

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        AbmSchaltangaben schaltangaben = unmarshalled.getSchaltangaben();
        assertThat(schaltangaben.getV5Id(), equalTo(abmBuilder.v5Id));
    }

    public void schaltungShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder();
        abmBuilder.addSchaltungKupfer("01345", "11", "12").addSchaltungKupfer("01346", "15", "13");

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        List<AbmSchaltung> schaltungen = unmarshalled.getSchaltangaben().getSchaltungen();
        assertThat(schaltungen, hasSize(abmBuilder.schaltungKupfer.size()));
        for (int i = 0; i < schaltungen.size(); i++) {
            AbmSchaltung witaSchaltung = schaltungen.get(i);
            SchaltungKupferType xmlSchaltung = abmBuilder.schaltungKupfer.get(i);
            assertThat(witaSchaltung.getUevt(), equalTo(xmlSchaltung.getUEVT()));
            assertThat(witaSchaltung.getEvs(), equalTo(xmlSchaltung.getEVS()));
            assertThat(witaSchaltung.getDoppelader(), equalTo(xmlSchaltung.getDoppelader()));
        }
    }

    public void zeitschlitzShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder();
        abmBuilder.zeitSchlitz.add("12");
        abmBuilder.zeitSchlitz.add("45");

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        List<String> zeitSchlitz = unmarshalled.getSchaltangaben().getZeitSchlitz();
        assertThat(zeitSchlitz, equalTo(abmBuilder.zeitSchlitz));
    }

    public void leitungsAbschnitteShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder()
                .addLeitungsabschnitt("13", "0.5", 1)
                .addLeitungsabschnitt("19", "0.2", 2)
                .addLeitungsabschnitt("200", "0.9", 3);

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        Leitung leitung = unmarshalled.getLeitung();

        List<LeitungsAbschnitt> leitungsAbschnitte = leitung.getLeitungsAbschnitte();
        assertThat(leitungsAbschnitte, hasSize(abmBuilder.leitungsAbschnitte.size()));
        for (int i = 0; i < leitungsAbschnitte.size(); i++) {
            LeitungsAbschnitt witaLeitungsAbschnitt = leitungsAbschnitte.get(i);
            LeitungsabschnittType xmlLeitungsabschnitte = abmBuilder.leitungsAbschnitte.get(i);
            assertThat(witaLeitungsAbschnitt.getLeitungsDurchmesser(),
                    equalTo(xmlLeitungsabschnitte.getLeitungsdurchmesser()));
            assertThat(witaLeitungsAbschnitt.getLeitungsLaenge(), equalTo(xmlLeitungsabschnitte.getLeitungslaenge()));
            assertThat(witaLeitungsAbschnitt.getLaufendeNummer(),
                    equalTo(xmlLeitungsabschnitte.getLfdNrLeitungsabschnitt()));
        }

    }

    public void meldungsPositionenShouldBeRead() {
        AbmJaxbBuilder abmBuilder = new AbmJaxbBuilder().addMeldungsPosition("Code1", "Text1").addMeldungsPosition("Code2",
                "Text2");
        abmBuilder.ansprechpartnerTelekom.setAnrede("1");
        abmBuilder.ansprechpartnerTelekom.setNachname("Huber");
        abmBuilder.ansprechpartnerTelekom.setTelefonnummer("089-445657");

        AuftragsBestaetigungsMeldung unmarshalled = (AuftragsBestaetigungsMeldung) abmReader.unmarshal(abmBuilder.build());

        Set<MeldungsPositionWithAnsprechpartner> meldungsPositionen = unmarshalled.getMeldungsPositionen();
        assertThat(meldungsPositionen, hasSize(2));

        for (MeldungspositionType xmlMeldungsPosition : abmBuilder.getMeldungspositionen()) {
            boolean found = false;
            for (MeldungsPositionWithAnsprechpartner witaMeldungsPosition : meldungsPositionen) {
                if (witaMeldungsPosition.getMeldungsCode().equals(xmlMeldungsPosition.getMeldungscode())) {
                    found = true;
                    assertThat(witaMeldungsPosition.getMeldungsText(), equalTo(xmlMeldungsPosition.getMeldungstext()));
                    AnsprechpartnerTelekom ansprechpartnerTelekom = witaMeldungsPosition.getAnsprechpartnerTelekom();
                    assertThat(ansprechpartnerTelekom.getAnrede(),
                            equalTo(AnredeConverterV2.toMwf(abmBuilder.ansprechpartnerTelekom.getAnrede())));
                    assertThat(ansprechpartnerTelekom.getNachname(),
                            equalTo(abmBuilder.ansprechpartnerTelekom.getNachname()));
                    assertThat(ansprechpartnerTelekom.getVorname(),
                            equalTo(abmBuilder.ansprechpartnerTelekom.getVorname()));
                    assertThat(ansprechpartnerTelekom.getEmailAdresse(),
                            equalTo(abmBuilder.ansprechpartnerTelekom.getEmailadresse()));
                    assertThat(ansprechpartnerTelekom.getTelefonNummer(),
                            equalTo(abmBuilder.ansprechpartnerTelekom.getTelefonnummer()));
                }
            }
            assertTrue(found, "Meldungsposition mit Code " + xmlMeldungsPosition.getMeldungscode() + " nicht gefunden!");
        }
    }


}
