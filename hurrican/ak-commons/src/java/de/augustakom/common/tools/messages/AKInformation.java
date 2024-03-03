/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 07:44:00
 */
package de.augustakom.common.tools.messages;

import java.io.*;


/**
 * Objekt stellt eine Information dar. <br> Im Gegensatz zu Exceptions soll diese Klasse nur dazu dienen, Informationen
 * an einen Client mitzuteilen.
 */
public class AKInformation extends AKMessage implements Serializable {

    /**
     * Konstruktor mit Angabe des Ursprungs sowie einem Text.
     */
    public AKInformation(Object source, String information) {
        super(source, information);
    }

    /**
     * @return Returns the information.
     */
    public String getInformation() {
        return getMessage();
    }

    /**
     * @param information The information to set.
     */
    public void setInformation(String information) {
        setMessage(information);
    }

}


