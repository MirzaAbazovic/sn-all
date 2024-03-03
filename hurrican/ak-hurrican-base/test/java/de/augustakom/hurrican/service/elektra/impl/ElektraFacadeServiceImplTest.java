package de.augustakom.hurrican.service.elektra.impl;

import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.elektra.ElektraCancelOrderResponseDto;
import de.augustakom.hurrican.service.elektra.ElektraFacadeService;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.mnet.elektra.services.AddDialNumbersRequest;
import de.mnet.elektra.services.AddDialNumbersResponse;
import de.mnet.elektra.services.CancelOrderRequest;
import de.mnet.elektra.services.CancelOrderResponse;
import de.mnet.elektra.services.ChangeOrderCancellationDateRequest;
import de.mnet.elektra.services.ChangeOrderCancellationDateResponse;
import de.mnet.elektra.services.ChangeOrderDialNumberRequest;
import de.mnet.elektra.services.ChangeOrderDialNumberResponse;
import de.mnet.elektra.services.DeleteDialNumbersRequest;
import de.mnet.elektra.services.DeleteDialNumbersResponse;
import de.mnet.elektra.services.DialingNumberType;
import de.mnet.elektra.services.GenerateAndPrintReportRequest;
import de.mnet.elektra.services.GenerateAndPrintReportResponse;
import de.mnet.elektra.services.GenerateAndPrintReportWithEvaluationType;
import de.mnet.elektra.services.NumberPortierungType;
import de.mnet.elektra.services.PortCancelledDialNumberRequest;
import de.mnet.elektra.services.PortCancelledDialNumberResponse;
import de.mnet.elektra.services.ResponseStatusType;
import de.mnet.elektra.services.UndoPlannedOrderCancellationRequest;
import de.mnet.elektra.services.UndoPlannedOrderCancellationResponse;
import de.mnet.elektra.services.UpdatePortKennungTnbRequest;
import de.mnet.elektra.services.UpdatePortKennungTnbResponse;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Portierungszeitfenster;

@Test(groups = { BaseTest.UNIT })
public class ElektraFacadeServiceImplTest {

    @InjectMocks
    private ElektraFacadeService testling;

    @Mock
    private MnetWebServiceTemplate elektraWebServiceTemplate;
    @Mock
    private AccountService accountService;
    @Mock
    private CCAuftragService auftragService;

    @BeforeMethod
    public void setUp() {
        testling = new ElektraFacadeServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdatePortKennungTnb() throws Exception {
        final String pkiAufkennung = "D001";
        final String pkiAbgKennung = "D052";

        final UpdatePortKennungTnbResponse response = new UpdatePortKennungTnbResponse();
        response.setModifications("Modified ORDER__NO 1");
        response.setStatus(ResponseStatusType.OK);

        when(elektraWebServiceTemplate.marshalSendAndReceive(any(UpdatePortKennungTnbRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                UpdatePortKennungTnbRequest request = (UpdatePortKennungTnbRequest) invocationOnMock.getArguments()[0];
                Assert.assertEquals(request.getPortKennungTnbAuf(), pkiAufkennung);
                Assert.assertEquals(request.getPortKennungTnbAbg(), pkiAbgKennung);

                Assert.assertEquals(request.getBillingOrderNoOrig().size(), 1L);
                Assert.assertEquals(request.getBillingOrderNoOrig().get(0), new Long(1));
                return response;
            }
        });

        final ElektraResponseDto elektraResponseDto = testling.updatePortKennungTnb(Collections.singletonList(1L),
                pkiAufkennung, pkiAbgKennung);
        Assert.assertEquals(elektraResponseDto.getModifications(), response.getModifications());
        Assert.assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.OK);

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(UpdatePortKennungTnbRequest.class));
    }

    @Test
    public void testChangeOrderDialNumber() throws Exception {
        final String portKennungTnbAbg = "ABC";
        final String portKennungTnbAuf = "XYZ";
        final LocalDate realDate = LocalDateTime.now().plusMinutes(1).toLocalDate();
        final LocalDate ruemVaReceivedAt = LocalDateTime.now().plusMinutes(2).toLocalDate();
        final LocalDate vaOrderSentAt = LocalDateTime.now().plusMinutes(3).toLocalDate();
        final String responseMsg = "Changed Order";
        final List<Long> orderNumbers = Collections.singletonList(1L);
        final List<DialingNumberType> dialNumbers = Collections.singletonList(createDialNumber());

        final ChangeOrderDialNumberResponse response = new ChangeOrderDialNumberResponse();
        response.setModifications(responseMsg);
        response.setStatus(ResponseStatusType.OK);

        when(elektraWebServiceTemplate.marshalSendAndReceive(any(ChangeOrderDialNumberRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ChangeOrderDialNumberRequest request = (ChangeOrderDialNumberRequest) invocationOnMock.getArguments()[0];

                Assert.assertEquals(request.getRealDate(), realDate);
                Assert.assertEquals(request.getRuemVaReceivedAt(), ruemVaReceivedAt);
                Assert.assertEquals(request.getVaOrderSentAt(), vaOrderSentAt);

                Assert.assertEquals(request.getBillingOrderNoOrig().size(), 1L);
                Assert.assertEquals(request.getBillingOrderNoOrig().get(0), orderNumbers.get(0));

                NumberPortierungType numberPortierung = request.getNumberPortierung();
                Assert.assertEquals(numberPortierung.getPortKennungTnbAbg(), portKennungTnbAbg);
                Assert.assertEquals(numberPortierung.getPortKennungTnbAuf(), portKennungTnbAuf);
                Assert.assertEquals(numberPortierung.getDialNumbers().size(), 1L);
                Assert.assertEquals(numberPortierung.getDialNumbers().get(0), dialNumbers.get(0));
                return response;
            }
        });

        NumberPortierungType numberPortierung = new NumberPortierungType();
        numberPortierung.setPortKennungTnbAbg(portKennungTnbAbg);
        numberPortierung.setPortKennungTnbAuf(portKennungTnbAuf);
        numberPortierung.getDialNumbers().addAll(dialNumbers);

        final ElektraResponseDto elektraResponseDto = testling.changeOrderDialNumber(realDate, ruemVaReceivedAt,
                vaOrderSentAt, orderNumbers, numberPortierung);
        Assert.assertEquals(elektraResponseDto.getModifications(), response.getModifications());
        Assert.assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.OK);

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(ChangeOrderDialNumberRequest.class));
    }

    @Test
    public void testPortCancelledDialNumber() throws Exception {
        final String portKennungTnbAuf = "D001";
        final LocalDate realDate = LocalDateTime.now().toLocalDate();
        final LocalDate rrnpReceivedAt = LocalDateTime.now().minusMinutes(5).toLocalDate();
        final String responseMsg = "Changed Order";
        final List<DialingNumberType> dialNumbers = Collections.singletonList(createDialNumber());

        final PortCancelledDialNumberResponse response = new PortCancelledDialNumberResponse();
        response.setModifications(responseMsg);
        response.setStatus(ResponseStatusType.OK);

        when(elektraWebServiceTemplate.marshalSendAndReceive(any(PortCancelledDialNumberRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                PortCancelledDialNumberRequest request = (PortCancelledDialNumberRequest) invocationOnMock.getArguments()[0];

                Assert.assertEquals(request.getRealTimeFrom().getHour(), 6); //ZF1
                Assert.assertEquals(request.getRealTimeTo().getHour(), 8); //ZF1
                Assert.assertEquals(request.getRrnpRequestReceivedAt(), rrnpReceivedAt);

                Assert.assertEquals(request.getPortKennungTnbAuf(), portKennungTnbAuf);
                Assert.assertEquals(request.getDialNumbers().size(), 1L);
                Assert.assertEquals(request.getDialNumbers().get(0), dialNumbers.get(0));
                return response;
            }
        });

        final ElektraResponseDto elektraResponseDto = testling.portCancelledDialNumber(Portierungszeitfenster.ZF1.timeFrom(realDate),
                Portierungszeitfenster.ZF1.timeTo(realDate), dialNumbers, rrnpReceivedAt, portKennungTnbAuf);
        Assert.assertEquals(elektraResponseDto.getModifications(), response.getModifications());
        Assert.assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.OK);

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(PortCancelledDialNumberRequest.class));
    }

    private DialingNumberType createDialNumber() {
        DialingNumberType dialNumber = new DialingNumberType();
        dialNumber.setAreaDialingCode("89");
        dialNumber.setDialingNumber("12345");
        dialNumber.setCentral("0");
        dialNumber.setRangeFrom(0);
        dialNumber.setRangeTo(100);
        return dialNumber;
    }

    @Test
    public void testChangeOrderCancellationDate() throws Exception {
        final Set<Long> orderNoOrigs = new HashSet<>(Arrays.asList(1L, 2L));
        LocalDate realDate = LocalDate.now();

        final ChangeOrderCancellationDateResponse response = new ChangeOrderCancellationDateResponse();
        response.setModifications("Modified ORDER__NO 1");
        response.setStatus(ResponseStatusType.OK);

        when(elektraWebServiceTemplate.marshalSendAndReceive(any(ChangeOrderCancellationDateRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ChangeOrderCancellationDateRequest request = (ChangeOrderCancellationDateRequest) invocationOnMock.getArguments()[0];

                Assert.assertEquals(request.getBillingOrderNoOrig().size(), orderNoOrigs.size());
                Assert.assertTrue(request.getBillingOrderNoOrig().containsAll(orderNoOrigs));
                return response;
            }
        });

        final ElektraResponseDto elektraResponseDto = testling.changeOrderCancellationDate(orderNoOrigs, realDate);
        Assert.assertEquals(elektraResponseDto.getModifications(), response.getModifications());
        Assert.assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.OK);

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(ChangeOrderCancellationDateRequest.class));
    }
    
    
    @Test
    public void testCancelOrder() throws Exception {
        final long orderNoOrig = 999L;
        final LocalDate cancelDate = LocalDateTime.now().plusDays(14).toLocalDate();
        final LocalDate entryDate = LocalDateTime.now().minusDays(1).toLocalDate();
        final CarrierCode futureCarrier = CarrierCode.TELEFONICA;
        
        final CancelOrderResponse response = new CancelOrderResponse();
        response.setReclaimPositions(true);
        response.setModifications("Modified ORDER__NO 999");
        response.setStatus(ResponseStatusType.OK);

        when(elektraWebServiceTemplate.marshalSendAndReceive(any(CancelOrderRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                CancelOrderRequest request = (CancelOrderRequest) invocationOnMock.getArguments()[0];

                Assert.assertEquals(request.getBillingOrderNoOrig(), orderNoOrig);
                Assert.assertEquals(request.getFutureCarrier(), futureCarrier.getITUCarrierCode());
                Assert.assertEquals(request.getCancelDate(), cancelDate);
                Assert.assertEquals(request.getEntryDate(), entryDate);
                Assert.assertNotNull(request.getDialNumbers());
                return response;
            }
        });

        final ElektraCancelOrderResponseDto elektraResponseDto = testling.cancelOrder(
                orderNoOrig, cancelDate, entryDate, futureCarrier, Arrays.asList(createDialNumber()));
        Assert.assertEquals(elektraResponseDto.getModifications(), response.getModifications());
        Assert.assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.OK);
        Assert.assertTrue(elektraResponseDto.isReclaimPositions());

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(CancelOrderRequest.class));
    }


    @Test
    public void testUndoCancellation() throws Exception {
        final long orderNoOrig = 999L;

        final UndoPlannedOrderCancellationResponse response = new UndoPlannedOrderCancellationResponse();
        response.setReclaimPositions(true);
        response.setModifications("modified ORDER__NO 999");
        response.setStatus(ResponseStatusType.OK);

        when(elektraWebServiceTemplate.marshalSendAndReceive(any(UndoPlannedOrderCancellationRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                UndoPlannedOrderCancellationRequest request = (UndoPlannedOrderCancellationRequest) invocationOnMock.getArguments()[0];

                Assert.assertEquals(request.getBillingOrderNoOrig(), orderNoOrig);
                return response;
            }
        });

        final ElektraCancelOrderResponseDto elektraResponseDto = testling.undoCancellation(orderNoOrig);
        Assert.assertEquals(elektraResponseDto.getModifications(), response.getModifications());
        Assert.assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.OK);
        Assert.assertTrue(elektraResponseDto.isReclaimPositions());

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(UndoPlannedOrderCancellationRequest.class));
    }
    

    @Test
    public void testAddDialNumber() throws StoreException {
        final Long orderNoOrig = 9L;
        final DialingNumberType dn = createDialNumber();
        final LocalDate realDate = LocalDate.now().plusDays(1);

        final AddDialNumbersRequest request = new AddDialNumbersRequest();
        request.setBillingOrderNoOrig(orderNoOrig);
        request.setPortKennungTnbAbg("D001");
        request.setPortKennungTnbAuf("D052");
        request.setRealDate(realDate);
        request.getDialNumbers().add(dn);

        final AddDialNumbersResponse response = new AddDialNumbersResponse();
        response.setModifications("added number: 12345");
        response.setStatus(ResponseStatusType.OK);

        when(elektraWebServiceTemplate.marshalSendAndReceive(any(AddDialNumbersRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                AddDialNumbersRequest request = (AddDialNumbersRequest) invocationOnMock.getArguments()[0];

                Assert.assertEquals(request.getBillingOrderNoOrig(), request.getBillingOrderNoOrig());
                Assert.assertEquals(request.getDialNumbers().size(), 1);
                Assert.assertEquals(request.getPortKennungTnbAbg(), "D001");
                Assert.assertEquals(request.getPortKennungTnbAuf(), "D052");
                Assert.assertEquals(request.getRealDate(), realDate);
                return response;
            }
        });

        final ElektraResponseDto elektraResponseDto = testling.addDialNumber(orderNoOrig, dn, "D052", "D001", realDate);
        Assert.assertEquals(elektraResponseDto.getModifications(), response.getModifications());
        Assert.assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.OK);

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(AddDialNumbersRequest.class));
    }

    @Test
    public void testDeleteDialNumber() throws StoreException {
        final Long orderNoOrig = 9L;
        final DialingNumberType dn = createDialNumber();

        final DeleteDialNumbersRequest request = new DeleteDialNumbersRequest();
        request.setBillingOrderNoOrig(orderNoOrig);
        request.getDialNumbers().add(dn);

        final DeleteDialNumbersResponse response = new DeleteDialNumbersResponse();
        response.setModifications("deleted number: 12345");
        response.setStatus(ResponseStatusType.OK);

        when(elektraWebServiceTemplate.marshalSendAndReceive(any(DeleteDialNumbersRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                DeleteDialNumbersRequest request = (DeleteDialNumbersRequest) invocationOnMock.getArguments()[0];

                Assert.assertEquals(request.getBillingOrderNoOrig(), request.getBillingOrderNoOrig());
                Assert.assertEquals(request.getDialNumbers().size(), 1);
                return response;
            }
        });

        final ElektraResponseDto elektraResponseDto = testling.deleteDialNumber(orderNoOrig, dn);
        Assert.assertEquals(elektraResponseDto.getModifications(), response.getModifications());
        Assert.assertEquals(elektraResponseDto.getStatus(), ElektraResponseDto.ResponseStatus.OK);

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(DeleteDialNumbersRequest.class));
    }

    @Test
    public void testGenerateAndPrintReportByType() throws Exception {
        String reportType = "REPORT";
        ArgumentCaptor<GenerateAndPrintReportRequest> ac = ArgumentCaptor.forClass(GenerateAndPrintReportRequest.class);
        GenerateAndPrintReportResponse result = new GenerateAndPrintReportResponse();
        result.setResult("OK");
        when(elektraWebServiceTemplate.marshalSendAndReceive(ac.capture())).thenReturn(result);

        Assert.assertEquals(testling.generateAndPrintReportByType(300L, reportType), "OK");

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(GenerateAndPrintReportRequest.class));
        Assert.assertEquals(ac.getValue().getGenerateAndPrintReportByTypeRequest().getBillingOrderNoOrig(), 300L);
        Assert.assertEquals(ac.getValue().getGenerateAndPrintReportByTypeRequest().getDocumentName(), reportType);
        Assert.assertNull(ac.getValue().getGenerateAndPrintReportWithEvaluationRequest());
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Es ist ein nicht erwarteter Fehler.*\nTEST")
    public void testGenerateAndPrintReportByTypeException() throws Exception {
        String reportType = "REPORT";
        when(elektraWebServiceTemplate.marshalSendAndReceive(any(GenerateAndPrintReportRequest.class))).thenThrow(new RuntimeException("TEST"));
        testling.generateAndPrintReportByType(300L, reportType);

    }

    @Test
    public void testGenerateAndPrintReportWithEvaluation() throws Exception {
        AuftragDaten auftragDaten = mock(AuftragDaten.class);
        when(auftragDaten.getAuftragNoOrig()).thenReturn(100L);
        when(auftragDaten.getProdId()).thenReturn(881L);
        CBVorgang cbVorgang = mock(CBVorgang.class);
        when(cbVorgang.getReturnRealDate()).thenReturn(new Date());
        when(cbVorgang.getTyp()).thenReturn(CBVorgang.TYP_KUENDIGUNG);
        when(cbVorgang.getGfTypInternRefId()).thenReturn(111L);

        ArgumentCaptor<GenerateAndPrintReportRequest> ac = ArgumentCaptor.forClass(GenerateAndPrintReportRequest.class);
        GenerateAndPrintReportResponse result = new GenerateAndPrintReportResponse();
        result.setResult("OK");
        when(elektraWebServiceTemplate.marshalSendAndReceive(ac.capture())).thenReturn(result);

        Assert.assertEquals(testling.generateAndPrintReportWithEvaluation(auftragDaten, cbVorgang), "OK");

        verify(elektraWebServiceTemplate).marshalSendAndReceive(any(GenerateAndPrintReportRequest.class));
        Assert.assertNull(ac.getValue().getGenerateAndPrintReportByTypeRequest());
        GenerateAndPrintReportWithEvaluationType catchedEvaluationType = ac.getValue().getGenerateAndPrintReportWithEvaluationRequest();
        Assert.assertEquals(catchedEvaluationType.getBillingOrderNoOrig(), 100L);
        Assert.assertEquals(catchedEvaluationType.getOrderType(), CBVorgang.TYP_KUENDIGUNG.longValue());
        Assert.assertEquals(catchedEvaluationType.getBusinessCaseIntern().longValue(), 111L);
        Assert.assertEquals(catchedEvaluationType.getHurricanProdId().longValue(), 881L);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "The date must not be null")
    public void testGenerateAndPrintReportWithEvaluationException() throws Exception {
        testling.generateAndPrintReportWithEvaluation(new AuftragDaten(), new CBVorgang());
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Es ist ein nicht erwarteter Fehler.*\nTEST")
    public void testGenerateAndPrintReportWithEvaluationExceptionRealDateNull() throws Exception {
        when(elektraWebServiceTemplate.marshalSendAndReceive(any(GenerateAndPrintReportRequest.class))).thenThrow(new RuntimeException("TEST"));
        AuftragDaten auftragDaten = mock(AuftragDaten.class);
        CBVorgang cbVorgang = mock(CBVorgang.class);
        when(cbVorgang.getReturnRealDate()).thenReturn(new Date());
        testling.generateAndPrintReportWithEvaluation(auftragDaten, cbVorgang);
    }
}
