/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2004 11:02:31
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.net.AKMailException;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockBuilder;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.model.cc.LockDetailBuilder;
import de.augustakom.hurrican.model.cc.SperreInfoBuilder;
import de.augustakom.hurrican.model.cc.SperreVerteilungBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.mock.CCMockFactory;


/**
 * TestNG-Test fuer <code>LockService</code>.
 *
 *
 */
public class LockServiceTest extends AbstractHurricanBaseServiceTest {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(LockServiceTest.class);
    private MailSender mailSender = null;
    private AuftragDatenBuilder auftragDatenBuilder = null;
    private SperreVerteilungBuilder sperreVerteilungBuilder = null;
    private LockService lockService = null;

    public LockServiceTest() {
        super();
    }

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        // get the MailSender mock
        mailSender = CCMockFactory.getMailSender();
        // get the LockService
        lockService = getCCService(LockService.class);
        // Reset the mock
        reset(mailSender);
        // Build AuftragDaten with Auftrag
        auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        auftragDatenBuilder.build();
        // Build a SperreVerteilung
        sperreVerteilungBuilder = getBuilder(SperreVerteilungBuilder.class)
                .withProduktBuilder(auftragDatenBuilder.getProdBuilder());
        sperreVerteilungBuilder.build();
    }


    /**
     * Tested die Methode createLock. Checkt ob die Email versandt wurde und ob nachher ein Lock auf dem Auftrag ist.
     *
     * @throws StoreException
     * @throws AKMailException
     * @throws FindException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testCreateLock() throws StoreException, AKMailException, FindException {
        //Build 2 SperreInfo for the corresponding Abteilung
        getBuilder(SperreInfoBuilder.class)
                .withAbteilungBuilder(sperreVerteilungBuilder.getAbteilungBuilder())
                .withEmail("Test1@test.de")
                .build();
        getBuilder(SperreInfoBuilder.class)
                .withAbteilungBuilder(sperreVerteilungBuilder.getAbteilungBuilder())
                .withEmail("Test2@test.de")
                .build();
        Lock lock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withCreatedAt(null)
                .withCreatedFrom(null)
                .setPersist(false)
                .build();

        // Prepare for the test
        ArgumentCaptor<SimpleMailMessage> mailMessage = ArgumentCaptor.forClass(SimpleMailMessage.class);
        // Call the Method
        lockService.createLock(lock, getSessionId());
        // Test the result
        verify(mailSender).send(mailMessage.capture());
        Assert.assertNotNull(lock.getCreatedFrom());
        Assert.assertEquals(mailMessage.getValue().getFrom(), "Hurrican");
        Assert.assertEquals(mailMessage.getValue().getTo(), new String[] { "Test1@test.de", "Test2@test.de" });
        Assert.assertTrue(lockService.hasActiveLock(lock.getAuftragId()));
        Assert.assertEquals(lockService.findLockDetails(lock.getId()).size(), 1);
        Assert.assertEquals(lockService.findLockDetails(lock.getId()).get(0).getAbteilungId(),
                sperreVerteilungBuilder.getAbteilungBuilder().get().getId());
    }

    /**
     * Wenn es schon einen aktiven Lock auf den Auftrag gibt, der nicht der parentLock ist, dann werfe StoreException
     *
     * @throws StoreException
     * @throws AKMailException
     * @throws FindException
     */
    @Test(groups = { "service" },
            expectedExceptions = StoreException.class)
    public void testCreateLockNoPreviousLocks() throws StoreException, AKMailException, FindException {
        // Erstelle die beiden Locks
        Lock lock1 = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .setPersist(false)
                .build();
        lockService.createLock(lock1, getSessionId());

        Lock lock2 = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .setPersist(false)
                .build();
        // Das sollte eine StoreException werfen
        lockService.createLock(lock2, getSessionId());
    }

    /**
     * Tested ob keine Email gesendet wird, wenn keine Email im SperreInfo hinterlegt ist
     *
     * @throws StoreException
     * @throws AKMailException
     * @throws FindException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testCreateLockNoEmail() throws StoreException, AKMailException, FindException {
        Lock lock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .setPersist(false)
                .build();

        // Call the Method
        lockService.createLock(lock, getSessionId());
        // Test the result
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        Assert.assertTrue(lockService.hasActiveLock(lock.getAuftragId()));
    }

    /**
     * Tested ob createLock beim Parent-Lock den Status auf finished setzt
     *
     * @throws StoreException
     * @throws AKMailException
     * @throws FindException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testCreateLockParentLockFinished() throws StoreException, AKMailException, FindException {
        Pair<Lock, Lock> locks = createLocks();

        // Test the result
        Assert.assertEquals(locks.getFirst().getLockStateRefId(), Lock.REF_ID_LOCK_STATE_FINISHED);
        Assert.assertEquals(locks.getSecond().getLockStateRefId(), Lock.REF_ID_LOCK_STATE_ACTIVE);
    }

    /**
     * Tested ob die Parent und die Child LockModes kompatibel sein muessen
     *
     * @throws StoreException
     * @throws AKMailException
     * @throws FindException
     */
    @Test(groups = { "service" },
            expectedExceptions = StoreException.class)
    public void testCreateLocksCheckLockModes() throws StoreException, AKMailException, FindException {
        // Create the parent lock with lockMode OUTGOING
        Lock parentLock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                        // Use new Integer to check if equals is used
                .withLockModeRefId(Long.valueOf(Lock.REF_ID_LOCK_MODE_OUTGOING))
                .setPersist(false)
                .build();
        lockService.createLock(parentLock, getSessionId());

        // create the lock with lockMode FULL
        Lock lock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withParentLock(parentLock)
                .withLockModeRefId(Long.valueOf(Lock.REF_ID_LOCK_MODE_FULL))
                .setPersist(false)
                .build();
        lockService.createLock(lock, getSessionId());
    }

    /**
     * Tested findActiveLocks
     *
     * @throws FindException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFindActiveLocks() throws FindException {
        // Create an active Lock
        Lock activeLock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withLockModeStateId(Long.valueOf(Lock.REF_ID_LOCK_STATE_ACTIVE))
                .build();
        // Create an inactive Lock
        Lock finishedLock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withLockModeStateId(Long.valueOf(Lock.REF_ID_LOCK_STATE_FINISHED))
                .build();
        List<Lock> activeLocks = lockService.findActiveLocks();
        Assert.assertTrue(activeLocks.contains(activeLock));
        Assert.assertFalse(activeLocks.contains(finishedLock));
    }

    private Pair<Lock, Lock> createLocks() throws StoreException, AKMailException {
        // Create the parent lock
        Lock parentLock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withLockModeRefId(Lock.REF_ID_LOCK_MODE_OUTGOING)
                .setPersist(false)
                .build();
        lockService.createLock(parentLock, getSessionId());

        // create the lock
        Lock lock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withParentLock(parentLock)
                .withLockModeRefId(Lock.REF_ID_LOCK_MODE_CHANGE_TO_FULL)
                .setPersist(false)
                .build();
        lockService.createLock(lock, getSessionId());
        return Pair.create(parentLock, lock);
    }

    /**
     * Tested FindLocks4Auftrag
     *
     * @throws StoreException
     * @throws AKMailException
     * @throws FindException
     */
    @Test(groups = { "service" })
    public void testFindLocks4Auftrag() throws StoreException, AKMailException, FindException {
        Lock lock = createLocks().getSecond();

        // suche erst nur die aktiven Sperren
        List<Lock> locks = lockService.findLocks4Auftrag(auftragDatenBuilder.get().getAuftragId(), true);
        Assert.assertEquals(locks.size(), 1);
        Assert.assertEquals(locks.get(0), lock);
        // suche dann alle Sperren
        List<Lock> allLocks = lockService.findLocks4Auftrag(auftragDatenBuilder.get().getAuftragId(), false);
        Assert.assertEquals(allLocks.size(), 2);
    }

    @Test(groups = { "service" })
    public void testFinishActiveLocks() throws Exception {
        Long auftragId = auftragDatenBuilder.get().getAuftragId();
        Pair<Lock, Lock> createdLocks = createLocks();
        Assert.assertEquals(createdLocks.getFirst().getLockStateRefId(), Lock.REF_ID_LOCK_STATE_FINISHED);
        Assert.assertEquals(createdLocks.getSecond().getLockStateRefId(), Lock.REF_ID_LOCK_STATE_ACTIVE);

        lockService.finishActiveLocks(auftragId);

        List<Lock> finishedLocks = lockService.findLocks4Auftrag(auftragId, false);
        Assert.assertEquals(finishedLocks.size(), 2);
        for (Lock lock : finishedLocks) {
            Assert.assertEquals(lock.getLockStateRefId(), Lock.REF_ID_LOCK_STATE_FINISHED);
        }
    }

    /**
     * Tested findLock
     *
     * @throws FindException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFindLock() throws FindException {
        // Create an active Lock
        Lock lock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .build();
        Lock foundLock = lockService.findLock(lock.getId());
        Assert.assertEquals(foundLock, lock);
    }

    /**
     * Tested saveLock
     *
     * @throws FindException
     * @throws StoreException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testSaveLock() throws FindException, StoreException {
        // Create an active Lock
        Lock lock = getBuilder(LockBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .setPersist(false)
                .build();

        lockService.saveLock(lock);
        Lock foundLock = lockService.findLock(lock.getId());
        Assert.assertEquals(foundLock, lock);
    }

    /**
     * Tested saveLockDetail und findLockDetail
     *
     * @throws FindException
     * @throws StoreException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testSaveFindLockDetail() throws FindException, StoreException {
        LockDetail lockDetail = getBuilder(LockDetailBuilder.class)
                .build();
        lockService.saveLockDetail(lockDetail);

        List<LockDetail> foundLockDetail = lockService.findLockDetails(lockDetail.getLockId());
        Assert.assertEquals(foundLockDetail.size(), 1);
        Assert.assertTrue(foundLockDetail.contains(lockDetail));
    }

}


