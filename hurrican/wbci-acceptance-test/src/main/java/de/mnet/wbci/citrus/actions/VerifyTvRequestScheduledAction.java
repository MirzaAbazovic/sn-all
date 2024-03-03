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
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Verifies that the sendAfter property of the TV-Request is set and therefore the request is scheduled.
 *
 *
 */
public class VerifyTvRequestScheduledAction extends AbstractRequestAction {

    private boolean expectedScheduled;
    private WbciDao wbciDao;

    public VerifyTvRequestScheduledAction(WbciCommonService wbciCommonService, WbciDao wbciDao, boolean expectedScheduled) {
        super("verifyTvRequestScheduled", wbciCommonService, RequestTyp.TV);
        this.wbciDao = wbciDao;
        this.expectedScheduled = expectedScheduled;
    }

    @Override
    public void doExecute(TestContext context) {
        final TerminverschiebungsAnfrage terminverschiebungsAnfrage = (TerminverschiebungsAnfrage) retrieve(context);
        if (expectedScheduled) {
            Assert.assertNull(terminverschiebungsAnfrage.getProcessedAt());
            terminverschiebungsAnfrage.setSendAfter(Date.from(ZonedDateTime.now().minusMinutes(1).toInstant()));
            wbciDao.store(terminverschiebungsAnfrage);
        }
        else {
            Assert.assertNotNull(terminverschiebungsAnfrage.getProcessedAt());
        }
    }

}
