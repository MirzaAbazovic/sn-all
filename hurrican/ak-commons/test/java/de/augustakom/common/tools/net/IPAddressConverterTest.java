/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 11:19:21
 */
package de.augustakom.common.tools.net;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = { BaseTest.UNIT })
public class IPAddressConverterTest extends BaseTest {

    @DataProvider(name = "dataCreateBinaryCodeOfIpAddress")
    protected Object[][] dataCreateBinaryCodeOfIpAddress() {
        // @formatter:off
        return new Object[][] {
                {                               "192.168.2.1", true, "11000000101010000000001000000001"},
                {                            "192.168.2.1/16", true, "11000000101010000000001000000001"},
                {                            "192.168.2.1/16", false, "1100000010101000"},
                {                            "192.168.0.0/16", false, "1100000010101000"},
                {                               "12.12.12.12", true, "00001100000011000000110000001100"},
                {                                   "0.0.0.0", true, "00000000000000000000000000000000"},
                {                           "255.255.255.255", true, "11111111111111111111111111111111"},
                {                                       "192", true, "00000000000000000000000011000000"},
                {                                 "256.0.0.0", true, null},
                {   "1234:1234:1234:1234:1234:1234:1234:1234", true, "0001001000110100000100100011010000010010001101000001001000110100"},
                {                      "2001:db8:a001:1::/48", false, "001000000000000100001101101110001010000000000001"},
                {                      "2001:db8:a001:1::/48", true, "0010000000000001000011011011100010100000000000010000000000000001"},
                { "2001:db8:a001:ffff:ffff:ffff:ffff:ffff/48", true, "0010000000000001000011011011100010100000000000011111111111111111"},
                { "2001:db8:a001:ffff:ffff:ffff:ffff:ffff/48", false, "001000000000000100001101101110001010000000000001"},
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataCreateBinaryCodeOfIpAddress")
    public void testCreateBinaryCodeOfIpAddress(String ip, boolean ignorePrefixLength, String expectedBinary) throws Exception {
        assertEquals(IPAddressConverter.parseIPAddress(ip, ignorePrefixLength), expectedBinary);
    }

    @DataProvider(name = "dataCreateBinaryCodeOfIpAddress_WithNetmaskAndIPTools")
    protected Object[][] dataCreateBinaryCodeOfIpAddress_WithNetmaskAndIPTools() {
        // @formatter:off
        return new Object[][] {
                { "192.168.2.1", "255.255.255.255"   , "11000000101010000000001000000001"},
                { "192.168.2.1", "255.255.0.0"       , "1100000010101000"},
                { "12.12.12.12", "255.255.255.255"   , "00001100000011000000110000001100"},
                {     "0.0.0.0", "255.255.255.0"     , "000000000000000000000000"},
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataCreateBinaryCodeOfIpAddress_WithNetmaskAndIPTools")
    public void testCreateBinaryCodeOfIpAddress_WithNetmaskAndIPTools(String ip, String netmask, String bitsExpected) {
        assertEquals(IPAddressConverter.parseIPAddress(IPToolsV4.instance().netmaskToAddressWithPrefix(ip,
                netmask), false), bitsExpected);
    }
}
