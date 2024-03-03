/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.13
 */
package de.mnet.wbci.acceptance.donating;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendABBMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

/**
 * Test fuer WBCI Geschaeftsfall 'Kuendigung ohne Rufnummernuebernahme - abgebend'. (M-net = abgebender EKP)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueOrnAbg_Test extends AbstractWbciAcceptanceTestBuilder {

    /**
     * Tests the ABBM-TR when sent from the Donating Carrier (M-Net in this case):
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEORN   ->
     *              <-  RUEMVA
     *     AKMTR    ->
     *              <-  ABBMTR
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAbg_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAbg_02, WbciCdmVersion.V1, WbciVersion.V2,
                GeschaeftsfallTyp.VA_KUE_ORN);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.UETN_NM));
    }

    /**
     * Tests the receiving of an AKM-TR after an ABBM-TR has been sent:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEORN   ->
     *              <-  RUEMVA
     *     AKMTR    ->
     *              <-  ABBMTR
     *     AKMTR    ->
     * </pre>
     */
    @CitrusTest
    public void VaKueOrnAbg_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAbg_03, WbciCdmVersion.V1, WbciVersion.V2,
                GeschaeftsfallTyp.VA_KUE_ORN);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        applyBehavior(new SendABBMTR_TestBehavior(MeldungsCode.UETN_NM));

        applyBehavior(new ReceiveAKMTR_TestBehavior(2));
    }


    /**
     * Tests receiving KUEORN with Einzelrufnummer from the Donating Carrier (M-Net in this case): Match zu Hurrican
     * Auftrags Nr. 837384 , und Taifun-Nr. 2129402  (over manual assignment).
     * <p/>
     * Use this for GUI-Tests of RUEM-VA-LineIds;
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEORN   ->
     *
     * </pre>
     * <p/>
     * => use this for GUI - RUEM-VA / ABBM Tests (with Anschlussart 'Virtuell' => WITA-Vertragnr is expected but not
     * set)
     */
    @CitrusTest
    public void VaKueOrnAbg_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAbg_04, WbciCdmVersion.V1, WbciVersion.V2,
                GeschaeftsfallTyp.VA_KUE_ORN);

        applyBehavior(new ReceiveVA_TestBehavior());
    }

}
