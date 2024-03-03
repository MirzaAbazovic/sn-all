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
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 * Test fuer WBCI Geschaeftsfall 'Reine Rufnummernportierung - abgebend'. (M-net = abgebender EKP)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaRrnpAbg_Test extends AbstractWbciAcceptanceTestBuilder {

    /**
     * Tests receiving RRNP with Portierungszeitfenster ZF3 which is not supported at this time. GF should not be marked
     * for clarification.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     VARRNP   ->
     *
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAbg_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAbg_02, WbciCdmVersion.V1, WbciVersion.V2, GeschaeftsfallTyp.VA_RRNP);

        applyBehavior(new ReceiveVA_TestBehavior());

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    /**
     * Tests receiving RRNP with Einzelrufnummer from the Donating Carrier (M-Net in this case): Match zu Hurrican
     * Auftrags Nr. 731806, und Taifun-Nr. 1917844 (over manual assignment).
     * <p/>
     * Use this for GUI-Tests of RUEM-VA-Dialog;
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAbg_03_Test() {

        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAbg_03, WbciCdmVersion.V1, WbciVersion.V2, GeschaeftsfallTyp.VA_RRNP);

        applyBehavior(new ReceiveVA_TestBehavior());

        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

}
