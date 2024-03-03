/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2006 15:57:50
 */
package de.augustakom.hurrican.gui.tools.scheduler.status;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.quartz.QuartzRMISchedulerClient;
import de.augustakom.hurrican.gui.base.AbstractSchedulerPanel;
import de.augustakom.hurrican.service.cc.RegistryService;


/**
 * Panel zur Anzeige des AK-Scheduler Status.
 *
 *
 */
public class AKSchedulerStatusPanel extends AbstractSchedulerPanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(AKSchedulerStatusPanel.class);

    private AKJTextField tfName;
    private AKJTextField tfHost;
    private AKJTextField tfPort;
    private AKJTextArea taSummary;

    private AKReflectionTableModel<AKSchedulerRunningJobModel> tbMdlJobs;

    public AKSchedulerStatusPanel() {
        super("de/augustakom/hurrican/gui/tools/scheduler/resources/AKSchedulerStatusPanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblName = getSwingFactory().createLabel("scheduler.name", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblHost = getSwingFactory().createLabel("scheduler.host", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblPort = getSwingFactory().createLabel("scheduler.port", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblSummary = getSwingFactory().createLabel("summary", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblJobs = getSwingFactory().createLabel("current.jobs", AKJLabel.LEFT, Font.BOLD);

        tfName = getSwingFactory().createTextField("scheduler.name");
        tfHost = getSwingFactory().createTextField("scheduler.host");
        tfPort = getSwingFactory().createTextField("scheduler.port");
        taSummary = getSwingFactory().createTextArea("summary", false);
        taSummary.setWrapStyleWord(true);
        taSummary.setLineWrap(true);
        AKJScrollPane spSummary = new AKJScrollPane(taSummary, new Dimension(350, 140));
        AKJButton btnRefresh = getSwingFactory().createButton("refresh", getActionListener());

        tbMdlJobs = new AKReflectionTableModel<AKSchedulerRunningJobModel>(
                new String[] { "Job-Name", "scheduled time", "fired time", "next fire time" },
                new String[] { "fullJobName", "scheduledFireTime", "fireTime", "nextFireTime" },
                new Class[] { String.class, Date.class, Date.class, Date.class });
        AKJTable tbJobs = new AKJTable(tbMdlJobs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbJobs.attachSorter();
        tbJobs.fitTable(new int[] { 150, 110, 110, 110 });
        tbJobs.setDefaultRenderer(Date.class, new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME_LONG));
        AKJScrollPane spJobs = new AKJScrollPane(tbJobs, new Dimension(500, 120));

        this.setLayout(new GridBagLayout());
        this.add(lblName, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(tfName, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblHost, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfHost, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblPort, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfPort, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblSummary, GBCFactory.createGBC(0, 0, 0, 3, 4, 1, GridBagConstraints.HORIZONTAL));
        this.add(spSummary, GBCFactory.createGBC(0, 0, 0, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblJobs, GBCFactory.createGBC(0, 0, 0, 5, 4, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 2, 2, 2)));
        this.add(spJobs, GBCFactory.createGBC(0, 0, 0, 6, 4, 1, GridBagConstraints.HORIZONTAL));
        this.add(btnRefresh, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 8, 1, 1, GridBagConstraints.BOTH));

        try {
            RegistryService rs = getCCService(RegistryService.class);
            tfName.setText(rs.getStringValue(RegistryService.REGID_SCHEDULER_NAME));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        tfHost.setText(System.getProperty("hurricanweb.host"));
        tfPort.setText(System.getProperty("hurricanweb.rmi.port"));
    }


    @Override
    public final void loadData() {
        try {
            taSummary.setText(null);
            tbMdlJobs.removeAll();

            QuartzRMISchedulerClient schedulerClient = getSchedulerClient(tfHost.getText(), tfPort.getText(), tfName.getText());

            Scheduler scheduler = schedulerClient.getScheduler();
            SchedulerMetaData meta = scheduler.getMetaData();
            taSummary.setText(meta.getSummary());

            @SuppressWarnings("unchecked")
            List<JobExecutionContext> runningJobs = scheduler.getCurrentlyExecutingJobs();
            List<AKSchedulerRunningJobModel> jobs = new ArrayList<AKSchedulerRunningJobModel>();
            if (runningJobs != null) {
                for (JobExecutionContext ctx : runningJobs) {
                    JobDetail jd = ctx.getJobDetail();

                    AKSchedulerRunningJobModel job = new AKSchedulerRunningJobModel();
                    job.setFullJobName((jd != null) ? jd.getFullName() : null);
                    job.setScheduledFireTime(ctx.getScheduledFireTime());
                    job.setFireTime(ctx.getFireTime());
                    job.setNextFireTime(ctx.getNextFireTime());
                    jobs.add(job);
                }
            }

            tbMdlJobs.setData(jobs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if ("refresh".equals(command)) {
            loadData();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}


