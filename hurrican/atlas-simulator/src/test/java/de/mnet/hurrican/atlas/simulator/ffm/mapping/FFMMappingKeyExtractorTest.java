/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm.mapping;

import static org.mockito.Mockito.*;

import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.xml.namespace.SimpleNamespaceContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class FFMMappingKeyExtractorTest {

    @InjectMocks
    private FFMMappingKeyExtractor testling;

    @Mock
    private NamespaceContextBuilder nsContextBuilder;

    @BeforeMethod
    public void setupTest() throws Exception {
        testling = new FFMMappingKeyExtractor();
        MockitoAnnotations.initMocks(this);
        testling.afterPropertiesSet();
    }

    @Test
    public void testGetMappingKeyFromRootQname() throws Exception {
        Message request = new DefaultMessage("<wfs:createOrder xmlns:wfs=\"http://m-net.de\"></wfs:createOrder>");
        Assert.assertEquals(testling.getMappingKey(request), "createOrder");

        request = new DefaultMessage("<wfs:deleteOrder xmlns:wfs=\"http://m-net.de\"></wfs:deleteOrder>");
        Assert.assertEquals(testling.getMappingKey(request), "deleteOrder");
    }

    @Test
    public void testGetMappingKeyFromOrderDetails() throws Exception {
        Message request = new DefaultMessage("<wfs:createOrder xmlns:wfs=\"http://m-net.de\"><wfs:order><wfs:description><wfs:details>MAPPING_KEY</wfs:details></wfs:description></wfs:order></wfs:createOrder>");

        SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();
        namespaceContext.bindNamespaceUri("wfs", "http://m-net.de");
        when(nsContextBuilder.buildContext(request, null)).thenReturn(namespaceContext);

        Assert.assertEquals(testling.getMappingKey(request), "MAPPING_KEY");

        verify(nsContextBuilder).buildContext(request, null);
    }
}
