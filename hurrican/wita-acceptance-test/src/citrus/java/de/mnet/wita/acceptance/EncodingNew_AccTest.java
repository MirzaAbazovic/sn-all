/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2015
 */
package de.mnet.wita.acceptance;

import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;

@Test(groups = BaseTest.ACCEPTANCE)
public class EncodingNew_AccTest extends AbstractWitaAcceptanceTest {

    public static final String TEST_TOKEN = "ßÄÉÖÜäéöüß";

    /**
     * Tests encoding in new cb & abbm
     *  - new CB --> user last name
     *  - abbm --> meldungstext
     * Bound via ${encoding_test_token} citrus variable
     */
    @CitrusTest(name = "EncodingNew_AccTest")
    public void testEncoding() throws Exception {
        useCase(WitaAcceptanceUseCase.ENCODING_NEU_ABBM, WitaCdmVersion.V1);
        variables().add("encoding_test_token", TEST_TOKEN);

        final AcceptanceTestWorkflow workflow = workflow().get();
        final CreatedData createdData = createData(workflow, WitaSimulatorTestUser.ENCODING_NEU_ABBM);
        workflow().select(workflow).send(createdData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotificationWithNewVariables("ABBM", VariableNames.CONTRACT_ID);
        workflow().waitForABBM();

        workflow().doWithWorkflow(wf -> {
            Assert.assertTrue(wf.getCbVorgang().getReturnBemerkung().contains(TEST_TOKEN));
        });
    }

    private CreatedData createData(AcceptanceTestWorkflow workflow, WitaSimulatorTestUser witaSimulatorTestUser) throws Exception {
        final AcceptanceTestDataBuilder testDataBuilder = workflow().getTestDataBuilder();
        final AcceptanceTestDataBuilder testData = testDataBuilder
                .withUserName(TEST_TOKEN);
        try {
            return workflow.createData(testData);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to create test data", e);
        }
    }

}
