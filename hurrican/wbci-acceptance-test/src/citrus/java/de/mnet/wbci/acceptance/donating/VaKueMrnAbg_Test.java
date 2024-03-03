/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendABBMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendABBM_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;

/**
 * Test fuer WBCI Geschaeftsfall 'Kuendigung mit Rufnummernuebernahme - abgebend'. (M-net = abgebender EKP)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAbg_Test extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests the ABBM-TR when sent from the Donating Carrier (M-Net in this case):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     AKMTR    ->
     *              <-  ABBMTR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.UETN_NM));
    }

    /**
     * Tests the ABBM when sent from the Donating Carrier (M-Net in this case):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.RNG));
    }

    /**
     * Tests receiving KUEMRN with Einzelrufnummer from the Donating Carrier (M-Net in this case): Match zu Hurrican
     * Auftrags Nr. 726564, und Taifun-Nr. 1907209 (over manual assignment)
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     * </pre>
     * <p/>
     * => use this for GUI - RUEM-VA / ABBM Tests (with WITA-VertragsNr as technical resource)
     */
    @CitrusTest
    public void VaKueMrnAbg_04_Test() {

        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildingsAutoDetermination(true, new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                                .addRufnummer(new RufnummerOnkzBuilder().withOnkz("821").withRufnummer("888877").build())
                                .withAlleRufnummernPortieren(true)
                                .build()
                ).build()));

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    /**
     * Tests receiving KUEMRN with Einzelrufnummer from the Donating Carrier (M-Net in this case): Match zu Hurrican
     * Auftrags Nr. 839933, und Taifun-Nr. 2132784 (over manual assignment).
     * <p/>
     * Use this for GUI-Tests of RUEM-VA-LineIds;
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *
     * </pre>
     * <p/>
     * => use this for GUI - RUEM-VA / ABBM Tests (with WBCI-Line-ID as technical resource)
     */
    @CitrusTest
    public void VaKueMrnAbg_05_Test() {

        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_05, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildingsAutoDetermination(true, new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                                .addRufnummer(new RufnummerOnkzBuilder().withOnkz("8703").withRufnummer("4659584").build())
                                .withAlleRufnummernPortieren(true)
                                .build()
                ).build()));

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    /**
     * Tests receiving KUEMRN with Einzelrufnummer from the Donating Carrier (M-Net in this case): Match zu Hurrican
     * Auftrags Nr. 808936, und Taifun-Nr. 2070349 (over manual assignment).
     * <p/>
     * Use this for GUI-Tests of RUEM-VA-LineIds;
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *
     * </pre>
     * <p/>
     * => use this for GUI - RUEM-VA / ABBM Tests (with expected but not creatable WBCI-Line-ID as technical resource)
     */
    @CitrusTest
    public void VaKueMrnAbg_06_Test() {

        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_06, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildingsAutoDetermination(true, new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                                .addRufnummer(new RufnummerOnkzBuilder().withOnkz("821").withRufnummer("227927130").build())
                                .withAlleRufnummernPortieren(true)
                                .build()
                ).build()));

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    /**
     * Tests receiving KUEMRN with one matching and one mismatching Einzelrufnummer from the Donating Carrier (M-Net in
     * this case): Match zu Hurrican Auftrags Nr. 726564, und Taifun-Nr. 1907209 (over manual assignment)
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB                            Hurrican (Donating Carrier)
     *     KUEMRN                      ->
     *      |- 0821 888777 OK
     *      |- 999 88878 NOT OK
     *
     * </pre>
     * <p/>
     * => use this for GUI - ABBM Tests (RNG with all active Einzelrufnummern)
     */
    @CitrusTest
    public void VaKueMrnAbg_07_Test() {

        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_07, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildingsAutoDetermination(true, new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                                .addRufnummer(new RufnummerOnkzBuilder().withOnkz("821").withRufnummer("888877").build())
                                .addRufnummer(new RufnummerOnkzBuilder().withOnkz("999").withRufnummer("888878").build())
                                .build()
                ).build()));

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    /**
     * Tests receiving KUEMRN with one matching and one mismatching Blockrufnummer from the Donating Carrier (M-Net in
     * this case): Match zu Hurrican Taifun-Nr. 2079463 (over manual assignment)
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB                            Hurrican (Donating Carrier)
     *     KUEMRN                      ->
     *      |- 089 45841 0
     *      |-- 000 - 599 OK
     *      |-- 600 - 700 NOT OK
     *
     * </pre>
     * <p/>
     * => use this for GUI - ABBM Tests (RNG with empty Rufnummernlist)
     */
    @CitrusTest
    public void VaKueMrnAbg_08_Test() {

        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_08, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildingsAutoDetermination(true, new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(new RufnummernportierungAnlageBuilder()
                                .withOnkz("89").withDurchwahlnummer("45841").withAbfragestelle("0")
                                .addRufnummernblock(new RufnummernblockBuilder().withRnrBlockVon("000").withRnrBlockBis("599").build())
                                .addRufnummernblock(new RufnummernblockBuilder().withRnrBlockVon("600").withRnrBlockBis("700").build())
                                .build()
                ).build()));

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    /**
     * Tests the typical KUEMRN use-case, but with an AKM-TR without PKIAuf set within it:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     AKMTR    ->
     *              <-  ABBMTR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_09_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_09, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveAKMTR_TestBehavior()
                .withExpectedKlaerfall(true));

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.UETN_NM));
    }

    /**
     * Tests receiving KUEMRN with Portierungszeitfenster ZF3 which is not supported. GF should be marked for
     * clarification.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_10_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_10, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    /**
     * Tests the ABBM-TR when sent from the Donating Carrier (M-Net in this case):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     AKMTR    ->
     *              <-  ABBMTR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_11_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_11, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.UETN_NM)
                .withExpectedException(WbciServiceException.class,
                        "Da in der AKM-TR keine Ressourcenübernahme angefordert wurde, ist das Versenden einer " +
                                "ABBM-TR mit dem MeldungsCode UETN_NM oder UETN_BB nicht möglich."
                ));
    }

    /**
     * Tests receiving KUEMRN with one matching and two mismatching Einzelrufnummern from the Donating Carrier (M-Net in
     * this case): Match to Hurrican Auftrags Nr. 674039, und Taifun-Nr. 1791322
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB                            Hurrican (Donating Carrier)
     *     KUEMRN                      ->
     *      |- 09131 6104357 OK
     *      |- 09131 610358 NOT OK
     *      |- 09131 610359 NOT OK
     *                                          DNs in Taifun
     *                                          |- 09131 6104357
     *                                          |- 09131 6104358
     *                                          |- 09131 6104359
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_12_Test() {

        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_12, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior().withSearchBuildingsAutoDetermination(true, new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                                .addRufnummer(new RufnummerOnkzBuilder().withOnkz("9131").withRufnummer("6104357").build())
                                .addRufnummer(new RufnummerOnkzBuilder().withOnkz("9131").withRufnummer("610358").build())
                                .addRufnummer(new RufnummerOnkzBuilder().withOnkz("9131").withRufnummer("610359").build())
                                .build()
                ).build()));

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    /**
     * Tests receiving KUEMRN with an matching Hurrican Auftrag and Taifun-Nr. <br/>
     * Afterward the test secures that the {@link SendRUEMVA_TestBehavior} will check that a WITA
     * {@link de.mnet.wita.model.VorabstimmungAbgebend} is created.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  PROTOCOL_VA
     *              <-  RUEM-VA
     *                   |- create VorabstimmungAbgebend
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_13_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_13, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        final Rufnummer rufnummer = generatedTaifunData.getDialNumbers().get(0);
        final Long expectedTaifunOrderId = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();

        applyBehavior(new ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior(generatedTaifunData));
        hurrican().assertVaRequestAssignedToOrder(expectedTaifunOrderId, KundenTyp.PK);
        atlas().receiveCustomerServiceMessage("PROTOCOL_VA");

        // SendRUEMVA asserts that a VorabstimmungAbgebend is created
        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .addEinzelrufnummer(rufnummer.getOnKz(), rufnummer.getDnBase(), "D123"));
        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests receiving KUEMRN with an matching Hurrican Auftrag and Taifun-Nr.
     * Afterward the test secures that the {@link SendABBM_TestBehavior} test behavior will
     * check that a WITA {@link de.mnet.wita.model.VorabstimmungAbgebend} is created.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  PROTOCOL_VA
     *              <-  ABBM
     *                   |- create VorabstimmungAbgebend
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_14_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_14, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        final Long expectedTaifunOrderId = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();

        applyBehavior(new ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior(generatedTaifunData));
        hurrican().assertVaRequestAssignedToOrder(expectedTaifunOrderId, KundenTyp.PK);
        atlas().receiveCustomerServiceMessage("PROTOCOL_VA");

        // SendABBM asserts that a VorabstimmungAbgebend is created
        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.KNI));
        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests receiving KUEMRN with an matching Hurrican Auftrag and Taifun-Nr.
     * After sending the corresponding RUEM-VA a WITA {@link de.mnet.wita.model.VorabstimmungAbgebend} should be
     * created. After receiving the AKM-TR with RessourcenUebernahme=ja the carrier within the created WITA
     * VorabstimmungAbgebend should be updated with the PKIAuf from the AKM-TR.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  PROTOCOL_VA
     *              <-  RUEM-VA
     *                   |- create VorabstimmungAbgebend
     *     AKMTR    ->
     *                   |- update Carrier in VorabstimmungAbgebend based on PKIAuf in AKMTR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_15_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_15, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        final Rufnummer rufnummer = generatedTaifunData.getDialNumbers().get(0);
        final Long expectedTaifunOrderId = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();

        applyBehavior(new ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior(generatedTaifunData));
        hurrican().assertVaRequestAssignedToOrder(expectedTaifunOrderId, KundenTyp.PK);
        atlas().receiveCustomerServiceMessage("PROTOCOL_VA");

        // SendRUEMVA asserts that a VorabstimmungAbgebend is created
        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .addEinzelrufnummer(rufnummer.getOnKz(), rufnummer.getDnBase(), "D123"));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

}
