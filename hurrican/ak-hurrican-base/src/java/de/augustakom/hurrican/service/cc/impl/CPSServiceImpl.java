/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2009 08:50:00
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.CPSTransactionDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionLog;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSGetServiceOrderStatusResponseData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.CommunicationException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.impl.command.cps.CPSQueryAttainableBitrateCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CPSSendAsyncTxCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CPSSendSyncTxCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CancelCPSTxCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CreateCPSTx4BACommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CreateCPSTx4DNServicesCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CreateCPSTx4LazyInitCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CreateCPSTx4LockCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CreateCPSTx4MDUInitCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CreateCPSTxCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CreateCPSTxQueryCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.CreateCpsTx4OltChildCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.DisjoinCPSTxCommand;
import de.augustakom.hurrican.service.cc.impl.command.cps.FinishCPSTxCommand;
import de.mnet.common.service.locator.ServiceLocator;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Service-Implementierung von <code>CPSService</code>.
 *
 *
 */
@CcTxRequired
public class CPSServiceImpl extends DefaultCCService implements CPSService {

    private static final Logger LOGGER = Logger.getLogger(CPSServiceImpl.class);
    private static final Ordering<CPSTransactionExt> EXECDATE_DESC_ORDERING = Ordering.natural()
            .onResultOf(new Function<CPSTransactionExt, Comparable>() {
                @Override
                public Comparable apply(final CPSTransactionExt input) {
                    return input.getEstimatedExecTime();
                }
            });
    private final Ordering<AuftragDaten> auftragDatenOrderingByStatus = new Ordering<AuftragDaten>() {
        @Override
        public int compare(@Nullable AuftragDaten left, @Nullable AuftragDaten right) {
            if ((left == null) || (right == null)) {
                return 0;
            }
            return Longs.compare(left.getAuftragStatusId(), right.getAuftragStatusId());
        }
    };
    private final Ordering<AuftragDaten> auftragDatenOrderingByCpsTxCount = new Ordering<AuftragDaten>() {
        @Override
        public int compare(@Nullable AuftragDaten left, @Nullable AuftragDaten right) {
            if ((left == null) || (right == null)) {
                return 0;
            }
            List<CPSTransaction> leftCpsTx;
            try {
                leftCpsTx = findActiveCPSTransactions(left.getAuftragNoOrig(), null, null);
                List<CPSTransaction> rightCpsTx = findActiveCPSTransactions(right.getAuftragNoOrig(), null, null);
                return Ints.compare(leftCpsTx.size(), rightCpsTx.size());
            }
            catch (FindException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }
    };
    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    BAService baService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AccountService")
    private AccountService accountService;
    @Resource(name = "de.mnet.common.service.locator.ServiceLocator")
    private ServiceLocator serviceLocator;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;

    @Override
    public CPSTransaction saveCPSTransaction(CPSTransaction toSave, Long sessionId) throws StoreException {
        if ((toSave == null) || (sessionId == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            String loginName = getLoginNameSilent(sessionId);

            if (toSave.getId() == null) {
                toSave.setTxUser(loginName);
            }

            toSave.setUserW(loginName);
            return ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public void saveCPSTransactionTxNew(CPSTransaction toSave, Long sessionId) throws StoreException {
        saveCPSTransaction(toSave, sessionId);
    }

    @Override
    public List<CPSTransactionExt> findOpenTransactions(Integer limit) throws FindException {
        try {
            return ((CPSTransactionDAO) getDAO()).findOpenTransactions(limit);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSTransactionExt> findExpiredTransactions(Integer offsetDays) throws FindException {
        try {
            Date ablaufDatum = DateTools.changeDate(
                    DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), Calendar.DAY_OF_MONTH,
                    offsetDays);
            return ((CPSTransactionDAO) getDAO()).findExpiredTransactions(ablaufDatum);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSTransactionExt> findCPSTransaction(CPSTransactionExt example) throws FindException {
        try {
            return ((ByExampleDAO) getDAO()).queryByExample(
                    example, CPSTransactionExt.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CPSTransaction findCPSTransactionById(Long id) throws FindException {
        try {
            return ((FindDAO) getDAO()).findById(id, CPSTransaction.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @NotNull
    public CPSProvisioningAllowed isCPSProvisioningAllowed(Long auftragId, LazyInitMode lazyInitMode,
            boolean checkAutoCreation, boolean check4SubOrder, boolean checkOrderState) throws FindException {
        try {
            if (!LazyInitMode.isInitialLoad(lazyInitMode)) {
                // pruefen, ob CPS-Provisionierung fuer Produkt erlaubt
                Produkt prod = produktService.findProdukt4Auftrag(auftragId);
                if (BooleanTools.nullToFalse(prod.getCpsProvisioning())) {
                    if (checkAutoCreation && !BooleanTools.nullToFalse(prod.getCpsAutoCreation())) {
                        return new CPSProvisioningAllowed(false,
                                "Product is not configured for automatic CPS provisioning! " +
                                        "You can make a manual CPS-Tx for the order."
                        );
                    }

                    // pruefen, ob Auftrag fuer manuelle Provisionierung definiert ist
                    AuftragTechnik aTech = auftragService.findAuftragTechnikByAuftragIdTx(auftragId);
                    if (!BooleanTools.nullToFalse(aTech.getPreventCPSProvisioning())) {
                        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);

                        // pruefen, ob CPS-Prov. am Standort erlaubt ist
                        // (bei Sub-Order Check muss Standort-Pruefung nicht durchlaufen werden!)
                        boolean isVirtual = prod.isVirtualProduct();
                        if (!check4SubOrder && !isVirtual && !isHVTAllowed(auftragId)) {
                            return new CPSProvisioningAllowed(false,
                                    "HVT is not configured for CPS provisioning!");
                        }

                        // pruefen, ob Auftrag einem VPN zugeordnet ist
                        if ((!checkOrderState || checkOrderState(auftragDaten).isProvisioningAllowed())
                                && (aTech.getVpnId() != null)
                                && checkAutoCreation) {
                            return new CPSProvisioningAllowed(false, "Tech order is assigned to VPN.");
                        }
                    }
                    else {
                        return new CPSProvisioningAllowed(false, "Tech order is marked as manual provisioning.");
                    }
                }
                else {
                    return new CPSProvisioningAllowed(false,
                            "Product of tech order is not configured for CPS provisioning.");
                }
            }
            else {
                return checkProvisioningAllowed4InitialLoad(auftragService, auftragId, lazyInitMode);
            }

            return new CPSProvisioningAllowed(true, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Prueft, ob ein Initial-Load fuer den angegebenen Auftrag erlaubt ist. Ist fuer den Auftrag eine nicht stornierte
     * / fehlerhafte CPS-Tx vorhanden, darf der Auftrag nicht mehr initialisiert werden.
     */
    private CPSProvisioningAllowed checkProvisioningAllowed4InitialLoad(CCAuftragService auftragService,
            Long auftragId, LazyInitMode lazyInitMode) throws FindException {
        List<CPSTransaction> existingCPSTx = findSuccessfulCPSTransaction4TechOrder(auftragId);
        if (CollectionTools.isEmpty(existingCPSTx)) {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
            return checkOrderState(auftragDaten);
        }
        else {
            boolean techOrderIsInitialized = false;
            if (!LazyInitMode.reInit.equals(lazyInitMode)) {
                for (CPSTransaction cpsTx : existingCPSTx) {
                    if (NumberTools.isIn(cpsTx.getServiceOrderType(), new Number[] {
                            CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB,
                            CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB })) {
                        techOrderIsInitialized = true;
                        break;
                    }
                }

                if (techOrderIsInitialized) {
                    return new CPSProvisioningAllowed(false,
                            "Initial load for order is not allowed! Order is already initialized!");
                }
            }

            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
            return checkOrderState(auftragDaten);
        }
    }

    /*
     * Ueberprueft, ob der HVT fuer eine CPS-Provisionierung zugelassen ist.
     *
     * @param auftragId ID des Auftrags
     *
     * @return true, wenn der HVT fuer CPS-Provisionierung zugelassen ist oder der Auftrag nicht ueber einen HVT
     * angeschlossen ist (Stichwort: Direktanschluss)
     *
     * @throws ServiceNotFoundException
     *
     * @throws FindException
     */
    boolean isHVTAllowed(Long auftragId) throws FindException {
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        if ((endstelle != null) && (endstelle.getHvtIdStandort() != null)) {
            HVTStandort hvtStandort = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
            return (hvtStandort != null) && BooleanTools.nullToFalse(hvtStandort.getCpsProvisioning());
        }

        return false;
    }

    /*
     * Prueft, ob der Status des angegebenen Auftrags fuer eine CPS-Provisionierung in Frage kommt.
     */
    private CPSProvisioningAllowed checkOrderState(AuftragDaten auftragDaten) {
        if (NumberTools.isLess(auftragDaten.getStatusId(), AuftragStatus.TECHNISCHE_REALISIERUNG) ||
                NumberTools.isIn(auftragDaten.getStatusId(),
                        new Number[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE }
                )) {
            return new CPSProvisioningAllowed(false,
                    "CPS provisioning not allowed for actual order state.");
        }
        return new CPSProvisioningAllowed(true, null);
    }

    @Override
    @CcTxRequiresNew
    public CPSTransactionResult createCPSTransaction(CreateCPSTransactionParameter parameters) throws StoreException {
        if (parameters.getAuftragId() == null) {
            throw new StoreException("Order ID for transaction not defined");
        }
        if (parameters.getEstimatedExecTime() == null) {
            throw new StoreException("Excution date not defined");
        }
        if (parameters.getServiceOrderType() == null) {
            throw new StoreException("ServiceOrderType for transaction not defined");
        }
        if (parameters.getServiceOrderPrio() == null) {
            throw new StoreException("ServiceOrderPrio for transaction not defined");
        }
        if (parameters.getSessionId() == null) {
            throw new StoreException("Session-ID of user not defined");
        }

        try {
            if (isCreateOrModifySubscriber(parameters.getServiceOrderType())) {
                List<String> errors = checkIfTxPermitted4OltChild(parameters);
                if (CollectionUtils.isNotEmpty(errors)) {
                    throw new StoreException("CPS-Provisioning is not allowed for order " + parameters.getAuftragId() +
                            ".\nReason: " + errors.get(0));
                }
            }

            CPSProvisioningAllowed allowed = isCPSProvisioningAllowed(
                    parameters.getAuftragId(),
                    parameters.getLazyInitMode(),
                    BooleanTools.nullToFalse(parameters.isAutoCreation()),
                    false,
                    true);

            if (!allowed.isProvisioningAllowed()) {
                throw new StoreException("CPS-Provisioning is not allowed for order " + parameters.getAuftragId() +
                        ".\nReason: " + allowed.getNoCPSProvisioningReason());
            }

            // CPS-Tx erstellen und 'bekannte' Daten eintragen (Save ueber CreateCPSTxCommand)
            CPSTransaction cpsTx = new CPSTransaction();
            cpsTx.setAuftragId(parameters.getAuftragId());
            cpsTx.setVerlaufId(parameters.getVerlaufId());
            cpsTx.setTxState(CPSTransaction.TX_STATE_IN_PREPARING);
            cpsTx.setTxSource(parameters.getTxSource());
            cpsTx.setServiceOrderPrio(parameters.getServiceOrderPrio());
            cpsTx.setServiceOrderType(parameters.getServiceOrderType());
            cpsTx.setEstimatedExecTime(parameters.getEstimatedExecTime());
            cpsTx.setServiceOrderStackId(parameters.getServiceOrderStackId());
            cpsTx.setServiceOrderStackSeq(parameters.getServiceOrderStackSeq());

            IServiceCommand cmd = serviceLocator.getCmdBean(CreateCPSTxCommand.class.getName());
            cmd.prepare(CreateCPSTxCommand.KEY_CPS_TRANSACTION, cpsTx);
            cmd.prepare(CreateCPSTxCommand.KEY_SESSION_ID, parameters.getSessionId());
            cmd.prepare(CreateCPSTxCommand.KEY_LOCK_MODE, parameters.getLockMode());
            cmd.prepare(CreateCPSTxCommand.KEY_LAZY_INIT_MODE, parameters.getLazyInitMode());
            cmd.prepare(CreateCPSTxCommand.KEY_AUTO_CREATION, parameters.isAutoCreation());
            cmd.prepare(CreateCPSTxCommand.KEY_FORCE_EXEC_TYPE, parameters.isForceExecType());
            Object result = cmd.execute();

            CPSTransactionResult txResult = new CPSTransactionResult();
            AKWarnings warnings = new AKWarnings();
            txResult.setWarnings(warnings);

            if (result instanceof ServiceCommandResult) {
                ServiceCommandResult cmdRes = (ServiceCommandResult) result;
                if (cmdRes.isOk()) {
                    List<CPSTransaction> txList = new ArrayList<>();
                    txList.add((CPSTransaction) cmdRes.getResultObject());
                    txResult.setCpsTransactions(txList);
                }
                else {
                    warnings.addAKWarning(this, cmdRes.getMessage());
                }
            }
            else {
                warnings.addAKWarning(this, "Result from CreateCPSTxCommand is invalid! " +
                        "Expected: ServiceCommandResult; but is: " + result);
            }

            return txResult;
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error creating CPS transactions for tech order " + parameters.getAuftragId() + ": "
                            + e.getMessage(), e
            );
        }
    }

    /**
     * Prueft, ob der Auftrag an einem {@link HWRack} vom Typ {@link HWOltChild} haengt. Falls ja, dann muss dieses
     * bereits 'freigegeben' sein, damit die Provisionierung zulaessig ist. <br/> Haengt der Auftrag nicht an einer
     * Hardware vom Typ {@link HWOltChild} so findet keine weitere Pruefung statt.
     *
     * @param parameters
     * @return falls Provisionierung nicht erlaubt ist: Liste mit Begruendung(en); sonst eine leere Liste
     * @throws FindException
     */
    protected List<String> checkIfTxPermitted4OltChild(CreateCPSTransactionParameter parameters) throws FindException {
        Long auftragId = parameters.getAuftragId();
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
        HWOltChild hwOltChild = getDpoOrOntRack4Auftrag(auftragId);
        if (hwOltChild != null) {
            return checkIfTxPermitted4OltChild(hwOltChild, auftragDaten, parameters.getServiceOrderType());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> checkIfTxPermitted4OltChild(HWOltChild hwOltChild, AuftragDaten auftragDaten, Long serviceOrderType) throws FindException {
        List<String> errors = new ArrayList<>();
        if (hwOltChild.isDpoOrOntRack()) {
            final String readableServiceOrderType = CPSTransaction.getReadableServiceOrderType(serviceOrderType);

            if (!isHwOltChildEnabled(hwOltChild)) {
                errors.add(String.format(
                                "CPS-TX %s is not possible as %s has not been enabled yet",
                                readableServiceOrderType,
                                hwOltChild.getRackTyp())
                );
            }
        }
        return errors;
    }

    private boolean isHwOltChildEnabled(HWOltChild hwOltChild) {
        return hwOltChild.getFreigabe() != null;
    }

    private HWOltChild getDpoOrOntRack4Auftrag(Long auftragId) throws FindException {
        HWRack hwRack = auftragService.findHwRackByAuftragId(auftragId);
        if (hwRack != null && hwRack.isDpoOrOntRack()) {
            return HWOltChild.class.cast(hwRack);
        }
        return null;
    }

    private boolean isCreateOrModifySubscriber(Long serviceOrderType) {
        return isCreateSubscriber(serviceOrderType) || isModifySubscriber(serviceOrderType);
    }

    private boolean isCreateSubscriber(Long serviceOrderType) {
        return CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB.equals(serviceOrderType);
    }

    private boolean isModifySubscriber(Long serviceOrderType) {
        return CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB.equals(serviceOrderType);
    }

    @Override
    public CPSTransactionResult createCPSTransactions4BA(Date respectedRealDate, Long sessionId) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CreateCPSTx4BACommand.class.getName());
            cmd.prepare(CreateCPSTx4BACommand.KEY_RESPECTED_REAL_DATE, respectedRealDate);
            cmd.prepare(CreateCPSTx4BACommand.KEY_SESSION_ID, sessionId);
            Object result = cmd.execute();

            if (result instanceof List) {
                @SuppressWarnings("unchecked")
                // CreateCPSTx4BACommand.execute liefert List<CPSTransaction>
                        List<CPSTransaction> txList = (List<CPSTransaction>) result;
                if (CollectionTools.isNotEmpty(txList)) {
                    return new CPSTransactionResult(txList, ((IWarningAware) cmd).getWarnings());
                }
                else {
                    throw new StoreException("No CPS-Tx for provisioning orders at date " + respectedRealDate
                            + " created!");
                }
            }

            return null;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error during creation of CPS transactions for provisioning orders: " + e.getMessage(), e);
        }
    }

    @Override
    public CPSTransactionResult createCPSTransaction4BA(Verlauf verlauf, Long sessionId) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CreateCPSTx4BACommand.class.getName());
            cmd.prepare(CreateCPSTx4BACommand.KEY_ASSIGNED_PROVISIONING_ORDER, verlauf);
            cmd.prepare(CreateCPSTx4BACommand.KEY_SESSION_ID, sessionId);
            Object result = cmd.execute();

            if (result instanceof List<?>) {
                @SuppressWarnings("unchecked")
                // CreateCPSTx4BACommand.execute liefert List<CPSTransaction>
                        List<CPSTransaction> txList = (List<CPSTransaction>) result;
                if (CollectionTools.isNotEmpty(txList)) {
                    return new CPSTransactionResult(txList, ((IWarningAware) cmd).getWarnings());
                }
                throw new StoreException("No CPS-Tx for provisioning order " + verlauf.getId() + " created!");
            }

            return null;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error during creation of CPS transactions for provisioning orders: " + e.getMessage(), e);
        }
    }

    @Override
    public CPSTransactionResult createCPSTransactions4DNServices(Date changeDate, Long sessionId)
            throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CreateCPSTx4DNServicesCommand.class.getName());
            cmd.prepare(CreateCPSTx4DNServicesCommand.KEY_DN_SERVICES_CHANGE_DATE, changeDate);
            cmd.prepare(CreateCPSTx4DNServicesCommand.KEY_SESSION_ID, sessionId);
            Object result = cmd.execute();

            @SuppressWarnings("unchecked")
            // CreateCPSTx4DNServicesCommand.execute liefert List<CPSTransaction>
                    CPSTransactionResult retVal = new CPSTransactionResult((List<CPSTransaction>) result,
                    ((IWarningAware) cmd).getWarnings());
            return retVal;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error during creation of CPS transactions for provisioning orders: " + e.getMessage(), e);
        }
    }

    @Override
    public CPSTransactionResult createCPSTransactions4Lock(Long sessionId) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CreateCPSTx4LockCommand.class.getName());
            cmd.prepare(CreateCPSTx4LockCommand.KEY_SESSION_ID, sessionId);
            Object result = cmd.execute();

            @SuppressWarnings("unchecked")
            // CreateCPSTx4LockCommand.execute liefert List<CPSTransaction>
                    CPSTransactionResult retVal = new CPSTransactionResult((List<CPSTransaction>) result,
                    ((IWarningAware) cmd).getWarnings());
            return retVal;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error during creation of CPS transactions for (un)locks: " + e.getMessage(), e);
        }
    }

    @Override
    public CPSTransactionResult createCPSTransaction4MDUInit(Long mduRackId, boolean sendUpdate, Long sessionId,
            boolean useInitialized) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CreateCPSTx4MDUInitCommand.class.getName());
            cmd.prepare(CreateCPSTx4MDUInitCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(CreateCPSTx4MDUInitCommand.KEY_MDU_RACK_ID, mduRackId);
            cmd.prepare(CreateCPSTx4MDUInitCommand.KEY_SEND_UPDATE_FLAG, sendUpdate);
            cmd.prepare(CreateCPSTx4MDUInitCommand.KEY_USE_INITIALIZED, useInitialized);
            Object result = cmd.execute();

            List<CPSTransaction> cpsTxList = new ArrayList<>();
            cpsTxList.add((CPSTransaction) result);

            return new CPSTransactionResult(cpsTxList, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error during creation of CPS transactions for provisioning orders: " + e.getMessage(), e);
        }
    }

    @Override
    public CPSTransactionResult createCPSTransaction4MDUInitTx(final Long mduRackId, final boolean sendUpdate,
            final Long sessionId, final boolean useInitialized) throws StoreException {
        return createCPSTransaction4MDUInit(mduRackId, sendUpdate, sessionId, useInitialized);
    }

    @Override
    public CPSTransactionResult createCPSTransaction4OltChild(final Long oltChildId, final Long serviceOrderType,
            final Long sessionId) throws StoreException {
        try {
            final IServiceCommand cmd = serviceLocator.getCmdBean(CreateCpsTx4OltChildCommand.class);
            cmd.prepare(CreateCpsTx4OltChildCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(CreateCpsTx4OltChildCommand.KEY_RACK_ID, oltChildId);
            cmd.prepare(CreateCpsTx4OltChildCommand.KEY_SERVICE_ORDER_TYPE, serviceOrderType);
            final CPSTransaction result = (CPSTransaction) cmd.execute();
            return new CPSTransactionResult(Lists.newArrayList(result), null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error during creation of CPS transactions for provisioning orders: " + e.getMessage(), e);
        }
    }

    @Override
    @CcTxRequiresNew
    public void sendCPSTx2CPS(CPSTransaction cpsTx, Long sessionId) throws StoreException {
        sendCPSTx2CPS(cpsTx, sessionId, false);
    }

    @Override
    @CcTxRequiresNew
    public void sendCPSTx2CPS(CPSTransaction cpsTx, Long sessionId, boolean sync) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(sync
                    ? CPSSendSyncTxCommand.class.getName()
                    : CPSSendAsyncTxCommand.class.getName());
            cmd.prepare(CPSSendAsyncTxCommand.KEY_CPS_TX, cpsTx);
            cmd.prepare(CPSSendAsyncTxCommand.KEY_SESSION_ID, sessionId);

            cmd.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error sending CPS transaction to CPS: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendCpsTx2CPSAsyncWithoutNewTx(CPSTransaction cpsTx, Long sessionId) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CPSSendAsyncTxCommand.class.getName());
            cmd.prepare(CPSSendAsyncTxCommand.KEY_CPS_TX, cpsTx);
            cmd.prepare(CPSSendAsyncTxCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(CPSSendAsyncTxCommand.KEY_NEW_TRANSACTION_4_CPS_TX_LOG, false);
            cmd.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error sending CPS transaction to CPS: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean cancelCPSTransaction(Long txId, Long sessionId) throws StoreException, CommunicationException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CancelCPSTxCommand.class.getName());
            cmd.prepare(CancelCPSTxCommand.KEY_CPS_TX_ID, txId);
            cmd.prepare(CancelCPSTxCommand.KEY_SESSION_ID, sessionId);
            cmd.execute();

            return true;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error cancelling CPS transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public void disjoinCPSTransaction(Long txId, Long sessionId) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(DisjoinCPSTxCommand.class.getName());
            cmd.prepare(DisjoinCPSTxCommand.KEY_CPS_TX_ID, txId);
            cmd.prepare(DisjoinCPSTxCommand.KEY_SESSION_ID, sessionId);
            cmd.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error while disjoining CPS transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean finishCPSTransaction(Long txId, Object cpsResult, Long sessionId) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(FinishCPSTxCommand.class.getName());
            cmd.prepare(FinishCPSTxCommand.KEY_CPS_TX_ID, txId);
            cmd.prepare(FinishCPSTxCommand.KEY_CPS_RESULT, cpsResult);
            cmd.prepare(FinishCPSTxCommand.KEY_SESSION_ID, sessionId);
            return (Boolean) cmd.execute();
        }
        catch (Exception e) {
            throw new StoreException("Error finishing CPS-Tx", e);
        }
    }

    @Override
    @CcTxRequiresNew
    public void saveCPSTransactionLogTxNew(CPSTransactionLog toSave) throws StoreException {
        try {
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveCPSTransactionLogInTx(CPSTransactionLog toSave) throws StoreException {
        try {
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSTransactionLog> findCPSTxLogs4CPSTx(Long cpsTxId) throws FindException {
        try {
            CPSTransactionLog example = new CPSTransactionLog();
            example.setCpsTxId(cpsTxId);

            return ((ByExampleDAO) getDAO()).queryByExample(example, CPSTransactionLog.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void closeCPSTx(Long cpsTxId, Long sessionId) throws StoreException {
        if ((cpsTxId == null) || (sessionId == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            CPSTransaction cpsTxToClose = findCPSTransactionById(cpsTxId);

            if ((cpsTxToClose != null)
                    && NumberTools.isIn(cpsTxToClose.getTxState(),
                    new Number[] { CPSTransaction.TX_STATE_IN_PREPARING_FAILURE,
                            CPSTransaction.TX_STATE_TRANSMISSION_FAILURE }
            )) {
                cpsTxToClose.setTxState(CPSTransaction.TX_STATE_FAILURE_CLOSED);
                // TODO MM the TxNew annotation has no effect in saveCPSTransactionTxNew(..) -> called from same class. Either call non-transacted method or move outside class
                saveCPSTransactionTxNew(cpsTxToClose, sessionId);
            }
            else {
                throw new StoreException("CPS-Tx kann bei aktuellem Status nicht geschlossen werden!");
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public CPSGetServiceOrderStatusResponseData getStateForCPSTx(CPSTransactionExt tx, Long sessionId)
            throws StoreException {
        try {
            // <GETSOSTATUS_REQUEST>>
            // <TRANSACTION_ID>123456789</TRANSACTION_ID>
            // <GETSOSTATUS_REQUEST>
            //
            // Das Antwort Foramt koennte folgendermassen aussehen:
            //
            // <GETSOSTATUS_RESPONSE>
            // <SO_STATUS></SO_STATUS>
            // <SO_RESULT></SO_RESULT>
            // <SO_RESPONSE></SO_RESPONSE>
            // </GETSOSTATUS_RESPONSE>
            // Synchron 22000

            IServiceCommand cmd = serviceLocator.getCmdBean(CreateCPSTxQueryCommand.class.getName());
            cmd.prepare(CreateCPSTxQueryCommand.KEY_CPS_TRANSACTION, tx);
            cmd.prepare(CreateCPSTxQueryCommand.KEY_SESSION_ID, sessionId);
            return (CPSGetServiceOrderStatusResponseData) cmd.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error getting state for CPS transaction : " + e.getMessage(), e);
        }
    }

    @Override
    public void resendCPSTx(CPSTransactionExt tx, Long sessionId) throws FindException, StoreException {
        if ((tx == null) || (tx.getTxState() == null)) {
            throw new RuntimeException("CPSTransaction not completely initialized!");
        }

        if ((tx.getTxState().longValue() == CPSTransaction.TX_STATE_TRANSMISSION_FAILURE.longValue()) ||
                (tx.getTxState().longValue() == CPSTransaction.TX_STATE_IN_PREPARING.longValue())) {

            CPSTransaction cpsToResend = findCPSTransactionById(tx.getId());
            // TODO MM the TxNew annotation has no effect in sendCPSTx2CPS(..) -> called from same class. Either call non-transacted method or move outside class
            sendCPSTx2CPS(cpsToResend, sessionId);
        }
        else {
            throw new StoreException("CPS transaction is not in a state to resend it to the CPS system.");
        }
    }

    @Override
    public List<CPSTransactionSubOrder> findCPSTransactionSubOrders(Long cpsTxId) throws FindException {
        if (cpsTxId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            CPSTransactionSubOrder example = new CPSTransactionSubOrder();
            example.setCpsTxId(cpsTxId);

            return ((ByExampleDAO) getDAO()).queryByExample(example, CPSTransactionSubOrder.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveCPSTransactionSubOrder(CPSTransactionSubOrder toSave) throws StoreException {
        try {
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSTransaction> findCPSTransactions4Verlauf(Long verlaufId) throws FindException {
        try {
            List<CPSTransaction> result = new ArrayList<>();

            CPSTransactionExt example = new CPSTransactionExt();
            example.setVerlaufId(verlaufId);
            List<CPSTransactionExt> cpsTxs = findCPSTransaction(example);
            CollectionTools.addAllIgnoreNull(result, cpsTxs);

            // weitere CPS-Tx ueber Sub-Orders ermitteln
            CPSTransactionSubOrder exampleSubOrders = new CPSTransactionSubOrder();
            exampleSubOrders.setVerlaufId(verlaufId);
            List<CPSTransactionSubOrder> subOrders =
                    ((ByExampleDAO) getDAO()).queryByExample(exampleSubOrders, CPSTransactionSubOrder.class);
            if (CollectionTools.isNotEmpty(subOrders)) {
                for (CPSTransactionSubOrder subOrder : subOrders) {
                    result.add(findCPSTransactionById(subOrder.getCpsTxId()));
                }
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSTransaction> findActiveCPSTransactions(final Long orderNoOrig, final Long hwRackId,
            final Long serviceOrderType) throws FindException {
        if (orderNoOrig == null
                && hwRackId == null
                && serviceOrderType == null) {
            return null;
        }
        try {
            CPSTransactionExt example = new CPSTransactionExt();
            example.setHwRackId(hwRackId);
            example.setOrderNoOrig(orderNoOrig);
            example.setServiceOrderType(serviceOrderType);
            List<CPSTransactionExt> cpsTxs = findCPSTransaction(example);
            if (CollectionTools.isNotEmpty(cpsTxs)) {
                List<CPSTransaction> result = new ArrayList<>();

                for (CPSTransaction cpsTx : cpsTxs) {
                    if (cpsTx.isActive()) {
                        result.add(cpsTx);
                    }
                }
                return result;
            }
            return ImmutableList.of();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSTransactionExt> findActiveCPSTransactions(Long orderNoOrig) throws FindException {
        try {
            final List<CPSTransactionExt> activeTrxList = ((CPSTransactionDAO) getDAO()).findActiveCPSTransactions4BillingOrder(orderNoOrig);
            return activeTrxList;
        }
        catch (Exception e) {
            final String msg = String.format("Error by fetching active CPS transactions for billing Order [%d]", orderNoOrig);
            LOGGER.error(msg, e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public List<CPSTransactionExt> findSuccessfulCPSTransactions(final Long orderNoOrig, final Long hwRackId,
            final Long serviceOrderType) throws FindException {
        try {
            CPSTransactionExt example = new CPSTransactionExt();
            example.setHwRackId(hwRackId);
            example.setOrderNoOrig(orderNoOrig);
            example.setServiceOrderType(serviceOrderType);
            example.setTxState(CPSTransaction.TX_STATE_SUCCESS);
            return findCPSTransaction(example);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSTransaction> findCPSTransactions4TechOrder(final Long auftragId, Collection<Long> txStates, Collection<Long> serviceOrderTypes) {
        return ((CPSTransactionDAO) getDAO()).findCPSTransactions4TechOrder(auftragId, txStates, serviceOrderTypes);
    }

    @Override
    public Optional<CPSTransaction> findLatestCPSTransactions4TechOrder(final Long auftragId, Collection<Long> txStates, Collection<Long> serviceOrderTypes) {
        final List<CPSTransaction> cpsTrxLst = ((CPSTransactionDAO) getDAO()).findCPSTransactions4TechOrder(auftragId, txStates, serviceOrderTypes,
                false, true, 1);  // newest TRX at first position
        return cpsTrxLst.stream().findFirst();
    }

    @Override
    public List<Long> findVerlaufIDs4CPSTransaction(CPSTransaction cpsTx) throws FindException {
        try {
            List<Long> verlaufIDs = new ArrayList<>();
            if (cpsTx.getVerlaufId() != null) {
                verlaufIDs.add(cpsTx.getVerlaufId());
            }

            List<CPSTransactionSubOrder> subOrders = findCPSTransactionSubOrders(cpsTx.getId());
            if (CollectionTools.isNotEmpty(subOrders)) {
                for (CPSTransactionSubOrder subOrder : subOrders) {
                    if (subOrder.getVerlaufId() != null) {
                        verlaufIDs.add(subOrder.getVerlaufId());
                    }
                }
            }

            return verlaufIDs;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSTransactionExt> findCPSTransactionsForTechOrder(CCAuftragModel ccAuftragModel) throws FindException {
        List<CPSTransactionExt> cpsTransactionList;
        if ((null == ccAuftragModel) || (null == ccAuftragModel.getAuftragId())) {
            throw new FindException("Auftrag NOT SET!!!");
        }

        try {
            CPSTransactionExt cpsTransactionExample = new CPSTransactionExt();
            cpsTransactionExample.setAuftragId(ccAuftragModel.getAuftragId());
            cpsTransactionList = ((ByExampleDAO) getDAO()).queryByExample(
                    cpsTransactionExample, CPSTransactionExt.class, null, new String[] { "id" });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }

        return cpsTransactionList;
    }

    @Override
    public List<CPSTransaction> findSuccessfulCPSTransaction4TechOrder(Long auftragId) throws FindException {
        if (auftragId == null) {
            return null;
        }
        try {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);

            CPSTransactionDAO cpsTxDao = (CPSTransactionDAO) getDAO();
            List<CPSTransaction> cpsTransactions = ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null))
                    ? cpsTxDao.findCPSTransactions4BillingOrder(auftragDaten.getAuftragNoOrig())
                    : cpsTxDao.findCPSTransactions4TechOrder(auftragId);

            if (CollectionTools.isNotEmpty(cpsTransactions)) {
                // abgebrochene / fehlerhafte CPS-Tx heraus filtern
                CollectionUtils.filter(cpsTransactions, obj -> {
                    CPSTransaction cpsTx = (CPSTransaction) obj;
                    return !(cpsTx.isPreparing() || cpsTx.isCancelled() || cpsTx.isFailure() || cpsTx.isLockSubscriber());
                });
            }

            return cpsTransactions;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CPSTransactionResult doLazyInit(LazyInitMode lazyInitMode, Long auftragId, CPSTransaction followingCPSTx,
            Long sessionId) throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CreateCPSTx4LazyInitCommand.class.getName());
            cmd.prepare(CreateCPSTx4LazyInitCommand.KEY_LAZY_INIT_MODE, lazyInitMode);
            cmd.prepare(CreateCPSTx4LazyInitCommand.KEY_AUFTRAG_ID_FOR_INIT, auftragId);
            cmd.prepare(CreateCPSTx4LazyInitCommand.KEY_FOLLOWING_CPS_TRANSACTION, followingCPSTx);
            cmd.prepare(CreateCPSTx4LazyInitCommand.KEY_SESSION_ID, sessionId);
            Object result = cmd.execute();

            if (result instanceof CPSTransactionResult) {
                return (CPSTransactionResult) result;
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error during lazy init: " + e.getMessage(), e);
        }
    }

    @Override
    public Pair<Integer, Integer> queryAttainableBitrate(Long ccAuftragId, Long sessionId) throws FindException {
        if ((ccAuftragId == null) || (sessionId == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CPSQueryAttainableBitrateCommand.class.getName());
            cmd.prepare(CPSQueryAttainableBitrateCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(CPSQueryAttainableBitrateCommand.KEY_AUFTRAG_ID, ccAuftragId);
            cmd.prepare(CPSQueryAttainableBitrateCommand.KEY_USER_NAME, getUserNameAndFirstNameSilent(sessionId));
            @SuppressWarnings("unchecked")
            Pair<Integer, Integer> result = (Pair<Integer, Integer>) cmd.execute();
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(
                    "Fehler Query Attainable Bitrate: " + e.getMessage(), e);
        }
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    /**
     * Injected
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * Injected
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public long getAuftragIdByAuftragNoOrig(long billingOrderNo, LocalDate when, boolean isDelSub) {
        try {
            // lade alle Hurrican-Auftraege fuer die Taifun OrderNo
            List<AuftragDaten> auftragDaten = (!isDelSub) ? auftragService.findAuftragDaten4OrderNoOrig(billingOrderNo)
                    : auftragService.findAllAuftragDaten4OrderNoOrig(billingOrderNo);
            return filterAndSortAuftragDaten(auftragDaten, when, isDelSub);
        }
        catch (FindException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public long getAuftragIdBySwitchEqn(String switchAK, String hwEQN, LocalDate when) {
        try {
            List<AuftragDaten> auftragDaten = auftragService
                    .findAuftragDatenByEquipment(switchAK, hwEQN, DateConverterUtils.asDate(when));
            return filterAndSortAuftragDaten(auftragDaten, when, false);
        }
        catch (FindException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public long getAuftragIdByRackEqn(String rackBezeichnung, String hwEQN, LocalDate when) {
        try {
            List<AuftragDaten> auftragDaten = auftragService
                    .findAuftragDatenByRackAndEqn(rackBezeichnung, hwEQN, DateConverterUtils.asDate(when));
            return filterAndSortAuftragDaten(auftragDaten, when, false);
        }
        catch (FindException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public long getAuftragIdByCwmpId(String cwmpId, LocalDate when, boolean isDelSub) throws FindException {
        BAuftrag bAuftrag;
        try {
            bAuftrag = billingAuftragService.findAuftrag4CwmpId(cwmpId, DateConverterUtils.asDate(when));
        }
        catch (FindException e) {
            if (e.getCause() instanceof IncorrectResultSizeDataAccessException) {
                throw new IllegalArgumentException(AUFTRAG_SUCHE_NICHT_EINDEUTIG);
            }
            throw e;
        }
        return getAuftragIdByAuftragNoOrig(bAuftrag.getAuftragNoOrig(), when, isDelSub);
    }

    // @formatter:off
    /**
     * Filtert und sortiert die Auftraege um die ID des Auftrages herauszufinden der fuer eine CPS Transaktion geeignet
     * ist. Kriterien:
     * <ul>
     *     <li>Auftraege mit Status < TECHNISCHE_REALISIERUNG und >= AUFTRAG_GEKUENDIGT werden ignoriert</li>
     *     <li>Auftraege mit ueberschrittenem Kuendigungsdatum oder noch nicht erreichtem Inbetriebnahmedatum werden ignoriert</li>
     *     <li>Auftraege mit in TECHNISCHER_REALISIERUNG mit einem Bauauftrag in der Zukunft werden ignoriert</li>
     *     <li>Auftraege, fuer die isCPSProvisioningAllowed "false" liefert, werden ignoriert</li>
     * </ul>
     *
     * @throws IllegalArgumentException wenn uebergebenen Liste leer ist (original oder nach Filterung), oder zu
     *                                  unterschiedl. Taifun AuftragsNummern gehoeren
     */
    // @formatter:on
    private long filterAndSortAuftragDaten(List<AuftragDaten> auftragDaten, LocalDate when, boolean isDelSub)
            throws FindException {
        if (CollectionUtils.isEmpty(auftragDaten)) {
            throw new IllegalArgumentException(KEIN_AUFTRAG_GEFUNDEN);
        }

        // filtere alle Auftraege, nach den Kriterien
        List<AuftragDaten> gueltigeAuftraege = Lists.newArrayList();
        for (AuftragDaten auftrag : auftragDaten) {
            if (!isDelSub) {
                if (AuftragStatus.TECHNISCHE_REALISIERUNG.equals(auftrag.getAuftragStatusId())) {
                    if ((auftrag.getInbetriebnahme() != null)
                            && DateConverterUtils.asDate(when).before(auftrag.getInbetriebnahme())) {
                        continue;
                    }

                    // auftrag nehmen, falls ein aktiver BA fuer when existiert
                    Verlauf verlauf = baService.findActVerlauf4Auftrag(auftrag.getAuftragId(), false);
                    if (verlauf == null || DateConverterUtils.asDate(when).before(verlauf.getRealisierungstermin())) {
                        continue;
                    }
                }
                else {
                    if ((auftrag.getInbetriebnahme() == null)
                            || DateConverterUtils.asDate(when).before(auftrag.getInbetriebnahme())) {
                        continue;
                    }
                }
                if ((auftrag.getKuendigung() != null)
                        && DateConverterUtils.asDate(when).after(auftrag.getKuendigung())) {
                    continue;
                }
                if (auftrag.getAuftragStatusId() < AuftragStatus.TECHNISCHE_REALISIERUNG) {
                    continue;
                }
                if (auftrag.getAuftragStatusId() >= AuftragStatus.AUFTRAG_GEKUENDIGT) {
                    continue;
                }
            }

            if (!isCPSProvisioningAllowed(auftrag.getAuftragId(), LazyInitMode.noInitialLoad, false, false, !isDelSub)
                    .isProvisioningAllowed()) {
                continue;
            }

            if ((!gueltigeAuftraege.isEmpty())
                    && !gueltigeAuftraege.get(0).getAuftragNoOrig().equals(auftrag.getAuftragNoOrig())) {
                throw new IllegalArgumentException(AUFTRAG_SUCHE_NICHT_EINDEUTIG);
            }
            gueltigeAuftraege.add(auftrag);
        }

        if (gueltigeAuftraege.isEmpty()) {
            throw new IllegalArgumentException(AUFTRAG_GEFUNDEN_JEDOCH_NICHT_GUELTIG);
        }

        /**
         * Sortiere Auftraege: Reihenfolge:
         * <ol>
         * <li>Neuschaltung vor Kuendigung beachten - auftragDaten.status aufsteigend</li>
         * <li>nur den mit SO-Data - count(auftrag.cpsTx) aufsteigend</li>
         * </ol>
         */
        List<Ordering<AuftragDaten>> auftragOrderings =
                ImmutableList.of(auftragDatenOrderingByStatus, auftragDatenOrderingByCpsTxCount);
        Collections.sort(gueltigeAuftraege, Ordering.compound(auftragOrderings));

        AuftragDaten validOrder = Iterables.getFirst(gueltigeAuftraege, null);
        return validOrder != null ? validOrder.getAuftragId() : -1L;
    }

    @Override
    public long getAuftragIdByRadiusAccount(String radiusAccount, LocalDate when) {
        try {
            IntAccount account = accountService.findIntAccount(radiusAccount, IntAccount.LINR_EINWAHLACCOUNT, when);
            if (account == null) {
                account = accountService.findIntAccount(radiusAccount, IntAccount.LINR_EINWAHLACCOUNT_KONFIG, when);
                if (account == null) {
                    throw new IllegalArgumentException(KEIN_AUFTRAG_GEFUNDEN);
                }
            }
            List<AuftragTechnik> auftragTechniken = auftragService.findAuftragTechnik4IntAccount(account.getId());
            List<AuftragDaten> auftragDaten = Lists.transform(auftragTechniken,
                    new Function<AuftragTechnik, AuftragDaten>() {
                        @Override
                        @Nonnull
                        public AuftragDaten apply(AuftragTechnik technik) {
                            Preconditions.checkNotNull(technik, "Finder duerfen keine Null-Elemente liefern");
                            try {
                                return auftragService.findAuftragDatenByAuftragIdTx(technik.getAuftragId());
                            }
                            catch (FindException e) {
                                throw new RuntimeException(e.getMessage(), e.getCause());
                            }
                        }
                    }
            );
            return filterAndSortAuftragDaten(auftragDaten, when, false);
        }
        catch (FindException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Long getLastCpsTxServiceOrderType4Rack(Long hwRackId) throws FindException {
        CPSTransactionExt example = new CPSTransactionExt();
        example.setHwRackId(hwRackId);
        example.setTxState(CPSTransaction.TX_STATE_SUCCESS);
        List<CPSTransactionExt> cpsTransactionExtList = findCPSTransaction(example);

        if (cpsTransactionExtList != null && !cpsTransactionExtList.isEmpty()) {
            return EXECDATE_DESC_ORDERING.reverse().sortedCopy(cpsTransactionExtList).get(0).getServiceOrderType();
        }
        return null;
    }

    @Override
    public Long getLastCpsTxServiceOrderType4Subscriber(Long auftragId) {
        try {
            final Comparator<CPSTransactionExt> estimatedExecDesc = (CPSTransactionExt o1, CPSTransactionExt o2) ->
                    o2.getEstimatedExecTime().compareTo(o1.getEstimatedExecTime());
            final Date now = new Date();

            CPSTransactionExt example = new CPSTransactionExt();
            example.setAuftragId(auftragId);
            List<CPSTransactionExt> allTx = findCPSTransaction(example);

            final Optional<CPSTransactionExt> currentTx = allTx.stream()
                    .filter(t -> t.isSubscriberType())
                    .filter(t -> CPSTransaction.TX_STATE_IN_PROVISIONING.equals(t.getTxState())
                            || CPSTransaction.TX_STATE_SUCCESS.equals(t.getTxState()))
                    .filter(t -> now.compareTo(t.getEstimatedExecTime()) >= 0)
                    .sorted(estimatedExecDesc)
                    .findFirst();

            return currentTx.map(t -> t.getServiceOrderType()).orElse(null);
        } catch (Exception e){
            return null;
        }
    }


    @Override
    public boolean isCpsTxServiceOrderTypeExecuteable(Long hwRackId, Long serviceOrderType) throws FindException {

        Long lastSendCpsTxServiceOrderType = getLastCpsTxServiceOrderType4Rack(hwRackId);

        //create nur, wenn noch keine CpsTx besteht oder letzte Tx delete
        if (CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE.equals(serviceOrderType)
              && (  lastSendCpsTxServiceOrderType == null
                 || CPSTransaction.SERVICE_ORDER_TYPE_DELETE_DEVICE.equals(lastSendCpsTxServiceOrderType))) {
            return true;
        }

        // modify nur nach create/modify
        if (CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE.equals(serviceOrderType)
            && (   CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE.equals(lastSendCpsTxServiceOrderType)
                || CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE.equals(lastSendCpsTxServiceOrderType))) {
            return true;
        }

        // delete nur nach create/modify
        if (CPSTransaction.SERVICE_ORDER_TYPE_DELETE_DEVICE.equals(serviceOrderType)
            && (   CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE.equals(lastSendCpsTxServiceOrderType)
                || CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE.equals(lastSendCpsTxServiceOrderType))) {
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public Long getExecutableCpsTxServiceOrderType4Subscriber(Long auftragId) {
        final Long  currentType = getLastCpsTxServiceOrderType4Subscriber(auftragId);

        if (currentType != null){
            if (CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB.equals(currentType)){
                return CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB;
            }

            if (CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB.equals(currentType)
                    || CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB.equals(currentType)) {
                return CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB;
            }
        }

        return CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB;
    }

}
