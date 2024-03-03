/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2004 12:34:30
 */
package de.augustakom.authentication.gui.tree;


/**
 * Filter-Objekt fuer den Tree, um AKUser-Objekte zu filtern. <br>
 */
public class UserFilter {

    public Long roleId = null;
    public Long accountId = null;

    /**
     * Gibt die Account-ID zurueck, nach der gefiltert werden soll. <br> Der Wert Long.MIN_VALUE bedeutet, dass nur nach
     * Benutzern gesucht werden soll, denen <strong>kein</strong> Account zugeordnet ist.
     *
     * @return Returns the accountId.
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId The accountId to set.
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * Gibt die Role-ID zurueck, nach der gefiltert werden soll. <br> Der Wert Long.MIN_VALUE bedeutet, dass nur nach
     * den Benutzern gesucht werden soll, denen <strong>keine</strong> Rolle zugeordnet ist.
     *
     * @return Returns the roleId.
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId The roleId to set.
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
