/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2011 13:44:23
 */
package de.augustakom.common.gui.utils;

import java.awt.*;
import com.google.common.base.Preconditions;

/**
 * Hilfsklasse fuer die Arbeit mit Screens.
 */
public class ScreenTools {

    /**
     * Setzt die {@code preferredSize} des Containers {@code toLimit} auf {@code percent} Prozent der maximal
     * verfuegbaren Screen Groesse.
     *
     * @param toLimit
     * @param percent
     */
    public static void limitPreferredSizeIfToBig(Container toLimit, int percent) {
        limitPreferredSizeIfToBig(toLimit, percent, percent);
    }

    /**
     * @param toLimit
     * @param percentWidth
     * @param percentHeight
     * @see ScreenTools#limitPreferredSizeIfToBig(Container, int) Allerdings kann hier die Prozentangabe fuer die Breite
     * / Hoehe einzeln angegeben werden.
     */
    public static void limitPreferredSizeIfToBig(Container toLimit, int percentWidth, int percentHeight) {
        Preconditions.checkNotNull(toLimit);
        Dimension currentDim = toLimit.getPreferredSize();
        Dimension maxDim = GuiInfo.getScreenSize();

        double widthToSet = currentDim.getWidth();
        double heightToSet = currentDim.getHeight();

        boolean changed = false;
        if (widthToSet > maxDim.getWidth()) {
            int percent4Width = ((percentWidth > 100) || (percentWidth < 0)) ? 100 : percentWidth;
            widthToSet = calculateSize(maxDim.getWidth(), percent4Width);
            changed = true;
        }

        if (heightToSet > maxDim.getHeight()) {
            int percent4Height = ((percentHeight > 100) || (percentHeight < 0)) ? 100 : percentHeight;
            heightToSet = calculateSize(maxDim.getHeight(), percent4Height);
            changed = true;
        }

        if (changed) {
            Dimension newSize = new Dimension();
            newSize.setSize(widthToSet, heightToSet);
            toLimit.setPreferredSize(newSize);
        }
    }

    private static double calculateSize(double maxSize, int percent) {
        return (percent == 100) ? maxSize : (maxSize / 100) * percent;
    }

}


