/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 */
package de.augustakom.common.gui.swing.xml;

import javax.swing.*;

/**
 * TextPane for rendering xml
 */
public class XmlTextPane extends JTextPane {

    private static final String TEXT_XML = "text/xml";

    /**
     * Default-Konstruktor fuer eine {@link JTextPane}, die den Inhalt in XML-Style darstellt.
     */
    public XmlTextPane() {
        this.setEditorKitForContentType(TEXT_XML, new XmlEditorKit());
        this.setContentType(TEXT_XML);
        this.setEditable(false);
    }
}
