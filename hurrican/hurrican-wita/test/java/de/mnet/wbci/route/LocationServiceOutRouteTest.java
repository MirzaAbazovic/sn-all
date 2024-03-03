/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.13
 */
package de.mnet.wbci.route;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.apache.camel.CamelContext;
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
import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wbci.route.processor.location.MarshalLocationServiceProcessor;
import de.mnet.wbci.route.processor.location.UnmarshalLocationServiceProcessor;

@ContextConfiguration({ "classpath:de/mnet/wbci/route/camel-test-context.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Test(groups = BaseTest.UNIT)
public class LocationServiceOutRouteTest extends AbstractTestNGSpringContextTests {
    private static final String URI_DIRECT = "direct";
    private static final String URI_LOCATION_SERVICE = "locationService";
    private static final String URI_ATLAS_ESB_LOCATION_SERVICE_OUT = "locationServiceQueue";

    @Produce(uri = URI_DIRECT + ":" + URI_LOCATION_SERVICE)
    private ProducerTemplate wbciLocatoinServiceOutProducer;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private RouteConfigHelper routeConfigHelperMock;

    @Autowired
    private MarshalLocationServiceProcessor marshalLocationServiceProcessorMock;

    @Autowired
    private UnmarshalLocationServiceProcessor unmarshalLocationServiceProcessorMock;

    @Mock
    private Processor outboundAtlasEsbLocationProcessorMock;

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
    private LocationServiceOutRoute locationServiceOutRoute;

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
        when(routeConfigHelperMock.getLocationServiceOut()).thenReturn(URI_ATLAS_ESB_LOCATION_SERVICE_OUT);
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
        springCamelContext.addRoutes(locationServiceOutRoute);
        springCamelContext.addRoutes(createMockAtlasEsbLocationServiceOutRoute());
        springCamelContext.start();
    }

    /**
     * This route consumes messages intended for the AtlasESB LocationService. This route is required when testing the
     * location service behavior. If this route doesn't exist within the camel context and a message is sent to the
     * atlas esb location service, then camel will throw an exception.
     *
     * @return the AtlasESB location service mock consumer
     */
    public RouteBuilder createMockAtlasEsbLocationServiceOutRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(URI_DIRECT + ":" + URI_ATLAS_ESB_LOCATION_SERVICE_OUT).process(outboundAtlasEsbLocationProcessorMock);
            }
        };
    }


    @Test
    public void testLocationServiceOut() throws Exception {
        wbciLocatoinServiceOutProducer.sendBody("Some message");

        verify(marshalLocationServiceProcessorMock).process(any(Exchange.class));
        verify(outboundAtlasEsbLocationProcessorMock).process(any(Exchange.class));
        verify(unmarshalLocationServiceProcessorMock).process(any(Exchange.class));
    }
}
