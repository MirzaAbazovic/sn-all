/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.13
 */
package de.mnet.hurrican.simulator.handler;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.simulator.helper.XPathHelper;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
@ContextConfiguration({ "classpath:simulator-test-context.xml" })
public class SimulatorEndpointAdapterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SimulatorEndpointAdapter handler;

    private EndpointAdapter endpointAdapter = Mockito.mock(EndpointAdapter.class);

    private XPathHelper xPathHelperMock = Mockito.mock(XPathHelper.class);

    @BeforeClass
    public void setupTest() {
        handler.setResponseEndpointAdapter(endpointAdapter);
        handler.setXPathHelper(xPathHelperMock);
    }

    @Test
    public void testV1Version() throws Exception {
        reset(endpointAdapter);

        when(endpointAdapter.handleMessage((Message) anyObject())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new DefaultMessage("OK");
            }
        });

        handler.handleMessage(buildTestRequest("1", "SIM1"));
    }

    @Test
    public void testV2Version() throws Exception {
        reset(endpointAdapter);

        when(endpointAdapter.handleMessage((Message) anyObject())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new DefaultMessage("OK");
            }
        });

        handler.handleMessage(buildTestRequest("2", "SIM2"));
    }

    @Test
    public void testDefaultBehavior() throws Exception {
        reset(endpointAdapter);

        when(endpointAdapter.handleMessage((Message) anyObject())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new DefaultMessage("OK");
            }
        });

        handler.handleMessage(buildTestRequest("1", "UNKNOWN"));
    }

    private Message buildTestRequest(String version, String builderName) {
        return new DefaultMessage("<sim" + version + ":TestMessage xmlns:sim" + version + "=\"http://mnet.de/sim/v" + version + "/envelope\">" +
                "<Text>" + builderName + "</Text>" +
                "</sim" + version + ":TestMessage>");
    }
}
