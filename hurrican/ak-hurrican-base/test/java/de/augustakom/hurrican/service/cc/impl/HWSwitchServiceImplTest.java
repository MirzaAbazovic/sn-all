package de.augustakom.hurrican.service.cc.impl;


import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.*;
import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.exceptions.UpdateException;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.dao.cc.SwitchMigrationLogDao;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionExtBuilder;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EGTypeBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.SwitchMigrationLog;
import de.augustakom.hurrican.model.cc.SwitchMigrationLogAuftrag;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.utils.CalculatedSwitch4VoipAuftrag;

@Test(groups = BaseTest.UNIT)
public class HWSwitchServiceImplTest extends BaseTest {

    @Mock
    private EndgeraeteService endgeraeteService;

    @Mock
    private CPSService cpsService;

    @Mock
    private CCAuftragService ccAuftragService;

    @Mock
    private ProduktService produktService;

    @Mock
    BAService baService;

    @Mock
    private SwitchMigrationLogDao switchMigrationLogDao;

    @InjectMocks
    @Spy
    private HWSwitchServiceImpl cut;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(switchMigrationLogDao.store(any(SwitchMigrationLog.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
        when(ccAuftragService.findAuftragDatenByAuftragId(anyLong())).thenAnswer(invocationOnMock -> new AuftragDaten());
    }

    @Test
    public void testCheckAuftraegeForCertifiedSwitchesReturnsWarningForNotCertifiedEndgeraet() throws Exception {
        final List<Long> auftragIds = Collections.singletonList(815L);
        final HWSwitch certifiedSwitch = new HWSwitchBuilder()
                .withRandomId()
                .withName("asdf")
                .build();
        final HWSwitch notCertifiedSwitch = new HWSwitchBuilder()
                .withRandomId()
                .build();
        final EGConfig egConfWithCertifiedSwitch = createEgConfigWithSwitch(certifiedSwitch);
        final EGConfig egConfWithoutCertifiedSwitch = createEgConfigWithSwitch(notCertifiedSwitch);

        when(endgeraeteService.findEgConfigs4Auftrag(auftragIds.get(0)))
                .thenReturn(Arrays.asList(egConfWithCertifiedSwitch, egConfWithoutCertifiedSwitch));

        final String result = cut.checkAuftraegeForCertifiedSwitchesAsWarnings(auftragIds, certifiedSwitch).getWarningsAsText();

        MatcherAssert.assertThat(result, Matchers.containsString(egConfWithoutCertifiedSwitch.getEgType().getHersteller()));
        MatcherAssert.assertThat(result, Matchers.containsString(egConfWithoutCertifiedSwitch.getEgType().getModell()));
        MatcherAssert.assertThat(result, Matchers.containsString(auftragIds.get(0).toString()));
        MatcherAssert.assertThat(result, Matchers.containsString(certifiedSwitch.getName()));
    }

    private EGConfig createEgConfigWithSwitch(final HWSwitch hwSwitch) {
        final EGTypeBuilder egTypeBuilder = new EGTypeBuilder()
                .withRandomId()
                .withHersteller("hersteller")
                .withModell("modell" + hwSwitch.getName())
                .withOrderedCertifiedSwitches(ImmutableList.of(hwSwitch));
        return new EGConfigBuilder()
                .withRandomId()
                .withEGTypeBuilder(egTypeBuilder)
                .withEG2AuftragBuilder(new EG2AuftragBuilder().withRandomId())
                .build();
    }

    @Test
    public void moveOrdersToSwitch() throws UpdateException, FindException, ServiceNotFoundException, IOException, InvalidFormatException {
        Long prodId = 1L;
        final Long sessionId = 9999L;
        final Date execDate = new Date();
        HWSwitch sourceSwitch = new HWSwitchBuilder().setPersist(false)
                .withName("TEST02")
                .build();
        HWSwitch destinationSwitch = new HWSwitchBuilder().setPersist(false)
                .withName("TEST01")
                .build();
        SwitchMigrationView orderToMigrate1 = createSwitchMigrationView(1L, prodId);
        SwitchMigrationView orderToMigrate2 = createSwitchMigrationView(2L, prodId);
        SwitchMigrationView orderToMigrate3 = createSwitchMigrationView(3L, prodId);
        SwitchMigrationView orderToMigrate4 = createSwitchMigrationView(4L, prodId);

        doNothing().when(cut).moveOrderToSwitchNewTx(orderToMigrate1.getAuftragId(), destinationSwitch);
        doNothing().when(cut).moveOrderToSwitchNewTx(orderToMigrate2.getAuftragId(), destinationSwitch);
        doNothing().when(cut).moveOrderToSwitchNewTx(orderToMigrate3.getAuftragId(), destinationSwitch);
        doThrow(new FindException()).when(cut).moveOrderToSwitchNewTx(orderToMigrate4.getAuftragId(),
                destinationSwitch);
        doReturn(cut).when(cut).getCCService(HWSwitchService.class);

        doReturn(Pair.create(Either.left(1L), new AKWarnings())).when(cut).updateCPSNewTx(orderToMigrate1.getAuftragId(), sessionId, execDate);
        doReturn(Pair.create(Either.left(1L), new AKWarnings().addAKWarning(null, "AKWarning returned from updateCPS"))).when(cut)
            .updateCPSNewTx(orderToMigrate2.getAuftragId(), sessionId, execDate);
        doThrow(new FindException()).when(cut).updateCPSNewTx(orderToMigrate3.getAuftragId(), sessionId, execDate);

        Map<Long, SwitchMigrationLogAuftrag> migrationLogAuftrags = new HashMap<>();
        when(switchMigrationLogDao.store(any(SwitchMigrationLogAuftrag.class))).thenAnswer(invocationOnMock -> {
            if (invocationOnMock.getArguments()[0] instanceof SwitchMigrationLogAuftrag){
                SwitchMigrationLogAuftrag switchMigrationLogAuftrag = (SwitchMigrationLogAuftrag) invocationOnMock.getArguments()[0];
                migrationLogAuftrags.put(switchMigrationLogAuftrag.getAuftragId(), switchMigrationLogAuftrag);
            }
            return invocationOnMock.getArguments()[0];
        });
        when(switchMigrationLogDao.findByProperty(any(), anyString(), anyObject())).
                thenAnswer(invocationOnMock -> new ArrayList<>(migrationLogAuftrags.values()));

        byte[] xlsData = cut.moveOrdersToSwitch(Arrays.asList(orderToMigrate1, orderToMigrate2, orderToMigrate3, orderToMigrate4),
                sourceSwitch, destinationSwitch, sessionId, execDate);

        verify(cut, times(4)).moveOrderToSwitchNewTx(any(), eq(destinationSwitch));

        verify(cut).updateCPSNewTx(orderToMigrate1.getAuftragId(), sessionId, execDate);
        verify(cut).updateCPSNewTx(orderToMigrate2.getAuftragId(), sessionId, execDate);
        verify(cut).updateCPSNewTx(orderToMigrate3.getAuftragId(), sessionId, execDate);
        verify(cut, never()).updateCPSNewTx(orderToMigrate4.getAuftragId(), sessionId, execDate);

        assertNotNull(xlsData);
        Workbook workbook = XlsPoiTool.loadExcelFile(xlsData);
        Sheet auftragSheet = workbook.getSheetAt(1);

        assertEquals(auftragSheet.getLastRowNum(), 4);
        assertTrue(auftragSheet.getRow(2).getCell(HWSwitchServiceImpl.ROW_MESSAGE).getStringCellValue().startsWith("AKWarning returned from updateCPS"));
        assertTrue(auftragSheet.getRow(3).getCell(HWSwitchServiceImpl.ROW_MESSAGE).getStringCellValue().startsWith("Fehler beim Erstellen der CPS-TX"));
        assertTrue(auftragSheet.getRow(4).getCell(HWSwitchServiceImpl.ROW_MESSAGE).getStringCellValue().startsWith("Fehler beim Auftrag umschreiben"));
    }

    @Test
    public void testUpdateHwSwitchBasedComponentsNoAction() throws Exception {
        Long auftragId = 44L;
        CalculatedSwitch4VoipAuftrag calculatedSwitch = new CalculatedSwitch4VoipAuftrag();
        calculatedSwitch.calculatedHwSwitch = new HWSwitch();
        calculatedSwitch.isOverride = false;
        when(ccAuftragService.calculateSwitch4VoipAuftrag(auftragId)).thenReturn(Either.left(calculatedSwitch));

        Either<String, String> result = cut.updateHwSwitchBasedComponents(auftragId, calculatedSwitch);

        assertNotNull(result);
        assertTrue(result.isLeft());
        assertNull(result.getLeft());
        verify(ccAuftragService, never()).updateSwitchForAuftrag(anyLong(), any());
    }

    @Test
    public void testUpdateHwSwitchBasedComponentsNoFutureSwitch() throws Exception {
        Long auftragId = 44L;
        CalculatedSwitch4VoipAuftrag calculatedSwitch = new CalculatedSwitch4VoipAuftrag();
        calculatedSwitch.calculatedHwSwitch = null;
        calculatedSwitch.isOverride = false;
        when(ccAuftragService.calculateSwitch4VoipAuftrag(auftragId)).thenReturn(Either.left(calculatedSwitch));

        Either<String, String> result = cut.updateHwSwitchBasedComponents(auftragId, calculatedSwitch);

        assertNotNull(result);
        assertTrue(result.isLeft());
        assertNull(result.getLeft());
        verify(ccAuftragService, never()).updateSwitchForAuftrag(anyLong(), any());
    }

    @Test
    public void testUpdateHwSwitchBasedComponentsNoVoip() throws Exception {
        Long auftragId = 44L;
        CalculatedSwitch4VoipAuftrag calculatedSwitch = new CalculatedSwitch4VoipAuftrag();
        calculatedSwitch.calculatedHwSwitch = null;
        calculatedSwitch.isOverride = false;
        when(ccAuftragService.calculateSwitch4VoipAuftrag(auftragId)).thenReturn(Either.right(Boolean.TRUE));

        Either<String, String> result = cut.updateHwSwitchBasedComponents(auftragId, calculatedSwitch);

        assertNotNull(result);
        assertTrue(result.isLeft());
        assertNull(result.getLeft());
        verify(ccAuftragService, never()).updateSwitchForAuftrag(anyLong(), any());
    }

    @Test
    public void testUpdateHwSwitchBasedComponentsNoCurrenSwitch() throws Exception {
        Long auftragId = 44L;
        HWSwitch hwswitch = new HWSwitch();
        CalculatedSwitch4VoipAuftrag calculatedSwitch = new CalculatedSwitch4VoipAuftrag();
        calculatedSwitch.calculatedHwSwitch = hwswitch;
        calculatedSwitch.isOverride = true;

        when(ccAuftragService.calculateSwitch4VoipAuftrag(auftragId)).thenReturn(Either.left(calculatedSwitch));
        when(ccAuftragService.getSwitchKennung4Auftrag(auftragId)).thenReturn(null);

        Either<String, String> result = cut.updateHwSwitchBasedComponents(auftragId, calculatedSwitch);

        assertNotNull(result);
        assertTrue(result.isLeft());
        assertNotNull(result.getLeft());
        verify(ccAuftragService, times(1)).updateSwitchForAuftrag(anyLong(), any());
    }

    @Test
    public void testUpdateHwSwitchBasedComponents() throws Exception {
        Long auftragId = 44L;
        HWSwitch hwswitch1 = new HWSwitchBuilder().withName("SWITCH1").build();
        HWSwitch hwswitch2 = new HWSwitchBuilder().withName("SWITCH2").build();
        CalculatedSwitch4VoipAuftrag calculatedSwitch1 = new CalculatedSwitch4VoipAuftrag();
        calculatedSwitch1.calculatedHwSwitch = hwswitch1;
        calculatedSwitch1.isOverride = true;
        CalculatedSwitch4VoipAuftrag calculatedSwitch2 = new CalculatedSwitch4VoipAuftrag();
        calculatedSwitch2.calculatedHwSwitch = hwswitch2;
        calculatedSwitch2.isOverride = false;

        when(ccAuftragService.getSwitchKennung4Auftrag(auftragId)).thenReturn(hwswitch1);
        when(ccAuftragService.calculateSwitch4VoipAuftrag(auftragId)).thenReturn(Either.left(calculatedSwitch2));

        Either<String, String> result = cut.updateHwSwitchBasedComponents(auftragId, calculatedSwitch1);

        assertNotNull(result);
        assertTrue(result.isLeft());
        assertNotNull(result.getLeft());
        verify(ccAuftragService, times(1)).updateSwitchForAuftrag(anyLong(), any());
    }

    private SwitchMigrationView createSwitchMigrationView(Long auftragId, Long prodId) {
        SwitchMigrationView switchMigrationView = new SwitchMigrationView();
        switchMigrationView.setAuftragId(auftragId);
        switchMigrationView.setBillingAuftragId(100000 + auftragId);
        switchMigrationView.setProdId(prodId);
        return switchMigrationView;
    }

    @Test
    public void testUpdateCPS() throws Exception {
        final Long auftragId = 123456L;
        final Long sessionId = 9999L;
        final Date execDate = new Date();

        Verlauf verlauf = new VerlaufBuilder()
                .setPersist(false)
                .withRealisierungstermin(Date.from(ZonedDateTime.now().minusDays(1).toInstant()))
                .build();
        when(baService.findLastVerlauf4Auftrag(auftragId, false)).thenReturn(verlauf);
        when(cpsService.isCPSProvisioningAllowed(auftragId, CPSServiceOrderData.LazyInitMode.noInitialLoad, true, true, true)).thenReturn(new CPSProvisioningAllowed(true, null));
        CPSTransactionExt cps1 = new CPSTransactionExtBuilder().withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB).withId(3700L).build();
        CPSTransactionExt cps2 = new CPSTransactionExtBuilder().withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB).withId(3500L).build();
        CPSTransactionExt cps3 = new CPSTransactionExtBuilder().withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_GET_SODATA).withId(3900L).build();
        when(cpsService.findCPSTransaction(any(CPSTransactionExt.class))).thenReturn(Arrays.asList(cps1, cps2, cps3));
        CPSTransactionResult cpsTxResult = mock(CPSTransactionResult.class);
        CPSTransaction cpsTxNew = new CPSTransactionBuilder().withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB).build();
        when(cpsTxResult.getCpsTransactions()).thenReturn(Collections.singletonList(cpsTxNew));
        when(cpsTxResult.getWarnings()).thenReturn(new AKWarnings());
        when(cpsService.createCPSTransaction(any())).thenReturn(cpsTxResult);

        final Pair<Either<Long, Boolean>, AKWarnings> result = cut.updateCPSNewTx(auftragId, sessionId, execDate);

        verify(baService).findLastVerlauf4Auftrag(auftragId, false);
        final ArgumentCaptor<CPSTransactionExt> acCpsSearchExample = ArgumentCaptor.forClass(CPSTransactionExt.class);
        verify(cpsService).findCPSTransaction(acCpsSearchExample.capture());
        assertEquals(acCpsSearchExample.getValue().getAuftragId(), auftragId);

        final ArgumentCaptor<CreateCPSTransactionParameter> acCreateCpsTx = ArgumentCaptor.forClass(CreateCPSTransactionParameter.class);
        verify(cpsService).createCPSTransaction(acCreateCpsTx.capture());
        final CreateCPSTransactionParameter cpsParameter = acCreateCpsTx.getValue();
        assertEquals(cpsParameter.getAuftragId(), auftragId);
        assertEquals(cpsParameter.getEstimatedExecTime(), execDate);
        assertEquals(cpsParameter.getServiceOrderType(), CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB);
        assertEquals(cpsParameter.getSessionId(), sessionId);

        verify(cpsService).sendCPSTx2CPS(cpsTxNew, sessionId);
        Assert.assertTrue(result.getSecond().isEmpty());

        //assertWarning
        final String warningMessage = "TEST-WARNING";
        when(cpsTxResult.getWarnings()).thenReturn(new AKWarnings().addAKWarning(this, warningMessage));
        final Pair<Either<Long, Boolean>, AKWarnings> secondResult = cut.updateCPSNewTx(auftragId, sessionId, execDate);
        assertNotNull(secondResult);
        Assert.assertTrue(secondResult.getSecond().getWarningsAsText().startsWith(warningMessage),
                String.format("'%s' won't start with '%s'", secondResult.getSecond().getWarningsAsText(), warningMessage));
        verify(cpsService, times(2)).sendCPSTx2CPS(cpsTxNew, sessionId);
    }

    @Test
    public void testUpdateCPSNotAllowed() throws Exception {
        final Long auftragId = 123456L;
        final Long sessionId = 9999L;
        final Date execDate = new Date();
        Verlauf verlauf = new VerlaufBuilder()
                .setPersist(false)
                .withRealisierungstermin(Date.from(ZonedDateTime.now().minusDays(1).toInstant()))
                .build();
        when(baService.findLastVerlauf4Auftrag(auftragId, false)).thenReturn(verlauf);
        when(cpsService.isCPSProvisioningAllowed(auftragId, CPSServiceOrderData.LazyInitMode.noInitialLoad, true, true, true)).thenReturn(new CPSProvisioningAllowed(false, null));

        final Pair<Either<Long, Boolean>, AKWarnings> result = cut.updateCPSNewTx(auftragId, sessionId, execDate);
        verify(cpsService).findCPSTransaction(any());
        verify(cpsService, never()).createCPSTransaction(any());
        verify(cpsService, never()).sendCPSTx2CPS(any(), anyLong());
        Assert.assertTrue(result.getSecond().isNotEmpty());
    }

    @Test(expectedExceptions = UpdateException.class)
    public void testUpdateCPSNoValidExecDate() throws Exception {
        final Long auftragId = 123456L;
        final Long sessionId = 9999L;
        LocalDateTime execDate = LocalDateTime.now().minusDays(1);
        cut.updateCPSNewTx(auftragId, sessionId, Date.from(execDate.atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Test(expectedExceptions = UpdateException.class)
    public void testUpdateCPSNoExecDate() throws Exception {
        final Long auftragId = 123456L;
        final Long sessionId = 9999L;
        cut.updateCPSNewTx(auftragId, sessionId, null);
    }

    @Test
    public void testUpdateCPSNotNeaded() throws Exception {
        final Long auftragId = 123456L;
        final Long sessionId = 9999L;
        final Date execDate = new Date();

        Verlauf verlauf = new VerlaufBuilder()
                .setPersist(false)
                .withRealisierungstermin(Date.from(ZonedDateTime.now().minusDays(1).toInstant()))
                .build();
        when(baService.findLastVerlauf4Auftrag(auftragId, false)).thenReturn(verlauf);
        when(cpsService.isCPSProvisioningAllowed(auftragId, CPSServiceOrderData.LazyInitMode.noInitialLoad, true, true, true)).thenReturn(new CPSProvisioningAllowed(true, null));
        CPSTransactionExt cps1 = new CPSTransactionExtBuilder().withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB).withId(3700L).build();
        CPSTransactionExt cps2 = new CPSTransactionExtBuilder().withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB).withId(3800L).build();
        when(cpsService.findCPSTransaction(any(CPSTransactionExt.class))).thenReturn(Arrays.asList(cps1, cps2));

        final Pair<Either<Long, Boolean>, AKWarnings> result = cut.updateCPSNewTx(auftragId, sessionId, execDate);
        verify(cpsService).findCPSTransaction(any());
        verify(cpsService, never()).createCPSTransaction(any());
        verify(cpsService, never()).sendCPSTx2CPS(any(), anyLong());
        Assert.assertTrue(result.getSecond().isNotEmpty());
    }


    @DataProvider
    public Object[][] cpsNotNeededBaRealisierungstermin() {
        return new Object[][] {
                { null, true },
                { new VerlaufBuilder()
                        .setPersist(false)
                        .withRealisierungstermin(Date.from(ZonedDateTime.now().plusDays(1).toInstant()))
                        .build(), true
                },
                { new VerlaufBuilder()
                        .setPersist(false)
                        .withRealisierungstermin(Date.from(ZonedDateTime.now().plusDays(1).toInstant()))
                        .withAnlass(BAVerlaufAnlass.NEUSCHALTUNG)
                        .build(), false
                },
                { new VerlaufBuilder()
                        .setPersist(false)
                        .withRealisierungstermin(Date.from(ZonedDateTime.now().toInstant()))
                        .withAnlass(BAVerlaufAnlass.NEUSCHALTUNG)
                        .build(), true
                },
                { new VerlaufBuilder()
                        .setPersist(false)
                        .withRealisierungstermin(Date.from(ZonedDateTime.now().toInstant()))
                        .withAnlass(BAVerlaufAnlass.ABW_TKG46_AENDERUNG)
                        .build(), true
                },
                { new VerlaufBuilder()
                        .setPersist(false)
                        .withRealisierungstermin(Date.from(ZonedDateTime.now().plusDays(1).toInstant()))
                        .withAnlass(BAVerlaufAnlass.ABW_TKG46_AENDERUNG)
                        .build(), true
                }
        };
    }

    @Test(dataProvider = "cpsNotNeededBaRealisierungstermin")
    public void testUpdateCPSNotNeadedBaRealisierungstermin(Verlauf verlauf, Boolean cpsTxRequired) throws Exception {
        final Long auftragId = 123456L;
        final Long sessionId = 9999L;
        final Date execDate = new Date();

        when(baService.findLastVerlauf4Auftrag(auftragId, false)).thenReturn(verlauf);

        final Pair<Either<Long, Boolean>, AKWarnings> akWarnings = cut.updateCPSNewTx(auftragId, sessionId, execDate);
        verify(baService).findLastVerlauf4Auftrag(auftragId, false);
        Assert.assertEquals(akWarnings.getFirst().getRight(), cpsTxRequired);
        Assert.assertTrue(akWarnings.getSecond().isNotEmpty());
    }

}
