/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.InvalidRequestStatusChangeException;
import de.mnet.wbci.exception.WbciRequestNotFoundException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciWitaServiceFacade;

@Test(groups = UNIT)
public class WbciRequestStatusUpdateServiceImplTest {

    @Mock
    private WbciDao wbciDaoMock;
    @Mock
    private WbciCommonService wbciCommonServiceMock;
    @Mock
    private WbciDeadlineService wbciDeadlineServiceMock;
    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallServiceMock;
    @Mock
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    @Mock
    private VorabstimmungsAnfrage vorabstimmungsAnfrageMock;
    @Mock
    private WbciGeschaeftsfall wbciGeschaeftsfall;
    @Mock
    private WbciRequest wbciRequestMock;
    @InjectMocks
    @Spy
    private WbciRequestStatusUpdateServiceImpl testling;
    private String meldungCodes = "SomeCode";
    private MeldungTyp meldungType = MeldungTyp.ABBM;
    private LocalDateTime meldungDate = LocalDateTime.now();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "legalVaStatusChanges")
    public Object[][] legalVaStatusChanges() {
        return new WbciRequestStatus[][] {
                { VA_VORGEHALTEN, VA_VERSENDET },
                { VA_VERSENDET, RUEM_VA_EMPFANGEN },
                { ABBM_TR_EMPFANGEN, AKM_TR_VERSENDET },
                { ABBM_TR_VERSENDET, AKM_TR_EMPFANGEN }
        };
    }

    @DataProvider(name = "legalTvStatusChanges")
    public Object[][] legalTvStatusChanges() {
        return new WbciRequestStatus[][] {
                { TV_VORGEHALTEN, TV_VERSENDET },
                { TV_EMPFANGEN, TV_ERLM_VERSENDET }
        };
    }

    @DataProvider(name = "legalStornoStatusChanges")
    public Object[][] legalStornoStatusChanges() {
        return new WbciRequestStatus[][] {
                { STORNO_VORGEHALTEN, STORNO_VERSENDET },
                { STORNO_VERSENDET, STORNO_ABBM_EMPFANGEN }
        };
    }

    @DataProvider(name = "illegalStatusChanges")
    public Object[][] illegalStatusChanges() {
        return new WbciRequestStatus[][] {
                { VA_VORGEHALTEN, STORNO_ERLM_VERSENDET },
                { VA_VORGEHALTEN, null },
                { TV_VORGEHALTEN, TV_ABBM_VERSENDET },
                { TV_VORGEHALTEN, null },
                { STORNO_VORGEHALTEN, STORNO_ABBM_EMPFANGEN },
                { STORNO_VORGEHALTEN, null },
                { null, VA_VORGEHALTEN },
                { null, null }
        };
    }

    @Test(dataProvider = "legalVaStatusChanges")
    public void testUpdateVaStatus(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        updateVorabstimmungsAnfrageStatus(currentStatus, newStatus);
    }

    @Test(dataProvider = "illegalStatusChanges", expectedExceptions = InvalidRequestStatusChangeException.class, expectedExceptionsMessageRegExp = "Update of WBCIRequest \\(ID='0'\\) Status failed. Cannot update from '.*' to '.*'")
    public void testUpdateVaStatusWithIllegalStatusChange(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        updateVorabstimmungsAnfrageStatus(currentStatus, newStatus);
    }

    @Test(expectedExceptions = WbciRequestNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find WbciRequest for VorabstimmungsId '123'.")
    public void testVaNotFound() {
        when(wbciDaoMock.findWbciRequestByType("123", VorabstimmungsAnfrage.class)).thenReturn(Collections.<VorabstimmungsAnfrage>emptyList());
        testling.updateVorabstimmungsAnfrageStatus("123", VA_VERSENDET, meldungCodes, meldungType, meldungDate);
    }

    private void updateVorabstimmungsAnfrageStatus(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        String vaId = "123";

        List<VorabstimmungsAnfrage> wbciRequestList = new ArrayList<>();
        wbciRequestList.add(vorabstimmungsAnfrageMock);

        when(wbciDaoMock.findWbciRequestByType(vaId, VorabstimmungsAnfrage.class)).thenReturn(wbciRequestList);
        when(vorabstimmungsAnfrageMock.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfall);
        when(wbciGeschaeftsfall.getId()).thenReturn(1L);
        when(vorabstimmungsAnfrageMock.getRequestStatus()).thenReturn(currentStatus);
        when(vorabstimmungsAnfrageMock.getUpdatedAt()).thenReturn(new Date());

        testling.updateVorabstimmungsAnfrageStatus(vaId, newStatus, meldungCodes, meldungType, meldungDate);

        verify(wbciDeadlineServiceMock).updateAnswerDeadline(vorabstimmungsAnfrageMock);
        verify(vorabstimmungsAnfrageMock).setUpdatedAt((Date) notNull());
        verify(vorabstimmungsAnfrageMock).setRequestStatus(newStatus);
        verify(vorabstimmungsAnfrageMock).setLastMeldungCodes(meldungCodes);
        verify(vorabstimmungsAnfrageMock).setLastMeldungType(meldungType);
        verify(vorabstimmungsAnfrageMock).setLastMeldungDate(Date.from(meldungDate.atZone(ZoneId.systemDefault()).toInstant()));
        verify(wbciDaoMock).store(vorabstimmungsAnfrageMock);
        verify(wbciWitaServiceFacade).updateOrCreateWitaVorabstimmungAbgebend(any(WbciRequest.class));
    }

    @Test(dataProvider = "legalTvStatusChanges")
    public void testUpdateTvStatus(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        updateTvStatus(currentStatus, newStatus);
    }

    @Test(dataProvider = "illegalStatusChanges", expectedExceptions = InvalidRequestStatusChangeException.class, expectedExceptionsMessageRegExp = "Update of WBCIRequest \\(ID='0'\\) Status failed. Cannot update from '.*' to '.*'")
    public void testUpdateTvStatusWithIllegalStatusChange(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        updateTvStatus(currentStatus, newStatus);
    }

    @Test(expectedExceptions = WbciRequestNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find WbciRequest for VorabstimmungsId '123' and Storno-, TerminverschiebungsId '987'.")
    public void testTvNotFound() {
        when(wbciDaoMock.findWbciRequestByChangeId("123", "987")).thenReturn(Collections.<WbciRequest>emptyList());
        testling.updateTerminverschiebungAnfrageStatus("123", "987", TV_VERSENDET, meldungCodes, meldungType, meldungDate);
    }

    private void updateTvStatus(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        String vaId = "123";
        String changeId = "123";

        List<WbciRequest> wbciRequestList = new ArrayList<>();
        wbciRequestList.add(wbciRequestMock);

        when(wbciDaoMock.findWbciRequestByChangeId(vaId, changeId)).thenReturn(wbciRequestList);
        when(wbciRequestMock.getRequestStatus()).thenReturn(currentStatus);
        when(wbciRequestMock.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfall);
        when(wbciRequestMock.getUpdatedAt()).thenReturn(new Date());

        testling.updateTerminverschiebungAnfrageStatus(vaId, changeId, newStatus, meldungCodes, meldungType, meldungDate);

        verify(wbciDeadlineServiceMock).updateAnswerDeadline(wbciRequestMock);
        verify(wbciRequestMock).setUpdatedAt((Date) notNull());
        verify(wbciRequestMock).setRequestStatus(newStatus);
        verify(wbciRequestMock).setLastMeldungCodes(meldungCodes);
        verify(wbciRequestMock).setLastMeldungType(meldungType);
        verify(wbciRequestMock).setLastMeldungDate(Date.from(meldungDate.atZone(ZoneId.systemDefault()).toInstant()));
        verify(wbciDaoMock).store(wbciRequestMock);
        verify(wbciWitaServiceFacade).updateOrCreateWitaVorabstimmungAbgebend(any(WbciRequest.class));
    }

    @Test(dataProvider = "legalStornoStatusChanges")
    public void testUpdateStornoStatus(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        updateStornoAnfrageStatus(currentStatus, newStatus);
    }

    @Test(dataProvider = "illegalStatusChanges", expectedExceptions = InvalidRequestStatusChangeException.class, expectedExceptionsMessageRegExp = "Update of WBCIRequest \\(ID='0'\\) Status failed. Cannot update from '.*' to '.*'")
    public void testUpdateStornoStatusWithIllegalStatusChange(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        updateStornoAnfrageStatus(currentStatus, newStatus);
    }

    @Test(expectedExceptions = WbciRequestNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find WbciRequest for VorabstimmungsId '123' and Storno-, TerminverschiebungsId '987'.")
    public void testStornoNotFound() {
        when(wbciDaoMock.findWbciRequestByChangeId("123", "987")).thenReturn(Collections.<WbciRequest>emptyList());
        testling.updateStornoAnfrageStatus("123", "987", STORNO_VERSENDET, meldungCodes, meldungType, meldungDate);
    }

    @Test(expectedExceptions = InvalidRequestStatusChangeException.class)
    public void testUpdateVorabstimmungAbgebendException() throws Exception {
        when(wbciWitaServiceFacade.updateOrCreateWitaVorabstimmungAbgebend(any(WbciRequest.class))).thenThrow(new WbciServiceException("TEST"));
        updateStornoAnfrageStatus(STORNO_VERSENDET, STORNO_ERLM_EMPFANGEN);
    }

    private void updateStornoAnfrageStatus(WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        String vaId = "123";
        String changeId = "123";

        List<WbciRequest> wbciRequestList = new ArrayList<>();
        wbciRequestList.add(wbciRequestMock);

        when(wbciDaoMock.findWbciRequestByChangeId(vaId, changeId)).thenReturn(wbciRequestList);
        when(wbciRequestMock.getRequestStatus()).thenReturn(currentStatus);
        when(wbciRequestMock.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfall);
        when(wbciRequestMock.getUpdatedAt()).thenReturn(new Date());

        testling.updateStornoAnfrageStatus(vaId, changeId, newStatus, meldungCodes, meldungType, meldungDate);

        verify(wbciDeadlineServiceMock).updateAnswerDeadline(wbciRequestMock);
        verify(wbciRequestMock).setUpdatedAt((Date) notNull());
        verify(wbciRequestMock).setRequestStatus(newStatus);
        verify(wbciRequestMock).setLastMeldungCodes(meldungCodes);
        verify(wbciRequestMock).setLastMeldungType(meldungType);
        verify(wbciRequestMock).setLastMeldungDate(Date.from(meldungDate.atZone(ZoneId.systemDefault()).toInstant()));
        verify(wbciDaoMock).store(wbciRequestMock);
        verify(wbciWitaServiceFacade).updateOrCreateWitaVorabstimmungAbgebend(any(WbciRequest.class));
    }

}
