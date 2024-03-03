/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2014
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveTV_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendERLM_TV_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaRrnpAbg_Terminverschiebung_Test extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_RRNP;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests the TV use-case, when the TV is sent between the RUEM-VA and AKM-TR:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     VARRNP   ->
     *              <-  RUEMVA
     *     TV   ->
     *              <-  ERLM
     *     AKMTR   ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAbg_Terminverschiebung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAbg_Terminverschiebung_01, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveTV_TestBehavior());

        applyBehavior(new SendERLM_TV_TestBehavior()
                .withExpectedGfStatus(WbciGeschaeftsfallStatus.PASSIVE));
    }

}
