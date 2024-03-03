/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.13
 */
package de.mnet.wbci.acceptance.kft;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.LeitungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.UebernahmeRessourceMeldungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueOrnKftBuilder;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_VA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;

/**
 * Testfaelle TF 2.x : EKPauf und VA-KUE-ORN
 * 
 *
 */
@Test(groups = BaseTest.KFT)
public class Kft02_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_ORN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 2.1.01 Gutfall: RUEM-VA mit Auftragsbestätigung (MC 8001) und AKM-TR mit MC 8010:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_2_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020101", "020101")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());
    }

    /**
     * 2.1.02 RUEM-VA mit Auftragsbestätigung (MC 8001) und abweichender Adresse: AKM-TR mit MC 8105:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_2_1_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_1_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WBCI_CDM_VERSION).withOrt("Musterhausen"))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020102", "020104")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA, MeldungsCode.ADAORT));

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withoutUebernahme()));
    }

    /**
     * 2.2.01 falsche RN: ABBM mit MC 8103:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WBCI_CDM_VERSION)
                        .withAnschlussIdentifikation(
                        new RufnummerOnkzBuilder()
                                .withOnkz("111")
                                .withRufnummer("999999999").build()
                        ))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020201", "020201")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.RNG));
    }

    /**
     * 2.2.02 falsche Adresse: ABBM mit MC 8108:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WBCI_CDM_VERSION).withPostleitzahl("80000"))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020202", "020202")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.ADFPLZ));
    }

    /**
     * 2.2.03 falsche Adresse: PLZ und Ort, ABBM mit MC 8108, 8109:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_03() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WBCI_CDM_VERSION).withPostleitzahl("80000"))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020203", "020206")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.ADFPLZ, MeldungsCode.ADFORT));
    }

    /**
     * 2.2.04 falscher Anschlussinhaber: ABBM mit MC 8112:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_04() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WBCI_CDM_VERSION)
                        .withAnschlussinhaber("Mustermann", "Max", Anrede.HERR))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020204", "020207")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.AIF));
    }

    /**
     * 2.2.05 falsche ID: ABBM mit MC 8115:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_05() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_05, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(null)
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020205", "020210")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 2.2.06 Kunde unbekannt: ABBM mit MC 8117:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_06() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_06, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WBCI_CDM_VERSION)
                        .withEndkunde("Mustermann", "Max", Anrede.HERR))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020206", "020212")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.KNI));
    }

    /**
     * 2.2.07 falscher Projektkenner: ABBM mit MC 8149:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_07() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_07, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(null)
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020207", "020214")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.UPK));
    }

    /**
     * 2.2.08 Vertragsnummer in AKM-TR falsch: ABBM-TR mit MC 8143:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    @Test(enabled = false) // TODO: unstable test
    public void Test_2_2_08() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_08, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020208", "020216")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withLeitungen(new LeitungKftBuilder(WBCI_CDM_VERSION).withVertragsnummer("123456789").build())));

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.WVNR_ABG));
    }

    /**
     * 2.2.09 LineID in AKM-TR falsch: ABBM-TR mit MC 8144:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_09() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_09, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020209", "020217")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withLeitungen(new LeitungKftBuilder(WBCI_CDM_VERSION)
                                .withVertragsnummer(null)
                                .withLineId(CarrierCode.DTAG.getITUCarrierCode() + ".1234567").build())));

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.LID_ABG));
    }

    /**
     * 2.2.10 Übernahme Ressource nicht möglich: ABBM-TR mit MC 8147:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    public void Test_2_2_10() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_10, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020210", "020220")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withUebernahme(true)));

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.UETN_NM));
    }

    /**
     * 2.2.11 AKM-TR doppelt versenden: ABBM-TR mit MC 8148:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    // Da es nicht moeglich ist ueber Hurrican eine zweite AKM-TR zu schicken, nach dem die erste rausgeschickt
    // worden ist, muessen wir die AKM-TR direkt an Atlas-ESB schicken
    public void Test_2_2_11() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_2_2_11, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "020211", "020221")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());

        kft().sendCarrierChangeUpdate("AKMTR");
        atlas().receiveCarrierChangeUpdate("AKMTR");

        applyBehavior(new ReceiveABBMTR_TestBehavior(false, MeldungsCode.UETN_BB));
    }

}
