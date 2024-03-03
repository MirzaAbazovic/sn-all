/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2012 10:32:23
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;

public class WitaClearingToolsFrame extends AKJAbstractInternalFrame {

    private static final long serialVersionUID = 6344067184795305394L;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/WitaClearingToolsFrame.xml";

    private static final String TITEL = "title";

    public WitaClearingToolsFrame() {
        super(RESOURCE);
        createGUI();
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText(TITEL));
        WitaClearingToolsPanel clearingToolsPnl = new WitaClearingToolsPanel();

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(clearingToolsPnl, BorderLayout.CENTER);
        this.pack();
    }

    @Override
    protected void execute(String command) {
        // not used
    }
}
