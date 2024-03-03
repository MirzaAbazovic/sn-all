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
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the WbciGeschaeftsfall, with the help of the VorabstimmungsID and verifies that the Geschaeftsfall-Status
 * matches the expected status.
 *
 *
 */
public class VerifyGfStatusAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private WbciGeschaeftsfallStatus expectedStatus;

    /** Logger */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(VerifyGfStatusAction.class);

    /**
     * @param wbciCommonService
     * @param expectedStatus    the expected {@link de.mnet.wbci.model.WbciRequestStatus}
     */
    public VerifyGfStatusAction(WbciCommonService wbciCommonService, WbciGeschaeftsfallStatus expectedStatus) {
        super("verifyGFStatus");
        this.wbciCommonService = wbciCommonService;
        this.expectedStatus = expectedStatus;
    }

    @Override
    public void doExecute(TestContext context) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = getWbciGeschaeftsfall(context, wbciCommonService);
        LOGGER.info("Validating WBCI geschaeftsfall status");
        Assert.assertEquals(wbciGeschaeftsfall.getStatus(), expectedStatus);
        LOGGER.info(String.format("WBCI geschaeftsfall status '%s' - value OK", expectedStatus));
    }

}
