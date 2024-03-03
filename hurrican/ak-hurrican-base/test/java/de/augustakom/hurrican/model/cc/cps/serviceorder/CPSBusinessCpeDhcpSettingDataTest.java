/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 16:00:30
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;

/**
 * Testklasse fuer {@link CPSBusinessCpeDhcpSettingData}.
 *
 *
 * @since Release 10
 */
public class CPSBusinessCpeDhcpSettingDataTest extends BaseTest {

    private CPSBusinessCpeDhcpSettingData cut;

    /**
     * Test method for {@link de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeDhcpSettingData#getDhcpStartIpV6()}.
     */
    @Test
    public void testSetDhcpStartIpV6_WithPrefix() {
        final String dhcpStartString = "2001:DA01:0:0:0:0:0:1";
        IPAddress dhcpStart = new IPAddressBuilder().withAddress(dhcpStartString + "/64")
                .withAddressType(AddressTypeEnum.IPV6_full).setPersist(false).build();
        final String dhcpEndString = "2001:DA01:0:0:0:0:0:20";
        IPAddress dhcpEnd = new IPAddressBuilder().withAddress(dhcpEndString + "/64")
                .withAddressType(AddressTypeEnum.IPV6_full).setPersist(false).build();
        EGConfig egConfig = new EGConfig();
        egConfig.setDhcpPoolFromRef(dhcpStart);
        egConfig.setDhcpPoolToRef(dhcpEnd);
        cut = new CPSBusinessCpeDhcpSettingData(egConfig);
        assertNull(cut.getDhcpStartIpV4());
        assertNull(cut.getDhcpEndIpV4());
        assertEquals(cut.getDhcpStartIpV6(), dhcpStartString);
        assertEquals(cut.getDhcpEndIpV6(), dhcpEndString);
    }

    /**
     * Test method for {@link de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeDhcpSettingData#getDhcpStartIpV6()}.
     */
    @Test
    public void testSetDhcpStartIpV6_NoPrefix() {
        final String dhcpStartString = "2001:DA01:0:0:0:0:0:1";
        IPAddress dhcpStart = new IPAddressBuilder().withAddress(dhcpStartString)
                .withAddressType(AddressTypeEnum.IPV6_full).setPersist(false).build();
        final String dhcpEndString = "2001:DA01:0:0:0:0:0:20";
        IPAddress dhcpEnd = new IPAddressBuilder().withAddress(dhcpEndString)
                .withAddressType(AddressTypeEnum.IPV6_full).setPersist(false).build();
        EGConfig egConfig = new EGConfig();
        egConfig.setDhcpPoolFromRef(dhcpStart);
        egConfig.setDhcpPoolToRef(dhcpEnd);
        cut = new CPSBusinessCpeDhcpSettingData(egConfig);
        assertNull(cut.getDhcpStartIpV4());
        assertNull(cut.getDhcpEndIpV4());
        assertEquals(cut.getDhcpStartIpV6(), dhcpStartString);
        assertEquals(cut.getDhcpEndIpV6(), dhcpEndString);
    }

    /**
     * Test method for {@link de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeDhcpSettingData#getDhcpEndIpV4()}.
     */
    @Test
    public void testSetDhcpEndIpV4_NoPrefix() {
        final String dhcpStartString = "192.168.2.1";
        IPAddress dhcpStart = new IPAddressBuilder().withAddress(dhcpStartString)
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        final String dhcpEndString = "192.168.2.254";
        IPAddress dhcpEnd = new IPAddressBuilder().withAddress(dhcpEndString)
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        EGConfig egConfig = new EGConfig();
        egConfig.setDhcpPoolFromRef(dhcpStart);
        egConfig.setDhcpPoolToRef(dhcpEnd);
        cut = new CPSBusinessCpeDhcpSettingData(egConfig);
        assertNull(cut.getDhcpStartIpV6());
        assertNull(cut.getDhcpEndIpV6());
        assertEquals(cut.getDhcpStartIpV4(), dhcpStartString);
        assertEquals(cut.getDhcpEndIpV4(), dhcpEndString);
    }

    /**
     * Test method for {@link de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeDhcpSettingData#getDhcpEndIpV4()}.
     */
    @Test
    public void testSetDhcpEndIpV4_WithPrefix() {
        final String dhcpStartString = "192.168.2.1";
        IPAddress dhcpStart = new IPAddressBuilder().withAddress(dhcpStartString + "/32")
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        final String dhcpEndString = "192.168.2.254";
        IPAddress dhcpEnd = new IPAddressBuilder().withAddress(dhcpEndString + "/32")
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        EGConfig egConfig = new EGConfig();
        egConfig.setDhcpPoolFromRef(dhcpStart);
        egConfig.setDhcpPoolToRef(dhcpEnd);
        cut = new CPSBusinessCpeDhcpSettingData(egConfig);
        assertNull(cut.getDhcpStartIpV6());
        assertNull(cut.getDhcpEndIpV6());
        assertEquals(cut.getDhcpStartIpV4(), dhcpStartString);
        assertEquals(cut.getDhcpEndIpV4(), dhcpEndString);
    }

} // end
