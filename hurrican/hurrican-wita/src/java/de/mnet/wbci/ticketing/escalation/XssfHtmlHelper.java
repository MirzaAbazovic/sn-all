/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.04.2014
 */
package de.mnet.wbci.ticketing.escalation;

import java.util.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Implementation of an HtmlHelper for XSSF files.
 *
 * @author Ken Arnold, Industrious Media LLC
 *
 */
public class XssfHtmlHelper {
    private final XSSFWorkbook wb;

    public XssfHtmlHelper(XSSFWorkbook wb) {
        this.wb = wb;
    }

    public void colorStyles(CellStyle style, Formatter out) {
        XSSFCellStyle cs = (XSSFCellStyle) style;
        styleColor(out, "background-color", cs.getFillForegroundXSSFColor());
        styleColor(out, "text-color", cs.getFont().getXSSFColor());
    }

    private void styleColor(Formatter out, String attr, XSSFColor color) {
        if (color == null || color.isAuto()) {
            return;
        }

        byte[] rgb = color.getRgb();
        if (rgb == null) {
            return;
        }

        // This is done twice -- rgba is new with CSS 3, and browser that don't
        // support it will ignore the rgba specification and stick with the
        // solid color, which is declared first
        out.format("  %s: #%02x%02x%02x;%n", attr, rgb[0], rgb[1], rgb[2]);
    }
}

