/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 11:57:34
 */
package de.augustakom.authentication.model;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;

/**
 * Abbildung eines Users. <br> Das Modell enthaelt div. Informationen ueber den User.
 *
 *
 */
public class AKUser extends AbstractAuthenticationModel {

    private static final long serialVersionUID = -1643929202861405976L;

    private Long id;
    public static final String LOGIN_NAME = "loginName";
    private String loginName;
    public static final String NAME = "name";
    private String name;
    public static final String FIRST_NAME = "firstName";
    private String firstName;
    private String email;
    private String phone;
    private String phoneNeutral;
    private String fax;
    public static final String DEPARTMENT_ID = "departmentId";
    private Long departmentId;
    public static final String ACTIVE = "active";
    private boolean active = true;
    private Long niederlassungId;
    private Long extServiceProviderId;
    /**
     * Angabe, ob der User Projektleiter eines Auftrag sein kann.
     */
    private Boolean projektleiter = false;
    /**
     * Angabe, ob der User ein 'Manager' (Bereichsleiter) ist.
     */
    private Boolean manager = false;
    private AKTeam team;

    @Nullable
    private AKBereich bereich;

    /**
     * Required by Hibernate
     */
    public AKUser() {
        super();
    }

    /**
     * Konstruktor mit Angabe aller User-Parameter.
     *
     * @param id           ID des Benutzers.
     * @param loginName    Loginname des Benutzers
     * @param name         Nachname des Benutzers
     * @param firstName    Vorname des Benutzers
     * @param email        EMail-Adresse des Benutzers
     * @param phone
     * @param phoneNeutral
     * @param fax
     * @param departmentId Abeilungs-ID des Benutzers
     * @param active       Angabe, ob der User aktiviert ist
     * @param bereich      optionale Angabe des Bereichs
     */
    public AKUser(Long id, String loginName, String name, String firstName, String email,
            String phone, String phoneNeutral, String fax, Long departmentId, boolean active, @Nullable AKBereich bereich) {
        super();
        this.id = id;
        this.loginName = loginName;
        this.name = name;
        this.firstName = firstName;
        this.email = email;
        this.phone = phone;
        this.phoneNeutral = phoneNeutral;
        this.fax = fax;
        this.departmentId = departmentId;
        this.active = active;
        this.bereich = bereich;
    }

    public boolean isHurricanWsUser() {
        return StringUtils.equals(getLoginName(), "hurrican-ws");
    }

    /**
     * Gibt den Namen und Vornamen in einem String zurueck.
     */
    public String getNameAndFirstName() {
        return StringUtils.join(new String[] { getName(), getFirstName() }, " ");
    }

    public String getFirstNameAndName() {
        return StringUtils.join(new String[] { getFirstName(), getName() }, " ");
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneNeutral() {
        return phoneNeutral;
    }

    public void setPhoneNeutral(String phoneNeutral) {
        this.phoneNeutral = phoneNeutral;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    public Long getExtServiceProviderId() {
        return extServiceProviderId;
    }

    public void setExtServiceProviderId(Long extServiceProviderId) {
        this.extServiceProviderId = extServiceProviderId;
    }

    public Boolean isProjektleiter() {
        return projektleiter;
    }

    public void setProjektleiter(Boolean projektleiter) {
        this.projektleiter = projektleiter;
    }

    public Boolean isManager() {
        return manager;
    }

    public void setManager(Boolean manager) {
        this.manager = manager;
    }

    public AKTeam getTeam() {
        return team;
    }

    public void setTeam(AKTeam team) {
        this.team = team;
    }

    @CheckForNull
    public AKBereich getBereich() {
        return bereich;
    }

    public void setBereich(@Nullable AKBereich bereich) {
        this.bereich = bereich;
    }
}
