/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2010 17:19:57
 */
package de.mnet.migration.common.dao;

import static org.testng.Assert.*;

import java.io.*;
import java.time.*;
import java.util.*;
import org.springframework.core.io.InputStreamResource;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationBaseTest;
import de.mnet.migration.common.dao.impl.ExcelDataLoader;
import de.mnet.migration.common.util.ColumnName;

@Test(groups = "unit")
public class ExcelDataLoaderTest extends MigrationBaseTest {
    public static class TestEntity {
        String column1;
        Integer column2;
        Long column3;
        @ColumnName("Seid ihr alle da?")
        Boolean column4;
        boolean column5;
        LocalDateTime column6;
    }

    public void testMapSpreadsheet() {
        ExcelDataLoader<TestEntity> dao = new ExcelDataLoader<TestEntity>();
        String name = ExcelDataLoaderTest.class.getPackage().getName().replaceAll("\\.", "/").concat("/test.xls");
        InputStream stream = null;
        try {
            stream = ExcelDataLoaderTest.class.getClassLoader().getResourceAsStream(name);
            dao.setExcelFile(new InputStreamResource(stream));
            dao.setEntityClass(TestEntity.class);
            dao.setCaptionRow(2);

            Iterator<TestEntity> nextBlock = dao.getSourceData().iterator();
            assertEntity(nextBlock.next(), "1", Integer.valueOf(1), Long.valueOf(1), Boolean.TRUE, true, LocalDateTime.of(2010, 1, 1, 0, 0, 0, 0));
            assertEntity(nextBlock.next(), "2", Integer.valueOf(2), Long.valueOf(2), Boolean.TRUE, true, LocalDateTime.of(2010, 1, 2, 0, 0, 0, 0));
            assertEntity(nextBlock.next(), "3", Integer.valueOf(3), Long.valueOf(3), null, false, LocalDateTime.of(2010, 1, 3, 0, 0, 0, 0));
            assertEntity(nextBlock.next(), "A", Integer.valueOf(4), Long.valueOf(4), Boolean.TRUE, true, LocalDateTime.of(1904, 1, 1, 17, 0, 0, 0));
            assertEntity(nextBlock.next(), "B", Integer.valueOf(5), Long.valueOf(5), Boolean.FALSE, false, LocalDateTime.of(1904, 1, 1, 12, 0, 0, 0));
            assertEntity(nextBlock.next(), "C", Integer.valueOf(6), Long.valueOf(6), Boolean.TRUE, true, null);
            assertFalse(nextBlock.hasNext());
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException e) { // ignored
                }
            }
        }
    }

    private void assertEntity(TestEntity next, String col1, Integer col2, Long col3, Boolean col4, boolean col5, LocalDateTime col6) {
        assertEquals(next.column1, col1);
        assertEquals(next.column2, col2);
        assertEquals(next.column3, col3);
        assertEquals(next.column4, col4);
        assertEquals(next.column5, col5);
        assertEquals(next.column6, col6);
    }
}
