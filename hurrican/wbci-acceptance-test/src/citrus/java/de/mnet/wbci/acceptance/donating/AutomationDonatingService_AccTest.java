/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.14
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.ReceiveSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.model.Carrier;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.TriggerIncomingAkmTrProcessing_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.TriggerOutgoingRuemVaProcessing_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.TriggerStrAufhErlmDonatingProcessing_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 * Tests the WBCI Automcation Service (abgebende Faelle)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class AutomationDonatingService_AccTest extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;

    /**
     * Tests the automated RUEM-VA and AKM-TR processing, good case.
     * M-net = donating carrier
     * <pre>
     *     AtlasESB               Hurrican (Receiving Carrier)                 Elektra        WITA
     *     VA                ->
     *                       <-   RUEMVA
     *                            cancelOrder                        ->
     *     AKM-TR            ->
     *                            WITA cancellation                                       ->   check if CBVorgang is created - no communication!
     *                            Update PKI Auf in Taifun           ->
     *     STR-AUFH          ->
     *                       <-   ERLM (STR-AUFH)
     *
     *                            (auto processing called)
     *                            check: Hurrican order re-activated
     *                            WITA cancellation                                       ->   check if CBVorgang is cancelled
     *                            Taifun undoCancellation            ->
     *
     * </pre>
     */
    @CitrusTest
    @Test(enabled = false) // TODO: unstable test
    public void AutomationDonatingService_01_Success_Test() {
        automationDonatingProcessing(false);
    }

    /**
     * Ablauf analog zu {@link AutomationDonatingService_AccTest#AutomationDonatingService_01_Success_Test()}
     * <p/>
     * Allerdings wird fuer UNDO_AUFTRAG_KUENDIGUNG ein AutomationError erwartet, da die WITA Kuendigung nicht storniert
     * werden konnte!
     */
    @CitrusTest
    @Test(enabled = false) // TODO: unstable test
    public void AutomationDonatingService_01_AutomationError_Test() {
        automationDonatingProcessing(true);
    }

    private void automationDonatingProcessing(boolean forceWitaStornoFailure) {
        simulatorUseCase(WbciSimulatorUseCase.DonatingAutomationService_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().premiumCallWithBlockDns(true, 200L).persist();

        Long billingOrderNoOrig = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();
        variable(VariableNames.BILLING_ORDER_NO_ORIG, billingOrderNoOrig);
        Long customerId = generatedTaifunData.getKunde().getKundeNo();
        variable(VariableNames.CUSTOMER_ID, customerId);

        // make sure that no other GFs will be automatically processed (by setting 'klaerfall' flag)
        hurrican().resetAutomatedOutgoingRuemVaProcessing();
        hurrican().cancelHurricanOrders(billingOrderNoOrig);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildings(false, false));

        hurrican().assignUserToWbciGeschaeftsfall(USER_ID_GLINKJO);

        hurrican().createHurricanMaxiDslHvtAuftrag(billingOrderNoOrig, customerId, true, false);
        hurrican().manuallyAssignTaifunOrderId(billingOrderNoOrig);

        hurrican().assertAuftragDatenStatus(AuftragStatus.IN_BETRIEB);

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withAutomatable()
                .addEinzelrufnummer("821", "123456789", "D043"));

        applyBehavior(new TriggerOutgoingRuemVaProcessing_TestBehavior());

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(
                new TriggerIncomingAkmTrProcessing_TestBehavior(true)
                        .withUpdatePortKennungTnb("UpdatePortKennungTnbRequest", "UpdatePortKennungTnbResponse")
        );

        hurrican().resetAutomatedErlmStrAufhDonatingProcessing();
        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_AUF).withExpectedKlaerfall(false));
        hurrican().clearKlaerfall();  // notwendig, weil durch Empfang von STR-AUFH Klaerfall gesetzt wird

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        if (forceWitaStornoFailure) {
            applyBehavior(
                    new TriggerStrAufhErlmDonatingProcessing_TestBehavior()
                            .withAutomationStatus(AutomationTask.AutomationStatus.ERROR));
        }
        else {
            hurrican().markWitaRequestAsUnsent();
            applyBehavior(new TriggerStrAufhErlmDonatingProcessing_TestBehavior());
        }

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }


    /**
     * Tests the automated RUEM-VA and AKM-TR processing, good case.
     * M-net = donating carrier
     * <pre>
     *     AtlasESB               Hurrican (Receiving Carrier)                 Elektra        WITA
     *     VA-KUE_ORN        ->
     *                       <-   RUEMVA
     *                            cancelOrder                        ->
     *     AKM-TR            ->
     *                            WITA cancellation                                       ->   check if CBVorgang is created - no communication!
     *
     * </pre>
     */
    @CitrusTest
    public void AutomationDonatingService_03_Success_Test() {
        GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_ORN;
        simulatorUseCase(WbciSimulatorUseCase.DonatingAutomationService_03, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().premiumCallWithBlockDns(true, 200L).persist();

        Long billingOrderNoOrig = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();
        variable(VariableNames.BILLING_ORDER_NO_ORIG, billingOrderNoOrig);
        Long customerId = generatedTaifunData.getKunde().getKundeNo();
        variable(VariableNames.CUSTOMER_ID, customerId);

        // make sure that no other GFs will be automatically processed (by setting 'klaerfall' flag)
        hurrican().resetAutomatedOutgoingRuemVaProcessing();
        hurrican().cancelHurricanOrders(billingOrderNoOrig);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildings(false, false));

        hurrican().assignUserToWbciGeschaeftsfall(USER_ID_GLINKJO);

        hurrican().createHurricanMaxiDslHvtAuftrag(billingOrderNoOrig, customerId, true, false);
        hurrican().manuallyAssignTaifunOrderId(billingOrderNoOrig);

        hurrican().assertAuftragDatenStatus(AuftragStatus.IN_BETRIEB);

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withAutomatable());

        applyBehavior(new TriggerOutgoingRuemVaProcessing_TestBehavior());

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests the automated RUEM-VA and AKM-TR processing; BadCase because existing 'Carrierbestellug' has invalid data.
     * M-net = donating carrier
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)      Elektra        WITA
     *     VA       ->
     *              <-  RUEMVA
     *                  cancelOrder                   ->
     *     AKM-TR   ->
     *                  WITA cancellation                           ->   ERROR because 'Carrierbestellung' is invalid!
     *
     * </pre>
     */
    @CitrusTest
    @Test(enabled = false) // TODO: unstable test
    public void AutomationDonatingService_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.DonatingAutomationService_02, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().premiumCallWithBlockDns(true, 200L).persist();

        Long billingOrderNoOrig = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();
        variable(VariableNames.BILLING_ORDER_NO_ORIG, billingOrderNoOrig);
        Long customerId = generatedTaifunData.getKunde().getKundeNo();
        variable(VariableNames.CUSTOMER_ID, customerId);

        // make sure that no other GFs will be automatically processed (by setting 'klaerfall' flag)
        hurrican().resetAutomatedOutgoingRuemVaProcessing();
        hurrican().cancelHurricanOrders(billingOrderNoOrig);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildings(false, false));

        hurrican().createHurricanMaxiDslHvtAuftrag(billingOrderNoOrig, customerId, true, true);
        hurrican().manuallyAssignTaifunOrderId(billingOrderNoOrig);

        hurrican().assertAuftragDatenStatus(AuftragStatus.IN_BETRIEB);

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withAutomatable()
                .addEinzelrufnummer("821", "123456789", "D043"));

        applyBehavior(new TriggerOutgoingRuemVaProcessing_TestBehavior());

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new TriggerIncomingAkmTrProcessing_TestBehavior(false).withUpdatePortKennungTnb("UpdatePortKennungTnbRequest", "UpdatePortKennungTnbResponse"));

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }


    /**
     * Tests the Storno Aenderung usecase sent from the Donating Carrier use case with successful change of the {@link
     * WbciGeschaeftsfall} in union with the cancelOrder#entryDate:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUE-ORN   ->
     *              <-  RUEM-VA
     *     STRAEN   ->
     *              <- ERLM
     *     KUE-MRN  ->
     *              <-  RUEM-VA
     *               cancelOrder (with KUE-ORN entry date)              ->   Elektra
     * </pre>
     */
    @CitrusTest
    @Test(enabled = false) // TODO: unstable test
    public void AutomationDonatingService_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.DonatingAutomationService_04, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        String vaKueOrnVaId = hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG);
        variables().add(VariableNames.PRE_AGREEMENT_ID, vaKueOrnVaId);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();

        applyBehavior(new ReceiveVA_TestBehavior(VA_KUE_ORN)
                .withGeneratedTaifunData(generatedTaifunData).withSearchBuildings(false, false));
        hurrican().assertVaRequestAssignedToOrder(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());

        //change creation date of the first VA's to check later the correct date of cancelOrder#entryDate
        hurrican().changeCreationDate(LocalDateTime.now().minusDays(2));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withRuemVaTemplate("RUEMVA_ORN")
                .withExplicitGeschaeftsfallTyp(VA_KUE_ORN));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_AUF).withExplicitGeschaeftsfallTyp(VA_KUE_ORN));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF).withExplicitGeschaeftsfallTyp(VA_KUE_ORN));

        // neue VA-Id erzeugen
        variables().add(VariableNames.PRE_AGREEMENT_ID, hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG));

        // VA KUE-MRN erstellen
        applyBehavior(new ReceiveVA_TestBehavior(GeschaeftsfallTyp.VA_KUE_MRN)
                .withExpectedWbciGeschaeftsfallStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withExpectedWbciRequestStatus(WbciRequestStatus.VA_EMPFANGEN)
                .withGeneratedTaifunData(generatedTaifunData)
                .withSearchBuildings(false, false));

        hurrican().assertLinkedStrAenGfStatus(WbciGeschaeftsfallStatus.COMPLETE);

        Long billingOrderNoOrig = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();
        variable(VariableNames.BILLING_ORDER_NO_ORIG, billingOrderNoOrig);
        Long customerId = generatedTaifunData.getKunde().getKundeNo();
        variable(VariableNames.CUSTOMER_ID, customerId);

        // make sure that no other GFs will be automatically processed (by setting 'klaerfall' flag)
        hurrican().resetAutomatedOutgoingRuemVaProcessing();
        hurrican().cancelHurricanOrders(billingOrderNoOrig);
        hurrican().assignUserToWbciGeschaeftsfall(USER_ID_GLINKJO);

        hurrican().createHurricanMaxiDslHvtAuftrag(billingOrderNoOrig, customerId, true, false);
        hurrican().manuallyAssignTaifunOrderId(billingOrderNoOrig);

        hurrican().assertAuftragDatenStatus(AuftragStatus.IN_BETRIEB);

        Rufnummer dialNumber = generatedTaifunData.getDialNumbers().get(0);
        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .addEinzelrufnummer(dialNumber.getOnKz(), dialNumber.getDnBase(), "D052")
                .withExplicitGeschaeftsfallTyp(VA_KUE_MRN)
                .withAutomatable());

        applyBehavior(new TriggerOutgoingRuemVaProcessing_TestBehavior());

        doFinally(hurrican().closeWbciGeschaeftsfallAction().withVorabstimmungsId(vaKueOrnVaId));
        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

}
