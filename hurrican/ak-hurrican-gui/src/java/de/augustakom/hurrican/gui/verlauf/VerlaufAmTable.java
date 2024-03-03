/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2015
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.view.AbstractVerlaufView;


/**
 * AKJTable-Implementierung fuer die Bauauftragruecklaeufermaske-AM).
 * Für den Benutzer wird durch eine farbige Markierung ersichtlich, ob der BA den Status "erledigt" hat
 * oder als "NOT POSSIBLE" gekennzeichnet ist.
 *
 */
public class VerlaufAmTable extends AKJTable implements Observer {

    private static final long serialVersionUID = -6817157410261230596L;

    protected static final Color BG_COLOR_FINISH = new Color(0, 175, 0);          // gruen
    protected static final Color BG_COLOR_NOT_POSSIBLE = new Color(255, 0, 0);    // rot

    public VerlaufAmTable() {
        super();
    }

    /**
     * Diese Methode wird von JTable immer aufgerufen. <br> In dieser Implementierung wird die Hintergrundfarbe jeder
     * zweiten Zeile geaendert.
     *
     * @see javax.swing.JTable#prepareRenderer(TableCellRenderer, int, int)
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if (getModel() instanceof AKMutableTableModel) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<AbstractVerlaufView> model = (AKMutableTableModel<AbstractVerlaufView>) getModel();

            AbstractVerlaufView view = model.getDataAtRow(row);

            if (view.getHasSubOrders()) {
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
            }

            if (view.isGuiFinished()) {
                comp.setBackground(BG_COLOR_FINISH);
                return comp;
            }

            if (!isCellSelected(row, column) && BooleanTools.nullToFalse(view.getVerlaufNotPossible())) {
                // hervorheben, falls Verlauf als 'NOT_POSSIBLE' gekennzeichnet ist
                comp.setBackground(BG_COLOR_NOT_POSSIBLE);
                return comp;
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


