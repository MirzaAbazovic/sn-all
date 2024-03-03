/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2010 16:47:56
 */
package de.mnet.migration.common.util.excel;

import java.util.*;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;


/**
 * Nimmt die Spaltennamen so wie sie sind
 *
 *
 */
public class ColumnNameExtractor {
    private static final Logger LOGGER = Logger.getLogger(ColumnNameExtractor.class);

    public Map<String, Integer> getColumns(HSSFSheet sheet, Integer captionRow) {
        Map<String, Integer> columns = new HashMap<String, Integer>();
        HSSFRow row = sheet.getRow(captionRow);
        int minCol = row.getFirstCellNum();
        int maxCol = row.getLastCellNum();
        for (int col = minCol; col < maxCol; ++col) {
            HSSFCell cell = row.getCell(col);
            if (cell != null) {
                try {
                    String content = CellMapper.getStringFromCell(cell);
                    if (content == null) {
                        content = "";
                    }
                    String column = columnNameExtension(content, cell, sheet).toLowerCase();
                    if (columns.containsKey(column)) {
                        LOGGER.warn("getColumns() - Column name '" + column + "' used twice");
                    }
                    columns.put(column, Integer.valueOf(col));
                }
                catch (Exception e) { // NOPMD can be ignored since we only want to act if no exception is thrown
                    LOGGER.warn("getColumns() - Exception getting cell contents (row: " + captionRow + ", cell: " + col + ")", e);
                }
            }
        }
        return columns;
    }

    /**
     * Kann ueberschrieben werden
     */
    // parameters used in overridden functions in subclasses
    public String columnNameExtension(String key, HSSFCell cell, HSSFSheet sheet) {
        return key;
    }
}
