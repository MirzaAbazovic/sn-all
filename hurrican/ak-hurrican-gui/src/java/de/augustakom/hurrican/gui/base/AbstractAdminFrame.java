/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 12:03:06
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.SwingFactory;


/**
 * Abstraktes InternalFrame, das die Buttons 'Neu' und 'Speichern' zur Verfuegung stellt. <br> Bei der Betaetigung der
 * Buttons wird die Methode getDataPanels() aufgerufen und auf den zurueck gelieferten Panels die entsprechende Methode
 * (createNew oder doSave) aufgerufen. <br> <br> Die Ableitungen sollten die GUI-Komponenten dem Panel zuordnen, das
 * ueber die Methode getChildPanel() erreicht werden kann. Das Layout des Child-Panels ist auf 'BorderLayout'
 * vordefiniert.
 *
 *
 */
public abstract class AbstractAdminFrame extends AbstractInternalServiceFrame implements InternalFrameListener {

    protected AKJPanel childPanel = null;

    protected AKJButton btnNew = null;
    protected AKJButton btnSave = null;

    protected boolean createButtons = true;

    private static final SwingFactory internalSF =
            SwingFactory.getInstance("de/augustakom/hurrican/gui/base/resources/AbstractAdminFrame.xml");

    /**
     * Konstruktor mit Angabe der zu verwendenen Resource-Datei.
     *
     * @param resource
     */
    public AbstractAdminFrame(String resource) {
        super(resource);
        createDefaultGUI();
    }

    /**
     * Konstruktor mit Angabe der zu verwendenen Resource-Datei und Flag, ob die Buttons 'New' und 'Save' erzeugt werden
     * sollen.
     *
     * @param resource
     */
    public AbstractAdminFrame(String resource, boolean createButtons) {
        super(resource);
        this.createButtons = createButtons;
        createDefaultGUI();
    }

    /**
     * Gibt den Save-Button zurueck.
     *
     * @return
     */
    protected AKJButton getSaveButton() {
        return btnSave;
    }

    /**
     * Gibt den New-Button zurueck.
     *
     * @return
     */
    protected AKJButton getNewButton() {
        return btnNew;
    }

    /**
     * Die Ableitungen muessen alle Panels vom Typ AbstractAdminPanel zurueck geben, die ueber die Buttons 'New' und
     * 'Save' aufgerufen werden sollen. <br> Die Aufrufe erfolgen in der angegebenen Reihenfolge.
     *
     * @return Array mit den AdminPanels, die aufgerufen werden sollen.
     */
    protected abstract AbstractAdminPanel[] getAdminPanels();

    /**
     * Erzeugt die Standard-GUI.
     */
    protected final void createDefaultGUI() {
        addInternalFrameListener(this);
        childPanel = new AKJPanel(new BorderLayout());

        AKJPanel master = new AKJPanel(new BorderLayout());
        master.add(childPanel, BorderLayout.CENTER);

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        if (createButtons) {
            ButtonActionListener al = new ButtonActionListener();
            btnNew = internalSF.createButton("create.new", al);
            btnSave = internalSF.createButton("save", al);

            btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            btnPanel.add(btnNew, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
            btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.NONE));

            master.add(btnPanel, BorderLayout.SOUTH);
            manageGUI(new AKManageableComponent[] { btnSave, btnNew });
        }

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(new AKJScrollPane(master), BorderLayout.CENTER);
    }

    /**
     * Gibt das Panel zurueck, auf das die Ableitungen beliebige GUI-Komponenten platzieren koennen.
     *
     * @return AKJPanel
     */
    protected AKJPanel getChildPanel() {
        return childPanel;
    }

    class ButtonActionListener implements ActionListener {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if ("create.new".equals(e.getActionCommand())) {
                AbstractAdminPanel[] panels = getAdminPanels();
                if (panels != null) {
                    for (int i = 0; i < panels.length; i++) {
                        panels[i].createNew();
                    }
                }
            }
            else if ("save".equals(e.getActionCommand())) {
                AbstractAdminPanel[] panels = getAdminPanels();
                if (panels != null) {
                    for (int i = 0; i < panels.length; i++) {
                        panels[i].saveData();
                    }
                }
            }
        }
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameOpened(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameOpened(InternalFrameEvent e) {
        // DataPanels werden dazu veranlasst, ihre Daten zu laden
        AbstractAdminPanel[] panels = getAdminPanels();
        if (panels != null) {
            for (int i = 0; i < panels.length; i++) {
                panels[i].loadData();
            }
        }
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameActivated(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameClosed(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameClosing(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameDeiconified(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameIconified(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameIconified(InternalFrameEvent e) {
    }

}


