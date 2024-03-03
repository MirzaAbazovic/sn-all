/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.13
 */
package de.mnet.wbci.route.handler;

import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.camel.Exchange;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;
import de.mnet.wbci.service.WbciMeldungService;

@Test(groups = BaseTest.UNIT)
public class AfterWbciMessageSentHandlerTest extends BaseTest {
    @Mock
    private ExchangeHelper exchangeHelperMock;
    @Mock
    private WbciMeldungService wbciMeldungServiceMock;
    @Mock
    private WbciCommonService wbciCommonServiceMock;
    @Mock
    private WbciDeadlineService wbciDeadlineServiceMock;
    @Mock
    private WbciGeschaeftsfallStatusUpdateService gfStatusUpdateServiceMock;
    @Mock
    private WbciDao wbciDaoMock;
    @Mock
    private WbciGeschaeftsfall wbciGeschaeftsfallMock;
    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallServiceMock;
    @Mock
    private WbciGeschaeftsfall linkedWbciGeschaeftsfallMock;

    @InjectMocks
    private AfterWbciMessageSentHandler testling = new AfterWbciMessageSentHandler();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "statusChanges")
    public Object[][] statusChanges() {
        return new WbciRequestStatus[][] {
                { VA_VORGEHALTEN, VA_VERSENDET },
                { TV_VORGEHALTEN, TV_VERSENDET },
                { STORNO_VORGEHALTEN, STORNO_VERSENDET }
        };
    }

    @Test(dataProvider = "statusChanges")
    public void shouldUpdateVaRequestStatusAndSetProcessedAt(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) throws Exception {
        WbciRequest wbciRequestMock = Mockito.mock(WbciRequest.class);
        Exchange exchangeMock = Mockito.mock(Exchange.class);
        WbciGeschaeftsfallStatus newGfStatus = WbciGeschaeftsfallStatus.ACTIVE;

        when(exchangeHelperMock.getOriginalMessageFromOutMessage(exchangeMock)).thenReturn(wbciRequestMock);
        when(wbciRequestMock.getRequestStatus()).thenReturn(currentStatus);
        when(wbciGeschaeftsfallMock.getId()).thenReturn(1L);
        when(gfStatusUpdateServiceMock.lookupStatusBasedOnRequestStatusChange(newStatus, newStatus, wbciRequestMock)).thenReturn(newGfStatus);
        when(wbciRequestMock.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfallMock);

        testling.handleSuccessfulWbciRequest(exchangeMock);

        verify(wbciRequestMock).setRequestStatus(newStatus);
        verify(wbciRequestMock).setProcessedAt(any(Date.class));
        verify(gfStatusUpdateServiceMock).updateGeschaeftsfallStatus(1L, newGfStatus);
        verify(wbciMeldungServiceMock, never()).updateCorrelatingRequestForMeldung(any(Meldung.class));
        verify(wbciDeadlineServiceMock).updateAnswerDeadline(wbciRequestMock);
    }

    @Test
    public void shouldCallUpdateCorrelatingRequest() throws Exception {
        Meldung meldung = Mockito.mock(Meldung.class);
        Exchange exchangeMock = Mockito.mock(Exchange.class);

        when(exchangeHelperMock.getOriginalMessageFromOutMessage(exchangeMock)).thenReturn(meldung);

        testling.handleSuccessfulWbciRequest(exchangeMock);

        verify(meldung).setProcessedAt(any(Date.class));
        verify(wbciMeldungServiceMock, times(1)).updateCorrelatingRequestForMeldung(any(Meldung.class));
        verify(wbciDeadlineServiceMock, never()).updateAnswerDeadline(any(WbciRequest.class));
    }

    @Test
    public void shouldCloseLinkedStrAenGeschaeftsfall() throws Exception {
        String linkedVaId = "DEU.MNET.12345678";

        WbciRequest wbciRequestMock = Mockito.mock(WbciRequest.class);
        Exchange exchangeMock = Mockito.mock(Exchange.class);

        when(exchangeHelperMock.getOriginalMessageFromOutMessage(exchangeMock)).thenReturn(wbciRequestMock);
        when(wbciRequestMock.getRequestStatus()).thenReturn(VA_VORGEHALTEN);
        when(gfStatusUpdateServiceMock.lookupStatusBasedOnRequestStatusChange(VA_VERSENDET, VA_VERSENDET, wbciRequestMock)).thenReturn(WbciGeschaeftsfallStatus.ACTIVE);
        when(wbciRequestMock.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfallMock);
        when(wbciGeschaeftsfallMock.getStrAenVorabstimmungsId()).thenReturn(linkedVaId);

        when(wbciDaoMock.findWbciGeschaeftsfall(linkedVaId)).thenReturn(linkedWbciGeschaeftsfallMock);

        testling.handleSuccessfulWbciRequest(exchangeMock);

        verify(wbciGeschaeftsfallServiceMock).closeLinkedStrAenGeschaeftsfall(wbciRequestMock);
    }
}
