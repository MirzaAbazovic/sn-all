/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.impl;

import static org.mockito.Mockito.*;

import java.time.*;
import javax.jms.*;
import javax.xml.ws.*;
import javax.xml.ws.handler.*;
import org.apache.cxf.transport.jms.JMSConstants;
import org.apache.cxf.transport.jms.JMSMessageHeadersType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1.NotifyOrderFeedbackTestBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1.NotifyUpdateOrderTestBuilder;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.service.exceptions.AtlasErrorHandlingService;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyOrderFeedback;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyUpdateOrder;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

@Test(groups = BaseTest.UNIT)
public class FFMNotificationConsumerImplTest extends BaseTest {

    @Mock
    private FFMService ffmService;
    @Mock
    private AtlasErrorHandlingService atlasErrorHandlingService;
    @Mock
    private WebServiceContext webServiceContext;
    @Mock
    private MessageContext messageContext;
    @Mock
    private TextMessage jmsMessage;

    @InjectMocks
    private FFMNotificationConsumerImpl testling;

    @BeforeMethod
    public void setUp() {
        testling = new FFMNotificationConsumerImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotifyOrderFeedbackTextFeedback() {
        NotifyOrderFeedback textFeedback = new NotifyOrderFeedbackTestBuilder()
                .withMaterial(null)
                .build();

        testling.notifyOrderFeedback(textFeedback);
        verify(ffmService).textFeedback(
                eq(textFeedback.getOrderId()), eq(textFeedback.getText()), eq(textFeedback.getCaptured()));
        verify(ffmService, never()).materialFeedback(anyString(), any(NotifyOrderFeedback.Material.class));
        verify(atlasErrorHandlingService, never()).handleError(any(HandleError.class));
    }

    @Test
    public void testNotifyOrderFeedbackMaterialFeedback() {
        NotifyOrderFeedback materialFeedback = new NotifyOrderFeedbackTestBuilder()
                .withText(null).build();

        testling.notifyOrderFeedback(materialFeedback);
        verify(ffmService, never()).textFeedback(anyString(), anyString(), any(LocalDateTime.class));
        verify(ffmService).materialFeedback(eq(materialFeedback.getOrderId()), eq(materialFeedback.getMaterial()));
        verify(atlasErrorHandlingService, never()).handleError(any(HandleError.class));
    }

    @Test
    public void testNotifyOrderFeedbackUndefinedFeedback() {
        testling.notifyOrderFeedback(new NotifyOrderFeedback());
        verify(ffmService, never()).textFeedback(anyString(), anyString(), any(LocalDateTime.class));
        verify(ffmService, never()).materialFeedback(anyString(), any(NotifyOrderFeedback.Material.class));
        verify(atlasErrorHandlingService, never()).handleError(any(HandleError.class));
    }

    @Test
    public void testNotifyOrderFeedbackExceptionHandling() {
        NotifyOrderFeedback textFeedback = new NotifyOrderFeedbackTestBuilder()
                .withMaterial(null)
                .build();

        FFMServiceException e = new FFMServiceException("TEST");
        doThrow(e).when(ffmService).textFeedback(anyString(), anyString(), any(LocalDateTime.class));
        when(webServiceContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.get(JMSConstants.JMS_SERVER_REQUEST_HEADERS)).thenReturn(new JMSMessageHeadersType());
        when(messageContext.get(JMSConstants.JMS_REQUEST_MESSAGE)).thenReturn(jmsMessage);
        testling.notifyOrderFeedback(textFeedback);
        verify(ffmService, never()).materialFeedback(anyString(), any(NotifyOrderFeedback.Material.class));
        verify(atlasErrorHandlingService).handleError(any(HandleError.class));
    }

    @Test
    public void testNotifyUpdateOrderExceptionHandling() {
        NotifyUpdateOrder notifyUpdate = new NotifyUpdateOrderTestBuilder().build();

        FFMServiceException e = new FFMServiceException("TEST");
        doThrow(e).when(ffmService).notifyUpdateOrder(eq(notifyUpdate), anyLong());
        when(webServiceContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.get(JMSConstants.JMS_SERVER_REQUEST_HEADERS)).thenReturn(new JMSMessageHeadersType());
        when(messageContext.get(JMSConstants.JMS_REQUEST_MESSAGE)).thenReturn(jmsMessage);
        testling.notifyUpdateOrder(notifyUpdate);
        verify(atlasErrorHandlingService).handleError(any(HandleError.class));
    }
}
