/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 16:13:55
 */
package de.mnet.hurrican.scheduler.service;

import java.util.*;
import org.quartz.JobExecutionContext;

import de.mnet.hurrican.scheduler.exceptions.AKSchedulerDeleteException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerStoreException;
import de.mnet.hurrican.scheduler.model.JobError;
import de.mnet.hurrican.scheduler.model.JobExecution;
import de.mnet.hurrican.scheduler.service.utils.SchedulerService;

/**
 * Service fuer die Verwaltung von Scheduler-Jobs.
 *
 *
 */
public interface SchedulerJobService extends SchedulerService {

    /**
     * Protokolliert die Durchfuehrung des Jobs mit dem Execution-Context <code>context</code>. Das
     * erstellte/gespeicherte Modell wird zurueck gegeben.
     *
     * @throws AKSchedulerStoreException wenn beim Protokollieren der Job-Ausfuehrung ein Fehler auftritt.
     */
    public JobExecution createJobExecution(JobExecutionContext context) throws AKSchedulerStoreException;

    /**
     * Markiert das angegebene JobExecution-Modell als beendet (das Property 'endTime' wird auf das aktuelle Datum
     * gesetzt). <br> Die Aenderung wird natuerlich in der DB gespeichert.
     *
     * @throws AKSchedulerStoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void setJobExecutionFinished(JobExecution job) throws AKSchedulerStoreException;

    /**
     * Protokolliert den Error <code>error</code> zu dem Job, der im <code>context</code> hinterlegt ist. ACHTUNG: wird
     * im <code>context</code> kein JobExecution-Modell gefunden, wird der Fehler nicht protokolliert!
     *
     * @param context JobExecutionContext des Jobs
     * @param error   zu protokollierender Fehler
     * @throws AKSchedulerStoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void addJobError(JobExecutionContext context, Throwable error) throws AKSchedulerStoreException;

    /**
     * Ermittelt alle gespeicherten Fehler zu einem bestimmten Job.
     *
     * @param context
     * @return Liste mit allen gespeicherten Fehlern zu dem Job, der ueber den JobExecutionContext definiert ist.
     * @throws AKSchedulerFindException
     */
    public List<JobError> getJobErrors(JobExecutionContext context) throws AKSchedulerFindException;

    /**
     * Ermittelt alle gespeicherten Fehler zu einem bestimmten Job.
     *
     * @return Liste mit allen gespeicherten Fehlern zu dem Job, der ueber die JobId definiert ist.
     * @throws AKSchedulerFindException
     */
    public List<JobError> getJobErrorsByJobId(Long jobId) throws AKSchedulerFindException;

    /**
     * Loescht alle 'alten' Job-Protokollierungen aus der Datenbank. <br> Eine Job-Protokollierung wird als 'alt'
     * angesehen, wenn die Start-Zeit des Jobs "heue - 1 Woche" ist.
     *
     * @return Anzahl der geloeschten Job-Protokollierungen
     * @throws AKSchedulerDeleteException wenn beim Loeschen ein Fehler auftritt.
     */
    public int deleteOldJobs() throws AKSchedulerDeleteException;

    /**
     * Liefert die letzte erfolgreiche Job-Ausführung
     */
    public JobExecution findLastSuccessfullJobExecution(String jobName) throws AKSchedulerFindException;
}
