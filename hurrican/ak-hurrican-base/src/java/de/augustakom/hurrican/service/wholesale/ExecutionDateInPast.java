/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2012 11:27:27
 */
package de.augustakom.hurrican.service.wholesale;

import static de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode.*;

import java.time.*;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class ExecutionDateInPast extends WholesaleException {

    private final LocalDate executionDate;
    private static final String BESCHREIBUNG_TEMPLATE = "The desired execution date %s is in the past!";

    public ExecutionDateInPast(LocalDate executionDate) {
        super(EXECUTION_DATE_IN_PAST);
        this.executionDate = executionDate;
    }

    private static final long serialVersionUID = -3308290039712813800L;

    @Override
    public String getFehlerBeschreibung() {
        return String.format(BESCHREIBUNG_TEMPLATE, executionDate);
    }

}


