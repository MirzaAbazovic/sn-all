/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 15:38:52
 */
package de.augustakom.hurrican.tools.jaxb;

import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.adapters.*;

public final class XmlSchemaDateTimeBinder extends XmlAdapter<String, LocalDateTime> {

    private XmlSchemaDateTimeBinder() {
        // We hide the constructor of this utility class because it makes no
        // sense to instantiate it
    }

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return parseDateTime(v);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return printDateTime(v);
    }

    public static LocalDateTime parseDateTime(String s) {
        if (s == null) {
            return null;
        }
        Calendar date = DatatypeConverter.parseDate(s);
        final ZonedDateTime dateTime = ZonedDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()));
        return dateTime.toLocalDateTime();
    }

    public static String printDateTime(LocalDateTime localDate) {
        if (localDate == null) {
            return null;
        }
        final ZonedDateTime dt = localDate.atZone(ZoneId.systemDefault());
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"));  // -->2015-08-17T11:41:46.925+02:00
    }
}


