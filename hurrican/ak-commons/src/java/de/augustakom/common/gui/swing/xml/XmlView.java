/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.common.gui.swing.xml;

import java.awt.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.text.*;

/**
 * The view represents each child element as a line of text. <br> <br> Stolen from
 * http://code.google.com/p/xml-text-editor/ (Pattern and Color definitions are changed!)
 */
class XmlView extends PlainView {

    private static Map<Pattern, Color> patternColors;
    private static String GENERIC_XML_NAME = "[A-Za-z]+[A-Za-z0-9\\-_]*(:[A-Za-z]+[A-Za-z0-9\\-_]+)?";
    private static String TAG_PATTERN = "(</?" + GENERIC_XML_NAME + ")";
    private static String TAG_END_PATTERN = "(>|/>)";
    private static String TAG_ATTRIBUTE_PATTERN = "(" + GENERIC_XML_NAME + ")\\w*\\=";
    private static String TAG_ATTRIBUTE_VALUE = "\\w*\\=\\w*(\"[^\"]*\")";
    private static String TAG_COMMENT = "(<\\!--[\\w ]*-->)";
    private static String TAG_CDATA = "(<\\!\\[CDATA\\[.*\\]\\]>)";

    private static final Color DARK_GREEN = new Color(63, 127, 127);
    private static final Color DARK_RED = new Color(200, 10, 50);

    static {
        // NOTE: the order is important!
        patternColors = new HashMap<Pattern, Color>();
        patternColors.put(Pattern.compile(TAG_PATTERN), DARK_GREEN);
        patternColors.put(Pattern.compile(TAG_CDATA), Color.GRAY);
        patternColors.put(Pattern.compile(TAG_ATTRIBUTE_PATTERN), DARK_RED);
        patternColors.put(Pattern.compile(TAG_END_PATTERN), DARK_GREEN);
        patternColors.put(Pattern.compile(TAG_ATTRIBUTE_VALUE), Color.BLACK);
        patternColors.put(Pattern.compile(TAG_COMMENT), Color.BLUE);
    }

    public XmlView(Element element) {
        super(element);
        // Set tabsize to 4 (instead of the default 8)
        getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
    }

    @Override
    protected int drawUnselectedText(Graphics graphics, int xIn, int y, int p0, int p1) throws BadLocationException {
        int x = xIn;
        Document doc = getDocument();
        String text = doc.getText(p0, p1 - p0);

        Segment segment = getLineBuffer();

        SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
        SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();

        // Match all regexes on this snippet, store positions
        for (Map.Entry<Pattern, Color> entry : patternColors.entrySet()) {
            Matcher matcher = entry.getKey().matcher(text);

            while (matcher.find()) {
                startMap.put(matcher.start(), matcher.end());
                colorMap.put(matcher.start(), entry.getValue());
            }
        }

        int i = 0;

        // Colour the parts
        for (Map.Entry<Integer, Integer> entry : startMap.entrySet()) {
            int start = entry.getKey();
            int end = entry.getValue();

            if (i < start) {
                graphics.setColor(Color.RED);
                doc.getText(p0 + i, start - i, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }

            graphics.setColor(colorMap.get(start));
            i = end;
            doc.getText(p0 + start, i - start, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
        }

        // Paint possible remaining text black
        if (i < text.length()) {
            graphics.setColor(Color.RED);
            doc.getText(p0 + i, text.length() - i, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
        }
        return x;
    }
}
