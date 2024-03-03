/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2010 17:59:43
 */
package de.mnet.migration.common.util.csv;

import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.supercsv.io.CsvBeanReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationBaseTest;
import de.mnet.migration.common.util.ColumnName;
import de.mnet.migration.common.util.csv.CsvMapper.FieldMapping;

@Test(groups = "unit")
public class CsvMapperTest extends MigrationBaseTest {
    public static final Map<String, Integer> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<String, Integer>());

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testCreateFieldMappingExceptionForTestEntity00() {
        CsvMapper.createFieldMapping(EMPTY_MAP, TestEntity00.class);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateFieldMappingExceptionForTestEntity01() {
        CsvMapper.createFieldMapping(EMPTY_MAP, TestEntity01.class);
    }

    public void testCreateFieldMappingTestEntity1() throws IOException {
        CsvBeanReader reader = mock(CsvBeanReader.class);
        when(reader.getHeader(true)).thenReturn(new String[] { "a", "col1", "blub" });
        List<FieldMapping> fieldMapping = CsvMapper.createFieldMapping(CsvMapper.extractHeader(true, reader), TestEntity1.class);

        assertEquals(fieldMapping.size(), 3);
        for (FieldMapping mapping : fieldMapping) {
            switch (mapping.columnIndex) {
                case 0:
                    assertEquals(mapping.field.getName(), "col0");
                    break;
                case 1:
                    assertEquals(mapping.field.getName(), "col1");
                    break;
                case 2:
                    assertEquals(mapping.field.getName(), "col2");
                    break;
                default:
                    fail("incorrect column index");
            }
        }
    }

    public void testCreateFieldMappingTestEntity2() {
        List<FieldMapping> fieldMapping = CsvMapper.createFieldMapping(EMPTY_MAP, TestEntity2.class);

        assertEquals(fieldMapping.size(), 2);
        for (FieldMapping mapping : fieldMapping) {
            switch (mapping.columnIndex) {
                case 0:
                    assertEquals(mapping.field.getName(), "col0");
                    break;
                case 3:
                    assertEquals(mapping.field.getName(), "col3");
                    break;
                default:
                    fail("incorrect column index");
            }
        }
    }

    private TestEntity1 testEntity1(String col0, Integer col1, String col2) {
        TestEntity1 result = new TestEntity1();
        result.col0 = col0;
        result.col1 = col1;
        result.col2 = col2;
        return result;
    }

    private TestEntity2 testEntity2(String col0, Boolean col3) {
        TestEntity2 result = new TestEntity2();
        result.col0 = col0;
        result.col3 = col3;
        return result;
    }

    @DataProvider
    public Object[][] dataProviderMapSheetTestEntity() {
        List<TestEntity1> csvContent1 = new ArrayList<TestEntity1>();
        csvContent1.add(testEntity1("col0", 1, "c2"));
        csvContent1.add(testEntity1("0", 0, "col2"));
        csvContent1.add(testEntity1("x0", 5, "col2"));

        List<TestEntity2> csvContent2 = new ArrayList<TestEntity2>();
        csvContent2.add(testEntity2("col0", true));
        csvContent2.add(testEntity2("0", false));

        return new Object[][] {
                { TestEntity1.class, array("col0", "col1", "blub"), array("col0", "col1", "col2"), csvContent1 },
                { TestEntity2.class, array("col0", "", null, "col3"), array("col0", null, null, "col3"), csvContent2 },
        };
    }

    @Test(dataProvider = "dataProviderMapSheetTestEntity")
    public void testMapSheetTestEntity(Class<?> entityClass, String[] csvHeader, final String[] expected, List<?> csvContent)
            throws Exception {
        CsvBeanReader reader = mock(CsvBeanReader.class);
        when(reader.getHeader(true)).thenReturn(csvHeader);
        List<FieldMapping> fieldMapping = CsvMapper.createFieldMapping(CsvMapper.extractHeader(true, reader), entityClass);
        final Iterator<?> contentIt = csvContent.iterator();

        CsvBeanReader readerMock = mock(CsvBeanReader.class);
        when(readerMock.read(eq(entityClass), (String) anyVararg())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                for (int i = 0; i < expected.length; i++) {
                    assertEquals(invocation.getArguments()[i + 1], expected[i]);
                }
                if (contentIt.hasNext()) {
                    return contentIt.next();
                }
                return null;
            }
        });

        List<?> result = CsvMapper.mapSheet(readerMock, fieldMapping, entityClass, null);
        assertEquals(result, csvContent);
    }

    public static class TestEntity00 {
        @ColumnIndex(-1)
        public String columnInvalid;
    }

    public static class TestEntity01 {
        @ColumnName("nix")
        public String nix;
    }

    public static class TestEntity1 {
        @ColumnIndex(0)
        public String col0;
        public Integer col1;
        @ColumnName("blub")
        public String col2;

        // setter required for SuperCsv
        public void setcol0(String col0) {
            this.col0 = col0;
        }

        public void setcol1(Integer col1) {
            this.col1 = col1;
        }

        public void setcol2(String col2) {
            this.col2 = col2;
        }
    }

    public static class TestEntity2 {
        @ColumnIndex(0)
        public String col0;
        @ColumnIndex(3)
        public Boolean col3;

        // setter required for SuperCsv
        public void setcol0(String col0) {
            this.col0 = col0;
        }

        public void setcol1(Boolean col3) {
            this.col3 = col3;
        }
    }
}
