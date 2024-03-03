/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tree;

import java.awt.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.mnet.wbci.model.WbciEntity;

/**
 * SelectionListener fuer die Tree-Nodes, um die Details anzuzeigen.
 */
public class VaTreeSelectionListener implements TreeSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(VaTreeSelectionListener.class);

    private final VaDetailViewer vaDetailViewer;

    public VaTreeSelectionListener(VaDetailViewer vaDetailViewer) {
        this.vaDetailViewer = vaDetailViewer;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        showVaDetails(e);
    }

    private void showVaDetails(TreeSelectionEvent e) {
        try {
            TreePath selPath = e.getPath();

            DynamicTreeNode node = null;
            Object lastPathComponent = null;
            if (selPath != null) {
                lastPathComponent = selPath.getLastPathComponent();
            }
            if ((lastPathComponent == null) || !(lastPathComponent instanceof DynamicTreeNode)) {
                LOGGER.info("Could not determine node, or node is not of type DynamicTreeNode");
                return;
            }
            else {
                node = (DynamicTreeNode) lastPathComponent;
            }

            if (node.getUserObject() instanceof WbciEntity) {
                vaDetailViewer.showVaDetails((WbciEntity) node.getUserObject());
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(
                    (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), ex);
        }
    }
}
