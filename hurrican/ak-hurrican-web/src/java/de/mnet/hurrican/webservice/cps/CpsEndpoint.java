/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 18:42:56
 */
package de.mnet.hurrican.webservice.cps;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.servlet.*;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionLog;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.impl.command.cps.AbstractCPSCommand;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.cps.GetSoDataRequest;
import de.mnet.hurrican.cps.GetSoDataResponse;
import de.mnet.hurrican.cps.LacAndDn;
import de.mnet.hurrican.cps.OrderQuery;
import de.mnet.hurrican.cps.ProvisionRequest;
import de.mnet.hurrican.cps.ProvisionResponse;
import de.mnet.hurrican.cps.ProvisionResult;
import de.mnet.hurrican.cps.SubscriberType;
import de.mnet.hurrican.webservice.base.MnetServletContextHelper;

/**
 * SOAP 1.1 endpoint fuer CPS Web-Services.
 *
 *
 */
@Endpoint
@ObjectsAreNonnullByDefault
public class CpsEndpoint {
    public static final String CPS_NAMESPACE = "http://www.mnet.de/hurrican/cps/1.0/";
    private static final Logger LOGGER = Logger.getLogger(CpsEndpoint.class);

    @Autowired
    private CPSService cpsService;

    @Autowired
    private RufnummerService rufnummerService;

    @Autowired
    private XStreamMarshaller xStreamMarshaller;

    @PayloadRoot(localPart = "getSoDataRequest", namespace = CPS_NAMESPACE)
    @ResponsePayload
    public GetSoDataResponse getSoData(@RequestPayload GetSoDataRequest request) {
        Preconditions.checkArgument(!request.getWhen().isBefore(LocalDate.now()),
                "Parameter 'when' darf nicht in der Vergangenheit liegen");

        long auftragId;
        try {
            auftragId = getAuftragId(request.getQuery(), request.getWhen(), false);
        }
        catch (FindException e1) {
            throw new RuntimeException(e1.getMessage(), e1.getCause());
        }
        CreateCPSTransactionParameter parameters = new CreateCPSTransactionParameter();
        parameters.setAuftragId(auftragId);
        parameters.setEstimatedExecTime(DateConverterUtils.asDate(request.getWhen()));
        parameters.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_GET_SODATA);
        parameters.setServiceOrderPrio(CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT);
        parameters.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_WEBSERVICE);
        parameters.setSessionId(getSessionId());

        CPSTransaction cpsTransaction = null;
        try {
            CPSTransactionResult cpsTransactionResult = cpsService.createCPSTransaction(parameters);
            if (!cpsTransactionResult.getWarnings().isEmpty()) {
                throw new RuntimeException(cpsTransactionResult.getWarnings().getWarningsAsText());
            }
            if ((cpsTransactionResult.getCpsTransactions() == null)
                    || (cpsTransactionResult.getCpsTransactions().get(0) == null)) {
                throw new RuntimeException("Keine Antwort vom CpsService");
            }
            cpsTransaction = cpsTransactionResult.getCpsTransactions().get(0);
            cpsTransaction.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_GET_SODATA);
            cpsTransaction.setTxState(CPSTransaction.TX_STATE_SUCCESS);
            cpsTransaction = cpsService.saveCPSTransaction(cpsTransaction, getSessionId());
        }
        catch (StoreException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        String soDataXml = new String(cpsTransaction.getServiceOrderData(), StringTools.CC_DEFAULT_CHARSET);

        GetSoDataResponse response = new GetSoDataResponse();
        response.setSodata(soDataXml);
        return response;
    }

    /*
     * Ermittle sessionId ueber Servlet-Context.
     *
     * Laueft der Endpoint als Unit-Test, so liefert diese Methode null - ist fuer den Testfall OK
     */
    @CheckForNull
    protected Long getSessionId() {
        try {
            ServletContext ctx = MnetServletContextHelper.getServletContextFromTCH();
            return (Long) ctx.getAttribute(HurricanConstants.HURRICAN_SESSION_ID);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    String convertXstreamSoDataXml(CPSServiceOrderData soData) {
        try {
            String soDataXml = AbstractCPSCommand.transformSOData2XML(soData, xStreamMarshaller);
            return soDataXml;
        }
        catch (ServiceCommandException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    /**
     * CPS provisionieren. Je uebermitteltem Query-Parameter folgende Aktionen durchfuehren: <ul>
     * <li>Hurrican-Auftragsnummmer ueber query-Parameter ermitteln</li> <li>CPS-Transaktion erstellen</li>
     * <li>CPS-Transaktion an CPS uebermitteln - dieser verarbeitet diese synchron per default. Sollte asynchron
     * verarbeitung gewuenscht {@link ProvisionRequest#isAsynch()} auf {@literal TRUE} setzen.</li> <li>Query und ggf.
     * Fehler (auch vom CPS) in Result protokollieren</li> </ul>
     */
    @PayloadRoot(localPart = "provisionRequest", namespace = CPS_NAMESPACE)
    @ResponsePayload
    public ProvisionResponse provision(@RequestPayload ProvisionRequest request) {
        ProvisionResponse response = new ProvisionResponse();
        for (OrderQuery query : request.getQuery()) {
            ProvisionResult result = new ProvisionResult();
            result.setQuery(query);
            try {
                long auftragId = getAuftragId(query, LocalDate.now(), (request.getSubscribe() == SubscriberType.DELETE));
                CPSTransaction tx = createCpsTx(auftragId, request.getSubscribe());
                result.setCpsTxId(tx.getId());
                boolean isSynchronous = (request.isAsynch() == null) ? true : !request.isAsynch();
                cpsService.sendCPSTx2CPS(tx, getSessionId(), isSynchronous);
                if ((isSynchronous && !CPSTransaction.TX_STATE_SUCCESS.equals(tx.getTxState()))
                        || (!isSynchronous && NumberTools.isNotIn(tx.getTxState(),
                        new Number[] { CPSTransaction.TX_STATE_SUCCESS, CPSTransaction.TX_STATE_IN_PROVISIONING }))) {
                    List<CPSTransactionLog> logs = cpsService.findCPSTxLogs4CPSTx(tx.getId());
                    StringBuilder sb = new StringBuilder();
                    for (CPSTransactionLog log : logs) {
                        sb.append(log.getMessage());
                    }
                    result.setError(sb.toString());
                }
            }
            catch (Exception e) {
                LOGGER.error(String.format("Fehler beim Provisionieren mit query=%s", query), e);
                result.setError(e.getMessage());
            }
            response.getResult().add(result);
        }
        return response;
    }


    /**
     * Findet den Auftrag (dessen ID) passend zur query.
     */
    private long getAuftragId(OrderQuery query, LocalDate when, boolean isDelSub) throws FindException {
        if (query.getBillingOrderNo() != null) {
            return cpsService.getAuftragIdByAuftragNoOrig(query.getBillingOrderNo(), when, isDelSub);
        }
        else if (query.getLacAndDn() != null) {
            Long billingOrderNo = findAuftragNoOrig(query.getLacAndDn(), when);
            return cpsService.getAuftragIdByAuftragNoOrig(billingOrderNo, when, isDelSub);
        }
        else if (query.getSwitchAndEqn() != null) {
            return cpsService.getAuftragIdBySwitchEqn(query.getSwitchAndEqn().getSwitch(), query.getSwitchAndEqn()
                    .getEqn(), when);
        }
        else if (query.getRackAndEqn() != null) {
            return cpsService.getAuftragIdByRackEqn(query.getRackAndEqn().getRack(), query.getRackAndEqn()
                    .getEqn(), when);
        }
        else if (query.getRadiusAccount() != null) {
            return cpsService.getAuftragIdByRadiusAccount(query.getRadiusAccount(), when);
        }
        else if (query.getCwmpId() != null) {
            return cpsService.getAuftragIdByCwmpId(query.getCwmpId(), when, isDelSub);
        }
        throw new IllegalArgumentException("query parameter wird z.Zt. nicht unterstuetzt");
    }

    /**
     * Sucht für Lac/Dn eine Taifun Auftragsnummer.
     *
     * @param lacAndDn
     * @param when
     * @return
     * @throws FindException
     * @throws IllegalArgumentException wenn keine oder keine eindeutige Auftragsnummer gefunden wird
     */
    protected Long findAuftragNoOrig(LacAndDn lacAndDn, LocalDate when) throws FindException {
        RufnummerQuery rnQuery = new RufnummerQuery();
        rnQuery.setOnKz(lacAndDn.getLac());
        rnQuery.setDnBase(lacAndDn.getDn());
        rnQuery.setOnlyActive(true);
        rnQuery.setValid(DateConverterUtils.asDate(when));
        List<AuftragDNView> auftragDNViews = rufnummerService.findAuftragDNViews(rnQuery);
        Long billingOrderNo = null;
        for (AuftragDNView auftragDNView : auftragDNViews) {
            if (auftragDNView.getAuftragStatusId() >= AuftragStatus.AUFTRAG_GEKUENDIGT) {
                continue;
            }
            if (billingOrderNo == null) {
                billingOrderNo = auftragDNView.getAuftragNoOrig();
            }
            else if (!billingOrderNo.equals(auftragDNView.getAuftragNoOrig())) {
                throw new IllegalArgumentException("Lac/Dn ist nicht eindeutig einem Taifun Auftrag zuzuordnen");
            }
        }
        if (billingOrderNo == null) {
            throw new IllegalArgumentException("Keinen Taifun Auftrag für Lac/Dn gefunden");
        }
        return billingOrderNo;
    }

    private CPSTransaction createCpsTx(long auftragId, SubscriberType type) {
        CreateCPSTransactionParameter parameters = new CreateCPSTransactionParameter();
        parameters.setAuftragId(auftragId);
        parameters.setEstimatedExecTime(DateConverterUtils.asDate(LocalDate.now()));
        switch (type) {
            case INIT:
                parameters.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
                parameters.setLazyInitMode(LazyInitMode.initialLoad);
                break;
            case CREATE:
                parameters.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
                parameters.setLazyInitMode(LazyInitMode.noInitialLoad);
                break;
            case MODIFY:
                parameters.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB);
                parameters.setForceExecType(Boolean.TRUE);
                break;
            case DELETE:
                parameters.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);
                parameters.setForceExecType(Boolean.TRUE);
                break;
            default:
                throw new IllegalArgumentException("Nicht unterstuetzter SubscriberType: " + type);
        }
        parameters.setServiceOrderPrio(CPSTransaction.SERVICE_ORDER_PRIO_HIGH);
        parameters.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_WEBSERVICE);
        parameters.setSessionId(getSessionId());

        CPSTransaction cpsTransaction;
        try {
            CPSTransactionResult cpsTransactionResult = cpsService.createCPSTransaction(parameters);
            if (!cpsTransactionResult.getWarnings().isEmpty()) {
                throw new RuntimeException(cpsTransactionResult.getWarnings().getWarningsAsText());
            }
            if ((cpsTransactionResult.getCpsTransactions() == null)
                    || (cpsTransactionResult.getCpsTransactions().get(0) == null)) {
                throw new RuntimeException("Keine Antwort vom CpsService");
            }
            cpsTransaction = cpsTransactionResult.getCpsTransactions().get(0);
            return cpsTransaction;
        }
        catch (StoreException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }

    }
}
