/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2004 08:11:40
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>VPNVertragView</code>.
 *
 *
 */
public class VPNVertragViewTableModel extends AKTableModelXML<VPNVertragView> {

    private final boolean allowEdit;

    public VPNVertragViewTableModel(boolean allowEdit) {
        super("de/augustakom/hurrican/gui/auftrag/shared/resources/VPNVertragViewTableModel.xml");
        this.allowEdit = allowEdit;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (allowEdit) {
            return super.isCellEditable(row, column);
        }
        return false;
    }
}


