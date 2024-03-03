/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2007 09:47:15
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import javax.annotation.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.augustakom.hurrican.model.shared.iface.ICBVorgangStatusModel;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * AKJTable-Implementierung fuer Tables, die Objekte vom Typ <code>ICBVorgangModel</code> darstellen. <br> Je nach
 * Status des CB-Vorgangs wird die Hintergrundfarbe der Zeile anders dargestellt.
 */
public class CBVorgangTable extends AKJTable {

    private static final long serialVersionUID = 8788741966081724046L;

    protected static final Color BG_COLOR_FINISH_OK = new Color(0, 175, 0); // gruen
    protected static final Color BG_COLOR_FINISH_ERR = new Color(255, 100, 100); // helles rot
    protected static final Color BG_COLOR_CLOSED = new Color(255, 153, 0); // orange
    protected static final Color BG_COLOR_AUTOMATION_ERROR = new Color(192, 192, 192); // grau
    private String columnToColor;

    public CBVorgangTable() {
        super();
    }

    public CBVorgangTable(TableModel dm, int autoResizeMode, int selectionMode, @Nonnull String columnToColor) {
        super(dm, autoResizeMode, selectionMode);
        this.columnToColor = columnToColor;
    }

    /**
     * Diese Methode wird von JTable immer aufgerufen. <br> Standardmaessig wird jede zweite Zeile in einer anderen
     * Farbe aufgerufen. <br> Je nach Status vom CB-Vorgang bzw. dessen Prio-Flag/Aenderungskennzeichen, wird ebenfalls
     * eine andere Farbe verwendet.
     *
     * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if ((getModel() instanceof AKMutableTableModel) && !isRowSelected(row)) {

            String name = getColumnName(column);
            if (columnToColor.equals(name)) {
                Object data = ((AKMutableTableModel<?>) getModel()).getDataAtRow(row);

                if (data instanceof CBVorgangNiederlassung) {
                    CBVorgang cbVorgang = ((CBVorgangNiederlassung) data).getCbVorgang();
                    if (cbVorgang instanceof WitaCBVorgang) {
                        WitaCBVorgang witaCbVorgang = (WitaCBVorgang) cbVorgang;
                        if (!witaCbVorgang.isClosed() &&
                                (witaCbVorgang.aenderungsKennzeichenIsDifferent()
                                        || witaCbVorgang.getPrio())) {
                            comp.setBackground(VorgangTable.DTAG_MAGENTA);
                        }
                    }
                }

                if (data instanceof ICBVorgangStatusModel) {
                    ICBVorgangStatusModel status = (ICBVorgangStatusModel) data;
                    comp.setForeground(Color.BLACK);

                    if (NumberTools.equal(status.getStatus(), CBVorgang.STATUS_ANSWERED)) {
                        if (BooleanTools.nullToFalse(status.getReturnOk())) {
                            comp.setBackground(BG_COLOR_FINISH_OK);
                        }
                        else {
                            comp.setBackground(BG_COLOR_FINISH_ERR);
                        }
                    }
                    else if (NumberTools.equal(status.getStatus(), CBVorgang.STATUS_CLOSED)) {
                        comp.setBackground(BG_COLOR_CLOSED);
                    }

                    if (status.hasAutomationErrors()) {
                        // Fehler bei der automatischen Abarbeitung von WITA Rueckmeldungen farbig markieren
                        comp.setBackground(BG_COLOR_AUTOMATION_ERROR);
                        comp.setForeground(Color.RED);
                    }
                }
            }
        }

        return comp;
    }

}
