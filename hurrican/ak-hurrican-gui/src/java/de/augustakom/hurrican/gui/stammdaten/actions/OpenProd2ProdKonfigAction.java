/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2005 10:48:07
 */
package de.augustakom.hurrican.gui.stammdaten.actions;

import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.stammdaten.Produkt2ProduktFrame;
import de.augustakom.hurrican.model.cc.Produkt;


/**
 * Action, um das Frame fuer die Produkt2Produkt-Konfiguration zu oeffnen.
 *
 *
 */
public class OpenProd2ProdKonfigAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;
    private Produkt produkt = null;

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object tmp = getValue(AKAbstractOpenFrameAction.OBJECT_4_ACTION);
        if (tmp instanceof Produkt) {
            this.produkt = (Produkt) tmp;
            this.uniqueName = "Produkt" + produkt.getId();
            super.actionPerformed(e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    protected AKJInternalFrame getFrameToOpen() {
        return new Produkt2ProduktFrame(produkt);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getMainFrame()
     */
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getUniqueName()
     */
    protected String getUniqueName() {
        return uniqueName;
    }

}


