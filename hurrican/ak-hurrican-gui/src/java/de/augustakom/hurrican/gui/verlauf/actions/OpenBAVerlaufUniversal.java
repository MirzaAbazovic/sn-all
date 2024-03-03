/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2014
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.hurrican.gui.verlauf.BauauftragFrame;

/**
 * Action, um die abteilungs-uebergreifende Bauauftrags-Maske zu oeffnen.
 */
public class OpenBAVerlaufUniversal extends OpenVerlaufFrameAction {

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        BauauftragFrame frame = new BauauftragFrame();
        uniqueName = frame.getUniqueName();
        return frame;
    }

    @Override
    protected Long getAbteilung4Verlauf() {
        return null;
    }

}
