/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2008 11:48:27
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import com.google.common.base.Preconditions;
import org.quartz.JobDataMap;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.quartz.QuartzRMISchedulerClient;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.query.ResourcenMonitorQuery;
import de.augustakom.hurrican.service.cc.RegistryService;

/**
 * Abstrakte Implementierung fuer Panels vom Typ Ressourcen-Monitor.
 *
 *
 */
public abstract class AbstractMonitorPanel extends AbstractDataPanel {

    private ResourcenMonitorQuery query = new ResourcenMonitorQuery();

    /**
     * Konstruktor mit Angabe der Resource-Datei.
     *
     * @param resource
     */
    public AbstractMonitorPanel(String resource) {
        super(resource);
    }

    /**
     * Muss von den Ableitungen ueberschrieben werden, um den Resourcen-Monitor anzuzeigen. <br>
     */
    protected abstract void showRM();

    /**
     * Muss von den Ableitungen ueberschrieben werden, um den Resourcen-Monitor-Lauf zu starten. <br>
     */
    protected void startRMRun() {
        try {
            // Job-Namen ermitteln.
            RegistryService rs = getCCService(RegistryService.class);
            String rmJob = rs.getStringValue(RegistryService.REGID_SCHEDULER_RESSOURCEN_MONITOR_JOB);

            // Initialisiere Scheduler
            String schedulerName = rs.getStringValue(RegistryService.REGID_SCHEDULER_NAME);
            QuartzRMISchedulerClient schedulerClient = new QuartzRMISchedulerClient();

            String schedulerHost = System.getProperty("hurricanweb.host");
            String schedulerPort = System.getProperty("hurricanweb.rmi.port");
            schedulerClient.connect2RMIScheduler(schedulerHost, schedulerPort, schedulerName);

            // Pruefe ob Job bereits laeuft
            if (schedulerClient.isJobRunning(rmJob)) {
                throw new HurricanGUIException("Ressourcenmonitor wurde bereits gestartet.");
            }

            // Job-Parameter
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("monitor.type", getRMType());

            // Job triggern
            schedulerClient.triggerJobWithVolatileTrigger(rmJob, jobDataMap);
            MessageHelper.showInfoDialog(getMainFrame(), "Job wurde gestartet.", null, true);

            // TableModel leeren
            refresh();
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /**
     * Muss von den Ableitungen ueberschrieben werden und liefert den Typ des Resourcen-Monitors zurück. <br>
     */
    protected abstract Long getRMType();


    public void setQuery(ResourcenMonitorQuery query) {
        Preconditions.checkNotNull(query);
        this.query = query;
    }

    public ResourcenMonitorQuery getQuery() {
        return query;
    }

}


