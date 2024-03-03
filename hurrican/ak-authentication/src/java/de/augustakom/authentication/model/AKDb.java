/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.2005 11:39:05
 */
package de.augustakom.authentication.model;


/**
 * Modell fuer die Abbildung einer DB-Information. Der Parametername <code>name</code> ist nicht der physikalische Name
 * einer Datenbank sondern eine Art Alias-Name. Die Client-Applikation muss diesen Alias-Namen auswerten und mit den
 * zugehoerigen Account-Informationen die DB-Connection herstellen.
 *
 *
 */
public class AKDb extends AbstractAuthenticationModel {

    private Long id = null;
    private String name = null;
    private String driver = null;
    private String url = null;
    private String schema = null;
    private String hibernateDialect = null;
    private String description = null;

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
     * @return Returns the driver.
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver The driver to set.
     */
    public void setDriver(String driver) {
        this.driver = driver;
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
     * @return Returns the schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema The schema to set.
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return Returns the hibernateDialect.
     */
    public String getHibernateDialect() {
        return this.hibernateDialect;
    }

    /**
     * @param hibernateDialect The hibernateDialect to set.
     */
    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }

}


