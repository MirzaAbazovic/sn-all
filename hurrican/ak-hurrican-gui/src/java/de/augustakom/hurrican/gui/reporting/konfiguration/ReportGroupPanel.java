/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2007 11:53:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.reporting.ReportGruppe;
import de.augustakom.hurrican.service.reporting.ReportConfigService;


/**
 * Panel fuer die Administration der Report-Gruppen.
 *
 *
 */
public class ReportGroupPanel extends AbstractAdminPanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ReportGroupPanel.class);

    private AKReflectionTableModel<ReportGruppe> tbModelGruppe = null;

    private AKJButton btnSave = null;
    private AKJButton btnDelete = null;

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextArea taDesc = null;

    private ReportGruppe detail = null;

    /**
     * Standardkonstruktor
     */
    public ReportGroupPanel() {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/ReportGroupPanel.xml");
        createGUI();
        setModel(null);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblDesc = getSwingFactory().createLabel("desc");

        tfId = getSwingFactory().createFormattedTextField("id");
        tfName = getSwingFactory().createTextField("name");
        taDesc = getSwingFactory().createTextArea("desc");

        AKJScrollPane spText = new AKJScrollPane(taDesc, new Dimension(100, 100));

        // Panel mit Buttons für Neu und Save
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        AKJButton btnNew = getSwingFactory().createButton("new", getActionListener());
        btnSave = getSwingFactory().createButton("save", getActionListener());
        btnDelete = getSwingFactory().createButton("delete", getActionListener());

        btnPanel.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnDelete, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnSave, btnNew, btnDelete);

        tbModelGruppe = new AKReflectionTableModel<ReportGruppe>(
                new String[] { "ID", "Name", "Beschreibung" },
                new String[] { "id", "name", "description" },
                new Class[] { Long.class, String.class, String.class });
        AKJTable tbGruppe = new AKJTable(tbModelGruppe, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbGruppe.attachSorter();
        tbGruppe.addTableListener(this);
        tbGruppe.fitTable(new int[] { 50, 200, 300 });
        AKJScrollPane spBausteine = new AKJScrollPane(tbGruppe, new Dimension(600, 150));

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfId, GBCFactory.createGBC(100, 0, 3, 1, 5, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblName, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfName, GBCFactory.createGBC(100, 0, 3, 2, 5, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 4, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(lblDesc, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(spText, GBCFactory.createGBC(100, 0, 3, 5, 5, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 50, 8, 7, 1, 1, GridBagConstraints.BOTH));

        AKJPanel tempPanel = new AKJPanel(new GridBagLayout());
        tempPanel.add(spBausteine, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        tempPanel.add(dataPanel, GBCFactory.createGBC(100, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        tempPanel.add(btnPanel, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));

        tfId.setEditable(false);
        setGUIEnable(false);

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
        setGUIEnable(true);
    }

    /* Aktiviert die GUI-Elemente */
    private void setGUIEnable(boolean bool) {
        btnSave.setEnabled(bool);

        // Teste ob Referenzen auf Report-Gruppe vorhanden sind
        // und gebe Löschen-Button entsprechend frei
        if (bool) {
            // Gruppe nicht gespeichert
            if ((detail == null) || (detail.getId() == null)) {
                btnDelete.setEnabled(false);
            }
            else {
                try {
                    ReportConfigService rs = getReportService(ReportConfigService.class);
                    btnDelete.setEnabled(!rs.testReferences4ReportGroup(detail.getId()));
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        else {
            btnDelete.setEnabled(false);
        }

        tfName.setEnabled(bool);
        taDesc.setEnabled(bool);
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
        // Fehlerbehandlung
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Keine Daten vorhanden.", null, true);
            return;
        }
        if (StringUtils.isBlank(detail.getName())) {
            MessageHelper.showInfoDialog(getMainFrame(), "Nicht alle benötigten Daten vorhanden.", null, true);
            return;
        }
        // Alle Daten vorhanden, speichere Report-Gruppe
        try {
            ReportConfigService service = getReportService(ReportConfigService.class);
            service.saveReportGroup(detail);

            // Lade TableModel neu und aktualisiere Anzeige
            loadData();
            tbModelGruppe.fireTableDataChanged();
            showDetails(detail);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            this.detail = null;
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        if (detail == null) {
            detail = new ReportGruppe();
        }
        try {
            detail.setName(tfName.getText(null));
            detail.setDescription(taDesc.getText(null));
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
        loadData();
        GuiTools.cleanFields(this);
        setGUIEnable(false);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save".equals(command)) {
            getModel();
            saveModel();
        }
        else if ("new".equals(command)) {
            createNew();
        }
        else if ("delete".equals(command)) {
            deleteBaustein();
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
            // Alle verfügbaren Report-Gruppen ermitteln
            ReportConfigService rs = getReportService(ReportConfigService.class);
            List<ReportGruppe> gruppen = rs.findAllReportGroups();
            if (CollectionTools.isNotEmpty(gruppen)) {
                tbModelGruppe.setData(gruppen);
            }
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
        this.detail = (details instanceof ReportGruppe) ? (ReportGruppe) details : null;

        if (detail != null) {
            tfId.setValue(detail.getId());
            tfName.setText(detail.getName());
            taDesc.setText(detail.getDescription());
            // GUI-Elemente aktivieren
            setGUIEnable(true);
        }
    }

    /*
     * Funktion löscht eine bestimmte Report-Gruppe
     */
    private void deleteBaustein() {
        //Fehlerbehandlung
        if ((detail == null) || (detail.getId() == null)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Kein Text-Baustein ausgewählt.", null, true);
            return;
        }
        // Lösche aktuellen TextBaustein
        try {
            ReportConfigService rs = getReportService(ReportConfigService.class);
            rs.deleteReportGroupById(detail.getId());
            // Lade Tabelle neu und lösche Inhalte der GUI-Elemente
            loadData();
            tbModelGruppe.fireTableDataChanged();
            detail = null;
            clear();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


}
