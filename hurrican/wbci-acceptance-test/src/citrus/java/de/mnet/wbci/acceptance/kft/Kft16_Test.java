/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.13
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
 * Testfaelle TF 16.x : EKPabg und STR-AEN (M-net versendet)
 *
 *
 */
@Test(groups = BaseTest.KFT)
public class Kft16_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final RequestTyp stornoTyp = RequestTyp.STR_AEN_ABG;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 16.1.01 Gutfall: Auftragsbestätigung: ERLM mit MC 8004:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *              <-  STR-AEN
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void Test_16_1_01() {
        final String useCaseNumber = "160101";
        simulatorUseCase(WbciSimulatorUseCase.Kft_16_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        final String preAgreementId = createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, useCaseNumber, useCaseNumber);
        createAndStoreVa(preAgreementId);

        applyBehavior(new SendSTR_TestBehavior(stornoTyp)
                .withStornoId(createPreAgreementId(stornoTyp, CarrierCode.DTAG, useCaseNumber, useCaseNumber)));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(stornoTyp));
    }

    /**
     * 16.2.01 Schlechtfall: ID in Auftrag falsch: ABBM mit MC 8115:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *              <-  STR-AEN
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void Test_16_2_01() {
        final String useCaseNumber = "160201";
        simulatorUseCase(WbciSimulatorUseCase.Kft_16_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        final String preAgreementId = createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, useCaseNumber, useCaseNumber);
        createAndStoreVa(preAgreementId);

        applyBehavior(new SendSTR_TestBehavior(stornoTyp)
                .withStornoId(createPreAgreementId(stornoTyp, CarrierCode.DTAG, useCaseNumber, useCaseNumber)));

        applyBehavior(new ReceiveABBM_STR_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 16.2.02 Schlechtfall: TV abgelehnt: ABBM mit MC 8141:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *              <-  STR-AEN
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void Test_16_2_02() {
        final String useCaseNumberKft = "160202";
        final String useCaseNumberSim = "160203";
        simulatorUseCase(WbciSimulatorUseCase.Kft_16_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        final String preAgreementId = createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, useCaseNumberKft, useCaseNumberSim);
        createAndStoreVa(preAgreementId);

        applyBehavior(new SendSTR_TestBehavior(stornoTyp)
                .withStornoId(createPreAgreementId(stornoTyp, CarrierCode.DTAG, useCaseNumberKft, useCaseNumberSim)));

        applyBehavior(new ReceiveABBM_STR_TestBehavior(MeldungsCode.STORNO_ABG));
    }

    /**
     * Creates new VorabstimmungsAnfrage instance and stores it to the database directly. Also creates proper test
     * variables so future test actions can refer to basic VorabstimmungsAnfrage properties.
     *
     * @param vorabstimmungsId
     * @return
     */
    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> createAndStoreVa(String vorabstimmungsId) {
        final WbciGeschaeftsfallKueMrn geschaeftsfall = kft().createAndStoreGeschaeftsfallKueMrn(vorabstimmungsId,
                CarrierCode.MNET, CarrierCode.DTAG);
        return kft()
                .createAndStoreVa(WbciRequestStatus.RUEM_VA_VERSENDET, geschaeftsfall, MeldungsCode.ZWA,
                        MeldungTyp.RUEM_VA, IOType.IN);
    }

}
