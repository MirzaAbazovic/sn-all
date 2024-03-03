/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.07.13
 */
package de.mnet.hurrican.atlas.simulator.wita.builder;

import java.util.*;
import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.container.RepeatOnErrorUntilTrue;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.validation.ControlMessageValidationContext;
import com.consol.citrus.validation.builder.PayloadTemplateMessageBuilder;
import com.consol.citrus.variable.MessageHeaderVariableExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.atlas.simulator.config.AtlasSimulatorConfiguration;
import de.mnet.hurrican.atlas.simulator.wita.AbstractSimulatorBaseTest;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderMessageHeaders;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderServiceVersion;
import de.mnet.hurrican.atlas.simulator.wita.actions.WriteVariablesTestAction;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WitaTestBuilderTest extends AbstractSimulatorBaseTest {

    @Autowired
    private AtlasSimulatorConfiguration config;

    @Test
    public void testReceiveInitialOrder() throws Exception {
        final String externalOrderId = "11111111";

        MockTestBuilder testBuilder = new MockTestBuilder() {
            @Override
            protected void configure() {
                receiveOrderREQ();
            }

            protected void verifyTestCase(TestCase testCase) {
                assertReceiveActionWithoutTemplateValidation(testCase, externalOrderId);
            }
        };

        testBuilder.setSimulatorConfiguration(config);
        testBuilder.setServiceVersion(WitaLineOrderServiceVersion.V1);
        testBuilder.setExternalOrderId(externalOrderId);
        testBuilder.execute(new TestContext());
    }

    @Test
    public void testReceiveInitialOrderWithTemplate() throws Exception {
        final String externalOrderId = "22222222";

        MockTestBuilder testBuilder = new MockTestBuilder() {
            @Override
            protected void configure() {
                receiveOrderREQ("NEU");
            }

            protected void verifyTestCase(TestCase testCase) {
                assertReceiveActionWithTemplateValidation(testCase, externalOrderId);
            }
        };

        testBuilder.setSimulatorConfiguration(config);
        testBuilder.setServiceVersion(WitaLineOrderServiceVersion.V1);
        testBuilder.setExternalOrderId(externalOrderId);
        testBuilder.execute(new TestContext());
    }

    @Test
    public void testDisableTemplateValidation() throws Exception {
        final String externalOrderId = "33333333";
        Assert.assertTrue(config.isTemplateValidationActive());
        config.setTemplateValidation(false);
        MockTestBuilder testBuilder = new MockTestBuilder() {
            @Override
            protected void configure() {
                receiveOrderREQ("NEU");
            }

            protected void verifyTestCase(TestCase testCase) {
                assertReceiveActionWithoutTemplateValidation(testCase, externalOrderId);
            }
        };
        try {
            testBuilder.setSimulatorConfiguration(config);
            testBuilder.setServiceVersion(WitaLineOrderServiceVersion.V1);
            testBuilder.setExternalOrderId(externalOrderId);
            testBuilder.execute(new TestContext());
        }
        finally {
            config.setTemplateValidation(true);
        }
    }

    private void assertReceiveActionWithoutTemplateValidation(TestCase testCase, String externalOrderId) {
        List<TestAction> testActions = testCase.getActions();
        Assert.assertTrue(!testActions.isEmpty());
        Assert.assertTrue(testActions.get(0) instanceof RepeatOnErrorUntilTrue);
        Assert.assertTrue(testActions.get(1) instanceof WriteVariablesTestAction);
        Assert.assertTrue(testActions.get(2) instanceof ReceiveMessageAction);

        ReceiveMessageAction receiveMessageAction = (ReceiveMessageAction) testActions.get(2);
        Assert.assertEquals(receiveMessageAction.getMessageSelectorString(), WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID
                + " = '" + externalOrderId + "'");
        Assert.assertEquals(receiveMessageAction.getValidationContexts().size(), 0);

        Assert.assertEquals(receiveMessageAction.getVariableExtractors().size(), 1);
        Assert.assertTrue(receiveMessageAction.getVariableExtractors().get(0) instanceof MessageHeaderVariableExtractor);

        MessageHeaderVariableExtractor variableExtractor = (MessageHeaderVariableExtractor) receiveMessageAction
                .getVariableExtractors().get(0);
        Assert.assertTrue(variableExtractor.getHeaderMappings().containsKey(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID));
        Assert.assertTrue(variableExtractor.getHeaderMappings().containsKey(WitaLineOrderMessageHeaders.CONTRACT_ID));
        Assert.assertTrue(variableExtractor.getHeaderMappings().containsKey(WitaLineOrderMessageHeaders.CUSTOMER_ID));
    }

    private void assertReceiveActionWithTemplateValidation(TestCase testCase, String externalOrderId) {
        List<TestAction> testActions = testCase.getActions();
        Assert.assertTrue(!testActions.isEmpty());
        Assert.assertTrue(testActions.get(0) instanceof RepeatOnErrorUntilTrue);
        Assert.assertTrue(testActions.get(1) instanceof WriteVariablesTestAction);
        Assert.assertTrue(testActions.get(2) instanceof ReceiveMessageAction);

        ReceiveMessageAction receiveMessageAction = (ReceiveMessageAction) testActions.get(2);
        Assert.assertEquals(receiveMessageAction.getMessageSelectorString(), WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID
                + " = '" + externalOrderId + "'");
        Assert.assertEquals(receiveMessageAction.getValidationContexts().size(), 1);
        Assert.assertTrue(((ControlMessageValidationContext) receiveMessageAction.getValidationContexts().get(0))
                .getMessageBuilder() instanceof PayloadTemplateMessageBuilder);

        Assert.assertEquals(receiveMessageAction.getVariableExtractors().size(), 1);
        Assert.assertTrue(receiveMessageAction.getVariableExtractors().get(0) instanceof MessageHeaderVariableExtractor);

        MessageHeaderVariableExtractor variableExtractor = (MessageHeaderVariableExtractor) receiveMessageAction
                .getVariableExtractors().get(0);
        Assert.assertTrue(variableExtractor.getHeaderMappings().containsKey(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID));
        Assert.assertTrue(variableExtractor.getHeaderMappings().containsKey(WitaLineOrderMessageHeaders.CONTRACT_ID));
        Assert.assertTrue(variableExtractor.getHeaderMappings().containsKey(WitaLineOrderMessageHeaders.CUSTOMER_ID));
    }

}
