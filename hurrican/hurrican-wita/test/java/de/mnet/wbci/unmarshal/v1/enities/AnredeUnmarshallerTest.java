/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

@Test(groups = BaseTest.UNIT)
public class AnredeUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Autowired
    private AnredeUnmarshaller testling;

    @DataProvider(name = "testApplyDataProvider")
    public Object[][] testApplyDataProvider() {
        return new Object[][] {
                { "1", Anrede.HERR },
                { "2", Anrede.FRAU },
                { "4", Anrede.FIRMA },
                { "9", Anrede.UNBEKANNT },
                { null, Anrede.UNBEKANNT },
        };
    }

    @Test(dataProvider = "testApplyDataProvider")
    public void testApply(String input, Anrede expected) throws Exception {
        Assert.assertEquals(testling.apply(input), expected);
    }

}
