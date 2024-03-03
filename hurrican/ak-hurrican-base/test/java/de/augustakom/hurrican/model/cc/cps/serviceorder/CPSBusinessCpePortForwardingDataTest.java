/**
 *
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.PortForwarding;

/**
 * Testklasse fuer {@link CPSBusinessCpePortForwardingData}.
 */
public class CPSBusinessCpePortForwardingDataTest extends BaseTest {

    private CPSBusinessCpePortForwardingData cut;

    @Test
    public void testSetV6() {
        final String TRANSPORT_PROTOCOL_TYPE = PortForwarding.TransportProtocolType.TCP.name();
        final String IP_ADDRESS_SOURCE_V6 = "2001:db8:a001:1::1";
        final String IP_ADDRESS_DEST_V6 = "2001:db8:a001:1::10";
        final Integer SOURCE_PORT = Integer.valueOf(80);
        final Integer DEST_PORT = Integer.valueOf(80);
        IPAddress ipAddressSource = new IPAddressBuilder().withAddress(IP_ADDRESS_SOURCE_V6)
                .withAddressType(AddressTypeEnum.IPV6_full).setPersist(false).build();
        IPAddress ipAddressDest = new IPAddressBuilder().withAddress(IP_ADDRESS_DEST_V6)
                .withAddressType(AddressTypeEnum.IPV6_full).setPersist(false).build();

        PortForwarding portForwarding = new PortForwarding();
        portForwarding.setSourceIpAddressRef(ipAddressSource);
        portForwarding.setDestIpAddressRef(ipAddressDest);
        portForwarding.setTransportProtocol(TRANSPORT_PROTOCOL_TYPE);
        portForwarding.setSourcePort(SOURCE_PORT);
        portForwarding.setDestPort(DEST_PORT);

        cut = new CPSBusinessCpePortForwardingData(portForwarding);

        assertNotNull(cut.getSourceIpV6());
        assertNotNull(cut.getDestinationIpV6());
        assertNotNull(cut.getProtocol());
        assertNotNull(cut.getSourcePort());
        assertNotNull(cut.getDestinationPort());
        assertEquals(cut.getSourceIpV6(), IP_ADDRESS_SOURCE_V6);
        assertEquals(cut.getDestinationIpV6(), IP_ADDRESS_DEST_V6);
        assertEquals(cut.getProtocol(), TRANSPORT_PROTOCOL_TYPE);
        assertEquals(cut.getSourcePort(), SOURCE_PORT);
        assertEquals(cut.getDestinationPort(), DEST_PORT);
    }

    @Test
    public void testSetV4() {
        final String TRANSPORT_PROTOCOL_TYPE = PortForwarding.TransportProtocolType.UDP.name();
        final String IP_ADDRESS_SOURCE_V4 = "192.168.1.1";
        final String IP_ADDRESS_DEST_V4 = "192.168.1.10";
        final Integer SOURCE_PORT = Integer.valueOf(1234);
        final Integer DEST_PORT = Integer.valueOf(1234);
        IPAddress ipAddressSource = new IPAddressBuilder().withAddress(IP_ADDRESS_SOURCE_V4)
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        IPAddress ipAddressDest = new IPAddressBuilder().withAddress(IP_ADDRESS_DEST_V4)
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();

        PortForwarding portForwarding = new PortForwarding();
        portForwarding.setSourceIpAddressRef(ipAddressSource);
        portForwarding.setDestIpAddressRef(ipAddressDest);
        portForwarding.setTransportProtocol(TRANSPORT_PROTOCOL_TYPE);
        portForwarding.setSourcePort(SOURCE_PORT);
        portForwarding.setDestPort(DEST_PORT);

        cut = new CPSBusinessCpePortForwardingData(portForwarding);

        assertNotNull(cut.getSourceIpV4());
        assertNotNull(cut.getDestinationIpV4());
        assertNotNull(cut.getProtocol());
        assertNotNull(cut.getSourcePort());
        assertNotNull(cut.getDestinationPort());
        assertEquals(cut.getSourceIpV4(), IP_ADDRESS_SOURCE_V4);
        assertEquals(cut.getDestinationIpV4(), IP_ADDRESS_DEST_V4);
        assertEquals(cut.getProtocol(), TRANSPORT_PROTOCOL_TYPE);
        assertEquals(cut.getSourcePort(), SOURCE_PORT);
        assertEquals(cut.getDestinationPort(), DEST_PORT);
    }
}
