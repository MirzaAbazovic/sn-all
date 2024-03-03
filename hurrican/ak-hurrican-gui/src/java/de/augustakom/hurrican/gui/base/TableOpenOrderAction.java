/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2007 15:18:24
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;
import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Action-Klasse, um aus einer Tabelle heraus ein Auftrags-Frame zu oeffnen. <br> Hierfuer ist es notwendig, dass die
 * Action-Source vom Mouse-Event eine Tabelle und das selektierte Objekt vom Typ <code>CCAuftragModel</code> ist.
 *
 *
 */
public class TableOpenOrderAction extends AKAbstractAction {

    private static final long serialVersionUID = 1229463215676704118L;

    public TableOpenOrderAction() {
        super();
        setName("Auftrag öffnen");
        setActionCommand("open.auftrag");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object actionSrc = getValue(ACTION_SOURCE);
        if ((actionSrc instanceof MouseEvent) && (((MouseEvent) actionSrc).getSource() instanceof AKJTable)) {
            MouseEvent me = (MouseEvent) actionSrc;
            AKJTable table = (AKJTable) me.getSource();
            Point point = new Point(me.getX(), me.getY());
            int row = table.rowAtPoint(point);
            int column = table.columnAtPoint(point);
            table.changeSelection(row, column, false, false);

            Object value = null;
            if (table.getModel() instanceof AKMutableTableModel) {
                value = ((AKMutableTableModel<?>) table.getModel()).getDataAtRow(row);
            }
            if ((value instanceof CCAuftragModel) && (((CCAuftragModel) value).getAuftragId() != null)) {
                AuftragDataFrame.openFrame((CCAuftragModel) value);
            }
        }
    }
}
