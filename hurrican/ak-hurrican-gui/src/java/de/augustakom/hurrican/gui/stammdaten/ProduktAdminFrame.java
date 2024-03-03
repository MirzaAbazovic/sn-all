/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2004 14:47:44
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Admin-Frame fuer die Verwaltung der Produkte.
 *
 *
 */
public class ProduktAdminFrame extends AbstractAdminFrame {

    private static final long serialVersionUID = 7043530796280122361L;
    private ProduktTablePanel tablePanel = null;
    private ProduktAdminPanel produktPanel = null;

    /**
     * Konstruktor
     */
    public ProduktAdminFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminFrame#getAdminPanels()
     */
    protected AbstractAdminPanel[] getAdminPanels() {
        return new AbstractAdminPanel[] { produktPanel, tablePanel };
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Produkte");

        produktPanel = new ProduktAdminPanel();
        tablePanel = new ProduktTablePanel(produktPanel);
        produktPanel.setProdukteTable(tablePanel.getProdukteTable());

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tablePanel);
        split.setBottomComponent(produktPanel);

        getChildPanel().add(split, BorderLayout.CENTER);
        pack();
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


