/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2007 09:39:16
 */
package de.augustakom.common.gui.swing.table;

import java.awt.event.*;
import javax.swing.table.*;
import org.apache.commons.lang.ObjectUtils;

import de.augustakom.common.gui.swing.AKJTable;


/**
 * MouseListener fuer einen Table-Header. <br> Listener liefert Popup-Menü um Tabelle zu filtern und ermöglicht mit
 * einem Linksklick das Sortieren der Datensätze.
 *
 *
 */
public class AKTableHeaderMouseListener extends AKTablePopupMouseListener {

    private AKTableSorter<?> sorter = null;
    private boolean holdSelection = false;

    /**
     * Konstruktor mit Angabe des TableSorters, der von dem MouseListener angesprochen werden soll.
     *
     * @param sorter
     */
    public AKTableHeaderMouseListener(AKTableSorter<?> sorter) {
        super();
        super.setChangeSelectionOnPopup(false);
        super.setShowCopyActions(false);
        this.sorter = sorter;
    }

    /**
     * @param sorter
     * @param holdSelection
     * @see AKTableHeaderMouseListener(AKTableSorter) Ueber den Parameter {@code holdSelection} wird definiert, ob die
     * Table-Selection nach einer Sortierung bestehen bleiben soll ({@code true}) oder nicht ({@code false}). <br> Dies
     * ist allerdings nur dann möglich, wenn nur eine einzelne Zeile selektiert war!
     */
    public AKTableHeaderMouseListener(AKTableSorter<?> sorter, boolean holdSelection) {
        super();
        super.setChangeSelectionOnPopup(false);
        super.setShowCopyActions(false);
        this.holdSelection = holdSelection;
        this.sorter = sorter;
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        sort(e);
    }

    /* Funktion stößt das Sortieren einer Tabelle an */
    private void sort(MouseEvent e) {
        AKJTable table2Sort = null;
        if (e.getSource() instanceof JTableHeader) {
            table2Sort = (AKJTable) ((JTableHeader) e.getSource()).getTable();
            if (e.getButton() != MouseEvent.BUTTON3) {
                TableColumnModel columnModel = table2Sort.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = table2Sort.convertColumnIndexToModel(viewColumn);
                if ((e.getClickCount() == 1) && (column != -1)) {

                    AKMutableTableModel<?> tableModel = (AKMutableTableModel<?>) table2Sort.getModel();

                    int lastSelectedRow = -1;
                    Object lastSelectedObject = null;
                    if (holdSelection) {
                        lastSelectedRow = table2Sort.getSelectedRow();
                        if ((lastSelectedRow >= 0) && (table2Sort.getSelectedRowCount() == 1)) {
                            lastSelectedObject = tableModel.getDataAtRow(lastSelectedRow);
                        }
                    }

                    sorter.setAscending(!sorter.isAscending());
                    sorter.sortByColumn(column, sorter.isAscending());

                    if (holdSelection && (lastSelectedObject != null)) {
                        int rowCount = table2Sort.getRowCount();
                        for (int i = 0; i < rowCount; i++) {
                            Object objectAtRow = tableModel.getDataAtRow(i);
                            if (ObjectUtils.equals(objectAtRow, lastSelectedObject)) {
                                AKJTable table = table2Sort;
                                table.selectAndScrollToRow(i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

}
