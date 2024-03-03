/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the WbciGeschaeftsfall, with the help of the VorabstimmungsID and verifies that the
 * Geschaeftsfall-Automatable flag matches the expected value.
 *
 *
 */
public class VerifyAutomatableAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private boolean expectedValue;

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyAutomatableAction.class);

    /**
     * @param wbciCommonService
     * @param expectedValue    the expected automatable value
     */
    public VerifyAutomatableAction(WbciCommonService wbciCommonService, boolean expectedValue) {
        super("verifyAutomatable");
        this.wbciCommonService = wbciCommonService;
        this.expectedValue = expectedValue;
    }

    @Override
    public void doExecute(TestContext context) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = getWbciGeschaeftsfall(context, wbciCommonService);
        LOGGER.info("Validating WBCI geschaeftsfall automatable value");
        Assert.assertEquals(wbciGeschaeftsfall.getAutomatable(), Boolean.valueOf(expectedValue));
        LOGGER.info(String.format("WBCI geschaeftsfall automatable value '%s' - value OK", expectedValue));
    }

}
