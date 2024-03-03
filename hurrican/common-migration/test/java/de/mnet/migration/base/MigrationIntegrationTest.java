/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.2009 08:50:02
 */
package de.mnet.migration.base;


import static org.testng.Assert.*;

import javax.annotation.*;
import javax.sql.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;

import de.augustakom.common.InitializeLog4J;
import de.augustakom.common.tools.spring.PropertyBean;
import de.mnet.migration.common.result.MigrationResult;


/**
 * Basisklasse von der alle Integration Tests ableiten. Konfiguriert das Logging-Framework.
 * <p/>
 * <b>Achtung:</b>Es wird empfohlen die @BeforeMethod Annotation anstatt der @BeforeClass Annotation zu verwenden, falls
 * man Instanzen aus dem Spring Context benötigt. Dadurch wird sichergestellt, dass der Spring Context auch wirklich
 * initialisiert wurde
 */
@ContextConfiguration("/de/mnet/migration/common/resources/test-base-migration.xml")
public abstract class MigrationIntegrationTest extends AbstractTestNGSpringContextTests {
    protected static final String NO_SUITE = "<keine Suite angegeben>";
    @Autowired
    protected PropertyBean propertyBean;

    protected String migrationUser;
    protected String migrationRecSource;

    @BeforeSuite(alwaysRun = true)
    public void setupTests() {
        InitializeLog4J.initializeLog4J("log4j-test");
        // To make BeanUtils copy nulls as nulls!
        // Do not remove (except if you have a better idea where to put it)!
        BeanUtilsBean.getInstance().getConvertUtils().register(false, true, 0);
    }


    @PostConstruct
    public void setupMigrationUser() {
        migrationUser = propertyBean.getProperty("migration.userw");
    }

    @PostConstruct
    public void setupMigrationRecSource() {
        migrationRecSource = propertyBean.getProperty("migration.rec.source");
    }

    protected void assertNoRows(DataSource dataSource, String viewOrTable) {
        assertNoRows(dataSource, viewOrTable, "");
    }

    protected void assertNoRows(DataSource dataSource, String viewOrTable, String details) {
        int count = new JdbcTemplate(dataSource).queryForObject("select count(*) from " + viewOrTable, Integer.class);
        assertEquals(count, 0, "View/Table '" + viewOrTable + "' should be empty. " + details);
    }

    protected void assertLessRowsThan(DataSource dataSource, String viewName, int maxValue) {
        int count = new JdbcTemplate(dataSource).queryForObject("select count(*) from " + viewName, Integer.class);
        assertTrue(count <= maxValue, "View " + viewName + " should have max " + maxValue +
                " entries, but has " + count + ".");
    }

    protected void checkIfResultNotNull(MigrationResult result, String migrationName) {
        String message;
        message = "Could not find any migration result. Did migration already run for ";
        assertNotNull(result, message + migrationName);
    }

    /**
     * Asserts {@code |count1 - count2| < count1 * tolerance}. If it fails it produces a dedicated message using the
     * supplied parameter as follows (where v1, v2 are calculated values): <p>({@code errorMsg}: (|{@code name1} -
     * {@code name2}| < Toleranz (Schwelle: {@code tolerance})) = (|{@code count1} - {@code count2}| = v1 < v2) </p>
     */
    protected void assertTolerance(String errorMsg, double tolerance, int count1, String name1, int count2, String name2) {
        double toleranceValue = tolerance * count1;
        double difference = Math.abs(count1 - count2);
        assertTrue(difference < toleranceValue,
                String.format("(%s: (|%s - %s| < Toleranz (Schwelle: %f)) = (|%d - %d| = %f < %f)",
                        errorMsg, name1, name2, tolerance, count1, count2, difference, toleranceValue)
        );
    }
}
