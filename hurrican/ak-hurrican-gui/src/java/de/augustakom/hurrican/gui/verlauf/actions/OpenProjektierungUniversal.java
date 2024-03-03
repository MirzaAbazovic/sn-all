/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.2014
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.hurrican.gui.verlauf.ProjektierungFrame;

/**
 * Action, um die abteilungs-uebergreifende Projektierungs-GUI zu oeffnen
 *
 *
 */
public class OpenProjektierungUniversal extends OpenProjektierungFrameAction {

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        ProjektierungFrame frame = new ProjektierungFrame();
        uniqueName = frame.getUniqueName();
        return frame;
    }

    @Override
    protected Long getAbteilung4Projektierung() {
        return null;
    }


}


