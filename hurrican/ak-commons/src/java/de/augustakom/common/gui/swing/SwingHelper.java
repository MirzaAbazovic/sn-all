/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2005 08:42:20
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.*;


/**
 * Hilfsklasse fuer div. Swing-Funktionen.
 *
 *
 */
public class SwingHelper {

    /**
     * Setzt das Fenster <code>window</code> auf die oberste Ebene des Bildschirms.
     *
     * @param window
     */
    public static void moveToScreenFront(Window window) {
        if (window != null) {
            window.setAlwaysOnTop(true);
            window.repaint();
            window.setAlwaysOnTop(false);
        }
    }

    /**
     * Checks if for the assigned component have loaded the XML resource inside of the swingFactory. If not an error
     * dialog appears.
     *
     * @param component    any kind of {@link Component}
     * @param swingFactory initialized {@link SwingFactory} e.g. over {@code SwingFactory.getInstance(resource)}
     * @param resource     Path to XML resource as {@link String}.
     */
    public static void checkXmlResourceLoaded(Component component, SwingFactory swingFactory, String resource) {
        if ((swingFactory instanceof SwingFactoryXML) && !((SwingFactoryXML) swingFactory).isXmlDocumentLoaded()) {
            MessageHelper.showErrorDialog(
                    component,
                    new MissingResourceException(
                            "XML-Resource '" + resource + "' konnte nicht gefunden werden!",
                            component.getName(),
                            resource)
            );
        }
    }
}


