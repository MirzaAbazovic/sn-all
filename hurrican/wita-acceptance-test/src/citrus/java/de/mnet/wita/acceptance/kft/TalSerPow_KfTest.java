/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2015
 */
package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.WitaSimulatorTestUser.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Auftragsposition;

@Test(groups = BaseTest.ACCEPTANCE)
public class TalSerPow_KfTest extends AbstractWitaAcceptanceTest {

    /**
     * Vorbedingung für Portwechselauftrag - Auftrag muss erst erledigt werden Es wird eine Änderung der Anschaltung am
     * Verteilerstreifen HVt-ÜVt beantragt.
     * <p/>
     * "CuDA 2 Draht (HVt) CuDA 2 Draht hbr (HVt) CuDA 4 Draht hbr (HVt)"
     * <p/>
     * Bereitstellung: "QEB / 0000 ABM / 0000 ERLM / 0010 ENTM / 0010 "
     * <p/>
     * Portwechsel "QEB / 0000 ABM / 0000 ERLM / 0010 ENTM / 0010"
     */
    @CitrusTest(name = "TalSerPow01_KfTest")
    public void talSerPow01() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        final String montagehinweis = "#OSL_Bereit_MM#";
        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_SERVICE_POW_01_NEUBESTELLUNG)
                .withCBVorgangMontagehinweis(montagehinweis);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });

        workflow().doWithWorkflow(wf -> { wf.assertStandortKundeSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertUebertragungsverfahrenSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertSchaltangabenSet(); });

        //////////////////////////////////////////////////////
        useCase(WitaAcceptanceUseCase.TAL_SERVICE_POW_01, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        workflow().select(workflow2);
        workflow().sendSerPow(WitaSimulatorTestUser.TAL_SERVICE_POW_01)
                .testDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withCBVorgangMontagehinweis(montagehinweis)
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId());

        workflow().doWithWorkflow(wf -> { wf.assertGeschaeftsfallTyp(GeschaeftsfallTyp.PORTWECHSEL); });
        workflow().doWithWorkflow(wf -> { wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H); });

        atlas().receiveOrder("POW");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        variables().add("delayDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.assertUebertragungsverfahrenSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertSchaltangabenSet(); });
    }

}
