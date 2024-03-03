/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.05.2005 08:54:08
 */
package de.augustakom.common.tools.poi;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.NestableException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellValue;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;

/**
 * Various utility functions that make working with a cells and rows easier. The various methods that deal with style's
 * allow you to create your HSSFCellStyles as you need them. When you apply a style change to a cell, the code will
 * attempt to see if a style already exists that meets your needs. If not, then it will create a new style. This is to
 * prevent creating too many styles. there is an upper limit in Excel on the number of styles that can be supported.
 *
 * @author Eric Pugh epugh@upstate.com
 */
public class HSSFCellTools {

    private static final Logger LOGGER = Logger.getLogger(HSSFCellTools.class);


    /**
     * Get a row from the spreadsheet, and create it if it doesn't exist.
     *
     * @param rowCounter The 0 based row number
     * @param sheet      The sheet that the row is part of.
     * @return The row indicated by the rowCounter
     */
    public static HSSFRow getRow(int rowCounter, HSSFSheet sheet) {
        HSSFRow row = sheet.getRow((short) rowCounter);
        if (row == null) {
            row = sheet.createRow((short) rowCounter);
        }
        return row;
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
     * Liest aus dem Objekt <b><code>source</code></b> das Property <b><code>property</code></b> aus und erzeugt aus dem
     * Wert (falls nicht null) eine neue Zelle in der Zeile <code>row</code>.
     *
     * @param row
     * @param column
     * @param source
     * @param property
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static HSSFCell createCell(HSSFRow row, int column, Object source, String property, boolean silent)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = null;
        try {
            if (source != null) {
                value = PropertyUtils.getProperty(source, property);
            }
        }
        catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            if (!silent) {
                throw e;
            }
        }
        catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
            if (!silent) {
                throw e;
            }
        }
        catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
            if (!silent) {
                throw e;
            }
        }
        catch (NoSuchMethodException e) {
            LOGGER.error(e.getMessage(), e);
            if (!silent) {
                throw e;
            }
        }

        String cellValue = (value != null) ? "" + value : "";
        return createCell(row, column, cellValue);
    }

    /**
     * Creates a cell, gives it a value, and applies a style if provided
     *
     * @param row    the row to create the cell in
     * @param column the column index to create the cell in
     * @param value  The value of the cell
     * @param style  If the style is not null, then set
     * @return A new HSSFCell
     */

    public static HSSFCell createCell(HSSFRow row, int column, String value, HSSFCellStyle style) {
        HSSFCell cell = getCell(row, column);

        cell.setCellValue(new HSSFRichTextString(value));
        if (style != null) {
            cell.setCellStyle(style);
        }

        return cell;
    }

    /**
     * Create a cell, and give it a value.
     *
     * @param row    the row to create the cell in
     * @param column the column index to create the cell in
     * @param value  The value of the cell
     * @return A new HSSFCell.
     */
    public static HSSFCell createCell(HSSFRow row, int column, String value) {
        return createCell(row, column, value, null);
    }

    /**
     * Take a cell, and align it.
     *
     * @param cell     the cell to set the alignment for
     * @param workbook The workbook that is being worked with.
     * @param align    the column alignment to use.
     * @throws NestableException Thrown if an error happens.
     * @see HSSFCellStyle for alignment options
     */
    public static void setAlignment(HSSFCell cell, HSSFWorkbook workbook, short align)
            throws NestableException {
        setCellStyleProperty(cell, workbook, "alignment", Short.valueOf(align));
    }

    /**
     * Take a cell, and apply a font to it
     *
     * @param cell     the cell to set the alignment for
     * @param workbook The workbook that is being worked with.
     * @param font     The HSSFFont that you want to set...
     * @throws NestableException Thrown if an error happens.
     */
    public static void setFont(HSSFCell cell, HSSFWorkbook workbook, HSSFFont font)
            throws NestableException {
        setCellStyleProperty(cell, workbook, "font", font);
    }

    /**
     * This method attempt to find an already existing HSSFCellStyle that matches what you want the style to be. If it
     * does not find the style, then it creates a new one. If it does create a new one, then it applyies the
     * propertyName and propertyValue to the style. This is nessasary because Excel has an upper limit on the number of
     * Styles that it supports.
     *
     * @param workbook      The workbook that is being worked with.
     * @param propertyName  The name of the property that is to be changed.
     * @param propertyValue The value of the property that is to be changed.
     * @param cell          The cell that needs it's style changes
     * @throws NestableException Thrown if an error happens.
     */
    public static void setCellStyleProperty(HSSFCell cell, HSSFWorkbook workbook,
            String propertyName, Object propertyValue) throws NestableException {
        try {
            HSSFCellStyle originalStyle = cell.getCellStyle();
            HSSFCellStyle newStyle = null;
            Map<String, Object> values = PropertyUtils.describe(originalStyle);
            values.put(propertyName, propertyValue);
            values.remove("index");

            // index seems like what index the cellstyle is in the list of
            // styles for a workbook.
            // not good to compare on!
            short numberCellStyles = workbook.getNumCellStyles();

            for (short i = 0; i < numberCellStyles; i++) {
                HSSFCellStyle wbStyle = workbook.getCellStyleAt(i);
                Map<String, Object> wbStyleMap = PropertyUtils.describe(wbStyle);
                wbStyleMap.remove("index");

                if (wbStyleMap.equals(values)) {
                    newStyle = wbStyle;
                    break;
                }
            }

            if (newStyle == null) {
                newStyle = workbook.createCellStyle();
                newStyle.setFont(workbook.getFontAt(originalStyle.getFontIndex()));
                PropertyUtils.copyProperties(newStyle, originalStyle);
                PropertyUtils.setProperty(newStyle, propertyName, propertyValue);
            }

            cell.setCellStyle(newStyle);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new NestableException("Couldn't setCellStyleProperty.", e);
        }
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
     * Gibt einen numerischen Wert als String zurueck.
     *
     * @param cellValue
     * @param pattern   Pattern fuer die Formatierung.
     * @return
     */
    public static String getNumericCellValueAsString(CellValue cellValue, String pattern) {
        if ((cellValue != null) && (cellValue.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)) {
            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
            df.applyPattern(pattern);
            return df.format(cellValue.getNumberValue());
        }
        return null;
    }

    /**
     * Funktion liefert einen Integer-Wert aus einer Excel-Zelle
     *
     * @return Integer-Wert der Zelle (bei NUMERIC- oder STRING-Zellen), falls ein gueltiger Integer-Wert existiert,
     * sonst {@code null}
     */
    public static Integer getIntegerFromCell(HSSFRow row, Integer cell) {
        Long value = getLongFromCell(row, cell);
        return (value == null ? null : Integer.valueOf(value.intValue()));
    }

    /**
     * @see #getIntegerFromCell(HSSFRow, Integer)
     */
    public static Integer getIntegerFromCell(HSSFCell cell) {
        Long value = getLongFromCell(cell);
        return (value == null ? null : Integer.valueOf(value.intValue()));
    }

    /**
     * @see #getIntegerFromCell(HSSFRow, Integer)
     */
    public static Integer getIntegerFromCellValue(CellValue cellValue) {
        Long value = getLongFromCellValue(cellValue);
        return (value == null ? null : Integer.valueOf(value.intValue()));
    }

    /**
     * Funktion liefert einen Long-Wert aus einer Excel-Zelle
     *
     * @return Long-Wert der Zelle (bei NUMERIC- oder STRING-Zellen), falls ein gueltiger Long-Wert existiert, sonst
     * {@code null}
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
                return Long.valueOf(cellValue.longValue());
            }
        }
        catch (IllegalStateException e) { // NOPMD trying with string, nothing to do in catch block
            LOGGER.warn("getLongFromCell() - Trying to get numeric value from formula failed", e);
        }
        try {
            if ((cell.getCellType() == HSSFCell.CELL_TYPE_STRING) || (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)) {
                String str = StringUtils.trimToNull(cell.getRichStringCellValue().getString());
                return NumberTools.convertString2Long(str, null);
            }
        }
        catch (IllegalStateException e) {
            LOGGER.warn("getLongFromCell() - Trying to get string value from formula failed", e);
        }
        return null;
    }

    /**
     * @see #getLongFromCell(HSSFRow, Integer)
     */
    public static Long getLongFromCellValue(CellValue cellValue) {
        if (cellValue == null) {
            return null;
        }
        if (cellValue.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            Double value = cellValue.getNumberValue();
            return Long.valueOf(value.longValue());
        }
        else if (cellValue.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            String str = StringUtils.trimToNull(cellValue.getStringValue());
            Long longValue = null;
            try {
                longValue = Long.valueOf(str);
            }
            catch (NumberFormatException e) {
                LOGGER.debug("getLongFromCellValue() - ignore NFE, return null");
            }
            return longValue;
        }
        return null;
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
     * @see #getStringFromCell(HSSFRow, Integer)
     */
    public static String getStringFromCellValue(CellValue cellValue) {
        if (cellValue == null) {
            return null;
        }
        if (cellValue.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            return StringUtils.trimToNull(cellValue.getStringValue());

        }
        else if (cellValue.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            return StringUtils.trimToNull(getNumericCellValueAsString(cellValue, "#"));
        }
        return null;
    }

    /**
     * Funktion liefert einen Datums-Wert aus einer Excel-Zelle
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
            Date date;
            try {
                date = new SimpleDateFormat(DateTools.PATTERN_DAY_MONTH_YEAR).parse(dateString);
                return date;
            }
            catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }
}
