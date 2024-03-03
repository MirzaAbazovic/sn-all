/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2009 08:55:52
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.evolving.wsdl.sa.v1.types.ServiceResponse;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import com.evolving.wsdl.sa.v1.types.ServiceResponseDocument;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.ICPSConstants;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.DSLAMProfileMonitorService;


/**
 * Test NG Klasse fuer {@link FinishCPSTxCommand}
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class FinishCPSTxCommandTest extends AbstractHurricanBaseServiceTest {

    private static final Long TAIFUN_ORDER__NO_SUCCESS = AuftragBuilder.getLongId();
    private static final Long TAIFUN_ORDER__NO_ERROR = AuftragBuilder.getLongId();

    @SuppressWarnings("unused")
    private CPSService cpsService;

    private DSLAMProfileMonitorService dslamService;

    private ServiceResponseDocument serviceResponseDocument;
    private ServiceResponse serviceResponse;
    private ServiceResponse2 serviceResponse2;

    /**
     * Initialize the tests
     */
    @BeforeMethod(dependsOnMethods = "beginTransactions")
    public void prepareTest() {
        cpsService = getCCService(CPSService.class);
        dslamService = getCCService(DSLAMProfileMonitorService.class);
        serviceResponseDocument = mock(ServiceResponseDocument.class);
        serviceResponse = mock(ServiceResponse.class);
        serviceResponse2 = mock(ServiceResponse2.class);
    }

    /**
     * Test for {@link FinishCPSTxCommand#equals(Object)} - Test nimmt eine erfolgreiche Rueckmeldung an
     */
    @Test
    public void testExecuteFinishCPSTxSuccess_DSLAMProfileMonitored() throws Exception {
        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class)
                        .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT));
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class)
                        .withEndstelleBuilder(endstelleBBuilder))
                .withAuftragDatenBuilder(auftragDatenBuilder);
        TechLeistungBuilder techLeistungBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_DOWNSTREAM)
                .withLongValue(Long.valueOf(18000));
        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withTechleistungBuilder(techLeistungBuilder)
                .build();

        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .withEstimatedExecTime(DateTools.changeDate(new Date(), Calendar.DATE, 1))
                .withTxState(CPSTransaction.TX_STATE_IN_PROVISIONING)
                .withOrderNoOrig(TAIFUN_ORDER__NO_SUCCESS)
                .withTxSource(CPSTransaction.TX_SOURCE_HURRICAN_VERLAUF)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .build();

        when(serviceResponseDocument.getServiceResponse()).thenReturn(serviceResponse);
        when(serviceResponse.getServiceResponse()).thenReturn(serviceResponse2);
        when(serviceResponse2.getTransactionId()).thenReturn("" + cpsTx.getId());
        when(serviceResponse2.getSOResult()).thenReturn(ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS);

        // partial mock (=spy) for CPSService.createCPSTxLog because of transaction definition (RequiresNew)
        CPSService cpsService = (CPSService) getBean("de.augustakom.hurrican.service.cc.CPSService");

        FinishCPSTxCommand command = (FinishCPSTxCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.FinishCPSTxCommand");
        command.prepare(FinishCPSTxCommand.KEY_CPS_TX_ID, cpsTx.getId());
        command.prepare(FinishCPSTxCommand.KEY_CPS_RESULT, serviceResponseDocument);
        command.prepare(FinishCPSTxCommand.KEY_SESSION_ID, Long.valueOf(1));
        command.setCpsService(cpsService);

        command.execute();

        // CPS-Tx erneut laden und Status pruefen
        CPSTransaction cpsTxLoaded = cpsService.findCPSTransactionById(cpsTx.getId());
        assertNotNull(cpsTxLoaded, "CPS-Tx not loaded!");
        assertEquals(cpsTxLoaded.getTxState(), CPSTransaction.TX_STATE_SUCCESS, "CPS TxState not as expected!");
        Collection<Long> currentlyMonitoredAuftragIds = dslamService.findCurrentlyMonitoredAuftragIds();
        assertTrue(currentlyMonitoredAuftragIds.contains(cpsTxLoaded.getAuftragId()));
    }

    /**
     * Test for {@link FinishCPSTxCommand#equals(Object)} - Test nimmt eine fehlerhafte Rueckmeldung an
     */
    @Test
    public void testExecuteFinishCPSTxError() throws Exception {
        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .withEstimatedExecTime(DateTools.changeDate(new Date(), Calendar.DATE, 1))
                .withTxState(CPSTransaction.TX_STATE_IN_PROVISIONING)
                .withOrderNoOrig(TAIFUN_ORDER__NO_ERROR)
                .build();

        when(serviceResponseDocument.getServiceResponse()).thenReturn(serviceResponse);
        when(serviceResponse.getServiceResponse()).thenReturn(serviceResponse2);
        when(serviceResponse2.getTransactionId()).thenReturn("" + cpsTx.getId());
        when(serviceResponse2.getSOResult()).thenReturn(ICPSConstants.SERVICE_RESPONSE_SORESULT_CODE_SUCCESS + 1); // +1 --> error

        // partial mock (=spy) for CPSService.createCPSTxLog because of transaction definition (RequiresNew)
        CPSService cpsService = (CPSService) getBean("de.augustakom.hurrican.service.cc.CPSService");

        FinishCPSTxCommand command = (FinishCPSTxCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.FinishCPSTxCommand");
        command.prepare(FinishCPSTxCommand.KEY_CPS_TX_ID, cpsTx.getId());
        command.prepare(FinishCPSTxCommand.KEY_CPS_RESULT, serviceResponseDocument);
        command.prepare(FinishCPSTxCommand.KEY_SESSION_ID, Long.valueOf(1));
        command.setCpsService(cpsService);

        command.execute();

        // CPS-Tx erneut laden und Status pruefen
        CPSTransaction cpsTxLoaded = cpsService.findCPSTransactionById(cpsTx.getId());
        assertNotNull(cpsTxLoaded, "CPS-Tx not loaded!");
        assertEquals(cpsTxLoaded.getTxState(), CPSTransaction.TX_STATE_FAILURE_CLOSED, "CPS TxState not as expected!");
    }
}


