/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015 09:05
 */
package de.augustakom.hurrican.gui.hvt.umzug;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;

/**
 * Basis-Frame fuer den HVT-Umzug
 */
public class HvtUmzugFrame extends AKJAbstractInternalFrame {

    public HvtUmzugFrame() {
        super("de/augustakom/hurrican/gui/hvt/umzug/resources/HvtUmzugFrame.xml");
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(new HvtUmzugPanel(), BorderLayout.CENTER);
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // not needed for this panel
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed for this panel
    }

}
