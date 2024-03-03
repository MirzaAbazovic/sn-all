/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2005 12:04:21
 */
package de.augustakom.common.tools.poi;

import java.util.*;
import javax.swing.table.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 * Sheet Viewer Table Model - The model for the Sheet Viewer just overrides things.
 *
 * @author Andrew C. Oliver
 */
public class HSSFSheetTableModel extends AbstractTableModel {

    private HSSFSheet st = null;
    int maxcol = 0;

    /**
     * @param st
     * @param maxcol
     */
    public HSSFSheetTableModel(HSSFSheet st, int maxcol) {
        this.st = st;
        this.maxcol = maxcol;
    }

    /**
     * @param st
     */
    public HSSFSheetTableModel(HSSFSheet st) {
        this.st = st;
        Iterator i = st.rowIterator();

        while (i.hasNext()) {
            HSSFRow row = (HSSFRow) i.next();
            if (maxcol < (row.getLastCellNum() + 1)) {
                this.maxcol = row.getLastCellNum();
            }
        }
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return this.maxcol + 1;
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        HSSFRow r = st.getRow(row);
        HSSFCell c = null;
        if (r != null) {
            c = r.getCell(col);
            if (c != null) {
                int cellType = c.getCellType();
                switch (cellType) {
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        return c.getNumericCellValue();
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                        return c.getBooleanCellValue();
                    case HSSFCell.CELL_TYPE_FORMULA:
                        return c.getCellFormula();
                    default:
                        return c.getRichStringCellValue().getString();
                }
            }
        }
        return c;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return st.getLastRowNum() + 1;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class getColumnClass(int c) {
        return HSSFCell.class;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

}
