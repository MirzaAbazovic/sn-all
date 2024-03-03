/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;


/**
 * AK-Implementierung eines JFrames.
 *
 *
 */
public class AKJFrame extends JFrame {

    /**
     * Erzeugt ein neues Frame.
     */
    public AKJFrame() {
        super();
    }

    /**
     * Erzeugt ein neues Frame mit Angabe eines <code>GraphicConfiguration</code>-Objekts.
     *
     * @param gc
     */
    public AKJFrame(GraphicsConfiguration gc) {
        super(gc);
    }

    /**
     * Erzeugt ein neues Frame mit Angabe des Titels.
     *
     * @param title Titel fuer das Frame
     * @throws java.awt.HeadlessException
     */
    public AKJFrame(String title) {
        super(title);
    }

    /**
     * Erzeugt ein neues Frame mit Angabe des Titels und eines <code>GraphicsConfiguration</code>-Objekts.
     *
     * @param title Titel fuer das Frame
     * @param gc
     */
    public AKJFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
    }
}
