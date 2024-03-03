/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2015
 */
package de.mnet.wita.acceptance;

import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.auftrag.Auftragsposition;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GeschaeftsfallKueKd_AccTest extends AbstractWitaAcceptanceTest {


    @CitrusTest(name = "GeschaeftsfallKueKd_WorkflowShouldBeClosedAfterKueKdWithAbmEntm_AccTest")
    public void workflowShouldBeClosedAfterKueKdWithAbmEntm() throws Exception {
        useCase(WitaAcceptanceUseCase.KUE_KD_QEB_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        CreatedData createdData = createData(workflow, WitaSimulatorTestUser.KUE_KD_QEB_ABM_ERLM_ENTM);
        workflow().select(workflow).send(createdData, CBVorgang.TYP_KUENDIGUNG);

        hurrican().assertCbFieldsForKuendigung(createdData, false, true);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), "productIdentifier");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().doWithWorkflow(wf -> { wf.waitForENTMWorkflowClosed(); });

        workflow().doWithWorkflow(wf -> { assertTrue(wf.getCbVorgang().getReturnOk()); });
        workflow().doWithWorkflow(wf -> { assertNotNull(wf.getCbVorgang().getAnsweredAt()); });
        workflow().doWithWorkflow(wf -> {
            assertEquals(wf.getCbVorgang().getStatus(), CBVorgang.STATUS_ANSWERED); });
        workflow().doWithWorkflow(wf -> {
            assertEquals(wf.getCbVorgang().getReturnRealDate(), wf.getCbVorgang().getVorgabeMnet()); });
    }

    @CitrusTest(name = "GeschaeftsfallKueKd_WorkflowShouldBeClosedAfterKueKdWithAbmEntmForKvz_AccTest")
    public void workflowShouldBeClosedAfterKueKdWithAbmAndEntmForKvz() throws Exception {
        useCase(WitaAcceptanceUseCase.KUE_KD_QEB_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        CreatedData createdData =
                createData(workflow, WitaSimulatorTestUser.KUE_KD_QEB_ABM_ERLM_ENTM, "9KW/82100/82100/123456");
        workflow().select(workflow).send(createdData, CBVorgang.TYP_KUENDIGUNG);

        hurrican().assertCbFieldsForKuendigung(createdData, false, true);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), "productIdentifier");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().doWithWorkflow(wf -> { wf.waitForENTMWorkflowClosed(); });

        workflow().doWithWorkflow(wf -> { assertTrue(wf.getCbVorgang().getReturnOk()); });
        workflow().doWithWorkflow(wf -> { assertNotNull(wf.getCbVorgang().getAnsweredAt()); });
        workflow().doWithWorkflow(wf -> {
            assertEquals(wf.getCbVorgang().getStatus(), CBVorgang.STATUS_ANSWERED); });
        workflow().doWithWorkflow(wf -> {
            assertEquals(wf.getCbVorgang().getReturnRealDate(), wf.getCbVorgang().getVorgabeMnet()); });
        workflow().doWithWorkflow(wf -> { wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.KVZ_2H); });
    }


    @CitrusTest(name = "GeschaeftsfallKueKd_WorkflowShouldBeClosedAfterKueKdWithQebAndStornoWithEntm_AccTest")
    public void workflowShouldBeClosedAfterKueKdWithQebAndStornoWithEntm() throws Exception {
        useCase(WitaAcceptanceUseCase.KUE_KD_QEB_STORNO_ERLM_ENTM, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        CreatedData createdData =
                createData(workflow, WitaSimulatorTestUser.KUE_KD_QEB_STORNO_ERLM_ENTM);
        workflow().select(workflow).send(createdData, CBVorgang.TYP_KUENDIGUNG);

        hurrican().assertCbFieldsForKuendigung(createdData, false, true);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), "productIdentifier");

        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID);
        workflow().waitForQEB();

        workflow().sendStorno();
        atlas().receiveOrder("STORNO");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().doWithWorkflow(wf -> { wf.waitForENTMWorkflowClosed(); });

        workflow().doWithWorkflow(wf -> { assertTrue(wf.getCbVorgang().getReturnOk()); });
        workflow().doWithWorkflow(wf -> { assertNotNull(wf.getCbVorgang().getAnsweredAt()); });
        workflow().doWithWorkflow(wf -> {
            assertEquals(wf.getCbVorgang().getStatus(), CBVorgang.STATUS_ANSWERED); });
    }


    @CitrusTest(name = "GeschaeftsfallKueKd_WorkflowShouldBeClosedAfterKueKdWithQebAbmAndTvWithAbmErlmEntm_AccTest")
    public void workflowShouldBeClosedAfterKueKdWithQebAbmAndTvWithAbmErlmEntm() throws Exception {
        useCase(WitaAcceptanceUseCase.KUE_KD_QEB_ABM_TV, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        CreatedData createdData =
                createData(workflow, WitaSimulatorTestUser.KUE_KD_QEB_ABM_TV);
        workflow().select(workflow).send(createdData, CBVorgang.TYP_KUENDIGUNG);

        hurrican().assertCbFieldsForKuendigung(createdData, false, true);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), "productIdentifier");

        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID);
        workflow().waitForQEB();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        workflow().sendTv();
        atlas().receiveNotification("TV");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().doWithWorkflow(wf -> { wf.waitForENTMWorkflowClosed(); });

        workflow().doWithWorkflow(wf -> { assertTrue(wf.getCbVorgang().getReturnOk()); });
        workflow().doWithWorkflow(wf -> { assertNotNull(wf.getCbVorgang().getAnsweredAt()); });
        workflow().doWithWorkflow(wf -> { assertNotEquals(wf.getCbVorgang().getVorgabeMnet(), wf.getCbVorgang().getPreviousVorgabeMnet()); });
        workflow().doWithWorkflow(wf -> { assertNotEquals(wf.getCbVorgang().getReturnRealDate(), wf.getCbVorgang().getVorgabeMnet()); });
    }


    @CitrusTest(name = "GeschaeftsfallKueKd_PositiveStorno_AccTest")
    public void testCloseCbVorgangForPositiveStornoOnKuendigung() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_KUENDIGUNG_KUE_KD_04, getWitaVersionForAcceptanceTest());

        AcceptanceTestWorkflow workflow = workflow().get();
        CreatedData createdData = createData(workflow, WitaSimulatorTestUser.TAL_KUENDIGUNG_KUE_KD_04);
        workflow().select(workflow).send(createdData, CBVorgang.TYP_KUENDIGUNG);

        hurrican().assertCbFieldsForKuendigung(createdData, false, true);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", createdData.carrierbestellung.getVtrNr());

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        hurrican().closeCBVorgang();
        hurrican().assertCbFieldsForKuendigung(createdData, false, false);

        // @formatter:off
        workflow().sendStorno();
        atlas().receiveNotification("STORNO");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        // @formatter:on

        hurrican().closeCBVorgang();
        hurrican().assertCbFieldsForKuendigung(createdData, true, true);
    }

    private CreatedData createData(AcceptanceTestWorkflow workflow, WitaSimulatorTestUser witaSimulatorTestUser) {
        return createData(workflow, witaSimulatorTestUser, "96W/82100/82100/1234");
    }

    /**
     * Creates new cbVorgang test data.
     * @param workflow
     * @param witaSimulatorTestUser
     * @return
     */
    private CreatedData createData(AcceptanceTestWorkflow workflow, WitaSimulatorTestUser witaSimulatorTestUser, String lbz) {
        Date cbRealDate = Date.from(LocalDate.now().minusMonths(6).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant());
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(witaSimulatorTestUser)
                .withCarrierbestellungRealDate(cbRealDate)
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungLbz(lbz)
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer());
        try {
            return workflow.createData(testData);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to create test data", e);
        }
    }

}
