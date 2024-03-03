/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2005 13:13:02
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Frame zur Administration der Billing-Leistungen. <br> Die Konfiguration der Billing-Leistungen wird fuer die
 * Bauauftrags-Steuerung verwendet.
 *
 *
 */
public class BillingLeistungKonfigFrame extends AbstractAdminFrame {

    private TechLeistungKonfigPanel konfPanel = null;

    /**
     * Default-Konstruktor.
     */
    public BillingLeistungKonfigFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminFrame#getAdminPanels()
     */
    protected AbstractAdminPanel[] getAdminPanels() {
        return new AbstractAdminPanel[] { konfPanel };
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected final void createGUI() {
        setTitle("Billing-Leistungen konfigurieren");

        konfPanel = new TechLeistungKonfigPanel();

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(konfPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


