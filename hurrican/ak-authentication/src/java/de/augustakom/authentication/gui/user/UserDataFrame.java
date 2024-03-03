/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 08:18:43
 */
package de.augustakom.authentication.gui.user;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.gui.tree.AdminTreeNode;
import de.augustakom.authentication.gui.tree.IAdminTreeModel;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;


/**
 * InternalFrame fuer die Verwaltung von AKUser-Objekten.
 */
public class UserDataFrame extends AKJAbstractInternalFrame {

    private static final Logger LOGGER = Logger.getLogger(UserDataFrame.class);

    private static final String ACTION_SAVE = "save";
    private static final String ACTION_CANCEL = "cancel";

    private AKUser model = null;
    private AKJDefaultMutableTreeNode treeNode = null;

    private UserDataPanel userPanel = null;
    private UserAssignmentsPanel assignmentPanel = null;

    /**
     * Konstruktor fuer das Frame mit Angabe des anzuzeigenden AKUser-Objekts.
     *
     * @param model Angabe des Modells, das dargestellt werden soll.
     */
    public UserDataFrame(AKUser model) {
        super("de/augustakom/authentication/gui/user/resources/UserDataFrame.xml");

        this.model = model;
        findNode4User(this.model);
        createGUI();
    }

    /**
     * Konstruktor fuer das Frame mit Angabe des AKUser-Objekts und eines TreeNodes. Dieser Konstruktor kann dafuer
     * verwendet werden, wenn das Frame ueber den Tree geoeffnet wird.
     *
     * @param model Angabe des Modells, das dargestellt werden soll.
     * @param node  TreeNode, von dem aus das Frame aufgerufen wurde oder <code>null</code>, falls das Frame aus einem
     *              Menu augerufen wurde.
     */
    public UserDataFrame(AKUser model, AKJDefaultMutableTreeNode node) {
        super("de/augustakom/authentication/gui/user/resources/UserDataFrame.xml");

        this.treeNode = node;
        this.model = model;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    @Override
    public String getUniqueName() {
        return getTitle();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        String title = getTitle() + ((model.getName() != null) ? model.getName() : getSwingFactory().getText("new.user"));
        setTitle(title);

        AKJButton btnSave = getSwingFactory().createButton(ACTION_SAVE, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(ACTION_CANCEL, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(new JPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(new JPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        userPanel = new UserDataPanel(model);
        assignmentPanel = new UserAssignmentsPanel(model);

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.setLayout(new GridBagLayout());
        child.add(userPanel, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(assignmentPanel, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        child.add(btnPanel, GBCFactory.createGBC(100, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(new AKJScrollPane(child), BorderLayout.CENTER);

        pack();
    }

    /* Versucht, den TreeNode zu finden, der das User-Object enthaelt. */
    private void findNode4User(AKUser user) {
        IAdminTreeModel treeModel = ((IAdminTreeModel) GUISystemRegistry.instance().getValue(
                GUISystemRegistry.REGKEY_TREE_MODEL));
        this.treeNode = treeModel.findNode(AKUser.class, user.getId(), "getId");
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

    /* Veranlasst die Child-Panels dazu, die Daten zu speichern. */
    private void save() {
        try {
            setWaitCursor();

            // alte IDs speichern
            Long userId = model.getId();
            Long depId = model.getDepartmentId();

            if (treeNode == null) {
                treeNode = new AdminTreeNode();
            }
            treeNode.setUserObject(model);

            userPanel.doSave();
            assignmentPanel.doSave();

            if ((userId == null) || !depId.equals(model.getDepartmentId())) {
                IAdminTreeModel treeModel = ((IAdminTreeModel) GUISystemRegistry.instance().getValue(
                        GUISystemRegistry.REGKEY_TREE_MODEL));
                // Node mit User-Object bei Neuanlage einfuegen oder bei Aenderung der Department-ID verschieben
                AKJDefaultMutableTreeNode newParent = treeModel.findNode(AKDepartment.class, model.getDepartmentId(), "getId");
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

    /* Schliesst das Fenster ohne zu speichern. */
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
    public void update(Observable o, Object arg) {
    }

}

