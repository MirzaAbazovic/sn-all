/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2011 09:27:43
 */
package de.augustakom.common.tools.net;

import static org.testng.Assert.*;

import java.math.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * TestNG-Test fuer {@link IPToolsV4}.
 */
@Test(groups = { BaseTest.UNIT })
public class IPToolsV4Test extends BaseTest {

    private IPToolsV4 cut;

    @BeforeMethod
    public void setUp() {
        cut = IPToolsV4.instance();
    }

    @DataProvider(name = "dataProviderGetPrefixLength4Address")
    protected Object[][] dataProviderGetPrefixLength4Address() {
        // @formatter:off
        return new Object[][] {
                {               null, -1 },
                {                 "", -1 },
                {        "192.168.1", -1 },
                {    "192.168.1.1.1", -1 },
                {     "192.168.1.1/", -1 },
                {   "192.168.1.1/33", -1 },
                {      "192.168.1.1", 32 },
                {   "192.168.1.1/00", -1 },
                {   "192.168.1.1/01", 1 },
                {    "192.168.1.1/2", 2 },
                {    "192.168.1.1/3", 3 },
                {    "192.168.1.1/8", 8 },
                {   "192.168.1.1/16", 16 },
                { "192.168.1.1/0032", 32 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetPrefixLength4Address")
    public void testGetPrefixLength4Address(String address, int expectedPrefixLength) {
        assertEquals(cut.getPrefixLength4Address(address), expectedPrefixLength);
    }

    @DataProvider(name = "dataProviderGetPrefixLength4Netmask")
    protected Object[][] dataProviderGetPrefixLength4Netmask() {
        // @formatter:off
        return new Object[][] {
                {                  null, -1 },
                {                    "", -1 },
                {         "255.255.248", -1 },
                { "255.255.255.255.255", -1 },
                {   "255.255.255.255/1", -1 },
                {     "128.255.255.255", -1 },
                {         "192.168.1.1", -1 },
                {             "0.0.0.0",  0 },
                {           "128.0.0.0",  1 },
                {           "192.0.0.0",  2 },
                {           "224.0.0.0",  3 },
                {           "255.0.0.0",  8 },
                {         "255.255.0.0", 16 },
                {     "255.255.255.255", 32 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetPrefixLength4Netmask")
    public void testGetPrefixLength4Netmask(String netmask, int expectedPrefixLength) {
        assertEquals(cut.getPrefixLength4Netmask(netmask), expectedPrefixLength);
    }

    @DataProvider(name = "dataProviderGetPrefixLength4IPSize")
    protected Object[][] dataProviderGetPrefixLength4IPSize() {
        // @formatter:off
        return new Object[][] {
                {         0L,  0},
                {         1L,  0},
                {         2L,  1},
                {         3L,  2},
                {         4L,  2},
                {         8L,  3},
                {       256L,  8},
                {       260L,  9},
                {4294967296L, 32},
                {4294967297L, -1}
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetPrefixLength4IPSize")
    public void testGetPrefixLength4IPSize(long ipSize, int expectedPrefixLength) {
        BigInteger ipSizeAsBigInt = BigInteger.valueOf(ipSize);
        assertEquals(cut.getPrefixLength4IPSize(ipSizeAsBigInt), expectedPrefixLength);
    }

    @DataProvider(name = "dataProviderGetIPSize4Netmask")
    protected Object[][] dataProviderGetIPSize4Netmask() {
        // @formatter:off
        return new Object[][] {
                { null,  BigInteger.valueOf(-1) },
                { "0.0.0.0",  BigInteger.valueOf(2).pow(32) },
                { "128.0.0.0",  BigInteger.valueOf(2).pow(31)  },
                { "192.0.0.0",  BigInteger.valueOf(2).pow(30)  },
                { "255.0.0.0",  BigInteger.valueOf(2).pow(24) },
                { "255.255.0.0",  BigInteger.valueOf(2).pow(16) },
                { "255.255.255.0",  BigInteger.valueOf(2).pow(8) },
                { "255.255.255.255",  BigInteger.valueOf(2).pow(0) },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetIPSize4Netmask")
    public void testGetIPSize4Netmask(String netmask, BigInteger ipSize) {
        assertTrue(ipSize.compareTo(cut.getIPSize4Netmask(netmask)) == 0);
    }

    @DataProvider(name = "dataProviderGetNetmask")
    protected Object[][] dataProviderGetNetmask() {
        // @formatter:off
        return new Object[][] {
                { -1, null },
                {  0, "0.0.0.0" },
                {  1, "128.0.0.0" },
                {  2, "192.0.0.0" },
                {  3, "224.0.0.0" },
                {  5, "248.0.0.0" },
                {  8, "255.0.0.0" },
                { 16, "255.255.0.0" },
                { 32, "255.255.255.255" },
                { 33, null },
        };
        // @formatter:on
    }

    @DataProvider(name = "dataProviderGetNetmaskByAddress")
    protected Object[][] dataProviderGetNetmaskByAddress() {
        // @formatter:off
        return new Object[][] {
                { null, null },
                { "", null },
                { "192.168.1.1", "255.255.255.255" },
                { "192.168.2.0/24", "255.255.255.0" },
                { "10.97.0.0/16", "255.255.0.0" },
                { "10.0.0.0/8", "255.0.0.0" },
                { "190.0.0.0/5", "248.0.0.0" },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetNetmask")
    public void testGetNetmask(int prefixLength, String netmask) {
        assertEquals(cut.getNetmask(prefixLength), netmask);
    }

    @Test(dataProvider = "dataProviderGetNetmaskByAddress")
    public void testGetNetmask(String address, String netmask) {
        assertEquals(cut.getNetmask(address), netmask);
    }


    @DataProvider(name = "dataProviderNormalizeValidAddress")
    protected Object[][] dataProviderNormalizeValidAddress() {
        // @formatter:off
        return new Object[][] {
                {"192.168.2.1", "192.168.2.1"},
                {"192.168.2.1/32", "192.168.2.1/32"},
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderNormalizeValidAddress")
    public void testNormalizeValidAddress(String input, String expectedOutput) {
        assertEquals(cut.normalizeValidAddress(input), expectedOutput);
    }

    @DataProvider(name = "dataGetAddressWithoutPrefix")
    protected Object[][] dataGetAddressWithoutPrefix() {
        // @formatter:off
        return new Object[][] {
                {"192.168.2.1",     "192.168.2.1"},
                {"192.168.2.1/32",  "192.168.2.1"},
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataGetAddressWithoutPrefix")
    public void testGetAddressWithoutPrefix(String input, String expectedOutput) {
        assertEquals(cut.getAddressWithoutPrefix(input), expectedOutput);
    }

    @DataProvider(name = "dataNetmaskToAddressWithPrefix")
    public Object[][] dataNetmaskToAddressWithPrefix() {
        return new Object[][] {
                { "192.168.2.1", "255.255.255.255", "192.168.2.1/32" },
                { "192.168.2.1", "255.255.255.0", "192.168.2.1/24" },
                { "192.168.2.1", "255.255.0.0", "192.168.2.1/16" },
                { "192.168.2.1", "255.0.0.0", "192.168.2.1/8" },
                { "192.168.2.1", "0.0.0.0", "192.168.2.1/0" },
                { "192.168.2.1", "255.255.0.255", null },
                { "192.168.2.1/1", "255.255.255.0", null },
                { null, "255.255.255.0", null },
                { "192.168.2.1", null, null },
                { null, null, null },
        };
    }

    @Test(dataProvider = "dataNetmaskToAddressWithPrefix")
    public void testNetmaskToAddressWithPrefix(String address, String netmask, String expected) {
        assertEquals(cut.netmaskToAddressWithPrefix(address, netmask), expected);
    }
}
