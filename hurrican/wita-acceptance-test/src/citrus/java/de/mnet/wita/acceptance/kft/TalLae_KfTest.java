/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2015
 */
package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.AbbmMeldungsCode.*;
import static de.mnet.wita.WitaSimulatorTestUser.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.GeschaeftsfallTyp;

@Test(groups = BaseTest.ACCEPTANCE)
public class TalLae_KfTest extends AbstractWitaAcceptanceTest {

    /**
     * Vorbedingung für Leistungsänderungauftrag - Auftrag muss erst erledigt werden. Es wird eine andere
     * Produktvariante der gleichen Produktgruppe (z.B. von niederbit auf hochbit) als im Auftrag aus der Vorbedingung
     * beantragt.
     * LAE Auftrag verzögert sich.
     * <p/>
     * "QEB / 0000 ABM / 0000 ERLM / 0010 ENTM / 0010 "
     * <p/>
     * "QEB / 0000 ABM / 0000 VZM /0071 ERLM / 0010 ENTM / 0010 "
     */
    @CitrusTest(name = "TalLae_01_KfTest")
    public void talLae01() throws Exception {
        CreatedData baseOrder = createBaseOrder(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM_NBIT,
                TAL_AENDERUNG_LAE_01_NEUBESTELLUNG);

        useCase(WitaAcceptanceUseCase.TAL_AENDERUNG_LAE_01, getWitaVersionForAcceptanceTest());

        AcceptanceTestWorkflow workflowLae = workflow().get();
        workflow().select(workflowLae).send(workflowLae.createData(getLaeBuilder(
                        TAL_AENDERUNG_LAE_01, Produkt.AK_CONNECT, baseOrder.auftrag.getAuftragId())),
                CBVorgang.TYP_PORTWECHSEL);

        atlas().receiveOrder("LAE");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("VZM");
        workflow().waitForVZM();

        variables().add("delayDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 25)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.assertStandortKundeSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertStandortKundeSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertMontageleistungSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertUebertragungsverfahrenSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertSchaltangabenSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertGeschaeftsfallTyp(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG); });
    }


    /**
     * Vorbedingung für Leistungsänderungauftrag. Nach der QEB des Auftrages aus der Vorbedingung wird ein LAE Auftrag
     * gesendet.
     * <p/>
     * "QEB / 0000 ABM / 0000 ERLM / 0010 ENTM / 0010 "
     * <p/>
     * "QEB / 0000 ABBM / 1012 "
     */
    @CitrusTest(name = "TalLae_02_KfTest")
    public void talLae02() throws Exception {
        CreatedData baseOrder = createBaseOrder(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM_NBIT,
                TAL_AENDERUNG_LAE_02_NEUBESTELLUNG);

        useCase(WitaAcceptanceUseCase.TAL_AENDERUNG_LAE_02, getWitaVersionForAcceptanceTest());

        AcceptanceTestWorkflow workflowLae = workflow().get();
        workflow().select(workflowLae).send(workflowLae.createData(getLaeBuilder(
                        TAL_AENDERUNG_LAE_02, null, baseOrder.auftrag.getAuftragId())),
                CBVorgang.TYP_PORTWECHSEL);

        atlas().receiveOrder("LAE");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABBM");
        workflow().waitForNonClosingAbbm(AUFTRAG_ALREADY_EXISTS);
    }


    private CreatedData createBaseOrder(WitaAcceptanceUseCase useCase, WitaSimulatorTestUser userName) throws Exception {
        useCase(useCase, getWitaVersionForAcceptanceTest());

        AcceptanceTestWorkflow workflow = workflow().get();
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(userName)
                .withUetv(Uebertragungsverfahren.N01)
                .withRangSsType("2N")
                .withRangSchnittstelle(RangSchnittstelle.N)
                .withHurricanProduktId(Produkt.PROD_ID_ISDN_TK);
        CreatedData createdData = workflow.createData(testData);

        workflow().select(workflow).send(createdData, CBVorgang.TYP_NEU);
        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        variables().add(VariableNames.LBZ_LEITUNGSSCHLUESSELZAHL, "96U");
        variables().add(VariableNames.LBZ_ONKZ_A, "4401");
        variables().add(VariableNames.LBZ_ONKZ_B, "4401");
        variables().add(VariableNames.LBZ_ORDNUNGSNUMMER, "0123456789");

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });
        return createdData;
    }

    private AcceptanceTestDataBuilder getLaeBuilder(WitaSimulatorTestUser testUser, Long hurricanProduktId, Long baseOrder) {
        Long prodId = (hurricanProduktId != null) ? hurricanProduktId : Produkt.PROD_ID_MAXI_DSL_UND_ISDN;
        // @formatter:off
        return workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withUserName(testUser)
                .withHurricanProduktId(prodId)
                .withRangSsType("2H")
                .withRangSchnittstelle(RangSchnittstelle.H)
                .withUetv(Uebertragungsverfahren.H13)
                .withCarrierbestellungAuftragId4TalNa(baseOrder);
        // @formatter:on
    }

}
