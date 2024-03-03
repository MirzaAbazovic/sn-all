/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.01.2010 16:10:40
 */
package de.augustakom.hurrican.gui.base.tree.actions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;


/**
 *
 */
public class OpenChildsAction extends AKAbstractAction {
    private static final Logger LOGGER = Logger.getLogger(OpenChildsAction.class);

    private final DynamicTree tree;
    private final DynamicTreeNode node;

    public OpenChildsAction(DynamicTree tree, DynamicTreeNode node) {
        this.tree = tree;
        this.node = node;
        setActionCommand("expand.children");
        setName("Unterebene öffnen");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            TreePath path = new TreePath(node.getPath());
            tree.expandPath(path);
            for (DynamicTreeNode child : node.getChildren()) {
                child.loadChildrenInBackground(true);
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            MessageHelper.showErrorDialog(
                    (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), ex);
        }
    }

}
