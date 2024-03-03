/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the VA Request, with the help of the VorabstimmungsID and verifies that the request status matches the
 * expected status.
 */
public class VerifyVaRequestStatusAction extends AbstractRequestAction {

    private WbciRequestStatus expectedStatus;

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyVaRequestStatusAction.class);

    /**
     * @param wbciCommonService
     * @param expectedStatus    the expected {@link de.mnet.wbci.model.WbciRequestStatus}
     */
    public VerifyVaRequestStatusAction(WbciCommonService wbciCommonService, WbciRequestStatus expectedStatus) {
        super("verifyVaRequestStatus", wbciCommonService, RequestTyp.VA);
        this.expectedStatus = expectedStatus;
    }

    @Override
    public void doExecute(TestContext context) {
        final VorabstimmungsAnfrage vorabstimmungsAnfrage = (VorabstimmungsAnfrage) retrieve(context);
        LOGGER.info("Validating VA request status status");
        Assert.assertEquals(vorabstimmungsAnfrage.getRequestStatus(), expectedStatus);

        if (WbciRequestStatus.VA_VERSENDET == expectedStatus) {
            Assert.assertNotNull(vorabstimmungsAnfrage.getSendAfter());
            Assert.assertNotNull(vorabstimmungsAnfrage.getProcessedAt());
        }
        else if (WbciRequestStatus.VA_VORGEHALTEN == expectedStatus) {
            Assert.assertNotNull(vorabstimmungsAnfrage.getSendAfter());
            Assert.assertNull(vorabstimmungsAnfrage.getProcessedAt());
        }

        LOGGER.info(String.format("VA request status '%s' - value OK", expectedStatus));
    }

}
