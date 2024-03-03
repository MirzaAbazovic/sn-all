/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2012 17:28:44
 */
package de.mnet.wita.ticketing;

import static com.google.common.collect.Lists.*;
import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.common.customer.service.CustomerService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.common.BsiDelayProtokollEintragSent;
import de.mnet.wita.message.common.BsiProtokollEintragSent;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.ticketing.converter.AbstractMwfBsiDelayProtokollConverter;
import de.mnet.wita.ticketing.converter.AbstractMwfBsiProtokollConverter;

@Test(groups = UNIT)
public class WitaBsiProtokollServiceImplTest extends BaseTest {

    @Mock
    private AbstractMwfBsiProtokollConverter<AuftragsBestaetigungsMeldung> mockConverter;
    @Mock
    private AbstractMwfBsiDelayProtokollConverter<Auftrag> mockAuftragDelayConverter;

    @Mock
    private MwfEntityDao mwfEntityDao;

    @Mock
    private CamelProxyLookupService camelProxyLookupService;

    @Mock
    private CustomerService customerService;

    @Mock
    private CCAuftragService auftragService;

    @Mock
    private BillingAuftragService bAuftragService;

    @Captor
    private ArgumentCaptor<AuftragsBestaetigungsMeldung> abmCaptor;

    @InjectMocks
    private WitaBsiProtokollServiceImpl witaBsiProtokollService;

    private Long hurricanAuftragId = 1000L;

    @BeforeMethod
    public void setupMocks() {
        witaBsiProtokollService = new WitaBsiProtokollServiceImpl();
        MockitoAnnotations.initMocks(this);
        hurricanAuftragId++;
    }

    public void dispatchShouldWork() throws Exception {
        AddCommunication protokollEintrag = new AddCommunication();
        setupDummyConverter(protokollEintrag, hurricanAuftragId);

        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();

        BAuftrag bAuftrag = setupContractData(hurricanAuftragId);
        when(camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class)).thenReturn(customerService);

        witaBsiProtokollService.init();
        witaBsiProtokollService.protokolliereNachricht(abm);

        verify(mockConverter).convert(abm);
        verify(camelProxyLookupService).lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class);
        verify(customerService).sendCustomerServiceProtocol(protokollEintrag);
        verify(mwfEntityDao).store(abmCaptor.capture());
        assertEquals(protokollEintrag.getContext().getContractId(), bAuftrag.getAuftragNoOrig().toString());
        assertEquals(protokollEintrag.getCustomerId(), bAuftrag.getKundeNo().toString());
        assertEquals(abmCaptor.getValue(), abm);
        assertEquals(abm.getSentToBsi(), BsiProtokollEintragSent.SENT_TO_BSI);
    }

    public void dispatchShouldWorkForStornierteAuftraege() throws Exception {
        Long hurricanAuftragId = 5L;
        AddCommunication protokollEintrag = new AddCommunication();
        setupDummyConverter(protokollEintrag, hurricanAuftragId);

        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();

        BAuftrag bAuftrag = setupContractData(hurricanAuftragId);
        when(bAuftragService.findAuftrag(bAuftrag.getAuftragNoOrig())).thenReturn(null);
        when(bAuftragService.findAuftragStornoByAuftragNoOrig(bAuftrag.getAuftragNoOrig())).thenReturn(bAuftrag);
        when(camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class)).thenReturn(customerService);

        witaBsiProtokollService.init();
        witaBsiProtokollService.protokolliereNachricht(abm);

        verify(mockConverter).convert(abm);
        verify(camelProxyLookupService).lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class);
        verify(customerService).sendCustomerServiceProtocol(protokollEintrag);
        verify(mwfEntityDao).store(abmCaptor.capture());
        assertEquals(protokollEintrag.getContext().getContractId(), bAuftrag.getAuftragNoOrig().toString());
        assertEquals(protokollEintrag.getCustomerId(), bAuftrag.getKundeNo().toString());
        assertEquals(abmCaptor.getValue(), abm);
        assertEquals(abm.getSentToBsi(), BsiProtokollEintragSent.SENT_TO_BSI);
    }

    public void statusShouldBeSetCorrectlyIfNoConverter() {
        witaBsiProtokollService.bsiProtokollConverters = newArrayList();
        witaBsiProtokollService.bsiDelayProtokollConverters = newArrayList();
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();

        witaBsiProtokollService.init();
        witaBsiProtokollService.protokolliereNachricht(abm);

        verify(mwfEntityDao).store(abmCaptor.capture());
        assertEquals(abmCaptor.getValue(), abm);
        assertEquals(abm.getSentToBsi(), BsiProtokollEintragSent.DONT_SEND_TO_BSI);
    }

    @DataProvider
    public Object[][] flagsToRetry() {
        return new Object[][] { { BsiProtokollEintragSent.NOT_SENT_TO_BSI, BsiProtokollEintragSent.ERROR_SEND_TO_BSI },
                { null, BsiProtokollEintragSent.ERROR_SEND_TO_BSI },
                { BsiProtokollEintragSent.ERROR_SEND_TO_BSI, BsiProtokollEintragSent.ERROR_SEND_TO_BSI } };
    }

    @Test(dataProvider = "flagsToRetry")
    public void statusShouldBeIncrementedOnWebserviceError(BsiProtokollEintragSent before,
            BsiProtokollEintragSent afterError) throws Exception {
        AddCommunication protokollEintrag = new AddCommunication();
        setupDummyConverter(protokollEintrag, hurricanAuftragId);
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();
        abm.setSentToBsi(before);

        BAuftrag bAuftrag = setupContractData(hurricanAuftragId);
        when(camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class)).thenReturn(customerService);
        doThrow(new RuntimeException()).when(customerService).sendCustomerServiceProtocol(protokollEintrag);

        witaBsiProtokollService.init();
        witaBsiProtokollService.protokolliereNachricht(abm);

        verify(mwfEntityDao).store(abmCaptor.capture());
        assertEquals(protokollEintrag.getContext().getContractId(), bAuftrag.getAuftragNoOrig().toString());
        assertEquals(protokollEintrag.getCustomerId(), bAuftrag.getKundeNo().toString());
        assertEquals(abmCaptor.getValue(), abm);
        assertEquals(abm.getSentToBsi(), afterError);
    }


    @Test
    public void protokolliereDelay() throws StoreException, FindException {
        AddCommunication protokollEintrag = new AddCommunication();
        setupDummyConverter(protokollEintrag, hurricanAuftragId);
        Auftrag witaRequest = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();

        BAuftrag bAuftrag = setupContractData(hurricanAuftragId);
        when(camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class)).thenReturn(customerService);

        witaBsiProtokollService.init();
        witaBsiProtokollService.protokolliereDelay(witaRequest);

        verify(mwfEntityDao).store(witaRequest);
        verify(camelProxyLookupService).lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class);
        verify(customerService).sendCustomerServiceProtocol(any(AddCommunication.class));
        assertEquals(protokollEintrag.getContext().getContractId(), bAuftrag.getAuftragNoOrig().toString());
        assertEquals(protokollEintrag.getCustomerId(), bAuftrag.getKundeNo().toString());
        assertEquals(witaRequest.getDelaySentToBsi(), BsiDelayProtokollEintragSent.DELAY_SENT_TO_BSI);
    }


    @Test
    public void delayForMwfEntityWithoutDelayConverter() throws StoreException, FindException {
        AddCommunication protokollEintrag = new AddCommunication();
        setupDummyConverter(protokollEintrag, hurricanAuftragId);
        TerminVerschiebung tv = new TerminVerschiebungBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();

        BAuftrag bAuftrag = setupContractData(hurricanAuftragId);
        when(camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class)).thenReturn(customerService);

        witaBsiProtokollService.init();
        witaBsiProtokollService.protokolliereDelay(tv);

        verify(mwfEntityDao).store(tv);
        verify(camelProxyLookupService, times(0)).lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class);
        verify(customerService, times(0)).sendCustomerServiceProtocol(any(AddCommunication.class));
        assertNull(protokollEintrag.getContext());
        assertNull(protokollEintrag.getCustomerId());
        assertEquals(tv.getDelaySentToBsi(), BsiDelayProtokollEintragSent.DONT_SEND_DELAY_TO_BSI);
    }


    @Test
    public void delayWithException() throws StoreException, FindException {
        AddCommunication protokollEintrag = new AddCommunication();
        setupDummyConverter(protokollEintrag, hurricanAuftragId);
        Auftrag witaRequest = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();

        BAuftrag bAuftrag = setupContractData(hurricanAuftragId);
        when(camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class)).thenReturn(customerService);
        doThrow(new RuntimeException("error calling bsi"))
                .when(customerService).sendCustomerServiceProtocol(any(AddCommunication.class));

        witaBsiProtokollService.init();
        witaBsiProtokollService.protokolliereDelay(witaRequest);

        verify(mwfEntityDao).store(witaRequest);
        assertEquals(protokollEintrag.getContext().getContractId(), bAuftrag.getAuftragNoOrig().toString());
        assertEquals(protokollEintrag.getCustomerId(), bAuftrag.getKundeNo().toString());
        assertEquals(witaRequest.getDelaySentToBsi(), BsiDelayProtokollEintragSent.ERROR_SENDING_DELAY_TO_BSI);
    }


    /**
     * @param afterError Wird nicht benutzt, nur um den gleichen DataProvider zu benutzen
     */
    @Test(dataProvider = "flagsToRetry")
    public void statusShouldBeSetOnWebserviceSuccess(BsiProtokollEintragSent before, BsiProtokollEintragSent afterError) throws FindException {
        AddCommunication protokollEintrag = new AddCommunication();
        setupDummyConverter(protokollEintrag, hurricanAuftragId);
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();
        abm.setSentToBsi(before);

        BAuftrag bAuftrag = setupContractData(hurricanAuftragId);
        when(camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE, CustomerService.class)).thenReturn(customerService);

        witaBsiProtokollService.init();
        witaBsiProtokollService.protokolliereNachricht(abm);

        verify(mwfEntityDao).store(abmCaptor.capture());
        assertEquals(abmCaptor.getValue(), abm);
        assertEquals(protokollEintrag.getContext().getContractId(), bAuftrag.getAuftragNoOrig().toString());
        assertEquals(protokollEintrag.getCustomerId(), bAuftrag.getKundeNo().toString());
        assertEquals(abm.getSentToBsi(), BsiProtokollEintragSent.SENT_TO_BSI);
    }

    private AddCommunication setupDummyConverter(AddCommunication protokollEintrag, Long hurricanAuftragId) {
        List<AbstractMwfBsiProtokollConverter<? extends MwfEntity>> converters = new ArrayList<>();
        List<AbstractMwfBsiDelayProtokollConverter<? extends MwfEntity>> delayConverters = new ArrayList<>();

        when(mockConverter.findHurricanAuftragId(any(AuftragsBestaetigungsMeldung.class))).thenReturn(hurricanAuftragId);
        when(mockConverter.getTypeToConvert()).thenReturn(AuftragsBestaetigungsMeldung.class);
        when(mockConverter.convert(any(MwfEntity.class))).thenReturn(protokollEintrag);
        converters.add(mockConverter);

        when(mockAuftragDelayConverter.findHurricanAuftragId(any(Auftrag.class))).thenReturn(hurricanAuftragId);
        when(mockAuftragDelayConverter.getTypeToConvert()).thenReturn(Auftrag.class);
        when(mockAuftragDelayConverter.convert(any(Auftrag.class))).thenReturn(protokollEintrag);
        delayConverters.add(mockAuftragDelayConverter);

        witaBsiProtokollService.bsiProtokollConverters = converters;
        witaBsiProtokollService.bsiDelayProtokollConverters = delayConverters;
        return protokollEintrag;
    }

    private BAuftrag setupContractData(Long hurricanAuftragId) throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragNoOrig(27L);
        when(auftragService.findAuftragDatenByAuftragIdTx(hurricanAuftragId)).thenReturn(auftragDaten);
        BAuftrag bAuftrag = new BAuftrag();
        bAuftrag.setAuftragNoOrig(auftragDaten.getAuftragNoOrig());
        bAuftrag.setKundeNo(2456L);
        when(bAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig())).thenReturn(bAuftrag);

        return bAuftrag;
    }
}
