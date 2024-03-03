/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.service.WbciAutomationService;

/**
 * Triggers the processing of the inbound STR-AUFH ERLM notifications.
 */
public class TriggerAutomatedStrAufhErlmProcessingTestAction extends AbstractWbciTestAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerAutomatedStrAufhErlmProcessingTestAction.class);

    private WbciAutomationService wbciAutomationService;

    public TriggerAutomatedStrAufhErlmProcessingTestAction(WbciAutomationService wbciAutomationService) {
        super("triggerAutomatedStrAufhErlmProcessing");
        this.wbciAutomationService = wbciAutomationService;
    }

    @Override
    public void doExecute(TestContext context) {
        new SimpleAsyncTaskExecutor().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        AKUser user = getCitrusAkUser();
                        final Collection<String> vaIds = wbciAutomationService.processAutomatableStrAufhErlms(user);
                        LOGGER.info(String.format("Automatically processed the following ERLM-TVs: %s", vaIds));
                    }
                }
        );
    }
    
}
