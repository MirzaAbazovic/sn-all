/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 12:53:37
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import java.time.*;
import javax.xml.datatype.*;

public class ReservePortRes {
    public final String lineId;
    public final LocalDate executionDate;

    public ReservePortRes(String lineId, LocalDate executionDate) {
        this.lineId = lineId;
        this.executionDate = LocalDate.from(executionDate.atStartOfDay(ZoneId.systemDefault()));
    }

    public ReservePortRes(String lineId, XMLGregorianCalendar executionDate) {
        this(lineId, Instant.ofEpochMilli(executionDate.toGregorianCalendar().getTime().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
    }

}


