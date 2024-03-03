/**
 *
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;

/**
 * Testklasse fuer {@link CPSBusinessCpeIpConfigData}.
 */
@Test(groups = { BaseTest.UNIT })
public class CPSBusinessCpeIpConfigDataTest extends BaseTest {

    private CPSBusinessCpeIpConfigData cut;

    @Test
    public void testSetV6_LAN() {
        final String IP_ADDRESS_V6 = "2001:db8:a001:1::1/48";
        final String EGIPAddressType = EndgeraetIp.AddressType.LAN.name();
        IPAddress ipAddressV6 = new IPAddressBuilder().withAddress(IP_ADDRESS_V6)
                .withAddressType(AddressTypeEnum.IPV6_full).setPersist(false).build();

        EndgeraetIp egIp = new EndgeraetIp();
        egIp.setAddressType(EGIPAddressType);
        egIp.setIpAddressRef(ipAddressV6);

        cut = new CPSBusinessCpeIpConfigData(egIp);

        assertNotNull(cut.getIpV6Address());
        assertNotNull(cut.getPrefix());
        assertNotNull(cut.getType());
        assertEquals(cut.getPrefix(), "48");
        assertEquals(cut.getType(), EGIPAddressType);
        assertEquals(cut.getIpV6Address(), IP_ADDRESS_V6);
    }

    @Test
    public void testSetV6_LANWithPrefix() {
        final String IP_ADDRESS_V6R = "0:0:0:1::1/64";
        final String IP_ADDRESS_V6P = "2001:db8:a001::/48";
        final String IP_ADDRESS_V6_ABSOLUTE = "2001:db8:a001:1::1/64";
        final String EGIPAddressType = EndgeraetIp.AddressType.LAN.name();
        IPAddress prefix = new IPAddressBuilder()
                .withAddressType(AddressTypeEnum.IPV6_prefix)
                .withAddress(IP_ADDRESS_V6P)
                .setPersist(false)
                .build();
        IPAddress ipAddressV6 = new IPAddressBuilder()
                .withAddressType(AddressTypeEnum.IPV6_relative)
                .withAddress(IP_ADDRESS_V6R)
                .withPrefixRef(prefix)
                .setPersist(false)
                .build();

        EndgeraetIp egIp = new EndgeraetIp();
        egIp.setAddressType(EGIPAddressType);
        egIp.setIpAddressRef(ipAddressV6);

        cut = new CPSBusinessCpeIpConfigData(egIp);

        assertNotNull(cut.getIpV6Address());
        assertNotNull(cut.getPrefix());
        assertNotNull(cut.getType());
        assertEquals(cut.getPrefix(), "64");
        assertEquals(cut.getType(), EGIPAddressType);
        assertEquals(cut.getIpV6Address(), IP_ADDRESS_V6_ABSOLUTE);
    }

    @Test
    public void testSetV6_WAN() {
        final String IP_ADDRESS_V6 = "2001:db8:a001:1::1/64";
        final String EGIPAddressType = EndgeraetIp.AddressType.WAN.name();
        IPAddress ipAddressV6 = new IPAddressBuilder().withAddress(IP_ADDRESS_V6)
                .withAddressType(AddressTypeEnum.IPV6_full).setPersist(false).build();

        EndgeraetIp egIp = new EndgeraetIp();
        egIp.setAddressType(EGIPAddressType);
        egIp.setIpAddressRef(ipAddressV6);

        cut = new CPSBusinessCpeIpConfigData(egIp);

        assertNotNull(cut.getIpV6Address());
        assertNotNull(cut.getPrefix());
        assertNotNull(cut.getType());
        assertEquals(cut.getPrefix(), "128");
        assertEquals(cut.getType(), EGIPAddressType);
        assertEquals(cut.getIpV6Address(), IP_ADDRESS_V6);
    }

    @Test
    public void testSetV4_LAN() {
        final String IP_ADDRESS_V4 = "192.168.1.1/24";
        final String EGIPAddressType = EndgeraetIp.AddressType.LAN.name();
        IPAddress ipAddressV6 = new IPAddressBuilder().withAddress(IP_ADDRESS_V4)
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();

        EndgeraetIp egIp = new EndgeraetIp();
        egIp.setAddressType(EGIPAddressType);
        egIp.setIpAddressRef(ipAddressV6);

        cut = new CPSBusinessCpeIpConfigData(egIp);

        assertNotNull(cut.getIpV4Address());
        assertNotNull(cut.getPrefix());
        assertNotNull(cut.getType());
        assertEquals(cut.getPrefix(), "24");
        assertEquals(cut.getType(), EGIPAddressType);
        assertEquals(cut.getIpV4Address(), IP_ADDRESS_V4);
    }

    @Test
    public void testSetV4_WAN() {
        final String IP_ADDRESS_V4 = "192.168.1.1/24";
        final String EGIPAddressType = EndgeraetIp.AddressType.WAN.name();
        IPAddress ipAddressV6 = new IPAddressBuilder().withAddress(IP_ADDRESS_V4)
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();

        EndgeraetIp egIp = new EndgeraetIp();
        egIp.setAddressType(EGIPAddressType);
        egIp.setIpAddressRef(ipAddressV6);

        cut = new CPSBusinessCpeIpConfigData(egIp);

        assertNotNull(cut.getIpV4Address());
        assertNotNull(cut.getPrefix());
        assertNotNull(cut.getType());
        assertEquals(cut.getPrefix(), "32");
        assertEquals(cut.getType(), EGIPAddressType);
        assertEquals(cut.getIpV4Address(), IP_ADDRESS_V4);
    }
}
