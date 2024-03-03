/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.05.2004 07:53:56
 */
package de.augustakom.authentication.model;


/**
 * Modell fuer die Abbildung von Account-Daten fuer andere Datenbanken. <br>
 *
 *
 */
public class AKAccount extends AbstractAuthenticationModel {

    private Long id = null;
    private Long applicationId = null;
    private Long dbId = null;
    private String name = null;
    private String accountUser = null;
    private String accountPassword = null;
    private Integer maxActive = null;
    private Integer maxIdle = null;
    private String description = null;

    /**
     * Standardkonstruktor.
     */
    public AKAccount() {
        super();
    }

    /**
     * Konstruktor mit Angabe aller Eigenschaften.
     *
     * @param id              ID des Accounts
     * @param dbId            ID der Datenbank, zu der der Account gehoert.
     * @param appId           ID der Applikation, fuer die der Account bestimmt ist.
     * @param accName         Name des Accounts.
     * @param accountUser     Benutzername fuer die Datenbank
     * @param accountPassword Passwort (verschluesselt) fuer die Datenbank
     * @param maxActive       Anzahl maximal aktiver Sessions
     * @param maxIdle         Anzahl maximaler Sessions im Warte-Zustand
     * @param description     Beschreibung fuer den Account
     */
    public AKAccount(Long id, Long dbId, Long appId, String accName,
            String accountUser, String accountPassword, Integer maxActive, Integer maxIdle, String description) {
        super();
        setId(id);
        setDbId(dbId);
        setApplicationId(appId);
        setName(accName);
        setAccountUser(accountUser);
        setAccountPassword(accountPassword);
        setMaxActive(maxActive);
        setMaxIdle(maxIdle);
        setDescription(description);
    }

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
     * Gibt einen Benutzernamen zurueck, der fuer die Datenquelle verwendet werden soll.
     *
     * @return Returns the accountUser.
     */
    public String getAccountUser() {
        return accountUser;
    }

    /**
     * @param accountUser The accountUser to set.
     */
    public void setAccountUser(String dbAccountName) {
        this.accountUser = dbAccountName;
    }

    /**
     * Gibt ein Benutzerpasswort zurueck, das fuer die Datenquelle verwendet werden soll.
     *
     * @return Returns the accountPassword.
     */
    public String getAccountPassword() {
        return accountPassword;
    }

    /**
     * @param accountPassword The accountPassword to set.
     */
    public void setAccountPassword(String dbAccountPassword) {
        this.accountPassword = dbAccountPassword;
    }

    /**
     * Gibt die Anzahl der maximal zulaessigen Connections fuer den Account an. <br> Negativer Werte fuer unendlich
     * (siehe Apache BasicDataSource).
     *
     * @return Returns the maxActive.
     */
    public Integer getMaxActive() {
        return maxActive;
    }

    /**
     * @param maxActive The maxActive to set.
     */
    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    /**
     * Gibt die Anzahl der maximal zulaessigen Connection im Warte-Zustand fuer den Account an. <br> Negativer Werte
     * fuer unendlich (siehe Apache BasicDataSource).
     *
     * @return Returns the maxIdle.
     */
    public Integer getMaxIdle() {
        return maxIdle;
    }

    /**
     * @param maxIdle The maxIdle to set.
     */
    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    /**
     * Gibt einen logischen Namen fuer die Datenquelle zurueck. <br> Anhand dieses Namens koennen die Clients die URLs
     * und Treiber fuer die Datenquellen auswerten.
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen der Datenbank, fuer die der Account bestimmt ist.  <br> <strong>Der Name ist nicht der physische
     * DB-Name!</strong><br> Es ist lediglich ein Name, den die verwendende Applikation auswerten und auf eine DB-URL
     * mappen muss.
     *
     * @param name The name to set.
     */
    public void setName(String dbName) {
        this.name = dbName;
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
     * @return Returns the dbId.
     */
    public Long getDbId() {
        return dbId;
    }

    /**
     * @param dbId The dbId to set.
     */
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

}
