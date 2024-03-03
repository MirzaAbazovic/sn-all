/*

 * Copyright (c) 2015 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 04.11.2015

 */

package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Produkt;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.WbciTestDataBuilder;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.auftrag.Auftragsposition;

/**
 * Kft fuer Verbundleistung<br/> Created by petersde on 04.11.2015.
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class TalVbl_KfTest extends AbstractWitaAcceptanceTest {

    /*
     * Gutfall für Verbundleistung mit Einzelanschlussportierung.
     * Alter Test: TalVblKfTest#talVerbundleistungVblt01
     */
    @CitrusTest(name = "kft_v1000_tal_vblt_01")
    public void v1000_tal_vblt_01() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_01, getWitaVersionForAcceptanceTest());
        String montagehinweis = "#OSL_Bereit_MM#";
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");
        variables().add(VariableNames.PRODUCT_IDENTIFIER, "TAL; CuDA 2 Draht hbr (HVt)");
        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().withActCarrier(TNB.AKOM).surfAndFonWithDns(1))
                .withVorgabeMnet(getPortDate())
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withCBVorgangMontagehinweis(montagehinweis)
                .withTerminReservierungsId("1000000000")
                .withFirma()
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBLT_01);

        final WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        testDataBuilder.withVorabstimmungsId(wbciTestDataBuilder.getWbciVorabstimmungsId());
        wbciTestDataBuilder.createWbciVorabstimmungForAccpetanceTestBuilder(testDataBuilder, WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG);

        workflow()
                .select(workflow().get())
                .sendVbl(testDataBuilder);
        atlas()
                .receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("TAM");
        workflow().waitForTAM();

        atlas().sendNotification("MTAM");
        workflow().doWithWorkflow(AcceptanceTestWorkflow::waitForMTAM);

        atlas().sendNotification("ABM_2");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertStandortKundeIsFirma);
        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertMontageleistungSet);
        workflow().doWithWorkflow(wf -> wf.assertMontageleistungHinweis(montagehinweis));
        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestandssucheSet);
        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertSchaltangabenSet);
        workflow().doWithWorkflow(wf -> wf.assertRufnummernPortierungEinzelanschlussSet(1));
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /*
     * Weggang von Telekom - Wechsel enthält keine RNR-Portierung. Produkt nicht wechselfähig.
     */
    @CitrusTest(name = "kft_v1000_tal_vblt_02")
    public void v1000_tal_vblt_02() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_02, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().surfAndFonWithDns(0, false))
                .withHurricanProduktId(Produkt.AK_CONNECT)
                .withAddressType(CCAddress.ADDRESS_FORMAT_BUSINESS)
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBLT_02);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder("VBL").extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(),
                "requestedCustomerDate");

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.KEIN_WECHSELFAEHIGES_PRODUKT);

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertStandortKundeIsFirma);
        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestandssucheSet);
        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertRufnummernPortierungNotSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /*
     * Schlechtfall - Weggang von Telekom. <br/>
     * Der Anbieterwechsel ist nur mit dem Geschaeftsfall 'PV' moeglich.
     */
    @CitrusTest(name = "kft_ v1000_tal_vblt_03")
    public void v1000_tal_vblt_03() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_03, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().surfAndFonWithDns(0, false))
                .withCreateAnschlussinhaberAddress(false)
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBLT_03);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder("VBL").extractFromPayload(REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.ANBIETERWECHSEL_NUR_MIT_PV);

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestandssucheSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Gutfall - Zugang zu Telekom. <br/> Wechsel enthält keine RNR-Portierung, Wechselanfrage bestätigt
     */
    @CitrusTest(name = "kft_v1000_tal_vblt_04")
    public void v1000_tal_vblt_04() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_04, getWitaVersionForAcceptanceTest());
        workflow().sendBereitstellung(WitaSimulatorTestUser.TAL_PROVIDERWECHSEL_PV3_04);

        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");
        atlas().receiveOrderWithRequestedCustomerDate("NEU");
        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM");
        workflow().waitForABM();
        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 7)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");
        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().sendNotification("ABM-PV");
        workflow().waitForAbmPv();

        atlas().sendNotification("ABM-PV-2");
        workflow().waitForSecondAbmPv();

        atlas().sendNotification("ERLM-PV");
        workflow().waitForErlmPv();

        atlas().sendNotification("ENTM-PV");
        workflow().waitForEntmPv();
    }

    /*
     * Verbundleistung mit Storno.
     */
    @CitrusTest(name = "kft_v1000_tal_vblt_05")
    public void v1000_tal_vblt_05() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_05, getWitaVersionForAcceptanceTest());

        String montagehinweis = "#OSL_Bereit_MM#";
        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().withActCarrier(TNB.AKOM).surfAndFonWithDns(1))
                .withVorgabeMnet(getPortDate())
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withFirma()
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBLT_05);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas()
                .receiveOrder("VBL")
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().sendStorno();
        atlas().receiveOrder("STORNO");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestandssucheSet);
        workflow().doWithWorkflow(wf -> wf.assertRufnummernPortierungEinzelanschlussSet(1));
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Gutfall: Verbundleistung mit Terminverschiebung.
     */
    @CitrusTest(name = "kft_v1000_tal_vblt_06")
    public void v1000_tal_vblt_06() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_06, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().withActCarrier(TNB.AKOM).surfAndFonWithDns(1))
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBLT_06);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder("VBL")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate")
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        final LocalDateTime tv = getPortDate();
        workflow().sendTv(DateCalculationHelper.addWorkingDays(tv, 14).toLocalDate());
        atlas().receiveNotification("TV")
                .extractFromPayload(REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE_TV);

        variables().add(VariableNames.PRODUCT_IDENTIFIER, "TAL; CuDA 2 Draht hbr (HVt)");

        atlas().sendNotification("ABM_2");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestandssucheSet);
        workflow().doWithWorkflow(wf -> wf.assertRufnummernPortierungEinzelanschlussSet(1));
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /*
     * kft_v1000_tal_vblt_07 <br/>
     * Schlechtfall: Verbundleistung mit Terminverschiebung. <br/> KWT vor einem arbeitsfreien Tag ist nicht möglich
     * Der Test wird nicht ausgefuehrt, da Hurrican einen Samstag als Datum nicht akzeptiert
     */

    @CitrusTest(name = "kft_v1000_tal_vblt_08")
    public void v1000_tal_vblt_08() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_08, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().withActCarrier(TNB.AKOM).surfAndFonWithDns(1))
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBLT_08);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder("VBL")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate")
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        final LocalDateTime tv = getPortDate();
        workflow().sendTv(DateCalculationHelper.addWorkingDays(tv, 14).toLocalDate());
        atlas().receiveNotification("TV");

        atlas().sendNotification("ABBM");
        workflow().waitForABBM();

    }

    @CitrusTest(name = "kft_v1000_tal_vblt_09")
    public void v1000_tal_vblt_09() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_09, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().withActCarrier(TNB.AKOM).surfAndFonWithDns(1))
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBLT_09);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder("VBL")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate")
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        final LocalDateTime tv = getPortDate();
        workflow().sendTv(DateCalculationHelper.addWorkingDays(tv, 14).toLocalDate());
        atlas().receiveNotification("TV");

        atlas().sendNotification("ABBM");
        workflow().waitForABBM();
    }

    /*
     * Alter Test: TalVblKfTest#talVbl3_01
     */
    @CitrusTest(name = "kft_v1000_tal_vbl_01")
    public void kft_v1000_tal_vbl_01() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBL3_01, getWitaVersionForAcceptanceTest());

        String montagehinweis = "#OSL_Bereit_MM#";

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().surfAndFonWithDns(0, false))
                .withVorabstimmungEinzelanschluss(ProduktGruppe.ADSL_SA, Carrier.ID_QSC)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withCBVorgangMontagehinweis(montagehinweis)
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBL3_01);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder().extractFromPayload(REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER, VariableNames.RUFNUMMER_ONKZ);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
    }

    /*
     * Alter Test: TalVblKfTest#talVbl3_02
     */
    @CitrusTest(name = "TalVbl_talVbl3_02_KfTest")
    public void talVbl3_02() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBL3_02, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().surfAndFonWithDns(0, false))
                .withVorabstimmungEinzelanschluss(ProduktGruppe.ADSL_SA, Carrier.ID_QSC)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBL3_02);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder().extractFromPayload(REQUESTED_CUSTOMER_DATE.getXpath(),
                "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABBM();
    }

    @CitrusTest(name = "Kft_talVbl3_03")
    public void talVbl3_03() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBL3_03, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().surfAndFonWithDns(0, false))
                .withVorabstimmungEinzelanschluss(ProduktGruppe.ADSL_SA, Carrier.ID_QSC)
                .withVierDraht()
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBL3_03);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder("VBL").extractFromPayload(REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABBM();
    }

    /*
     * Alter Test: TalVblKfTest#talVbl3_04
     */
    @CitrusTest(name = "TalVbl_talVbl3_04_KfTest")
    public void talVbl3_04() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBL3_04, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().surfAndFonWithDns(0, false))
                .withVorabstimmungEinzelanschluss(ProduktGruppe.ADSL_SA, Carrier.ID_QSC)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBL3_04);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder().extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID)
                .extractFromPayload(REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().sendStorno();
        atlas().receiveOrder("STORNO");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
    }

    /*
     * Test als abgebender Provider (AKM-PV, ABBM-PV)
     * Testablauf:
     * - DTAG triggert Test u. schickt AKM-PV
     * - DTAG simuliert aufnehmenden Provider u. schickt Storno
     * - ABBM-PV wird empfangen
     * Keine Test-Implementierung notwendig, da M-net als abgebender Provider in diesem Testfall nicht aufgefordert ist,
     * etwas anderes als eine TEQ zu senden. Der Empfang von AKM-PV und ABBM-PV ist in anderen Tests bereits vorhanden.
     */
    public void talVbl3_05() throws Exception {
        // kein doing notwendig
    }

    /*
     * Alter Test: TalVblKfTest#talVbl3_06
     */
    @CitrusTest(name = "TalVbl_talVbl3_06_KfTest")
    public void talVbl3_06() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBL3_06, getWitaVersionForAcceptanceTest());

        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
                .withTaifunData(getTaifunDataFactory().surfAndFonWithDns(0, false))
                .withVorabstimmungEinzelanschluss(ProduktGruppe.ADSL_SA, Carrier.ID_QSC)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBL3_06);

        workflow().select(workflow().get()).sendVbl(testDataBuilder);
        atlas().receiveOrder()
                .extractFromPayload(REQUESTED_CUSTOMER_DATE.getXpath(),
                        "requestedCustomerDate")
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().sendTv();
        atlas().receiveNotification("TV").extractFromPayload(
                REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), "requestedCustomerChangeDate");

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
    }

    /*
     * Schlechtfall - Wechsel enthält RNR-Portierung am Einzelanschluss Universal, eine Rufnummer ist nicht korrekt.
     * z.Z. kein explizites Setzen von falscher Rufnummer, Universalanschluss
     */
    //    @CitrusTest(name = "TalVbl_VbltWechselMitRNRundNummerFalsch_KfTest")
    //    public void talVerbundleistungVbltWechselMitRNRundNummerFalsch() throws Exception {
    //        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_05, getWitaVersionForAcceptanceTest());
    //
    //        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder()
    //                .withTaifunData(getTaifunDataFactory().withActCarrier(TNB.AKOM).surfAndFonWithDns(3))
    //                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, Carrier.ID_DTAG)
    //                .withCreateAnschlussinhaberAddress(false)
    //                .withVorgabeMnet(getPortDate())
    //                .withUserName(WitaSimulatorTestUser.TAL_VERBUNDLEISTUNG_VBLT_05);
    //
    //        workflow().select(workflow().get()).sendVbl(testDataBuilder);
    //        atlas().receiveOrder().extractFromPayload(REQUESTED_CUSTOMER_DATE.getXpath(),
    //                "requestedCustomerDate");
    //
    //        atlas().sendNotification("QEB");
    //        workflow().waitForQEB();
    //
    //        atlas().sendNotificationWithNewVariables("ABBM", CONTRACT_ID, PRODUCT_IDENTIFIER);
    //        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.RUFNUMMER_NICHT_IN_BESTAND);
    //
    //        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestandssucheSet);
    //        workflow().doWithWorkflow(wf -> wf.assertRufnummernPortierungEinzelanschlussSet(3));
    //        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    //    }

}
