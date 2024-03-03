/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.2007 15:53:54
 */
package de.mnet.hurrican.scheduler.job.errorhandler;

import org.quartz.JobExecutionContext;

import de.augustakom.common.tools.messages.AKWarnings;

/**
 * Abstrakte Implementierung eines Handlers für AK-Warnings
 *
 *
 */
public abstract class AbstractJobWarningsHandler extends AbstractJobHandler {

    /**
     * Methode, um Ak-Warnings zu behandeln.
     *
     * @param context  JobExecutionContext des Jobs, bei dem Warnings aufgetreten
     * @param warnings (optional) die aufgetretenen Warnings.
     */
    public abstract void handleWarnings(JobExecutionContext context, AKWarnings warnings);

}
