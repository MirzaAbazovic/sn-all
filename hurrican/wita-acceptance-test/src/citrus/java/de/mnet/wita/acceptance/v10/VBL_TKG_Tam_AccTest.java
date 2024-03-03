/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.08.2016
 */
package de.mnet.wita.acceptance.v10;

import static de.mnet.wita.WitaSimulatorTestUser.*;
import static de.mnet.wita.acceptance.common.utils.DateCalculation.*;
import static de.mnet.wita.citrus.VariableNames.*;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.WbciTestDataBuilder;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;

@Test(groups = BaseTest.ACCEPTANCE)
public class VBL_TKG_Tam_AccTest extends AbstractWitaAcceptanceTest {

    @CitrusTest(name = "GeschaeftsfallVbl_Tam_MTam_After_RuemPv_AccTest")
    public void testThatTamAndMTamIsPosibleAfterRuemPv() throws Exception {
        doTestTamWithVblWorkflow(WorkflowCondition.PROCESS_COMPLETE_WORKFLOW);
    }

    @CitrusTest(name = "GeschaeftsfallVbl_Tam_MTam_Open_CbVorgang_AccTest")
    public void testThatTamAndMTamCreateOpenCbVorgang() throws Exception {
        doTestTamWithVblWorkflow(WorkflowCondition.ONLY_TAM);
    }


    private void doTestTamWithVblWorkflow(WorkflowCondition workflowCondition) throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_VBL_TAM, getWitaVersionForAcceptanceTest());

        String montagehinweis = "#OSL_Bereit_MM#";
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");
        variables().add(VariableNames.PRODUCT_IDENTIFIER, "TAL; CuDA 2 Draht hbr (HVt)");
        variables().add(TESTFALL_ID, TAL_VBL_TAM_MTAM_RUEMPV.getName());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().withActCarrier(TNB.AKOM).surfAndFonWithDns(1))
                .withVorgabeMnet(getPortDate())
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withCBVorgangMontagehinweis(montagehinweis)
                .withTerminReservierungsId("1000000000")
                .withFirma()
                .withUetv(Uebertragungsverfahren.H04)
                .withUserName(TAL_VBL_TAM_MTAM_RUEMPV.getName());

        final WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        testDataBuilder.withVorabstimmungsId(wbciTestDataBuilder.getWbciVorabstimmungsId());
        wbciTestDataBuilder.createWbciVorabstimmungForAccpetanceTestBuilder(testDataBuilder, WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG);

        workflow()
                .select(workflow().get())
                .sendVbl(testDataBuilder);

        variable("contractId", "citrus:randomNumber(10)");
        atlas().receiveOrder("VBL")
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

        atlas().sendNotification("TAM");
        workflow().waitForTAM();
        atlas().sendNotification("MTAM");
        workflow().waitForMTAM();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        if (workflowCondition.equals(WorkflowCondition.PROCESS_COMPLETE_WORKFLOW)) {
            workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
            atlas().sendNotification("ERLM");
            workflow().waitForERLM();

            atlas().sendNotification("ENTM");
            workflow().waitForENTM();
        }
    }

    private enum WorkflowCondition {
        PROCESS_COMPLETE_WORKFLOW,
        ONLY_TAM
    }

}

