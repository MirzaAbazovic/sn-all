/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2005 13:32:39
 */
package de.augustakom.common.gui.utils;

import java.awt.*;


/**
 * Hilfsklasse, um Informationen ueber das GUI-System abzufragen.
 *
 *
 */
public class GuiInfo {

    /**
     * Gibt die aktuelle Bildschirmgroesse zurueck.
     *
     * @return
     */
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

}


