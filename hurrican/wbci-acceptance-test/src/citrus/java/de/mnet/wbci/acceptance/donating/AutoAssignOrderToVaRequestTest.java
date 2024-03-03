/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.acceptance.WbciSimulatorUseCase.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 * Test um die automatische Auftragszuordnung zu einem VA-Request zu testen. <br> Fuer die Tests notwendige Taifun-Daten
 * werden ueber die TaifunDataFactory dynamisch angelegt.
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class AutoAssignOrderToVaRequestTest extends AbstractWbciAcceptanceTestBuilder {

    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests receiving KUEMRN with 'Einzelrufnummer'. Asserts that a Taifun order is assigned to the request in
     * hurrican. (Test with simple test data - only one order matches to the given DialNumber 0821/123456789)
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEMRN                     ->
     *                                    Rufnummer is matching => no location service call
     *
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_01_Test() {
        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        doTest(AutoAssignOrderToVaRequest_01, VA_KUE_MRN, false, false, generatedTaifunData);
    }

    /**
     * Tests receiving KUEMRN with 'Einzelrufnummer'. Asserts that a Taifun order is assigned to the request in
     * hurrican. (Test data in Taifun: two orders match the given dial number - the dial number is historised; the 'ALT'
     * record is assigned to a cancelled order, the 'AKT' record is assigned to an active order.)
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEMRN                     ->
     *                                    Rufnummer is matching => no location service call
     *
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_02_Test() {
        GeneratedTaifunData cancelled = getNewTaifunDataFactory().surfAndFonWithDns(1, true, true, null).persist();
        Rufnummer dialNumber = cancelled.getDialNumbers().get(0);
        GeneratedTaifunData active = getNewTaifunDataFactory().surfAndFonWithDns(1);
        active.getDialNumbers().get(0).setDnNoOrig(dialNumber.getDnNoOrig());
        active.getDialNumbers().get(0).setOnKz(dialNumber.getOnKz());
        active.getDialNumbers().get(0).setDnBase(dialNumber.getDnBase());
        active.persist();

        doTest(AutoAssignOrderToVaRequest_02, VA_KUE_MRN, false, false, active);
    }

    /**
     * Tests receiving KUE-ORN. Asserts that a Taifun order is assigned to the request in hurrican. (Test data in
     * Taifun: only one order matches to the given location)
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEORN                     ->
     *                                <-  LocationService#searchBuildings
     *     SEARCH_BUILDINGS_RESPONSE  ->
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_03_Test() {
        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        doTest(AutoAssignOrderToVaRequest_03, GeschaeftsfallTyp.VA_KUE_ORN, true, false, generatedTaifunData);
    }

    /**
     * Tests receiving KUE-MRN. Asserts that a Taifun order is assigned to the request in hurrican. (Test data in
     * Taifun: search is done by a block number)
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEMRN                     ->
     *                                    Rufnummer is matching => no location service call
     *
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_04_Test() {
        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory()
                .premiumCallWithBlockDn(100L).persist();
        doTest(AutoAssignOrderToVaRequest_04, VA_KUE_MRN, false, false, generatedTaifunData);
    }

    /**
     * Tests receiving KUE-ORN. Asserts that a Taifun order is <b>NOT</b> assigned to the request in hurrican. In Taifun
     * two orders exist that match the given location and since no unique order was found, no order should be assigned.
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEORN                     ->
     *                                <-  LocationService#searchBuildings
     *     SEARCH_BUILDINGS_RESPONSE  ->
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_05_Test() {
        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(0).persist();
        generatedTaifunData.setBillingAuftrag(null);
        getNewTaifunDataFactory().surfAndFonWithDns(0, false, true, generatedTaifunData.getAddress().getGeoId());

        doTest(AutoAssignOrderToVaRequest_05, GeschaeftsfallTyp.VA_KUE_ORN, true, false, generatedTaifunData);
    }

    /**
     * Tests receiving KUEMRN with 3 'Einzelrufnummer'. Asserts that a Taifun order is assigned to the request in
     * hurrican. In Taifun an order exists matching the 3 numbers from the WBCI KUEMRN request.
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEMRN                     ->
     *                                    Rufnummer is matching => no location service call
     *
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_06_Test() {
        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(3).persist();
        doTest(AutoAssignOrderToVaRequest_06, VA_KUE_MRN, false, false, generatedTaifunData);
    }

    /**
     * Tests receiving KUEMRN with 3 'Einzelrufnummer'. Asserts that a Taifun order is assigned to the request in
     * hurrican. In Taifun one order matches 1 of the DialNumbers and a different order matches the other 2 DialNumbers.
     * However, since only one of the orders matched the address, this order is assigned to the request.
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEMRN                     ->
     *                                <-  LocationService#searchBuildings
     *     SEARCH_BUILDINGS_RESPONSE  ->
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    @Test
    public void AutoAssignOrderToVaRequest_07_Test() {
        GeneratedTaifunData taifunWith1Dn = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        GeneratedTaifunData taifunWith2Dns = getNewTaifunDataFactory().surfAndFonWithDns(2).persist();

        // Rufnummern der zweiten Daten-Generierung zur ersten hinzufuegen, damit diese von Citrus im Request
        // eingetragen werden
        taifunWith1Dn.getDialNumbers().addAll(taifunWith2Dns.getDialNumbers());

        doTest(AutoAssignOrderToVaRequest_07, VA_KUE_MRN, true, false, taifunWith1Dn);
    }

    /**
     * Tests assigning a new KUEMRN to a taifun order which already have an assigned and active WbciGeschaeftsfall. This
     * should result in rejecting the new KUEMRN with an ABBM with a MeldungsCode VAE.
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEMRN                     ->
     *                                <-  ABBM with VAE
     * </pre>
     */
    @CitrusTest
    @Test
    public void AutoAssignOrderToVaRequest_08_Test() {
        simulatorUseCase(AutoAssignOrderToVaRequest_08, WBCI_CDM_VERSION, wbciVersion, VA_KUE_MRN);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();

        Long expectedTaifunOrderId = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();

        String vorabstimmungsId = hurrican().createPreAgreementId(RequestTyp.VA);
        hurrican().createAndStoreGeschaeftsfall(new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION)
                .withVorabstimmungsId(vorabstimmungsId)
                .withBillingOrderNoOrig(expectedTaifunOrderId)
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET));

        // eine zweite Vorabstimmung schicken, die mit einer ABBM mit MC VAE abgelehnt werden sollte, weil bereits
        // eine aktive Vorabstimmung zu diesem Taifun-Auftrag existiert
        applyBehavior(new ReceiveVA_TestBehavior().withGeneratedTaifunData(generatedTaifunData).withSearchBuildings(false, false));
        hurrican().assignTaifunOrderAndRejectVa(expectedTaifunOrderId);
        hurrican().assertVaRequestAssignedToOrder(expectedTaifunOrderId, KundenTyp.PK);
        //Protokolleintrag für Taifunauftragszuordnung
        atlas().receiveCustomerServiceMessage("PROTOCOL_VA");

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertVaMeldungsCodes(MeldungsCode.VAE);
        hurrican().assertVaRequestStatus(WbciRequestStatus.ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.COMPLETE);
        atlas().receiveCarrierChangeUpdate("ABBM");
        //Protokolleintrag für eine ABBM
        atlas().receiveCustomerServiceMessage("PROTOCOL_VA");

        // die manuell erzeugte Vorabstimmungsanfrage muss abgeschlossen werden, damit der Test beim nächsten
        // Lauf nicht fehlschlägt
        doFinally(
                hurrican().closeWbciGeschaeftsfallAction().withVorabstimmungsId(vorabstimmungsId)
        );
    }

    /**
     * Tests receiving RRNP with 'Einzelrufnummer'. Asserts that a Taifun order is assigned to the request in hurrican.
     * (Test with simple test data - only one order matches to the given DialNumber)
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     RRNP                       ->
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_09_Test() {
        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        doTest(AutoAssignOrderToVaRequest_09, VA_RRNP, false, false, generatedTaifunData);
    }

    /**
     * Tests receiving RRNP with 'Blockrufnummer'. Asserts that a Taifun order is assigned to the request in hurrican.
     * (Test data in Taifun: search is done by a block number)
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     RRNP                     ->
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_10_Test() {
        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory()
                .premiumCallWithBlockDn(100L).persist();
        doTest(AutoAssignOrderToVaRequest_10, VA_RRNP, false, false, generatedTaifunData);
    }

    /**
     * Tests receiving KUE-MRN with block number. In Taifun two orders do match the given dn number base for the block.
     * One Taifun order is marked as billing relevant for this block number and should be assigned to the VA request as
     * main order.
     * <p>
     * <p>
     * <pre>
     *     AtlasESB                       Hurrican (Donating Carrier)
     *     KUEMRN                     ->
     *                                    Rufnummer matches 2 orders => location service call
     *
     *                                <-  CustomerService#addCommunication
     * </pre>
     */
    @CitrusTest
    public void AutoAssignOrderToVaRequest_11_Test() {
        GeneratedTaifunData billingRelevant = getNewTaifunDataFactory()
                .premiumCallWithBlockDn(100L).persist();

        Rufnummer dialNumber = billingRelevant.getDialNumbers().get(0);
        GeneratedTaifunData nonBillingRelevant = getNewTaifunDataFactory()
                .premiumCallWithBlockDn(
                        billingRelevant.getKunde(), billingRelevant.getAddress(), billingRelevant.getRInfo(),
                        100L, dialNumber.getOnKz(), dialNumber.getDnBase(), false)
                .persistOrderDataOnly();

        doTest(AutoAssignOrderToVaRequest_11, VA_KUE_MRN, true, false,
                billingRelevant, nonBillingRelevant.getBillingAuftrag().getAuftragNoOrig());
    }

    /**
     * Performs auto assign test and verifies incoming VA request, location search and order assignment status.
     *
     * @param useCase
     * @param gfType
     * @param searchBuildings
     * @param defaultSearch
     * @param generatedTaifunData
     * @param nonBillingTaifunOrderIds
     */
    private void doTest(WbciSimulatorUseCase useCase, GeschaeftsfallTyp gfType, boolean searchBuildings,
            boolean defaultSearch, GeneratedTaifunData generatedTaifunData,
            Long... nonBillingTaifunOrderIds) {

        simulatorUseCase(useCase, WBCI_CDM_VERSION, wbciVersion, gfType);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withGeneratedTaifunData(generatedTaifunData)
                .withSearchBuildings(searchBuildings, defaultSearch));

        Long taifunOrderId = (generatedTaifunData != null && generatedTaifunData.getBillingAuftrag() != null)
                ? generatedTaifunData.getBillingAuftrag().getAuftragNoOrig()
                : null;

        if (taifunOrderId != null) {
            KundenTyp kundenTyp = (generatedTaifunData != null && generatedTaifunData.getKunde().isBusinessCustomer())
                    ? KundenTyp.GK : KundenTyp.PK;
            hurrican().assertVaRequestAssignedToOrder(taifunOrderId, kundenTyp);
            atlas().receiveCustomerServiceMessage("PROTOCOL_VA");
        }
        else {
            hurrican().assertVaRequestNotAssignedToOrder();
        }

        if (nonBillingTaifunOrderIds.length > 0) {
            hurrican().assertNonBillingRelevantTaifunOrderIds(nonBillingTaifunOrderIds);
        }

        doFinally(
                hurrican().closeWbciGeschaeftsfallAction()
        );
    }


}
