/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.06.2005 09:12:33
 */
package de.augustakom.common.gui.iface;

import java.awt.*;

/**
 * Interface fuer Komponenten, die abhaengig von ihrem Status (aktive/inaktiv) ihre Farbe aendern koennen.
 */
public interface AKColorChangeableComponent {

    /**
     * Setzt die Farbe, die als Hintergrund fuer das TextField verwendet werden soll, wenn es aktiv ist.
     *
     * @param activeColor Hintergrundfarbe bei aktivem TextField
     */
    void setActiveColor(Color activeColor);

    /**
     * Gibt die Farbe zurueck, die als Hintergrund fuer das TextField verwendet werden soll, wenn es aktiv ist.
     *
     * @return Gibt die zu verwendende Hintergrundfarbe fuer ein aktives TextField zurueck.
     */
    Color getActiveColor();

    /**
     * Setzt die Farbe, die als Hintergrund fuer das TextField verwendet werden soll, wenn es inaktiv ist.
     *
     * @param inactiveColor Hintergrundfarbe bei inaktivem TextField
     */
    void setInactiveColor(Color inactiveColor);

    /**
     * Gibt die Farbe zurueck, die als Hintergrund fuer das TextField verwendet werden soll, wenn es inaktiv ist.
     *
     * @return Gibt die zu verwendende Hintergrundfarbe fuer ein inaktives TextField zurueck.
     */
    Color getInactiveColor();
}
