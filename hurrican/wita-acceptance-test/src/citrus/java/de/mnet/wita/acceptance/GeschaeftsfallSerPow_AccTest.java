/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.2015
 */
package de.mnet.wita.acceptance;

import static de.mnet.wita.WitaSimulatorTestUser.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;

/**
 * Acceptance Tests fuer WITA Geschaeftsfall 'SerPow'.
 * (Test basierend auf Citrus ohne zusaetzlich notwendigen / eigenstaendig laufenden WITA Simulator Prozess)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GeschaeftsfallSerPow_AccTest extends AbstractWitaAcceptanceTest {

    @CitrusTest(name = "GeschaeftsfallSerPow_Storno_AccTest")
    public void storno() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());
        CreatedData createdData = createBaseOrderForSerPow();
        useCase(WitaAcceptanceUseCase.SER_POW_QEB_STORNO_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        workflow().sendSerPow(WitaSimulatorTestUser.SER_POW_QEB_STORNO_ERLM_ENTM)
            .testDataBuilder().withCarrierbestellungAuftragId4TalNa(createdData.auftrag.getAuftragId());
        atlas().receiveOrderWithRequestedCustomerDate("POW");

        atlas().sendNotification("QEB2");
        workflow().waitForQEB();

        workflow().sendStorno();
        atlas().receiveOrder("STORNO");

        atlas().sendNotification("ERLM2");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM2");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { Assert.assertTrue(wf.getCbVorgang().isStorno()); } );
    }


    @CitrusTest(name = "GeschaeftsfallSerPow_Terminverschiebung_AccTest")
    public void terminverschiebung() throws Exception {
        useCase(WitaAcceptanceUseCase.SER_POW_QEB_TV_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        CreatedData createdData = createBaseOrderForSerPow();

        workflow().sendSerPow(WitaSimulatorTestUser.SER_POW_QEB_TV_ABM_ERLM_ENTM)
                .testDataBuilder().withCarrierbestellungAuftragId4TalNa(createdData.auftrag.getAuftragId());
        atlas().receiveOrderWithRequestedCustomerDate("POW");

        atlas().sendNotification("QEB2");
        workflow().waitForQEB();

        workflow().sendTv();
        atlas().receiveOrder("TV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE_TV);;

        atlas().sendNotification("ABM2");
        workflow().waitForABM();

        atlas().sendNotification("ERLM2");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM2");
        workflow().waitForENTM();
    }


    /* Erstellt eine WITA Neubestellung mit NEU_QEB_ABM_ERLM_ENTM, die als Basis fuer die SerPow Testfaelle dient. */
    private CreatedData createBaseOrderForSerPow() throws Exception {
        AcceptanceTestWorkflow workflow = workflow().get();
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(NEU_QEB_ABM_ERLM_ENTM);
        CreatedData createdData = workflow.createData(testData);

        workflow().select(workflow).send(createdData, CBVorgang.TYP_NEU);
        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });
        return createdData;
    }

}
