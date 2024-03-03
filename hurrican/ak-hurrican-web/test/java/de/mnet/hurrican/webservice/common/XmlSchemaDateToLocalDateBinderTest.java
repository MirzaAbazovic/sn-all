/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 18:02:54
 */
package de.mnet.hurrican.webservice.common;

import static org.testng.Assert.*;

import java.time.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.webservice.tools.XmlSchemaDateToLocalDateBinder;

@Test(groups = BaseTest.UNIT)
public class XmlSchemaDateToLocalDateBinderTest extends BaseTest {

    @DataProvider
    public Object[][] string2LocalDate() {
        return new Object[][] {
                { "2012-02-28+01:00", LocalDate.of(2012, 02, 28) },
                { "2012-02-28", LocalDate.of(2012, 02, 28) },
                { "2012-01-01+01:00", LocalDate.of(2012, 01, 01) },
        };
    }

    @Test(dataProvider = "string2LocalDate")
    public void shouldUnmarshalXmlDate(String xmlString, LocalDate desiredDate) {
        LocalDate parsed = XmlSchemaDateToLocalDateBinder.parseDate(xmlString);

        assertEquals(parsed, desiredDate);
    }

    @DataProvider
    public Object[][] localDate2String() {
        return new Object[][] {
                { LocalDate.of(2012, 02, 28), "2012-02-28" },
                { LocalDate.of(2025, 12, 28), "2025-12-28" },
                { LocalDate.of(2012, 02, 29), "2012-02-29" },
                { LocalDate.of(2012, 01, 01), "2012-01-01" },
        };
    }

    @Test(dataProvider = "localDate2String")
    public void localDateShouldBeMarshalledWithoutTimezone(LocalDate localDate, String desiredXmlString) {
        String xmlDate = XmlSchemaDateToLocalDateBinder.printDate(localDate);

        assertEquals(xmlDate, desiredXmlString);
    }

    @DataProvider
    public Object[][] localDates() {
        return new Object[][] {
                { LocalDate.of(2011, 01, 31) },
                { LocalDate.of(2025, 12, 28) },
                { LocalDate.of(2012, 02, 29) },
                { LocalDate.now() },
                // NEVER DO THIS: { LocalDate.of(Calendar.getInstance())},
        };
    }

    @Test(dataProvider = "localDates")
    public void localDateShouldStaySameByMarshalling(LocalDate date) {
        LocalDate parsedDate = XmlSchemaDateToLocalDateBinder.parseDate(XmlSchemaDateToLocalDateBinder.printDate(date));

        assertEquals(parsedDate, date);
    }

    public void shouldMarshalXmlDate() {
        String input = "2012-02-28+01:00";
        LocalDate parsed = XmlSchemaDateToLocalDateBinder.parseDate(input);

        assertEquals(XmlSchemaDateToLocalDateBinder.printDate(parsed), "2012-02-28");
    }

}


