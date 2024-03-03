/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2009 12:06:35
 */
package de.augustakom.hurrican.gui.base.tree.hardware.actions;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeMouseListener;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.hardware.MassenbenachrichtigungPanel;


/**
 * Action fuer die Erstellung einer Auftrags-Liste fuer die Massenbenachrichtigung
 *
 *
 */
public class MassenbenachrichtigungAction extends AKAbstractAction {
    private static final Logger LOGGER = Logger.getLogger(MassenbenachrichtigungAction.class);
    private final Collection<DynamicTreeNode> nodes;
    private final DynamicTreeMouseListener dynamicTreeMouseListener;

    /**
     * Konstruktor mit Angabe des zugehoerigen Panel Containers
     */
    public MassenbenachrichtigungAction(Collection<DynamicTreeNode> nodes, DynamicTreeMouseListener dynamicTreeMouseListener) {
        this.nodes = nodes;
        this.dynamicTreeMouseListener = dynamicTreeMouseListener;
        setName("SDSL Massenbenachrichtigung");
        setActionCommand("mb.action");
        setIcon("de/augustakom/hurrican/gui/images/hvt.gif");
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            AKJPanel panel = new MassenbenachrichtigungPanel(nodes);
            dynamicTreeMouseListener.showPanel(panel, panel.getName());
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            MessageHelper.showErrorDialog(
                    (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), ex);
        }
    }
}
