/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2006 09:10:26
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;
import java.net.*;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.TriggerListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.quartz.QuartzRMISchedulerClient;
import de.augustakom.hurrican.gui.tools.scheduler.controller.AKJobListener;
import de.augustakom.hurrican.gui.tools.scheduler.controller.AKSchedulerListener;
import de.augustakom.hurrican.gui.tools.scheduler.controller.AKTriggerListener;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.common.scheduler.IRMISchedulerConnect;


/**
 * Basis-Panel fuer Implementierungen, die eine Verbindung zum AK-Scheduler herstellen muessen.
 *
 *
 */
public abstract class AbstractSchedulerPanel extends AbstractServicePanel {

    private ClassPathXmlApplicationContext listenerAppContext = null;
    private QuartzRMISchedulerClient schedulerClient = null;
    private String schedulerName = null;
    private String schedulerHost = null;
    private String schedulerPort = null;
    private String hostName = null;

    public AbstractSchedulerPanel(String resource, LayoutManager layout) {
        super(resource, layout);
    }

    public AbstractSchedulerPanel(String resource) {
        super(resource);
    }

    /**
     * Stellt eine RMI-Verbindung zum Scheduler her.
     */
    protected void initSchedulerClient(String rmiHost, String rmiPort, String schedulerName) throws SchedulerException {
        this.schedulerName = schedulerName;
        this.schedulerHost = rmiHost;
        this.schedulerPort = rmiPort;
        connect();
    }

    /**
     * Stellt eine RMI-Verbindung zum Scheduler her.
     */
    protected void initSchedulerClient() throws ServiceNotFoundException, FindException, SchedulerException {
        RegistryService rs = getCCService(RegistryService.class);
        this.schedulerName = rs.getStringValue(RegistryService.REGID_SCHEDULER_NAME);
        this.schedulerHost = System.getProperty("hurricanweb.host");
        this.schedulerPort = System.getProperty("hurricanweb.rmi.port");
        connect();
    }

    protected void connect() throws SchedulerException {
        schedulerClient = new QuartzRMISchedulerClient();
        try {
            schedulerClient.connect2RMIScheduler(schedulerHost, schedulerPort, schedulerName);
        }
        catch (SchedulerException e) {
            schedulerClient = null;
            throw e;
        }
    }

    protected QuartzRMISchedulerClient getSchedulerClient() throws FindException, ServiceNotFoundException, SchedulerException {
        initSchedulerClient();
        return schedulerClient;
    }

    protected QuartzRMISchedulerClient getSchedulerClient(String rmiHost, String rmiPort, String schedulerName) throws SchedulerException {
        initSchedulerClient(rmiHost, rmiPort, schedulerName);
        return schedulerClient;
    }

    protected String getSchedulerName() {
        return schedulerName;
    }

    protected String getHostName() throws UnknownHostException {
        if (hostName == null) {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        return hostName;
    }

    protected void createListeners(String xmlAppContextFileName,
            SchedulerListener schedulerListener, JobListener jobListener,
            TriggerListener triggerListener) throws UnknownHostException {
        if (listenerAppContext != null) {
            listenerAppContext.destroy();
        }

        listenerAppContext = new ClassPathXmlApplicationContext(xmlAppContextFileName);

        listenerAppContext.getBean("rmiSchedulerListenerServer", AKSchedulerListener.class)
                .setListener(schedulerListener);
        listenerAppContext.getBean("rmiJobListenerServer", AKJobListener.class).setListener(
                jobListener);
        listenerAppContext.getBean("rmiTriggerListenerServer", AKTriggerListener.class).setListener(
                triggerListener);

        listenerAppContext.getBean("rmiSchedulerConnectClient", IRMISchedulerConnect.class)
                .onConnect(getHostName());
    }

    protected void removeListeners() {
        if (listenerAppContext != null) {
            listenerAppContext
                    .getBean("rmiSchedulerConnectClient", IRMISchedulerConnect.class)
                    .onDisconnect(hostName);
            listenerAppContext.destroy();
        }
    }
}
