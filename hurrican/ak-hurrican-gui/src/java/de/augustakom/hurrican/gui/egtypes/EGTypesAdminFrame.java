/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2011 12:33:48
 */
package de.augustakom.hurrican.gui.egtypes;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;

/**
 *
 */
public class EGTypesAdminFrame extends AbstractAdminFrame {
    private EGTypesAdminPanel egTypesAdminPanel = null;

    public EGTypesAdminFrame() {
        super("de/augustakom/hurrican/gui/egtypes/resources/EGTypesAdminFrame.xml");
        createGUI();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        egTypesAdminPanel = new EGTypesAdminPanel();
        getChildPanel().add(egTypesAdminPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    protected AbstractAdminPanel[] getAdminPanels() {
        return new AbstractAdminPanel[] { egTypesAdminPanel };
    }

}


