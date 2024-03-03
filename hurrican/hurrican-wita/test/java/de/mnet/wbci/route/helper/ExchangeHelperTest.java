/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.wbci.route.helper;

import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;
import javax.jms.Queue;
import javax.xml.transform.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.bean.BeanInvocation;
import org.apache.camel.spi.UnitOfWork;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.util.ReflectionUtils;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.exceptions.helper.AtlasErrorMessageHelper;
import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wbci.integration.CarrierNegotationService;
import de.mnet.wbci.model.WbciMessage;

@Test(groups = BaseTest.UNIT)
public class ExchangeHelperTest {

    @InjectMocks
    private ExchangeHelper testling;

    @Mock
    protected Exchange exchangeMock;

    @Mock
    protected UnitOfWork unitOfWorkMock;

    @Mock
    private BeanInvocation beanInvocationMock;

    @Mock
    protected WbciMessage wbciMessageMock;

    @Mock
    protected Message messageMock;

    @Mock
    protected Queue queueMock;

    @Mock
    protected Map<String, Object> headersMock;

    @Mock
    private SoapMessageFactory soapMessageFactory;

    @Mock
    private SoapMessage soapMessage;

    @Mock
    private SoapBody soapBody;

    @Mock
    private Source sourceMock;

    @BeforeMethod
    public void setupMockListener() throws Exception {
        testling = new ExchangeHelper();
        MockitoAnnotations.initMocks(this);
    }

    private void setupExchangeMockWithOriginalInMessage() {
        when(exchangeMock.getUnitOfWork()).thenReturn(unitOfWorkMock);
        when(unitOfWorkMock.getOriginalInMessage()).thenReturn(messageMock);
    }

    private void setupMessageMockWithNoHeaders() {
        Map<String, Object> headers = new HashMap<>();
        setupMessageMockWithHeaders(headers);
    }

    private void setupMessageMockWithHeader(String key, Object value) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(key, value);
        setupMessageMockWithHeaders(headers);
    }

    private void setupMessageMockWithHeaders(Map<String, Object> headers) {
        when(messageMock.getHeaders()).thenReturn(headers);
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            when(messageMock.getHeader(entry.getKey())).thenReturn(entry.getValue());
        }
    }

    @Test
    public void testGetOriginalMessageFromOutMessage() throws Exception {
        when(exchangeMock.getUnitOfWork()).thenReturn(unitOfWorkMock);
        when(unitOfWorkMock.getOriginalInMessage()).thenReturn(messageMock);
        when(messageMock.getBody()).thenReturn(wbciMessageMock);

        Assert.assertEquals(testling.getOriginalMessageFromOutMessage(exchangeMock), wbciMessageMock);
    }

    @Test
    public void testGetOriginalMessageFromOutMessageBeanInvocation() throws Exception {
        when(exchangeMock.getUnitOfWork()).thenReturn(unitOfWorkMock);
        when(unitOfWorkMock.getOriginalInMessage()).thenReturn(messageMock);
        when(messageMock.getBody()).thenReturn(beanInvocationMock);
        when(beanInvocationMock.getMethod()).thenReturn(ReflectionUtils.getAllDeclaredMethods(CarrierNegotationService.class)[0]);
        when(beanInvocationMock.getArgs()).thenReturn(new Object[] { wbciMessageMock });

        Assert.assertEquals(testling.getOriginalMessageFromOutMessage(exchangeMock), wbciMessageMock);
    }

    @Test
    public void testGetOriginalMessageFromInMessage() throws Exception {
        when(exchangeMock.getIn()).thenReturn(messageMock);
        when(messageMock.getBody()).thenReturn(wbciMessageMock);

        Assert.assertEquals(testling.getOriginalMessageFromInMessage(exchangeMock), wbciMessageMock);
    }

    @Test
    public void testGetOriginalCdmPayloadFromInMessage() throws Exception {
        String body = "body";

        setupExchangeMockWithOriginalInMessage();
        when(messageMock.getBody()).thenReturn(body);

        Assert.assertEquals(testling.getOriginalCdmPayloadFromInMessage(exchangeMock), body);
    }

    @Test
    public void testGetOriginalCdmPayloadFromInMessageWithNullBody() throws Exception {
        setupExchangeMockWithOriginalInMessage();
        when(messageMock.getBody()).thenReturn(null);

        Assert.assertNull(testling.getOriginalCdmPayloadFromInMessage(exchangeMock));
    }

    @Test
    public void testGetOriginalInHeadersFromExchange() throws Exception {
        String key = "some key";
        String value = "some value";
        setupExchangeMockWithOriginalInMessage();
        setupMessageMockWithHeader(key, value);

        List<String[]> headerList = testling.getOriginalInHeadersFromExchange(exchangeMock);
        Assert.assertEquals(headerList.size(), 1);
        Assert.assertEquals(headerList.get(0)[0], key);
        Assert.assertEquals(headerList.get(0)[1], value);
    }

    @Test
    public void testGetOriginalInHeadersFromExchangeWithFilter() throws Exception {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        String[] filterOutHeaders = { key1 };
        Map<String, Object> headers = new HashMap<>();
        headers.put(key1, value1);
        headers.put(key2, value2);
        setupExchangeMockWithOriginalInMessage();
        setupMessageMockWithHeaders(headers);

        List<String[]> headerList = testling.getOriginalInHeadersFromExchange(exchangeMock, filterOutHeaders);
        Assert.assertEquals(headerList.size(), 1);
        Assert.assertEquals(headerList.get(0)[0], key2);
        Assert.assertEquals(headerList.get(0)[1], value2);
    }

    @Test
    public void testGetOriginalInHeadersFromExchangeForEmptyHeaders() throws Exception {
        setupExchangeMockWithOriginalInMessage();
        setupMessageMockWithNoHeaders();

        List<String[]> headerList = testling.getOriginalInHeadersFromExchange(exchangeMock);
        Assert.assertEquals(headerList.size(), 0);
    }

    @Test
    public void testGetHeaderPropertyValueAsString() throws Exception {
        Assert.assertNull(testling.getHeaderPropertyValueAsString(null));
        Assert.assertEquals(testling.getHeaderPropertyValueAsString("qqq"), "qqq");

        String queueName = "somequeue";
        when(queueMock.getQueueName()).thenReturn(queueName);
        Assert.assertEquals(testling.getHeaderPropertyValueAsString(queueMock), queueName);
    }

    @Test
    public void testGetOriginalJmsEndpointFromExchange() throws Exception {
        setupExchangeMockWithOriginalInMessage();
        setupMessageMockWithHeader(AtlasEsbConstants.JMS_DESTINATION_KEY, queueMock);

        String queueName = "somequeue";
        when(queueMock.getQueueName()).thenReturn(queueName);

        Assert.assertEquals(testling.getOriginalJmsEndpointFromExchange(exchangeMock), queueName);
    }

    @Test
    public void testGetOriginalEsbTrackingIdFromExchange() throws Exception {
        setupExchangeMockWithOriginalInMessage();
        String trackingId = "T123";
        setupMessageMockWithHeader(AtlasEsbConstants.ESB_TRACKING_ID_KEY, trackingId);
        Assert.assertEquals(testling.getOriginalEsbTrackingIdFromExchange(exchangeMock), trackingId);
    }

    @Test
    public void testGetOriginalEsbErrorIdFromExchange() throws Exception {
        setupExchangeMockWithOriginalInMessage();
        Long errorId = 1L;
        setupMessageMockWithHeader(AtlasEsbConstants.ESB_ERROR_ID_KEY, errorId);
        Assert.assertEquals(testling.getOriginalEsbErrorIdFromExchange(exchangeMock), errorId.toString());
    }

    @Test
    public void testGetOriginalEsbRetryCountFromExchange() throws Exception {
        setupExchangeMockWithOriginalInMessage();
        Long retryCount = 1L;
        setupMessageMockWithHeader(AtlasEsbConstants.ESB_RETRY_COUNT_KEY, retryCount);
        Assert.assertEquals(testling.getOriginalEsbRetryCountFromExchange(exchangeMock), retryCount.toString());
    }

    @Test
    public void testGetOriginalSoapActionFromExchange() throws Exception {
        setupExchangeMockWithOriginalInMessage();
        String soapAction = "";
        setupMessageMockWithHeader(AtlasEsbConstants.SOAPACTION_TIBCO, soapAction);
        Assert.assertEquals(testling.getOriginalSoapActionFromExchange(exchangeMock), soapAction);
    }

    @Test
    public void testExtractCdmServiceNameFromSoapAction() throws Exception {
        Assert.assertNull(AtlasErrorMessageHelper.extractCdmServiceNameFromSoapAction(null));
        Assert.assertEquals(AtlasErrorMessageHelper.extractCdmServiceNameFromSoapAction(""), null);
        Assert.assertEquals(AtlasErrorMessageHelper.extractCdmServiceNameFromSoapAction("/svc/op"), "svc");
        Assert.assertEquals(AtlasErrorMessageHelper.extractCdmServiceNameFromSoapAction("\"/svc/op\""), "svc");
    }

    @Test
    public void testExtractCdmOperationNameFromSoapAction() throws Exception {
        Assert.assertEquals(AtlasErrorMessageHelper.extractCdmOperationNameFromSoapAction("/svc/op"), "op");
        Assert.assertEquals(AtlasErrorMessageHelper.extractCdmOperationNameFromSoapAction("\"/svc/op\""), "op");
    }

    @Test
    public void testIsValueInList() {
        Assert.assertTrue(AtlasErrorMessageHelper.isValueInList(new String[] { "JMS(.*)" }, "JMSDeliveryMode"));
        Assert.assertTrue(AtlasErrorMessageHelper.isValueInList(new String[] { "JMSDeliveryMode" }, "JMSDeliveryMode"));
    }

    @Test
    public void testGetExchangeOptions() {
        Map options = new HashMap();
        options.put("a", "1");

        when(exchangeMock.getUnitOfWork()).thenReturn(unitOfWorkMock);
        when(unitOfWorkMock.getOriginalInMessage()).thenReturn(messageMock);
        when(messageMock.getBody()).thenReturn(beanInvocationMock);
        when(beanInvocationMock.getMethod()).thenReturn(ReflectionUtils.getAllDeclaredMethods(CarrierNegotationService.class)[0]);
        when(beanInvocationMock.getArgs()).thenReturn(new Object[] { wbciMessageMock, options });

        Assert.assertEquals(testling.getExchangeOptions(exchangeMock), options);
   }

    @Test
    public void testGetSoapPayloadFromExchange() throws IOException {
        when(exchangeMock.getIn()).thenReturn(messageMock);
        when(messageMock.getBody()).thenReturn(wbciMessageMock);

        Mockito.when(exchangeMock.getIn()).thenReturn(messageMock);
        Mockito.when(messageMock.getBody()).thenReturn("<xml>");
        Mockito.when(soapMessageFactory.createWebServiceMessage(Matchers.any(InputStream.class))).thenReturn(soapMessage);
        Mockito.when(soapMessage.getPayloadSource()).thenReturn(sourceMock);

        Assert.assertEquals(testling.getSoapPayloadFromExchange(exchangeMock, soapMessageFactory), sourceMock);
   }
}
