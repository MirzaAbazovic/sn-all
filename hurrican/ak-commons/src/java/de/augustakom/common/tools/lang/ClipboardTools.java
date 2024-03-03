/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2005 10:46:23
 */
package de.augustakom.common.tools.lang;

import java.awt.datatransfer.*;


/**
 * Klasse mit Hilfsfunktionen fuer das Arbeiten mit dem System-Clipboard.
 *
 *
 */
public class ClipboardTools {

    /**
     * Kopiert den String <code>toCopy</code> in das System-Clipboard.
     *
     * @param toCopy
     */
    public static void copy2Clipboard(String toCopy) {
        Clipboard clip = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection content = new StringSelection(toCopy);
        clip.setContents(content, content);
    }

}


