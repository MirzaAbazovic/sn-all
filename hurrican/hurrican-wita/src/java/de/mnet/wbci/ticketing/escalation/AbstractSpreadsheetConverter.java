/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2014
 */
package de.mnet.wbci.ticketing.escalation;

import java.util.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Common class for all spreadsheet converters.
 *
 *
 */
public abstract class AbstractSpreadsheetConverter {
    /**
     * Create a library of cell styles
     */
    protected static Map<STYLE, CellStyle> createStyles(Workbook wb) {
        Map<STYLE, CellStyle> styles = new HashMap<>();
        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(titleFont);
        styles.put(STYLE.TITLE, style);

        Font monthFont = wb.createFont();
        monthFont.setFontHeightInPoints((short) 11);
        monthFont.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put(STYLE.HEADER, style);

        style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put(STYLE.ORANGE, style);

        style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put(STYLE.RED, style);

        style = wb.createCellStyle();
        style.setWrapText(true);
        styles.put(STYLE.MULTILINE, style);

        style = wb.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put(STYLE.LINE, style);

        return styles;
    }

    protected static enum STYLE {
        TITLE, HEADER, RED, MULTILINE, LINE, ORANGE
    }
}
