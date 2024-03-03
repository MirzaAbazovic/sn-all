/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2009 13:03:03
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;


/**
 * Command-Klasse, um einen Lazy-Init fuer einen Auftrag durchzufuehren.
 * <p/>
 * Fuehrt ein 'lazyInit' fuer den Auftrag durch. <br> Der 'lazyInit' wird in folgenden Faellen durchgefuehrt: <br> <ul>
 * <li>SO-Type ist in (modifySubscriber, cancelSubscriber) <li>Auftrag besitzt keine erfolgreiche CPS-Transaction </ul>
 * <br> Der Init wird benoetigt, damit der CPS die zu provisionierende Differenz ermitteln kann. <br> Als Execution-Date
 * wird immer (now - 1 Tag) angegeben. <br> Der 'lazyInit' wird per synchronem WS-Call an den CPS uebergeben.
 *
 *
 */
public class CreateCPSTx4LazyInitCommand extends CPSSendSyncTxCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateCPSTx4LazyInitCommand.class);

    public static final String KEY_AUFTRAG_ID_FOR_INIT = "auftrag.id.for.init";
    public static final String KEY_FOLLOWING_CPS_TRANSACTION = "cps.following.cps.tx";

    private Long auftragId = null;
    private CPSTransaction followingCPSTx = null;

    private LazyInitMode lazyInitMode = null;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        loadRequiredData();
        if (isLazyInitNecessary()) {
            return doLazyInit();
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        super.loadRequiredData();

        lazyInitMode = getPreparedValue(KEY_LAZY_INIT_MODE, LazyInitMode.class, true, null);
        if (lazyInitMode == null) {
            lazyInitMode = LazyInitMode.initialLoad;
        }
        else if (!LazyInitMode.isInitialLoad(lazyInitMode)) {
            throw new FindException("Mode for initial load is not valid! Must be INITIAL_LOAD, WITH_SERVICE_LOGIC or ReInit!");
        }

        auftragId = getPreparedValue(KEY_AUFTRAG_ID_FOR_INIT, Long.class, false,
                "Tech order ID for CPS lazy init not defined!");

        followingCPSTx = getPreparedValue(KEY_FOLLOWING_CPS_TRANSACTION, CPSTransaction.class, true,
                "CPS-Transaction object is not defined for command!");
    }

    /*
     * Prueft, ob fuer den Auftrag ein Lazy-Init durchgefuehrt werden muss.
     * Dies ist dann der Fall, wenn fuer den Auftrag noch keine (erfolgreiche)
     * CPS-Transaction vorhanden ist
     * @return true wenn fuer den Auftrag noch ein Lazy-Init durchgefuehrt werden muss
     * @throws ServiceCommandException
     */
    private boolean isLazyInitNecessary() throws ServiceCommandException {
        try {
            if ((followingCPSTx != null) &&
                    NumberTools.isIn(followingCPSTx.getServiceOrderType(), new Number[] {
                            CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                            CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB })) {

                List<CPSTransaction> successfulCPSTransactions =
                        cpsService.findSuccessfulCPSTransaction4TechOrder(auftragId);

                for (CPSTransaction cpsTrans : successfulCPSTransactions) {
                    if (NumberTools.isIn(cpsTrans.getServiceOrderType(), new Number[] {
                            CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB,
                            CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB })) {
                        return false;
                    }
                }
                return true;
            }
            else if (followingCPSTx == null) {
                return true;
            }

            return false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error during Lazy-Init check: " + e.getMessage(), e);
        }
    }

    /*
     * Fuehrt den Lazy-Init fuer den Auftrag durch und uebermittelt die CPS-Tx
     * per synchroner Schnittstelle an den CPS.
     * @throws ServiceCommandException
     */
    private CPSTransactionResult doLazyInit() throws ServiceCommandException {
        CPSTransactionResult cpsTxInitResult = null;
        try {
            Date now = new Date();
            Date execDate = (followingCPSTx != null)
                    ? DateTools.changeDate(followingCPSTx.getEstimatedExecTime(), Calendar.DAY_OF_MONTH, -1)
                    : now;

            // falls Exec-Date in Zukunft --> Exec-Date auf #now# setzen
            if (DateTools.isDateAfter(execDate, now)) {
                execDate = now;
            }

            Long serviceOrderType = LazyInitMode.reInit.equals(lazyInitMode)
                    ? CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB
                    : CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB;

            cpsTxInitResult = cpsService.createCPSTransaction(new CreateCPSTransactionParameter(auftragId, null, serviceOrderType, CPSTransaction.TX_SOURCE_HURRICAN_ORDER, CPSTransaction.SERVICE_ORDER_PRIO_HIGH, execDate, null,
                    null, null, lazyInitMode, Boolean.FALSE, Boolean.FALSE, getSessionId()));

            if (cpsTxInitResult != null) {
                if (CollectionTools.isNotEmpty(cpsTxInitResult.getCpsTransactions())) {
                    CPSTransaction cpsTx = cpsTxInitResult.getCpsTransactions().get(0);

                    // send to CPS (synchron)
                    send2CPS(cpsTx);
                }
                return cpsTxInitResult;
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error during Lazy-Init for order: " + e.getMessage(), e);
        }
        finally {
            if ((cpsTxInitResult != null) && (getWarnings() != null)) {
                cpsTxInitResult.setWarnings(getWarnings());
            }
        }
    }

}
