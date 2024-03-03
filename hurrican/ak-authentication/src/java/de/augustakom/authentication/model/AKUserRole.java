/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2004 11:11:14
 */
package de.augustakom.authentication.model;


/**
 * Modell zur Abbildung einer Verbindung zwischen einem AKUser- und einem AKRole-Objekt.
 *
 *
 */
public class AKUserRole extends AbstractAuthenticationModel {

    private Long id = null;
    private Long userId = null;
    private Long roleId = null;

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
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

    /**
     * @return Returns the userId.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId The userId to set.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
