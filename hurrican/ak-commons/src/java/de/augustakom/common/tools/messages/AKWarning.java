/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 07:44:00
 */
package de.augustakom.common.tools.messages;

import java.io.*;


/**
 * Objekt stellt eine Warnung bzw. eine System-Information dar. <br> Im Gegensatz zu Exceptions soll diese Klasse nur
 * dazu dienen, Informationen die evtl. negative Auswirkungen auf das System haben koennen, an einen Client
 * mitzuteilen.
 *
 *
 */
public class AKWarning extends AKMessage implements Serializable {

    /**
     * Konstruktor mit Angabe des Ursprungs sowie einem Text.
     *
     * @param source
     * @param warning
     */
    public AKWarning(Object source, String warning) {
        super(source, warning);
    }

    /**
     * @return Returns the warning.
     */
    public String getWarning() {
        return getMessage();
    }

    /**
     * @param warning The warning to set.
     */
    public void setWarning(String warning) {
        setMessage(warning);
    }

}


