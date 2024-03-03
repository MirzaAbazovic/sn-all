/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 08:54:18
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AbteilungBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.LockBuilder;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.model.cc.LockDetailBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.LockService;


/**
 * Unit Test for {@link DisjoinCPSTxCommandTest}
 *
 *
 */
public class DisjoinCPSTxCommandTest extends AbstractHurricanBaseServiceTest {

    private CPSTransaction cpsTx;
    private LockDetail lockDetail;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_LOCK_SUB)
                .withEstimatedExecTime(new Date())
                .build();

        lockDetail = getBuilder(LockDetailBuilder.class)
                .setLockBuilder(getBuilder(LockBuilder.class))
                .setAbteilungBuilder(getBuilder(AbteilungBuilder.class))
                .withCpsTxId(cpsTx.getId())
                .build();
    }

    /**
     * Test for {@link DisjoinCPSTxCommand#equals(Object)}
     */
    @Test(groups = BaseTest.SERVICE)
    public void testExecuteDisjoinLock() throws Exception {
        Assert.assertNotNull(lockDetail.getCpsTxId(), "LockDetail is not assigned to CPS-Tx!");

        LockService lockService = getCCService(LockService.class);

        DisjoinCPSTxCommand disjoinCmd = new DisjoinCPSTxCommand();
        disjoinCmd.setLockService(lockService);
        disjoinCmd.setCcRufnummernService(getCCService(CCRufnummernService.class));

        disjoinCmd.prepare(DisjoinCPSTxCommand.KEY_CPS_TX_ID, cpsTx.getId());
        disjoinCmd.prepare(DisjoinCPSTxCommand.KEY_SESSION_ID, getSessionId());

        disjoinCmd.execute();

        List<LockDetail> lockDetails = lockService.findLockDetails(lockDetail.getLockId());
        assertNotEmpty(lockDetails, "No LockDetails found for Lock-ID!");
        for (LockDetail lockDetail : lockDetails) {
            Assert.assertNull(lockDetail.getCpsTxId(), "LockDetail is still assigned to CPS-Tx!");
            Assert.assertNull(lockDetail.getExecutedAt(), "LockDetail.executedAt is not removed!");
            Assert.assertNull(lockDetail.getExecutedFrom(), "LockDetail.executedFrom is not removed");
        }
    }

}


