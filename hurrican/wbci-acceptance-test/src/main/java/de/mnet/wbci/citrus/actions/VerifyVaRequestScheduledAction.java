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
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the VA-Request, with the help of the VorabstimmungsID and verifies that the sendAfter property is set, this
 * would mean that the request is scheduled.
 *
 *
 */
public class VerifyVaRequestScheduledAction extends AbstractRequestAction {

    private boolean expectedScheduled;
    private WbciDao wbciDao;

    public VerifyVaRequestScheduledAction(WbciCommonService wbciCommonService, WbciDao wbciDao, boolean expectedScheduled) {
        super("verifyVaRequestScheduled", wbciCommonService, RequestTyp.VA);
        this.wbciDao = wbciDao;
        this.expectedScheduled = expectedScheduled;
    }

    @Override
    public void doExecute(TestContext context) {
        final VorabstimmungsAnfrage vorabstimmungsAnfrage = (VorabstimmungsAnfrage) retrieve(context);
        if (expectedScheduled) {
            Assert.assertNull(vorabstimmungsAnfrage.getProcessedAt());
            vorabstimmungsAnfrage.setSendAfter(Date.from(ZonedDateTime.now().minusMinutes(1).toInstant()));
            wbciDao.store(vorabstimmungsAnfrage);
        }
        else {
            Assert.assertNotNull(vorabstimmungsAnfrage.getProcessedAt());
        }
    }

}
