/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.04.2010 11:37:55
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.util.*;


/**
 * Leeres Panel!
 */
public class EmptyHWBaugruppenChangePanel extends AbstractHWBaugruppenChangeDefinitionPanel {

    // FIXME to be removed, if all HWBgChange panels are defined!

    public EmptyHWBaugruppenChangePanel() {
        super(null, null);
    }

    @Override
    protected final void createGUI() {
    }

    @Override
    protected void clearAll() {

    }

    @Override
    protected void addPanelWithDestinationData() {
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void validateByStatus() {
    }

}


