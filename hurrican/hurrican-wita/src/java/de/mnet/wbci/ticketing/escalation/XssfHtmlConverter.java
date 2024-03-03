/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.04.2014
 */
package de.mnet.wbci.ticketing.escalation;
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

import static org.apache.poi.ss.usermodel.CellStyle.*;

import java.io.*;
import java.util.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This example shows how to display a spreadsheet in HTML using the classes for spreadsheet display.
 *
 * @author Ken Arnold, Industrious Media LLC
 */
public final class XssfHtmlConverter {
    private static final String DEFAULTS_CLASS = "excelDefaults";
    private static final String COL_HEAD_CLASS = "colHeader";
    private static final String ROW_HEAD_CLASS = "rowHeader";
    private static final Map<Short, String> ALIGN = mapFor(ALIGN_LEFT, "left",
            ALIGN_CENTER, "center", ALIGN_RIGHT, "right", ALIGN_FILL, "left",
            ALIGN_JUSTIFY, "left", ALIGN_CENTER_SELECTION, "center");
    private static final Map<Short, String> VERTICAL_ALIGN = mapFor(
            VERTICAL_BOTTOM, "bottom", VERTICAL_CENTER, "middle", VERTICAL_TOP,
            "top");
    private static final Map<Short, String> BORDER = mapFor(BORDER_DASH_DOT,
            "dashed 1pt", BORDER_DASH_DOT_DOT, "dashed 1pt", BORDER_DASHED,
            "dashed 1pt", BORDER_DOTTED, "dotted 1pt", BORDER_DOUBLE,
            "double 3pt", BORDER_HAIR, "solid 1px", BORDER_MEDIUM, "solid 2pt",
            BORDER_MEDIUM_DASH_DOT, "dashed 2pt", BORDER_MEDIUM_DASH_DOT_DOT,
            "dashed 2pt", BORDER_MEDIUM_DASHED, "dashed 2pt", BORDER_NONE,
            "none", BORDER_SLANTED_DASH_DOT, "dashed 2pt", BORDER_THICK,
            "solid 3pt", BORDER_THIN, "dashed 1pt");
    private final XSSFWorkbook wb;
    private final Appendable output;
    private Formatter out;
    private boolean gotBounds;
    private int firstColumn;
    private int endColumn;
    private XssfHtmlHelper helper;
    private boolean completeHTML;
    private List<Integer> spanRows;

    private XssfHtmlConverter(XSSFWorkbook wb, Appendable output) {
        if (wb == null) {
            throw new NullPointerException("wb");
        }
        if (output == null) {
            throw new NullPointerException("output");
        }
        this.wb = wb;
        this.output = output;
        setupColorMap();
    }

    @SuppressWarnings({ "unchecked" })
    private static <K, V> Map<K, V> mapFor(Object... mapping) {
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < mapping.length; i += 2) {
            map.put((K) mapping[i], (V) mapping[i + 1]);
        }
        return map;
    }

    /**
     * Creates a new converter to HTML for the given workbook.
     *
     * @param wb     The workbook.
     * @param output Where the HTML output will be written.
     * @return An object for converting the workbook to HTML.
     */
    public static Appendable create(XSSFWorkbook wb, Appendable output) throws IOException {

        XssfHtmlConverter XssfHtmlConverter = new de.mnet.wbci.ticketing.escalation.XssfHtmlConverter(wb, output);
        XssfHtmlConverter.setCompleteHTML(true);
        XssfHtmlConverter.printPage();
        return XssfHtmlConverter.output;
    }

    public static String createEscalationHtmlReport(XSSFWorkbook wb) throws IOException {
        Appendable output = new StringWriter();
        XssfHtmlConverter XssfHtmlConverter = new XssfHtmlConverter(wb, output);
        XssfHtmlConverter.setCompleteHTML(false);
        XssfHtmlConverter.setSpanColumnsForRows(0);
        XssfHtmlConverter.printPage();
        return XssfHtmlConverter.output.toString();
    }

    private void setSpanColumnsForRows(Integer... rowNumbers) {
        spanRows = Arrays.asList(rowNumbers);
    }

    private static int ultimateCellType(Cell c) {
        int type = c.getCellType();
        if (type == Cell.CELL_TYPE_FORMULA) {
            type = c.getCachedFormulaResultType();
        }
        return type;
    }

    private void setupColorMap() {
        helper = new XssfHtmlHelper(wb);
    }

    public void setCompleteHTML(boolean completeHTML) {
        this.completeHTML = completeHTML;
    }

    public void printPage() throws IOException {
        try {
            ensureOut();
            if (completeHTML) {
                out.format(
                        "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>%n");
                out.format("<html>%n");
                out.format("<head>%n");
                out.format("</head>%n");
                out.format("<body>%n");
            }

            print();

            if (completeHTML) {
                out.format("</body>%n");
                out.format("</html>%n");
            }
        }
        finally {
            if (out != null) {
                out.close();
            }
            if (output instanceof Closeable) {
                Closeable closeable = (Closeable) output;
                closeable.close();
            }
        }
    }

    public void print() {
        printInlineStyle();
        printSheets();
    }

    private void printInlineStyle() {
        //out.format("<link href=\"excelStyle.css\" rel=\"stylesheet\" type=\"text/css\">%n");
        out.format("<style type=\"text/css\">%n");
        printStyles();
        out.format("</style>%n");
    }

    private void ensureOut() {
        if (out == null) {
            out = new Formatter(output);
        }
    }

    public void printStyles() {
        ensureOut();

        // First, copy the base css
        // Sonar Warning wg. throw in finally: Java 7 handelt Fileexception durch Definition im Try Block
        // Exceptions koennen mit Throwable.getSuppressed abgefangen werden
        try (InputStream file = getClass().getResourceAsStream("/de/mnet/wbci/ticketing/escalation/excelStyle.css");
                BufferedReader in = new BufferedReader(new InputStreamReader(file, "UTF-8"))){
            String line;
            while ((line = in.readLine()) != null) {
                out.format("%s%n", line);
            }
        }
        catch (IOException e) {
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable t : suppressed) {
                throw new IllegalStateException("Reading standard css", t.getCause());
            }
            throw new IllegalStateException("Reading standard css", e);
        }

        // now add css for each used style
        Set<CellStyle> seen = new HashSet<>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            Iterator<Row> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                for (Cell cell : row) {
                    CellStyle style = cell.getCellStyle();
                    if (!seen.contains(style)) {
                        printStyle(style);
                        seen.add(style);
                    }
                }
            }
        }
    }

    private void printStyle(CellStyle style) {
        out.format(".%s .%s {%n", DEFAULTS_CLASS, styleName(style));
        styleContents(style);
        out.format("}%n");
    }

    private void styleContents(CellStyle style) {
        styleOut("text-align", style.getAlignment(), ALIGN);
        styleOut("vertical-align", style.getAlignment(), VERTICAL_ALIGN);
        fontStyle(style);
        borderStyles(style);
        helper.colorStyles(style, out);
    }

    private void borderStyles(CellStyle style) {
        styleOut("border-left", style.getBorderLeft(), BORDER);
        styleOut("border-right", style.getBorderRight(), BORDER);
        styleOut("border-top", style.getBorderTop(), BORDER);
        styleOut("border-bottom", style.getBorderBottom(), BORDER);
    }

    private void fontStyle(CellStyle style) {
        Font font = wb.getFontAt(style.getFontIndex());

        if (font.getBoldweight() >= HSSFFont.BOLDWEIGHT_NORMAL) {
            out.format("  font-weight: bold;%n");
        }
        if (font.getItalic()) {
            out.format("  font-style: italic;%n");
        }

        int fontheight = (int) (font.getFontHeightInPoints() * 0.7);
        if (fontheight == 9) {
            //fix for stupid ol Windows
            fontheight = 10;
        }
        out.format("  font-size: %dpt;%n", fontheight);
        out.format("  width: auto;%n");

        // Font color is handled with the other colors
    }

    private String styleName(CellStyle style) {
        if (style == null) {
            style = wb.getCellStyleAt((short) 0);
        }
        StringBuilder sb = new StringBuilder();
        Formatter fmt = new Formatter(sb);
        fmt.format("style_%02x", style.getIndex());
        return fmt.toString();
    }

    private <K> void styleOut(String attr, K key, Map<K, String> mapping) {
        String value = mapping.get(key);
        if (value != null) {
            out.format("  %s: %s;%n", attr, value);
        }
    }

    private void printSheets() {
        ensureOut();
        Sheet sheet = wb.getSheetAt(0);
        printSheet(sheet);
    }

    public void printSheet(Sheet sheet) {
        ensureOut();
        out.format("<table class=%s>%n", DEFAULTS_CLASS);
        printCols(sheet);
        printSheetContent(sheet);
        out.format("</table>%n");
    }

    private void printCols(Sheet sheet) {
        ensureColumnBounds(sheet);
    }

    private void ensureColumnBounds(Sheet sheet) {
        if (gotBounds) {
            return;
        }

        Iterator<Row> iter = sheet.rowIterator();
        firstColumn = (iter.hasNext() ? Integer.MAX_VALUE : 0);
        endColumn = 0;
        while (iter.hasNext()) {
            Row row = iter.next();
            short firstCell = row.getFirstCellNum();
            if (firstCell >= 0) {
                firstColumn = Math.min(firstColumn, firstCell);
                endColumn = Math.max(endColumn, row.getLastCellNum());
            }
        }
        gotBounds = true;
    }

    private void printColumnHeads() {
        out.format("<thead>%n");
        out.format("  <tr class=%s>%n", COL_HEAD_CLASS);
        out.format("    <th class=%s>&#x25CA;</th>%n", COL_HEAD_CLASS);
        //noinspection UnusedDeclaration
        StringBuilder colName = new StringBuilder();
        for (int i = firstColumn; i < endColumn; i++) {
            colName.setLength(0);
            int cnum = i;
            do {
                colName.insert(0, (char) ('A' + cnum % 26));
                cnum /= 26;
            }
            while (cnum > 0);
            out.format("    <th class=%s>%s</th>%n", COL_HEAD_CLASS, colName);
        }
        out.format("  </tr>%n");
        out.format("</thead>%n");
    }

    private void printSheetContent(Sheet sheet) {
        printColumnHeads();

        out.format("<tbody>%n");
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();

            out.format("  <tr>%n");
            out.format("    <td class=%s>%d</td>%n", ROW_HEAD_CLASS,
                    row.getRowNum() + 1);
            for (int i = firstColumn; i < endColumn; i++) {
                String content = "&nbsp;";
                String attrs = "";
                CellStyle style = null;
                if (i >= row.getFirstCellNum() && i < row.getLastCellNum()) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        style = cell.getCellStyle();
                        attrs = tagStyle(cell, style);
                        //Set the value that is rendered for the cell
                        //also applies the format
                        CellFormat cf = CellFormat.getInstance(
                                style.getDataFormatString());
                        CellFormatResult result = cf.apply(cell);
                        content = result.text;
                        if (content.equals("")) {
                            content = "&nbsp;";
                        }
                    }
                }
                // if a span row is set, the first cell will span to the end column and hold the values of the first row column
                if (spanRows.contains(row.getRowNum())) {
                    attrs += "colspan=\"" + endColumn + "\"";
                    i = endColumn;
                }

                out.format("    <td class=%s %s>%s</td>%n", styleName(style),
                        attrs, content);
            }
            out.format("  </tr>%n");
        }
        out.format("</tbody>%n");
    }

    private String tagStyle(Cell cell, CellStyle style) {
        if (style.getAlignment() == ALIGN_GENERAL) {
            switch (ultimateCellType(cell)) {
                case HSSFCell.CELL_TYPE_STRING:
                    return "style=\"text-align: left; border-collapse: collapse;\"";
                case HSSFCell.CELL_TYPE_BOOLEAN:
                case HSSFCell.CELL_TYPE_ERROR:
                    return "style=\"text-align: center;\"";
                case HSSFCell.CELL_TYPE_NUMERIC:
                default:
                    // "right" is the default
                    break;
            }
        }
        return "";
    }
}
