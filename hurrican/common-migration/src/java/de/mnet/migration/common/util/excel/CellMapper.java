/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2010 16:47:21
 */
package de.mnet.migration.common.util.excel;

import java.lang.reflect.*;
import java.math.*;
import java.text.*;
import java.time.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import de.mnet.migration.common.util.Pair;


/**
 *
 */
public class CellMapper {
    private static final Logger LOGGER = Logger.getLogger(CellMapper.class);

    public Pair<Object, Boolean> map(HSSFRow currentRow, Field field, Integer column) {
        boolean nonNull = false;
        Object value = null;

        if (field.getType().equals(BigDecimal.class)) {
            String stringValue = getStringFromCell(currentRow, column);
            if (StringUtils.isNotBlank(stringValue)) {
                stringValue = stringValue.replaceAll(",", ".");
                value = new BigDecimal(stringValue);
                nonNull = true;
            }
        }
        else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
            value = getIntegerFromCell(currentRow, column);
            nonNull = value != null;
        }
        else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
            value = getLongFromCell(currentRow, column);
            nonNull = value != null;
        }
        else if (field.getType().equals(Date.class)) {
            value = getDateFromCell(currentRow, column);
            nonNull = value != null;
        }
        else if (field.getType().equals(LocalDateTime.class)) {
            Date date = getDateFromCell(currentRow, column);
            if (date != null) {
                value = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                nonNull = true;
            }
        }
        else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
            HSSFCell cell = currentRow.getCell(column);
            try {
                if (cell != null) {
                    value = cell.getBooleanCellValue();
                    nonNull = value != null;
                }
                else if (field.getType().equals(boolean.class)) {
                    value = Boolean.FALSE;
                }
            }
            catch (IllegalStateException e) {
                String cellVal = getStringFromCell(currentRow, column);
                value = ("true".equalsIgnoreCase(cellVal) || "1".equals(cellVal) || "x".equalsIgnoreCase(cellVal)
                        || "Ja".equalsIgnoreCase(cellVal)) ? Boolean.TRUE : Boolean.FALSE;
            }
        }
        else if (field.getType().equals(String.class)) {
            value = getStringFromCell(currentRow, column);
            nonNull = value != null;
        }

        return Pair.create(value, Boolean.valueOf(nonNull));
    }


    /**
     * Funktion liefert einen String-Wert aus einer Excel-Zelle
     *
     * @return String-Wert der Zelle (bei NUMERIC- oder STRING-Zellen), falls ein gueltiger Wert existiert, sonst {@code
     * null}. Der Leere String wird zu {@code null}.
     *
     */
    public static String getStringFromCell(HSSFRow row, Integer cell) {
        if ((row == null) || (cell == null)) {
            return null;
        }
        HSSFCell currentCell = getCell(row, cell);
        return getStringFromCell(currentCell);
    }


    /**
     * @see #getStringFromCell(HSSFRow, Integer)
     */
    public static String getStringFromCell(HSSFCell cell) {
        if (cell == null) {
            return null;
        }
        try {
            if ((cell.getCellType() == HSSFCell.CELL_TYPE_STRING) || (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)) {
                return StringUtils.trimToNull(cell.getRichStringCellValue().getString());
            }
        }
        catch (IllegalStateException e) { // NOPMD trying with string, nothing to do in catch block
            LOGGER.warn("getStringFromCell() - Trying to get string value from formula failed", e);
        }
        try {
            if ((cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) || (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)) {
                return StringUtils.trimToNull(getNumericCellValueAsString(cell, "#"));
            }
        }
        catch (IllegalStateException e) { // NOPMD trying with string, nothing to do in catch block
            LOGGER.warn("getStringFromCell() - Trying to get string value from formula failed", e);
        }
        return null;
    }

    /**
     * Get a specific cell from a row. If the cell doesn't exist, then create it.
     *
     * @param row    The row that the cell is part of
     * @param column The column index that the cell is in.
     * @return The cell indicated by the column.
     */
    public static HSSFCell getCell(HSSFRow row, int column) {
        HSSFCell cell = row.getCell(column);

        if (cell == null) {
            cell = row.createCell(column);
        }
        return cell;
    }


    /**
     * Gibt einen numerischen Wert als String zurueck.
     *
     * @param cell
     * @param pattern Pattern fuer die Formatierung.
     * @return
     */
    public static String getNumericCellValueAsString(HSSFCell cell, String pattern) {
        if ((cell != null) && (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)) {
            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
            df.applyPattern(pattern);
            return df.format(cell.getNumericCellValue());
        }
        return null;
    }


    /**
     * Funktion liefert einen Datums-Wert aus einer Excel-Zelle
     *
     *
     */
    public static Date getDateFromCell(HSSFRow row, Integer cell) {
        if ((row == null) || (cell == null)) {
            return null;
        }
        HSSFCell currentCell = getCell(row, cell);
        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            return currentCell.getDateCellValue();
        }
        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            String dateString = currentCell.getRichStringCellValue().getString();
            Date date = null;
            try {
                date = new SimpleDateFormat("dd.MM.yyyy").parse(dateString);
                return date;
            }
            catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }


    /**
     * Funktion liefert einen Integer-Wert aus einer Excel-Zelle
     *
     * @return Integer-Wert der Zelle (bei NUMERIC- oder STRING-Zellen), falls ein gueltiger Integer-Wert existiert,
     * sonst {@code null}
     *
     */
    public static Integer getIntegerFromCell(HSSFRow row, Integer cell) {
        Long value = getLongFromCell(row, cell);
        return (value == null ? null : Integer.valueOf(value.intValue()));
    }


    /**
     * Funktion liefert einen Long-Wert aus einer Excel-Zelle
     *
     * @return Long-Wert der Zelle (bei NUMERIC- oder STRING-Zellen), falls ein gueltiger Long-Wert existiert, sonst
     * {@code null}
     *
     */
    public static Long getLongFromCell(HSSFRow row, Integer cell) {
        if ((row == null) || (cell == null)) {
            return null;
        }
        HSSFCell currentCell = getCell(row, cell);
        return getLongFromCell(currentCell);
    }

    /**
     * @see #getLongFromCell(HSSFRow, Integer)
     */
    public static Long getLongFromCell(HSSFCell cell) {
        if (cell == null) {
            return null;
        }
        try {
            if ((cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) || (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)) {
                Double cellValue = cell.getNumericCellValue();
                return (cellValue != null) ? Long.valueOf(cellValue.longValue()) : null;
            }
        }
        catch (IllegalStateException e) { // NOPMD trying with string, nothing to do in catch block
            LOGGER.warn("getLongFromCell() - Trying to get numeric value from formula failed", e);
        }
        try {
            if ((cell.getCellType() == HSSFCell.CELL_TYPE_STRING) || (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)) {
                String str = StringUtils.trimToNull(cell.getRichStringCellValue().getString());
                Long longValue = null;
                try {
                    longValue = Long.valueOf(str);
                }
                catch (NumberFormatException e) {
                    LOGGER.debug("getLongFromCell() - ignore NFE, return null");
                }
                return longValue;
            }
        }
        catch (IllegalStateException e) {
            LOGGER.warn("getLongFromCell() - Trying to get string value from formula failed", e);
        }
        return null;
    }
}
