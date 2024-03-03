/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2012 11:27:27
 */
package de.augustakom.hurrican.service.wholesale;

import static de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode.*;

import javax.annotation.*;
import org.apache.commons.lang.Validate;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class ModifyPortReservationDateToEarlierDateException extends WholesaleException {

    private final String fehlerbeschreibung;

    public ModifyPortReservationDateToEarlierDateException(@Nonnull final String fehlerbeschreibung) {
        super(MODIFY_PORT_RESERVATION_DATE_TO_EARLIER_DATE);
        this.fehlerbeschreibung = fehlerbeschreibung;
        Validate.notNull(fehlerbeschreibung, "ModifyPortReservationDateToEarlierDateException braucht eine Fehlerbeschreibung");
    }

    private static final long serialVersionUID = -3308290039712813800L;

    @Override
    public String getFehlerBeschreibung() {
        return fehlerbeschreibung;
    }

}


