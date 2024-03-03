/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2010 08:29:29
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
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
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionSubOrderBuilder;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceResponseSOData;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HWService;

/**
 * TestNG Klasse fuer Unit-Tests von {@link FinishCPSTxCommand}
 */
@Test(groups = BaseTest.UNIT)
public class FinishCPSTxCommandUnitTest extends BaseTest {

    @InjectMocks
    @Spy
    private FinishCPSTxCommand testling = new FinishCPSTxCommand();
    @Mock
    private BAService baServiceMock;
    @Mock
    private CPSService cpsServiceMock;
    @Mock
    private HWService hwService;
    @Mock
    private FTTXHardwareService fttxHardwareService;

    private CPSTransaction cpsTx;

    @BeforeMethod
    public void setUp() throws FindException {
        MockitoAnnotations.initMocks(this);

        cpsTx = new CPSTransactionBuilder()
                .withVerlaufId(Long.MAX_VALUE)
                .setPersist(false).build();

        CPSTransactionSubOrder cpsTxSubOrder = new CPSTransactionSubOrderBuilder()
                .withVerlaufId(Long.MAX_VALUE - 1)
                .setPersist(false).build();

        List<VerlaufAbteilung> verlaufAbteilungen = new ArrayList<>();
        List<VerlaufAbteilung> verlaufAbteilungenSubOrders = new ArrayList<>();
        VerlaufAbteilung vaSTVoice = new VerlaufAbteilungBuilder().withAbteilungId(Abteilung.ST_VOICE)
                .setPersist(false).build();
        verlaufAbteilungen.add(vaSTVoice);
        verlaufAbteilungenSubOrders.add(vaSTVoice);
        VerlaufAbteilung vaSTOnline = new VerlaufAbteilungBuilder().withAbteilungId(Abteilung.ST_ONLINE)
                .setPersist(false).build();
        verlaufAbteilungen.add(vaSTOnline);
        VerlaufAbteilung vaFieldService = new VerlaufAbteilungBuilder().withAbteilungId(Abteilung.FIELD_SERVICE)
                .setPersist(false).build();
        verlaufAbteilungen.add(vaFieldService);

        when(baServiceMock.findVerlaufAbteilungen(cpsTx.getVerlaufId())).thenReturn(verlaufAbteilungen);
        when(baServiceMock.findVerlaufAbteilungen(cpsTxSubOrder.getVerlaufId()))
                .thenReturn(verlaufAbteilungenSubOrders);
        when(cpsServiceMock.findCPSTransactionSubOrders(any(Long.class))).thenReturn(Arrays.asList(cpsTxSubOrder));
    }

    public void testGetAffectedVerlaufIDs4CPSTx() throws FindException {
        Set<Long> verlaufIDs = testling.getAffectedVerlaufIDs4CPSTx(cpsTx);
        assertNotNull(verlaufIDs);
        assertEquals(verlaufIDs.size(), 2);
    }

    public void testFinishProvisioningOrders() throws StoreException {
        testling.finishProvisioningOrders(cpsTx, null);

        verify(baServiceMock, times(3)).finishVerlauf4Abteilung(
                any(VerlaufAbteilung.class),
                eq("CPS"),
                any(String.class),
                any(Date.class),
                any(Long.class),
                (Long) isNull(),
                (Boolean) isNull(),
                (Long) isNull());
    }

    public void testDoTxSourceDependentActions_txSourceVerlauf() throws Exception {
        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_VERLAUF)
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .build();

        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();

        doNothing().when(testling).monitorDSLAMProfileForAuftragIfNeccessary(cpsTx.getAuftragId());
        doNothing().when(testling).finishProvisioningOrders(cpsTx, soData);

        testling.doTxSourceDependentActions(cpsTx, soData);

        verify(testling).monitorDSLAMProfileForAuftragIfNeccessary(eq(cpsTx.getAuftragId()));
        verify(testling).finishProvisioningOrders(eq(cpsTx), eq(soData));
    }

    public void testDoTxSourceDependentActions_txSourceMduWithSoTypeInitMdu() throws Exception {
        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_MDU)
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_INIT_MDU)
                .build();
        cpsTx.setHwRackId(RandomTools.createLong());

        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
        doReturn(hwService).when(testling).getCCService(eq(HWService.class));
        when(hwService.findRackById(cpsTx.getHwRackId())).thenReturn(new HWMdu());

        testling.doTxSourceDependentActions(cpsTx, soData);
        ArgumentCaptor<Date> dateArgCaptor = ArgumentCaptor.forClass(Date.class);
        verify(hwService).freigabeMDU(eq(cpsTx.getHwRackId()), dateArgCaptor.capture());
        assertTrue(DateTools.isDateEqual(new Date(), dateArgCaptor.getValue()));
    }

    public void testDoTxSourceDependentActions_txSourceDpuWithSoTypeInitDpu() throws Exception {
        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_DPU)
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)
                .build();
        cpsTx.setHwRackId(RandomTools.createLong());

        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
        doReturn(hwService).when(testling).getCCService(eq(HWService.class));
        when(hwService.findRackById(cpsTx.getHwRackId())).thenReturn(new HWDpu());

        testling.doTxSourceDependentActions(cpsTx, soData);
        ArgumentCaptor<Date> dateArgCaptor = ArgumentCaptor.forClass(Date.class);
        verify(hwService).freigabeDPU(eq(cpsTx.getHwRackId()), dateArgCaptor.capture());
        assertTrue(DateTools.isDateEqual(new Date(), dateArgCaptor.getValue()));
    }

    public void testDoTxSourceDependentActions_txSourceMduWithoutSoTypeInitMdu() throws Exception {
        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_MDU)
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .build();
        cpsTx.setHwRackId(RandomTools.createLong());

        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
        doReturn(hwService).when(testling).getCCService(eq(HWService.class));

        testling.doTxSourceDependentActions(cpsTx, soData);
        verify(hwService, never()).freigabeMDU(any(Long.class), any(Date.class));
    }

    public void testDoTxSourceDependentActions_txSourceDpuWithoutSoTypeInitDpu() throws Exception {
        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_DPU)
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .build();
        cpsTx.setHwRackId(RandomTools.createLong());

        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
        doReturn(hwService).when(testling).getCCService(eq(HWService.class));

        testling.doTxSourceDependentActions(cpsTx, soData);
        verify(hwService, never()).freigabeDPU(any(Long.class), any(Date.class));
    }

    public void testDoTxSourceDependentActions_FreigabeMDUNotNecessary() throws Exception {
        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_MDU)
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_INIT_MDU)
                .build();
        cpsTx.setHwRackId(RandomTools.createLong());

        HWMdu mdu = new HWMdu();
        mdu.setFreigabe(new Date());
        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
        doReturn(hwService).when(testling).getCCService(eq(HWService.class));
        when(hwService.findRackById(cpsTx.getHwRackId())).thenReturn(mdu);

        testling.doTxSourceDependentActions(cpsTx, soData);
        verify(hwService, never()).freigabeMDU(eq(cpsTx.getHwRackId()), any(Date.class));
    }

    @Test(dataProvider = "freigabeOltChildDP")
    public void testDoTxSourceDependentActions_FreigabeOltChild(Long cpsTxSource, HWOltChild oltChild) throws Exception {
        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(cpsTxSource)
                .withAuftragBuilder(new AuftragBuilder().withRandomId())
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)
                .build();
        cpsTx.setHwRackId(RandomTools.createLong());

        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
        soData.setExecDate("2014-01-01 00:00:00");
        doReturn(hwService).when(testling).getCCService(eq(HWService.class));
        when(hwService.findRackByIdNewTx(cpsTx.getHwRackId())).thenReturn(oltChild);
        when(fttxHardwareService.findAuftraege4OltChild(oltChild)).thenReturn(Collections.<AuftragDaten>emptySet());

        testling.doTxSourceDependentActions(cpsTx, soData);
        Assert.assertEquals(oltChild.getFreigabe(), soData.getExecDateAsDate());
        verify(hwService).saveHWRackNewTx(oltChild);
    }

    @Test(dataProvider = "freigabeOltChildDP")
    public void testDoTxSourceDependentActions_CreateSubscriberTx(Long cpsTxSource, HWOltChild oltChild) throws Exception {
        Set<AuftragDaten> auftragDatenSet = new HashSet<>();
        AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder()
                .withRandomId()
                .withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG)
                .setPersist(false);
        AuftragDaten auftragDaten = auftragDatenBuilder.build();
        auftragDatenSet.add(auftragDaten);

        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(cpsTxSource)
                .withAuftragBuilder(new AuftragBuilder().withAuftragDatenBuilder(auftragDatenBuilder))
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)
                .build();
        cpsTx.setHwRackId(RandomTools.createLong());

        oltChild.setFreigabe(new Date());

        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
        doReturn(hwService).when(testling).getCCService(eq(HWService.class));
        when(hwService.findRackByIdNewTx(cpsTx.getHwRackId())).thenReturn(oltChild);

        when(fttxHardwareService.findAuftraege4OltChild(oltChild)).thenReturn(auftragDatenSet);

        CPSTransactionResult cpsTxResult =new CPSTransactionResult();
        CPSTransaction cpsCreateSubTx = new CPSTransactionBuilder()
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_ORDER)
                .withAuftragBuilder(new AuftragBuilder().withAuftragDatenBuilder(auftragDatenBuilder))
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();
        cpsTxResult.setCpsTransactions(Collections.singletonList(cpsCreateSubTx));
        when(cpsServiceMock.createCPSTransaction(any(CreateCPSTransactionParameter.class))).thenReturn(cpsTxResult);
        when(cpsServiceMock.getExecutableCpsTxServiceOrderType4Subscriber(anyLong())).thenReturn(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);

        testling.doTxSourceDependentActions(cpsTx, soData);
        verify(hwService, never()).saveHWRackNewTx(oltChild);
        verify(cpsServiceMock).sendCPSTx2CPS(eq(cpsCreateSubTx), anyLong());

        ArgumentCaptor<CreateCPSTransactionParameter> cpsTxParameter = ArgumentCaptor.forClass(CreateCPSTransactionParameter.class);
        verify(cpsServiceMock).createCPSTransaction(cpsTxParameter.capture());
        assertEquals(cpsTxParameter.getValue().getAuftragId(), auftragDaten.getAuftragId());
        assertEquals(cpsTxParameter.getValue().getServiceOrderType(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        assertEquals(cpsTxParameter.getValue().getTxSource(), CPSTransaction.TX_SOURCE_HURRICAN_ORDER);
        assertEquals(cpsTxParameter.getValue().getServiceOrderPrio(), CPSTransaction.SERVICE_ORDER_PRIO_HIGH);
    }

    @Test(dataProvider = "freigabeOltChildDP")
    public void testDoTxSourceDependentActions_TxIsNotPermitted(Long cpsTxSource, HWOltChild oltChild) throws Exception {
        Set<AuftragDaten> auftragDatenSet = new HashSet<>();
        AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder()
                .withRandomId()
                .withAuftragNoOrig(99L)
                .withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG)
                .setPersist(false);
        AuftragDaten auftragDaten = auftragDatenBuilder.build();
        auftragDatenSet.add(auftragDaten);

        CPSTransaction cpsTx = new CPSTransactionBuilder()
                .withTxSource(cpsTxSource)
                .withAuftragBuilder(new AuftragBuilder().withAuftragDatenBuilder(auftragDatenBuilder))
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)
                .build();
        cpsTx.setHwRackId(RandomTools.createLong());

        oltChild.setFreigabe(new Date());

        CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
        doReturn(hwService).when(testling).getCCService(eq(HWService.class));
        when(hwService.findRackByIdNewTx(cpsTx.getHwRackId())).thenReturn(oltChild);

        when(fttxHardwareService.findAuftraege4OltChild(oltChild)).thenReturn(auftragDatenSet);

        CPSTransaction cpsCreateSubTx = new CPSTransactionBuilder()
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_ORDER)
                .withAuftragBuilder(new AuftragBuilder().withAuftragDatenBuilder(auftragDatenBuilder))
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();
        when(cpsServiceMock.checkIfTxPermitted4OltChild(oltChild, auftragDaten, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)).thenReturn(Arrays.asList("Some Error"));
        when(cpsServiceMock.getExecutableCpsTxServiceOrderType4Subscriber(anyLong())).thenReturn(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);

        testling.doTxSourceDependentActions(cpsTx, soData);
        verify(hwService, never()).saveHWRackNewTx(oltChild);
        verify(cpsServiceMock, never()).createCPSTransaction(any(CreateCPSTransactionParameter.class));
        verify(cpsServiceMock, never()).sendCPSTx2CPS(eq(cpsCreateSubTx), anyLong());
        verify(cpsServiceMock, atLeastOnce()).checkIfTxPermitted4OltChild(oltChild, auftragDaten, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
    }

    @DataProvider
    public Object[][] freigabeOltChildDP() {
        return new Object[][] {
                {CPSTransaction.TX_SOURCE_HURRICAN_ONT, new HWOnt()},
                {CPSTransaction.TX_SOURCE_HURRICAN_DPO, new HWDpo()}
        };
    }


    @Test
    public void testCreateSubscriberCPSTransaction() throws FindException {
        HWOltChild oltChild = new HWDpo();
        
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        Date tomorrow = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        Date yesterday = Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        
        AuftragDaten storno = new AuftragDatenBuilder().withStatusId(AuftragStatus.STORNO).setPersist(false).build();
        AuftragDaten cancelled = new AuftragDatenBuilder().withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT).setPersist(false).build();
        AuftragDaten inRealisierungToday = new AuftragDatenBuilder().withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG).withInbetriebnahme(null).withVorgabeSCV(now).setPersist(false).build();
        AuftragDaten inRealisierungFuture = new AuftragDatenBuilder().withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG).withInbetriebnahme(null).withVorgabeSCV(tomorrow).setPersist(false).build();
        AuftragDaten inBetrieb = new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).withInbetriebnahme(now).setPersist(false).build();;
        AuftragDaten inKuendigung = new AuftragDatenBuilder().withStatusId(AuftragStatus.KUENDIGUNG_TECHN_REAL).withInbetriebnahme(yesterday).withKuendigung(tomorrow).setPersist(false).build();

        Set<AuftragDaten> result = new HashSet<>();
        result.addAll(Arrays.asList(storno, cancelled, inRealisierungToday, inRealisierungFuture, inBetrieb, inKuendigung));
        
        when(fttxHardwareService.findAuftraege4OltChild(any(HWOltChild.class))).thenReturn(result);
        when(cpsServiceMock.checkIfTxPermitted4OltChild(any(HWOltChild.class), any(AuftragDaten.class), anyLong()))
                .thenReturn(Arrays.asList("Error"));
        when(cpsServiceMock.getExecutableCpsTxServiceOrderType4Subscriber(anyLong()))
                .thenReturn(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);

        testling.createSubscriberCPSTransaction(oltChild, new CPSTransaction());
        
        verify(cpsServiceMock).checkIfTxPermitted4OltChild(oltChild, inRealisierungToday, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        verify(cpsServiceMock).checkIfTxPermitted4OltChild(oltChild, inBetrieb, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        verify(cpsServiceMock).checkIfTxPermitted4OltChild(oltChild, inKuendigung, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        verify(cpsServiceMock, never()).checkIfTxPermitted4OltChild(eq(oltChild), eq(storno), anyLong());
        verify(cpsServiceMock, never()).checkIfTxPermitted4OltChild(eq(oltChild), eq(cancelled), anyLong());
        verify(cpsServiceMock, never()).checkIfTxPermitted4OltChild(eq(oltChild), eq(inRealisierungFuture), anyLong());
    }

}
