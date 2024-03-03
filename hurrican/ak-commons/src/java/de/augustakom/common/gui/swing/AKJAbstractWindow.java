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


/**
 * Abstrakte Implementierung fuer ein AKJWindow das bestimmte Initialisierungen bereits vornimmt.
 *
 *
 */
public abstract class AKJAbstractWindow extends AKJWindow implements Observer {

    private SwingFactory swingFactory = null;
    private ActionListener defaultActionListener = null;

    /**
     * Konstruktor mit Angabe der Resource-URL fuer die SwingFactory und eines Frames als Owner. <br> Der Owner wird
     * benoetigt, damit GUI-Komponenten auf dem Window den Focus erhalten koennen.
     *
     * @param resource Resource-URL fuer die SwingFactory
     * @param owner    Owner fuer das Window
     */
    public AKJAbstractWindow(String resource, Frame owner) {
        super(owner);
        init(resource);
    }

    /**
     * @see java.awt.Window#dispose()
     */
    public void dispose() {
        if (getParent() instanceof Frame) {
            ((Frame) getParent()).setVisible(false);
        }
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
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactoryOwner#getSwingFactory()
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
        public void actionPerformed(ActionEvent e) {
            execute(e.getActionCommand());
        }
    }

}
