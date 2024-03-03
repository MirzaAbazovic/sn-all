/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.acceptance.receiving;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueOrnKftBuilder;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveTV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveERLM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendTV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueOrnAuf_Terminverschiebung_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_ORN;

    /**
     * Tests a TV (answered with an ERLM) sent by the Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  TV
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_Terminverschiebung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_Terminverschiebung_01, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDateTime kundenwunschtermin =
                DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14).atStartOfDay();
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WbciCdmVersion.V1).withKundenwunschtermin(kundenwunschtermin != null ? kundenwunschtermin.toLocalDate() : null)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7).toLocalDate())));

        applyBehavior(new ReceiveERLM_TV_TestBehavior());
    }

    /**
     * Tests a TV (answered with an ABBM) sent by the Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  TV
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_Terminverschiebung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_Terminverschiebung_02, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WbciCdmVersion.V1).withKundenwunschtermin(kundenwunschtermin != null ? kundenwunschtermin : null)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));
        applyBehavior(new ReceiveABBM_TV_TestBehavior(MeldungsCode.TV_ABG));
    }

    /**
     * Tests a TV (answered with an ABBM) sent by the Donating Carrier (AtlasESB):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *     TV       ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_Terminverschiebung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_Terminverschiebung_03, wbciCdmVersion, WbciVersion.V2, geschaeftsfallTyp);

        final LocalDateTime kundenwunschtermin =
                LocalDateTime.of(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14), LocalTime.now());
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin != null ? kundenwunschtermin.toLocalDate() : null)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new ReceiveTV_TestBehavior()
                .withSkipTvRequestChecks()
                .withSkipGeschaeftsfallChecks());

        // ABBM is automatically sent by hurrican when the TV is processed
        atlas().receiveCarrierChangeUpdate("ABBM");

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertNumberOfRequests(TerminverschiebungsAnfrage.class, 1);
        hurrican().assertKundenwunschtermin(VariableNames.REQUESTED_CUSTOMER_DATE, RequestTyp.VA);
        hurrican().assertWechseltermin(VariableNames.REQUESTED_CUSTOMER_DATE);
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_EMPFANGEN);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests a TV sent by the Receiving Carrier (Hurrican) with ERLM that misses correct AenderungsIdRef element. Result
     * is a error service notification to Atlas and message is neither accepted nor stored.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  TV
     *     ERLM     ->
     *              <-  ATLAS_ERROR
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_Terminverschiebung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_Terminverschiebung_04, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WbciCdmVersion.V1).withKundenwunschtermin(kundenwunschtermin != null ? kundenwunschtermin : null)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));

        atlas().sendCarrierChangeUpdate("ERLM");

        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_VERSENDET);

        atlas().receiveErrorHandlingServiceMessage("ATLAS_ERROR");
    }

    /**
     * Tests a TV sent by the Receiving Carrier (Hurrican) with ABBM that misses correct AenderungsIdRef element. Result
     * is a error service notification to Atlas and message is neither accepted nor stored.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  TV
     *     ABBM     ->
     *              <-  ATLAS_ERROR
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_Terminverschiebung_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_Terminverschiebung_05, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueOrnKftBuilder(WbciCdmVersion.V1).withKundenwunschtermin(kundenwunschtermin != null ? kundenwunschtermin : null)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));

        atlas().sendCarrierChangeUpdate("ABBM");

        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_VERSENDET);

        atlas().receiveErrorHandlingServiceMessage("ATLAS_ERROR");
    }

}
