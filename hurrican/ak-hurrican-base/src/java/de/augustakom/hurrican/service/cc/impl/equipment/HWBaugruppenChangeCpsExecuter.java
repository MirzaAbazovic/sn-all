/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2010 08:49:09
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;


/**
 * Executer-Klasse, um fuer einen Baugruppen-Schwenk die benoetigten CPS-Transactions (init od. modify) auszufuehren.
 * <br><br> Evtl. auftretende Warnungen / Fehlermeldungen bei der Erstellung der CPS-Tx werden protokolliert und koennen
 * aus dem Executer ermittelt werden.
 */
public class HWBaugruppenChangeCpsExecuter {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeCpsExecuter.class);

    private HWBaugruppenChange hwBgChange;
    private boolean cpsInit;
    private HWBaugruppenChangeService hwBaugruppenChangeService;
    private CPSService cpsService;
    private CCAuftragService auftragService;
    private Long sessionId;

    private StringBuilder cpsWarningsAndErrors;

    /**
     * Uebergibt dem Executer die notwendigen Modelle u. Services.
     *
     * @param hwBgChange
     * @param cpsInit    Flag definiert, ob ein Initial-Load (true) oder ein modifySubscriber (false) fuer die Auftraege
     *                   erstellt werden soll.
     * @param cpsService
     */
    public void configure(HWBaugruppenChange hwBgChange,
            boolean cpsInit,
            HWBaugruppenChangeService hwBaugruppenChangeService,
            CPSService cpsService,
            CCAuftragService auftragService,
            Long sessionId) {
        setHwBgChange(hwBgChange);
        setCpsInit(cpsInit);
        setHwBaugruppenChangeService(hwBaugruppenChangeService);
        setCpsService(cpsService);
        setAuftragService(auftragService);
        setSessionId(sessionId);

        cpsWarningsAndErrors = new StringBuilder();
    }


    /**
     * Fuehrt die notwendige CPS-Tx fuer den Baugruppen-Schwenk durch.
     */
    public void execute() throws StoreException {
        try {
            if (!isCpsActionAllowed()) {
                throw new StoreException("Die CPS-Aktion ist im aktuellen Status der Planung nicht erlaubt!");
            }

            List<HWBaugruppenChangePort2PortView> portMappingViews = hwBaugruppenChangeService.findPort2PortViews(hwBgChange);
            Collection<Long> orderIds4CpsAction = (cpsInit)
                    ? filterOrders4CpsInit(portMappingViews)
                    : filterOrders4CpsModify(portMappingViews);

            if (CollectionTools.isNotEmpty(orderIds4CpsAction)) {
                for (Long orderId : orderIds4CpsAction) {
                    try {
                        CPSTransactionResult cpsTxResult = null;
                        if (cpsInit) {
                            // Lazy-Init ausfuehren
                            cpsTxResult = cpsService.doLazyInit(LazyInitMode.initialLoad, orderId, null, sessionId);
                        }
                        else {
                            // modifySubscriber ausfuehren
                            cpsTxResult = cpsService.createCPSTransaction(
                                    new CreateCPSTransactionParameter(orderId, null, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                                            CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT, new Date(), null, null, null, null, false, false, sessionId)
                            );

                            if ((cpsTxResult.getCpsTransactions() != null) && (cpsTxResult.getCpsTransactions().size() == 1)) {
                                cpsService.sendCPSTx2CPS(cpsTxResult.getCpsTransactions().get(0), sessionId);
                            }
                        }

                        String warnings = (cpsTxResult.getWarnings() != null) ? cpsTxResult.getWarnings().getWarningsAsText() : null;
                        if (StringUtils.isNotBlank(warnings)) {
                            cpsWarningsAndErrors.append(warnings);
                            cpsWarningsAndErrors.append(SystemUtils.LINE_SEPARATOR);
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        cpsWarningsAndErrors.append("Error creating CPS-Tx for tech order " + orderId + ": ");
                        cpsWarningsAndErrors.append(e.getMessage());
                        cpsWarningsAndErrors.append(SystemUtils.LINE_SEPARATOR);
                    }
                }
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Erstellung der CPS-Transactionen: " + e.getMessage(), e);
        }
    }


    /**
     * Ermittelt alle Auftraege des Baugruppen-Schwenks, fuer die eine CPS-Initialisierung notwendig ist.
     */
    Collection<Long> filterOrders4CpsInit(List<HWBaugruppenChangePort2PortView> portMappingViews) throws FindException {
        if (portMappingViews == null) {
            return null;
        }

        Map<Long, Long> orderIdMap4CpsInit = new HashMap<Long, Long>();
        for (HWBaugruppenChangePort2PortView portMappingView : portMappingViews) {
            if (!BooleanTools.nullToFalse(portMappingView.getHasSuccessfulCpsTx())) {
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(portMappingView.getAuftragId());
                if (isOrderValid4CpsTx(auftragDaten)
                        && !orderIdMap4CpsInit.containsKey(auftragDaten.getAuftragNoOrig())) {

                    orderIdMap4CpsInit.put(auftragDaten.getAuftragNoOrig(), auftragDaten.getAuftragId());
                }
            }
        }

        return orderIdMap4CpsInit.values();
    }


    /**
     * Ermittelt alle Auftraege des Baugruppen-Schwenks, fuer die ein modifySubscriber notwendig ist.
     */
    Collection<Long> filterOrders4CpsModify(List<HWBaugruppenChangePort2PortView> portMappingViews) throws FindException {
        if (portMappingViews == null) {
            return null;
        }

        Map<Long, Long> orderIdMap4CpsModify = new HashMap<Long, Long>();
        for (HWBaugruppenChangePort2PortView portMappingView : portMappingViews) {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(portMappingView.getAuftragId());
            if (isOrderValid4CpsTx(auftragDaten)
                    && !orderIdMap4CpsModify.containsKey(auftragDaten.getAuftragNoOrig())) {

                orderIdMap4CpsModify.put(auftragDaten.getAuftragNoOrig(), auftragDaten.getAuftragId());
            }
        }

        return orderIdMap4CpsModify.values();
    }


    /**
     * Prueft, ob fuer den Auftrag grundsaetzlich eine CPS-Tx erstellt werden darf. Dies ist dann der Fall, wenn der
     * Auftrag aktuell noch in Betrieb bzw. noch nicht gekuendigt ist.
     */
    boolean isOrderValid4CpsTx(AuftragDaten auftragDaten) {
        if ((auftragDaten != null)
                && NumberTools.isGreaterOrEqual(auftragDaten.getStatusId(), AuftragStatus.IN_BETRIEB)
                && NumberTools.isLess(auftragDaten.getAuftragStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT)) {
            return true;
        }
        return false;
    }


    /**
     * Pruefung, ob die angegebene CPS-Aktion (init od. modify) in dem aktuellen Status des Baugruppen-Schwenks erlaubt
     * ist.
     *
     * @return
     */
    boolean isCpsActionAllowed() {
        if ((cpsInit && hwBgChange.isExecuteAllowed()) ||
                (!cpsInit && hwBgChange.isCloseAllowed())) {
            return true;
        }
        return false;
    }


    public void setHwBgChange(HWBaugruppenChange hwBgChange) {
        this.hwBgChange = hwBgChange;
    }

    public void setCpsInit(boolean cpsInit) {
        this.cpsInit = cpsInit;
    }

    public void setHwBaugruppenChangeService(HWBaugruppenChangeService hwBaugruppenChangeService) {
        this.hwBaugruppenChangeService = hwBaugruppenChangeService;
    }

    public void setCpsService(CPSService cpsService) {
        this.cpsService = cpsService;
    }


    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gibt die aufgetretenen Warnungen / Fehlermeldungen zurueck.
     *
     * @return
     */
    public StringBuilder getCpsWarningsAndErrors() {
        return cpsWarningsAndErrors;
    }

}


