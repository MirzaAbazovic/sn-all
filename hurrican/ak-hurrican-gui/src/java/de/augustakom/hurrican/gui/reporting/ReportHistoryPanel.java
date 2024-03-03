/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2007 12:03:10
 */
package de.augustakom.hurrican.gui.reporting;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.view.ReportRequestView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Panel zur Anzeige der ReportHistorie <br>
 *
 *
 */
public class ReportHistoryPanel extends AbstractDataPanel implements AKTableOwner, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(ReportHistoryPanel.class);
    private static final String ARCHIV_STATUS_NULL = "unbekannt";
    private static final String ARCHIV_STATUS_TRUE = "Zur Archivierung vorgesehen";
    private static final String ARCHIV_STATUS_FALSE = "Keine Archivierung";
    private static final String ARCHIV_STATUS_OK = "Report archiviert";


    // GUI-Komponenten
    private AKJFormattedTextField tfReportId = null;
    private AKJFormattedTextField tfBuendelNo = null;
    private AKJTextField tfReportName = null;
    private AKJTextArea taReportDesc = null;
    private AKJTextField tfRequestFrom = null;
    private AKJTextArea taRequestError = null;
    private AKJDateComponent dcRequestAt = null;
    private AKJTextField tfArchivStatus = null;
    private AKJDateComponent dcReportDownloaded = null;
    private AKJDateComponent dcReportArchived = null;
    private AKJDateComponent dcFilterBegin = null;
    private AKJDateComponent dcFilterEnde = null;
    private AKJButton btnReport = null;
    private AKJButton btnSerienbrief = null;
    private AKJButton btnArchiv = null;

    private AKJTable tbReports = null;
    private AKReflectionTableModel<ReportRequestView> tbMdlReports = null;

    private Long auftragId = null;
    private Long kundeNoOrig = null;
    private Date filterBegin = null;
    private Date filterEnde = null;

    private ReportRequestView detail = null;

    private boolean guiCreated = false;

    /**
     * Default-Const.
     */
    public ReportHistoryPanel() {
        super("de/augustakom/hurrican/gui/reporting/resources/ReportHistoryPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        if (guiCreated) { return; }
        // Table für Reports
        tbMdlReports = new AKReflectionTableModel<ReportRequestView>(
                new String[] { "ID", "Report", "Bündelnr.", "Tech. Auftragsnr.", "User", "Erstellt am", "Heruntergeladen am", "Wiederholter Druck", "RFA" },
                new String[] { "requestId", "reportName", "buendelNo", "auftragId", "requestFrom", "requestAt", "reportDownloadedAt", "printReason", "reportRFA" },
                new Class[] { Long.class, String.class, Integer.class, Long.class, String.class, Date.class,
                        Date.class, String.class, Boolean.class }
        );

        tbReports = new AKJTable(tbMdlReports, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbReports.attachSorter();
        tbReports.addTableListener(this);
        tbReports.addMouseListener(new AKTableDoubleClickMouseListener(this));
        RePrintAction printAction = new RePrintAction();
        printAction.setParentClass(this.getClass());
        tbReports.addPopupAction(printAction);
        tbReports.fitTable(new int[] { 50, 200, 100, 100, 100, 100, 100, 100, 30 });
        TableColumn tcFinished = tbReports.getColumnModel().getColumn(4);
        tcFinished.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME));
        TableColumn tcDownload = tbReports.getColumnModel().getColumn(5);
        tcDownload.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME));

        AKJScrollPane tableSP = new AKJScrollPane(tbReports, new Dimension(800, 250));

        AKJLabel lblReportId = getSwingFactory().createLabel("report.id");
        AKJLabel lblReportName = getSwingFactory().createLabel("report.name");
        AKJLabel lblBuendelNo = getSwingFactory().createLabel("report.buendel.no");
        AKJLabel lblReportDesc = getSwingFactory().createLabel("report.desc");
        AKJLabel lblRequestFrom = getSwingFactory().createLabel("request.from");
        AKJLabel lblRequestAt = getSwingFactory().createLabel("request.at");
        AKJLabel lblArchivStatus = getSwingFactory().createLabel("request.archivstatus");
        AKJLabel lblReportDownloaded = getSwingFactory().createLabel("report.downloaded");
        AKJLabel lblReportArchived = getSwingFactory().createLabel("report.archived");
        AKJLabel lblRequestError = getSwingFactory().createLabel("request.error");
        AKJLabel lblFilterVon = getSwingFactory().createLabel("filter.von");
        AKJLabel lblFilterBis = getSwingFactory().createLabel("filter.bis");

        tfReportId = getSwingFactory().createFormattedTextField("report.id", false);
        tfBuendelNo = getSwingFactory().createFormattedTextField("report.buendel.no", false);
        tfReportName = getSwingFactory().createTextField("report.name", false);
        taReportDesc = getSwingFactory().createTextArea("report.desc", false);
        AKJScrollPane spReportDesc = new AKJScrollPane(taReportDesc, new Dimension(300, 30));
        taRequestError = getSwingFactory().createTextArea("request.error", false);
        AKJScrollPane spRequestError = new AKJScrollPane(taRequestError, new Dimension(300, 50));
        tfRequestFrom = getSwingFactory().createTextField("request.from", false);
        dcRequestAt = getSwingFactory().createDateComponent("request.at", false);
        tfArchivStatus = getSwingFactory().createTextField("request.archivstatus", false);
        dcReportDownloaded = getSwingFactory().createDateComponent("report.downloaded", false);
        dcReportArchived = getSwingFactory().createDateComponent("report.archived", false);
        btnReport = getSwingFactory().createButton("download.report", getActionListener());
        btnSerienbrief = getSwingFactory().createButton("download.serienbrief", getActionListener());
        btnArchiv = getSwingFactory().createButton("report.archiv", getActionListener());
        AKJButton btnRefresh = getSwingFactory().createButton("refresh", getActionListener());
        dcFilterBegin = getSwingFactory().createDateComponent("filter.von");
        dcFilterEnde = getSwingFactory().createDateComponent("filter.bis");

        AKJPanel filterPnl = new AKJPanel(new GridBagLayout(), "Filter");
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(lblFilterVon, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(dcFilterBegin, GBCFactory.createGBC(50, 0, 3, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(lblFilterBis, GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(dcFilterEnde, GBCFactory.createGBC(50, 0, 8, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 10, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(btnRefresh, GBCFactory.createGBC(0, 0, 11, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 12, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel tempPnl = new AKJPanel(new GridBagLayout(), "Report-Vorlage");
        tempPnl.add(lblReportId, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        tempPnl.add(tfReportId, GBCFactory.createGBC(0, 0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        tempPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        tempPnl.add(lblReportName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPnl.add(tfReportName, GBCFactory.createGBC(0, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        tempPnl.add(lblReportDesc, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPnl.add(spReportDesc, GBCFactory.createGBC(100, 100, 2, 2, 2, 2, GridBagConstraints.BOTH));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnReport, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnSerienbrief, GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnArchiv, GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.NONE));

        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), "Report");
        detPnl.add(lblRequestFrom, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(tfRequestFrom, GBCFactory.createGBC(100, 0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(lblBuendelNo, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(tfBuendelNo, GBCFactory.createGBC(100, 0, 7, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 9, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(lblRequestAt, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(dcRequestAt, GBCFactory.createGBC(100, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblArchivStatus, GBCFactory.createGBC(0, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(tfArchivStatus, GBCFactory.createGBC(100, 0, 7, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblReportDownloaded, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(dcReportDownloaded, GBCFactory.createGBC(100, 0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblReportArchived, GBCFactory.createGBC(0, 0, 5, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(dcReportArchived, GBCFactory.createGBC(100, 0, 7, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblRequestError, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(spRequestError, GBCFactory.createGBC(100, 100, 2, 3, 7, 2, GridBagConstraints.BOTH));
        detPnl.add(btnPnl, GBCFactory.createGBC(100, 100, 0, 5, 9, 2, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(filterPnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tableSP, GBCFactory.createGBC(100, 100, 0, 1, 1, 2, GridBagConstraints.BOTH));
        this.add(tempPnl, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(detPnl, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnReport, btnSerienbrief, btnArchiv, printAction);

        guiCreated = true;
        cleanFields();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        tbMdlReports.removeAll();
        try {
            setWaitCursor();

            ReportService service = getReportService(ReportService.class);
            List<ReportRequestView> list = null;
            if ((auftragId == null) && (kundeNoOrig != null)) {
                list = service.findReportRequest4Auftrag(kundeNoOrig, null, filterBegin, filterEnde);
            }
            else if (auftragId != null) {
                list = service.findReportRequest4Auftrag(null, auftragId, filterBegin, filterEnde);
            }
            else {
                // Zeige offene Reports
                list = service.findReportRequest4Auftrag(null, null, filterBegin, filterEnde);
            }

            tbMdlReports.setData(list);
            tbMdlReports.fireTableDataChanged();
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
     * Funktion setzt GUI-Elemente zurück
     */
    private void cleanFields() {
        GuiTools.cleanFields(this);

        // Deaktiviere Download-Button
        btnReport.setEnabled(Boolean.FALSE);
        btnSerienbrief.setEnabled(Boolean.FALSE);
        btnArchiv.setEnabled(Boolean.FALSE);
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof ReportRequestView) {
            this.detail = (ReportRequestView) details;
            tfReportId.setValue(detail.getReportId().toString());
            tfReportName.setText(detail.getReportName());
            taReportDesc.setText(detail.getReportDescription());
            tfRequestFrom.setText(detail.getRequestFrom());
            if (detail.getBuendelNo() != null) {
                tfBuendelNo.setValue(detail.getBuendelNo().toString());
            }
            else {
                tfBuendelNo.setValue(null);
            }
            dcRequestAt.setDate(detail.getRequestAt());
            dcReportDownloaded.setDate(detail.getReportDownloadedAt());
            dcReportArchived.setDate(detail.getReportArchivedAt());
            taRequestError.setText(detail.getError());
            // Aktiviere Download-Buttons
            try {
                btnReport.setEnabled(Boolean.FALSE);
                btnSerienbrief.setEnabled(Boolean.FALSE);
                if (StringUtils.isNotBlank(detail.getFile())) {
                    String fileName = System.getProperty("report.path.output") + detail.getFile();

                    File test = new File(fileName);
                    if (test.exists() && test.canRead()) {
                        btnReport.setEnabled(Boolean.TRUE);
                        if (detail.getBuendelNo() != null) {
                            btnSerienbrief.setEnabled(Boolean.TRUE);
                        }
                    }
                }
                // Archiv-Button
                if (detail.getReportRFA() == null) {
                    btnArchiv.setEnabled(true);
                }
                else {
                    btnArchiv.setEnabled(false);
                }
                // Archiv-Status
                if (detail.getReportArchivedAt() != null) {
                    tfArchivStatus.setText(ARCHIV_STATUS_OK);
                }
                else {
                    if (detail.getReportRFA() == null) {
                        tfArchivStatus.setText(ARCHIV_STATUS_NULL);
                    }
                    else if (detail.getReportRFA().booleanValue()) {
                        tfArchivStatus.setText(ARCHIV_STATUS_TRUE);
                    }
                    else {
                        tfArchivStatus.setText(ARCHIV_STATUS_FALSE);
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        else {
            detail = null;
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        // Setze GUI-Elemente zurück
        cleanFields();
        detail = null;
        kundeNoOrig = null;
        auftragId = null;
        Date date = null;

        if (model instanceof CCAuftragModel) {
            auftragId = ((CCAuftragModel) model).getAuftragId();
        }
        else if (model instanceof KundenModel) {
            kundeNoOrig = ((KundenModel) model).getKundeNo();
        }
        else {
            // Zeige offene Reports, filter nach heutigem Datum
            date = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        }
        // Setze Filter
        filterEnde = date;
        dcFilterEnde.setDate(date);
        filterBegin = date;
        dcFilterBegin.setDate(date);

        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection == detail) {
            execute("download.report");
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        // Download Report
        if ("download.report".equals(command) && (detail != null)) {
            try {
                setWaitCursor();

                ReportService rs = getReportService(ReportService.class);
                ReportRequest request = rs.findReportRequest(detail.getRequestId());

                ShowReportFrame.openFrame(request, null, null);

                // Aktualisiere Ansicht
                detail.setReportDownloadedAt(request.getReportDownloadedAt());
                tbMdlReports.fireTableDataChanged();
                showDetails(detail);

            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
        // Download Serienbrief
        if ("download.serienbrief".equals(command) && (detail != null)) {
            try {
                setWaitCursor();

                ReportService rs = getReportService(ReportService.class);
                ReportRequest request = rs.findReportRequest(detail.getRequestId());

                ShowReportFrame.openFrame(null, request.getBuendelNo(), null);

                // Aktualisiere Ansicht
                detail.setReportDownloadedAt(request.getReportDownloadedAt());
                tbMdlReports.fireTableDataChanged();
                showDetails(detail);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
        if ("refresh".equals(command)) {
            // Ermittle Filter
            filterBegin = dcFilterBegin.getDate(null);
            filterEnde = dcFilterEnde.getDate(null);

            // Setze Auswahl zurück
            detail = null;
            cleanFields();

            dcFilterBegin.setDate(filterBegin);
            dcFilterEnde.setDate(filterEnde);

            // Lade TableModel neu
            readModel();
        }
        if ("report.archiv".equals(command)) {
            changeReportArchivFlag();
        }
    }

    /*
     * Funktion ändert den Wert RFA (Ready for Archiv) des aktuellen ReportRequest-Objekts
     */
    private void changeReportArchivFlag() {
        if ((detail == null) || (detail.getRequestId() == null)) {
            return;
        }
        try {
            ReportService repService = getReportService(ReportService.class);
            ReportRequest request = repService.findReportRequest(detail.getRequestId());
            if (request != null) {
                if (request.getRfa() == null) {
                    request.setRfa(Boolean.FALSE);
                }
                else {
                    request.setRfa(!request.getRfa());
                }
                repService.saveReportRequest(request);

                // Aktualisiere Tabelle
                detail.setReportRFA(request.getRfa());
                tbMdlReports.fireTableDataChanged();
                showDetails(detail);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Action, um den Report erneut vom ReportServer erzeugen zu lassen. */
    class RePrintAction extends AKAbstractAction {
        public RePrintAction() {
            setName("Report neu erzeugen");
            setTooltip("Report erneut erzeugen lassen");
            setActionCommand("re.print");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ReportRequestView view = (ReportRequestView) ((AKMutableTableModel) tbReports.getModel()).getDataAtRow(tbReports.getSelectedRow());

                ReportService repService = getReportService(ReportService.class);
                ReportRequest request = repService.findReportRequest(view.getRequestId());
                if (request != null) {
                    int opt = MessageHelper.showYesNoQuestion(getMainFrame(),
                            "Soll der Report erneut erzeugt werden?", "Erneut erzeugen?");
                    if (opt == JOptionPane.YES_OPTION) {
                        repService.sendReportRequest(request, HurricanSystemRegistry.instance().getSessionId());
                    }
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

}
