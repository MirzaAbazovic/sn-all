/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 10:12:21
 */
package de.augustakom.hurrican.gui.tools.sap;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;


/**
 * Frame zur Darstellung der SAP-Buchungsdaten.
 *
 *
 */
public class SAPDatenFrame extends AbstractDataFrame {

    private SAPDatenPanel sapDataPanel = null;

    /**
     * Standardkonstruktor.
     */
    public SAPDatenFrame() {
        super(null, false);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Debitoren-Posten");
        setIcon("de/augustakom/hurrican/gui/images/calculator.gif");

        sapDataPanel = new SAPDatenPanel();
        getChildPanel().add(sapDataPanel, BorderLayout.CENTER);

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

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
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

    public void showDetails(String debNo) {
        sapDataPanel.showDetails(debNo);
    }

    /**
     * Oeffnet (oder aktiviert) das Frame mit den SAP-Daten eines Debitors.
     */
    public static void showSAPDaten(String debNo) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames =
                mainFrame.findInternalFrames(SAPDatenFrame.class);
        SAPDatenFrame sapFrame = null;
        if (frames != null && frames.length == 1) {
            sapFrame = (SAPDatenFrame) frames[0];
            mainFrame.activateInternalFrame(sapFrame.getUniqueName());
        }
        else {
            sapFrame = new SAPDatenFrame();
            mainFrame.registerFrame(sapFrame, true);
        }

        sapFrame.showDetails(debNo);
    }


}


