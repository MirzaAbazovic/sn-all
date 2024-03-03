/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequestStatus;

@Test(groups = UNIT)
public class WbciGeschaeftsfallStatusUpdateServiceImplTest {
    @InjectMocks
    @Spy
    private WbciGeschaeftsfallStatusUpdateServiceImpl testling = new WbciGeschaeftsfallStatusUpdateServiceImpl();

    @Mock
    private WbciDao wbciDaoMock;

    @Mock
    private WbciGeschaeftsfall wbciGeschaeftsfallMock;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateGeschaeftsfallStatusById() throws Exception {
        Long gfId = 1L;
        when(wbciDaoMock.findById(gfId, WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfallMock);
        when(wbciGeschaeftsfallMock.getStatus()).thenReturn(WbciGeschaeftsfallStatus.ACTIVE);
        testling.updateGeschaeftsfallStatus(gfId, WbciGeschaeftsfallStatus.COMPLETE);
        verify(wbciGeschaeftsfallMock).setStatus(WbciGeschaeftsfallStatus.COMPLETE);
        verify(wbciDaoMock).store(wbciGeschaeftsfallMock);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Update of Geschaeftsfall \\(ID='1'\\) Status  failed\\. Cannot update from 'COMPLETE' to 'NEW_VA'")
    public void testUpdateGeschaeftsfallStatusByIdWithInvalidStatusChange() throws Exception {
        Long gfId = 1L;
        when(wbciDaoMock.findById(gfId, WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfallMock);
        when(wbciGeschaeftsfallMock.getStatus()).thenReturn(WbciGeschaeftsfallStatus.COMPLETE);
        when(wbciGeschaeftsfallMock.getId()).thenReturn(gfId);
        testling.updateGeschaeftsfallStatus(gfId, WbciGeschaeftsfallStatus.NEW_VA);
        verify(wbciGeschaeftsfallMock, never()).setStatus(WbciGeschaeftsfallStatus.NEW_VA);
        verify(wbciDaoMock, never()).store(wbciGeschaeftsfallMock);
    }

    @DataProvider
    public Object[][] statusLookup() {
        WbciMessage wbciMessageMock = Mockito.mock(WbciMessage.class);
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);
        when(wbciMessageMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getTyp()).thenReturn(GeschaeftsfallTyp.VA_KUE_MRN);

        ErledigtmeldungStornoAuf erledigtmeldungStornoAufMock = Mockito.mock(ErledigtmeldungStornoAuf.class);
        ErledigtmeldungStornoAen erledigtmeldungStornoAenMock = Mockito.mock(ErledigtmeldungStornoAen.class);

        RueckmeldungVorabstimmung rrnpRuemVaMock = Mockito.mock(RueckmeldungVorabstimmung.class);
        WbciGeschaeftsfall rrnpGfMock = Mockito.mock(WbciGeschaeftsfall.class);
        when(rrnpRuemVaMock.getWbciGeschaeftsfall()).thenReturn(rrnpGfMock);
        when(rrnpGfMock.getTyp()).thenReturn(GeschaeftsfallTyp.VA_RRNP);

        RueckmeldungVorabstimmung kuemrnRuemVaMock = Mockito.mock(RueckmeldungVorabstimmung.class);
        WbciGeschaeftsfall kuemrnGfMock = Mockito.mock(WbciGeschaeftsfall.class);
        when(kuemrnRuemVaMock.getWbciGeschaeftsfall()).thenReturn(kuemrnGfMock);
        when(kuemrnGfMock.getTyp()).thenReturn(GeschaeftsfallTyp.VA_KUE_MRN);

        return new Object[][] {
                // {New WbciRequestStatus, WbciMessage , Expected WbciGeschaeftsfallStatus}

                // ACTIV

                { ABBM_TR_VERSENDET, AKM_TR_EMPFANGEN, wbciMessageMock, ACTIVE },
                { AKM_TR_EMPFANGEN, RUEM_VA_VERSENDET, wbciMessageMock, ACTIVE },
                { VA_EMPFANGEN, VA_EMPFANGEN, wbciMessageMock, ACTIVE },
                { AKM_TR_VERSENDET, RUEM_VA_EMPFANGEN, wbciMessageMock, ACTIVE },
                { VA_VERSENDET, VA_VERSENDET, wbciMessageMock, ACTIVE },
                { VA_VORGEHALTEN, VA_VORGEHALTEN, wbciMessageMock, ACTIVE },
                { ABBM_TR_EMPFANGEN, AKM_TR_VERSENDET, wbciMessageMock, ACTIVE },
                { ABBM_EMPFANGEN, VA_VERSENDET, wbciMessageMock, ACTIVE },
                { RUEM_VA_EMPFANGEN, VA_VERSENDET, wbciMessageMock, ACTIVE },
                { TV_EMPFANGEN, RUEM_VA_VERSENDET, wbciMessageMock, ACTIVE },
                { TV_ERLM_EMPFANGEN, RUEM_VA_EMPFANGEN, wbciMessageMock, ACTIVE },
                { TV_ABBM_EMPFANGEN, RUEM_VA_EMPFANGEN, wbciMessageMock, ACTIVE },
                { TV_VERSENDET, RUEM_VA_EMPFANGEN, wbciMessageMock, ACTIVE },
                { TV_VORGEHALTEN, RUEM_VA_EMPFANGEN, wbciMessageMock, ACTIVE },
                { STORNO_VERSENDET, AKM_TR_VERSENDET, wbciMessageMock, ACTIVE },
                { STORNO_VORGEHALTEN, AKM_TR_VERSENDET, wbciMessageMock, ACTIVE },
                { STORNO_ABBM_EMPFANGEN, AKM_TR_VERSENDET, wbciMessageMock, ACTIVE },
                { STORNO_EMPFANGEN, RUEM_VA_EMPFANGEN, wbciMessageMock, ACTIVE },
                { STORNO_ERLM_EMPFANGEN, RUEM_VA_EMPFANGEN, erledigtmeldungStornoAufMock, ACTIVE },
                { RUEM_VA_VERSENDET, VA_EMPFANGEN, kuemrnRuemVaMock, ACTIVE },
                { TV_ERLM_VERSENDET, RUEM_VA_VERSENDET, wbciMessageMock, ACTIVE },
                { TV_ABBM_VERSENDET, RUEM_VA_VERSENDET, wbciMessageMock, ACTIVE },
                { STORNO_ABBM_VERSENDET, RUEM_VA_VERSENDET, wbciMessageMock, ACTIVE },

                // PASSIV

                { TV_ERLM_VERSENDET, RUEM_VA_VERSENDET, rrnpRuemVaMock, PASSIVE },
                { TV_ABBM_VERSENDET, RUEM_VA_VERSENDET, rrnpRuemVaMock, PASSIVE },
                { STORNO_ABBM_VERSENDET, RUEM_VA_VERSENDET, rrnpRuemVaMock, PASSIVE },
                { RUEM_VA_VERSENDET, RUEM_VA_VERSENDET, rrnpRuemVaMock, PASSIVE },

                // COMPLETE
                { STORNO_ERLM_VERSENDET, RUEM_VA_EMPFANGEN, erledigtmeldungStornoAufMock, COMPLETE },
                { ABBM_VERSENDET, VA_EMPFANGEN, wbciMessageMock, COMPLETE },

                // NEW_VA
                { STORNO_ERLM_EMPFANGEN, AKM_TR_VERSENDET, erledigtmeldungStornoAenMock, NEW_VA },
                { STORNO_ERLM_VERSENDET, AKM_TR_EMPFANGEN, erledigtmeldungStornoAenMock, NEW_VA },

        };
    }

    @Test(dataProvider = "statusLookup")
    public void testLookupStatusBasedOnRequestStatusChange(WbciRequestStatus newRequestStatus, WbciRequestStatus vaRequestStatus, WbciMessage wbciMessage, WbciGeschaeftsfallStatus expectedStatus) throws Exception {
        Assert.assertEquals(testling.lookupStatusBasedOnRequestStatusChange(newRequestStatus, vaRequestStatus, wbciMessage), expectedStatus);
    }
}
