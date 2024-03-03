/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2015
 */
package de.mnet.wita.acceptance;

import static de.augustakom.hurrican.model.cc.Carrier.*;
import static de.mnet.wita.WitaSimulatorTestUser.*;
import static org.testng.Assert.*;

import java.time.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.google.common.collect.Iterables;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.auftrag.Auftragsposition;

@Test(groups = BaseTest.ACCEPTANCE)
public class GeschaeftsfallLmae_AccTest extends AbstractWitaAcceptanceTest {

    private final String PRODUCT_IDENTIFIER = "productIdentifier";
    private final String PRODUCT_IDENTIFIER_1 = "TAL; CuDA 4 Draht hbr (HVt)";
    private final String REQUESTED_CUSTOMER_DATE = "requestedCustomerDate";
    private final String ABM_TEMPLATE = "abmTemplate";

    @CitrusTest(name = "GeschaeftsfallLmae_dtagPortChange_AccTest")
    public void dtagPortChange() throws Exception {

        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_01, WitaCdmVersion.V1);
        final AcceptanceTestWorkflow workflow = workflow().get();

        AcceptanceTestDataBuilder builder = workflow().getTestDataBuilder()
                .withUserName(TAL_AENDERUNG_AEN_LMAE_03_VBL)
                .withTaifunData(getTaifunDataFactory()
                                .withActCarrier(TNB.AKOM)
                                .surfAndFonWithDns(3)
                )
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, ID_DTAG)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate());

        final CreatedData baseData = workflow.createData(builder);

        workflow().select(workflow).send(baseData, CBVorgang.TYP_ANBIETERWECHSEL);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        variables().add("contractId", "citrus:randomNumber(10)");

        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                context.setVariable(ABM_TEMPLATE, "1");

                String identifier = context.getVariable(PRODUCT_IDENTIFIER);
                if (PRODUCT_IDENTIFIER_1.equalsIgnoreCase(identifier)) {
                    context.setVariable(ABM_TEMPLATE, "3");
                }
            }
        });

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        conditional(
                atlas().sendNotification("ABM")
        ).when("${" + ABM_TEMPLATE + "} = 1");
        conditional(
                atlas().sendNotification("ABM_3")
        ).when("${" + ABM_TEMPLATE + "} = 3");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });

        workflow().doWithWorkflow(wf -> { wf.assertBestandssucheSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertRufnummernPortierungEinzelanschlussSet(3); });

        useCase(WitaAcceptanceUseCase.TAL_AENDERUNG_AEN_LMAE_01, WitaCdmVersion.V1);

        final Uebertragungsverfahren[] oldUetv = new Uebertragungsverfahren[1];
        workflow().doWithWorkflow(wf -> {
            Carrierbestellung cb = carrierService.findCB(baseData.carrierbestellung.getId());
            Endstelle endstelle = Iterables.getOnlyElement(endstellenService.findEndstellen4Carrierbestellung(cb));
            Equipment equipment = rangierungsService.findEquipment4Endstelle(endstelle, false, true);
            oldUetv[0] = equipment.getUetv();
        });

        Uebertragungsverfahren newUetv = Uebertragungsverfahren.H13;

        workflow().sendLmae(WitaSimulatorTestUser.TAL_AENDERUNG_AEN_LMAE_01)
                .testDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 14).atStartOfDay())
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId())
                .withUetv(newUetv);

//        workflow().doWithWorkflow(wf -> { wf.sendLmaeForDtagPortChange(TAL_AENDERUNG_AEN_LMAE_01, newUetv); });

        atlas().receiveOrder("AEN_LMAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        variables().add("delayDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");

        atlas().sendNotification("VZM");
        workflow().waitForVZM();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });

        workflow().doWithWorkflow(wf -> {
            Carrierbestellung cb = carrierService.findCB(wf.getCreatedData().carrierbestellung.getId());  // that's it!
            Endstelle endstelle = Iterables.getOnlyElement(endstellenService.findEndstellen4Carrierbestellung(cb));
            Equipment equipment = rangierungsService.findEquipment4Endstelle(endstelle, false, true);
            Uebertragungsverfahren actualNewUetv = equipment.getUetv();

            assertEquals(actualNewUetv, newUetv);
        });
        workflow().doWithWorkflow(wf -> { wf.assertDtagPortChangeUebertragungsverfahrenSet(newUetv, oldUetv[0]); });
    }

    @CitrusTest(name = "GeschaeftsfallLmae_vbl4drahtWithLmae_AccTest")
    public void vbl4drahtWithLmae() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_01, WitaCdmVersion.V1);
        final AcceptanceTestWorkflow workflow = workflow().get();
        AcceptanceTestDataBuilder builder = workflow().getTestDataBuilder()
                .withUserName(WitaSimulatorTestUser.TAL_AENDERUNG_AEN_LMAE_03_VBL)
                .withTaifunData(
                        getTaifunDataFactory()
                                .withActCarrier(TNB.AKOM)
                                .surfAndFonWithDns(3)
                )
                .withVierDraht("02", "02")
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate());

        final CreatedData baseData = workflow.createData(builder);

        workflow().select(workflow).send(baseData, CBVorgang.TYP_ANBIETERWECHSEL);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        variables().add("contractId", "citrus:randomNumber(10)");

        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                context.setVariable(ABM_TEMPLATE, "1");

                String identifier = context.getVariable(PRODUCT_IDENTIFIER);
                if (PRODUCT_IDENTIFIER_1.equalsIgnoreCase(identifier)) {
                    context.setVariable(ABM_TEMPLATE, "3");
                }
            }
        });

        atlas().sendNotification("QEB");

        workflow().waitForQEB();

        conditional(
                atlas().sendNotification("ABM")
        ).when("${" + ABM_TEMPLATE + "} = 1");

        conditional(
                atlas().sendNotification("ABM_3")
        ).when("${" + ABM_TEMPLATE + "} = 3");

        workflow().waitForABM();

        atlas().sendNotification("ERLM");

        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });

        workflow().assertBestandssucheSet();

        workflow().assertRufnummernPortierungEinzelanschlussSet(3);

        useCase(WitaAcceptanceUseCase.TAL_AENDERUNG_AEN_LMAE_03, WitaCdmVersion.V1);

        AcceptanceTestDataBuilder builder1 = workflow().getTestDataBuilder()
                .withUserName(WitaSimulatorTestUser.TAL_AENDERUNG_AEN_LMAE_03)
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId())
                .withUetv(Uebertragungsverfahren.H13)
                .withVierDraht("02", "02");

        workflow().doWithWorkflow(wf -> { wf.sendLmae(builder1, Auftragsposition.ProduktBezeichner.HVT_4H); });

        atlas().receiveOrder("AEN_LMAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");
        atlas().sendNotification("QEB");

        workflow().waitForQEB();

        atlas().sendNotification("ABBM");

        workflow().waitForABBM();

        workflow().assertUebertragungsverfahrenSet();

    }

    @CitrusTest(name = "GeschaeftsfallLmae_Terminverschiebung_AccTest")
    public void terminverschiebung() throws Exception {
        CreatedData baseData = createBaseOrderForLmae();

        useCase(WitaAcceptanceUseCase.LMAE_QEB_TV_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        workflow().sendLmae(WitaSimulatorTestUser.LMAE_QEB_TV_ABM_ERLM_ENTM)
                .testDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId())
                .withUetv(Uebertragungsverfahren.H13);

        atlas().receiveOrder("AEN_LMAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().sendTv();
        atlas().receiveNotification("TV");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

    }

    @CitrusTest(name = "GeschaeftsfallLmae_StandardGutfall_AccTest")
    public void lmaeStandardGutfall() throws Exception {
        CreatedData baseData = createBaseOrderForLmae();

        useCase(WitaAcceptanceUseCase.LMAE_QEB_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        workflow().sendLmae(WitaSimulatorTestUser.LMAE_QEB_ABM_ERLM_ENTM)
                .testDataBuilder()
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId())
                .withUetv(Uebertragungsverfahren.H13);

        atlas().receiveOrder("AEN_LMAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
    }


    @CitrusTest(name = "GeschaeftsfallLmae_Storno_AccTest")
    public void storno() throws Exception {
        CreatedData baseData = createBaseOrderForLmae();

        useCase(WitaAcceptanceUseCase.LMAE_QEB_STORNO_ERLM_ENTM, WitaCdmVersion.V1);

        workflow().sendLmae(WitaSimulatorTestUser.LMAE_QEB_STORNO_ERLM_ENTM)
                .testDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId())
                .withUetv(Uebertragungsverfahren.H13);

        atlas().receiveOrder("AEN_LMAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().sendStorno();
        atlas().receiveNotification("STORNO");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { assertTrue(wf.getCbVorgang().isStorno()); });
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
                .withUetv(Uebertragungsverfahren.H13)
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

    private CreatedData createBaseOrderForLmae() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(NEU_QEB_ABM_ERLM_ENTM)
                .withUetv(Uebertragungsverfahren.H04);
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
