/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag.actions;

import java.awt.event.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.innenauftrag.IaLevel1TreeNode;
import de.augustakom.hurrican.gui.innenauftrag.IaLevel3TreeNode;
import de.augustakom.hurrican.gui.innenauftrag.IaLevel5TreeNode;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Created by glinkjo on 23.02.2015.
 */
public class DeleteIaLevelAction extends AKAbstractAction {

    private static final long serialVersionUID = -5063579605627482868L;

    private final DynamicTreeNode treeNode;

    public DeleteIaLevelAction(DynamicTreeNode treeNode) {
        this.treeNode = treeNode;

        setName("Ebene löschen...");
        setActionCommand("delete.level");
        setTooltip("Löscht die selektierte Ebene");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            InnenauftragService iaService = CCServiceFinder.instance().getCCService(InnenauftragService.class);

            int option = MessageHelper.showYesNoQuestion(HurricanSystemRegistry.instance().getMainFrame(),
                    "Soll die selektierte Ebene inkl. aller Sub-Ebenen wirklich gelöscht werden?",
                    "Ebene löschen?");
            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            DynamicTreeNode parent = (DynamicTreeNode) treeNode.getParent();
            if (treeNode instanceof IaLevel5TreeNode) {
                IaLevel3 level3 = ((IaLevel5TreeNode) treeNode).getIaLevel3OfCurrentNode();
                level3.getLevel5s().remove(treeNode.getUserObject());

                iaService.saveIaLevel(((IaLevel5TreeNode) treeNode).getIaLevel1OfCurrentNode());
            }
            else if (treeNode instanceof  IaLevel3TreeNode) {
                IaLevel3 level3 = (IaLevel3) treeNode.getUserObject();
                level3.removeChilds();

                IaLevel1 level1 = ((IaLevel3TreeNode) treeNode).getIaLevel1OfCurrentNode();
                level1.getLevel3s().remove(treeNode.getUserObject());
                iaService.saveIaLevel(level1);
            }
            else if (treeNode instanceof IaLevel1TreeNode) {
                IaLevel1 level1 = ((IaLevel1TreeNode) treeNode).getIaLevel1OfCurrentNode();
                level1.removeChilds();
                iaService.saveIaLevel(level1);
                iaService.deleteIaLevel(level1);
            }

            parent.refreshChildren();
        }
        catch (Exception ex) {
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }
}
