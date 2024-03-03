/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2011 12:12:44
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import java.awt.event.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.mnet.wita.bpm.CommonWorkflowService.WorkflowReentryState;
import de.mnet.wita.model.ResendableMessage;
import de.mnet.wita.model.WorkflowInstanceDto;
import de.mnet.wita.service.WitaAdminService;

/**
 * Dialog, um die Details zu einer RUEM-PV anzugeben und diese zu versenden.
 */
public class WitaKlaerfaelleDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, ItemListener {

    private static final long serialVersionUID = -704663102833399544L;
    private static final Logger LOGGER = Logger.getLogger(WitaKlaerfaelleDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/resources/WitaKlaerfaelleDialog.xml";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm:ss");

    private static final String STATUS_CODE = "status.code";
    private static final String MELDUNG = "meldung";

    private AKJComboBox cbStatusCode;
    private AKJComboBox cbMeldung;

    private WitaAdminService adminService;

    private final WorkflowInstanceDto workflowErrorTask;

    public WitaKlaerfaelleDialog(WorkflowInstanceDto workflowErrorTask) {
        super(RESOURCE);
        this.workflowErrorTask = workflowErrorTask;
        initServices();
        createGUI();
        loadData();
    }

    private void initServices() {
        try {
            adminService = getCCService(WitaAdminService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Speichern", "Aktualisiert den Workflow", true, true);

        AKJLabel lblStatusCode = getSwingFactory().createLabel(STATUS_CODE);
        AKJLabel lblMeldung = getSwingFactory().createLabel(MELDUNG);

        cbStatusCode = getSwingFactory().createComboBox(STATUS_CODE);
        cbStatusCode.addItemListener(this);

        cbMeldung = getSwingFactory().createComboBox(MELDUNG);
        cbMeldung.addItemListener(this);

        AKJPanel details = new AKJPanel(new GridBagLayout());

        int yCorrdinate = 0;
        // @formatter:off
        details.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 0, yCorrdinate  , 1, 1, GridBagConstraints.NONE));
        details.add(lblStatusCode       , GBCFactory.createGBC(  0,  0, 1, ++yCorrdinate, 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 2, yCorrdinate  , 1, 1, GridBagConstraints.NONE));
        details.add(cbStatusCode        , GBCFactory.createGBC(100,  0, 3, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(new AKJPanel()      , GBCFactory.createGBC(100,100, 4, ++yCorrdinate, 1, 1, GridBagConstraints.BOTH));
        details.add(lblMeldung          , GBCFactory.createGBC(  0,  0, 1, ++yCorrdinate, 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 2, yCorrdinate  , 1, 1, GridBagConstraints.NONE));
        details.add(cbMeldung           , GBCFactory.createGBC(100,  0, 3, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(new AKJPanel()      , GBCFactory.createGBC(100,100, 4, ++yCorrdinate, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(details, BorderLayout.CENTER);
    }

    @Override
    protected void validateSaveButton() {
        // nothing to do - save button should not be managed!
    }

    @Override
    public final void loadData() {
        cbStatusCode.addItems(workflowErrorTask.getPossibleReentryStates(), true);
        cbMeldung.addItem("", null);
        for (ResendableMessage message : adminService.findAllResenableMessages(workflowErrorTask.getBusinessKey())) {
            String datum = "noch nicht gesendet";
            if (message.getVersandZeitstempel() != null) {
                datum = message.getVersandZeitstempel().format(DATE_TIME_FORMATTER);
            }
            cbMeldung.addItem(message.getMessageType() + " (" + datum + ")", message);
        }

        cbStatusCode.setSelectedItem(WorkflowReentryState.CLOSED);
    }

    @Override
    protected void doSave() {
        try {
            WorkflowReentryState workflowReentryState = (WorkflowReentryState) cbStatusCode.getSelectedItem();
            if (workflowReentryState == WorkflowReentryState.CLOSED) {
                int result = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Geschlossene Workflows können nicht wieder geöffnet werden."
                                + "\nWollen Sie den Workflow wirklich schließen?", "Workflow wirklich schließen?"
                );
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            adminService.resetErrorState(workflowErrorTask.getBusinessKey(), workflowReentryState);
            String message = "Workflow Zustand wurde auf '" + workflowReentryState.toString() + "' zurueckgesetzt.\n\n";

            if (cbMeldung.getSelectedItemValue() != null) {
                ResendableMessage workflowErrorMessage = (ResendableMessage) cbMeldung.getSelectedItemValue();
                try {
                    adminService.resendMessage(workflowErrorMessage);
                    message += workflowErrorMessage.getMessageType() + " wurde erfolgreich gesendet.";
                }
                catch (Exception e) {
                    message += workflowErrorMessage.getMessageType() + " konnte nicht gesendet werden: "
                            + e.getMessage();
                }
            }
            MessageHelper.showInfoDialog(this, message);
            prepare4Close();
            setValue(null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cbStatusCode) {
            if (WorkflowReentryState.CLOSED == cbStatusCode.getSelectedItem()) {
                cbMeldung.setSelectedItem(null);
                cbMeldung.setEnabled(false);
            }
            else {
                cbMeldung.setEnabled(true);
            }
        }
    }

}
