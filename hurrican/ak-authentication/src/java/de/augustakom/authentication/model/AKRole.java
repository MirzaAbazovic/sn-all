/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 12:02:26
 */
package de.augustakom.authentication.model;


/**
 * Abbildung einer Rolle. <br>
 *
 *
 */
public class AKRole extends AbstractAuthenticationModel implements Comparable<AKRole> {

    private Long id = null;
    private String name = null;
    private String description = null;
    private Long applicationId = null;
    private boolean admin = false;

    /**
     * Standardkonstruktor
     */
    public AKRole() {
        super();
    }

    /**
     * Konstruktor (ohne Angabe der Rollen-Description).
     *
     * @param id
     * @param name
     * @param appId
     * @param admin
     */
    public AKRole(Long id, String name, Long appId, boolean admin) {
        super();
        setId(id);
        setName(name);
        setApplicationId(appId);
        setAdmin(admin);
    }

    /**
     * Konstruktor mit Angabe aller Daten.
     *
     * @param id    ID der Rolle
     * @param name  Name der Rolle
     * @param desc  Beschreibung der Rolle
     * @param appId ID der Applikation, fuer die die Rolle bestimmt ist.
     * @param admin Angabe, ob die Rolle Admin-Rechte besitzt
     */
    public AKRole(Long id, String name, String desc, Long appId, boolean admin) {
        super();
        setId(id);
        setName(name);
        setDescription(desc);
        setApplicationId(appId);
        setAdmin(admin);
    }

    /**
     * @return Gibt die ID der Applikation zurueck, fuer die die Rolle gueltig ist.
     */
    public Long getApplicationId() {
        return applicationId;
    }

    /**
     * @param applicationId Setzt ID der Applikation, fuer die die Rolle gueltig ist.
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
     * @return Returns the admin.
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * @param admin The admin to set.
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


    @Override
    public int compareTo(AKRole akRole) {
        return this.name.compareToIgnoreCase(akRole.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AKRole))
            return false;

        AKRole akRole = (AKRole) o;

        if (admin != akRole.admin)
            return false;
        if (id != null ? !id.equals(akRole.id) : akRole.id != null)
            return false;
        if (name != null ? !name.equals(akRole.name) : akRole.name != null)
            return false;
        if (description != null ? !description.equals(akRole.description) : akRole.description != null)
            return false;
        return !(applicationId != null ? !applicationId.equals(akRole.applicationId) : akRole.applicationId != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (applicationId != null ? applicationId.hashCode() : 0);
        result = 31 * result + (admin ? 1 : 0);
        return result;
    }
}
