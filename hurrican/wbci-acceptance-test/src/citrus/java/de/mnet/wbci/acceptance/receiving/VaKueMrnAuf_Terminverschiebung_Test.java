/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
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
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.common.model.Carrier;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveTV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveERLM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendTV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.CarrierCode;
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

@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAuf_Terminverschiebung_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;

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
    public void VaKueMrnAuf_Terminverschiebung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_Terminverschiebung_01, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));

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
    public void VaKueMrnAuf_Terminverschiebung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_Terminverschiebung_02, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin)));

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
     *  RUEMVA   ->
     *     TV       ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_Terminverschiebung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_Terminverschiebung_03, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new ReceiveTV_TestBehavior()
                .withSkipTvRequestChecks()
                .withSkipGeschaeftsfallChecks());

        atlas().receiveCarrierChangeUpdate("ABBM");

        // ABBM is automatically sent by hurrican when the TV is processed
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertNumberOfRequests(TerminverschiebungsAnfrage.class, 1);
        hurrican().assertWechseltermin(VariableNames.REQUESTED_CUSTOMER_DATE);
        hurrican().assertKundenwunschtermin(VariableNames.REQUESTED_CUSTOMER_DATE, RequestTyp.VA);
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_EMPFANGEN);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests a TV (answered with an ERLM) sent by the Receiving Carrier (Hurrican). The ERLM doesn't contain a
     * wechseltermin, therefore the Geschaeftsfall should be marked as Klaerfall:
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
    public void VaKueMrnAuf_Terminverschiebung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_Terminverschiebung_04, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));

        applyBehavior(new ReceiveERLM_TV_TestBehavior().withExpectedKlaerfall(true));
    }

    /**
     * Tests a TV (answered with an ERLM) sent by the Receiving Carrier (Hurrican). The ERLM contains another
     * wechseltermin as the one within the TV, therefore the Geschaeftsfall should be marked as Klaerfall:
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
    public void VaKueMrnAuf_Terminverschiebung_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_Terminverschiebung_05, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));

        applyBehavior(new ReceiveERLM_TV_TestBehavior().withExpectedKlaerfall(true));
    }

    /**
     * Tests a TV (answered with an ABBM) sent by the Receiving Carrier (Hurrican). The ABBM doesn't contain a
     * wechseltermin, therefore the Geschaeftsfall should be marked as Klaerfall:
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
    public void VaKueMrnAuf_Terminverschiebung_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_Terminverschiebung_06, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));
        applyBehavior(new ReceiveABBM_TV_TestBehavior(MeldungsCode.TV_ABG).withExpectedKlaerfall(true));
    }

    /**
     * Tests a TV (answered with an ABBM) sent by the Receiving Carrier (Hurrican). The ABBM contains another
     * wechseltermin as the one within the VA, therefore the Geschaeftsfall should be marked as Klaerfall:
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
    public void VaKueMrnAuf_Terminverschiebung_07_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_Terminverschiebung_07, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));
        applyBehavior(new ReceiveABBM_TV_TestBehavior(MeldungsCode.TV_ABG).withExpectedKlaerfall(true));
    }

    /**
     * Tests the TV sent from Donating Carrier (Atlas) use case with an unknown VaRefId:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *     TV   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_Terminverschiebung_08_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_Terminverschiebung_08, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        final String vaRefId = hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG);
        final String changeId = hurrican().createPreAgreementId(RequestTyp.TV, Carrier.DTAG);

        variables().add(VariableNames.PRE_AGREEMENT_ID, vaRefId);
        variables().add(VariableNames.CHANGE_ID, changeId);
        variables().add(VariableNames.CARRIER_CODE_ABGEBEND, CarrierCode.DTAG.getITUCarrierCode());
        variables().add(VariableNames.CARRIER_CODE_AUFNEHMEND, CarrierCode.MNET.getITUCarrierCode());

        applyBehavior(new ReceiveTV_TestBehavior()
                .withChangeId(changeId)
                .withSkipGeschaeftsfallChecks()
                .withSkipTvRequestChecks()
                .withPreAgreementId(vaRefId)
        );

        // ABBM is automatically sent by hurrican when the TV is processed
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertNumberOfRequests(TerminverschiebungsAnfrage.class, 0);

        atlas().receiveCarrierChangeUpdate("ABBM");
    }

}
