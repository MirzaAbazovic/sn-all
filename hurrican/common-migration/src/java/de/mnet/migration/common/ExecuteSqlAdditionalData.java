/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2010 15:27:48
 */

package de.mnet.migration.common;

import java.util.*;
import javax.sql.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 * Fuehrt ein beliebiges SQL-Statement aus. <br> Es sollte sich hier nur um DDLs handeln (also z.B. ForeignKeys).
 * <p/>
 * Achtung: Diese Klasse ist nich zum Laden zusätzlicher Daten.
 */
public class ExecuteSqlAdditionalData implements MigrationAdditionalData {

    private static final Logger LOGGER = Logger.getLogger(ExecuteSqlAdditionalData.class);

    DataSource dataSource;
    String sql;

    @Override
    public List<TransformationResult> call() throws Exception {
        List<TransformationResult> result = new ArrayList<TransformationResult>();
        try {
            new JdbcTemplate(dataSource).execute(sql);
        }
        catch (DataAccessException e) {
            LOGGER.warn("call() - Fehler bei SQL-Execute: " + sql);
            LOGGER.error(e);
            result.add(new TransformationResult(TransformationStatus.WARNING, null, null,
                    "SQL " + sql + " konnte nicht ausgefuehrt werden", 0L, "EXECUTE_SQL", e));
        }
        return result;
    }

    @Required
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
