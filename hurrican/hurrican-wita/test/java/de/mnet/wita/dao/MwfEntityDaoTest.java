package de.mnet.wita.dao;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster.*;
import static de.mnet.wita.message.common.BsiProtokollEintragSent.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.exceptions.AuftragNotFoundException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.TestAnlage;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.BsiProtokollEintragSent;
import de.mnet.wita.message.common.SmsStatus;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.servicetest.persistence.MwfEntityPersistenceTest;

/**
 * more test for {@link MwfEntityDao} can be found in {@link MwfEntityPersistenceTest}
 */
@Test(groups = BaseTest.SLOW)
public class MwfEntityDaoTest extends AbstractServiceTest {

    private static final int MAX_SEND_BLOCK_PER_GESCHAEFTSFALL = 12;

    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private Provider<AuftragDatenBuilder> auftragDatenProvider;
    @Autowired
    private Provider<WitaCBVorgangBuilder> cbVorgangBuilder;

    @DataProvider
    public Object[][] dataProvidergetLastAkmPv() {
        // @formatter:off
        return new Object[][] {
                { array(AnkuendigungsMeldungPv.class), "1", -1 },
                { array(AnkuendigungsMeldungPv.class), "2", -1 },
                { array(akmPv("2")), "1", -1 },
                { array(akmPv("1")), "2", -1 },

                { array(akmPv("1")), "1", 0 },

                { array(akmPv("1"), akmPv("2"), akmPv("3")), "1", 0 },
                { array(akmPv("1"), akmPv("2"), akmPv("3")), "2", 1 },
                { array(akmPv("1"), akmPv("2"), akmPv("3")), "3", 2 },

                { array(akmPv("1"), akmPv("2"), akmPv("2")),             "2", 2 },
                { array(akmPv("1"), akmPv("2"), akmPv("2"), akmPv("3")), "2", 2 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProvidergetLastAkmPv")
    public void testgetLastAkmPv(AnkuendigungsMeldungPv[] akmPvs, String vertragsnr, int expectedAkmPvIndex) {
        for (AnkuendigungsMeldungPv akmPv : akmPvs) {
            mwfEntityDao.store(akmPv);
        }
        flushAndClear();

        assertEquals(mwfEntityDao.getLastAkmPv(vertragsnr), (expectedAkmPvIndex == -1) ? null
                : akmPvs[expectedAkmPvIndex]);
    }

    private AnkuendigungsMeldungPv akmPv(String vertragsnummer) {
        return new AnkuendigungsMeldungPvBuilder().withVertragsnummer(vertragsnummer).build();
    }

    @DataProvider
    public Object[][] dataProviderGetLastTam() {
        // @formatter:off
        return new Object[][] {
                { array(TerminAnforderungsMeldung.class), "1", -1 },
                { array(TerminAnforderungsMeldung.class), "2", -1 },
                { array(tam("2")), "1", -1 },
                { array(tam("1")), "2", -1 },

                { array(tam("1")), "1", 0 },

                { array(tam("1"), tam("2"), tam("3")), "1", 0 },
                { array(tam("1"), tam("2"), tam("3")), "2", 1 },
                { array(tam("1"), tam("2"), tam("3")), "3", 2 },

                { array(tam("1"), tam("2"), tam("2")),           "2", 2 },
                { array(tam("1"), tam("2"), tam("2"), tam("3")), "2", 2 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetLastTam")
    public void testGetLastTam(TerminAnforderungsMeldung[] tams, String extAuftragsnr, int expectedTamIndex) {
        for (TerminAnforderungsMeldung tam : tams) {
            mwfEntityDao.store(tam);
        }
        flushAndClear();

        assertEquals(mwfEntityDao.getLastTam(extAuftragsnr), (expectedTamIndex == -1) ? null : tams[expectedTamIndex]);
    }

    public void testGetAuftragRequestsForAuftragId() {
        AuftragDaten auftragDaten = auftragDatenProvider.get().build();
        WitaCBVorgang cbVorgang = cbVorgangBuilder.get().withAuftragId(auftragDaten.getAuftragId())
                .withCarrierRefNr("STEST3243241543").build();
        Auftrag mwfAuftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)
                .withExterneAuftragsnummer(cbVorgang.getCarrierRefNr()).withCbVorgangId(cbVorgang.getId()).buildValid();
        mwfEntityDao.store(mwfAuftrag);

        List<Auftrag> auftrag = mwfEntityDao.getAuftragRequestsForAuftragId(auftragDaten.getAuftragId());

        assertEquals(Iterables.getOnlyElement(auftrag).getCbVorgangId(), cbVorgang.getId());
    }

    private TerminAnforderungsMeldung tam(String extAuftragsnummer) {
        return new TerminAnforderungsMeldungBuilder().withExterneAuftragsnummer(extAuftragsnummer).build();
    }

    public void findUnsentRequestWithAlreadySentRequest() {
        Auftrag mwfAuftrag1 = createTestAuftrag(LocalDateTime.now(), false);
        assertNull(mwfEntityDao.findUnsentRequest(mwfAuftrag1.getCbVorgangId()));
    }

    public void findUnsentRequestWithCancelledRequest() {
        Auftrag mwfAuftrag1 = createTestAuftrag(null, true);
        assertNull(mwfEntityDao.findUnsentRequest(mwfAuftrag1.getCbVorgangId()));
    }

    public void findUnsentRequestWithNotYetSentRequest() {
        Auftrag mwfAuftrag1 = createTestAuftrag(null, false);
        assertNotNull(mwfEntityDao.findUnsentRequest(mwfAuftrag1.getCbVorgangId()));
    }

    private Auftrag createTestAuftrag(LocalDateTime sentAt, boolean cancelled) {
        AuftragDaten auftragDaten = auftragDatenProvider.get().build();
        WitaCBVorgang cbVorgang = cbVorgangBuilder.get().withAuftragId(auftragDaten.getAuftragId())
                .withCarrierRefNr("STEST3243241543").build();
        Auftrag mwfAuftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).withExterneAuftragsnummer(cbVorgang.getCarrierRefNr())
                .withSentAt(sentAt)
                .withRequestWurdeStorniert(cancelled)
                .withCbVorgangId(cbVorgang.getId())
                .buildValid();
        mwfEntityDao.store(mwfAuftrag);
        return mwfAuftrag;
    }

    public void findUnsentRequestsByGeschaeftsfall() {
        final LocalDateTime pastDate = LocalDateTime.now().minusMinutes(1);
        Auftrag mwfAuftrag1 = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG).withKundenwunschtermin(
                LocalDate.now().minusYears(25), SLOT_9)).withSentAt(null).withRequestWurdeStorniert(false).buildValid();
        Auftrag mwfAuftrag2 = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG).withKundenwunschtermin(
                LocalDate.now().minusYears(25), SLOT_9)).withSentAt(null).withRequestWurdeStorniert(false).withEarliestSendDate(pastDate).buildValid();

        mwfEntityDao.store(mwfAuftrag1);
        mwfEntityDao.store(mwfAuftrag2);
        flushAndClear();

        List<Long> result = mwfEntityDao.findUnsentRequests(BEREITSTELLUNG, MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);
        assertTrue(!result.isEmpty()); // due to remaining not sent BEREITSTELLUNGen in database
        assertThat(result, hasItem(mwfAuftrag1.getId()));
        assertThat(result, hasItem(mwfAuftrag2.getId()));
    }

    public void findUnsentRequestsByGeschaeftsfallWithEarliestSendDateInFuture() {
        final LocalDateTime futureDate = LocalDateTime.now().plusMinutes(1);
        Auftrag mwfAuftrag = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG)
                .withKundenwunschtermin(LocalDate.now(), SLOT_9))
                .withSentAt(null)
                .withRequestWurdeStorniert(false)
                .withEarliestSendDate(futureDate)
                .buildValid();

        mwfEntityDao.store(mwfAuftrag);
        flushAndClear();

        List<Long> result = mwfEntityDao.findUnsentRequests(BEREITSTELLUNG, MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);
        assertThat(result, not(hasItem(mwfAuftrag.getId())));
    }

    public void findUnsentRequestByGeschaeftsfallWithDifferentGeschaeftsfall() {
        Auftrag mwfAuftrag = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG).withKundenwunschtermin(
                LocalDate.now(), SLOT_9)).withSentAt(null).withRequestWurdeStorniert(false).buildValid();
        mwfEntityDao.store(mwfAuftrag);
        flushAndClear();

        List<Long> result = mwfEntityDao.findUnsentRequests(LEISTUNGS_AENDERUNG, MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);
        assertThat(result, not(hasItem(mwfAuftrag.getId())));
    }

    public void findUnsentRequestsByGeschaeftsfallWithCancelledRequest() {
        Auftrag mwfAuftrag = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG).withKundenwunschtermin(
                LocalDate.now(), SLOT_9)).withSentAt(null).withRequestWurdeStorniert(true).buildValid();
        mwfEntityDao.store(mwfAuftrag);
        flushAndClear();

        List<Long> result = mwfEntityDao.findUnsentRequests(BEREITSTELLUNG, MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);
        assertThat(result, not(hasItem(mwfAuftrag.getId())));
    }

    public void findUnsentRequestsByGeschaeftsfallWithAlreadySentRequest() {
        Auftrag mwfAuftrag = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG).withKundenwunschtermin(
                LocalDate.now(), SLOT_9)).withSentAt(LocalDateTime.now()).withRequestWurdeStorniert(false).buildValid();
        mwfEntityDao.store(mwfAuftrag);
        flushAndClear();

        List<Long> result = mwfEntityDao.findUnsentRequests(BEREITSTELLUNG, MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);
        assertThat(result, not(hasItem(mwfAuftrag.getId())));
    }

    public void findGeschaeftsfall4Anlage() {
        AuftragDaten auftragDaten = auftragDatenProvider.get().build();
        WitaCBVorgang cbVorgang = cbVorgangBuilder.get().withAuftragId(auftragDaten.getAuftragId())
                .withCarrierRefNr("STEST3243241543").build();
        Auftrag mwfAuftrag = new AuftragBuilder(
                new GeschaeftsfallBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).addTestAnlage(TestAnlage.SIMPLE,
                        Anlagentyp.LAGEPLAN)
        ).withExterneAuftragsnummer(cbVorgang.getCarrierRefNr())
                .withSentAt(LocalDateTime.now()).withCbVorgangId(cbVorgang.getId()).buildValid();
        mwfEntityDao.store(mwfAuftrag);

        MnetWitaRequest foundAuftrag = mwfEntityDao.findRequest4Anlage(mwfAuftrag.getGeschaeftsfall().getAnlagen()
                .iterator().next());

        assertEquals(foundAuftrag, mwfAuftrag);

    }

    public void findNonExistingMeldung4Anlage() {
        AuftragDaten auftragDaten = auftragDatenProvider.get().build();
        WitaCBVorgang cbVorgang = cbVorgangBuilder.get().withAuftragId(auftragDaten.getAuftragId())
                .withCarrierRefNr("STEST3243241543").build();
        Auftrag mwfAuftrag = new AuftragBuilder(
                new GeschaeftsfallBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).addTestAnlage(TestAnlage.SIMPLE,
                        Anlagentyp.LAGEPLAN)
        ).withExterneAuftragsnummer(cbVorgang.getCarrierRefNr())
                .withSentAt(LocalDateTime.now()).withCbVorgangId(cbVorgang.getId()).buildValid();
        mwfEntityDao.store(mwfAuftrag);

        Meldung<?> meldung = mwfEntityDao.findMeldung4Anlage(mwfAuftrag.getGeschaeftsfall().getAnlagen().iterator()
                .next());

        assertNull(meldung);
    }

    public void findMeldung4Anlage() {
        AnkuendigungsMeldungPv akmPv = createMeldungWithAnlage();
        mwfEntityDao.store(akmPv);

        Meldung<?> meldung = mwfEntityDao.findMeldung4Anlage(akmPv.getAnlagen().iterator().next());

        assertEquals(meldung, akmPv);

    }

    public void dontFindAnlage4Request() {
        AuftragDaten auftragDaten = auftragDatenProvider.get().build();
        WitaCBVorgang cbVorgang = cbVorgangBuilder.get().withAuftragId(auftragDaten.getAuftragId())
                .withCarrierRefNr("STEST3243241543").build();
        Auftrag mwfAuftrag = new AuftragBuilder(
                new GeschaeftsfallBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).addTestAnlage(TestAnlage.SIMPLE,
                        Anlagentyp.LAGEPLAN)
        ).withExterneAuftragsnummer(cbVorgang.getCarrierRefNr())
                .withSentAt(LocalDateTime.now()).withCbVorgangId(cbVorgang.getId()).buildValid();
        mwfEntityDao.store(mwfAuftrag);

        List<Anlage> anlagen = mwfEntityDao.findUnArchivedAnlagen();

        assertThat(anlagen, not(contains(mwfAuftrag.getGeschaeftsfall().getAnlagen().iterator().next())));
    }

    public void findNonExistingGeschaeftsfall4Anlage() {
        AnkuendigungsMeldungPv akmPv = createMeldungWithAnlage();
        mwfEntityDao.store(akmPv);

        MnetWitaRequest request = mwfEntityDao.findRequest4Anlage(akmPv.getAnlagen().iterator().next());

        assertNull(request);

    }

    public void testFindUnarchivedAnlagen() {
        AnkuendigungsMeldungPv akmPv = createMeldungWithAnlage();
        mwfEntityDao.store(akmPv);
        assertThat(akmPv.getAnlagen(), hasSize(1));

        List<Anlage> anlagen = mwfEntityDao.findUnArchivedAnlagen();

        assertTrue(anlagen.containsAll(akmPv.getAnlagen()));
    }

    public void testDontFindArchivedAnlagen() {
        AnkuendigungsMeldungPv akmPv = createMeldungWithAnlage();
        Anlage anlage = akmPv.getAnlagen().iterator().next();
        anlage.setArchivSchluessel("sdhf");
        mwfEntityDao.store(akmPv);
        List<Anlage> anlagen = mwfEntityDao.findUnArchivedAnlagen();
        assertFalse(anlagen.contains(anlage));
    }

    public void testDontFindInfectedUnarchivedAnlagen() {
        AnkuendigungsMeldungPv akmPv = createMeldungWithAnlage();
        Anlage anlage = akmPv.getAnlagen().iterator().next();
        anlage.setArchivingCancelReason("darum");
        mwfEntityDao.store(akmPv);
        List<Anlage> anlagen = mwfEntityDao.findUnArchivedAnlagen();
        assertFalse(anlagen.contains(anlage));
    }

    @DataProvider
    public Object[][] sentToBsiFlagsShouldBeSent() {
        // @formatter:off
        return new Object[][] {
                { NOT_SENT_TO_BSI, true},
                { DONT_SEND_TO_BSI, false},
                { ERROR_SEND_TO_BSI, true},
                { SENT_TO_BSI, false},
                { null, true},};
        // @formatter:on
    }

    @Test(dataProvider = "sentToBsiFlagsShouldBeSent")
    public void findMeldungenToBeSentToBsi(BsiProtokollEintragSent flag, boolean shouldBeSent) {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();
        abm.setSentToBsi(flag);
        mwfEntityDao.store(abm);

        List<Meldung<?>> meldungen = mwfEntityDao.findMeldungenToBeSentToBsi();

        assertEquals(meldungen.contains(abm), shouldBeSent);
    }

    @Test(dataProvider = "sentToBsiFlagsShouldBeSent")
    public void findRequestsToBeSentToBsi(BsiProtokollEintragSent flag, boolean shouldBeSent) {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();
        auftrag.setSentToBsi(flag);
        auftrag.setSentAt(new Date());
        mwfEntityDao.store(auftrag);

        List<MnetWitaRequest> requests = mwfEntityDao.findRequestsToBeSentToBsi();

        assertEquals(requests.contains(auftrag), shouldBeSent);
    }

    @DataProvider
    public Object[][] pvMeldungenNotToBeSendToBsi() {
        // @formatter:off
        return new Object[][] {
                { new AuftragsBestaetigungsMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(), true },
                { new AbbruchMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),               true },
                { new RueckMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                 true },
                { new AnkuendigungsMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),         true },
                { new ErledigtMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),              true },

                { new AuftragsBestaetigungsMeldungBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),   false },
                { new AbbruchMeldungBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                 false },

                { new ErledigtMeldungPvBuilder().withSentToBsi(DONT_SEND_TO_BSI).build(),               false },
                { new ErledigtMeldungBuilder().withSentToBsi(DONT_SEND_TO_BSI).build(),                 false },
                { new ErledigtMeldungPvBuilder().withSentToBsi(NOT_SENT_TO_BSI).build(),                false },
                { new ErledigtMeldungPvBuilder().withSentToBsi(SENT_TO_BSI).build(),                    false },
            };
        // @formatter:on
    }

    @Test(dataProvider = "pvMeldungenNotToBeSendToBsi")
    public <T extends Meldung<?>> void findPvMeldungenNotToBeSendToBsi(T meldung, boolean shouldBeFound) {
        clearErrorMeldungenFromProductionDB();
        mwfEntityDao.store(meldung);
        flushAndClear();

        List<Meldung<?>> meldungen = mwfEntityDao.findPvMeldungenNotToBeSendToBsi();
        if (shouldBeFound) {
            assertEquals(Iterables.getOnlyElement(meldungen), meldung);
        }
        else {
            assertTrue(meldungen.isEmpty());
        }
    }

    private void clearErrorMeldungenFromProductionDB() {
        // da nur die im Builder angelegten Error-Meldungen untersucht werden sollen,
        // werden die Meldungen aus der Produktion ignoriert.
        List<Meldung<?>> meldungen = mwfEntityDao.findPvMeldungenNotToBeSendToBsi();
        for (Meldung<?> meldung : meldungen) {
            meldung.setSentToBsi(DONT_SEND_TO_BSI);
            mwfEntityDao.store(meldung);
            flushAndClear();
        }
    }

    public void notSentRequestsShouldNotBeFoundForBsi() {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();
        auftrag.setSentToBsi(NOT_SENT_TO_BSI);
        auftrag.setSentAt(null);
        mwfEntityDao.store(auftrag);

        List<MnetWitaRequest> requests = mwfEntityDao.findRequestsToBeSentToBsi();

        assertFalse(requests.contains(auftrag));
    }

    private AnkuendigungsMeldungPv createMeldungWithAnlage() {
        return new AnkuendigungsMeldungPvBuilder().addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.LAGEPLAN).build();
    }

    @Test(expectedExceptions = AuftragNotFoundException.class)
    public void testGetAuftragOfCbVorgangZero() {
        mwfEntityDao.getAuftragOfCbVorgang(-2L);
    }

    public void testGetAuftragOfCbVorgangOne() {
        Long cbVorgangId = 123456789L;
        Auftrag auftrag = (new AuftragBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId).buildValid();
        mwfEntityDao.store(auftrag);
        flushAndClear();

        Auftrag result = mwfEntityDao.getAuftragOfCbVorgang(cbVorgangId);
        assertEquals(result, auftrag);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetAuftragOfCbVorgangTwo() {
        Long cbVorgangId = 123456789L;
        Auftrag auftrag1 = (new AuftragBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId).buildValid();
        Auftrag auftrag2 = (new AuftragBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId).buildValid();
        mwfEntityDao.store(auftrag1);
        mwfEntityDao.store(auftrag2);
        flushAndClear();

        mwfEntityDao.getAuftragOfCbVorgang(cbVorgangId);
    }

    @Test(expectedExceptions = AuftragNotFoundException.class)
    public void testGetStornosOfCbVorgangZero() {
        mwfEntityDao.getStornosOfCbVorgang(-2L);
    }

    public void testGetStornosOfCbVorgangOne() {
        Long cbVorgangId = 123456790L;
        Storno storno = (new StornoBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId).buildValid();
        mwfEntityDao.store(storno);
        flushAndClear();

        List<Storno> result = mwfEntityDao.getStornosOfCbVorgang(cbVorgangId);
        assertThat(result, hasSize(1));
        assertThat(result, contains(storno));
    }

    public void testGetStornosOfCbVorgangTwo() {
        Long cbVorgangId = 123456791L;
        Storno storno1 = (new StornoBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId).buildValid();
        Storno storno2 = (new StornoBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId).buildValid();
        mwfEntityDao.store(storno1);
        mwfEntityDao.store(storno2);
        flushAndClear();

        List<Storno> result = mwfEntityDao.getStornosOfCbVorgang(cbVorgangId);
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(storno1, storno2));
    }

    @Test(expectedExceptions = AuftragNotFoundException.class)
    public void getTerminverschiebungenOfCbVorgangZero() {
        mwfEntityDao.getTerminverschiebungenOfCbVorgang(-2L);
    }

    public void getTerminverschiebungenOfCbVorgangOne() {
        Long cbVorgangId = 123456792L;
        TerminVerschiebung tv = (new TerminVerschiebungBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId)
                .buildValid();
        mwfEntityDao.store(tv);
        flushAndClear();

        List<TerminVerschiebung> result = mwfEntityDao.getTerminverschiebungenOfCbVorgang(cbVorgangId);
        assertThat(result, hasSize(1));
        assertThat(result, contains(tv));
    }

    public void getTerminverschiebungenOfCbVorgangTwo() {
        Long cbVorgangId = 123456793L;
        TerminVerschiebung tv1 = (new TerminVerschiebungBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId)
                .buildValid();
        TerminVerschiebung tv2 = (new TerminVerschiebungBuilder(BEREITSTELLUNG)).withCbVorgangId(cbVorgangId)
                .buildValid();
        mwfEntityDao.store(tv1);
        mwfEntityDao.store(tv2);
        flushAndClear();

        List<TerminVerschiebung> result = mwfEntityDao.getTerminverschiebungenOfCbVorgang(cbVorgangId);
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(tv1, tv2));
    }

    public void findMeldungenForSmsVersand() {
        AuftragsBestaetigungsMeldung abmOffen = new AuftragsBestaetigungsMeldungBuilder()
                .withSmsStatus(SmsStatus.OFFEN).build();
        AuftragsBestaetigungsMeldung abmGesendet = new AuftragsBestaetigungsMeldungBuilder()
                .withSmsStatus(SmsStatus.GESENDET).build();

        mwfEntityDao.store(abmOffen);
        mwfEntityDao.store(abmGesendet);
        flushAndClear();

        List<Meldung<?>> result = mwfEntityDao.findMeldungenForSmsVersand();
        assertTrue(!result.isEmpty());
        assertThat(result, hasItem(abmOffen));
    }
}
