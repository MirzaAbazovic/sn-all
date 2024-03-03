/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 15:24:23
 */
package de.mnet.migration.common.util;

import java.lang.reflect.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.RowMapper;

import de.mnet.common.tools.DateConverterUtils;


/**
 * Can map the following: <ul> <li>Boolean, Integer, Long, Float, Double</li> <li>boolean, int, long, float, double</li>
 * <li>DateTime <b>(Does timezone magic, see below)</b></li> <li>Enum (only if {@code public static (enumType)
 * valueOf(Integer index) { ... }} is defined in the Enum, which has to return the corresponding Enum for the int
 * value</li> </ul> <br> When getting DateTimes from the database, the time zone of the read DateTime is set to a newly
 * defined time zone that has the offset of the default time zone of new DateTime(). The reason for this is that when
 * reading time (only time, no date) from a Microsoft SQL database, the year is set to 1754, which results in a time
 * zone that has an offset of 53 minutes and 28 seconds.
 */
public class ReflectionMapper<T> implements RowMapper<T> {
    private Class<T> entityClass;
    private volatile Map<Field, Integer> columnMapping = new HashMap<Field, Integer>();

    /*
     * TIME ADJUSTMENT
     *
     * When local standard time was about to reach
     * Samstag, 1. April 1893, 00:00:00 clocks were turned forward 0:06:32 hours to
     * Samstag, 1. April 1893, 00:06:32 local standard time instead
     *
     * Local Date                   Local Time       DST  UTC Offset      Time Zone
     * --------------------------------------------------------------------------
     * Freitag, 31. März 1893        23:59:57        No   UTC+0:53:28h    LMT
     *                               23:59:58        No   UTC+0:53:28h    LMT
     *                               23:59:59        No   UTC+0:53:28h    LMT
     * Samstag, 1. April 1893  00:00:00 -> 00:06:32  No   UTC+1h          CET
     *                               00:06:33        No   UTC+1h          CET
     *                               00:06:34        No   UTC+1h          CET
     */
    public static final LocalDateTime adjustBefore = LocalDateTime.of(1893, 4, 1, 0, 6, 32, 0);
    public static final Duration adjustFor = Duration.ofMillis((6 * 60 + 32) * 1000);


    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value = "REC_CATCH_EXCEPTION",
            justification = "Various exceptions are thrown and caught in one catch block")
    @Override
    public T mapRow(ResultSet rs, int rowNum) {
        T newInstance;
        try {
            newInstance = entityClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Exception while trying to create new Instance of class " + entityClass.getName(), e);
        }
        if (columnMapping.isEmpty()) {
            getColumnMapping(rs);
        }
        for (Map.Entry<Field, Integer> entry : columnMapping.entrySet()) {
            Object object = null;
            Field field = entry.getKey();
            try {
                object = rs.getObject(entry.getValue());
                if (boolean.class.equals(field.getType())) {
                    boolean foundBoolean = rs.getBoolean(entry.getValue());
                    object = Boolean.valueOf(foundBoolean);
                }
                if (Boolean.class.equals(field.getType())) {
                    boolean foundBoolean = rs.getBoolean(entry.getValue());
                    if (!rs.wasNull()) {
                        object = Boolean.valueOf(foundBoolean);
                    }
                    else {
                        object = null;
                    }
                }
                if (Long.class.equals(field.getType())) {
                    long foundLong = rs.getLong(entry.getValue());
                    if (!rs.wasNull()) {
                        object = Long.valueOf(foundLong);
                    }
                    else {
                        object = null;
                    }
                }
                if (Integer.class.equals(field.getType())) {
                    int foundLong = rs.getInt(entry.getValue());
                    if (!rs.wasNull()) {
                        object = Integer.valueOf(foundLong);
                    }
                    else {
                        object = null;
                    }
                }

                if (Float.class.equals(field.getType())) {
                    float foundFloat = rs.getFloat(entry.getValue());
                    if (!rs.wasNull()) {
                        object = Float.valueOf(foundFloat);
                    }
                    else {
                        object = null;
                    }
                }

                if (Double.class.equals(field.getType())) {
                    double foundDouble = rs.getDouble(entry.getValue());
                    if (!rs.wasNull()) {
                        object = Double.valueOf(foundDouble);
                    }
                    else {
                        object = null;
                    }
                }

                if (LocalDateTime.class.equals(field.getType()) && (object != null)) {
                    final Date date = Optional.ofNullable((Date) object).orElse(new Date());
                    object = DateConverterUtils.asLocalDateTime(date);
                    if (((LocalDateTime) object).isBefore(adjustBefore)) {
                        object = ((LocalDateTime) object).plus(adjustFor);
                    }
                }

                if (Enum.class.isAssignableFrom(field.getType())) {
                    if (!rs.wasNull()) {
                        Method valueOf = field.getType().getDeclaredMethod("valueOf", Integer.class);
                        object = valueOf.invoke(null, rs.getInt(entry.getValue()));
                    }
                    else {
                        object = null;
                    }
                }

                if ((object != null) && Clob.class.isAssignableFrom(object.getClass())) {
                    if (!rs.wasNull()) {
                        object = IOUtils.toString(((Clob) object).getCharacterStream());
                    }
                    else {
                        object = null;
                    }
                }

                // MUST be after CLOB check due to fields type of CLOB is also String
                if (String.class.equals(field.getType())) {
                    if (!rs.wasNull() && (object != null)) {
                        object = ((String) object).replaceAll("\\u00A0", " ");
                    }
                    else {
                        object = null;
                    }
                }

                field.setAccessible(true);
                field.set(newInstance, object);
            }
            catch (Exception e) {
                throw new RuntimeException("Exception while trying to set field " + field.getName() +
                        " of class " + entityClass.getName() + " (type: " + field.getType() + ") to " +
                        (object == null ? "null" : object.toString()) + " (type: " + (object == null ? "-" : object.getClass()) + ")", e);
            }
        }
        return newInstance;
    }


    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value = "DE_MIGHT_IGNORE",
            justification = "Sql-Exception is ignored if name does not exist")
    private synchronized void getColumnMapping(ResultSet rs) {
        if (!columnMapping.isEmpty()) {
            return;
        }
        Map<Field, Integer> map = new HashMap<Field, Integer>();
        List<Field> allFields = ReflectionUtil.getAllFields(entityClass, entityClass);
        allFields = ReflectionUtil.filterStaticAndFinal(allFields);
        allFields = ReflectionUtil.filterTransient(allFields);
        for (Field field : allFields) {
            String columnName = field.getName();
            ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                columnName = annotation.value();
            }
            int column = -1;
            Set<String> possibleNames = ReflectionUtil.createPossibleNames(columnName);
            for (String possibleName : possibleNames) {
                try {
                    column = rs.findColumn(possibleName);
                    break;
                }
                catch (SQLException e) {// NOPMD can be ignored since name does not exist*/
                }
            }
            if (column == -1) {
                throw new RuntimeException("Could not match field '" + columnName +
                        "' of class " + entityClass.getName() + " to an existing column");
            }
            map.put(field, column);
        }
        columnMapping = Collections.unmodifiableMap(map);
    }


    /**
     * Injected
     */
    @SuppressWarnings("unchecked")
    public void setEntityClass(String entityClass) {
        try {
            this.entityClass = (Class<T>) Class.forName(entityClass);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find class " + entityClass, e);
        }
        this.columnMapping.clear();
    }
}
