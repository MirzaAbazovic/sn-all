/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2007 08:53:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.reporting.konfiguration.actions.ShowGruppen4TxtBausteinAction;
import de.augustakom.hurrican.model.reporting.TxtBaustein;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.reporting.ReportConfigService;


/**
 * Panel fuer die Administration der Text-Bausteine.
 *
 *
 */
public class TxtBausteinePanel extends AbstractAdminPanel implements AKNavigationBarListener, AKModelOwner, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(TxtBausteinePanel.class);

    private AKJNavigationBar navBar = null;
    private AKReflectionTableModel<TxtBaustein> tbModelBausteine = null;

    private AKJButton btnSave = null;
    private AKJButton btnHistory = null;
    private AKJButton btnDelete = null;

    private AKJFormattedTextField tfId = null;
    private AKJFormattedTextField tfIdOrig = null;
    private AKJTextField tfName = null;
    private AKJTextArea taText = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;
    private AKJCheckBox cbEditable = null;
    private AKJCheckBox cbMandatory = null;

    private TxtBaustein detail = null;

    /**
     * Standardkonstruktor
     */
    public TxtBausteinePanel() {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/ReportTxtBausteinePanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        // Table für Text-Bausteine
        tbModelBausteine = new AKReflectionTableModel<TxtBaustein>(
                new String[] { "ID", "ID_ORIG", "Name", "Gültig von", "Gültig bis", "Editierbar", "Mandatory" },
                new String[] { "id", "idOrig", "name", "gueltigVon", "gueltigBis", "editable", "mandatory" },
                new Class[] { Long.class, Long.class, String.class, Date.class, Date.class, Boolean.class,
                        Boolean.class }
        );

        AKJTable tbBausteine = new AKJTable(tbModelBausteine, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbBausteine.attachSorter();
        tbBausteine.addTableListener(this);
        ShowGruppen4TxtBausteinAction gruppenAction = new ShowGruppen4TxtBausteinAction(tbBausteine);
        gruppenAction.setParentClass(this.getClass());
        tbBausteine.addPopupAction(gruppenAction);
        tbBausteine.fitTable(new int[] { 50, 50, 300, 70, 70, 70, 70 });
        AKJScrollPane tableSP = new AKJScrollPane(tbBausteine, new Dimension(900, 150));

        // Baustein-Details
        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblIdOrig = getSwingFactory().createLabel("id.orig");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblText = getSwingFactory().createLabel("text");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("gueltig.bis");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblEditable = getSwingFactory().createLabel("editable");
        AKJLabel lblMandatory = getSwingFactory().createLabel("mandatory");

        navBar = new AKJNavigationBar(this, true, true);
        tfId = getSwingFactory().createFormattedTextField("id");
        tfIdOrig = getSwingFactory().createFormattedTextField("id.orig");
        tfName = getSwingFactory().createTextField("name");
        taText = getSwingFactory().createTextArea("text");
        dcGueltigVon = getSwingFactory().createDateComponent("gueltig.von");
        dcGueltigBis = getSwingFactory().createDateComponent("gueltig.bis");
        cbEditable = getSwingFactory().createCheckBox("editable");
        cbMandatory = getSwingFactory().createCheckBox("mandatory");
        AKJScrollPane spText = new AKJScrollPane(taText, new Dimension(100, 100));

        // Panel mit Buttons für Neu und Save
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        AKJButton btnNew = getSwingFactory().createButton("new", getActionListener());
        btnSave = getSwingFactory().createButton("save", getActionListener());
        btnDelete = getSwingFactory().createButton("delete", getActionListener());
        btnHistory = getSwingFactory().createButton("history", getActionListener());

        btnPanel.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnDelete, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnHistory, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnSave, btnNew, btnDelete, btnHistory);

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(navBar, GBCFactory.createGBC(0, 0, 1, 0, 5, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfId, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblIdOrig, GBCFactory.createGBC(0, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfIdOrig, GBCFactory.createGBC(100, 0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblName, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfName, GBCFactory.createGBC(100, 0, 3, 2, 5, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblEditable, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbEditable, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 3, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(lblMandatory, GBCFactory.createGBC(0, 0, 5, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 3, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(cbMandatory, GBCFactory.createGBC(100, 0, 7, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblGueltigVon, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(dcGueltigVon, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblGueltigBis, GBCFactory.createGBC(0, 0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(dcGueltigBis, GBCFactory.createGBC(100, 0, 7, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblText, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(spText, GBCFactory.createGBC(100, 0, 3, 5, 5, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 50, 8, 7, 1, 1, GridBagConstraints.BOTH));

        AKJPanel detailPanel = new AKJPanel(new GridBagLayout());
        detailPanel.add(tableSP, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        detailPanel.add(dataPanel, GBCFactory.createGBC(100, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(btnPanel, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 4, 1, 1, GridBagConstraints.VERTICAL));

        tfId.setEditable(false);
        tfIdOrig.setEditable(false);
        setGUIEnable(false);

        this.setLayout(new BorderLayout());
        this.add(detailPanel, BorderLayout.NORTH);
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
        // intentionally left blank
    }

    /* 'Loescht' die TextFields */
    private void clear() {
        GuiTools.cleanFields(this);
        cbEditable.setSelected(false);
        cbMandatory.setSelected(false);
        setGUIEnable(true);
    }

    /* Aktiviert die GUI-Elemente */
    private void setGUIEnable(boolean bool) {
        boolean bool1 = bool;
        btnSave.setEnabled(bool1);
        dcGueltigBis.setEnabled(bool1);
        btnHistory.setEnabled(bool1);

        // Teste ob Referenzen auf Text-Baustein vorhanden sind und gebe Löschen-Button entsprechend frei
        if (bool1) {
            // Txt-Baustein nicht gespeichert
            if ((detail == null) || (detail.getId() == null)) {
                btnDelete.setEnabled(false);
            }
            else {
                try {
                    ReportConfigService rs = getReportService(ReportConfigService.class);
                    List<TxtBausteinGruppe> test = rs.findTxtBausteinGruppen4TxtBaustein(detail.getIdOrig());
                    if (CollectionTools.isNotEmpty(test)) {
                        btnDelete.setEnabled(false);
                    }
                    else {
                        btnDelete.setEnabled(true);
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        else {
            btnDelete.setEnabled(false);
        }

        // Falls Txt-Baustein bereits aktiv, gebe nicht alle GUI-Elemente zum Editieren frei
        Date currentDate = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DAY_OF_MONTH);
        if (bool1
                && (detail != null)
                && (detail.getId() != null)
                && DateTools.isBefore(detail.getGueltigVon(), currentDate)) {
            bool1 = false;
        }

        tfName.setEnabled(bool1);
        taText.setEnabled(bool1);
        dcGueltigVon.setEnabled(bool1);
        cbEditable.setEnabled(bool1);
        cbMandatory.setEnabled(bool1);
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
        // intentionally left blank
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() {
        //Fehlerbehandlung
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Keine Daten vorhanden.", null, true);
            return;
        }
        if (StringUtils.isBlank(detail.getName())
                || StringUtils.isBlank(detail.getText()) || (detail.getEditable() == null)
                || (detail.getMandatory() == null)
                || (detail.getGueltigBis() == null) || (detail.getGueltigVon() == null)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Nicht alle benötigten Daten vorhanden.", null, true);
            return;
        }
        saveTxtBausteinAndRefreshView();
    }

    private void saveTxtBausteinAndRefreshView() {
        // Alle Daten vorhanden, speichere TxtBaustein
        try {
            ReportConfigService service = getReportService(ReportConfigService.class);
            service.saveTxtBaustein(detail);

            // Aktualisiere Ansicht
            loadData();
            int i = navBar.getNavPosition();
            showDetails(detail);
            navBar.navigateTo(i);
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
            detail = new TxtBaustein();
        }
        try {
            detail.setName(tfName.getText(null));
            detail.setText(taText.getText(null));
            detail.setEditable(cbEditable.isSelectedBoolean());
            detail.setMandatory(cbMandatory.isSelectedBoolean());
            detail.setGueltigVon(dcGueltigVon.getDate(null));
            detail.setGueltigBis(dcGueltigBis.getDate(null));

            // Prüfe NOT-NULL Werte und setze bei Bedarf die Standardwerte
            // Berichtige Datum
            if (detail.getGueltigBis() == null) {
                detail.setGueltigBis(DateTools.getHurricanEndDate());
            }
            if (detail.getGueltigVon() == null) {
                detail.setGueltigVon(DateTools.getActualSQLDate());
            }

            // Ein neuer Datensatz darf das Gültig_von Datum nicht in der Vergangenheit haben
            if ((detail.getId() == null) && detail.getGueltigVon().before(DateTools.getActualSQLDate())) {
                detail.setGueltigVon(DateTools.getActualSQLDate());
            }

            if (detail.getEditable() == null) {
                detail.setEditable(Boolean.FALSE);
            }
            if (detail.getMandatory() == null) {
                detail.setMandatory(Boolean.FALSE);
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
        // intentionally left blank
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
        else if ("history".equals(command)) {
            historyBaustein();
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
        // intentionally left blank
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            showProgressBar("laden...");

            ReportConfigService rs = getReportService(ReportConfigService.class);
            List<TxtBaustein> bausteine = rs.findAllNewTxtBausteine();
            tbModelBausteine.setData(bausteine);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
            stopProgressBar();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if ((details != null) && (details instanceof TxtBaustein)) {
            // Ermittle alle historisierten Txt-Bausteine
            List<TxtBaustein> bausteine = null;
            try {
                ReportConfigService rs = getReportService(ReportConfigService.class);
                bausteine = rs.findAllTxtBausteine4IdOrig(((TxtBaustein) details).getIdOrig());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }

            // Setze alle Bausteine in NavBar
            if (CollectionTools.isNotEmpty(bausteine)) {
                navBar.setData(bausteine);
                navBar.navigateTo(navBar.getNavCount() - 1);
            }
            else {
                clear();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        this.detail = (obj instanceof TxtBaustein) ? (TxtBaustein) obj : null;

        if (detail != null) {
            tfId.setValue(detail.getId());
            tfIdOrig.setValue(detail.getIdOrig());
            tfName.setText(detail.getName());
            taText.setText(detail.getText());
            dcGueltigVon.setDate(detail.getGueltigVon());
            dcGueltigBis.setDate(detail.getGueltigBis());
            cbEditable.setSelected(detail.getEditable());
            cbMandatory.setSelected(detail.getMandatory());
            // GUI-Elemente aktivieren
            setGUIEnable(true);
        }
    }

    /*
     * Funktion löscht einen bestimmten Text-Baustein
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
            rs.deleteTxtBausteinById(detail.getId());

            if (navBar.getNavCount() > 1) {
                showDetails(detail);
            }
            else {
                detail = null;
                clear();
            }
            // Lade Übersichtstabelle neu
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Funktion historisiert einen Text-Baustein
     */
    private void historyBaustein() {
        //Fehlerbehandlung
        if ((detail == null) || (detail.getId() == null)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Kein Text-Baustein ausgewählt.", null, true);
            return;
        }

        // Historisiere aktuellen TextBaustein
        try {
            // Frage Datum der Historisierung ab
            Date currentDate = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DAY_OF_MONTH);
            AKDateSelectionDialog dlg = new AKDateSelectionDialog("Historisierung",
                    "Zu welchem Datum soll der Text-Baustein historisiert werden ?", "Datum:");
            dlg.showDate(currentDate);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            Date date = (result instanceof Date) ? (Date) result : null;

            // Prüfe eingegebenes Datum
            if (date == null) {
                MessageHelper.showInfoDialog(getMainFrame(), "Fehler bei der Eingabe oder Dialog abgebrochen.", null, true);
            }
            else if (DateTools.isBefore(date, currentDate)) {
                MessageHelper.showInfoDialog(getMainFrame(), "Datum liegt in der Vergangenheit, Historisierung nicht möglich.", null, true);
            }
            else {
                ReportConfigService rs = getReportService(ReportConfigService.class);

                // Dupliziere Text-Baustein, füge neues GültigVon-Datum ein und speicher Objekt
                TxtBaustein oldTB = detail;
                detail = new TxtBaustein();
                detail.setIdOrig(oldTB.getIdOrig());
                detail.setEditable(oldTB.getEditable());
                detail.setGueltigBis(DateTools.getHurricanEndDate());
                detail.setGueltigVon(date);
                detail.setMandatory(oldTB.getMandatory());
                detail.setName(oldTB.getName());
                detail.setText(oldTB.getText());
                rs.saveTxtBaustein(detail);

                // Beende aktuellen Datensatz
                oldTB.setGueltigBis(DateTools.changeDate(date, Calendar.DAY_OF_MONTH, -1));
                rs.saveTxtBaustein(oldTB);

                // Lade Tabelle neu und lösche Inhalte der GUI-Elemente
                loadData();
                showDetails(detail);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}
