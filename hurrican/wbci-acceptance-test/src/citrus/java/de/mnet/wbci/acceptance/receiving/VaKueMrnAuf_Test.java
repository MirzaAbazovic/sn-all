/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.13
 */
package de.mnet.wbci.acceptance.receiving;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_VA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAuf_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;

    /**
     * Tests sending two VAs to different Donating Carriers. The AKM-TR is sent by the Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB              Hurrican (Receiving Carrier)
     *                       <-  VA
     *     RUEMVA            ->
     *                       <-  VA (VODAFONE)
     *     RUEMVA (VODAFONE) ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_02, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(WbciCdmVersion.V1).withAbgebenderEKP(CarrierCode.VODAFONE)));
        applyBehavior(new ReceiveRUEMVA_TestBehavior());
    }

    /**
     * Tests the RUEM-VA sent by the Donating Carrier (Atlas) with a different MeldungCode (e.g. AdresseAbweichend):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_03, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA, MeldungsCode.ADAHSNR));
    }

    /**
     * Tests the AKM-TR sent by the Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_04_Test() throws Exception {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_04, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        hurrican().assertRufnummernblock(
                new RufnummernblockBuilder()
                        .withRnrBlockVon("00")
                        .withRnrBlockBis("99")
                        .build()
        );

        applyBehavior(new SendAKMTR_TestBehavior());
    }

    /**
     * Tests the AKM-TR sent by the Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_05_Test() throws Exception {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_05, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(WbciCdmVersion.V1, false)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        hurrican().assertRufnummerOnkzEinzel(
                new RufnummerOnkzBuilder()
                        .withOnkz("89")
                        .withRufnummer("123456789")
                        .build(),
                new RufnummerOnkzBuilder()
                        .withOnkz("89")
                        .withRufnummer("1234566660")
                        .build()
        );

        applyBehavior(new SendAKMTR_TestBehavior());
    }

    /**
     * Tests the ABBM sent to the VA by the Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_06, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.RNG));
    }

    /**
     * Tests the AKM-TR sent by the Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_07_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_07, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.UETN_NM, MeldungsCode.UETN_BB));
    }

    /**
     * Tests marking the WbciGeschaeftsfall as 'Klaerfall':
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM     ->
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_08_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_08, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.RNG));

        // just receive a RuemVa without additional checks
        atlas().sendCarrierChangeUpdate("RUEMVA");

        hurrican().assertKlaerfallStatus(true, null);
    }

    /**
     * Tests marking the WbciGeschaeftsfall as 'Klaerfall':
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_09_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_09, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        // just receive a RuemVa without additional checks
        atlas().sendCarrierChangeUpdate("RUEMVA");
        hurrican().assertKlaerfallStatus(true, null);
    }

    /**
     * Tests the AKM-TR with MeldungsCode 8148 (UETN_BB) sent by the Receiving Carrier (Hurrican). In this case the
     * Geschaeftsfall should not be marked as Klaerfall:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_10_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_10, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.UETN_BB).withExpectedKlaerfall(false));
    }

    /**
     * Tests the RUEM-VA (NAT is before KWT) sent by the Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_11_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_11, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(
                new ReceiveRUEMVA_TestBehavior(LocalDateTime.of(2012, 1, 2, 0, 0, 0, 0), MeldungsCode.ZWA)
                        .isKlaerfall(true)
                        .withKlaerfallGrund(
                                "(?s).*Der angegebene Kundenwunschtermin ist nicht gültig. Bitte beachten Sie folgende Kriterien.*")
        );

        hurrican().assertBemerkung("(?s).*Der angegebene Wechseltermin \\d+.\\d+.\\d+ in der Rueckmeldung zur Vorabstimmung " +
                "\\(RUEM-VA\\) liegt vor dem Kundenwunschtermin \\d+.\\d+.\\d+ der Vorabstimmung!.*");
    }

    /**
     * Tests the RUEM-VA (NAT is a holiday -> GF should be marked as Klaerfall) sent by the Receiving Carrier
     * (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_12_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_12, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(
                new ReceiveRUEMVA_TestBehavior(LocalDateTime.of(2012, 1, 1, 0, 0, 0, 0), MeldungsCode.ZWA)
                        .isKlaerfall(true)
                        .withKlaerfallGrund(
                                "(?s).*Der angegebene Kundenwunschtermin ist nicht gültig. Bitte beachten Sie folgende Kriterien.*")
        );
    }

    /**
     * Tests the RUEM-VA (Technologie is not set, but is mandatory -> GF should be marked as Klaerfall) sent by the
     * Receiving Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_13_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_13, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(
                new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA)
                        .isKlaerfall(true)
                        .withKlaerfallGrund("(?s).*Die Attribute .* sind Pflichtfelder und müssen gesetzt sein..*")
        );
    }

    /**
     * Tests the RUEM-VA (Technische Ressource is not set - see WITA-2155) sent by the Receiving Carrier (Hurrican) GF
     * should not be marked as Klaerfall anymore!!!
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_14_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_14, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));
    }

    /**
     * Tests the RUEM-VA, haven't a ADA-Meldungscode (see WITA-1827) with schema invalid Standort data sent by the
     * donating carrier (atlas). However this test verifies that the RUEMVA is processed successfully in any case.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB                        Hurrican (Receiving Carrier)
     *                               <-     VA
     *     RUEMVA (invalid )    ->
     *                          <-    ErrorHandlingService
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_15_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_15, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        atlas().sendCarrierChangeUpdate("RUEMVA");
        atlas().receiveErrorHandlingServiceMessage("ERROR_MSG");
        hurrican().assertNoIoArchiveEntryCreated(IOType.IN, geschaeftsfallTyp, MeldungTyp.RUEM_VA);
    }

    /**
     * Tests the RUEM-VA, haven't a ADA-Meldungscode (see WITA-1827) with wrong Standort data sent by the donating
     * carrier (atlas). However this test verifies that the RUEMVA is processed successfully in any case.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB                        Hurrican (Receiving Carrier)
     *                                 <-  VA
     *     RUEMVA (marked as Klärfall) ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_16_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_16, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA, MeldungsCode.ADAHSNR)
                        .isKlaerfall(true)
                        .withKlaerfallGrund(
                                "(?s).*Die abweichende Adresse ist nicht gültig, da bei Meldungscode 8107 - .* nur das Attribut 'Hausnummer' in der abweichenden Adresse gesetzt werden darf!.*")
        );
    }

    /**
     * Tests the processing of a RUEM-VA which contains a Rufnummer that differs to the Rufnummer requested within the
     * VA. This test verifies that the RUEMVA is processed successfully and the GF is marked as Klaerfall.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB                        Hurrican (Receiving Carrier)
     *                                 <-  VA
     *     RUEMVA (marked as Klärfall) ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_17_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_17, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA)
                        .isKlaerfall(true)
                        .withKlaerfallGrund(
                                "(?s).*Keine, der innerhalb der RUEM-VA angegebenen Rufnummern, ist in der Vorabstimmung enthalten.*")
        );
    }

    /**
     * Tests the processing of a RUEM-VA which contains a Rufnummerblock that differs to the Rufnummerblock requested
     * within the VA. This test verifies that the RUEMVA is processed successfully and the GF is marked as Klaerfall.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_18_Test() throws Exception {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_18, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior()
                        .isKlaerfall(true)
                        .withKlaerfallGrund(
                                "(?s).*Keine, der innerhalb der RUEM-VA angegebenen Rufnummern, ist in der Vorabstimmung enthalten.*")
        );

        hurrican().assertRufnummernblock(
                new RufnummernblockBuilder()
                        .withRnrBlockVon("00")
                        .withRnrBlockBis("49")
                        .build(),
                new RufnummernblockBuilder()
                        .withRnrBlockVon("50")
                        .withRnrBlockBis("99")
                        .build()
        );
    }

    /**
     * Tests the ABBM handling when an ABBM is sent by the parnter Carrier with the meldungscode SONST, but without any
     * begruendung. The meldung should be processed sucessfully and the Klaerfall flag should NOT be set.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_19_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_19, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.SONST).withExpectedKlaerfall(false));
    }

}
