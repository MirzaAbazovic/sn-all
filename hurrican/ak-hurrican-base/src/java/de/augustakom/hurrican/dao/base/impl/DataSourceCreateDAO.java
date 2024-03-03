/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2005 10:32:47
 */
package de.augustakom.hurrican.dao.base.impl;

import javax.sql.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Abstrakte Klasse fuer DAOs, die die DataSource selbst erstellen.
 *
 *
 */
public abstract class DataSourceCreateDAO {

    private String dbDriver = null;
    private String dbUrl = null;
    private String dbUser = null;
    private String dbPassword = null;

    protected DataSource dataSource = null;

    /**
     * Erzeugt die DataSource mit den gesetzten Parametern.
     */
    protected void createDataSource() {
        if (dataSource == null) {
            dataSource = new BasicDataSource();
            ((BasicDataSource) dataSource).setDriverClassName(getDbDriver());
            ((BasicDataSource) dataSource).setUrl(getDbUrl());
            ((BasicDataSource) dataSource).setUsername(getDbUser());
            ((BasicDataSource) dataSource).setPassword(getDbPassword());
        }
    }

    /**
     * Erzeugt eine Instanz von <code>org.springframework.jdbc.core.JdbcTemplate</code>.
     */
    protected JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    /**
     * @return Returns the dbDriver.
     */
    public String getDbDriver() {
        return dbDriver;
    }

    /**
     * @param dbDriver The dbDriver to set.
     */
    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    /**
     * @return Returns the dbPassword.
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * @param dbPassword The dbPassword to set.
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    /**
     * @return Returns the dbUrl.
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * @param dbUrl The dbUrl to set.
     */
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * @return Returns the dbUser.
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * @param dbUser The dbUser to set.
     */
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

}


