/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2004 14:40:06
 */
package de.augustakom.authentication.model;


/**
 * Modell, um eine GUI-Komponente abzubilden.
 *
 *
 */
public class AKGUIComponent extends AbstractAuthenticationModel {

    private Long id = null;
    private String name = null;
    private String parent = null;
    private String type = null;
    private String description = null;
    private Long applicationId = null;

    /**
     * @return Returns the applicationId.
     */
    public Long getApplicationId() {
        return applicationId;
    }

    /**
     * @param applicationId The applicationId to set.
     */
    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the parent.
     */
    public String getParent() {
        return parent;
    }

    /**
     * @param parent The parent to set.
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }
}


