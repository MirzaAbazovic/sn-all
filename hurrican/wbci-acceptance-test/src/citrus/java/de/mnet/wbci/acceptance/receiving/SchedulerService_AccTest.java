/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.wbci.acceptance.receiving;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.ReceiveSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciStornoAenderungAbgAnfrageKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciStornoAufhebungAufAnfrageKftBuilder;
import de.mnet.wbci.acceptance.common.model.Carrier;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveTV_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class SchedulerService_AccTest extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private final int minutesMessageOnHold = 2;

    @Override
    protected int getWbciMinutesWhileMessageOnHold() {
        return minutesMessageOnHold;
    }

    /**
     * Tests whether a new VA request will be scheduled and not sent immediately:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     * </pre>
     */
    @CitrusTest
    public void SchedulerService_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.SchedulerService_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        hurrican().clearScheduledRequests();

        WbciGeschaeftsfall geschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnKftBuilder(getWbciCdmVersion()).build();

        hurrican().createWbciVorgang(geschaeftsfallKueMrn);
        hurrican().assertWbciOutgoingRequestCreated();
        hurrican().assertVaRequestScheduled(true);
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_VORGEHALTEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().triggerScheduled();
        atlas().receiveCarrierChangeRequest("VA_KUEMRN");

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp());
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests whether a new TV request will be scheduled and not sent immediately:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  TV
     * </pre>
     */
    @CitrusTest
    public void SchedulerService_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.SchedulerService_02, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        hurrican().clearScheduledRequests();

        createAndStoreVa(hurrican().createPreAgreementId(RequestTyp.VA));
        final LocalDate kundenwunschtermin = getDateInWorkingDaysFromNow(14).toLocalDate();

        hurrican().createWbciTv(asWorkingDayAndNextDayNotHoliday(kundenwunschtermin.plusDays(7)), null);
        hurrican().assertTvRequestScheduled(true);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_VORGEHALTEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().triggerScheduled();
        atlas().receiveCarrierChangeReschedule("TV");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), RequestTyp.TV);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests whether a new Storno request will be scheduled and not sent immediately:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  STRAUF
     * </pre>
     */
    @CitrusTest
    public void SchedulerService_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.SchedulerService_03, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        hurrican().clearScheduledRequests();

        createAndStoreVa(hurrican().createPreAgreementId(RequestTyp.VA));

        hurrican().createWbciStorno(new WbciStornoAufhebungAufAnfrageKftBuilder<>(getWbciCdmVersion()).build());
        hurrican().assertStornoRequestScheduled(true);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VORGEHALTEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().triggerScheduled();

        atlas().receiveCarrierChangeCancel("STR");

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), RequestTyp.STR_AUFH_AUF);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

    }

    /**
     * Tests the race condition, where an M-Net TV request is initially held (before being sent out by the
     * scheduler service). However in the mean time the partner carrier sends a Storno request. In this case the
     * partner EKPs storno request is answered with an ABBM response notification, indicating that a TV Request
     * already exists which they will shortly receive.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN    ->
     *               <-  RUEMVA
     *                   TV (VORGEHALTEN)
     *     STRAEN ->
     *               <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void SchedulerService_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.SchedulerService_04, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        hurrican().clearScheduledRequests();

        String dtagStornoId = hurrican().createPreAgreementId(RequestTyp.STR_AEN_AUF, Carrier.DTAG);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        // create M-Net TV, but do not send
        final LocalDate kundenwunschtermin = getDateInWorkingDaysFromNow(14).toLocalDate();
        hurrican().createWbciTv(asWorkingDayAndNextDayNotHoliday(kundenwunschtermin.plusDays(7)), null);
        hurrican().assertTvRequestScheduled(true);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_VORGEHALTEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        // simulate DTAG Storno and check ABBM returned
        variables().add(VariableNames.STORNO_ID, dtagStornoId);

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_AUF)
                        .withSkipStornoRequestChecks()
                        .withSkipGeschaeftsfallChecks()
        );

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the race condition, where an M-Net Storno request is initially held (before being sent out by the
     * scheduler service). However in the mean time the partner carrier also sends a Storno request. In this case the
     * partner EKPs storno request is answered with an ABBM response notification, indicating that a Storno Request
     * already exists which they will shortly receive.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN    ->
     *               <-  RUEMVA
     *                   STRAEN (VORGEHALTEN)
     *     STRAEN ->
     *               <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void SchedulerService_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.SchedulerService_05, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        hurrican().clearScheduledRequests();

        String dtagStornoId = hurrican().createPreAgreementId(RequestTyp.STR_AEN_AUF, Carrier.DTAG);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        // create M-Net Storno, but do not send
        hurrican().createWbciStorno(new WbciStornoAenderungAbgAnfrageKftBuilder<>(getWbciCdmVersion()).build());
        hurrican().assertStornoRequestScheduled(true);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VORGEHALTEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        // simulate DTAG Storno and check ABBM returned
        variables().add(VariableNames.STORNO_ID, dtagStornoId);

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_AUF)
                        .withSkipStornoRequestChecks()
                        .withSkipGeschaeftsfallChecks()
        );

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the race condition, where an M-Net Storno request is initially held (before being sent out by the
     * scheduler service). However in the mean time the partner carrier sends a TV request. In this case the
     * partner EKPs TV request is answered with an ABBM response notification, indicating that a Storno Request
     * already exists which they will shortly receive.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN    ->
     *               <-  RUEMVA
     *                   STRAEN (VORGEHALTEN)
     *     TV        ->
     *               <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void SchedulerService_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.SchedulerService_06, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        hurrican().clearScheduledRequests();

        String dtagChangeId = hurrican().createPreAgreementId(RequestTyp.TV, Carrier.DTAG);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        // create M-Net Storno, but do not send
        hurrican().createWbciStorno(new WbciStornoAenderungAbgAnfrageKftBuilder<>(getWbciCdmVersion()).build());
        hurrican().assertStornoRequestScheduled(true);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VORGEHALTEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        // simulate DTAG TV and check ABBM returned
        variables().add(VariableNames.CHANGE_ID, dtagChangeId);

        applyBehavior(new ReceiveTV_TestBehavior()
                        .withSkipTvRequestChecks()
                        .withSkipGeschaeftsfallChecks()
        );

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
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
