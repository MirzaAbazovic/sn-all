/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKJTable;

/**
 * Abstrakte Klasse fuer alle Tables im WBCI-Umfeld.
 */
public abstract class AbstractWbciTable extends AKJTable {

    private static final long serialVersionUID = 3572678763607431671L;

    protected static final Color BG_COLOR_GREEN = new Color(0, 175, 0); // gruen
    protected static final Color BG_COLOR_RED = new Color(255, 100, 100); // helles rot
    protected static final Color BG_COLOR_YELLOW = Color.YELLOW;
    protected static final Color BG_COLOR_GREY = new Color(240, 240, 240); // grau für erste spalte
    protected static final Color BG_COLOR_BLACK = new Color(0, 0, 0);
    protected static final Color BG_COLOR_LIGHT_GREEN = new Color(0, 230, 0); // hell gruen
    protected static final Color BG_COLOR_ORANGE = new Color(255, 153, 0); // orange

    public AbstractWbciTable() {
        super();
    }

    public AbstractWbciTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public AbstractWbciTable(TableModel dm) {
        super(dm);
    }

    public AbstractWbciTable(TableModel dm, int autoResizeMode, int selectionMode) {
        super(dm, autoResizeMode, selectionMode);
    }

    public AbstractWbciTable(TableModel dm, int autoResizeMode, int selectionMode, boolean filterEnabled) {
        super(dm, autoResizeMode, selectionMode, filterEnabled);
    }

    public AbstractWbciTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    public AbstractWbciTable(Vector<?> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
    }

    public AbstractWbciTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public AbstractWbciTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }
}
