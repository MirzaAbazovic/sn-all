/*
 * Copyright (c) 2015 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015 16:09:41
 */
package de.augustakom.hurrican.model.cc;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class DBQueryDefTest extends BaseTest {

    private String testSql = "SELECT A.COL_A, B.COL_B, C_COL AS COL_C, D.D_COL AS D, D.D_COL_FROM_D as E "
            + ", D.F_COL_FROM as FROM_F "
            + "FROM TABLE_A A, TABLE_B B, TABLE_C C, TABLE_D D WHERE BLA=BLUB;";

    public void testColumnNames() {
        DBQueryDef dbQueryDef = new DBQueryDef();
        dbQueryDef.setSqlQuery(testSql);
        checkColumnNames(dbQueryDef.getColumnNames());

        dbQueryDef.setSqlQuery(testSql.toLowerCase());
        checkColumnNames(dbQueryDef.getColumnNames());

        dbQueryDef.setSqlQuery("Select 1 from DUAL");
        Assert.assertEquals(dbQueryDef.getColumnNames().length, 1);
        Assert.assertEquals(dbQueryDef.getColumnNames()[0], "1");

        dbQueryDef.setSqlQuery("Select SEQUENCE.NEXT from DUAL");
        Assert.assertEquals(dbQueryDef.getColumnNames().length, 1);
        Assert.assertEquals(dbQueryDef.getColumnNames()[0], "NEXT");
    }

    private void checkColumnNames(String[] columnNames) {
        Assert.assertEquals(columnNames.length, 6);
        Assert.assertEquals(columnNames[0], "COL_A");
        Assert.assertEquals(columnNames[1], "COL_B");
        Assert.assertEquals(columnNames[2], "COL_C");
        Assert.assertEquals(columnNames[3], "D");
        Assert.assertEquals(columnNames[4], "E");
        Assert.assertEquals(columnNames[5], "FROM_F");
    }

}