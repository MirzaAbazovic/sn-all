/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import javax.swing.*;

/**
 *
 */
public class CPSTxTextArea extends JTextArea {
    private static final Integer size = 12;
    private static final String SANS_SERIF = "Sans-Serif";
    private static final Font cpsTextAreaFont = new Font(SANS_SERIF, Font.PLAIN, size);

    /**
     * Default-Konstruktor
     */
    public CPSTxTextArea() {
        super();
        this.setFont(cpsTextAreaFont);
        this.setLineWrap(Boolean.FALSE);
        this.setEditable(Boolean.FALSE);
    }
}
