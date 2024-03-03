/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2010 18:36:32
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;


/**
 * Wrapper Klasse zum Dispatchen der Scheduler Events auf Hurrican Seite.
 *
 *
 */
public class AKTriggerListener extends AKAbstractSchedulerListener implements TriggerListener {

    private TriggerListener listener = null;

    /**
     * @param listener the listener to set
     */
    public void setListener(TriggerListener listener) {
        this.listener = listener;
    }

    /* (non-Javadoc)
     * @see org.quartz.TriggerListener#getName()
     */
    @Override
    public String getName() {
        if (listener != null) {
            return listener.getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.quartz.TriggerListener#triggerFired(org.quartz.Trigger, org.quartz.JobExecutionContext)
     */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        callMethod(listener, "triggerFired",
                new Class<?>[] { Trigger.class, JobExecutionContext.class },
                new Object[] { trigger, context });
    }

    /* (non-Javadoc)
     * @see org.quartz.TriggerListener#vetoJobExecution(org.quartz.Trigger, org.quartz.JobExecutionContext)
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.quartz.TriggerListener#triggerMisfired(org.quartz.Trigger)
     */
    @Override
    public void triggerMisfired(Trigger trigger) {
        callMethod(listener, "triggerMisfired",
                new Class<?>[] { Trigger.class },
                new Object[] { trigger });
    }

    /* (non-Javadoc)
     * @see org.quartz.TriggerListener#triggerComplete(org.quartz.Trigger, org.quartz.JobExecutionContext, int)
     */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
            int triggerInstructionCode) {
        callMethod(listener, "triggerComplete",
                new Class<?>[] { Trigger.class, JobExecutionContext.class, int.class },
                new Object[] { trigger, context, triggerInstructionCode });
    }

}
