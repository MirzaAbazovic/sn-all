/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 16:51:12
 */
package de.mnet.common.service.status;

import javax.sql.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Prueft die Verbindung zu einer bestimmenten SQL-Datasource vorhanden ist.
 */
public class DataSourceStatusService implements ApplicationStatusService {

    private static final Logger LOGGER = Logger.getLogger(DataSourceStatusService.class);

    @Autowired
    private ApplicationContext applicationContext;

    private String dataSourceName;
    private String tableName;

    @Override
    public ApplicationStatusResult getStatus() {
        ApplicationStatusResult status = new ApplicationStatusResult();
        try {
            DataSource dataSource = applicationContext.getBean(dataSourceName, DataSource.class);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            int size = jdbcTemplate.queryForInt("select count(*) from " + tableName + " where rownum <= 1");
            if (size == 0) {
                status.addError("DataSource " + dataSourceName + ": No entries in " + tableName + "!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            status.addError("Exception thrown when checking db-connection for dataSource " + dataSourceName + ": "
                    + e.getMessage());
        }
        return status;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getStatusName() {
        return "Checking DataSource " + dataSourceName;
    }

}
