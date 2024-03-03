/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.14
 */
package de.augustakom.hurrican.service.exceptions.helper;

import static org.mockito.Mockito.*;

import java.util.*;
import javax.jms.Queue;
import javax.jms.*;
import javax.xml.ws.*;
import javax.xml.ws.handler.*;
import com.google.common.base.Throwables;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.jms.JMSConstants;
import org.apache.cxf.transport.jms.JMSMessageHeadersType;
import org.apache.cxf.transport.jms.JMSPropertyType;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.BusinessKeyBuilder;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 *
 */
public class HandleErrorFactoryTest {

    @Mock
    private WebServiceContext webServiceContext;
    @Mock
    private MessageContext messageContext;
    @Mock
    private Message webServiceMessage;
    @Mock
    private TextMessage jmsMessage;

    private HandleErrorFactory testling;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateFromWebServiceMessage() throws Exception {
        testling = new HandleErrorFactory(webServiceMessage);

        String soapAction = "WorkforceNotificationService/notifyOrderFeedback";
        String trackingId = UUID.randomUUID().toString();
        final String queueName = "JMS.Sample.Queue";

        when(webServiceMessage.get(JMSConstants.JMS_SERVER_REQUEST_HEADERS)).thenReturn(getJmsRequestHeaders(trackingId, soapAction));
        when(webServiceMessage.get(JMSConstants.JMS_REQUEST_MESSAGE)).thenReturn(jmsMessage);
        when(jmsMessage.getText()).thenReturn("SOAP_MESSAGE_BODY");
        when(jmsMessage.getJMSDestination()).thenReturn((Queue) () -> queueName);

        FFMServiceException cause = new FFMServiceException("TEST");
        HandleError result = testling.create(cause, "HUR-TECH-1000", "PROCESS_NAME");

        Assert.assertEquals(result.getTrackingId(), trackingId);
        Assert.assertEquals(result.getBusinessKey().size(), 0);

        Assert.assertNotNull(result.getComponent());
        Assert.assertNotNull(result.getComponent().getHost());
        Assert.assertEquals(result.getComponent().getProcessName(), "PROCESS_NAME");
        Assert.assertEquals(result.getComponent().getOperation(), "notifyOrderFeedback");
        Assert.assertEquals(result.getComponent().getService(), "WorkforceNotificationService");

        Assert.assertNotNull(result.getMessage());
        Assert.assertEquals(result.getMessage().getPayload(), "SOAP_MESSAGE_BODY");
        Assert.assertNotNull(result.getMessage().getRetryInfo());
        Assert.assertEquals(result.getMessage().getRetryInfo().getOrigErrorId(), trackingId + "_orig");
        Assert.assertEquals(result.getMessage().getRetryInfo().getRetryCount().intValue(), 1);
        Assert.assertEquals(result.getMessage().getJMSProperty().size(), 5);
        Assert.assertEquals(result.getMessage().getJMSEndpoint(), queueName);
        Assert.assertEquals(result.getMessage().getContext().size(), 0);

        Assert.assertNotNull(result.getError());
        Assert.assertEquals(result.getError().getMessage(), "TEST");
        Assert.assertEquals(result.getError().getCode(), "HUR-TECH-1000");
        Assert.assertEquals(result.getError().getErrorDetails(), Throwables.getStackTraceAsString(cause));
        Assert.assertNotNull(result.getError().getTime());
    }

    @Test
    public void testCreateFromWebServiceContext() throws Exception {
        testling = new HandleErrorFactory(webServiceContext);

        String soapAction = "WorkforceNotificationService/notifyUpdateOrder";
        String trackingId = UUID.randomUUID().toString();
        String workforceOrderId = UUID.randomUUID().toString();
        final String queueName = "JMS.Sample.Queue";

        when(webServiceContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.get(JMSConstants.JMS_SERVER_REQUEST_HEADERS)).thenReturn(getJmsRequestHeaders(trackingId, soapAction));
        when(messageContext.get(JMSConstants.JMS_REQUEST_MESSAGE)).thenReturn(jmsMessage);
        when(jmsMessage.getText()).thenReturn("SOAP_MESSAGE_BODY");
        when(jmsMessage.getJMSDestination()).thenReturn((Queue) () -> queueName);

        FFMServiceException cause = new FFMServiceException("TEST");
        HandleError result = testling.create(cause, "HUR-TECH-1000", "PROCESS_NAME",
                new BusinessKeyBuilder().withKeyValue("orderId", workforceOrderId).build());

        Assert.assertEquals(result.getTrackingId(), trackingId);
        Assert.assertEquals(result.getBusinessKey().size(), 1);

        Assert.assertEquals(result.getBusinessKey().get(0).getKey(), "orderId");
        Assert.assertEquals(result.getBusinessKey().get(0).getValue(), workforceOrderId);

        Assert.assertNotNull(result.getComponent());
        Assert.assertNotNull(result.getComponent().getHost());
        Assert.assertEquals(result.getComponent().getProcessName(), "PROCESS_NAME");
        Assert.assertEquals(result.getComponent().getOperation(), "notifyUpdateOrder");
        Assert.assertEquals(result.getComponent().getService(), "WorkforceNotificationService");

        Assert.assertNotNull(result.getMessage());
        Assert.assertEquals(result.getMessage().getPayload(), "SOAP_MESSAGE_BODY");
        Assert.assertNotNull(result.getMessage().getRetryInfo());
        Assert.assertEquals(result.getMessage().getRetryInfo().getOrigErrorId(), trackingId + "_orig");
        Assert.assertEquals(result.getMessage().getRetryInfo().getRetryCount().intValue(), 1);
        Assert.assertEquals(result.getMessage().getJMSProperty().size(), 5);
        Assert.assertEquals(result.getMessage().getJMSEndpoint(), queueName);
        Assert.assertEquals(result.getMessage().getContext().size(), 0);

        Assert.assertNotNull(result.getError());
        Assert.assertEquals(result.getError().getMessage(), "TEST");
        Assert.assertEquals(result.getError().getCode(), "HUR-TECH-1000");
        Assert.assertEquals(result.getError().getErrorDetails(), Throwables.getStackTraceAsString(cause));
        Assert.assertNotNull(result.getError().getTime());

        //assert sorted jmsProperties
        List<HandleError.Message.JMSProperty> jmsProperties = result.getMessage().getJMSProperty();
        Assert.assertEquals(jmsProperties.get(0).getKey(), AtlasEsbConstants.ESB_ERROR_ID_KEY);
        Assert.assertEquals(jmsProperties.get(1).getKey(), AtlasEsbConstants.ESB_RETRY_COUNT_KEY);
        Assert.assertEquals(jmsProperties.get(2).getKey(), AtlasEsbConstants.ESB_TRACKING_ID_KEY);
        Assert.assertEquals(jmsProperties.get(3).getKey(), AtlasEsbConstants.SOAPACTION_TIBCO);
        Assert.assertEquals(jmsProperties.get(4).getKey(), "something");
    }

    @Test
    public void testCreateWithBusinessKey() throws Exception {
        testling = new HandleErrorFactory(webServiceContext);

        String workforceOrderId = UUID.randomUUID().toString();
        final String queueName = "JMS.Sample.Queue";

        when(webServiceContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.get(JMSConstants.JMS_SERVER_REQUEST_HEADERS)).thenReturn(new JMSMessageHeadersType());
        when(messageContext.get(JMSConstants.JMS_REQUEST_MESSAGE)).thenReturn(jmsMessage);
        when(jmsMessage.getText()).thenReturn("SOAP_MESSAGE_BODY");
        when(jmsMessage.getJMSDestination()).thenReturn((Queue) () -> queueName);

        FFMServiceException cause = new FFMServiceException("TEST");
        HandleError result = testling.create(cause, "HUR-TECH-1000", "PROCESS_NAME",
                new BusinessKeyBuilder().withKeyValue("orderId", workforceOrderId).build());

        Assert.assertEquals(result.getBusinessKey().size(), 1);
        Assert.assertEquals(result.getBusinessKey().get(0).getKey(), "orderId");
        Assert.assertEquals(result.getBusinessKey().get(0).getValue(), workforceOrderId);
    }

    private JMSMessageHeadersType getJmsRequestHeaders(String esbTrackingId, String soapAction) {
        JMSPropertyType p1 = new JMSPropertyType();
        p1.setName("something");
        p1.setValue("else");

        JMSPropertyType p2 = new JMSPropertyType();
        p2.setName(AtlasEsbConstants.ESB_TRACKING_ID_KEY);
        p2.setValue(esbTrackingId);

        JMSPropertyType p3 = new JMSPropertyType();
        p3.setName(AtlasEsbConstants.ESB_ERROR_ID_KEY);
        p3.setValue(esbTrackingId + "_orig");

        JMSPropertyType p4 = new JMSPropertyType();
        p4.setName(AtlasEsbConstants.ESB_RETRY_COUNT_KEY);
        p4.setValue("1");

        JMSPropertyType p5 = new JMSPropertyType();
        p5.setName(AtlasEsbConstants.SOAPACTION_TIBCO);
        p5.setValue(soapAction);

        JMSMessageHeadersType jmsHeaders = new JMSMessageHeadersType();
        jmsHeaders.getProperty().add(p1);
        jmsHeaders.getProperty().add(p2);
        jmsHeaders.getProperty().add(p3);
        jmsHeaders.getProperty().add(p4);
        jmsHeaders.getProperty().add(p5);

        return jmsHeaders;
    }

}
