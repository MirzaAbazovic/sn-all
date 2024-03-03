/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.ReceiveERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.ReceiveSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendABBM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.builder.RufnummernportierungAnlageKftBuilder;
import de.mnet.wbci.acceptance.common.model.Carrier;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAbg_StornoAufhebung_Test extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests the Storno Aufhebung sent from the Receiving Carrier use case with ERLM:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     STRAUF   ->
     *              <-  ERLM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_01, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AUFH_AUF));
    }

    /**
     * Tests the Storno Aufhebung sent from the Donating Carrier use case with ERLM:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *              <-  STRAUF
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_02, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_ABG));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AUFH_ABG));
    }

    /**
     * Tests the Storno Aufhebung sent from the Receiving Carrier use case with ABBM:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     STRAUF   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_03, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        applyBehavior(new SendABBM_STR_TestBehavior(RequestTyp.STR_AUFH_AUF,
                MeldungsCode.STORNO_ABG));
    }

    /**
     * Tests the Storno Aufhebung sent from the Donating Carrier use case with ABBM:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *              <-  STRAUF
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_04, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_ABG));

        atlas().sendCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, GeschaeftsfallTyp.VA_KUE_MRN, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the Storno Aufhebung sent from the Donating Carrier use case with ERLM that misses correct StornoIdRef
     * element. Result is a error service notification to Atlas and message is neither accepted nor stored.
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *              <-  STRAUF
     *     ERLM     ->
     *              <-  ATLAS_ERROR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_05, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_ABG));

        atlas().sendCarrierChangeUpdate("ERLM");

        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VERSENDET);

        atlas().receiveErrorHandlingServiceMessage("ATLAS_ERROR");
    }

    /**
     * Tests the Storno Aufhebung sent from the Donating Carrier use case with ABBM that misses correct StornoIdRef
     * element. Result is a error service notification to Atlas and message is neither accepted nor stored.
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *              <-  STRAUF
     *     ABBM     ->
     *              <-  ATLAS_ERROR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_06, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_ABG));

        atlas().sendCarrierChangeUpdate("ABBM");

        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VERSENDET);

        atlas().receiveErrorHandlingServiceMessage("ATLAS_ERROR");
    }


    /**
     * Tests the Storno use-case, when 2nd Storno with <b>same</b> changeId is sent before the ERLM/ABBM is received:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     STRAUF   ->
     *     STRAUF   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_07_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_07, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_AUF).withStornoId(hurrican().createPreAgreementId(RequestTyp.STR_AUFH_AUF, Carrier.DTAG)));

        /** Send 2nd Storno with same changeId */
        atlas().sendCarrierChangeCancel("STR");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), RequestTyp.STR_AUFH_AUF, 2, false);
        hurrican().assertNumberOfRequests(StornoAufhebungAufAnfrage.class, 1);
        hurrican().assertKlaerfallStatus(false, null);

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the Storno use-case, when 2nd Storno with <b>different</b> changeId is sent before the ERLM/ABBM is
     * received:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *     STRAUF   ->
     *     STRAUF   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_08_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_08, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_AUF).withStornoId(hurrican().createPreAgreementId(RequestTyp.STR_AUFH_AUF, Carrier.DTAG)));

        /** Send 2nd Storno with different changeId */
        String parallelStornoId = hurrican().createPreAgreementId(RequestTyp.STR_AUFH_AUF, Carrier.DTAG);
        variables().add(VariableNames.STORNO_ID, parallelStornoId);
        atlas().sendCarrierChangeCancel("STR");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), RequestTyp.STR_AUFH_AUF, 2, false);
        hurrican().assertNumberOfRequests(StornoAufhebungAufAnfrage.class, 1);
        hurrican().assertKlaerfallStatus(false, null);

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the Storno Aufhebung sent from the Receiving Carrier use case with ERLM:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     STRAUF   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_09_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_09, WBCI_CDM_VERSION, wbciVersion,
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

        // Wenn zu der VA ein WITA Vorgang existiert sollte bei Empfang von STR_AUFH kein Klaerfall-Flag gesetzt werden
        // Stattdessen soll ein Hinweis im zugehoerigen ERLM-Dialog angezeigt werden
        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_AUF).withExpectedKlaerfall(false));
    }


    /**
     * Tests the Storno Aufhebung sent from the Receiving Carrier use case with ERLM and secures that the {@link
     * de.mnet.wbci.acceptance.donating.behavior.SendERLM_TV_TestBehavior}will check that a WITA {@link
     * de.mnet.wita.model.VorabstimmungAbgebend} is created:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     STRAUF   ->
     *              <-  ERLM
     *                   |- create VorabstimmungAbgebend
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_10_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_10, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        receiveVaAndSendRuemVaFromGeneratedData();

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AUFH_AUF));
    }

    /**
     * Tests the Storno Aufhebung sent from the Donating Carrier use case with ERLM and secures that the {@link
     * de.mnet.wbci.acceptance.donating.behavior.SendERLM_TV_TestBehavior}will check that a WITA {@link
     * de.mnet.wita.model.VorabstimmungAbgebend} is created:
     * <p>
     * <pre>
     *     AtlasESB                             Hurrican (Donating Carrier)
     *     KUEMRN                           ->
     *                                      <-  RUEMVA
     *                                      <-  STRAUF
     *     ERLM                             ->
     *     |- create VorabstimmungAbgebend
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_11_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_11, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        receiveVaAndSendRuemVaFromGeneratedData();

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_ABG));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AUFH_ABG));
    }

    /**
     * STR-AUF mit unterschrittener Vorlauffrist fuehrt zu einer ABBM:
     * <p>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     STRAUF   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAufhebung_12_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAufhebung_12, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        // Datum von RUEM-VA bzw. GF vorziehen, damit Storno als zu kurzfristig erkannt wird
        hurrican().changeWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(1).atStartOfDay());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_AUF)
                .withSkipStornoRequestChecks()
                .withSkipGeschaeftsfallChecks());

        atlas().receiveCarrierChangeUpdate("ABBM");

        hurrican().assertNumberOfRequests(StornoAufhebungAufAnfrage.class, 1);
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_VERSENDET);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }


    public void receiveVaAndSendRuemVaFromGeneratedData() {
        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().premiumCallWithBlockDn(50L).persist();
        applyBehavior(new ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior(generatedTaifunData));

        Rufnummer rufnummer = generatedTaifunData.getDialNumbers().get(0);
        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withRufnummernportieurng(new RufnummernportierungAnlageKftBuilder(WBCI_CDM_VERSION)
                        .withOnkz(rufnummer.getOnKz())
                        .withDurchwahlnummer(rufnummer.getDnBase())
                        .withAbfragestelle(rufnummer.getDirectDial())
                        .withRufnummernbloecke(Collections.singletonList(new RufnummernblockBuilder()
                                .withRnrBlockVon("00")
                                .withRnrBlockBis("49")
                                .withPkiAbg("D123")
                                .build()))
                        .build()));
    }

}
