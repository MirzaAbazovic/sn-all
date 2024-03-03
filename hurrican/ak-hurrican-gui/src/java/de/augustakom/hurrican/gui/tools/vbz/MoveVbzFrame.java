/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.08.2005 10:17:16
 */
package de.augustakom.hurrican.gui.tools.vbz;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame, um eine VerbindungsBezeichnung einem anderen Auftrag zuzuordnen.
 *
 *
 */
public class MoveVbzFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konstruktor.
     */
    public MoveVbzFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle("Verbindungsbezeichnung umhängen");

        this.setLayout(new BorderLayout());
        this.add(new MoveVbzPanel(), BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


