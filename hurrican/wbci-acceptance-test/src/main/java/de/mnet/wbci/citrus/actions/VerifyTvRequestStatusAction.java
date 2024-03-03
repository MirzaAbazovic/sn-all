/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the TV Request, with the help of the VorabstimmungsID and ChangeId and verifies that the request status
 * matches the expected status.
 */
public class VerifyTvRequestStatusAction extends AbstractRequestAction {

    private WbciRequestStatus expectedStatus;

    /**
     * @param wbciCommonService
     * @param expectedStatus    the expected {@link de.mnet.wbci.model.WbciRequestStatus}
     */
    public VerifyTvRequestStatusAction(WbciCommonService wbciCommonService, WbciRequestStatus expectedStatus) {
        super("verifyTvRequestStatus", wbciCommonService, RequestTyp.TV);
        this.expectedStatus = expectedStatus;
    }

    @Override
    public void doExecute(TestContext context) {
        final TerminverschiebungsAnfrage terminverschiebungsAnfrage = (TerminverschiebungsAnfrage) retrieve(context);
        Assert.assertEquals(terminverschiebungsAnfrage.getRequestStatus(), expectedStatus);
        if (WbciRequestStatus.TV_VERSENDET == expectedStatus) {
            Assert.assertNotNull(terminverschiebungsAnfrage.getSendAfter());
            Assert.assertNotNull(terminverschiebungsAnfrage.getProcessedAt());
        }
        else if (WbciRequestStatus.TV_VORGEHALTEN == expectedStatus) {
            Assert.assertNotNull(terminverschiebungsAnfrage.getSendAfter());
            Assert.assertNull(terminverschiebungsAnfrage.getProcessedAt());
        }
    }

}
