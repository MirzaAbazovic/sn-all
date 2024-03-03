/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 07:20:46
 */
package de.mnet.hurrican.webservice.wholesale;

import java.util.*;
import com.google.common.base.Function;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.hurrican.model.wholesale.WholesaleAssignedVdslProfile;
import de.augustakom.hurrican.model.wholesale.WholesaleChangePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeReason;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeVdslProfileRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleGetAvailablePortsResponse;
import de.augustakom.hurrican.model.wholesale.WholesalePort;
import de.augustakom.hurrican.model.wholesale.WholesaleVdslProfile;
import de.mnet.hurrican.wholesale.fault.clearance.AssignedVdslProfile;
import de.mnet.hurrican.wholesale.fault.clearance.ChangePortRequest;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeReason;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeVdslProfileRequest;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsResponse;
import de.mnet.hurrican.wholesale.fault.clearance.Port;
import de.mnet.hurrican.wholesale.fault.clearance.VdslProfile;

/**
 * Funktionen um Typen des WholesaleFaultClearance Webservice (JAXB) zu Typen des Services(POJO) zu konvertieren und
 * umgekehrt. JAXB Pfichtfelder haben keinen not-null check.
 */
public class WholesaleFaultClearanceServiceFunctions {

    public final static Function<WholesaleGetAvailablePortsResponse, GetAvailablePortsResponse> toGetAvailablePortsResponse = new Function<WholesaleGetAvailablePortsResponse, GetAvailablePortsResponse>() {
        @Override
        public GetAvailablePortsResponse apply(WholesaleGetAvailablePortsResponse in) {
            GetAvailablePortsResponse out = new GetAvailablePortsResponse();
            out.getPorts().addAll(toPorts.apply(in.getPorts()));
            out.getPossibleChangeReasons().addAll(toChangeReasons.apply(in.getPossibleChangeReasons()));
            return out;
        }
    };

    public final static Function<List<WholesalePort>, List<Port>> toPorts = new Function<List<WholesalePort>, List<Port>>() {
        @Override
        public List<Port> apply(List<WholesalePort> in) {
            List<Port> out = new ArrayList<Port>();
            if (CollectionUtils.isNotEmpty(in)) {
                for (WholesalePort wholesalePort : in) {
                    Port port = new Port();
                    port.setPortId(wholesalePort.getPortId());
                    port.setFree(wholesalePort.isFree());
                    port.setTechType(wholesalePort.getTechType());
                    port.setHwEqnIn(wholesalePort.getHwEqnIn());
                    port.setPortTypeIn(wholesalePort.getPortTypeIn());
                    port.setHwRackNameIn(wholesalePort.getHwRackNameIn());
                    port.setMaxBandwidth(wholesalePort.getMaxBandwidth());
                    port.setAvailableFrom(wholesalePort.getAvailableFrom());
                    port.setComment(wholesalePort.getComment());

                    out.add(port);
                }
            }
            return out;
        }
    };

    public final static Function<List<WholesaleChangeReason>, List<ChangeReason>> toChangeReasons = new Function<List<WholesaleChangeReason>, List<ChangeReason>>() {
        @Override
        public List<ChangeReason> apply(List<WholesaleChangeReason> in) {
            List<ChangeReason> out = new ArrayList<ChangeReason>();
            if (CollectionUtils.isNotEmpty(in)) {
                for (WholesaleChangeReason wholesaleChangeReason : in) {
                    ChangeReason changeReason = new ChangeReason();
                    changeReason.setId(wholesaleChangeReason.getChangeReasonId());
                    changeReason.setDescription(wholesaleChangeReason.getDescription());

                    out.add(changeReason);
                }
            }
            return out;
        }
    };

    public final static Function<List<WholesaleAssignedVdslProfile>, List<AssignedVdslProfile>> toAssignedVdslProfiles = new Function<List<WholesaleAssignedVdslProfile>, List<AssignedVdslProfile>>() {
        @Override
        public List<AssignedVdslProfile> apply(List<WholesaleAssignedVdslProfile> input) {
            List<AssignedVdslProfile> out = new ArrayList<AssignedVdslProfile>(input.size());
            for (WholesaleAssignedVdslProfile wholesaleVdslProfile : input) {
                AssignedVdslProfile vdslProfile = new AssignedVdslProfile();
                vdslProfile.setAssignedBy(wholesaleVdslProfile.getAssignedBy());
                vdslProfile.setChangeReason(wholesaleVdslProfile.getChangeReason());
                vdslProfile.setComment(wholesaleVdslProfile.getComment());
                vdslProfile.setProfileName(wholesaleVdslProfile.getProfileName());
                vdslProfile.setValidFrom(wholesaleVdslProfile.getValidFrom());
                vdslProfile.setValidTo(wholesaleVdslProfile.getValidTo());
                out.add(vdslProfile);
            }
            return out;
        }
    };

    public final static Function<List<WholesaleVdslProfile>, List<VdslProfile>> toVdslProfiles = new Function<List<WholesaleVdslProfile>, List<VdslProfile>>() {
        @Override
        public List<VdslProfile> apply(List<WholesaleVdslProfile> input) {
            List<VdslProfile> out = new ArrayList<VdslProfile>(input.size());
            for (WholesaleVdslProfile wholesaleVdslProfile : input) {
                VdslProfile vdslProfile = new VdslProfile();
                vdslProfile.setId(wholesaleVdslProfile.getId());
                vdslProfile.setName(wholesaleVdslProfile.getName());
                out.add(vdslProfile);
            }
            return out;
        }
    };

    public final static Function<ChangeVdslProfileRequest, WholesaleChangeVdslProfileRequest> toWholesaleChangeVdslProfileRequest = new Function<ChangeVdslProfileRequest, WholesaleChangeVdslProfileRequest>() {
        @Override
        public WholesaleChangeVdslProfileRequest apply(ChangeVdslProfileRequest in) {
            WholesaleChangeVdslProfileRequest out = new WholesaleChangeVdslProfileRequest();
            out.setChangeReasonId(in.getChangeReasonId());
            out.setComment(in.getComment());
            out.setLineId(in.getLineId());
            out.setNewProfileId(in.getNewProfileId());
            out.setUsername(in.getUsername());
            out.setValidFrom(in.getValidFrom());
            return out;
        }
    };

    public final static Function<ChangePortRequest, WholesaleChangePortRequest> toWholesaleChangePortRequest = new Function<ChangePortRequest, WholesaleChangePortRequest>() {
        @Override
        public WholesaleChangePortRequest apply(ChangePortRequest in) {
            WholesaleChangePortRequest out = new WholesaleChangePortRequest();
            out.setLineId(in.getLineId());
            out.setNewPortId(in.getNewPortId());
            out.setChangeReasonId(in.getChangeReasonId());
            return out;
        }
    };

}


