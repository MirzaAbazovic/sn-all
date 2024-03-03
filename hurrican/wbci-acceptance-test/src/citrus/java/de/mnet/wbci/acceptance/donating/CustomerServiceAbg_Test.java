/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

/**
 * Test fuer CustomerService Protocol - abgebend'. (M-net = abgebender EKP)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class CustomerServiceAbg_Test extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests that the customer service protocol is sent for the typical KUEMRN use-case:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican                CustomerService
     *     KUEMRN   ->
     *                  PROTOCOL (VA)      ->
     *              <-  RUEMVA
     *                  PROTOCOL (RUEMVA)  ->
     *     AKMTR    ->
     *                  PROTOCOL (AKMTR)   ->
     * </pre>
     */
    @CitrusTest
    public void CustomerServiceAbg_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.CustomerServiceAbg_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().premiumCallWithBlockDns(true, 200L).persist();

        Long billingOrderNoOrig = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();
        variable(VariableNames.BILLING_ORDER_NO_ORIG, billingOrderNoOrig);
        variable(VariableNames.CUSTOMER_ID, generatedTaifunData.getKunde().getKundeNo());

        // manually assigns a valid taifun auftrag to the geschaeftsfall
        hurrican().manuallyAssignTaifunOrderId(billingOrderNoOrig);

        atlas().receiveCustomerServiceMessage("PROTOCOL_VA");

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        atlas().receiveCustomerServiceMessage("PROTOCOL_RUEMVA");

        applyBehavior(new ReceiveAKMTR_TestBehavior());

        atlas().receiveCustomerServiceMessage("PROTOCOL_AKMTR");
    }

}
