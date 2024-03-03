/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2014
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.utils.TestUtils;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAbgStandardAblauf_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private CarrierCode carrierCode;

    /**
     * Tests the typical KUEMRN use-case with all configured {@link CarrierCode}s (except M-net):
     * <p/>
     * <p/>
     * 
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     AKMTR    ->
     * </pre>
     */
    @Parameters("carrierCode")
    @Test(dataProvider = "carrierCodeDataProvider")
    public void VaKueMrnAbgStandardAblauf_01_Test(ITestContext testContext, CarrierCode carrierCode) {
        this.carrierCode = carrierCode;
        executeTest(testContext);
    }

    @DataProvider
    public Object[][] carrierCodeDataProvider() {
        return TestUtils.convertCarrierCodesToTestParameterArray();
    }

    @Override
    protected void configure() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbgStandardAblauf_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        assert carrierCode != null;
        applyBehavior(new ReceiveVA_TestBehavior().withAufnehmenderEKP(carrierCode));

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveAKMTR_TestBehavior());
    }

}
