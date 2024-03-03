/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2006 13:03:48
 */
package de.mnet.hurrican.scheduler.job.listener;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;


/**
 * Verhindert die gleichzeitige Ausfuehrung eines bestimmten Jobs. <br> Im Gegensatz zum Einsatz des Marker-Interfaces
 * <code>StatefulJob</code>, das auch eine mehrfache gleichzeitige Ausfuehrung eines Jobs verhindert wird durch diesen
 * Listener die Job-Ausfuehrung komplett verhindert. Bei StatefulJobs wird dagegen die Ausfuehrung nur so lange heraus
 * gezoegert, bis die vorherige Job-Instanz beendet wurde. <br> <br>
 *
 *
 */
public class VetoMultipleExecutionTriggerListener extends AbstractTriggerListener {

    private static final Logger LOGGER = Logger.getLogger(VetoMultipleExecutionTriggerListener.class);

    /**
     * Ueberprueft, ob der Listener den auszufuehrenden Job 'beachten' soll. <br> Ist dies der Fall, wird geprueft, ob
     * von diesem Job gerade eine Instanz ausgefuehrt wird und verhindert dies, wenn notwendig.
     *
     * @see org.quartz.TriggerListener#vetoJobExecution(org.quartz.Trigger, org.quartz.JobExecutionContext)
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        // Name des auszufuehrenden Jobs ermitteln und pruefen, ob der
        // Listener diesen Job ueberhaupt beachtet.
        String fullJobNameToCheck = getFullJobName();
        String fullJobName2BeExecuted = StringUtils.join(
                new Object[] { trigger.getJobGroup(), ".", trigger.getJobName() });
        boolean checkRunningJob = StringUtils.equals(fullJobNameToCheck, fullJobName2BeExecuted);
        if (checkRunningJob) {
            try {
                Scheduler scheduler = context.getScheduler();
                @SuppressWarnings("unchecked")
                List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
                if (jobs != null) {
                    for (JobExecutionContext ctx : jobs) {
                        JobDetail jd = ctx.getJobDetail();
                        boolean veto = StringUtils.equals(fullJobNameToCheck, jd.getFullName());
                        if (veto) {
                            LOGGER.warn("Veto execution of job: " + jd.getFullName());
                            return veto;
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                new LogDBJobErrorHandler().handleError(context, e, null);
                return true;
            }
        }
        return false;
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        // not used
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        // not used
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode) {
        // not used
    }

}


