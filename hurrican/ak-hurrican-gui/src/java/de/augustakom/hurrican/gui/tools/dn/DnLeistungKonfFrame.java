/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2005 10:55:02
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.*;
import javax.swing.event.*;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;

/**
 * Frame fuer die Konfiguration der Rufnummernleistungen
 *
 *
 */
public class DnLeistungKonfFrame extends AbstractDataFrame implements ChangeListener {

    /**
     * Default-Const.
     */
    public DnLeistungKonfFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Konfiguration der Rufnummern-Leistungen");
        DnLeistungBuendelKonfPanel dnLeistungBuendelKonfPanel = new DnLeistungBuendelKonfPanel();
        DnLeistungParameterKonfPanel dnLeistungParameterKonfPanel = new DnLeistungParameterKonfPanel();

        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addChangeListener(this);
        tabbedPane.addTab("Konfiguration Parameter", dnLeistungParameterKonfPanel);
        tabbedPane.setToolTipTextAt(0, "Konfiguration der Parameter zu den Leistungen");
        tabbedPane.addTab("Konfiguration Leistungsbuendel", dnLeistungBuendelKonfPanel);
        tabbedPane.setToolTipTextAt(1, "Konfiguration der Leistungsbuendel");

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent arg0) {
    }

}
