/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2009 08:50:49
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.io.*;
import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.evolving.wsdl.sa.v1.types.ServiceResponse;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import com.evolving.wsdl.sa.v1.types.ServiceResponseDocument;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatchers;
import org.springframework.ws.test.client.ResponseCreators;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;
import de.augustakom.hurrican.model.cc.cps.ICPSConstants;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.tools.AuftragFtthSuFBuilder;
import de.augustakom.hurrican.model.tools.CpsTx4FtthDeviceBuilder;
import de.augustakom.hurrican.model.tools.StandortFtthBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;

/**
 * TestCase fuer <code>CPSService</code>
 */
@Test(groups = BaseTest.SERVICE)
public class CPSServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CPSServiceTest.class);

    @Resource(name = "cpsWSTemplate")
    private MnetWebServiceTemplate cpsWsTemplate;
    private Auftrag auftrag;

    @BeforeMethod
    public void setup() {
        auftrag = createQueryAttainableBitrateData();
    }

    private Auftrag createQueryAttainableBitrateData() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withHwTypeName("XDSL_AGB")
                .withName("ADBF");

        HWDslamBuilder hwDslamBuilder = getBuilder(HWDslamBuilder.class)
                .withHwProducerBuilder(
                        getBuilder(HVTTechnikBuilder.class)
                                .withHersteller("SIEMENS")
                                .withCpsName("SIEMENS")
                );

        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(bgTypBuilder)
                .withRackBuilder(hwDslamBuilder);

        EquipmentBuilder eqInBuilder = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder)
                .withHwEQN("Ü02/1-302-11");

        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class));

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder);

        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withEndstelleBuilder(endstelleBBuilder)
                .build();

        return auftragBuilder.get();
    }

    private ServiceResponseDocument createSOServiceResponseData(String maxAttBrDn, String maxAttBrUp, Integer soResult) {
        ServiceResponse2 serviceResponse2 = ServiceResponse2.Factory.newInstance();
        serviceResponse2.setSOResult(soResult);

        XmlObject xmlObject = XmlObject.Factory.newInstance();
        XmlCursor xmlCursor = xmlObject.newCursor();
        xmlCursor.toNextToken();
        StringBuilder cdata = new StringBuilder();
        cdata.append("<?xml version=\"1.0\" encoding=\"UTF8\"?>");
        cdata.append("<QUERY>");
        cdata.append("<INSTANCE>");
        cdata.append("<ENTRY>");
        if (maxAttBrDn != null) {
            cdata.append("<MAXATTBRDN>");
            cdata.append(maxAttBrDn);
            cdata.append("</MAXATTBRDN>");
        }
        if (maxAttBrUp != null) {
            cdata.append("<MAXATTBRUP>");
            cdata.append(maxAttBrUp);
            cdata.append("</MAXATTBRUP>");
        }
        cdata.append("</ENTRY>");
        cdata.append("</INSTANCE>");
        cdata.append("</QUERY>");
        xmlCursor.insertChars(cdata.toString());

        serviceResponse2.setSOResponseData(xmlObject);
        serviceResponse2.addNewSOResponseData();
        ServiceResponse serviceResponse = ServiceResponse.Factory.newInstance();
        serviceResponse.setServiceResponse(serviceResponse2);
        ServiceResponseDocument serviceResponseDocument = ServiceResponseDocument.Factory.newInstance();
        serviceResponseDocument.setServiceResponse(serviceResponse);
        return serviceResponseDocument;
    }

    @DataProvider
    public Object[][] dataProviderQueryAttainableBitrate() {
        // @formatter:off
        return new Object[][] {
                { createSOServiceResponseData("1", "1", ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS), true },
                { createSOServiceResponseData("1", "1", ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS - 1), false },
                { createSOServiceResponseData("1", null, ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS), false },
                { createSOServiceResponseData(null, "1", ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS), false },
                { createSOServiceResponseData("1", "", ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS), false },
                { createSOServiceResponseData("", "1", ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS), false },
                { createSOServiceResponseData("", "", ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS), false },
                { createSOServiceResponseData(null, null, ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS), false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderQueryAttainableBitrate")
    public void testQueryAttainableBitrate(ServiceResponseDocument serviceResponseDocument, boolean resultExpected)
            throws IOException, FindException {
        MockWebServiceServer mockServer = MockWebServiceServer.createServer(getCpsWsTemplate());
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(serviceResponseDocument.newInputStream())));
        CPSService service = getCCService(CPSService.class);
        Pair<Integer, Integer> result = service.queryAttainableBitrate(auftrag.getAuftragId(), -1L);

        if (resultExpected) {
            assertNotNull(result);
            assertTrue(result.getFirst() != null);
            assertTrue(result.getSecond() != null);
        }
        else {
            assertNull(result);
        }
    }

    public void testCloseCPSTxSetStateToFailureClosed() throws FindException, StoreException {
        Long orderNoOrig = Long.valueOf(123);
        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withOrderNoOrig(orderNoOrig)
                .withTxState(CPSTransaction.TX_STATE_IN_PREPARING_FAILURE)
                .withEstimatedExecTime(new Date())
                .withRegion(Niederlassung.ID_AUGSBURG)
                .build();

        CPSService service = getCCService(CPSService.class);
        service.closeCPSTx(cpsTx.getId(), Long.valueOf(1));
        assertEquals(cpsTx.getTxState(), CPSTransaction.TX_STATE_FAILURE_CLOSED, "TX-State is invalid!");
    }

    @Test(enabled = false)
    public void createCPSTransaction4OltChildInit() throws FindException, StoreException {
        CPSService service = getCCService(CPSService.class);
        service.createCPSTransaction4OltChild(Long.valueOf(2), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE,
                Long.valueOf(1));
    }

    public void testFindActiveCPSTransactions() throws FindException {
        Long orderNoOrig = Long.valueOf(123);
        getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withOrderNoOrig(orderNoOrig)
                .withTxState(CPSTransaction.TX_STATE_IN_PROVISIONING)
                .withEstimatedExecTime(new Date())
                .withRegion(Niederlassung.ID_AUGSBURG)
                .build();

        CPSService service = getCCService(CPSService.class);
        List<CPSTransaction> result = service.findActiveCPSTransactions(orderNoOrig, null, null);
        assertNotEmpty(result, "Keine aktiven CPS-Tx gefunden!");
        assertEquals(result.size(), 1, "Anzahl aktiver CPS-Tx ungueltig!");
    }

    public void testFindActiveCPSTransactionsAssertEmptyResult() throws FindException {
        Long orderNoOrig = Long.valueOf(123);
        getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withOrderNoOrig(orderNoOrig)
                .withTxState(CPSTransaction.TX_STATE_FAILURE_CLOSED)
                .withEstimatedExecTime(new Date())
                .withRegion(Niederlassung.ID_AUGSBURG)
                .build();

        CPSService service = getCCService(CPSService.class);
        List<CPSTransaction> result = service.findActiveCPSTransactions(orderNoOrig, null, null);
        assertEmpty(result, "Aktive CPS-Tx gefunden, keine erwartet!");
    }

    public void testSaveCPSTransaction() throws Exception {
        CPSTransaction cpsTx = new CPSTransaction();
        cpsTx.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_VERLAUF);
        cpsTx.setTxState(CPSTransaction.TX_STATE_IN_PROVISIONING);
        cpsTx.setEstimatedExecTime(new Date());
        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        cpsTx.setAuftragId(Long.valueOf(206265));
        cpsTx.setOrderNoOrig(Long.valueOf(11111));

        CPSService cs = getCCService(CPSService.class);
        cs.saveCPSTransaction(cpsTx, getSessionId());
    }

    public void testFindActiveCPSTransactions4Device() throws FindException, StoreException {
        final Long serviceOrderType = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE;
        final StandortFtthBuilder standortFtthBuilder = new StandortFtthBuilder();
        final AuftragFtthSuFBuilder auftragFtthSuFBuilder = new AuftragFtthSuFBuilder()
                .withStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        final CpsTx4FtthDeviceBuilder cpsTx4FtthDeviceBuilder = new CpsTx4FtthDeviceBuilder()
                .withServiceOrderType(serviceOrderType)
                .withTxState(CPSTransaction.TX_STATE_IN_PREPARING);

        final StandortFtthBuilder.StandortFtth standortFtth = standortFtthBuilder.prepare(this, null);
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = auftragFtthSuFBuilder.prepare(this, standortFtth);
        final CpsTx4FtthDeviceBuilder.CpsTx4FtthDevice cpsTx4FtthDevice = cpsTx4FtthDeviceBuilder.prepare(this,
                standortFtth.hwOntBuilder);

        auftragFtthSuFBuilder.build(auftragFtthSuF);
        cpsTx4FtthDeviceBuilder.build(cpsTx4FtthDevice);

        CPSService service = getCCService(CPSService.class);
        List<CPSTransaction> result = service.findActiveCPSTransactions(null, standortFtth.hwOntBuilder.get().getId(),
                serviceOrderType);
        assertNotNull(result);
        assertEquals(result.size(), 1);
    }

    public void testFindSuccessfulCPSTransactions4Device() throws FindException, StoreException {
        final Long serviceOrderType = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE;
        final StandortFtthBuilder standortFtthBuilder = new StandortFtthBuilder();
        final AuftragFtthSuFBuilder auftragFtthSuFBuilder = new AuftragFtthSuFBuilder()
                .withStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        final CpsTx4FtthDeviceBuilder cpsTx4FtthDeviceBuilder = new CpsTx4FtthDeviceBuilder()
                .withServiceOrderType(serviceOrderType);

        final StandortFtthBuilder.StandortFtth standortFtth = standortFtthBuilder.prepare(this, null);
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = auftragFtthSuFBuilder.prepare(this, standortFtth);
        final CpsTx4FtthDeviceBuilder.CpsTx4FtthDevice cpsTx4FtthDevice = cpsTx4FtthDeviceBuilder.prepare(this,
                standortFtth.hwOntBuilder);

        auftragFtthSuFBuilder.build(auftragFtthSuF);
        cpsTx4FtthDeviceBuilder.build(cpsTx4FtthDevice);

        CPSService service = getCCService(CPSService.class);
        List<CPSTransactionExt> result = service.findSuccessfulCPSTransactions(null,
                standortFtth.hwOntBuilder.get().getId(),
                serviceOrderType);
        assertNotNull(result);
        assertEquals(result.size(), 1);
    }

    public void testGetAuftragIdByAuftragNoOrigIgnoreStatus() {
        final StandortFtthBuilder standortFtthBuilder = new StandortFtthBuilder();
        final AuftragFtthSuFBuilder auftragFtthSuFBuilder = new AuftragFtthSuFBuilder()
                .withStatusId(AuftragStatus.ABSAGE);
        final StandortFtthBuilder.StandortFtth standortFtth = standortFtthBuilder.prepare(this, null);
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = auftragFtthSuFBuilder.prepare(this, standortFtth);
        auftragFtthSuFBuilder.build(auftragFtthSuF);

        CPSService service = getCCService(CPSService.class);
        long auftragId = service.getAuftragIdByAuftragNoOrig(auftragFtthSuF.auftragDatenBuilder.get()
                .getAuftragNoOrig(), LocalDate.now(), true);
        assertEquals(auftragId, auftragFtthSuF.auftragBuilder.get().getAuftragId().longValue());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetAuftragIdByAuftragNoOrigCheckStatus() {
        final StandortFtthBuilder standortFtthBuilder = new StandortFtthBuilder();
        final AuftragFtthSuFBuilder auftragFtthSuFBuilder = new AuftragFtthSuFBuilder()
                .withStatusId(AuftragStatus.ABSAGE);
        final StandortFtthBuilder.StandortFtth standortFtth = standortFtthBuilder.prepare(this, null);
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = auftragFtthSuFBuilder.prepare(this, standortFtth);
        auftragFtthSuFBuilder.build(auftragFtthSuF);

        CPSService service = getCCService(CPSService.class);
        long auftragId = service.getAuftragIdByAuftragNoOrig(auftragFtthSuF.auftragDatenBuilder.get()
                .getAuftragNoOrig(), LocalDate.now(), false);
    }

    @Test(enabled = false)
    public void testCreateCPSTransaction() throws Exception {
        Long auftragId = Long.valueOf(217960);
        Date execDate = DateTools.plusWorkDays(1);

        CPSService cps = getCCService(CPSService.class);
        CPSTransactionResult cpsTxResult = cps.createCPSTransaction(
                new CreateCPSTransactionParameter(auftragId, null, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                        CPSTransaction.TX_SOURCE_HURRICAN_ORDER, CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT,
                        execDate, null, null, null, null, null, false, getSessionId())
        );
        Assert.assertNotNull(cpsTxResult, "CPSTransactionResult not created!");
        assertNotEmpty(cpsTxResult.getCpsTransactions(), "No CPS-Tx in CPSTransactionResult!");
        CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);
        Assert.assertNotNull(cpsTx, "CPS-Tx not created!");
        LOGGER.debug("Created CPS-Tx: " + cpsTx.getId());
    }

    @Test(enabled = false)
    public void testCreateCPSTransactions4BA() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        Date nextWorkingDay = DateTools.plusWorkDays(1);
        LOGGER.debug("next working day: " + nextWorkingDay);
        CPSTransactionResult result = cps.createCPSTransactions4BA(nextWorkingDay, getSessionId());

        Assert.assertNotNull(result, "CPS result object is null!");
        assertNotEmpty(result.getCpsTransactions(), "No CPS-Transactions created!");
        for (CPSTransaction cpsTx : result.getCpsTransactions()) {
            LOGGER.debug(".. created Tx: " + cpsTx.getId() + " - " + cpsTx.getAuftragId());
        }

        if (result.getWarnings() != null) {
            LOGGER.debug("... occured warnings: " + result.getWarnings().getWarningsAsText());
        }
    }

    @Test(enabled = false)
    public void testFindCPSTransaction() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        CPSTransactionExt example = new CPSTransactionExt();
        example.setVerlaufId(Long.valueOf(173682));

        List<CPSTransactionExt> result = cps.findCPSTransaction(example);
        assertNotEmpty(result, "CPS-Tx not found for example object!");
    }

    @Test(enabled = false)
    public void testCreateCPSTransaction4BA() throws Exception {
        BAService bas = getCCService(BAService.class);
        Verlauf v = bas.findVerlauf(Long.valueOf(184541));

        CPSService cps = getCCService(CPSService.class);
        CPSTransactionResult result = cps.createCPSTransaction4BA(v, getSessionId());

        Assert.assertNotNull(result.getCpsTransactions(), "No CPS-Transaction created!");
        assertNotEmpty(result.getCpsTransactions(), "No CPS-Transaction created!");
        CPSTransaction cpsTx = result.getCpsTransactions().get(0);
        LOGGER.debug(".. created Tx: " + cpsTx.getId() + " - " + cpsTx.getAuftragId());
    }

    @Test(enabled = false)
    public void testCreateCPSTransactions4DNServices() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        CPSTransactionResult result = cps.createCPSTransactions4DNServices(new Date(), getSessionId());
        Assert.assertNotNull(result, "CPSTransactionResult is null!");
        assertNotEmpty(result.getCpsTransactions(), "es wurden keine CPS-Transactions fuer DN-Services angelegt!");
    }

    @Test(enabled = false)
    public void testCreateCPSTransactions4Lock() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        CPSTransactionResult result = cps.createCPSTransactions4Lock(getSessionId());
        Assert.assertNotNull(result, "CPSTransactionResult is null!");
        assertNotEmpty(result.getCpsTransactions(), "es wurden keine CPS-Transactions fuer Locks angelegt!");
    }

    @Test(enabled = false)
    public void testCreateCPSTransaction4MDUInit() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        Long hwRackId = Long.valueOf(913);
        CPSTransactionResult result = cps.createCPSTransaction4MDUInit(hwRackId, false, getSessionId(), false);
        Assert.assertNotNull(result, "CPSTransactionResult is null!");
        assertNotEmpty(result.getCpsTransactions(), "es wurde keine CPS-Transaction fuer den MDU Init angelegt!");
    }

    @Test(enabled = false)
    public void testSendCPSTx2CPS() throws Exception {
        CPSService cs = getCCService(CPSService.class);
        CPSTransaction cpsTx = cs.findCPSTransactionById(1695644L);
        cs.sendCPSTx2CPS(cpsTx, getSessionId());
    }

    @Test(enabled = false)
    public void testCancelCPSTransaction() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        boolean cancelled = cps.cancelCPSTransaction(new Long(3000117), getSessionId());
        Assert.assertTrue(cancelled, "CPS-Tx is not cancelled!");
    }

    @Test(enabled = false)
    public void testDisjoinCPSTransaction() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        cps.disjoinCPSTransaction(new Long(1544644), getSessionId());
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.CPSServiceImpl#saveCPSTransactionSubOrder(CPSTransactionSubOrder)}.
     */
    @Test(enabled = false)
    public void testSaveCPSTransactionSubOrder() throws Exception {
        CPSService cps = getCCService(CPSService.class);

        CPSTransactionSubOrder toSave = new CPSTransactionSubOrder();
        toSave.setCpsTxId(Long.valueOf(606));
        toSave.setAuftragId(Long.valueOf(111));
        cps.saveCPSTransactionSubOrder(toSave);
    }

    /**
     * Test-Case fuehrt testCreateCPSTransaction und sendCPSTransaction fuer einen Auftrag aus.
     *
     *
     */
    @Test(enabled = false)
    public void testCreateCPSTx4OrderAndSend() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        CPSTransactionResult cpsTxResult = cps.createCPSTransaction(new CreateCPSTransactionParameter(Long
                .valueOf(205548), null, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                CPSTransaction.TX_SOURCE_HURRICAN_ORDER, CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT,
                new Date(), null, null, null, null, null, false, getSessionId()
        ));

        Assert.assertNotNull(cpsTxResult, "CPSTransactionResult not created!");
        assertNotEmpty(cpsTxResult.getCpsTransactions(), "No CPS-Tx in CPSTransactionResult!");
        CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);
        Assert.assertNotNull(cpsTx, "CPS-Tx was not created!");

        // CPS-Tx an CPS senden
        cps.sendCPSTx2CPS(cpsTx, getSessionId());
    }

    @Test(enabled = false)
    public void testFindCPSTransactions4Verlauf() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        List<CPSTransaction> result = cps.findCPSTransactions4Verlauf(Long.valueOf(184543));

        assertNotEmpty(result, "keine CPS-Tx fuer Verlauf gefunden!");
        for (CPSTransaction tx : result) {
            LOGGER.debug("CPS-Tx: " + tx.getId());
        }
    }

    @Test(enabled = false)
    public void testDoLazyInit() throws Exception {
        CPSService cps = getCCService(CPSService.class);
        CPSTransactionResult result = cps.doLazyInit(LazyInitMode.initialLoad, Long.valueOf(134269), null,
                getSessionId());
        Assert.assertNotNull(result, "Lazy-Init not done!");

        CPSTransaction cpsTx = cps.findCPSTransactionById(result.getCpsTransactions().get(0).getId());
        Assert.assertEquals(cpsTx.getTxState(), CPSTransaction.TX_STATE_SUCCESS, "TX-State not OK");
    }

    @Test
    public void testFindCPSTransactions4TechOrder() throws Exception {
        final CPSService cps = getCCService(CPSService.class);
        final List<Long> soCreateModCancelTypes = Arrays.asList(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);
        final List<CPSTransaction> retval = cps.findCPSTransactions4TechOrder(452858L, Collections.singletonList(CPSTransaction.TX_STATE_SUCCESS), soCreateModCancelTypes);
        assertNotNull(retval);
        assertTrue(retval.size() > 0);
    }

    @Test
    public void findLatestCPSTransactions4TechOrder_Found() throws Exception {
        final CPSService cps = getCCService(CPSService.class);
        final List<Long> soCreateModCancelTypes = Arrays.asList(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);
        final Optional<CPSTransaction> latestCPSTransactions4TechOrder = cps.findLatestCPSTransactions4TechOrder(452858L, Collections.singletonList(CPSTransaction.TX_STATE_SUCCESS), soCreateModCancelTypes);
        assertTrue(latestCPSTransactions4TechOrder.isPresent());
    }

    @Test
    public void findLatestCPSTransactions4TechOrder_NOT_Found() throws Exception {
        final CPSService cps = getCCService(CPSService.class);
        final List<Long> soCreateModCancelTypes = Arrays.asList(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);
        final long invalidAuftragId = 12345568999L;
        final Optional<CPSTransaction> latestCPSTransactions4TechOrder = cps.findLatestCPSTransactions4TechOrder(invalidAuftragId, Collections.singletonList(CPSTransaction.TX_STATE_SUCCESS), soCreateModCancelTypes);
        assertFalse(latestCPSTransactions4TechOrder.isPresent());
    }

    MnetWebServiceTemplate getCpsWsTemplate() {
        return cpsWsTemplate;
    }

    /**
     * Injected
     */
    void setCpsWsTemplate(MnetWebServiceTemplate cpsWsTemplate) {
        this.cpsWsTemplate = cpsWsTemplate;
    }
}
