/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2015
 */
package de.mnet.wita.acceptance;

import static de.mnet.wita.WitaSimulatorTestUser.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.SendWitaBereitstellungTestAction;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.common.SmsStatus;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Acceptance Tests fuer WITA Geschaeftsfall 'Bereitstellung'.
 * (Test basierend auf Citrus ohne zusaetzlich notwendigen / eigenstaendig laufenden WITA Simulator Prozess)
 *
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GeschaeftsfallNeu_AccTest extends AbstractWitaAcceptanceTest {

    private final WitaCdmVersion WITA_CDM_VERSION =  getWitaVersionForAcceptanceTest();

    /**
     * Neubestellung TAL; CuDA 4 Draht hbr (HVt)
     */
    @CitrusTest(name = "GeschaeftsfallNeu_Neubestellung_AccTest")
    public void neubestellung() {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ABM_ERLM_ENTM);

        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(workflow -> { assertEquals(workflow.getAbm().getSmsStatus(), SmsStatus.OFFEN); });
        workflow().doWithWorkflow(workflow -> { workflow.waitForENTMWorkflowClosed(); });

        workflow().doWithWorkflow(workflow -> {
            workflow.assertAbmValuesSetOnCbVorgang("52B/89/4401/1000000000", "100/50", "50/25"); });

        workflow().doWithWorkflow(workflow -> { workflow.waitForIOArchiveEntry(); } );
    }

    /**
     * Neubestellung TAL; CuDA 2 Draht hbr (KVz)
     */
    @CitrusTest(name = "GeschaeftsfallNeu_NeubestellungKvz_AccTest")
    public void neubestellungKvz() {
        configureWitaSendLimit(GeschaeftsfallTyp.BEREITSTELLUNG, KollokationsTyp.FTTC_KVZ, true, -1L);
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ABM_ERLM_ENTM)
                                .testDataBuilder()
                                .withKvz()
                                .withUetv(Uebertragungsverfahren.H18);

        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(workflow -> { workflow.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.KVZ_2H); });
    }

    /**
     * Neubestellung TAL; CuDA 2 Draht hbr (KVz) mit Kvz nicht erlaubt.
     */
    @CitrusTest(name = "GeschaeftsfallNeu_NeubestellungKvzNotAllowed_AccTest")
    public void neubestellungKvzNotAllowed() {
        configureWitaSendLimit(GeschaeftsfallTyp.BEREITSTELLUNG, KollokationsTyp.FTTC_KVZ, false, 0L);
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, WITA_CDM_VERSION);

        SendWitaBereitstellungTestAction action = workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ABM_ERLM_ENTM);

        action.testDataBuilder()
                .withKvz()
                .withUetv(Uebertragungsverfahren.H18);

        assertException(action).exception(CitrusRuntimeException.class);
    }

    @CitrusTest(name = "GeschaeftsfallNeu_WorkflowShouldBeClosedAfterAbbm_AccTest")
    public void workflowShouldBeClosedAfterAbbm() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_ABBM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_ABBM);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID);
        workflow().waitForABBM();

        workflow().doWithWorkflow(workflow -> { workflow.assertAbbmValuesSetOnCbVorgang(
                    "1001 : Die Anschrift ist nicht bekannt oder ist zur Rufnummer nicht plausibel."); });
    }

    /**
     * Neubestellung TAL with TV sent by Hurrican.
     */
    @CitrusTest(name = "GeschaeftsfallNeu_Terminverschiebung_AccTest")
    public void neubestellungMitTerminverschiebung() {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_TV_ABM_ERLM_ENTM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ABM_TV_ABM_ERLM_ENTM);

        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().sendTv();
        atlas().receiveNotification()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE_TV);

        atlas().sendNotification("ABM_2");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
    }


    @CitrusTest(name = "GeschaeftsfallNeu_Stornierung_AccTest")
    public void neubestellungMitStornierung() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_STORNO_ERLM_ENTM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_STORNO_ERLM_ENTM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        workflow().sendStorno();
        atlas().receiveNotification("STORNO");

        atlas().sendNotificationWithNewVariables("ERLM", VariableNames.CONTRACT_ID);
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForERLM();
    }


    @CitrusTest(name = "GeschaeftsfallNeu_MitZweiterABM_AccTest")
    public void neubestellungMitZweiterABM() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ABM_ERLM_ENTM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ABM_ABM_ERLM_ENTM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        variables().add("bindingDeliveryDate", "atlas:changeDate('yyyy-MM-dd','${requestedCustomerDate}','+4d')");
        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        variables().add("bindingDeliveryDate", "atlas:changeDate('yyyy-MM-dd','${requestedCustomerDate}','+5d')");
        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(workflow -> { assertTrue(workflow.getCbVorgang().getSecondAbmReceived()); });
    }


    @CitrusTest(name = "GeschaeftsfallNeu_AbbmAufAbm_AccTest")
    public void neubestellungMitAbbmAufAbm() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ABBM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ABM_ABBM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().doWithWorkflow(workflow -> { workflow.closeCbVorgang(); });

        atlas().sendNotification("ABBM");
        workflow().waitForABBM();

        workflow().doWithWorkflow(workflow -> { assertTrue(workflow.getCbVorgang().getPrio()); });
        workflow().doWithWorkflow(workflow -> { assertTrue(workflow.getCbVorgang().isAnswered()); });
        workflow().doWithWorkflow(workflow -> { assertNotNull(workflow.getCbVorgang().getReturnRealDate()); });
        workflow().doWithWorkflow(workflow -> { assertFalse(workflow.getCbVorgang().getReturnOk()); });
        workflow().doWithWorkflow(workflow -> { assertTrue(workflow.getCbVorgang().getAbbmOnAbm()); });
    }


    @CitrusTest(name = "GeschaeftsfallNeu_ErlmEntmOhneAbm_AccTest")
    public void neubestellungOhneAbm() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ERLM_ENTM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ERLM_ENTM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ERLM", VariableNames.CONTRACT_ID);
        workflow().waitForErlmInWorkflowError();

        atlas().sendNotification("ENTM");
        workflow().waitForEntmInWorkflowError();
    }


    @CitrusTest(name = "GeschaeftsfallNeu_StornoMitAnlage_AccTest")
    public void stornoMitAnlage() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_STORNO_ABBM, WITA_CDM_VERSION);

        parallel(
                sequential(workflow().sendBereitstellungWithLageplanAndKundenauftrag(WitaSimulatorTestUser.NEU_QEB_ABM_STORNO_ABBM)),

                sequential(
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("LAGEPLAN_RESPONSE"),
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("KUNDENAUFTRAG_RESPONSE")
                )
        );

        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().sendStorno();
        atlas().receiveNotification("STORNO");

        atlas().sendNotification("ABBM");
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.STORNO_NOT_POSSIBLE);

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(workflow -> { workflow.assertAnlageSetForOnlyStorno(2); });
    }


    @CitrusTest(name = "GeschaeftsfallNeu_TvMitAnlage_AccTest")
    public void terminverschiebungMitAnlage() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_TV_ABM_ERLM_ENTM, WITA_CDM_VERSION);

        parallel(
                sequential(workflow().sendBereitstellungWithLageplanAndKundenauftrag(
                        WitaSimulatorTestUser.NEU_QEB_ABM_TV_ABM_ERLM_ENTM)),

                sequential(
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("LAGEPLAN_RESPONSE"),
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("KUNDENAUFTRAG_RESPONSE")
                )
        );

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().sendTv();
        atlas().receiveNotification("TV_WITH_ATTACHEMENT")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE_TV);

        atlas().sendNotification("ABM_2");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(workflow -> { workflow.assertAnlageSetForOnlyTv(2); });
    }


    @CitrusTest(name = "GeschaeftsfallNeu_tamAfterTam_AccTest")
    public void tamAfterTam() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_ABM_TAM_TAM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_ABM_TAM_TAM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("TAM");
        workflow().doWithWorkflow(workflow -> { workflow.waitForTAM(); });
        workflow().doWithWorkflow(workflow -> { assertNotNull(workflow.getCbVorgang().getTamUserTask()); });
        workflow().doWithWorkflow(workflow -> { assertFalse(workflow.getCbVorgang().getTamUserTask().isMahnTam()); });

        atlas().sendNotification("TAM");
        workflow().doWithWorkflow(workflow -> { workflow.waitForSecondTAM(); });
        workflow().doWithWorkflow(workflow -> { assertNotNull(workflow.getCbVorgang().getTamUserTask()); });
        workflow().doWithWorkflow(workflow -> { assertTrue(workflow.getCbVorgang().getTamUserTask().isMahnTam()); });
    }


    @CitrusTest(name = "GeschaeftsfallNeu_mtamAfterTam_AccTest")
    public void mTamAfterTam() throws Exception {
        if (shouldTestBeExecuted(WitaCdmVersion.V1)) {

            useCase(WitaAcceptanceUseCase.NEU_ABM_TAM_MTAM, WITA_CDM_VERSION);

            workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_ABM_TAM_MTAM);
            atlas().receiveOrderWithRequestedCustomerDate();

            atlas().sendNotification("QEB");
            workflow().waitForQEB();

            atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
            workflow().waitForABM();

            atlas().sendNotification("TAM");
            workflow().doWithWorkflow(workflow -> { workflow.waitForTAM(); });
            workflow().doWithWorkflow(workflow -> { assertNotNull(workflow.getCbVorgang().getTamUserTask()); });
            workflow().doWithWorkflow(workflow -> { assertFalse(workflow.getCbVorgang().getTamUserTask().isMahnTam()); });

            atlas().sendNotification("MTAM");
            workflow().doWithWorkflow(workflow -> { workflow.waitForMTAM(); });
            workflow().doWithWorkflow(workflow -> { assertNotNull(workflow.getCbVorgang().getTamUserTask()); });
            workflow().doWithWorkflow(workflow -> { assertTrue(workflow.getCbVorgang().getTamUserTask().isMahnTam()); });
        }
    }


    @CitrusTest(name = "GeschaeftsfallNeu_tamAfterTvIsNotMahnTam_AccTest")
    public void tamAfterTvIsNotMahnTam() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_ABM_TAM_TV_TAM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_ABM_TAM_TV_TAM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("TAM");
        workflow().doWithWorkflow(workflow -> { workflow.waitForTAM(); });
        workflow().doWithWorkflow(workflow -> { assertNotNull(workflow.getCbVorgang().getTamUserTask()); });
        workflow().doWithWorkflow(workflow -> { assertFalse(workflow.getCbVorgang().getTamUserTask().isMahnTam()); });

        workflow().sendTv();
        atlas().receiveNotification()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE_TV);

        atlas().sendNotification("TAM");
        workflow().doWithWorkflow(workflow -> { workflow.waitForSecondTAM(); });
        workflow().doWithWorkflow(workflow -> { assertNotNull(workflow.getCbVorgang().getTamUserTask()); });
        workflow().doWithWorkflow(workflow -> { assertFalse(workflow.getCbVorgang().getTamUserTask().isMahnTam()); });
    }


    /**
     * Test zweier Aufträge mit Auftragsklammer. Lage der TAE muss bei einem Auftrag aus Kundenübergabe geholt werden,
     * beim anderen Auftrag von der Kundenübergabe des Hauptauftrags
     */
    @CitrusTest(name = "GeschaeftsfallNeu_LageTaeFromKundenuebergabe_AccTest")
    public void testLageTaeFromKundenuebergabe() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_02B, WITA_CDM_VERSION);

        AcceptanceTestWorkflow workflowMaster = workflow().get();
        AcceptanceTestWorkflow workflowChild = workflow().get();
        GeneratedTaifunData taifunData1 = taifunDataFactory.surfAndFonWithDns(0, true);

        // the floor must be set to null, otherwise this is taken rather than loading the TAEDose information from the
        // hurrican auftrag's endstelle connect
        taifunData1.getAddress().setFloor(null);

        AcceptanceTestDataBuilder builderMaster = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_02BA)
                .withTaifunData(taifunData1)
                .withRangLeiste1("01")
                .withRangStift1("01")
                .withKundenuebergabe("Geb. 1", "Etage 2", "1234");
        CreatedData masterCreatedData = workflowMaster.createData(builderMaster);

        AcceptanceTestDataBuilder builderChild = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_02BB)
                .withTaifunData(taifunData1)
                .withRangLeiste1("01")
                .withRangStift1("02");
        CreatedData childCreatedData = workflowChild.createData(builderChild);

        hurrican().createMasterChildCbVorgang(workflowMaster, masterCreatedData, workflowChild, childCreatedData);

        atlas().receiveOrder();
        atlas().receiveOrder();

        variables().add(VariableNames.EXTERNAL_ORDER_ID, String.format("${%s}", VariableNames.MASTER_EXTERNAL_ORDER_ID));

        atlas().sendNotification("QEB");
        workflow().select(workflowMaster).waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().select(workflowMaster).waitForABM();

        atlas().sendNotification("ERLM");
        workflow().select(workflowMaster).waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().select(workflowMaster).waitForENTM();

        workflow().select(workflowMaster).doWithWorkflow(workflow -> {
            workflow.assertCountOfRequestsWithSameAuftragsKlammer(2); });

        String lageTae = "Geb. 1, Etage 2, 1234";
        workflow().select(workflowMaster).doWithWorkflow(workflow -> {
            workflow.assertStandortKundeLageTaeEquals(lageTae); });
        workflow().select(workflowChild).doWithWorkflow(workflow -> {
            workflow.assertStandortKundeLageTaeEquals(lageTae); });
    }


    /**
     * Test zweier Aufträge mit Auftragsklammer, die Suborders haben verschiedene Anhaenge
     */
    @CitrusTest(name = "GeschaeftsfallNeu_NeuMitKlammerUndVerschiedenenAnlagen_AccTest")
    public void neubestellungMitKlammerUndVerschiedenenAnlagen() throws Exception {
        // @formatter:off
        useCase(WitaAcceptanceUseCase.TAL_BEREIT_NEU_02B, WITA_CDM_VERSION);

        AcceptanceTestWorkflow workflowMaster = workflow().get();
        AcceptanceTestWorkflow workflowChild = workflow().get();
        GeneratedTaifunData taifunData = taifunDataFactory.surfAndFonWithDns(0, true);

        AcceptanceTestDataBuilder builderMaster = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_02BA)
                .withTaifunData(taifunData)
                .withRangLeiste1("01")
                .withRangStift1("01")
                .withCreateVormieter()
                .withCudaKuendigung();
        CreatedData masterCreatedData = workflowMaster.createData(builderMaster);

        AcceptanceTestDataBuilder builderChild = workflow().getTestDataBuilder()
                .withUserName(TAL_BEREIT_NEU_02BB)
                .withTaifunData(taifunData)
                .withRangLeiste1("01")
                .withRangStift1("02")
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(builderMaster.getVorgabeMnet().toLocalDate(), 7).atStartOfDay())
                .withLageplan().withCudaKuendigung();
        CreatedData childCreatedData = workflowChild.createData(builderChild);

        parallel(
                sequential(hurrican().createMasterChildCbVorgang(workflowMaster, masterCreatedData, workflowChild, childCreatedData)),

                sequential(
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("CUDAKUENDIGUNG_RESPONSE"),
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("LAGEPLAN_RESPONSE"),
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("CUDAKUENDIGUNG_RESPONSE")
                )
        );

        atlas().receiveOrder();
        atlas().receiveOrder();

        variables().add(VariableNames.EXTERNAL_ORDER_ID, String.format("${%s}", VariableNames.MASTER_EXTERNAL_ORDER_ID));

        atlas().sendNotification("QEB");
        workflow().select(workflowMaster).waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().select(workflowMaster).waitForABM();

        atlas().sendNotification("ERLM");
        workflow().select(workflowMaster).waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().select(workflowMaster).waitForENTM();

        workflow().select(workflowMaster).doWithWorkflow(workflow -> { workflow.assertBktoFakturaSet(); } );
        workflow().select(workflowMaster).doWithWorkflow(workflow -> { workflow.assertAnlageSet(1); } );
        workflow().select(workflowMaster).doWithWorkflow(workflow -> { workflow.assertVormieterSet(); } );
        workflow().select(workflowMaster).doWithWorkflow(workflow -> { workflow.assertAuftragsKennerSet(); } );
        workflow().select(workflowMaster).doWithWorkflow(workflow -> { workflow.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H); } );

        workflow().select(workflowChild).doWithWorkflow(workflow -> { workflow.assertAnlageSet(2); } );
        workflow().select(workflowChild).doWithWorkflow(workflow -> { workflow.assertAuftragsKennerSet(); } );
        workflow().select(workflowChild).doWithWorkflow(workflow -> { workflow.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H); } );
        // @formatter:on
    }

    @CitrusTest(name = "GeschaeftsfallNeu_NeubestellungMitTamUndVorzeitigerTV_AccTest")
    public void neubestellungMitTamUndVorzeitigerTV() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_ABM_TV_TAM_ABM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_ABM_TV_TAM_ABM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().sendTv();
        atlas().receiveNotification()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("TAM");
        workflow().waitForTAM();

        atlas().sendNotification("ABM_2");
        workflow().waitForABM();

        workflow().doWithWorkflow(workflow -> {
            assertEquals(workflow.getCbVorgang().getStatus(), CBVorgang.STATUS_ANSWERED); });
        workflow().doWithWorkflow(workflow -> {
            assertEquals(workflow.getCbVorgang().getTamUserTask().getStatus(), UserTask.UserTaskStatus.GESCHLOSSEN); });
    }

    @CitrusTest(name = "GeschaeftsfallNeu_NeubestellungMitTamUndVorzeitigerTvErlmCloseUserTask_AccTest")
    public void neubestellungMitTamUndVorzeitigerTvErlmCloseUsertask() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_ABM_TV_TAM_ERLM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_ABM_TV_TAM_ERLM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().sendTv();
        atlas().receiveNotification()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("TAM");
        workflow().waitForTAM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        workflow().doWithWorkflow(workflow -> {
            assertEquals(workflow.getCbVorgang().getStatus(), CBVorgang.STATUS_ANSWERED); });
        workflow().doWithWorkflow(workflow -> {
            assertEquals(workflow.getCbVorgang().getTamUserTask().getStatus(), UserTask.UserTaskStatus.GESCHLOSSEN); });
    }

    @CitrusTest(name = "GeschaeftsfallNeu_NeubestellungTamMahnTamAbbm_AccTest")
    public void neubestellungTamMahnTamAbbm() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_TAM_TAM_ABBM, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ABM_TAM_TAM_ABBM);
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        atlas().sendNotification("TAM");
        workflow().waitForTAM();

        atlas().sendNotification("TAM");
        workflow().waitForTAM();

        atlas().sendNotification("ABBM");
        workflow().waitForABBM();

        workflow().doWithWorkflow(workflow -> { assertTrue(workflow.getCbVorgang().isAnswered()); });
        workflow().doWithWorkflow(workflow -> {
            assertEquals(workflow.getCbVorgang().getTamUserTask().getStatus(), UserTask.UserTaskStatus.GESCHLOSSEN); });
    }

    @CitrusTest(name = "GeschaeftsfallNeu_NeubestellungWithAbwAndAbmTerminDayBeforeHoliday_AccTest")
    public void neubestellungWithAbwAndAbmTerminDayBeforeHoliday() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_BEFORE_HOLIDAY, WITA_CDM_VERSION);

        workflow().sendBereitstellung(WitaSimulatorTestUser.NEU_QEB_ABM_BEFORE_HOLIDAY)
            .testDataBuilder()
            .withCBVorgangMontagehinweis(String.format("test %s providerwechsel", WitaCBVorgang.ANBIETERWECHSEL_46TKG));
        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        int currentYear = LocalDate.now().getYear();

        // set the ABM date to a day before holiday
        variables().add(
                VariableNames.REQUESTED_CUSTOMER_DATE,
                DateTools.formatDate(Date.from(LocalDate.of(currentYear + 1, 8, 7).atStartOfDay(ZoneId.systemDefault()).toInstant()), // 8.8. ist Feiertag!
                        DateTools.PATTERN_YEAR_MONTH_DAY));

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().doWithWorkflow(workflow -> {
            workflow.assertKlaerfall(true, "Der best.*tigte Liefertermin liegt auf einem schaltfreien Tag oder auf einem Tag VOR einem schaltfreien Tag.*"); } );
    }

}
