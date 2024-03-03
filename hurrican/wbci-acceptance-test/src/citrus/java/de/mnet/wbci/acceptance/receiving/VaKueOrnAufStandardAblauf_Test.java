/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2014
 */
package de.mnet.wbci.acceptance.receiving;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.utils.TestUtils;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueOrnAufStandardAblauf_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_ORN;
    private CarrierCode carrierCode;

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
    @Parameters("carrierCode")
    @Test(dataProvider = "carrierCodeDataProvider")
    public void VaKueOrnAufStandardAblauf_01_Test(ITestContext testContext, CarrierCode carrierCode) {
        this.carrierCode = carrierCode;
        executeTest(testContext);
    }

    @DataProvider
    public Object[][] carrierCodeDataProvider() {
        return TestUtils.convertCarrierCodesToTestParameterArray();
    }

    @Override
    protected void configure() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueOrnAufStandardAblauf_01, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        assert carrierCode != null;
        applyBehavior(new SendVA_TestBehavior().withAbgebenderEKP(carrierCode));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());
    }

}
