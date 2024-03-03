/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2012 09:54:10
 */
package de.augustakom.authentication.gui.department;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.tools.lang.NumberTools;

/**
 * Panel fuer die Bearbeitung der Daten eines AKDepartment-Objekts.
 */
public class DepartmentDataPanel extends AbstractAuthenticationPanel implements SavePanel {

    private static final Logger LOGGER = Logger.getLogger(DepartmentDataPanel.class);

    private static final String DEPARTMENT_ID = "department.id";
    private static final String DEPARTMENT_NAME = "department.name";
    private static final String DEPARTMENT_DESCRIPTION = "department.description";

    private static final String CMD_NEW_TEAM = "button.new.team";
    private static final String CMD_EDIT_TEAM = "button.edit.team";
    private static final String CMD_DELETE_TEAM = "button.delete.team";

    private AKJButton btnEditTeam = null;
    private AKJButton btnDeleteTeam = null;

    private AKJTable tbTeams = null;
    private AKJTextField tfName = null;
    private AKJTextArea taDescription = null;
    private AKJFormattedTextField tfId = null;
    private AKTableModelXML<AKTeam> tbMdlTeams = null;

    private AKDepartment model = null;

    public DepartmentDataPanel(AKDepartment model) {
        super("de/augustakom/authentication/gui/department/resources/DepartmentDataPanel.xml");
        this.model = model;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblId = getSwingFactory().createLabel(DEPARTMENT_ID);
        AKJLabel lblName = getSwingFactory().createLabel(DEPARTMENT_NAME);
        AKJLabel lblDescription = getSwingFactory().createLabel(DEPARTMENT_DESCRIPTION);

        tfId = getSwingFactory().createFormattedTextField(DEPARTMENT_ID);
        tfId.setEditable(false);
        tfName = getSwingFactory().createTextField(DEPARTMENT_NAME);
        taDescription = getSwingFactory().createTextArea(DEPARTMENT_DESCRIPTION);
        taDescription.setWrapStyleWord(true);
        taDescription.setLineWrap(true);
        AKJScrollPane scrDesc = new AKJScrollPane(taDescription);

        tbMdlTeams = new AKTableModelXML<AKTeam>(
                "de/augustakom/authentication/gui/department/resources/TeamsTableModel.xml");
        tbTeams = new AKJTable(tbMdlTeams, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbTeams.fitTable(new int[] { 70, 230 });
        AKJScrollPane spTbTeams = new AKJScrollPane(tbTeams);
        spTbTeams.setPreferredSize(new Dimension(300, 200));

        AKJPanel tableButtonPanel = createTeamButtonPanel();
        //@formatter:off
        this.setLayout(new GridBagLayout());
        this.add(new JPanel()    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblId           , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel()    , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        this.add(tfId            , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblName         , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfName          , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblDescription  , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(scrDesc         , GBCFactory.createGBC(100, 50, 3, 3, 1, 3, GridBagConstraints.BOTH));
        this.add(spTbTeams       , GBCFactory.createGBC(100,  0, 1, 6, 3, 1, GridBagConstraints.HORIZONTAL));
        this.add(tableButtonPanel, GBCFactory.createGBC(100,  0, 1, 7, 3, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel()    , GBCFactory.createGBC( 50,  0, 4, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on

        read();
    }

    private AKJPanel createTeamButtonPanel() {
        final TeamActionListener actionListener = new TeamActionListener();
        AKJButton btnNewTeam = getSwingFactory().createButton(CMD_NEW_TEAM, actionListener);
        btnEditTeam = getSwingFactory().createButton(CMD_EDIT_TEAM, actionListener);
        btnEditTeam.setEnabled(false);
        btnDeleteTeam = getSwingFactory().createButton(CMD_DELETE_TEAM, actionListener);
        btnDeleteTeam.setEnabled(false);

        tbTeams.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    boolean enabled = !lsm.isSelectionEmpty();
                    btnDeleteTeam.setEnabled(enabled);
                    btnEditTeam.setEnabled(enabled);
                }
            }
        });

        tbTeams.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    actionListener.actionPerformed(new ActionEvent(tbTeams, -1, CMD_EDIT_TEAM));
                }
            }
        });

        AKJPanel tableButtonPanel = new AKJPanel(new GridBagLayout());
        //@formatter:off
        tableButtonPanel.add(btnNewTeam    , GBCFactory.createGBC(100,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        tableButtonPanel.add(btnEditTeam   , GBCFactory.createGBC(100,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        tableButtonPanel.add(btnDeleteTeam , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on

        return tableButtonPanel;
    }

    /**
     * Liest die Daten des Modells aus und zeigt sie in den entsprechenden GUI-Komponenten an.
     */
    private void read() {
        if (model.getId() != null) {
            tfId.setValue(model.getId());
            tfName.setText(model.getName());
            taDescription.setText(model.getDescription());
            tbMdlTeams.setData(model.getTeams());
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void doSave() throws GUIException {
        try {
            setWaitCursor();

            model.setName(tfName.getText());
            model.setDescription(taDescription.getText());

            validateModel(model);

            try {
                AKDepartmentService departmentService = getAuthenticationService(
                        AKAuthenticationServiceNames.DEPARTMENT_SERVICE, AKDepartmentService.class);
                departmentService.save(model);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new GUIException(GUIException.DEPARTMENT_SAVING_ERROR, e);
            }
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Validiert das AKDepartment-Objekt.<br> Werden ungueltige Daten festgestellt, wird eine GUIException geworfen.
     */
    private void validateModel(AKDepartment department) throws GUIException {
        if (StringUtils.isBlank(department.getName())) {
            throw new GUIException(GUIException.DEPARTMENT_VALIDATE_NAME);
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
    }

    private void createTeam() {
        AKTeam newTeam = new AKTeam();
        EditTeamDialog dialog = new EditTeamDialog(newTeam);
        Object object = DialogHelper.showDialog(DepartmentDataPanel.this, dialog, true, true);
        if (object instanceof Integer) {
            Integer result = (Integer) object;
            if (NumberTools.equal(result, Integer.valueOf(JOptionPane.OK_OPTION))) {
                tbMdlTeams.addObject(newTeam);
            }
        }
    }

    private void deleteTeam(int teamIndex) {
        try {
            AKTeam toDelete = tbMdlTeams.getDataAtRow(teamIndex);

            // Check if deletion of team is allowed
            AKUserService userService = getAuthenticationService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            List<AKUser> usersMapped2Team = userService.findByTeam(toDelete);
            if (CollectionUtils.isNotEmpty(usersMapped2Team)) {
                StringBuilder users = new StringBuilder();
                for (AKUser akUser : usersMapped2Team) {
                    users.append(akUser.getNameAndFirstName());
                    users.append("\n");
                }
                throw new GUIException(String.format(
                        "Das Team %s kann nicht entfernt werden, da folgende User noch zugeordnet sind:%n%s",
                        toDelete.getName(), users.toString()));
            }

            int confirmResult = MessageHelper.showConfirmDialog(DepartmentDataPanel.this,
                    String.format("Team '%s' wirklich löschen?", toDelete.getName()), "Team löschen?",
                    JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION == confirmResult) {
                tbMdlTeams.removeObject(toDelete);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(DepartmentDataPanel.this, e);
        }
    }

    private void editTeam(int teamIndex) {
        try {
            AKTeam toEdit = tbMdlTeams.getDataAtRow(teamIndex);
            EditTeamDialog dialog = new EditTeamDialog(toEdit);
            DialogHelper.showDialog(DepartmentDataPanel.this, dialog, true, true);
            tbMdlTeams.fireTableRowsUpdated(teamIndex, teamIndex);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(DepartmentDataPanel.this, e);
        }
    }

    private class TeamActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            int teamIndex = tbTeams.getSelectedRow();
            if ((teamIndex == -1) && !CMD_NEW_TEAM.equals(cmd)) {
                return;
            }

            if (CMD_DELETE_TEAM.equals(cmd)) {
                deleteTeam(teamIndex);
            }
            else if (CMD_EDIT_TEAM.equals(cmd)) {
                editTeam(teamIndex);
            }
            else if (CMD_NEW_TEAM.equals(cmd)) {
                createTeam();
            }
        }
    }
}


