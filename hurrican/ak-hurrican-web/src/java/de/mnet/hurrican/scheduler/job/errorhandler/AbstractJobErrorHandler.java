/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 14:46:54
 */
package de.mnet.hurrican.scheduler.job.errorhandler;

import org.quartz.JobExecutionContext;


/**
 * Abstrakte Implementierung eines Error-Handlers.
 *
 *
 */
public abstract class AbstractJobErrorHandler extends AbstractJobHandler {

    /**
     * Methode, um einen Fehler waehrend einer Job-Execution zu behandeln.
     *
     * @param context JobExecutionContext des Jobs, bei dem ein Fehler auftritt.
     * @param error   (optional) der aufgetretene Fehler.
     * @param params  (optional) Parameter fuer Detail-Angaben.
     */
    public abstract void handleError(JobExecutionContext context, Throwable error, Object[] params);

}


