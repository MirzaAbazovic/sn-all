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
import de.mnet.wbci.acceptance.common.behavior.ReceiveSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendABBM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendERLM_STR_TestBehavior;
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
 * Testfaelle TF 8.x : TVS (EKPauf mit STR_AEN durch EKBab)
 */
@Test(groups = BaseTest.KFT)
public class Kft08_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final RequestTyp stornoTyp = RequestTyp.STR_AEN_ABG;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 8.1.01 Gutfall: Auftragsbestätigung: ERLM mit MC 8004:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *     STR-AEN  ->
     *              <-  ERLM
     * </pre>
     */
    @CitrusTest
    public void Test_8_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_8_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        final String useCaseNumber = "080101";
        final String preAgreementId = createKftDefaultPreAgreementIdRef();

        createAndStoreGfVaAndRuemVa(preAgreementId);

        applyBehavior(new ReceiveSTR_TestBehavior(stornoTyp)
                .withStornoId(createPreAgreementId(stornoTyp, CarrierCode.DTAG, useCaseNumber, useCaseNumber)));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));
    }

    /**
     * 8.2.01 Schlechtfall: ID in Auftrag falsch: ABBM mit MC 8115:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *     STR-AEN  ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_8_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_8_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        final String useCaseNumber = "080201";
        final String preAgreementId = createKftDefaultPreAgreementIdRef();

        createAndStoreGfVaAndRuemVa(preAgreementId);

        applyBehavior(new ReceiveSTR_TestBehavior(stornoTyp)
                .withStornoId(createPreAgreementId(stornoTyp, CarrierCode.DTAG, useCaseNumber, useCaseNumber)));

        applyBehavior(new SendABBM_STR_TestBehavior(stornoTyp, MeldungsCode.BVID));

    }

    /**
     * 8.2.02 Schlechtfall: Storno abgelehnt: ABBM mit MC 8142:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *     STR-AEN  ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_8_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_8_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        final String useCaseNumberKft = "080202";
        final String useCaseNumberSim = "080203";
        final String preAgreementId = createKftDefaultPreAgreementIdRef();

        createAndStoreGfVaAndRuemVa(preAgreementId);

        applyBehavior(new ReceiveSTR_TestBehavior(stornoTyp)
                .withStornoId(createPreAgreementId(stornoTyp, CarrierCode.DTAG, useCaseNumberKft, useCaseNumberSim)));

        applyBehavior(new SendABBM_STR_TestBehavior(stornoTyp, MeldungsCode.STORNO_ABG));
    }

    /**
     * Simulates the following scenario in the WBCI database, <b>without</b> actually sending or receiving any messages:
     * <ol> <li>the Receipt of a VA</li> <li>the sending a RUEM-VA</li> </ol> Proper test variables are also created so
     * that future test actions can refer to the geschaeftsfall, va and ruem-va properties.
     *
     * @param vorabstimmungsId
     * @return
     */
    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> createAndStoreGfVaAndRuemVa(String vorabstimmungsId) {
        final WbciGeschaeftsfallKueMrn gf = kft().createAndStoreGeschaeftsfallKueMrn(vorabstimmungsId,
                CarrierCode.DTAG, CarrierCode.MNET);
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = kft().createAndStoreVa(
                WbciRequestStatus.RUEM_VA_VERSENDET,
                gf,
                MeldungsCode.ZWA,
                MeldungTyp.RUEM_VA,
                IOType.IN);
        kft().createAndStoreRuemVa(gf, IOType.OUT);
        return va;
    }

}
