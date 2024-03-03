/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 14:51:17
 */
package de.mnet.hurrican.e2e.wholesale.acceptance;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Collections2;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.testng.annotations.Test;

import de.mnet.hurrican.e2e.wholesale.acceptance.model.Produkt;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;
import de.mnet.hurrican.wholesale.fault.clearance.ChangePortFault;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeReason;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsResponse;
import de.mnet.hurrican.wholesale.fault.clearance.Port;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersResponse;
import de.mnet.hurrican.wholesale.workflow.VLAN;

/**
 * Acceptance-Tests fuer ChangePort.
 */
@Test(groups = E2E, enabled = false)
public class ChangePortTest extends BaseWholesaleE2ETest {

    public void changePortSuccess() throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withHwBaugruppeMaxBandwidth(50000)
                .withZweiteHwBaugruppeMaxBandwidth(100000)
                .getStandortData();

        LocalDate today = LocalDate.now();
        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb50().withTP())
                .reservePortInPast(LocalDate.now().minusDays(5))
                .getOrderParameters(today)
                .getAvailablePorts();

        GetOrderParametersResponse orderParametersBeforeChange = state.getOrderParametersResponse;
        GetAvailablePortsResponse response = state.getAvailablePortsResponse;
        assertNotNull(response);

        // auf Port von anderer Baugruppe wechseln, damit sich die VLANs unterscheiden!
        Port portToUse = response.getPorts().get(response.getPorts().size() - 1);
        ChangeReason changeReason = response.getPossibleChangeReasons().get(0);

        state.changePort(portToUse, changeReason)
                .getOrderParameters(today);

        GetOrderParametersResponse orderParametersAfterChange = state.getOrderParametersResponse;
        assertThat(orderParametersAfterChange.getBgPort(), equalTo(portToUse.getHwEqnIn()));

        List<VLAN> vlansBeforeChange = orderParametersBeforeChange.getVlans();
        List<VLAN> vlansAfterChange = orderParametersAfterChange.getVlans();
        assertTrue(vlansAreDifferent(vlansBeforeChange, vlansAfterChange));
    }

    private boolean vlansAreDifferent(List<VLAN> vlansBeforeChange, List<VLAN> vlansAfterChange) {
        for (VLAN before : vlansBeforeChange) {
            for (VLAN after : vlansAfterChange) {
                if (!before.getType().equals(after.getType())) {
                    continue;
                }
                if (before.getSvlanBackbone() != after.getSvlanBackbone()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void changePortOnInvalidOrderYieldsException() {
        try {
            getNewWholesaleOrderState()
                    .lineId("invalid.line.id")
                    .changePort(new Port(), new ChangeReason());
        }
        catch (SoapFaultClientException e) {
            ChangePortFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), "HUR-0006");
            return;
        }
        fail("changePort with invalid line Id should have thrown an exception!");
    }


    public void changePortWithPortInUseYieldsException() throws Exception {
        try {
            standortData = standortDataBuilderProvider.get()
                    .getStandortData();

            // Wholesale-Order erstellen
            WholesaleOrderState state = getNewWholesaleOrderState();
            state
                    .product(Produkt.fttb50().withTP())
                    .reservePortInPast(LocalDate.now().minusDays(5))
                    .getAvailablePorts();

            // GetAvailablePorts fuer ChangePort abfragen
            GetAvailablePortsResponse getAvailablePortsResponse = state.getAvailablePortsResponse;
            assertNotNull(getAvailablePortsResponse);

            // weiteren Auftrag am gleichen Standort reservieren
            WholesaleOrderState stateSecondOrder = getNewWholesaleOrderState();
            stateSecondOrder
                    .product(Produkt.fttb50().withTP())
                    .reservePort(LocalDate.now().plusDays(2))
                    .getAvailablePorts();
            // GetAvailablePorts erneut abfragen (muss einen weniger enthalten als zuvor)
            GetAvailablePortsResponse getAvailablePortsResponseAfterSecondOrder = stateSecondOrder.getAvailablePortsResponse;

            // ermittelt den Port, der durch den zweiten Auftrag belegt wurde
            Port portToUse = getNewlyReservedPort(getAvailablePortsResponse.getPorts(), getAvailablePortsResponseAfterSecondOrder.getPorts());
            ChangeReason changeReason = getAvailablePortsResponse.getPossibleChangeReasons().get(0);

            state.changePort(portToUse, changeReason);
        }
        catch (SoapFaultClientException e) {
            ChangePortFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), "HUR-0018");
            return;
        }
        fail("changePort should have failed because a used port was selected!");
    }

    /*
     * Ermittelt einen Port, der in {@code availablePorts} enthalten ist, nicht jedoch in {@code availablePortsAfterSecondOrder}.
     */
    private Port getNewlyReservedPort(List<Port> availablePorts, final List<Port> availablePortsAfterSecondOrder) {
        Collection<Port> filtered = Collections2.filter(availablePorts, new com.google.common.base.Predicate<Port>() {
            @Override
            public boolean apply(Port input) {
                for (Port port : availablePortsAfterSecondOrder) {
                    if (input.getPortId() == port.getPortId()) {
                        return false;
                    }
                }
                return true;
            }
        });

        assertNotEmpty(filtered);
        return filtered.iterator().next();
    }

}


