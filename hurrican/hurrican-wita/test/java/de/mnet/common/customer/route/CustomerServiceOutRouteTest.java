/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.13
 */
package de.mnet.common.customer.route;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
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
import de.mnet.common.customer.route.processor.MarshalCustomerServiceMessageProcessor;
import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wbci.model.builder.cdm.customer.AddCommunicationTestBuilder;

@ContextConfiguration({ "classpath:de/mnet/common/customer/camel-test-context.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Test(groups = BaseTest.UNIT)
public class CustomerServiceOutRouteTest extends AbstractTestNGSpringContextTests {
    private static final String URI_DIRECT = "direct";
    private static final String URI_CUSTOMER_SVC = "customerService";
    private static final String URI_ATLAS_ESB_CUSTOMER_SERVICE_OUT = "customerServiceQueue";

    @Produce(uri = URI_DIRECT + ":" + URI_CUSTOMER_SVC)
    private ProducerTemplate customerServiceOutProducer;

    @Autowired
    private RouteConfigHelper routeConfigHelperMock;

    @Autowired
    private MarshalCustomerServiceMessageProcessor marshalCustomerServiceMessageProcessorMock;

    @Mock
    private Processor outboundAtlasEsbCustomerProcessorMock;

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
    private CustomerServiceOutRoute customerServiceOutRoute;

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
        when(routeConfigHelperMock.getCustomerServiceOut()).thenReturn(URI_ATLAS_ESB_CUSTOMER_SERVICE_OUT);
        when(dbTransactionManagerMock.getTransaction(any(TransactionDefinition.class))).thenReturn(dbSimpleTransactionStatus);
        when(emsTransactionManagerMock.getTransaction(any(TransactionDefinition.class))).thenReturn(emsSimpleTransactionStatus);
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
        springCamelContext.addRoutes(customerServiceOutRoute);
        springCamelContext.addRoutes(createMockAtlasEsbV1CustomerServiceRoute());
        springCamelContext.start();
    }

    /**
     * This route consumes messages intended for the AtlasESB CustomerService. This route is required when testing the
     * customer service behavior. If this route doesn't exist within the camel context and a message is sent to the
     * atlas esb customer service, then camel will throw an exception.
     *
     * @return the AtlasESB customer service mock consumer
     */
    public RouteBuilder createMockAtlasEsbV1CustomerServiceRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(URI_DIRECT + ":" + URI_ATLAS_ESB_CUSTOMER_SERVICE_OUT).process(outboundAtlasEsbCustomerProcessorMock);
            }
        };
    }

    @Test
    public void testCustomerServiceOut() throws Exception {
        customerServiceOutProducer.sendBody(new AddCommunicationTestBuilder().buildValid());

        verify(marshalCustomerServiceMessageProcessorMock).process(any(Exchange.class));
        verify(outboundAtlasEsbCustomerProcessorMock).process(any(Exchange.class));
    }
}
