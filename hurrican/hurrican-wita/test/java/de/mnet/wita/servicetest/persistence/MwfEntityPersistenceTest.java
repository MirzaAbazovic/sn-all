/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 10:52:24
 */
package de.mnet.wita.servicetest.persistence;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.validation.*;
import javax.validation.groups.*;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.MnetWitaRequestBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.TestAnlage;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungKundeBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.MessageBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.VerzoegerungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.VerzoegerungsMeldungPvBuilder;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.EntgeltMeldungPv;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.message.meldung.position.Positionsattribute;
import de.mnet.wita.message.meldung.position.ProduktPosition;
import de.mnet.wita.validators.groups.Workflow;

@Test(groups = SERVICE)
public class MwfEntityPersistenceTest extends AbstractServiceTest {

    @Autowired
    private MwfEntityDao mwfEntityDao;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DataProvider
    public Object[][] requestProvider() {
        // @formatter:off
        return new Object[][] {
                { GeschaeftsfallTyp.BEREITSTELLUNG },
                { GeschaeftsfallTyp.KUENDIGUNG_KUNDE },
                { GeschaeftsfallTyp.LEISTUNGS_AENDERUNG },
                { GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG },
                { GeschaeftsfallTyp.PORTWECHSEL },
                { GeschaeftsfallTyp.PROVIDERWECHSEL },
                { GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
                { GeschaeftsfallTyp.VERBUNDLEISTUNG }
        };
        // @formatter:on
    }

    @Test(dataProvider = "requestProvider")
    public void testPersistAuftrag(GeschaeftsfallTyp geschaeftsfallTyp) {
        testPersistAuftrag(new AuftragBuilder(geschaeftsfallTyp));
    }

    @Test(dataProvider = "requestProvider")
    public void testPersistAuftragWithBesteller(GeschaeftsfallTyp geschaeftsfallTyp) {
        Kunde besteller = new Kunde();
        besteller.setKundennummer("1234567890");
        besteller.setLeistungsnummer("1234567890");
        testPersistAuftrag(new AuftragBuilder(geschaeftsfallTyp).withBesteller(besteller));

    }

    @Test(dataProvider = "requestProvider")
    public void testPersistAuftragWithAuftragsKenner(GeschaeftsfallTyp geschaeftsfallTyp) {
        testPersistAuftrag(new AuftragBuilder(geschaeftsfallTyp).withAuftragsKenner(1234L, 10));

    }

    @Test(dataProvider = "requestProvider")
    public void testPersistAuftragWithProjekt(GeschaeftsfallTyp geschaeftsfallTyp) {
        testPersistAuftrag(new AuftragBuilder(geschaeftsfallTyp).withProjekt("0815 Projekt"));

    }

    private void testPersistAuftrag(MnetWitaRequestBuilder<Auftrag> auftragBuilder) {
        try {
            MnetWitaRequest auftrag = auftragBuilder.buildValid();
            mwfEntityDao.store(auftrag);

            Auftrag achieved = mwfEntityDao.getAuftragOfCbVorgang(auftrag.getCbVorgangId());
            assertThat(achieved, notNullValue());
        }
        catch (ConstraintViolationException e) {
            fail("Constraint violations:" + e.getConstraintViolations());
        }
    }

    @Test(dataProvider = "requestProvider")
    public void testPersistStorno(GeschaeftsfallTyp geschaeftsfallTyp) {
        StornoBuilder stornoBuilder = new StornoBuilder(geschaeftsfallTyp);
        MnetWitaRequest storno = stornoBuilder.buildValid();
        mwfEntityDao.store(storno);

        Storno achieved = Iterables.getOnlyElement(mwfEntityDao.getStornosOfCbVorgang(storno.getCbVorgangId()));
        assertThat(achieved, notNullValue());
    }

    @Test(dataProvider = "requestProvider")
    public void testPersistTerminverschiebung(GeschaeftsfallTyp geschaeftsfallTyp) {
        TerminVerschiebungBuilder terminVerschiebungBuilder = new TerminVerschiebungBuilder(geschaeftsfallTyp);
        MnetWitaRequest tv = terminVerschiebungBuilder.buildValid();
        mwfEntityDao.store(tv);

        List<TerminVerschiebung> achieved = mwfEntityDao.getTerminverschiebungenOfCbVorgang(tv.getCbVorgangId());
        assertThat(achieved, notNullValue());
        assertThat(achieved, hasSize(1));
    }

    @DataProvider
    public Object[][] meldungProvider() {
        // @formatter:off
        return new Object[][] { {
                new AbbruchMeldungBuilder(), AbbruchMeldung.class},
                { new AbbruchMeldungPvBuilder(), AbbruchMeldungPv.class},
                { new AnkuendigungsMeldungPvBuilder(), AnkuendigungsMeldungPv.class},
                { new AuftragsBestaetigungsMeldungBuilder(), AuftragsBestaetigungsMeldung.class},
                { new AuftragsBestaetigungsMeldungPvBuilder(), AuftragsBestaetigungsMeldungPv.class},
                { new EntgeltMeldungBuilder(), EntgeltMeldung.class},
                { new EntgeltMeldungPvBuilder(), EntgeltMeldungPv.class},
                { new ErledigtMeldungBuilder(), ErledigtMeldung.class},
                { new ErledigtMeldungPvBuilder(), ErledigtMeldungPv.class},
                { new ErledigtMeldungKundeBuilder(), ErledigtMeldungKunde.class},
                { new QualifizierteEingangsBestaetigungBuilder(), QualifizierteEingangsBestaetigung.class},
                { new RueckMeldungPvBuilder(), RueckMeldungPv.class},
                { new TerminAnforderungsMeldungBuilder(), TerminAnforderungsMeldung.class},
                {new VerzoegerungsMeldungPvBuilder(), VerzoegerungsMeldungPv.class},
                { new VerzoegerungsMeldungBuilder(), VerzoegerungsMeldung.class},

        };
        // @formatter:on
    }

    @Test(dataProvider = "meldungProvider")
    public <T extends Meldung<?>> void testPersistMwf(MessageBuilder<T, ?, ?> builder, Class<T> clazz) {
        T meldung = builder.build();
        mwfEntityDao.store(meldung);

        T stored = mwfEntityDao.findById(meldung.getId(), clazz);
        assertThat(stored, notNullValue());
        assertThat(stored.getKundenNummer(), equalTo(meldung.getKundenNummer()));
    }

    @DataProvider
    public Object[][] meldungenMitAnhang() {
        return new Object[][] { { new AnkuendigungsMeldungPvBuilder(), AnkuendigungsMeldungPv.class },
                { new EntgeltMeldungBuilder(), EntgeltMeldung.class } };
    }

    @Test(dataProvider = "meldungenMitAnhang")
    public <T extends Meldung<?>> void testPersistMeldungMitAnlage(MessageBuilder<T, ?, ?> builder, Class<T> clazz) {
        T meldung = builder.addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE).build();
        mwfEntityDao.store(meldung);

        T stored = mwfEntityDao.findById(meldung.getId(), clazz);
        assertThat(stored, notNullValue());
        assertThat(stored.getKundenNummer(), equalTo(meldung.getKundenNummer()));
        assertTrue(CollectionTools.isNotEmpty(stored.getAnlagen()));
    }

    @Test(dataProvider = "meldungenMitAnhang")
    public <T extends Meldung<?>> void testMeldungMitAnlageTooLarge(MessageBuilder<T, ?, ?> builder, @SuppressWarnings("unused") Class<T> clazz) {
        T meldung = builder.addTestAnlage(TestAnlage.TOO_LARGE, Anlagentyp.SONSTIGE).build();
        mwfEntityDao.store(meldung);

        Set<ConstraintViolation<T>> violations = validator.validate(meldung, Default.class, Workflow.class);
        assertTrue(CollectionTools.isNotEmpty(violations));
    }

    public void testPersistMeldungMitAnlage() {
        EntgeltMeldung meldung = new EntgeltMeldungBuilder().addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE).build();
        mwfEntityDao.store(meldung);

        EntgeltMeldung stored = mwfEntityDao.findById(meldung.getId(), EntgeltMeldung.class);
        assertThat(stored, notNullValue());
        assertThat(stored.getKundenNummer(), equalTo(meldung.getKundenNummer()));
    }

    @DataProvider
    public Object[][] dataProviderPersistGeschaeftsfallWithMaxPossibleData() {
        // @formatter:off
        return new Object[][] {
                { (new GeschaeftsfallBuilder(BEREITSTELLUNG))
                        .withAnsprechpartnerNachname("Test for NEU")
                        .withKundenwunschtermin(LocalDate.now(), Zeitfenster.SLOT_3)
                        .withBestandsSuche(new BestandsSuche("89", "1234567", null, null, null))
                        .withVormieter(new Vormieter("Max", "Weber", null, null, null))
                        .addTestAnlage(TestAnlage.LARGE, Anlagentyp.LAGEPLAN).buildValid() },
                { (new GeschaeftsfallBuilder(KUENDIGUNG_KUNDE))
                        .withAnsprechpartnerNachname("Test for KUE-KD")
                        .withKundenwunschtermin(LocalDate.now())
                        .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.KUENDIGUNGSSCHREIBEN).buildValid() },
                { (new GeschaeftsfallBuilder(LEISTUNGS_AENDERUNG))
                        .withAnsprechpartnerNachname("Test for LAE")
                        .withKundenwunschtermin(LocalDate.now(), Zeitfenster.SLOT_2)
                        .withBestandsSuche(new BestandsSuche("89", "1234567", null, null, null))
                        .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE).buildValid() },
                { (new GeschaeftsfallBuilder(LEISTUNGSMERKMAL_AENDERUNG))
                        .withAnsprechpartnerNachname("Test for AEN-LMAE")
                        .withKundenwunschtermin(LocalDate.now())
                        .withBestandsSuche(new BestandsSuche("89", "1234567", null, null, null))
                        .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE).buildValid() },
                { (new GeschaeftsfallBuilder(PROVIDERWECHSEL))
                        .withAnsprechpartnerNachname("Test for PV")
                        .withKundenwunschtermin(LocalDate.now(), Zeitfenster.SLOT_3)
                        .withBestandsSuche(new BestandsSuche("89", "1234567", null, null, null))
                        .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.KUENDIGUNG_ABGEBENDER_PROVIDER).buildValid() },
                { (new GeschaeftsfallBuilder(VERBUNDLEISTUNG))
                        .withAnsprechpartnerNachname("Test for VBL")
                        .withKundenwunschtermin(LocalDate.now(), Zeitfenster.SLOT_4)
                        .withBestandsSuche(new BestandsSuche("89", "1234567", null, null, null))
                        .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE).buildValid() },
                { (new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG))
                        .withAnsprechpartnerNachname("Test for REX-MK")
                        .withKundenwunschtermin(LocalDate.now(), Zeitfenster.SLOT_6)
                        .withBestandsSuche(new BestandsSuche("89", "1234567", null, null, null))
                        .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.PORTIERUNGSANZEIGE).buildValid() },
                { (new GeschaeftsfallBuilder(PORTWECHSEL))
                        .withAnsprechpartnerNachname("Test for SER-POW")
                        .withKundenwunschtermin(LocalDate.now())
                        .withBestandsSuche(new BestandsSuche("89", "1234567", null, null, null))
                        .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE).buildValid() },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderPersistGeschaeftsfallWithMaxPossibleData")
    public void testPersistGeschaeftsfallWithMaxPossibleData(Geschaeftsfall geschaeftsfall) {
        try {
            mwfEntityDao.store(geschaeftsfall);

            Geschaeftsfall result = mwfEntityDao.findById(geschaeftsfall.getId(), geschaeftsfall.getGeschaeftsfallTyp()
                    .getClazz());
            assertThat(result, notNullValue());
        }
        catch (ConstraintViolationException e) {
            fail("Constraint violations:" + e.getConstraintViolations());
        }
    }

    public void testPersistProduktPosition() {
        ProduktPosition produktPosition = new ProduktPosition(AktionsCode.WEGFALL, ProduktBezeichner.HVT_2H);
        produktPosition.setUebertragungsVerfahren(Uebertragungsverfahren.H01);
        mwfEntityDao.store(produktPosition);

        ProduktPosition stored = mwfEntityDao.findById(produktPosition.getId(),
                ProduktPosition.class);
        assertThat(stored, notNullValue());
        assertThat(stored.getAktionsCode(), equalTo(AktionsCode.WEGFALL));
        assertThat(stored.getProduktBezeichner(), equalTo(ProduktBezeichner.HVT_2H));
        assertThat(stored.getUebertragungsVerfahren(), equalTo(Uebertragungsverfahren.H01));
    }

    public void testPersistMeldungsposition() {
        MeldungsPositionWithAnsprechpartner meldungsPosition = new MeldungsPositionWithAnsprechpartner();
        meldungsPosition.setMeldungsCode("1234");
        meldungsPosition.setMeldungsText("Text");

        mwfEntityDao.store(meldungsPosition);

        MeldungsPositionWithAnsprechpartner stored = mwfEntityDao.findById(meldungsPosition.getId(),
                MeldungsPositionWithAnsprechpartner.class);
        assertThat(stored, notNullValue());
    }

    public void testGetLastAkmPv() {
        AnkuendigungsMeldungPv akmPv1 = new AnkuendigungsMeldungPvBuilder().withVertragsnummer("65431").build();
        AnkuendigungsMeldungPv akmPv2 = new AnkuendigungsMeldungPvBuilder().withVertragsnummer("65431").build();
        mwfEntityDao.store(akmPv1);
        mwfEntityDao.store(akmPv2);

        AnkuendigungsMeldungPv lastAkmPv = mwfEntityDao.getLastAkmPv("65431");
        assertThat(lastAkmPv, equalTo(akmPv2));
    }

    public void thatAnschlussPortierungKorrektCouldBeNull() {
        AbbruchMeldungBuilder abbmBuilder =
                new AbbruchMeldungBuilder().withPositionsattribute();
        AbbruchMeldung abbm = abbmBuilder.build();
        mwfEntityDao.store(abbm);
        assertNotNull(abbm.getId());

        Positionsattribute positionsattribute = abbm.getMeldungsPositionen().iterator().next().getPositionsattribute();
        assertNotNull(positionsattribute);
        assertNotNull(positionsattribute.getId());
        assertNull(positionsattribute.getAnschlussPortierungKorrekt());
    }

    public void persistAbbmPositionsattribute() {
        AbbruchMeldungBuilder abbmBuilder =
                new AbbruchMeldungBuilder().withPositionsattribute().withAnschlussPortierungKorrekt();
        AbbruchMeldung abbm = abbmBuilder.build();
        mwfEntityDao.store(abbm);
        assertNotNull(abbm.getId());

        Positionsattribute positionsattribute = abbm.getMeldungsPositionen().iterator().next().getPositionsattribute();
        assertNotNull(positionsattribute);
        assertNotNull(positionsattribute.getId());
        assertNotNull(positionsattribute.getAlternativprodukt());
        assertNotNull(positionsattribute.getFehlauftragsnummer());
        assertNotNull(positionsattribute.getErledigungsterminOffenerAuftrag());

        assertNotNull(positionsattribute.getStandortKundeKorrektur());
        assertNotNull(positionsattribute.getStandortKundeKorrektur().getId());
        assertNotNull(positionsattribute.getStandortKundeKorrektur().getHausnummer());

        assertNotNull(positionsattribute.getAnschlussPortierungKorrekt());
        assertNotNull(positionsattribute.getAnschlussPortierungKorrekt().getOnkzDurchwahlAbfragestelle().getId());
        assertNotNull(positionsattribute.getAnschlussPortierungKorrekt().getRufnummernbloecke().get(0).getId());

        assertNotNull(positionsattribute.getDoppeladerBelegt().get(0).getId());
        assertNotNull(positionsattribute.getDoppeladerBelegt().get(0).getLeitungsbezeichnungString());
    }
}
