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
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallRrnpKftBuilder;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_VA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

/**
 * Testfaelle TF 3.x : EKPauf und VA-RRNP
 */
@Test(groups = BaseTest.KFT)
public class Kft03_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_RRNP;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 3.1.01 Gutfall: RUEM-VA mit Auftragsbestätigung (MC 8001)
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030101", "030101")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
    }

    /**
     * 3.1.02 Gutfall: RUEM-VA mit abweichender Adresse (MC 8105)
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_1_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_1_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(WBCI_CDM_VERSION))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030102", "030104")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));

    }

    /**
     * 3.2.01 Schlechtfall: falsche RN: ABBM mit MC 8103
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(WBCI_CDM_VERSION))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030201", "030201")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.RNG));
    }

    /**
     * 3.2.02 Schlechtfall: falsche Adresse: ABBM mit MC 8108
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(WBCI_CDM_VERSION))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030202", "030202")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.ADFPLZ));
    }

    /**
     * 3.2.03 Schlechtfall: falsche Adresse: PLZ und Ort, ABBM mit MC 8108, 8109
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_2_03() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_2_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(WBCI_CDM_VERSION))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030203", "030206")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.ADFPLZ, MeldungsCode.ADFORT));
    }

    /**
     * 3.2.04 Schlechtfall: falscher Anschlussinhaber: ABBM mit MC 8112
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_2_04() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_2_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(WBCI_CDM_VERSION))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030204", "030207")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.AIF));
    }

    /**
     * 3.2.05 Schlechtfall: falsche ID: ABBM mit MC 8115
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_2_05() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_2_05, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(WBCI_CDM_VERSION))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030205", "030210")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 3.2.06 Schlechtfall: Kunde unbekannt: ABBM mit MC 8117
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_2_06() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_2_06, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(WBCI_CDM_VERSION))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030206", "030212")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.KNI));
    }

    /**
     * 3.2.07 Schlechtfall: falscher Projektkenner: ABBM mit MC 8149
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_3_2_07() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_3_2_07, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(WBCI_CDM_VERSION))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "030207", "030214")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.UPK));
    }

}
