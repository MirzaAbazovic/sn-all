/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2012 15:05:59
 */
package de.augustakom.authentication.gui.department;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationOptionDialog;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;

/**
 *
 */
public class EditTeamDialog extends AbstractAuthenticationOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(EditTeamDialog.class);

    private static final String CMD_CANCEL = "cancel";
    private static final String CMD_SAVE = "save";

    private TeamDataPanel teamDataPanel = null;

    private AKTeam model = null;

    public EditTeamDialog(AKTeam team) {
        super("de/augustakom/authentication/gui/department/resources/EditTeamDialog.xml", true);
        this.model = team;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJButton btnCancel = getSwingFactory().createButton(CMD_CANCEL, getActionListener());
        AKJButton btnSave = getSwingFactory().createButton(CMD_SAVE, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        //@formatter:off
        btnPanel.add(btnSave       , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnCancel     , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:off

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(2, 2));

        teamDataPanel = new TeamDataPanel(model);
        AKJPanel child = new AKJPanel(new GridBagLayout());
        //@formatter:off
        child.add(teamDataPanel, GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnPanel     , GBCFactory.createGBC(  0,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(fill         , GBCFactory.createGBC(100, 100, 1, 2, 1, 1, GridBagConstraints.BOTH));
        //@formatter:off

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(child, BorderLayout.CENTER);
    }

    @Override
    protected void execute(String command) {
        if (CMD_SAVE.equals(command)) {
            try {
                save();
            }
            catch (AKAuthenticationException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
            }
        }
        else if (CMD_CANCEL.equals(command)) {
            cancel();
        }
    }

    /**
     * Schliesst den Dialog.
     */
    private void cancel() {
        prepare4Close();
        setValue(Integer.valueOf(JOptionPane.CANCEL_OPTION));
    }

    /**
     * Kopiert den Benutzer.
     */
    private void save() throws AKAuthenticationException {
        try {
            teamDataPanel.doSave();

            prepare4Close();
            setValue(Integer.valueOf(JOptionPane.OK_OPTION));
        }
        catch (GUIException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}
