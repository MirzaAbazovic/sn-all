package de.augustakom.hurrican.tools.jaxb;

import static org.testng.Assert.*;

import java.time.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class XmlSchemaDateTimeBinderTest {

    @DataProvider
    public Object[][] dateTime2String() {
        return new Object[][] {
                { ZonedDateTime.of(2012, 2, 28, 9, 0, 0, 0, ZoneId.of("UTC")), "2012-02-28T09:00:00.000Z" },
                { ZonedDateTime.of(2015, 12, 1, 17, 10, 10, 0, ZoneId.of("UTC")), "2015-12-01T17:10:10.000Z" },
        };
    }

    @DataProvider
    public Object[][] string2Datime() {
        return new Object[][] {
                { ZonedDateTime.of(2012, 2, 28, 9, 0, 0, 0, ZoneId.systemDefault()), "2012-02-28T09:00:00" },
                { ZonedDateTime.of(2015, 12, 1, 17, 10, 10, 0, ZoneId.of("UTC")), "2015-12-01T17:10:10.000Z" },
        };
    }

    @Test(dataProvider = "dateTime2String")
    public void localDateShouldBeMarshalledWithoutTimezone(ZonedDateTime dateTime, String desiredXmlString) {
        String xmlDate = XmlSchemaDateTimeBinder.printDateTime(dateTime.toLocalDateTime());

        assertEquals(xmlDate, desiredXmlString);
    }

    @Test(dataProvider = "string2Datime")
    public void shouldUnmarshalXmlDate(ZonedDateTime desiredDate, String xmlString) {
        LocalDateTime parsed = XmlSchemaDateTimeBinder.parseDateTime(xmlString);

        assertEquals(parsed.toString(), desiredDate.toString());
    }


}