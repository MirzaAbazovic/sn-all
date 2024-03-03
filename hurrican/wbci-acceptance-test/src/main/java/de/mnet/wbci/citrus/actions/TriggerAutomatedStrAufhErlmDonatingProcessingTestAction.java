/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.citrus.WbciAcceptanceTestConstants;
import de.mnet.wbci.service.WbciAutomationDonatingService;

/**
 * Created by glinkjo on 01.12.14.
 */
public class TriggerAutomatedStrAufhErlmDonatingProcessingTestAction extends AbstractWbciTestAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerAutomatedStrAufhErlmProcessingTestAction.class);

    private WbciAutomationDonatingService wbciAutomationDonatingService;

    public TriggerAutomatedStrAufhErlmDonatingProcessingTestAction(WbciAutomationDonatingService wbciAutomationDonatingService) {
        super("triggerAutomatedStrAufhErlmDonatingProcessing");
        this.wbciAutomationDonatingService = wbciAutomationDonatingService;
    }

    @Override
    public void doExecute(TestContext context) {
        new SimpleAsyncTaskExecutor().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        AKUser user = getCitrusAkUser(WbciAcceptanceTestConstants.TEST_USER_ID);
                        final Collection<String> vaIds = wbciAutomationDonatingService
                                .processAutomatableStrAufhErlmsDonating(
                                        user, WbciAcceptanceTestConstants.SESSION_ID_TEST_USER);
                        LOGGER.info(String.format("Automatically processed the following ERLM-TVs: %s", vaIds));
                    }
                }
        );
    }

}
