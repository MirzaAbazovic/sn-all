/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2009 11:48:08
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.utils;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSTelephoneNumberData;


/**
 * Test NG Klasse fuer CPSPhoneNumberModificator
 *
 *
 */
@Test(groups = { "unit" })
public class CPSPhoneNumberModificatorTest {

    /**
     * Test fuer {@link CPSPhoneNumberModificator#modifyDirectDial(java.util.List, de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSDNData)}
     */
    @SuppressWarnings("unchecked")
    public void testModifyDirectDial() {
        List<CPSTelephoneNumberData> ewsdPhoneNumbers = new ArrayList<CPSTelephoneNumberData>();

        CPSTelephoneNumberData number1 = new CPSTelephoneNumberData();
        number1.setLac("0821");
        number1.setDn("123456");
        number1.setDirectDial("0");
        ewsdPhoneNumbers.add(number1);

        CPSTelephoneNumberData number2 = new CPSTelephoneNumberData();
        number2.setLac("0821");
        number2.setDn("123456");
        number2.setDirectDial("5");

        String directDial3 = "7";
        CPSTelephoneNumberData number3 = new CPSTelephoneNumberData();
        number3.setLac("0821");
        number3.setDn("55555");
        number3.setDirectDial(directDial3);

        CPSPhoneNumberModificator.modifyDirectDial((List) ewsdPhoneNumbers, number2);
        Assert.assertEquals(number2.getDirectDial(), number1.getDirectDial(), "DIRECT_DIAL not modified!");

        CPSPhoneNumberModificator.modifyDirectDial((List) ewsdPhoneNumbers, number3);
        Assert.assertEquals(number3.getDirectDial(), directDial3, "DIRECT_DIAL is modified but should not!");
    }

}


