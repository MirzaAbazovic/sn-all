/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2004 14:38:47
 */
package de.augustakom.hurrican.gui.system.actions;

import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;


/**
 * Action, um alle z.Z. im MainFrame geoeffneten InternalFrames zu schliessen.
 *
 *
 */
public class CloseAllAction extends AKAbstractAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        HurricanSystemRegistry.instance().getMainFrame().closeAll();
    }

}


