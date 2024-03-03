/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.13
 */
package de.mnet.wbci.acceptance.kft;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.SendABBM_TV_TestBehavior;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveTV_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendERLM_TV_TestBehavior;
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
 * Testfaelle TF 14.x : EKPabg und TVS-VA (M-net empfängt)
 *
 *
 */
@Test(groups = BaseTest.KFT)
public class Kft14_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 14.1.01 Gutfall: Auftragsbestätigung: ERLM mit MC 8003:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *      TV-VA   ->
     *              <-  ERLM
     * </pre>
     */
    @CitrusTest
    public void Test_14_1_01() {
        final String useCaseNumber = "140101";
        simulatorUseCase(WbciSimulatorUseCase.Kft_14_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        final String vorabstimmungsId = createKftDefaultPreAgreementIdRef();
        WbciGeschaeftsfallKueMrnKftBuilder kueMrnKftBuilder = new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION)
                .withVorabstimmungsId(vorabstimmungsId)
                .withKundenwunschtermin(DateCalculationHelper.getDateInWorkingDaysFromNow(21).toLocalDate())
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG);
        final WbciGeschaeftsfallKueMrn geschaeftsfall = kft().createAndStoreGeschaeftsfall(kueMrnKftBuilder);
        kft().createAndStoreVa(WbciRequestStatus.RUEM_VA_VERSENDET, geschaeftsfall, MeldungsCode.ZWA,
                MeldungTyp.RUEM_VA, IOType.IN);
        kft().createAndStoreRuemVa(geschaeftsfall, IOType.OUT);

        applyBehavior(new ReceiveTV_TestBehavior()
                        .withChangeId(createPreAgreementId(RequestTyp.TV, CarrierCode.DTAG, useCaseNumber, useCaseNumber))
                        .withSkipUpdatedKundenwunschterminCheck()
                        .withSkipAnswerDeadlineCheck()
        );

        applyBehavior(new SendERLM_TV_TestBehavior());
    }

    /**
     * 14.2.01 Schlechtfall: ID in Auftrag falsch: ABBM mit MC 8115:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *      TV-VA   ->
     *              <-  ABBM (8115)
     * </pre>
     */
    @CitrusTest
    public void Test_14_2_01() {
        final String useCaseNumber = "140201";
        simulatorUseCase(WbciSimulatorUseCase.Kft_14_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = createAndStoreVa(createKftDefaultPreAgreementIdRef());
        kft().createAndStoreRuemVa(va.getWbciGeschaeftsfall(), IOType.OUT);

        applyBehavior(new ReceiveTV_TestBehavior()
                .withChangeId(createPreAgreementId(RequestTyp.TV, CarrierCode.DTAG, useCaseNumber, useCaseNumber))
                .withSkipUpdatedKundenwunschterminCheck());

        applyBehavior(new SendABBM_TV_TestBehavior()
                .withBuilder(new AbbruchmeldungKftBuilder(getWbciCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT)
                        .withMeldungsCodes(MeldungsCode.BVID)
                        .withWechseltermin(va.getWbciGeschaeftsfall().getWechseltermin()))
                .withExpectedMeldungsCodes(MeldungsCode.BVID));
    }

    /**
     * 14.2.02 Schlechtfall: TV abgelehnt: ABBM mit MC 8141:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *      TV-VA   ->
     *              <-  ABBM (8141)
     * </pre>
     */
    @CitrusTest
    public void Test_14_2_02() {
        final String useCaseNumber = "140202";
        simulatorUseCase(WbciSimulatorUseCase.Kft_14_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = createAndStoreVa(createKftDefaultPreAgreementIdRef());
        kft().createAndStoreRuemVa(va.getWbciGeschaeftsfall(), IOType.OUT);

        applyBehavior(new ReceiveTV_TestBehavior()
                .withChangeId(createPreAgreementId(RequestTyp.TV, CarrierCode.DTAG, useCaseNumber, useCaseNumber))
                .withSkipUpdatedKundenwunschterminCheck());

        applyBehavior(new SendABBM_TV_TestBehavior()
                .withBuilder(new AbbruchmeldungKftBuilder(getWbciCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT)
                        .withMeldungsCodes(MeldungsCode.TV_ABG)
                        .withWechseltermin(va.getWbciGeschaeftsfall().getWechseltermin()))
                .withExpectedMeldungsCodes(MeldungsCode.TV_ABG));
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
        return kft().createAndStoreVa(WbciRequestStatus.RUEM_VA_VERSENDET, geschaeftsfall, MeldungsCode.ZWA,
                MeldungTyp.RUEM_VA, IOType.IN);
    }

}
