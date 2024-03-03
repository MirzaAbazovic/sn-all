/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 25.07.2014 
 */
package de.mnet.wbci.acceptance.receiving;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.ReceiveERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendSTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveERLM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendTV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_WithTaifunAndHurricanOrder_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.AutomationTaskBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

/**
 * Tests the WBCI Automcation Service
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class AutomationService_AccTest extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;

    /**
     * Tests the automated RUEM-VA processing, good case.
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)      Elektra
     *              <-  VA
     *     RUEMVA   ->
     *                  ChangeOrderDialNumber           ->
     *              <-  AKM-TR
     * </pre>
     */
    @CitrusTest
    public void AutomationService_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.AutomationService_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        // make sure that no other GFs will be automatically processed
        hurrican().resetAutomatedRuemVaProcessing();

        applySendAutomatableVA_TestBehavior();
        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));

        // triggger auto-processing
        hurrican().triggerAutomatedRuemVaProcessing();

        variables().add(VariableNames.RESCHEDULED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 21)");

        elektra().receiveElektraServiceRequest("ChangeOrderDialNumberRequest");

        elektra().sendElektraServiceResponse("ChangeOrderDialNumberResponse");

        atlas().receiveCarrierChangeUpdate("AKMTR");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build(),
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.WBCI_SEND_AKMTR)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build()
        ));

        hurrican().assertVaRequestStatus(WbciRequestStatus.AKM_TR_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests the automated RUEM-VA processing, with negative Elektra response.
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)      Elektra
     *              <-  VA
     *     RUEMVA   ->
     *                  ChangeOrderDialNumber           ->
     * </pre>
     */
    @CitrusTest
    public void AutomationService_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.AutomationService_02, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        // make sure that no other GFs will be automatically processed
        hurrican().resetAutomatedRuemVaProcessing();

        applySendAutomatableVA_TestBehavior();
        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));

        // triggger auto-processing
        hurrican().triggerAutomatedRuemVaProcessing();

        variables().add(VariableNames.RESCHEDULED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 21)");

        elektra().receiveElektraServiceRequest("ChangeOrderDialNumberRequest");

        elektra().sendElektraServiceResponse("ChangeOrderDialNumberResponse");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.ERROR)
                        .withName(AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .build()
        ));

        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests the automated RUEM-VA processing with a RUEM-VA which contains more than WITA-Vertragsnummer. In such a
     * case the sending of the AKM-TR is not possible and should be marked as faulty.
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)      Elektra
     *              <-  VA
     *     RUEMVA   ->
     *                  ChangeOrderDialNumber           ->
     *              <-  AKM-TR (faulty because of more than one WITA-Vertragsnummer within the RUEM-VA)
     * </pre>
     */
    @CitrusTest
    public void AutomationService_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.AutomationService_03, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        // make sure that no other GFs will be automatically processed
        hurrican().resetAutomatedRuemVaProcessing();

        // create VA that can be automatically processed
        applySendAutomatableVA_TestBehavior();
        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));

        // triggger auto-processing
        hurrican().triggerAutomatedRuemVaProcessing();

        variables().add(VariableNames.RESCHEDULED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 21)");

        elektra().receiveElektraServiceRequest("ChangeOrderDialNumberRequest");

        elektra().sendElektraServiceResponse("ChangeOrderDialNumberResponse");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build(),
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.ERROR)
                        .withName(AutomationTask.TaskName.WBCI_SEND_AKMTR)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(null)
                        .build()
        ));

        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }


    /**
     * Tests the automated ERLM-TV processing and expects an automation failure because of a missing/inactive WITA
     * order.
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)        WITA
     *              <-  VA
     *     RUEMVA   ->
     *              <-  TV
     *     ERLM-TV  ->
     *                  doTerminverschiebung           ->   Failure because WITA workflow for VA-Id not active
     * </pre>
     */
    @CitrusTest
    public void AutomationService_ErlmTv_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.AutomationService_ErlmTv_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        // make sure that no other GFs will be automatically processed
        hurrican().resetAutomatedErlmTvProcessing();

        // create VA that can be automatically processed
        applySendAutomatableVA_TestBehavior();
        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                LocalDateTime.now().plusDays(30).toLocalDate())));
        applyBehavior(new ReceiveERLM_TV_TestBehavior());

        // WITA Vorgang erstellen (aber keine Activiti Workflow-Instanz -> somit ist der WITA Vorgang nicht 'aktiv')
        hurrican().storeWitaCBVorgang(
                new WitaCBVorgangBuilder()
                        .withAuftragId(1L)
                        .withCarrierRefNr("020001")
                        .withTyp(WitaCBVorgang.TYP_REX_MK)
                        .build(),
                true);

        // triggger auto-processing of ERLM-TVs
        hurrican().triggerAutomatedErlmTvProcessing();

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.ERROR)
                        .withName(AutomationTask.TaskName.WITA_SEND_TV)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withExecutionLog("Konnte keinen (oder keinen eindeutigen) aktiven WITA Vorgang zur Vorabstimmungs-Id ")
                        .build()
        ));

        // GF has to be active because of automation error!
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests the automated STR-AUFH ERLM processing and expects an automation failure because of a missing/inactive WITA
     * order.
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)        WITA
     *              <-  VA
     *     RUEMVA   ->
     *              <-  STR-AUFH
     *     ERLM     ->
     *                  doStorno                       ->   Failure because WITA workflow for VA-Id not active
     * </pre>
     */
    @CitrusTest
    public void AutomationService_StrAufhErlm_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.AutomationService_ErlmStrAufh1, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        // make sure that no other GFs will be automatically processed
        hurrican().resetAutomatedErlmStrAufhProcessing();

        // create VA that can be automatically processed
        applySendAutomatableVA_TestBehavior();
        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));
        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_AUF));
        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        // WITA Vorgang erstellen (aber keine Activiti Workflow-Instanz -> somit ist der WITA Vorgang nicht 'aktiv')
        hurrican().storeWitaCBVorgang(
                new WitaCBVorgangBuilder()
                        .withAuftragId(1L)
                        .withCarrierRefNr("020001")
                        .withTyp(WitaCBVorgang.TYP_REX_MK)
                        .build(),
                true);

        // triggger auto-processing of ERLM-TVs
        hurrican().triggerAutomatedStrAufhErlmProcessing();

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.ERROR)
                        .withName(AutomationTask.TaskName.WITA_SEND_STORNO)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withExecutionLog("Konnte keinen (oder keinen eindeutigen) aktiven WITA Vorgang zur Vorabstimmungs-Id ")
                        .build()
        ));

        // GF has to be active because of automation error!
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }


    /**
     * Tests the automated AKM-TR processing, bad case because WITA order could not be created.
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)      Elektra
     *              <-  VA
     *     RUEMVA   ->
     *                  ChangeOrderDialNumber           ->
     *              <-  AKM-TR
     *                    |
     *                    |--> try to create WITA order and fail because of missing ports
     * </pre>
     */
    @CitrusTest
    public void AutomationService_AkmTr_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.AutomationService_AkmTr_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        // make sure that no other GFs will be automatically processed
        hurrican().resetAutomatedRuemVaProcessing();
        hurrican().resetAutomatedAkmTrProcessing();

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(3).persist();

        // create VA that can be automatically processed
        applyBehavior(new SendVA_TestBehavior()
                .withGeneratedTaifunData(generatedTaifunData)
                .withUserId(USER_ID_GLINKJO)
                .withRealTaifunAndHurricanAuftrag(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig(), 1L)
                .withAutoprocessing(true));
        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));
        applyBehavior(new SendAKMTR_TestBehavior());

        hurrican().assertVaRequestStatus(WbciRequestStatus.AKM_TR_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().changeGfStatus(WbciGeschaeftsfallStatus.PASSIVE);
        hurrican().changeMnetTechnologie(Technologie.TAL_ISDN);

        // triggger AKM-TR auto-processing
        hurrican().triggerAutomatedAkmTrProcessing();
        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.ERROR)
                        .withName(AutomationTask.TaskName.WITA_SEND_ANBIETERWECHSEL)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withExecutionLog("Unexpected error during the automatic creation of the WITA order of typ ANBIETERWECHSEL")
                        .build()
        ));
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        doFinally(hurrican().closeWbciGeschaeftsfallAction());

    }

    private void applySendAutomatableVA_TestBehavior() {
        applyBehavior(new SendVA_WithTaifunAndHurricanOrder_TestBehavior(
                getNewTaifunDataFactory().surfAndFonWithDns(1).persist())
                .withAutoprocessing(true));
    }


    /**
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)      Elektra
     *              <-  VA
     *     RUEMVA   ->
     *                  ChangeOrderDialNumber           ->
     *              <-  AKM-TR
     * </pre>
     */
    @Test(enabled = false)
    @CitrusTest
    public void ngnReviewDataGenerator_Test() {
        simulatorUseCase(WbciSimulatorUseCase.AutomationService_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        // make sure that no other GFs will be automatically processed
        hurrican().resetAutomatedRuemVaProcessing();

        applyBehavior(new SendVA_WithTaifunAndHurricanOrder_TestBehavior(
                getNewTaifunDataFactory().surfAndFonWithDns(1).persist())
                .withAutoprocessing(true));

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));

        atlas().sendCarrierChangeUpdate("RUEMVA");

        // triggger auto-processing
        hurrican().triggerAutomatedRuemVaProcessing();

        variables().add(VariableNames.RESCHEDULED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 21)");

        elektra().receiveElektraServiceRequest("ChangeOrderDialNumberRequest");

        elektra().sendElektraServiceResponse("ChangeOrderDialNumberResponse");

        atlas().receiveCarrierChangeUpdate("AKMTR");

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

}
