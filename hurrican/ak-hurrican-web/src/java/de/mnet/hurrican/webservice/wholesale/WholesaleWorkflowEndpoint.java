/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 18:42:56
 */
package de.mnet.hurrican.webservice.wholesale;

import java.time.*;
import javax.servlet.*;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleReleasePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.augustakom.hurrican.service.wholesale.WholesaleService;
import de.mnet.hurrican.webservice.base.MnetServletContextHelper;
import de.mnet.hurrican.wholesale.workflow.CancelModifyPortRequest;
import de.mnet.hurrican.wholesale.workflow.CancelModifyPortResponse;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersRequest;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortRequest;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateRequest;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortResponse;
import de.mnet.hurrican.wholesale.workflow.ReleasePortRequest;
import de.mnet.hurrican.wholesale.workflow.ReleasePortResponse;
import de.mnet.hurrican.wholesale.workflow.ReservePortRequest;
import de.mnet.hurrican.wholesale.workflow.ReservePortResponse;

@Endpoint
public class WholesaleWorkflowEndpoint {

    public static final String WHS_WF_NAMESPACE = "http://www.mnet.de/hurrican/wholesale/workflow/1.0/";

    @Autowired
    private WholesaleService wholesaleService;

    @PayloadRoot(localPart = "reservePortRequest", namespace = WHS_WF_NAMESPACE)
    @ResponsePayload
    public ReservePortResponse reservePort(@RequestPayload ReservePortRequest request) {
        if (request.getDesiredExecutionDate().isBefore(LocalDate.now())) {
            throw new ExecutionDateInPast(request.getDesiredExecutionDate());
        }
        // Zum testen von technischen Fehlern
        if (request.getGeoId() <= 0) {
            throw new RuntimeException("Database not accessable");
        }
        ServletContext ctx = MnetServletContextHelper.getServletContextFromTCH();
        WholesaleReservePortRequest whsRequest = WholesaleServiceFunctions.toWholesaleReservePortRequest.apply(request);
        whsRequest.setSessionId((Long) ctx.getAttribute(HurricanConstants.HURRICAN_SESSION_ID));
        WholesaleReservePortResponse whsResponse = wholesaleService.reservePort(whsRequest);
        return WholesaleServiceFunctions.toReservePortResponse.apply(whsResponse);
    }

    @PayloadRoot(localPart = "getOrderParametersRequest", namespace = WHS_WF_NAMESPACE)
    @ResponsePayload
    public GetOrderParametersResponse getOrderParameters(@RequestPayload GetOrderParametersRequest request) {
        throw new NotImplementedException("getOrderParameters will no longer be supported");
    }

    @PayloadRoot(localPart = "modifyPortRequest", namespace = WHS_WF_NAMESPACE)
    @ResponsePayload
    public ModifyPortResponse modifyPort(@RequestPayload ModifyPortRequest request) {
        WholesaleModifyPortRequest whsRequest = WholesaleServiceFunctions.toWholesaleModifyPortRequest.apply(request);
        WholesaleModifyPortResponse whsResponse = wholesaleService.modifyPort(whsRequest);
        return WholesaleServiceFunctions.toModifyPortResponse.apply(whsResponse);
    }

    @PayloadRoot(localPart = "cancelModifyPortRequest", namespace = WHS_WF_NAMESPACE)
    @ResponsePayload
    public CancelModifyPortResponse cancelModifyPort(@RequestPayload CancelModifyPortRequest request) {
        WholesaleCancelModifyPortRequest whsRequest = WholesaleServiceFunctions.toWholesaleCancelModifyPortRequest.apply(request);
        WholesaleCancelModifyPortResponse whsResponse = wholesaleService.cancelModifyPort(whsRequest);
        return WholesaleServiceFunctions.toCancelModifyPortResponse.apply(whsResponse);
    }

    @PayloadRoot(localPart = "releasePortRequest", namespace = WHS_WF_NAMESPACE)
    @ResponsePayload
    public ReleasePortResponse releasePort(@RequestPayload ReleasePortRequest request) {
        ReleasePortResponse response = new ReleasePortResponse();
        WholesaleReleasePortRequest whsRequest = WholesaleServiceFunctions.toWholesaleReleasePortRequest.apply(request);
        LocalDate releaseDate = wholesaleService.releasePort(whsRequest);
        response.setExecutionDate(releaseDate);
        return response;
    }

    @PayloadRoot(localPart = "modifyPortReservationDateRequest", namespace = WHS_WF_NAMESPACE)
    @ResponsePayload
    public ModifyPortReservationDateResponse modifyPortReservationDate(
            @RequestPayload ModifyPortReservationDateRequest request) {
        WholesaleModifyPortReservationDateRequest whsRequest = WholesaleServiceFunctions.toWholesaleModifyPortReservationDateRequest.apply(request);
        WholesaleModifyPortReservationDateResponse whsResponse = wholesaleService.modifyPortReservationDate(whsRequest);

        return WholesaleServiceFunctions.toModifyPortReservationDateResponse.apply(whsResponse);
    }
}


