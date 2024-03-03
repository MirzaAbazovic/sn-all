/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2011 15:51:17
 */
package de.mnet.common.tools;

import java.time.*;
import java.util.*;
import javax.xml.datatype.*;
import org.apache.commons.lang.StringUtils;

public final class DateConverterUtils {

    public static final DatatypeFactory dataTypeFactory;

    static {
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date toDate(XMLGregorianCalendar xgc) {
        if (xgc == null) {
            return null;
        }
        return xgc.toGregorianCalendar().getTime();
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDateTime date) {
        try {
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(date.atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(Date date) {
        if (date == null) {
            return null;
        }
        return toXmlGregorianCalendar(Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        XMLGregorianCalendar xmlGregorianCalendar = dataTypeFactory.newXMLGregorianCalendar();
        xmlGregorianCalendar.setDay(localDate.getDayOfMonth());
        xmlGregorianCalendar.setMonth(localDate.getMonthValue());
        xmlGregorianCalendar.setYear(localDate.getYear());
        return xmlGregorianCalendar;
    }

    public static LocalDateTime toDateTime(XMLGregorianCalendar xgc) {
        if (xgc == null) {
            return null;
        }
        return LocalDateTime.from(toDate(xgc).toInstant().atZone(ZoneId.systemDefault()));
    }

    public static LocalDate toLocalDate(XMLGregorianCalendar gregCalendar) {
        if (gregCalendar == null) {
            return null;
        }
        return LocalDate.of(gregCalendar.getYear(), gregCalendar.getMonth(), gregCalendar.getDay());
    }

    public static LocalDate toLocalDate(Calendar calendarDate) {
        if (calendarDate == null) {
            return null;
        }
        return LocalDate.of(calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH) + 1, calendarDate.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Converts a String to an XMLGregorian Calendar
     *
     * @param lexicalRepresentation
     * @return null if lexicalRepresentation could not be converted
     */
    public static XMLGregorianCalendar fromLexicalRepresentation(String lexicalRepresentation) {
        if (StringUtils.isBlank(lexicalRepresentation)) {
            return null;
        }
        try {
            return dataTypeFactory.newXMLGregorianCalendar(lexicalRepresentation.trim());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Instant asInstant(java.util.Date toConvert) {
        if (toConvert == null) {
            return null;
        }
        final boolean isSqlDate = toConvert instanceof java.sql.Date;
        return isSqlDate ? Instant.ofEpochMilli(toConvert.getTime()) : toConvert.toInstant();
    }

    /**
     * konvertiert ein java.util.Date bzw. ein java.sql.Date in ein LocalDate.
     * (java.sql.Date implementiert die toInstant Methode nicht; daher der Umweg...)
     * @param toConvert zu konvertierendes Date Objekt
     * @return ein LocalDate oder {@code null}, wenn toConvert=null war
     */
    public static LocalDate asLocalDate(java.util.Date toConvert) {
        return asLocalDate(toConvert, null);
    }

    public static LocalDate asLocalDate(java.util.Date toConvert, java.util.Date defaultValue) {
        if (toConvert == null && defaultValue == null) {
            return null;
        } else if (toConvert == null) {
            return asInstant(defaultValue).atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            return asInstant(toConvert).atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    /**
     * @see DateConverterUtils#asLocalDate(java.util.Date) nur mit LocalDateTime
     */
    public static LocalDateTime asLocalDateTime(LocalDate toConvert) {
        return asLocalDateTime(toConvert, null);
    }

    public static LocalDateTime asLocalDateTime(LocalDate toConvert, java.util.Date defaultValue) {
        if (toConvert == null && defaultValue == null) {
            return null;
        } else if (toConvert == null) {
            return asInstant(defaultValue).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            return toConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    /**
     * @see DateConverterUtils#asLocalDate(java.util.Date) nur mit LocalDateTime
     */
    public static LocalDateTime asLocalDateTime(java.util.Date toConvert) {
        return asLocalDateTime(toConvert, null);
    }

    public static LocalDateTime asLocalDateTime(java.util.Date toConvert, java.util.Date defaultValue) {
        if (toConvert == null && defaultValue == null) {
            return null;
        } else if (toConvert == null) {
            return asInstant(defaultValue).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            return asInstant(toConvert).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    public static LocalTime asLocalTime(java.util.Date toConvert) {
        return asLocalTime(toConvert, null);
    }

    public static LocalTime asLocalTime(java.util.Date toConvert, java.util.Date defaultValue) {
        if (toConvert == null && defaultValue == null) {
            return null;
        } else if (toConvert == null) {
            return asInstant(defaultValue).atZone(ZoneId.systemDefault()).toLocalTime();
        } else {
            return asInstant(toConvert).atZone(ZoneId.systemDefault()).toLocalTime();
        }
    }

    public static LocalTime asLocalTime(LocalDateTime toConvert) {
        return asLocalTime(toConvert, null);
    }

    public static LocalTime asLocalTime(LocalDateTime toConvert, LocalDateTime defaultValue) {
        if (toConvert == null && defaultValue == null) {
            return null;
        } else if (toConvert == null) {
            return defaultValue.toLocalTime();
        } else {
            return toConvert.toLocalTime();
        }
    }

    public static java.util.Date asDate(LocalDateTime toConvert) {
        return asDate(toConvert, null);
    }

    public static java.util.Date asDate(LocalDateTime toConvert, LocalDateTime defaultValue) {
        if (toConvert == null && defaultValue == null) {
            return null;
        } else if (toConvert == null) {
            return Date.from(defaultValue.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            return Date.from(toConvert.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    public static java.util.Date asDate(LocalDate toConvert) {
        return asDate(toConvert, null);
    }

    public static java.util.Date asDate(LocalDate toConvert, LocalDate defaultValue) {
        if (toConvert == null && defaultValue == null) {
            return null;
        } else if (toConvert == null) {
            return Date.from(defaultValue.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            return Date.from(toConvert.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

}
