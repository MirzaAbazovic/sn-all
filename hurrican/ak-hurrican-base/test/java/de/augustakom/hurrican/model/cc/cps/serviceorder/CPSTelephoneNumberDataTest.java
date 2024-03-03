/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 15:29:00
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.TNB;

@Test(groups = BaseTest.UNIT)
public class CPSTelephoneNumberDataTest extends BaseTest {

    public void testTransferDNData() {
        final String onkz = "0821";
        final String dnBase = "11111";
        final String directDial = "0";

        Rufnummer dnWithDirectDial = new RufnummerBuilder()
                .withOnKz(onkz)
                .withDnBase(dnBase)
                .withDirectDial(directDial)
                .withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung)
                .build();

        CPSTelephoneNumberData cut = new CPSTelephoneNumberData();
        cut.transferDNData(dnWithDirectDial);
        assertEquals(cut.getLac(), onkz);
        assertEquals(cut.getDn(), dnBase);
        assertEquals(cut.getDirectDial(), directDial);
        assertEquals(cut.getCarrierId(), TNB.AKOM.tnbKennung);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransferDNDataWithInvalidDirectDialNoDigit() {
        Rufnummer dnWithDirectDial = new RufnummerBuilder()
                .withOnKz("0821")
                .withDnBase("11111")
                .withDirectDial("x")
                .build();

        CPSTelephoneNumberData cut = new CPSTelephoneNumberData();
        cut.transferDNData(dnWithDirectDial);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransferDNDataWithInvalidDirectDial() {
        Rufnummer dnWithDirectDial = new RufnummerBuilder()
                .withOnKz("0821")
                .withDnBase("11111")
                .withDirectDial("20")
                .build();

        CPSTelephoneNumberData cut = new CPSTelephoneNumberData();
        cut.transferDNData(dnWithDirectDial);
    }
}
