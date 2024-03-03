package de.mnet.wbci.dao;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wbci.model.AutomationTask.TaskName.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.RequestTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.LockMode;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.KuendigungCheck;
import de.augustakom.hurrican.model.cc.KuendigungCheckBuilder;
import de.augustakom.hurrican.model.cc.KuendigungFrist;
import de.augustakom.hurrican.model.cc.KuendigungFristBuilder;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.Projekt;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceTestBuilder;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;
import de.mnet.wbci.model.builder.AntwortfristBuilder;
import de.mnet.wbci.model.builder.AutomationTaskBuilder;
import de.mnet.wbci.model.builder.AutomationTaskTestBuilder;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.FirmaTestBuilder;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaBuilder;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.ProjektTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.StandortTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TechnischeRessourceTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpTestBuilder;
import de.mnet.wbci.model.builder.WbciTestBuilder;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.VerzoegerungsMeldungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = SLOW)
public class WbciDaoTest<REQ extends WbciRequest, M extends Meldung> extends AbstractServiceTest {

    private static final Random rnd = new Random();
    
    @Autowired
    @Qualifier("de.mnet.wbci.dao.WbciDao")
    private WbciDao wbciDao;
    
    private Comparator creationComparator = new Comparator<WbciEntity>() {
        @Override
        public int compare(WbciEntity o1, WbciEntity o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
    private List<REQ> storedRequests;
    private List<M> storedMeldungen;
    private List<Class<M>> storedMeldungClasses;
    private REQ request4Meldungen;
    @Autowired
    private Provider<WitaCBVorgangBuilder> witaCbVorgangBuilder;

    private static String getRandomVorabstimmungsId() {
        int randNum = rnd.nextInt(999999999);
        return String.format("DEU.MNET.V%09d", randNum);
    }

    @DataProvider(name = "entities")
    public Object[][] createEntities() {
        return new Object[][] {
                { AutomationTask.class, new AutomationTaskTestBuilder(), VA_KUE_MRN },
                { Firma.class, new FirmaTestBuilder(), VA_KUE_MRN },
                { Person.class, new PersonTestBuilder(), VA_KUE_MRN },
                { Projekt.class, new ProjektTestBuilder(), VA_KUE_MRN },
                { Abbruchmeldung.class, new AbbruchmeldungTestBuilder(), VA_KUE_MRN },
                { AbbruchmeldungTechnRessource.class, new AbbruchmeldungTechnRessourceTestBuilder(), VA_KUE_MRN },
                { RueckmeldungVorabstimmung.class, new RueckmeldungVorabstimmungTestBuilder(), VA_KUE_MRN },
                { UebernahmeRessourceMeldung.class, new UebernahmeRessourceMeldungTestBuilder(), VA_KUE_MRN },
                { RufnummernportierungAnlage.class, new RufnummernportierungAnlageTestBuilder(), VA_KUE_MRN },
                { RufnummernportierungEinzeln.class, new RufnummernportierungEinzelnTestBuilder(), VA_KUE_MRN },
                { Standort.class, new StandortTestBuilder(), VA_KUE_MRN },
                { VorabstimmungsAnfrage.class, new VorabstimmungsAnfrageTestBuilder(), VA_KUE_MRN },
                { VorabstimmungsAnfrage.class, new VorabstimmungsAnfrageTestBuilder(), GeschaeftsfallTyp.VA_KUE_ORN },
                { VorabstimmungsAnfrage.class, new VorabstimmungsAnfrageTestBuilder(), GeschaeftsfallTyp.VA_RRNP },
                { TerminverschiebungsAnfrage.class, new TerminverschiebungsAnfrageTestBuilder<>(), VA_KUE_MRN },
                { WbciGeschaeftsfallKueMrn.class, new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN },
                { WbciGeschaeftsfallKueOrn.class, new WbciGeschaeftsfallKueOrnTestBuilder(), GeschaeftsfallTyp.VA_KUE_ORN },
                { WbciGeschaeftsfallRrnp.class, new WbciGeschaeftsfallRrnpTestBuilder(), GeschaeftsfallTyp.VA_RRNP },
                { Erledigtmeldung.class, new ErledigtmeldungTestBuilder(), VA_KUE_MRN },
                { StornoAenderungAbgAnfrage.class, new StornoAenderungAbgAnfrageTestBuilder<>(), VA_KUE_MRN },
                { StornoAenderungAufAnfrage.class, new StornoAenderungAufAnfrageTestBuilder<>(), VA_KUE_MRN },
                { StornoAufhebungAbgAnfrage.class, new StornoAufhebungAbgAnfrageTestBuilder<>(), VA_KUE_MRN },
                { StornoAufhebungAufAnfrage.class, new StornoAufhebungAufAnfrageTestBuilder<>(), VA_KUE_MRN },
        };
    }

    @Test(dataProvider = "entities")
    public void verifyStore(Class<WbciEntity> entity, WbciTestBuilder builder, GeschaeftsfallTyp geschaeftsfallTyp) {
        WbciEntity newEntity = createEntity(builder, geschaeftsfallTyp);
        assertEntityExists(newEntity.getId(), entity);
    }

    @Test(dataProvider = "entities")
    public void verifyFind(Class<WbciEntity> entity, WbciTestBuilder builder, GeschaeftsfallTyp geschaeftsfallTyp) {
        WbciEntity newEntity = createEntity(builder, geschaeftsfallTyp);
        assertEntityExists(newEntity.getId(), entity);

        WbciEntity entityById = wbciDao.findById(newEntity.getId(), entity);
        assertEquals(newEntity, entityById);
    }

    private <E extends WbciEntity, B extends WbciTestBuilder<E>> E createEntity(B builder,
            GeschaeftsfallTyp geschaeftsfallTyp) {
        E entity = builder.buildValid(V1, geschaeftsfallTyp);
        return wbciDao.store(entity);
    }

    protected <T extends WbciEntity> void assertEntityExists(Long id, Class<T> type) {
        T t = wbciDao.findById(id, type);
        assertNotNull(t);
        assertNotNull(t.getId());
    }

    @DataProvider(name = "requestTypes")
    public Object[][] listOfRequestTypes() {
        return new Object[][] {
                { RequestTyp.VA },
                { RequestTyp.TV },
                { RequestTyp.STR_AUFH_ABG },
                { STR_AUFH_AUF },
                { RequestTyp.STR_AEN_ABG },
                { RequestTyp.STR_AEN_AUF }
        };
    }

    @Test(dataProvider = "requestTypes")
    public void getNextSequenceValue(RequestTyp requestTyp) {
        Long nextSequenceValue = wbciDao.getNextSequenceValue(requestTyp);

        assertNotNull(nextSequenceValue);
        assertTrue(nextSequenceValue > 0);
        assertEquals(wbciDao.getNextSequenceValue(requestTyp), (Long) (nextSequenceValue + 1));
    }

    @Test
    public void testFindLastWbciRequestByType() throws Exception {
        storeTestData();
        Collections.sort(storedRequests, creationComparator);
        WbciRequest lastRequest = storedRequests.get(storedRequests.size() - 1);
        assertEquals(wbciDao.findLastWbciRequestByType(lastRequest.getVorabstimmungsId(), WbciRequest.class), lastRequest);
    }

    @Test
    public void testFindLastVA() throws Exception {
        storeTestData();
        Collections.sort(storedRequests, creationComparator);
        WbciRequest lastRequest = storedRequests.get(storedRequests.size() - 1);
        final StornoAufhebungAufAnfrage storno =
                wbciDao.findLastWbciRequestByType(lastRequest.getVorabstimmungsId(), StornoAufhebungAufAnfrage.class);
        assertEquals(storno, lastRequest);
        assertNull(wbciDao.findLastWbciRequestByType(lastRequest.getVorabstimmungsId(), VorabstimmungsAnfrage.class));
    }

    @DataProvider
    public Object[][] hasFaultyAutomationTasksDataProvider() {
        final WbciCdmVersion v1 = V1;
        final GeschaeftsfallTyp vaKueMrn = VA_KUE_MRN;
        WbciGeschaeftsfall gfWithoutFaultyAutomationTasks = new WbciGeschaeftsfallKueMrnTestBuilder()
                .addAutomationTask(new AutomationTaskTestBuilder()
                                .buildValid(v1, vaKueMrn)
                )
                .addAutomationTask(new AutomationTaskTestBuilder()
                                .withName(AutomationTask.TaskName.RUFNUMMER_IN_TAIFUN_ENTFERNEN)
                                .buildValid(v1, vaKueMrn)
                )
                .buildValid(v1, vaKueMrn);
        WbciGeschaeftsfall gfWithFaultyNotAutomatedAutomationTasks = new WbciGeschaeftsfallKueMrnTestBuilder()
                .addAutomationTask(new AutomationTaskTestBuilder()
                                .buildValid(v1, vaKueMrn)
                )
                .addAutomationTask(new AutomationTaskTestBuilder()
                                .withName(AutomationTask.TaskName.RUFNUMMER_IN_TAIFUN_ENTFERNEN)
                                .withStatus(AutomationTask.AutomationStatus.ERROR)
                                .buildValid(v1, vaKueMrn)
                )
                .buildValid(v1, vaKueMrn);
        WbciGeschaeftsfall gfWithFaultyAutomationTasks = new WbciGeschaeftsfallKueMrnTestBuilder()
                .addAutomationTask(new AutomationTaskTestBuilder()
                                .withStatus(AutomationTask.AutomationStatus.ERROR)
                                .buildValid(v1, vaKueMrn)
                )
                .addAutomationTask(new AutomationTaskTestBuilder()
                                .withName(AutomationTask.TaskName.RUFNUMMER_IN_TAIFUN_ENTFERNEN)
                                .withStatus(AutomationTask.AutomationStatus.ERROR)
                                .buildValid(v1, vaKueMrn)
                )
                .buildValid(v1, vaKueMrn);
        WbciGeschaeftsfall gfWithNotCompletedAutomationTasks = new WbciGeschaeftsfallKueMrnTestBuilder()
                .addAutomationTask(new AutomationTaskTestBuilder()
                                .withStatus(AutomationTask.AutomationStatus.FEATURE_IS_NOT_ENABLED)
                                .buildValid(v1, vaKueMrn)
                )
                .addAutomationTask(new AutomationTaskTestBuilder()
                                .withName(AutomationTask.TaskName.RUFNUMMER_IN_TAIFUN_ENTFERNEN)
                                .withStatus(AutomationTask.AutomationStatus.ERROR)
                                .buildValid(v1, vaKueMrn)
                )
                .buildValid(v1, vaKueMrn);
        return new Object[][] {
                // all automation tasks are completed -> false expected
                { gfWithoutFaultyAutomationTasks, false },
                // a not automated automation task is not completed -> false expected since only automated tasks are considered in this case
                { gfWithFaultyNotAutomatedAutomationTasks, false },
                // an automated automation task is not completed due to an error -> true expected
                { gfWithFaultyAutomationTasks, true },
                // an automated automation task is not completed due to an unavailable feature -> true expected
                { gfWithNotCompletedAutomationTasks, true },
        };
    }

    @Test(dataProvider = "hasFaultyAutomationTasksDataProvider")
    public void hasFaultyAutomationTasks(WbciGeschaeftsfall geschaeftsfall, boolean expected) {
        wbciDao.store(geschaeftsfall);
        wbciDao.flushSession();
        assertEquals(wbciDao.hasFaultyAutomationTasks(geschaeftsfall.getVorabstimmungsId()), expected);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindWbciRequestByType() throws Exception {
        // create 3 requests for a GF; VA Request and two Storno Requests
        VorabstimmungsAnfrageTestBuilder vaBuilder = new VorabstimmungsAnfrageTestBuilder();
        VorabstimmungsAnfrage va = createEntity(vaBuilder, VA_KUE_MRN);

        StornoAenderungAbgAnfrageTestBuilder strBuilder1 = new StornoAenderungAbgAnfrageTestBuilder();
        strBuilder1.withWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
        createEntity(strBuilder1, VA_KUE_MRN);

        StornoAenderungAbgAnfrageTestBuilder strBuilder2 = new StornoAenderungAbgAnfrageTestBuilder();
        strBuilder2.withWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
        createEntity(strBuilder2, VA_KUE_MRN);

        wbciDao.flushSession();

        // Check VA Request is found
        List<VorabstimmungsAnfrage> vaRequests = wbciDao.findWbciRequestByType(va.getVorabstimmungsId(), VorabstimmungsAnfrage.class);
        assertEquals(vaRequests.size(), 1);
        assertEquals(vaRequests.get(0).getId(), va.getId());

        // Check Storno Requests are found
        List<StornoAenderungAbgAnfrage> stornoRequests = wbciDao.findWbciRequestByType(va.getVorabstimmungsId(), StornoAenderungAbgAnfrage.class);
        assertEquals(stornoRequests.size(), 2);

        // Check for all Requests are found
        List<WbciRequest> requests = wbciDao.findWbciRequestByType(va.getVorabstimmungsId(), WbciRequest.class);
        assertEquals(requests.size(), 3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindWbciRequestByChangeId() throws Exception {
        VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn> vaBuilder = new VorabstimmungsAnfrageTestBuilder<>();
        VorabstimmungsAnfrage va = createEntity(vaBuilder, VA_KUE_MRN);

        //
        // search for non-existant aenderungsId
        //

        List<WbciRequest> wbciRequests = wbciDao.findWbciRequestByChangeId("", "");
        assertTrue(CollectionUtils.isEmpty(wbciRequests));

        //
        // search for storno by changeId
        //

        StornoAenderungAbgAnfrageTestBuilder strBuilder = new StornoAenderungAbgAnfrageTestBuilder();
        strBuilder.withWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
        StornoAenderungAbgAnfrage storno1 = createEntity(strBuilder, VA_KUE_MRN);
        wbciDao.flushSession();

        wbciRequests = wbciDao.findWbciRequestByChangeId(va.getVorabstimmungsId(), storno1.getAenderungsId());
        assertNotNull(wbciRequests);
        assertEquals(wbciRequests.size(), 1);

        //
        // search for tv by changeId
        //

        TerminverschiebungsAnfrageTestBuilder tvBuilder = new TerminverschiebungsAnfrageTestBuilder();
        tvBuilder.withWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
        TerminverschiebungsAnfrage tv1 = createEntity(tvBuilder, VA_KUE_MRN);

        wbciDao.flushSession();

        wbciRequests = wbciDao.findWbciRequestByChangeId(va.getVorabstimmungsId(), tv1.getAenderungsId());
        assertNotNull(wbciRequests);
        assertEquals(wbciRequests.size(), 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindWbciRequestByChangeIdAndTyp() throws Exception {
        VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn> vaBuilder = new VorabstimmungsAnfrageTestBuilder<>();
        VorabstimmungsAnfrage va = createEntity(vaBuilder, VA_KUE_MRN);

        //
        // search for non-existant aenderungsId
        //

        TerminverschiebungsAnfrage tvNotvalid = wbciDao.findWbciRequestByChangeId("", "", TerminverschiebungsAnfrage.class);
        assertNull(tvNotvalid);

        //
        // search for storno by changeId
        //

        StornoAenderungAbgAnfrageTestBuilder strBuilder = new StornoAenderungAbgAnfrageTestBuilder();
        strBuilder.withWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
        StornoAenderungAbgAnfrage storno1 = createEntity(strBuilder, VA_KUE_MRN);
        wbciDao.flushSession();

        StornoAnfrage stornoAnfrageFound = wbciDao.findWbciRequestByChangeId(va.getVorabstimmungsId(), storno1.getAenderungsId(), StornoAnfrage.class);
        assertNotNull(stornoAnfrageFound);
        assertEquals(stornoAnfrageFound, storno1);

        //
        // search for tv by changeId
        //

        TerminverschiebungsAnfrageTestBuilder tvBuilder = new TerminverschiebungsAnfrageTestBuilder();
        tvBuilder.withWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
        TerminverschiebungsAnfrage tv1 = createEntity(tvBuilder, VA_KUE_MRN);

        wbciDao.flushSession();

        TerminverschiebungsAnfrage tvFound = wbciDao.findWbciRequestByChangeId(va.getVorabstimmungsId(), tv1.getAenderungsId(), TerminverschiebungsAnfrage.class);
        assertNotNull(tvFound);
        assertEquals(tvFound, tv1);
    }

    @Test
    public void testFindWbciGeschaeftsfall() throws Exception {
        String newVaId = "DEU.MNET.V900000001";
        WbciGeschaeftsfall testGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(V1,
                VA_KUE_MRN);
        testGeschaeftsfall.setVorabstimmungsId(newVaId);
        wbciDao.store(testGeschaeftsfall);
        assertEquals(wbciDao.findWbciGeschaeftsfall(newVaId),
                testGeschaeftsfall);
    }

    @Test
    public void testDeleteGeschaeftsfall() throws Exception {
        String newVaId = "DEU.MNET.V900000002";
        WbciGeschaeftsfall testGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(V1,
                VA_KUE_MRN);
        testGeschaeftsfall.setVorabstimmungsId(newVaId);
        wbciDao.store(testGeschaeftsfall);

        WbciGeschaeftsfall geschaeftsfall = wbciDao.findWbciGeschaeftsfall(newVaId);

        assertNotNull(geschaeftsfall);
        assertEquals(geschaeftsfall.getVorabstimmungsId(), newVaId);

        Long projektId = geschaeftsfall.getProjekt().getId();
        Projekt projekt = wbciDao.findById(projektId, Projekt.class);
        assertNotNull(projekt);

        wbciDao.delete(geschaeftsfall);

        assertNull(wbciDao.findWbciGeschaeftsfall(newVaId));
        assertNull(wbciDao.findById(projektId, Projekt.class)); //cascade delete
    }

    @Test
    public void testFindNonBillingRelevantOrderNoOrigs() throws Exception {
        WbciGeschaeftsfall geschaeftsfall = createWbciGeschaeftsfallAufnehmend(WbciGeschaeftsfallStatus.ACTIVE, LocalDate.now()
                .minusDays(1), false, false, true, false, Technologie.FTTC);

        Assert.assertEquals(geschaeftsfall.getNonBillingRelevantOrderNoOrigs().size(), 3L);

        Set<Long> nonBillingRelevantOrderNos = wbciDao.findWbciGeschaeftsfall(geschaeftsfall.getVorabstimmungsId())
                .getNonBillingRelevantOrderNoOrigs();

        assertNotNull(nonBillingRelevantOrderNos);
        assertEquals(nonBillingRelevantOrderNos.size(), 3L);
        assertTrue(nonBillingRelevantOrderNos.containsAll(geschaeftsfall.getNonBillingRelevantOrderNoOrigs()));
    }

    @Test
    public void testFindKuendigungCheckForOeNoOrig() {
        KuendigungCheck check = new KuendigungCheckBuilder()
                .withRandomOeNoOrig()
                .withDurchVertrieb()
                .addKuendiungFrist(new KuendigungFristBuilder()
                        .withFristAuf(KuendigungFrist.FristAuf.ENDE_MVLZ)
                        .withFristInWochen(4L)
                        .withDescription("test")
                        .setPersist(false)
                        .build())
                .setPersist(false).build();

        wbciDao.store(check);

        KuendigungCheck result = wbciDao.findKuendigungCheckForOeNoOrig(check.getOeNoOrig());
        assertNotNull(result);
        assertEquals(result.getOeNoOrig(), check.getOeNoOrig());
        assertEquals(result.getKuendigungFristen(), check.getKuendigungFristen());
    }

    @Test
    public void testByIdWithLockMode() throws Exception {
        WbciGeschaeftsfall testGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(V1,
                VA_KUE_MRN);
        testGeschaeftsfall.setVorabstimmungsId("DEU.MNET.V990000001");
        wbciDao.store(testGeschaeftsfall);

        wbciDao.flushSession();

        assertEquals(wbciDao.byIdWithLockMode(testGeschaeftsfall.getId(), LockMode.PESSIMISTIC_WRITE, WbciGeschaeftsfall.class),
                testGeschaeftsfall);
    }

    @Test
    public void testFindMeldungForVaID() throws Exception {
        storeTestData();
        String vaId = request4Meldungen.getVorabstimmungsId();
        List<M> expectedList = new ArrayList<>();
        for (M meldung : storedMeldungen) {
            if (meldung.getVorabstimmungsId().equals(vaId)) {
                expectedList.add(meldung);
            }
        }
        assertLists(expectedList, wbciDao.findMeldungenForVaId(vaId));
    }

    @Test
    public void testFindMeldungForVaIdAndMeldungTyp() throws Exception {
        storeTestData();
        String vaId = request4Meldungen.getVorabstimmungsId();
        for (Class<M> meldungClass : storedMeldungClasses) {
            List<M> expectedList = new ArrayList<>();
            for (M meldung : storedMeldungen) {
                if (meldung.getClass().equals(meldungClass) && meldung.getVorabstimmungsId().equals(vaId)) {
                    expectedList.add(meldung);
                }
            }
            assertLists(expectedList, wbciDao.findMeldungenForVaIdAndMeldungClass(vaId, meldungClass));
        }
    }

    private void assertLists(List<? extends WbciEntity> expectedList, List<? extends WbciEntity> result) {
        Collections.sort(expectedList, creationComparator);
        Collections.sort(result, creationComparator);
        assertEquals(result, expectedList);
    }

    private void storeTestData() {
        storedRequests = new ArrayList<>();
        storedMeldungen = new ArrayList<>();
        storedMeldungClasses = new ArrayList<>();

        for (Object[] dataRow : Arrays.asList(createEntities())) {
            List<Object> data = Arrays.asList(dataRow);
            if (WbciRequest.class.isAssignableFrom((Class) data.get(0))) {
                REQ temp = (REQ) ((WbciTestBuilder) data.get(1)).buildValid(V1,
                        (GeschaeftsfallTyp) data.get(2));
                wbciDao.store(temp);
                storedRequests.add(temp);
            }
        }
        request4Meldungen = storedRequests.get(0);

        for (Object[] dataRow : Arrays.asList(createEntities())) {
            List<Object> data = Arrays.asList(dataRow);
            if (Meldung.class.isAssignableFrom((Class) data.get(0))) {
                M temp = (M) ((WbciTestBuilder) data.get(1)).buildValid(V1,
                        (GeschaeftsfallTyp) data.get(2));
                temp.setWbciGeschaeftsfall(request4Meldungen.getWbciGeschaeftsfall());
                wbciDao.store(temp);
                storedMeldungen.add(temp);
                if (!storedMeldungClasses.contains(temp.getClass())) {
                    storedMeldungClasses.add((Class<M>) temp.getClass());
                }
            }
        }
    }

    @Test
    public void findVorabstimmungIdsByBillingOrderNoOrig() throws Exception {

        final long validBillingOrderNo = 123L;
        final long unknownBillingOrderNoOrig = -123L;

        // vaWithAbbmTrReceivedAndActiveGf
        createVaWithRequestStatusGfStatusAndBillingOrderNo(validBillingOrderNo, "DEU.MNET.V999999991", WbciRequestStatus.ABBM_TR_EMPFANGEN, ACTIVE);
        // vaWithAkmTrReceivedAndActiveGf
        createVaWithRequestStatusGfStatusAndBillingOrderNo(validBillingOrderNo, "DEU.MNET.V999999992", WbciRequestStatus.AKM_TR_EMPFANGEN, ACTIVE);
        // vaWithAkmTrReceivedAndCompleteGf
        createVaWithRequestStatusGfStatusAndBillingOrderNo(validBillingOrderNo, "DEU.MNET.V999999993", WbciRequestStatus.AKM_TR_EMPFANGEN, WbciGeschaeftsfallStatus.COMPLETE);
        // vaWithRuemVaReceivedAndNewVaGf
        createVaWithRequestStatusGfStatusAndBillingOrderNo(validBillingOrderNo, "DEU.MNET.V999999994", WbciRequestStatus.RUEM_VA_EMPFANGEN, NEW_VA);
        //storno with ERLM
        createStornoWithRequestStatusGfStatusAndBillingOrderNo(validBillingOrderNo, "DEU.MNET.S999999995", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, ACTIVE);
        wbciDao.flushSession();

        // search by taifun order id
        VorabstimmungIdsByBillingOrderNoCriteria criteria = new VorabstimmungIdsByBillingOrderNoCriteria(unknownBillingOrderNoOrig, VorabstimmungsAnfrage.class);
        List<String> vorabstimmungIds = wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
        assertEquals(vorabstimmungIds.size(), 0);

        criteria = new VorabstimmungIdsByBillingOrderNoCriteria(validBillingOrderNo, VorabstimmungsAnfrage.class);
        vorabstimmungIds = wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
        assertEquals(vorabstimmungIds.size(), 4);

        // search by taifun order id & request status
        criteria = new VorabstimmungIdsByBillingOrderNoCriteria(validBillingOrderNo, VorabstimmungsAnfrage.class)
                .addMatchingRequestStatus(WbciRequestStatus.AKM_TR_EMPFANGEN);
        vorabstimmungIds = wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
        assertEquals(vorabstimmungIds.size(), 2);

        // search by taifun order id & gf status
        criteria = new VorabstimmungIdsByBillingOrderNoCriteria(validBillingOrderNo, VorabstimmungsAnfrage.class)
                .addMatchingGeschaeftsfallStatus(NEW_VA);
        vorabstimmungIds = wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
        assertEquals(vorabstimmungIds.size(), 1);

        // search Strono  by taifun order id & gf status
        criteria = new VorabstimmungIdsByBillingOrderNoCriteria(validBillingOrderNo, WbciRequest.class)
                .addMatchingGeschaeftsfallStatus(ACTIVE);
        vorabstimmungIds = wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
        assertEquals(vorabstimmungIds.size(), 3);

        // search Strono  by taifun order id & gf status
        criteria = new VorabstimmungIdsByBillingOrderNoCriteria(validBillingOrderNo, StornoAnfrage.class)
                .addMatchingGeschaeftsfallStatus(ACTIVE);
        vorabstimmungIds = wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
        assertEquals(vorabstimmungIds.size(), 1);
    }

    @Test
    public void testFindPreagreementsWithOverdueAbmPv() throws Exception {

        //
        // create test data
        //

        // Resource uebernahme = true, wita vertrag numbers = 2, wechseltermin <= 14 AT
        // -> 2 should be returned
        String vaId1 = "ABC991";
        String vn1_1 = vaId1 + "_1";
        String vn1_2 = vaId1 + "_2";
        WbciGeschaeftsfallKueMrn gf1 = createWbciGeschaeftsfallKueMrn(1L, vaId1, CarrierCode.DTAG, DateCalculationHelper.getDateInWorkingDaysFromNow(14).toLocalDate());
        wbciDao.store(gf1);
        createVaWithGF(gf1, WbciRequestStatus.AKM_TR_EMPFANGEN);
        createNotificationsForResourceUebernahmeAndWitaVertragsNumbers(gf1, Boolean.TRUE, vn1_1, vn1_2);

        // Resource uebernahme = true, wita vertrag numbers = 2, wechseltermin <= 14 AT
        // -> 2 should be returned (ABM-PV exists for first)
        String vaId2 = "ABC992";
        String vn2_1 = vaId2 + "_1";
        String vn2_2 = vaId2 + "_2";
        String vn2_3 = vaId2 + "_3";
        WbciGeschaeftsfallKueMrn gf2 = createWbciGeschaeftsfallKueMrn(1L, vaId2, CarrierCode.DTAG, DateCalculationHelper.getDateInWorkingDaysFromNow(14).toLocalDate());
        wbciDao.store(gf2);
        createVaWithGF(gf2, WbciRequestStatus.AKM_TR_EMPFANGEN);
        createNotificationsForResourceUebernahmeAndWitaVertragsNumbers(gf2, Boolean.TRUE, vn2_1, vn2_2, vn2_3);
        createAkmPv(vn2_1);
        createAbmPv(vn2_1);
        createVzm(vn2_1);
        createAkmPv(vn2_2);
        createVzm(vn2_2);

        // status ABBM_TR_VERSENDET, Resource uebernahme = true, wita vertrag numbers = 1, wechseltermin < 14 AT
        // -> should NOT be returned (AKM-TR is cancelled with an ABBM-TR)
        String vaId3 = "ABC993";
        String vn3_1 = vaId3 + "_1";
        WbciGeschaeftsfallKueMrn gf3 = createWbciGeschaeftsfallKueMrn(1L, vaId3, CarrierCode.DTAG, DateCalculationHelper.getDateInWorkingDaysFromNow(12).toLocalDate());
        wbciDao.store(gf3);
        createVaWithGF(gf3, WbciRequestStatus.ABBM_TR_VERSENDET);
        createNotificationsForResourceUebernahmeAndWitaVertragsNumbers(gf3, Boolean.TRUE, vn3_1);

        // Resource uebernahme = true, wita vertrag numbers = 1, wechseltermin > 14 AT
        // -> should NOT be returned (wechseltermin outside range)
        String vaId4 = "ABC994";
        String vn4_1 = vaId4 + "_1";
        WbciGeschaeftsfallKueMrn gf4 = createWbciGeschaeftsfallKueMrn(1L, vaId4, CarrierCode.DTAG, DateCalculationHelper.getDateInWorkingDaysFromNow(15).toLocalDate());
        wbciDao.store(gf4);
        createVaWithGF(gf4, WbciRequestStatus.AKM_TR_EMPFANGEN);
        createNotificationsForResourceUebernahmeAndWitaVertragsNumbers(gf4, Boolean.TRUE, vn4_1);

        // Resource uebernahme = false, wita vertrag numbers = 1, wechseltermin <= 14 AT
        // -> should NOT be returned (uebernahme = false)
        String vaId5 = "ABC995";
        String vn5_1 = vaId5 + "_1";
        WbciGeschaeftsfallKueMrn gf5 = createWbciGeschaeftsfallKueMrn(1L, vaId5, CarrierCode.DTAG, DateCalculationHelper.getDateInWorkingDaysFromNow(14).toLocalDate());
        wbciDao.store(gf5);
        createVaWithGF(gf5, WbciRequestStatus.AKM_TR_EMPFANGEN);
        createNotificationsForResourceUebernahmeAndWitaVertragsNumbers(gf5, Boolean.FALSE, vn5_1);

        // gf status NEW_VA_EXPIRED, Resource uebernahme = true, wita vertrag numbers = 1, wechseltermin < 14 AT
        // -> should NOT be returned, gf status NEW_VA_EXPIRED
        String vaId6 = "ABC996";
        String vn6_1 = vaId6 + "_1";
        WbciGeschaeftsfallKueMrn gf6 = createWbciGeschaeftsfallKueMrn(1L, vaId6, CarrierCode.DTAG, DateCalculationHelper.getDateInWorkingDaysFromNow(12).toLocalDate());
        gf6.setStatus(NEW_VA_EXPIRED);
        wbciDao.store(gf6);
        createVaWithGF(gf6, WbciRequestStatus.AKM_TR_EMPFANGEN);
        createNotificationsForResourceUebernahmeAndWitaVertragsNumbers(gf6, Boolean.TRUE, vn6_1);

        wbciDao.flushSession();

        //
        // execute query
        //

        final List<OverdueAbmPvVO> vos = wbciDao.findPreagreementsWithOverdueAbmPv(DateCalculationHelper.getDateInWorkingDaysFromNow(15).toLocalDate());
        assertTrue(!vos.isEmpty());

        assertInList(vos, gf1, vn1_1, false);
        assertInList(vos, gf1, vn1_2, false);
        assertInList(vos, gf2, vn2_2, true);
        assertInList(vos, gf2, vn2_3, false);

        assertNotInList(vos, vn2_1);
        assertNotInList(vos, vn3_1);
        assertNotInList(vos, vn4_1);
        assertNotInList(vos, vn5_1);
        assertNotInList(vos, vn6_1);
    }

    private OverdueAbmPvVO findInList(List<OverdueAbmPvVO> lst, String vertragsnummer) {
        for (OverdueAbmPvVO vo : lst) {
            if (vertragsnummer.equals(vo.getVertragsnummer())) {
                return vo;
            }
        }
        return null;
    }

    private void assertInList(List<OverdueAbmPvVO> lst, WbciGeschaeftsfall gf, String vertragsnummer, boolean akmPvReceived) {
        OverdueAbmPvVO vo = findInList(lst, vertragsnummer);
        Assert.assertNotNull(vo, String.format("No record found matching Vertragsnummer : '%s'.", vertragsnummer));
        Assert.assertEquals(vo.getVaid(), gf.getVorabstimmungsId());
        Assert.assertEquals(vo.getEkpAuf(), gf.getAufnehmenderEKP());
        Assert.assertEquals(vo.getWechseltermin().getTime(), Date.from(gf.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
        Assert.assertEquals(vo.getAuftragNoOrig(), gf.getBillingOrderNoOrig());
        Assert.assertEquals(vo.getAuftragId(), gf.getAuftragId());
        Assert.assertEquals(vo.isAkmPvReceived(), akmPvReceived);
    }

    private void assertNotInList(List<OverdueAbmPvVO> lst, String vertragsnummer) {
        OverdueAbmPvVO vo = findInList(lst, vertragsnummer);
        Assert.assertNull(vo, String.format("Was not expecting record with Vertragsnummer : '%s'.", vertragsnummer));
    }

    @Test
    public void testFindMostRecentPreagreements() throws Exception {

        final int originNumberOfPreagreementsReceiving = wbciDao.findMostRecentPreagreements(CarrierRole.AUFNEHMEND).size();
        final int originNumberOfPreagreementsDonating = wbciDao.findMostRecentPreagreements(CarrierRole.ABGEBEND).size();
        //
        // create test data
        //

        // GF 1 with VA and Storno, IOType OUT, M-Net is receiving Carrier
        WbciGeschaeftsfallKueMrn gf1 = createWbciGeschaeftsfallKueMrn(1L, "ABC123", CarrierCode.MNET);
        gf1.addAutomationTask(new AutomationTaskTestBuilder()
                        .withStatus(AutomationTask.AutomationStatus.ERROR)
                        .buildValid(V1, VA_KUE_MRN)
        );
        wbciDao.store(gf1);
        VorabstimmungsAnfrage va1 = createVaWithGF(gf1, MeldungTyp.ABBM_TR, null, false, MeldungsCode.ZWA);
        StornoAenderungAufAnfrage storno1 = createStornoWithGF(gf1, MeldungTyp.ABBM, null, false, MeldungsCode.ZWA);

        // GF 2 with VA and STORNO, IOType IN, M-Net is donating Carrier
        WbciGeschaeftsfallKueMrn gf2 = createWbciGeschaeftsfallKueMrn(2L, "ABC234", CarrierCode.DTAG);
        wbciDao.store(gf2);
        VorabstimmungsAnfrage va2 = createVaWithGF(gf2, MeldungTyp.ABBM_TR, null, false, MeldungsCode.ZWA);
        StornoAenderungAufAnfrage storno2 = createStornoWithGF(gf2, MeldungTyp.ABBM, null, false, MeldungsCode.ZWA);

        // GF 3 with VA with GF Status COMPLETE, IOType IN, M-Net is donating Carrier
        WbciGeschaeftsfallKueMrn gf3 = createWbciGeschaeftsfallKueMrn(3L, "ABC345", CarrierCode.DTAG);
        gf3.setStatus(WbciGeschaeftsfallStatus.COMPLETE);
        wbciDao.store(gf3);
        createVaWithGF(gf3, MeldungTyp.ABBM_TR, null, false, MeldungsCode.ZWA);

        // GF 4 with VA with GF Status PASSIVE, IOType IN, M-Net is donating Carrier
        WbciGeschaeftsfallKueMrn gf4 = createWbciGeschaeftsfallKueMrn(3L, "ABC346", CarrierCode.DTAG);
        gf4.setStatus(WbciGeschaeftsfallStatus.PASSIVE);
        wbciDao.store(gf4);
        final VorabstimmungsAnfrage va4 = createVaWithGF(gf4, MeldungTyp.ABBM_TR, null, false, MeldungsCode.ZWA);

        wbciDao.flushSession();

        //
        // execute query
        //

        final List<PreAgreementVO> preAgreementVOsReceiving = wbciDao.findMostRecentPreagreements(CarrierRole.AUFNEHMEND);
        assertTrue(!preAgreementVOsReceiving.isEmpty());
        assertEquals(preAgreementVOsReceiving.size(), originNumberOfPreagreementsReceiving + 1);
        final PreAgreementVO preAgreementVO = preAgreementVOsReceiving.get(0);
        assertEquals(preAgreementVO.getWbciRequestId(), storno1.getId());
        assertTrue(preAgreementVO.isAutomationErrors());

        final List<PreAgreementVO> preAgreementVOsReceivingForPreAgreementId =
                wbciDao.findMostRecentPreagreements(CarrierRole.AUFNEHMEND, gf1.getVorabstimmungsId());
        assertTrue(preAgreementVOsReceivingForPreAgreementId.size() == 1);
        assertEquals(preAgreementVOsReceivingForPreAgreementId.get(0).getVaid(), gf1.getVorabstimmungsId());

        final List<PreAgreementVO> preAgreementVOsDonating = wbciDao.findMostRecentPreagreements(CarrierRole.ABGEBEND);
        assertTrue(!preAgreementVOsDonating.isEmpty());
        assertEquals(preAgreementVOsDonating.size(), originNumberOfPreagreementsDonating + 2);
        PreAgreementVO paVO = preAgreementVOsDonating.get(0);
        assertEquals(paVO.getWbciRequestId(), va4.getId());
        paVO = preAgreementVOsDonating.get(1);
        assertEquals(paVO.getWbciRequestId(), storno2.getId());
        assertEquals(paVO.getAenderungskz(), RequestTyp.STR_AEN_AUF.getShortName());
        assertEquals(paVO.getVaid(), storno2.getVorabstimmungsId());
        assertEquals(paVO.getAuftragId(), storno2.getWbciGeschaeftsfall().getAuftragId());
        assertEquals(paVO.getAuftragNoOrig(), storno2.getWbciGeschaeftsfall().getBillingOrderNoOrig());
        assertEquals(paVO.getGfTypeShortName(), storno2.getWbciGeschaeftsfall().getTyp().getShortName());
        assertEquals(paVO.getEkpAbgITU(), storno2.getWbciGeschaeftsfall().getAbgebenderEKP().getITUCarrierCode());
        assertEquals(paVO.getEkpAufITU(), storno2.getWbciGeschaeftsfall().getAufnehmenderEKP().getITUCarrierCode());
        assertDatesAreEqual(paVO.getVorgabeDatum(), storno2.getWbciGeschaeftsfall().getKundenwunschtermin().atStartOfDay());
        assertDatesAreEqual(paVO.getWechseltermin(), storno2.getWbciGeschaeftsfall().getWechseltermin().atStartOfDay());
        assertEquals(paVO.getGeschaeftsfallStatus(), storno2.getWbciGeschaeftsfall().getStatus());
        assertEquals(paVO.getRequestStatus(), storno2.getRequestStatus());
        assertDatesAreEqual(paVO.getProcessedAt(), DateConverterUtils.asLocalDateTime(storno2.getProcessedAt()));
        assertEquals(paVO.getRueckmeldung(), storno2.getLastMeldungType().toString());
        assertDatesAreEqual(paVO.getRueckmeldeDatum(), DateConverterUtils.asLocalDateTime(storno2.getLastMeldungDate()));
        assertEquals(paVO.getMeldungCodes(), storno2.getLastMeldungCodes());
        assertEquals(paVO.getMnetTechnologie(), storno2.getWbciGeschaeftsfall().getMnetTechnologie().getWbciTechnologieCode());
        assertEquals(paVO.getKlaerfall(), storno2.getWbciGeschaeftsfall().getKlaerfall());
        assertNull(paVO.getDaysUntilDeadlineMnet());
        assertNotNull(paVO.getDaysUntilDeadlinePartner());
        assertEquals(paVO.getUserId(), storno2.getWbciGeschaeftsfall().getUserId());
        assertEquals(paVO.getUserName(), storno2.getWbciGeschaeftsfall().getUserName());
        assertEquals(paVO.getTeamId(), storno2.getWbciGeschaeftsfall().getTeamId());
        assertNull(paVO.getTeamName());
        assertNull(paVO.getDaysUntilDeadlineMnet());
        assertNotNull(paVO.getInternalStatus());

        assertFalse(isPreagreementInVOList(preAgreementVOsDonating, gf3.getVorabstimmungsId()));
        assertTrue(isPreagreementInVOList(preAgreementVOsDonating, gf4.getVorabstimmungsId()));
    }

    private boolean isPreagreementInVOList(List<PreAgreementVO> preAgreementVOs, String vaId) {
        for (PreAgreementVO preAgreementVO : preAgreementVOs) {
            if (preAgreementVO.getVaid().equals(vaId)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPreagreementInWbciGeschaeftsfallList(List<WbciGeschaeftsfall> list, String vaId) {
        for (WbciGeschaeftsfall gf : list) {
            if (gf.getVorabstimmungsId().equals(vaId)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void findScheduledWbciRequestIds() throws Exception {
        final List<Long> existingScheduledWbciRequestIds = wbciDao.findScheduledWbciRequestIds();
        // GF1 and VA1 where sendAfter is not set -> should not be sent
        WbciGeschaeftsfallKueMrn gf1 = createWbciGeschaeftsfallKueMrn(1L, "DEU.MNET.VBBB000001", CarrierCode.MNET);
        createVaWithGF(gf1, MeldungTyp.ABBM_TR, null, true, MeldungsCode.ZWA);

        // VA2 und STR2 where sendAfter is set to a date in the past and no processedAt -> should be sent
        final String vorabstimmungsId = "DEU.MNET.VBBB000002";
        WbciGeschaeftsfallKueMrn gf2 = createWbciGeschaeftsfallKueMrn(2L, vorabstimmungsId, CarrierCode.MNET);
        VorabstimmungsAnfrage va2 = createVaWithGF(gf2, MeldungTyp.ABBM_TR, LocalDateTime.now().minusMinutes(4), true, MeldungsCode.ZWA);
        StornoAenderungAufAnfrage str2 = createStornoWithGF(gf2, MeldungTyp.ABBM_TR, LocalDateTime.now().minusMinutes(2), true, MeldungsCode.ZWA);

        // GF3 and VA3 where sendAfter is set to a date in the future -> VA should not be sent
        WbciGeschaeftsfallKueMrn gf3 = createWbciGeschaeftsfallKueMrn(3L, "DEU.MNET.VBBB000003", CarrierCode.MNET);
        createVaWithGF(gf3, MeldungTyp.ABBM_TR, LocalDateTime.now().plusMinutes(5), true, MeldungsCode.ZWA);

        // GF4 where sendAfter is set to a date in the past and processedAt is set -> VA should not be sent
        WbciGeschaeftsfallKueMrn gf4 = createWbciGeschaeftsfallKueMrn(4L, "DEU.MNET.VBBB000004", CarrierCode.MNET);
        createVaWithGF(gf4, MeldungTyp.ABBM_TR, LocalDateTime.now().minusMinutes(5), false, MeldungsCode.ZWA);

        wbciDao.flushSession();

        final List<Long> scheduledWbciRequestIds = wbciDao.findScheduledWbciRequestIds();
        scheduledWbciRequestIds.removeAll(existingScheduledWbciRequestIds);
        assertEquals(scheduledWbciRequestIds.size(), 2);
        assertEquals(scheduledWbciRequestIds.get(0), va2.getId());
        assertEquals(scheduledWbciRequestIds.get(1), str2.getId());
    }

    @Test
    public void findActiveGfByOrderNoOrig() {
        final Long billingOrderNo1 = 111L;
        final Long billingOrderNo2 = 222L;
        final Long billingOrderNo3 = 333L;
        WbciGeschaeftsfallKueMrn gf1 = createWbciGeschaeftsfallKueMrn(billingOrderNo1, "DEU.MNET.V000000001", CarrierCode.MNET);
        gf1.setStatus(ACTIVE);
        wbciDao.store(gf1);
        WbciGeschaeftsfallKueMrn gf2 = createWbciGeschaeftsfallKueMrn(billingOrderNo1, "DEU.MNET.V000000002", CarrierCode.MNET);
        gf2.setStatus(ACTIVE);
        wbciDao.store(gf2);
        WbciGeschaeftsfallKueMrn gf3 = createWbciGeschaeftsfallKueMrn(billingOrderNo1, "DEU.MNET.V000000003", CarrierCode.MNET);
        gf3.setStatus(WbciGeschaeftsfallStatus.COMPLETE);
        wbciDao.store(gf3);
        WbciGeschaeftsfallKueMrn gf4 = createWbciGeschaeftsfallKueMrn(billingOrderNo2, "DEU.MNET.V000000004", CarrierCode.MNET);
        gf4.setStatus(ACTIVE);
        wbciDao.store(gf4);
        WbciGeschaeftsfallKueMrn gf5 = createWbciGeschaeftsfallKueMrn(billingOrderNo3, "DEU.MNET.V000000005", CarrierCode.MNET);
        gf5.setStatus(NEW_VA);
        wbciDao.store(gf5);
        WbciGeschaeftsfallKueMrn gf6 = createWbciGeschaeftsfallKueMrn(billingOrderNo3, "DEU.MNET.V000000006", CarrierCode.MNET);
        gf6.setStatus(NEW_VA_EXPIRED);
        wbciDao.store(gf6);

        List<WbciGeschaeftsfall> activeGFs;

        activeGFs = wbciDao.findActiveGfByOrderNoOrig(billingOrderNo1, true);
        assertEquals(activeGFs.size(), 2);

        activeGFs = wbciDao.findActiveGfByOrderNoOrig(billingOrderNo2, true);
        assertEquals(activeGFs.size(), 1);

        activeGFs = wbciDao.findActiveGfByOrderNoOrig(billingOrderNo3, true);
        assertEquals(activeGFs.size(), 2);

        activeGFs = wbciDao.findActiveGfByOrderNoOrig(billingOrderNo3, false);
        assertEquals(activeGFs.size(), 0);
    }

    public void findGfByOrderNoOrig() {
        final Long billingOrderNo1 = 4L;
        final Long billingOrderNo2 = 5L;
        WbciGeschaeftsfallKueMrn gf1 = createWbciGeschaeftsfallKueMrn(billingOrderNo1, "DEU.MNET.V00000000X",
                CarrierCode.MNET);
        gf1.setStatus(WbciGeschaeftsfallStatus.ACTIVE);
        gf1.setNonBillingRelevantOrderNoOrigs(Sets.newHashSet(billingOrderNo2));
        wbciDao.store(gf1);

        List<WbciGeschaeftsfall> gfs = wbciDao.findGfByOrderNoOrig(billingOrderNo1);
        assertNotEmpty(gfs);
        assertEquals(gfs.size(), 1);

        List<WbciGeschaeftsfall> gfsByNonBillingRelevant = wbciDao.findGfByOrderNoOrig(billingOrderNo2);
        assertNotEmpty(gfsByNonBillingRelevant);
        assertEquals(gfsByNonBillingRelevant.size(), 1);
        assertEquals(gfsByNonBillingRelevant.get(0).getId(), gf1.getId());
        assertEquals(gfsByNonBillingRelevant.get(0).getNonBillingRelevantOrderNoOrigs().iterator().next(),
                billingOrderNo2);
    }

    private void assertDatesAreEqual(Date date, LocalDateTime dateTime) {
        if (dateTime == null) {
            assertNull(date);
        }
        else {
            assertEquals(date.getTime(), dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    @SuppressWarnings("unchecked")
    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> createVaWithRequestStatusGfStatusAndBillingOrderNo(long billingOrder, String vorabstimmungsId, WbciRequestStatus requestStatus, WbciGeschaeftsfallStatus gfStatus) {
        WbciGeschaeftsfallKueMrn gf = createWbciGeschaeftsfallKueMrn(billingOrder, vorabstimmungsId, CarrierCode.DTAG);
        gf.setStatus(gfStatus);
        gf = wbciDao.store(gf);

        final VorabstimmungsAnfrageTestBuilder vaBuilder1 = new VorabstimmungsAnfrageTestBuilder();
        vaBuilder1.withRequestStatus(requestStatus);
        vaBuilder1.withWbciGeschaeftsfall(gf);
        final VorabstimmungsAnfrage vaWithAbbmTr = vaBuilder1.buildValid(V1, VA_KUE_MRN);
        return wbciDao.store(vaWithAbbmTr);
    }

    @SuppressWarnings("unchecked")
    private StornoAnfrage<WbciGeschaeftsfallKueMrn> createStornoWithRequestStatusGfStatusAndBillingOrderNo(long billingOrder, String vorabstimmungsId, WbciRequestStatus requestStatus, WbciGeschaeftsfallStatus gfStatus) {
        WbciGeschaeftsfallKueMrn gf = createWbciGeschaeftsfallKueMrn(billingOrder, vorabstimmungsId, CarrierCode.DTAG);
        gf.setStatus(gfStatus);
        gf = wbciDao.store(gf);

        final StornoAufhebungAbgAnfrageTestBuilder builder = new StornoAufhebungAbgAnfrageTestBuilder();
        builder.withRequestStatus(requestStatus);
        builder.withWbciGeschaeftsfall(gf);
        final StornoAnfrage strono = builder.buildValid(V1, VA_KUE_MRN);
        return wbciDao.store(strono);
    }

    private WbciGeschaeftsfallKueMrn createWbciGeschaeftsfallKueMrn(long billingOrderNoOrig, String vorabstimmungsId, CarrierCode aufnehmenderEKP) {
        return createWbciGeschaeftsfallKueMrn(billingOrderNoOrig, vorabstimmungsId, aufnehmenderEKP, DateCalculationHelper.getDateInWorkingDaysFromNow(14).toLocalDate());
    }

    private WbciGeschaeftsfallKueMrn createWbciGeschaeftsfallKueMrn(long billingOrderNoOrig, String vorabstimmungsId, CarrierCode aufnehmenderEKP, LocalDate wechseltermin) {
        return new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBillingOrderNoOrig(billingOrderNoOrig)
                .withVorabstimmungsId(vorabstimmungsId)
                .withWechseltermin(wechseltermin)
                .withAufnehmenderEKP(aufnehmenderEKP)
                .withBearbeiter(123L, "testAccount", 2L)
                .buildValid(V1, VA_KUE_MRN);
    }

    private UebernahmeRessourceMeldung createAkmTr(WbciGeschaeftsfall gf, LocalDateTime sendAfter) {
        final UebernahmeRessourceMeldungTestBuilder builder = new UebernahmeRessourceMeldungTestBuilder();
        builder.withWbciGeschaeftsfall(gf);
        builder.withSendAfter(sendAfter);
        UebernahmeRessourceMeldung meldung = builder.buildValid(V1, gf.getTyp());
        wbciDao.store(meldung);
        wbciDao.flushSession();
        return meldung;
    }

    private ErledigtmeldungTerminverschiebung createErlmTv(WbciGeschaeftsfall gf) {
        final ErledigtmeldungTestBuilder builder = new ErledigtmeldungTestBuilder();
        builder.withWbciGeschaeftsfall(gf);
        
        ErledigtmeldungTerminverschiebung erlmTv = builder.buildValidForTv(V1, gf.getTyp());
        wbciDao.store(erlmTv);
        wbciDao.flushSession();
        return erlmTv;
    }

    private Erledigtmeldung createErlmStr(WbciGeschaeftsfall gf, RequestTyp requestTyp, boolean createWitaVorgang) {
        final ErledigtmeldungTestBuilder builder = new ErledigtmeldungTestBuilder();
        builder.withWbciGeschaeftsfall(gf);

        Erledigtmeldung erlmStrAuf = builder.buildValidForStorno(V1, gf.getTyp(), requestTyp);
        wbciDao.store(erlmStrAuf);
        wbciDao.flushSession();
        
        if (createWitaVorgang) {
            createValidWitaCBVorgang(gf.getVorabstimmungsId());
        }
        
        return erlmStrAuf;
    }

    @SuppressWarnings("unchecked")
    private VorabstimmungsAnfrage createVaWithGF(WbciGeschaeftsfall gf, MeldungTyp meldungTyp,
            LocalDateTime sendAfter, boolean withoutProcessedAt, MeldungsCode... meldungsCodes) {
        final VorabstimmungsAnfrageTestBuilder vaBuilder = new VorabstimmungsAnfrageTestBuilder();
        vaBuilder.withLastMeldung(meldungTyp, LocalDateTime.now(), meldungsCodes);
        vaBuilder.withWbciGeschaeftsfall(gf);
        if (withoutProcessedAt) {
            vaBuilder.withoutProcessedAt();
        }
        /** set update date to keep sorting of the {@link WbciDao#findMostRecentPreagreements(CarrierRole)} query**/
        vaBuilder.withUpdatedAt(LocalDateTime.now());
        vaBuilder.withSendAfter(sendAfter);
        vaBuilder.withAnswerDeadline(LocalDate.now().plusDays(8));
        vaBuilder.withIsMnetDeadline(false);
        final VorabstimmungsAnfrage va = vaBuilder.buildValid(V1, gf.getTyp());
        return wbciDao.store(va);
    }

    private VorabstimmungsAnfrage createVaWithGF(WbciGeschaeftsfall gf, WbciRequestStatus wbciRequestStatus) {
        final VorabstimmungsAnfrageTestBuilder vaBuilder = new VorabstimmungsAnfrageTestBuilder();
        vaBuilder.withRequestStatus(wbciRequestStatus);
        vaBuilder.withWbciGeschaeftsfall(gf);
        /** set update date to keep sorting of the {@link WbciDao#findMostRecentPreagreements(CarrierRole)} query**/
        vaBuilder.withUpdatedAt(LocalDateTime.now());
        final VorabstimmungsAnfrage va = vaBuilder.buildValid(WbciCdmVersion.V1, gf.getTyp());
        return wbciDao.store(va);
    }

    @SuppressWarnings("unchecked")
    private void createNotificationsForResourceUebernahmeAndWitaVertragsNumbers(WbciGeschaeftsfall gf, Boolean withUebernahme, String... vertragsNumbers) {
        RueckmeldungVorabstimmungTestBuilder ruemVaBuilder = new RueckmeldungVorabstimmungTestBuilder();
        for (String vertragsNumber : vertragsNumbers) {
            ruemVaBuilder.addTechnischeRessource(
                    new TechnischeRessourceTestBuilder()
                            .withVertragsnummer(vertragsNumber)
                            .buildValid(V1, gf.getTyp())
            );
        }
        ruemVaBuilder.withWbciGeschaeftsfall(gf);
        wbciDao.store(ruemVaBuilder.buildValid(V1, gf.getTyp()));

        UebernahmeRessourceMeldungTestBuilder akmTrBuilder = new UebernahmeRessourceMeldungTestBuilder();
        akmTrBuilder.withUebernahme(withUebernahme);
        akmTrBuilder.withWbciGeschaeftsfall(gf);
        wbciDao.store(akmTrBuilder.buildValid(V1, gf.getTyp()));
    }

    @SuppressWarnings("unchecked")
    private void createAbmPv(String... vertragsNumbers) {
        for (String vertragsNumber : vertragsNumbers) {
            AuftragsBestaetigungsMeldungPvBuilder abmPvBuilder = new AuftragsBestaetigungsMeldungPvBuilder();
            abmPvBuilder.withVertragsnummer(vertragsNumber);
            wbciDao.store(abmPvBuilder.build());
        }
    }

    @SuppressWarnings("unchecked")
         private void createAkmPv(String... vertragsNumbers) {
        for (String vertragsNumber : vertragsNumbers) {
            AnkuendigungsMeldungPvBuilder akmPvBuilder = new AnkuendigungsMeldungPvBuilder();
            akmPvBuilder.withVertragsnummer(vertragsNumber);
            wbciDao.store(akmPvBuilder.build());
        }
    }

    @SuppressWarnings("unchecked")
    private void createVzm(String... vertragsNumbers) {
        for (String vertragsNumber : vertragsNumbers) {
            VerzoegerungsMeldungBuilder vzmBuilder = new VerzoegerungsMeldungBuilder();
            vzmBuilder.withVertragsnummer(vertragsNumber);
            wbciDao.store(vzmBuilder.build());
        }
    }

    @SuppressWarnings("unchecked")
    private StornoAenderungAufAnfrage createStornoWithGF(WbciGeschaeftsfall gf, MeldungTyp meldungTyp,
            LocalDateTime sendAfter, boolean withoutProcessedAt, MeldungsCode... meldungsCodes) {
        final StornoAenderungAufAnfrageTestBuilder stornoBuilder = new StornoAenderungAufAnfrageTestBuilder();
        stornoBuilder.withLastMeldung(meldungTyp, LocalDateTime.now(), meldungsCodes);
        stornoBuilder.withWbciGeschaeftsfall(gf);
        if (withoutProcessedAt) {
            stornoBuilder.withoutProcessedAt();
        }
        stornoBuilder.withSendAfter(sendAfter);
        /** set update date to keep sorting of the {@link WbciDao#findMostRecentPreagreements(CarrierRole)} query**/
        stornoBuilder.withUpdatedAt(LocalDateTime.now());
        stornoBuilder.withAnswerDeadline(LocalDate.now().plusDays(3));
        stornoBuilder.withIsMnetDeadline(false);
        final StornoAenderungAufAnfrage va = stornoBuilder.buildValid(V1, gf.getTyp());
        return wbciDao.store(va);
    }

    @Test
    public void findAntwortfrist() {
        final Antwortfrist antwortfristVa = new AntwortfristBuilder()
                .withRequestTyp(VA)
                .withRequestStatus(WbciRequestStatus.VA_VORGEHALTEN)
                .withFristInStunden(72L)
                .build();
        wbciDao.store(antwortfristVa);
        final Antwortfrist antwortfristTv = new AntwortfristBuilder()
                .withRequestTyp(TV)
                .withRequestStatus(WbciRequestStatus.TV_VORGEHALTEN)
                .withFristInStunden(48L)
                .build();
        wbciDao.store(antwortfristTv);

        Antwortfrist antwortfrist = wbciDao.findAntwortfrist(VA, WbciRequestStatus.VA_VORGEHALTEN);
        assertNotNull(antwortfrist);
        assertEquals(antwortfrist.getFristInStunden().longValue(), 72L);
        antwortfrist = wbciDao.findAntwortfrist(TV, WbciRequestStatus.TV_VORGEHALTEN);
        assertNotNull(antwortfrist);
        assertEquals(antwortfrist.getFristInStunden().longValue(), 48L);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void createAntwortfristForSameStatus() {
        final Antwortfrist antwortfristVa = new AntwortfristBuilder()
                .withRequestTyp(RequestTyp.VA)
                .withRequestStatus(WbciRequestStatus.VA_EMPFANGEN)
                .withFristInStunden(72L)
                .build();
        wbciDao.store(antwortfristVa);
        final Antwortfrist antwortfristVa2 = new AntwortfristBuilder()
                .withRequestTyp(RequestTyp.VA)
                .withRequestStatus(WbciRequestStatus.VA_EMPFANGEN)
                .withFristInStunden(48L)
                .build();
        wbciDao.store(antwortfristVa2);
        wbciDao.findAntwortfrist(RequestTyp.VA, WbciRequestStatus.VA_EMPFANGEN);
    }

    @DataProvider
    public Object[][] findElapsedPreagreementCandidatesData() {
        return new Object[][] {
                // active, elapsed, no todos, not klaerfall -> should be completed
                { ACTIVE, LocalDate.now().minusDays(3), false, false, true },
                // new_va, elapsed, no todos, not klaerfall -> should not be complete (wrong state)
                { NEW_VA, LocalDate.now().minusDays(3), false, false, false },
                // active, not elapsed, no todos, not klaerfall -> should not be completed (wechseltermin in future)
                { ACTIVE, LocalDate.now(), false, false, false },
                // active, no wechseltermin, no todos, not klaerfall -> should not be completed (no wechseltermin)
                { ACTIVE, LocalDate.now(), false, false, false },
                // active, elapsed, with todos, not klaerfall -> should not be completed (todos exist)
                { ACTIVE, LocalDate.now().minusDays(3), true, false, false },
                // active, elapsed, no todos, klaerfall -> should not be completed (marked as klaerfall)
                { ACTIVE, LocalDate.now().minusDays(3), false, true, false },
        };
    }

    @Test(dataProvider = "findElapsedPreagreementCandidatesData")
    public void findElapsedPreagreementCandidates(WbciGeschaeftsfallStatus status, LocalDate wechselTermin,
            boolean withUncompletedAutomationTasks, boolean klaerfall, boolean shouldGfBeCandidate) {
        WbciGeschaeftsfall gf = createWbciGeschaeftsfallAufnehmend(status, wechselTermin, false, withUncompletedAutomationTasks, false,
                klaerfall, Technologie.FTTC);
        List<WbciGeschaeftsfall> elapsedPreagreementCandidates = wbciDao.findElapsedPreagreements(0);
        wbciDao.flushSession();
        wbciDao.refresh(gf);

        boolean isGfCandidate = false;
        if (elapsedPreagreementCandidates != null) {
            for (WbciGeschaeftsfall gfCandidate : elapsedPreagreementCandidates) {
                if (gfCandidate.getId().equals(gf.getId())) {
                    isGfCandidate = true;
                }
            }
        }
        assertEquals(shouldGfBeCandidate, isGfCandidate);
    }

    
    @Test
    public void testFindAutomatableStrAufErlmsForWitaProcessing() throws Exception {
        WbciGeschaeftsfall gfActive = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfPassive = createWbciGeschaeftsfallAufnehmend(PASSIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfNewVaExpired = createWbciGeschaeftsfallAufnehmend(NEW_VA_EXPIRED, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfCompleted = createWbciGeschaeftsfallAufnehmend(COMPLETE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfKlaerfall = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, true, Technologie.FTTC);
        WbciGeschaeftsfall gfNoWita = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfNotAutomatable = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationError = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, true, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationDone = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfOtherTechnology = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.ADSL_SA);
        WbciGeschaeftsfall gfStrAufErlmVersendet = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);

        for (WbciGeschaeftsfall gf : Arrays.asList(gfActive, gfPassive, gfNewVaExpired, gfCompleted, gfNoWita, gfKlaerfall, gfNotAutomatable, gfAutomationError, gfAutomationDone, gfOtherTechnology, gfStrAufErlmVersendet)) {
            createVaWithGF(gf, WbciRequestStatus.RUEM_VA_EMPFANGEN);
        }

        StornoAnfrage<WbciGeschaeftsfall> strAufActive = createStornoAufhebungAuf(gfActive, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmActive = createErlmStr(strAufActive.getWbciGeschaeftsfall(), strAufActive.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strAufPassive = createStornoAufhebungAuf(gfPassive, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmPassive = createErlmStr(strAufPassive.getWbciGeschaeftsfall(), strAufPassive.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strNewVaExpired = createStornoAufhebungAuf(gfNewVaExpired, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmNewVaExpired = createErlmStr(strNewVaExpired.getWbciGeschaeftsfall(), strNewVaExpired.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strCompleted = createStornoAufhebungAuf(gfCompleted, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmCompleted = createErlmStr(strCompleted.getWbciGeschaeftsfall(), strCompleted.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strKlaerfall = createStornoAufhebungAuf(gfKlaerfall, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmKlaerfall = createErlmStr(strKlaerfall.getWbciGeschaeftsfall(), strKlaerfall.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strNoWita = createStornoAufhebungAuf(gfNoWita, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmNoWita = createErlmStr(strNoWita.getWbciGeschaeftsfall(), strNoWita.getTyp(), false);

        StornoAnfrage<WbciGeschaeftsfall> strNotAutomatable = createStornoAufhebungAuf(gfNotAutomatable, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmNotAutomatable = createErlmStr(strNotAutomatable.getWbciGeschaeftsfall(), strNotAutomatable.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strAutomationError = createStornoAufhebungAuf(gfAutomationError, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmAutomationError = createErlmStr(strAutomationError.getWbciGeschaeftsfall(), strAutomationError.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strAutomationDone = createStornoAufhebungAuf(gfAutomationDone, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmAutomationDone = createErlmStr(strAutomationDone.getWbciGeschaeftsfall(), strAutomationDone.getTyp(), true);
        addAutomationTask(gfAutomationDone, erlmAutomationDone, AutomationTask.TaskName.WITA_SEND_STORNO);

        StornoAnfrage<WbciGeschaeftsfall> strOtherTechnology = createStornoAufhebungAuf(gfOtherTechnology, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmOtherTechnology = createErlmStr(strOtherTechnology.getWbciGeschaeftsfall(), strOtherTechnology.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strErlmVersendet = createStornoAufhebungAuf(gfStrAufErlmVersendet, WbciRequestStatus.STORNO_ERLM_VERSENDET);
        Erledigtmeldung erlmVersendet = createErlmStr(strErlmVersendet.getWbciGeschaeftsfall(), strErlmVersendet.getTyp(), true);

        List<ErledigtmeldungStornoAuf> result = wbciDao.findAutomateableStrAufhErlmsForWitaProcessing(Technologie.getWitaOrderRelevantTechnologies());
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, erlmActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, erlmPassive.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmNewVaExpired.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmKlaerfall.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmNoWita.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmNotAutomatable.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmAutomationError.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmAutomationDone.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmOtherTechnology.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmVersendet.getVorabstimmungsId()));
    }



    @Test
    public void testFindAutomatableStrAufErlmsDonatingProcessing() throws Exception {
        WbciGeschaeftsfall gfActive = createWbciGeschaeftsfallAbgebend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfPassive = createWbciGeschaeftsfallAbgebend(PASSIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfNewVaExpired = createWbciGeschaeftsfallAbgebend(NEW_VA_EXPIRED, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfCompleted = createWbciGeschaeftsfallAbgebend(COMPLETE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfKlaerfall = createWbciGeschaeftsfallAbgebend(ACTIVE, LocalDate.now(), true, false, false, true, Technologie.FTTC);
        WbciGeschaeftsfall gfNotAutomatable = createWbciGeschaeftsfallAbgebend(ACTIVE, LocalDate.now(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationError = createWbciGeschaeftsfallAbgebend(ACTIVE, LocalDate.now(), true, true, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationDone = createWbciGeschaeftsfallAbgebend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfStrAufErlmVersendet = createWbciGeschaeftsfallAbgebend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);

        for (WbciGeschaeftsfall gf : Arrays.asList(gfActive, gfPassive, gfNewVaExpired, gfCompleted, gfKlaerfall, gfNotAutomatable, gfAutomationError, gfAutomationDone, gfStrAufErlmVersendet)) {
            createVaWithGF(gf, WbciRequestStatus.STORNO_EMPFANGEN);
        }

        StornoAnfrage<WbciGeschaeftsfall> strAufActive = createStornoAufhebungAuf(gfActive, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmActive = createErlmStr(strAufActive.getWbciGeschaeftsfall(), strAufActive.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strAufPassive = createStornoAufhebungAuf(gfPassive, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmPassive = createErlmStr(strAufPassive.getWbciGeschaeftsfall(), strAufPassive.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strNewVaExpired = createStornoAufhebungAuf(gfNewVaExpired, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmNewVaExpired = createErlmStr(strNewVaExpired.getWbciGeschaeftsfall(), strNewVaExpired.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strCompleted = createStornoAufhebungAuf(gfCompleted, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmCompleted = createErlmStr(strCompleted.getWbciGeschaeftsfall(), strCompleted.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strKlaerfall = createStornoAufhebungAuf(gfKlaerfall, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmKlaerfall = createErlmStr(strKlaerfall.getWbciGeschaeftsfall(), strKlaerfall.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strNotAutomatable = createStornoAufhebungAuf(gfNotAutomatable, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmNotAutomatable = createErlmStr(strNotAutomatable.getWbciGeschaeftsfall(), strNotAutomatable.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strAutomationError = createStornoAufhebungAuf(gfAutomationError, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmAutomationError = createErlmStr(strAutomationError.getWbciGeschaeftsfall(), strAutomationError.getTyp(), true);

        StornoAnfrage<WbciGeschaeftsfall> strAutomationDone = createStornoAufhebungAuf(gfAutomationDone, WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        Erledigtmeldung erlmAutomationDone = createErlmStr(strAutomationDone.getWbciGeschaeftsfall(), strAutomationDone.getTyp(), true);
        addAutomationTask(gfAutomationDone, erlmAutomationDone, AutomationTask.TaskName.UNDO_AUFTRAG_KUENDIGUNG);

        StornoAnfrage<WbciGeschaeftsfall> strErlmVersendet = createStornoAufhebungAuf(gfStrAufErlmVersendet, WbciRequestStatus.STORNO_ERLM_VERSENDET);
        Erledigtmeldung erlmVersendet = createErlmStr(strErlmVersendet.getWbciGeschaeftsfall(), strErlmVersendet.getTyp(), true);

        List<ErledigtmeldungStornoAuf> result = wbciDao.findAutomateableStrAufhErlmsDonatingProcessing();
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, erlmActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, erlmPassive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, erlmVersendet.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, erlmCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmNewVaExpired.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmKlaerfall.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmNotAutomatable.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmAutomationError.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmAutomationDone.getVorabstimmungsId()));
    }


    @Test
    public void testFindAutomatableTvErlmsForWitaProcessing() throws Exception {
        WbciGeschaeftsfall gfActive = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfPassive = createWbciGeschaeftsfallAufnehmend(PASSIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfCompleted = createWbciGeschaeftsfallAufnehmend(COMPLETE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfKlaerfall = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, true, Technologie.FTTC);
        WbciGeschaeftsfall gfNoWita = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfNotAutomatable = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationError = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, true, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationDone = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfOtherTechnology = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.ADSL_SA);
        WbciGeschaeftsfall gfTvErlmVersendet = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfActiveStorno = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);

        for (WbciGeschaeftsfall gf : Arrays.asList(gfActive, gfPassive, gfCompleted, gfNoWita, gfKlaerfall, gfNotAutomatable, gfAutomationError, gfAutomationDone, gfOtherTechnology, gfTvErlmVersendet, gfActiveStorno)) {
            createVaWithGF(gf, WbciRequestStatus.RUEM_VA_EMPFANGEN);
        }
        
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvActive = createTv(gfActive, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvActive = createErlmTv(tvActive.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvActive.getVorabstimmungsId());
        
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvPassive = createTv(gfPassive, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvPassive = createErlmTv(tvPassive.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvPassive.getVorabstimmungsId());
        
        // VA abgeschlossen --> nicht in Result erwartet
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvCompleted = createTv(gfCompleted, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvCompleted = createErlmTv(tvCompleted.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvCompleted.getVorabstimmungsId());
        
        // VA als Klaerfall markiert --> nicht in Result erwartet
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvKlaerfall = createTv(gfKlaerfall, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvKlaerfall = createErlmTv(tvKlaerfall.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvKlaerfall.getVorabstimmungsId());
        
        // kein WITA-Vorgang zur VA --> nicht in Result erwartet
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvNoWita = createTv(gfNoWita, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvNoWita = createErlmTv(tvNoWita.getWbciGeschaeftsfall());
        
        // VA nicht automatisierbar --> nicht in Result erwartet
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvNotAutomatable = createTv(gfNotAutomatable, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvNotAutomatable = createErlmTv(tvNotAutomatable.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvNotAutomatable.getVorabstimmungsId());
        
        // VA mit Automatisierungsfehler --> nicht in Result erwartet
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvAutomationError = createTv(gfAutomationError, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvAutomationError = createErlmTv(tvAutomationError.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvAutomationError.getVorabstimmungsId());

        // VA mit bereits durchgefuehrter Automatisierung --> nicht in Result erwartet
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvAutomationDone = createTv(gfAutomationDone, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvAutomationDone = createErlmTv(tvAutomationDone.getWbciGeschaeftsfall());
        addAutomationTask(gfAutomationDone, erlmTvAutomationDone, AutomationTask.TaskName.WITA_SEND_TV);
        createValidWitaCBVorgang(tvAutomationDone.getVorabstimmungsId());
        
        // keine TAL-Technologie --> abhaengig von Filter-Parameter im Result erwartet oder nicht
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvOtherTechnology = createTv(gfOtherTechnology, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        ErledigtmeldungTerminverschiebung erlmTvOtherTechnology = createErlmTv(tvOtherTechnology.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvOtherTechnology.getVorabstimmungsId());
        
        // Vorgang mit Status 'TV_ERLM_VERSENDET' --> nicht in Result erwartet, da falscher Storno
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvErlmVersendet = createTv(gfTvErlmVersendet, WbciRequestStatus.TV_ERLM_VERSENDET);
        ErledigtmeldungTerminverschiebung erlmTvVersendet = createErlmTv(tvErlmVersendet.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvErlmVersendet.getVorabstimmungsId());

        // Vorgang mit aktivem Storno --> nicht in Result erwartet
        TerminverschiebungsAnfrage<WbciGeschaeftsfall> tvActiveStorno = createTv(gfActiveStorno, WbciRequestStatus.TV_ERLM_EMPFANGEN);
        StornoAnfrage<WbciGeschaeftsfall> storno = createStornoAufhebungAbg(gfActiveStorno, STORNO_VERSENDET);
        ErledigtmeldungTerminverschiebung erlmTvActiveStorno = createErlmTv(tvActiveStorno.getWbciGeschaeftsfall());
        createValidWitaCBVorgang(tvActiveStorno.getVorabstimmungsId());

        List<ErledigtmeldungTerminverschiebung> result = wbciDao.findAutomateableTvErlmsForWitaProcessing(Technologie.getWitaOrderRelevantTechnologies());
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, erlmTvActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, erlmTvPassive.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvNoWita.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvKlaerfall.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvNotAutomatable.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvAutomationError.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvAutomationDone.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvOtherTechnology.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvVersendet.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvActiveStorno.getVorabstimmungsId()));

        result = wbciDao.findAutomateableTvErlmsForWitaProcessing(Collections.<Technologie>emptyList());
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, erlmTvActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, erlmTvPassive.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvNoWita.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvKlaerfall.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvNotAutomatable.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvAutomationError.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvAutomationDone.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, erlmTvOtherTechnology.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvVersendet.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, erlmTvActiveStorno.getVorabstimmungsId()));
    }
    

    @Test
    public void testFindAutomatableAkmTRsForWitaProcessing() throws Exception {
        WbciGeschaeftsfall gfActive = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfPassive = createWbciGeschaeftsfallAufnehmend(PASSIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfCompleted = createWbciGeschaeftsfallAufnehmend(COMPLETE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfKlaerfall = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, true, Technologie.FTTC);
        WbciGeschaeftsfall gfWita = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfNotAutomatable = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationError = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, true, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationDone = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfOtherTechnologie = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.ADSL_SA);
        WbciGeschaeftsfall gfAkmTrEmpfangen = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);

        for (WbciGeschaeftsfall gf : Arrays.asList(gfActive, gfPassive, gfCompleted, gfWita, gfKlaerfall, gfNotAutomatable, gfAutomationError, gfAutomationDone, gfOtherTechnologie)) {
            createVaWithGF(gf, WbciRequestStatus.AKM_TR_VERSENDET);
        }
        createVaWithGF(gfAkmTrEmpfangen, WbciRequestStatus.AKM_TR_EMPFANGEN);

        UebernahmeRessourceMeldung akmTrActive = createAkmTr(gfActive, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrPassive = createAkmTr(gfPassive, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrCompleted = createAkmTr(gfCompleted, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrWita = createAkmTr(gfWita, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrKlaerfall = createAkmTr(gfKlaerfall, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrNotAutomatable = createAkmTr(gfNotAutomatable, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrAutomationError = createAkmTr(gfAutomationError, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrAutomationDone = createAkmTr(gfAutomationDone, LocalDateTime.now().minusDays(1));
        addAutomationTask(gfAutomationDone, akmTrAutomationDone, AutomationTask.TaskName.WITA_SEND_NEUBESTELLUNG);
        UebernahmeRessourceMeldung akmTrWrongTechnologie = createAkmTr(gfOtherTechnologie, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrWrongRequestState = createAkmTr(gfAkmTrEmpfangen, LocalDateTime.now().minusDays(1));

        List<UebernahmeRessourceMeldung> result = wbciDao.findAutomatableAkmTRsForWitaProcesing(Technologie.getWitaOrderRelevantTechnologies());
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, akmTrActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrPassive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrWita.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrKlaerfall.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrNotAutomatable.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrAutomationError.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrAutomationDone.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrWrongTechnologie.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrWrongRequestState.getVorabstimmungsId()));

        result = wbciDao.findAutomatableAkmTRsForWitaProcesing(Collections.<Technologie>emptyList());
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, akmTrActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrPassive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrWita.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrKlaerfall.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrNotAutomatable.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrAutomationError.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrAutomationDone.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrWrongTechnologie.getVorabstimmungsId()));

        //test open wita order, and open storno, and get latest AKM-TR
        createValidWitaCBVorgang(gfWita.getVorabstimmungsId());
        StornoAnfrage<WbciGeschaeftsfall> stornoAuf = createStornoAufhebungAbg(gfActive, WbciRequestStatus.STORNO_VORGEHALTEN);
        UebernahmeRessourceMeldung akmTrPassiveSecondAkmTr = createAkmTr(gfPassive, LocalDateTime.now());
        result = wbciDao.findAutomatableAkmTRsForWitaProcesing(Collections.<Technologie>emptyList());
        assertNotEmpty(result);
        assertFalse(isPreagreementInWbciMessageList(result, akmTrWita.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, stornoAuf.getWbciGeschaeftsfall().getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrPassive.getVorabstimmungsId()));
        assertTrue(isMeldungInList(result, akmTrPassiveSecondAkmTr));
        assertFalse(isMeldungInList(result, akmTrPassive));
    }

    @Test
    public void testFindAutomatableIncomingAkmTRsForWitaProcessing() throws Exception {
        WbciGeschaeftsfall gfActive = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfPassive = createWbciGeschaeftsfallAufnehmend(PASSIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfCompleted = createWbciGeschaeftsfallAufnehmend(COMPLETE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfKlaerfall = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, true, Technologie.FTTC);
        WbciGeschaeftsfall gfWita = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfNotAutomatable = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationError = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, true, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfAutomationDone = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfOtherTechnologie = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTB);
        WbciGeschaeftsfall gfAkmTrVersendet = createWbciGeschaeftsfallAufnehmend(ACTIVE, LocalDate.now(), true, false, false, false, Technologie.FTTC);

        for (WbciGeschaeftsfall gf : Arrays.asList(gfActive, gfPassive, gfCompleted, gfWita, gfKlaerfall, gfNotAutomatable, gfAutomationError, gfAutomationDone, gfOtherTechnologie)) {
            createVaWithGF(gf, WbciRequestStatus.AKM_TR_EMPFANGEN);
        }
        createVaWithGF(gfAkmTrVersendet, WbciRequestStatus.AKM_TR_VERSENDET);

        UebernahmeRessourceMeldung akmTrActive = createAkmTr(gfActive, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrPassive = createAkmTr(gfPassive, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrCompleted = createAkmTr(gfCompleted, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrWita = createAkmTr(gfWita, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrKlaerfall = createAkmTr(gfKlaerfall, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrNotAutomatable = createAkmTr(gfNotAutomatable, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrAutomationError = createAkmTr(gfAutomationError, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrAutomationDone = createAkmTr(gfAutomationDone, LocalDateTime.now().minusDays(1));
        addAutomationTask(gfAutomationDone, akmTrAutomationDone, AutomationTask.TaskName.WITA_SEND_KUENDIGUNG);
        UebernahmeRessourceMeldung akmTrOtherTechnologie = createAkmTr(gfOtherTechnologie, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrWrongRequestState = createAkmTr(gfAkmTrVersendet, LocalDateTime.now().minusDays(1));

        List<UebernahmeRessourceMeldung> result = wbciDao.findAutomatableIncomingAkmTRsForWitaProcesing();
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, akmTrActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrPassive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrWita.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrOtherTechnologie.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrKlaerfall.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrNotAutomatable.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrAutomationError.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrAutomationDone.getVorabstimmungsId()));

        //test open wita order, and open storno, and get latest AKM-TR
        createValidWitaCBVorgang(gfWita.getVorabstimmungsId());
        StornoAnfrage<WbciGeschaeftsfall> stornoAuf = createStornoAufhebungAbg(gfActive, WbciRequestStatus.STORNO_VORGEHALTEN);
        UebernahmeRessourceMeldung akmTrPassiveSecondAkmTr = createAkmTr(gfPassive, LocalDateTime.now());
        result = wbciDao.findAutomatableIncomingAkmTRsForWitaProcesing();
        assertNotEmpty(result);
        assertFalse(isPreagreementInWbciMessageList(result, akmTrWita.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, stornoAuf.getWbciGeschaeftsfall().getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrPassive.getVorabstimmungsId()));
        assertTrue(isMeldungInList(result, akmTrPassiveSecondAkmTr));
        assertFalse(isMeldungInList(result, akmTrPassive));
    }
    

    private <MEL extends Meldung> boolean isMeldungInList(List<MEL> result, MEL expectedMeldung) {
        if (result != null) {
            for (MEL m : result) {
                if (m.equals(expectedMeldung)) {
                    return true;
                }
            }
        }
        return false;
    }

    private WitaCBVorgang createValidWitaCBVorgang(String vaID) {
        WitaCBVorgang cbVorgang = witaCbVorgangBuilder.get().build();
        cbVorgang.setVorabstimmungsId(vaID);
        wbciDao.store(cbVorgang);
        wbciDao.flushSession();
        return cbVorgang;
    }

    @Test
    public void testFindAkmTrsNearToWechseltermin() throws Exception {
        WbciGeschaeftsfall gfActive = createWbciGeschaeftsfallAufnehmend(ACTIVE, DateCalculationHelper.getDateInWorkingDaysFromNow(12).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfPassive = createWbciGeschaeftsfallAufnehmend(PASSIVE, DateCalculationHelper.getDateInWorkingDaysFromNow(-5).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfCompleted = createWbciGeschaeftsfallAufnehmend(COMPLETE, DateCalculationHelper.getDateInWorkingDaysFromNow(6).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfNewVa = createWbciGeschaeftsfallAufnehmend(NEW_VA, DateCalculationHelper.getDateInWorkingDaysFromNow(6).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfNewVaExpired = createWbciGeschaeftsfallAufnehmend(NEW_VA_EXPIRED, DateCalculationHelper.getDateInWorkingDaysFromNow(6).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfWtOutOfRange = createWbciGeschaeftsfallAufnehmend(ACTIVE, DateCalculationHelper.getDateInWorkingDaysFromNow(13).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall gfOtherTechnologie = createWbciGeschaeftsfallAufnehmend(ACTIVE, DateCalculationHelper.getDateInWorkingDaysFromNow(12).toLocalDate(), false, false, false, false, Technologie.ADSL_SA);
        WbciGeschaeftsfall gfAkmTrEmpfangen = createWbciGeschaeftsfallAufnehmend(ACTIVE, DateCalculationHelper.getDateInWorkingDaysFromNow(6).toLocalDate(), false, false, false, false, Technologie.FTTC);
        for (WbciGeschaeftsfall gf : Arrays.asList(gfActive, gfPassive, gfNewVa, gfNewVaExpired, gfCompleted, gfWtOutOfRange, gfOtherTechnologie)) {
            createVaWithGF(gf, WbciRequestStatus.AKM_TR_VERSENDET);
        }
        createVaWithGF(gfAkmTrEmpfangen, WbciRequestStatus.AKM_TR_EMPFANGEN);
        UebernahmeRessourceMeldung akmTrActive = createAkmTr(gfActive, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrPassive = createAkmTr(gfPassive, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrCompleted = createAkmTr(gfCompleted, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrNewVa = createAkmTr(gfNewVa, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrNewVaExpired = createAkmTr(gfNewVaExpired, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrWtOutOfRange = createAkmTr(gfWtOutOfRange, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrWrongTechnologie = createAkmTr(gfOtherTechnologie, LocalDateTime.now().minusDays(1));
        UebernahmeRessourceMeldung akmTrWrongRequestState = createAkmTr(gfAkmTrEmpfangen, LocalDateTime.now().minusDays(1));

        List<UebernahmeRessourceMeldung> result = wbciDao.findOverdueAkmTrsNearToWechseltermin(12, CarrierCode.MNET, Technologie.getWitaOrderRelevantTechnologies());
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, akmTrActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrPassive.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrNewVa.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrNewVaExpired.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrWtOutOfRange.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrWrongTechnologie.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrWrongRequestState.getVorabstimmungsId()));

        result = wbciDao.findOverdueAkmTrsNearToWechseltermin(12, CarrierCode.MNET, Collections.<Technologie>emptyList());
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciMessageList(result, akmTrActive.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrPassive.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrCompleted.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrNewVa.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrNewVaExpired.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciMessageList(result, akmTrWtOutOfRange.getVorabstimmungsId()));
        assertTrue(isPreagreementInWbciMessageList(result, akmTrWrongTechnologie.getVorabstimmungsId()));
    }

    @Test
    public void testFindPreagreementsWithStatusAndWechselTerminBefore() throws Exception {
        int wechselTerminInDays = 8;

        WbciGeschaeftsfall wechselTerminBeforeDate = createWbciGeschaeftsfallAufnehmend(NEW_VA, DateCalculationHelper.getDateInWorkingDaysFromNow(wechselTerminInDays - 1).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall wechselTerminOnDate = createWbciGeschaeftsfallAufnehmend(NEW_VA, DateCalculationHelper.getDateInWorkingDaysFromNow(wechselTerminInDays).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall wechselTerminAfterDate = createWbciGeschaeftsfallAufnehmend(NEW_VA, DateCalculationHelper.getDateInWorkingDaysFromNow(wechselTerminInDays + 1).toLocalDate(), false, false, false, false, Technologie.FTTC);
        WbciGeschaeftsfall wrongStatus = createWbciGeschaeftsfallAufnehmend(ACTIVE, DateCalculationHelper.getDateInWorkingDaysFromNow(wechselTerminInDays - 1).toLocalDate(), false, false, false, false, Technologie.FTTC);

        List<WbciGeschaeftsfall> result = wbciDao.findPreagreementsWithStatusAndWechselTerminBefore(NEW_VA, DateCalculationHelper.getDateInWorkingDaysFromNow(wechselTerminInDays).toLocalDate());
        assertNotEmpty(result);
        assertTrue(isPreagreementInWbciGeschaeftsfallList(result, wechselTerminBeforeDate.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciGeschaeftsfallList(result, wechselTerminOnDate.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciGeschaeftsfallList(result, wechselTerminAfterDate.getVorabstimmungsId()));
        assertFalse(isPreagreementInWbciGeschaeftsfallList(result, wrongStatus.getVorabstimmungsId()));
    }

    @Test
    public void testAutomationTasks() throws Exception {
        String vaId = getRandomVorabstimmungsId();
        WbciGeschaeftsfallKueMrn testGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vaId)
                .withStatus(ACTIVE)
                .addAutomationTask(
                        new AutomationTaskBuilder()
                                .withName(AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN)
                                .withCreatedAt(LocalDateTime.now())
                                .withUserId(1L)
                                .withUserName("blub")
                                .build()
                )
                .addAutomationTask(
                        new AutomationTaskBuilder()
                                .withName(AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN)
                                .withCreatedAt(LocalDateTime.now())
                                .withUserId(1L)
                                .withUserName("bla")
                                .build()
                )
                .buildValid(V1, VA_KUE_MRN);
        wbciDao.store(testGeschaeftsfall);
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vaId);
        assertNotEmpty(wbciGeschaeftsfall.getAutomationTasks());
        assertEquals(wbciGeschaeftsfall.getAutomationTasks().size(), 2);
    }


    @DataProvider
    public Object[][] findPreagreementsWithAutomatableRuemVa() {
        // should match - no wita vertragsnummer
        WbciGeschaeftsfallKueMrn gf0 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va0 = createVaWithRequestStatus(gf0, WbciRequestStatus.RUEM_VA_EMPFANGEN);
        RueckmeldungVorabstimmung ruemVa0 = createRuemVaWithLineIdsAndWitaContract(gf0, 0, 0, MeldungsCode.ZWA);

        // should match - one wita vertragsnummer
        WbciGeschaeftsfallKueMrn gf1 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va1 = createVaWithRequestStatus(gf1, WbciRequestStatus.RUEM_VA_EMPFANGEN);
        RueckmeldungVorabstimmung ruemVa1 = createRuemVaWithLineIdsAndWitaContract(gf1, 1, 0, MeldungsCode.ZWA);

        // no match: VA with wrong request status (RUEM_VA_VERSENDET)
        WbciGeschaeftsfallKueMrn gf2 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va2 = createVaWithRequestStatus(gf2, RUEM_VA_VERSENDET);
        RueckmeldungVorabstimmung ruemVa2 = createRuemVaWithLineIdsAndWitaContract(gf2, 1, 0, MeldungsCode.ZWA);

        // no match: GF not automatable
        WbciGeschaeftsfallKueMrn gf3 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        gf3.setAutomatable(Boolean.FALSE);
        VorabstimmungsAnfrage va3 = createVaWithRequestStatus(gf3, RUEM_VA_VERSENDET);
        RueckmeldungVorabstimmung ruemVa3 = createRuemVaWithLineIdsAndWitaContract(gf3, 1, 0, MeldungsCode.ZWA);

        // no match: GF with wrong status (NEW_VA)
        WbciGeschaeftsfallKueMrn gf4 = createGfWithAutomationTask(NEW_VA, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va4 = createVaWithRequestStatus(gf4, RUEM_VA_VERSENDET);
        RueckmeldungVorabstimmung ruemVa4 = createRuemVaWithLineIdsAndWitaContract(gf4, 1, 0, MeldungsCode.ZWA);

        // no match: RUEM-VA with wrong Meldungs-Code (ADA)
        WbciGeschaeftsfallKueMrn gf5 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va5 = createVaWithRequestStatus(gf5, RUEM_VA_VERSENDET);
        RueckmeldungVorabstimmung ruemVa5 = createRuemVaWithLineIdsAndWitaContract(gf5, 1, 0, MeldungsCode.ADAHSNR);

        // no match: RUEM-VA with too many wita vertrags nummern (2)
        WbciGeschaeftsfallKueMrn gf6 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va6 = createVaWithRequestStatus(gf6, RUEM_VA_VERSENDET);
        RueckmeldungVorabstimmung ruemVa6 = createRuemVaWithLineIdsAndWitaContract(gf6, 2, 0, MeldungsCode.ZWA);

        // no match: RUEM-VA with line id
        WbciGeschaeftsfallKueMrn gf7 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va7 = createVaWithRequestStatus(gf7, RUEM_VA_VERSENDET);
        RueckmeldungVorabstimmung ruemVa7 = createRuemVaWithLineIdsAndWitaContract(gf7, 0, 1, MeldungsCode.ZWA);

        // no match: GF with active Storno
        WbciGeschaeftsfallKueMrn gf8 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va8 = createVaWithRequestStatus(gf8, RUEM_VA_VERSENDET);
        RueckmeldungVorabstimmung ruemVa8 = createRuemVaWithLineIdsAndWitaContract(gf8, 1, 0, MeldungsCode.ZWA);
        StornoAenderungAbgAnfrage storno = new StornoAenderungAbgAnfrageTestBuilder().withWbciGeschaeftsfall(gf8).buildValid(V1, VA_KUE_MRN);

        // no match: GF with automation error
        WbciGeschaeftsfallKueMrn gf9 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.ERROR);
        VorabstimmungsAnfrage va9 = createVaWithRequestStatus(gf9, RUEM_VA_VERSENDET);
        RueckmeldungVorabstimmung ruemVa9 = createRuemVaWithLineIdsAndWitaContract(gf9, 1, 0, MeldungsCode.ZWA);

        // no match: RUEM_VA already processed
        WbciGeschaeftsfallKueMrn gf10 = createGfWithAutomationTask(PASSIVE, true, AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, AutomationTask.AutomationStatus.COMPLETED);
        VorabstimmungsAnfrage va10 = createVaWithRequestStatus(gf10, WbciRequestStatus.RUEM_VA_EMPFANGEN);
        RueckmeldungVorabstimmung ruemVa10 = createRuemVaWithLineIdsAndWitaContract(gf10, 0, 0, MeldungsCode.ZWA);

        return new Object[][] {
                { gf0, va0, ruemVa0, null, false, true },
                { gf1, va1, ruemVa1, null, false, true },
                { gf2, va2, ruemVa2, null, false, false },
                { gf3, va3, ruemVa3, null, false, false },
                { gf4, va4, ruemVa4, null, false, false },
                { gf5, va5, ruemVa5, null, false, false },
                { gf6, va6, ruemVa6, null, false, false },
                { gf7, va7, ruemVa7, null, false, false },
                { gf8, va8, ruemVa8, storno, false, false },
                { gf9, va9, ruemVa9, null, false, false },
                { gf10, va10, ruemVa10, null, true, false },
        };
    }

    @Test(dataProvider = "findPreagreementsWithAutomatableRuemVa")
    public void testFindPreagreementsWithAutomatableRuemVa(WbciGeschaeftsfallKueMrn gf, VorabstimmungsAnfrage va, 
            RueckmeldungVorabstimmung ruemVa, 
            StornoAenderungAbgAnfrage storno,
            boolean addAutomationTaskForMeldung,
            boolean shouldMatch) throws Exception {

        final int originNumberOfPreagreementsReceiving = wbciDao.findPreagreementsWithAutomatableRuemVa().size();

        //
        // create test data
        //

        wbciDao.store(gf);
        wbciDao.store(va);
        wbciDao.store(ruemVa);
        
        if (addAutomationTaskForMeldung) {
            addAutomationTask(gf, ruemVa, AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN);
        }
        
        if (storno != null) {
            wbciDao.store(storno);
        }
        wbciDao.flushSession();

        //
        // execute query
        //

        final List<String> preagreements = wbciDao.findPreagreementsWithAutomatableRuemVa();
        if (shouldMatch) {
            assertTrue(!preagreements.isEmpty());
            assertEquals(preagreements.size(), originNumberOfPreagreementsReceiving + 1);
            assertTrue(preagreements.contains(gf.getVorabstimmungsId()));
        }
        else {
            assertEquals(preagreements.size(), originNumberOfPreagreementsReceiving);
        }

    }

    @DataProvider
    public Object[][] automateableOutgoingRuemVaForKuendigung() {
        // @formatter:off
        return new Object[][] {
            // GF-Builder,                               GF-Typ, GF-Klaerf, GF-Auto, GF-Stat,  VA-Status,         Aut-Err, Task-Name, inResult
            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, false, true,    PASSIVE,  RUEM_VA_VERSENDET, false,   null,      true },
            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, false, true,    ACTIVE,   RUEM_VA_VERSENDET, false,   null,      true },
            { new WbciGeschaeftsfallKueOrnTestBuilder(), VA_KUE_ORN, false, true,    PASSIVE,  RUEM_VA_VERSENDET, false,   null,      true },
            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, false, true,    PASSIVE,  RUEM_VA_VERSENDET, false,   TAIFUN_NACH_RUEMVA_AKTUALISIEREN, true },

            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, false, true,    COMPLETE, RUEM_VA_VERSENDET, false,   null,      false },
            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, true,  true,    PASSIVE,  RUEM_VA_VERSENDET, false,   null,      false },
            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, false, false,   PASSIVE,  RUEM_VA_VERSENDET, false,   null,      false },

            { new WbciGeschaeftsfallRrnpTestBuilder(),   VA_RRNP,    false, true,    PASSIVE,  RUEM_VA_VERSENDET, false,   null,      false },
            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, false, true,    PASSIVE,  RUEM_VA_VERSENDET, true,    null,      false },
            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, false, true,    PASSIVE,  ABBM_VERSENDET,    false,   null,      false },

            { new WbciGeschaeftsfallKueMrnTestBuilder(), VA_KUE_MRN, false, true,    ACTIVE,   RUEM_VA_VERSENDET, false,   AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN, false },

        };
    // @formatter:on
    }

    @Test(dataProvider = "automateableOutgoingRuemVaForKuendigung")
    public void testFindAutomateableOutgoingRuemVaForKuendigung(WbciGeschaeftsfallBuilder<WbciGeschaeftsfall> gfTestBuilder,
            GeschaeftsfallTyp gfTyp, boolean gfKlaerfall, boolean gfAutomatable, WbciGeschaeftsfallStatus gfStatus,
            WbciRequestStatus vaStatus, boolean createAutomationError, AutomationTask.TaskName completedAutomationTaskName,
            boolean expectedInResult) {
        gfTestBuilder
                .withKlaerfall(gfKlaerfall)
                .withAutomatable(gfAutomatable)
                .withStatus(gfStatus);
        WbciGeschaeftsfall gf = ((WbciTestBuilder<WbciGeschaeftsfall>)gfTestBuilder).buildValid(V1, gfTyp);
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder()
                .withRequestStatus(vaStatus)
                .withWbciGeschaeftsfall(gf)
                .buildValid(V1, gf.getTyp());
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withWbciGeschaeftsfall(gf)
                .buildValid(V1, gf.getTyp());
        wbciDao.store(gf);
        wbciDao.store(va);
        wbciDao.store(ruemVa);
        if(createAutomationError) {
            AutomationTask errorAutomationTask = new AutomationTaskTestBuilder()
                    .withMeldung(ruemVa)
                    .withStatus(AutomationTask.AutomationStatus.ERROR)
                    .buildValid(V1, gf.getTyp());
            gf.addAutomationTask(errorAutomationTask);
            wbciDao.store(errorAutomationTask);
        }
        if(completedAutomationTaskName != null) {
            AutomationTask completedAutomationTask = new AutomationTaskTestBuilder()
                    .withMeldung(ruemVa)
                    .withName(completedAutomationTaskName)
                    .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                    .buildValid(V1, gf.getTyp());
            gf.addAutomationTask(completedAutomationTask);
            wbciDao.store(completedAutomationTask);
        }
        wbciDao.flushSession();

        List<WbciGeschaeftsfall> result = wbciDao.findAutomateableOutgoingRuemVaForKuendigung();

        if(expectedInResult) {
            assertTrue(!result.isEmpty());
            assertTrue(result.contains(gf));
        } else {
            assertFalse(result.contains(gf));
        }
    }


    private WbciGeschaeftsfall createWbciGeschaeftsfallAbgebend(WbciGeschaeftsfallStatus status, LocalDate wechseltermin,
            boolean automatable, boolean withUncompletedAutomationTasks, boolean withNonRelevantBillingOrderNo,
            boolean klaerfall, Technologie withMnetTechnologie) {
        return createWbciGeschaeftsfall(false, status, wechseltermin, automatable, withUncompletedAutomationTasks,
                withNonRelevantBillingOrderNo, klaerfall, withMnetTechnologie);
    }

    private WbciGeschaeftsfall createWbciGeschaeftsfallAufnehmend(WbciGeschaeftsfallStatus status, LocalDate wechseltermin,
            boolean automatable, boolean withUncompletedAutomationTasks, boolean withNonRelevantBillingOrderNo,
            boolean klaerfall, Technologie withMnetTechnologie) {
        return createWbciGeschaeftsfall(true, status, wechseltermin, automatable, withUncompletedAutomationTasks,
                withNonRelevantBillingOrderNo, klaerfall, withMnetTechnologie);
    }

    private WbciGeschaeftsfall createWbciGeschaeftsfall(boolean aufnehmend, WbciGeschaeftsfallStatus status, LocalDate wechseltermin,
            boolean automatable, boolean withUncompletedAutomationTasks, boolean withNonRelevantBillingOrderNo,
            boolean klaerfall, Technologie withMnetTechnologie) {
        List<AutomationTask> automationTasks;
        if (withUncompletedAutomationTasks) {
            automationTasks = Arrays.asList(
                    new AutomationTaskTestBuilder()
                            .withName(AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN)
                            .withStatus(AutomationTask.AutomationStatus.ERROR)
                            .buildValid(V1, VA_KUE_MRN),
                    new AutomationTaskTestBuilder()
                            .withName(AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN)
                            .buildValid(V1, VA_KUE_MRN)
            );
        }
        else {
            automationTasks = Arrays.asList(
                    new AutomationTaskTestBuilder()
                            .withName(AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN)
                            .buildValid(V1, VA_KUE_MRN),
                    new AutomationTaskTestBuilder()
                            .withName(AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN)
                            .buildValid(V1, VA_KUE_MRN)
            );
        }

        Set<Long> nonBillingRelevantOrderNos = new HashSet<>();
        if (withNonRelevantBillingOrderNo) {
            nonBillingRelevantOrderNos.add(1001L);
            nonBillingRelevantOrderNos.add(1002L);
            nonBillingRelevantOrderNos.add(1003L);
        }

        CarrierCode abgebenderEkp = (aufnehmend) ? CarrierCode.DTAG : CarrierCode.MNET;
        CarrierCode aufnehmenderEkp = (aufnehmend) ? CarrierCode.MNET : CarrierCode.DTAG;

        String vaId = getRandomVorabstimmungsId();
        WbciGeschaeftsfallKueMrn testGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAbgebenderEKP(abgebenderEkp)
                .withAufnehmenderEKP(aufnehmenderEkp)
                .withVorabstimmungsId(vaId)
                .withStatus(status)
                .withAutomatable(automatable)
                .withAutomationTasks(automationTasks)
                .withNonBillingRelevantOrderNos(nonBillingRelevantOrderNos)
                .withKlaerfall(klaerfall)
                .withMnetTechnologie(withMnetTechnologie)
                .withWechseltermin(wechseltermin)
                .buildValid(V1, VA_KUE_MRN);
        wbciDao.store(testGeschaeftsfall);
        return wbciDao.findWbciGeschaeftsfall(vaId);
    }
    
    private void addAutomationTask(WbciGeschaeftsfall gf, Meldung meldungForTask, AutomationTask.TaskName taskName) {
        AutomationTask automationTask = new AutomationTaskTestBuilder()
                .withName(taskName)
                .withMeldung(meldungForTask)
                .buildValid(V1, gf.getTyp());
        
        List<AutomationTask> tasks = new ArrayList<>();
        if (gf.getAutomationTasks() != null) {
            tasks.addAll(gf.getAutomationTasks());
        }
        tasks.add(automationTask);
        
        gf.setAutomationTasks(tasks);
        wbciDao.store(gf);
    }

    private <GF extends WbciGeschaeftsfall> StornoAnfrage<GF> createStornoAufhebungAbg(GF wbciGeschaeftsfall, WbciRequestStatus requestStatus) {
        final StornoAufhebungAbgAnfrageTestBuilder<GF> stornoBuilder = new StornoAufhebungAbgAnfrageTestBuilder<>();
        stornoBuilder.withRequestStatus(requestStatus);
        stornoBuilder.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        StornoAnfrage<GF> storno = stornoBuilder.buildValid(V1, VA_KUE_MRN);
        wbciDao.store(storno);
        wbciDao.flushSession();
        return storno;
    }

    private <GF extends WbciGeschaeftsfall> StornoAnfrage<GF> createStornoAufhebungAuf(GF wbciGeschaeftsfall, WbciRequestStatus requestStatus) {
        final StornoAufhebungAufAnfrageTestBuilder<GF> stornoBuilder = new StornoAufhebungAufAnfrageTestBuilder<>();
        stornoBuilder.withRequestStatus(requestStatus);
        stornoBuilder.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        StornoAnfrage<GF> storno = stornoBuilder.buildValid(V1, VA_KUE_MRN);
        wbciDao.store(storno);
        wbciDao.flushSession();
        return storno;
    }

    private <GF extends WbciGeschaeftsfall> TerminverschiebungsAnfrage<GF> createTv(GF wbciGeschaeftsfall, WbciRequestStatus requestStatus) {
        final TerminverschiebungsAnfrageTestBuilder<GF> tvBuilder = new TerminverschiebungsAnfrageTestBuilder<>();
        tvBuilder.withRequestStatus(requestStatus);
        tvBuilder.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        TerminverschiebungsAnfrage<GF> tv = tvBuilder.buildValid(V1, wbciGeschaeftsfall.getTyp());
        wbciDao.store(tv);
        wbciDao.flushSession();
        return tv;
    }

    private boolean isPreagreementInWbciMessageList(List<? extends WbciMessage> wbciMessageList, String vaId) {
        for (WbciMessage wbciMessage : wbciMessageList) {
            if (vaId.equals(wbciMessage.getVorabstimmungsId())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private WbciGeschaeftsfallKueMrn createGfWithAutomationTask(WbciGeschaeftsfallStatus gfStatus, Boolean gfAutomatable, AutomationTask.TaskName taskname, AutomationTask.AutomationStatus atStatus) {
        return new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAutomatable(gfAutomatable)
                .withStatus(gfStatus)
                .addAutomationTask(
                        new AutomationTaskBuilder()
                                .withName(taskname)
                                .withCreatedAt(LocalDateTime.now())
                                .withUserId(1L)
                                .withUserName("blub")
                                .withStatus(atStatus)
                                .build()
                ).buildValid(V1, VA_KUE_MRN);
    }

    @SuppressWarnings("unchecked")
    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> createVaWithRequestStatus(WbciGeschaeftsfallKueMrn gf, WbciRequestStatus requestStatus) {
        final VorabstimmungsAnfrageTestBuilder vaBuilder1 = new VorabstimmungsAnfrageTestBuilder();
        vaBuilder1.withRequestStatus(requestStatus);
        vaBuilder1.withWbciGeschaeftsfall(gf);
        return vaBuilder1.buildValid(V1, VA_KUE_MRN);
    }

    @SuppressWarnings("unchecked")
    private RueckmeldungVorabstimmung createRuemVaWithLineIdsAndWitaContract(WbciGeschaeftsfall gf, int witaVertragsNumberCount, int lineIdCount, MeldungsCode code) {
        RueckmeldungVorabstimmungTestBuilder ruemVaBuilder = new RueckmeldungVorabstimmungTestBuilder();

        for (int i = 0; i < witaVertragsNumberCount; i++) {
            ruemVaBuilder.addTechnischeRessource(
                    new TechnischeRessourceTestBuilder()
                            .withVertragsnummer("vertrag" + i)
                            .buildValid(V1, gf.getTyp())
            );
        }
        for (int i = 0; i < lineIdCount; i++) {
            ruemVaBuilder.addTechnischeRessource(
                    new TechnischeRessourceTestBuilder()
                            .withLineId("line100" + i)
                            .buildValid(V1, gf.getTyp())
            );
        }
        ruemVaBuilder.withWbciGeschaeftsfall(gf);
        ruemVaBuilder.withMeldungsPositionen(new HashSet<>(Collections.singletonList(
                new MeldungPositionRueckmeldungVaBuilder()
                        .withMeldungsText("Some text")
                        .withMeldungsCode(code)
                        .build()
        )));
        return ruemVaBuilder.buildValid(V1, gf.getTyp());
    }

}
