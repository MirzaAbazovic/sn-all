/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.13
 */
package de.mnet.wbci.acceptance.receiving;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.ReceiveERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.ReceiveSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungKftBuilder;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueOrnAuf_StornoAenderung_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_ORN;

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEORN
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_StornoAenderung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_StornoAenderung_01, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ERLM. After sending the STR-AEN ERLM
     * Meldung a second GF (GF2) is created, which is then linked to the first GF (GF1). On completion the status of GF1
     * is checked to ensure that the geschaeftsfall is complete.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEORN
     *     RUEMVA   ->
     *     STRAEN   ->
     *              <-  ERLM
     *              <-  KUEORN
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_StornoAenderung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_StornoAenderung_02, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_ABG));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AEN_ABG));

        applyBehavior(new SendVA_TestBehavior().withVaLinkedToStrAenGf(true));
    }

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEORN
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_StornoAenderung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_StornoAenderung_03, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        atlas().sendCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEORN
     *     RUEMVA   ->
     *     STRAEN   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAuf_StornoAenderung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAuf_StornoAenderung_04, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_ABG));

        hurrican().createWbciMeldung(
                new AbbruchmeldungKftBuilder(WbciCdmVersion.V1, geschaeftsfallTyp, IOType.OUT)
                        .withoutWechseltermin()
                        .withMeldungsCodes(MeldungsCode.STORNO_ABG)
                        .withBegruendung("Sag ich nicht")
                        .buildForStorno(RequestTyp.STR_AEN_ABG), RequestTyp.STR_AEN_ABG
        );
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        atlas().receiveCarrierChangeUpdate("ABBM");
    }

}
