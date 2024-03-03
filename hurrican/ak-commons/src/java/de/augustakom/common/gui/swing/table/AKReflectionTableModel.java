/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2005 11:28:06
 */
package de.augustakom.common.gui.swing.table;

import java.util.*;
import java.util.function.*;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * TableModel fuer Darstellung beliebiger Objekte. <br> Die darzustellenden Daten werden per Reflection ermittelt.
 *
 *
 */
public class AKReflectionTableModel<T> extends AKTableModel<T> implements AKFilterTableModel {

    private static final long serialVersionUID = -1483952345592844054L;

    private static final Logger LOGGER = Logger.getLogger(AKReflectionTableModel.class);

    public static final String FILTER_PARAM_TABLE = "table";

    protected String[] columnNames = null;
    protected String[] propertyNames = null;
    protected Class<?>[] classTypes = null;
    private Map<String, String> colNames2Property = null;


    /**
     * Konstruktor mit Angabe der Spalten- und Property-Namen sowie der Klassentypen.
     *
     * @param columnNames   Angabe der Spaltennamen fuer die Tabelle
     * @param propertyNames Angabe der Property-Namen der darzustellenden Bean (in der gleichen Reihenfolge wie
     *                      <code>columnNames</code>!)
     * @param classTypes    Angabe der Klassen (in der Reihenfolge von <code>propertyNames</code>.)
     * @throws IllegalArgumentException
     */
    public AKReflectionTableModel(String[] columnNames, String[] propertyNames,
            Class<?>[] classTypes) {
        super();
        this.columnNames = (String[]) ArrayUtils.clone(columnNames);
        this.propertyNames = (String[]) ArrayUtils.clone(propertyNames);
        this.classTypes = (Class<?>[]) ArrayUtils.clone(classTypes);

        if ((this.columnNames == null) || (this.propertyNames == null)) {
            throw new IllegalArgumentException("Es muessen Column-Names und Properties angegeben werden!");
        }

        if (this.columnNames.length != this.propertyNames.length) {
            throw new IllegalArgumentException("Die Anzahl der Column-Names und der Properties stimmt nicht ueberein!");
        }
        init();
    }

    /**
     * Konstruktor mit Angabe der Spalten- und Property-Namen sowie der Klassentypen in Form von einem
     * ReflectionTableMetaData-Objekt.
     *
     * @param reflectionTableMetaDatas Liste aller Metadaten, die notwendig sind zum Aufbau des Tabellenmodells
     */
    public AKReflectionTableModel(ReflectionTableMetaData... reflectionTableMetaDatas) {
        assert reflectionTableMetaDatas != null;
        final int length = reflectionTableMetaDatas.length;
        this.columnNames = new String[length];
        this.propertyNames = new String[length];
        this.classTypes = new Class[length];
        for (int i = 0; i < reflectionTableMetaDatas.length; i++) {
            ReflectionTableMetaData reflectionTableMetaData = reflectionTableMetaDatas[i];
            columnNames[i] = reflectionTableMetaData.getColumnName();
            propertyNames[i] = reflectionTableMetaData.getPropertyName();
            classTypes[i] = reflectionTableMetaData.getPropertyType();
        }
        init();
    }

    /* Initialisiert das TableModel */
    private void init() {
        // Column- und Property-Names werden in einer Map gespeichert.
        // Diese wird benoetigt, falls gefiltert werden soll und die Spalten
        // umsortiert sind.
        colNames2Property = new HashMap<String, String>();
        for (int i = 0; i < columnNames.length; i++) {
            colNames2Property.put(columnNames[i], propertyNames[i]);
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return (filteredData != null) ? filteredData.size() : super.getRowCount();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Sucht in den Spalten Namen ({@code columnNames}) nach dem Index fuer den Parameter '{@code columnName}'.
     * @param columnName Bezeichner der Spalte, wie der Anwender ihn sieht
     * @return 0 basierten Index oder empty, wenn suche nicht erfolgreich
     */
    @Override
    public Optional<Integer> getColumnIndex(String columnName) {
        if (columnName == null || columnNames == null || getColumnCount() <= 0) {
            return Optional.empty();
        }
        // Streams bzw. parallele Streams sind bei der Ermittlung des Indexes problematisch, da keine Reihenfolge
        // garantiert werden kann!
        for(int i=0; i < columnNames.length; i++) {
            if (StringUtils.equals(columnName, columnNames[i])) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o != null) {
            try {
                return PropertyUtils.getProperty(o, propertyNames[column]);
            }
            catch (NestedNullException e) {
                return null;
            }
            catch (NoSuchMethodException e) {
                try {
                    // try to get value with 'get'+property
                    String getter = String.format("get%s", StringUtils.capitalize(propertyNames[column]));
                    return MethodUtils.invokeMethod(o, getter, null);
                }
                catch (Exception ex) {
                    LOGGER.error(e.getMessage(), e);
                    return "<property not valid>";
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return "<property not valid>";
            }
        }

        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if ((classTypes != null) && ((classTypes.length - 1) >= columnIndex)) {
            return classTypes[columnIndex];
        }
        return super.getColumnClass(columnIndex);
    }

    public int findRow(String propertyName, Object value) {
        for (int i = 0; i < getRowCount(); i++) {
            Object o = getDataAtRow(i);
            if (o != null) {
                try {
                    Object property = PropertyUtils.getProperty(o, propertyName);
                    if (value.equals(property)) {
                        return i;
                    }
                }
                catch (NestedNullException e) {
                    continue;
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    continue;
                }
            }
        }
        return -1;
    }

    /** Findet die erste Zeile für die das Prädikat 'true' liefert. */
    public int findFirstRowBy(Predicate<T> predicate) {
        for (int i = 0; i < getRowCount(); i++) {
            final T dataAtRow = getDataAtRow(i);
            if (dataAtRow != null && predicate.test(dataAtRow)) {
                return i;
            }
        }
        return -1;
    }
}
