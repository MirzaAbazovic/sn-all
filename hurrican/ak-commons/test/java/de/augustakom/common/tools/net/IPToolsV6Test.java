/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2011 09:28:21
 */
package de.augustakom.common.tools.net;

import static org.testng.Assert.*;

import java.math.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * TestNG-Test fuer {@link IPToolsV6}.
 */
@Test(groups = { "unit" })
public class IPToolsV6Test extends BaseTest {

    private IPToolsV6 cut;

    @BeforeMethod
    public void setUp() {
        cut = IPToolsV6.instance();
    }

    @DataProvider(name = "dataProviderCollapseNormalizedAddress")
    protected Object[][] dataProviderCollapseNormalizedAddress() {
        // @formatter:off
        return new Object[][] {
                {                                      null, null },
                {                                        "", null },
                {                         "0:0:0:0:0:0:0:0", "::" },
                {                        "ff:0:0:0:0:0:0:0", "ff::" },
                {                        "0:0:0:0:0:0:0:ff", "::ff" },
                {                     "ff:1234:0:0:0:0:0:0", "ff:1234::" },
                {                     "0:0:0:0:0:0:1234:ff", "::1234:ff" },
                {                  "ff:1234:0:0:0:0:0:asdf", "ff:1234::asdf" },
                {                  "ff:1234:0:0:0:0:asdf:0", "ff:1234::asdf:0" },
                {                 "ff:1234:0:0:ff:0:asdf:0", "ff:1234::ff:0:asdf:0" },
                {                     "ff:0:ff:0:ff:0:ff:0", "ff::ff:0:ff:0:ff:0" },
                {         "ff:1234:asdf:1234:asdf:1234:0:0", "ff:1234:asdf:1234:asdf:1234::" },
                {         "0:0:ff:1234:asdf:1234:asdf:1234", "::ff:1234:asdf:1234:asdf:1234" },
                {    "1234:1234:1234:1234:1234:1234:1234:0", "1234:1234:1234:1234:1234:1234:1234::" },
                {    "0:1234:1234:1234:1234:1234:1234:1234", "::1234:1234:1234:1234:1234:1234:1234" },
                { "1234:1234:1234:1234:1234:1234:1234:1234", "1234:1234:1234:1234:1234:1234:1234:1234" }
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderCollapseNormalizedAddress")
    public void collapseNormalizedAddress(String address, String expectedResult) {
        String result = cut.collapseNormalizedAddress(address);
        assertEquals(result, expectedResult);
    }

    @DataProvider(name = "dataProviderExpandAddress")
    protected Object[][] dataProviderExpandAddress() {
        // @formatter:off
        return new Object[][] {
                {                                      null, null },
                {                                        "", "" },
                {                                     "ff:", "ff:" },
                {                                    "ff::", "ff:0:0:0:0:0:0:0" },
                {                                    "::ff", "0:0:0:0:0:0:0:ff" },
                {                                  "ff::ff", "ff:0:0:0:0:0:0:ff" },
                {                           "ff::ff:0:0:ff", "ff:0:0:0:ff:0:0:ff" },
                {    "1234:1234:1234:1234:1234:1234:1234::", "1234:1234:1234:1234:1234:1234:1234:0" },
                { "1234:1234:1234:1234:1234:1234:1234:1234", "1234:1234:1234:1234:1234:1234:1234:1234" }
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderExpandAddress")
    public void expandAddress(String address, String expectedResult) {
        String result = cut.expandAddress(address);
        assertEquals(result, expectedResult);
    }


    @DataProvider(name = "dataProviderNormalizeValidAddress")
    protected Object[][] dataProviderNormalizeValidAddress() {
        // @formatter:off
        return new Object[][] {
                {                                    null, null },
                {                                      "", null },
                {             "2001:db8:a001:1:1:1:1:1:1", null },
                {                                 "2001:", null },
                {  "2001:1234:1234:1234:1234:1234:1234:", null },
                {                       "2001:db8:a001::", "2001:db8:a001::" },
                {            "2001:0db8:a001:1:1:01:10:1", "2001:db8:a001:1:1:1:10:1" },
                {                     "2001:db8:a001::/0", "2001:db8:a001::/0" },
                {                      "2001:db8:a001::1", "2001:db8:a001::1" },
                {                       "::2001:db8:a001", "::2001:db8:a001" },
                {                             "ABCD:EF::", "abcd:ef::" },
                {                                    "::", "::" },
                {                               "::/0048", "::/48" },
                {                                 "::/48", "::/48" },
                {         "2001:db8:a001:1:1:1:1:1:1/144", null },
                {                    "2001:db8:a001::/48", "2001:db8:a001::/48" },
                {         "2001:db8:a001:1:1:01:10:1/128", "2001:db8:a001:1:1:1:10:1/128" },
                {                  "2001:db8:a001::1/128", "2001:db8:a001::1/128" },
                {                   "::2001:db8:a001/128", "::2001:db8:a001/128" },
                {                        "ABCD:EF::/0032", "abcd:ef::/32" },
                {                                  "::/0", "::/0" },
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderNormalizeValidAddress")
    public void testNormalizeValidAddress(String address, String expectedAddress) {
        assertEquals(cut.normalizeValidAddress(address), expectedAddress);
    }

    @DataProvider(name = "dataProviderGetPrefixLength4Address")
    protected Object[][] dataProviderGetPrefixLength4Address() {
        // @formatter:off
        return new Object[][] {
                {                        null,  -1 },
                {                          "",  -1 },
                {             "2001:db8:a001",  -1 },
                { "2001:db8:a001:1:1:1:1:1:1",  -1 },
                {          "2001:db8:a001::/",  -1 },
                {       "2001:db8:a001::/129",  -1 },
                {           "2001:db8:a001::", 128 },
                {        "2001:db8:a001::/00",  -1 },
                {        "2001:db8:a001::/01",   1 },
                {         "2001:db8:a001::/2",   2 },
                {         "2001:db8:a001::/3",   3 },
                {         "2001:db8:a001::/8",   8 },
                {        "2001:db8:a001::/64",  64 },
                {      "2001:db8:a001::/0128", 128 },
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderGetPrefixLength4Address")
    public void testGetPrefixLength4Address(String address, int expectedPrefixLength) {
        assertEquals(cut.getPrefixLength4Address(address), expectedPrefixLength);
    }

    @DataProvider(name = "dataProviderGetPrefixLength4Netmask")
    protected Object[][] dataProviderGetPrefixLength4Netmask() {
        // @formatter:off
        return new Object[][] {
                {                                           null, -1 },
                {                                             "", -1 },
                {                               "ffff:ffff:ffff", -1 },
                { "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", -1 },
                {                            "ffff:ffff:ffff::/", -1 },
                {                           "ffff:ffff:ffff::/1", -1 },
                {                              "2001:db8:a001::", -1 },
                {                                  "8000.ffff::", -1 },
                {                                           "::",  0 },
                {                              "0:0:0:0:0:0:0:0",  0 },
                {                             "ffff:ffff:ffff::", 48 },
                {                                       "8000::",  1 },
                {                                       "c000::",  2 },
                {                                       "e000::",  3 },
                {                                       "ffff::", 16 },
                {                        "ffff:ffff:ffff:ffff::", 64 },
                {      "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", 128 },
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderGetPrefixLength4Netmask")
    public void testGetPrefixLength4Netmask(String netmask, int expectedPrefixLength) {
        assertEquals(cut.getPrefixLength4Netmask(netmask), expectedPrefixLength);
    }

    @DataProvider(name = "dataProviderGetPrefixLength4IPSize")
    protected Object[][] dataProviderGetPrefixLength4IPSize() {
        // @formatter:off
        return new Object[][] {
                {         0L,  0 },
                {         1L,  0 },
                {         2L,  1 },
                {         3L,  2 },
                {         4L,  2 },
                {         8L,  3 },
                {       256L,  8 },
                {       260L,  9 },
                {4294967296L, 32 },
                {4294967297L, 33 }
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderGetPrefixLength4IPSize")
    public void testGetPrefixLength4IPSize(long ipSize, int expectedPrefixLength) {
        BigInteger ipSizeAsBigInt = BigInteger.valueOf(ipSize);
        assertEquals(cut.getPrefixLength4IPSize(ipSizeAsBigInt), expectedPrefixLength);
    }

    @DataProvider(name = "dataProviderGetIPSize4Netmask")
    protected Object[][] dataProviderGetIPSize4Netmask() {
        // @formatter:off
        return new Object[][] {
                { null,  BigInteger.valueOf(-1) },
                { "::",  BigInteger.valueOf(2).pow(128) },
                { "8000::",  BigInteger.valueOf(2).pow(127)  },
                { "c000::",  BigInteger.valueOf(2).pow(126)  },
                { "ff00::",  BigInteger.valueOf(2).pow(120) },
                { "ffff::",  BigInteger.valueOf(2).pow(112) },
                { "ffff:ff00::",  BigInteger.valueOf(2).pow(104) },
                { "ffff:ffff::",  BigInteger.valueOf(2).pow(96) },
                { "ffff:ffff:ffff:ffff::",  BigInteger.valueOf(2).pow(64) },
                { "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff",  BigInteger.valueOf(2).pow(0) },
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderGetIPSize4Netmask")
    public void testGetIPSize4Netmask(String netmask, BigInteger ipSize) {
        assertTrue(ipSize.compareTo(cut.getIPSize4Netmask(netmask)) == 0);
    }

    @DataProvider(name = "dataProviderGetNetmask")
    protected Object[][] dataProviderGetNetmask() {
        // @formatter:off
        return new Object[][] {
                { -1, null },
                {  0, "0:0:0:0:0:0:0:0" },
                {  1, "8000:0:0:0:0:0:0:0" },
                {  2, "c000:0:0:0:0:0:0:0" },
                {  3, "e000:0:0:0:0:0:0:0" },
                {  5, "f800:0:0:0:0:0:0:0" },
                {  8, "ff00:0:0:0:0:0:0:0" },
                { 16, "ffff:0:0:0:0:0:0:0" },
                { 32, "ffff:ffff:0:0:0:0:0:0" },
                { 64, "ffff:ffff:ffff:ffff:0:0:0:0" },
                {128, "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff" },
                {129, null },
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderGetNetmask")
    public void testGetNetmask(int prefixLength, String netmask) {
        assertEquals(cut.getNetmask(prefixLength), netmask);
    }

    @DataProvider(name = "dataProviderCreateAbsoluteAddress")
    protected Object[][] dataProviderCreateAbsoluteAddress() {
        // @formatter:off
        return new Object[][] {
                { null, null, null },
                {          "2001::/16",                                null, null },
                {                 null,                            "::1/16", null },
                {          "2001::/16",                            "::1/16", "2001::1/16" },
                {          "2001::/16",                            "::1/32", "2001::1/32" },
                {          "2001::/16",                            "::1/16", "2001::1/16" },
                {          "2001::/16",                            "::1/32", "2001::1/32" },
                {      "2001:db8::/32",                        "::1:0:1/32", "2001:db8::1:0:1/32" },
                {      "2001:DB8::/32",                            "::1/32", "2001:db8::1/32" },
                { "2001:db8:a001::/48", "0:0:0:ffff:ffff:ffff:ffff:ffff/48", "2001:db8:a001:ffff:ffff:ffff:ffff:ffff/48" },
        };
        // @formatter:on
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderCreateAbsoluteAddress")
    public void testCreateAbsoluteAddress(String prefix, String relativeAddress, String absoluteAddress) {
        assertEquals(cut.createAbsoluteAddress(prefix, relativeAddress), absoluteAddress);
    }

}
