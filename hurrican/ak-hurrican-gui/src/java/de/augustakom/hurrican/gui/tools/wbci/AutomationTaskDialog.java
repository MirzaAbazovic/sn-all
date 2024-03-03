/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Dialog zur Anzeige eines {@link AutomationTask}s
 */
public class AutomationTaskDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {
    private static final long serialVersionUID = 5811517576597449093L;

    private static final Logger LOGGER = Logger.getLogger(AutomationTaskDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/AutomationTaskDialog.xml";

    private static final String DIALOG_TITLE = "dialog.title";
    private static final String SAVE_TEXT = "save.txt";
    private static final String SAVE_TOOLTIP = "save.tooltip";
    private static final String CANCEL_TEXT = "cancel.txt";
    private static final String CANCEL_TOOLTIP = "cancel.tooltip";
    private static final String NAME = "name";
    private static final String STATUS = "status";
    private static final String CREATED_AT = "created.at";
    private static final String COMPLETED_AT = "completed.at";
    private static final String AUTOMATABLE = "automatable";
    private static final String USERNAME = "username";
    private static final String EXECUTION_LOG = "execution.log";

    private AKJTextField tfName;
    private AKJTextField tfStatus;
    private AKJDateComponent dcDateCreatedAt;
    private AKJDateComponent dcDateCompletedAt;
    private AKJCheckBox cbAutomatable;
    private AKJTextField tfUsername;
    private AKJTextArea taExecutionLog;

    private AutomationTask automationTask;
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;

    /**
     * Konstruktor mit Angabe des {@link de.mnet.wbci.model.WbciRequest}s, auf den sich der Dialog beziehen soll.
     *
     * @param automationTask
     */
    public AutomationTaskDialog(AutomationTask automationTask) {
        super(RESOURCE, true, true);
        this.automationTask = automationTask;

        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Looks up to the CCServices and init the service.
     *
     * @throws de.augustakom.common.service.exceptions.ServiceNotFoundException if a service could not be looked up
     */
    protected final void initServices() throws ServiceNotFoundException {
        wbciGeschaeftsfallService = getCCService(WbciGeschaeftsfallService.class);
    }

    @Override
    protected final void createGUI() {
        setSize(new Dimension(500, 100));
        setTitle(getSwingFactory().getText(DIALOG_TITLE));

        AKJLabel lblName = getSwingFactory().createLabel(NAME);
        AKJLabel lblStatus = getSwingFactory().createLabel(STATUS);
        AKJLabel lblCreatedAt = getSwingFactory().createLabel(CREATED_AT);
        AKJLabel lblCompletedAt = getSwingFactory().createLabel(COMPLETED_AT);
        AKJLabel lblAutomatable = getSwingFactory().createLabel(AUTOMATABLE);
        AKJLabel lblUsername = getSwingFactory().createLabel(USERNAME);
        AKJLabel lblExecutionLog = getSwingFactory().createLabel(EXECUTION_LOG);

        tfName = getSwingFactory().createTextField(NAME, false);
        tfStatus = getSwingFactory().createTextField(STATUS, false);
        dcDateCreatedAt = getSwingFactory().createDateComponent(CREATED_AT, false);
        dcDateCompletedAt = getSwingFactory().createDateComponent(COMPLETED_AT, false);
        cbAutomatable = getSwingFactory().createCheckBox(AUTOMATABLE, false);
        tfUsername = getSwingFactory().createTextField(USERNAME, false);
        taExecutionLog = getSwingFactory().createTextArea(EXECUTION_LOG, false, true, true);
        AKJScrollPane spExecutionLog = new AKJScrollPane(taExecutionLog, new Dimension(200, 100));

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.setLayout(new GridBagLayout());
        dtlPnl.add(lblName,             GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel(),      GBCFactory.createGBC(  0,   0, 1, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(tfName,              GBCFactory.createGBC(100,   0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblStatus,           GBCFactory.createGBC(  0,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(tfStatus,            GBCFactory.createGBC(100,   0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblAutomatable,      GBCFactory.createGBC(  0,   0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(cbAutomatable,       GBCFactory.createGBC(100,   0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblCreatedAt,        GBCFactory.createGBC(  0,   0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(dcDateCreatedAt,     GBCFactory.createGBC(100,   0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblCompletedAt,      GBCFactory.createGBC(  0,   0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(dcDateCompletedAt,   GBCFactory.createGBC(100,   0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblUsername,         GBCFactory.createGBC(  0,   0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(tfUsername,          GBCFactory.createGBC(100,   0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblExecutionLog,     GBCFactory.createGBC(  0,   0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(spExecutionLog,      GBCFactory.createGBC(100, 100, 2, 6, 1, 1, GridBagConstraints.BOTH));
        dtlPnl.add(new AKJPanel(),      GBCFactory.createGBC(  0,   0, 3, 8, 1, 1, GridBagConstraints.NONE));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);

        boolean saveEnabled = !automationTask.isDone();
        configureButton(CMD_SAVE, getSwingFactory().getText(SAVE_TEXT), getSwingFactory().getText(SAVE_TOOLTIP), true, saveEnabled);
        configureButton(CMD_CANCEL, getSwingFactory().getText(CANCEL_TEXT), getSwingFactory().getText(CANCEL_TOOLTIP), true, true);
    }

    @Override
    protected void validateSaveButton() {
        // nothing to do
    }

    @Override
    public final void loadData() {
        tfName.setText(automationTask.getName().name());
        tfStatus.setText(automationTask.getStatus().name());
        dcDateCreatedAt.setDate(automationTask.getCreatedAt());
        dcDateCompletedAt.setDate(automationTask.getCompletedAt());
        cbAutomatable.setSelected(automationTask.getAutomatable());
        tfUsername.setText(automationTask.getUserName());
        taExecutionLog.setText(automationTask.getExecutionLog());
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void cancel() {
        prepare4Close();
        setValue(null);
    }

    @Override
    protected void doSave() {
        try {
            automationTask = wbciGeschaeftsfallService.completeAutomationTask(automationTask,
                    HurricanSystemRegistry.instance().getCurrentUser());
            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}
