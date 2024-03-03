/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2011 09:14:04
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die Darstellung aller nicht freigabebereiten Rangierungen (diese konnten durch die automatische Freigabe
 * nicht freigegeben werden).
 *
 *
 */
public class RangierungsKlaerfaelleFrame extends AKJAbstractInternalFrame {

    /**
     * Konstruktor
     */
    public RangierungsKlaerfaelleFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle("Rangierungsklärfälle anzeigen");

        RangierungsKlaerfaellePanel rangierungPanel = new RangierungsKlaerfaellePanel();
        this.getContentPane().setLayout(new BorderLayout());
        this.add(rangierungPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


