package de.mnet.hurrican.scheduler.job.statistics;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.hurrican.service.cc.PortStatisticService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

public class PortUsageStatisticsGatheringJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(PortUsageStatisticsGatheringJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            PortStatisticService statsService = getCCService(PortStatisticService.class);
            statsService.generatePortUsageStatistics();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            new LogDBJobErrorHandler().handleError(context, ex, null);
            new SendMailJobErrorHandler().handleError(context, ex, null);
        }
    }

}
