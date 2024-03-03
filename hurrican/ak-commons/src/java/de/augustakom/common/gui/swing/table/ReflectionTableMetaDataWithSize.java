/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.13
 */
package de.augustakom.common.gui.swing.table;

public class ReflectionTableMetaDataWithSize extends ReflectionTableMetaData {

    /**
     * represents the column size for the GUI
     */
    int columnSize;

    public ReflectionTableMetaDataWithSize(String columnName, String propertyName, Class propertyType, int columnSize) {
        super(columnName, propertyName, propertyType);
        assert columnSize > 0;
        this.columnSize = columnSize;
    }

    public int getColumnSize() {
        return columnSize;
    }
}
