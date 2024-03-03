/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public class WbciGeschaeftsfallStatusTest {

    @DataProvider
    public Object[][] legalStatusChanges() {
        return new Object[][] {
                { ACTIVE, ACTIVE },
                { ACTIVE, COMPLETE },
                { ACTIVE, PASSIVE },
                { ACTIVE, NEW_VA },
                { PASSIVE, PASSIVE },
                { PASSIVE, ACTIVE },
                { PASSIVE, COMPLETE },
                { PASSIVE, NEW_VA },
                { NEW_VA, NEW_VA },
                { NEW_VA, COMPLETE },
                { NEW_VA, NEW_VA_EXPIRED },
                { NEW_VA_EXPIRED, NEW_VA_EXPIRED },
                { NEW_VA_EXPIRED, COMPLETE },
                { COMPLETE, COMPLETE },
        };
    }

    @DataProvider
    public Object[][] illegalStatusChanges() {
        return new Object[][] {
                { COMPLETE, ACTIVE },
                    { ACTIVE, NEW_VA_EXPIRED },
                    { NEW_VA, PASSIVE },
                    { PASSIVE, null },
        };
    }

    @Test
    public void testGetDescription() throws Exception {
        for (WbciGeschaeftsfallStatus wbciGeschaeftsfallStatus : WbciGeschaeftsfallStatus.values()) {
            Assert.assertNotNull(wbciGeschaeftsfallStatus.getDescription());
        }
    }

    @Test
    public void testGetNextLegalStatusChanges() throws Exception {
        for (WbciGeschaeftsfallStatus wbciGeschaeftsfallStatus : WbciGeschaeftsfallStatus.values()) {
            Assert.assertNotNull(wbciGeschaeftsfallStatus.getDescription());
        }
    }

    @Test(dataProvider = "legalStatusChanges")
    public void testLegalStatusChanges(WbciGeschaeftsfallStatus currentStatus, WbciGeschaeftsfallStatus newStatus) throws Exception {
        Assert.assertTrue(currentStatus.isLegalStatusChange(newStatus));
    }

    @Test(dataProvider = "illegalStatusChanges")
    public void testIllegalStatusChanges(WbciGeschaeftsfallStatus currentStatus, WbciGeschaeftsfallStatus newStatus) throws Exception {
        Assert.assertFalse(currentStatus.isLegalStatusChange(newStatus));
    }
}
