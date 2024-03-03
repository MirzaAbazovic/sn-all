/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2014
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.commons.lang.StringUtils;

/**
 * Ein JFormattedTextField.AbstractFormatter, welcher den Input eines Textfields gegen eine RegExp prueft und die
 * Eingabe ablehnt wenn diese nicht der Vorgabe entspricht.<p/> Wird in der XML Beschreibung eines FormattedTextField
 * wie folgt angegeben:
 * <pre>
 *             &lt;format type="regexp"&gt;[1-9][0-9]*&lt;/format&gt;
 * </pre>
 *
 *
 * @see String#matches(String)
 */
public class RegexpFormatter extends JFormattedTextField.AbstractFormatter {
    private String regexp;

    public RegexpFormatter(String regexp) {
        this.regexp = regexp;
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        return value == null ? null : value.toString();
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        return text;
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                super.insertString(fb, offset, string, attr);
                Document doc = fb.getDocument();
                String docText = doc.getText(0, doc.getLength());
                if (!docText.matches(regexp)) {
                    Toolkit.getDefaultToolkit().beep();
                    super.remove(fb, offset, string.length());
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Document doc = fb.getDocument();
                String replaced = doc.getText(offset, length);
                super.replace(fb, offset, length, text, attrs);
                String docText = doc.getText(0, doc.getLength());
                if (StringUtils.isNotEmpty(docText) && !docText.matches(regexp)) {
                    Toolkit.getDefaultToolkit().beep();
                    super.replace(fb, offset, text.length(), replaced, attrs);
                }
            }
        };
    }
}