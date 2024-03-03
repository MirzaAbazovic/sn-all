/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2005 11:12:24
 */
package de.augustakom.authentication.model;

import java.io.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanNameAware;

import de.augustakom.common.AKDefaultConstants;

/**
 * Modell zur Abbilung eines Authentication-Systems. <br> In dem Modell ist ein Name fuer das System sowie die Angaben
 * fuer die DB-Connection enthalten.
 */
public class AuthenticationSystem implements Serializable, BeanNameAware {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationSystem.class);

    private static final String JDBC_DEFAULT_TIMEOUT = "db.pool.default.timeout";
    private static final String JDBC_MAX_CONN_LIFETIME_MILLIS = "db.pool.default.maxConnLifetimeMillis";
    private static final String JDBC_MAX_IDLE = "max.idle";
    private static final String JDBC_MAX_ACTIVE = "max.active";
    private static final String JDBC_INITIAL_SIZE = "initial.size";

    private static final String DEFAULT_JDBC_DEFAULT_TIMEOUT = "15000";
    private static final String DEFAULT_MAX_CONN_LIFETIME_MILLIS = "1800000";
    private static final String DEFAULT_JDBC_MAX_IDLE = "1";
    private static final String DEFAULT_JDBC_MAX_ACTIVE = "5";
    private static final String DEFAULT_JDBC_INITIAL_SIZE = "0";

    private String beanName = null;
    private String name = null;
    private String driver = null;
    private String url = null;
    private String schema = null;
    private String user = null;
    private String password = null;
    private String displayName = null;
    private String hibernateDialect = null;

    /**
     * Helper function to store system property with default value and log value out
     * @param sysProp, system property name
     * @param defaultValue, default value to set
     */
    protected static void setDefaultSystemProperty(String sysProp, String defaultValue){
        if (StringUtils.isBlank(System.getProperty(sysProp))) {
            System.setProperty(sysProp,defaultValue);
        }
        LOGGER.info("System property "+sysProp+"="+ System.getProperty(sysProp));
    }


    /**
     * Speichert die Bean-Properties 'user' und 'password' in den System-Properties. <br> Name der System-Properties:
     * propertyPrefix+beanProperty
     */
    public void saveUserAndPassword2SystemProperties(String propertyPrefix) {
        System.setProperty(propertyPrefix + "user", getUser());
        System.setProperty(propertyPrefix + "password", getPassword());
    }

    /**
     * Speichert die Bean-Properties 'driver' und 'url' in den System-Properties. <br> Name der System-Properties:
     * propertyPrefix+beanProperty
     */
    public void saveDriverAndUrl2SystemProperties(String propertyPrefix) {
        System.setProperty(propertyPrefix + "driver", getDriver());
        System.setProperty(propertyPrefix + "url", getUrl());
        System.setProperty(propertyPrefix + "schema", getSchema());
    }

    /**
     * Ueberprueft, ob in den System-Properties Angaben fuer folgende JDBC-Properties hinterlegt sind: <ul>
     * <li>initialSize <li>maxActive <li>maxIdle <li>dbPoolDefaultTimeout </ul> Falls fuer eines der angegebenen
     * JDBC-Property kein Wert in den System-Properties eingetragen ist, wird ein Default-Wert angegeben.
     */
    public void loadDefaultConnectionValues2SystemProperties(String propertyPrefix) {
        String initialSize = propertyPrefix + JDBC_INITIAL_SIZE;
        setDefaultSystemProperty(initialSize,DEFAULT_JDBC_INITIAL_SIZE);

        String maxConnectionLifeTime = propertyPrefix + JDBC_MAX_CONN_LIFETIME_MILLIS;
        setDefaultSystemProperty(maxConnectionLifeTime,DEFAULT_MAX_CONN_LIFETIME_MILLIS);

        String maxActive = propertyPrefix + JDBC_MAX_ACTIVE;
        setDefaultSystemProperty(maxActive,DEFAULT_JDBC_MAX_ACTIVE);

        String maxIdle = propertyPrefix + JDBC_MAX_IDLE;
        setDefaultSystemProperty(maxIdle,DEFAULT_JDBC_MAX_IDLE);

        String defaultTimeout = JDBC_DEFAULT_TIMEOUT;
        setDefaultSystemProperty(defaultTimeout,DEFAULT_JDBC_DEFAULT_TIMEOUT);
    }

    /**
     * Speichert das Property 'hibernateDialect' in die SystemProperties. <br> Name des System-Properties:
     * propertyPrefix+beanProperty
     */
    public void saveHibernateDialect2SystemProperties(String propertyPrefix) {
        System.setProperty(propertyPrefix + AKDefaultConstants.HIBERNATE_DIALECT_SUFFIX, getHibernateDialect());
    }

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * Gibt den Namen der Bean zurueck.
     */
    public String getBeanName() {
        return beanName;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDisplayName() {
        return (StringUtils.isBlank(displayName)) ? getName() : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHibernateDialect() {
        return this.hibernateDialect;
    }

    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }

    /**
     * @return the schema, possibly the empty string, never {@code null}
     */
    public String getSchema() {
        if (StringUtils.isBlank(schema)) {
            return "";
        }
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
