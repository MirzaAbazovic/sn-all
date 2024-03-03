/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2015
 */
package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.WitaSimulatorTestUser.*;
import static de.mnet.wita.acceptance.common.utils.DateCalculation.*;

import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.helper.WbciRequestStatusHelper;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.CreateWbciEntityAction;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.service.MwfEntityService;

@Test(groups = BaseTest.ACCEPTANCE)
public class TalNeu_KfTest extends AbstractWitaAcceptanceTest {

    @Value("${atlas.lineorderservice.createOrder:/LineOrderService/createOrder}")
    private String createOderSoapAction;
    @Autowired
    TalOrderWorkflowService workflowService;
    @Autowired
    MwfEntityService mwfEntityService;

    /**
     * <ol> <li>Auftrag konsistent zu Rahmenvertrag.</li> <li>Produktbezeichner = TAL; CuDa 4Draht hbr (AAL) Doppelte
     * Schaltangaben Kupfer/CCA</li> </ol>
     */
    @CitrusTest(name = "TalNeu_01b_KfTest")
    public void talBereitNeu01b() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_01B, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_01B)
                .withVierDraht("02", "02")
                .withCreateVormieter();

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_4H));
    }

    /**
     * <ol> <li>Auftrag konsistent zu Rahmenvertrag.</li> <li>Produktbezeichner = TAL; CuDa 2Draht mit ZWR Einfache
     * Schaltangaben Kupfer/CCA</li> </ol>
     * <p>
     * ZWR Produktbezeichner nicht unterstuetzt, daher Senden via xml.
     * <p>
     * Achtung: hier handelt es sich um einen Test, der nur ein XML-Template verschickt! Es wird kein WITA-Workflow
     * dafuer gestartet!
     */
    @CitrusTest(name = "TalNeu_01a_KfTest")
    public void talBereitNeu01a() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_01A, getWitaVersionForAcceptanceTest());

        String extAuftragNummer = hurrican().getNextCarrierRefNr();
        String resource = String.format("templates/cdm/v%s/%s/talBereitNeu01a.xml",
                getWitaVersionForAcceptanceTest().getVersion(),
                WitaAcceptanceUseCase.TAL_BEREIT_NEU_01A.name()
        );
        String xmlTemplateString = Resources.toString(Resources.getResource(resource),
                Charset.defaultCharset());
        PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

        Properties fileProperties = new Properties();
        fileProperties.put("zeit.stempel", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss.SSSZZ")));
        fileProperties.put("externe.auftrags.nummer", extAuftragNummer);
        fileProperties.put("kunde.kunden.nummer", CarrierKennung.DTAG_KUNDEN_NR_MNET);
        fileProperties.put("kunde.leistungs.nummer", CarrierKennung.DTAG_LEISTUNGS_NR_MNET);
        fileProperties.put("bkto.faktura", "5883000326");
        fileProperties.put("ansprechpartner.nachname", TAL_BEREIT_NEU_01A.getName());

        LocalDate kundenwunschtermin = DateCalculationHelper.addWorkingDays(LocalDate.now(), 21);
        fileProperties.put("kundenwunschtermin.datum", kundenwunschtermin.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));

        String xmlString = propertyPlaceholderHelper.replacePlaceholders(xmlTemplateString, fileProperties);
        atlas().sendXmlToAtlas(xmlString, createOderSoapAction);

        atlas().receiveNotification("NEU", false);

        variables().add(VariableNames.EXTERNAL_ORDER_ID, extAuftragNummer);
        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID, VariableNames.CUSTOMER_ID);
        receiveError();

        atlas().sendNotification("ABM");
        receiveError();

        atlas().sendNotification("ERLM");
        receiveError();

        atlas().sendNotification("ENTM");
        receiveError();
    }

    private void receiveError() {
        atlas().receiveError("ERROR", AtlasEsbConstants.HUR_ERROR_CODE, "WITA_TECH_1000");
    }

    /**
     * Test zweier Aufträge mit Kenner: ProjektID. ProjektKenner muss gleich sein. <ol> <li>Auftrag 1: BKTOFaktura
     * ProjektKenner StandortA Firmadaten</li> <li>Auftrag 2: ProjektKenner StandortA Firmadaten</li> </ol>
     */
    @CitrusTest(name = "TalNeu_02a_KfTest")
    public void talBereitNeu02a() throws Exception {
        for (int i = 0; i < 2; i++) {

            final WitaSimulatorTestUser user = (i == 0) ? TAL_BEREIT_NEU_02AA : TAL_BEREIT_NEU_02AB;
            final WitaAcceptanceUseCase useCase = (i == 0) ? WitaAcceptanceUseCase.TAL_BEREIT_NEU_02AA : WitaAcceptanceUseCase.TAL_BEREIT_NEU_02AB;
            useCase(useCase, getWitaVersionForAcceptanceTest());

            final AcceptanceTestWorkflow workflow = workflow().get();
            final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                    .withHurricanProduktId(Produkt.AK_CONNECT)
                    .withUserName(user)
                    .withProjektKenner("TELEKOM_KFT_PROJEKT")
                    .withAddressType(CCAddress.ADDRESS_FORMAT_BUSINESS)
                    .withVorgabeMnet(
                            DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                                    DateCalculationHelper.addWorkingDays(LocalDate.now(), 14)
                            ).atStartOfDay()
                    )
                    .withTaifunData(
                            getTaifunDataFactory()
                                    .surfAndFonWithDns(0, false)
                    );


            final String preAgreementId = createPreAgreementId(RequestTyp.VA);
            if (i == 0 && getWitaVersionForAcceptanceTest().isGreaterOrEqualThan(WitaCdmVersion.V1)) {
                testData.withVorabstimmungsId(preAgreementId);
                testData.withTerminReservierungsId("1000000000");
            }

            action(new CreateWbciEntityAction(wbciDao) {
                @Override
                public WbciEntity getWbciEntity(TestContext context) {
                    return new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                            .withRequestStatus(WbciRequestStatusHelper.getActiveWbciRequestStatus(de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG))
                            .withWbciGeschaeftsfall(
                                    new WbciGeschaeftsfallKueMrnTestBuilder()
                                            .withWechseltermin( DateCalculationHelper.addWorkingDays(LocalDate.now(), 14))
                                            .withVorabstimmungsId(preAgreementId)
                                            .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                            )
                            .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
                }
            });

            final CreatedData baseData = workflow.createData(testData);
            workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

            atlas().receiveOrderWithRequestedCustomerDate("NEU");

            atlas().sendNotification("QEB");
            workflow().waitForQEB();

            atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
            workflow().waitForABM();

            atlas().sendNotification("ERLM");
            workflow().waitForERLM();

            atlas().sendNotification("ENTM");
            workflow().waitForENTM();
            workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

            workflow().doWithWorkflow(AcceptanceTestWorkflow::assertProjektKennerSet);
            workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBktoFakturaSet);
            workflow().doWithWorkflow(AcceptanceTestWorkflow::assertStandortKundeOrtsteilSet);
            workflow().doWithWorkflow(AcceptanceTestWorkflow::assertStandortKundeOrtsteilSet);
            workflow().doWithWorkflow(AcceptanceTestWorkflow::assertStandortKundeLageTaeSet);
            workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
        }
    }

    protected String createPreAgreementId(RequestTyp requestTyp) {
        final String nextValueWithLeadingZeros = String.format("%08d", wbciDao.getNextSequenceValue(requestTyp));
        // z.B. 'DEU.MNET.VH12345678'
        final String vorabstimmungsId = String.format("%s.%s%s%s",
                CarrierCode.MNET.getITUCarrierCode(),
                requestTyp.getPreAgreementIdCode(),
                WbciConstants.VA_ROUTING_PREFIX_HURRICAN,
                nextValueWithLeadingZeros);

        variables().add(VariableNames.PRE_AGREEMENT_ID, vorabstimmungsId);
        return vorabstimmungsId;
    }

    @CitrusTest(name = "TalNeu_02b_KfTest")
    public void talBereitNeu02b() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_02B, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow1 = workflow().get();
        final AcceptanceTestDataBuilder testData1 = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_02BA)
                .withRangLeiste1("01")
                .withRangStift1("01")
                .withCreateVormieter();

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        final LocalDate kwt2 = DateCalculationHelper.addWorkingDays(testData1.getVorgabeMnet().toLocalDate(), 7);
        final AcceptanceTestDataBuilder testData2 = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_02BB)
                .withRangLeiste1("01")
                .withRangStift1("02")
                .withVorgabeMnet(kwt2.atStartOfDay());

        hurrican().createMasterChildCbVorgang(workflow1, workflow1.createData(testData1), workflow2, workflow2.createData(testData2));

        atlas().receiveOrder();
        atlas().receiveOrder();

        variables().add(VariableNames.EXTERNAL_ORDER_ID, String.format("${%s}", VariableNames.MASTER_EXTERNAL_ORDER_ID));
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("QEB");
        workflow().select(workflow1).waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().select(workflow1).waitForABM();

        atlas().sendNotification("ERLM");
        workflow().select(workflow1).waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().select(workflow1).waitForENTM();

        workflow().select(workflow1).doWithWorkflow(workflow -> workflow.assertCountOfRequestsWithSameAuftragsKlammer(2));

        workflow().select(workflow1).doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        workflow().select(workflow1).doWithWorkflow(AcceptanceTestWorkflow::assertBktoFakturaSet);
        workflow().select(workflow1).doWithWorkflow(AcceptanceTestWorkflow::assertVormieterSet);
        workflow().select(workflow1).doWithWorkflow(AcceptanceTestWorkflow::assertAuftragsKennerSet);
        workflow().select(workflow1).doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));

        workflow().select(workflow2).doWithWorkflow(AcceptanceTestWorkflow::assertAuftragsKennerSet);
        workflow().select(workflow2).doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    @CitrusTest(name = "TalNeu_03_KfTest")
    public void talBereitNeu03() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_03, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        // Hurrican erlaubt keinen Kundenwunschtermin am Wochenende, daher stimmt dieser Testfall
        // nicht mit den Vorgaben des KFT überein.
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_03);

        variable(VariableNames.REQUESTED_CUSTOMER_DATE, workflow().getTestDataBuilder().getVorgabeMnet());

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);
    }

    /**
     * <ol> <li>Auftrag konsistent zum Rahmenvertrag. Auftrag dient zum Simulation für die Zurückgabe von mehreren
     * Meldungscodes</li> </ol>
     */
    @CitrusTest(name = "TalNeu_04_KfTest")
    public void talBereitNeu04() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_04, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withHurricanProduktId(Produkt.AK_CONNECT)
                .withUserName(TAL_BEREIT_NEU_04)
                .withVierDraht()
                .withAddressType(CCAddress.ADDRESS_FORMAT_BUSINESS)
                .withTaifunData(
                        getTaifunDataFactory().
                                withActCarrier(TNB.AKOM).
                                surfAndFonWithDns(3, false)
                );

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertStandortKundeSet);
        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertStandortKundeIsFirma);
        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBktoFakturaSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_4H));
        workflow().doWithWorkflow(wf -> Assert.assertEquals(wf.getCbVorgang().getTalRealisierungsZeitfenster(),
                TalRealisierungsZeitfenster.VORMITTAG));
    }


    /**
     * <ol> <li>Auftrag mit falschen Schaltangaben. (Schaltangaben nicht manipuliert, nur ABBM erwartet).</li> </ol>
     */
    @CitrusTest(name = "TalNeu_06_KfTest")
    public void talBereitNeu06() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_06, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_06)
                .withVierDraht();

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.SCHALTANGABEN_NOT_CORRECT);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_4H));
    }

    /**
     * <ol> <li>Bestellung des Produktes nicht möglich. Alternativprodukt wird angebeten</li> </ol>
     */
    @CitrusTest(name = "TalNeu_07_KfTest")
    public void talBereitNeu07() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_07, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_07);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.ALTERNATIVPRODUKT);

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestellerSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * <ol> <li>"Bestellung im Namen Dritter. Besteller ist nicht berechtigt für Dritte zu bestellen."</li> </ol>
     */
    @CitrusTest(name = "TalNeu_08_KfTest")
    public void talBereitNeu08() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_08, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_08)
                .withCreateAnsprechpartnerTechnik();

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.BESTELLER_NOT_AUTHORIZED);

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestellerSet);
        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertAnsprechpartnerTechnikSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * <ol> <li>StandortA Daten im Auftrag sind nicht eindeutig</li> </ol>
     * <p/>
     * StandortA: ohne Optionalattributen - z.Z. nicht beruecksichtigt
     */
    @CitrusTest(name = "TalNeu_09_KfTest")
    public void talBereitNeu09() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_09, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_09);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.STANDORTANGABEN_NOT_SUFFICIENT);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * <ol> <li>StandortA Daten im Auftrag sind fehlerhaft: Anschrift ist nicht bekannt</li> </ol>
     * <p/>
     * StandortA: ohne Optionalattributen - z.Z. nicht beruecksichtigt
     */
    @CitrusTest(name = "TalNeu_10_KfTest")
    public void talBereitNeu10() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_10, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_10);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.ANSCHRIFT_NOT_CORRECT);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * <ol> <li>StandortB Daten im Auftrag sind nicht plausibel.</li> </ol>
     * <p/>
     * StandortA: ohne Optionalattributen - z.Z. nicht beruecksichtigt
     */
    @CitrusTest(name = "TalNeu_11_KfTest")
    public void talBereitNeu11() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_11, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_11);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.STANDORTANGABEN_B_NOT_CORRECT);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    @CitrusTest(name = "TalNeu_12a_KfTest")
    public void talBereitNeu12a() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_12A, getWitaVersionForAcceptanceTest());

        Resource resource = testResource().getXmlTemplate("talBereitNeu12a");
        final String xmlTemplateString = Resources.toString(resource.getURL(), Charset.defaultCharset());
        final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

        final Properties fileProperties = new Properties();

        final String extAuftragNummer = hurrican().getNextCarrierRefNr();
        fileProperties.put("externe.auftrags.nummer", extAuftragNummer);
        fileProperties.put("ansprechpartner.nachname", TAL_BEREIT_NEU_12A.getName());
        final LocalDate kundenwunschtermin = DateCalculationHelper.addWorkingDays(LocalDate.now(), 14);
        fileProperties.put("kundenwunschtermin.datum", kundenwunschtermin.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
        fileProperties.put("zeit.stempel", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss.SSSZZ"))); // 2011-08-16T11:03:58.864+02:00

        final String xmlString = propertyPlaceholderHelper.replacePlaceholders(xmlTemplateString, fileProperties);
        atlas().sendXmlToAtlas(xmlString, createOderSoapAction);
    }

    @CitrusTest(name = "TalNeu_12b_KfTest")
    public void talBereitNeu12b() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_12B, getWitaVersionForAcceptanceTest());

        Resource resource = testResource().getXmlTemplate("talBereitNeu12b");
        final String xmlTemplateString = Resources.toString(resource.getURL(), Charset.defaultCharset());
        final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

        final Properties fileProperties = new Properties();
        final String extAuftragNummer = hurrican().getNextCarrierRefNr();
        fileProperties.put("externe.auftrags.nummer", extAuftragNummer);
        fileProperties.put("ansprechpartner.nachname", TAL_BEREIT_NEU_12B.getName());
        final LocalDate kundenwunschtermin = DateCalculationHelper.addWorkingDays(LocalDate.now(), 14);
        fileProperties.put("kundenwunschtermin.datum", kundenwunschtermin.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
        fileProperties.put("zeit.stempel", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss.SSSZZ"))); // 2011-08-16T11:03:58.864+02:00

        final String xmlString = propertyPlaceholderHelper.replacePlaceholders(xmlTemplateString, fileProperties);
        atlas().sendXmlToAtlas(xmlString, createOderSoapAction);
    }

    /**
     * <ol> <li>Auftrag für Produkt senden, für das keine Vereinbarung vorliegt. (nicht im Rahmenvertrag
     * hinterlegt)</li> </ol>
     */
    @CitrusTest(name = "TalNeu_13_KfTest")
    public void talBereitNeu13() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_12B, getWitaVersionForAcceptanceTest());
        Resource talBereitNeu13 = testResource().getXmlTemplate("talBereitNeu13");
        final String xmlTemplateString = Resources.toString(talBereitNeu13.getURL(), Charset.defaultCharset());
        final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

        final Properties fileProperties = new Properties();
        final String extAuftragNummer = hurrican().getNextCarrierRefNr();
        fileProperties.put("externe.auftrags.nummer", extAuftragNummer);
        fileProperties.put("kunde.kunden.nummer", CarrierKennung.DTAG_KUNDEN_NR_AUGUSTAKOM);
        fileProperties.put("kunde.leistungs.nummer", CarrierKennung.DTAG_LEISTUNGS_NR_AUGUSTAKOM);
        fileProperties.put("besteller.kunden.nummer", CarrierKennung.DTAG_KUNDEN_NR_MNET);
        fileProperties.put("besteller.leistungs.nummer", CarrierKennung.DTAG_LEISTUNGS_NR_MNET);
        fileProperties.put("ansprechpartner.nachname", TAL_BEREIT_NEU_13.getName());
        final LocalDate kundenwunschtermin = DateCalculationHelper.addWorkingDays(LocalDate.now(), 14);
        fileProperties.put("kundenwunschtermin.datum", kundenwunschtermin.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
        fileProperties.put("zeit.stempel", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss.SSSZZ"))); // 2011-08-16T11:03:58.864+02:00

        final String xmlString = propertyPlaceholderHelper.replacePlaceholders(xmlTemplateString, fileProperties);
        atlas().sendXmlToAtlas(xmlString, createOderSoapAction);
    }

    /**
     * <ol> <li>Schritt 1: Auftrag dient als Vorbedingung und muss erledigt sein</li> <li>Schritt 2: Einstellen eines
     * bis zur gleichen externen Auftragsnummer verschiedenen Auftrages</li> </ol>
     * <p>
     * StandortA: ohne Optionalattributen - z.Z. nicht beruecksichtigt
     */
    @CitrusTest(name = "TalNeu_14_KfTest")
    public void talBereitNeu14() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_14A, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_14a);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrder();

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

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        final AcceptanceTestDataBuilder testData2 = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_14a)
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId());
        workflow().select(workflow2).send(workflow2.createData(testData2), CBVorgang.TYP_NEU);
        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.EXTERNEAUFTRAGNUMMER_NOT_CORRECT);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * <ol> <li>StandortB Daten im Auftrag sind nicht plausibel.</li> </ol>
     * <p>
     * StandortA: ohne Optionalattributen - z.Z. nicht beruecksichtigt
     */
    @CitrusTest(name = "TalNeu_15_KfTest")
    public void talBereitNeu15() throws Exception {
        // WICHTIG: wita.count.of.days.before.sent in T_WITA_CONFIG beruecksichtigen!
        // Zur Zeit wird diese in modify-hurrican4devel auf 100 Tage eingestellt

        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_15, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_15);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.SCHALTTERMIN_UNZULAESSIG);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Test zweier Aufträge mit Kenner: ProjektID. ProjektKenner der Aufträge sind nicht bekannt.
     * <p>
     * Valider Auftrag wird abgebrochen mit ABBM 2006. "Projektkenner ist nicht bekannt"
     * <p>
     * ProjektKenner StandortA Firmadaten
     */
    @CitrusTest(name = "TalNeu_16_KfTest")
    public void talBereitNeu16() throws Exception {
        for (int i = 0; i < 2; i++) {

            useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_16, getWitaVersionForAcceptanceTest());

            final AcceptanceTestWorkflow workflow = workflow().get();
            final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                    .withHurricanProduktId(Produkt.AK_CONNECT)
                    .withUserName(TAL_BEREIT_NEU_16)
                    .withProjektKenner("KEIN_KFT_PROJEKT")
                    .withAddressType(CCAddress.ADDRESS_FORMAT_BUSINESS)
                    .withTaifunData(
                            getTaifunDataFactory()
                                    .surfAndFonWithDns(0, false)
                    );

            final CreatedData baseData = workflow.createData(testData);
            workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

            atlas().receiveOrderWithRequestedCustomerDate("NEU");

            atlas().sendNotification("QEB");
            workflow().waitForQEB();

            atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
            workflow().waitForNonClosingAbbm(AbbmMeldungsCode.PROJEKTKENNER_NICHT_BEKANNT);

            workflow().doWithWorkflow(AcceptanceTestWorkflow::assertProjektKennerSet);
            workflow().doWithWorkflow(AcceptanceTestWorkflow::assertStandortKundeSet);
            workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
        }
    }

    /**
     * Valider Auftrag wird abgebrochen mit ABBM 1040. "Reservierungsnummer ist ungültig"
     */
    @CitrusTest(name = "TalNeu_17_KfTest")
    public void talBereitNeu17() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_17, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_17)
                .withTerminReservierungsId("1000000000");

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.RESERVIERUNGSNUMMER_UNGUELTIG);

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestellerSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Valider Auftrag wird abgebrochen mit ABBM 1045. "Der Termin zur Reservierungs ID ist ungleich dem
     * Kundenwunschtermin"
     */
    @CitrusTest(name = "TalNeu_18_KfTest")
    public void talBereitNeu18() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_18, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_18)
                .withTerminReservierungsId("1000000000");

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.RESERVIERUNGSNUMMER_UNGLEICH_WUNSCHTERMIN);

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestellerSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * <ol> <li>TAL_NEU wird korrekt gesendet - QEB und ABM zurueck</li> <li>Storno wird gesendet</li> <li>Auf ERLM und
     * ENTM wird nicht gewartet, da sonst der Workflow abgeschlossen wird.</li> <li>ABBM 1087 "Eine Stornierung ist
     * nicht mehr möglich." wird empfangen</li> </ol>
     */
    @CitrusTest(name = "TalNeu_20_KfTest")
    public void talBereitNeu20() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_20, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_20);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        workflow().sendStorno();
        atlas().receiveOrder("STORNO");

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.STORNO_NOT_POSSIBLE);

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Neubestellung mit Terminverschiebung. <ol> <li>Gutfall: Terminverschiebung wird nach QEB gesendet.</li>
     * <li>Schlechtfall: 2te Terminverschiebung wird nach ERLM gesendet.</li> </ol>
     */
    @CitrusTest(name = "TalNeu_23_KfTest")
    public void talBereitNeu23() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_23, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_23);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        atlas().sendNotification("TAM");
        workflow().waitForTAM();

        workflow().sendTv();
        atlas().receiveNotification("TV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), "requestedCustomerDate2");

        atlas().sendNotificationWithNewVariables("ABM2", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestellerSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * "Valider Auftrag wird prozessiert. Wegen nicht ausgeführten Montagearbeiten wird ein TAM ausgeschickt. Provider
     * stellt keinen TV-Auftrag ein, sondern schickt ein Erledigungsmeldung Kunde."
     */
    @CitrusTest(name = "TalNeu_24_KfTest")
    public void talBereitNeu24() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_24, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_24);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        atlas().sendNotification("TAM");
        workflow().waitForTAM();

        atlas().sendNotification("ERLM-K");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::assertBestellerSet);
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    @CitrusTest(name = "TalNeu_25_KfTest")
    public void talBereitNeu25() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_25, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_25);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().sendTv(); // TODO(WITAX): hier muss ein ungültiges Zeitfenster gesetzt werden
        atlas().receiveNotification("TV");

        //atlas().sendNotification("ABBM");
        //workflow().waitForABBM();
    }

    @CitrusTest(name = "TalNeu_26_KfTest")
    public void talBereitNeu26() throws Exception {
          useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_26, getWitaVersionForAcceptanceTest());

        final String extAuftragNummer = hurrican().getNextCarrierRefNr();
        final String resource = String.format("templates/cdm/v%s/%s/talBereitNeu26.xml",
                getWitaVersionForAcceptanceTest().getVersion(),
                WitaAcceptanceUseCase.TAL_BEREIT_NEU_26.name()
        );
        final String xmlTemplateString = Resources.toString(Resources.getResource(resource), Charset.defaultCharset());
        final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

        final Properties fileProperties = new Properties();
        fileProperties.put("zeit.stempel", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss.SSSZZ")));
        fileProperties.put("externe.auftrags.nummer", extAuftragNummer);
        fileProperties.put("kunde.kunden.nummer", CarrierKennung.DTAG_KUNDEN_NR_MNET);
        fileProperties.put("kunde.leistungs.nummer", CarrierKennung.DTAG_LEISTUNGS_NR_MNET);
        fileProperties.put("bkto.faktura", "5883000326");
        fileProperties.put("ansprechpartner.nachname", TAL_BEREIT_NEU_26.getName());

        final LocalDate kundenwunschtermin = DateCalculationHelper.addWorkingDays(LocalDate.now(), 21);
        fileProperties.put("kundenwunschtermin.datum", kundenwunschtermin.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
        fileProperties.put("kundenwunschtermin.zeitfenster", "4711");

        final String xmlString = propertyPlaceholderHelper.replacePlaceholders(xmlTemplateString, fileProperties);
        atlas().sendXmlToAtlas(xmlString, createOderSoapAction);

        atlas().receiveNotification("NEU", false);

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID, VariableNames.CUSTOMER_ID);
        receiveError();
    }

    @CitrusTest(name = "TalNeu_27_KfTest")
    public void talBereitNeu27() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_27, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_27);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().sendTv();
        atlas().receiveNotification("TV");

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID);
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.TV_AUS_BETRIEBLICHEN_GRUENDEN_NICHT_MOEGLICH);
    }
}
