/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 14:36:38
 */
package de.augustakom.authentication.gui.db;

import java.awt.event.*;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.model.AKDb;

/**
 * Action, um ein DbAdminFrame (mit 'leerem' AKDb-Objekt) aufzurufen.
 *
 *
 */
public class NewDbAction extends AbstractAuthenticationServiceAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        DbDataFrame frame = new DbDataFrame(new AKDb(), null);
        GUISystemRegistry.instance().getMainFrame().registerFrame(frame, false);
    }

}
