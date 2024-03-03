/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.mnet.hurrican.atlas.simulator.service;

import static org.mockito.Mockito.*;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.atlas.simulator.AbstractSimulatorTestBuilder;
import de.mnet.hurrican.atlas.simulator.SimulatorVariableNames;
import de.mnet.hurrican.atlas.simulator.builder.MockTestBuilder;
import de.mnet.hurrican.atlas.simulator.ffm.FFMVersion;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AtlasTestBuilderServiceTest {

    private AtlasTestBuilderService testling = new AtlasTestBuilderService();

    @Mock
    private ApplicationContext applicationContext;

    @BeforeClass
    public void setupTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRun() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(SimulatorVariableNames.ORDER_ID.getName(), "1234569");
        parameters.put(SimulatorVariableNames.ESB_TRACKING_ID.getName(), "11111111");
        parameters.put(SimulatorVariableNames.INTERFACE_VERSION.getName(), FFMVersion.V1.name());

        when(applicationContext.getBean(TestContext.class)).thenReturn(new TestContext());

        AbstractSimulatorTestBuilder mockTestBuilder = new MockTestBuilder(applicationContext);
        testling.run(mockTestBuilder, parameters, applicationContext);

        Assert.assertEquals(mockTestBuilder.getOrderId(), "1234569");
        Assert.assertEquals(mockTestBuilder.getEsbTrackingId(), "11111111");
        Assert.assertEquals(mockTestBuilder.getInterfaceVersion(), FFMVersion.V1.getMajorVersion());

        verify(applicationContext).getBean(TestContext.class);
    }

    @Test
    public void testGetTestBuilderParameter() throws Exception {
        Assert.assertEquals(testling.getTestBuilderParameter().size(), 5L);
    }
}
