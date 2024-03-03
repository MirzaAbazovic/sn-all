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
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.errorhandling.processor.GenerateErrorMessageProcessor;
import de.mnet.common.errorhandling.processor.MarshalErrorMessageProcessor;
import de.mnet.common.exceptions.HurricanExceptionLogErrorHandler;
import de.mnet.common.route.helper.ExceptionHelper;
import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;
import de.mnet.wbci.route.processor.carriernegotiation.in.ConvertCdmV1ToWbciFormatProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.CreateMessageProcessingMetadataProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.CustomerServiceProtocolInProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.PostProcessInProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.StoreWbciMessageProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.WbciIoArchiveInProcessor;

@ContextConfiguration({ "classpath:de/mnet/wbci/route/camel-test-context.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Test(groups = BaseTest.UNIT)
public class CarrierNegotiationInV1RouteTest extends AbstractTestNGSpringContextTests {

    private static final String URI_IN_COMPONENT = "direct";
    private static final String URI_CARRIER_NEG_SVC = "carrierNegotiationServiceV1In";
    private static final String URI_ERROR_SVC = "errorServiceV1Out";

    @Produce(uri = URI_IN_COMPONENT + ":" + URI_CARRIER_NEG_SVC)
    private ProducerTemplate wbciInProducer;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ConvertCdmV1ToWbciFormatProcessor convertCdmV1ToWbciFormatProcessorMock;

    @Autowired
    private RouteConfigHelper routeConfigHelperMock;

    @Autowired
    private WbciIoArchiveInProcessor wbciIoArchiveInProcessorMock;

    @Autowired
    private PostProcessInProcessor postProcessInProcessorMock;

    @Autowired
    private StoreWbciMessageProcessor storeWbciMessageProcessorMock;

    @Autowired
    private GenerateErrorMessageProcessor generateErrorMessageProcessorMock;

    @Autowired
    private MarshalErrorMessageProcessor marshalErrorMessageProcessorMock;

    @Autowired
    private CustomerServiceProtocolInProcessor customerServiceProtocolInProcessorMock;

    @Autowired
    private CreateMessageProcessingMetadataProcessor createMessageProcessingMetadataProcessor;

    @Autowired
    private MessageProcessingMetadataHelper metadataHelperMock;

    @Autowired
    private HurricanExceptionLogErrorHandler hurricanExceptionLogErrorHandlerMock;

    @Mock
    private Processor inboundErrorServiceProcessorMock;

    @Mock
    private Predicate isPostProcessMessageMock;

    @Mock
    private Predicate isIncomingMessageDuplicateVaRequestMock;

    @Autowired
    @Qualifier("emsTransactionManager")
    PlatformTransactionManager emsTransactionManagerMock;

    @Spy
    private SimpleTransactionStatus emsSimpleTransactionStatus;

    @Autowired
    @Qualifier("dbTransactionManager")
    PlatformTransactionManager dbTransactionManagerMock;

    @Spy
    private SimpleTransactionStatus dbSimpleTransactionStatus;

    @Autowired
    private CarrierNegotiationInV1Route carrierNegotiationInV1Route;

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
        when(routeConfigHelperMock.getAtlasInComponent()).thenReturn("direct");
        when(routeConfigHelperMock.getCarrierNegotiationServiceV1In()).thenReturn(URI_CARRIER_NEG_SVC);
        when(routeConfigHelperMock.getCarrierNegotiationServiceInParameters()).thenReturn("");
        when(routeConfigHelperMock.getErrorHandlingServiceOut()).thenReturn(URI_ERROR_SVC);
        when(dbTransactionManagerMock.getTransaction(any(TransactionDefinition.class))).thenReturn(dbSimpleTransactionStatus);
        when(emsTransactionManagerMock.getTransaction(any(TransactionDefinition.class))).thenReturn(emsSimpleTransactionStatus);
        when(metadataHelperMock.isPostProcessMessage()).thenReturn(isPostProcessMessageMock);
        when(metadataHelperMock.isIncomingMessageDuplicateVaRequest()).thenReturn(isIncomingMessageDuplicateVaRequestMock);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Exchange exchange = (Exchange) invocation.getArguments()[0];
                exchange.setProperty(ExceptionHelper.ERROR_SERVICE_MESSAGE_KEY, "");
                return null;
            }
        }).when(generateErrorMessageProcessorMock).process(any(Exchange.class));
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
        springCamelContext.addRoutes(carrierNegotiationInV1Route);
        springCamelContext.addRoutes(createMockAtlasEsbV1ErrorServiceRoute());
        springCamelContext.start();
    }

    /**
     * This route consumes messages intended for the AtlasESB ErrorService. This route is required when testing the
     * error service behavior. If this route doesn't exist within the camel context and a message is sent to the atlas
     * esb error service, then camel will throw an exception.
     *
     * @return the AtlasESB error service mock consumer
     */
    public RouteBuilder createMockAtlasEsbV1ErrorServiceRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(URI_IN_COMPONENT + ":" + URI_ERROR_SVC).process(inboundErrorServiceProcessorMock);
            }
        };
    }

    @Test
    public void inboundMessageShouldBeProcessesSuccessfully() throws Exception {
        successfullyProcessInboundMessage(true, false);
    }

    @Test
    public void inboundMessageShouldBeProcessesSuccessfullyWithPostProcessingSkipped() throws Exception {
        successfullyProcessInboundMessage(false, false);
    }

    private void successfullyProcessInboundMessage(boolean postProcessMessage, boolean simulateDuplicateVaRequest) throws Exception {
        when(isPostProcessMessageMock.matches(any(Exchange.class))).thenReturn(postProcessMessage);
        when(isIncomingMessageDuplicateVaRequestMock.matches(any(Exchange.class))).thenReturn(simulateDuplicateVaRequest);

        wbciInProducer.sendBody("Some message");

        verify(convertCdmV1ToWbciFormatProcessorMock).process(any(Exchange.class));
        verify(storeWbciMessageProcessorMock).process(any(Exchange.class));
        if (simulateDuplicateVaRequest) {
            verify(wbciIoArchiveInProcessorMock, never()).process(any(Exchange.class));
        }
        else {
            verify(wbciIoArchiveInProcessorMock).process(any(Exchange.class));
        }

        if (postProcessMessage) {
            verify(postProcessInProcessorMock).process(any(Exchange.class));
            verify(customerServiceProtocolInProcessorMock).process(any(Exchange.class));
        }
        else {
            verify(postProcessInProcessorMock, never()).process(any(Exchange.class));
            verify(customerServiceProtocolInProcessorMock, never()).process(any(Exchange.class));
        }

        // ensure both EMS transactions and the DB transaction are committed
        verify(emsTransactionManagerMock, times(2)).commit(any(TransactionStatus.class));
        verify(dbTransactionManagerMock).commit(any(TransactionStatus.class));

        // ensure that no exceptions were thrown
        verify(generateErrorMessageProcessorMock, never()).process(any(Exchange.class));
        verify(marshalErrorMessageProcessorMock, never()).process(any(Exchange.class));
        verify(hurricanExceptionLogErrorHandlerMock, never()).handleError(any(Throwable.class));
    }

    @Test
    public void inboundMessageCausesProcessorExceptionInBizRoute() throws Exception {
        doThrow(new RuntimeException("simulated processor ex")).when(storeWbciMessageProcessorMock).process(any(Exchange.class));

        wbciInProducer.sendBody("Some message");

        verify(convertCdmV1ToWbciFormatProcessorMock).process(any(Exchange.class));

        // ensure that the error handling processors were called and that a message was sent to the AtlasESB
        // error service
        verify(generateErrorMessageProcessorMock).process(any(Exchange.class));
        verify(marshalErrorMessageProcessorMock).process(any(Exchange.class));
        verify(inboundErrorServiceProcessorMock).process(any(Exchange.class));

        // ensure that the inner EMS and DB transactions were rolled back
        // but that the outer JMS transaction was committed
        verify(dbTransactionManagerMock).rollback(any(TransactionStatus.class));
        verify(emsTransactionManagerMock).rollback(any(TransactionStatus.class));
        verify(emsTransactionManagerMock).commit(any(TransactionStatus.class));
    }

    @Test
    public void inboundMessageCausesExceptionOnDbTxCommit() throws Exception {
        when(isPostProcessMessageMock.matches(any(Exchange.class))).thenReturn(Boolean.TRUE);
        when(isIncomingMessageDuplicateVaRequestMock.matches(any(Exchange.class))).thenReturn(Boolean.FALSE);

        doThrow(new RuntimeException("simulated db ex")).when(dbTransactionManagerMock).commit(any(TransactionStatus.class));

        wbciInProducer.sendBody("Some message");

        verify(convertCdmV1ToWbciFormatProcessorMock).process(any(Exchange.class));
        verify(storeWbciMessageProcessorMock).process(any(Exchange.class));
        verify(wbciIoArchiveInProcessorMock).process(any(Exchange.class));
        verify(postProcessInProcessorMock).process(any(Exchange.class));
        verify(customerServiceProtocolInProcessorMock).process(any(Exchange.class));

        // ensure that the error handling processors were called and that a message was sent to the AtlasESB
        // error service
        verify(generateErrorMessageProcessorMock).process(any(Exchange.class));
        verify(marshalErrorMessageProcessorMock).process(any(Exchange.class));
        verify(inboundErrorServiceProcessorMock).process(any(Exchange.class));

        // ensure that the inner EMS transaction was rolled back (after the DB transaction commit failed)
        // but that the outer JMS transaction was committed.
        verify(dbTransactionManagerMock).commit(any(TransactionStatus.class));
        verify(emsTransactionManagerMock).rollback(any(TransactionStatus.class));
        verify(emsTransactionManagerMock).commit(any(TransactionStatus.class));
    }

    @Test
    public void inboundMessageCausesExceptionOnFirstEmsTxCommit() throws Exception {
        when(isPostProcessMessageMock.matches(any(Exchange.class))).thenReturn(Boolean.TRUE);
        when(isIncomingMessageDuplicateVaRequestMock.matches(any(Exchange.class))).thenReturn(Boolean.FALSE);

        doAnswer(new Answer() {
            private int invoked = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (invoked == 0) {
                    invoked++;
                    throw new RuntimeException("simulated ems ex");
                }
                return null;
            }
        }).when(emsTransactionManagerMock).commit(any(TransactionStatus.class));

        wbciInProducer.sendBody("Some message");

        verify(convertCdmV1ToWbciFormatProcessorMock).process(any(Exchange.class));
        verify(storeWbciMessageProcessorMock).process(any(Exchange.class));
        verify(wbciIoArchiveInProcessorMock).process(any(Exchange.class));
        verify(postProcessInProcessorMock).process(any(Exchange.class));
        verify(customerServiceProtocolInProcessorMock).process(any(Exchange.class));

        // ensure that the error handling processors were called and that a message was sent to the AtlasESB
        // error service
        verify(generateErrorMessageProcessorMock).process(any(Exchange.class));
        verify(marshalErrorMessageProcessorMock).process(any(Exchange.class));
        verify(inboundErrorServiceProcessorMock).process(any(Exchange.class));

        // ensure that the outer JMS transaction was committed (even though the inner JMS transaction failed on commit)
        verify(dbTransactionManagerMock).commit(any(TransactionStatus.class));
        verify(emsTransactionManagerMock, times(2)).commit(any(TransactionStatus.class));
        verify(emsTransactionManagerMock, never()).rollback(any(TransactionStatus.class));
    }

    @Test
    public void inboundMessageCausesExceptionInMainRoute() throws Exception {
        ModelCamelContext modelCamelContext = (ModelCamelContext) camelContext;
        modelCamelContext.getRouteDefinition(carrierNegotiationInV1Route.getWbciInRouteId()).adviceWith(modelCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint(carrierNegotiationInV1Route.getWbciInEmsTxUri()).skipSendToOriginalEndpoint().process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw new RuntimeException("some exception thrown in main route");
                    }
                });
            }
        });

        wbciInProducer.sendBody("Some message");

        // ensure that the error handling processors were called and that a message was sent to the AtlasESB
        // error service
        verify(generateErrorMessageProcessorMock).process(any(Exchange.class));
        verify(marshalErrorMessageProcessorMock).process(any(Exchange.class));
        verify(inboundErrorServiceProcessorMock).process(any(Exchange.class));

        // ensure that the outer JMS transaction was committed (even though the inner JMS transaction failed on commit)
        verify(emsTransactionManagerMock).commit(any(TransactionStatus.class));
        verify(emsTransactionManagerMock, never()).rollback(any(TransactionStatus.class));
    }

}
