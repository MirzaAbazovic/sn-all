/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 25.07.2014 
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import de.mnet.wbci.service.WbciAutomationService;

/**
 * Triggers the processing of the inbound RUEM-VA notifications.
 */
public class TriggerAutomatedRuemVaProcessingTestAction extends AbstractWbciTestAction {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerAutomatedRuemVaProcessingTestAction.class);

    private WbciAutomationService wbciAutomationService;

    public TriggerAutomatedRuemVaProcessingTestAction(WbciAutomationService wbciAutomationService) {
        super("triggerAutomatedProcessRuemVa");
        this.wbciAutomationService = wbciAutomationService;
    }

    @Override
    public void doExecute(TestContext context) {
        new SimpleAsyncTaskExecutor().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        final Collection<String> vaIds = wbciAutomationService.processAutomatableRuemVas(getCitrusAkUser());
                        LOGGER.info(String.format("Automatically processed the following RUEM-VAs: %s", vaIds));
                    }
                }
        );
    }

}