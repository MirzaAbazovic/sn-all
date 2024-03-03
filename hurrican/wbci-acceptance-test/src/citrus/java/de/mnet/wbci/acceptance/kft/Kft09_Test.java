/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.13
 */
package de.mnet.wbci.acceptance.kft;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.RueckmeldungVorabstimmungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.RufnummerOnkzKftBuilder;
import de.mnet.wbci.acceptance.common.builder.RufnummernportierungEinzelKftBuilder;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendABBMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendABBM_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.StandortBuilder;

/**
 * Testfaelle TF 9.x : EKPabg und VA-KUE-MRN
 *
 *
 */
@Test(groups = BaseTest.KFT)
public class Kft09_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 9.1.01 Gutfall: RUEM-VA mit Auftragsbestätigung (MC 8001 oder 8002) und AKM-TR mit MC 8010:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  RUEMVA
     *     AKM-TR   ->
     * </pre>
     */
    @CitrusTest
    public void Test_9_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        final String useCaseNumber = "090101";
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, useCaseNumber, useCaseNumber)));
        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));
        applyBehavior(new ReceiveAKMTR_TestBehavior());
    }

    /**
     * 9.1.02 Gutfall: RUEM-VA mit abweichender Adresse (MC 8105) und AKM-TR mit MC 8010
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  RUEMVA
     *     AKM-TR   ->
     * </pre>
     */
    @CitrusTest
    public void Test_9_1_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_1_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090102", "090104")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ADAORT, MeldungsCode.ZWA)
                .withBuilder(new RueckmeldungVorabstimmungKftBuilder(getWbciCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT)
                        .withExplicitMeldungsCode(
                                new StandortBuilder()
                                        .withOrt("Starnberg")
                                        .build(),
                                MeldungsCode.ADAORT
                        )));

        applyBehavior(new ReceiveAKMTR_TestBehavior());
    }

    /**
     * 9.2.01 Schlechtfall: falsche RN: ABBM mit MC 8103
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090201", "090201")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.RNG));
    }

    /**
     * 9.2.02 Schlechtfall: falsche Adresse: ABBM mit MC 8108
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    @Test(enabled = false)
    public void Test_9_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090202", "090202")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.ADFPLZ));
    }

    /**
     * 9.2.03 Schlechtfall: falsche Adresse: PLZ und Ort, ABBM mit MC 8108, 8109
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    @Test(enabled = false)
    public void Test_9_2_03() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090203", "090206")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.ADFPLZ, MeldungsCode.ADFORT));
    }

    /**
     * 9.2.04 Schlechtfall: falscher Anschlussinhaber: ABBM mit MC 8112
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_04() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090204", "090207")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.AIF));
    }

    /**
     * 9.2.05 Schlechtfall: falsche ID: ABBM mit MC 8115
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_05() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_05, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090205", "090210")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 9.2.06 Schlechtfall: Kunde unbekannt: ABBM mit MC 8117
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_06() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_06, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090206", "090212")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.KNI));
    }

    /**
     * 9.2.07 Schlechtfall: falscher Projektkenner: ABBM mit MC 8149
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_07() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_07, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090207", "090214")));

        applyBehavior(new SendABBM_TestBehavior(MeldungsCode.UPK));
    }

    /**
     * 9.2.08 Schlechtfall: Vertragsnummer in AKM-TR falsch: ABBM-TR mit MC 8143
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *         VA   ->
     *              <-  RUEMVA
     *      AKMTR   ->
     *              <-  ABBM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_08() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_08, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090208", "090216")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA).withBuilder(createDefaultRuemVa()));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.WVNR_ABG));
    }


    /**
     * 9.2.09 Schlechtfall: LineID in AKM-TR falsch: ABBM-TR mit MC 8144
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB         Hurrican (Donating Carrier)
     *      VA-KUE-MRN  ->
     *                  <-  RUEM-VA
     *      AKMTR       ->
     *                  <-  ABBM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_09() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_09, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090209", "090217")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA).withBuilder(createDefaultRuemVa()));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.LID_ABG));
    }

    /**
     * 9.2.10 Schlechtfall: Übernahme Ressource nicht möglich: ABBM-TR mit MC 8147
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB         Hurrican (Donating Carrier)
     *      VA-KUE-MRN  ->
     *                  <-  RUEM-VA
     *      AKMTR       ->
     *                  <-  ABBM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_10() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_10, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090210", "090220")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA).withBuilder(createDefaultRuemVa()));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.UETN_NM));
    }

    /**
     * 9.2.11 Schlechtfall: AKM-TR doppelt versenden: ABBM-TR mit MC 8148
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB         Hurrican (Donating Carrier)
     *      VA-KUE-MRN  ->
     *                  <-  RUEM-VA
     *      AKMTR       ->
     *      AKMTR       ->
     *                  <-  ABBM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_11() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_2_11, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090211", "090221")));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA).withBuilder(createDefaultRuemVa()));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        // now resend the AKM-TR and ensure that an ABBM-TR with the MeldungsCode.UETN_BB is returned
        atlas().sendCarrierChangeUpdate("AKMTR");
        atlas().receiveCarrierChangeUpdate("ABBMTR");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.AKM_TR, 2, true);
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM_TR);
        hurrican().assertVaRequestStatus(WbciRequestStatus.AKM_TR_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * 9.2.12 Schlechtfall: M-net schickt neg. TEQ wg. falscher Versionsnummer in AKM-TR
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB       Hurrican (Donating Carrier)
     *      VA-KUE-MRN ->
     *                 <-  RUEM-VA
     *      AKM-TR     ->
     *                 <-  neg. TEQ mit Code 0993
     * (Der Testfall hoert beim Versand der RUEM-VA auf und dient nur dazu, die Voraussetzung fuer den
     * Empfang einer AKM-TR mit falscher Versionsnummer ueber das 'richtige' KFT-System zu triggern.
     * Der Versand der neg. TEQ wird hier nicht extra simuliert, da dies der Atlas ESB uebernimmt.)
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_12() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090212", "090222")));
        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));
    }

    /**
     * 9.2.13 Schlechtfall: M-net schickt neg. TEQ wg. falscher Datenstruktur in AKM-TR
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB       Hurrican (Donating Carrier)
     *      VA-KUE-MRN ->
     *                 <-  RUEM-VA
     *      AKM-TR     ->
     *                 <-  neg. TEQ mit Code 0995
     * (Der Testfall hoert beim Versand der RUEM-VA auf und dient nur dazu, die Voraussetzung fuer den
     * Empfang einer AKM-TR mit falscher Datenstruktur ueber das 'richtige' KFT-System zu triggern.
     * Der Versand der neg. TEQ wird hier nicht extra simuliert, da dies der Atlas ESB uebernimmt.)
     * </pre>
     */
    @CitrusTest
    public void Test_9_2_13() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_9_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        applyBehavior(new ReceiveVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.DTAG, "090213", "090223")));
        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));
    }


    private RueckmeldungVorabstimmungKftBuilder createDefaultRuemVa() {
        RueckmeldungVorabstimmungKftBuilder ruemVaKftBuilder = new RueckmeldungVorabstimmungKftBuilder(
                getWbciCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT);
        ruemVaKftBuilder.withRufnummernportierung(new RufnummernportierungEinzelKftBuilder(WBCI_CDM_VERSION)
                .withRufnummerOnkzs(Arrays.asList(
                        new RufnummerOnkzKftBuilder(WBCI_CDM_VERSION).withOnkz("30").withRufnummer("99999999").build(),
                        new RufnummerOnkzKftBuilder(WBCI_CDM_VERSION).withOnkz("30").withRufnummer("99999998").build()))
                .build());
        return ruemVaKftBuilder;
    }

}
