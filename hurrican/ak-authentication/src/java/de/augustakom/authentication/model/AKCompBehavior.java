/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2004 16:01:23
 */
package de.augustakom.authentication.model;


/**
 * Modell bildet das Verhalten einer GUI-Komponente fuer eine best. Rolle ab.
 *
 *
 */
public class AKCompBehavior extends AbstractAuthenticationModel {

    private Long id = null;
    private Long componentId = null;
    private Long roleId = null;
    private boolean visible = true;
    private boolean executable = false;

    /**
     * @return Returns the componentId.
     */
    public Long getComponentId() {
        return componentId;
    }

    /**
     * @param componentId The componentId to set.
     */
    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    /**
     * @return Returns the executable.
     */
    public boolean isExecutable() {
        return executable;
    }

    /**
     * @param executable The executable to set.
     */
    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

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
     * @return Returns the visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible The visible to set.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}


