/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.13
 */
package de.mnet.wbci.acceptance.kft;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.LeitungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.RufnummernportierungAnlageKftBuilder;
import de.mnet.wbci.acceptance.common.builder.UebernahmeRessourceMeldungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_VA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.Anrede;
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
 * Testfaelle TF 1.x : EKPauf und VA-KUE-MRN
 * 
 *
 */
@Test(groups = BaseTest.KFT)
public class Kft01_Test extends AbstractWbciKftTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * 1.1.01 Gutfall: RUEM-VA mit Auftragsbestätigung (MC 8001) und AKM-TR mit MC 8010:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_1_1_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_1_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        final String useCaseNumber = "010101";

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, useCaseNumber, useCaseNumber)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());
    }

    /**
     * 1.1.02 RUEM-VA mit Auftragsbestätigung (MC 8001) und abweichender Adresse: AKM-TR mit MC 8105:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     * </pre>
     */
    @CitrusTest
    public void Test_1_1_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_1_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION).withOrt("Musterhausen"))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010102", "010104")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA, MeldungsCode.ADAORT));

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withoutUebernahme()));
    }

    /**
     * 1.2.01 falsche RN: ABBM mit MC 8103:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_01() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION)
                        .withRufnummernportierung(new RufnummernportierungAnlageKftBuilder(WBCI_CDM_VERSION).build()))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010201", "010201")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.RNG));
    }

    /**
     * 1.2.02 falsche Adresse: ABBM mit MC 8108:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_02() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION).withPostleitzahl("80000"))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010202", "010202")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.ADFPLZ));
    }

    /**
     * 1.2.03 falsche Adresse: PLZ und Ort, ABBM mit MC 8108, 8109:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_03() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION).withPostleitzahl("80000"))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010203", "010206")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.ADFPLZ, MeldungsCode.ADFORT));
    }

    /**
     * 1.2.04 falscher Anschlussinhaber: ABBM mit MC 8112:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_04() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION)
                        .withAnschlussinhaber("Mustermann", "Max", Anrede.HERR))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010204", "010207")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.AIF));
    }

    /**
     * 1.2.05 falsche ID: ABBM mit MC 8115:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_05() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_05, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(null)
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010205", "010210")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.BVID));
    }

    /**
     * 1.2.06 Kunde unbekannt: ABBM mit MC 8117:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_06() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_06, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION)
                        .withEndkunde("Mustermann", "Max", Anrede.HERR))
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010206", "010212")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.KNI));
    }

    /**
     * 1.2.07 falscher Projektkenner: ABBM mit MC 8149:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *                  <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_07() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_07, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(null)
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010207", "010214")));

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.UPK));
    }

    /**
     * 1.2.08 Vertragsnummer in AKM-TR falsch: ABBM-TR mit MC 8143:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_08() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_08, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010208", "010216")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withLeitungen(new LeitungKftBuilder(WBCI_CDM_VERSION).withVertragsnummer("123456789").build())));

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.WVNR_ABG));
    }

    /**
     * 1.2.09 LineID in AKM-TR falsch: ABBM-TR mit MC 8144:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_09() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_09, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010209", "010217")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withLeitungen(new LeitungKftBuilder(WBCI_CDM_VERSION)
                                .withVertragsnummer(null)
                                .withLineId(CarrierCode.DTAG.getITUCarrierCode() + ".1234567").build())));

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.LID_ABG));
    }

    /**
     * 1.2.10 Übernahme Ressource nicht möglich: ABBM-TR mit MC 8147:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_10() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_10, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010210", "010220")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withUebernahme(true)));

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.UETN_NM));
    }

    /**
     * 1.2.11 AKM-TR doppelt versenden: ABBM-TR mit MC 8148:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    // Da es nicht moeglich ist ueber Hurrican eine zweite AKM-TR zu schicken, nach dem die erste rausgeschickt
    // worden ist, muessen wir die AKM-TR direkt an Atlas-ESB schicken
    public void Test_1_2_11() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_11, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010211", "010221")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());

        kft().sendCarrierChangeUpdate("AKMTR");
        atlas().receiveCarrierChangeUpdate("AKMTR");

        applyBehavior(new ReceiveABBMTR_TestBehavior(false, MeldungsCode.UETN_BB));
    }

    /**
     * 1.2.12 Schlechtfall: KFT antwortet auf gültige AKM-TR mit TEQ mit MC 0993: Versionsnummer fehlerhaft:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB                 Hurrican (Receiving Carrier)
     *                      <-      AKM-TR
     *     Negative TEQ     ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_12() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_12, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010212", "010222")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withUebernahme(true)));
    }

    /**
     * 1.2.13 Schlechtfall: KFT antwortet auf gültige AKM-TR mit TEQ mit MC 0995: Datenstruktur ungültig:
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB                 Hurrican (Receiving Carrier)
     *                      <-      AKM-TR
     *     Negative TEQ     ->
     * </pre>
     */
    @CitrusTest
    public void Test_1_2_13() {
        simulatorUseCase(WbciSimulatorUseCase.Kft_1_2_13, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior()
                .withPreAgreementId(createPreAgreementId(RequestTyp.VA, CarrierCode.MNET, "010213", "010223")));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior(
                new UebernahmeRessourceMeldungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withUebernahme(true)));
    }

    /**
     * Creates new VorabstimmungsAnfrage instance and stores it to the database directly. Also creates proper test
     * variables so future test actions can refer to basic VorabstimmungsAnfrage properties.
     * 
     * @param vorabstimmungsId
     * @return
     */
    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> createAndStoreVa(String vorabstimmungsId) {
        WbciGeschaeftsfallKueMrn gf = kft().createAndStoreGeschaeftsfallKueMrn(vorabstimmungsId, CarrierCode.DTAG,
                CarrierCode.MNET);
        return kft().createAndStoreVa(
                WbciRequestStatus.RUEM_VA_EMPFANGEN,
                gf,
                MeldungsCode.ZWA,
                MeldungTyp.RUEM_VA,
                IOType.IN);
    }

}
