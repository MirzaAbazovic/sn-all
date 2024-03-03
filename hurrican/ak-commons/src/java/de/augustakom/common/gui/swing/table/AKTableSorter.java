/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2004 10:43:48
 */

package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.apache.log4j.Logger;


/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel) and itself implements TableModel.
 * TableSorter does not store or copy the data in the TableModel, instead it maintains an array of integers which it
 * keeps the same size as the number of rows in its model. When the model changes it notifies the sorter that something
 * has changed eg. "rowsAdded" so that its internal array of integers can be reallocated. As requests are made of the
 * sorter (like getValueAt(row, col) it redirects them to its model via the mapping array. That way the TableSorter
 * appears to hold another copy of the table with the rows in a different order. The sorting algorithm used is stable
 * which means that it does not move around rows when its comparison function returns 0 to denote that they are
 * equivalent.
 */
public class AKTableSorter<T> extends AKTableMap implements AKMutableTableModel<T>, Comparator<Integer> {

    private static final Logger LOGGER = Logger.getLogger(AKTableSorter.class);

    private Integer indexes[];
    private List<Integer> sortingColumns = new Vector<>();
    private boolean ascending = false;
    private boolean isSorted = false;
    private boolean holdSelection = false;

    private JTable table2Sort = null;

    private Object modelCache[][];
    private Class<?> modelClassCache[];

    /**
     * Standardkonstruktor
     */
    public AKTableSorter() {
        indexes = new Integer[0];
    }

    /**
     * Konstruktor mit Angabe des TableModels.
     */
    public AKTableSorter(TableModel model) {
        setModel(model);
    }

    public AKTableSorter(TableModel model, boolean holdSelection) {
        this.holdSelection = holdSelection;
        setModel(model);
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableMap#setModel(javax.swing.table.TableModel)
     */
    @Override
    public void setModel(TableModel model) {
        super.setModel(model);

        if (model instanceof AKTableSorterAware) {
            //noinspection unchecked
            ((AKTableSorterAware<T>) model).setTableSorter(this);
        }

        reallocateIndexes();
    }

    /**
     * Vergleicht die Daten von zwei Zeilen einer bestimmten Spalte.
     */
    @SuppressWarnings("Duplicates")
    private int compareRowsByColumn(int row1, int row2, int column) {
        Class<?> type = modelClassCache[column];

        Object o1 = modelCache[row1][column];
        Object o2 = modelCache[row2][column];

        // Check for nulls.
        // If both values are null, return 0.
        if ((o1 == null) && (o2 == null)) {
            return 0;
        }
        else if (o1 == null) { // Define null less than everything.
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }

        if (Comparable.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            Comparable c1 = (Comparable) o1;
            @SuppressWarnings("unchecked")
            Comparable c2 = (Comparable) o2;
            @SuppressWarnings("unchecked")
            int result = c1.compareTo(c2);
            return result;
        }
        else {
            return o1.toString().compareTo(o2.toString());
        }
    }

    @Override
    public int compare(Integer row1, Integer row2) {
        for (Integer column : sortingColumns) {
            int result = compareRowsByColumn(row1, row2, column);
            if (result != 0) {
                return ascending ? result : -result;
            }
        }
        return 0;
    }

    /**
     * Baut die Indexe des TableModels neu auf.
     */
    private void reallocateIndexes() {
        int rowCount = model.getRowCount();

        // Set up a new array of indexes with the right number of elements
        // for the new data model.
        modelCache = new Object[model.getRowCount()][model.getColumnCount()];
        modelClassCache = new Class<?>[model.getColumnCount()];
        for (int column = 0; column < model.getColumnCount(); column++) {
            modelClassCache[column] = model.getColumnClass(column);
            for (int row = 0; row < model.getRowCount(); row++) {
                modelCache[row][column] = model.getValueAt(row, column);
            }
        }

        indexes = new Integer[rowCount];

        // Initialise with the identity mapping.
        for (int row = 0; row < rowCount; row++) {
            indexes[row] = row;
        }
    }

    /**
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        reallocateIndexes();
        super.tableChanged(e);
    }

    /**
     * Ueberprueft, ob das hinterlegte TableModel noch aktuell ist.
     */
    private void checkModel() {
        if (indexes.length != model.getRowCount()) {
            LOGGER.info("TableSorter wurde nicht ueber eine Aenderung im TableModel informiert!");
            setModel(model);
        }
    }

    /**
     * Fuehrt die Sortierung durh.
     */
    protected void sort() {
        checkModel();
        Arrays.sort(indexes, this);
        isSorted = true;
    }


    /**
     * Leitet den Aufruf an das TableModel weiter, das sortiert wird.
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int aRow, int aColumn) {
        checkModel();
        return model.getValueAt(indexes[aRow], aColumn);
    }

    /**
     * Leitet den Aufruf an das TableModel weiter, das sortiert wird.
     *
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        checkModel();
        model.setValueAt(aValue, indexes[aRow], aColumn);
        //Eine evt. existierende Sortierung sollte nicht aufgehoben werden, da der Benutzer Spalten
        // editiert und die Tabelle nicht ständig neu sortiert haben möchte. Darüber hinaus müsste die
        // Tabelle über Änderungen in der Sortierung benachrichtigt werden.
    }

    /**
     * Sortiert das TableModel ueber die Spalte <code>column</code>. <br> Ueber das Flag <code>ascending</code> kann
     * angegeben werden, ob die Sortierung aufsteigend (true) oder absteigend (false) erfolgen soll.
     *
     * @param column    Index der Spalte, nach der sortiert werden soll
     * @param ascending Sortierung soll aufsteigend/absteigend erfolgen
     */
    void sortByColumn(int column, boolean ascending) {
        try {
            setWaitCursor();

            this.ascending = ascending;
            sortingColumns.clear();
            sortingColumns.add(column);
            sort();
            super.tableChanged(new TableModelEvent(this));
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Fuegt dem TableHeader der Table einen MouseListener hinzu. <br> Ueber diesen MouseListener wird die Sortierung
     * ausgeloest, wenn ein TableHeader angeklickt wird.
     */
    public void addMouseListenerToHeaderInTable(JTable table) {
        final AKTableSorter<T> sorter = this;
        table2Sort = table;
        table2Sort.setColumnSelectionAllowed(false);

        AKTableHeaderMouseListener listMouseListener = new AKTableHeaderMouseListener(sorter, holdSelection);
        JTableHeader th = table2Sort.getTableHeader();
        th.addMouseListener(listMouseListener);
    }

    /**
     * Entfernt all mouseListener auf table2Sort.
     */
    public void clearMouseListenerToHeaderInTable() {
        for (MouseListener ml : table2Sort.getTableHeader().getMouseListeners()) {
            if (ml instanceof AKTableHeaderMouseListener) {
                table2Sort.getTableHeader().removeMouseListener(ml);
            }
        }
    }

    /**
     * @return Objekt an der angegebenen Zeile.
     * @throws IllegalArgumentException Kann zwei Ursachen haben: <ul> <li> Wird geworfen, wenn versucht wird, auf einen
     *                                  Index zuzugreifen, der nicht vorhanden ist. <br> <li> urspruengliches TableModel
     *                                  ist nicht vom Typ ITableModelRowSelector </ul>
     * @see de.augustakom.common.gui.swing.table.AKMutableTableModel Gibt das Objekt an der angegebenen Zeile (row)
     * zurueck. <br>
     */
    @Override
    public T getDataAtRow(int row) {
        int rowToSelect = row;
        if (isSorted) {
            /*
             * Ist die Table sortiert, dann aendert sich nicht die
             * Reihenfolge im Vektor bzw der ArrayList sondern nur der
             * Index innerhalb des Modells.
             * Die Zeilennummer wird deshalb hier umgesetzt.
             */
            rowToSelect = getSortedRowNum(row);
        }

        if (getModel() instanceof AKMutableTableModel) {
            return getAKMutableTableModel().getDataAtRow(rowToSelect);
        }
        else {
            throw new IllegalArgumentException("TableModel of TableSorter is not of type AKMutableTableModel!");
        }
    }

    @SuppressWarnings("unchecked")
    private AKMutableTableModel<T> getAKMutableTableModel() {
        if (getModel() instanceof AKMutableTableModel) {
            return ((AKMutableTableModel<T>) getModel());
        }
        else {
            throw new IllegalArgumentException("TableModel of TableSorter is not of type AKMutableTableModel!");
        }

    }

    /**
     * Liefert den Zeilenindex fuer das uebergebene Datenobjekt
     * @param data2Search das Datenobjekt, nach dem im Datenmodell gesucht wird
     * @return der optionale Zeilenindex im Datenmodell, der Index ist optional, da das Objekt evtl. nicht gefunden wird,
     * wenn z.B. ein Filter aktiv ist.
     */
    public Optional<Integer> getRowIndexOfData(T data2Search) {
        for (int row = 0; row < getRowCount(); row++) {
            T data = getDataAtRow(row);
            if (data.equals(data2Search)) {
                return Optional.of(row);
            }
        }
        return Optional.empty();
    }


    /**
     * Gibt den Index der Zeile 'row' zurueck, an welchem sich das Modell in der sortierten List befindet.
     *
     * @param row Zeile, zu der der Index in der sortierten Liste ermittelt werden soll.
     * @return Index, an welchem sich die Zeile in der Liste befindet.
     */
    private int getSortedRowNum(int row) {
        if (indexes.length > row) {
            return indexes[row];
        }
        throw new IllegalArgumentException("Could not get row " + row + " of " + indexes.length + " rows");
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKMutableTableModel#setData(java.util.Collection)
     */
    @Override
    public void setData(Collection<T> data) {
        getAKMutableTableModel().setData(data);
        reallocateIndexes();
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKMutableTableModel#getData()
     */
    @Override
    public Collection<T> getData() {
        return getAKMutableTableModel().getData();
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKMutableTableModel#addObject(java.lang.Object)
     */
    @Override
    public void addObject(T obj) {
        getAKMutableTableModel().addObject(obj);
        reallocateIndexes();
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKMutableTableModel#removeObject(java.lang.Object)
     */
    @Override
    public void removeObject(Object objToRemove) {
        getAKMutableTableModel().removeObject(objToRemove);
        reallocateIndexes();
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKMutableTableModel#removeAll()
     */
    @Override
    public void removeAll() {
        getAKMutableTableModel().removeAll();
        reallocateIndexes();
    }

    @Override
    public void disableRow(int row) {
        getAKMutableTableModel().disableRow(row);
    }

    @Override
    public void enableRow(int row) {
        getAKMutableTableModel().enableRow(row);
    }

    @Override
    public void enableAllRows() {
        getAKMutableTableModel().enableAllRows();
    }

    @Override
    public boolean isRowEnabled(int row) {
        return getAKMutableTableModel().isRowEnabled(row);
    }

    @Override
    public void setDisabledRows(Collection<Integer> disabledRows) {
        getAKMutableTableModel().setDisabledRows(disabledRows);
    }

    /* Aendert den Tabellen-Cursor auf 'WAIT' */
    private void setWaitCursor() {
        if (table2Sort != null) {
            table2Sort.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }

    /* Aendert den Tabellen-Cursor auf 'DEFAULT' */
    private void setDefaultCursor() {
        if (table2Sort != null) {
            table2Sort.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    boolean isAscending() {
        return ascending;
    }

    void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public void addObjects(Collection<? extends T> objs) {
        getAKMutableTableModel().addObjects(objs);
    }

    @Override
    public void removeObjects(Collection<? extends T> objs) {
        getAKMutableTableModel().removeObjects(objs);
    }
}
