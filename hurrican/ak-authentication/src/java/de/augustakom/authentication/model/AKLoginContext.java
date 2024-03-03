/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 12:56:12
 */
package de.augustakom.authentication.model;

import java.util.*;


/**
 * Modell fuer den Login-Vorgang. <br> In dieses Modell muessen von der Client-Applikation alle Daten eingetragen
 * werden, die fuer den Login notwendig sind. <br> Folgende Angaben muessen unbedingt vorhanden sein: <ul>
 * <li>Benutzername <li>Passwort (nicht verschluesselt!) <li>Applikationsname </ul> Folende Daten sind optional: <ul>
 * <li>Host-Name <li>IP-Adresse </ul>
 * <p/>
 * <br><br> Die Parameter <code>sessionId</code> und <code>user</code> werden vom Authentication-Service in das Objekt
 * geschrieben. Dadurch erhaelt die Client-Applikation alle notwendigen Daten ueber den Benutzer.
 *
 *
 */
public class AKLoginContext extends AbstractAuthenticationModel {

    private String userName = null;
    private String password = null;
    private String applicationName = null;
    private String applicationVersion = null;
    private String hostUser = null;
    private String hostName = null;
    private String ipAddress = null;
    private AKUser user = null;
    private AKApplication application = null;
    private AKUserSession userSession = null;
    private List<AKAccount> accounts = null;
    private List<AKRole> roles = null;
    private Map<Long, AKDb> databases = null;
    private boolean noDeprecation = false;

    /**
     * @return Returns the applicationName.
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName The applicationName to set.
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the userName.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName The userName to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
    public void setHostName(String hostName) {
        this.hostName = hostName;
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
     * @return Returns the user.
     */
    public AKUser getUser() {
        return user;
    }

    /**
     * @param user The user to set.
     */
    public void setUser(AKUser user) {
        this.user = user;
    }

    /**
     * @return Returns the application.
     */
    public AKApplication getApplication() {
        return application;
    }

    /**
     * @param application The application to set.
     */
    public void setApplication(AKApplication application) {
        this.application = application;
    }

    /**
     * @return Returns the userSession.
     */
    public AKUserSession getUserSession() {
        return userSession;
    }

    /**
     * @param userSession The userSession to set.
     */
    public void setUserSession(AKUserSession userSession) {
        this.userSession = userSession;
    }

    /**
     * Gibt eine Liste mit allen DB-Accounts fuer den angemeldeten Benutzer bei der gewaehlten Applikation zurueck.
     *
     * @return Returns the accounts (List of AKAccount Objects).
     */
    public List<AKAccount> getAccounts() {
        return accounts;
    }

    /**
     * @param accounts The accounts to set.
     */
    public void setAccounts(List<AKAccount> accounts) {
        this.accounts = accounts;
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

    /**
     * @return Returns the roles.
     */
    public List<AKRole> getRoles() {
        return roles;
    }

    /**
     * @param roles The roles to set.
     */
    public void setRoles(List<AKRole> roles) {
        this.roles = roles;
    }

    /**
     * Gibt eine Map mit allen verfuegbaren Datenbanken zurueck. Als Key wird die ID der Datenbank verwendet, als Value
     * das entsprechende AKDb-Objekt.
     *
     * @return Returns the databases.
     */
    public Map<Long, AKDb> getDatabases() {
        return databases;
    }

    /**
     * @param databases The databases to set.
     */
    public void setDatabases(Map<Long, AKDb> databases) {
        this.databases = databases;
    }

    /**
     * @return Returns the noDeprecation.
     */
    public boolean isNoDeprecation() {
        return noDeprecation;
    }

    /**
     * Kann auf 'true' gesetzt werden, damit die UserSession keine Deprecation-Time erhaelt. <br> Darf nur fuer
     * Server-Prozesse gesetzt werden!
     *
     * @param noDeprecation The noDeprecation to set.
     */
    public void setNoDeprecation(boolean noDeprecation) {
        this.noDeprecation = noDeprecation;
    }
}
