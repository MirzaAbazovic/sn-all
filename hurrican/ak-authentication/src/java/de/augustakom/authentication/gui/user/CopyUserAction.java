/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2005 14:27:19
 */
package de.augustakom.authentication.gui.user;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.swing.DialogHelper;

/**
 *
 */
public class CopyUserAction extends AbstractAuthenticationServiceAction {

    private static final Logger LOGGER = Logger.getLogger(CopyUserAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object userObj = getValue(SystemConstants.ACTION_PROPERTY_USEROBJECT);

        if (userObj instanceof AKUser) {
            AKUser origUser = (AKUser) userObj;
            LOGGER.debug(((AKUser) userObj).getName());
            AKUser newUser = new AKUser();
            newUser.setDepartmentId(origUser.getDepartmentId());

            UserRoleCopyDialog dialog = new UserRoleCopyDialog(newUser, origUser);
            DialogHelper.showDialog(GUISystemRegistry.instance().getMainFrame(), dialog, true, true);
        }
    }
}
