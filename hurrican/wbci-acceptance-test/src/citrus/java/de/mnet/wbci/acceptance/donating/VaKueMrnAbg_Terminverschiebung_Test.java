/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungKftBuilder;
import de.mnet.wbci.acceptance.common.model.Carrier;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveTV_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendERLM_TV_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
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
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAbg_Terminverschiebung_Test extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests the TV use-case, when the TV is sent between the RUEM-VA and AKM-TR:
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     TV   ->
     *              <-  ERLM
     *     AKMTR   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_01, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        hurrican().closeWbciGeschaeftsfallProcessingAction();
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.PASSIVE);

        applyBehavior(new ReceiveTV_TestBehavior());

        applyBehavior(new SendERLM_TV_TestBehavior());

        applyBehavior(new ReceiveAKMTR_TestBehavior());
    }

    /**
     * Tests the TV use-case with ABBM:
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     TV   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_02, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveTV_TestBehavior());

        hurrican().createWbciMeldung(
                new AbbruchmeldungKftBuilder(WbciCdmVersion.V1, geschaeftsfallTyp, IOType.OUT)
                        .withMeldungsCodes(MeldungsCode.TV_ABG)
                        .buildForTv(), RequestTyp.TV
        );
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertTvMeldungsCodes(MeldungsCode.TV_ABG);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        atlas().receiveCarrierChangeUpdate("ABBM");
    }

    /**
     * Tests the TV use-case, when TV is sent before the RUEM-VA is received. An ABBM Meldung should automatically be
     * sent back by Hurrican:
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *     TV       ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_03, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new ReceiveTV_TestBehavior()
                .withSkipTvRequestChecks()
                .withSkipGeschaeftsfallChecks());

        // ABBM is automatically sent by hurrican when the TV is processed
        atlas().receiveCarrierChangeUpdate("ABBM");

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertNumberOfRequests(TerminverschiebungsAnfrage.class, 1);
        hurrican().assertKundenwunschtermin(VariableNames.REQUESTED_CUSTOMER_DATE, RequestTyp.VA);
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the TV use-case, when 2nd TV with <b>same</b> changeId is sent before the ERLM/ABBM is received:
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     TV       ->
     *     TV       ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_04, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveTV_TestBehavior()
                .withChangeId(hurrican().createPreAgreementId(RequestTyp.TV, Carrier.DTAG)));

        /** Send 2nd TV with same changeId */
        atlas().sendCarrierChangeReschedule("TV");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), RequestTyp.TV, 2, false);
        hurrican().assertNumberOfRequests(TerminverschiebungsAnfrage.class, 1);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertKundenwunschtermin(VariableNames.RESCHEDULED_CUSTOMER_DATE, RequestTyp.TV);

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the TV use-case, when 2nd TV with <b>different</b> changeId is sent before the ERLM/ABBM is received:
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *     TV       ->
     *     TV       ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_05, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        String changeId = hurrican().createPreAgreementId(RequestTyp.TV, Carrier.DTAG);
        applyBehavior(new ReceiveTV_TestBehavior()
                .withChangeId(changeId));

        /** Send 2nd TV with different changeId */
        String parallelChangeId = hurrican().createPreAgreementId(RequestTyp.TV, Carrier.DTAG);
        variables().add(VariableNames.CHANGE_ID, parallelChangeId);
        atlas().sendCarrierChangeReschedule("TV");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), RequestTyp.TV, 2, false);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertWechseltermin(VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the TV use-case, when a TV is sent and there is already at least one WitaCbVorgang for this
     * VorabstimmungsId:
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     TV   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_06, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        hurrican().storeWitaCBVorgang(
                new WitaCBVorgangBuilder()
                        .withCarrierRefNr("987")
                        .withAuftragId(1L)
                        .withTyp(WitaCBVorgang.TYP_REX_MK)
                        .build(),
                true
        );

        hurrican().storeWitaCBVorgang(
                new WitaCBVorgangBuilder()
                        .withCarrierRefNr("123")
                        .withAuftragId(2L)
                        .withTyp(WitaCBVorgang.TYP_REX_MK)
                        .build(),
                true
        );

        applyBehavior(new ReceiveTV_TestBehavior().withExpectedKlaerfall(true));

        hurrican().assertBemerkung("(?s).*Zu der Vorabstimmung .* existieren bereits ein oder mehrere WITA-Vorgänge " +
                "mit folgenden Nummern: \\d+, \\d+.*");
    }

    /**
     * Tests the TV use-case, when the TV is sent between the RUEM-VA and AKM-TR and secures that the {@link
     * SendERLM_TV_TestBehavior}will check that a WITA {@link de.mnet.wita.model.VorabstimmungAbgebend} is created.
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     TV   ->
     *              <-  ERLM
     *                   |- create VorabstimmungAbgebend
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_07_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_07, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        GeneratedTaifunData generatedData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        Rufnummer rufnummer = generatedData.getDialNumbers().get(0);
        applyBehavior(new ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior(generatedData));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .addEinzelrufnummer(rufnummer.getOnKz(), rufnummer.getDnBase(), "D123"));

        hurrican().closeWbciGeschaeftsfallProcessingAction();
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.PASSIVE);

        applyBehavior(new ReceiveTV_TestBehavior());

        applyBehavior(new SendERLM_TV_TestBehavior());
    }

    /**
     * Tests the TV use-case, when the TV is sent just two days before the change date.
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     TV   ->
     *              <-  ABBM (Frist unterschritten)
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_08_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_08, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        // Datum von RUEM-VA bzw. GF vorziehen, damit TV als zu kurzfristig erkannt wird
        hurrican().changeWechseltermin(
                DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(1).atStartOfDay());
        variables().add(VariableNames.REQUESTED_CUSTOMER_DATE,
                String.format("wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', %s)", 1));

        applyBehavior(new ReceiveTV_TestBehavior()
                .withSkipTvRequestChecks()
                .withSkipGeschaeftsfallChecks());

        atlas().receiveCarrierChangeUpdate("ABBM");

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertNumberOfRequests(TerminverschiebungsAnfrage.class, 1);
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_VERSENDET);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the TV use-case, with TV date before(!) the date from RUEM-VA
     * <p>
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     TV   ->
     *              <-  ABBM (Terminvorziehung)
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_Terminverschiebung_09_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_Terminverschiebung_09, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveTV_TestBehavior()
                .withSkipTvRequestChecks()
                .withSkipGeschaeftsfallChecks());

        atlas().receiveCarrierChangeUpdate("ABBM");

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertNumberOfRequests(TerminverschiebungsAnfrage.class, 1);
        hurrican().assertKundenwunschtermin(VariableNames.REQUESTED_CUSTOMER_DATE, RequestTyp.VA);
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_VERSENDET);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

}
