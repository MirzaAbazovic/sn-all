/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2012 15:09:30
 */
package de.augustakom.common.tools.poi;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Wrapper Methoden rund ums Apache POI SS Modell. Dieses wiederum bietet transparenten Zugriff sowohl auf XLS Dateien
 * (über HSSF) bzw. XLSX Dateien (über OOXML).
 *
 *
 */
public class XlsPoiTool {
    /**
     * Liest das erste Sheet einer XLS oder XLSX Datei.
     *
     * @param xlsData
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static Sheet loadExcelSheet(byte[] xlsData) throws InvalidFormatException, IOException {
        return loadExcelFile(xlsData, 0);
    }

    /**
     * Liest ein Sheet einer XLS oder XLSX Datei.
     *
     * @param xlsData
     * @param sheetNumber die nummer des Sheets (erste Sheet = 0)
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static Sheet loadExcelFile(byte[] xlsData, int sheetNumber) throws InvalidFormatException, IOException {
        Workbook workbook = loadExcelFile(xlsData);
        return workbook.getSheetAt(sheetNumber);
    }

    /**
     * Liest eine XLS oder XLSX Datei .
     *
     * @param xlsData
     * @return Workbook, das alle Sheets enthält
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static Workbook loadExcelFile(byte[] xlsData) throws IOException, InvalidFormatException {
        ByteArrayInputStream xlsStream = new ByteArrayInputStream(xlsData);
        return WorkbookFactory.create(xlsStream);
    }

    /**
     * Liest das erste Sheet einer XLS oder XLSX Datei.
     *
     * @param stream
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static Sheet loadExcelFile(InputStream stream) throws InvalidFormatException, IOException {
        return loadExcelFile(stream, 0);
    }

    /**
     * Liest ein Sheet einer XLS oder XLSX Datei.
     *
     * @param stream
     * @param sheetNumber die nummer des Sheets (erste Sheet = 0)
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    private static Sheet loadExcelFile(InputStream stream, int sheetNumber) throws InvalidFormatException, IOException {
        Workbook workbook = WorkbookFactory.create(stream);
        return workbook.getSheetAt(sheetNumber);
    }

    /**
     * Gibt den Wert einer Zelle als String zurück. Eine numerisch formatierte Zelle wird als Long gelesen, für sonstige
     * Formatierungen wird (die überschriebene) <code>toString</code> Methode aufgerufen.<br> Hat die Zelle keinen Wert
     * oder ist diese nicht vorhanden, wird (wenn vorhanden) der default Wert zurückgegeben.
     * <p/>
     * <b>Achtung:</b> Als Datum formattierte Zellen werden von POI als NUMERIC Typ interpretiert ... d.h. es wird der
     * Integer Wert ausgelesen. Bitte {@link #getContentAsDate(Row, int, Date...)} nutzen.
     *
     * @param row          Excel-Zeile
     * @param column       Spalte, die ausgelesen werden soll
     * @param defaultValue Default-Wert, der zurueck gegeben werden soll, falls keine entsprechende Spalte vorhanden
     *                     ist
     * @return
     */
    public static String getContentAsString(Row row, int column, String... defaultValue) {
        Cell cell = row != null ? row.getCell(column) : null;
        if (cell != null) {
            String content;
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    content = Long.toString((long) cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                case Cell.CELL_TYPE_BLANK:
                    content = cell.getStringCellValue();
                    break;
                default:
                    content = cell.toString();
            }
            if (StringUtils.isNotEmpty(content)) {
                return content;
            }
        }
        return (defaultValue != null) && (defaultValue.length > 0) ? defaultValue[0] : null;
    }

    /**
     * Gibt den Wert einer Zelle (XLS Format: numerisch oder Text) als Integer zurück.<br> Hat die Zelle keinen Wert
     * wird (wenn vorhanden) der default Wert zurückgegeben.
     *
     * @param row
     * @param column
     * @param defaultValue
     * @return
     */
    public static Integer getContentAsInt(Row row, int column, Integer... defaultValue) {
        Cell cell = row != null ? row.getCell(column) : null;
        if (cell != null) {
            Integer value = null;
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = Integer.valueOf(cell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                default:
                    value = Double.valueOf(cell.getNumericCellValue()).intValue();
                    break;
            }
            if (value != null) {
                return value;
            }
        }
        return (defaultValue != null) && (defaultValue.length > 0) ? defaultValue[0] : null;
    }

    /**
     * Gibt den Wert einer Zelle (XLS Format: numerisch oder Text) als Long zurück.<br> Hat die Zelle keinen Wert wird
     * (wenn vorhanden) der default Wert zurückgegeben.
     *
     * @param row
     * @param column
     * @param defaultValue
     * @return
     */
    public static Long getContentAsLong(Row row, int column, Long... defaultValue) {
        Cell cell = row != null ? row.getCell(column) : null;
        if (cell != null) {
            Long value = null;
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = Long.valueOf(cell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                default:
                    value = Double.valueOf(cell.getNumericCellValue()).longValue();
                    break;
            }
            if (value != null) {
                return value;
            }
        }
        return (defaultValue != null) && (defaultValue.length > 0) ? defaultValue[0] : null;
    }

    /**
     * Liest eine Zelle als Datumswert aus. Erwartet das die Zelle als XLS Datum formatiert ist.<br> Hat die Zelle
     * keinen Wert wird (wenn vorhanden) der default Wert zurückgegeben.
     *
     * @param row
     * @param column
     * @param defaultValue
     * @return
     */
    public static Date getContentAsDate(Row row, int column, Date... defaultValue) {
        Cell cell = row != null ? row.getCell(column) : null;
        if (cell != null) {
            Date date = cell.getDateCellValue();
            if (date != null) {
                return date;
            }
        }
        return (defaultValue != null) && (defaultValue.length > 0) ? defaultValue[0] : null;
    }

    public static void setContent(Row row, int column, String content) {
        setContent(row, column, content, false);
    }

    public static void setContent(Row row, int column, String content, boolean cellExists) {
        if (row == null) {
            throw new IllegalArgumentException("Das Row Objekt ist nicht gesetzt!");
        }
        Cell cell = cellExists ? row.getCell(column) : row.createCell(column);
        if (cell == null) {
            throw new IllegalArgumentException(String.format("Das Cell Objekt für die Spalte '%s' konnte nicht "
                    + "erzeugt oder ermittelt werden!", column));
        }
        cell.setCellValue(content);
    }

    public static void setContent(Row row, int column, Date content) {
        if (row == null) {
            throw new IllegalArgumentException("Das Row Objekt ist nicht gesetzt!");
        }
        if (content != null) {
            Cell cell = row.createCell(column);
            cell.setCellValue(content);
        }
    }

    public static void setContent(Row row, int column, Long content) {
        if (row == null) {
            throw new IllegalArgumentException("Das Row Objekt ist nicht gesetzt!");
        }
        Cell cell = row.createCell(column);
        cell.setCellValue(content);
    }

    /**
     * Prueft ob die Zelle {@code column} leer ist. Leer bedeutet {@code null} oder <code>Cell.CELL_TYPE_BLANK</code>.
     */
    public static boolean isEmpty(Row row, int column) {
        Cell cell = row != null ? row.getCell(column) : null;
        return (cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK);
    }

    /**
     * Prueft ob die Zelle {@code column} NICHT leer ist. Leer bedeutet {@code null} oder {@code ""}.
     */
    public static boolean isNotEmpty(Row row, int column) {
        return !isEmpty(row, column);
    }

    /**
     * Schreibt die Daten des {@code workbook}s in einen Byte Array Stream
     */
    public static byte[] writeWorkbook(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Loggt die Spalten einer Zeile. Welche spalten ausgegeben werden, bestimmt das {@code indexes} array.
     */
    public static void logRowAsInfo(Row row, Logger logger, String[] columnNames, int[] columns) {
        if ((row == null) || (logger == null) || (columns == null) || (columns.length <= 0)) {
            return;
        }
        StringBuilder logBuffer = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if ((columnNames != null) && (columnNames.length > i)) {
                logBuffer.append(String.format("(%s) ", columnNames[i]));
            }
            logBuffer.append(getContentAsString(row, columns[i], ""));
            logBuffer.append(", ");
        }
        logger.info(logBuffer.toString());
    }

    public static void copyRowWithoutContent(Workbook workbook, Sheet worksheet, int sourceRowNum, int destinationRowNum) {
        // Get the source / new row
        Row newRow = worksheet.getRow(destinationRowNum);
        Row sourceRow = worksheet.getRow(sourceRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
        } else {
            newRow = worksheet.createRow(destinationRowNum);
        }

        newRow.setHeight(sourceRow.getHeight());
        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                continue;
            }

            // Copy style from old cell and apply to new cell
            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());
        }

        // If there are are any merged regions in the source row, copy to new row
        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
                        (newRow.getRowNum() +
                                (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()
                                )),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                worksheet.addMergedRegion(newCellRangeAddress);
            }
        }
    }

}
