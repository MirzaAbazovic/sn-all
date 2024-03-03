/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.01.2012 19:56:08
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;

/**
 * Faerbt die Zeile in der Tabelle, in der das gegebene Objekt steht (zur Zeit gruen) *
 */
public class ColoredRowTable extends AKJTable {

    private static final long serialVersionUID = 7795350627038868640L;
    protected static final Color BG_COLOR_IMMUTABLE = new Color(0, 175, 0); // gruen
    private Object coloredObject;

    public ColoredRowTable(TableModel dm, int autoResizeMode, int selectionMode) {
        super(dm, autoResizeMode, selectionMode);
    }

    /**
     * Diese Methode wird von JTable immer aufgerufen. <br> Standardmaessig wird jede zweite Zeile in einer anderen
     * Farbe aufgerufen. <br> Wenn die aktuelle Zeile gleich dem uebergebenen Object ist, wird ebenfalls eine andere
     * Farbe verwendet.
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if (getModel() instanceof AKMutableTableModel) {
            Object data = ((AKMutableTableModel<?>) getModel()).getDataAtRow(row);

            if (data == coloredObject) {
                comp.setBackground(BG_COLOR_IMMUTABLE);
            }
        }

        return comp;
    }

    public void setColoredObject(Object immutableObject) {
        this.coloredObject = immutableObject;
    }
}
