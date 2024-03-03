/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2010 15:00:24
 */

package de.mnet.hurrican.scheduler.job.rmi;

import java.util.*;
import java.util.concurrent.locks.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.mnet.common.scheduler.IRMISchedulerConnect;
import de.mnet.hurrican.scheduler.HurricanScheduler;

/**
 * RMI Scheduler, der <code>connect/disconnect</code> Operationen der Hurrican Controller verarbeitet und die Scheduler
 * Events ueber RMI Proxies an die Controller verteilt.<br> <ul> <li><code>onConnect</code> Den Applikationskontext
 * (Proxies) erzeugen und unter <code>hostName</code> in Map speichern. <li><code>onDisconnect</code> Den
 * Applikationskontext (Proxies) abmelden und unter <code>hostName</code> aus der Map loeschen. </ul>
 *
 *
 */
public class RMISchedulerConnect implements IRMISchedulerConnect {

    private static final Logger LOGGER = Logger.getLogger(RMISchedulerConnect.class);

    private static final String RMI_JOB_LISTENER_CLIENT_NAME = "rmiJobListenerClient";
    private static final String RMI_TRIGGER_LISTENER_CLIENT_NAME = "rmiTriggerListenerClient";
    private static final String RMI_SCHEDULER_LISTENER_CLIENT_NAME = "rmiSchedulerListenerClient";
    private static final String JOB_URL_KEY = "de.mnet.hurrican.scheduler.job.rmi.jobURL";
    private static final String TRIGGER_URL_KEY = "de.mnet.hurrican.scheduler.job.rmi.triggerURL";
    private static final String SCHEDULER_URL_KEY = "de.mnet.hurrican.scheduler.job.rmi.schedulerURL";

    private JobListenerClient jobListenerClient;
    private final ReentrantLock lock = new ReentrantLock();
    private TriggerListenerClient triggerListenerClient;
    private SchedulerListenerClient schedulerListenerClient;
    private final Map<String, ClassPathXmlApplicationContext> clients = new HashMap<>();

    /**
     * Erstellt die vollstaendige Service URL aus <code>hostName</code> und <code>serviceName</code>.
     */
    private String getListenerServiceURL(String hostName, String serviceName) {
        return StringUtils.join(new String[] { "rmi://", hostName, ":2111/", serviceName });
    }

    /**
     * Schliesst den Applikationskontext und loescht diesen unter <code>hostName</code> aus der Map.
     */
    private void destroyAppContext(String hostName) {
        ClassPathXmlApplicationContext applicationContext = clients.get(hostName);
        if (applicationContext != null) {
            applicationContext.destroy();
        }
        clients.remove(hostName);
        removeListeners();
    }

    /**
     * Mehrere Applikationskontexte schliessen.
     */
    private void destroyAppContexts(Set<String> hostNames) {
        if (hostNames != null && !hostNames.isEmpty()) {
            for (String hostName : hostNames) {
                destroyAppContext(hostName);
            }
        }
    }

    /**
     * Alle lokalen Listener nach Bedarf erzeugen und beim Scheduler anmelden.
     */
    private void createListeners() throws SchedulerException {
        if (!clients.isEmpty()) {
            Scheduler scheduler = HurricanScheduler.getInstance().getBean("ak.scheduler",
                    Scheduler.class);

            if (schedulerListenerClient == null) {
                schedulerListenerClient = new SchedulerListenerClient();
                scheduler.addSchedulerListener(schedulerListenerClient);
            }

            if (jobListenerClient == null) {
                jobListenerClient = new JobListenerClient();
                scheduler.addGlobalJobListener(jobListenerClient);
            }

            if (triggerListenerClient == null) {
                triggerListenerClient = new TriggerListenerClient();
                scheduler.addGlobalTriggerListener(triggerListenerClient);
            }
        }
    }

    /**
     * Alle lokalen Listener nach Bedarf beim Scheduler abmelden.
     */
    private void removeListeners() {
        if (clients.isEmpty()) {
            Scheduler scheduler = HurricanScheduler.getInstance().getBean("ak.scheduler",
                    Scheduler.class);

            if (schedulerListenerClient != null) {
                try {
                    scheduler.removeSchedulerListener(schedulerListenerClient);
                }
                catch (Exception e) {
                    LOGGER.info(e.getMessage(), e);
                }
                schedulerListenerClient = null;
            }

            if (jobListenerClient != null) {
                try {
                    scheduler.removeGlobalJobListener(jobListenerClient.getName());
                }
                catch (Exception e) {
                    LOGGER.info(e.getMessage(), e);
                }
                jobListenerClient = null;
            }

            if (triggerListenerClient != null) {
                try {
                    scheduler.removeGlobalTriggerListener(triggerListenerClient.getName());
                }
                catch (Exception e) {
                    LOGGER.info(e.getMessage(), e);
                }
                triggerListenerClient = null;
            }
        }
    }

    /**
     * Den <code>connect</code> eines Hurrican Contollers verarbeiten.
     */
    @Override
    public void onConnect(String hostName) {
        lock.lock();
        try {
            destroyAppContext(hostName);

            String schedulerListenerServiceURL = getListenerServiceURL(hostName, "schedulerListener");
            String triggerListenerServiceURL = getListenerServiceURL(hostName, "triggerListener");
            String jobListenerServiceURL = getListenerServiceURL(hostName, "jobListener");

            // Inject URLs to application context via system property
            System.setProperty(SCHEDULER_URL_KEY, schedulerListenerServiceURL);
            System.setProperty(TRIGGER_URL_KEY, triggerListenerServiceURL);
            System.setProperty(JOB_URL_KEY, jobListenerServiceURL);

            ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                    "classpath:/de/mnet/hurrican/scheduler/job/resources/scheduler-controller-remote.xml");

            // Clear injected URLs
            System.clearProperty(JOB_URL_KEY);
            System.clearProperty(TRIGGER_URL_KEY);
            System.clearProperty(SCHEDULER_URL_KEY);

            clients.put(hostName, applicationContext);
            createListeners();
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Ein Hurrican Controller meldet sich ab.
     */
    @Override
    public void onDisconnect(String hostName) {
        lock.lock();
        try {
            destroyAppContext(hostName);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Wrapper Klasse zum delegieren der Scheduler Events an alle 'angeschlossenen' Hurrican Controller. Sollte ein
     * Controller nicht mehr erreichbar sein, wird der zugehoerige Applikationskontext geschlossen.<br> Der Code in den
     * Methoden ist nahezu identisch. Darum boete sich an, den redundanten Code mit Reflection Mechanismen zu
     * optimieren. Allerdings ist der von Spring erzeugte Listener selbst nur ein Proxy, so dass ein
     * <code>getMethod</code> fehlschlaegt. Workaround bisher nicht bekannt.
     */
    class SchedulerListenerClient implements SchedulerListener {

        @Override
        public void jobScheduled(Trigger trigger) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .jobScheduled(trigger);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void jobUnscheduled(String triggerName, String triggerGroup) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .jobUnscheduled(triggerName, triggerGroup);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void triggerFinalized(Trigger trigger) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .triggerFinalized(trigger);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void triggersPaused(String triggerName, String triggerGroup) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .triggersPaused(triggerName, triggerGroup);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void triggersResumed(String triggerName, String triggerGroup) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .triggersResumed(triggerName, triggerGroup);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void jobAdded(JobDetail jobDetail) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .jobAdded(jobDetail);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void jobDeleted(String jobName, String groupName) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .jobDeleted(jobName, groupName);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void jobsPaused(String jobName, String jobGroup) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .jobsPaused(jobName, jobGroup);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void jobsResumed(String jobName, String jobGroup) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .jobsResumed(jobName, jobGroup);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void schedulerError(String msg, SchedulerException cause) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .schedulerError(msg, cause);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void schedulerInStandbyMode() {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .schedulerInStandbyMode();
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void schedulerStarted() {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .schedulerStarted();
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void schedulerShutdown() {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue()
                                .getBean(RMI_SCHEDULER_LISTENER_CLIENT_NAME, SchedulerListener.class)
                                .schedulerShutdown();
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

    }


    /**
     * Wrapper Klasse zum delegieren der Job Events an alle 'angeschlossenen' Hurrican Controller. Sollte ein Controller
     * nicht mehr erreichbar sein, wird der zugehoerige Applikationskontext geschlossen.<br> Der Code in den Methoden
     * ist nahezu identisch. Darum boete sich an, den redundanten Code mit Reflection Mechanismen zu optimieren.
     * Allerdings ist der von Spring erzeugte Listener selbst nur ein Proxy, so dass ein <code>getMethod</code>
     * fehlschlaegt. Workaround bisher nicht bekannt.
     */
    class JobListenerClient implements JobListener {

        private static final String NAME = "JobListenerClient";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void jobToBeExecuted(JobExecutionContext context) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue().getBean(RMI_JOB_LISTENER_CLIENT_NAME, JobListener.class)
                                .jobToBeExecuted(context);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void jobExecutionVetoed(JobExecutionContext context) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue().getBean(RMI_JOB_LISTENER_CLIENT_NAME, JobListener.class)
                                .jobExecutionVetoed(context);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue().getBean(RMI_JOB_LISTENER_CLIENT_NAME, JobListener.class)
                                .jobWasExecuted(context, jobException);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

    }


    /**
     * Wrapper Klasse zum delegieren der Trigger Events an alle 'angeschlossenen' Hurrican Controller. Sollte ein
     * Controller nicht mehr erreichbar sein, wird der zugehoerige Applikationskontext geschlossen.<br> Der Code in den
     * Methoden ist nahezu identisch. Darum boete sich an, den redundanten Code mit Reflection Mechanismen zu
     * optimieren. Allerdings ist der von Spring erzeugte Listener selbst nur ein Proxy, so dass ein
     * <code>getMethod</code> fehlschlaegt. Workaround bisher nicht bekannt.
     */
    class TriggerListenerClient implements TriggerListener {

        private static final String NAME = "TriggerListenerClient";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void triggerFired(Trigger trigger, JobExecutionContext context) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue().getBean(RMI_TRIGGER_LISTENER_CLIENT_NAME, TriggerListener.class)
                                .triggerFired(trigger, context);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
            return false;
        }

        @Override
        public void triggerMisfired(Trigger trigger) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue().getBean(RMI_TRIGGER_LISTENER_CLIENT_NAME, TriggerListener.class)
                                .triggerMisfired(trigger);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void triggerComplete(Trigger trigger, JobExecutionContext context,
                int triggerInstructionCode) {
            lock.lock();
            try {
                Set<String> hostNames = new HashSet<>();
                for (Map.Entry<String, ClassPathXmlApplicationContext> client : clients.entrySet()) {
                    try {
                        client.getValue().getBean(RMI_TRIGGER_LISTENER_CLIENT_NAME, TriggerListener.class)
                                .triggerComplete(trigger, context, triggerInstructionCode);
                    }
                    catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                        hostNames.add(client.getKey());
                    }
                }

                destroyAppContexts(hostNames);
            }
            finally {
                lock.unlock();
            }
        }

    }

}
