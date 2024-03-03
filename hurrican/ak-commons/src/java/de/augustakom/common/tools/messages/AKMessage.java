/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.04.2006 09:12:16
 */
package de.augustakom.common.tools.messages;

import java.io.*;


/**
 * Objekt stellt eine Message dar. <br> Diese Klasse soll dazu dienen, z.B. in Services Informationen zu generieren und
 * ueber die Klasse AKMessages gesammelt an den Caller zu uebergeben.
 */
public abstract class AKMessage implements Serializable {

    private String message;
    private String sourceAsString;

    /**
     * Konstruktor mit Angabe des Ursprungs sowie einem Text.
     */
    public AKMessage(Object source, String message) {
        super();
        setSource(source);
        this.message = message;
    }

    public String getSourceAsString() {
        return sourceAsString;
    }

    /**
     * @param source The source to set.
     */
    private void setSource(Object source) {
        if (source != null) {
            if (source.getClass().isPrimitive() || (source instanceof String)) {
                sourceAsString = source.getClass().getSimpleName() + ": " + source;
            }
            else {
                sourceAsString = source.getClass().getSimpleName();
            }
        }
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

}


