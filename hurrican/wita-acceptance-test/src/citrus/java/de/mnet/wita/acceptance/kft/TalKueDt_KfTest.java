/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2015
 */
package de.mnet.wita.acceptance.kft;

import com.consol.citrus.dsl.annotations.CitrusTest;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import org.testng.annotations.Test;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class TalKueDt_KfTest extends AbstractWitaAcceptanceTest {

    /**
     * <ol> <li>Vorbedingung fuer Kuendigungsauftrag - Auftrag muss erst erledigt werden</li> <li>Der Provider erhält
     * eine ERLM und eine ENTM zu einem seiner Kunden (Bestand)</li> </ol>
     */
    @CitrusTest(name = "TalKueDt_01_KfTest")
    public void testTalKueDt01() throws Exception {
        sendKueDtSuccessfulForTestUser(WitaAcceptanceUseCase.TAL_KUENDIGUNG_KUE_DT_01, WitaSimulatorTestUser.TAL_KUENDIGUNG_KUE_DT_01);

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

        atlas().sendNotification("ERLM2");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM2");
        workflow().waitForENTM();

        workflow().assertWorkflowClosed();
    }

    /**
     * ENTM vor ERLM sollte auch funktionieren
     */
    @CitrusTest(name = "KueDtErlmAfterEntm_KfTest")
    public void testKueDtErlmAfterEntm() throws Exception {
        sendKueDtSuccessfulForTestUser(WitaAcceptanceUseCase.KUEDT_ENTM_ERLM, WitaSimulatorTestUser.KUEDT_ENTM_ERLM);

        atlas().sendNotification("ENTM2");

        if (atlas().getCdmVersion() != null) {
            atlas().receiveError("ERROR", AtlasEsbConstants.HUR_ERROR_CODE, "WITA_TECH_1002");

            workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

            atlas().sendNotification("ERLM2");
            workflow().waitForERLM();

            // simulate the error service by resending the ENTM2
            atlas().sendNotification("ENTM2");
            workflow().waitForENTM();
        }
        else {
            workflow().waitForENTM();

            workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

            atlas().sendNotification("ERLM2");
            workflow().waitForERLM();
        }

        workflow().assertWorkflowClosed();
    }

    private void sendKueDtSuccessfulForTestUser(WitaAcceptanceUseCase useCase, WitaSimulatorTestUser userName) throws Exception {
        useCase(useCase, getWitaVersionForAcceptanceTest());

        AcceptanceTestWorkflow workflow = workflow().get();
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14).atStartOfDay())
                .withUserName(userName);
        CreatedData createdData = workflow.createData(testData);

        workflow().select(workflow).send(createdData, CBVorgang.TYP_NEU);
        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
    }
}