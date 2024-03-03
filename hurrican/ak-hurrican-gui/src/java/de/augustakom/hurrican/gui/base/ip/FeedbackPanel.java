/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2011 16:38:43
 */
package de.augustakom.hurrican.gui.base.ip;

import java.awt.*;

import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;

/**
 * Das Panel liefert Informationen ueber die Validitaet einer eingegebenen IP-Adresse. Falls die Adresse valide ist
 * kommt eine entsprechende Meldung in der Farbe gruen. Ansonsten ist die Meldung in der Farbe rot.
 *
 *
 * @since Release 10
 */
class FeedbackPanel extends AKJPanel {

    private static final long serialVersionUID = 9075586724309183396L;
    private final static Color BAD_COLOR = Color.RED;
    private final static Color GOOD_COLOR = new Color(34, 139, 34);
    private final static String GOOD_MESSAGE = "Die angegebene IP-Adresse ist valide.";

    private final AKJLabel lbFeedback;

    public FeedbackPanel() {
        lbFeedback = new AKJLabel();
        add(lbFeedback);
    }

    void setFeedbackText(String text) {
        lbFeedback.setText(text);
        lbFeedback.setForeground(BAD_COLOR);
        revalidate();
        repaint();
    }

    void resetFeedbackText() {
        lbFeedback.setText(GOOD_MESSAGE);
        lbFeedback.setForeground(GOOD_COLOR);
        revalidate();
        repaint();
    }

} // end


