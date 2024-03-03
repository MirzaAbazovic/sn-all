/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2007 08:40:19
 */
package de.augustakom.hurrican.gui.reporting;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportReason;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Dialog, um einen Report auszuwaehlen.
 *
 *
 */
public class ReportDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(ReportDialog.class);

    private AKJTable tbReports = null;
    private AKReflectionTableModel<Report> tbModelReports = null;


    private AuftragDaten auftragDaten = null;
    private Long auftragId = null;
    private Long kundeNoOrig = null;
    private Long reportType = null;

    /**
     * Konstruktor mit Angabe der Kunden- und Auftragsdaten. Die restlichen Daten werden von dem Dialog selbst geladen.
     */
    public ReportDialog(Long reportType, Long kundeNoOrig, Long auftragId) {
        super("de/augustakom/hurrican/gui/reporting/resources/ReportDialog.xml");
        this.kundeNoOrig = kundeNoOrig;
        this.auftragId = auftragId;
        this.reportType = reportType;
        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        StringBuilder title = new StringBuilder();
        title.append(getSwingFactory().getText("title"));
        setTitle(title.toString());
        setIconURL("de/augustakom/hurrican/gui/images/printer.gif");

        configureButton(CMD_SAVE, "Ok", null, true, true);
        configureButton(CMD_CANCEL, "Abbrechen", null, true, true);

        // Tabelle fuer Reports
        tbModelReports = new AKReflectionTableModel<Report>(
                new String[] { "ID", "Name", "User", "Beschreibung" },
                new String[] { "id", "name", "userw", "description" },
                new Class[] { Long.class, String.class, String.class, String.class });

        tbReports = new AKJTable(tbModelReports, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbReports.attachSorter();
        tbReports.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbReports.fitTable(new int[] { 50, 250, 70, 400 });
        AKJScrollPane tableSP = new AKJScrollPane(tbReports, new Dimension(800, 200));

        // Falls Auftragsreport erstellt werden soll, zeige zusaetzlich Button fuer Jasper-Account-Anschreiben
        if ((reportType != null) && (reportType.equals(Report.REPORT_TYPE_AUFTRAG))) {
            AKJButton btnAccount = getSwingFactory().createButton("account", getActionListener());
            getButtonPanel().add(btnAccount, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));

            manageGUI(new AKManageableComponent[] { btnAccount });
        }

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(tableSP, BorderLayout.CENTER);
    }

    /* Laedt die Liste der verfuegbaren Reports*/
    private void read() {
        try {
            setWaitCursor();

            List<Report> list = null;
            ReportService service = getReportService(ReportService.class);
            CCAuftragService as = getCCService(CCAuftragService.class);

            // Ermittle KundeNo falls diese nicht gesetzt wurde
            if ((kundeNoOrig == null) && (auftragId != null)) {
                Auftrag auftrag = as.findAuftragById(auftragId);
                if (auftrag != null) {
                    kundeNoOrig = auftrag.getKundeNo();
                }
            }

            // Teste ob KundeNo vorhanden. Falls nicht, breche ab
            if (kundeNoOrig == null) {
                MessageHelper.showInfoDialog(getMainFrame(), "Es konnten nicht alle Daten für eine Report-Anforderung ermittelt werden. Es wird kein Report erzeugt.", null, true);
                closeCurrentDialog(AKJOptionDialog.OK_OPTION);
                return;
            }

            // Ermittle verfuegbare Reports
            if (auftragId != null) {
                auftragDaten = as.findAuftragDatenByAuftragId(auftragId);
                if (auftragDaten != null) {
                    list = service.findAvailableReports(reportType, auftragDaten, HurricanSystemRegistry.instance().getSessionId());
                }
            }
            else {
                list = service.findAvailableReports(reportType, null, HurricanSystemRegistry.instance().getSessionId());
            }

            // Falls keine Reports vorhanden sind, gebe Hinweis auf Account-Anschreiben
            if (CollectionTools.isEmpty(list)) {
                showDialogNoReports(reportType);
                return;
            }

            tbModelReports.setData(list);
            tbModelReports.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /*
     * Funktion zeigt einen Dialog mit Hinweis auf Jasper-Account-Anschreiben
     */
    private void showDialogNoReports(Long reportType) {
        String[] buttons = null;
        String text = null;

        // Unterscheide Auftrag - Kunde
        if ((reportType != null) && (reportType.equals(Report.REPORT_TYPE_AUFTRAG))) {
            buttons = new String[2];
            buttons[0] = "Abbrechen";
            buttons[1] = "Account-Anschreiben";
            text = "Für dieses Produkt ist im aktuellen Status kein Report vorhanden.";
        }
        else {
            buttons = new String[1];
            buttons[0] = "Abbrechen";
            text = "Kein Report vorhanden.";
        }
        int test = MessageHelper.showOptionDialog(getMainFrame(),
                text, "Report-Server", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, buttons, null);

        // Fallunterscheidung fuer gedrueckten Button
        if (test == 1) {
            createAccountJasper();
        }
        else {
            closeCurrentDialog(AKJOptionDialog.CANCEL_OPTION);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        // Button "Account-Anschreiben" gedrueckt
        if ("account".equals(command)) {
            createAccountJasper();
        }
    }

    /**
     * Funktion erzeugt ein Account-Anschreiben mit Jasper-Reports
     */
    private void createAccountJasper() {
        try {
            if (auftragId != null) {
                CCAuftragService as = getCCService(CCAuftragService.class);
                JasperPrint jp = as.reportAccounts(auftragId, HurricanSystemRegistry.instance().getSessionId());

                JasperPrintManager.printReport(jp, true);
            }
            else {
                MessageHelper.showInfoDialog(this, "Die nötigen Daten zum Drucken des Account-Anschreiben sind nicht vorhanden");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            // Schliesse Dialog
            closeCurrentDialog(AKJOptionDialog.OK_OPTION);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        // Ermittle Selektion
        Integer selection = tbReports.getSelectedRow();
        Report rep = null;

        if ((selection != null) && (selection != -1)) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<Report> tbMdlRep = (AKMutableTableModel<Report>) tbReports.getModel();
            rep = tbMdlRep.getDataAtRow(selection);

            // Erzeuge Request
            if (rep != null) {
                createRequest(rep);
            }

            // Schliesse Dialog
            closeCurrentDialog(AKJOptionDialog.OK_OPTION);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Kein Report ausgewählt!", null, true);
        }
    }

    /* Funktion beendet Frame */
    private void closeCurrentDialog(int status) {
        prepare4Close();
        setValue(Integer.valueOf(status));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (getButton(CMD_SAVE).isEnabled()) {
            doSave();
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Sie haben keine Berechtigung einen Report zu " +
                    "erstellen", null, true);
        }
    }

    /*
     * Funktion erzeugt einen ReportRequest
     */
    private void createRequest(Report rep) {
        if ((rep == null) || (kundeNoOrig == null)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Es konnten nicht alle Daten für eine " +
                    "Report-Anforderung ermittelt werden. Es wird kein Report erzeugt.", null, true);
            return;
        }
        // Erzeuge ReportRequest
        try {
            setWaitCursor();

            Long reasonId = null;

            // Pruefe, ob bereits ein Report der gleichen Reportgruppe fuer den aktuellen
            // Auftrag/Kunden erstellt wurde.
            ReportService service = getReportService(ReportService.class);
            List<ReportRequest> reports = service.findReportRequests4ReportGruppe(rep, kundeNoOrig, (auftragDaten != null) ? auftragDaten.getAuftragId() : null);

            if (CollectionTools.isNotEmpty(reports)) {
                ReportRequest lastReport = null;
                for (ReportRequest test : reports) {
                    if (test != null) {
                        lastReport = test;
                        break;
                    }
                }

                ReprintReasonDialog reasonDialog = null;
                if ((lastReport != null) && (lastReport.getRfa() == null)) {
                    reasonDialog = new ReprintReasonDialog(true);
                }
                else {
                    reasonDialog = new ReprintReasonDialog(false);
                }
                Object obj = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        reasonDialog, true, true);
                ReportReason reason = (obj instanceof ReportReason) ? (ReportReason) obj : null;
                if (reason != null) {
                    reasonId = reason.getId();
                }
                else if ((obj instanceof Integer) && ((Integer) obj == AKJOptionDialog.CANCEL_OPTION)) {
                    return;
                }
            }

            // Starte Report-Erstellung
            Long requestId = null;
            if (auftragDaten != null) {
                requestId = service.createReportRequest(rep.getId(), HurricanSystemRegistry.instance().getSessionId(),
                        kundeNoOrig, auftragDaten.getAuftragNoOrig(), auftragDaten.getAuftragId(), null, reasonId, false);
            }
            else {
                requestId = service.createReportRequest(rep.getId(), HurricanSystemRegistry.instance().getSessionId(),
                        kundeNoOrig, null, null, null, reasonId, false);
            }

            // Bearbeite Text-Bausteine
            if (requestId != null) {
                List<TxtBausteinGruppe> list = service.findAllTxtBausteinGruppen4Report(rep.getId());
                ReportRequest request = service.findReportRequest(requestId);
                if (CollectionTools.isNotEmpty(list)) {
                    for (TxtBausteinGruppe gruppe : list) {
                        if ((gruppe != null) && (gruppe.getId() != null)) {
                            TxtBausteinDialog dlg = new TxtBausteinDialog(request, gruppe);
                            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                        }
                    }
                }

                // Schicke Nachricht an Report-Server zur Bearbeitung des Reports
                service.sendReportRequest(request, HurricanSystemRegistry.instance().getSessionId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

}


