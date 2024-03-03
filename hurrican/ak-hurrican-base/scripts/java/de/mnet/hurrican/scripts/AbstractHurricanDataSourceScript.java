/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2006 11:33:08
 */
package de.mnet.hurrican.scripts;

import java.sql.*;
import java.util.*;
import javax.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;


/**
 * Abstrakte Script-Klasse fuer Scripte, die eine eigene/spezielle DataSource benoetigen.
 *
 *
 */
public abstract class AbstractHurricanDataSourceScript extends AbstractHurricanScript {

    private Map<String, DataSource> dataSourceMap = null;


    public AbstractHurricanDataSourceScript() {
        super();
    }

    public AbstractHurricanDataSourceScript(String arg0) {
        super(arg0);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        init();
    }

    protected void setUpDSOnly() throws Exception {
        configureLogging();
        init();
    }

    /* Initialisiert die Klasse */
    protected void init() {
        dataSourceMap = new HashMap();
    }

    /**
     * Erzeugt ein JdbcTemplate. <br> Als DataSource wird die DataSource mit dem Namen 'dsName' verwendet.
     *
     *
     */
    protected JdbcTemplate getJdbcTemplate(String dsName) {
        DataSource ds = dataSourceMap.get(dsName);
        return new JdbcTemplate(ds);
    }

    /**
     * Erzeugt eine DataSource mit den angegebenen Daten. AutoCommit=true
     *
     * @param name   Name, um die DataSource wieder zu referenzieren.
     * @param driver Driver-Klasse
     * @param url    URL fuer die DB
     * @param user   User-Name fuer die Connection
     * @param pw     Passwort fuer die Connection
     *
     */
    protected void createDataSource(String name, Class driver, String url, String user, String pw) {
        createDataSource(name, driver, url, user, pw, true);
    }

    /**
     * @param name       Name, um die DataSource wieder zu referenzieren.
     * @param driver     Driver-Klasse
     * @param url        URL fuer die DB
     * @param user       User-Name fuer die Connection
     * @param pw         Passwort fuer die Connection
     * @param autoCommit Definition des AutoCommit-Params
     *
     * @see createDataSource
     */
    protected void createDataSource(String name, Class driver, String url, String user, String pw, boolean autoCommit) {
        SingleConnectionDataSource scds = new SingleConnectionDataSource(
                driver.getName(), url, user, pw, false);
        scds.setAutoCommit(autoCommit);
        dataSourceMap.put(name, scds);
    }

    /**
     * Erzeugt eine DataSource mit den angegebenen Daten.
     *
     * @param name   Name, um die DataSource wieder zu referenzieren.
     * @param driver Driver-Klasse
     * @param url    URL fuer die DB
     * @param user   User-Name fuer die Connection
     * @param pw     Passwort fuer die Connection
     *
     */
    protected void createDataSource(String name, String driver, String url, String user, String pw) {
        SingleConnectionDataSource scds = new SingleConnectionDataSource(
                driver, url, user, pw, false);
        dataSourceMap.put(name, scds);
    }

    /**
     * Versucht, die DataSource mit dem Namen 'name' zu schliessen.
     *
     *
     */
    protected void closeDataSource(String name) {
        try {
            DataSource ds = dataSourceMap.get(name);
            if (ds != null) {
                DataSourceUtils.releaseConnection(ds.getConnection(), ds);
            }
        }
        catch (SQLException e) {
            new SQLErrorCodeSQLExceptionTranslator()
                    .translate("Closing connection", null, e);
        }
    }

}
