/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015 09:12
 */
package de.augustakom.hurrican.gui.hvt.umzug.actions;

import java.util.*;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.hvt.umzug.HvtUmzugFrame;

/**
 * Action, um das Frame fuer den HVT-Umzug zu oeffnen.
 */
public class OpenHvtUmzugFrameAction extends AKAbstractOpenFrameAction {

    private static final String UNIQUE_NAME = UUID.randomUUID().toString().replaceAll("-", "");

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected AKJInternalFrame getFrameToOpen() {
        return new HvtUmzugFrame();
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected String getUniqueName() {
        return UNIQUE_NAME;
    }

    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected boolean maximizeFrame() {
        return true;
    }

}
