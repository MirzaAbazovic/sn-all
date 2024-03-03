/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 17:09:00
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Frame zur Darstellung der offenen Rangierungs-Auftraege.
 *
 *
 */
public class RangierungsAuftragFrame extends AbstractAdminFrame {

    private RangierungsAuftragPanel panel = null;

    /**
     * Default-Const.
     */
    public RangierungsAuftragFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Rangierungsaufträge");
        panel = new RangierungsAuftragPanel();
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(panel, BorderLayout.CENTER);
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

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminFrame#getAdminPanels()
     */
    protected AbstractAdminPanel[] getAdminPanels() {
        return new AbstractAdminPanel[] { panel };
    }

}


