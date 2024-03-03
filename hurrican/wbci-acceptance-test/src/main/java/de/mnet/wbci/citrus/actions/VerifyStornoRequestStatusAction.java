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
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the Storno Request, with the help of the VorabstimmungsID and ChangeId and verifies that the request status
 * matches the expected status.
 */
public class VerifyStornoRequestStatusAction extends AbstractRequestAction {

    private WbciRequestStatus expectedStatus;

    /**
     * @param wbciCommonService
     * @param expectedStatus    the expected {@link de.mnet.wbci.model.WbciRequestStatus}
     */
    public VerifyStornoRequestStatusAction(WbciCommonService wbciCommonService, WbciRequestStatus expectedStatus) {
        super("verifyStornoRequestStatus", wbciCommonService, RequestTyp.STR_AUFH_AUF);
        this.expectedStatus = expectedStatus;
    }

    @Override
    public void doExecute(TestContext context) {
        final StornoAnfrage stornoAnfrage = (StornoAnfrage) retrieve(context);
        Assert.assertEquals(stornoAnfrage.getRequestStatus(), expectedStatus);
        if (WbciRequestStatus.STORNO_VERSENDET == expectedStatus) {
            Assert.assertNotNull(stornoAnfrage.getSendAfter());
            Assert.assertNotNull(stornoAnfrage.getProcessedAt());
        }
        else if (WbciRequestStatus.STORNO_VORGEHALTEN == expectedStatus) {
            Assert.assertNotNull(stornoAnfrage.getSendAfter());
            Assert.assertNull(stornoAnfrage.getProcessedAt());
        }
    }

}
