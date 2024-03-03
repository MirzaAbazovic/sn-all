/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2004 14:20:35
 */
package de.augustakom.authentication.gui.user;

import java.awt.event.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.swing.MessageHelper;


/**
 * Laedt alle Benutzer, die einer bestimmten zugeordnet sind und uebergibt die Ergebnismenge einem Frame.
 *
 *
 */
public class ShowUsers4RoleAction extends AbstractAuthenticationServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowUsers4RoleAction.class);
    private static final long serialVersionUID = -8841743208519413763L;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object obj = getValue(SystemConstants.ACTION_PROPERTY_USEROBJECT);
        if (obj instanceof AKRole) {
            AKRole role = (AKRole) obj;

            try {
                AKUserService service = getAuthenticationService(
                        AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
                List<AKUser> users = service.findByRole(role.getId());

                if ((users == null) || (users.isEmpty())) {
                    throw new GUIException(GUIException.USERS_FOR_ROLE_NOT_FOUND, new Object[] { role.getName() });
                }

                UserListFrame frame = new UserListFrame(role, users);
                GUISystemRegistry.instance().getMainFrame().registerFrame(frame, false);
            }
            catch (GUIException ex) {
                LOGGER.info(ex.getMessage());
                MessageHelper.showInfoDialog(GUISystemRegistry.instance().getMainFrame(), ex.getLocalizedMessage());
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), ex);
            }
        }
        else {
            LOGGER.warn("User-Object for action <ShowUsers4RoleAction> is not of type AKRole!");
        }
    }

}
