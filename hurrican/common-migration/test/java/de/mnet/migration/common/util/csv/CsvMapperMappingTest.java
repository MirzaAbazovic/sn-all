/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2010 17:59:43
 */
package de.mnet.migration.common.util.csv;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.supercsv.prefs.CsvPreference;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationBaseTest;


@Test(groups = "unit")
public class CsvMapperMappingTest extends MigrationBaseTest {

    public void testReadCsvFileWithMapping() {
        Resource testCsvFile = new ClassPathResource("de/mnet/migration/common/util/csv/csv-mapper-test.csv");
        List<TestEntity> testEntities = CsvMapper.map(testCsvFile, TestEntity.class, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE, true);

        assertTrue(testEntities.size() == 7);
        TestEntity secondEntity = testEntities.get(1);

        assertEquals(Long.valueOf(2), secondEntity.longtest);
        assertEquals("DSLplus 1000 (2000k)", secondEntity.stringtest);
        assertEquals(TestEnum.E1, secondEntity.enumtest);
    }

    public void testNonExistentEnum() {
        Resource testCsvFile = new ClassPathResource("de/mnet/migration/common/util/csv/csv-mapper-test.csv");
        List<TestEntity> testEntities = CsvMapper.map(testCsvFile, TestEntity.class, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE, true);

        TestEntity lastEntity = testEntities.get(6);
        assertEquals(Long.valueOf(0), lastEntity.longtest);
        assertNull(lastEntity.stringtest);
        assertNull(lastEntity.enumtest);
    }

    public static enum TestEnum {
        E0(0), E1(1), E2(2), E3(3), E4(4), E5(5);

        private final Integer id;

        private TestEnum(Integer id) {
            this.id = id;
        }

        public static TestEnum valueOf(Integer id) {
            for (TestEnum e : values()) {
                if (e != null && e.id.equals(id)) {
                    return e;
                }
            }
            return null;
        }
    }

    public static class TestEntity {
        public Long longtest;
        public TestEnum enumtest;
        public String stringtest;

        public void setLongtest(Long longtest) {
            this.longtest = longtest;
        }

        public void setEnumtest(TestEnum enumtest) {
            this.enumtest = enumtest;
        }

        public void setStringtest(String stringtest) {
            this.stringtest = stringtest;
        }
    }


}
