/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.awt;

import java.awt.*;

/**
 * Diese Klasse stellt Methoden zur Verfuegung, um mit Bildschirmkoordination zu arbeiten.
 */
public class LocationHelper {

    /**
     * Gibt Koordinaten fuer den zu zentrierenden Container zurueck, um ihn zentriert darstellen zu koennen. <br> Wenn
     * ein Owner vorhanden ist (nicht <code>null</code>) und dargestellt wird (isShowing=true), dann werden die
     * Koordination relativ zu dem Owner berechnet. Ansonsten beziehen sich die Koordination auf den gesamten
     * Bildschirm.
     *
     * @param owner     Owner-Komponente.
     * @param container Container, der zentriert werden soll
     * @return Koordinaten, um den Container zentriert darzustellen.
     */
    public static Point getCenterLocation(Component owner, Container container) {
        return getCenterLocation(owner, container.getSize());
    }

    /**
     * @see #getCenterLocation(Component, Container)
     */
    public static Point getCenterLocation(Component owner, Dimension sizeOfComp2Show) {
        Point parentLocation = null;
        Dimension parentSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (owner != null && owner.isShowing()) {
            parentLocation = owner.getLocationOnScreen();
            parentSize = owner.getSize();
        }

        if (parentLocation == null) {
            parentLocation = new Point(0, 0);
        }

        int x = parentLocation.x + (parentSize.width / 2 - sizeOfComp2Show.width / 2);
        int y = parentLocation.y + (parentSize.height / 2 - sizeOfComp2Show.height / 2);

        x = (x > 0) ? x : 0;
        y = (y > 0) ? y : 0;

        return new Point(x, y);
    }
}
