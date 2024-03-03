/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 09:01:12
 */
package de.augustakom.hurrican.service.wholesale;

import java.time.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 *
 */
@SoapFault(faultCode = FaultCode.SERVER)
@ObjectsAreNonnullByDefault
public class Auftrag2EkpFrameContractNotFoundException extends WholesaleException {

    private static final String ERROR_MSG_UNFORMATTED = "no auftrag2EkpFrameContract valid at %s could be found for hurrican order-Id %d, line-Id %s";

    private final String errorMsg;
    @CheckForNull
    private final Throwable cause;

    public Auftrag2EkpFrameContractNotFoundException(String lineId, Long auftragId, LocalDate when) {
        super(WholesaleFehlerCode.AUFTRAG_2_EKP_FRAME_CONTRACT_NOT_FOUND);
        this.cause = null;
        this.errorMsg = String.format(ERROR_MSG_UNFORMATTED, when.toString(), auftragId, lineId);
    }

    public Auftrag2EkpFrameContractNotFoundException(String lineId, Long auftragId, LocalDate when, Throwable cause) {
        super(WholesaleFehlerCode.AUFTRAG_2_EKP_FRAME_CONTRACT_NOT_FOUND);
        this.cause = cause;
        this.errorMsg = String.format(ERROR_MSG_UNFORMATTED, when.toString(), auftragId, lineId);
    }

    @Override
    public String getFehlerBeschreibung() {
        StringBuilder errorMsgBuilder = new StringBuilder();
        errorMsgBuilder.append("WholesaleService threw an Error!");
        if (StringUtils.isNotEmpty(errorMsg)) {
            errorMsgBuilder.append(" ").append(errorMsg);
        }
        if (cause != null) {
            errorMsgBuilder.append("\n").append(ExceptionUtils.getFullStackTrace(cause));
        }
        return errorMsgBuilder.toString();
    }

}


