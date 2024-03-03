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
 * Abstrakte Implementierung eines AKJOptionDialogs der bestimmte Initialisierungen bereits vornimmt.
 *
 *
 */
public abstract class AKJAbstractOptionDialog extends AKJOptionDialog implements Observer {

    private static final long serialVersionUID = -3990900033531956516L;
    protected boolean useScrollPane = false;
    private SwingFactory swingFactory = null;
    private ActionListener defaultActionListener = null;
    private AKJPanel childPanel = null;

    /**
     * Konstruktor mit Angabe der Resource-URL fuer die SwingFactory.
     *
     * @param resource Resource-URL fuer die SwingFactory.
     */
    public AKJAbstractOptionDialog(String resource) {
        super();
        init(resource, false);
    }

    /**
     * Konstruktor mit Angabe der Resource-URL fuer die SwingFactory. <br> Ueber den Parameter
     * <code>useScrollPane</code> wird definiert, ob das ChildPanel in einer ScrollPane dargestellt werden soll.
     *
     * @param resource
     * @param useScrollPane
     */
    public AKJAbstractOptionDialog(String resource, boolean useScrollPane) {
        super();
        init(resource, useScrollPane);
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
     * @param useScrollPane
     */
    protected void init(String resource, boolean useScrollPane) {
        swingFactory = SwingFactory.getInstance(resource);
        SwingHelper.checkXmlResourceLoaded(this, swingFactory, resource);
        defaultActionListener = new DefaultActionListener();
        this.useScrollPane = useScrollPane;

        initDefaultGUI();
    }

    /**
     * Initialisiert die Default-GUI.
     */
    protected void initDefaultGUI() {
        childPanel = new AKJPanel();

        this.setBorder(BorderFactory.createEmptyBorder());
        this.setLayout(new BorderLayout());

        if (useScrollPane) {
            JScrollPane sp = new JScrollPane(childPanel);
            sp.setBorder(BorderFactory.createEmptyBorder());
            this.add(sp, BorderLayout.CENTER);
        }
        else {
            this.add(childPanel, BorderLayout.CENTER);
        }
    }

    /**
     * Gibt das Panel zurueck, auf das die Ableitungen die GUI platzieren koennen.
     *
     * @return Instanz von AKJPanel.
     */
    protected AKJPanel getChildPanel() {
        return childPanel;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory
     */
    protected SwingFactory getSwingFactory() {
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
