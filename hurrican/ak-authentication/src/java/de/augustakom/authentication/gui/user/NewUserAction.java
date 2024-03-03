/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 14:36:38
 */
package de.augustakom.authentication.gui.user;

import java.awt.event.*;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;


/**
 * Action, um ein UserAdminFrame aufzurufen.
 *
 *
 */
public class NewUserAction extends AbstractAuthenticationServiceAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        AKUser user = new AKUser();

        // User mit Department-ID vorbelegen, sofern Parameter 'ACTION_PROPERTY_USEROBJECT'
        // vom Typ AKDepartment ist.
        Object userObj = getValue(SystemConstants.ACTION_PROPERTY_USEROBJECT);
        if (userObj instanceof AKDepartment) {
            user.setDepartmentId(((AKDepartment) userObj).getId());
        }

        UserDataFrame frame = new UserDataFrame(user, null);
        GUISystemRegistry.instance().getMainFrame().registerFrame(frame, false);
    }

}
