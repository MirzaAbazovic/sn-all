/*
 * Copyright (c) 2015 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015 08:09:41
 */
package de.augustakom.common.tools.dao.jdbc;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class ResultSetHelperTest extends BaseTest {

    @Test(dataProvider = "resultSetDataProvider")
    public void testGetResultSet(String[] columnNames, List<Object[]> result) {
        checkResultSet(ResultSetHelper.getResultSet(columnNames, result), columnNames, result);
    }

    @Test
    public void testGetResultSetDefaultColumnNames() {
        List<Object[]> result = Arrays.asList(new Object[] { 1, 2 }, new Object[] { 3, 4 });
        checkResultSet(ResultSetHelper.getResultSet(null, result),
                new String[] {"COL1", "COL2"}, result);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testColumnNameMismatch() {
        ResultSetHelper.getResultSet(new String[] {"COL1", "COL2", "COL3"},
                Arrays.asList(new Object[] { 1, 2 }, new Object[] { 3, 4 }));
    }

    @DataProvider
    public Object[][] resultSetDataProvider() {
        return new Object[][] {
            { new String[] {"COL_A", "COL_B", "COL_C", "COL_D"}, Arrays.asList(new Object[] {1, "2", 3, new Date()}, new Object[] {4, "3", 2, new Date()}) },
            { new String[] {"B", "A", "C", "D"}, Collections.singletonList(new Object[] { "A", "C", "D", "C" }) },
            { new String[] {"COL_A", "COL_B"}, Collections.emptyList() }
        };
    }

    private void checkResultSet(List<Map<String, Object>> resultSet, String[] columnNames, List<Object[]> result) {
        Assert.assertEquals(resultSet.size(), result.size());

        for (int i = 0; i < result.size(); i++) {
            Map<String, Object> resultSetRow = resultSet.get(i);
            Object[] rowData = result.get(i);

            Assert.assertEquals(resultSetRow.size(), rowData.length);
            for (int j = 0; j < columnNames.length; j++) {
                Assert.assertNotNull(resultSetRow.get(columnNames[j]));
                Assert.assertEquals(resultSetRow.get(columnNames[j]), rowData[j]);
            }
        }
    }
}