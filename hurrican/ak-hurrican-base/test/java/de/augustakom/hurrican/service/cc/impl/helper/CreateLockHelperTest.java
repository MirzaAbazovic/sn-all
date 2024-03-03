/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2009 13:32:00
 */
package de.augustakom.hurrican.service.cc.impl.helper;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Lock;


/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CreateLockHelperTest extends BaseTest {
    /**
     * Tested ob die CheckLockModes das richtige macht
     */
    public void testCheckLockModes() {
        CreateLockHelper createLockHelper = new CreateLockHelper();
        // Create a Map for LockMode -> List of allowed ParentLockModes
        @SuppressWarnings("serial")
        Map<Long, List<Long>> allowedModes = new HashMap<Long, List<Long>>() {
            {
                put(Lock.REF_ID_LOCK_MODE_OUTGOING, Arrays.asList(Lock.REF_ID_LOCK_MODE_UNLOCK));
                put(Lock.REF_ID_LOCK_MODE_FULL, Arrays.asList(Lock.REF_ID_LOCK_MODE_UNLOCK));
                put(Lock.REF_ID_LOCK_MODE_CHANGE_TO_FULL, Arrays.asList(Lock.REF_ID_LOCK_MODE_OUTGOING));
                put(Lock.REF_ID_LOCK_MODE_UNLOCK, Arrays.asList(
                        Lock.REF_ID_LOCK_MODE_OUTGOING,
                        Lock.REF_ID_LOCK_MODE_FULL,
                        Lock.REF_ID_LOCK_MODE_CHANGE_TO_FULL
                ));
                put(Lock.REF_ID_LOCK_MODE_DEMONTAGE, Arrays.asList(
                        Lock.REF_ID_LOCK_MODE_OUTGOING,
                        Lock.REF_ID_LOCK_MODE_FULL,
                        Lock.REF_ID_LOCK_MODE_CHANGE_TO_FULL
                ));
            }
        };

        for (Long lockMode : allowedModes.keySet()) {
            for (Long parentLockMode : allowedModes.keySet()) {
                Assert.assertEquals(createLockHelper.checkLockModes(
                                // Erzeuge neuen Integer um zu checken ob mit equals verglichen wird.
                                new Long(parentLockMode),
                                new Long(lockMode)),
                        allowedModes.get(lockMode).contains(parentLockMode)
                );
            }
        }
    }
}
