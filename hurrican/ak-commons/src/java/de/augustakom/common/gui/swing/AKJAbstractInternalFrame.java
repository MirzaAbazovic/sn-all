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
import org.apache.log4j.Logger;


/**
 * Abstrakte Implementierung fuer ein AKJInternalFrame das bestimmte Initialisierungen bereits vornimmt. <br> Alle Flags
 * fuer die Groessenaenderung des Internal-Frames (z.B. Resizeable) sind auf <code>true</code> gesetzt.
 *
 *
 */
public abstract class AKJAbstractInternalFrame extends AKJInternalFrame implements Observer {

    private static final Logger LOGGER = Logger.getLogger(AKJAbstractInternalFrame.class);

    private SwingFactory swingFactory = null;
    private ActionListener defaultActionListener = null;

    /**
     * Konstruktor mit Angabe der Resource-URL fuer die SwingFactory.
     *
     * @param resource Resource-URL fuer die SwingFactory.
     */
    public AKJAbstractInternalFrame(String resource) {
        super();
        init(resource);
    }

    /**
     * Initialisiert das Frame.
     *
     * @param resource
     */
    protected void init(String resource) {
        swingFactory = SwingFactory.getInstance(resource);
        SwingHelper.checkXmlResourceLoaded(this, swingFactory, resource);
        if (swingFactory != null) {
            setTitle(swingFactory.getText("title"));
            ImageIcon icon = swingFactory.createIcon("icon");
            if (icon != null) {
                setFrameIcon(icon);
            }
        }
        else {
            LOGGER.warn("SwingFactory not found! Frame: " + getClass().getName());
        }

        defaultActionListener = new DefaultActionListener();

        try {
            setResizable(true);
            setSelected(true);
            setClosable(true);
            setMaximizable(true);
            setIconifiable(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Uebergibt dem Frame die URL des zu verwendenden Icons.
     *
     * @param url URL des Icons.
     */
    protected void setIcon(String url) {
        IconHelper helper = new IconHelper();
        ImageIcon icon = helper.getIcon(url);
        if (icon != null) {
            setFrameIcon(icon);
        }
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
     * @see de.augustakom.common.gui.swing.SwingFactoryOwner#getSwingFactory()
     */
    protected SwingFactory getSwingFactory() {
        return swingFactory;
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
