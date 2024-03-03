/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.05.2007 15:10:05
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.event.*;

import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Admin-Panel fuer die Verwaltung von Text-Bausteinen.
 *
 *
 */
public class TxtBausteineAdminPanel extends AbstractAdminPanel implements ChangeListener, AKNavigationBarListener {

    /**
     * Konstruktor
     */
    public TxtBausteineAdminPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    protected final void createGUI() {
        // Erzeuge Sub-Panels
        TxtBausteinePanel repBausteinePnl = new TxtBausteinePanel();
        TxtBaustein2GruppePanel repBaustein2GruppePnl = new TxtBaustein2GruppePanel();
        TxtBausteinGruppePanel repBausteinGruppePnl = new TxtBausteinGruppePanel();

        // Sub-Panels zu AKJTabbedPane hinzufügen
        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab("Text-Bausteine", repBausteinePnl);
        tabbedPane.addTab("Text-Bausteingruppen", repBausteinGruppePnl);
        tabbedPane.addTab("Zuordnung Baustein-Gruppe", repBaustein2GruppePnl);

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    public void showDetails(Object details) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    public final void loadData() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    public void createNew() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    public void saveData() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    protected void execute(String command) {
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
