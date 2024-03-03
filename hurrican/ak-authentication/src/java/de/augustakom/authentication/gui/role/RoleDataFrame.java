/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2004 09:20:41
 */
package de.augustakom.authentication.gui.role;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.gui.tree.AdminTreeNode;
import de.augustakom.authentication.gui.tree.IAdminTreeModel;
import de.augustakom.authentication.gui.tree.RoleTreeGroup;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;


/**
 * Frame, um ein AKRole-Objekt darzustellen und aendern zu koennen.
 *
 *
 */
public class RoleDataFrame extends AKJAbstractInternalFrame {

    private static final Logger LOGGER = Logger.getLogger(RoleDataFrame.class);

    private static final String ACTION_SAVE = "save";
    private static final String ACTION_CANCEL = "cancel";

    private AKRole model = null;
    private AKJDefaultMutableTreeNode treeNode = null;

    private RoleDataPanel dataPanel = null;

    /**
     * Konstruktor fuer das Frame
     *
     * @param model    Angabe des Modells (vom Typ AKRole), das dargestellt werden soll
     * @param treeNode TreeNode, von dem aus das Frame aufgerufen wurde oder <code>null</code>, falls das Frame aus
     *                 einem Menu aufgerufen wurde.
     */
    public RoleDataFrame(AKRole model, AKJDefaultMutableTreeNode treeNode) {
        super("de/augustakom/authentication/gui/role/resources/RoleDataFrame.xml");

        this.model = model;
        this.treeNode = treeNode;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        String title = getTitle() + ((model.getName() != null) ? model.getName() : getSwingFactory().getText("new.role"));
        setTitle(title);

        AKJButton btnSave = getSwingFactory().createButton(ACTION_SAVE, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(ACTION_CANCEL, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(new JPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(new JPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        dataPanel = new RoleDataPanel(model);

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.setLayout(new GridBagLayout());
        child.add(dataPanel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        child.add(btnPanel, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(new AKJScrollPane(child), BorderLayout.CENTER);

        pack();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    @Override
    public String getUniqueName() {
        return getTitle();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
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

            Long roleId = model.getId();
            if (treeNode == null) {
                treeNode = new AdminTreeNode();
            }
            treeNode.setUserObject(model);

            dataPanel.doSave();

            if (roleId == null) {
                // Node mit User-Object wird in den Baum eingetragen
                IAdminTreeModel treeModel = ((IAdminTreeModel) GUISystemRegistry.instance().getValue(
                        GUISystemRegistry.REGKEY_TREE_MODEL));
                AKJDefaultMutableTreeNode newParent = treeModel.findNode(RoleTreeGroup.class, null, null);
                treeModel.moveNode(newParent, treeNode);
            }

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

        int result = MessageHelper.showConfirmDialog(
                this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                setClosed(true);
            }
            catch (PropertyVetoException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    }

}
