/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2009 09:14:01
 */
package de.augustakom.hurrican.service.cc.impl.helper;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.LockDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.model.cc.SperreVerteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.SperreVerteilungService;


/**
 * Class with main functionality to create a lock
 *
 *
 */
@CcTxRequired
public class CreateLockHelper {
    private static final Logger LOGGER = Logger.getLogger(CreateLockHelper.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;
    @Resource(name = "lockDAO")
    private LockDAO lockDAO;
    @Resource(name = "de.augustakom.hurrican.service.cc.SperreVerteilungService")
    private SperreVerteilungService sperreVerteilungService;

    /**
     * Erstellt einen Lock mit den zugehoerigen LockDetails
     *
     * @param lock Der Lock, der erzeugt werden soll. Die auftragId und createdFrom muss gesetzt sein.
     * @return Die betroffenen Abteilungen
     * @throws StoreException
     */
    public Set<Long> createLock(Lock lock) throws StoreException {
        if (lock.getAuftragId() == null) {
            throw new StoreException(StoreException.UNABLE_TO_CREATE_LOCK, new Object[] { "Auftragsnummer wurde nicht angegeben!" });
        }

        if (lock.getCreatedFrom() == null) {
            throw new StoreException(StoreException.UNABLE_TO_CREATE_LOCK, new Object[] { "User wurde nicht angegeben!" });
        }

        checkActiveLocks(lock);

        Set<Long> abteilungsIds = getAbteilungsIds(lock);

        try {
            saveLock(lock);
            saveLockDetails(lock, abteilungsIds);
            return abteilungsIds;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    StoreException.UNABLE_TO_CREATE_LOCK, new Object[] { e.getLocalizedMessage() }, e);
        }
    }

    /**
     * Pruefe ob es schon aktive Locks auf dem Auftrag gibt, die nicht das parentLock sind.
     *
     * @param lock
     * @throws StoreException
     */
    private void checkActiveLocks(Lock lock) throws StoreException {
        List<Lock> actLocks;
        try {
            actLocks = findLocks4Auftrag(lock.getAuftragId(), true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Suchen der vorhandenen Sperren fuer die AuftragId " + lock.getAuftragId(), e);
        }

        /* If there is an active lock then it should be the parent of the new lock */
        if (CollectionTools.isNotEmpty(actLocks)) {
            for (Lock actLock : actLocks) {
                if (NumberTools.notEqual(actLock.getId(), lock.getParentLockId())) {
                    throw new StoreException(StoreException.UNABLE_TO_CREATE_LOCK,
                            new Object[] { "Es gibt schon eine aktive Sperre auf diesem Auftrag. Sperr-ID: " + actLock.getId() });
                }
            }
        }
    }

    /**
     * Sucht die Abteilungen, die vom Lock betroffen sind.
     *
     * @param lock
     * @return Die Abteilungen die vom Lock betroffen sind
     * @throws StoreException
     */
    private Set<Long> getAbteilungsIds(Lock lock) throws StoreException {
        Set<Long> abteilungsIds;
        try {
            abteilungsIds = getAbteilungIds4Lock(lock.getAuftragId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Einrichten der Sperre ist ein Fehler aufgetreten.", e);
        }
        if ((abteilungsIds == null) || abteilungsIds.isEmpty()) {
            throw new StoreException(StoreException.UNABLE_TO_CREATE_SPERRE,
                    new Object[] { "Es wurden keine Abteilungen angegeben, an die die Sperre verteilt werden soll!" });
        }
        return abteilungsIds;
    }

    /**
     * Speichert die Sperre lock in der Datenbank. Dabei wird das Datum createdAt gesetzt und der Status des parentLock
     * auf finished gesetzt
     *
     * @param lock
     * @throws StoreException
     */
    private void saveLock(Lock lock) throws StoreException {
        // Lock Variablen setzen
        Date now = new Date();
        lock.setCreatedAt(now);

        // Get parentLockId
        Lock parentLock = null;
        Long parentLockId = null;
        parentLockId = lock.getParentLockId();
        if (parentLockId != null) {
            parentLock = lockDAO.findById(lock.getParentLockId(), Lock.class);
            if (parentLock == null) {
                throw new StoreException(StoreException.UNABLE_TO_CREATE_LOCK,
                        new Object[] { "ParentLock existiert nicht!" });
            }
            if (!checkLockModes(parentLock.getLockModeRefId(), lock.getLockModeRefId())) {
                throw new StoreException(StoreException.UNABLE_TO_CREATE_LOCK,
                        new Object[] { "ParentLockStatus ist mit LockStatus nicht kompatibel!" });
            }
            // Parent auf finished setzen
            parentLock.setLockStateRefId(Lock.REF_ID_LOCK_STATE_FINISHED);
            lockDAO.store(parentLock);
        }

        lockDAO.store(lock);
        lock.debugModel(LOGGER);
    }

    /**
     * Prueft, ob der LockStatus auf 'lockState' gesetzt werden kann, wenn der parentLock einen LockState von
     * 'parentLockState' hat. Ist package local zum Unit-Testen
     */
    boolean checkLockModes(Long parentLockModeRefId, Long lockModeRefId) {
        // Wechsel auf Vollsperre nur, wenn schon Teilsperre vorhanden.
        if (NumberTools.equal(lockModeRefId, Lock.REF_ID_LOCK_MODE_CHANGE_TO_FULL) &&
                NumberTools.equal(parentLockModeRefId, Lock.REF_ID_LOCK_MODE_OUTGOING)) {
            return true;
        }
        if (NumberTools.equal(lockModeRefId, Lock.REF_ID_LOCK_MODE_UNLOCK)
                && (NumberTools.equal(parentLockModeRefId, Lock.REF_ID_LOCK_MODE_OUTGOING)
                || NumberTools.equal(parentLockModeRefId, Lock.REF_ID_LOCK_MODE_FULL)
                || NumberTools.equal(parentLockModeRefId, Lock.REF_ID_LOCK_MODE_CHANGE_TO_FULL))) {
            return true;
        }
        if (NumberTools.equal(lockModeRefId, Lock.REF_ID_LOCK_MODE_DEMONTAGE)
                && NumberTools.notEqual(parentLockModeRefId, Lock.REF_ID_LOCK_MODE_UNLOCK)
                && NumberTools.notEqual(parentLockModeRefId, Lock.REF_ID_LOCK_MODE_DEMONTAGE)) {
            return true;
        }
        if ((NumberTools.equal(lockModeRefId, Lock.REF_ID_LOCK_MODE_OUTGOING)
                || NumberTools.equal(lockModeRefId, Lock.REF_ID_LOCK_MODE_FULL))
                && NumberTools.equal(parentLockModeRefId, Lock.REF_ID_LOCK_MODE_UNLOCK)) {
            return true;
        }
        return false;

    }

    /**
     * Erzeugt LockDetails fuer die angegebenen Abteilungen
     *
     * @param lock   Das Lock, zu dem die LockDetails gehoeren.
     * @param abtIds Die Abeilungen, die von lock betroffen sind
     */
    private void saveLockDetails(Lock lock, Set<Long> abtIds) {
        Long lockId = lock.getId();

        // LockDetail fuer die betroffenen Abteilungen erstellen
        for (Long abteilungId : abtIds) {
            LockDetail lockDetail = new LockDetail();
            lockDetail.setLockId(lockId);
            lockDetail.setAbteilungId(abteilungId);
            // Wichtig: kein Datum setzen!!!
            lockDAO.store(lockDetail);
            lockDetail.debugModel(LOGGER);
        }
    }

    /**
     * Sucht nach den Abteilungen, fuer die ein LockDetail-Eintrag fuer den Kunden-Auftraeg erstellt werden soll.
     * Funktioniert ueber: Lock -> Auftrag -> Produkt -> SperreVerteilung -> Abteilung
     *
     * @return Die betroffenen Abteilungen
     */
    private Set<Long> getAbteilungIds4Lock(Long auftragId) throws ServiceNotFoundException, FindException {
        AuftragDaten ad = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
        if (ad == null) {
            throw new FindException(
                    "Es konnten keine Daten zum zu sperrenden Auftrag gefunden werden.\n" +
                            "Deshalb kann keine Sperre eingerichtet werden."
            );
        }

        Set<Long> abteilungen = new HashSet<>();
        List<SperreVerteilung> svs = sperreVerteilungService.findSperreVerteilungen4Produkt(ad.getProdId());
        if (svs != null) {
            for (SperreVerteilung sv : svs) {
                Long abtId = sv.getAbteilungId();
                abteilungen.add(abtId);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Lock: zu informierende Abteilung: " + abtId);
                }
            }
        }

        if (abteilungen.isEmpty()) {
            throw new FindException(
                    "Es konnten keine Abteilungen ermittelt werden, die über den Lock informiert werden müssen.\n" +
                            "Deshalb kann der Lock nicht angelegt werden!"
            );
        }

        return abteilungen;
    }

    public List<Lock> findLocks4Auftrag(Long auftragId, boolean onlyActive) throws FindException {
        if (auftragId == null) { return null; }
        try {
            return lockDAO.findByAuftragId(auftragId, onlyActive);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    public void setLockDAO(LockDAO dao) {
        this.lockDAO = dao;
    }

    public void setSperreVerteilungService(SperreVerteilungService sperreVerteilungService) {
        this.sperreVerteilungService = sperreVerteilungService;
    }


}
