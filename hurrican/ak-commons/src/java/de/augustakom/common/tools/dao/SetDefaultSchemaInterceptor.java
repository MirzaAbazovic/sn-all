/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2006 08:04:38
 */
package de.augustakom.common.tools.dao;

import java.sql.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.jdbc.JdbcHelper;

/**
 * Interceptor, um nach dem Aufbau einer Connection das Default-Schema der Connection zu setzen. <br> <br> Dem
 * Interceptor kann der zu verwendende Schema-Name und das notwendige SQL-Command zum Aendern des Schemas uebergeben
 * werden. <br> Bei jedem Call von <code>getConnection()</code> wird ueberprueft, ob auf der Connection das Schema
 * bereits geaendert wurde. Ist dies nicht der Fall, wird das Schema geaendert.
 *
 *
 */
public class SetDefaultSchemaInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = Logger.getLogger(SetDefaultSchemaInterceptor.class);

    /**
     * DB-spezifisches SQL-Command, um das Default-Schema fuer eine Connection zu aendern. <br> Bsp.: <br> Oracle:
     * <code>ALTER SESSION SET CURRENT_SCHEMA=</code> <br> DB2: <code>SET CURRENT SCHEMA=</code> <br>
     */
    private static final String CHANGE_SCHEMA_COMMAND = "ALTER SESSION SET CURRENT_SCHEMA=";

    private String schemaName4Connection;
    private String schemaName;

    @Override
    public Object invoke(MethodInvocation i) throws Throwable {
        Object result;
        try {
            result = i.proceed();
        } catch (SQLException t) {
            String url = "unknown";
            if(i.getThis() instanceof BasicDataSource) {
                url = ((BasicDataSource) i.getThis()).getUrl();
            }
            throw new SQLException("invoke failed on '" + i.getMethod().getName() + "' for schemaName=" + getSchemaName() + " url=" + url, t);
        }
        if (result instanceof Connection) {
            Connection con = (Connection) result;
            setSchemaOnConnection(con);
        }
        return result;
    }

    /**
     * Aendert das Default-Schema der DB-Connection.
     *
     * @param con Connection, deren Default-Schema geaendert werden soll.
     * @throws SQLException
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE", justification = "Required for flexibility")
    protected void setSchemaOnConnection(Connection con) throws SQLException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    String.format("Setting current schema to '%s' on connection %s", getSchemaName(), con));
        }

        if (StringUtils.isNotBlank(getSchemaName4Connection())) {
            Statement sqlStatement = con.createStatement();
            try {
                sqlStatement.execute(SetDefaultSchemaInterceptor.CHANGE_SCHEMA_COMMAND + getSchemaName());
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
                // keine Exceptions weiter reichen, da nicht jede DB ein Schema-Handling unterstuetzt
            }
            finally {
                JdbcHelper.closeQuiet(null, sqlStatement);
            }
        }
    }

    /**
     * Gibt den zu verwendenden Schema-Namen fuer die Connection zurueck.
     */
    private String getSchemaName4Connection() {
        if (schemaName4Connection == null) {
            schemaName4Connection = getSchemaName();
        }
        return schemaName4Connection;
    }

    /**
     * Setzt den Schema-Namen fuer die Connection.
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaName() {
        return schemaName;
    }

}
