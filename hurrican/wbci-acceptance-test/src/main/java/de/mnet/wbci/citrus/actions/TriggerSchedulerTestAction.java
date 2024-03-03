/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciSchedulerService;

/**
 * Test action which triggers all scheduled requests.
 *
 *
 */
public class TriggerSchedulerTestAction extends AbstractWbciTestAction {

    private WbciSchedulerService wbciSchedulerService;
    private WbciDao wbciDao;

    public TriggerSchedulerTestAction(WbciSchedulerService wbciSchedulerService, WbciDao wbciDao) {
        super("triggerScheduler");
        this.wbciSchedulerService = wbciSchedulerService;
        this.wbciDao = wbciDao;
    }

    @Override
    public void doExecute(TestContext context) {
        final List<Long> scheduledWbciRequestIds = wbciSchedulerService.findScheduledWbciRequestIds();
        for (Long requestId : scheduledWbciRequestIds) {
            wbciSchedulerService.sendScheduledRequest(requestId);
        }
        Assert.assertEquals(wbciDao.findScheduledWbciRequestIds().size(), 0);
        for (Long requestId : scheduledWbciRequestIds) {
            final WbciRequest byId = wbciDao.findById(requestId, WbciRequest.class);
            Assert.assertNotNull(byId.getSendAfter());
            Assert.assertNotNull(byId.getProcessedAt());
        }
    }

}
