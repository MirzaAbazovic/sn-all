/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2015
 */
package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.WitaSimulatorTestUser.*;
import static de.mnet.wita.acceptance.common.utils.DateCalculation.*;
import static de.mnet.wita.citrus.VariableNames.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.ProduktGruppe;
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
public class TalVw_KfTest extends AbstractWitaAcceptanceTest {

    @CitrusTest(name = "TAL_VW_NEU_01")
    public void talVwNeu01() throws Exception {
        setDefaultWitaVersionTo(WitaCdmVersion.V1);
        useCase(WitaAcceptanceUseCase.TAL_WITA_VERSION_MIGR_NEU, WitaCdmVersion.V1);

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_VW_NEU_01);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        useCase(WitaAcceptanceUseCase.TAL_WITA_VERSION_MIGR_NEU, WitaCdmVersion.V2);

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
    }

    @CitrusTest(name = "TAL_VW_KUE_02")
    public void talVwKue02() throws Exception {
        setDefaultWitaVersionTo(WitaCdmVersion.V1);
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_VW_KUE_02_NEUBESTELLUNG);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");
        waitForDefaultNotificationSequence(true);

        AcceptanceTestDataBuilder kueKdBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungRealDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withCarrierbestellungLbz("96W/44010/440100/1234")
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer());

        workflow().select(workflow().get()).sendKueKd(kueKdBuilder);
        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        useCase(WitaAcceptanceUseCase.TAL_WITA_VERSION_MIGR_NEU, WitaCdmVersion.V2);

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
    }

    @CitrusTest(name = "TAL_VW_LAE_03")
    public void talVwLae03() throws Exception {
        setDefaultWitaVersionTo(WitaCdmVersion.V1);
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_VW_KUE_02_NEUBESTELLUNG);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");
        waitForDefaultNotificationSequence(true);

        AcceptanceTestDataBuilder laeBuilder = workflow().getTestDataBuilder().
                withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withUserName(TAL_VW_LAE_03)
                .withHurricanProduktId(Produkt.AK_CONNECT)
                .withRangSsType("2H")
                .withReferencingCbBuilder(
                        (new CarrierbestellungBuilder())
                                .withCarrier(Carrier.ID_DTAG)
                                .withLbz("96U/44010/44010/557788")
                                .withVtrNr(hurrican().getNextVertragsnummer()),
                        Uebertragungsverfahren.N01, RangSchnittstelle.N);

        workflow().select(workflow().get()).sendLae(laeBuilder, Auftragsposition.ProduktBezeichner.HVT_2H);
        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        useCase(WitaAcceptanceUseCase.TAL_WITA_VERSION_MIGR_NEU, WitaCdmVersion.V2);

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
    }

    @CitrusTest(name = "TAL_VW_PV_04")
    public void talVwPv04() throws Exception {
        setDefaultWitaVersionTo(WitaCdmVersion.V1);
        useCase(WitaAcceptanceUseCase.TAL_VW_PV_04, WitaCdmVersion.V1);

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, Carrier.ID_QSC)
                .withUserName(TAL_VW_PV_04);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_ANBIETERWECHSEL);

        atlas().receiveOrder("PV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        action(new AbstractTestAction() {
            public void doExecute(TestContext context) {
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable(REQUESTED_CUSTOMER_DATE)));
            }
        });

        useCase(WitaAcceptanceUseCase.TAL_VW_PV_04, WitaCdmVersion.V2);

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
    }

    @CitrusTest(name = "TAL_VW_VBLT_05")
    public void talVwVblt05() throws Exception {
        setDefaultWitaVersionTo(WitaCdmVersion.V1);
        useCase(WitaAcceptanceUseCase.TAL_WITA_VERSION_MIGR_NEU, WitaCdmVersion.V1);

        final AcceptanceTestDataBuilder builder = workflow().getTestDataBuilder()
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withCreateAnschlussinhaberAddress(false).withVorgabeMnet(getPortDate())
                .withTaifunData(getTaifunDataFactory().withActCarrier(TNB.AKOM).surfAndFonWithDns(1))
                .withUserName(TAL_VW_VBLT_05);

        workflow().select(workflow().get()).sendVbl(builder);
        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        action(new AbstractTestAction() {
            public void doExecute(TestContext context) {
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable(REQUESTED_CUSTOMER_DATE)));
            }
        });

        atlas().sendNotificationWithNewVariables("ABM",
                VariableNames.CONTRACT_ID,
                VariableNames.PRODUCT_IDENTIFIER,
                VariableNames.THIRD_PARTY_SALESMAN_CUSTOMER_ID);
        workflow().waitForABM();

        useCase(WitaAcceptanceUseCase.TAL_VW_PV_04, WitaCdmVersion.V2);

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
    }

    @CitrusTest(name = "TAL_VW_SER_POW_06")
    public void talVwSerPow06() throws Exception {
        setDefaultWitaVersionTo(WitaCdmVersion.V1);
        useCase(WitaAcceptanceUseCase.TAL_VW_SER_POW_06, WitaCdmVersion.V1);

        final String montagehinweis = "#OSL_Bereit_MM#";
        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_VW_NEU_01)
                .withCBVorgangMontagehinweis(montagehinweis);;

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        waitForDefaultNotificationSequence(true);

        useCase(WitaAcceptanceUseCase.TAL_VW_SER_POW_06, WitaCdmVersion.V1);

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        workflow().select(workflow2);
        workflow().sendSerPow(WitaSimulatorTestUser.TAL_SERVICE_POW_01)
                .testDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withCBVorgangMontagehinweis(montagehinweis)
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId());

        workflow().doWithWorkflow(wf -> wf.assertGeschaeftsfallTyp(GeschaeftsfallTyp.PORTWECHSEL));
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));

        atlas().receiveOrder("POW2");
/*
        atlas().sendNotification("QEB2");
        workflow().waitForQEB();

        variables().add("delayDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");

        atlas().sendNotification("ABM2");
        workflow().waitForABM();

        atlas().sendNotification("ERLM2");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM2");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
        */
    }

    @CitrusTest(name = "TAL_VW_REX-MK_07")
    public void talVwRexMk07() throws Exception {
    }

    @CitrusTest(name = "TAL_VW_PGW_08")
    public void talVwPgw08() throws Exception {
    }

    private void waitForDefaultNotificationSequence(boolean closeCbVorgang) {
        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        if (closeCbVorgang) {
            workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
        }
    }
}
