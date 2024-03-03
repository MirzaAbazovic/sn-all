/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

@Test(groups = BaseTest.ACCEPTANCE)
public class ErrorService_AccTest extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests KUEMRN use-case, sending an *INVALID* Xml payload to Hurrican and checks that a message containing the
     * invalid payload is sent to the error handling service :
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  ErrorHandlingService
     * </pre>
     */
    @CitrusTest
    @Test(enabled = true)
    public void ErrorService_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.ErrorService_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        variable(VariableNames.PRE_AGREEMENT_ID, "wbci:createVorabstimmungsId()");
        variable(VariableNames.CARRIER_CODE_ABGEBEND, CarrierCode.MNET.getITUCarrierCode());
        variable(VariableNames.CARRIER_CODE_AUFNEHMEND, CarrierCode.DTAG.getITUCarrierCode());
        variable(VariableNames.REQUESTED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 14)");

        atlas().sendCarrierChangeRequest("VA_KUEMRN_ABG");

        atlas().receiveErrorHandlingServiceMessage("ERROR_MSG");

        hurrican().assertNoIoArchiveEntryCreated(IOType.IN, geschaeftsfallTyp, null);

    }

    /**
     * Tests KUEMRN use-case, sending an *INVALID* Xml payload to Hurrican and then Hurrican sending also an *INVALID*
     * error request to the Atlas, which has to be logged into the T_EXCEPTION_LOG table:
     * <p/>
     * <pre>
     *     AtlasESB                 Hurrican (Donating Carrier)
     *     KUEMRN(invalid)   ->
     *                       <-     ErrorHandlingService(invalid)
     * </pre>
     * Attention!!! - Enable this test only for manual testing! It could run successfully only after changing some of
     * code in GenerateErrorMessageProcessor To be able to test the described behaviour the
     * GenerateErrorMessageProcessor must be changed and for example the ERROR_CODE should be set to null. In this case
     * an invalid error request will be generated which will cause adding an entry into the T_EXCEPTION_LOG table. Since
     * the GenerateErrorMessageProcessor is part of the web archive, which is deployed in the server, it is not so easy
     * to inject another processor bean instead without redeploying the application.
     * <p/>
     * If this test runs successfully would mean that the ErrorService_01_Test would fail and vice versa!
     */
    @CitrusTest
    @Test(enabled = false)
    public void ErrorService_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.ErrorService_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        hurrican().saveLatestExceptionLogEntryId(ExceptionLogEntryContext.ATLAS_ERROR_SERVICE_ERROR);

        variable(VariableNames.PRE_AGREEMENT_ID, "wbci:createVorabstimmungsId()");
        variable(VariableNames.CARRIER_CODE_ABGEBEND, CarrierCode.MNET.getITUCarrierCode());
        variable(VariableNames.CARRIER_CODE_AUFNEHMEND, CarrierCode.DTAG.getITUCarrierCode());
        variable(VariableNames.REQUESTED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 14)");

        atlas().sendCarrierChangeRequest("VA_KUEMRN_ABG");

        hurrican().assertWbciExceptionLogEntryCreated(ExceptionLogEntryContext.ATLAS_ERROR_SERVICE_ERROR);
    }

}
