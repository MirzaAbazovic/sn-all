/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.05.2011 11:30:00
 */
package de.mnet.common.tools;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.xml.datatype.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public class DateConverterUtilsTest {

    private LocalDateTime localDateAsLocalDateTime;
    private LocalDateTime localDateTime;
    private Date localDateTimeAsDate;
    private LocalDate localDate;
    private Date localDateAsDate;

    @BeforeMethod
    public void setUp() throws Exception {
        this.localDateTime = LocalDateTime.now();
        this.localDateTimeAsDate = Date.from(this.localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        this.localDate = LocalDate.now();
        this.localDateAsLocalDateTime = this.localDate.atStartOfDay();
        this.localDateAsDate = Date.from(this.localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void dateXmlGregorianCalendarConversion() throws DatatypeConfigurationException {
        Date date = new Date();
        XMLGregorianCalendar xgc = DateConverterUtils.toXmlGregorianCalendar(date);
        Date convertedDate = DateConverterUtils.toDate(xgc);
        assertEquals(date, convertedDate);
    }

    public void dateTimeXmlGregorianCalendarConversion() throws DatatypeConfigurationException {

        LocalDateTime dateTime = LocalDateTime.now();
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(dateTime.atZone(ZoneId.systemDefault()));
        XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        LocalDateTime convertedDateTime = DateConverterUtils.toDateTime(xgc);
        assertEquals(dateTime, convertedDateTime);
    }

    public void localDateXmlGregorianCalendarConversion() {
        LocalDate localDate = LocalDate.now();
        XMLGregorianCalendar xgc = DateConverterUtils.toXmlGregorianCalendar(localDate);
        LocalDate convertedDateTime = DateConverterUtils.toLocalDate(xgc);
        assertEquals(localDate, convertedDateTime);
    }

    public void xmlGregorianCalendarConversionWithNull() throws DatatypeConfigurationException {
        assertNull(DateConverterUtils.toXmlGregorianCalendar((Date) null));
        assertNull(DateConverterUtils.toXmlGregorianCalendar((LocalDate) null));
        assertNull(DateConverterUtils.toDate(null));
        assertNull(DateConverterUtils.toDateTime(null));
    }

    public void lexicalRepresentationToGregorianCalendar() throws DatatypeConfigurationException {
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        XMLGregorianCalendar xgc = DateConverterUtils.toXmlGregorianCalendar(now);
        String lexicalRepresentation = xgc.toXMLFormat();

        XMLGregorianCalendar parsedXgc = DateConverterUtils
                .fromLexicalRepresentation(lexicalRepresentation);

        assertEquals(parsedXgc, xgc);
    }

    public void trimLexicalRepresentationToGregorianCalendar() throws DatatypeConfigurationException {
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        XMLGregorianCalendar xgc = DateConverterUtils.toXmlGregorianCalendar(now);
        String lexicalRepresentation = "   " + xgc.toXMLFormat() + "  ";

        XMLGregorianCalendar parsedXgc = DateConverterUtils
                .fromLexicalRepresentation(lexicalRepresentation);

        assertEquals(parsedXgc, xgc);
    }

    public void wrongLexicalRepresentationToGregorianCalendar() {
        String lexicalRepresentation = "kein Datum";

        XMLGregorianCalendar parsedXgc = DateConverterUtils
                .fromLexicalRepresentation(lexicalRepresentation);

        assertNull(parsedXgc);
    }

    public void nullLexicalRepresentationToGregorianCalendar() {
        String lexicalRepresentation = null;

        XMLGregorianCalendar parsedXgc = DateConverterUtils
                .fromLexicalRepresentation(lexicalRepresentation);

        assertNull(parsedXgc);
    }

    public void testAsLocalDate() throws Exception {
        assertNull(DateConverterUtils.asLocalDate(null));
        assertNull(DateConverterUtils.asLocalDate(null, null));
        assertEquals(DateConverterUtils.asLocalDate(null, localDateAsDate), localDate);
        assertEquals(DateConverterUtils.asLocalDate(localDateAsDate, null), localDate);
        assertEquals(DateConverterUtils.asLocalDate(localDateAsDate, localDateAsDate), localDate);
        assertEquals(DateConverterUtils.asLocalDate(localDateAsDate), localDate);
    }

    public void testAsDate_LocalDate() throws Exception {
        assertNull(DateConverterUtils.asDate((LocalDate) null));
        assertNull(DateConverterUtils.asDate((LocalDate) null, (LocalDate) null));
        assertEquals(DateConverterUtils.asDate((LocalDate) null, localDate), localDateAsDate);
        assertEquals(DateConverterUtils.asDate(localDate, (LocalDate) null), localDateAsDate);
        assertEquals(DateConverterUtils.asDate(localDate, localDate), localDateAsDate);
        assertEquals(DateConverterUtils.asDate(localDate), localDateAsDate);
    }

    public void testAsLocalDateTimeWithDate() throws Exception {
        assertNull(DateConverterUtils.asLocalDateTime((Date) null));
        assertNull(DateConverterUtils.asLocalDateTime((Date) null, null));
        assertEquals(DateConverterUtils.asLocalDateTime((Date) null, localDateTimeAsDate), localDateTime);
        assertEquals(DateConverterUtils.asLocalDateTime(localDateTimeAsDate, null), localDateTime);
        assertEquals(DateConverterUtils.asLocalDateTime(localDateTimeAsDate, localDateTimeAsDate), localDateTime);
        assertEquals(DateConverterUtils.asLocalDateTime(localDateTimeAsDate), localDateTime);
    }

    public void testAsLocalDateTimeWithLocalDate() throws Exception {
        assertNull(DateConverterUtils.asLocalDateTime((LocalDate)null));
        assertNull(DateConverterUtils.asLocalDateTime((LocalDate)null, null));
        assertEquals(DateConverterUtils.asLocalDateTime((LocalDate)null, localDateAsDate), localDateAsLocalDateTime);
        assertEquals(DateConverterUtils.asLocalDateTime(localDate, null), localDateAsLocalDateTime);
        assertEquals(DateConverterUtils.asLocalDateTime(localDate, localDateAsDate), localDateAsLocalDateTime);
        assertEquals(DateConverterUtils.asLocalDateTime(localDate), localDateAsLocalDateTime);
    }

    public void testAsDate_LocalDateTime() throws Exception {
        assertNull(DateConverterUtils.asDate((LocalDateTime) null));
        assertNull(DateConverterUtils.asDate((LocalDateTime) null, (LocalDateTime) null));
        assertEquals(DateConverterUtils.asDate((LocalDateTime) null, localDateTime), localDateTimeAsDate);
        assertEquals(DateConverterUtils.asDate(localDateTime, (LocalDateTime) null), localDateTimeAsDate);
        assertEquals(DateConverterUtils.asDate(localDateTime, localDateTime), localDateTimeAsDate);
        assertEquals(DateConverterUtils.asDate(localDateTime), localDateTimeAsDate);
    }

}
