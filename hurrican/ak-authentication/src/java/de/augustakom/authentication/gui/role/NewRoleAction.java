/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 14:36:38
 */
package de.augustakom.authentication.gui.role;

import java.awt.event.*;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.model.AKRole;

/**
 * Action, um ein RoleAdminFrame (mit 'leerem' AKRole-Objekt) aufzurufen.
 *
 *
 */
public class NewRoleAction extends AbstractAuthenticationServiceAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        RoleDataFrame frame = new RoleDataFrame(new AKRole(), null);
        GUISystemRegistry.instance().getMainFrame().registerFrame(frame, false);
    }

}
