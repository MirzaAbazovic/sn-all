/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2012 10:50:12
 */
package de.augustakom.hurrican.gui.auftrag.mvs;

import de.augustakom.common.gui.swing.table.AKTableModel;

class AuftragMVSDomainTableModel extends AKTableModel<String> {

    private static final long serialVersionUID = -5997053294703981101L;
    private final String columnTitle;

    AuftragMVSDomainTableModel(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    /**
     * @see javax.swing.table.DefaultTableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return 1;
    }

    /**
     * @see javax.swing.table.DefaultTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        return columnTitle;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        return getDataAtRow(row);
    }

}
