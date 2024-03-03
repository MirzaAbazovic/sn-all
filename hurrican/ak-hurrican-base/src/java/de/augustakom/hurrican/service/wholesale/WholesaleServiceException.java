/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.03.2012 07:59:39
 */
package de.augustakom.hurrican.service.wholesale;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Allgemeine Exception fuer Wholesale-Fehler.
 */
@SoapFault(faultCode = FaultCode.SERVER)
public class WholesaleServiceException extends WholesaleException {
    private static final long serialVersionUID = 2728849061234533624L;

    private final String errorMsg;
    private final String lineId;
    private final Throwable cause;

    public WholesaleServiceException(WholesaleFehlerCode fehler, @CheckForNull String errorMsg,
            @CheckForNull String lineId, @CheckForNull Throwable cause) {
        super(fehler);
        this.errorMsg = errorMsg;
        this.lineId = lineId;
        this.cause = cause;
    }

    @Override
    public String getFehlerBeschreibung() {
        StringBuilder errorMsgBuilder = new StringBuilder();
        errorMsgBuilder.append("WholesaleService caused an Error!");
        String message = getMessage();
        if (StringUtils.isNotEmpty(message)) {
            errorMsgBuilder.append(" ").append(message);
        }
        return errorMsgBuilder.toString();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsgBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(errorMsg)) {
            errorMsgBuilder.append(errorMsg);
        }
        if (StringUtils.isNotEmpty(lineId)) {
            errorMsgBuilder.append(" ").append(String.format("LineId = %s", lineId));
        }
        if (cause != null) {
            errorMsgBuilder.append("\n").append(ExceptionUtils.getFullStackTrace(cause));
        }
        return errorMsgBuilder.toString();
    }


}


