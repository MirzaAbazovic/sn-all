/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2004
 */
package de.augustakom.common.gui.awt;

import java.awt.*;

/**
 * Factory-Klasse, um GridBagConstraints-Objekte zu erstellen.
 */
public class GBCFactory {

    // Definition eines Insets-Objekt fuer den Standard-Abstand zwischen Komponenten
    private static final Insets DEFAULT_INSETS = new Insets(2, 2, 2, 2);

    /**
     * Gibt ein GridBagConstraint-Objekt mit den entsprechenden Werten zurueck.
     *
     * @param weightx    Ausdehnung in X-Richtung (0 - 100)
     * @param weighty    Ausdehnung in Y-Richtung (0 - 100)
     * @param x          X-Koordinate, auf der die linke obere Ecke platziert werden soll
     * @param y          Y-Koordinate, auf der die linke obere Ecke platziert werden soll
     * @param gridwidth  Angabe der Spaltenzahl, ueber die sich die Komponente erstrecken soll.
     * @param gridheight Angabe der Zeilenzahl, ueber die sich die Komponente erstrecken soll
     * @param fill       Ausdehnungskonstante fuer die Komponente
     * @return GridBagConstraints-Objekt mit den angegebenen Daten.
     */
    public static GridBagConstraints createGBC(int weightx, int weighty, int x, int y,
            int gridwidth, int gridheight, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.insets = DEFAULT_INSETS;
        gbc.fill = fill;
        return gbc;
    }

    /**
     * Gibt ein GridBagConstraint-Objekt mit den entsprechenden Werten zurueck.
     *
     * @param weightx    Ausdehnung in X-Richtung (0 - 100)
     * @param weighty    Ausdehnung in Y-Richtung (0 - 100)
     * @param x          X-Koordinate, auf der die linke obere Ecke platziert werden soll
     * @param y          Y-Koordinate, auf der die linke obere Ecke platziert werden soll
     * @param gridwidth  Angabe der Spaltenzahl, ueber die sich die Komponente erstrecken soll.
     * @param gridheight Angabe der Zeilenzahl, ueber die sich die Komponente erstrecken soll
     * @param fill       Ausdehnungskonstante fuer die Komponente
     * @param anchor     Konstante für die Orientierung der Komponente (e.g. WEST, EAST)
     * @return GridBagConstraints-Objekt mit den angegebenen Daten.
     */
    public static GridBagConstraints createGBCAnchor(int weightx, int weighty, int x, int y,
            int gridwidth, int gridheight, int fill, int anchor) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.insets = DEFAULT_INSETS;
        gbc.fill = fill;
        gbc.anchor = anchor;
        return gbc;
    }

    /**
     * Gibt ein GridBagConstraint-Objekt mit den entsprechenden Werten zurueck.
     *
     * @param weightx    Ausdehnung in X-Richtung (0 - 100)
     * @param weighty    Ausdehnung in Y-Richtung (0 - 100)
     * @param x          X-Koordinate, auf der die linke obere Ecke platziert werden soll
     * @param y          Y-Koordinate, auf der die linke obere Ecke platziert werden soll
     * @param gridwidth  Angabe der Spaltenzahl, ueber die sich die Komponente erstrecken soll.
     * @param gridheight Angabe der Zeilenzahl, ueber die sich die Komponente erstrecken soll
     * @param fill       Ausdehnungskonstante fuer die Komponente
     * @param insets
     * @return GridBagConstraints-Objekt mit den angegebenen Daten.
     */
    public static GridBagConstraints createGBC(int weightx, int weighty, int x, int y,
            int gridwidth, int gridheight, int fill, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.insets = insets;
        gbc.fill = fill;
        return gbc;
    }

    /**
     * same as {@link #createGBC(int, int, int, int, int, int, int, Insets)}, just with the option of internal Padding
     * *
     */
    public static GridBagConstraints createGBC(int weightx, int weighty, int x, int y,
            int gridwidth, int gridheight, int fill, int internalPaddingX, int internalPaddingY) {
        GridBagConstraints gbc = createGBC(weightx, weighty, x, y, gridwidth, gridheight, fill);
        gbc.ipadx = internalPaddingX;
        gbc.ipady = internalPaddingY;
        return gbc;
    }

    /**
     * @see #createGBC(int, int, int, int, int, int, int, Insets)
     */
    public static GridBagConstraints createGBC(int weightx, int weighty, int x, int y,
            int gridwidth, int gridheight, int fill, int leftInsets) {
        return createGBC(weightx, weighty, x, y, gridwidth, gridheight, fill,
                new Insets(2, leftInsets, 2, 2));
    }
}
