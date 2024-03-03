/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2005 09:46:41
 */
package de.augustakom.common.tools.dao.jdbc;

import java.sql.*;
import java.util.*;
import java.util.Date;
import org.apache.log4j.Logger;


/**
 * Hilfsklasse fuer die Arbeit mit einem java.sql.ResultSet.
 *
 *
 */
public class ResultSetHelper {
    private static final Logger LOGGER = Logger.getLogger(ResultSetHelper.class);

    /**
     * Ermittelt aus dem ResultSet <code>rs</code> den Wert aus der Spalte <code>column</code> und gibt diesen Wert als
     * String zurueck. <br> Wichtig: Alle Exceptions werden unterdrueckt!
     *
     * @param rs     Das Result-Set
     * @param column Name der Spalte
     * @return String
     */
    public static String getStringSilent(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        }
        catch (Exception e) {
            LOGGER.debug("getStringSilent() - got exception", e);
        }
        return null;
    }

    /**
     * @param rs
     * @param column
     * @return
     * @see getStringSilent(ResultSet, String)
     */
    public static Integer getIntegerSilent(ResultSet rs, String column) {
        try {
            return Integer.valueOf(rs.getInt(column));
        }
        catch (Exception e) {
            LOGGER.debug("getIntegerSilent() - got exception", e);
        }
        return null;
    }

    /**
     * @param rs
     * @param column
     * @return
     * @see getStringSilent(ResultSet, String)
     */
    public static Long getLongSilent(ResultSet rs, String column) {
        try {
            return Long.valueOf(rs.getLong(column));
        }
        catch (Exception e) {
            LOGGER.debug("getLongSilent() - got exception", e);
        }
        return null;
    }

    /**
     * @param rs
     * @param column
     * @return
     * @see getStringSilent(ResultSet, String)
     */
    public static Date getDateSilent(ResultSet rs, String column) {
        try {
            return rs.getDate(column);
        }
        catch (Exception e) {
            LOGGER.debug("getDateSilent() - got exception", e);
        }
        return null;
    }

    /**
     * @param rs
     * @param column
     * @return
     * @see getStringSilent(ResultSet, String)
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "method is only used in DAOs; return null necessary to read null values from the database!")
    public static Boolean getBooleanSilent(ResultSet rs, String column) {
        try {
            String raw = rs.getString(column);
            if ((raw == null) || (raw.length() <= 0) || raw.startsWith("0")) {
                return Boolean.FALSE;
            }

            return Boolean.TRUE;
        }
        catch (Exception e) {
            LOGGER.debug("getBooleanSilent() - got exception", e);
        }
        return null;
    }

    /**
     * Helper constructs proper result set with column names and row values as it is used in
     * Spring JDBC supported classes.
     * @param columnNames
     * @param result
     * @return
     */
    public static List<Map<String, Object>> getResultSet(String[] columnNames, List<Object[]> result) {
        List<Map<String, Object>> resultSet = new ArrayList<>();
        if (result != null) {
            for (Object[] rowValues : result) {
                if (columnNames == null) {
                    columnNames = new String[rowValues.length];
                    for (int i = 0; i < rowValues.length; i++) {
                        columnNames[i] = "COL" + (i + 1);
                    }
                }

                if (columnNames.length != rowValues.length) {
                    throw new IllegalArgumentException("SQL result set column mismatch - expected column set does not fit result value set!");
                }

                Map<String, Object> rowData = new LinkedHashMap<>();
                for (int i = 0; i < rowValues.length; i++) {
                    rowData.put(columnNames[i], rowValues[i]);
                }

                resultSet.add(rowData);
            }
        }

        return resultSet;
    }
}


