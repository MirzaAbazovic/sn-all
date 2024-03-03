/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2009 10:25:50
 */
package de.augustakom.hurrican.service.cc.impl;

import static com.google.common.collect.ImmutableList.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.isNull;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.CPSTransactionDAO;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;

@Test(groups = { BaseTest.UNIT })
public class CPSServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private CPSServiceImpl sut;

    @Mock
    private EndstellenService endstellenService;
    @Mock
    private ProduktService produktService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private AccountService accountService;
    @Mock
    private ReferenceService referenceService;

    @Mock
    BAService baService;

    @Mock
    CPSTransactionDAO cpsTxDao;

    final AuftragDaten ausTaifunUebernommen, inBetrieb, inKuendigung;
    final Verlauf baGestern, baHeute, baMorgen;
    final IntAccount account;
    final AuftragTechnik technik;
    final Produkt produkt;

    public CPSServiceImplTest() {
        produkt = new ProduktBuilder()
                .withCpsProvisioning(Boolean.TRUE)
                .withAutoHvtZuordnung(Boolean.FALSE)
                .build();

        account = new IntAccountBuilder().build();
        AuftragTechnikBuilder technikBuilder = new AuftragTechnikBuilder()
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .withIntAccountId(account.getId())
                .withPreventCPSProvisioning(Boolean.FALSE);

        technik = technikBuilder.build();

        ausTaifunUebernommen = new AuftragDatenBuilder()
                .withInbetriebnahme(null)
                .withKuendigung(null)
                .withStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN)
                .setPersist(false).build();
        inBetrieb = new AuftragDatenBuilder()
                .withInbetriebnahme(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withKuendigung(null)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withAuftragId(technik.getAuftragId())
                .setPersist(false).build();
        inKuendigung = new AuftragDatenBuilder()
                .withInbetriebnahme(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withKuendigung(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withStatusId(AuftragStatus.KUENDIGUNG)
                .withAuftragId(3L)
                .setPersist(false).build();

        baGestern = new VerlaufBuilder()
                .withAkt(Boolean.TRUE)
                .withRealisierungstermin(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        baHeute = new VerlaufBuilder()
                .withAkt(Boolean.TRUE)
                .withRealisierungstermin(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        baMorgen = new VerlaufBuilder()
                .withAkt(Boolean.TRUE)
                .withRealisierungstermin(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
    }

    @BeforeMethod
    public void setUp() throws FindException {
        MockitoAnnotations.initMocks(this);
        sut.setDAO(cpsTxDao);
        when(auftragService.findAuftragTechnikByAuftragIdTx(any(Long.class))).thenReturn(technik);
        when(endstellenService.findEndstelle4Auftrag(any(Long.class), any(String.class))).thenReturn(null);
        when(produktService.findProdukt4Auftrag(any(Long.class))).thenReturn(produkt);
    }

    public void testIsHVTAllowed() throws FindException, ServiceNotFoundException {
        boolean result = sut.isHVTAllowed(Long.valueOf(111));
        assertFalse(result, "Test if HVT is allowed returned true - but false expected!");
    }

    @DataProvider(name = "cpsProvisioningAllowed_vp_DP")
    public Object[][] cpsProvisioningAllowed_vp_DP() {
        return new Object[][] {
                { ProduktGruppe.IPSEC, true },
                { ProduktGruppe.MVS_ENTERPRISE, true },
                { ProduktGruppe.MVS_SITE, true },
                { ProduktGruppe.AK_DSLPLUS, false }
        };
    }

    @Test(dataProvider = "cpsProvisioningAllowed_vp_DP")
    public void testIsCPSProvisioningAllowed_virtualProducts(Long produktGruppe, boolean expRes) throws FindException,
            ServiceNotFoundException {
        final Long aId = Long.valueOf(1234123);
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(
                new ProduktBuilder().setPersist(false).withProduktGruppeId(produktGruppe).withCpsProvisioning(true)
                        .build()
        );
        when(auftragService.findAuftragTechnikByAuftragIdTx(anyLong())).thenReturn(
                new AuftragTechnikBuilder().setPersist(false).build());
        when(auftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(
                new AuftragDatenBuilder().setPersist(false).build());
        doReturn(false).when(sut).isHVTAllowed(aId);
        Assert.assertEquals(sut.isCPSProvisioningAllowed(aId, LazyInitMode.noInitialLoad, false, false, true)
                .isProvisioningAllowed(), expRes);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = CPSService.KEIN_AUFTRAG_GEFUNDEN)
    public void getAuftragIdByAuftragNoOrigYieldsKeinAuftragGefunden() throws Exception {
        List<AuftragDaten> list = of();
        when(auftragService.findAuftragDaten4OrderNoOrig(any(Long.class))).thenReturn(list);
        sut.getAuftragIdByAuftragNoOrig(0, LocalDate.now(), false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = CPSService.AUFTRAG_GEFUNDEN_JEDOCH_NICHT_GUELTIG)
    public void getAuftragIdByAuftragNoOrigYieldsAuftragNichtGueltig() throws Exception {
        List<AuftragDaten> list = of(ausTaifunUebernommen);
        when(auftragService.findAuftragDaten4OrderNoOrig(any(Long.class))).thenReturn(list);
        sut.getAuftragIdByAuftragNoOrig(0, LocalDate.now(), false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = CPSService.AUFTRAG_GEFUNDEN_JEDOCH_NICHT_GUELTIG)
    public void getAuftragIdByAuftragNoOrigMitFrueherAlsInKuendigungYieldsAuftragNichtGueltig() throws Exception {
        List<AuftragDaten> list = of(inKuendigung);
        when(auftragService.findAuftragDaten4OrderNoOrig(any(Long.class))).thenReturn(list);
        sut.getAuftragIdByAuftragNoOrig(0, LocalDate.now().minusDays(1), false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = CPSService.AUFTRAG_GEFUNDEN_JEDOCH_NICHT_GUELTIG)
    public void getAuftragIdByAuftragNoOrigFuerSpaeterAlsInKuendigungYieldsAuftragNichtGueltig() throws Exception {
        List<AuftragDaten> list = of(inKuendigung);
        when(auftragService.findAuftragDaten4OrderNoOrig(any(Long.class))).thenReturn(list);
        sut.getAuftragIdByAuftragNoOrig(0, LocalDate.now().plusDays(2), false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = CPSService.AUFTRAG_GEFUNDEN_JEDOCH_NICHT_GUELTIG)
    public void getAuftragIdByAuftragNoOrigFuerFruehlerAlsInBetriebYieldsAuftragNichtGueltig() throws Exception {
        List<AuftragDaten> list = of(inBetrieb);
        when(auftragService.findAuftragDaten4OrderNoOrig(any(Long.class))).thenReturn(list);
        sut.getAuftragIdByAuftragNoOrig(0, LocalDate.now().minusDays(1), false);
    }

    public void getAuftragIdByAuftragNoOrigFuerEindeutig() throws Exception {
        List<AuftragDaten> list = of(inBetrieb);
        when(auftragService.findAuftragDatenByAuftragId(inBetrieb.getAuftragId())).thenReturn(inBetrieb);
        when(auftragService.findAuftragDaten4OrderNoOrig(any(Long.class))).thenReturn(list);
        Long id = sut.getAuftragIdByAuftragNoOrig(0, LocalDate.now(), false);
        assertThat(id, equalTo(inBetrieb.getAuftragId()));
    }

    @DataProvider
    protected Object[][] getAuftragIdByAuftragNoOrigFuerRealisierungVorKuendigungProvider() {
        return new Object[][] {
                { baGestern, AuftragStatus.TECHNISCHE_REALISIERUNG },
                { baHeute, AuftragStatus.TECHNISCHE_REALISIERUNG },
                { baMorgen, AuftragStatus.IN_BETRIEB },
        };
    }

    @Test(dataProvider = "getAuftragIdByAuftragNoOrigFuerRealisierungVorKuendigungProvider")
    public void getAuftragIdByAuftragNoOrigFuerRealisierungVorKuendigung(Verlauf verlauf, Long expectedStatus)
            throws Exception {
        when(baService.findActVerlauf4Auftrag(any(Long.class), eq(false))).thenReturn(verlauf);
        List<AuftragDaten> list = Lists.newLinkedList();
        Long auftragNoOrig = 1234L;
        for (long status : AuftragStatus.VALID_AUFTRAG_STATI) {
            AuftragDaten auftragDaten = new AuftragDatenBuilder()
                    .withInbetriebnahme(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .withKuendigung(null)
                    .withStatusId(status)
                    .withAuftragId(status)
                    .withAuftragNoOrig(auftragNoOrig)
                    .setPersist(false).build();
            list.add(auftragDaten);
            when(auftragService.findAuftragDatenByAuftragId(status)).thenReturn(auftragDaten);
        }
        Collections.shuffle(list);
        when(auftragService.findAuftragDaten4OrderNoOrig(any(Long.class))).thenReturn(list);
        Long id = sut.getAuftragIdByAuftragNoOrig(0, LocalDate.now(), false);
        assertThat(id, equalTo(expectedStatus));
    }

    public void getAuftragIdByRadiusAccount() throws FindException {
        LocalDate when = LocalDate.now();

        when(accountService.findIntAccount(account.getAccount(), IntAccount.LINR_EINWAHLACCOUNT, when))
                .thenReturn(account);
        when(auftragService.findAuftragTechnik4IntAccount(account.getId()))
                .thenReturn(ImmutableList.of(technik));
        when(auftragService.findAuftragDatenByAuftragId(inBetrieb.getAuftragId()))
                .thenReturn(inBetrieb);
        when(auftragService.findAuftragDatenByAuftragIdTx(technik.getAuftragId()))
                .thenReturn(inBetrieb);

        long auftragId = sut.getAuftragIdByRadiusAccount(account.getAccount(), LocalDate.now());

        assertThat(auftragId, equalTo(technik.getAuftragId()));
    }

    @DataProvider
    Object[][] testCheckForValidCpsTXDataProvider() {
        return new Object[][] {
                { CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, Boolean.FALSE },  // create nicht möglich
                { CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE, Boolean.TRUE },   // modify o.k.
                { CPSTransaction.SERVICE_ORDER_TYPE_DELETE_DEVICE, Boolean.TRUE },   // delete o.k.
        };
    }

    @Test(dataProvider = "testCheckForValidCpsTXDataProvider")
    public void testCheckForValidCpsTXServiceOrderType(Long serviceOrderType, boolean expectedResult) throws Exception {

        List<CPSTransactionExt> cpsTransactionExtList = createCpsTransactionExtList();
        doReturn(cpsTransactionExtList).when(sut).findCPSTransaction(any(CPSTransactionExt.class));

        boolean result = sut.isCpsTxServiceOrderTypeExecuteable(1L, serviceOrderType);
        assertThat(result, equalTo(expectedResult));

    }

    private List<CPSTransactionExt> createCpsTransactionExtList() {
        List<CPSTransactionExt> cpsTransactionExtList = Lists.newArrayList();

        CPSTransactionExt cpsTransactionExtCre = new CPSTransactionExt();
        cpsTransactionExtCre.setId(1L);
        cpsTransactionExtCre.setTimestamp(DateTools.minusWorkDays(1));
        cpsTransactionExtCre.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
        cpsTransactionExtList.add(cpsTransactionExtCre);
        return cpsTransactionExtList;
    }

    public void testGetLastCpsTxServiceOrderType4Rack() throws Exception {
        final long rackId = 815L;
        final CPSTransactionExt txFromYesterday = new CPSTransactionExt();
        txFromYesterday.setEstimatedExecTime(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        txFromYesterday.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_TX);
        final CPSTransactionExt txFromToday = new CPSTransactionExt();
        txFromToday.setEstimatedExecTime(new Date());
        txFromToday.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB);
        final Reference reference = new ReferenceBuilder().withRandomId().build();

        when(cpsTxDao.queryByExample(any(CPSTransactionExt.class), eq(CPSTransactionExt.class), eq(new String[] { "id" }), isNull(String[].class)))
                .thenReturn(Lists.newArrayList(txFromToday, txFromToday));
        when(referenceService.findReference(CPSTransaction.TX_STATE_SUCCESS)).thenReturn(reference);

        final long result = sut.getLastCpsTxServiceOrderType4Rack(rackId);

        assertThat(result, equalTo(txFromToday.getServiceOrderType()));
    }


    @DataProvider
    public Object[][] checkIfTxPermitted4OltChildDP() {
        final Long auftragId = 1L;
        final Long auftragNoOrig = 2L;

        final CreateCPSTransactionParameter createSubscriberParam = createMockTxParameter(auftragId, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        final CreateCPSTransactionParameter modifySubscriberParam = createMockTxParameter(auftragId, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB);

        final AuftragDaten auftragDaten = createMockAuftragDaten(auftragNoOrig, auftragId, AuftragStatus.TECHNISCHE_REALISIERUNG, true);

        final HWOltChild hwOltChildDpo = createMockHWOltChild(new Date(), HWRack.RACK_TYPE_DPO);
        final HWOltChild hwOltChildOnt = createMockHWOltChild(new Date(), HWRack.RACK_TYPE_ONT);
        final HWOltChild hwOltChildNotFreigegeben = createMockHWOltChild(null, HWRack.RACK_TYPE_DPO);
        final HWOltChild hwOltChildMdu = createMockHWOltChild(new Date(), HWRack.RACK_TYPE_MDU);

        return new Object[][] {
                {createSubscriberParam, auftragId, auftragDaten, hwOltChildOnt, 0},
                {modifySubscriberParam, auftragId, auftragDaten, hwOltChildDpo, 0},
                {createSubscriberParam, auftragId, auftragDaten, hwOltChildNotFreigegeben, 1}, // OltChild not freigegeben
                {createSubscriberParam, auftragId, auftragDaten, hwOltChildMdu, 0}, // No checks done since this is MDU OltChild (not DPO or ONT)
        };
    }

    @Test(dataProvider = "checkIfTxPermitted4OltChildDP")
    public void testCheckIfTxPermitted4OltChild(CreateCPSTransactionParameter parameterMock, Long auftragId, AuftragDaten auftragDaten, HWOltChild hwOltChild, int expectedErrors) throws Exception {

        when(auftragService.findAuftragDatenByAuftragId(auftragId)).thenReturn(auftragDaten);
        when(auftragService.findHwRackByAuftragId(auftragId)).thenReturn(hwOltChild);

        Assert.assertEquals(sut.checkIfTxPermitted4OltChild(parameterMock).size(), expectedErrors);
    }

    private AuftragDaten createMockAuftragDaten(Long auftragNoOrig, Long auftragId, Long statusId, boolean isActive) {
        AuftragDaten auftragDatenMock = Mockito.mock(AuftragDaten.class);
        when(auftragDatenMock.getAuftragId()).thenReturn(auftragId);
        when(auftragDatenMock.getAuftragNoOrig()).thenReturn(auftragNoOrig);
        when(auftragDatenMock.getStatusId()).thenReturn(statusId);
        when(auftragDatenMock.isAuftragActive()).thenReturn(isActive);
        return auftragDatenMock;
    }

    private CreateCPSTransactionParameter createMockTxParameter(Long auftragId, Long serviceOrderType) {
        CreateCPSTransactionParameter parameterMock = Mockito.mock(CreateCPSTransactionParameter.class);
        when(parameterMock.getAuftragId()).thenReturn(auftragId);
        when(parameterMock.getServiceOrderType()).thenReturn(serviceOrderType);
        return parameterMock;
    }

    private HWOltChild createMockHWOltChild(Date freigabe, String rackType) {
        HWOltChild hwOltChildMock = Mockito.mock(HWOltChild.class);
        when(hwOltChildMock.getFreigabe()).thenReturn(freigabe);
        when(hwOltChildMock.getRackTyp()).thenReturn(rackType);
        when(hwOltChildMock.isDpoOrOntRack()).thenReturn(rackType.equals(HWRack.RACK_TYPE_DPO) || rackType.equals(HWRack.RACK_TYPE_ONT));
        return hwOltChildMock;
    }

    @DataProvider
    public Object[][] getExecutableCpsTxServiceOrderType4SubscriberDP() {
        Long create = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB;
        Long modify = CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB;
        Long cancel = CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB;
        Long query = CPSTransaction.SERVICE_ORDER_TYPE_QUERY_HARDWARE;

        Long provisioning = CPSTransaction.TX_STATE_IN_PROVISIONING;
        Long success = CPSTransaction.TX_STATE_SUCCESS;
        Long cancelled = CPSTransaction.TX_STATE_CANCELLED;

        Date now = new Date();
        Date yesterday = DateTools.changeDate(now, Calendar.DAY_OF_MONTH, -1);
        Date tomorrow = DateTools.changeDate(now, Calendar.DAY_OF_MONTH, 1);
        Date past = DateTools.changeDate(now, Calendar.DAY_OF_MONTH, -2);

        return new Object[][] {
                { Collections.emptyList(),                                  create}, // Keine Txs -> create
                { Arrays.asList(createTxExt(query, success, yesterday)),    create}, // Keine Sub Txs -> create
                { Arrays.asList(createTxExt(create, cancelled, yesterday)), create}, // Keine Sub Txs -> create
                { Arrays.asList(createTxExt(query, success, past),
                        createTxExt(create, success, yesterday)),           modify}, // Create Sub Tx -> modify
                { Arrays.asList(createTxExt(create, success, past),
                        createTxExt(cancel, success, yesterday)),           create}, // Delete Sub Tx -> create
                { Arrays.asList(createTxExt(create, success, past),
                        createTxExt(cancel, cancelled, yesterday)),         modify}, // Create Sub Tx -> modify
                { Arrays.asList(createTxExt(create, success, past),
                        createTxExt(modify, provisioning, yesterday)),      modify}, // Modify Sub Tx -> modify
                { Arrays.asList(createTxExt(create, success, past),
                        createTxExt(cancel, provisioning, tomorrow)),       modify}, // Create Sub Tx -> modify
        };
    }

    private CPSTransactionExt createTxExt(Long type, Long state, Date execTime) {
        CPSTransactionExt txExt = new CPSTransactionExt();
        txExt.setServiceOrderType(type);
        txExt.setTxState(state);
        txExt.setEstimatedExecTime(execTime);
        return txExt;
    }

    @Test(dataProvider = "getExecutableCpsTxServiceOrderType4SubscriberDP")
    public void testGetExecutableCpsTxServiceOrderType4Subscriber(List<CPSTransactionExt> txs,
            Long expectedType) throws Exception {
        doReturn(txs).when(sut).findCPSTransaction(any());

        Long toValidate = sut.getExecutableCpsTxServiceOrderType4Subscriber(1L);
        assertEquals(toValidate, expectedType);
    }

}
