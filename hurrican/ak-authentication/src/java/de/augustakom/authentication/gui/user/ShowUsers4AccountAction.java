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
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.swing.MessageHelper;


/**
 * Laedt alle Benutzer, die einem bestimmten Account zugeordnet sind und uebergibt die Ergebnismenge einem Frame.
 *
 *
 */
public class ShowUsers4AccountAction extends AbstractAuthenticationServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowUsers4AccountAction.class);
    private static final long serialVersionUID = -8405567791776215819L;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object obj = getValue(SystemConstants.ACTION_PROPERTY_USEROBJECT);
        if (obj instanceof AKAccount) {
            AKAccount account = (AKAccount) obj;

            try {
                AKUserService service = getAuthenticationService(
                        AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
                List<AKUser> users = service.findByAccount(account.getId());
                if ((users == null) || (users.isEmpty())) {
                    throw new GUIException(GUIException.USERS_FOR_ACCOUNT_NOT_FOUND, new Object[] { account.getName() });
                }

                UserListFrame frame = new UserListFrame(account, users);
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
            LOGGER.warn("User-Object for action <ShowUsers4RoleAction> is not of type AKAccount!");
        }
    }

}
