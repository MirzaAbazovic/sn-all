/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.13
 */
package de.augustakom.common.gui.utils;

import java.text.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;

/**
 *
 */
public class SwingFactoryUtils {

    private final static String WHITESPACE = " ";

    /**
     * Converts a String into a valid multiline-HTML-taged-String, if the string contains a {linebreak}-tag.
     * <p/>
     * This methode will be typical used to get a MultiLine-Button see {@link de.augustakom.common.gui.swing.SwingFactory#createButton(String)}.
     */
    public static String convertMultilineTextToHtml(String text) {
        final String[] lines = text.split("\\{linebreak\\}");
        if (lines.length == 1) {
            return lines[0];
        }
        String html = "<html>%s</html>";
        String div = "<div align=\"center\">%s</div>";
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(String.format(div, line));
        }
        return String.format(html, builder.toString());
    }

    /**
     * Format text with a thousands separators, this method works also for string which also contains not purely strings
     * number.
     *
     * @param text
     * @return if string has numbers like '100000 kbit/s / 10000 kbit/s' this method return a string '100.000 kbit/s
     * 10.000 kbit/s'
     */
    public static String formatStringTextWithThousandsSeparators(String text) {
        if (text == null) {
            return null;
        }
        String endResultText = "";
        String[] textArray = text.split("\\s++");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
        for (int i = 0; i < textArray.length; i++) {
            if (StringUtils.isNumeric(textArray[i])) {
                endResultText += formatter.format(Integer.parseInt(textArray[i])) + WHITESPACE;
            }
            else {
                endResultText += textArray[i] + WHITESPACE;
            }
        }
        return endResultText.trim();
    }

}
