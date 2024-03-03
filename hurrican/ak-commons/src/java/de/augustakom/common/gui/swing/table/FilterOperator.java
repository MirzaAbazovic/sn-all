/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2010 18:49:48
 */
package de.augustakom.common.gui.swing.table;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Operator fuer die Filterung von Daten - EQ, NEQ, ... Immutable, damit Thread-safe. Besitzt einen Namen, der im
 * Standard-Fall {@code null} ist.
 * <p/>
 * Besitzt {@code protected} Kontruktoren, so dass er fuer nicht-Standard-Filter (z.B. Filter, die nicht auf dem
 * aktuellen Wert in einer Spalte filtern, sondern zusaetzliche Information aus dem View-Objekt ziehen) ueberschrieben
 * werden kann.
 *
 *
 */
public class FilterOperator {

    protected final Object filterValue;
    protected final Integer column;
    protected final FilterOperators filterOperator;
    protected final String name;
    protected final String propertyName;

    protected FilterOperator() {
        this(null, null, null, null);
    }

    protected FilterOperator(String name) {
        this(name, null, null, null);
    }

    protected FilterOperator(String name, Object filterValue) {
        this(name, null, filterValue, null);
    }

    public FilterOperator(FilterOperators filterOperator, Object filterValue, Integer column) {
        this(null, filterOperator, filterValue, column);
    }

    public FilterOperator(String name, FilterOperators filterOperator, Object filterValue, Integer column) {
        this(name, filterOperator, filterValue, column, null);
    }

    public FilterOperator(String name, FilterOperators filterOperator, Object filterValue, Integer column,
            String propertyName) {
        this.name = name;
        this.filterOperator = filterOperator;
        this.filterValue = filterValue;
        this.column = column;
        this.propertyName = propertyName;
    }

    public boolean filter(@SuppressWarnings("rawtypes") AKMutableTableModel model, int row) {
        Object objectToCompare = model.getValueAt(row, column);
        if (propertyName != null) {
            try {
                objectToCompare = PropertyUtils.getProperty(objectToCompare, propertyName);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(String.format("Unexpected exception thrown during retrieving " +
                        "property '%s' from the object '%s'", propertyName, objectToCompare), e);
            }
        }
        return filterOperator.compare(objectToCompare, filterValue);
    }

    public String getName() {
        return name;
    }

}
