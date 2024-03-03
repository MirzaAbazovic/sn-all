/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2012 10:15:44
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.wholesale.EditDialog;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 *
 */
public class HvtKvzAdresseAdminPanel extends AbstractAdminPanel {
    private static final Logger LOGGER = Logger.getLogger(HvtKvzAdresseAdminPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/hvt/resources/HvtKvzAdresseAdminPanel.xml";

    private static final String[] KVZ_ADRESSE_PROPERTY_NAMES = new String[] { "kvzNummer", "strasse",
            "hausNr", "plz", "ort" };

    private static final String BTN_DELETE = "delete";
    private static final String BTN_EDIT = "edit";

    private final String[] kvzAdresseLabels = new String[] {
            getSwingFactory().getLabelText("KvzAdresse.kvzNummer"),
            getSwingFactory().getLabelText("KvzAdresse.strasse"),
            getSwingFactory().getLabelText("KvzAdresse.hausNr"),
            getSwingFactory().getLabelText("KvzAdresse.plz"),
            getSwingFactory().getLabelText("KvzAdresse.ort")
    };

    private HVTStandort hvtStandort;

    private AKJTable kvzAdressTable;
    private AKTableModel<KvzAdresse> kvzAdresseTableModel;

    private AKJButton btnEdit;
    private AKJButton btnDelete;

    public HvtKvzAdresseAdminPanel(KvzAdresse kvzAdresse) {
        super(RESOURCE);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        kvzAdresseTableModel = new AKReflectionTableModel<KvzAdresse>(
                kvzAdresseLabels,
                KVZ_ADRESSE_PROPERTY_NAMES,
                new Class[] { KvzAdresse.class });

        kvzAdressTable = new AKJTable(kvzAdresseTableModel);
        kvzAdressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        kvzAdressTable.attachSorter();
        kvzAdressTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getClickCount() > 1)) {
                    executeEdit();
                }
            }
        });

        btnEdit = getSwingFactory().createButton(BTN_EDIT, getActionListener());
        btnDelete = getSwingFactory().createButton(BTN_DELETE, getActionListener());

        AKJPanel buttonPanel = new AKJPanel(new GridBagLayout());
        //@formatter:off
        buttonPanel.add(btnEdit,   GBCFactory.createGBC(0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        buttonPanel.add(btnDelete, GBCFactory.createGBC(0,   0, 0, 1, 1, 1, GridBagConstraints.NONE));
        //@formatter:on

        AKJPanel right = new AKJPanel(new GridBagLayout());
        //@formatter:off
        right.add(buttonPanel,      GBCFactory.createGBC(0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        right.add(new AKJPanel(),   GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        //@formatter:on

        AKJPanel tablePanel = new AKJPanel(new GridBagLayout());

        tablePanel.add(new AKJScrollPane(kvzAdressTable, new Dimension(800, 300)),
                GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        setLayout(new GridBagLayout());

        //@formatter:off
        add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        add(tablePanel,     GBCFactory.createGBC(  0,   0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        add(buttonPanel,    GBCFactory.createGBC(  0,   0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(), GBCFactory.createGBC(100,   0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(), GBCFactory.createGBC(  0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));

    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void showDetails(Object details) {
        ((AKMutableTableModel<KvzAdresse>) kvzAdressTable.getModel()).removeAll();
        hvtStandort = null;
        if (details instanceof HVTStandort) {
            this.hvtStandort = (HVTStandort) details;
            if(hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                try {
                    HVTService hvtService = getCCService(HVTService.class);
                    List<KvzAdresse> kvzAdressen = hvtService.findKvzAdressen(hvtStandort.getId());
                    ((AKMutableTableModel<KvzAdresse>) kvzAdressTable.getModel()).addObjects(kvzAdressen);
                    enableButtons();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
            else {
                disableButtons();
            }
        }
    }

    @Override
    public final void loadData() {
    }

    private void disableButtons()   {
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void enableButtons()   {
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void createNew() {
        if((hvtStandort != null) && hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ))   {
            EditDialog dialogNew = new EditDialog(RESOURCE, new KvzAdresse(),
                    Lists.newArrayList(KVZ_ADRESSE_PROPERTY_NAMES));
            Object object = DialogHelper.showDialog(this, dialogNew, true, true);
            if (object instanceof KvzAdresse) {
                KvzAdresse newKvzAdresse = (KvzAdresse) object;
                newKvzAdresse.setHvtStandortId(hvtStandort.getId());
                try {
                    newKvzAdresse.setKvzNummer(KvzAdresse.formatKvzNr(newKvzAdresse.getKvzNummer()));
                    getCCService(HVTService.class).saveKvzAdresse(newKvzAdresse);
                    ((AKMutableTableModel<KvzAdresse>) kvzAdressTable.getModel()).addObject(newKvzAdresse);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
        }
    }

    @Override
    public void saveData() {
    }

    @Override
    protected void execute(String command) {
        if(BTN_EDIT.equals(command))    {
            executeEdit();
        }
        else if(BTN_DELETE.equals(command)) {
            executeDelete();
        }
    }

    @SuppressWarnings("unchecked")
    private void executeDelete() {
        Object object = ((AKMutableTableModel<KvzAdresse>) kvzAdressTable.getModel()).getDataAtRow(kvzAdressTable.getSelectedRow());
        if((object != null) && (object instanceof KvzAdresse))    {
            KvzAdresse kvzAdresse = (KvzAdresse) object;
            int confirmResult = MessageHelper.showConfirmDialog(this,
                    String.format("Adresse für KVZ-Nummer '%s' wirklich löschen?", kvzAdresse.getKvzNummer()),
                    "KVZ Adresse löschen?", JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION != confirmResult) {
                return;
            }
            try {
                getCCService(HVTService.class).deleteKvzAdresse(kvzAdresse);
                ((AKMutableTableModel<KvzAdresse>) kvzAdressTable.getModel()).removeObject(kvzAdresse);
            }
            catch(Exception e)  {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void executeEdit() {
        @SuppressWarnings("unchecked")
        Object object = ((AKMutableTableModel<T>) kvzAdressTable.getModel()).getDataAtRow(kvzAdressTable.getSelectedRow());
        if((object != null) && (object instanceof KvzAdresse))    {
            KvzAdresse kvzAdresse = (KvzAdresse) object;
            EditDialog dialogNew = new EditDialog(RESOURCE, kvzAdresse,
                    Lists.newArrayList(KVZ_ADRESSE_PROPERTY_NAMES));
            try {
                Object objFromDialog = DialogHelper.showDialog(this, dialogNew, true, true);
                if((objFromDialog != null) && (objFromDialog instanceof KvzAdresse))    {
                    KvzAdresse kvzAdressetoSave = (KvzAdresse) objFromDialog;
                    kvzAdressetoSave.setKvzNummer(KvzAdresse.formatKvzNr(kvzAdressetoSave.getKvzNummer()));
                    getCCService(HVTService.class).saveKvzAdresse(kvzAdressetoSave);
                    kvzAdresseTableModel.fireTableDataChanged();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
                showDetails(hvtStandort);
            }
        }
    }
}


