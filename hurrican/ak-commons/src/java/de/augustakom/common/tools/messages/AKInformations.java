/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 07:46:38
 */
package de.augustakom.common.tools.messages;

import java.io.*;


/**
 * Wrapper-Klasse, die mehrere Objekte des Typs <code>AKInformation</code> enthalten kann.
 */
public class AKInformations extends AKMessages implements Serializable {

    /**
     * Default-Konstruktor.
     */
    public AKInformations() {
        super();
    }

    /**
     * Fuegt eine neue Information hinzu.
     */
    public void addAKInformation(Object source, String information) {
        addAKMessage(new AKInformation(source, information));
    }

    /**
     * Fuegt alle Informations des Objekts <code>informations2Add</code> an die aktuellen Informationen an.
     */
    public void addAKInformation(AKInformations informations2Add) {
        addAKMessages(informations2Add);
    }

    /**
     * Erzeugt einen String mit den eingetragenen Informationen.
     *
     * @return String mit den Informationen.
     */
    public String getInformationsAsText() {
        return getMessagesAsText();
    }

}


