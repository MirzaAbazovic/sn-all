/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 15:38:52
 */
package de.mnet.common.webservice.tools;

import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.xml.bind.*;

import de.mnet.common.tools.DateConverterUtils;

public final class XmlSchemaDateToLocalDateBinder {

    private XmlSchemaDateToLocalDateBinder() {
        // We hide the constructor of this utility class because it makes no
        // sense to instantiate it
    }

    public static LocalDate parseDate(String s) {
        if (s == null) {
            return null;
        }
        Calendar date = DatatypeConverter.parseDate(s);
        return DateConverterUtils.toLocalDate(date);
    }

    public static String printDate(LocalDate dt) {
        if (dt == null) {
            return null;
        }
        return dt.toString();
    }

    public static LocalDateTime parseDateTime(String s) {
        if (s == null) {
            return null;
        }
        final Calendar date = DatatypeConverter.parseDate(s);
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return localDateTime;
    }

    public static String printDateTime(LocalDateTime dt) {
        if (dt == null) {
            return null;
        }
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));  // -->2015-08-17T11:41:46.925
    }
}


