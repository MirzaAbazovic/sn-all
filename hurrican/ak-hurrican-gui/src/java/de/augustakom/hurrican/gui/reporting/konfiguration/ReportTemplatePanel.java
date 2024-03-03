/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 08:53:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportTemplate;
import de.augustakom.hurrican.service.reporting.ReportConfigService;


/**
 * Panel fuer die Administration der Reports.
 *
 *
 */
public class ReportTemplatePanel extends AbstractAdminPanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ReportTemplatePanel.class);

    private AKReflectionTableModel<ReportTemplate> tbModelTemplates = null;

    private AKJButton btnUp = null;
    private AKJButton btnDown = null;

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfFilename = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;

    private ReportTemplate detail = null;
    private Report report = null;

    /**
     * Standardkonstruktor
     */
    public ReportTemplatePanel() {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/ReportTemplatePanel.xml");
        createGUI();

    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("gueltig.bis");
        AKJLabel lblFilename = getSwingFactory().createLabel("filename");

        tfId = getSwingFactory().createFormattedTextField("id");
        dcGueltigVon = getSwingFactory().createDateComponent("gueltig.von");
        dcGueltigBis = getSwingFactory().createDateComponent("gueltig.bis");
        tfFilename = getSwingFactory().createTextField("filename");
        btnUp = getSwingFactory().createButton("upload", getActionListener(), null);
        btnDown = getSwingFactory().createButton("download", getActionListener(), null);

        // Panel mit Buttons für Neu und Save
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        AKJButton btnNew = getSwingFactory().createButton("new", getActionListener());
        AKJButton btnSave = getSwingFactory().createButton("save", getActionListener());

        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnNew, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnSave, btnNew);

        tbModelTemplates = new AKReflectionTableModel<ReportTemplate>(
                new String[] { "ID", "Dateiname", "Gültig von", "Gültig bis", "User" },
                new String[] { "id", "filenameOrig", "gueltigVon", "gueltigBis", "userw" },
                new Class[] { Long.class, String.class, Date.class, Date.class, String.class });
        AKJTable tbTemplates = new AKJTable(tbModelTemplates, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbTemplates.attachSorter();
        tbTemplates.addTableListener(this);
        tbTemplates.fitTable(new int[] { 50, 250, 150, 150, 80 });
        AKJScrollPane spTemplates = new AKJScrollPane(tbTemplates, new Dimension(620, 100));

        AKJPanel table = new AKJPanel(new GridBagLayout());
        table.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("assigned.templates")));
        table.add(spTemplates, GBCFactory.createGBC(100, 0, 0, 1, 1, 3, GridBagConstraints.HORIZONTAL));
        table.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfId, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblFilename, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfFilename, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(btnUp, GBCFactory.createGBC(0, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(btnDown, GBCFactory.createGBC(0, 0, 5, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblGueltigVon, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(dcGueltigVon, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblGueltigBis, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(dcGueltigBis, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 50, 6, 5, 1, 1, GridBagConstraints.BOTH));

        AKJPanel detailPanel = new AKJPanel(new GridBagLayout());
        detailPanel.add(dataPanel, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(btnPanel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel tempPanel = new AKJPanel(new GridBagLayout());
        tempPanel.add(table, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPanel.add(detailPanel, GBCFactory.createGBC(100, 100, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        tfId.setEditable(false);
        tfFilename.setEnabled(false);
        btnUp.setEnabled(false);
        dcGueltigBis.setEnabled(false);
        dcGueltigVon.setEnabled(false);

        this.setLayout(new BorderLayout());
        this.add(tempPanel, BorderLayout.NORTH);
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
        btnUp.setEnabled(true);
        dcGueltigBis.setEnabled(true);
        dcGueltigVon.setEnabled(true);
    }

    private void disableFields() {
        btnUp.setEnabled(false);
        btnDown.setEnabled(false);
        dcGueltigBis.setEnabled(false);
        dcGueltigVon.setEnabled(false);
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

    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() {
        if (detail != null) {
            if (report == null) {
                MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst einen Report auswählen", null, true);
            }
            else {
                // Teste Daten auf Vollständigkeit
                if ((detail.getFilenameOrig() == null) || (detail.getGueltigVon() == null)
                        || (detail.getUserw() == null)) {
                    MessageHelper.showInfoDialog(getMainFrame(), "Nicht alle benötigten Datenfelder gefüllt!", null, true);
                    return;
                }
                try {
                    ReportConfigService service = getReportService(ReportConfigService.class);
                    if (detail.getId() == null) {
                        service.saveNewReportTemplate(detail);
                    }
                    else {
                        service.saveReportTemplate(detail);
                    }
                    loadData();
                    tbModelTemplates.fireTableDataChanged();
                    showDetails(detail);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(this, e);
                    this.detail = null;
                }
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        if (detail == null) {
            detail = new ReportTemplate();
        }
        try {
            // Aktuellen User ermitteln
            Long sessionId = HurricanSystemRegistry.instance().getSessionId();
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService userService = (AKUserService) authSL.getService(AKAuthenticationServiceNames.USER_SERVICE, null);
            AKUser user = userService.findUserBySessionId(sessionId);

            // Daten setzen
            if (user != null) {
                detail.setUserw(user.getLoginName());
            }

            if (report != null) {
                detail.setReportId(report.getId());
            }

            detail.setFilenameOrig(tfFilename.getText(null));
            detail.setGueltigVon(dcGueltigVon.getDate(null));
            detail.setGueltigBis(dcGueltigBis.getDate(null));

            if (detail.getGueltigBis() == null) {
                detail.setGueltigBis(DateTools.getHurricanEndDate());
            }
            if ((detail.getGueltigVon() == null) || detail.getGueltigVon().before(DateTools.getActualSQLDate())) {
                detail.setGueltigVon(DateTools.getActualSQLDate());
            }

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
        this.report = (model instanceof Report) ? (Report) model : null;
        loadData();
        GuiTools.cleanFields(this);
        disableFields();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save".equals(command)) {
            if ((report != null) && (report.getId() != null)) {
                getModel();
                saveModel();
            }
        }
        else if ("new".equals(command)) {
            if ((report != null) && (report.getId() != null)) {
                createNew();
            }
        }
        else if ("upload".equals(command)) {
            uploadFile();
        }
        else if ("download".equals(command)) {
            downloadFile();
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

            // alle Report-Vorlagen ermitteln
            ReportConfigService rs = getReportService(ReportConfigService.class);
            List<ReportTemplate> templates = rs.findAllReportTemplates4Report(report.getId());
            tbModelTemplates.setData(templates);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        this.detail = (details instanceof ReportTemplate) ? (ReportTemplate) details : null;

        if (detail != null) {
            tfId.setValue(detail.getId());
            tfFilename.setText(detail.getFilenameOrig());
            btnUp.setEnabled(false);
            btnDown.setEnabled(true);
            Date aktDate = Calendar.getInstance().getTime();
            dcGueltigVon.setDate(detail.getGueltigVon());
            dcGueltigVon.setEnabled(DateTools.isDateAfter(detail.getGueltigVon(), aktDate));
            dcGueltigBis.setDate(detail.getGueltigBis());
            dcGueltigBis.setEnabled(DateTools.isDateAfterOrEqual(detail.getGueltigBis(), aktDate));
        }
    }

    /*
     * Funktion öffnet ein Dialog-Fenster zur Dateiauswahl
     */
    private void uploadFile() {
        File upload = null;

        // Öffne Dialog
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            upload = chooser.getSelectedFile();
        }

        // Setze Dateiname in Textfeld
        if (upload != null) {
            tfFilename.setText(upload.getPath());
        }
    }

    /*
     * Funktion ermöglicht den Download einer Report-Vorlage
     */
    private void downloadFile() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst eine Report-Vorlage auswählen", null, true);
            return;
        }
        File download = null;

        // Öffne Dialog
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(detail.getFilenameOrig()));
        int returnVal = chooser.showSaveDialog(getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            download = chooser.getSelectedFile();
        }

        // Kopiere Datei
        if (download != null) {
            try {
                ReportConfigService rs = getReportService(ReportConfigService.class);
                rs.downloadFile(detail, download.getPath());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }


}
