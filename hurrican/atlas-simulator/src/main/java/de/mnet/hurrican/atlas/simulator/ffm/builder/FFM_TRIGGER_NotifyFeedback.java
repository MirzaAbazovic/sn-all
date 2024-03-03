/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm.builder;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.hurrican.simulator.builder.UseCaseTrigger;

/**
 *
 */
@Component(FFM_TRIGGER_NotifyFeedback.ID)
@Scope("prototype")
public class FFM_TRIGGER_NotifyFeedback extends AbstractFFMTestBuilder implements UseCaseTrigger {
    /**
     * Trigger id used in HTML form GUI for conditional display of form fields
     */
    public static final String ID = "FFM_TRIGGER_NotifyFeedback";

    @Override
    public void configure() {
        name("NotifyFeedback (TRIGGER)");

        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                if (context.getVariables().containsKey("payload")) {
                    String payload = context.replaceDynamicContentInString(context.getVariable("payload"));
                    context.setVariable("payload", payload);
                }
            }
        });

        sendFeedbackNotification("dynamicPayload");
    }

    @Override
    public String getDisplayName() {
        return "NotifyFeedback";
    }

    @Override
    public boolean isDefault() {
        return true;
    }
}
