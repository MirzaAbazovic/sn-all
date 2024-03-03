/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.13
 */
package de.mnet.wbci.acceptance.receiving;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_WithTaifunAndHurricanOrder_TestBehavior;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueOrnAuf_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_ORN;

    /**
     * Tests the AKM-TR sent by the Receiving Carrier (Hurrican) with a different MeldungCode (e.g. AdresseAbweichend):
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
    public void VaKueOrnAuf_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_02, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA, MeldungsCode.ADAHSNR));
    }

    /**
     * Tests the sending of a AKM-TR after an ABBM-TR has been received:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     *              <-  AKM-TR
     *     ABBM-TR  ->
     *              <-  AKM-TR
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_03, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_WithTaifunAndHurricanOrder_TestBehavior(getNewTaifunDataFactory().surfAndFonWithDns(1).persist()));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());

        applyBehavior(new ReceiveABBMTR_TestBehavior(MeldungsCode.UETN_NM));

        // When a ABBM-TR is received the GF is marked for clarification
        // We close the issue here so that after the AKM-TR is resent we can check
        // that the GF is not marked for clarification
        hurrican().closeGeschaefsfallIssue("Problem fixed");

        applyBehavior(new SendAKMTR_TestBehavior(2));
    }

    /**
     * Tests the RUEM-VA (tnbKennungAbg not set -> GF should NOT be marked as Klaerfall) sent by the Donating Carrier
     * (AtlasESB):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_04, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(
                new ReceiveRUEMVA_TestBehavior()
                        .isKlaerfall(false)
        );
    }

    /**
     * Tests the RUEM-VA (identifizierer is set -> verify that no Bemerkung has been added to the GF) sent by the
     * Donating Carrier (AtlasESB):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_05, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        hurrican().assertBemerkung(null);
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
    public void VaKueOrnAuf_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_06, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA, MeldungsCode.ADAORT)
                .isKlaerfall(true)
                .withKlaerfallGrund(
                        "(?s).*Die abweichende Adresse ist nicht gültig, da bei Meldungscode 8105 - .* nur das Attribut 'Ort' in der abweichenden Adresse gesetzt werden darf!.*"));
    }
}
