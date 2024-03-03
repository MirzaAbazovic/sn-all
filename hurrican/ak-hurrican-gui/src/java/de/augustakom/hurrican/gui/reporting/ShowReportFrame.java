/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2007 08:59:53
 */
package de.augustakom.hurrican.gui.reporting;

import java.awt.*;
import java.util.*;
import java.util.List;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.service.reporting.ReportService;
import de.augustakom.hurrican.service.reporting.utils.ReportServiceFinder;

/**
 * Frame zur Anzeige des PDF-Files inkl. Druckersteuerung
 *
 *
 */
public class ShowReportFrame extends AbstractDataFrame {

    private ShowReportPanel reportPanel = null;

    /**
     * Konstruktor mit Angabe des Dateinames der anzuzeigenden PDF-Datei
     */
    public ShowReportFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Report-Viewer");

        configureButton(CMD_SAVE, null, null, false, false);

        reportPanel = new ShowReportPanel();
        getChildPanel().add(reportPanel, BorderLayout.CENTER);

        pack();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
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
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) {
    }

    /**
     * Funktion übergibt Liste mit ReportRequests (IDs oder Objekte) an SubPanel
     *
     * @param requests
     *
     */
    public void setRequests(List requests) {
        reportPanel.setRequests(requests);
    }

    /**
     * Funktion übergibt Liste mit RequestIds an SubPanel und druckt alle Reports automatisch aus
     *
     * @param requests
     *
     */
    public void printRequests(List requests) {
        reportPanel.setRequests(requests);
        reportPanel.printRequests();
    }

    /**
     * Oeffnet oder aktiviert das Frame und zeigt die Daten an
     */
    public static void openFrame(ReportRequest request, Integer buendelNo, List<Long> requestIds) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames = mainFrame.findInternalFrames(ShowReportFrame.class);
        ShowReportFrame dataFrame = null;
        if (frames != null && frames.length == 1) {
            dataFrame = (ShowReportFrame) frames[0];
            mainFrame.activateInternalFrame(dataFrame.getUniqueName());
        }
        else {
            dataFrame = new ShowReportFrame();
            mainFrame.registerFrame(dataFrame, true);
        }

        // Erzeuge Liste mit ReportRequests
        List list = null;

        if (request != null) {
            list = new ArrayList();
            list.add(request);
        }
        else if (buendelNo != null) {
            try {
                ReportService service = ReportServiceFinder.instance().getReportService(ReportService.class);
                list = service.findAllRequests4BuendelNo(buendelNo);
            }
            catch (Exception e) {
                list = null;
            }
        }
        else {
            list = requestIds;
        }

        // Übergebe Panel die benötigten Daten
        if (CollectionTools.isNotEmpty(list)) {
            dataFrame.setRequests(list);
        }
        else {
            MessageHelper.showInfoDialog(mainFrame, "Keine Reports zur Anzeige übergeben");
        }
    }

    /**
     * Oeffnet oder aktiviert das Frame und zeigt die Daten an und druckt alle übergebenen Reports
     */
    public static void printReports(List<Long> requestIds) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames = mainFrame.findInternalFrames(ShowReportFrame.class);
        ShowReportFrame dataFrame = null;
        if (frames != null && frames.length == 1) {
            dataFrame = (ShowReportFrame) frames[0];
            mainFrame.activateInternalFrame(dataFrame.getUniqueName());
        }
        else {
            dataFrame = new ShowReportFrame();
            mainFrame.registerFrame(dataFrame, true);
        }

        // Übergebe Panel die benötigten Daten
        if (CollectionTools.isNotEmpty(requestIds)) {
            dataFrame.printRequests(requestIds);
        }
        else {
            MessageHelper.showInfoDialog(mainFrame, "Keine Reports zum Druck übergeben");
        }
    }

}
