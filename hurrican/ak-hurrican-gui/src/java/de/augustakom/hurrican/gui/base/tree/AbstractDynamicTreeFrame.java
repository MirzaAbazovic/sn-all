/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2008 14:03:10
 */
package de.augustakom.hurrican.gui.base.tree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;


/**
 * Abstrakte Basisklasse fuer ein Internal-Frame, in dem ein dynamischer Tree dargestellt wird. <br> Die Darstellung ist
 * wie folgt aufgeteilt: <br> linke Haelfte: dynamischer Tree <br> rechte Haelfte: freier Raum (z.B. fuer weitere
 * Sub-Panels)
 *
 *
 */
public abstract class AbstractDynamicTreeFrame extends AKJAbstractInternalFrame {

    private static final long serialVersionUID = -6850288660415881190L;

    private AKJSplitPane splitPane = null;
    private double dividerLocation = 0.3d;

    /**
     * Konstruktor fuer das Frame.
     *
     * @param resource        Ressource-File fuer das Frame
     * @param dividerLocation (optional) Angabe der Divider-Location fuer die SplitPane, die links den Tree und rechts
     *                        die Modell-Panels darstellt (z.B. '0.3d').
     */
    public AbstractDynamicTreeFrame(String resource, double dividerLocation) {
        super(resource);
        this.dividerLocation = dividerLocation;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        this.setTitle(title);
        this.addComponentListener(new TreeFrameComponentListener());

        splitPane = new AKJSplitPane();
        splitPane.setDividerSize(2);

        AKJScrollPane sp4Panels = new AKJScrollPane();

        DynamicTreePanel treePanel = new DynamicTreePanel(getRootNodeClass(), getMouseListener(sp4Panels));
        splitPane.setLeftComponent(treePanel);
        splitPane.setRightComponent(sp4Panels);

        this.getContentPane().add(splitPane, BorderLayout.CENTER);
    }


    protected abstract Class<? extends DynamicTreeNode> getRootNodeClass();


    /**
     * Erstellt einen Standard MouseListener fuer den Tree
     */
    protected DynamicTreeMouseListener getMouseListener(JComponent panelContainer) {
        return new DynamicTreeMouseListener(this, panelContainer);
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * Component-Listener, um Events des Frames zu erhalten.
     */
    class TreeFrameComponentListener implements ComponentListener {

        /**
         * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
         */
        public void componentShown(ComponentEvent e) {
            splitPane.setDividerLocation(dividerLocation);
        }

        /**
         * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
         */
        public void componentHidden(ComponentEvent e) {
        }

        /**
         * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
         */
        public void componentMoved(ComponentEvent e) {
        }

        /**
         * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
         */
        public void componentResized(ComponentEvent e) {
            splitPane.setDividerLocation(dividerLocation);
        }
    }
}
