/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2005 16:44:31
 */
package de.augustakom.hurrican.gui.kubena;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die Kubena (=Kunden-Benachrichtigung).
 *
 *
 */
public class KubenaFrame extends AKJAbstractInternalFrame {

    // eindeutiger Name des Frames.
    private static final String UNIQUE_NAME = "kubena";

    /**
     * Default-Konstruktor.
     */
    public KubenaFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Kundenbenachrichtigung");
        setIcon("de/augustakom/hurrican/gui/images/message.gif");

        KubenaDefinitionPanel defPanel = new KubenaDefinitionPanel();
        this.getContentPane().setLayout(new BorderLayout());
        this.add(defPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    public String getUniqueName() {
        return UNIQUE_NAME;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


