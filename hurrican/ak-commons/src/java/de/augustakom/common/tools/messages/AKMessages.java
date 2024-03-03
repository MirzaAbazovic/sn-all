/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.04.2006 09:12:23
 */
package de.augustakom.common.tools.messages;

import java.io.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SystemUtils;


/**
 * Wrapper-Klasse, die mehrere Objekte des Typs <code>AKMessage</code> enthalten kann.
 */
public class AKMessages implements Serializable {

    private static final long serialVersionUID = -701452925828937482L;
    private List<AKMessage> messages = new ArrayList<>();

    /**
     * Default-Konstruktor.
     */
    public AKMessages() {
        super();
    }

    /**
     * Fuegt eine neue Message hinzu.
     */
    protected void addAKMessage(AKMessage message) {
        messages.add(message);
    }

    /**
     * Fuegt alle Messages der Liste <code>messages2Add</code> an die aktuellen Messages an.
     */
    public void addAKMessages(AKMessages messages2Add) {
        if ((messages2Add != null) && messages2Add.isNotEmpty()) {
            CollectionUtils.addAll(this.messages, messages2Add.getAKMessages().iterator());
        }
    }

    /**
     * Ueberprueft, ob Messages existieren.
     *
     * @return true, wenn mindestens eine Message eingetragen ist.
     */
    public boolean isEmpty() {
        return ((messages == null) || (messages.isEmpty()));
    }

    /**
     * Ueberprueft, ob Messages existieren.
     *
     * @return true, wenn mindestens eine Message eingetragen ist.
     */
    public boolean isNotEmpty() {
        return ((messages != null) && (!messages.isEmpty()));
    }

    /**
     * Gibt alle generierten AKMessages in einer Liste zurueck.
     *
     * @return Liste mit Objekten des Typs <code>AKMessage</code>.
     */
    public List<AKMessage> getAKMessages() {
        return messages;
    }

    /**
     * Erzeugt einen String mit den eingetragenen Warnungen.
     *
     * @return String mit den Warnungen.
     */
    public String getMessagesAsText() {
        if (isEmpty()) { return null; }

        StringBuilder sb = new StringBuilder();
        for (AKMessage w : messages) {
            sb.append(w.getMessage());
            if (w.getSourceAsString() != null) {
                sb.append(" [Source: ");
                sb.append(w.getSourceAsString());
                sb.append("]");
            }
            sb.append(SystemUtils.LINE_SEPARATOR);
        }
        return sb.toString();
    }

}


