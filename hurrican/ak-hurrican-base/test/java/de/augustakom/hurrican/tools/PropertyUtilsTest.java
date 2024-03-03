/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2012 15:04:39
 */
package de.augustakom.hurrican.tools;

import static org.testng.Assert.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;

/**
 * Tested EGConfigPanelIPModel.xml in dem das Property "ipAddressRef.absoluteAddress" ausgewertet werden soll.
 *
 *
 */
@Test(groups = BaseTest.UNIT)
public class PropertyUtilsTest {

    private IPAddress address;

    @BeforeMethod
    public void setup() {
        IPAddress prefix = new IPAddress();
        prefix.setIpType(AddressTypeEnum.IPV6_prefix);
        prefix.setAddress("2001:0db8:a001::/48");

        address = new IPAddress();
        address.setIpType(AddressTypeEnum.IPV6_relative);
        address.setAddress("0:0:0:abc::/48");
        address.setPrefixRef(prefix);
    }

    @Test
    public void testDerivedProperty() throws Exception {
        assertEquals(address.getAbsoluteAddress(), "2001:db8:a001:abc::/48");
        assertEquals(address.getAbsoluteAddress(), PropertyUtils.getProperty(address, "absoluteAddress"));
    }

    @Test
    public void testNestedDerivedProperty() throws Exception {
        OuterClass outer = new OuterClass();
        outer.setNested(address);
        assertEquals(outer.getNested().getAbsoluteAddress(), "2001:db8:a001:abc::/48");
        assertEquals(outer.getNested().getAbsoluteAddress(), PropertyUtils.getProperty(outer, "nested.absoluteAddress"));
    }

    public static class OuterClass {
        private IPAddress nested;

        public void setNested(IPAddress nested) {
            this.nested = nested;
        }

        public IPAddress getNested() {
            return nested;
        }
    }

}


