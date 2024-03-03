/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2004 14:33:57
 */
package de.augustakom.hurrican.gui.system.actions;

import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;


/**
 * Action, um alle z.Z. im MainFrame geoeffneten InternalFrames auf ihre urspruengliche Groesse zu setzen
 *
 *
 */
public class MaximizeAllAction extends AKAbstractAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        HurricanSystemRegistry.instance().getMainFrame().deIconifyAll();
    }

}


