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
import de.mnet.wbci.acceptance.common.behavior.ReceiveABBM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.ReceiveERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.ReceiveSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendABBM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendSTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

@Test(groups = BaseTest.ACCEPTANCE)
public class VaRrnpAuf_StornoAenderung_Test extends AbstractWbciAcceptanceTestBuilder {

    private final RequestTyp stornoType = RequestTyp.STR_AEN_ABG;
    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_RRNP;

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  RRNP
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_StornoAenderung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_StornoAenderung_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  RRNP
     *     RUEMVA   ->
     *     STRAEN   ->
     *              <-  ERLM
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_StornoAenderung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_StornoAenderung_02, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(stornoType));

        applyBehavior(new SendERLM_STR_TestBehavior(stornoType));
    }

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  RRNP
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_StornoAenderung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_StornoAenderung_03, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new ReceiveABBM_STR_TestBehavior(MeldungsCode.STORNO_ABG));
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  RRNP
     *     RUEMVA   ->
     *     STRAEN   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_StornoAenderung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_StornoAenderung_04, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(stornoType));

        applyBehavior(new SendABBM_STR_TestBehavior(stornoType, MeldungsCode.STORNO_ABG));
    }

}
