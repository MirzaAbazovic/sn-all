package de.mnet.wbci.citrus.actions;

import java.time.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciSchedulerService;

/**
 * Clears all requests scheduled to be sent. The scheduled request's <i>processedAt</i> attribute is set to now.
 * As a result the request is skipped, next time the scheduler is triggered. No requests are actually sent. They are
 * marked as sent so that when the scheduler is triggered later from another test, these requests will not interfere
 * with the test.
 */
public class ClearScheduledRequestsTestAction extends AbstractWbciTestAction {

    private WbciSchedulerService wbciSchedulerService;
    private WbciDao wbciDao;

    public ClearScheduledRequestsTestAction(WbciSchedulerService wbciSchedulerService, WbciDao wbciDao) {
        super("clearScheduledRequests");
        this.wbciSchedulerService = wbciSchedulerService;
        this.wbciDao = wbciDao;
    }

    @Override
    public void doExecute(TestContext context) {
        final List<Long> scheduledWbciRequestIds = wbciSchedulerService.findScheduledWbciRequestIds();
        for (Long requestId : scheduledWbciRequestIds) {
            WbciRequest req = wbciDao.findById(requestId, WbciRequest.class);
            req.setProcessedAt(new Date());
            wbciDao.store(req);
        }
        Assert.assertEquals(wbciDao.findScheduledWbciRequestIds().size(), 0);
    }

}
