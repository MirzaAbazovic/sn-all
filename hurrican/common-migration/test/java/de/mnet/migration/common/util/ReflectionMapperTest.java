/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.07.2010 11:47:17
 */
package de.mnet.migration.common.util;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.migration.base.MigrationBaseTest;


/**
 *
 */
@Test(groups = "unit")
public class ReflectionMapperTest extends MigrationBaseTest {

    private static final int STRING_COLUMN_NUM = 1;
    private static final int DATE_COLUMN_NUM = 2;
    private static final int BOOLEAN_COLUMN_NUM = 3;
    private static final int LONG_COLUMN_NUM = 4;
    private static final int INT_COLUMN_NUM = 5;
    private static final int BOOL_COLUMN_NUM = 6;
    private static final int FLOAT_COLUMN_NUM = 7;
    private static final int DOUBLE_COLUMN_NUM = 8;
    private static final int ENUM_COLUMN_NUM = 8;

    public static class Entity {
        public String testString;
        public LocalDateTime testDate;
        public Boolean testBoolean;
        public Long testLong;
        public Integer testInt;
        public boolean testBool;
        public Float testFloat;
        public Double testDouble;
        public TestEnum testEnum;
    }

    public static enum TestEnum {
        A(0), B(4), C(10);
        private final Integer i;

        private TestEnum(Integer i) {
            this.i = i;
        }

        public static TestEnum valueOf(Integer i) {
            for (TestEnum e : values()) {
                if ((e != null) && e.i.equals(i)) {
                    return e;
                }
            }
            return null;
        }
    }

    private static final int ERROR_ENUM_COLUMN_NUM = 1;

    public static class ErrorEntity {
        public ErrorEnum testEnum;
    }

    public static enum ErrorEnum {
        A, B, C;
    }

    private ReflectionMapper<Entity> mapper;
    private ResultSet resultSet;

    @BeforeMethod
    public void setupMapperAndResultSet() throws SQLException {
        mapper = setUpReflectionMapper();
        resultSet = setUpResultSet();
    }


    /**
     * ***** FIELD TESTS *********
     */

    public void testDateTimeNull() {
        Object test = null;
        LocalDateTime dateTime = LocalDateTime.from((Optional.ofNullable((Date) test).orElse(new Date())).toInstant().atZone(ZoneId.systemDefault()));
        assertNotNull(dateTime);
    }

    public void testReflectionPrimitiveBoolean() throws SQLException {
        when(resultSet.getBoolean(BOOL_COLUMN_NUM)).thenReturn(true);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testBool, true);
    }

    public void testMapCreateEntity() {
        ResultSet resultSet = mock(ResultSet.class);
        ReflectionMapper<Entity> mapper = setUpReflectionMapper();
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
    }

    public void testMapStringColumn() throws SQLException {
        ReflectionMapper<Entity> mapper = setUpReflectionMapper();
        ResultSet resultSet = setUpResultSet();
        when(resultSet.getObject(STRING_COLUMN_NUM)).thenReturn("Test");
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testString, "Test");
        assertEquals(entity.testDate, null);
    }

    public void testMapStringColumnNbsp() throws SQLException {
        ReflectionMapper<Entity> mapper = setUpReflectionMapper();
        ResultSet resultSet = setUpResultSet();
        when(resultSet.getObject(STRING_COLUMN_NUM)).thenReturn("Test <--nbsp\u00A0<--nbsp <--space");
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        //                                   v-------v-- should now be normal spaces
        assertEquals(entity.testString, "Test <--nbsp <--nbsp <--space");
        assertEquals(entity.testDate, null);
    }

    public void testMapDateColumn() throws SQLException {
        Date testDate = new Date();
        when(resultSet.getObject(DATE_COLUMN_NUM)).thenReturn(testDate);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testString, null);
        assertEquals(entity.testDate, DateConverterUtils.asLocalDateTime(testDate));
    }

    public void testMapDateColumnBefore1893() throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.set(1802, 4, 2, 14, 57, 34);
        Date testDate = cal.getTime();
        when(resultSet.getObject(DATE_COLUMN_NUM)).thenReturn(testDate);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testString, null);
        assertEquals(entity.testDate, DateConverterUtils.asLocalDateTime(testDate).plus(Duration.ofMillis((6 * 60 + 32) * 1000)));
    }

    public void testMapBooleanNullColumn() throws SQLException {
        when(resultSet.getBoolean(BOOLEAN_COLUMN_NUM)).thenReturn(false);
        when(resultSet.wasNull()).thenReturn(true);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testBoolean, null);
    }

    public void testMapBooleanColumn() throws SQLException {
        when(resultSet.getBoolean(BOOLEAN_COLUMN_NUM)).thenReturn(true);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testString, null);
        assertEquals(entity.testDate, null);
        assertEquals(entity.testBoolean, Boolean.TRUE);
    }

    public void testMapFloatColumn() throws SQLException {
        when(resultSet.getFloat(FLOAT_COLUMN_NUM)).thenReturn(21.21f);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testString, null);
        assertEquals(entity.testDate, null);
        assertEquals(entity.testFloat, Float.valueOf(21.21f));
    }

    public void testMapDoubleColumn() throws SQLException {
        when(resultSet.getDouble(DOUBLE_COLUMN_NUM)).thenReturn(21.21);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testString, null);
        assertEquals(entity.testDouble, Double.valueOf(21.21));
    }

    public void testMapNullDateColumn() throws SQLException {
        when(resultSet.getObject(DATE_COLUMN_NUM)).thenReturn(null);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testString, null);
        assertEquals(entity.testDate, null);
    }

    public void testMapLongColumn() throws SQLException {
        when(resultSet.getLong(LONG_COLUMN_NUM)).thenReturn(1L);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testLong, Long.valueOf(1L));
    }

    public void testMapLongNullColumn() throws SQLException {
        when(resultSet.getLong(LONG_COLUMN_NUM)).thenReturn(0L);
        when(resultSet.wasNull()).thenReturn(true);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testLong, null);
    }

    public void testMapIntColumn() throws SQLException {
        when(resultSet.getInt(INT_COLUMN_NUM)).thenReturn(1);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testInt, Integer.valueOf(1));
    }


    public void testMapIntNullColumn() throws SQLException {
        when(resultSet.getInt(INT_COLUMN_NUM)).thenReturn(0);
        when(resultSet.wasNull()).thenReturn(true);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testInt, null);
    }

    public void testMapEnumColumn() throws SQLException {
        when(resultSet.getInt(ENUM_COLUMN_NUM)).thenReturn(4);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testEnum, TestEnum.B);
    }

    public void testMapEnumColumnNull() throws SQLException {
        when(resultSet.getInt(ENUM_COLUMN_NUM)).thenReturn(0);
        when(resultSet.wasNull()).thenReturn(true);
        Entity entity = mapper.mapRow(resultSet, 0);
        assertNotNull(entity);
        assertEquals(entity.testEnum, null);
    }

    public void testMapEnumColumnError() throws SQLException {
        ReflectionMapper<Entity> mapper = new ReflectionMapper<Entity>();
        mapper.setEntityClass(ErrorEntity.class.getName());
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.findColumn(anyString())).thenThrow(new SQLException("Test exception"));
        doReturn(ERROR_ENUM_COLUMN_NUM).when(resultSet).findColumn("testEnum");
        when(resultSet.getInt(ERROR_ENUM_COLUMN_NUM)).thenReturn(0);
        try {
            mapper.mapRow(resultSet, 0);
        }
        catch (Exception e) {
            assertTrue(e.getCause().getClass().equals(NoSuchMethodException.class));
            return;
        }
        fail("Exception was not thrown");
    }


    @Test(groups = "unit", expectedExceptions = { RuntimeException.class })
    public void testColumnNotFound() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.findColumn(any(String.class))).thenThrow(new SQLException());
        mapper.mapRow(resultSet, 0);
    }

    private ReflectionMapper<Entity> setUpReflectionMapper() {
        ReflectionMapper<Entity> mapper = new ReflectionMapper<Entity>();
        mapper.setEntityClass(Entity.class.getName());
        return mapper;
    }


    private ResultSet setUpResultSet() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.findColumn(anyString())).thenThrow(new SQLException("Test exception"));
        doReturn(STRING_COLUMN_NUM).when(resultSet).findColumn("testString");
        doReturn(DATE_COLUMN_NUM).when(resultSet).findColumn("testDate");
        doReturn(BOOLEAN_COLUMN_NUM).when(resultSet).findColumn("testBoolean");
        doReturn(LONG_COLUMN_NUM).when(resultSet).findColumn("testLong");
        doReturn(INT_COLUMN_NUM).when(resultSet).findColumn("testInt");
        doReturn(BOOL_COLUMN_NUM).when(resultSet).findColumn("testBool");
        doReturn(FLOAT_COLUMN_NUM).when(resultSet).findColumn("testFloat");
        doReturn(DOUBLE_COLUMN_NUM).when(resultSet).findColumn("testDouble");
        doReturn(ENUM_COLUMN_NUM).when(resultSet).findColumn("testEnum");
        return resultSet;
    }


    /**
     * ***** NAME MAP TESTS *********
     */

    public static class Entity2 {
        public String someName2;
        public String doYouJSPCode;
        public String some5Numbers9InName;
    }

    public void testMapNames() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.findColumn(anyString())).thenThrow(new SQLException("Test exception"));
        doReturn(1).when(resultSet).findColumn("some Name 2");
        doReturn(2).when(resultSet).findColumn("do You JSP Code");
        doReturn(3).when(resultSet).findColumn("some 5 Numbers 9 In_Name");
        when(resultSet.getObject(1)).thenReturn("BluePill");
        when(resultSet.getObject(2)).thenReturn("RedPill");
        when(resultSet.getObject(3)).thenReturn("GreenPill");
        ReflectionMapper<Entity2> mapper = new ReflectionMapper<Entity2>();
        mapper.setEntityClass(Entity2.class.getName());

        Entity2 entity = mapper.mapRow(resultSet, 0);
        assertEquals(entity.someName2, "BluePill");
        assertEquals(entity.doYouJSPCode, "RedPill");
        assertEquals(entity.some5Numbers9InName, "GreenPill");
    }
}
