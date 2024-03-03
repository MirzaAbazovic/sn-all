/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2012 18:23:09
 */
package de.augustakom.authentication.gui.department;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.gui.tree.AdminTreeNode;
import de.augustakom.authentication.gui.tree.IAdminTreeModel;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.locator.ServiceLocator;

/**
 * Frame, um ein AKDepartment-Objekt darzustellen und aendern zu koennen.
 */
public class DepartmentDataFrame extends AKJAbstractInternalFrame {

    private static final Logger LOGGER = Logger.getLogger(DepartmentDataFrame.class);

    private static final String ACTION_SAVE = "save";
    private static final String ACTION_CANCEL = "cancel";

    private AKDepartment model = null;
    private AdminTreeNode treeNode = null;

    private DepartmentDataPanel dataPanel = null;

    public DepartmentDataFrame(AKDepartment model, AdminTreeNode treeNode) {
        super("de/augustakom/authentication/gui/department/resources/DepartmentDataFrame.xml");

        this.treeNode = treeNode;
        this.model = model;
        createGUI();
    }

    @Override
    public String getUniqueName() {
        return super.getUniqueName() + model.getId();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void createGUI() {
        setTitle(String.format("%s%s", getTitle(), model.getName()));

        AKJButton btnSave = getSwingFactory().createButton(ACTION_SAVE, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(ACTION_CANCEL, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        //@formatter:off
        btnPanel.add(new JPanel(), GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSave     , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCancel   , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(new JPanel(), GBCFactory.createGBC(100,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on

        dataPanel = new DepartmentDataPanel(model);

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.setLayout(new GridBagLayout());
        //@formatter:off
        child.add(dataPanel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        child.add(btnPanel , GBCFactory.createGBC(100,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(new AKJScrollPane(child), BorderLayout.CENTER);

        addInternalFrameListener(new FrameClosedListener());

        pack();
    }

    @Override
    protected void execute(String command) {
        if (ACTION_SAVE.equals(command)) {
            save();
        }
        else if (ACTION_CANCEL.equals(command)) {
            cancel();
        }
    }

    /**
     * Veranlasst die Panels dazu, die Daten zu speichern.
     */
    private void save() {
        try {
            setWaitCursor();

            dataPanel.doSave();

            setClosed(true);
        }
        catch (PropertyVetoException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (GUIException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Schliesst das Fenster ohne zu speichern.
     */
    private void cancel() {
        String title = getSwingFactory().getText("close.wihtout.save.title");
        String msg = getSwingFactory().getText("close.wihtout.save.msg");

        int result = MessageHelper.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                undoChanges();
                setClosed(true);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    private void undoChanges() throws Exception {
        if (treeNode != null) {
            // Aenderungen (falls vorhanden) verwerfen
            AKDepartmentService departmentService = ServiceLocator.instance().getService(
                    AKDepartmentService.class.getName(), AKDepartmentService.class);
            AKDepartment department = departmentService.findDepartmentById(model.getId());

            IAdminTreeModel treeModel = ((IAdminTreeModel) GUISystemRegistry.instance().getValue(
                    GUISystemRegistry.REGKEY_TREE_MODEL));
            treeModel.updateUserObject4Node(treeNode, department);
        }
        else {
            MessageHelper.showErrorDialog(this, new GUIException(GUIException.DEPARTMENT_TREE_NODE_MISSING));
        }
    }

    class FrameClosedListener extends InternalFrameAdapter {
        @Override
        public void internalFrameClosed(InternalFrameEvent event) {
            try {
                undoChanges();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(DepartmentDataFrame.this, e);
            }
        }
    }
}


