/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 16:13:41
 */
package de.augustakom.authentication.gui.tree;

import java.util.*;
import javax.annotation.*;

import de.augustakom.authentication.gui.exceptions.TreeException;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * TreeService-Implementierung fuer ein AKDepartment-Objekt.
 */
public class DepartmentTreeService extends AbstractTreeService<AKUser, AKDepartment> {

    private static final long serialVersionUID = -7353479842787479591L;
    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    private AKUserService userService;

    @Override
    public List<AKUser> getChildren(Object parent, Object filter) throws TreeException {
        try {
            List<AKUser> users = userService.findByDepartment(((AKDepartment) parent).getId());

            List<AKUser> result = new ArrayList<>();
            if (users != null) {
                for (int i = 0; i < users.size(); i++) {
                    AKUser user = users.get(i);
                    if (matchFilter(user, filter)) {
                        result.add(user);
                    }
                }
            }

            return result;
        }
        catch (Exception e) {
            throw new TreeException(TreeException._UNKNOWN, e);
        }
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, AKDepartment data) throws TreeException {
        nodeToFill.setText(data.getName());
        nodeToFill.setTooltip(data.getDescription());
        nodeToFill.setIconName("de/augustakom/authentication/gui/images/department.gif");
        nodeToFill.setUserObject(data);
    }

    /*
     * Ueberprueft, ob das AKUser-Objekt den Filter-Parametern
     * entspricht. Ist dies der Fall, wird <code>true</code>
     * zurueck gegeben.
     * @param user zu ueberpruefendes AKUser-Objekt
     * @param filter Filter-Objekt vom Typ AdminTreeFilter, mit dem
     * das AKUser-Objekt geprueft werden soll
     * @return true: Objekt stimmt mit Filter ueberein.
     */
    private boolean matchFilter(AKUser user, Object filter) throws AKAuthenticationException {
        if (filter instanceof UserFilter) {
            UserFilter tf = (UserFilter) filter;

            if ((tf.getRoleId() != null) && (!hasRole(user.getId(), tf.getRoleId()))) {
                return false;
            }

            if ((tf.getAccountId() != null) && (!hasAccount(user.getId(), tf.getAccountId()))) {
                return false;
            }
        }

        return true;
    }

    /*
     * Ueberprueft, ob dem User mit der ID <code>userId</code>
     * die Rolle mit der ID <code>roleId</code> zugeordnet ist.
     * @param userId ID des Users
     * @param roleId ID der Rolle, nach der gesucht wird.
     * @return true: Benutzer besitzt die Rolle
     * @throws TreeException
     * @throws AKAuthenticationException
     */
    private boolean hasRole(Long userId, Long roleId) throws AKAuthenticationException {
        List<AKRole> roles = userService.getRoles(userId);
        if (roles != null) {
            if (roleId.equals(Long.MIN_VALUE)) {
                return (roles.isEmpty());
            }

            for (int i = 0; i < roles.size(); i++) {
                AKRole role = roles.get(i);
                if (roleId.equals(role.getId())) {
                    return true;
                }
            }
        }

        return (roleId.equals(Long.MIN_VALUE));
    }

    /*
     * Ueberprueft, ob dem User mit der ID <code>userId</code>
     * der Account mit der ID <code>accountId</code> zugeordnet ist.
     * @param userId ID des Users
     * @param accountId ID des Accounts, nach dem gesucht wird.
     * @return true: Benutzer besitzt den Account.
     * @throws TreeException
     * @throws AKAuthenticationException
     */
    private boolean hasAccount(Long userId, Long accountId) throws AKAuthenticationException {
        List<AKAccount> accounts = userService.getDBAccounts(userId);
        if (accounts != null) {
            if (accountId.equals(Long.MIN_VALUE)) {
                return (accounts.isEmpty());
            }

            for (int i = 0; i < accounts.size(); i++) {
                AKAccount acc = accounts.get(i);
                if (accountId.equals(acc.getId())) {
                    return true;
                }
            }
        }

        return (accountId.equals(Long.MIN_VALUE));
    }
}
