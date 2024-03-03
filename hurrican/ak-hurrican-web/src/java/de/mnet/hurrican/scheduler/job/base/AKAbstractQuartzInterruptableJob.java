/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2006 13:23:53
 */
package de.mnet.hurrican.scheduler.job.base;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

/**
 * Basis-Klasse fuer alle Jobs, deren Ausfuehrung unterbrochen werden muss. <br> In der Implementierung muss ueber die
 * Methode <code>isInterrupted</code> staendig abgefragt werden, ob die Ausfuehrung noch weiter laufen darf.
 *
 *
 */
public abstract class AKAbstractQuartzInterruptableJob extends AKAbstractQuartzJob implements InterruptableJob {

    private static final Logger LOGGER = Logger.getLogger(AKAbstractQuartzInterruptableJob.class);

    protected boolean interrupted = false;
    private JobExecutionContext jobExecutionContext;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        interrupted = false;
        this.jobExecutionContext = context;
    }

    /**
     * Gibt den JobExecutionContext des aktuellen Jobs zurueck.
     */
    protected JobExecutionContext getJobExecCtx() {
        return jobExecutionContext;
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        interrupted = true;
        handleInterrupt();
    }

    /**
     * Gibt an, ob die Job-Ausfuehrung abgebrochen werden soll.
     *
     * @return true wenn die Ausfuehrung abgebrochen werden soll/muss.
     */
    protected boolean isInterrupted() {
        if (interrupted) {
            LOGGER.info("Job is interrupted! Job-Name: " + jobExecutionContext.getJobDetail().getName());
        }
        return interrupted;
    }

    /**
     * Wird aufgerufen, wenn der Job abgebrochen wird. <br> In dieser Methode koennen die Implementierungen z.B.
     * Benachrichtigungen oder Aufraeumarbeiten durchfuehren.
     */
    protected abstract void handleInterrupt();
}
