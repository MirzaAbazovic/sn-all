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
import org.springframework.core.task.TaskExecutor;

import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.wbci.citrus.WbciAcceptanceTestConstants;
import de.mnet.wbci.service.WbciAutomationService;

/**
 * Triggers the processing of the inbound AKM-TRs. <br />
 * Creates WITA cancellation orders for the lines that are not requested and updates the PKIauf in Taifun.
 */
public class TriggerAutomatedIncomingAkmTrProcessingTestAction extends AbstractWbciTestAction {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerAutomatedIncomingAkmTrProcessingTestAction.class);

    private WbciAutomationService wbciAutomationService;

    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    public TriggerAutomatedIncomingAkmTrProcessingTestAction(WbciAutomationService wbciAutomationService) {
        super("triggerAutomatedIncomingAkmTrProcessing");
        this.wbciAutomationService = wbciAutomationService;
    }

    @Override
    public void doExecute(TestContext context) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Collection<String> witaVrtNr = wbciAutomationService.processAutomatableIncomingAkmTrs(
                        getCitrusAkUser(WbciAcceptanceTestConstants.TEST_USER_ID));

                LOGGER.info(String.format("Automatically created the following wita-orders: %s",
                        CollectionTools.formatCommaSeparated(witaVrtNr)));
            }
        });


    }

}