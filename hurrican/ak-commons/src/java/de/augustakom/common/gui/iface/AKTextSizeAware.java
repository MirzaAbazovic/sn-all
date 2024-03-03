/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2005 13:38:40
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer Klassen, die auf eine maximal zulaessige Zeichenzahl achten koennen/sollen.
 *
 *
 */
public interface AKTextSizeAware {

    /**
     * Gibt an, wie viele Zeichen das Objekt maximal darstellen kann.
     *
     * @param maxChars
     */
    public void setMaxChars(int maxChars);

    /**
     * Gibt die Anzahl der max. zulaessigen Zeichen auf dem Objekt zurueck.
     *
     * @return
     */
    public int getMaxChars();

}


