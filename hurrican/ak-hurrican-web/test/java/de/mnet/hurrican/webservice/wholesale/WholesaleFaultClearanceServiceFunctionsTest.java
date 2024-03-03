/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 07:43:34
 */
package de.mnet.hurrican.webservice.wholesale;

import static org.hamcrest.MatcherAssert.*;
import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeReason;
import de.augustakom.hurrican.model.wholesale.WholesaleGetAvailablePortsResponse;
import de.augustakom.hurrican.model.wholesale.WholesalePort;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeReason;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsResponse;
import de.mnet.hurrican.wholesale.fault.clearance.Port;

@Test(groups = UNIT)
public class WholesaleFaultClearanceServiceFunctionsTest extends BaseTest {

    private static WholesalePort createWholesalePort() {
        WholesalePort port = new WholesalePort();
        port.setPortId(Long.valueOf(999));
        port.setFree(true);
        port.setTechType("FTTB_VDSL");
        port.setHwEqnIn("1-2-3");
        port.setPortTypeIn("ABLTF");
        port.setHwRackNameIn("rack");
        port.setMaxBandwidth(Long.valueOf(50000));
        port.setAvailableFrom(LocalDate.now());
        port.setComment("comment");
        return port;
    }

    private static WholesaleChangeReason createWholesaleChangeReason() {
        WholesaleChangeReason changeReason = new WholesaleChangeReason();
        changeReason.setChangeReasonId(Long.valueOf(33));
        changeReason.setDescription("reason");
        return changeReason;
    }

    @Test
    public void toGetAvailablePortsResponse() {
        WholesaleGetAvailablePortsResponse wholesaleGetAvailablePortsResponse = new WholesaleGetAvailablePortsResponse();
        List<WholesalePort> ports = new ArrayList<WholesalePort>();
        WholesalePort wholesalePort = createWholesalePort();
        ports.add(wholesalePort);

        List<WholesaleChangeReason> changeReasons = new ArrayList<WholesaleChangeReason>();
        WholesaleChangeReason wholesaleChangeReason = createWholesaleChangeReason();
        changeReasons.add(wholesaleChangeReason);

        wholesaleGetAvailablePortsResponse.setPorts(ports);
        wholesaleGetAvailablePortsResponse.setPossibleChangeReasons(changeReasons);

        GetAvailablePortsResponse result = WholesaleFaultClearanceServiceFunctions.toGetAvailablePortsResponse.apply(wholesaleGetAvailablePortsResponse);
        assertNotNull(result);
        assertNotEmpty(result.getPorts());
        assertEquals(result.getPorts().size(), 1);
        Port port = result.getPorts().get(0);
        assertThat(port.getPortId(), equalTo(wholesalePort.getPortId()));
        assertThat(port.isFree(), equalTo(wholesalePort.isFree()));
        assertThat(port.getTechType(), equalTo(wholesalePort.getTechType()));
        assertThat(port.getHwEqnIn(), equalTo(wholesalePort.getHwEqnIn()));
        assertThat(port.getPortTypeIn(), equalTo(wholesalePort.getPortTypeIn()));
        assertThat(port.getHwRackNameIn(), equalTo(wholesalePort.getHwRackNameIn()));
        assertThat(port.getMaxBandwidth(), equalTo(wholesalePort.getMaxBandwidth()));
        assertThat(port.getAvailableFrom(), equalTo(wholesalePort.getAvailableFrom()));
        assertThat(port.getComment(), equalTo(wholesalePort.getComment()));

        assertNotEmpty(result.getPossibleChangeReasons());
        assertEquals(result.getPossibleChangeReasons().size(), 1);
        ChangeReason changeReason = result.getPossibleChangeReasons().get(0);
        assertThat(changeReason.getId(), equalTo(wholesaleChangeReason.getChangeReasonId()));
        assertThat(changeReason.getDescription(), equalTo(wholesaleChangeReason.getDescription()));
    }

}


