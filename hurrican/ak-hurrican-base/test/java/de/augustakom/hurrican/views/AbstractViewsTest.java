/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2011 14:24:30
 */
package de.augustakom.hurrican.views;

import static org.testng.Assert.*;

import java.util.*;
import javax.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.dao.jdbc.AKBasicDataSource;
import de.augustakom.common.tools.lang.PropertyUtil;

/**
 * Basisklasse für View-Tests Es wird unterschieden zwischen Testfällen, die die Anzahl der Zeilen pro View testen bzw.
 * ob eine View Ergebnisse zurückliefert und Testfällen die die Struktur einer View prüfen. Zum einen hinsichtlich dem
 * Vorhandensein der erwarteteten Spalten, zum anderen hinsichtlich der Abdeckung aller tatsächlich existierenden
 * Spalten im Testfall.
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public abstract class AbstractViewsTest extends BaseTest {

    @BeforeGroups(groups = BaseTest.VIEW)
    public static void setUp() {
        Properties properties = PropertyUtil.loadPropertyHierarchy("common", "properties", false);
        AKBasicDataSource ds = new AKBasicDataSource();
        ds.setDriverClassName(properties.getProperty("oracle.jdbc.driver.OracleDriver"));
        ds.setUrl(properties.getProperty("db.hurrican.jdbc.url"));
        ds.setUsername(properties.getProperty("db.hurrican.user"));
        ds.setPassword(properties.getProperty("db.hurrican.password"));
        dataSource = ds;
    }

    private static DataSource dataSource;

    private static final String VIEWNAME_PLACEHOLDER = "${VIEW}";
    private static final String SQL_SELECT_ROW_COUNT = "select count(*) from " + VIEWNAME_PLACEHOLDER
            + " where rownum=1";

    private static final String SQL_SELECT_ALL_COLUMNS_FOR_VIEW = "SELECT column_name from user_tab_columns WHERE table_name='"
            + VIEWNAME_PLACEHOLDER + "' order by column_name";

    protected String createSelectRowCountSQL(String view) {
        return SQL_SELECT_ROW_COUNT.replace(VIEWNAME_PLACEHOLDER, view);
    }

    protected String createSelectAllColumnsForViewSql(String viewName) {
        return SQL_SELECT_ALL_COLUMNS_FOR_VIEW.replace(VIEWNAME_PLACEHOLDER, viewName);
    }

    protected JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    protected void checkColumnsForView(String viewName, String[] columns) {
        List<String> columnNames = getJdbcTemplate().queryForList(createSelectAllColumnsForViewSql(viewName),
                String.class);
        for (String column : columns) {
            assertTrue(columnNames.contains(column));
        }
    }
}
