/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 07:46:38
 */
package de.augustakom.common.tools.messages;

import java.io.*;


/**
 * Wrapper-Klasse, die mehrere Objekte des Typs <code>AKWarning</code> enthalten kann.
 *
 *
 */
public class AKWarnings extends AKMessages implements Serializable {

    /**
     * Default-Konstruktor.
     */
    public AKWarnings() {
        super();
    }

    /**
     * Fuegt eine neue Warnung hinzu.
     *
     * @param source
     * @param warning
     */
    public AKWarnings addAKWarning(Object source, String warning) {
        addAKMessage(new AKWarning(source, warning));
        return this;
    }

    /**
     * Fuegt eine neue Warnung hinzu, wenn diese nicht {@code null} ist.
     *
     * @param source
     * @param warning
     */
    public AKWarnings addAKWarningNotNull(Object source, String warning) {
        if (warning != null) {
            addAKMessage(new AKWarning(source, warning));
        }
        return this;
    }

    /**
     * Fuegt alle Warnungen des Objekts <code>warnings</code> an die aktuellen Warnungen an.
     *
     * @param warnings2Add
     */
    public AKWarnings addAKWarnings(AKWarnings... warnings2Add) {
        if (warnings2Add != null) {
            for (AKWarnings toAdd : warnings2Add) {
                addAKMessages(toAdd);
            }
        }
        return this;
    }

    public AKWarnings addAKWarning(AKWarning warning2Add) {
        addAKMessage(warning2Add);
        return this;
    }

    /**
     * Erzeugt einen String mit den eingetragenen Warnungen.
     *
     * @return String mit den Warnungen.
     */
    public String getWarningsAsText() {
        return getMessagesAsText();
    }

}


