/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag.actions;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.innenauftrag.AbstractIaLevelTreeNode;
import de.augustakom.hurrican.gui.innenauftrag.EditIaLevelDialog;
import de.augustakom.hurrican.gui.innenauftrag.IaLevel1TreeNode;
import de.augustakom.hurrican.gui.innenauftrag.IaLevel3TreeNode;
import de.augustakom.hurrican.gui.innenauftrag.IaLevelRootNode;
import de.augustakom.hurrican.model.cc.innenauftrag.AbstractIaLevel;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel5;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Action, um einen weiteren IA-Level einzufuegen.
 */
public class AddIaLevelAction extends AKAbstractAction {

    private static final long serialVersionUID = -5063579605627482868L;

    private static final Logger LOGGER = Logger.getLogger(AddIaLevelAction.class);

    private final DynamicTreeNode treeNode;

    public AddIaLevelAction(DynamicTreeNode treeNode) {
        this.treeNode = treeNode;

        setName("neue Ebene anlegen...");
        setActionCommand("add.new.level");
        setTooltip("Fügt eine neue Ebene unter dieser Ebene ein");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            InnenauftragService iaService = CCServiceFinder.instance().getCCService(InnenauftragService.class);

            AbstractIaLevel newLevel = null;
            if (treeNode instanceof IaLevelRootNode) {
                newLevel = new IaLevel1();
            }
            else if (treeNode instanceof IaLevel1TreeNode) {
                newLevel = new IaLevel3();
            }
            else if (treeNode instanceof IaLevel3TreeNode) {
                newLevel = new IaLevel5();
            }
            else {
                LOGGER.warn(String.format("instance of DynamicTreeNode (type=%s) is not a supported subclass",
                        (treeNode == null) ? "null" : treeNode.getClass().getName()));
                return;
            }

            EditIaLevelDialog editDialog = new EditIaLevelDialog(getName(), newLevel, treeNode);
            Object retVal = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), editDialog, true, true);

            if ((retVal instanceof Integer) && NumberTools.equal((Integer) retVal, JOptionPane.OK_OPTION)) {
                IaLevel1 level1 = (treeNode instanceof IaLevelRootNode) ? (IaLevel1) newLevel : ((AbstractIaLevelTreeNode) treeNode).getIaLevel1OfCurrentNode();
                if (treeNode instanceof IaLevel1TreeNode) {
                    level1.addIaLevel((IaLevel3) newLevel);
                }
                else if (treeNode instanceof IaLevel3TreeNode) {
                    IaLevel3 level3 = (IaLevel3) treeNode.getUserObject();
                    level3.addIaLevel((IaLevel5) newLevel);
                }
                iaService.saveIaLevel(level1);
                treeNode.refreshChildren();
                treeNode.getTree().expandPath(new TreePath(treeNode.getPath()));
            }
        }
        catch (Exception ex) {
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }
}
