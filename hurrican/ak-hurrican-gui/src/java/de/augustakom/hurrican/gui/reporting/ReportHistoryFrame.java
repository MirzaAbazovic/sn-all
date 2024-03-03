/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2007 11:54:21
 */
package de.augustakom.hurrican.gui.reporting;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;


/**
 * Frame zur Darstellung der Report-Historie.
 *
 *
 */
public class ReportHistoryFrame extends AbstractDataFrame {

    private static final Logger LOGGER = Logger.getLogger(ReportHistoryFrame.class);

    private ReportHistoryPanel panel = null;

    /**
     * Standardkonstruktor.
     */
    public ReportHistoryFrame() {
        super("de/augustakom/hurrican/gui/reporting/resources/ReportHistoryFrame.xml", false);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        setIcon("de/augustakom/hurrican/gui/images/printer.gif");

        panel = new ReportHistoryPanel();
        getChildPanel().add(panel, BorderLayout.CENTER);

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
        panel.setModel(model);
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

    /**
     * Oeffnet oder aktiviert das Frame und zeigt die Daten an
     *
     * @param model Modell, dessen Daten angezeigt werden sollen.
     */
    public static void openFrame(Observable model) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames =
                mainFrame.findInternalFrames(ReportHistoryFrame.class);
        ReportHistoryFrame dataFrame = null;
        if (frames != null && frames.length == 1) {
            dataFrame = (ReportHistoryFrame) frames[0];
            mainFrame.activateInternalFrame(dataFrame.getUniqueName());
        }
        else {
            dataFrame = new ReportHistoryFrame();
            mainFrame.registerFrame(dataFrame, true);
        }

        try {
            dataFrame.setModel(model);

            // Setze Titel des Frames
            if (model instanceof Kunde) {
                Kunde kunde = (Kunde) model;
                String titel = dataFrame.getSwingFactory().getText("title") + " für Kunde "
                        + kunde.getName() + " (" + kunde.getKundeNo() + ")";
                dataFrame.setTitle(titel);
            }
            else if (model instanceof Auftrag) {
                Auftrag auftrag = (Auftrag) model;
                String titel = dataFrame.getSwingFactory().getText("title") + " für Auftrag "
                        + auftrag.getAuftragId();
                dataFrame.setTitle(titel);
            }
            else if (model == null) {
                String titel = dataFrame.getSwingFactory().getText("uebersicht");
                dataFrame.setTitle(titel);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }
}


