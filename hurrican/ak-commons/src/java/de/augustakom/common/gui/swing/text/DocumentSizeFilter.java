/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2005 13:31:00
 */
package de.augustakom.common.gui.swing.text;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * DocumentFilter um die Anzahl der max. zulaessigen Zeichen innerhalb einer TextKomponente zu begrenzen.
 */
public class DocumentSizeFilter extends DocumentFilter {
    private static final Logger LOGGER = Logger.getLogger(DocumentSizeFilter.class);

    protected JTextComponent textComp = null;
    protected int maxCharacters;
    private boolean notify = false;

    /**
     * Konstruktor mit Angabe der max. zulaessigen Anzahl von Zeichen.
     */
    public DocumentSizeFilter(JTextComponent textComp, int maxChars) {
        this.textComp = textComp;
        this.maxCharacters = maxChars;
    }

    /**
     * @see javax.swing.text.DocumentFilter#insertString(javax.swing.text.DocumentFilter.FilterBypass, int,
     * java.lang.String, javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
        if ((fb.getDocument().getLength() + str.length()) <= maxCharacters) {
            super.insertString(fb, offs, str, a);
        }
        else {
            String toInsert = StringUtils.substring(str, 0, maxCharacters - fb.getDocument().getLength());
            super.insertString(fb, offs, toInsert, a);
            notifyToLarge();
        }
    }

    /**
     * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass, int, int,
     * java.lang.String, javax.swing.text.AttributeSet)
     */
    @Override
    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
        if (str == null) {
            super.replace(fb, 0, fb.getDocument().getLength(), null, a);
            return;
        }

        if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters) {
            super.replace(fb, offs, length, str, a);
        }
        else {
            int subEnd = maxCharacters - fb.getDocument().getLength() + length;
            if (subEnd > 0) {
                String toInsert = StringUtils.substring(str, 0, subEnd);
                super.replace(fb, offs, length, toInsert, a);
            }
            notifyToLarge();
        }
    }

    /**
     * Erstellt eine Benachrichtigung, dass der Text nicht oder nicht ganz an die Text-Komponente uebergeben wurde.
     */
    protected void notifyToLarge() {
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Wechselt voruebergehend die Hintergrund-Farbe der Text-Komponente.
     */
    protected void changeColor() {
        if ((textComp != null) && !notify) {
            final Color defaultBG = textComp.getBackground();
            textComp.setBackground(Color.red);
            notify = true;
            final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(250);
                    return null;
                }

                @Override
                public void done() {
                    try {
                        get();
                    }
                    catch (Exception e) {
                        LOGGER.info("done() - got exception when sleeping", e);
                    }
                    finally {
                        textComp.setBackground(defaultBG);
                        notify = false;
                    }
                }
            };
            worker.execute();
        }
    }
}


