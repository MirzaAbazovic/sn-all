/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.wbci.citrus.actions;

import java.time.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Verifies that the sendAfter property of the Storno-Request is set and therefore the request is scheduled.
 *
 *
 */
public class VerifyStornoRequestScheduledAction extends AbstractRequestAction {

    private boolean expectedScheduled;
    private WbciDao wbciDao;

    public VerifyStornoRequestScheduledAction(WbciCommonService wbciCommonService, WbciDao wbciDao, boolean expectedScheduled) {
        super("verifyStornoRequestScheduled", wbciCommonService, RequestTyp.STR_AUFH_AUF);
        this.wbciDao = wbciDao;
        this.expectedScheduled = expectedScheduled;
    }

    @Override
    public void doExecute(TestContext context) {
        final StornoAnfrage stornoAnfrage = (StornoAnfrage) retrieve(context);
        if (expectedScheduled) {
            Assert.assertNull(stornoAnfrage.getProcessedAt());
            stornoAnfrage.setSendAfter(Date.from(ZonedDateTime.now().minusMinutes(1).toInstant()));
            wbciDao.store(stornoAnfrage);
        }
        else {
            Assert.assertNotNull(stornoAnfrage.getProcessedAt());
        }
    }

}
