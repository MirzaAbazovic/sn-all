/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2012 15:00:19
 */
package de.mnet.hurrican.webservice.wholesale;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import de.augustakom.hurrican.model.wholesale.WholesaleAssignedVdslProfile;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeReason;
import de.augustakom.hurrican.model.wholesale.WholesalePort;
import de.augustakom.hurrican.model.wholesale.WholesaleVdslProfile;
import de.augustakom.hurrican.service.wholesale.WholesaleFaultClearanceService;
import de.augustakom.hurrican.service.wholesale.WholesaleFaultClearanceService.RequestedChangeReasonType;
import de.mnet.hurrican.wholesale.fault.clearance.ChangePortRequest;
import de.mnet.hurrican.wholesale.fault.clearance.ChangePortResponse;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeVdslProfileRequest;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeVdslProfileResponse;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsRequest;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsResponse;
import de.mnet.hurrican.wholesale.fault.clearance.GetVdslProfilesRequest;
import de.mnet.hurrican.wholesale.fault.clearance.GetVdslProfilesResponse;

@Endpoint
public class WholesaleFaultClearanceEndpoint {
    public static final String WHS_FC_NAMESPACE = "http://www.mnet.de/hurrican/wholesale/fault/clearance/1.0/";

    @Autowired
    private WholesaleFaultClearanceService wholesaleFaultClearanceService;


    @PayloadRoot(localPart = "getAvailablePortsRequest", namespace = WHS_FC_NAMESPACE)
    @ResponsePayload
    public GetAvailablePortsResponse getAvailablePorts(@RequestPayload GetAvailablePortsRequest request) {
        assert request.getLineId() != null;

        List<WholesaleChangeReason> changeReasons = wholesaleFaultClearanceService.getChangeReasons(RequestedChangeReasonType.CHANGE_PORT);
        List<WholesalePort> availablePorts = wholesaleFaultClearanceService.getAvailablePorts(request.getLineId());

        GetAvailablePortsResponse response = new GetAvailablePortsResponse();
        response.getPorts().addAll(WholesaleFaultClearanceServiceFunctions.toPorts.apply(availablePorts));
        response.getPossibleChangeReasons().addAll(WholesaleFaultClearanceServiceFunctions.toChangeReasons.apply(changeReasons));
        return response;
    }

    @PayloadRoot(localPart = "changePortRequest", namespace = WHS_FC_NAMESPACE)
    @ResponsePayload
    public ChangePortResponse changePort(@RequestPayload ChangePortRequest request) {
        wholesaleFaultClearanceService.changePort(WholesaleFaultClearanceServiceFunctions.toWholesaleChangePortRequest.apply(request));
        return new ChangePortResponse();
    }

    @PayloadRoot(localPart = "getVdslProfilesRequest", namespace = WHS_FC_NAMESPACE)
    @ResponsePayload
    public GetVdslProfilesResponse getVdslProfiles(@RequestPayload GetVdslProfilesRequest request) {
        assert request.getLineId() != null;

        List<WholesaleChangeReason> changeReasons = wholesaleFaultClearanceService
                .getChangeReasons(RequestedChangeReasonType.CHANGE_VDSL_PROFILE);
        List<WholesaleAssignedVdslProfile> assignedVdslProfiles = wholesaleFaultClearanceService
                .getAssignedVdslProfiles(request.getLineId());
        List<WholesaleVdslProfile> vdslProfiles = wholesaleFaultClearanceService.getPossibleVdslProfiles(request
                .getLineId());

        GetVdslProfilesResponse response = new GetVdslProfilesResponse();
        response.getAssignedProfiles().addAll(
                WholesaleFaultClearanceServiceFunctions.toAssignedVdslProfiles.apply(assignedVdslProfiles));
        response.getPossibleProfiles().addAll(
                WholesaleFaultClearanceServiceFunctions.toVdslProfiles.apply(vdslProfiles));
        response.getPossibleChangeReasons().addAll(
                WholesaleFaultClearanceServiceFunctions.toChangeReasons.apply(changeReasons));
        return response;
    }

    @PayloadRoot(localPart = "changeVdslProfileRequest", namespace = WHS_FC_NAMESPACE)
    @ResponsePayload
    public ChangeVdslProfileResponse changeVdslProfile(@RequestPayload ChangeVdslProfileRequest request) {
        assert request.getLineId() != null;
        wholesaleFaultClearanceService.changeVdslProfile
                (WholesaleFaultClearanceServiceFunctions.toWholesaleChangeVdslProfileRequest.apply(request));
        return new ChangeVdslProfileResponse();
    }

}


