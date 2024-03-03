/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2005 11:05:06
 */
package de.augustakom.common.tools.dao.jdbc;

import java.sql.*;
import javax.sql.*;
import org.apache.log4j.Logger;


/**
 * Hilfsklasse fuer JDBC-Operationen.
 *
 *
 */
public class JdbcHelper {

    private static final Logger LOGGER = Logger.getLogger(JdbcHelper.class);

    /**
     * Schliesst die Connection der DataSource.
     *
     * @param dataSource
     */
    public static void closeQuiet(DataSource dataSource) {
        try {
            if (dataSource != null) {
                Connection connection = dataSource.getConnection();
                if (connection != null) {
                    connection.close();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Schliesst das ResultSet und das Statement. Evtl auftretende Exceptions werden unterdrueckt.
     *
     * @param rs
     * @param stmt
     *
     */
    public static void closeQuiet(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die Groesse des ResultSets.
     *
     * @param rs             ResultSet dessen Groesse ermittelt werden soll
     * @param setBeforeFirst Flag, ob das ResultSet nach dem Zaehl-Vorgang wieder vor den ersten Datensatz gestellt
     *                       werden soll.
     * @return Groesse des ResultSets oder -1 falls <code>null</code>
     *
     */
    public static int getSizeOfResultSet(ResultSet rs, boolean setBeforeFirst) throws SQLException {
        if (rs != null) {
            int count = 0;
            while (rs.next()) {
                count++;
            }

            if (setBeforeFirst) {
                rs.beforeFirst();
            }

            return count;
        }
        return -1;
    }
}


