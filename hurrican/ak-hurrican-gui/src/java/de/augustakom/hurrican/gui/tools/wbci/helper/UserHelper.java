/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.helper;

import java.util.*;

import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.mnet.wbci.config.WbciConstants;

/**
 * WBCI Helper class for working with Users / Roles from the GUI
 */
public final class UserHelper {

    private UserHelper() {
        // private as its a helper class
    }

    /**
     * Checks if the supplied {@code roleName} is contained within the list of roles.
     *
     * @param roles    list of roles
     * @param roleName role name to match against
     */
    public static boolean isRoleInList(List<AKRole> roles, String roleName) {
        for (AKRole userRole : roles) {
            if (userRole.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the list of roles for the current GUI user
     *
     * @param akUserService the user service required for accessing information on current user
     * @return the list of roles
     * @throws AKAuthenticationException
     */
    public static List<AKRole> getUserRoles(AKUserService akUserService) throws AKAuthenticationException {
        Long userId = HurricanSystemRegistry.instance().getCurrentUser().getId();
        return akUserService.getRoles(userId);
    }

    /**
     * Check if the current user is assigned the super-wbci role.
     *
     * @param akUserService the user service required for accessing information on current user
     * @return true if the user is a super wbci user, otherwise false
     * @throws AKAuthenticationException
     */
    public static boolean isSuperWbciUser(AKUserService akUserService) throws AKAuthenticationException {
        List<AKRole> userRoles = getUserRoles(akUserService);
        return isRoleInList(userRoles, WbciConstants.WBCI_SUPER_USER_ROLE);
    }

}
