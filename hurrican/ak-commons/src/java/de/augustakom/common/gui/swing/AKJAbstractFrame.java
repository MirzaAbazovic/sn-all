/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;


/**
 * Abstrakte Implementierung fuer ein AKJFrame das bestimmte Initialisierungen bereits vornimmt.
 *
 *
 */
public abstract class AKJAbstractFrame extends AKJFrame implements Observer {

    private SwingFactory swingFactory = null;
    private ActionListener defaultActionListener = null;

    /**
     * Konstruktor mit Angabe der Resource-URL fuer die SwingFactory
     *
     * @param resource Resource-URL fuer die SwingFactory
     */
    public AKJAbstractFrame(String resource) {
        super();
        init(resource);
    }

    /**
     * In dieser Methode kann die GUI aufgebaut werden. <br> Die Methode muss von den Ableitungen jedoch selbst
     * aufgerufen werden!
     */
    protected abstract void createGUI();

    /**
     * Initialisiert das Frame.
     *
     * @param resource
     */
    protected void init(String resource) {
        swingFactory = SwingFactory.getInstance(resource);
        SwingHelper.checkXmlResourceLoaded(this, swingFactory, resource);
        defaultActionListener = new DefaultActionListener();
        setTitle(swingFactory.getText("title"));
        ImageIcon icon = swingFactory.getIcon("icon");
        if (icon != null) {
            setIconImage(icon.getImage());
        }
    }

    /**
     * Wird vom ActionListener aufgerufen.
     *
     * @param command
     *
     */
    protected void execute(String command) {
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
     * @see de.augustakom.common.gui.swing.SwingFactoryOwner#getSwingFactory()
     */
    protected SwingFactory getSwingFactory() {
        return swingFactory;
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
