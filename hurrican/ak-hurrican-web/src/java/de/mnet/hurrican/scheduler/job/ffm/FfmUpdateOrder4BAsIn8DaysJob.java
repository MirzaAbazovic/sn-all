/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2015
 */
package de.mnet.hurrican.scheduler.job.ffm;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.mnet.common.tools.DateConverterUtils;

/**
 * Job ermittelt alle Bauauftraege in acht Tagen und triggert fuer diese ein FFM Update.
 */
public class FfmUpdateOrder4BAsIn8DaysJob extends FfmUpdateOrder4BAsJob {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        final Date baDate = DateConverterUtils.asDate(LocalDate.now().plus(8, ChronoUnit.DAYS));
        executeForDate(context, baDate);
    }
}
