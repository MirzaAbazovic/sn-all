/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.01.2012 20:00:14
 */
package de.augustakom.hurrican.gui.tools.tal;

import de.augustakom.common.gui.swing.table.AKTableModelXML;

/**
 * Ein Table-Model, bei dem die Zeile in der das {@code immutableObject} steht, nicht editierbar ist.
 */
public class XmlImmutableRowTableModel<T> extends AKTableModelXML<T> {

    private static final long serialVersionUID = -6976707158585636182L;
    private Object immutableObject;

    public XmlImmutableRowTableModel(String xmlDef) {
        super(xmlDef);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (this.getDataAtRow(row) == immutableObject) {
            return false;
        }
        return super.isCellEditable(row, column);
    }

    public void setImmutableObject(Object immutableObject) {
        this.immutableObject = immutableObject;
    }
}
