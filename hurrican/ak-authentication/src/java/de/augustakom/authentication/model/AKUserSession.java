/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 12:09:23
 */
package de.augustakom.authentication.model;

import java.util.*;


/**
 * Modell enthaelt Daten fuer eine einzelne Session. <br> (Eine Session entsteht immer dann, wenn sich ein Benutzer
 * einloggt. Sie ist jedoch immer nur eine gewisse Zeit lang gueltig.)
 *
 *
 */
public class AKUserSession extends AbstractAuthenticationModel {

    private Long sessionId = null;
    private Long userId = null;
    private Date loginTime = null;
    private Date deprecationTime = null;
    private String hostUser = null;
    private String hostName = null;
    private String ipAddress = null;
    private Long applicationId = null;
    private String applicationVersion = null;

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
     * @return Returns the deprecationTime.
     */
    public Date getDeprecationTime() {
        return deprecationTime == null ? null : new Date(deprecationTime.getTime());
    }

    /**
     * @param deprecationTime The deprecationTime to set.
     */
    public void setDeprecationTime(Date deprecationTime) {
        this.deprecationTime = deprecationTime == null ? null : new Date(deprecationTime.getTime());
    }

    /**
     * @return Returns the hostName.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName The hostName to set.
     */
    public void setHostName(String host) {
        this.hostName = host;
    }

    /**
     * @return Returns the ipAddress.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress The ipAddress to set.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @return Returns the loginTime.
     */
    public Date getLoginTime() {
        return loginTime == null ? null : new Date(loginTime.getTime());
    }

    /**
     * @param loginTime The loginTime to set.
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime == null ? null : new Date(loginTime.getTime());
    }

    /**
     * @return Returns the sessionId.
     */
    public Long getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId The sessionId to set.
     */
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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

    /**
     * @return Returns the applicationVersion.
     */
    public String getApplicationVersion() {
        return applicationVersion;
    }

    /**
     * @param applicationVersion The applicationVersion to set.
     */
    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    /**
     * @return Returns the hostUser.
     */
    public String getHostUser() {
        return hostUser;
    }

    /**
     * @param hostUser The hostUser to set.
     */
    public void setHostUser(String hostUser) {
        this.hostUser = hostUser;
    }
}
