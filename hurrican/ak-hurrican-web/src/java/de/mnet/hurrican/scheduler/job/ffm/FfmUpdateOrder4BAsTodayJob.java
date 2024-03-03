/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2015
 */
package de.mnet.hurrican.scheduler.job.ffm;

import java.util.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job ermittelt alle heutigen Bauauftraege und triggert fuer diese ein FFM Update.
 */
public class FfmUpdateOrder4BAsTodayJob extends FfmUpdateOrder4BAsJob {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        final Date baDate = new Date();
        executeForDate(context, baDate);
    }
}
