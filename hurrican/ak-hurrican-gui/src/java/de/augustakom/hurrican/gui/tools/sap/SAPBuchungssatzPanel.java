/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 10:28:10
 */
package de.augustakom.hurrican.gui.tools.sap;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.FloatTableCellRenderer;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.exmodules.sap.SAPBuchungssatz;


/**
 * Panel zur Anzeige von SAP-Buchungssätzen <br>
 *
 *
 */
public class SAPBuchungssatzPanel extends AbstractDataPanel implements AKTableOwner {

    private AKReflectionTableModel<SAPBuchungssatz> tbMdlBSs = null;

    private boolean guiCreated = false;

    /**
     * Default-Const.
     */
    public SAPBuchungssatzPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        if (guiCreated) { return; }

        tbMdlBSs = new AKReflectionTableModel<SAPBuchungssatz>(
                new String[] { "Zuordnung", "Belegnummer", "Art", "Belegdatum", "Buchungsdatum", "Betrag HW",
                        "Buchungstext", "Referenz", "Zahlungbedingung", "Nettofälligkeit", "Ausgleichsdatum",
                        "Mahnstufe", "Datum ltzte Mahnung", "Mahnbereich" },
                new String[] { "allocNo", "docNo", "docType", "docDate", "pstngDate", "lcAmount",
                        "itemText", "refDocNo", "pmntTrms", "blineDate", "clearDate",
                        "dunnLevel", "lastDunn", "dunnArea" },
                new Class[] { String.class, String.class, String.class, Date.class, Date.class, Float.class,
                        String.class, String.class, String.class, Date.class, Date.class,
                        Integer.class, Date.class, String.class }
        );
        AKJTable tbBSs = new AKJTable(tbMdlBSs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);

        tbBSs.attachSorter();
        tbBSs.addKeyListener(getRefreshKeyListener());
        tbBSs.addTableListener(this);
        tbBSs.fitTable(new int[] { 85, 65, 30, 70, 70, 80, 130, 90, 50, 70, 70, 30, 70, 30 });

        TableColumn col = tbBSs.getColumnModel().getColumn(5);
        col.setCellRenderer(new FloatTableCellRenderer("#0.00"));

        AKJScrollPane spTTs = new AKJScrollPane(tbBSs, new Dimension(830, 400));

        this.setLayout(new BorderLayout());
        this.add(spTTs, BorderLayout.NORTH);
        guiCreated = true;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * Funktion um das TableModel mit Daten zu füllen
     *
     * @param list
     *
     */
    public void setTableData(List<SAPBuchungssatz> list) {
        if (CollectionTools.isNotEmpty(list)) {
            // Ändere Reihenfolge der Datensätze, dass neueste Buchung zuerst erscheint
            tbMdlBSs.setData(CollectionTools.reverse(list));
        }
        else {
            tbMdlBSs.removeAll();
        }
        tbMdlBSs.fireTableDataChanged();

    }
}
