/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.05.2007 16:20:18
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
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.reporting.TxtBaustein;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.reporting.ReportConfigService;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Panel fuer die Zuordnung von Text-Bausteinen zu einer Baustein-Gruppe.
 *
 *
 */
public class TxtBaustein2GruppePanel extends AbstractAdminPanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(TxtBaustein2GruppePanel.class);

    private AKJButton btnSave = null;
    private AKJButton btnDelete = null;
    private AKJButton btnAdd = null;
    private AKJButton btnRemove = null;

    private AKReflectionTableModel<TxtBausteinGruppe> tbMdlGruppen = null;
    private AKJTable tbTxtAssigned = null;
    private AKReflectionTableModel<TxtBaustein> tbMdlTxtAssigned = null;
    private AKJTable tbTxtAvailable = null;
    private AKReflectionTableModel<TxtBaustein> tbMdlTxtAvailable = null;

    private TxtBausteinGruppe detail = null;

    /**
     * Standardkonstruktor
     */
    public TxtBaustein2GruppePanel() {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/TxtBaustein2GruppePanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        btnDelete = getSwingFactory().createButton("delete", getActionListener(), null);
        btnSave = getSwingFactory().createButton("save", getActionListener(), null);
        btnAdd = getSwingFactory().createButton("add", getActionListener(), null);
        btnRemove = getSwingFactory().createButton("remove", getActionListener(), null);
        AKJButton btnRefresh = getSwingFactory().createButton("refresh", getActionListener(), null);

        tbMdlGruppen = new AKReflectionTableModel<TxtBausteinGruppe>(
                new String[] { "ID", "Name" },
                new String[] { "id", "name" },
                new Class[] { Long.class, String.class });
        AKJTable tbGruppen = new AKJTable(tbMdlGruppen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbGruppen.attachSorter();
        tbGruppen.addTableListener(this);
        tbGruppen.fitTable(new int[] { 50, 150 });
        AKJScrollPane spGruppen = new AKJScrollPane(tbGruppen, new Dimension(230, 100));

        tbMdlTxtAssigned = new AKReflectionTableModel<TxtBaustein>(
                new String[] { "ID-Orig", "Name" },
                new String[] { "idOrig", "name" },
                new Class[] { Long.class, String.class });
        tbTxtAssigned = new AKJTable(tbMdlTxtAssigned, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbTxtAssigned.attachSorter();
        tbTxtAssigned.fitTable(new int[] { 50, 150 });
        AKJScrollPane spTxtAssigned = new AKJScrollPane(tbTxtAssigned, new Dimension(230, 100));

        tbMdlTxtAvailable = new AKReflectionTableModel<TxtBaustein>(
                new String[] { "ID-Orig", "Name" },
                new String[] { "idOrig", "name" },
                new Class[] { Long.class, String.class });
        tbTxtAvailable = new AKJTable(tbMdlTxtAvailable, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbTxtAvailable.attachSorter();
        tbTxtAvailable.fitTable(new int[] { 50, 150 });
        AKJScrollPane spTxtAvailable = new AKJScrollPane(tbTxtAvailable, new Dimension(230, 100));

        AKJPanel btns1 = new AKJPanel(new GridBagLayout());
        btns1.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btns1.add(btnSave, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btns1.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        btns1.add(btnDelete, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));
        btns1.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.NONE));
        btns1.add(btnRefresh, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 5, 2)));
        btns1.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 7, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel btns2 = new AKJPanel(new GridBagLayout());
        btns2.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btns2.add(btnAdd, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 5, 2)));
        btns2.add(btnRemove, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        btns2.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        spTxtAssigned.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("assigned.bausteine")));
        spTxtAvailable.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("available.bausteine")));
        spGruppen.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("gruppen")));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(spTxtAssigned, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        right.add(btns2, GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));
        right.add(spTxtAvailable, GBCFactory.createGBC(100, 100, 2, 1, 1, 1, GridBagConstraints.BOTH));

        AKJPanel menu = new AKJPanel(new GridBagLayout());
        menu.add(spGruppen, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        menu.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.VERTICAL));
        menu.add(btns1, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.VERTICAL));
        menu.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 0, 1, 1, GridBagConstraints.VERTICAL));
        menu.add(right, GBCFactory.createGBC(100, 0, 5, 0, 1, 1, GridBagConstraints.VERTICAL));

        setGuiEnabled(false);

        this.setLayout(new BorderLayout());
        this.add(menu, BorderLayout.CENTER);
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
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save".equals(command)) {
            saveConfig();
        }
        else if ("delete".equals(command)) {
            deleteConfig();
        }
        else if ("add".equals(command)) {
            addBaustein();
        }
        else if ("remove".equals(command)) {
            removeBaustein();
        }
        else if ("refresh".equals(command)) {
            loadData();
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
            // Alle verfügbaren TextBaustein-Gruppen ermitteln
            ReportConfigService rs = getReportService(ReportConfigService.class);
            List<TxtBausteinGruppe> gruppen = rs.findAllTxtBausteinGruppen();
            if (CollectionTools.isNotEmpty(gruppen)) {
                tbMdlGruppen.setData(gruppen);
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
        this.detail = (details instanceof TxtBausteinGruppe) ? (TxtBausteinGruppe) details : null;

        try {
            tbMdlTxtAssigned.removeAll();
            tbMdlTxtAvailable.removeAll();

            // Ermittle Zuordnung
            ReportService rs = getReportService(ReportService.class);
            ReportConfigService rcs = getReportService(ReportConfigService.class);
            List<TxtBaustein> bausteine = rs.findTxtBausteine4Gruppe(detail.getId());
            List<TxtBaustein> allBausteine = rcs.findAllNewTxtBausteine();
            if (CollectionTools.isNotEmpty(allBausteine)) {
                tbMdlTxtAvailable.setData(allBausteine);
            }
            if (CollectionTools.isNotEmpty(bausteine)) {
                tbMdlTxtAssigned.setData(bausteine);
                removeBausteineAssigned();
            }

            tbMdlTxtAssigned.fireTableDataChanged();
            tbMdlTxtAvailable.fireTableDataChanged();

            setGuiEnabled(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Speichert die aktuelle Konfiguration. */
    private void saveConfig() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst einen Text-Bausteingruppe auswaehlen!", null, true);
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
                // Gruppen-Zuordnung speichern
                ReportConfigService rs = getReportService(ReportConfigService.class);
                rs.saveTxtBausteine2GruppeZuordnung(detail.getId(), (List<TxtBaustein>) tbMdlTxtAssigned.getData());
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


    /* Entfernt alle Commands aus der Liste. */
    private void deleteConfig() {
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.delete.msg"), getSwingFactory().getText("ask.delete.title"));
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        else {
            try {
                setWaitCursor();

                // Lösche Zuordnungen aus DB
                if (detail != null) {
                    ReportConfigService rs = getReportService(ReportConfigService.class);
                    rs.deleteTxtBausteine2GruppeZuordnung(detail.getId());
                }

                // Lade Table neu
                showDetails(detail);
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

    /* Fuegt die selektierten Bausteine der Liste hinzu. */
    private void addBaustein() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst eine Bausteingruppe auswählen", null, true);
        }
        int[] selection = tbTxtAvailable.getSelectedRows();
        AKMutableTableModel tbMdlTxtSelected = (AKMutableTableModel) tbTxtAvailable.getModel();
        List data = new ArrayList();
        for (int j = 0; j < selection.length; j++) {
            TxtBaustein baustein = (TxtBaustein) tbMdlTxtSelected.getDataAtRow(selection[j]);

            // Objekt dem TableModel hinzufügen
            if (!contains(tbMdlTxtAssigned, baustein)) {
                tbMdlTxtAssigned.addObject(baustein);
                data.add(baustein);
            }
        }
        // Entferne Objekte aus tbMdlTxtAvailable
        Collection coll = tbMdlTxtAvailable.getData();
        coll.removeAll(data);
        tbMdlTxtAvailable.setData(coll);
    }

    /* Entfernt die selektierten Bausteine aus der Liste. */
    private void removeBaustein() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst eine Bausteingruppe auswählen", null, true);
        }
        int[] selection = tbTxtAssigned.getSelectedRows();
        AKMutableTableModel tmMdl = (AKMutableTableModel) tbTxtAssigned.getModel();

        if (selection != null) {
            List data = new ArrayList();
            for (int i = 0; i < selection.length; i++) {
                int row = selection[i];
                Object test = tmMdl.getDataAtRow(row);
                data.add(test);
            }
            Collection coll = tbMdlTxtAssigned.getData();
            coll.removeAll(data);
            tbMdlTxtAssigned.setData(coll);
            tbMdlTxtAvailable.addObjects(data);
        }
    }

    /* Funktion entfernt die zugeordneten Bausteine aus der Liste mit verfügbaren Bausteinen */
    private void removeBausteineAssigned() {
        Collection<TxtBaustein> data = tbMdlTxtAssigned.getData();
        Iterator<TxtBaustein> iter = data.iterator();
        while (iter.hasNext()) {
            TxtBaustein baustein = iter.next();
            Collection<TxtBaustein> coll = tbMdlTxtAvailable.getData();
            Collection<TxtBaustein> newData = new ArrayList<>();
            Iterator<TxtBaustein> collIter = coll.iterator();
            while (collIter.hasNext()) {
                TxtBaustein toCheck = collIter.next();
                if (baustein.getId().intValue() != toCheck.getId().intValue()) {
                    newData.add(toCheck);
                }
            }
            tbMdlTxtAvailable.setData(newData);
        }

    }

    /* Funktion prüft, ob eine Baustein bereits im TableModel enthalten ist. */
    private Boolean contains(AKTableModel model, TxtBaustein view) {
        Collection data = model.getData();
        if (CollectionTools.isEmpty(data)) {
            return Boolean.FALSE;
        }
        else {
            return data.contains(view);
        }
    }

    /* Setzt die Gui-Elemente editierbar */
    private void setGuiEnabled(boolean enable) {
        btnSave.setEnabled(enable);
        btnDelete.setEnabled(enable);
        btnAdd.setEnabled(enable);
        btnRemove.setEnabled(enable);
    }


}
