/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2005 13:33:41
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;

/**
 * Implementierung von AKJTable. <br> Die Tabelle ist dafuer gedacht, Objekte vom Typ <code>VerlaufAbteilung</code>
 * darzustellen. Bei diesen Objekten wird die Hintergrundfarbe der Zeile auf rot gesetzt, wenn der Verlauf einer
 * Abteilung noch nicht abgeschlossen ist.
 *
 *
 */
public class VerlaufAbtTable extends AKJTable {
    private final Color red = new Color(255, 69, 69);

    /**
     * Konstruktor.
     */
    public VerlaufAbtTable(TableModel dm, int autoResizeMode, int selectionMode) {
        super(dm, autoResizeMode, selectionMode);
    }

    /**
     * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        AKMutableTableModel tbMdl = (AKMutableTableModel) getModel();
        Object value = tbMdl.getDataAtRow(row);
        if (value instanceof VerlaufAbteilung) {
            VerlaufAbteilung va = (VerlaufAbteilung) value;
            if (va.getDatumErledigt() == null) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(red);
                return c;
            }
        }

        return super.prepareRenderer(renderer, row, column);
    }
}

