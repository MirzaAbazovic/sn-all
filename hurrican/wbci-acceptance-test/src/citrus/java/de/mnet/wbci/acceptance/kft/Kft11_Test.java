/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.10.13
 */
package de.mnet.wbci.acceptance.kft;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.RueckmeldungVorabstimmungKftBuilder;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendABBM_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.StandortBuilder;

/**
 * Testfaelle TF 11.x : EKPabg und VA-RRNP
 *
 *
 */
@Test(groups = BaseTest.KFT)
public class Kft11_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_RRNP;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 11.1.01 Gutfall: RUEM-VA mit Auftragsbestätigung (MC 8001 oder 8002)
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  RUEMVA
     * </pre>
     */
    @CitrusTest
    public void Test_11_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        final String useCaseNumber = "110101";
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, useCaseNumber, useCaseNumber)));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));
    }

    /**
     * 11.1.02 Gutfall: RUEM-VA mit abweichender Adresse (MC 8105)
     * <p/>
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  RUEMVA
     * </pre>
     */
    @CitrusTest
    public void Test_11_1_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_1_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "110102", "110104")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ADAORT, MeldungsCode.ZWA)
                .withBuilder(new RueckmeldungVorabstimmungKftBuilder(getWbciCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT)
                        .withExplicitMeldungsCode(
                                new StandortBuilder()
                                        .withOrt("Starnberg")
                                        .build(),
                                MeldungsCode.ADAORT
                        )));
    }

    /**
     * 11.2.01 Schlechtfall: falsche RN: ABBM mit MC 8103
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_11_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "110201", "110201")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.RNG));
    }

    /**
     * 11.2.02 Schlechtfall: falsche Adresse: ABBM mit MC 8108
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
    public void Test_11_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "110202", "110202")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.ADFPLZ));
    }

    /**
     * 11.2.03 Schlechtfall: falsche Adresse: PLZ und Ort, ABBM mit MC 8108, 8109
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
    public void Test_11_2_03() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_2_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "110203", "110206")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.ADFPLZ, MeldungsCode.ADFORT));
    }

    /**
     * 11.2.04 Schlechtfall: falscher Anschlussinhaber: ABBM mit MC 8112
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_11_2_04() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_2_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "110204", "110207")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.AIF));
    }

    /**
     * 11.2.05 Schlechtfall: falsche ID: ABBM mit MC 8115
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_11_2_05() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_2_05, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "110205", "110210")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 11.2.06 Schlechtfall: Kunde unbekannt: ABBM mit MC 8117
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_11_2_06() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_2_06, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "110206", "110212")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.KNI));
    }

    /**
     * 11.2.07 Schlechtfall: falscher Projektkenner: ABBM mit MC 8149
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_11_2_07() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_11_2_07, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "110207", "110214")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.UPK));
    }
}
