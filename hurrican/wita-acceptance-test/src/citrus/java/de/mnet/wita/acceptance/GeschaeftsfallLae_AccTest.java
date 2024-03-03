/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2015
 */
package de.mnet.wita.acceptance;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.AbstractWitaWorkflowTestAction;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.WitaCBVorgang;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GeschaeftsfallLae_AccTest extends AbstractWitaAcceptanceTest {

    @CitrusTest(name = "GeschaeftsfallLae_WorkflowShouldBeClosedAfterLaeWithAbmErlmEntm_AccTest")
    public void workflowShouldBeClosedAfterLaeWithAbmErlmEntm() {
        useCase(WitaAcceptanceUseCase.LAE_QEB_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        CreatedData createdData = createData(workflow, WitaSimulatorTestUser.LAE_QEB_ABM_ERLM_ENTM);
        workflow().select(workflow).send(createdData, CBVorgang.TYP_PORTWECHSEL);

        atlas().receiveOrder("LAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        action(new AbstractWitaWorkflowTestAction("checkCbVorgang") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                WitaCBVorgang cbVorgang = workflow.getCbVorgang();
                assertEquals(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
                assertTrue(cbVorgang.getReturnOk());
                assertThat("Real return date must be set to Vorgabe Mnet", cbVorgang.getReturnRealDate(), equalTo(cbVorgang.getVorgabeMnet()));
                assertThat("Date must be set", cbVorgang.getAnsweredAt(), notNullValue());
                assertThat("LBZ nicht auf CBVorgang geschrieben", cbVorgang.getReturnLBZ(), notNullValue());
                assertThat("VtrNr nicht auf CBVorgang geschrieben", cbVorgang.getReturnVTRNR(), notNullValue());
                assertThat("GeschaeftsfallTyp nicht korrekt", cbVorgang.getWitaGeschaeftsfallTyp(), equalTo(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG));
            }
        });
    }


    @CitrusTest(name = "GeschaeftsfallLae_LaeWithQebAndStornoWithTeqErlmEntm_AccTest")
    public void workflowShouldBeClosedAfterLaeWithQebAndStornoWithTeqErlmEntm() throws Exception {
        useCase(WitaAcceptanceUseCase.LAE_QEB_STORNO_ELRM_ENTM, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        CreatedData createdData = createData(workflow, WitaSimulatorTestUser.LAE_QEB_STORNO_ELRM_ENTM);
        workflow().select(workflow).send(createdData, CBVorgang.TYP_PORTWECHSEL);

        atlas().receiveOrderWithRequestedCustomerDate("LAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotificationWithNewVariables("QEB");
        workflow().waitForQEB();

        workflow().sendStorno();
        atlas().receiveNotification("STORNO");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        action(new AbstractWitaWorkflowTestAction("checkCbVorgang") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                WitaCBVorgang result = workflow.getCbVorgang();
                assertNotNull(result);
                assertEquals(result.getStatus(), CBVorgang.STATUS_ANSWERED);
                assertTrue(result.getReturnOk());
                assertThat("Date must be set", result.getAnsweredAt(), notNullValue());
                assertThat("GeschaeftsfallTyp nicht korrekt", result.getWitaGeschaeftsfallTyp(), equalTo(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG));
            }
        });
    }


    @CitrusTest(name = "GeschaeftsfallLae_LaeWithQebAbmAndTvWithAbmErlmEntm_AccTest")
    public void workflowShouldBeClosedAfterLaeWithQebAbmAndTvWithAbmErlmEntm() throws Exception {
        useCase(WitaAcceptanceUseCase.LAE_QEB_ABM_TV_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        CreatedData createdData = createData(workflow, WitaSimulatorTestUser.LAE_QEB_ABM_TV_ABM_ERLM_ENTM);
        workflow().select(workflow).send(createdData, CBVorgang.TYP_PORTWECHSEL);

        atlas().receiveOrderWithRequestedCustomerDate("LAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotificationWithNewVariables("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        workflow().sendTv();
        atlas().receiveNotification("TV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("ABM2");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        action(new AbstractWitaWorkflowTestAction("checkCbVorgang") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                WitaCBVorgang result = workflow.getCbVorgang();
                assertNotNull(result);
                assertEquals(result.getStatus(), CBVorgang.STATUS_ANSWERED);
                assertTrue(result.getReturnOk());
                assertTrue(DateTools.isDateEqual(result.getReturnRealDate(), result.getVorgabeMnet()),
                        "Real return date must be set to new date 'terminverschiebung'");
                assertTrue(result.getVorgabeMnet().after(result.getPreviousVorgabeMnet()));
                assertThat("Date must be set", result.getAnsweredAt(), notNullValue());
                assertThat("GeschaeftsfallTyp nicht korrekt", result.getWitaGeschaeftsfallTyp(), equalTo(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG));
            }
        });
    }


    /**
     * Creates new cbVorgang test data.
     * @param workflow
     * @param witaSimulatorTestUser
     * @return
     */
    private CreatedData createData(AcceptanceTestWorkflow workflow, WitaSimulatorTestUser witaSimulatorTestUser) {
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(witaSimulatorTestUser)
                .withReferencingCbBuilder(
                        (new CarrierbestellungBuilder())
                            .withCarrier(Carrier.ID_DTAG)
                            .withLbz("96U/82100/82100/557788")
                            .withVtrNr(hurrican().getNextVertragsnummer()));

        try {
            return workflow.createData(testData);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to create test data", e);
        }
    }
}
