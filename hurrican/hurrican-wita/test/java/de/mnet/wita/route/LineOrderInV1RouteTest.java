/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2014
 */
package de.mnet.wita.route;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
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

import de.mnet.common.errorhandling.processor.GenerateErrorMessageProcessor;
import de.mnet.common.errorhandling.processor.MarshalErrorMessageProcessor;
import de.mnet.common.exceptions.HurricanExceptionLogErrorHandler;
import de.mnet.common.route.helper.ExceptionHelper;
import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wita.route.processor.lineorder.in.ConvertCdmV1ToWitaFormatProcessor;
import de.mnet.wita.route.processor.lineorder.in.StoreWitaMessageInProcessor;
import de.mnet.wita.route.processor.lineorder.in.WitaIoArchiveInProcessor;

@SuppressWarnings("Duplicates")
@ContextConfiguration({ "classpath:de/mnet/wita/route/camel-test-context.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Test(groups = de.augustakom.common.BaseTest.UNIT)
public class LineOrderInV1RouteTest extends AbstractTestNGSpringContextTests {
    private static final String URI_IN_COMPONENT = "direct";
    private static final String URI_LINE_ORDER_SVC_V1 = "lineOrderServiceV1In";
    private static final String URI_ERROR_SVC_V1 = "errorServiceV1Out";

    @Produce(uri = URI_IN_COMPONENT + ":" + URI_LINE_ORDER_SVC_V1)
    private ProducerTemplate witaInProducer;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ConvertCdmV1ToWitaFormatProcessor convertCdmV1ToWitaFormatProcessor;

    @Autowired
    private RouteConfigHelper routeConfigHelperMock;

    @Autowired
    private WitaIoArchiveInProcessor witaIoArchiveInProcessor;

    @Autowired
    private StoreWitaMessageInProcessor storeWitaMessageInProcessor;

    @Autowired
    private GenerateErrorMessageProcessor generateErrorMessageProcessorMock;

    @Autowired
    private MarshalErrorMessageProcessor marshalErrorMessageProcessorMock;

    @Autowired
    private HurricanExceptionLogErrorHandler hurricanExceptionLogErrorHandlerMock;

    @Mock
    private Processor inboundErrorServiceProcessorMock;

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
    private LineOrderInV1Route lineOrderInV1Route;

    @Autowired
    private LineOrderInV2Route lineOrderInV2Route;

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
        when(routeConfigHelperMock.getLineOrderServiceV1In()).thenReturn(URI_LINE_ORDER_SVC_V1);
        when(routeConfigHelperMock.getLineOrderServiceInParameters()).thenReturn("");
        when(routeConfigHelperMock.getErrorHandlingServiceOut()).thenReturn(URI_ERROR_SVC_V1);
        when(dbTransactionManagerMock.getTransaction(any(TransactionDefinition.class))).thenReturn(dbSimpleTransactionStatus);
        when(emsTransactionManagerMock.getTransaction(any(TransactionDefinition.class))).thenReturn(emsSimpleTransactionStatus);

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
        springCamelContext.addRoutes(lineOrderInV1Route);
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
                from(URI_IN_COMPONENT + ":" + URI_ERROR_SVC_V1).process(inboundErrorServiceProcessorMock);
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
        witaInProducer.sendBody("Some message");

        verify(convertCdmV1ToWitaFormatProcessor).process(any(Exchange.class));
        verify(storeWitaMessageInProcessor).process(any(Exchange.class));
        verify(witaIoArchiveInProcessor).process(any(Exchange.class));

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
        doThrow(new RuntimeException("simulated processor ex")).when(storeWitaMessageInProcessor).process(any(Exchange.class));

        witaInProducer.sendBody("Some message");

        verify(convertCdmV1ToWitaFormatProcessor).process(any(Exchange.class));

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
        doThrow(new RuntimeException("simulated db ex")).when(dbTransactionManagerMock).commit(any(TransactionStatus.class));

        witaInProducer.sendBody("Some message");

        verify(convertCdmV1ToWitaFormatProcessor).process(any(Exchange.class));
        verify(storeWitaMessageInProcessor).process(any(Exchange.class));
        verify(witaIoArchiveInProcessor).process(any(Exchange.class));

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
    public void inboundMessageCausesExceptionInMainRoute() throws Exception {
        ModelCamelContext modelCamelContext = (ModelCamelContext) camelContext;
        modelCamelContext.getRouteDefinition(lineOrderInV1Route.getLineOrderInRouteId()).adviceWith(modelCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint(lineOrderInV1Route.getLineOrderInEmsTxUri()).skipSendToOriginalEndpoint().process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw new RuntimeException("some exception thrown in main route");
                    }
                });
            }
        });

        witaInProducer.sendBody("Some message");

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
