/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.2012 13:08:02
 */
package de.augustakom.hurrican.service.elektra.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.impl.AbstractHurricanWebServiceClient;
import de.augustakom.hurrican.service.elektra.ElektraCancelOrderResponseDto;
import de.augustakom.hurrican.service.elektra.ElektraFacadeService;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.augustakom.hurrican.service.elektra.builder.ElektraCancelOrderResponseDtoBuilder;
import de.augustakom.hurrican.service.elektra.builder.ElektraResponseDtoBuilder;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.common.webservice.tools.WebServiceFaultUnmarshaller;
import de.mnet.elektra.services.AddDialNumbersRequest;
import de.mnet.elektra.services.AddDialNumbersResponse;
import de.mnet.elektra.services.CancelOrderFault;
import de.mnet.elektra.services.CancelOrderRequest;
import de.mnet.elektra.services.CancelOrderResponse;
import de.mnet.elektra.services.ChangeOrderCancellationDateRequest;
import de.mnet.elektra.services.ChangeOrderCancellationDateResponse;
import de.mnet.elektra.services.ChangeOrderDialNumberRequest;
import de.mnet.elektra.services.ChangeOrderDialNumberResponse;
import de.mnet.elektra.services.ChangeOrderRealDateFault;
import de.mnet.elektra.services.ChangeOrderRealDateRequest;
import de.mnet.elektra.services.ChangeOrderRealDateRequest.Account;
import de.mnet.elektra.services.ChangeOrderRealDateResponse;
import de.mnet.elektra.services.DeleteDialNumbersRequest;
import de.mnet.elektra.services.DeleteDialNumbersResponse;
import de.mnet.elektra.services.DialingNumberType;
import de.mnet.elektra.services.GenerateAndPrintReportByType;
import de.mnet.elektra.services.GenerateAndPrintReportFault;
import de.mnet.elektra.services.GenerateAndPrintReportRequest;
import de.mnet.elektra.services.GenerateAndPrintReportResponse;
import de.mnet.elektra.services.GenerateAndPrintReportWithEvaluationType;
import de.mnet.elektra.services.NumberPortierungType;
import de.mnet.elektra.services.PortCancelledDialNumberRequest;
import de.mnet.elektra.services.PortCancelledDialNumberResponse;
import de.mnet.elektra.services.TerminateOrderFault;
import de.mnet.elektra.services.TerminateOrderRequest;
import de.mnet.elektra.services.TerminateOrderResponse;
import de.mnet.elektra.services.UndoPlannedOrderCancellationRequest;
import de.mnet.elektra.services.UndoPlannedOrderCancellationResponse;
import de.mnet.elektra.services.UpdatePortKennungTnbRequest;
import de.mnet.elektra.services.UpdatePortKennungTnbResponse;
import de.mnet.wbci.model.CarrierCode;

/**
 * Implementierung von {@link ElektraFacadeService}
 */
@ObjectsAreNonnullByDefault
public class ElektraFacadeServiceImpl extends AbstractHurricanWebServiceClient implements ElektraFacadeService {

    private static final Logger LOGGER = Logger.getLogger(ElektraFacadeServiceImpl.class);

    @Resource(name = "elektraWebServiceTemplate")
    private MnetWebServiceTemplate elektraWebServiceTemplate;
    @Resource(name = "de.augustakom.hurrican.service.cc.AccountService")
    private AccountService accountService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;

    @PostConstruct
    protected WebServiceTemplate configureAndGetWSTemplate() throws ServiceNotFoundException {
        return configureAndGetWsTemplateForConfig(WebServiceConfig.WS_CFG_ELEKTRA, elektraWebServiceTemplate);
    }

    @Override
    public String changeOrderRealDate(AuftragDaten auftragDaten, @Nullable Long referencedOrderNoOrig,
            CBVorgang cbVorgang, Set<Long> subOrderIds) throws StoreException, FindException {
        LocalDate realDate = Instant.ofEpochMilli(cbVorgang.getReturnRealDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LOGGER.info(String.format("changeOrderRealDateRequest: %s / %s / %s", auftragDaten.getAuftragNoOrig(), referencedOrderNoOrig, realDate));
        try {
            IntAccount account = getAccount4Order(auftragDaten, subOrderIds);
            ChangeOrderRealDateRequest changeOrderRealDateRequest =
                    createChangeOrderRealDateRequest(auftragDaten, referencedOrderNoOrig, cbVorgang, account, realDate);

            Object changeOrderResult = elektraWebServiceTemplate.marshalSendAndReceive(changeOrderRealDateRequest);

            return ((ChangeOrderRealDateResponse) changeOrderResult).getModifications();
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            ChangeOrderRealDateFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    ChangeOrderRealDateFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_MODIFY_ORDER,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_MODIFY_ORDER, new Object[] { "", e.getMessage() });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public String generateAndPrintReportWithEvaluation(AuftragDaten auftragDaten, CBVorgang cbVorgang) throws StoreException, FindException {
        LOGGER.info(String.format("generateAndPrintReportWithEvaluation: %s", auftragDaten.getAuftragNoOrig()));

        Date tmpRealDate = cbVorgang.getReturnRealDate();
        // emulate the joda behaviour
        if (tmpRealDate == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        LocalDate realDate = Instant.ofEpochMilli(cbVorgang.getReturnRealDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

        GenerateAndPrintReportRequest generateAndPrintReportRequest = createGenerateAndPrintReportWithEvaluationRequest(auftragDaten, cbVorgang, realDate);
        return generateAndPrintReport(generateAndPrintReportRequest);
    }

    @Override
    public String generateAndPrintReportByType(Long billingOrderNoOrig, String documentName) throws StoreException, FindException {
        LOGGER.info(String.format("generateAndPrintReportByType: %s", billingOrderNoOrig));

        GenerateAndPrintReportRequest generateAndPrintReportRequest = createGenerateAndPrintReportByTypeRequest(billingOrderNoOrig, documentName);
        return generateAndPrintReport(generateAndPrintReportRequest);
    }

    private String generateAndPrintReport(GenerateAndPrintReportRequest generateAndPrintReportRequest) throws StoreException {
        try {
            Object generateAndPrintReportResult = elektraWebServiceTemplate.marshalSendAndReceive(generateAndPrintReportRequest);

            return ((GenerateAndPrintReportResponse) generateAndPrintReportResult).getResult();
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            GenerateAndPrintReportFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    GenerateAndPrintReportFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_REPORT_GENERATION,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_REPORT_GENERATION, new Object[] { "", e.getMessage() });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public String terminateOrder(CBVorgang cbVorgang) throws StoreException {
        LOGGER.info(String.format("terminateOrder: %s / %s", cbVorgang.getAuftragId(), cbVorgang.getTyp()));
        try {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId());
            if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) {
                throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED,
                        new Object[] { "Zur Auftragsnummer wurden keine Daten gefunden bzw. besitzt der Auftrag keine Billing-Referenz!" });
            }

            TerminateOrderRequest request = new TerminateOrderRequest();
            request.setBillingOrderNoOrig(auftragDaten.getAuftragNoOrig());
            request.setOrderType(cbVorgang.getTyp());

            Object result = elektraWebServiceTemplate.marshalSendAndReceive(request);
            return ((TerminateOrderResponse) result).getModifications();
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            TerminateOrderFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    TerminateOrderFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_TERMINATE_ORDER,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_TERMINATE_ORDER, new Object[] { "", e.getMessage() });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public ElektraResponseDto changeOrderDialNumber(LocalDate realDate, LocalDate ruemVaReceivedAt, LocalDate vaOrderSentAt, List<Long> billingOrderNos, NumberPortierungType numberPortierung)
            throws StoreException {
        LOGGER.info(String.format("Calling changeOrderDialNumberRequest: %s (billingOrderNo) / %s", billingOrderNos, realDate));

        try {
            ChangeOrderDialNumberRequest request = new ChangeOrderDialNumberRequest();

            request.setRealDate(realDate);
            request.setRuemVaReceivedAt(ruemVaReceivedAt);
            request.setVaOrderSentAt(vaOrderSentAt);
            request.getBillingOrderNoOrig().addAll(billingOrderNos);
            request.setNumberPortierung(numberPortierung);

            return new ElektraResponseDtoBuilder()
                    .buildFrom((ChangeOrderDialNumberResponse) elektraWebServiceTemplate.marshalSendAndReceive(request));
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            TerminateOrderFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    TerminateOrderFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_MODIFY_ORDER,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_MODIFY_ORDER, new Object[] { "", e.getMessage() });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public ElektraResponseDto updatePortKennungTnb(List<Long> auftragOrderNo, String pkiKennungAuf, String pkiKennungAbg) throws StoreException {
        try {
            UpdatePortKennungTnbRequest request = new UpdatePortKennungTnbRequest();
            request.getBillingOrderNoOrig().addAll(auftragOrderNo);
            request.setPortKennungTnbAbg(pkiKennungAbg);
            request.setPortKennungTnbAuf(pkiKennungAuf);

            return new ElektraResponseDtoBuilder()
                    .buildFrom((UpdatePortKennungTnbResponse) elektraWebServiceTemplate.marshalSendAndReceive(request));
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            TerminateOrderFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    TerminateOrderFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_MODIFY_ORDER,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_MODIFY_ORDER, new Object[] { "", e.getMessage() });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public ElektraResponseDto portCancelledDialNumber(LocalDateTime realTimeFrom, LocalDateTime realTimeTo,
            List<DialingNumberType> dialNumbers, LocalDate rrnpReceivedAt, String pkiKennungAuf) {
        PortCancelledDialNumberRequest request = new PortCancelledDialNumberRequest();
        request.setPortKennungTnbAuf(pkiKennungAuf);
        request.setRealTimeFrom(realTimeFrom);
        request.setRealTimeTo(realTimeTo);
        request.setRrnpRequestReceivedAt(rrnpReceivedAt);
        request.getDialNumbers().addAll(dialNumbers);
        return new ElektraResponseDtoBuilder()
                .buildFrom((PortCancelledDialNumberResponse) elektraWebServiceTemplate.marshalSendAndReceive(request));
    }

    @Override
    public ElektraResponseDto addDialNumber(Long orderNoOrig, DialingNumberType toAdd, String pkiAuf, String pkiAbg, LocalDate realDate)
                throws StoreException {
        try {
            AddDialNumbersRequest request = new AddDialNumbersRequest();
            request.setBillingOrderNoOrig(orderNoOrig);
            request.setPortKennungTnbAbg(pkiAbg);
            request.setPortKennungTnbAuf(pkiAuf);
            request.setRealDate(realDate);
            request.getDialNumbers().add(toAdd);

            return new ElektraResponseDtoBuilder()
                    .buildFrom((AddDialNumbersResponse) elektraWebServiceTemplate.marshalSendAndReceive(request));
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            ChangeOrderRealDateFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    ChangeOrderRealDateFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_ADD_DIALNUMBER,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_ADD_DIALNUMBER, new Object[] { "", e.getMessage() });
        }
    }

    @Override
    public ElektraResponseDto deleteDialNumber(Long orderNoOrig, DialingNumberType toDelete) throws StoreException {
        try {
            DeleteDialNumbersRequest request = new DeleteDialNumbersRequest();
            request.setBillingOrderNoOrig(orderNoOrig);
            request.getDialNumbers().add(toDelete);
    
            return new ElektraResponseDtoBuilder()
                    .buildFrom((DeleteDialNumbersResponse) elektraWebServiceTemplate.marshalSendAndReceive(request));
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            ChangeOrderRealDateFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    ChangeOrderRealDateFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_DELETE_DIALNUMBER,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_DELETE_DIALNUMBER, new Object[] { "", e.getMessage() });
        }
    }

    private
    @Nullable
    IntAccount getAccount4Order(AuftragDaten auftragDaten, @Nullable Set<Long> subOrderIds) throws FindException {
        Set<Long> orderIds = new HashSet<>();
        orderIds.add(auftragDaten.getAuftragId());
        if (subOrderIds != null) {
            orderIds.addAll(subOrderIds);
        }

        IntAccount account = null;
        for (Long orderId : orderIds) {
            AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(orderId);
            if ((auftragTechnik != null) && (auftragTechnik.getIntAccountId() != null)) {
                account = accountService.findIntAccountById(auftragTechnik.getIntAccountId());
            }

            if (account != null) {
                break;
            }
        }

        return account;
    }

    @Override
    public ElektraResponseDto changeOrderCancellationDate(@Nullable Set<Long> referencedOrderNoOrigs, LocalDate realDate)
            throws StoreException, FindException {
        LOGGER.info(String.format("changeOrderCancellationDateRequest: %s / %s", referencedOrderNoOrigs, realDate));
        try {
            ChangeOrderCancellationDateRequest changeOrderCancellationDateRequest =
                    createChangeOrderCancellationDateRequest(new ArrayList<>(referencedOrderNoOrigs), realDate);

            Object changeOrderResult = elektraWebServiceTemplate.marshalSendAndReceive(changeOrderCancellationDateRequest);
            return new ElektraResponseDtoBuilder()
                    .buildFrom((ChangeOrderCancellationDateResponse) changeOrderResult);
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            ChangeOrderRealDateFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    ChangeOrderRealDateFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_MODIFY_ORDER,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_MODIFY_ORDER, new Object[] { "", e.getMessage() });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED, new Object[] { e.getMessage() }, e);
        }
    }


    @Override
    public ElektraCancelOrderResponseDto cancelOrder(Long orderNoOrig, LocalDate cancelDate, LocalDate entryDate, CarrierCode futureCarrier,
            List<DialingNumberType> dialingNumberTypesToPort) throws StoreException {
        LOGGER.info(String.format("cancelOrder: %s / %s", orderNoOrig, cancelDate));
        try {
            CancelOrderRequest request = new CancelOrderRequest();
            request.setBillingOrderNoOrig(orderNoOrig);
            request.setCancelDate(cancelDate);
            request.setFutureCarrier(futureCarrier.getITUCarrierCode());
            request.setEntryDate(entryDate);
            if (dialingNumberTypesToPort != null) {
                request.getDialNumbers().addAll(dialingNumberTypesToPort);
            }

            Object cancelOrderResponse = elektraWebServiceTemplate.marshalSendAndReceive(request);
            return new ElektraCancelOrderResponseDtoBuilder()
                    .buildFrom((CancelOrderResponse) cancelOrderResponse);
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            CancelOrderFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    CancelOrderFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_CANCEL_ORDER,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_CANCEL_ORDER, new Object[] { "", e.getMessage() });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED, new Object[] { e.getMessage() }, e);
        }
    }


    @Override
    public ElektraCancelOrderResponseDto undoCancellation(final Long billingOrderNoOrig) throws StoreException {
        LOGGER.info(String.format("undoCancellation: %s", billingOrderNoOrig));
        try {
            UndoPlannedOrderCancellationRequest request = new UndoPlannedOrderCancellationRequest();
            request.setBillingOrderNoOrig(billingOrderNoOrig);

            Object undoCancellationResponse = elektraWebServiceTemplate.marshalSendAndReceive(request);
            return new ElektraCancelOrderResponseDtoBuilder()
                    .buildFrom((UndoPlannedOrderCancellationResponse) undoCancellationResponse);
        }
        catch (SoapFaultClientException e) {
            LOGGER.error(e.getMessage(), e);
            CancelOrderFault fault = WebServiceFaultUnmarshaller.extractSoapFaultDetail(
                    CancelOrderFault.class, e.getSoapFault(), elektraWebServiceTemplate.getUnmarshaller());
            if (fault != null) {
                throw new StoreException(StoreException.ERROR_TAIFUN_UNDO_ORDER_CANCELLATION,
                        new Object[] { fault.getErrorCode(), fault.getErrorDescription() });
            }
            throw new StoreException(StoreException.ERROR_TAIFUN_UNDO_ORDER_CANCELLATION, 
                    new Object[] { "", e.getMessage() });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_TAIFUN_UNEXPECTED, new Object[] { e.getMessage() }, e);
        }
    }


    private ChangeOrderCancellationDateRequest createChangeOrderCancellationDateRequest(
            List<Long> referencedOrderNoOrigs, LocalDate realDate) {
        ChangeOrderCancellationDateRequest request = new ChangeOrderCancellationDateRequest();
        request.getBillingOrderNoOrig().addAll(referencedOrderNoOrigs);
        request.setRealDate(realDate);
        return request;
    }

    private ChangeOrderRealDateRequest createChangeOrderRealDateRequest(AuftragDaten auftragDaten,
            Long referencedOrderNoOrig, CBVorgang cbVorgang, @Nullable IntAccount intAccount, LocalDate realDate) {
        ChangeOrderRealDateRequest request = new ChangeOrderRealDateRequest();
        request.setBillingOrderNoOrig(auftragDaten.getAuftragNoOrig());
        request.setOrderType(cbVorgang.getTyp());
        request.setRealDate(realDate);
        request.setBillingOrderNoOld(referencedOrderNoOrig);

        if (intAccount != null) {
            Account account = new Account();
            account.setAccountName(intAccount.getAccount());
            account.setPassword(intAccount.getPasswort());
            request.setAccount(account);
        }

        return request;
    }

    private GenerateAndPrintReportRequest createGenerateAndPrintReportWithEvaluationRequest(AuftragDaten auftragDaten, CBVorgang cbVorgang, LocalDate realDate) {
        GenerateAndPrintReportRequest request = new GenerateAndPrintReportRequest();
        GenerateAndPrintReportWithEvaluationType printReportWithEvaluationType = new GenerateAndPrintReportWithEvaluationType();
        printReportWithEvaluationType.setBillingOrderNoOrig(auftragDaten.getAuftragNoOrig());
        printReportWithEvaluationType.setOrderType(cbVorgang.getTyp());
        printReportWithEvaluationType.setRealDate(realDate);
        printReportWithEvaluationType.setHurricanProdId(auftragDaten.getProdId());
        printReportWithEvaluationType.setKundeVorOrt(cbVorgang.getReturnKundeVorOrt());
        printReportWithEvaluationType.setBusinessCaseIntern(cbVorgang.getGfTypInternRefId());
        request.setGenerateAndPrintReportWithEvaluationRequest(printReportWithEvaluationType);

        return request ;
    }

    private GenerateAndPrintReportRequest createGenerateAndPrintReportByTypeRequest(Long billingOrderNoOrig, String documentName) {
        GenerateAndPrintReportRequest request = new GenerateAndPrintReportRequest();
        GenerateAndPrintReportByType generateAndPrintReportByType = new GenerateAndPrintReportByType();
        generateAndPrintReportByType.setBillingOrderNoOrig(billingOrderNoOrig);
        generateAndPrintReportByType.setDocumentName(documentName);
        generateAndPrintReportByType.setDoScanViewUpdate(Boolean.FALSE);
        request.setGenerateAndPrintReportByTypeRequest(generateAndPrintReportByType);

        return request ;
    }

}
