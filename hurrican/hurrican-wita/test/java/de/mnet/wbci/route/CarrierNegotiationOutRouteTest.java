/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.10.13
 */
package de.mnet.wbci.route;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.exceptions.HurricanExceptionLogErrorHandler;
import de.mnet.common.route.ExtractExchangeOptionsProcessor;
import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wbci.route.handler.AfterWbciMessageSentHandler;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;
import de.mnet.wbci.route.processor.carriernegotiation.out.ConvertWbciMessageToCdmProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.out.CustomerServiceProtocolOutProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.out.EvaluateCdmVersionProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.out.WbciIoArchiveOutProcessor;

@ContextConfiguration({ "classpath:de/mnet/wbci/route/camel-test-context.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Test(groups = BaseTest.UNIT)
public class CarrierNegotiationOutRouteTest extends AbstractTestNGSpringContextTests {

    private static final String URI_DIRECT = "direct";
    private static final String URI_CARRIER_NEG_SERVICE = "carrierNegotiationService";
    private static final String URI_ATLAS_ESB_CARRIER_NEGOTIATION_OUT = "queue:atlasCdmQueue";

    @Produce(uri = URI_DIRECT + ":" + URI_CARRIER_NEG_SERVICE)
    private ProducerTemplate wbciCarrierNegotiationOutProducer;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ExtractExchangeOptionsProcessor extractExchangeOptionsProcessorMock;

    @Autowired
    private WbciIoArchiveOutProcessor wbciIoArchiveOutProcessorMock;

    @Autowired
    private AfterWbciMessageSentHandler afterWbciMessageSentHandlerMock;

    @Autowired
    private RouteConfigHelper routeConfigHelperMock;

    @Autowired
    private ConvertWbciMessageToCdmProcessor convertWbciMessageToCdmProcessorMock;

    @Autowired
    private EvaluateCdmVersionProcessor evaluateCdmVersionProcessorMock;

    @Autowired
    private CustomerServiceProtocolOutProcessor customerServiceProtocolOutProcessorMock;

    @Autowired
    private HurricanExceptionLogErrorHandler hurricanExceptionLogErrorHandlerMock;

    @Autowired
    private MessageProcessingMetadataHelper metadataHelperMock;

    @Mock
    private Processor outboundAtlasEsbProcessorMock;

    @Mock
    private Predicate isResponseToDuplicateVaRequestMock;

    @Mock
    private Predicate isPostProcessMessageMock;

    @Autowired
    @Qualifier("emsTransactionManager")
    private PlatformTransactionManager emsTransactionManagerMock;

    @Spy
    private SimpleTransactionStatus emsSimpleTransactionStatus;

    @Autowired
    @Qualifier("dbTransactionManager")
    private PlatformTransactionManager dbTransactionManagerMock;

    @Spy
    private SimpleTransactionStatus dbSimpleTransactionStatus;

    @Autowired
    private CarrierNegotiationOutRoute carrierNegotiationOutRoute;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
        setupCommonMockBehavior();
        loadAndStartCamelContext();
    }

    /**
     * Setup common mock behavior
     */
    private void setupCommonMockBehavior() throws Exception {
        when(routeConfigHelperMock.getAtlasOutComponent()).thenReturn(URI_DIRECT);
        when(dbTransactionManagerMock.getTransaction(any(TransactionDefinition.class))).thenReturn(dbSimpleTransactionStatus);
        when(emsTransactionManagerMock.getTransaction(any(TransactionDefinition.class))).thenReturn(emsSimpleTransactionStatus);
        when(metadataHelperMock.isResponseToDuplicateVaRequest()).thenReturn(isResponseToDuplicateVaRequestMock);
        when(metadataHelperMock.isPostProcessMessage()).thenReturn(isPostProcessMessageMock);
    }

    /**
     * Creates and starts the camel context.
     * <p/>
     * Make sure that the mocks are setup before doing this.
     *
     * @throws Exception
     */
    private void loadAndStartCamelContext() throws Exception {
        SpringCamelContext springCamelContext = applicationContext.getBean("camelContext", SpringCamelContext.class);
        springCamelContext.addRoutes(carrierNegotiationOutRoute);
        springCamelContext.addRoutes(createMockAtlasEsbCarrierNegotiationOutRoute());
        springCamelContext.start();
    }

    /**
     * This route consumes messages intended for the AtlasESB CarrierNegotationService. This route is required when
     * testing the carrier negotiation service behavior. If this route doesn't exist within the camel context and a
     * message is sent to the atlas esb carrier negotiation service, then camel will throw an exception.
     *
     * @return the AtlasESB carrier negotiation service mock consumer
     */
    public RouteBuilder createMockAtlasEsbCarrierNegotiationOutRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(URI_DIRECT + ":" + URI_ATLAS_ESB_CARRIER_NEGOTIATION_OUT).process(outboundAtlasEsbProcessorMock);
            }
        };
    }

    @Test
    public void testCarrierNegotiationServiceOut() throws Exception {
        testCarrierNegotiationServiceOut(true, false);
    }

    @Test
    public void testCarrierNegotiationServiceSendMessageRoute_withResponseToInvalidRequest() throws Exception {
        testCarrierNegotiationServiceOut(false, false);
    }

    @Test
    public void testCarrierNegotiationServiceSendMessageRoute_withResponseToDuplicateVaRequest() throws Exception {
        testCarrierNegotiationServiceOut(false, true);
    }

    private void testCarrierNegotiationServiceOut(boolean isPostProcessMessage, boolean isResponseToDuplicateVaRequest) throws Exception {
        when(isPostProcessMessageMock.matches(any(Exchange.class))).thenReturn(isPostProcessMessage);
        when(isResponseToDuplicateVaRequestMock.matches(any(Exchange.class))).thenReturn(isResponseToDuplicateVaRequest);

        wbciCarrierNegotiationOutProducer.sendBody("Some message");

        verify(evaluateCdmVersionProcessorMock).process(any(Exchange.class));
        verify(convertWbciMessageToCdmProcessorMock).process(any(Exchange.class));
        verify(outboundAtlasEsbProcessorMock).process(any(Exchange.class));

        if (!isPostProcessMessage) {
            verify(afterWbciMessageSentHandlerMock, never()).handleSuccessfulWbciRequest(any(Exchange.class));
            verify(customerServiceProtocolOutProcessorMock, never()).process(any(Exchange.class));
            if (isResponseToDuplicateVaRequest) {
                verify(wbciIoArchiveOutProcessorMock, never()).process(any(Exchange.class));
            }
            else {
                verify(wbciIoArchiveOutProcessorMock).process(any(Exchange.class));
            }
        }
        else {
            verify(afterWbciMessageSentHandlerMock).handleSuccessfulWbciRequest(any(Exchange.class));
            verify(wbciIoArchiveOutProcessorMock).process(any(Exchange.class));
            verify(customerServiceProtocolOutProcessorMock).process(any(Exchange.class));
        }

        // ensure that no exceptions were thrown
        verify(hurricanExceptionLogErrorHandlerMock, never()).handleError(any(Throwable.class));
    }

}
