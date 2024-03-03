/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.2007 15:31:07
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.auftrag.innenauftrag.InnenauftragPanel;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;


/**
 * Frame zur Darstellung der Budgets zu einem Rangierungs-Auftrag.
 *
 *
 */
public class RangierungBudgetFrame extends AKJAbstractInternalFrame {

    private RangierungsAuftrag rangierungsAuftrag = null;

    /**
     * Konstruktor mit Angabe des Rangierungs-Auftrags.
     *
     * @param rangierungsAuftrag Rangierungs-Auftrag, dessen Budget angezeigt werden soll
     */
    public RangierungBudgetFrame(RangierungsAuftrag rangierungsAuftrag) {
        super(null);
        this.rangierungsAuftrag = rangierungsAuftrag;
        if (rangierungsAuftrag == null) {
            throw new IllegalArgumentException("Es muss ein Rangierungs-Auftrag angegeben werden.");
        }
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Budget zu Rangierungsauftrag " + rangierungsAuftrag.getId());
        try {
            InnenauftragPanel ip = new InnenauftragPanel();
            this.getContentPane().setLayout(new BorderLayout());
            this.getContentPane().add(ip, BorderLayout.CENTER);

            ip.setModel(rangierungsAuftrag);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
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


