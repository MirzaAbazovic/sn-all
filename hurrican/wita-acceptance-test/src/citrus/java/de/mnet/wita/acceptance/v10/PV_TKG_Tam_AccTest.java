/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.08.2016
 */
package de.mnet.wita.acceptance.v10;

import static de.mnet.wita.WitaSimulatorTestUser.*;
import static de.mnet.wita.acceptance.common.utils.DateCalculation.*;
import static de.mnet.wita.acceptance.v10.PV_TKG_Tam_AccTest.WorkflowCondition.*;
import static de.mnet.wita.citrus.VariableNames.*;

import java.time.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;

@Test(groups = BaseTest.ACCEPTANCE)
public class PV_TKG_Tam_AccTest extends AbstractWitaAcceptanceTest {

    @CitrusTest(name = "GeschaeftsfallPv_Tam_MTam_After_RuemPv_AccTest")
    public void testThatTamAndMTamIsPosibleAfterRuemPv() throws Exception {

        String meldungsCode = "6002";
        String meldungsText = "nicht ausführbar aus Endkundengrund";
        doTestTamWithPvWorkflow(WorkflowCondition.PROCESS_COMPLETE_WORKFLOW, meldungsCode, meldungsText);
    }

    @CitrusTest(name = "GeschaeftsfallPv_Tam_MTam_Open_CbVorgang_AccTest")
    public void testThatTamAndMTamCreateOpenCbVorgang() throws Exception {
        String meldungsCode = "6002";
        String meldungsText = "nicht ausführbar aus Endkundengrund";
        doTestTamWithPvWorkflow(WorkflowCondition.ONLY_TAM, meldungsCode, meldungsText);
    }

    @CitrusTest(name = "GeschaeftsfallPv_Tam_MTam_Open_CbVorgang_AccTest")
    public void testThatTamAndMTamCreateOpenCbVorgangWithCode6012() throws Exception {
        String meldungsCode = "6012";
        String meldungsText = "nicht ausführbar aus Endkundengrund";
        doTestTamWithPvWorkflow(WorkflowCondition.ONLY_TAM, meldungsCode, meldungsText);
    }

    private void doTestTamWithPvWorkflow(WorkflowCondition workflowCondition, String meldungsCode, String meldungsText) throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PV_TAM, getWitaVersionForAcceptanceTest());
        variables().add(TESTFALL_ID, TAL_PROVIDERWECHSEL_TAM_MTAM_RUEMPV.getName());

        final LocalDate vorgabeMNet = DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                DateCalculationHelper.addWorkingDays(LocalDate.now(), 21));

        AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorgabeMnet(vorgabeMNet.atStartOfDay())
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, Carrier.ID_QSC)
                .withUserName(TAL_PROVIDERWECHSEL_TAM_MTAM_RUEMPV);

        workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL);

        variable("testcaseId", TAL_PROVIDERWECHSEL_TAM_MTAM_RUEMPV.getName());
        variable("contractId", "citrus:randomNumber(10)");
        atlas().receiveOrder("PV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("RUEM-PV");

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        variable("meldungsText", meldungsText);
        variable("meldungsCode", meldungsCode);

        atlas().sendNotification("TAM");
        workflow().waitForTAM();
        atlas().sendNotification("MTAM");
        workflow().waitForMTAM();

        if (PROCESS_COMPLETE_WORKFLOW.equals(workflowCondition)) {
            atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
            workflow().waitForABM();
            workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
            atlas().sendNotification("ERLM");
            workflow().waitForERLM();

            atlas().sendNotification("ENTM");
            workflow().waitForENTM();
        }
    }

    enum WorkflowCondition {
        PROCESS_COMPLETE_WORKFLOW,
        ONLY_TAM
    }

}





