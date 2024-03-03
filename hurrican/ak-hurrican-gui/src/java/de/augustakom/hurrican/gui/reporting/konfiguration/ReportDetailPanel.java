/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 08:53:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.reporting.TxtBausteinDialog;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportGruppe;
import de.augustakom.hurrican.model.reporting.ReportPaperFormat;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.reporting.ReportConfigService;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Panel fuer die Administration der Reports.
 *
 *
 */
public class ReportDetailPanel extends AbstractAdminPanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ReportDetailPanel.class);

    private AKJButton btnTest = null;

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextArea taDesc = null;
    private AKJComboBox cbType = null;
    private AKJComboBox cbGroup = null;
    private AKJComboBox cbPageOne = null;
    private AKJComboBox cbPageTwo = null;
    private AKJCheckBox cbDuplex = null;

    private Report detail = null;
    private AbstractAdminPanel adminPanel = null;

    /**
     * Standardkonstruktor
     */
    public ReportDetailPanel(AbstractAdminPanel adminPanel) {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/ReportDetailPanel.xml");
        this.adminPanel = adminPanel;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblDesc = getSwingFactory().createLabel("beschreibung");
        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblType = getSwingFactory().createLabel("type");
        AKJLabel lblPrinter = getSwingFactory().createLabel("printer");
        AKJLabel lblPageOne = getSwingFactory().createLabel("page.one");
        AKJLabel lblPageTwo = getSwingFactory().createLabel("page.two");
        AKJLabel lblGroup = getSwingFactory().createLabel("group");
        AKJLabel lblDuplex = getSwingFactory().createLabel("duplex");

        tfName = getSwingFactory().createTextField("name");
        taDesc = getSwingFactory().createTextArea("beschreibung");
        tfId = getSwingFactory().createFormattedTextField("id");
        tfId.setEditable(false);
        cbType = getSwingFactory().createComboBox("type", false);
        AKJScrollPane spDesc = new AKJScrollPane(taDesc, new Dimension(100, 100));
        cbPageOne = getSwingFactory().createComboBox("page.one", new AKCustomListCellRenderer<>(ReportPaperFormat.class, ReportPaperFormat::getName));
        cbPageTwo = getSwingFactory().createComboBox("page.two", new AKCustomListCellRenderer<>(ReportPaperFormat.class, ReportPaperFormat::getName));
        cbGroup = getSwingFactory().createComboBox("group", new AKCustomListCellRenderer<>(ReportGruppe.class, ReportGruppe::getName));
        cbDuplex = getSwingFactory().createCheckBox("duplex", false);

        // Panel mit Detail-Informationen
        // @formatter:off
        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(lblId         , GBCFactory.createGBC(  0,  0, 1,  1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfId          , GBCFactory.createGBC(100,  0, 3,  1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblName       , GBCFactory.createGBC(  0,  0, 1,  2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 2,  2, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfName        , GBCFactory.createGBC(100,  0, 3,  2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblType       , GBCFactory.createGBC(  0,  0, 1,  3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbType        , GBCFactory.createGBC(100,  0, 3,  3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblGroup      , GBCFactory.createGBC(  0,  0, 1,  4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbGroup       , GBCFactory.createGBC(100,  0, 3,  4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblDesc       , GBCFactory.createGBC(  0,  0, 1,  5, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(spDesc        , GBCFactory.createGBC(100,100, 3,  5, 1, 1, GridBagConstraints.BOTH));
        dataPanel.add(lblPrinter    , GBCFactory.createGBC(  0,  0, 1,  6, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblDuplex     , GBCFactory.createGBC(  0,  0, 1,  7, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbDuplex      , GBCFactory.createGBC(100,  0, 3,  7, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblPageOne    , GBCFactory.createGBC(  0,  0, 1,  8, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbPageOne     , GBCFactory.createGBC(100,  0, 3,  8, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblPageTwo    , GBCFactory.createGBC(  0,  0, 1,  9, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbPageTwo     , GBCFactory.createGBC(100,  0, 3,  9, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(100,  0, 4, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        // Panel mit Buttons für Neu und Save
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        AKJButton btnNew = getSwingFactory().createButton("new", getActionListener());
        AKJButton btnSave = getSwingFactory().createButton("save", getActionListener());
        btnTest = getSwingFactory().createButton("test", getActionListener());

        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnNew, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnTest, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnSave, btnNew, btnTest);

        AKJPanel detailPanel = new AKJPanel(new GridBagLayout());
        detailPanel.add(dataPanel, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(btnPanel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        setElementsEditable(false);

        this.setLayout(new BorderLayout());
        this.add(detailPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        this.detail = null;
        clear();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {

    }

    /* 'Loescht' die TextFields */
    private void clear() {
        GuiTools.cleanFields(this);
        setElementsEditable(true);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        if (detail != null) {
            tfId.setValue(detail.getId());
            tfName.setText(detail.getName());
            taDesc.setText(detail.getDescription());
            cbType.selectItemWithValue(detail.getType());
            cbDuplex.setSelected(detail.getDuplexDruck());
            cbGroup.selectItem("getId", ReportGruppe.class, detail.getReportGruppeId());
            cbPageOne.selectItem("getId", ReportPaperFormat.class, detail.getFirstPage());
            cbPageTwo.selectItem("getId", ReportPaperFormat.class, detail.getSecondPage());
        }
        setElementsEditable(true);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() {
        if (detail != null) {
            try {
                ReportConfigService service = getReportService(ReportConfigService.class);
                service.saveReport(detail);

                // Falls Report gespeichert wurde oder verändert, füge ihn der Übersichtstabelle hinzu
                if (detail.getId() != null) {
                    ((ReportAdminPanel) adminPanel).addReport(detail);
                    ((ReportAdminPanel) adminPanel).tableChanged();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
                this.detail = null;
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        if (detail == null) {
            detail = new Report();
        }

        try {
            // Aktuellen User ermitteln
            Long sessionId = HurricanSystemRegistry.instance().getSessionId();
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService userService = (AKUserService) authSL.getService(AKAuthenticationServiceNames.USER_SERVICE, null);
            AKUser user = userService.findUserBySessionId(sessionId);

            // Daten setzen
            detail.setUserw(user.getLoginName());
            detail.setName(tfName.getText(null));
            detail.setDescription(taDesc.getText(null));
            detail.setType((Long) cbType.getSelectedItemValue());
            detail.setDuplexDruck(cbDuplex.isSelectedBoolean());
            detail.setReportGruppeId(((ReportGruppe) cbGroup.getSelectedItem()).getId());
            detail.setFirstPage(((ReportPaperFormat) cbPageOne.getSelectedItem()).getId());
            detail.setSecondPage(((ReportPaperFormat) cbPageTwo.getSelectedItem()).getId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }

        return detail;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.detail = (model instanceof Report) ? (Report) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save".equals(command)) {
            getModel();
            saveModel();
            adminPanel.showDetails(detail);
        }
        else if ("new".equals(command)) {
            adminPanel.createNew();
        }
        else if ("test".equals(command)) {
            testReport();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            // Lade Papierarten
            ReportService service = getReportService(ReportService.class);
            List<ReportPaperFormat> papers = service.findAllReportPaperFormat();
            cbPageOne.addItems(papers, true, ReportPaperFormat.class, true);
            cbPageTwo.addItems(papers, true, ReportPaperFormat.class, true);

            // Lade Reportgruppen
            ReportConfigService configService = getReportService(ReportConfigService.class);
            List<ReportGruppe> groups = configService.findAllReportGroups();
            cbGroup.addItems(groups, true, ReportGruppe.class, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
    }

    /*
     * Funktion setzt die Gui-Element editierbar
     */
    private void setElementsEditable(boolean edit) {
        cbType.setEnabled(edit);
        tfName.setEditable(edit);
        taDesc.setEditable(edit);
        cbGroup.setEnabled(edit);
        cbPageOne.setEnabled(edit);
        cbPageTwo.setEnabled(edit);
        btnTest.setEnabled(edit);
        cbDuplex.setEnabled(edit);
    }

    /*
     * Funktion stoesst zu Testzwecken eine Report-Erstellung an
     */
    private void testReport() {
        try {
            Long auftragId = null;
            Long kundeNoOrig = null;
            Long orderNoOrig = null;
            AuftragDaten auftragDaten = null;
            ReportService service = getReportService(ReportService.class);

            // Ermittle fehlenden Daten
            String auftragIdString = MessageHelper.showInputDialog("AuftragId:");
            if (StringUtils.isNotBlank(auftragIdString) && StringUtils.isNumeric(auftragIdString)) {
                auftragId = Long.valueOf(auftragIdString);

                if (auftragId != null) {
                    CCAuftragService as = getCCService(CCAuftragService.class);

                    // Ermittle KundeNo
                    Auftrag auftrag = as.findAuftragById(auftragId);
                    if (auftrag != null) {
                        kundeNoOrig = auftrag.getKundeNo();
                    }

                    // Ermittle orderNo
                    auftragDaten = as.findAuftragDatenByAuftragId(auftragId);
                    if (auftragDaten != null) {
                        orderNoOrig = auftragDaten.getAuftragNoOrig();
                    }
                }
            }

            if ((detail != null) && (detail.getId() != null) && (auftragId != null) && (kundeNoOrig != null)) {
                // Prüfe ob Report für aktuelles Produkt verfügbar
                List<Report> reports = service.findAvailableReports(detail.getType(), auftragDaten, HurricanSystemRegistry.instance().getSessionId());
                Map map = new HashMap();
                CollectionMapConverter.convert2Map(reports, map, "getId", null);
                if (!map.containsKey(detail.getId())) {
                    Integer question = MessageHelper.showYesNoQuestion(this,
                            "Report ist für ausgewählten Auftrag nicht verfügbar! Soll der Report trotzdem erstellt werden?", null);
                    if (question != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                // Starte Report-Erstellung
                Long requestId = service.createReportRequest(detail.getId(), HurricanSystemRegistry.instance().getSessionId(),
                        kundeNoOrig, orderNoOrig, auftragId, null, null, false);

                // Bearbeite Text-Bausteine
                if (requestId != null) {
                    List<TxtBausteinGruppe> list = service.findAllTxtBausteinGruppen4Report(detail.getId());
                    ReportRequest request = service.findReportRequest(requestId);
                    if (CollectionTools.isNotEmpty(list)) {
                        for (TxtBausteinGruppe gruppe : list) {
                            if ((gruppe != null) && (gruppe.getId() != null)) {
                                TxtBausteinDialog dlg = new TxtBausteinDialog(request, gruppe);
                                DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                            }
                        }
                    }
                    // Markiere ReportRequest als Test
                    request.setError("Test");
                    service.saveReportRequest(request);

                    // Schicke Nachricht an Report-Server zur Bearbeitung des Reports
                    service.sendReportRequest(request, HurricanSystemRegistry.instance().getSessionId());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }
}
