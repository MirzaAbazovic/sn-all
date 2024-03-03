/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2010 12:50:37
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.quartz.QuartzRMISchedulerClient;
import de.augustakom.hurrican.gui.base.AbstractSchedulerPanel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.RegistryService;

/**
 * Das Controller Panel dient zum Anzeigen und Modifizieren der Scheduler Jobs und deren Trigger. Die Verbindung zum
 * Scheduler im Tomcat erfolgt ueber RMI. Die Listener Events sind ebenfalls ueber RMI realisiert. Die Events kommen
 * ueber einen separaten Thread vom Tomcat Server. Da einfache Synchronisierung mit Lock zu Deadlocks fuehrt, ist dieser
 * Punkt offen. Allerdings ist das Risiko bei nur einem offenem Controller eher gering.
 *
 *
 */
public class AKSchedulerControllerPanel extends AbstractSchedulerPanel implements
        AKDataLoaderComponent {

    private static final long serialVersionUID = -4075123105161236957L;

    private static final Logger LOGGER = Logger.getLogger(AKSchedulerControllerPanel.class);

    public final static String ID_WIDGET_SCHEDULER_STANDBY = "scheduler.standby";
    public final static String ID_WIDGET_JOB_PAUSE = "job.pause";
    public final static String ID_WIDGET_JOB_TRIGGER = "job.trigger";
    public final static String ID_WIDGET_TRIGGER_PAUSE = "trigger.pause";
    public final static String ID_WIDGET_TRIGGER_EDIT = "trigger.edit";
    public final static String ID_WIDGET_TRIGGER_ADD = "trigger.add";
    public final static String ID_WIDGET_TRIGGER_DELETE = "trigger.delete";

    private transient Scheduler scheduler = null;
    private AKJTable tbJobs = null;
    private AKJTable tbTriggers = null;
    private AKJTextField tfName = null;
    private AKJTextField tfHost = null;
    private AKJTextField tfPort = null;
    private AKJTextArea taSummary = null;
    private AKJCheckBox cbStandby = null;
    private AKJCheckBox cbPauseJob = null;
    private AKJButton bTriggerJob = null;
    private AKJCheckBox cbPauseTrigger = null;
    private AKJButton bEditTrigger = null;
    private AKJButton bAddTrigger = null;
    private AKJButton bDelTrigger = null;
    private transient ListSelectionListener tbJobsListSelectionListener = null;
    private transient ListSelectionListener tbTriggersListSelectionListener = null;
    private AKReflectionTableModel<AKSchedulerControllerJobModel> tbMdlJobs = null;
    private AKReflectionTableModel<AKSchedulerControllerTriggerModel> tbMdlTriggers = null;

    public AKSchedulerControllerPanel() {
        super("de/augustakom/hurrican/gui/tools/scheduler/resources/AKSchedulerControllerPanel.xml");
        createGUI();
        init();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblName = getSwingFactory().createLabel("scheduler.name", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblHost = getSwingFactory().createLabel("scheduler.host", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblPort = getSwingFactory().createLabel("scheduler.port", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblJobs = getSwingFactory().createLabel("jobs.caption", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblTriggers = getSwingFactory().createLabel("triggers.caption", SwingConstants.LEFT, Font.BOLD);

        tfName = getSwingFactory().createTextField("scheduler.name", false);
        tfHost = getSwingFactory().createTextField("scheduler.host", false);
        tfPort = getSwingFactory().createTextField("scheduler.port", false);

        cbStandby = getSwingFactory().createCheckBox(ID_WIDGET_SCHEDULER_STANDBY, getActionListener(), false);
        cbStandby.setEnabled(false);

        cbPauseJob = getSwingFactory().createCheckBox(ID_WIDGET_JOB_PAUSE, getActionListener(), false);
        cbPauseJob.setEnabled(false);
        bTriggerJob = getSwingFactory().createButton(ID_WIDGET_JOB_TRIGGER, getActionListener());
        bTriggerJob.setEnabled(false);

        cbPauseTrigger = getSwingFactory().createCheckBox(ID_WIDGET_TRIGGER_PAUSE, getActionListener(), false);
        cbPauseTrigger.setEnabled(false);
        bEditTrigger = getSwingFactory().createButton(ID_WIDGET_TRIGGER_EDIT, getActionListener());
        bEditTrigger.setEnabled(false);
        bAddTrigger = getSwingFactory().createButton(ID_WIDGET_TRIGGER_ADD, getActionListener());
        bAddTrigger.setEnabled(false);
        bDelTrigger = getSwingFactory().createButton(ID_WIDGET_TRIGGER_DELETE, getActionListener());
        bDelTrigger.setEnabled(false);

        taSummary = getSwingFactory().createTextArea("summary", false);
        taSummary.setWrapStyleWord(true);
        taSummary.setLineWrap(false);
        AKJScrollPane spSummary = new AKJScrollPane(taSummary);

        tbMdlJobs = new AKReflectionTableModel<>(
                new String[] { "Job-Name", "next fire time", "Execution Status", "Schedule Status", "Pause Status", "scheduled time",
                        "fired time", "durable", "stateful", "volatile" },
                new String[] { "fullJobName", "nextFireTime", "execStatus", "scheduleStatus", "pauseStatus", "scheduledFireTime",
                        "fireTime", "durable", "stateful", "volatile" },
                new Class[] { String.class, Date.class, String.class, String.class, String.class, Date.class,
                        Date.class, Boolean.class, Boolean.class, Boolean.class }
        );
        tbJobs = new AKJTable(tbMdlJobs, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION);
        tbJobs.attachSorter();
        tbJobs.fitTable(new int[] { 250, 110, 70, 70, 70, 110, 110, 30, 30, 30 });
        tbJobs.setDefaultRenderer(Date.class, new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME_LONG));
        tbJobsListSelectionListener = new JobsTableSelectionListener();
        tbJobs.getSelectionModel().addListSelectionListener(tbJobsListSelectionListener);
        AKJScrollPane spJobs = new AKJScrollPane(tbJobs);

        tbMdlTriggers = new AKReflectionTableModel<>(
                new String[] { "Trigger-Name", "next fire time", "Execution Status", "Pause Status", "previous fire time", "final fire time",
                        "start time", "end time", "volatile" },
                new String[] { "fullTriggerName", "nextFireTime", "execStatus", "pauseStatus", "previousFireTime", "finalFireTime",
                        "startTime", "endTime", "volatile" },
                new Class[] { String.class, Date.class, String.class, String.class, Date.class, Date.class,
                        Date.class, Date.class, Boolean.class }
        );
        tbTriggers = new AKJTable(tbMdlTriggers, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION);
        tbTriggers.attachSorter();
        tbTriggers.fitTable(new int[] { 250, 110, 70, 70, 110, 110, 110, 110, 30 });
        tbTriggers.setDefaultRenderer(Date.class, new DateTableCellRenderer(
                DateTools.PATTERN_DATE_TIME_LONG));
        tbTriggersListSelectionListener = new TriggersTableSelectionListener();
        tbTriggers.getSelectionModel().addListSelectionListener(tbTriggersListSelectionListener);
        AKJScrollPane spTriggers = new AKJScrollPane(tbTriggers);

        AKJPanel url = new AKJPanel(new GridBagLayout());
        url.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.url")));
        url.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        url.add(lblName, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        url.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        url.add(tfName, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        url.add(lblHost, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        url.add(tfHost, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        url.add(lblPort, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        url.add(tfPort, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        url.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 4, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel summary = new AKJPanel(new GridBagLayout());
        summary.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.summary")));
        summary.add(spSummary, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        AKJPanel schedulerPanel = new AKJPanel(new GridBagLayout());
        schedulerPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.scheduler")));
        schedulerPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        schedulerPanel.add(cbStandby, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        schedulerPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        schedulerPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel jobPanelLeft = new AKJPanel(new BorderLayout());
        jobPanelLeft.add(spJobs, BorderLayout.CENTER);

        AKJPanel jobPanelRight = new AKJPanel(new GridBagLayout());
        jobPanelRight.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        jobPanelRight.add(lblJobs, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        jobPanelRight.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        jobPanelRight.add(cbPauseJob, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        jobPanelRight.add(bTriggerJob, GBCFactory.createGBC(100, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        jobPanelRight.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel triggerPanelLeft = new AKJPanel(new BorderLayout());
        triggerPanelLeft.add(spTriggers, BorderLayout.CENTER);

        AKJPanel triggerPanelRight = new AKJPanel(new GridBagLayout());
        triggerPanelRight.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        triggerPanelRight.add(lblTriggers, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        triggerPanelRight.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        triggerPanelRight.add(cbPauseTrigger, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        triggerPanelRight.add(bEditTrigger, GBCFactory.createGBC(100, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        triggerPanelRight.add(bAddTrigger, GBCFactory.createGBC(100, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        triggerPanelRight.add(bDelTrigger, GBCFactory.createGBC(100, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        triggerPanelRight.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel jobPanel = new AKJPanel(new GridBagLayout());
        jobPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.table.jobs")));
        jobPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        jobPanel.add(jobPanelLeft, GBCFactory.createGBC(90, 100, 1, 0, 1, 1, GridBagConstraints.BOTH));
        jobPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        jobPanel.add(jobPanelRight, GBCFactory.createGBC(10, 100, 3, 0, 1, 1, GridBagConstraints.BOTH));
        jobPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        jobPanel.add(triggerPanelLeft, GBCFactory.createGBC(90, 50, 1, 1, 1, 1, GridBagConstraints.BOTH));
        jobPanel.add(triggerPanelRight, GBCFactory.createGBC(10, 50, 3, 1, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(url, GBCFactory.createGBC(20, 20, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(summary, GBCFactory.createGBC(70, 20, 1, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(schedulerPanel, GBCFactory.createGBC(10, 20, 2, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(jobPanel, GBCFactory.createGBC(100, 100, 0, 1, 3, 1, GridBagConstraints.BOTH));

        setPreferredSize(new Dimension(1000, 550));
    }

    /**
     * Init auf <lu> <li>Host, Port, Name</li> <li>Remote Scheduler</li> <li>Remote Listener</li> </lu><br><br> Daten in
     * Job Tabelle respektive Trigger Tabelle laden
     */
    private void init() {
        try {
            setTfName();
            tfHost.setText(System.getProperty("hurricanweb.host"));
            tfPort.setText(System.getProperty("hurricanweb.rmi.port"));

            QuartzRMISchedulerClient schedulerClient = getSchedulerClient(tfHost.getText(),
                    tfPort.getText(), tfName.getText());
            scheduler = schedulerClient.getScheduler();
            cbStandby.setEnabled(true);
            updateGUISchedulerMetaData();

            JobListenerClient jobListener = new JobListenerClient();
            TriggerListenerClient triggerListener = new TriggerListenerClient();
            SchedulerListenerClient schedulerListener = new SchedulerListenerClient();

            jobListener.setName(getHostName() + "/jobListener");
            triggerListener.setName(getHostName() + "triggerListener");
            createListeners(
                    "classpath:/de/augustakom/hurrican/gui/tools/scheduler/resources/scheduler-controller-remote.xml",
                    schedulerListener, jobListener, triggerListener);
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

        loadData();
    }

    private void setTfName() {
        try {
            RegistryService rs = getCCService(RegistryService.class);
            tfName.setText(rs.getStringValue(RegistryService.REGID_SCHEDULER_NAME));
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public final void loadData() {
        loadJobData();
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    @Override
    protected void execute(String command) {
        if (scheduler != null) {
            try {
                if (StringUtils.equals(command, ID_WIDGET_SCHEDULER_STANDBY)) {
                    standbyScheduler();
                }
                else if (StringUtils.equals(command, ID_WIDGET_JOB_PAUSE)) {
                    pauseJob();
                }
                else if (StringUtils.equals(command, ID_WIDGET_JOB_TRIGGER)) {
                    triggerJob();
                }
                else if (StringUtils.equals(command, ID_WIDGET_TRIGGER_PAUSE)) {
                    pauseTrigger();
                }
                else if (StringUtils.equals(command, ID_WIDGET_TRIGGER_EDIT)) {
                    rescheduleJob();
                }
                else if (StringUtils.equals(command, ID_WIDGET_TRIGGER_ADD)) {
                    scheduleJob();
                }
                else if (StringUtils.equals(command, ID_WIDGET_TRIGGER_DELETE)) {
                    unscheduleJob();
                }
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * Trigger aus selektiertem Job loeschen
     *
     * @throws SchedulerException
     */
    private void unscheduleJob() throws SchedulerException {
        AKSchedulerControllerTriggerModel trigger = getTriggerAtSelectedRow();
        if ((trigger != null) &&
                (MessageHelper.showConfirmDialog(getMainFrame(),
                        StringUtils.join(new String[] { "Soll der Trigger '", trigger.getFullTriggerName(),
                                "' endgueltig geloescht werden?" }),
                        "Trigger loeschen", JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION)) {
            scheduler.unscheduleJob(trigger.getTriggerName(), trigger.getGroupName());
        }
    }

    /**
     * Einen neuen Trigger in den selektierten Job einfuegen
     *
     * @throws SchedulerException
     * @throws ParseException
     */
    private void scheduleJob() throws SchedulerException, ParseException {
        AKSchedulerControllerJobModel jobModel = getJobAtSelectedRow();
        if (jobModel != null) {
            JobDetail job = scheduler.getJobDetail(jobModel.getJobName(), jobModel.getGroupName());
            CronTrigger cronTrigger = new CronTrigger("<please edit>", jobModel.getJobName(), "0 0 12 ? * *");

            cronTrigger.setVolatility(job.isVolatile());
            cronTrigger.setJobName(job.getName());
            cronTrigger.setJobGroup(job.getGroup());
            AKSchedulerControllerCronDialog dlg = new AKSchedulerControllerCronDialog();
            dlg.setCronTrigger(cronTrigger);

            DialogHelper.showDialog(getMainFrame(), dlg, true, true);

            if (dlg.isSaved()) {
                scheduler.scheduleJob(cronTrigger);
            }
        }
    }

    /**
     * Selektierten Trigger modifizieren
     *
     * @throws SchedulerException
     */
    private void rescheduleJob() throws SchedulerException {
        AKSchedulerControllerTriggerModel triggerModel = getTriggerAtSelectedRow();
        if (triggerModel != null) {
            Trigger trigger = scheduler.getTrigger(triggerModel.getTriggerName(), triggerModel.getGroupName());

            if (trigger instanceof CronTrigger) {
                AKSchedulerControllerCronDialog dlg = new AKSchedulerControllerCronDialog();
                dlg.setCronTrigger((CronTrigger) trigger);

                DialogHelper.showDialog(getMainFrame(), dlg, true, true);

                if (dlg.isSaved()) {
                    scheduler.rescheduleJob(triggerModel.getTriggerName(), triggerModel.getGroupName(), trigger);
                }
            }
        }
    }

    /**
     * Selektierten Trigger pausieren oder reaktivieren
     *
     * @throws SchedulerException
     */
    private void pauseTrigger() throws SchedulerException {
        AKSchedulerControllerTriggerModel triggerModel = getTriggerAtSelectedRow();
        if (triggerModel != null) {
            if (cbPauseTrigger.isSelected()) {
                scheduler.pauseTrigger(triggerModel.getTriggerName(), triggerModel.getGroupName());
            }
            else {
                scheduler.resumeTrigger(triggerModel.getTriggerName(), triggerModel.getGroupName());
            }
        }
    }

    /**
     * Selektierten Job einmalig und sofort ausfuehren (dazu baut der Scheduler automatisch einen neuen SimpleTrigger)
     *
     * @throws SchedulerException
     */
    private void triggerJob() throws SchedulerException {
        AKSchedulerControllerJobModel job = getJobAtSelectedRow();
        if (job != null
                && MessageHelper.showConfirmDialog(
                getMainFrame(),
                StringUtils.join(new String[] { "Soll der Job '", job.getFullJobName(), "' sofort und einmalig ausgefuehrt werden?" }),
                "Job ausfuehren", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (job.getVolatile()) {
                scheduler.triggerJobWithVolatileTrigger(job.getJobName(), job.getGroupName());
            }
            else {
                scheduler.triggerJob(job.getJobName(), job.getGroupName());
            }
        }
    }

    /**
     * Alle Trigger des selektierten Jobs auf Status 'Pause' setzen
     *
     * @throws SchedulerException
     */
    private void pauseJob() throws SchedulerException {
        AKSchedulerControllerJobModel jobModel = getJobAtSelectedRow();
        if (jobModel != null) {
            if (cbPauseJob.isSelected()) {
                scheduler.pauseJob(jobModel.getJobName(), jobModel.getGroupName());
            }
            else {
                scheduler.resumeJob(jobModel.getJobName(), jobModel.getGroupName());
            }
        }
    }

    /**
     * Scheduler pausieren oder reaktivieren
     *
     * @throws SchedulerException
     */
    private void standbyScheduler() throws SchedulerException {
        if (cbStandby.isSelected()) {
            scheduler.standby();
        }
        else {
            scheduler.start();
        }
    }

    /**
     * Job attribute, die der Scheduler nicht direkt uebermittelt, selber berechnen.
     */
    private AKJobAttributes calculateJobAttributes(Trigger[] triggers) {
        AKJobAttributes attributes = new AKJobAttributes();
        int countRunning = 0;
        for (Trigger trigger : triggers) {
            if (!isTriggerStatePaused(trigger) && (trigger.getFinalFireTime() == null)) {
                if (trigger.getNextFireTime() != null
                        && (attributes.nextFireTime == null || attributes.nextFireTime.compareTo(trigger.getNextFireTime()) > 0)) {
                    attributes.nextFireTime = trigger.getNextFireTime();
                }
                countRunning++;
            }
        }

        if (attributes.nextFireTime != null) {
            attributes.isScheduled = true;
        }

        if (countRunning == 0) {
            attributes.isPaused = true;
        }

        return attributes;
    }

    private void fillJobWithJobDetail(AKSchedulerControllerJobModel job, JobDetail jobDetail) {
        job.setFullJobName(jobDetail.getFullName());
        job.setDurable(jobDetail.isDurable());
        job.setStateful(jobDetail.isStateful());
        job.setVolatile(jobDetail.isVolatile());
    }

    private void fillJobWithJobExecutionContext(AKSchedulerControllerJobModel job,
            JobExecutionContext context) {
        job.setScheduledFireTime(context.getScheduledFireTime());
        job.setFireTime(context.getFireTime());
    }

    private void fillTriggerMdl(AKSchedulerControllerTriggerModel triggerMdl, Trigger trigger) {
        triggerMdl.setFullTriggerName(trigger.getFullName());
        triggerMdl.setStartTime(trigger.getStartTime());
        triggerMdl.setEndTime(trigger.getEndTime());
        triggerMdl.setPreviousFireTime(trigger.getPreviousFireTime());
        triggerMdl.setNextFireTime(trigger.getNextFireTime());
        triggerMdl.setFinalFireTime(trigger.getFinalFireTime());
        triggerMdl.setVolatile(trigger.isVolatile());

        if (isTriggerStatePaused(trigger)) {
            triggerMdl.setPauseStatus(AKSchedulerControllerTriggerModel.TRIGGER_STATUS_PAUSED);
        }
        else {
            triggerMdl.setPauseStatus(AKSchedulerControllerTriggerModel.TRIGGER_STATUS_RESUMED);
        }
    }

    /**
     * Job Tabelle nicht komplett neu laden, anstatt updaten und Selektion auffrischen. Diese Methode nur dann
     * verwenden, wenn Felder sich veraendert haben, aber die Struktur (Anzahl und Identitaet der Zeilen) gleich
     * bleibt.
     */
    private void fireJobDataChanged() {
        int rowJ = tbJobs.getSelectedRow();
        int rowT = tbTriggers.getSelectedRow();

        tbJobs.getSelectionModel().removeListSelectionListener(tbJobsListSelectionListener);
        tbTriggers.getSelectionModel().removeListSelectionListener(tbTriggersListSelectionListener);

        tbMdlJobs.fireTableDataChanged();
        if (rowJ >= 0) {
            tbJobs.getSelectionModel().setSelectionInterval(rowJ, rowJ);
            if (rowT >= 0) {
                tbTriggers.getSelectionModel().setSelectionInterval(rowT, rowT);
            }
        }

        tbJobs.getSelectionModel().addListSelectionListener(tbJobsListSelectionListener);
        tbTriggers.getSelectionModel().addListSelectionListener(tbTriggersListSelectionListener);
    }

    /**
     * Trigger Tabelle nicht komplett neu laden, anstatt updaten und Selektion auffrischen. Diese Methode nur dann
     * verwenden, wenn Felder sich veraendert haben, aber die Struktur (Anzahl und Identitaet der Zeilen) gleich
     * bleibt.
     */
    private void fireTriggerDataChanged() {
        int rowT = tbTriggers.getSelectedRow();

        tbTriggers.getSelectionModel().removeListSelectionListener(tbTriggersListSelectionListener);

        tbMdlTriggers.fireTableDataChanged();
        if (rowT >= 0) {
            tbTriggers.getSelectionModel().setSelectionInterval(rowT, rowT);
        }

        tbTriggers.getSelectionModel().addListSelectionListener(tbTriggersListSelectionListener);
    }

    @SuppressWarnings("unchecked")
    private AKSchedulerControllerJobModel getJobAtSelectedRow() {
        int row = tbJobs.getSelectedRow();
        if (row >= 0) {
            return ((AKMutableTableModel<AKSchedulerControllerJobModel>) tbJobs.getModel()).getDataAtRow(tbJobs.getSelectedRow());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private AKSchedulerControllerTriggerModel getTriggerAtSelectedRow() {
        int row = tbTriggers.getSelectedRow();
        if (row >= 0) {
            return ((AKMutableTableModel<AKSchedulerControllerTriggerModel>) tbTriggers.getModel()).getDataAtRow(tbTriggers.getSelectedRow());
        }
        return null;
    }

    private AKSchedulerControllerJobModel getJobData(String fullName) throws FindException {
        if (fullName != null) {
            for (int j = 0; j < tbMdlJobs.getRowCount(); j++) {
                AKSchedulerControllerJobModel job = tbMdlJobs.getDataAtRow(j);
                if ((job != null) && StringUtils.equals(fullName, job.getFullJobName())) { return job; }
            }
        }

        throw new FindException("Der Job mit dem Namen '" + fullName
                + "' konnte nicht in der Jobtabelle ermittelt werden.");
    }

    private AKSchedulerControllerTriggerModel getTriggerData(String fullName) throws FindException {
        if (fullName != null) {
            for (int j = 0; j < tbMdlTriggers.getRowCount(); j++) {
                AKSchedulerControllerTriggerModel trigger = tbMdlTriggers.getDataAtRow(j);
                if ((trigger != null) && StringUtils.equals(fullName, trigger.getFullTriggerName())) { return trigger; }
            }
        }

        throw new FindException("Der Trigger mit dem Namen '" + fullName
                + "' konnte nicht in der Triggertabelle ermittelt werden.");
    }

    private boolean isTriggerStatePaused(Trigger trigger) {
        if (scheduler != null) {
            try {
                if (scheduler.getTriggerState(trigger.getName(), trigger.getGroup()) == Trigger.STATE_PAUSED) {
                    return true;
                }
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }

        return false;
    }

    /**
     * Job Tabelle neu laden und laden der Daten in Trigger Tabelle ausloesen
     */
    private void loadJobData() {
        if (scheduler != null) {
            try {
                AKSchedulerControllerJobModel selectedJobModel = getJobAtSelectedRow();

                String[] groupNamesJobs = scheduler.getJobGroupNames();
                List<AKSchedulerControllerJobModel> jobs = new ArrayList<>();
                if (groupNamesJobs != null) {
                    for (String groupName : groupNamesJobs) {
                        String[] jobNames = scheduler.getJobNames(groupName);
                        if (jobNames != null) {
                            for (String jobName : jobNames) {
                                JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
                                AKSchedulerControllerJobModel job = new AKSchedulerControllerJobModel();
                                fillJobWithJobDetail(job, jobDetail);
                                updateJobAttributes(jobName, groupName, job);

                                jobs.add(job);
                            }
                        }
                    }
                }

                /**
                 * Update der Job und Trigger Tabellen zunaechst verhindern.
                 * Hintergrund: 'tbMdlJobs.setData' fuehrt zu einem changeValue
                 * Event im Selectionlistener. Dadurch werden Jobtabelle und
                 * Triggertabell neu berechnet. Allerdings ist die bestehende
                 * Selektion in Jobs noch nicht wiederhergestellt. Somit wird
                 * die Triggertabelle immer leer sein und eine evtl.
                 * eingestellte Selektion verloren gehen.
                 */
                tbJobs.getSelectionModel().removeListSelectionListener(tbJobsListSelectionListener);
                tbMdlJobs.setData(jobs);
                @SuppressWarnings("unchecked")
                List<JobExecutionContext> runningJobs = scheduler.getCurrentlyExecutingJobs();
                if ((runningJobs != null) && !runningJobs.isEmpty()) {
                    for (JobExecutionContext ctx : runningJobs) {
                        JobDetail jd = ctx.getJobDetail();

                        AKSchedulerControllerJobModel job = getJobData(jd.getFullName());
                        fillJobWithJobExecutionContext(job, ctx);
                    }
                    tbMdlJobs.fireTableDataChanged();
                }
                tbJobs.getSelectionModel().addListSelectionListener(tbJobsListSelectionListener);

                if ((selectedJobModel == null)
                        || (!selectJobAtRowByName(selectedJobModel.getFullJobName()))) {
                    tbMdlTriggers.setData(null);
                    updateGUIJobs();
                    updateGUITriggers();
                }
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);

                tbJobs.getSelectionModel().addListSelectionListener(tbJobsListSelectionListener);
                LOGGER.info(e.getMessage(), e);
                tbMdlJobs.setData(null);
                tbMdlTriggers.setData(null);
                updateGUIJobs();
                updateGUITriggers();
            }
        }
    }

    /**
     * Trigger Tabelle neu laden
     */
    private void loadTriggerData() {
        if (scheduler != null) {
            try {
                AKSchedulerControllerTriggerModel selectedTrigger = getTriggerAtSelectedRow();

                AKSchedulerControllerJobModel selectedJob = getJobAtSelectedRow();
                if ((scheduler != null) && (selectedJob != null)) {
                    List<AKSchedulerControllerTriggerModel> triggersMdl = new ArrayList<>();

                    Trigger[] triggers = scheduler.getTriggersOfJob(selectedJob.getJobName(),
                            selectedJob.getGroupName());
                    for (Trigger trigger : triggers) {
                        AKSchedulerControllerTriggerModel triggerMdl = new AKSchedulerControllerTriggerModel();
                        fillTriggerMdl(triggerMdl, trigger);
                        triggersMdl.add(triggerMdl);
                    }
                    tbMdlTriggers.setData(triggersMdl);

                    if ((selectedTrigger == null)
                            || (!selectTriggerAtRowByName(selectedTrigger.getFullTriggerName()))) {
                        updateGUITriggers();
                    }
                }
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
                tbMdlTriggers.setData(null);
                updateGUITriggers();
            }
        }
    }

    /**
     * Wenn das Frame geschlossen werden soll, die Remote Listener aufraeumen
     */
    public void onClose() {
        try {
            removeListeners();
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private boolean selectJobAtRowByName(String fullName) {
        int row = 0;
        for (AKSchedulerControllerJobModel job : tbMdlJobs.getData()) {
            if (StringUtils.equals(job.getFullJobName(), fullName)) {
                tbJobs.getSelectionModel().setSelectionInterval(row, row);
                return true;
            }
            row++;
        }
        return false;
    }

    private boolean selectTriggerAtRowByName(String fullName) {
        int row = 0;
        for (AKSchedulerControllerTriggerModel trigger : tbMdlTriggers.getData()) {
            if (StringUtils.equals(trigger.getFullTriggerName(), fullName)) {
                tbTriggers.getSelectionModel().setSelectionInterval(row, row);
                return true;
            }
            row++;
        }

        return false;
    }

    /**
     * GUI Elemente (widgets) nach Selektion anpassen
     */
    private void updateGUIJobs() {
        if (tbJobs.getSelectedRow() < 0) {
            cbPauseJob.setEnabled(false);
            bTriggerJob.setEnabled(false);
        }
        else {
            bTriggerJob.setEnabled(true);

            AKSchedulerControllerJobModel job = tbMdlJobs.getDataAtRow(tbJobs.getSelectedRow());
            if (job != null) {
                if (StringUtils.equals(job.getScheduleStatus(), AKSchedulerControllerJobModel.JOB_STATUS_UNSCHEDULED)
                        && StringUtils.equals(job.getPauseStatus(), "")) {
                    cbPauseJob.setEnabled(false);
                    cbPauseJob.setSelected(false);
                }
                else {
                    cbPauseJob.setEnabled(true);
                    if (StringUtils.equals(job.getPauseStatus(), AKSchedulerControllerJobModel.JOB_STATUS_PAUSED)) {
                        cbPauseJob.setSelected(true);
                    }
                    else {
                        cbPauseJob.setSelected(false);
                    }
                }
            }
        }
    }

    private void updateGUISchedulerMetaData() {
        if (scheduler != null) {
            try {
                SchedulerMetaData meta = scheduler.getMetaData();
                taSummary.setText(meta.getSummary());
                taSummary.setCaretPosition(0);

                cbStandby.setSelected(meta.isInStandbyMode());
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }
    }

    /**
     * GUI Elemente (widgets) nach Selektion anpassen
     */
    private void updateGUITriggers() {
        if ((tbTriggers.getSelectedRow() < 0)
                || (tbMdlTriggers.getDataAtRow(tbTriggers.getSelectedRow()).getFinalFireTime() != null)) {
            cbPauseTrigger.setEnabled(false);
            cbPauseTrigger.setSelected(false);
            bEditTrigger.setEnabled(false);
            bDelTrigger.setEnabled(false);
            if (tbJobs.getSelectedRow() < 0) {
                bAddTrigger.setEnabled(false);
            }
            else {
                bAddTrigger.setEnabled(true);
            }
        }
        else {
            cbPauseTrigger.setEnabled(true);
            bEditTrigger.setEnabled(true);
            bDelTrigger.setEnabled(true);
            bAddTrigger.setEnabled(true);

            AKSchedulerControllerTriggerModel triggerMdl = tbMdlTriggers.getDataAtRow(tbTriggers
                    .getSelectedRow());
            if (triggerMdl != null) {
                if (StringUtils.equals(triggerMdl.getPauseStatus(), AKSchedulerControllerTriggerModel.TRIGGER_STATUS_PAUSED)) {
                    cbPauseTrigger.setSelected(true);
                }
                else {
                    cbPauseTrigger.setSelected(false);
                }
            }
        }
    }

    /**
     * Job Attribute in GUI Elementen (widgets) updaten
     */
    private void updateJobAttributes(String jobName, String groupName,
            AKSchedulerControllerJobModel job) throws SchedulerException {
        Trigger[] triggers = scheduler.getTriggersOfJob(jobName, groupName);
        if (triggers.length > 0) {
            AKJobAttributes attributes = calculateJobAttributes(triggers);
            if (attributes.isScheduled) {
                job.setNextFireTime(attributes.nextFireTime);
                job.setScheduleStatus(AKSchedulerControllerJobModel.JOB_STATUS_SCHEDULED);
            }
            else {
                job.setScheduleStatus(AKSchedulerControllerJobModel.JOB_STATUS_UNSCHEDULED);
                job.setPauseStatus("");
            }

            if (attributes.isPaused) {
                job.setPauseStatus(AKSchedulerControllerJobModel.JOB_STATUS_PAUSED);
            }
            else {
                job.setPauseStatus(AKSchedulerControllerJobModel.JOB_STATUS_RESUMED);
            }
        }
        else {
            job.setScheduleStatus(AKSchedulerControllerJobModel.JOB_STATUS_UNSCHEDULED);
            job.setPauseStatus("");
        }
    }

    /**
     * Endpunkt zum empfangen der Job Events.
     */
    class JobListenerClient implements JobListener {

        String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void jobExecutionVetoed(JobExecutionContext context) {
            try {
                AKSchedulerControllerJobModel job = getJobData(context.getJobDetail().getFullName());
                job.setExecStatus(AKSchedulerControllerJobModel.JOB_STATUS_EXECUTION_VETOED);
                fillJobWithJobExecutionContext(job, context);
                updateJobAttributes(context.getJobDetail().getName(), context.getJobDetail().getGroup(), job);
                fireJobDataChanged();
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }

        @Override
        public void jobToBeExecuted(JobExecutionContext context) {
            try {
                AKSchedulerControllerJobModel job = getJobData(context.getJobDetail().getFullName());
                job.setExecStatus(AKSchedulerControllerJobModel.JOB_STATUS_TO_BE_EXECUTED);
                fillJobWithJobExecutionContext(job, context);
                updateJobAttributes(context.getJobDetail().getName(), context.getJobDetail().getGroup(), job);
                fireJobDataChanged();
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }

        @Override
        public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
            try {
                AKSchedulerControllerJobModel job = getJobData(context.getJobDetail().getFullName());
                job.setExecStatus(AKSchedulerControllerJobModel.JOB_STATUS_WAS_EXECUTED);
                fillJobWithJobExecutionContext(job, context);
                updateJobAttributes(context.getJobDetail().getName(), context.getJobDetail().getGroup(), job);
                updateGUISchedulerMetaData();
                fireJobDataChanged();
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    /**
     * Endpunkt zum empfangen der Scheduler Events.
     */
    class SchedulerListenerClient implements SchedulerListener {

        @Override
        public void jobAdded(JobDetail jobDetail) {
            loadJobData();
        }

        @Override
        public void jobDeleted(String jobName, String groupName) {
            loadJobData();
        }

        @Override
        public void jobScheduled(Trigger trigger) {
            loadJobData();
        }

        @Override
        public void jobsPaused(String jobName, String jobGroup) {
            loadJobData();
        }

        @Override
        public void jobsResumed(String jobName, String jobGroup) {
            loadJobData();
        }

        @Override
        public void jobUnscheduled(String triggerName, String triggerGroup) {
            loadJobData();
        }

        @Override
        public void schedulerError(String msg, SchedulerException cause) {
            // intentionally left blank
        }

        @Override
        public void schedulerInStandbyMode() {
            updateGUISchedulerMetaData();
        }

        @Override
        public void schedulerShutdown() {
            // intentionally left blank
        }

        @Override
        public void schedulerStarted() {
            updateGUISchedulerMetaData();
        }

        @Override
        public void triggerFinalized(Trigger trigger) {
            loadJobData();
        }

        @Override
        public void triggersPaused(String triggerName, String triggerGroup) {
            loadJobData();
        }

        @Override
        public void triggersResumed(String triggerName, String triggerGroup) {
            loadJobData();
        }

    }

    /**
     * Endpunkt zum empfangen der Trigger Events.
     */
    class TriggerListenerClient implements TriggerListener {
        String name;

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void triggerComplete(Trigger trigger, JobExecutionContext context,
                int triggerInstructionCode) {
            try {
                AKSchedulerControllerTriggerModel triggerMdl = getTriggerData(trigger.getFullName());
                triggerMdl.setExecStatus(AKSchedulerControllerTriggerModel.TRIGGER_STATUS_EXECUTED);
                fillTriggerMdl(triggerMdl, trigger);
                fireTriggerDataChanged();
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }

        @Override
        public void triggerFired(Trigger trigger, JobExecutionContext context) {
            try {
                AKSchedulerControllerTriggerModel triggerMdl = getTriggerData(trigger.getFullName());
                triggerMdl.setExecStatus(AKSchedulerControllerTriggerModel.TRIGGER_STATUS_EXECUTING);
                fillTriggerMdl(triggerMdl, trigger);
                fireTriggerDataChanged();
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }

        @Override
        public void triggerMisfired(Trigger trigger) {
            try {
                AKSchedulerControllerTriggerModel triggerMdl = getTriggerData(trigger.getFullName());
                triggerMdl.setExecStatus(AKSchedulerControllerTriggerModel.TRIGGER_STATUS_MISFIRED);
                fillTriggerMdl(triggerMdl, trigger);
                fireTriggerDataChanged();
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }

        @Override
        public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
            return false;
        }

    }

    private class TriggersTableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                updateGUITriggers();
            }
        }
    }

    private class JobsTableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                updateGUIJobs();
                loadTriggerData();
            }
        }
    }

    static class AKJobAttributes {
        public Date nextFireTime = null;
        public boolean isScheduled = false;
        public boolean isPaused = false;
    }
}
