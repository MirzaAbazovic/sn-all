/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2005 09:48:43
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;
import de.augustakom.hurrican.model.cc.view.HVTBestellungView;


/**
 * Frame fuer das Bearbeiten einer HVT-Bestellung.
 *
 *
 */
public class HVTBestellungEditFrame extends AbstractDataFrame {

    private static final Logger LOGGER = Logger.getLogger(HVTBestellungEditFrame.class);

    private HVTBestellungEditPanel editPanel = null;

    /**
     * Default-Konstruktor.
     */
    public HVTBestellungEditFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("HVT-Bestellung bearbeiten");
        editPanel = new HVTBestellungEditPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(editPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(900, 600));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) {
        try {
            editPanel.setModel(model);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    public String getUniqueName() {
        return this.getClass().getName();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
        // NOSONAR squid:S1186 ; there is nothing to do here...
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        // NOSONAR squid:S1186 ; there is nothing to do here...
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() throws AKGUIException {
        // NOSONAR squid:S1186 ; there is nothing to do here...
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
        // NOSONAR squid:S1186 ; there is nothing to do here...
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * Oeffnet oder aktiviert das Frame und zeigt die Daten von <code>auftragModel</code> an.
     *
     * @param model Modell, dessen Daten angezeigt werden sollen.
     */
    public static void openFrame(HVTBestellungView model) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames =
                mainFrame.findInternalFrames(HVTBestellungEditFrame.class);
        HVTBestellungEditFrame dataFrame = null;
        if (frames != null && frames.length == 1) {
            dataFrame = (HVTBestellungEditFrame) frames[0];
            mainFrame.activateInternalFrame(dataFrame.getUniqueName());
        }
        else {
            dataFrame = new HVTBestellungEditFrame();
            mainFrame.registerFrame(dataFrame, false);
        }

        dataFrame.setModel(model);
    }
}


