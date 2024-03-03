/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2012 15:46:05
 */
package de.augustakom.common.tools.net;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * TestNG-Test fuer {@link IPValidationTool}.
 */
@Test(groups = { BaseTest.UNIT })
public class IPValidationToolTest {

    @DataProvider(name = "dataProviderValidateV4WithoutPrefix")
    protected Object[][] dataProviderValidateV4WithoutPrefix() {
        // @formatter:off
        return new Object[][] {
                { "192.168.0.1",        true },
                { "0.0.0.0",            true },
                { "255.255.255.255",    true },
                { "256.255.255.255",    false },
                { "256.168.0.1",        false },
                { "192.168.0.1/0",      false },
                { "192.168.0.1/01",     false },
                { "192.168.0.1/32",     false },
                { "192.168.0.1/33",     false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateV4WithoutPrefix")
    public void testValidateAddressV4WithoutPrefix(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV4WithoutPrefix(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateV6WithoutPrefix")
    protected Object[][] dataProviderValidateV6WithoutPrefix() {
        // @formatter:off
        return new Object[][] {
                {   "1:1:1:1:1:1:1:1",  true },
                {         "1:2:3::/0",  false },
                {        "1:2:3::/01",  false },
                {       "1:2:3::/128",  false },
                {       "1:2:3::/129",  false },
                {       "::",           true },
                {       "::/64",        false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateV6WithoutPrefix")
    public void testValidateAddressV6WithoutPrefix(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV6WithoutPrefix(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateV6WithPrefix")
    protected Object[][] dataProviderValidateV6WithPrefix() {
        // @formatter:off
        return new Object[][] {
                {   "1:1:1:1:1:1:1:1", false },
                {         "1:2:3::/0", false },
                {        "1:2:3::/01", true },
                {       "1:2:3::/128", true },
                {       "1:2:3::/129", false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateV6WithPrefix")
    public void testValidateAddressV6WithPrefix(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV6WithPrefix(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateV4WithPrefix")
    public Object[][] dataProviderValidateV4WithPrefix() {
        // @formatter:off
        return new Object[][] {
                {    "192.168.0.1", false },
                {  "192.168.0.1/0", false },
                { "192.168.0.1/01", true },
                { "192.168.0.1/32", true },
                { "192.168.0.1/33", false },
                { "192.168.0.1/33", false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateV4WithPrefix")
    public void testValidateAddressv4WithPrefix(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV4WithPrefix(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateV4")
    public Object[][] dataProviderValidateV4() {
        // @formatter:off
        return new Object[][] {
                {             null, false },
                {               "", false },
                {     "192.168.0.", false },
                {  "192.168.0.1.1", false },
                {    "192:168.0.1", false },
                {     "192168.0.1", false },
                {   "192.168.0.1/", false },
                {    "192.168.0.1", true  },
                {  "192.168.0.1/0", false },
                { "192.168.0.1/01", true  },
                { "192.168.0.1/32", true  },
                { "192.168.0.1/33", false },
                { "256.0.0.0",      false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateV4")
    public void testValidateAddressv4WithoutPrefix(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV4(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateAddressV6")
    protected Object[][] dataProviderValidateAddressV6() {
        // @formatter:off
        return new Object[][] {
                {                null,  false },
                {                  "",  false },
                {             "ffff:",  false },
                { "0:0:0:0:0:0:0:0:0",  false },
                {          "ffff.0::",  false },
                {       "ffffabcde::",  false },
                {           "ffff::/",  false },
                {     "2001:db8:a001",  false },
                {       "aBCd:eF00::",  true },
                {   "1:1:1:1:1:1:1:1",  true },
                {         "1:2:3::/0",  false },
                {        "1:2:3::/01",  true },
                {       "1:2:3::/128",  true },
                {       "1:2:3::/129",  false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateAddressV6")
    public void testValidateAddressV6(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV6(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidatePrefixV6")
    protected Object[][] dataProviderValidatePrefixV6() {
        // @formatter:off
        return new Object[][] {
                {                                               null,  false },
                {                                                 "",  false },
                {                                            "ffff:",  false },
                {                                "0:0:0:0:0:0:0:0:0",  false },
                {                                         "ffff.0::",  false },
                {                                      "ffffabcde::",  false },
                {                                           "ffff::",  false },
                {                                          "ffff::/",  false },
                {                                         "ffff::/0",  false },
                {      "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff/129",  false },
                { "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff/144",  false },
                {                                "2001:Db8:a001::/47", false },
                {                                             "::/0",  true },
                {                                "0:0:0:0:0:0:0:0/0",  true },
                {      "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff/128",  true },
                {                                "2001:Db8:a001::/48", true },
                {                                "2001:Db8:a001::/64", true },
                {                                        "ff8f::/16",  true },
                {                                        "ff80::/09",  true },
                {                                        "fff8::/13",  true },
                {                                        "ffff::/16",  true },
                {                                   "ffff:8000::/17",  true },
                {      "ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe/127",  true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidatePrefixV6")
    public void testValidatePrefixV6(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV6Prefix(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateRelativeEUI64AddressV6")
    protected Object[][] dataProviderValidateRelativeEUI64AddressV6() {
        // @formatter:off
        return new Object[][] {
                {                  null,                             null, false },
                {                    "",                             null, false },
                {                    "",                               "", false },
                {  "2001:db8:a001::/48",                   "0:0:0:1::/54", false },
                {  "2001:db8:a001::/78",                   "0:0:0:1::/64", false },
                {  "2001:db8:a001::/48",                 "0:0:0:1:1::/64", false },
                {  "2001:db8:a001::/48",                   "0:0:0:1::/64", true  },
                {  "2001:db8:a001::/48",                   "0:0:0:1::/65", false },
                {  "2001:db8:a001::/64",                   "0:0:0:1::/64", false },
                {  "2001:db8:a001::/64",                 "0:0:0:0:1::/64", false },
                {  "2001:db8:a001::/64",                          "::/64", true  },
                {  "2001:db8:a001::/64",                   "0:0:0:1::/64", false },
                { "2001:db8:a001::/064",                   "0:0:0:0::/64", true  },
                {  "2001:db8:a001::/64",                          "::/064", true  },
                {       "2001:db8::/32",       "0:0:ffff:ffff:0:0:0:0/64", true  },
                {       "2001:db8::/32",       "0:0:ffff:ffff:1:0:0:0/64", false  },
                {       "2001:db8::/49",                "0:0:0:7fff::/64", true  },
                {       "2001:db8::/49",                   "0:0:0:1::/64", true  },
                {       "2001:db8::/49",                "0:0:0:8fff::/64", false  },
                {       "2001:db8::/49",                   "0:0:1:0::/64", false  },
                {       "2001:db8::/49",                   "0:1:0:0::/64", false  },
                {       "2001:db8::/49",                   "1:1:0:0::/64", false  },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateRelativeEUI64AddressV6")
    public void testValidateRelativeEUI64AddressV6(String validPrefix, String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV6EUI64Relativ(validPrefix, address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateRelativeAddressV6")
    protected Object[][] dataProviderValidateRelativeAddressV6() {
        // @formatter:off
        return new Object[][] {
                {                   null,                                null, false },
                {                     "",                                null, false },
                {                     "",                                  "", false },
                {          "2001::/0016",                          "1::/0016", false },
                {         "2001:db8:/32",                          "0:1::/48", false },
                {         "2001:db8:/32",                        "0:0:1::/16", false },
                {  "2001:db8:a001::/128",                            "::/129", false },
                {   "2001:db8:a001::/64",                      "0:0:0:1::/48", false },
                {   "2001:db8:a001::/64",                             "::/63", false },
                {   "2001:db8:a001::/48",                         "0:0:0:1::", false },
                {   "2001:db8:a001::/48",                         "0:0:0:1::", false },
                {   "2001:db8:a001::/49",                      "0:0:0:1::/48", false },
                {   "2001:db8:a001::/48",                     "0:0:0:1::/129", false },
                {   "2001:db8:a001::/48",                   "0:0:0:8fff::/49", true  },
                {   "2001:db8:a001::/48",                      "0:0:0:1::/48", true  },
                {  "2001:db8:a001::/048",                      "0:0:0:1::/48", true  },
                {   "2001:db8:a001::/48",                     "0:0:0:1::/048", true  },
                {   "2001:db8:a001::/48",                    "0:0:0:0:1::/64", true  },
                {   "2001:db8:a001::/48",    "0:0:0:0:ffff:ffff:ffff:ffff/64", true  },
                {   "2001:db8:a001::/48", "0:0:0:ffff:ffff:ffff:ffff:ffff/64", true  },
                {  "2001:db8:a001::/128",                            "::/128", true  },
                {   "2001:db8:a001::/64",                             "::/64", true  },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateRelativeAddressV6")
    public void testValidateRelativeAddressV6(String validPrefix, String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV6Relative(validPrefix, address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateIPV6EUI64")
    protected Object[][] dataProviderValidateIPV6EUI64() {
        // @formatter:off
        return new Object[][] {
                {   "2001:0db8:a001:0001::/64",      true},
                {                      "::/64",      true},
                {   "2001:0db8:a001:0001::/63",      false},
                {   "2001:0db8:a001:0001::/65",      false},
                {   "2001:0db8:a001:0001::",         false},
                {   "2001:0db8:a001:0001:0001::/64", false},
                {   "2001:0db8:a001:0001:1::/64",    false},
                {   "2001:0db8:a001:0001::1/64",     false},
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateIPV6EUI64")
    public void testValidateIPV6EUI64(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV6EUI64(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

    @DataProvider(name = "dataProviderValidateIPV4Prefix")
    protected Object[][] dataProviderValidateIPV4Prefix() {
        // @formatter:off
        return new Object[][] {
                { "192.168.1.1/32", true },
                { "192.168.1.0/24", true },
                { "192.168.0.0/16", true },
                { "192.0.0.0/8",    true },
                { "0.0.0.0/1",      true },
                { "128.0.0.0/1",    true },
                { "0.0.0.0/0",      true },
                { "192.168.1.1/0",  false },
                { "192.168.1.1/24", false },
                { "127.0.0.0/1",    false },
                { "129.0.0.0/1",    false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValidateIPV4Prefix")
    public void testValidateIPV4Prefix(String address, boolean expectedResult) {
        boolean validationResult = IPValidationTool.validateIPV4Prefix(address).isValid();
        assertEquals(validationResult, expectedResult);
    }

}


