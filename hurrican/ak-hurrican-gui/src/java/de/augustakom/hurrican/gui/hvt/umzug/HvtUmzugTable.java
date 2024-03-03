/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.05.2015
 */
package de.augustakom.hurrican.gui.hvt.umzug;

import java.awt.*;
import java.time.*;
import java.util.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetailView;


/**
 * AKJTable-Implementierung zur Kennzeichnung von Aufträgen mit AdditionalOrder.
 *
 */
public class HvtUmzugTable extends AKJTable implements Observer {

    protected static final Color BG_COLOR_RED = new Color(255, 0, 0);       // rot
    protected static final Color BG_COLOR_GREEN = new Color(0, 220, 45);    // grün
    protected static final Color BG_COLOR_ORANGE = new Color(255, 150, 0);  // orange

    private static final long serialVersionUID = 5214755710292468692L;

    public HvtUmzugTable(TableModel dm, int autoResizeMode, int selectionMode) {
        super(dm, autoResizeMode, selectionMode);
    }

    public HvtUmzugTable() {
        super();
    }

    /**
     * Diese Methode wird von JTable immer aufgerufen. <br> In dieser Implementierung wird die Hintergrundfarbe auf rot
     * geändert, wenn es zu dem Auftrag eine AdditionalOrder gibt
     *
     * @see javax.swing.JTable#prepareRenderer(TableCellRenderer, int, int)
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if (getModel() instanceof AKMutableTableModel) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<HvtUmzugDetailView> model = (AKMutableTableModel<HvtUmzugDetailView>) getModel();
            HvtUmzugDetailView view = model.getDataAtRow(row);

            if (DateTools.isDateAfterOrEqual(view.getWitaBereitstellungAm(), Date.from(view.getHvtUmzug().getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                comp.setBackground(BG_COLOR_ORANGE);
            }

            if (BooleanTools.nullToFalse(view.getRangNeuErzeugt())) {
                comp.setBackground(BG_COLOR_GREEN);
            }

            if (BooleanTools.nullToFalse(view.getAdditionalOrder())) {
                comp.setBackground(BG_COLOR_RED);
            }
        }

        return comp;
    }

    /**
     * @see Observer#update(Observable, Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }

}



