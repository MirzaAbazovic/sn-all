/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2005 13:40:59
 */
package de.augustakom.common;

/**
 * Interface mit Standart-Konstanten.
 */
public interface AKDefaultConstants {

    /**
     * Suffix fuer ein System-Property zur Definition des Benutzernamens bei einer JDBC-Verbindung. <br> Der Prefix
     * koennte z.B. 'mistral' lauten.
     */
    public static final String JDBC_USER_SUFFIX = ".jdbc.user";

    /**
     * Suffix fuer ein System-Property zur Definition des Benutzerpassworts bei einer JDBC-Verbindung. <br> Der Prefix
     * koennte z.B. 'mistral' lauten.
     */
    public static final String JDBC_PASSWORD_SUFFIX = ".jdbc.password";

    /**
     * Suffix fuer ein System-Property zur Definition der DB-URL bei einer JDBC-Verbindung. <br>
     */
    public static final String JDBC_URL_SUFFIX = ".jdbc.url";

    /**
     * Suffix fuer ein System-Property zur Definition des DB-Drivers bei einer JDBC-Verbindung.
     */
    public static final String JDBC_DRIVER_SUFFIX = ".jdbc.driver";

    /**
     * Suffix fuer ein System-Property zur Definition des DB-Schemas bei einer JDBC-Verbindung.
     */
    public static final String JDBC_SCHEMA_SUFFIX = ".schema";

    /**
     * Suffix fuer ein System-Property zur Definition der maximal zulaessigen aktiven DB-Connections.
     */
    public static final String JDBC_MAX_ACTIVE_SUFFIX = ".jdbc.max.active";

    /**
     * Suffix fuer ein System-Property zur Definition der maximal zulaessigen wartenden DB-Connections.
     */
    public static final String JDBC_MAX_IDLE_SUFFIX = ".jdbc.max.idle";

    /**
     * Suffix fuer ein System-Property zur Definition des zu verwendenden Hibernate-Dialekts.
     */
    public static final String HIBERNATE_DIALECT_SUFFIX = ".hibernate.dialect";

}
