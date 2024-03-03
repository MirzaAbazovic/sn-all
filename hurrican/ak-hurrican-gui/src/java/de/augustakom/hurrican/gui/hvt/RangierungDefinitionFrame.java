/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2007 13:10:08
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;


/**
 * Frame fuer die Definition von Rangierungen.
 *
 *
 */
public class RangierungDefinitionFrame extends AKJAbstractInternalFrame {

    private RangierungsAuftrag rangierungsAuftrag = null;

    /**
     * Konstruktor fuer das Frame mit Angabe des zugehoerigen Rangierungs-Auftrags.
     *
     * @param rangierungsAuftrag
     */
    public RangierungDefinitionFrame(RangierungsAuftrag rangierungsAuftrag) {
        super(null);
        this.rangierungsAuftrag = rangierungsAuftrag;
        if (rangierungsAuftrag == null) {
            throw new IllegalArgumentException("Kein Rangierungs-Auftrag angegeben!");
        }
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        this.setTitle("Rangierungen definieren - ID " + rangierungsAuftrag.getId());

        RangierungDefinitionPanel rdp = new RangierungDefinitionPanel(rangierungsAuftrag);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(rdp, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    public String getUniqueName() {
        return super.getUniqueName() + rangierungsAuftrag.getId();
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


