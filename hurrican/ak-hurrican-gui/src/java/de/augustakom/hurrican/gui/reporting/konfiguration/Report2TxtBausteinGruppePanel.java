/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 15:20:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.reporting.ReportConfigService;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Panel fuer die Zuordnung von Text-Baustein-Gruppen zu einem ausgewählten Report.
 *
 *
 */
public class Report2TxtBausteinGruppePanel extends AbstractAdminPanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(Report2TxtBausteinGruppePanel.class);

    private AKJButton btnSave = null;
    private AKJButton btnDelete = null;
    private AKJButton btnAddCommand = null;
    private AKJButton btnRemCommand = null;
    private AKJButton btnRefresh = null;

    private AKJTable tbBausteine = null;
    private AKReflectionTableModel<TxtBausteinGruppe> tbMdlBausteine = null;
    private AKJList lsAssigned = null;
    private DefaultListModel lsMdlAssigned = null;

    private Report detail = null;

    /**
     * Standardkonstruktor
     */
    public Report2TxtBausteinGruppePanel() {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/Report2TxtBausteinGruppePanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        btnDelete = getSwingFactory().createButton("delete.config", getActionListener(), null);
        btnSave = getSwingFactory().createButton("save.config", getActionListener(), null);
        btnAddCommand = getSwingFactory().createButton("add.command", getActionListener(), null);
        btnRemCommand = getSwingFactory().createButton("remove.command", getActionListener(), null);
        btnRefresh = getSwingFactory().createButton("refresh.command", getActionListener(), null);
        AKJButton btnMoveUp = getSwingFactory().createButton("move.up", getActionListener(), null);
        AKJButton btnMoveDown = getSwingFactory().createButton("move.down", getActionListener(), null);

        tbMdlBausteine = new AKReflectionTableModel<TxtBausteinGruppe>(
                new String[] { "ID", "Name", "Beschreibung", "Mandatory" },
                new String[] { "id", "name", "description", "mandatory" },
                new Class[] { Long.class, String.class, String.class, Boolean.class });
        tbBausteine = new AKJTable(tbMdlBausteine, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbBausteine.attachSorter();
        tbBausteine.fitTable(new int[] { 50, 150, 300, 70 });
        AKJScrollPane spTable = new AKJScrollPane(tbBausteine, new Dimension(670, 400));

        lsMdlAssigned = new DefaultListModel();
        lsAssigned = new AKJList(lsMdlAssigned);
        lsAssigned.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lsAssigned.setCellRenderer(new AKCustomListCellRenderer<>(TxtBausteinGruppe.class, TxtBausteinGruppe::getName));
        AKJScrollPane spList = new AKJScrollPane(lsAssigned, new Dimension(180, 10));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("assigned.commands")));
        left.add(spList, GBCFactory.createGBC(100, 100, 0, 1, 1, 3, GridBagConstraints.BOTH));
        left.add(btnMoveUp, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        left.add(btnMoveDown, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(btnSave, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        mid.add(btnDelete, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));
        mid.add(btnRefresh, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.NONE));
        mid.add(btnAddCommand, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 5, 2)));
        mid.add(btnRemCommand, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 9, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("available.commands")));
        right.add(spTable, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(left, GBCFactory.createGBC(20, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        center.add(mid, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        center.add(right, GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.BOTH));

        setGuiEnabled(false);

        this.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
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
        try {
            setWaitCursor();

            lsMdlAssigned.removeAllElements();

            // zugeordnete Bausteine laden
            if (detail != null) {
                ReportService rs = getReportService(ReportService.class);
                List<TxtBausteinGruppe> gruppen = rs.findAllTxtBausteinGruppen4Report(detail.getId());
                if (CollectionTools.isNotEmpty(gruppen)) {
                    for (TxtBausteinGruppe gruppe : gruppen) {
                        lsMdlAssigned.addElement(gruppe);
                    }
                }
                // Aktiviere GUI-Elemente, falls Report bereits gespeichert wurde.
                setGuiEnabled(detail.getId() != null);

                // Lösche Auswahl in Tabellen
                tbBausteine.clearSelection();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
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
    public void setModel(Observable model) {
        this.detail = (model instanceof Report) ? (Report) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save.config".equals(command)) {
            saveConfig();
        }
        else if ("add.command".equals(command)) {
            addCommand();
        }
        else if ("remove.command".equals(command)) {
            removeCommand();
        }
        else if ("delete.config".equals(command)) {
            deleteCommand();
        }
        else if ("refresh.command".equals(command)) {
            loadData();
        }
        else if ("move.up".equals(command)) {
            moveUp();
        }
        else if ("move.down".equals(command)) {
            moveDown();
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
            // Lade alle gültigen Text-Baustein-Gruppen
            ReportConfigService rs = getReportService(ReportConfigService.class);
            List<TxtBausteinGruppe> gruppen = rs.findAllTxtBausteinGruppen();
            if (CollectionTools.isNotEmpty(gruppen)) {
                tbMdlBausteine.setData(gruppen);
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
    }

    /* Speichert die aktuelle Konfiguration. */
    private void saveConfig() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst einen Report auswaehlen!", null, true);
            return;
        }

        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.save.msg"), getSwingFactory().getText("ask.save.title"));
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            setWaitCursor();
            if (detail != null) {
                // zugeordnete Text-Bausteine auslesen
                List<TxtBausteinGruppe> gruppen = new ArrayList<>();
                for (int i = 0; i < lsMdlAssigned.getSize(); i++) {
                    gruppen.add((TxtBausteinGruppe) lsMdlAssigned.get(i));
                }
                // Text-Baustein-Gruppen speichern
                ReportConfigService rs = getReportService(ReportConfigService.class);
                rs.saveTxtBausteinGruppen4Report(detail.getId(), gruppen);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Fuegt die selektierten Commands der Liste hinzu. */
    private void addCommand() {
        int[] selection = tbBausteine.getSelectedRows();
        AKMutableTableModel tbMdl = (AKMutableTableModel) tbBausteine.getModel();
        for (int i = 0; i < selection.length; i++) {
            TxtBausteinGruppe txt2Add = (TxtBausteinGruppe) tbMdl.getDataAtRow(selection[i]);
            if (!lsAssigned.containsItem("getId", TxtBausteinGruppe.class, txt2Add.getId(), true)) {
                lsMdlAssigned.addElement(txt2Add);
            }
        }
    }

    /* Entfernt die selektierten Commands aus der Liste. */
    private void removeCommand() {
        Object[] selection = lsAssigned.getSelectedValues();
        if (selection != null) {
            for (int i = 0; i < selection.length; i++) {
                lsMdlAssigned.removeElement(selection[i]);
            }
        }
    }

    /* Entfernt alle Commands aus der Liste. */
    private void deleteCommand() {
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.delete.msg"), getSwingFactory().getText("ask.delete.title"));
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        else {
            try {
                setWaitCursor();

                // Lösche alle Daten aus View
                lsMdlAssigned.removeAllElements();

                // Speichere in DB
                if (detail != null) {
                    ReportConfigService rs = getReportService(ReportConfigService.class);
                    rs.deleteAllTxtBausteinGruppen4Report(detail.getId());
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /* Verschiebt das selektierte Command nach oben. */
    private void moveUp() {
        int index = lsAssigned.getSelectedIndex();
        lsAssigned.switchItems(index, --index);
    }

    /* Verschiebt das selektierte Command nach unten. */
    private void moveDown() {
        int index = lsAssigned.getSelectedIndex();
        lsAssigned.switchItems(index, ++index);
    }

    /*
     * Setzt die Gui-Elemente editierbar
     */
    private void setGuiEnabled(boolean enable) {
        btnSave.setEnabled(enable);
        btnDelete.setEnabled(enable);
        btnAddCommand.setEnabled(enable);
        btnRemCommand.setEnabled(enable);
        btnRefresh.setEnabled(enable);
        tbBausteine.setEnabled(enable);
        lsAssigned.setEnabled(enable);
    }


}
