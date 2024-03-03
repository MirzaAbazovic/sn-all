/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.net.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Hilfsklasse fuer Icon-Operationen.
 *
 *
 */
public class IconHelper {

    private static final Logger LOGGER = Logger.getLogger(IconHelper.class);

    public IconHelper() {
    }

    public static ImageIcon createImageIcon(String iconUrl) {
        return new IconHelper().getIcon(iconUrl);
    }

    /**
     * Interpretiert den uebergebenen String als URL und versucht diese Resource zu laden sowie daraus ein Icon zu
     * erzeugen.
     *
     * @param iconURL URL des Images
     * @return Instanz von javax.swing.ImageIcon oder <code>null</code>, falls die URL ungueltig war.
     */
    public ImageIcon getIcon(String iconURL) {
        if (StringUtils.isNotEmpty(iconURL)) {
            try {
                URL resourceURL = getClass().getClassLoader().getResource(iconURL);
                if (resourceURL != null) {
                    ImageIcon imageIcon = new ImageIcon(resourceURL);
                    return imageIcon;
                }
                LOGGER.warn("Resource not found for Icon-URL " + iconURL);
            }
            catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return null;
    }

}
