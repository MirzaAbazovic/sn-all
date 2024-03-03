/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 15:38:52
 */
package de.augustakom.hurrican.tools.jaxb;

import java.time.*;
import java.util.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.adapters.*;

public final class XmlSchemaLocalDateBinder extends XmlAdapter<String, LocalDate> {

    private XmlSchemaLocalDateBinder() {
        // We hide the constructor of this utility class because it makes no
        // sense to instantiate it
    }

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return parseLocalDate(v);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return printLocalDate(v);
    }

    public static LocalDate parseLocalDate(String s) {
        if (s == null) {
            return null;
        }
        Calendar date = DatatypeConverter.parseDate(s);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    }

    public static String printLocalDate(LocalDate dt) {
        if (dt == null) {
            return null;
        }
        return dt.toString();
    }
}


