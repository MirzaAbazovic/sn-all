/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 09:24:04
 */
package de.augustakom.common.gui.swing.wizard;

import java.awt.*;

/**
 * Div. Hilfsmethoden fuer den Wizard.
 */

public class WizardUtilities {

    public static void centerComponentOnScreen(Component component) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();

        Point p = new Point();
        p.x += ((d.width - component.getWidth()) / 2);
        p.y += ((d.height - component.getHeight()) / 2);

        if (p.x < 0) {
            p.x = 0;
        }

        if (p.y < 0) {
            p.y = 0;
        }

        component.setLocation(p);
    }

}
