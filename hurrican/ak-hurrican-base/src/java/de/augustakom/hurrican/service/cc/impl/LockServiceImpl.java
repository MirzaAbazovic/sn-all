/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.2009 10:21:12
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.net.AKMailException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.LockDAO;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.impl.helper.CreateLockHelper;
import de.augustakom.hurrican.service.cc.impl.helper.SendMailHelper;


/**
 * Service-Implementierung von <code>LockService</code>.
 *
 *
 */
@CcTxRequiredReadOnly
public class LockServiceImpl extends DefaultCCService implements LockService {

    private static final Logger LOGGER = Logger.getLogger(LockServiceImpl.class);

    // DAOs
    @Resource(name = "lockDAO")
    LockDAO lockDAO;

    // Helper Klassen
    @Resource(name = "cc.lockCreateLock")
    private CreateLockHelper createLockHelper = null;
    @Resource(name = "cc.lockSendMail")
    private transient SendMailHelper sendMailHelper = null;

    @Override
    @CcTxRequired
    public void createLock(Lock lock, Long sessionId) throws StoreException, AKMailException {

        String user4Lock = null;
        try {
            AKUser user = getAKUserBySessionId(sessionId);
            user4Lock = (user != null) ? user.getName() : "unbekannt";
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            user4Lock = "unbekannt";
        }

        lock.setCreatedFrom(user4Lock);

        Set<Long> abteilungIds = createLockHelper.createLock(lock);
        sendMailHelper.sendMail4Lock(lock, abteilungIds);
    }

    @Override
    public List<Lock> findLocks4Auftrag(Long auftragId, boolean onlyActive) throws FindException {
        return createLockHelper.findLocks4Auftrag(auftragId, onlyActive);
    }

    @Override
    public List<Lock> findLocksByExample(Lock example) throws FindException {
        try {
            return lockDAO.queryByExample(example, Lock.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<LockDetail> findLockDetailsByExample(LockDetail example) throws FindException {
        try {
            return lockDAO.queryByExample(example, LockDetail.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Lock> findActiveLocks() throws FindException {
        try {
            Lock example = new Lock();
            example.setLockStateRefId(Lock.REF_ID_LOCK_STATE_ACTIVE);

            return lockDAO
                    .queryByExample(example, Lock.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Lock findLock(Long lockId) throws FindException {
        if (lockId == null) { return null; }
        try {
            return lockDAO.findById(lockId, Lock.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean hasActiveLock(Long auftragId) throws FindException {
        if (auftragId == null) { throw new FindException(FindException.EMPTY_FIND_PARAMETER); }
        try {
            Lock example = new Lock();
            example.setAuftragId(auftragId);
            example.setLockStateRefId(Lock.REF_ID_LOCK_STATE_ACTIVE);

            List<Lock> activeLocks = lockDAO.queryByExample(example, Lock.class);
            return CollectionTools.isNotEmpty(activeLocks);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequired
    public void saveLock(Lock toSave) throws StoreException {
        if (toSave == null) { return; }
        try {
            lockDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<LockDetail> findLockDetails(Long lockId) throws FindException {
        if (lockId == null) { throw new FindException(FindException.EMPTY_FIND_PARAMETER); }
        try {
            LockDetail example = new LockDetail();
            example.setLockId(lockId);

            return lockDAO.queryByExample(
                    example, LockDetail.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequired
    public void saveLockDetail(LockDetail toSave) throws StoreException {
        if (toSave == null) { return; }
        try {
            lockDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    public void setLockDAO(LockDAO lockDAO) {
        this.lockDAO = lockDAO;
    }

    public void setLockCreateLock(CreateLockHelper createLockHelper) {
        this.createLockHelper = createLockHelper;
    }

    public void setLockSendMail(SendMailHelper sendMailHelper) {
        this.sendMailHelper = sendMailHelper;
    }

    @Override
    @CcTxRequired
    public void finishActiveLocks(Long auftragId) throws StoreException {
        try {
            List<Lock> activeLocks = findLocks4Auftrag(auftragId, true);
            if (CollectionTools.isNotEmpty(activeLocks)) {
                for (Lock lock : activeLocks) {
                    lock.setLockStateRefId(Lock.REF_ID_LOCK_STATE_FINISHED);
                    saveLock(lock);
                }
            }
        }
        catch (FindException e) {
            throw new StoreException(e.getMessage(), e.getCause());
        }
    }

}
