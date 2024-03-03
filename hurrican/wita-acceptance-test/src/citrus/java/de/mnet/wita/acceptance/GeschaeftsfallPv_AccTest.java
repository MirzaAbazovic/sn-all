/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2015
 */
package de.mnet.wita.acceptance;

import static de.augustakom.hurrican.model.cc.Carrier.*;
import static de.mnet.wita.WitaSimulatorTestUser.*;

import java.time.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wita.AbbmPvMeldungsCode;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.CreateInvalidPreAgreementIdAction;
import de.mnet.wita.citrus.actions.CreateWbciEntityAction;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.UserTask;

@Test(groups = BaseTest.ACCEPTANCE)
public class GeschaeftsfallPv_AccTest extends AbstractWitaAcceptanceTest {


    /**
     * Akzeptanztest fuer PV mit Anhang
     */
    @CitrusTest(name = "GeschaeftsfallPv_PvWithAnlage_AccTest")
    public void talPvWithAnlage() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_05, getWitaVersionForAcceptanceTest());

        AcceptanceTestWorkflow workflow = workflow().get();
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_PROVIDERWECHSEL_PV3_05)
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, ID_QSC)
                .withCudaKuendigung()
                .withPortierungsauftrag();

        parallel(
                workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL),

                sequential(
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("PORTIERUNGSAUFTRAG_RESPONSE"),
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("KUNDENAUFTRAG_RESPONSE")
                )
        );

        atlas().receiveOrder("PV");

        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID);
        workflow().waitForQEB();

        atlas().sendNotification("ABBM");
        workflow().waitForABBM();
    }

    /**
     * Weggang von zertifizierenden Provider. Wechsel ohne Leistungsänderung. Wechselanfrage bestätigt, mit geändertem
     * Abgabedatum
     */
    @CitrusTest(name = "GeschaeftsfallPv_Pv304_AccTest")
    public void talProviderwechselPv304() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_04, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.TAL_PROVIDERWECHSEL_PV3_04);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });

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

        atlas().sendNotification("ERLM-PV");
        workflow().waitForErlmPv();

        atlas().sendNotification("ENTM-PV");
        workflow().waitForEntmPv();
    }


    /**
     * Vorbedingung für Stornoauftrag. Weggang von zertifizierenden Provider. Nach Senden des Auftrages durch Simulator
     * erhält Provider eine AKM-PV.
     */
    @CitrusTest(name = "GeschaeftsfallPv_Pv307_AccTest")
    public void talProviderwechselPv307() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_07, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.TAL_PROVIDERWECHSEL_PV3_07);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); } );

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 12)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 10)");

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().sendNotification("ABBM-PV");
        workflow().waitForAbbmPv(AbbmPvMeldungsCode.WECHSEL_ZURUECKGEZOGEN);
    }


    /**
     * Annahme: der Carrier 'Wilhelm' ist noch nicht auf der WITA.
     */
    @CitrusTest(name = "GeschaeftsfallPv_ForCarrierNotOnWita_AccTest")
    public void talProviderwechselForCarrierNotOnWita() throws Exception {
        useCase(WitaAcceptanceUseCase.PV_NEU, getWitaVersionForAcceptanceTest());

        AcceptanceTestWorkflow workflow = workflow().get();
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(WitaSimulatorTestUser.PV_NEU)
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay())
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, Carrier.ID_WILHELM);

        workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL);
        atlas().receiveOrder("NEU");

        workflow().doWithWorkflow(wf -> { wf.assertInterimsProjektkenner(); });
    }


    /**
     * Weggang von zertifizierenden Provider. Negative Ruem-PV.
     */
    @CitrusTest(name = "GeschaeftsfallPv_NegativeRuemPv_AccTest")
    public void sendNegativeRuemPv() throws Exception {
        useCase(WitaAcceptanceUseCase.AKMPV_RUEMPV_ABBMPV, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.AKMPV_RUEMPV_ABBMPV);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); } );

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 12)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 10)");

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        workflow().sendNegativeRuemPv(RuemPvAntwortCode.SONSTIGES, "Keine Lust");
        atlas().receiveNotification("RUEM-PV");

        atlas().sendNotification("ABBM-PV");
        workflow().waitForAbbmPv(AbbmPvMeldungsCode.WECHSEL_ZURUECKGEZOGEN);
    }


    /**
     * Weggang von zertifizierenden Provider. ABBM-PV auf ABM-PV -> Workflow soll beendet werden
     */
    @CitrusTest(name = "GeschaeftsfallPv_AbbmPvAfterAbmPv_AccTest")
    public void abbmPvAfterAbmPv() throws Exception {
        useCase(WitaAcceptanceUseCase.AKMPV_RUEMPV_ABMPV_ABBMPV, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.AKMPV_RUEMPV_ABMPV_ABBMPV);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); } );

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 12)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 10)");

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        workflow().sendRuemPv();
        atlas().receiveNotification("RUEM-PV");

        atlas().sendNotification("ABM-PV");
        workflow().waitForAbmPv();

        atlas().sendNotification("ABBM-PV");
        workflow().waitForAbbmPv(AbbmPvMeldungsCode.WECHSEL_ZURUECKGEZOGEN);

        hurrican().assertAkmPvUserTaskIsAbbmOnAbm();
    }


    @CitrusTest(name = "GeschaeftsfallPv_ErlmPvAfterAkmPv_AccTest")
    public void erlmPvAfterAkmPv() throws Exception {
        useCase(WitaAcceptanceUseCase.AKMPV_ERLMPV_ENTMPV, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.AKMPV_ERLMPV_ENTMPV);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); } );

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 12)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 10)");

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().sendNotification("ERLM-PV");
        workflow().waitForErlmPvInWorkflowError();

        atlas().sendNotification("ENTM-PV");
        workflow().waitForEntmPvInWorkflowError();
    }


    @CitrusTest(name = "GeschaeftsfallPv_EntmPvAfterAkmPv_AccTest")
    public void entmPvAfterAkmPv() throws Exception {
        useCase(WitaAcceptanceUseCase.AKMPV_ENTMPV, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.AKMPV_ENTMPV);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); } );

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 12)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 10)");

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().sendNotification("ENTM-PV");
        workflow().waitForEntmPvInWorkflowError();
    }


    @CitrusTest(name = "GeschaeftsfallPv_akmPvAfterAkmPv_AccTest")
    public void akmPvAfterAkmPv() throws Exception {
        useCase(WitaAcceptanceUseCase.AKMPV_AKMPV, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.AKMPV_AKMPV);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); } );

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 12)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 10)");

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        hurrican().assertAkmPvUserTaskStatus(AkmPvUserTask.AkmPvStatus.AKM_PV_EMPFANGEN, UserTask.UserTaskStatus.OFFEN, false);
        hurrican().assertMwfEntityCountSearchByVtrNr(AnkuendigungsMeldungPv.class, 2);
    }

    @CitrusTest(name = "GeschaeftsfallPv_AbgebendeLeitungMitAutomatischerRuemPv_AccTest")
    public void talAbgebendeLeitungMitAutomatischerRuemPv() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_04, getWitaVersionForAcceptanceTest());

        // ATTENTIION: must be in sync with settings in TAL_PROVIDERWECHSEL_PV3_04
        LocalDate kwt = DateCalculationHelper.addWorkingDays(LocalDate.now(), 7);
        CreatedData createdData = createBaseOrderForAbgebendPv(TAL_PROVIDERWECHSEL_PV3_04, kwt);

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 7)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");
        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().receiveNotification("RUEM-PV");  // automatically sent from Hurrican!

        atlas().sendNotification("ABM-PV");
        workflow().waitForAbmPv();

        atlas().sendNotification("ERLM-PV");
        workflow().waitForErlmPv();

        atlas().sendNotification("ENTM-PV");
        workflow().waitForEntmPv();

        hurrican().assertMwfEntityCountSearchByVtrNr(RueckMeldungPv.class, 1);
    }


    /**
     * AKM-PV soll automatisch mit einer RUEM-PV beantwortet werden, wobei fuer die Auswertung auf eine WBCI-VA zurueck
     * gegriffen wird. (Die WBCI VA wird direkt ueber die Builder angelegt!)
     */
    @CitrusTest(name = "GeschaeftsfallPv_AbgebendeLeitungMitAutomatischerRuemPvUndWbciVa_AccTest")
    public void talAbgebendeLeitungMitAutomatischerRuemPvUndWbciVorabstimmung() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_09, getWitaVersionForAcceptanceTest());

        LocalDate kwt = DateCalculationHelper.addWorkingDays(LocalDate.now(), 14);
        CreatedData createdData = createBaseOrderForAbgebendPv(TAL_PROVIDERWECHSEL_PV3_09, kwt);

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 7)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");

        action(new CreateWbciEntityAction(wbciDao) {
            @Override
            public WbciEntity getWbciEntity(TestContext context) {
                return new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                        .withRequestStatus(WbciRequestStatus.AKM_TR_EMPFANGEN)
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnTestBuilder()
                                        .withWechseltermin(getDateTimeFromContext(context, VariableNames.REQUESTED_CUSTOMER_DATE).toLocalDate())
                                        .withVorabstimmungsId(createPreAgreementId(context, RequestTyp.VA))
                                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
            }
        });
        action(new CreateWbciEntityAction(wbciDao) {
            @Override
            public WbciEntity getWbciEntity(TestContext context) {
                return new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(Boolean.TRUE)
                        .withWbciGeschaeftsfall(getWbciGeschaeftsfall(context))
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
            }
        });

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().receiveNotification("RUEM-PV");  // automatically sent from Hurrican!

        atlas().sendNotification("ABM-PV");
        workflow().waitForAbmPv();

        atlas().sendNotification("ERLM-PV");
        workflow().waitForErlmPv();

        atlas().sendNotification("ENTM-PV");
        workflow().waitForEntmPv();

        hurrican().assertMwfEntityCountSearchByVtrNr(RueckMeldungPv.class, 1);
    }

    @CitrusTest(name = "GeschaeftsfallPv_AbgebendeLeitungMitAutomatischerRuemPvUndDoppelterAbmPv_AccTest")
    @Test(enabled = false) // TODO: unstable test
    public void talAbgebendeLeitungMitAutomatischerRuemPvUndDoppelterAbmPv() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_12, getWitaVersionForAcceptanceTest());

        // ATTENTIION: must be in sync with settings in TAL_PROVIDERWECHSEL_PV3_12
        LocalDate kwt = DateCalculationHelper.addWorkingDays(LocalDate.now(), 7);
        CreatedData createdData = createBaseOrderForAbgebendPv(TAL_PROVIDERWECHSEL_PV3_12, kwt);

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 7)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");
        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().receiveNotification("RUEM-PV");  // automatically sent from Hurrican!

        atlas().sendNotification("ABM-PV");
        workflow().waitForAbmPv();

        workflow().doWithWorkflow(wf -> { wf.closeAkmPvUserTask(); });

        // receive second ABM-PV
        atlas().sendNotification("ABM-PV2");
        workflow().waitForAbmPv();

        hurrican().assertAkmPvUserTaskStatus(AkmPvUserTask.AkmPvStatus.ABM_PV_EMPFANGEN, UserTask.UserTaskStatus.OFFEN, true);
    }

    /**
     * AKM-PV mit WBCI Vorabstimmungs-Id; jedoch keine AKM-TR mit Leitungsuebernahme 'Ja' ueber WBCI gesendet --> negative RUEM-PV
     */
    @CitrusTest(name = "GeschaeftsfallPv_AbgebendeLeitungMitAutomatischerNegRuemPv_WithoutWbciVa_AccTest")
    public void talAbgebendeLeitungMitAutomatischerNegRuemPvWithoutWbciVa() throws Exception {
        talAbgebendeLeitungMitAutomatischerNegRuemPv(
                WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_10, TAL_PROVIDERWECHSEL_PV3_10, true);
    }

    /**
     * AKM-PV mit ungueltiger WBCI Vorabstimmungs-Id --> negative RUEM-PV
     */
    @CitrusTest(name = "GeschaeftsfallPv_AbgebendeLeitungMitAutomatischerNegRuemPv_WithoutAkmTr_AccTest")
    public void talAbgebendeLeitungMitAutomatischerNegRuemPvWithoutAkmTr() throws Exception {
        talAbgebendeLeitungMitAutomatischerNegRuemPv(
                WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_11, TAL_PROVIDERWECHSEL_PV3_11, false);
    }

    private void talAbgebendeLeitungMitAutomatischerNegRuemPv(WitaAcceptanceUseCase useCase,
            WitaSimulatorTestUser testUser, boolean createValidWbciVa) throws Exception {
        useCase(useCase, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(testUser);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 7)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");

        if (createValidWbciVa) {
            // create a new VA with RuemVA but without an AKM-TR which leads to sending a negative RuemPV
            action(new CreateWbciEntityAction(wbciDao) {
                @Override
                public WbciEntity getWbciEntity(TestContext context) {
                    return new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                            .withRequestStatus(WbciRequestStatus.RUEM_VA_VERSENDET)
                            .withWbciGeschaeftsfall(
                                    new WbciGeschaeftsfallKueMrnTestBuilder()
                                            .withWechseltermin(getDateTimeFromContext(context, VariableNames.REQUESTED_CUSTOMER_DATE).toLocalDate())
                                            .withVorabstimmungsId(createPreAgreementId(context, RequestTyp.VA))
                                            .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                            )
                            .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
                }
            });
            action(new CreateWbciEntityAction(wbciDao) {
                @Override
                public WbciEntity getWbciEntity(TestContext context) {
                    final WbciGeschaeftsfall wbciGeschaeftsfall = getWbciGeschaeftsfall(context);
                    return new RueckmeldungVorabstimmungTestBuilder()
                            .withWechseltermin(wbciGeschaeftsfall.getWechseltermin().atStartOfDay().toLocalDate())
                            .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                            .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
                }
            });
        }
        else {
            action(new CreateInvalidPreAgreementIdAction());
        }

        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        atlas().receiveNotification("RUEM-PV");
    }


    private CreatedData createBaseOrderForAbgebendPv(WitaSimulatorTestUser userName, LocalDate kwt) throws Exception {
        AcceptanceTestWorkflow workflow = workflow().get();
        AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(userName)
                .withVorabstimmungAbgebend(true, AcceptanceTestDataBuilder.CARRIER_WITA_SIMULATOR, kwt);
        CreatedData createdData = workflow.createData(testData);

        workflow().select(workflow).send(createdData, CBVorgang.TYP_NEU);
        atlas().receiveOrderWithRequestedCustomerDate("NEU");

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
