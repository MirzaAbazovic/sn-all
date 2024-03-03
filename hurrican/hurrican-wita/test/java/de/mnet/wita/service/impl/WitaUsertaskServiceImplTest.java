/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 13:43:30
 */
package de.mnet.wita.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.activiti.engine.task.Task;
import org.fest.assertions.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.dao.TaskDao;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.model.AbgebendeLeitungenVorgang;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.AkmPvUserTask.AkmPvStatus;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.model.UserTask2AuftragDaten;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.model.VorabstimmungAbgebendBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaVorabstimmungService;
import de.mnet.wita.service.WitaWbciServiceFacade;

@Test(groups = UNIT)
public class WitaUsertaskServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private WitaUsertaskServiceImpl witaUsertaskService;
    @Mock
    private DateTimeCalculationService dateTimeCalculationService;
    @Mock
    private NiederlassungService niederlassungService;
    @Mock
    private CarrierService carrierService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private PhysikService physikService;
    @Mock
    private TaskDao taskDao;
    @Mock
    private WitaVorabstimmungService witaVorabstimmungService;
    @Mock
    private WitaWbciServiceFacade witaWbciServiceFacade;
    @Mock
    private WitaTalOrderService witaTalOrderService;
    @Mock
    private WitaConfigService witaConfigService;

    private KueDtUserTask kueDtUserTask;

    @BeforeMethod
    public void setup() {
        witaUsertaskService = new WitaUsertaskServiceImpl();
        MockitoAnnotations.initMocks(this);
        when(dateTimeCalculationService.getWorkingDaysBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenAnswer(
                new Answer<Integer>() {
                    @Override
                    public Integer answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        LocalDateTime today = (LocalDateTime) args[0];
                        LocalDateTime dateToCheck = (LocalDateTime) args[1];
                        return (int) ChronoUnit.DAYS.between(today.toLocalDate(), dateToCheck.toLocalDate());
                    }
                }
        );
    }

    @DataProvider
    public Object[][] restFrist() {
        LocalDateTime now = LocalDateTime.now();
        return new Object[][] { { now, now.plusDays(7), 3 }, { now, now.plusDays(10), 0 }, { now, now.plusDays(1), 9 }, };
    }

    @Test(dataProvider = "restFrist")
    public void shouldCalculateRestFrist(LocalDateTime tamDate, LocalDateTime now, int expectedRestFrist) {
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldung();
        tam.setVersandZeitstempel(Date.from(tamDate.atZone(ZoneId.systemDefault()).toInstant()));
        assertEquals(witaUsertaskService.getRestFristInTagen(now, tam), expectedRestFrist);
    }

    @Test(dataProvider = "getWitaVersions")
    public void restFristShouldBeWrittenOnTamVorgang(WitaCdmVersion version) {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(version);
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().build();
        tam.setVersandZeitstempel(new Date());
        WitaCBVorgang cbVorgang = createWitaCBVorgang(GeschaeftsfallTyp.BEREITSTELLUNG, "1234", null);
        setupCommonWorkflowService();
        setupTaskDao(createTaskDaoDataPair(cbVorgang, tam));

        List<TamVorgang> tamTasks = witaUsertaskService.findOpenTamTasksWithWiedervorlageWithoutTKGTams();

        assertThat(tamTasks, hasSize(1));
        assertTrue(Iterables.getOnlyElement(tamTasks).getRestFristInTagen() > 0);
    }

    private Pair<WitaCBVorgang, TerminAnforderungsMeldung> createTaskDaoDataPair(WitaCBVorgang cbVorgang, TerminAnforderungsMeldung tam)
    {
        return new Pair<>(cbVorgang, tam);
    }

    @Test(dataProvider = "getWitaVersions")
    public void findTamTasks(WitaCdmVersion version) {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(version);
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().build();
        tam.setVersandZeitstempel(new Date());
        WitaCBVorgang cbVorgang = createWitaCBVorgang(GeschaeftsfallTyp.BEREITSTELLUNG, "1234", null);
        Task taskMock = mock(Task.class);
        when(taskMock.getAssignee()).thenReturn("UserName");


        TerminAnforderungsMeldung tkgTam = new TerminAnforderungsMeldungBuilder().build();
        tkgTam.setVersandZeitstempel(new Date());
        WitaCBVorgang tkgCbVorgang = createWitaCBVorgang(GeschaeftsfallTyp.PROVIDERWECHSEL, "5678", null);
        when(taskMock.getAssignee()).thenReturn("UserName");

        Pair<WitaCBVorgang, TerminAnforderungsMeldung> p1 = createTaskDaoDataPair(cbVorgang, tam);
        Pair<WitaCBVorgang, TerminAnforderungsMeldung> p2 = createTaskDaoDataPair(tkgCbVorgang, tkgTam);
        List<Pair<WitaCBVorgang, TerminAnforderungsMeldung>> data = new ArrayList<>();
        data.add(p1);
        data.add(p2);
        setupTaskDao(data);

        List<TamVorgang> tamTasks = witaUsertaskService.findOpenTamTasksWithWiedervorlageWithoutTKGTams();
        if (version.isGreaterOrEqualThan(WitaCdmVersion.V2)) {
            assertThat(tamTasks, hasSize(1));
            TamVorgang tamVorgang = tamTasks.get(0);
            Assertions.assertThat(tamVorgang.getCarrierRefNr()).isEqualTo("1234");
        }
        else {
            assertThat(tamTasks, hasSize(2));
            TamVorgang found0 = tamTasks.get(0);
            TamVorgang found1 = tamTasks.get(1);
            Assertions.assertThat(found0.getCarrierRefNr()).isEqualTo("1234");
            Assertions.assertThat(found1.getCarrierRefNr()).isEqualTo("5678");

        }
    }

    @DataProvider
    private Object[][] getWitaVersions()
    {
        return new WitaCdmVersion[][] {
                {WitaCdmVersion.V1}, {WitaCdmVersion.V2}
        };
    }

    @Test(dataProvider = "getWitaVersions")
    public void thatTamTasksDoesNotContainTkgTams(WitaCdmVersion version)
    {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(version);

        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().build();
        tam.setVersandZeitstempel(new Date());
        WitaCBVorgang cbVorgang = createWitaCBVorgang(GeschaeftsfallTyp.BEREITSTELLUNG, "1234", null);
        Task taskMock = mock(Task.class);
        when(taskMock.getAssignee()).thenReturn("UserName");


        TerminAnforderungsMeldung tkgTam = new TerminAnforderungsMeldungBuilder().build();
        tkgTam.setVersandZeitstempel(new Date());
        WitaCBVorgang tkgCbVorgang = createWitaCBVorgang(GeschaeftsfallTyp.PROVIDERWECHSEL, "4567", null);
        when(taskMock.getAssignee()).thenReturn("UserName");

        Pair<WitaCBVorgang, TerminAnforderungsMeldung> p1 = createTaskDaoDataPair(cbVorgang, tam);
        Pair<WitaCBVorgang, TerminAnforderungsMeldung> p2 = createTaskDaoDataPair(tkgCbVorgang, tkgTam);
        List<Pair<WitaCBVorgang, TerminAnforderungsMeldung>> data = new ArrayList<>();
        data.add(p1);
        data.add(p2);
        setupTaskDao(data);

        List<TamVorgang> tamTasks = witaUsertaskService.findOpenTamTasksWithWiedervorlageWithoutTKGTams();
        if (version.isGreaterOrEqualThan(WitaCdmVersion.V2)) {
            assertThat(tamTasks, hasSize(1));
            TamVorgang tamVorgang = tamTasks.get(0);
            Assertions.assertThat(tamVorgang.getCarrierRefNr()).isEqualTo("1234");
        }
        else {
            assertThat(tamTasks, hasSize(2));
            TamVorgang found0 = tamTasks.get(0);
            TamVorgang found1 = tamTasks.get(1);
            Assertions.assertThat(found0.getCarrierRefNr()).isEqualTo("1234");
            Assertions.assertThat(found1.getCarrierRefNr()).isEqualTo("4567");
        }
    }

    @Test
    public void findTkgTamTasksForProviderWechsel() {
        doFindTkgTamTask(GeschaeftsfallTyp.PROVIDERWECHSEL);
    }

    @Test
    public void findTkgTamTasksForVerbundleistung() {
        doFindTkgTamTask(GeschaeftsfallTyp.VERBUNDLEISTUNG);
    }

    private void doFindTkgTamTask(GeschaeftsfallTyp geschaeftsfallTyp)
    {
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().build();
        tam.setVersandZeitstempel(new Date());
        WitaCBVorgang cbVorgang = createWitaCBVorgang(geschaeftsfallTyp, "1234", null);
        Task taskMock = mock(Task.class);
        when(taskMock.getAssignee()).thenReturn("UserName");
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.V1);
        setupTaskDao(createTaskDaoDataPair(cbVorgang, tam));

        List<TamVorgang> tamTasks = witaUsertaskService.findOpenTKGTamTasksWithWiedervorlage();
        Assertions.assertThat(tamTasks).hasSize(1);
    }

    @Test
    public void thatTamWithGfNeuAndWithoutVorabstimmungIsNotATkgTamTask() {
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().build();
        tam.setVersandZeitstempel(new Date());
        WitaCBVorgang cbVorgang = createWitaCBVorgang(GeschaeftsfallTyp.BEREITSTELLUNG, "1234", null);
        Task taskMock = mock(Task.class);
        when(taskMock.getAssignee()).thenReturn("UserName");
        setupTaskDao(createTaskDaoDataPair(cbVorgang, tam));

        List<TamVorgang> tamTasks = witaUsertaskService.findOpenTKGTamTasksWithWiedervorlage();
        Assertions.assertThat(tamTasks).isEmpty();
    }

    @Test
    public void thatTamWithGfNeuAndWithVorabstimmungIsTkgTamTask() {
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().build();
        tam.setVersandZeitstempel(new Date());
        WitaCBVorgang cbVorgang = createDefaultWitaCBVorgangWithBereitstellung();
        Task taskMock = mock(Task.class);
        when(taskMock.getAssignee()).thenReturn("UserName");
        setupTaskDao(createTaskDaoDataPair(cbVorgang, tam));

        List<TamVorgang> tamTasks = witaUsertaskService.findOpenTKGTamTasksWithWiedervorlage();
        Assertions.assertThat(tamTasks).hasSize(1);
    }

    private WitaCBVorgang createDefaultWitaCBVorgangWithBereitstellung()
    {
        return createWitaCBVorgang(GeschaeftsfallTyp.BEREITSTELLUNG, "1234", "vorabstimmung");
    }

    private WitaCBVorgang createWitaCBVorgang(GeschaeftsfallTyp geschaeftsfallTyp, String carrierRefNr, String vorabstimmungsId)
    {
        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setWitaGeschaeftsfallTyp(geschaeftsfallTyp);
        cbVorgang.setVorabstimmungsId(vorabstimmungsId);
        cbVorgang.setCarrierRefNr(carrierRefNr);
        return cbVorgang;
    }

    @Test(dataProvider = "getWitaVersions")
    public void testThatTamIsNotFilteredByTKTams(WitaCdmVersion version) {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(version);

        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().build();
        tam.setVersandZeitstempel(new Date());
        WitaCBVorgang cbVorgang = createWitaCBVorgang(GeschaeftsfallTyp.BEREITSTELLUNG, "1234", null);
        Task taskMock = mock(Task.class);
        when(taskMock.getAssignee()).thenReturn("UserName");
        setupTaskDao(createTaskDaoDataPair(cbVorgang, tam));
        List<TamVorgang> tamTasks = witaUsertaskService.findOpenTamTasksWithWiedervorlageWithoutTKGTams();
        if (version.isGreaterOrEqualThan(WitaCdmVersion.V2)) {
            assertThat(tamTasks, hasSize(1));
        }
        else {
            assertThat(tamTasks, hasSize(1));
        }
    }

    @Test
    public void findKueDtUserTaskTest() throws FindException {
        ErledigtMeldung erlm = new ErledigtMeldungBuilder().build();
        erlm.setVersandZeitstempel(new Date());
        Set<Long> cbIds = Sets.newHashSet(1L, 2L);
        String klaerfallBemerkung = "klaerfall bemerkung";

        kueDtUserTask = mock(KueDtUserTask.class);
        when(kueDtUserTask.getExterneAuftragsnummer()).thenReturn("123");
        when(kueDtUserTask.getCbIds()).thenReturn(cbIds);
        Task taskMock = mock(Task.class);
        when(taskMock.getAssignee()).thenReturn("UserName");
        when(auftragService.findAuftragTechnikByAuftragIdTx(any(Long.class))).thenReturn(new AuftragTechnik());
        when(niederlassungService.findNiederlassung(any(Long.class))).thenReturn(new Niederlassung());
        when(physikService.findVerbindungsBezeichnungById(any(Long.class))).thenReturn(new VerbindungsBezeichnung());
        when(witaTalOrderService.getKlaerfallBemerkungen(cbIds)).thenReturn(klaerfallBemerkung);
        setupKueDtTaskDao();
        List<AbgebendeLeitungenVorgang> findKueDtTasks = witaUsertaskService.findOpenAbgebendeLeitungenTasksWithWiedervorlage();
        assertThat(findKueDtTasks, hasSize(1));
        AbgebendeLeitungenVorgang result = findKueDtTasks.iterator().next();
        Assert.assertEquals(result.isKlaerfall(), Boolean.TRUE);
        Assert.assertEquals(result.getKlaerfallBemerkung(), klaerfallBemerkung);
    }

    @Test
    public void claimTamUserTask() throws StoreException {
        TamUserTask tamUserTask = new TamUserTask();
        tamUserTask.setTamBearbeitungsStatus(TamBearbeitungsStatus.OFFEN);

        AKUser user = new AKUserBuilder().withRandomId().withName("Flying dutchman").withLoginName("Flying_dutchman")
                .setPersist(false).get();

        UserTask userTask = witaUsertaskService.claimUserTask(tamUserTask, user);

        Mockito.verify(taskDao).store(tamUserTask);

        assertEquals(tamUserTask.getTamBearbeitungsStatus(), TamBearbeitungsStatus.IN_BEARBEITUNG);
        assertEquals(userTask.getUserId(), user.getId());
        assertNotNull(userTask.getLetzteAenderung());
    }

    @Test(expectedExceptions = StoreException.class)
    public void claimClaimedUserTask() throws StoreException {
        TamUserTask tamUserTask = new TamUserTask();
        AKUser user = new AKUserBuilder().withRandomId().withName("Richard").withLoginName("Richard").setPersist(false)
                .get();
        tamUserTask.setBearbeiter(user);
        witaUsertaskService.claimUserTask(tamUserTask, new AKUser());
    }

    @Test
    public void unclaimTamUserTask() throws StoreException {
        TamUserTask tamUserTask = new TamUserTask();
        tamUserTask.setTamBearbeitungsStatus(TamBearbeitungsStatus.IN_BEARBEITUNG);

        witaUsertaskService.claimUserTask(tamUserTask, null);
        Mockito.verify(taskDao).store(tamUserTask);

        assertEquals(TamBearbeitungsStatus.OFFEN, tamUserTask.getTamBearbeitungsStatus());
    }

    @Test
    public void checkUserTaskNotClaimedByOtherUserNoTask() {
        AKUser user = new AKUser();
        user.setLoginName("Richard");

        witaUsertaskService.checkUserTaskNotClaimedByOtherUser(null, user);
    }

    @Test(expectedExceptions = WitaBpmException.class)
    public void checkUserTaskNotClaimedByOtherUserNoUser() {
        TamUserTask tamUserTask = new TamUserTask();
        AKUser user = new AKUserBuilder().withRandomId().withName("Richard").withLoginName("Richard").setPersist(false)
                .get();
        tamUserTask.setBearbeiter(user);
        tamUserTask.setStatus(UserTaskStatus.OFFEN);

        witaUsertaskService.checkUserTaskNotClaimedByOtherUser(tamUserTask, null);
    }

    @Test
    public void checkUserTaskNotClaimedByOtherUser() {
        TamUserTask tamUserTask = new TamUserTask();
        AKUser userX = new AKUserBuilder().withRandomId().withName("Richard").withLoginName("Richard")
                .setPersist(false).get();
        tamUserTask.setBearbeiter(userX);
        tamUserTask.setStatus(UserTaskStatus.OFFEN);

        AKUser user = new AKUser();
        user.setId(userX.getId());
        user.setLoginName("Richard");

        witaUsertaskService.checkUserTaskNotClaimedByOtherUser(tamUserTask, user);
    }

    @Test(expectedExceptions = WitaBpmException.class)
    public void checkUserTaskClaimedByOtherUserOnOpenTask() {
        TamUserTask tamUserTask = new TamUserTask();
        AKUser userX = new AKUserBuilder().withRandomId().withName("Richard").withLoginName("Richard")
                .setPersist(false).get();
        tamUserTask.setBearbeiter(userX);
        tamUserTask.setStatus(UserTaskStatus.OFFEN);

        AKUser user = new AKUser();
        user.setLoginName("Flying dutchman");

        witaUsertaskService.checkUserTaskNotClaimedByOtherUser(tamUserTask, user);
    }

    @Test
    public void checkUserTaskClaimedByOtherUserOnClosedTask() {
        TamUserTask tamUserTask = new TamUserTask();
        AKUser userX = new AKUserBuilder().withRandomId().withName("Richard").withLoginName("Richard")
                .setPersist(false).get();
        tamUserTask.setBearbeiter(userX);
        tamUserTask.setStatus(UserTaskStatus.GESCHLOSSEN);

        AKUser user = new AKUser();
        user.setLoginName("Flying dutchman");

        witaUsertaskService.checkUserTaskNotClaimedByOtherUser(tamUserTask, user);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, expectedExceptionsMessageRegExp = ".*Task.*schon.*geschlossen.*")
    public void cannotCloseClosedUserTask() throws Exception {
        TamUserTask tamUserTask = new TamUserTask();
        tamUserTask.setStatus(UserTaskStatus.GESCHLOSSEN);

        witaUsertaskService.closeUserTask(tamUserTask, new AKUser());
    }

    @Test
    public void closeUserTask() throws StoreException {
        TamUserTask tamUserTask = new TamUserTask();
        witaUsertaskService.closeUserTask(tamUserTask, new AKUser());
        Mockito.verify(taskDao).store(tamUserTask);
        assertEquals(UserTaskStatus.GESCHLOSSEN, tamUserTask.getStatus());
    }

    @Test
    public void testResetUserTask() {
        TamUserTask tamUserTask = new TamUserTask();
        tamUserTask.setStatus(UserTaskStatus.GESCHLOSSEN);
        tamUserTask.setStatusLast(UserTaskStatus.OFFEN);

        witaUsertaskService.resetUserTask(tamUserTask);

        assertEquals(tamUserTask.getStatus(), UserTaskStatus.OFFEN);
        assertNull(tamUserTask.getStatusLast());

        Mockito.verify(taskDao).store(tamUserTask);
    }

    @Test
    public void testCloseAndResetUserTask() throws StoreException {
        TamUserTask tamUserTask = new TamUserTask();
        witaUsertaskService.closeUserTask(tamUserTask);
        witaUsertaskService.resetUserTask(tamUserTask);
        Mockito.verify(taskDao, times(2)).store(tamUserTask);
        assertEquals(UserTaskStatus.OFFEN, tamUserTask.getStatus());
    }

    @Test(expectedExceptions = WitaBpmException.class)
    public void closeUserTaskClaimedByOtherUser() throws StoreException {
        TamUserTask tamUserTask = new TamUserTask();
        AKUser user = new AKUserBuilder().withRandomId().withName("anderer Bearbeiter")
                .withLoginName("anderer_Bearbeiter").setPersist(false).get();
        tamUserTask.setBearbeiter(user);
        witaUsertaskService.closeUserTask(tamUserTask, new AKUser());
    }

    private void setupTaskDao(Pair<WitaCBVorgang, TerminAnforderungsMeldung> data) {
        List<Pair<WitaCBVorgang, TerminAnforderungsMeldung>> dataList = new ArrayList<>();
        dataList.add(data);
        setupTaskDao(dataList);
    }

    private void setupTaskDao(List<Pair<WitaCBVorgang, TerminAnforderungsMeldung>> data) {
        List<TamVorgang> meldungen = data.stream()
                .map(tam->createTamVorgang(tam.getFirst(), tam.getSecond()))
                .collect(Collectors.toList());
        when(taskDao.findOpenTamTasksWithWiedervorlage()).thenReturn(meldungen);
    }

    private TamVorgang createTamVorgang(WitaCBVorgang cbVorgang, TerminAnforderungsMeldung tam)
    {
        return new TamVorgang(cbVorgang, tam, 1, "Hinterdupfing", 1234657L, "444/234234/233", false, false);
    }

    private void setupKueDtTaskDao() {
        AbgebendeLeitungenVorgang kueDtVorgang = new AbgebendeLeitungenVorgang(kueDtUserTask);
        when(taskDao.findOpenAbgebendeLeitungenTasksWithWiedervorlage()).thenReturn(ImmutableList.of(kueDtVorgang));
    }

    private void setupCommonWorkflowService() {
        Task mockTask = mock(Task.class);
        when(mockTask.getAssignee()).thenReturn("Sepp");
    }

    @Test
    public void sortOpenAbgebendeLeitungenTasks() {
        AbgebendeLeitungenVorgang akmPvVorgang = new AbgebendeLeitungenVorgang(new AkmPvUserTask());
        AbgebendeLeitungenVorgang kueDtVorgang = new AbgebendeLeitungenVorgang(new KueDtUserTask());
        ImmutableList<AbgebendeLeitungenVorgang> list = ImmutableList.of(akmPvVorgang, kueDtVorgang);

        List<AbgebendeLeitungenVorgang> sorted = witaUsertaskService.sortOpenAbgebendeLeitungenTasks(list);

        assertEquals(sorted.iterator().next(), kueDtVorgang);
    }

    @Test
    public void sortOpenAbgebendeLeitungenTasksByAntwortFrist() {
        AbgebendeLeitungenVorgang vorgang1 = new AbgebendeLeitungenVorgang(new AkmPvUserTask());
        ((AkmPvUserTask) vorgang1.getUserTask()).setAntwortFrist(null);

        AbgebendeLeitungenVorgang vorgang2 = new AbgebendeLeitungenVorgang(new AkmPvUserTask());
        ((AkmPvUserTask) vorgang2.getUserTask()).setAntwortFrist(LocalDate.now().minusDays(10));

        AbgebendeLeitungenVorgang vorgang3 = new AbgebendeLeitungenVorgang(new AkmPvUserTask());
        ((AkmPvUserTask) vorgang3.getUserTask()).setAntwortFrist(LocalDate.now());

        ImmutableList<AbgebendeLeitungenVorgang> list = ImmutableList.of(vorgang1, vorgang2, vorgang3);

        List<AbgebendeLeitungenVorgang> sorted = witaUsertaskService.sortOpenAbgebendeLeitungenTasks(list);

        Iterator<AbgebendeLeitungenVorgang> iterator = sorted.iterator();
        assertEquals(iterator.next(), vorgang2);
        assertEquals(iterator.next(), vorgang3);
        assertEquals(iterator.next(), vorgang1);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void updateCarrierBestellungAfterAbmShouldAddMessageWithActiveAuftraege() throws Exception {
        AkmPvUserTask akmPvUserTask = createAkmPvUserTask(AkmPvStatus.ABM_PV_EMPFANGEN);

        AuftragDaten aktiveAuftragDaten = buildAuftragDaten(AuftragStatus.IN_BETRIEB);
        AuftragDaten gekuendigteAuftragDaten = buildAuftragDaten(AuftragStatus.KUENDIGUNG_ERFASSEN);

        Carrierbestellung cb = buildCarrierbestellung(aktiveAuftragDaten, gekuendigteAuftragDaten);

        witaUsertaskService.updateCarrierbestellung(akmPvUserTask, cb.getId());

        String benachrichtigung = akmPvUserTask.getBenachrichtigung();
        assertThat(benachrichtigung, containsString(aktiveAuftragDaten.getAuftragId().toString()));
        assertThat(benachrichtigung, not(containsString(gekuendigteAuftragDaten.getAuftragId().toString())));
    }

    @Test
    public void updateCarrierBestellungAfterAbbmShouldAddMessageWithGekuendigteAuftraege() throws Exception {
        AkmPvUserTask akmPvUserTask = createAkmPvUserTask(AkmPvStatus.ABBM_PV_EMPFANGEN);

        AuftragDaten aktiveAuftragDaten = buildAuftragDaten(AuftragStatus.IN_BETRIEB);
        AuftragDaten gekuendigteAuftragDaten = buildAuftragDaten(AuftragStatus.KUENDIGUNG_ERFASSEN);
        Carrierbestellung cb = buildCarrierbestellung(aktiveAuftragDaten, gekuendigteAuftragDaten);

        witaUsertaskService.updateCarrierbestellung(akmPvUserTask, cb.getId());

        String benachrichtigung = akmPvUserTask.getBenachrichtigung();
        assertThat(benachrichtigung, not(containsString(aktiveAuftragDaten.getAuftragId().toString())));
        assertThat(benachrichtigung, containsString(gekuendigteAuftragDaten.getAuftragId().toString()));
    }

    @Test
    public void updateCarrierBestellungAfterAbmShouldNotAddMessageWithoutActiveAuftraege() throws Exception {
        AkmPvUserTask akmPvUserTask = createAkmPvUserTask(AkmPvStatus.ABM_PV_EMPFANGEN);

        AuftragDaten gekuendigteAuftragDaten1 = buildAuftragDaten(AuftragStatus.KUENDIGUNG_TECHN_REAL);
        AuftragDaten gekuendigteAuftragDaten2 = buildAuftragDaten(AuftragStatus.KUENDIGUNG_ERFASSEN);
        Carrierbestellung cb = buildCarrierbestellung(gekuendigteAuftragDaten1, gekuendigteAuftragDaten2);

        witaUsertaskService.updateCarrierbestellung(akmPvUserTask, cb.getId());

        assertNull(akmPvUserTask.getBenachrichtigung());
    }

    @Test
    public void updateCarrierBestellungAfterAbmShouldNotAddMessageWithoutGekuendigteAuftraege() throws Exception {
        AkmPvUserTask akmPvUserTask = createAkmPvUserTask(AkmPvStatus.ABBM_PV_EMPFANGEN);

        AuftragDaten aktiveAuftragDaten1 = buildAuftragDaten(AuftragStatus.IN_BETRIEB);
        AuftragDaten aktiveAuftragDaten2 = buildAuftragDaten(AuftragStatus.AENDERUNG);
        Carrierbestellung cb = buildCarrierbestellung(aktiveAuftragDaten1, aktiveAuftragDaten2);

        witaUsertaskService.updateCarrierbestellung(akmPvUserTask, cb.getId());

        assertNull(akmPvUserTask.getBenachrichtigung());
    }

    @SuppressWarnings("unchecked")
    @DataProvider
    public Object[][] dataProviderCreateUserTask2AuftragDatenForTask() {
        AuftragDaten inBetriebAuftragDaten = buildAuftragDaten(AuftragStatus.IN_BETRIEB);
        AuftragDaten aenderungAuftragDaten = buildAuftragDaten(AuftragStatus.AENDERUNG);
        AuftragDaten inKuendigungAuftragDaten = buildAuftragDaten(AuftragStatus.KUENDIGUNG_TECHN_REAL);
        AuftragDaten gekuendigtAuftragDaten = buildAuftragDaten(AuftragStatus.AUFTRAG_GEKUENDIGT);
        AuftragDaten stornoAuftragDaten = buildAuftragDaten(AuftragStatus.STORNO);
        AuftragDaten absageAuftragDaten = buildAuftragDaten(AuftragStatus.ABSAGE);

        Carrierbestellung cb1 = new CarrierbestellungBuilder().withRandomId().build();
        Carrierbestellung cb2 = new CarrierbestellungBuilder().withRandomId().build();

        // @formatter:off
        return new Object[][] {
                { map(Pair.create(cb1, Arrays.asList(inBetriebAuftragDaten))),
                    set(task(inBetriebAuftragDaten, cb1)) },

                { map(Pair.create(cb1, Arrays.asList(inBetriebAuftragDaten, inBetriebAuftragDaten))),
                    set(task(inBetriebAuftragDaten, cb1), task(inBetriebAuftragDaten, cb1)) },

                { map(Pair.create(cb1, Arrays.asList(inBetriebAuftragDaten)),
                        Pair.create(cb2, Arrays.asList(aenderungAuftragDaten))),
                    set(task(inBetriebAuftragDaten, cb1), task(aenderungAuftragDaten, cb2)) },

                { map(Pair.create(cb1, Arrays.asList(inBetriebAuftragDaten)),
                        Pair.create(cb2, Arrays.asList(aenderungAuftragDaten))),
                    set(task(inBetriebAuftragDaten, cb1), task(aenderungAuftragDaten, cb2)) },

                { map(Pair.create(cb1, Arrays.asList(inBetriebAuftragDaten, inKuendigungAuftragDaten)),
                        Pair.create(cb2, Arrays.asList(aenderungAuftragDaten))),
                    set(task(inBetriebAuftragDaten, cb1), task(inKuendigungAuftragDaten, cb1),
                        task(aenderungAuftragDaten, cb2)) },

                // wenn nur gekündigte/abgesagte/stornierte, dann alle Aufträge setzen
                { map(Pair.create(cb1, Arrays.asList(gekuendigtAuftragDaten))),
                    set(task(gekuendigtAuftragDaten, cb1)) },

                { map(Pair.create(cb1, Arrays.asList(gekuendigtAuftragDaten)),
                        Pair.create(cb2, Arrays.asList(gekuendigtAuftragDaten))),
                    set(task(gekuendigtAuftragDaten, cb1), task(gekuendigtAuftragDaten, cb2)) },

                { map(Pair.create(cb1, Arrays.asList(gekuendigtAuftragDaten, stornoAuftragDaten))),
                    set(task(gekuendigtAuftragDaten, cb1), task(stornoAuftragDaten, cb1)) },


                // filter gekündigte/abgesagte/stornierte Aufträge
                { map(Pair.create(cb1, Arrays.asList(inBetriebAuftragDaten, gekuendigtAuftragDaten))),
                    set(task(inBetriebAuftragDaten, cb1)) },

                { map(Pair.create(cb1, Arrays.asList(inBetriebAuftragDaten, gekuendigtAuftragDaten, stornoAuftragDaten))),
                    set(task(inBetriebAuftragDaten, cb1)) },

                { map(Pair.create(cb1, Arrays.asList(inBetriebAuftragDaten, gekuendigtAuftragDaten)),
                        Pair.create(cb2, Arrays.asList(absageAuftragDaten))),
                    set(task(inBetriebAuftragDaten, cb1)) },
        };
        // @formatter:on
    }

    @Test
    public void testCreateUserTask2AuftragDatenFor() throws Exception {
        Long auftragId = 123890L;
        Long cbId = 1357L;
        AuftragDaten auftragDaten = (new AuftragDatenBuilder()).withAuftragId(auftragId).build();
        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten);

        AkmPvUserTask akmPvUserTask = createAkmPvUserTask(AkmPvStatus.AKM_PV_EMPFANGEN);

        witaUsertaskService.createUserTask2AuftragDatenFor(akmPvUserTask, Pair.create(auftragId, cbId));

        Set<UserTask2AuftragDaten> results = akmPvUserTask.getUserTaskAuftragDaten();
        assertNotNull(results);
        assertThat(results, hasSize(1));
        UserTask2AuftragDaten result = results.iterator().next();
        assertNotNull(result);
        assertThat(result.getAuftragId(), equalTo(auftragId));
        assertThat(result.getCbId(), equalTo(cbId));
    }

    @Test(dataProvider = "dataProviderCreateUserTask2AuftragDatenForTask")
    public void testCreateUserTask2AuftragDatenForTask(Map<Carrierbestellung, List<AuftragDaten>> ad2cbs,
            Set<UserTask2AuftragDaten> expectedUt2Ad) throws Exception {
        AkmPvUserTask akmPvUserTask = createAkmPvUserTask(AkmPvStatus.AKM_PV_EMPFANGEN);

        when(carrierService.findCBsByNotExactVertragsnummer(anyString())).thenReturn(
                new ArrayList<>(ad2cbs.keySet()));
        for (Map.Entry<Carrierbestellung, List<AuftragDaten>> ad2cb : ad2cbs.entrySet()) {
            when(carrierService.findAuftragDaten4CB(ad2cb.getKey().getId())).thenReturn(ad2cb.getValue());
        }

        Set<UserTask2AuftragDaten> result = witaUsertaskService.createUserTask2AuftragDatenForTask(akmPvUserTask);
        for (UserTask2AuftragDaten userTask2AuftragDaten : result) {
            userTask2AuftragDaten.setId(1L); // required for comparison via equals !!!
        }
        assertEqualsNoOrder(result.toArray(), expectedUt2Ad.toArray());
    }

    @Test
    public void testCreateKueDtUserTask() {
        ErledigtMeldung erlm = new ErledigtMeldungBuilder().build();
        KueDtUserTask kuedt = witaUsertaskService.createKueDtUserTask(erlm);
        assertEquals(kuedt.getExterneAuftragsnummer(), erlm.getExterneAuftragsnummer());
        assertEquals(kuedt.getVertragsNummer(), erlm.getVertragsNummer());
        assertEquals(kuedt.getEmpfangsDatum(), erlm.getVersandZeitstempel());
        assertEquals(kuedt.getKuendigungsDatum(), erlm.getErledigungstermin());
    }

    @Test
    public void testUpdateAkmPvUserTask() {
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder().build();
        AkmPvUserTask akmPvTask = witaUsertaskService.updateAkmPvUserTask(akmPv);
        assertEquals(akmPv.getExterneAuftragsnummer(), akmPvTask.getExterneAuftragsnummer());
        assertEquals(akmPv.getVertragsNummer(), akmPvTask.getVertragsNummer());
        assertEquals(akmPv.getVersandZeitstempel(), akmPvTask.getEmpfangsDatum());
        assertTrue(akmPv.getAufnehmenderProvider().getProvidernameAufnehmend()
                .equals(akmPvTask.getAufnehmenderProvider()));
        assertEquals(akmPv.getAufnehmenderProvider().getUebernahmeDatumGeplant(),
                akmPvTask.getGeplantesKuendigungsDatum());
        assertTrue(akmPv.getAufnehmenderProvider().getAntwortFrist().equals(akmPvTask.getAntwortFrist()));
    }

    @Test
    public void testGetAutomaticAnswerForAkmPvWithoutVorabstimmung() {
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder().build();
        Map<WitaTaskVariables, Object> expectedVariablesObjectMap = new HashMap<>();
        doReturn(expectedVariablesObjectMap).when(witaUsertaskService).getAutomaticAnswerForAkmPvFromWita(akmPv);
        Map<WitaTaskVariables, Object> variablesObjectMap = witaUsertaskService.getAutomaticAnswerForAkmPv(akmPv);
        verify(witaUsertaskService).getAutomaticAnswerForAkmPvFromWita(akmPv);
        assertEquals(variablesObjectMap, expectedVariablesObjectMap);
    }

    @Test
    public void testGetVorabstimmungAbgebendByAuftragId() throws FindException {
        Long auftragId = 535353L;
        Long cbId = 18376433L;

        Endstelle endstelle = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();
        when(endstellenService.findEndstelle4CarrierbestellungAndAuftrag(cbId, auftragId)).thenReturn(endstelle);

        VorabstimmungAbgebend vorabstimmungAbgebend = new VorabstimmungAbgebendBuilder().withAuftragId(auftragId)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).withCarrierDtag().withRueckmeldung(true)
                .withBemerkung("OK").build();

        when(witaVorabstimmungService.findVorabstimmungAbgebend(endstelle.getEndstelleTyp(), auftragId)).thenReturn(
                vorabstimmungAbgebend);
        VorabstimmungAbgebend vorabstimmungAbgebendResult = witaUsertaskService
                .getVorabstimmungAbgebendByAuftragId(auftragId, cbId);
        assertEquals(vorabstimmungAbgebend, vorabstimmungAbgebendResult);
    }

    private <K, V> Map<K, List<V>> map(Pair<K, List<V>>... pairs) {
        Map<K, List<V>> result = new HashMap<>();
        for (Pair<K, List<V>> pair : pairs) {
            result.put(pair.getFirst(), pair.getSecond());
        }
        return result;
    }

    private Carrierbestellung buildCarrierbestellung(AuftragDaten... auftragDaten) throws FindException {
        Carrierbestellung cb = new CarrierbestellungBuilder().withRandomId().build();
        when(carrierService.findAuftragDaten4CB(cb.getId())).thenReturn(ImmutableList.copyOf(auftragDaten));
        when(carrierService.findCB(cb.getId())).thenReturn(cb);
        return cb;
    }

    private AuftragDaten buildAuftragDaten(Long statusId) {
        return new AuftragDatenBuilder().withRandomId().withRandomAuftragId().withStatusId(statusId).build();
    }

    private AkmPvUserTask createAkmPvUserTask(AkmPvStatus status) {
        AkmPvUserTask akmPvUserTask = new AkmPvUserTask();
        akmPvUserTask.setEmpfangsDatum(new Date());
        akmPvUserTask.setKuendigungsDatum(LocalDate.now());
        akmPvUserTask.changeAkmPvStatus(status);
        return akmPvUserTask;
    }

    private UserTask2AuftragDaten task(AuftragDaten ad, Carrierbestellung cb) {
        UserTask2AuftragDaten result = new UserTask2AuftragDaten();
        result.setId(1L); // required for comparison via equals!!!
        result.setAuftragId(ad.getAuftragId());
        result.setCbId(cb.getId());
        return result;
    }

}
