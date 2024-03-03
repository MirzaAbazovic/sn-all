/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 01.07.2014 
 */
package de.mnet.wbci.acceptance.common;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.ReceiveERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_VA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_WithTaifunAndHurricanOrder_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciVersion;

@Test(groups = BaseTest.ACCEPTANCE)
public class HouseKeeping_AccTest extends AbstractWbciAcceptanceTestBuilder {
    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;
    @Autowired
    @Qualifier("atlasCustomerServiceEndpoint")
    private JmsEndpoint atlasCustomerServiceEndpoint;

    /**
     * Tests that elapsed VAs are cleaned up by the house-keeping job.
     */
    @CitrusTest
    public void HouseKeeping_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.HouseKeeping_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        String vorabstimmungsId = hurrican().createPreAgreementId(RequestTyp.VA);
        hurrican().createAndStoreGeschaeftsfall(new WbciGeschaeftsfallKueMrnKftBuilder(WBCI_CDM_VERSION)
                .withVorabstimmungsId(vorabstimmungsId)
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withWechseltermin(LocalDate.now())
                .withAbgebenderEKP(CarrierCode.DTAG)
                .withAufnehmenderEKP(CarrierCode.MNET));

        variables().add(VariableNames.PRE_AGREEMENT_ID, vorabstimmungsId);

        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().triggerWbciHousekeeping();

        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().changeWechseltermin(LocalDateTime.now().minusDays(4));

        hurrican().triggerWbciHousekeeping();

        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.COMPLETE);
    }

    /**
     * Tests the Storno Aufhebung sent from Receiving Carrier (Hurrican) with ERLM NOT marked as completed as long as
     * the user won't do it manually:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *              <-  STRAUF
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void HouseKeeping_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.HouseKeeping_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().triggerWbciHousekeeping();
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the ABBM sent to the VA by the Receiving Carrier (Hurrican) NOT marked as completed as long as the user
     * won't do it manually:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     ABBM   ->
     * </pre>
     */
    @CitrusTest
    public void HouseKeeping_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.HouseKeeping_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveABBM_VA_TestBehavior(MeldungsCode.RNG));

        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().triggerWbciHousekeeping();
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests that expired VAs (Status=NEW_VA, wechseltermin <= 7 AT) are updated by the house-keeping job.
     *     AtlasESB     Hurrican (Receiving Carrier)    CustomerService
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ERLM     ->
     *                  PROTOCOL (NEW_VA_EXPIRED)    ->
     *
     */
    @CitrusTest
    public void HouseKeeping_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.HouseKeeping_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        LocalDateTime expiredWechselTermin = DateCalculationHelper.getDateInWorkingDaysFromNow(7);

        applyBehavior(new SendVA_WithTaifunAndHurricanOrder_TestBehavior(getNewTaifunDataFactory().surfAndFonWithDns(1).persist()));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));

        hurrican().triggerWbciHousekeeping();

        purgeQueues(atlasCustomerServiceEndpoint.getEndpointConfiguration().getConnectionFactory()).queue(atlasCustomerServiceEndpoint.getEndpointConfiguration().getDestinationName());

        hurrican().changeWechseltermin(expiredWechselTermin);

        hurrican().triggerWbciHousekeeping();

        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.NEW_VA_EXPIRED);

        atlas().receiveCustomerServiceMessage("PROTOCOL_VA_NEW_EXPIRED");
    }

}