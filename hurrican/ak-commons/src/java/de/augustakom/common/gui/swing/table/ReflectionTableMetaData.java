/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.13
 */
package de.augustakom.common.gui.swing.table;

/**
 *
 */
public class ReflectionTableMetaData {

    /**
     * Name der Tabellenspalte
     */
    private String columnName;
    /**
     * Name des Objekt-Property, das in der Spalte angezeigt werden soll. Der Wert der Property wird ueber reflection
     * ermittelt.
     *
     * @see AKReflectionTableModel
     */
    private String propertyName;
    /**
     * Typ des Objekt-Property, das in der Spalte angezeigt werden soll. Der Wert der Property wird ueber reflection
     * ermittelt.
     *
     * @see AKReflectionTableModel
     */
    private Class propertyType;

    public ReflectionTableMetaData(String columnName, String propertyName, Class propertyType) {
        assert columnName != null && propertyName != null && propertyType != null;
        this.columnName = columnName;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class getPropertyType() {
        return propertyType;
    }

}
