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
import de.augustakom.hurrican.model.cc.Routing;

/**
 * Testklasse fuer {@link CPSBusinessCpeStaticRouteData}.
 */
@Test(groups = BaseTest.UNIT)
public class CPSBusinessCpeStaticRouteDataTest extends BaseTest {

    private CPSBusinessCpeStaticRouteData cut;

    @Test
    public void testSetV6_LAN() {
        final String IP_ADDRESS_V6P = "2001:db8:a001::/48";
        final String NEXT_HOP = "atm0";
        IPAddress prefix = new IPAddressBuilder().withAddress(IP_ADDRESS_V6P)
                .withAddressType(AddressTypeEnum.IPV6_prefix).setPersist(false).build();

        Routing routing = new Routing();
        routing.setNextHop(NEXT_HOP);
        routing.setDestinationAdressRef(prefix);

        cut = new CPSBusinessCpeStaticRouteData(routing);

        assertNotNull(cut.getDestinationIpV6());
        assertNotNull(cut.getPrefix());
        assertNotNull(cut.getNextHopIpV4());
        assertEquals(cut.getDestinationIpV6(), IP_ADDRESS_V6P);
        assertEquals(cut.getPrefix(), "48");
        assertEquals(cut.getNextHopIpV4(), NEXT_HOP);
    }

    @Test
    public void testSetV4_LAN() {
        final String IP_ADDRESS_V4P = "192.168.1.0/24";
        final String NEXT_HOP = "eth0";
        IPAddress prefix = new IPAddressBuilder().withAddress(IP_ADDRESS_V4P)
                .withAddressType(AddressTypeEnum.IPV4_prefix).setPersist(false).build();

        Routing routing = new Routing();
        routing.setNextHop(NEXT_HOP);
        routing.setDestinationAdressRef(prefix);

        cut = new CPSBusinessCpeStaticRouteData(routing);

        assertNotNull(cut.getDestinationIpV4());
        assertNotNull(cut.getPrefix());
        assertNotNull(cut.getNextHopIpV4());
        assertEquals(cut.getDestinationIpV4(), IP_ADDRESS_V4P);
        assertEquals(cut.getPrefix(), "24");
        assertEquals(cut.getNextHopIpV4(), NEXT_HOP);
    }
}
