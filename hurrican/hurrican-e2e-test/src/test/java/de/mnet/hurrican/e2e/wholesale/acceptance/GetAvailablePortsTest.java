/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 12:16:21
 */
package de.mnet.hurrican.e2e.wholesale.acceptance;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import static org.hamcrest.MatcherAssert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.NumberTools;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.Produkt;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeReason;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsResponse;
import de.mnet.hurrican.wholesale.fault.clearance.Port;

/**
 * Acceptance-Tests fuer GetAvailablePorts.
 */
@Test(groups = E2E, enabled = false)
public class GetAvailablePortsTest extends BaseWholesaleE2ETest {

    public void getAvailablePortsSuccess() throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withHwBaugruppeMaxBandwidth(50000)
                .withZweiteHwBaugruppeMaxBandwidth(100000)
                .getStandortData();

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb50().withTP())
                .reservePortInPast(LocalDate.now().minusDays(5))
                .getAvailablePorts();

        GetAvailablePortsResponse response = state.getAvailablePortsResponse;
        assertNotNull(response);
        assertNotEmpty(response.getPossibleChangeReasons());
        assertIdAndDescriptionDefined(response.getPossibleChangeReasons());
        assertNotEmpty(response.getPorts());
        assertAllNecessaryPropertiesDefined(response.getPorts());
        assertAllPortsHaveValidPhysikType(response.getPorts());
    }

    private void assertIdAndDescriptionDefined(List<ChangeReason> changeReasons) {
        for (ChangeReason changeReason : changeReasons) {
            assertNotNull(changeReason.getId());
            assertNotNull(changeReason.getDescription());
        }
    }

    private void assertAllNecessaryPropertiesDefined(List<Port> ports) {
        boolean port50000Found = false;
        boolean port100000Found = false;
        for (Port port : ports) {
            assertNotNull(port.getPortId());
            assertNotNull(port.getHwEqnIn());
            assertNotNull(port.getHwRackNameIn());
            assertNotNull(port.getPortTypeIn());
            assertNotNull(port.getTechType());
            assertNotNull(port.getMaxBandwidth());

            if (NumberTools.equal(Long.valueOf(50000), port.getMaxBandwidth())) {
                port50000Found = true;
            }
            else if (NumberTools.equal(Long.valueOf(100000), port.getMaxBandwidth())) {
                port100000Found = true;
            }
        }

        assertTrue(port50000Found);
        assertTrue(port100000Found);
    }

    private void assertAllPortsHaveValidPhysikType(List<Port> ports) {
        for (Port port : ports) {
            assertThat(port.getTechType(), equalTo("FTTB_VDSL2"));
        }
    }

}


