/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2005 10:39:06
 */
package de.augustakom.common.gui.swing.table;

import java.util.*;


/**
 * TableModel zur Darstellung von Maps.
 *
 *
 */
public class AKMapTableModel extends AKTableModel<Map<String, Object>> {

    private static final long serialVersionUID = 8242038369103872361L;
    private String[] colNamesArray = null;
    private Class<?>[] colTypes = null;

    /**
     * Uebergabe einer Liste mit Maps, deren Inhalte in der Tabelle dargestellt werden sollen.
     */
    @Override
    public void setData(Collection<Map<String, Object>> data) {
        super.setData(data);

        if ((data != null) && (!data.isEmpty())) {
            List<String> colNames = new ArrayList<>();
            Map<String, Object> map = getDataAtRow(0);
            Iterator<String> keyIt = map.keySet().iterator();
            while (keyIt.hasNext()) {
                colNames.add(keyIt.next());
                colNamesArray = colNames.toArray(new String[colNames.size()]);
            }
        }

        colTypes = new Class[getColumnCount()];
    }

    @Override
    public int getColumnCount() {
        return (colNamesArray != null) ? colNamesArray.length : 0;
    }

    @Override
    public String getColumnName(int column) {
        if (colNamesArray != null) {
            return colNamesArray[column];
        }
        return null;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Map<String, Object> map = getDataAtRow(row);
        if (map != null) {
            return map.get(colNamesArray[column]);
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if ((colTypes != null) && (colTypes.length > columnIndex)) {
            if (colTypes[columnIndex] != null) {
                return colTypes[columnIndex];
            }
            else {
                for (int i = 0; i < getRowCount(); i++) {
                    Object tmp = getValueAt(i, columnIndex);
                    if (tmp != null) {
                        colTypes[columnIndex] = tmp.getClass();
                        return tmp.getClass();
                    }
                }
            }
        }

        return String.class;
    }
}
