/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.06.2014
 */
package de.mnet.wbci.acceptance.elektra;

import static de.mnet.wbci.citrus.VariableValues.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import java.util.*;
import javax.sql.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueOrnKftBuilder;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveTV_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendERLM_TV_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveERLM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendTV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_WithTaifunAndHurricanOrder_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.AutomationTaskBuilder;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class ElektraService_AccTest extends AbstractWbciAcceptanceTestBuilder {

    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    @Autowired
    private DataSource hurricanDataSource;

    /**
     * Tests the Elektra automation for RUEM-VA notification:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)      Elektra
     *              <-  VA
     *     RUEMVA   ->
     *                                               ->   RUEM-VA Automation
     * </pre>
     */
    @CitrusTest
    public void ElektraService_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.ElektraService_01, WBCI_CDM_VERSION, wbciVersion, VA_KUE_ORN);

        applyBehavior(new SendVA_WithTaifunAndHurricanOrder_TestBehavior(getNewTaifunDataFactory().surfAndFonWithDns(1).persist()));

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));

        hurrican().createElektraServiceRequest(MeldungTyp.RUEM_VA);

        variables().add(VariableNames.RESCHEDULED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 21)");

        elektra().receiveElektraServiceRequest("ChangeOrderDialNumberRequest");

        elektra().sendElektraServiceResponse("ChangeOrderDialNumberResponse");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build()
        ));

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests the Elektra automation for AKM-TR notification:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)      Elektra
     *     VA       ->
     *              <-  RUEMVA
     *     AKMTR    ->
     *                                               ->  AKMTR Automation
     * </pre>
     */
    @CitrusTest
    public void ElektraService_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.ElektraService_02, WBCI_CDM_VERSION, wbciVersion, VA_KUE_MRN);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();

        Long billingOrderNoOrig = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();
        String vaId = hurrican().createPreAgreementId(RequestTyp.VA);
        variable(VariableNames.BILLING_ORDER_NO_ORIG, billingOrderNoOrig);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(vaId));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .addEinzelrufnummer(UNKNOWN_ONKZ, "111111111", "D052"));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        hurrican().manuallyAssignTaifunOrderId(billingOrderNoOrig);

        hurrican().createElektraServiceRequest(MeldungTyp.AKM_TR);

        elektra().receiveElektraServiceRequest("UpdatePortKennungTnbRequest");

        elektra().sendElektraServiceResponse("UpdatePortKennungTnbResponse");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build()
        ));

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }


    /**
     * Tests the Elektra automation after sending a TV-ERLM notification:
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)         Elektra
     *     KUEORN   ->
     *              <-  RUEMVA
     *     TV   ->
     *              <-  ERLM
     *                                               ->     TV-ERLM Automation
     * </pre>
     */
    @CitrusTest
    public void ElektraService_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.ElektraService_03, WBCI_CDM_VERSION, wbciVersion, VA_KUE_ORN);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();

        Long billingOrderNoOrig = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();
        String vaId = hurrican().createPreAgreementId(RequestTyp.VA);
        variable(VariableNames.BILLING_ORDER_NO_ORIG, billingOrderNoOrig);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(vaId));

        hurrican().manuallyAssignTaifunOrderId(billingOrderNoOrig);

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        hurrican().closeWbciGeschaeftsfallProcessingAction();
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.PASSIVE);

        applyBehavior(new ReceiveTV_TestBehavior());

        applyBehavior(new SendERLM_TV_TestBehavior());

        hurrican().createElektraServiceRequest(RequestTyp.TV, MeldungTyp.ERLM);

        elektra().receiveElektraServiceRequest("ChangeOrderCancellationDateRequest");

        elektra().sendElektraServiceResponse("ChangeOrderCancellationDateResponse");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build()
        ));

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests the Elektra automation after receiving a TV-ERLM notification:
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)         Elektra
     *              <-  VA
     *     RUEMVA   ->
     *              <-  TV
     *     ERLM     ->
     *                                               ->     TV-ERLM Automation
     * </pre>
     */
    @CitrusTest
    public void ElektraService_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.ElektraService_04, WBCI_CDM_VERSION, wbciVersion, VA_KUE_ORN);

        final LocalDate kundenwunschtermin =
                DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);

        applyBehavior(
                new SendVA_WithTaifunAndHurricanOrder_TestBehavior(
                        getNewTaifunDataFactory().surfAndFonWithDns(1).persist(),
                        new WbciGeschaeftsfallKueOrnKftBuilder(WbciCdmVersion.V1).withKundenwunschtermin(kundenwunschtermin))
        );

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));

        applyBehavior(new ReceiveERLM_TV_TestBehavior());

        hurrican().createElektraServiceRequest(RequestTyp.TV, MeldungTyp.ERLM);

        elektra().receiveElektraServiceRequest("ChangeOrderDialNumberRequest");

        elektra().sendElektraServiceResponse("ChangeOrderDialNumberResponse");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build()
        ));

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests the Elektra automation for incoming RRNP processing:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)      Elektra
     *     VA(RRNP) ->
     *                                               ->  RRNP Automation
     * </pre>
     */
    @CitrusTest
    public void ElektraService_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.ElektraService_05, WBCI_CDM_VERSION, wbciVersion, VA_RRNP);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        hurrican().createElektraServiceRequest(RequestTyp.VA, null);

        elektra().receiveElektraServiceRequest("PortCancelledDialNumberRequest");

        elektra().sendElektraServiceResponse("PortCancelledDialNumberResponse");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build()
        ));
    }

}
