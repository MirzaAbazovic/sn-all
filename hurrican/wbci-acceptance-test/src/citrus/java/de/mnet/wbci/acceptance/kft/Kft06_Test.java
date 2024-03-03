/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.13
 */
package de.mnet.wbci.acceptance.kft;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveERLM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendTV_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 * Testfaelle TF 6.x : TVS (EKPauf sendet Auftrag)
 *
 *
 */
@Test(groups = BaseTest.KFT)
public class Kft06_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 6.1.01 Gutfall: Terminverschiebung ok (MC 8003)
     * <p/>
     * <pre>
     *     AtlasESB       Hurrican (Receiving Carrier)
     *
     *                    store Dummy VA in DB
     *                <-  TV
     *     ERLM       ->
     * </pre>
     */
    @CitrusTest
    public void Test_6_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_6_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        final String vorabstimmungsId = createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "060101", "060101");
        final String aenderungsId = createPreAgreementId(RequestTyp.TV, CarrierCode.MNET, "060101", "060101");
        final LocalDate tvTermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);

        createAndStoreVa(vorabstimmungsId);

        applyBehavior(new SendTV_TestBehavior(tvTermin, aenderungsId)
                .withPreAgreementId(vorabstimmungsId));

        applyBehavior(new ReceiveERLM_TV_TestBehavior());
    }

    /**
     * 6.2.01 Schlechtfall: Vorabstimmungs-,Terminverschiebungs-,Storno-, ID wird bereits verwendet. ABBM 8115
     * <p/>
     * <pre>
     *     AtlasESB      Hurrican (Receiving Carrier)
     *
     *                   store Dummy VA in DB
     *                   <-  TV
     *     ABBM (8115)   ->
     * </pre>
     */
    @CitrusTest
    public void Test_6_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_6_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        final String vorabstimmungsId = createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "060201", "060201");
        final String aenderungsId = createPreAgreementId(RequestTyp.TV, CarrierCode.MNET, "060201", "060201");
        final LocalDate tvTermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);

        createAndStoreVa(vorabstimmungsId);

        applyBehavior(new SendTV_TestBehavior(tvTermin, aenderungsId)
                .withPreAgreementId(vorabstimmungsId));

        applyBehavior(new ReceiveABBM_TV_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 6.2.02 Schlechtfall: Terminverschiebung abgelehnt ABBM 8141
     * <p/>
     * <pre>
     *     AtlasESB      Hurrican (Receiving Carrier)
     *
     *                   store Dummy VA in DB
     *                   <-  TV
     *     ABBM (8141)   ->
     * </pre>
     */
    @CitrusTest
    public void Test_6_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_6_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        final String vorabstimmungsId = createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "060202", "060202");
        final String aenderungsId = createPreAgreementId(RequestTyp.TV, CarrierCode.MNET, "060202", "060202");
        final LocalDate tvTermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);

        createAndStoreVa(vorabstimmungsId);

        applyBehavior(new SendTV_TestBehavior(tvTermin, aenderungsId)
                .withPreAgreementId(vorabstimmungsId));

        applyBehavior(new ReceiveABBM_TV_TestBehavior(MeldungsCode.TV_ABG));
    }

    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> createAndStoreVa(String vorabstimmungsId) {
        WbciGeschaeftsfallKueMrn gf = kft().createAndStoreGeschaeftsfallKueMrn(
                vorabstimmungsId,
                CarrierCode.DTAG,
                CarrierCode.MNET);
        return kft().createAndStoreVa(
                WbciRequestStatus.RUEM_VA_EMPFANGEN,
                gf,
                IOType.IN);
    }

}
