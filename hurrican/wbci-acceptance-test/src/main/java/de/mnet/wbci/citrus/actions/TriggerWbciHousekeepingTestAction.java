/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 30.05.2014 
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Citrus test action for triggering the housekeeping job
 */
public class TriggerWbciHousekeepingTestAction extends AbstractWbciTestAction {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerWbciHousekeepingTestAction.class);

    private final WbciGeschaeftsfallService wbciGeschaeftsfallService;

    public TriggerWbciHousekeepingTestAction(WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super("triggerWbciHousekeeping");
        this.wbciGeschaeftsfallService = wbciGeschaeftsfallService;
        assert wbciGeschaeftsfallService != null;
    }

    @Override
    public void doExecute(TestContext context) {
        int preagreementsClosed = wbciGeschaeftsfallService.autoCompleteEligiblePreagreements();
        LOGGER.info(String.format("%s preagreements closed using housekeeping job", preagreementsClosed));

        int preagreementsUpdated = wbciGeschaeftsfallService.updateExpiredPreagreements();
        LOGGER.info(String.format("%s preagreements updated using housekeeping job", preagreementsUpdated));
    }

}