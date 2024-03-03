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
import de.mnet.wbci.acceptance.common.behavior.ReceiveABBM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.ReceiveERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendSTR_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 * Testfaelle TF 5.x : EKPauf und STR-AEN
 */
@Test(groups = BaseTest.KFT)
public class Kft05_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;
    private static final RequestTyp stornoType = RequestTyp.STR_AEN_AUF;

    /**
     * 5.1.01 Gutfall: Auftragsbestätigung: ERLM mit MC 8004
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  STORNO
     *     ERLM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_5_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_5_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        sendStorno("050101", "050101");

        applyBehavior(new ReceiveERLM_STR_TestBehavior(stornoType));
    }

    /**
     * 5.2.01 Schlechtfall: ID in Auftrag falsch: ABBM mit MC 8115
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  STORNO
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_5_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_5_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        sendStorno("050201", "050201");

        applyBehavior(new ReceiveABBM_STR_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 5.2.02 Schlechtfall: Storno abgelehnt: ABBM mit MC 8142
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  STORNO
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_5_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_5_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        sendStorno("050202", "050203");

        applyBehavior(new ReceiveABBM_STR_TestBehavior(MeldungsCode.STORNO_ABG));
    }

    private void sendStorno(String useCaseNumberKft, String useCaseNumberSim) {
        final String vorabstimmungsId = createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, useCaseNumberKft, useCaseNumberSim);
        final String stornoId = createPreAgreementId(stornoType, CarrierCode.MNET, useCaseNumberKft, useCaseNumberSim);

        createAndStoreVa(vorabstimmungsId);

        applyBehavior(new SendSTR_TestBehavior(stornoType)
                .withStornoId(stornoId)
                .withPreAgreementId(vorabstimmungsId));
    }

    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> createAndStoreVa(String vorabstimmungsId) {
        WbciGeschaeftsfallKueMrn gf = kft().createAndStoreGeschaeftsfallKueMrn(vorabstimmungsId, CarrierCode.DTAG,
                CarrierCode.MNET);
        return kft().createAndStoreVa(
                WbciRequestStatus.RUEM_VA_VERSENDET,
                gf,
                MeldungsCode.ZWA,
                MeldungTyp.RUEM_VA,
                IOType.IN);
    }

}
