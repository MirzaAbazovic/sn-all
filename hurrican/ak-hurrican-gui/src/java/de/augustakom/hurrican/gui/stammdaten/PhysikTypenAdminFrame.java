/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 14:37:42
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Frame fuer die Administration der Physik-Typen.
 *
 *
 */
public class PhysikTypenAdminFrame extends AbstractAdminFrame {

    private PhysikTypenAdminPanel adminPanel = null;

    /**
     * Standardkonstruktor.
     */
    public PhysikTypenAdminFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminFrame#getAdminPanels()
     */
    protected AbstractAdminPanel[] getAdminPanels() {
        return new AbstractAdminPanel[] { adminPanel };
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Physik-Typen");

        adminPanel = new PhysikTypenAdminPanel();
        getChildPanel().add(adminPanel, BorderLayout.CENTER);

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


