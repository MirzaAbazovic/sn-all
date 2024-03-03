/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2010 16:48:39
 */
package de.mnet.migration.common.util.excel;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;


/**
 * Sucht nach links oben nach der naechsten nicht-leeren Zelle und nimmt deren Wert konkateniert mit " >>> " als Praefix
 * fuer den Spaltennamen. Sinnvoll bei mehrfach vorhandenen Spaltennamen, wo aber z.B. eine Zeile darueber noch eine
 * Praezisierung vorhanden ist.
 *
 *
 */
public class ColumnNameExtractorWithCaption extends ColumnNameExtractor {
    @Override
    public String columnNameExtension(String key, HSSFCell cell, HSSFSheet sheet) {
        int rowNum = cell.getRowIndex() - 1;
        while (rowNum >= sheet.getFirstRowNum()) {
            HSSFRow row = sheet.getRow(rowNum);
            int colNum = cell.getColumnIndex();
            while (colNum >= row.getFirstCellNum()) {
                String add = CellMapper.getStringFromCell(row, colNum);
                if (!StringUtils.isEmpty(add)) {
                    return add + " >>> " + key;
                }
                colNum--;
            }
            rowNum--;
        }
        return key;
    }
}
