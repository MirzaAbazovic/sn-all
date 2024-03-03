/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.hurrican.atlas.simulator.service;

import java.util.*;
import com.consol.citrus.functions.core.RandomStringFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import de.mnet.hurrican.atlas.simulator.AbstractSimulatorTestBuilder;
import de.mnet.hurrican.atlas.simulator.SimulatorVariableNames;
import de.mnet.hurrican.atlas.simulator.ffm.FFMVariableNames;
import de.mnet.hurrican.atlas.simulator.ffm.FFMVersion;
import de.mnet.hurrican.atlas.simulator.ffm.builder.FFM_TRIGGER_NotifyFeedback;
import de.mnet.hurrican.atlas.simulator.ffm.builder.FFM_TRIGGER_NotifyUpdate;
import de.mnet.hurrican.simulator.builder.TestBuilderParam;
import de.mnet.hurrican.simulator.service.TestBuilderService;

/**
 *
 */
public class AtlasTestBuilderService implements TestBuilderService<AbstractSimulatorTestBuilder> {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger("SimActivityLogger");

    @Override
    public void run(AbstractSimulatorTestBuilder testBuilder, Map<String, Object> parameter, ApplicationContext applicationContext) {
        LOG.info("Executing test builder: " + testBuilder.getClass().getName());

        testBuilder.setApplicationContext(applicationContext);
        String orderId = parameter.get(SimulatorVariableNames.ORDER_ID.getName()).toString();

        // if no custom orderId is set create new
        if (!StringUtils.hasText(orderId)) {
            orderId = UUID.randomUUID().toString();
            parameter.put(SimulatorVariableNames.ORDER_ID.getName(), orderId);
        }

        String esbTrackingId = parameter.get(SimulatorVariableNames.ESB_TRACKING_ID.getName()).toString();

        // if no custom trackingId is set create new
        if (!StringUtils.hasText(esbTrackingId)) {
            esbTrackingId = new RandomStringFunction().execute(Collections.<String>singletonList("10"), null);
            parameter.put(SimulatorVariableNames.ESB_TRACKING_ID.getName(), esbTrackingId);
        }

        testBuilder.setEsbTrackingId(esbTrackingId);
        testBuilder.setOrderId(orderId);
        testBuilder.setInterfaceVersion(FFMVersion.valueOf(parameter.get(SimulatorVariableNames.INTERFACE_VERSION.getName()).toString()).getMajorVersion());

        testBuilder.setTestBuilderParameter(parameter);
        testBuilder.execute();
    }

    @Override
    public List<TestBuilderParam> getTestBuilderParameter() {
        List<TestBuilderParam> parameter = new ArrayList<>();

        parameter.add(new TestBuilderParam(SimulatorVariableNames.INTERFACE_VERSION.getName(), "InterfaceVersion", FFMVersion.V1.name()));
        parameter.add(new TestBuilderParam(SimulatorVariableNames.ESB_TRACKING_ID.getName(), "ESB_TrackingId", new RandomStringFunction().execute(Collections.singletonList("10"), null)));
        parameter.add(new TestBuilderParam(SimulatorVariableNames.ORDER_ID.getName(), "OrderId", UUID.randomUUID().toString()));

        parameter.add(new TestBuilderParam(FFMVariableNames.STATE.getName(), "State", "NEW", Arrays.asList("NEW", "ON_SITE", "DONE", "CUST", "TNFE"))
                .addUseCaseFilter(FFM_TRIGGER_NotifyUpdate.class));

        parameter.add(new TestBuilderParam(FFMVariableNames.FEEDBACK_TEXT.getName(), "FeedbackText", "News from FFM")
                .addUseCaseFilter(FFM_TRIGGER_NotifyFeedback.class));

        return parameter;
    }
}
