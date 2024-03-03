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
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.innenauftrag.AbstractIaLevelTreeNode;
import de.augustakom.hurrican.gui.innenauftrag.EditIaLevelDialog;
import de.augustakom.hurrican.model.cc.innenauftrag.AbstractIaLevel;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Created by glinkjo on 23.02.2015.
 */
public class EditIaLevelAction extends AKAbstractAction {

    private static final long serialVersionUID = 257858767569680646L;

    private final DynamicTreeNode treeNode;

    public EditIaLevelAction(DynamicTreeNode treeNode) {
        this.treeNode = treeNode;

        setName("Ebene editieren...");
        setActionCommand("edit.level");
        setTooltip("Selektierte Ebene editieren...");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            InnenauftragService iaService = CCServiceFinder.instance().getCCService(InnenauftragService.class);

            EditIaLevelDialog editDialog = new EditIaLevelDialog(getName(),
                    (AbstractIaLevel) treeNode.getUserObject(), (DynamicTreeNode) treeNode.getParent());
            Object retVal = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), editDialog, true, true);

            if ((retVal instanceof Integer) && NumberTools.equal((Integer) retVal, JOptionPane.OK_OPTION)) {
                iaService.saveIaLevel(((AbstractIaLevelTreeNode) treeNode).getIaLevel1OfCurrentNode());
            }
        }
        catch (Exception ex) {
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }
}
