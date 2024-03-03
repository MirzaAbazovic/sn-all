/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2007 08:59:53
 */
package de.augustakom.hurrican.gui.reporting;

import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;
import com.qoppa.pdf.PrintSettings;
import com.qoppa.pdf.dom.IPDFDocument;
import com.qoppa.pdfViewer.PDFViewerBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.reporting.Printer;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.service.reporting.ReportService;
import de.augustakom.hurrican.service.reporting.utils.ReportServiceFinder;

/**
 * Panel zur Anzeige des PDF-Files inkl. Druckersteuerung
 *
 *
 */
public class ShowReportPanel extends AbstractDataPanel {

    private static final Logger LOGGER = Logger.getLogger(ShowReportPanel.class);

    private String currentFile = null;
    private List<ReportRequest> requests = null;
    private List<String> singleFiles = null;

    private PDFViewerBean bean = null;

    /**
     * Konstruktor mit Angabe des Dateinames der anzuzeigenden PDF-Datei
     */
    public ShowReportPanel() {
        super("de/augustakom/hurrican/gui/reporting/resources/ShowReportPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected final void createGUI() {
        setLayout(new BorderLayout());
        setSize(800, 600);

        getViewerBean();
        add(bean);
    }


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
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
    public void readModel() {
        // Funktion lädt ReportRequest und kopiert Datei auf lokalen Rechner in temp-Verzeichnis
        try {
            ReportService rs = getReportService(ReportService.class);
            File report = null;

            // Download Report
            if (CollectionTools.isNotEmpty(requests)) {
                String serienbriefFile = ReportService.REPORT_TMP_PATH + "PDF_Viewer-" + (new Date().getTime()) + ".pdf";
                singleFiles = rs.getPDFBundled(requests, ReportService.REPORT_TMP_PATH, serienbriefFile);
                filesSetDelete();
                report = new File(serienbriefFile);
            }

            // Markiere Datei zum Löschen
            if ((report != null) && report.canRead()) {
                report.deleteOnExit();

                // Schreibe ReportRequest, dass Report heruntergeladen wurde
                requestSetDownload();

                currentFile = report.getPath();
            }
            else {
                MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        "Report konnte nicht vom Server geladen werden!", null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    new Exception("Fehler beim Download des Reports!"), true);
        }
    }

    /*
     * Funktion setzt bei allen Report-Requests das Downloaded-At-Datum
     */
    private void requestSetDownload() {
        if (CollectionTools.isNotEmpty(requests)) {
            try {
                ReportService rs = getReportService(ReportService.class);

                for (ReportRequest request : requests) {
                    if (request != null) {
                        request.setReportDownloadedAt(DateTools.getActualSQLDate());
                        rs.saveReportRequest(request);
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            }
        }

    }

    /*
     * Funktion löscht bei einem Serienbrief alle heruntergeladenen Einzel-Reports
     */
    private void filesSetDelete() {
        if (CollectionTools.isNotEmpty(singleFiles)) {
            for (String file : singleFiles) {
                if (file != null) {
                    File toDelete = new File(file);
                    if (toDelete.canRead()) {
                        toDelete.deleteOnExit();
                    }
                }
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
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
     * Funktion setzt ReportRequests und lädt PDF-Datei
     *
     * @param list
     *
     */
    public void setRequests(List list) {
        try {
            ReportService rs = getReportService(ReportService.class);
            int errors = 0;

            if (CollectionTools.isNotEmpty(list)) {
                requests = new ArrayList<>();
                for (Object obj : list) {
                    if (obj instanceof Long) {
                        ReportRequest request = rs.findReportRequest((Long) obj);
                        if (rs.testReportReadable(request)) {
                            requests.add(request);
                        }
                        else {
                            errors++;
                        }
                    }
                    else if (obj instanceof ReportRequest) {
                        if (rs.testReportReadable((ReportRequest) obj)) {
                            requests.add((ReportRequest) obj);
                        }
                        else {
                            errors++;
                        }
                    }
                }
            }
            else {
                requests = null;
            }

            if (errors != 0) {
                int result = MessageHelper.showConfirmDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        "Es können " + errors + " Report(s) nicht angezeigt werden. Trotzdem fortfahren?", "Fehler",
                        JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            if (CollectionTools.isNotEmpty(requests)) {
                readModel();

                // Lade Datei in PDF-Bean
                if (StringUtils.isNotBlank(currentFile)) {
                    bean.loadPDF(currentFile);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Funktion druckt alle Reports
     */
    public void printRequests() {
        printFile(false);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        List<String> files = new ArrayList<>();
        files.add(currentFile);

        if ("print".equals(command)) {
            printFile(true);
        }
        if ("print.silent".equals(command)) {
            printFile(false);
        }
    }

    /*
     * Funktion um PDFs drucken zu können, einschl. Papierschachtsteuerung
     */
    private void printFile(Boolean dialog) {
        try {
            ReportService rs = ReportServiceFinder.instance().getReportService(ReportService.class);
            Media kass2 = null;
            Media kass1 = null;

            // Ermittle Standard-Drucker
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            PrinterJob pj = PrinterJob.getPrinterJob();
            PrintService dfltService = PrintServiceLookup.lookupDefaultPrintService();
            pj.setPrintService(dfltService);

            // Drucke mit Dialog -> drucke aktuell angezeigtes PDF
            if (dialog) {
                IPDFDocument doc = bean.getDocument();
                doc.setPrintSettings(new PrintSettings(true, false, true, true));
                pj.setPageable(doc.getPageable(pj));

                // Zeige Dialog für Druckersteuerung
                if (pj.printDialog(aset)) {
                    pj.print(aset);
                }
            }
            // Print Silent
            else {
                Report rep = rs.findReport(requests.get(0).getRepId());
                Printer printer = rs.findPrinterByName(dfltService.getName());

                boolean printDuplex = ((rep != null) && BooleanTools.nullToFalse(rep.getDuplexDruck()))
                        && ((printer != null) && BooleanTools.nullToFalse(printer.getDuplex()));

                // Ermittle Schachtsteuerung
                if (CollectionTools.isNotEmpty(requests) && (requests.get(0) != null)) {
                    String[] trays = rs.findTrayName4ReportPrinter(requests.get(0).getRepId(), dfltService.getName());

                    // Ermittle Papierschacht
                    if (trays != null) {
                        Media med[] = (Media[]) dfltService.getSupportedAttributeValues(Media.class, null, null);
                        for (int k = 0; k < med.length; k++) {
                            if (StringUtils.isNotBlank(trays[1]) && StringUtils.contains(med[k].toString(), trays[1])) {
                                kass2 = med[k];
                            }
                            if (StringUtils.isNotBlank(trays[0]) && StringUtils.contains(med[k].toString(), trays[0])) {
                                kass1 = med[k];
                            }
                        }
                    }
                }

                // Erste und zweite Seite werden aus der selben Kassette gedruckt -> drucke Serienbrief
                if (((kass1 != null) && (kass2 != null) && StringUtils.equals(kass1.toString(), kass2.toString()))
                        || ((kass1 == null) && (kass2 == null))) {
                    IPDFDocument doc = bean.getDocument();
                    doc.setPrintSettings(new PrintSettings(true, false, true, true));
                    pj.setPageable(doc.getPageable(pj));

                    // Duplex-Druck
                    if (printDuplex) {
                        aset.add(Sides.TWO_SIDED_LONG_EDGE);
                    }

                    if (kass1 != null) {
                        aset.add(kass1);
                    }
                    pj.print(aset);
                }
                // Falls Papierschächte unterschieden werden, müssen einzelnen PDFs gedruckt werden
                else {
                    // Lade einzelne PDFs und drucke
                    for (String file : singleFiles) {
                        if (StringUtils.isNotBlank(file)) {
                            PDFViewerBean printBean = new PDFViewerBean();
                            printBean.loadPDF(file);
                            IPDFDocument doc = printBean.getDocument();
                            doc.setPrintSettings(new PrintSettings(true, false, true, true));
                            pj.setPageable(doc.getPageable(pj));

                            // Drucke erste Seite
                            if (kass1 != null) {
                                aset.add(kass1);
                            }
                            else {
                                aset.remove(Media.class);
                            }
                            if (printDuplex) {
                                aset.add(Sides.TWO_SIDED_LONG_EDGE);
                                aset.add(new PageRanges(1, 2));
                            }
                            else {
                                aset.add(new PageRanges(1, 1));
                            }
                            pj.print(aset);

                            // Drucke zweite und weitere Seiten
                            if (kass2 != null) {
                                aset.add(kass2);
                            }
                            else {
                                aset.remove(Media.class);
                            }
                            if (printDuplex && (doc.getPageCount() > 2)) {
                                aset.add(Sides.TWO_SIDED_LONG_EDGE);
                                aset.add(new PageRanges(3, doc.getPageCount()));
                                pj.print(aset);
                            }
                            else if (!printDuplex && (doc.getPageCount() > 1)) {
                                aset.add(new PageRanges(2, doc.getPageCount()));
                                pj.print(aset);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /*
     * Funktion liefert PDFViewerBean
     */
    private void getViewerBean() {
        bean = new PDFViewerBean();
        try {
            bean.getToolbar().getjbOpen().setVisible(false);
            bean.getToolbar().getjbPrint().setVisible(false);
            bean.getToolbar().getjbRotateCCW().setVisible(false);
            bean.getToolbar().getjbRotateCW().setVisible(false);
            bean.getToolbar().getJbFitHeight().setBorderPainted(false);
            bean.getToolbar().getJbFitWidth().setBorderPainted(false);
            bean.getToolbar().getJbActualSize().setBorderPainted(false);
            bean.getToolbar().getjbMagLess().setBorderPainted(false);
            bean.getToolbar().getjbMagMore().setBorderPainted(false);
            bean.getToolbar().getjbPageDown().setBorderPainted(false);
            bean.getToolbar().getjbPageFirst().setBorderPainted(false);
            bean.getToolbar().getjbPageLast().setBorderPainted(false);
            bean.getToolbar().getjbPageUp().setBorderPainted(false);
            bean.getToolbar().remove(3);
            bean.getSelectToolbar().setVisible(false);
            bean.setSplitPolicy(PDFViewerBean.SPLITPOLICY_NEVER_VISIBLE);

            // Eigene Buttons fürs Drucken hinzufügen
            AKJButton print = getSwingFactory().createButton("print", getActionListener());
            print.setBorderPainted(false);
            AKJButton printSilent = getSwingFactory().createButton("print.silent", getActionListener());
            printSilent.setBorderPainted(false);
            bean.getToolbar().add(print);
            bean.getToolbar().add(printSilent);

            bean.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray, 1));
            bean.setPreferredSize(new Dimension(800, 600));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    new Exception("Fehler bei der Anzeige des Reports!"), true);
        }
    }

}
