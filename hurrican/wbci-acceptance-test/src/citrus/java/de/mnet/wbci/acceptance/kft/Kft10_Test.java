/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.13
 */
package de.mnet.wbci.acceptance.kft;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.RueckmeldungVorabstimmungKftBuilder;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendABBMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendABBM_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.StandortBuilder;

/**
 * Testfaelle TF 10.x : EKPabg und VA-KUE-ORN
 *
 *
 */
@Test(groups = BaseTest.KFT)
public class Kft10_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_ORN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 10.1.01 Gutfall: RUEM-VA mit Auftragsbestätigung (MC 8001 oder 8002) und AKM-TR mit MC 8010:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  RUEMVA
     *     AKM-TR   ->
     * </pre>
     */
    @CitrusTest
    public void Test_10_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        final String useCaseNumber = "100101";
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, useCaseNumber, useCaseNumber)));
        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));
        applyBehavior(new ReceiveAKMTR_TestBehavior());
    }

    /**
     * 10.1.02 Gutfall: RUEM-VA mit abweichender Adresse (MC 8105) und AKM-TR mit MC 8010
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  RUEMVA
     *     AKM-TR   ->
     * </pre>
     */
    @CitrusTest
    public void Test_10_1_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_1_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100102", "100104")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ADAORT, MeldungsCode.ZWA)
                .withBuilder(new RueckmeldungVorabstimmungKftBuilder(getWbciCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT)
                        .withExplicitMeldungsCode(
                                new StandortBuilder()
                                        .withOrt("Starnberg")
                                        .build(),
                                MeldungsCode.ADAORT
                        )));

        applyBehavior(new ReceiveAKMTR_TestBehavior());
    }

    /**
     * 10.2.01 Schlechtfall: falsche RN: ABBM mit MC 8103
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100201", "100201")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.RNG));
    }

    /**
     * 10.2.02 Schlechtfall: falsche Adresse: ABBM mit MC 8108
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    @Test(enabled = false)
    public void Test_10_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100202", "100202")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.ADFPLZ));
    }

    /**
     * 10.2.03 Schlechtfall: falsche Adresse: PLZ und Ort, ABBM mit MC 8108, 8109
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    @Test(enabled = false)
    public void Test_10_2_03() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100203", "100206")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.ADFPLZ, MeldungsCode.ADFORT));
    }

    /**
     * 10.2.04 Schlechtfall: falscher Anschlussinhaber: ABBM mit MC 8112
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_04() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100204", "100207")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.AIF));
    }

    /**
     * 10.2.05 Schlechtfall: falsche ID: ABBM mit MC 8115
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_05() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_05, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100205", "100210")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 10.2.06 Schlechtfall: Kunde unbekannt: ABBM mit MC 8117
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_06() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_06, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100206", "100212")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.KNI));
    }

    /**
     * 10.2.07 Schlechtfall: falscher Projektkenner: ABBM mit MC 8149
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_07() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_07, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100207", "100214")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.UPK));
    }

    /**
     * 10.2.08 Schlechtfall: Vertragsnummer in AKM-TR falsch: ABBM-TR mit MC 8143
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *      AKMTR   ->
     *              <-  ABBM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_08() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_08, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100208", "100216")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA).withBuilder(createDefaultRuemVa()));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.WVNR_ABG));
    }

    /**
     * 10.2.09 Schlechtfall: LineID in AKM-TR falsch: ABBM-TR mit MC 8144
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *      AKMTR   ->
     *              <-  ABBM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_09() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_09, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100209", "100217")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA).withBuilder(createDefaultRuemVa()));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.LID_ABG));
    }

    /**
     * 10.2.10 Schlechtfall: Übernahme Ressource nicht möglich: ABBM-TR mit MC 8147
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *      AKMTR   ->
     *              <-  ABBM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_10() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_10, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100210", "100220")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA).withBuilder(createDefaultRuemVa()));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.UETN_NM));
    }

    /**
     * 10.2.11 Schlechtfall: AKM-TR doppelt versenden: ABBM-TR mit MC 8148
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *      VA      ->
     *              <-  RUEM-VA
     *      AKMTR   ->
     *      AKMTR   ->
     *              <-  ABBM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_10_2_11() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_10_2_11, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "100211", "100221")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA).withBuilder(createDefaultRuemVa()));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        // now resend the AKM-TR and ensure that an ABBM-TR with the MeldungsCode.UETN_BB is returned
        atlas().sendCarrierChangeUpdate("AKMTR");
        atlas().receiveCarrierChangeUpdate("ABBMTR");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.AKM_TR, 2, true);
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM_TR);
        hurrican().assertVaRequestStatus(WbciRequestStatus.AKM_TR_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    private RueckmeldungVorabstimmungKftBuilder createDefaultRuemVa() {
        return new RueckmeldungVorabstimmungKftBuilder(
                getWbciCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT);
    }

}
