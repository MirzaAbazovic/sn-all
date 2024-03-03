/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.07.2009 12:25:19
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungCPSPredicate;


/**
 * Command-Klasse, um aus Sperr-Eintraegen die notwendigen CPS-Transactions zu generieren.
 *
 *
 */
public class CreateCPSTx4LockCommand extends AbstractCPSCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateCPSTx4LockCommand.class);

    private static final String LOCK_MODE_UNLOCK = "UNLOCK";
    private static final String LOCK_MODE_OUTGOING = "OUTGOING";
    private static final String LOCK_MODE_CHANGE_TO_FULL = "CHANGE_TO_FULL";
    private static final String LOCK_MODE_FULL = "FULL";

    private LockService lockService;

    private Map<Long, List<LockDetail>> lock2LockDetailMap;
    private Map<Long, String> lockModeRefId2CpsModeMap;

    /**
     * called by Spring
     */
    public void init() {
        lock2LockDetailMap = new HashMap<Long, List<LockDetail>>();

        lockModeRefId2CpsModeMap = new HashMap<Long, String>();
        lockModeRefId2CpsModeMap.put(Lock.REF_ID_LOCK_MODE_OUTGOING, LOCK_MODE_OUTGOING);
        lockModeRefId2CpsModeMap.put(Lock.REF_ID_LOCK_MODE_CHANGE_TO_FULL, LOCK_MODE_CHANGE_TO_FULL);
        lockModeRefId2CpsModeMap.put(Lock.REF_ID_LOCK_MODE_FULL, LOCK_MODE_FULL);
        lockModeRefId2CpsModeMap.put(Lock.REF_ID_LOCK_MODE_UNLOCK, LOCK_MODE_UNLOCK);
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            loadRequiredData();

            List<Lock> activeLocks = lockService.findActiveLocks();
            List<Lock> filteredLocks = filterLocks(activeLocks);

            if (CollectionTools.isNotEmpty(filteredLocks)) {
                // CPS-Transaction pro Lock erstellen
                List<CPSTransaction> cpsTransactions = createCPSTransactions(filteredLocks);
                return cpsTransactions;
            }
            else {
                LOGGER.info("WARNINGS: " + ((getWarnings() != null) ? getWarnings().getWarningsAsText() : ""));
                return null;
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * Erstellt fuer die angegebenen Sperren die CPS-Transactions.
     *
     * @param locks
     * @return
     * @throws ServiceCommandException
     */
    List<CPSTransaction> createCPSTransactions(List<Lock> locks) throws ServiceCommandException {
        try {
            List<CPSTransaction> result = new ArrayList<CPSTransaction>();
            for (Lock lock : locks) {
                CPSTransaction cpsTx = null;
                try {
                    String lockMode = lockModeRefId2CpsModeMap.get(lock.getLockModeRefId());
                    if (StringUtils.isBlank(lockMode)) {
                        throw new HurricanServiceCommandException(
                                "Could not load LOCK_MODE for CPS-Tx. Reference ID from lock is: " + lock.getLockModeRefId());
                    }

                    // Ausfuehrungszeitpunkt auf "in 5 Minuten" setzen
                    Date execDateTime = DateTools.changeDate(new Date(), Calendar.MINUTE, 5);

                    // CPS-Transaction anlegen
                    CPSTransactionResult txResult = cpsService.createCPSTransaction(
                            new CreateCPSTransactionParameter(lock.getAuftragId(), null, CPSTransaction.SERVICE_ORDER_TYPE_LOCK_SUB, CPSTransaction.TX_SOURCE_HURRICAN_LOCK,
                                    CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT, execDateTime, null, null, lockMode, null, Boolean.FALSE, Boolean.FALSE,
                                    getSessionId())
                    );

                    if (CollectionTools.isNotEmpty(txResult.getCpsTransactions())) {
                        cpsTx = txResult.getCpsTransactions().get(0);
                        markLockDetailsAsProvisioned(lock, cpsTx);
                        result.add(cpsTx);
                    }
                    else {
                        String warnings = (txResult.getWarnings() != null)
                                ? txResult.getWarnings().getWarningsAsText() : null;
                        throw new HurricanServiceCommandException(warnings);
                    }
                }
                catch (Exception e) {
                    if (cpsTx != null) {
                        createCPSTxLog(cpsTx, e.getMessage(), true);
                    }
                    addWarning(lock.getAuftragId(), "Error creating CPS-Tx: " + e.getMessage());
                }
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Unexpected error during creation of CPS Transactions: " + e.getMessage(), e);
        }
    }

    /*
     * Markiert die relevanten Lock-Details (fuer ST Online/Voice/Connect)
     * als abgeschlossen.
     */
    void markLockDetailsAsProvisioned(Lock lock, CPSTransaction cpsTx) {
        List<LockDetail> lockDetails4CPS = lock2LockDetailMap.get(lock.getId());
        if (CollectionTools.isNotEmpty(lockDetails4CPS)) {
            for (LockDetail lockDetail : lockDetails4CPS) {
                try {
                    lockDetail.setExecutedAt(cpsTx.getEstimatedExecTime());
                    lockDetail.setExecutedFrom("CPS");
                    lockDetail.setCpsTxId(cpsTx.getId());

                    lockService.saveLockDetail(lockDetail);
                }
                catch (StoreException e) {
                    LOGGER.error(e.getMessage(), e);
                    createCPSTxLog(cpsTx, "Could not update the assigned LockDetail!\n" + ExceptionUtils.getFullStackTrace(e));
                }
            }
        }
    }

    /*
     * Filtert aus der angegebenen Liste nur die Sperren heraus,
     * die vom CPS noch betrachtet werden muessen.
     * <br>
     * Folgende Filterungen werden durchgefuehrt:
     * <ul>
     *   <li>Demontagen werden nicht beruecksichtigt
     *   <li>nur Locks, die noch nicht bearbeitet sind
     *   <li>nur Locks, die fuer ST Online / Voice / Connect sind
     *   <li>nur fuer Auftraege, bei denen CPS-Provisionierung erlaubt ist
     * </ul>
     */
    List<Lock> filterLocks(List<Lock> locks) throws ServiceCommandException {
        if (CollectionTools.isEmpty(locks)) {
            return locks;
        }
        else {
            try {
                List<Lock> filteredLocks = new ArrayList<Lock>();
                for (Lock lock : locks) {
                    if (!lock.isDemontage()) {
                        List<LockDetail> lockDetails = lockService.findLockDetails(lock.getId());
                        if (hasLockDetail4CPS(lock.getId(), lockDetails)) {

                            CPSProvisioningAllowed allowed = cpsService.isCPSProvisioningAllowed(
                                    lock.getAuftragId(),
                                    LazyInitMode.noInitialLoad,
                                    false,
                                    false,
                                    true);

                            if (allowed.isProvisioningAllowed()) {
                                filteredLocks.add(lock);
                            }
                            else {
                                addWarning(this, String.format("Order (%s) not allowed for CPS provisioning. Reason: %s",
                                        lock.getAuftragId(), allowed.getNoCPSProvisioningReason()));
                            }
                        }
                    }
                }

                return filteredLocks;
            }
            catch (FindException e) {
                LOGGER.error(e.getMessage(), e);
                throw new HurricanServiceCommandException("Error filtering locks: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Prueft, ob die Lock-Details einen fuer den CPS relevanten Eintrag besitzen. <br> Dies ist dann der Fall, wenn die
     * Sperre noch nicht erledigt ist und fuer eine der folgenden Abteilungen gedacht ist: <br> <ul> <li>ST Online
     * <li>ST Voice <li>M-Queue </ul>
     *
     * @param lockDetails
     * @return
     */
    boolean hasLockDetail4CPS(Long lockId, List<LockDetail> lockDetails) {
        if (CollectionTools.isNotEmpty(lockDetails)) {
            boolean isLock4CPS = false;

            List<LockDetail> lockDetails4CPS = new ArrayList<LockDetail>();
            for (LockDetail lockDetail : lockDetails) {
                if ((lockDetail.getExecutedAt() == null)
                        && (new VerlaufAbteilungCPSPredicate().evaluate(lockDetail.getAbteilungId()))) {
                    isLock4CPS = true;
                    lockDetails4CPS.add(lockDetail);
                }
            }

            if (isLock4CPS) {
                lock2LockDetailMap.put(lockId, lockDetails4CPS);
            }

            return isLock4CPS;
        }
        return false;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
    }

    /**
     * Injected
     */
    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

}


