/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2004 08:54:44
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.tools.reports.jasper.AKJasperExportTypes;
import de.augustakom.common.tools.reports.jasper.AKJasperExporter;
import de.augustakom.hurrican.gui.auftrag.actions.CreateBuendelAction;
import de.augustakom.hurrican.gui.auftrag.actions.DeleteBuendelAction;
import de.augustakom.hurrican.gui.auftrag.shared.KundeAuftragViewTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.reporting.actions.PrintKundeAction;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel stellt Stammdaten eines Kunden sowie alle zugehoerigen Auftraege zu einem Kunden dar.
 */
public class AuftragUebersichtPanel extends AbstractDataPanel implements AKModelOwner,
        AKObjectSelectionListener, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(AuftragUebersichtPanel.class);

    private static final String FILTER_ALL = "--alle--";

    private static final String CMD_DETAILS = "kunde.details";
    private static final String CMD_PRINT = "print";
    private static final String CMD_PDF = "pdf";
    private static final String CMD_REPORT = "report";
    private static final String CMD_REFRESH = "refresh";
    private static final String CMD_LOAD_ALL = "load.all";

    // GUI fuer Kundendaten
    private AKJFormattedTextField tfKundeNoOrig = null;
    private AKJFormattedTextField tfHauptKundeNo = null;
    private AKJTextField tfName = null;
    private AKJTextField tfVorname = null;
    private AKJTextField tfStrasse = null;
    private AKJTextField tfPLZ = null;
    private AKJTextField tfOrt = null;
    private AKJButton btnKDetails = null;

    // Sonstige GUI-Komponenten
    private AKJLabel lblCount = null;
    private AKJComboBox cbPGruppe = null;
    private AKJComboBox cbStatus = null;
    private AKJButton btnLoadAll = null;
    private AKJButton btnPrint = null;
    private AKJButton btnPDF = null;
    private AKJButton btnRefresh = null;
    private AKJButton btnReport = null;

    private AKJTable tbAuftraege = null;
    private KundeAuftragViewTableModel tbMdlAuftraege = null;

    private KundeAdresseView model = null;
    private int dataCount = 0;

    private AKManageableComponent[] managedComponents;

    /**
     * Konstruktor
     */
    public AuftragUebersichtPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragUebersichtPanel.xml");
        createGUI();
        loadDefaults();
    }

    @Override
    protected final void createGUI() {
        // Kundendaten
        AKJLabel lblKundeNoOrig = getSwingFactory().createLabel("kunde.no.orig");
        AKJLabel lblHauptKundeNo = getSwingFactory().createLabel("haupt.kunde.no");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblVorname = getSwingFactory().createLabel("vorname");
        AKJLabel lblStrasse = getSwingFactory().createLabel("strasse");
        AKJLabel lblOrt = getSwingFactory().createLabel("ort");

        tfKundeNoOrig = getSwingFactory().createFormattedTextField("kunde.no.orig");
        tfHauptKundeNo = getSwingFactory().createFormattedTextField("haupt.kunde.no");
        tfName = getSwingFactory().createTextField("name");
        tfVorname = getSwingFactory().createTextField("vorname");
        tfStrasse = getSwingFactory().createTextField("strasse");
        tfPLZ = getSwingFactory().createTextField("plz");
        tfOrt = getSwingFactory().createTextField("ort");
        setFieldsEditable(false);
        btnKDetails = getSwingFactory().createButton(CMD_DETAILS, getActionListener());

        // GUI-Elemente fuer Filter
        AKJLabel lblAuftraege = getSwingFactory().createLabel("auftraege");
        lblCount = getSwingFactory().createLabel("count");
        AKJLabel lblPGruppe = getSwingFactory().createLabel("produktgruppe");
        AKJLabel lblStatus = getSwingFactory().createLabel("auftragstatus");
        cbPGruppe = getSwingFactory().createComboBox("produktgruppe");
        cbPGruppe.setRenderer(new AKCustomListCellRenderer<>(ProduktGruppe.class, ProduktGruppe::getProduktGruppe));
        cbPGruppe.addItemListener(this);
        cbStatus = getSwingFactory().createComboBox("auftragstatus");
        cbStatus.setRenderer(new AKCustomListCellRenderer<>(AuftragStatus.class, AuftragStatus::getStatusText));
        cbStatus.addItemListener(this);
        btnLoadAll = getSwingFactory().createButton(CMD_LOAD_ALL, getActionListener(), null);

        btnPrint = getSwingFactory().createButton(CMD_PRINT, getActionListener(), null);
        btnRefresh = getSwingFactory().createButton(CMD_REFRESH, getActionListener(), null);
        btnPDF = getSwingFactory().createButton(CMD_PDF, getActionListener(), null);
        btnReport = getSwingFactory().createButton(CMD_REPORT, getActionListener());

        // @formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblKundeNoOrig , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfKundeNoOrig  , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblHauptKundeNo, GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfHauptKundeNo , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        left.add(lblAuftraege   , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCount       , GBCFactory.createGBC(100,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(lblName         , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(tfName          , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblVorname      , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfVorname       , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel()  , GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        mid.add(lblPGruppe      , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(cbPGruppe       , GBCFactory.createGBC(100,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnRefresh    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnPrint      , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE, 7));
        btnPanel.add(btnPDF        , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.NONE, 7));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblStrasse    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfStrasse     , GBCFactory.createGBC(100,  0, 2, 0, 6, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblOrt        , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfPLZ         , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        right.add(tfOrt         , GBCFactory.createGBC(100,  0, 3, 1, 6, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 6, 2, 1, 1, GridBagConstraints.VERTICAL));
        right.add(lblStatus     , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbStatus      , GBCFactory.createGBC(100,  0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(btnLoadAll    , GBCFactory.createGBC(  0,  0, 4, 3, 1, 1, GridBagConstraints.NONE));
        right.add(btnKDetails   , GBCFactory.createGBC(  0,  0, 10, 0, 1, 1, GridBagConstraints.NONE));
        right.add(btnPanel      , GBCFactory.createGBC(  0,  0, 10, 1, 3, 1, GridBagConstraints.NONE));
        right.add(new AKJPanel(), GBCFactory.createGBC(  0,100, 10, 1, 3, 2, GridBagConstraints.NONE));
        right.add(btnReport     , GBCFactory.createGBC(  0,  0, 10, 3, 3, 1, GridBagConstraints.NONE));

        AKJPanel kunde = new AKJPanel(new GridBagLayout());
        kunde.add(left          , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        kunde.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        kunde.add(mid           , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.VERTICAL));
        kunde.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.NONE));
        kunde.add(right         , GBCFactory.createGBC(  0,  0, 4, 0, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        tbMdlAuftraege = new KundeAuftragViewTableModel();
        tbAuftraege = new AKJTable(tbMdlAuftraege, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftraege.attachSorter();

        // Actions fuer Kontext-Menue
        CreateBuendel createBuendel = new CreateBuendel();
        createBuendel.setParentClass(this.getClass());
        tbAuftraege.addPopupAction(createBuendel);

        DeleteBuendel deleteBuendel = new DeleteBuendel();
        deleteBuendel.setParentClass(this.getClass());
        tbAuftraege.addPopupAction(deleteBuendel);

        tbAuftraege.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbAuftraege.addKeyListener(getRefreshKeyListener());
        tbAuftraege.fitTable(new int[] { 70, 90, 70, 115, 70, 70, 25, 105, 105, 100, 70, 70, 25 });
        AKJScrollPane spTable = new AKJScrollPane(tbAuftraege, new Dimension(985, 400));

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(kunde          , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel() , GBCFactory.createGBC(100,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(spTable        , GBCFactory.createGBC(100,100, 0, 2, 2, 1, GridBagConstraints.BOTH));
        // @formatter:on

        managedComponents = new AKManageableComponent[] { createBuendel, deleteBuendel, btnReport };
        manageGUI(managedComponents);
    }

    @Override
    public void setModel(Observable model) {
        if (model instanceof KundeAdresseView) {
            this.model = (KundeAdresseView) model;
        }
        else {
            this.model = null;
        }

        readModel();
    }

    @Override
    public void readModel() {
        clearAll();
        loadAuftraege(true);
    }

    @Override
    protected void refresh() {
        loadAuftraege(true);
    }

    /* Laedt die CC-Auftraege fuer einen Kunden. */
    private void loadAuftraege(final boolean excludeKonsolidiert) {

        if (model != null) {
            enableGuiElements(false);
            setWaitCursor();
            showProgressBar("laden...");
            showKundendaten();

            final SwingWorker<List<CCKundeAuftragView>, Void> worker = new SwingWorker<List<CCKundeAuftragView>, Void>() {
                private final Long kundeNo = model.getKundeNo();

                @Override
                protected List<CCKundeAuftragView> doInBackground() throws Exception {
                    CCKundenService service = getCCService(CCKundenService.class);
                    List<CCKundeAuftragView> result = service.findKundeAuftragViews4Kunde(kundeNo, excludeKonsolidiert);
                    return result;
                }

                @Override
                protected void done() {
                    try {
                        List<CCKundeAuftragView> result = get();
                        tbMdlAuftraege.setData(result);

                        dataCount = (result != null) ? result.size() : 0;
                        showRowCount();
                        enableGuiElements(true);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        stopProgressBar();
                        setDefaultCursor();
                    }
                }

            };

            worker.execute();
        }
    }

    /* Zeigt die Kundendaten in den TextFields an. */
    private void showKundendaten() {
        tfKundeNoOrig.setValue(model.getKundeNo());
        tfHauptKundeNo.setValue(model.getHauptKundenNo());
        tfName.setText(model.getName());
        tfVorname.setText(model.getVorname());
        tfPLZ.setText(StringUtils.trimToEmpty(model.getPlz()));
        tfOrt.setText(model.getCombinedOrtOrtsteil());
        tfStrasse.setText(model.getStrasseWithNumber());
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    /* Setzt den Editable-Status der Text-Fields auf <code>editable</code> */
    private void setFieldsEditable(boolean editable) {
        tfKundeNoOrig.setEditable(editable);
        tfHauptKundeNo.setEditable(editable);
        tfName.setEditable(editable);
        tfVorname.setEditable(editable);
        tfStrasse.setEditable(editable);
        tfPLZ.setEditable(editable);
        tfOrt.setEditable(editable);
    }

    /* Setzt den Status der Buttons und Checkboxen */
    private void enableGuiElements(boolean enable) {
        btnLoadAll.setEnabled(enable);
        btnRefresh.setEnabled(enable);
        btnKDetails.setEnabled(enable);
        btnPDF.setEnabled(enable);
        btnPrint.setEnabled(enable);
        btnReport.setEnabled(enable);
        cbPGruppe.setEnabled(enable);
        cbStatus.setEnabled(enable);
        if (enable) {
            manageGUI(managedComponents);
        }
    }

    /* 'Loescht den Inhalt aller Felder und der Tabelle */
    private void clearAll() {
        tbMdlAuftraege.removeAll();
        lblCount.setText("");

        tfKundeNoOrig.setValue(null);
        tfHauptKundeNo.setValue(null);
        tfName.setText("");
        tfVorname.setText("");
        tfStrasse.setText("");
        tfPLZ.setText("");
        tfOrt.setText("");

        try {
            cbPGruppe.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
        }
        catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    /* Laedt alle Standard-Daten */
    private void loadDefaults() {
        loadProduktGruppen();
        loadAuftragStati();
    }

    /* Laedt alle verfuegbaren ProduktGruppen und uebergibt sie der ComboBox */
    private void loadProduktGruppen() {
        List<ProduktGruppe> produktGruppen = null;
        try {
            ProduktService ps = getCCService(ProduktService.class);
            produktGruppen = ps.findProduktGruppen();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (produktGruppen == null) {
            produktGruppen = new ArrayList<ProduktGruppe>();
        }

        ProduktGruppe allPG = new ProduktGruppe();
        allPG.setProduktGruppe(FILTER_ALL);
        produktGruppen.add(0, allPG);

        DefaultComboBoxModel cbMdlPGruppe = new DefaultComboBoxModel();
        cbPGruppe.copyList2Model(produktGruppen, cbMdlPGruppe);
        cbPGruppe.setModel(cbMdlPGruppe);
    }

    /* Laedt alle AuftragStatis und uebergibt sie der ComboBox */
    private void loadAuftragStati() {
        List<AuftragStatus> auftragStati = null;
        try {
            // AuftragStati laden
            CCAuftragService as = getCCService(CCAuftragService.class);
            auftragStati = as.findAuftragStati();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (auftragStati == null) {
            auftragStati = new ArrayList<AuftragStatus>();
        }

        AuftragStatus allAS = new AuftragStatus();
        allAS.setStatusText(FILTER_ALL);
        auftragStati.add(0, allAS);

        DefaultComboBoxModel cbMdlAS = new DefaultComboBoxModel();
        cbStatus.copyList2Model(auftragStati, cbMdlAS);
        cbStatus.setModel(cbMdlAS);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return model;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (CMD_DETAILS.equals(command)) {
            if (this.model != null) {
                KundeDetailsDialog dlg = new KundeDetailsDialog(this.model);
                DialogHelper.showDialog(this, dlg, true, true);
            }
        }
        else if (CMD_PRINT.equals(command)) {
            printAuftragDetails(AKJasperExportTypes.EXPORT_TYPE_PRINT);
        }
        else if (CMD_PDF.equals(command)) {
            printAuftragDetails(AKJasperExportTypes.EXPORT_TYPE_PDF);
        }
        else if (CMD_REFRESH.equals(command)) {
            refresh();
        }
        else if (CMD_LOAD_ALL.equals(command)) {
            loadAuftraege(false);
        }
        else if (CMD_REPORT.equals(command)) {
            PrintKundeAction action = new PrintKundeAction();
            action.putValue(PrintKundeAction.MODEL_OWNER, this);
            action.actionPerformed(new ActionEvent(this, 0, CMD_REPORT));
        }
    }

    /* Erstellt einen Ausdruck der Auftragsdaten fuer den aktuellen Kunden. */
    private void printAuftragDetails(int exportType) {
        try {
            if (this.model != null) {
                setWaitCursor();
                CCKundenService ks = getCCService(CCKundenService.class);
                JasperPrint jp = ks.reportKundeAuftragViews(this.model.getKundeNo());

                switch (exportType) {
                    case AKJasperExportTypes.EXPORT_TYPE_PRINT:
                        JasperPrintManager.printReport(jp, true);
                        break;
                    case AKJasperExportTypes.EXPORT_TYPE_PDF:
                        File pdf = File.createTempFile("kunde_" + model.getKundeNo() + "-", ".pdf");
                        pdf.deleteOnExit();
                        AKJasperExporter.exportReport(jp, exportType, pdf.getAbsolutePath(), null);
                        Desktop.getDesktop().open(pdf);
                        break;
                    case AKJasperExporter.EXPORT_TYPE_HTML:
                        File html = File.createTempFile("kunde_" + model.getKundeNo() + "-", ".html");
                        html.deleteOnExit();
                        AKJasperExporter.exportReport(jp, exportType, html.getAbsolutePath(), null);
                        Desktop.getDesktop().open(html);
                        break;
                    default:
                        break;
                }
            }
            else {
                throw new Exception("Es ist kein Kunde ausgewählt, für den eine Übersicht gedruckt werden könnte.");
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

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Zeigt die Anzahl der angezeigten und aller Datensaetze an. */
    private void showRowCount() {
        StringBuilder count = new StringBuilder();
        count.append(tbMdlAuftraege.getRowCount());
        count.append(" / ");
        count.append(dataCount);
        lblCount.setText(count.toString());
    }


    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cbPGruppe) {
            tbMdlAuftraege.removeFilter(KundeAuftragViewTableModel.FILTERNAME_PRODUKTGRUPPE);
            tbMdlAuftraege.addFilter(new FilterOperator(KundeAuftragViewTableModel.FILTERNAME_PRODUKTGRUPPE, cbPGruppe.getSelectedItem()) {
                @Override
                public boolean filter(@SuppressWarnings("rawtypes") AKMutableTableModel model, int row) {
                    CCKundeAuftragView view = (CCKundeAuftragView) model.getDataAtRow(row);
                    ProduktGruppe filter = (ProduktGruppe) filterValue;
                    if ((filter.getId() != null) && !filter.getId().equals(view.getProduktGruppeId())) {
                        return false;
                    }
                    return true;
                }
            });
        }
        else if (e.getSource() == cbStatus) {
            tbMdlAuftraege.removeFilter(KundeAuftragViewTableModel.FILTERNAME_AUFTRAGSTATUS);
            tbMdlAuftraege.addFilter(new FilterOperator(KundeAuftragViewTableModel.FILTERNAME_AUFTRAGSTATUS, cbStatus.getSelectedItem()) {
                @Override
                public boolean filter(@SuppressWarnings("rawtypes") AKMutableTableModel model, int row) {
                    CCKundeAuftragView view = (CCKundeAuftragView) model.getDataAtRow(row);
                    AuftragStatus filter = (AuftragStatus) filterValue;
                    if ((filter.getId() != null) && !filter.getId().equals(view.getAuftragStatusId())) {
                        return false;
                    }
                    return true;
                }
            });
        }
        showRowCount();
    }

    /* Methode ruft die Action createBuendelAction, um eine Bündelnummer zuzuordnen
     * im Anschluss wird die Bündel-ID in der Auftragsübersicht aktualisiert  */
    private void createBuendelActionCall() {
        try {
            int selectedRow = tbAuftraege.getSelectedRow();
            CreateBuendelAction action = new CreateBuendelAction();

            action.perform(((AKMutableTableModel<CCKundeAuftragView>) tbAuftraege.getModel()).getDataAtRow(selectedRow));
            refresh();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Methode ruft die Action deleteBuendelAction auf, um eine Bündelnummer zu entfernen
     * im Anschluss wird die Bündel-ID in der Auftragsübersicht aktualisiert  */
    private void deleteBuendelActionCall() {
        try {
            int selectedRow = tbAuftraege.getSelectedRow();
            DeleteBuendelAction action = new DeleteBuendelAction();
            action.perform(((AKMutableTableModel<CCKundeAuftragView>) tbAuftraege.getModel()).getDataAtRow(selectedRow));
            refresh();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /* Action, um eine Bündelnummer zuzuordnen */
    class CreateBuendel extends AKAbstractAction {
        public CreateBuendel() {
            super();
            setName("Bündel zuordnen...");
            setTooltip("Ordnet den Auftrag einem Bündel zu");
            setActionCommand("create.buendel");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            createBuendelActionCall();
        }
    }

    /* Action, um die Bündelnummer zu entfernen. */
    class DeleteBuendel extends AKAbstractAction {
        public DeleteBuendel() {
            super();
            setName("Bündel entfernen...");
            setTooltip("Entfernt die Bündelnummer aus dem ausgewählten Auftrag");
            setActionCommand("delete.buendel");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteBuendelActionCall();
        }
    }


}
