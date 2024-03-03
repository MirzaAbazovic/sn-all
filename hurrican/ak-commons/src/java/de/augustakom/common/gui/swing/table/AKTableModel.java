/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2004
 */
package de.augustakom.common.gui.swing.table;

import java.util.*;
import javax.swing.table.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.ResourceReader;

/**
 * AK-Implementierung eines TableModels.
 *
 * @param <T> der Typ einer Zeile im Table-Modell
 * @see javax.swing.table.DefaultTableModel
 */
public class AKTableModel<T> extends DefaultTableModel implements AKMutableTableModel<T>, AKTableSorterAware<T>, AKFilterTableModel {

    private static final long serialVersionUID = 4043219820304999406L;

    private static final Logger LOGGER = Logger.getLogger(AKTableModel.class);

    protected Collection<T> data = null;
    private ResourceReader resourceReader = null;
    private String resource = null;
    private AKTableSorter<T> tableSorter = null;

    // Variablen für Filterung
    protected Collection<T> filteredData = null;
    private FilterRelation filter = new FilterRelation(FilterRelations.AND);
    private Boolean useFilter = Boolean.TRUE;
    private AKFilterTableModelListener filterListener = null;

    protected Collection<Integer> disabledRows = null;

    /**
     * Erzeugt ein neues TableModel
     */
    public AKTableModel() {
        super();
    }

    /**
     * Konstruktor fuer ein TableModel mit Angabe einer Resource-Datei (.properties) fuer den ResourceReader.
     */
    public AKTableModel(String resource) {
        super();
        this.resource = resource;
        init();
    }

    /**
     * Erzeugt ein neues TableModel mit Angabe der darzustellenden Daten ueber das zweidimensionale Array
     * <code>data</code> und der Spaltennamen (<code>columnNames</code>).
     */
    public AKTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }


    /**
     * @see de.augustakom.common.gui.swing.table.AKTableSorterAware#setTableSorter(de.augustakom.common.gui.swing.table.AKTableSorter)
     */
    @Override
    public void setTableSorter(AKTableSorter<T> tableSorter) {
        this.tableSorter = tableSorter;
    }

    /**
     * Gibt den TableSorter zurueck, der der Tabelle des Modells zugeordnet wurde.
     */
    protected AKTableSorter<T> getTableSorter() {
        return tableSorter;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        if (filteredData != null) {
            return filteredData.size();
        }
        return (data != null) ? data.size() : 0;
    }

    /**
     * Setzt die Daten fuer das TableModel. <br> Evtl. vorgenommene Filterungen werden hierdurch entfernt.
     */
    @Override
    public void setData(Collection<T> data) {
        this.data = data;
        doFilter();
    }

    /**
     * Gibt die Daten des TableModels zurueck. <br> Voraussetzung: Die Daten wurden zuvor ueber die Methode
     * <code>setData</code> gesetzt.
     *
     * @return Collection mit den Daten-Objekten.
     */
    @Override
    public Collection<T> getData() {
        return this.data;
    }

    public Collection<T> getData(boolean recognizeFiltered) {
        if (recognizeFiltered && filteredData != null) {
            return filteredData;
        }
        return getData();
    }

    /**
     * Fuegt dem TableModel ein neues Objekt hinzu.
     */
    @Override
    public void addObject(T obj) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        data.add(obj);
        doFilter();
    }

    /**
     * Fuegt dem TableModel alle Objekte aus <code>data</code> hinzu.
     */
    @Override
    public void addObjects(Collection<? extends T> data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        if (data != null) {
            this.data.addAll(data);
            doFilter();
        }
    }

    /**
     * Ersetzt das Objekt an Position <code>index</code> durch <code>obj</code>
     */
    public void replaceObject(int index, T obj) {
        if ((data != null) && (data instanceof List<?>)) {
            ((List<T>) data).set(index, obj);
            doFilter();
        }
    }

    /**
     * Entfernt das angegebene Objekt aus dem TableModel bzw. aus der Data-Liste.
     */
    @Override
    public void removeObject(Object objToRemove) {
        if (data != null) {
            data.remove(objToRemove);
            doFilter();
        }
    }

    /**
     * Entfernt alle Eintraege aus dem TableModel.
     */
    @Override
    public void removeAll() {
        if (data != null) {
            data.clear();
            clearFilter();
            fireTableDataChanged();
        }
    }

    /**
     * Entfernt alle Eintraege aus dem TableModel ausser Filter.
     */
    public void removeAllButKeepFilter() {
        if (data != null) {
            data.clear();
            fireTableDataChanged();
        }
    }

    /**
     * Gibt das Daten-Objekt an der angegebenen Zeile zurueck. <br> Die Methode kann nur dann ein Objekt zurueck geben,
     * wenn zuvor ueber die Methode <code>setData</code> eine Collection mit Objekten gesetzt wurde.
     *
     * @param row Zeilennummer, dessen Objekt gelesen werden soll.
     * @return Objekt an der angegebenen Zeile oder <code>null</code>.
     */
    @Override
    public T getDataAtRow(int row) {
        if ((filteredData != null) && (!filteredData.isEmpty()) && useFilter) {
            return getRow(row, filteredData);
        }
        return getRow(row, data);
    }

    /**
     * Hilfsfunktion für getDataAtRow, um eine Zeile einer bel. Collection zu liefern
     */
    private T getRow(int row, Collection<T> data) {
        if ((row >= 0) && (data != null) && (data.size() > row)) {
            if (data instanceof List<?>) {
                return ((List<T>) data).get(row);
            }
            Iterator<T> it = data.iterator();
            int count = 0;
            while (it.hasNext()) {
                if (count == row) {
                    return it.next();
                }
                it.next();
                count++;
            }
        }
        return null;
    }

    /**
     * Initialisiert das TableModel
     */
    private void init() {
        if (resource != null) {
            resourceReader = new ResourceReader(resource);
        }
    }

    /**
     * Gibt den ResourceReader fuer das TableModel zurueck.
     *
     * @return Instanz von <code>de.augustakom.common.tools.lang.ResourceReader</code>
     */
    protected ResourceReader getResourceReader() {
        return resourceReader;
    }


    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModel#clearFilter()
     */
    @Override
    public void clearFilter() {
        filter = new FilterRelation(FilterRelations.AND);
        filteredData = null;
        dataChanged();
    }


    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModel#removeFilter(java.lang.String)
     */
    @Override
    public void removeFilter(String name) {
        filter.removeFilter(name);
        doFilter();
    }


    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModel#addFilter(de.augustakom.common.gui.swing.table.FilterRelation)
     */
    @Override
    public void addFilter(FilterRelation newFilter) {
        this.filter.addChild(newFilter);
        doFilter();
    }


    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModel#addFilter(de.augustakom.common.gui.swing.table.FilterOperator)
     */
    @Override
    public void addFilter(FilterOperator newFilter) {
        this.filter.addChild(newFilter);
        doFilter();
    }


    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModel#setFilter(de.augustakom.common.gui.swing.table.FilterRelation)
     */
    @Override
    public void setFilter(FilterRelation newFilter) {
        if (newFilter == null) {
            clearFilter();
        }
        else {
            this.filter = newFilter;
            doFilter();
        }
    }

    /**
     * Liefert den Zeilenindex fuer das uebergebene Datenobjekt
     * @param data2Search das Datenobjekt, nach dem im Datenmodell gesucht wird
     * @return der optionale Zeilenindex im Datenmodell, der Index ist optional, da das Objekt evtl. nicht gefunden wird,
     * wenn z.B. ein Filter aktiv ist.
     */
    public Optional<Integer> getRowIndexOfData(T data2Search)
    {
        Collection<T> effectiveData = CollectionUtils.isEmpty(filteredData) ? filteredData : data;
        if (effectiveData != null)
        {
            int row = 0;
            for (T data : effectiveData) {
                if (data.equals(data2Search)) {
                    return Optional.of(row);
                }
                row++;
            }
        }
        return Optional.empty();
    }


    /**
     * Filtert die Tabellendaten
     */
    protected void doFilter() {
        try {
            filteredData = null;
            filteredData = filter.filter(this, data);
            dataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            useFilter = Boolean.TRUE;
        }
    }


    /**
     * Informiert Listener und TableModel über Änderungen
     */
    private void dataChanged() {
        fireTableDataChanged();
        if (filterListener != null) {
            filterListener.tableFiltered();
        }
    }


    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModel#addFilterTableModelListener(de.augustakom.common.gui.swing.table.AKFilterTableModelListener)
     */
    @Override
    public void addFilterTableModelListener(AKFilterTableModelListener listener) {
        this.filterListener = listener;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModel#removeFilterTableModelListener()
     */
    @Override
    public void removeFilterTableModelListener() {
        this.filterListener = null;
    }

    @Override
    public void setDisabledRows(Collection<Integer> disabledRows) {
        this.disabledRows = disabledRows;
    }

    @Override
    public void disableRow(int row) {
        if (this.disabledRows == null) {
            this.disabledRows = new ArrayList<>();
        }
        disabledRows.add(row);
    }

    @Override
    public void enableRow(int row) {
        if (this.disabledRows != null) {
            disabledRows.remove(row);
        }
    }

    @Override
    public void enableAllRows() {
        if (this.disabledRows != null) {
            disabledRows.clear();
        }
    }

    @Override
    public boolean isRowEnabled(int row) {
        return this.disabledRows == null || !disabledRows.contains(row);
    }

    @Override
    public void removeObjects(Collection<? extends T> objs) {
        this.data.removeAll(objs);
        doFilter();
    }
}
