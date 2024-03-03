/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2010 16:47:21
 */
package de.mnet.migration.common.util.excel;

import java.lang.reflect.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;

import de.mnet.migration.common.util.Pair;


/**
 * Mappt die Farbe der Excel-Spalte auf ein Short-Feld, oder ob eine Farbe (!= 64, ist wohl Standard) genutzt wurde auf
 * ein Boolean-Feld
 *
 *
 */
public class ColorMapper extends CellMapper {
    @Override
    public Pair<Object, Boolean> map(HSSFRow currentRow, Field field, Integer column) {
        Short color = null;
        HSSFCell cell = currentRow.getCell(column);
        if (cell != null) {
            HSSFCellStyle cellStyle = cell.getCellStyle();
            if (cellStyle != null) {
                color = cellStyle.getFillForegroundColor();
            }
        }
        Object value = null;
        if (field.getType().equals(Integer.class) || field.getType().equals(int.class) ||
                field.getType().equals(Long.class) || field.getType().equals(long.class) ||
                field.getType().equals(Short.class) || field.getType().equals(short.class)) {
            value = color;
        }
        else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
            if ((color != null) && !Short.valueOf((short) 64).equals(color)) {
                value = Boolean.TRUE;
            }
            else {
                value = Boolean.FALSE;
            }
        }
        return Pair.create(value, Boolean.FALSE);
    }
}
