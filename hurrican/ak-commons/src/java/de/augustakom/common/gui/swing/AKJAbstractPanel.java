/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


/**
 * Abstrakte Implementierung eines AKJPanels das bestimmte Initialisierungen bereits vornimmt.
 *
 *
 */
public abstract class AKJAbstractPanel extends AKJPanel implements Observer {

    private SwingFactory swingFactory = null;
    private ActionListener defaultActionListener = null;

    private String frameTitle = null;

    /**
     * Konstruktor mit Angabe der Resource-URL fuer die SwingFactory.
     *
     * @param resource Resource-URL fuer die SwingFactory.
     */
    public AKJAbstractPanel(String resource) {
        super();
        init(resource);
    }

    /**
     * Konstruktor mit Angabe der Resource-URL fuer die SwingFactory und des LayoutManagers.
     *
     * @param resource Resource-URL fuer die SwingFactory.
     * @param layout   LayoutManager fuer das Panel.
     */
    public AKJAbstractPanel(String resource, LayoutManager layout) {
        super(layout);
        init(resource);
    }

    /**
     * Sets cursor for specified component to Wait cursor
     */
    public static void setTopLevelWaitCursor(JComponent component) {
        RootPaneContainer root = (RootPaneContainer) component.getTopLevelAncestor();
        root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        root.getGlassPane().setVisible(true);
    }

    /**
     * Sets cursor for specified component to normal cursor
     */
    public static void setTopLevelDefaultCursor(JComponent component) {
        RootPaneContainer root = (RootPaneContainer) component.getTopLevelAncestor();
        root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        root.getGlassPane().setVisible(false);
    }

    /**
     * In dieser Methode kann die GUI aufgebaut werden. <br> Die Methode muss von den Ableitungen jedoch selbst
     * aufgerufen werden!
     */
    protected abstract void createGUI();

    /**
     * In dieser Methode koennen beliebige Aktionen durchgefuehrt werden. <br> Welche Aktion durchgefuehrt werden soll,
     * kann ueber den Parameter <code>command</code> bestimmt werden.
     *
     * @param command
     */
    protected abstract void execute(String command);

    /**
     * Initialisiert das Frame.
     *
     * @param resource
     */
    protected void init(String resource) {
        swingFactory = SwingFactory.getInstance(resource);
        SwingHelper.checkXmlResourceLoaded(this, swingFactory, resource);
        defaultActionListener = new DefaultActionListener();
        addMouseListener(new AdministrationMouseListener());
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactoryOwner#getSwingFactory()
     */
    public SwingFactory getSwingFactory() {
        return swingFactory;
    }

    /**
     * Gibt eine Instanz eines ActionListeners zurueck. <br> Der ActionListener ruft die Methode
     * <code>execute(String)</code> des Panels auf und uebergibt als Parameter den Wert von
     * <code>ActionEvent#getActionCommand()</code>.
     *
     * @return
     */
    protected ActionListener getActionListener() {
        return defaultActionListener;
    }

    /**
     * Setzt den Cursor des Panels auf 'WAIT'
     */
    public void setWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     * Setzt den Cursor des Panels auf 'DEFAULT'
     */
    public void setDefaultCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Ermittelt den Titel des ersten uebergeordneten Frames.
     */
    protected String getFrameTitle() {
        if (frameTitle == null) {
            Container parent = this.getParent();
            while (parent != null) {
                if (parent instanceof AKJInternalFrame) {
                    frameTitle = ((AKJInternalFrame) parent).getTitle();
                    break;
                }
                else if (parent instanceof AKJFrame) {
                    frameTitle = ((AKJFrame) parent).getTitle();
                    break;
                }
                else {
                    parent = parent.getParent();
                }
            }
        }
        return frameTitle;
    }

    /**
     * Uebergibt dem ersten gefundenen, uebergeordneten Frame den Titel <code>title</code>.
     *
     * @param title
     */
    protected void setFrameTitle(String title) {
        Container parent = this.getParent();
        while (parent != null) {
            if (parent instanceof AKJInternalFrame) {
                ((AKJInternalFrame) parent).setTitle(title);
                break;
            }
            else if (parent instanceof AKJFrame) {
                ((AKJFrame) parent).setTitle(title);
                break;
            }
            else {
                parent = parent.getParent();
            }
        }
    }

    /**
     * Ermittelt das ERSTE Parent-Frame bzw. den ersten Parent-Dialog zu dem aktuellen Panel.
     *
     * @return
     */
    protected Component getFirstParentFrame() {
        Container parent = this.getParent();
        while (parent != null) {
            if ((parent instanceof AKJInternalFrame) || (parent instanceof AKJFrame)
                    || (parent instanceof AKJDialog) || (parent instanceof AKJOptionDialog)) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Ruft die Methode <code>pack</code> auf dem ersten Window-Objekt auf, in dem das Panel dargestellt wird.
     */
    protected void packWindow() {
        Container parent = this.getParent();
        while (parent != null) {
            if (parent instanceof Window) {
                ((Window) parent).pack();
                return;
            }
            parent = parent.getParent();
        }
    }

    /**
     * ActionListener, den die Ableitungen fuer z.B. Buttons verwenden koennen. <br> Der ActionListener ruft die Methode
     * <code>execute(String)</code> des Panels auf. Als Parameter wird der Wert von
     * <code>ActionEvent#getActionCommand()</code> uebergeben.
     */
    class DefaultActionListener implements ActionListener {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            execute(e.getActionCommand());
        }
    }
}
