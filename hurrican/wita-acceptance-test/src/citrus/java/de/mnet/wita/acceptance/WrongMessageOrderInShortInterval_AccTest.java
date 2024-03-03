
package de.mnet.wita.acceptance;

import static de.mnet.wita.WitaSimulatorTestUser.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;

@Test(groups = BaseTest.ACCEPTANCE)
public class WrongMessageOrderInShortInterval_AccTest extends AbstractWitaAcceptanceTest {

    /**
     * Wrong message order: ENTM -> ERROR -> ERLM -> ENTM
     */
    @CitrusTest(name = "WrongMessageOrder_TestErlmAfterEntm")
    public void testErlmAfterEntm() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ENTM_ERLM, WitaCdmVersion.V1);

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(NEU_QEB_ABM_ENTM_ERLM);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        // starting with wrong message
        atlas().sendNotification("ENTM");
        // WITA_TECH_1002 = WITA message out of order error
        atlas().receiveError("ERROR", AtlasEsbConstants.HUR_ERROR_CODE, "WITA_TECH_1002");

        // send correct message
        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        // resend ENTM
        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });
    }

    /**
     * Wrong message order ENTM_PV before ERLM_PV
     */
    @CitrusTest(name = "WrongMessageOrder_ErlmPvAfterEntmPv")
    @Test(enabled = false) // TODO: unstable test
    public void testErlmPvAfterEntmPv() throws Exception {
        useCase(WitaAcceptanceUseCase.AKMPV_RUEMPV_ABMPV_ENTM_ERLM, WitaCdmVersion.V1);

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(AKMPV_RUEMPV_ABMPV_ENTM_ERLM);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();


        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 7)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");
        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        workflow().doWithWorkflow(wf -> { wf.sendRuemPv(); });
        atlas().receiveNotification("RUEM-PV");

        atlas().sendNotification("ABM-PV");
        workflow().waitForAbmPv();

        // simulating wrong message order
        atlas().sendNotification("ENTM-PV");
        atlas().receiveError("ERROR", AtlasEsbConstants.HUR_ERROR_CODE, "WITA_TECH_1002");

        // continue with correct one
        atlas().sendNotification("ERLM-PV");
        workflow().waitForErlmPv();

        atlas().sendNotification("ENTM-PV");
        workflow().waitForEntmPv();
    }

}
