/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2016 12:47
 */
package de.augustakom.hurrican.tools.jaxb;

import java.time.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.adapters.*;

public final class XmlSchemaLocalTimeBinder extends XmlAdapter<String, LocalTime> {

    private XmlSchemaLocalTimeBinder() {
        super();

    }

    @Override
    public LocalTime unmarshal(String v) throws Exception {
        return parseLocalDate(v);
    }

    @Override
    public String marshal(LocalTime v) throws Exception {
        return printLocalDate(v);
    }

    public static LocalTime parseLocalDate(String s) {
        if (s == null) {
            return null;
        }
        return DatatypeConverter.parseDate(s).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static String printLocalDate(LocalTime dt) {
        if (dt == null) {
            return null;
        }
        return dt.toString();
    }
}
