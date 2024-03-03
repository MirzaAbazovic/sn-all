/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import de.mnet.wbci.citrus.WbciAcceptanceTestConstants;
import de.mnet.wbci.service.WbciAutomationDonatingService;

/**
 * Triggers the processing of the outbound RUEM-VA notifications.
 */
public class TriggerAutomatedOutgoingRuemVaProcessingTestAction extends AbstractWbciTestAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerAutomatedOutgoingRuemVaProcessingTestAction.class);

    private WbciAutomationDonatingService wbciAutomationDonatingService;

    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    public TriggerAutomatedOutgoingRuemVaProcessingTestAction(WbciAutomationDonatingService wbciAutomationDonatingService) {
        super("triggerAutomatedProcessOutgoingRuemVa");
        this.wbciAutomationDonatingService = wbciAutomationDonatingService;
    }

    @Override
    public void doExecute(TestContext context) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Collection<String> vaIds = wbciAutomationDonatingService.processAutomatableOutgoingRuemVas(
                        getCitrusAkUser(), WbciAcceptanceTestConstants.SESSION_ID_TEST_USER);
                LOGGER.info(String.format("Automatically processed the following outgoing RUEM-VAs: %s", vaIds));
            }
        });
    }

}
