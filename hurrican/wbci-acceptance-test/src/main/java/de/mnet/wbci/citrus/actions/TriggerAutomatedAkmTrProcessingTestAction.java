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

import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.wbci.service.WbciAutomationService;

/**
 * Triggers the processing of the outgoing AKM-TRs (creates WITA orders for the receiving lines).
 */
public class TriggerAutomatedAkmTrProcessingTestAction extends AbstractWbciTestAction {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerAutomatedAkmTrProcessingTestAction.class);

    private WbciAutomationService wbciAutomationService;

    public TriggerAutomatedAkmTrProcessingTestAction(WbciAutomationService wbciAutomationService) {
        super("triggerAutomatedAkmTrProcessing");
        this.wbciAutomationService = wbciAutomationService;
    }

    @Override
    public void doExecute(TestContext context) {
        new SimpleAsyncTaskExecutor().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        final Collection<String> witaVrtNr = wbciAutomationService.processAutomatableAkmTrs(getCitrusAkUser());
                        LOGGER.info(String.format("Automatically create the following wita-orders: %s",
                                CollectionTools.formatCommaSeparated(witaVrtNr)));
                    }
                }
        );
    }

}