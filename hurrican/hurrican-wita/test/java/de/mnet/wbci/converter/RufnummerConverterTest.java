/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.14
 */
package de.mnet.wbci.converter;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class RufnummerConverterTest extends BaseTest {

    public void testConvertBillingDn() {
        Assert.assertEquals(
                RufnummerConverter.convertBillingDn(
                        new RufnummerBuilder()
                                .withOnKz("089")
                                .withDnBase("1233")
                                .build()
                ),
                "089/1233"
        );
        Assert.assertEquals(
                RufnummerConverter.convertBillingDn(
                        new RufnummerBuilder()
                                .withOnKz("089")
                                .withDnBase("1234")
                                .build()
                ),
                "089/1234"
        );
        Assert.assertEquals(
                RufnummerConverter.convertBillingDn(
                        new RufnummerBuilder()
                                .withOnKz("089")
                                .withDnBase("4500")
                                .withDirectDial("0")
                                .withRangeFrom("00")
                                .withRangeTo("99")
                                .build()
                ),
                "089/4500 (0) - 00 bis 99"
        );
    }

}
