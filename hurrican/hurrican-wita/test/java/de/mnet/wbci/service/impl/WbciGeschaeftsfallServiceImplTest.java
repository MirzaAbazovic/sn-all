/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.service.elektra.ElektraResponseDto.*;
import static de.mnet.wbci.model.AutomationTask.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.testng.Assert.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.common.base.Throwables;
import org.apache.commons.lang.StringUtils;
import org.hibernate.LockMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.augustakom.hurrican.service.elektra.builder.ElektraResponseDtoBuilder;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AutomationTaskBuilder;
import de.mnet.wbci.model.builder.AutomationTaskTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpTestBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciCustomerService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = UNIT)
public class WbciGeschaeftsfallServiceImplTest {

    @InjectMocks
    @Spy
    private WbciGeschaeftsfallServiceImpl testling = new WbciGeschaeftsfallServiceImpl();
    @Mock
    private WbciDao wbciDaoMock;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciMeldungService wbciMeldungService;
    @Mock
    private WbciGeschaeftsfallStatusUpdateService wbciGeschaeftsfallStatusUpdateServiceMock;
    @Mock
    private VorabstimmungsAnfrage vorabstimmungsAnfrageMock;
    @Mock
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    @Mock
    private WbciCustomerService wbciCustomerServiceMock;
    @Mock
    private WitaConfigService witaConfigServiceMock;

    private AKUser user;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCloseGeschaeftsfall() throws Exception {
        Long gfId = 1L;
        testling.closeGeschaeftsfall(gfId);
        verify(wbciGeschaeftsfallStatusUpdateServiceMock).updateGeschaeftsfallStatus(gfId,
                WbciGeschaeftsfallStatus.COMPLETE);
        user = new AKUserBuilder().setPersist(false).withRandomId().withName("username").build();
    }

    @Test
    public void isLinkedToStrAenGeschaeftsfall() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallStrAen = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.NEW_VA)
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);
        wbciGeschaeftsfallStrAen.setId(1L);

        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAbgebenderEKP(CarrierCode.MNET)
                .withAufnehmenderEKP(CarrierCode.DTAG)
                .buildValid(V1, VA_KUE_MRN);

        when(wbciDaoMock.findById(wbciGeschaeftsfallStrAen.getId(), WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfallStrAen);
        when(wbciCommonService.findWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfallKueMrn);

        // test positive link
        assertTrue(testling.isLinkedToStrAenGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), wbciGeschaeftsfallStrAen.getId()));

        // test straen vorabstimmung GF in invalid state
        wbciGeschaeftsfallStrAen.setStatus(WbciGeschaeftsfallStatus.ACTIVE);
        Assert.assertFalse(testling.isLinkedToStrAenGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), wbciGeschaeftsfallStrAen.getId()));

        // test straen vorabstimmung EKPauf is MNET
        wbciGeschaeftsfallStrAen.setStatus(WbciGeschaeftsfallStatus.NEW_VA);
        wbciGeschaeftsfallStrAen.setAbgebenderEKP(CarrierCode.DTAG);
        wbciGeschaeftsfallStrAen.setAufnehmenderEKP(CarrierCode.MNET);
        Assert.assertFalse(testling.isLinkedToStrAenGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), wbciGeschaeftsfallStrAen.getId()));

        // test straen vorabstimmung EKPauf does not match
        wbciGeschaeftsfallStrAen.setAbgebenderEKP(CarrierCode.MNET);
        wbciGeschaeftsfallStrAen.setAufnehmenderEKP(CarrierCode.DTAG);
        wbciGeschaeftsfallKueMrn.setAufnehmenderEKP(CarrierCode.KABEL_DEUTSCHLAND);
        Assert.assertFalse(testling.isLinkedToStrAenGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), wbciGeschaeftsfallStrAen.getId()));

        // test new vorabstimmung EKPauf does not match
        wbciGeschaeftsfallKueMrn.setAufnehmenderEKP(CarrierCode.DTAG);
        wbciGeschaeftsfallStrAen.setAufnehmenderEKP(CarrierCode.KABEL_DEUTSCHLAND);
        Assert.assertFalse(testling.isLinkedToStrAenGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), wbciGeschaeftsfallStrAen.getId()));
    }

    @Test
    public void assignTaifunOrderAndCloseStrAenGeschaeftsfall() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallStrAen = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.NEW_VA)
                .buildValid(V1, VA_KUE_MRN);
        wbciGeschaeftsfallStrAen.setId(1L);

        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final Long orderNoOrig = 99L;

        when(wbciDaoMock.findById(wbciGeschaeftsfallStrAen.getId(), WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfallStrAen);
        when(wbciCommonService.findWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfallKueMrn);

        Assert.assertNull(wbciGeschaeftsfallKueMrn.getStrAenVorabstimmungsId());
        testling.assignTaifunOrderAndCloseStrAenGeschaeftsfall(
                wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), orderNoOrig, wbciGeschaeftsfallStrAen.getId(), true);
        Assert.assertEquals(wbciGeschaeftsfallKueMrn.getStrAenVorabstimmungsId(), wbciGeschaeftsfallStrAen.getVorabstimmungsId());

        verify(wbciCommonService).assignTaifunOrder(wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), orderNoOrig, true);
        verify(wbciDaoMock).findById(wbciGeschaeftsfallStrAen.getId(), WbciGeschaeftsfall.class);
        verify(wbciCommonService).findWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId());
        verify(wbciGeschaeftsfallStatusUpdateServiceMock).updateGeschaeftsfallStatus(wbciGeschaeftsfallStrAen.getId(), WbciGeschaeftsfallStatus.COMPLETE);
        verify(wbciDaoMock).store(wbciGeschaeftsfallKueMrn);
    }

    @Test(expectedExceptions = WbciServiceException.class,
            expectedExceptionsMessageRegExp = "Geschaeftsfall is not closable after new VA, expected GF to be in status .*")
    public void assignTaifunOrderAndCloseStrAenGeschaeftsfallInvalid() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallStrAen = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .buildValid(V1, VA_KUE_MRN);
        wbciGeschaeftsfallStrAen.setId(1L);

        final Long orderNoOrig = 99L;

        when(wbciDaoMock.findById(wbciGeschaeftsfallStrAen.getId(), WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfallStrAen);

        testling.assignTaifunOrderAndCloseStrAenGeschaeftsfall("VA123", orderNoOrig, wbciGeschaeftsfallStrAen.getId(), false);
    }

    @Test(expectedExceptions = WbciServiceException.class, 
            expectedExceptionsMessageRegExp = "GF-Wechsel von RRNP zu KUE-MRN/KUE-ORN ueber STR-AEN ist nicht erlaubt!")
    public void assignTaifunOrderAndCloseStrAenGeschaeftsfallInvalidBecauseOfGfTypWechsel() throws FindException {
        WbciGeschaeftsfallRrnp wbciGeschaeftsfallStrAen = new WbciGeschaeftsfallRrnpTestBuilder()
                .withStatus(WbciGeschaeftsfallStatus.NEW_VA)
                .buildValid(V1, VA_RRNP);
        wbciGeschaeftsfallStrAen.setId(1L);

        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        when(wbciDaoMock.findById(wbciGeschaeftsfallStrAen.getId(), WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfallStrAen);
        when(wbciCommonService.findWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfallKueMrn);

        testling.assignTaifunOrderAndCloseStrAenGeschaeftsfall(
                wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), Long.valueOf(1), wbciGeschaeftsfallStrAen.getId(), false);
    }
    

    @Test
    public void assignTaifunOrderAndRejectVA() throws FindException {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValidWithoutVorabstimmungsId(V1, VA_KUE_MRN);
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> va = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn)
                .buildValid(V1, VA_KUE_MRN);

        final Long orderNoOrig = 99L;

        when(wbciDaoMock.findWbciGeschaeftsfall(va.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfallKueMrn);
        Assert.assertEquals(wbciGeschaeftsfallKueMrn.getStatus(), WbciGeschaeftsfallStatus.ACTIVE);

        testling.assignTaifunOrderAndRejectVA(va.getVorabstimmungsId(), orderNoOrig);
        verify(wbciCommonService).assignTaifunOrder(va.getVorabstimmungsId(), orderNoOrig, true);
        verify(wbciDaoMock).findWbciGeschaeftsfall(va.getVorabstimmungsId());
        verify(wbciMeldungService).createAndSendWbciMeldung(any(Abbruchmeldung.class), eq(va.getVorabstimmungsId()));
    }

    @Test
    public void isGfAssignedToTaifunOrderPositive() {
        final Long orderNoOrig = 99L;
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBillingOrderNoOrig(orderNoOrig)
                .buildValidWithoutVorabstimmungsId(V1, VA_KUE_MRN);
        String vorabstimmungsId = "DEU.MNET.VX12345678";

        when(wbciDaoMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfallKueMrn);

        final boolean gfAssignedToTaifunOrder = testling.isGfAssignedToTaifunOrder(vorabstimmungsId, orderNoOrig);
        verify(wbciDaoMock).findWbciGeschaeftsfall(vorabstimmungsId);
        assertTrue(gfAssignedToTaifunOrder);
    }

    @Test
    public void isGfAssignedToTaifunOrderNegative() {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBillingOrderNoOrig(10L)
                .buildValidWithoutVorabstimmungsId(V1, VA_KUE_MRN);
        String vorabstimmungsId = "DEU.MNET.VX12345678";

        when(wbciDaoMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfallKueMrn);

        final boolean gfAssignedToTaifunOrder = testling.isGfAssignedToTaifunOrder(vorabstimmungsId, 99L);
        verify(wbciDaoMock).findWbciGeschaeftsfall(vorabstimmungsId);
        assertFalse(gfAssignedToTaifunOrder);
    }

    @Test
    public void isGfAssignedToTaifunOrderGfNotAssigned() {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBillingOrderNoOrig(null)
                .buildValidWithoutVorabstimmungsId(V1, VA_KUE_MRN);
        String vorabstimmungsId = "DEU.MNET.VX12345678";

        when(wbciDaoMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfallKueMrn);

        final boolean gfAssignedToTaifunOrder = testling.isGfAssignedToTaifunOrder(vorabstimmungsId, 99L);
        verify(wbciDaoMock).findWbciGeschaeftsfall(vorabstimmungsId);
        assertFalse(gfAssignedToTaifunOrder);
    }

    @Test
    public void isGfAssignedToTaifunOrderGfIsNull() {
        String vorabstimmungsId = "DEU.MNET.VX12345678";
        when(wbciDaoMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(null);
        final boolean gfAssignedToTaifunOrder = testling.isGfAssignedToTaifunOrder(vorabstimmungsId, 99L);
        verify(wbciDaoMock).findWbciGeschaeftsfall(vorabstimmungsId);
        assertFalse(gfAssignedToTaifunOrder);
    }

    @Test
    public void testIssueClarifiedWithComment() throws FindException {
        prepareTestAndVerifyIssueClarified("fixed the problem");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testIssueClarifiedWithEmptyComment() throws FindException {
        prepareTestAndVerifyIssueClarified("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testIssueClarifiedWithoutComment() throws FindException {
        prepareTestAndVerifyIssueClarified(null);
    }

    private void prepareTestAndVerifyIssueClarified(String reason) {
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);
        AKUser akUserMock = Mockito.mock(AKUser.class);
        String vorabstimmungsId = "DEU.DTAG.V000000001";
        Long gfId = 1L;

        when(wbciDaoMock.findById(gfId, WbciGeschaeftsfall.class)).thenReturn(gfMock);
        when(gfMock.getVorabstimmungsId()).thenReturn(vorabstimmungsId);
        when(gfMock.getKlaerfall()).thenReturn(Boolean.TRUE);

        testling.issueClarified(gfId, akUserMock, reason);

        verify(gfMock).setKlaerfall(Boolean.FALSE);
        verify(wbciCommonService).addComment(vorabstimmungsId, reason, akUserMock);
    }

    @Test
    public void testIssueClarifiedUsingGeschaeftsfallNotMarkedForClarification() throws FindException {
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);
        AKUser akUserMock = Mockito.mock(AKUser.class);
        String vorabstimmungsId = "DEU.DTAG.V000000001";
        Long gfId = 1L;

        when(wbciDaoMock.findById(gfId, WbciGeschaeftsfall.class)).thenReturn(gfMock);
        when(gfMock.getVorabstimmungsId()).thenReturn(vorabstimmungsId);
        when(gfMock.getKlaerfall()).thenReturn(Boolean.FALSE);

        testling.issueClarified(gfId, akUserMock, "Some reason");

        verify(gfMock, never()).setKlaerfall(anyBoolean());
    }

    @Test
    public void testUpdateVorabstimmungsAnfrageWechseltermin() throws Exception {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(V1,
                VA_KUE_MRN);

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId()))
                .thenReturn(wbciGeschaeftsfall);

        testling.updateVorabstimmungsAnfrageWechseltermin(wbciGeschaeftsfall.getVorabstimmungsId(),
                DateCalculationHelper.getDateInWorkingDaysFromNow(7).toLocalDate());

        verify(wbciDaoMock).store(wbciGeschaeftsfall);
        Assert.assertNotNull(wbciGeschaeftsfall.getWechseltermin());
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testUpdateVorabstimmungsAnfrageWechselterminException() throws Exception {
        when(wbciDaoMock.findWbciGeschaeftsfall(anyString())).thenReturn(null);
        testling.updateVorabstimmungsAnfrageWechseltermin("VAID",
                DateCalculationHelper.getDateInWorkingDaysFromNow(7).toLocalDate());
        verify(wbciDaoMock, never()).store(any(WbciGeschaeftsfall.class));
    }

    @Test
    public void testUpdateInternalStatus() throws Exception {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        wbciGeschaeftsfall.setId(1L);

        when(wbciDaoMock.findById(wbciGeschaeftsfall.getId(), WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfall);

        final String internalStatus = "Das ist eine Bemerkung zu dem Status der aktuellen Vorabstimmung!";
        testling.updateInternalStatus(wbciGeschaeftsfall.getId(), internalStatus);

        verify(wbciDaoMock).findById(wbciGeschaeftsfall.getId(), WbciGeschaeftsfall.class);
        verify(wbciDaoMock).store(wbciGeschaeftsfall);
        Assert.assertEquals(wbciGeschaeftsfall.getInternalStatus(), internalStatus);
    }

    @Test
    public void testMarkGfForClarification() throws Exception {
        String vorabstimmungsId = "DEU.DTAG.V000000001";
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .buildValid(V1, VA_KUE_MRN);
        Long gfId = 1L;

        List<VorabstimmungsAnfrage> wbciRequestList = new ArrayList<>();
        wbciRequestList.add(vorabstimmungsAnfrageMock);

        when(wbciDaoMock.findById(gfId, WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfall);

        AKUser user = new AKUserBuilder().build();
        String reason = "ABBM nach RUEM-VA";
        testling.markGfForClarification(gfId, reason, user);

        verify(wbciDaoMock).findById(gfId, WbciGeschaeftsfall.class);
        verify(wbciCommonService).addComment(vorabstimmungsId, "Klärfall: " + reason, user);
        assertTrue(wbciGeschaeftsfall.getKlaerfall());
    }

    @Test
    public void testMarkGfAsAutomatable() throws Exception {
        String vorabstimmungsId = "DEU.DTAG.V000000011";
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .buildValid(V1, VA_KUE_MRN);
        Long gfId = 1L;
        VorabstimmungsAnfrage<WbciGeschaeftsfall> va = new VorabstimmungsAnfrageTestBuilder<>()
                .withRequestStatus(WbciRequestStatus.VA_EMPFANGEN)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciDaoMock.findById(gfId, WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfall);
        when(wbciDaoMock.findWbciRequestByType(vorabstimmungsId, WbciRequest.class)).thenReturn(Collections.singletonList(va));

        assertFalse(wbciGeschaeftsfall.getAutomatable());
        testling.markGfAsAutomatable(gfId, true);

        verify(wbciDaoMock).findById(gfId, WbciGeschaeftsfall.class);
        verify(wbciDaoMock).store(wbciGeschaeftsfall);
        verify(wbciDaoMock).findWbciRequestByType(vorabstimmungsId, WbciRequest.class);
        assertTrue(wbciGeschaeftsfall.getAutomatable());
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testMarkGfAsAutomatableException() throws Exception {
        String vorabstimmungsId = "DEU.DTAG.V000000011";
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .buildValid(V1, VA_KUE_MRN);
        Long gfId = 1L;
        VorabstimmungsAnfrage<WbciGeschaeftsfall> va = new VorabstimmungsAnfrageTestBuilder<>()
                .withRequestStatus(WbciRequestStatus.ABBM_VERSENDET)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciDaoMock.findById(gfId, WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfall);
        when(wbciDaoMock.findWbciRequestByType(vorabstimmungsId, WbciRequest.class)).thenReturn(Collections.singletonList(va));

        assertFalse(wbciGeschaeftsfall.getAutomatable());
        testling.markGfAsAutomatable(gfId, true);
    }

    @Test
    public void deleteGeschaeftsfallShouldSucceed() throws Exception {
        Long vaId = 1000L;
        Long tvId = 2000L;
        Long gfId = 3000L;
        String vorabstimmungsId = "DEU.MNET.V000000001";

        WbciGeschaeftsfallKueMrn geschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .buildValid(V1, VA_KUE_MRN);
        geschaeftsfall.setId(gfId);

        VorabstimmungsAnfrage vorabstimmungsAnfrage = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .withRequestStatus(WbciRequestStatus.VA_VORGEHALTEN)
                .withWbciGeschaeftsfall(geschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        vorabstimmungsAnfrage.setId(vaId);

        TerminverschiebungsAnfrage terminverschiebungsAnfrage = new TerminverschiebungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .withRequestStatus(WbciRequestStatus.TV_VORGEHALTEN)
                .withWbciGeschaeftsfall(geschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        terminverschiebungsAnfrage.setId(tvId);

        when(wbciDaoMock.byIdWithLockMode(gfId, LockMode.PESSIMISTIC_WRITE, WbciGeschaeftsfall.class)).thenReturn(geschaeftsfall);
        when(wbciDaoMock.findWbciRequestByType(vorabstimmungsId, WbciRequest.class, LockMode.PESSIMISTIC_WRITE)).thenReturn(Arrays.asList(vorabstimmungsAnfrage, terminverschiebungsAnfrage));

        testling.deleteGeschaeftsfall(gfId);

        verify(wbciDaoMock).delete(vorabstimmungsAnfrage);
        verify(wbciDaoMock).delete(terminverschiebungsAnfrage);
        verify(wbciDaoMock).delete(geschaeftsfall);
    }

    @Test(expectedExceptions = WbciServiceException.class , expectedExceptionsMessageRegExp = "Geschaeftsfall \\(.*\\) cannot be deleted.*")
    public void deleteGeschaeftsfallShouldFailSinceVaRequestAlreadySent() throws Exception {
        Long vaId = 1000L;
        Long gfId = 3000L;
        String vorabstimmungsId = "DEU.MNET.V000000001";

        WbciGeschaeftsfallKueMrn geschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .buildValid(V1, VA_KUE_MRN);
        geschaeftsfall.setId(gfId);

        VorabstimmungsAnfrage vorabstimmungsAnfrage = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .withWbciGeschaeftsfall(geschaeftsfall)
                .withRequestStatus(WbciRequestStatus.VA_VERSENDET)
                .buildValid(V1, VA_KUE_MRN);
        vorabstimmungsAnfrage.setId(vaId);

        when(wbciDaoMock.byIdWithLockMode(gfId, LockMode.PESSIMISTIC_WRITE, WbciGeschaeftsfall.class)).thenReturn(geschaeftsfall);
        when(wbciDaoMock.findWbciRequestByType(vorabstimmungsId, WbciRequest.class, LockMode.PESSIMISTIC_WRITE)).thenReturn(Arrays.<WbciRequest>asList((vorabstimmungsAnfrage)));

        testling.deleteGeschaeftsfall(gfId);
    }

    @DataProvider
    public Object[][] stornoAndTvRequests() {
        TerminverschiebungsAnfrage vorgehaltenTv = new TerminverschiebungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .withRequestStatus(WbciRequestStatus.TV_VORGEHALTEN)
                .buildValid(V1, VA_KUE_MRN);
        vorgehaltenTv.setId(1L);

        TerminverschiebungsAnfrage versendetTv = new TerminverschiebungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .withRequestStatus(WbciRequestStatus.TV_VERSENDET)
                .buildValid(V1, VA_KUE_MRN);
        versendetTv.setId(1L);

        StornoAnfrage vorgehaltenStorno = new StornoAenderungAbgAnfrageTestBuilder<>()
                .withRequestStatus(WbciRequestStatus.STORNO_VORGEHALTEN)
                .buildValid(V1, VA_KUE_MRN);
        vorgehaltenStorno.setId(1L);

        StornoAnfrage versendetStorno = new StornoAenderungAbgAnfrageTestBuilder<>()
                .withRequestStatus(WbciRequestStatus.STORNO_VERSENDET)
                .buildValid(V1, VA_KUE_MRN);
        versendetStorno.setId(1L);

        VorabstimmungsAnfrage vorgehaltenVa = new VorabstimmungsAnfrageTestBuilder<WbciGeschaeftsfallKueMrn>()
                .withRequestStatus(WbciRequestStatus.VA_VORGEHALTEN)
                .buildValid(V1, VA_KUE_MRN);
        vorgehaltenVa.setId(1L);

        // @formatter:off
        return new Object[][] {
                {vorgehaltenTv, true},
                {versendetTv, false},
                {vorgehaltenStorno, true},
                {versendetStorno, false},
                {vorgehaltenVa, false}
        };
        // @formatter:on
    }


    @Test(dataProvider = "stornoAndTvRequests")
    public void deleteStornoOrTvRequest(WbciRequest request, boolean success) throws Exception {
        when(wbciDaoMock.byIdWithLockMode(request.getId(), LockMode.PESSIMISTIC_WRITE, WbciRequest.class)).thenReturn(request);
        try {
            testling.deleteStornoOrTvRequest(request.getId());
            Assert.assertTrue(success, "was expecting WbciServiceException");
            verify(wbciDaoMock).delete(request);
        }
        catch (WbciServiceException e) {
            Assert.assertFalse(success);
        }
    }

    @Test
    public void testAssignCustomerTypeToEndCustomer() throws Exception {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        Long gfId = 1L;

        when(wbciDaoMock.findById(gfId, WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfall);
        KundenTyp kundenTyp = KundenTyp.PK;
        testling.assignCustomerTypeToEndCustomer(gfId, kundenTyp);

        Assert.assertEquals(wbciGeschaeftsfall.getEndkunde().getKundenTyp(), kundenTyp);

        verify(wbciDaoMock).findById(gfId, WbciGeschaeftsfall.class);
        verify(wbciDaoMock).store(wbciGeschaeftsfall);
    }

    @Test
    public void testCloseLinkedStrAenGeschaeftsfall() throws Exception {
        String linkedStrAenVaId = "DEU.MNET.V000000001";
        Long linkedStrAenId = 1L;

        WbciGeschaeftsfall strAenGf = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(V1, VA_KUE_MRN);
        strAenGf.setId(linkedStrAenId);

        WbciGeschaeftsfall newGf = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStrAenVorabstimmungsId(linkedStrAenVaId)
                .buildValid(V1, VA_KUE_MRN);

        when(vorabstimmungsAnfrageMock.getWbciGeschaeftsfall()).thenReturn(newGf);
        when(wbciDaoMock.findWbciGeschaeftsfall(linkedStrAenVaId)).thenReturn(strAenGf);

        testling.closeLinkedStrAenGeschaeftsfall(vorabstimmungsAnfrageMock);

        verify(wbciGeschaeftsfallStatusUpdateServiceMock).updateGeschaeftsfallStatus(strAenGf.getId(),
                WbciGeschaeftsfallStatus.COMPLETE);
    }

    @Test
    public void testCheckCbVorgangAndMarkForClarificationWithCbVorgang() {
        final long wbciGeschaeftsfallId = 1L;
        final String vorabstimmungsId = "DEU.MNET.VH12345678";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn =
                new WbciGeschaeftsfallKueMrnTestBuilder()
                        .withVorabstimmungsId(vorabstimmungsId)
                        .buildValid(V1, VA_KUE_MRN);
        wbciGeschaeftsfallKueMrn.setId(wbciGeschaeftsfallId);

        List<WitaCBVorgang> cbVorgangList = Arrays.asList(
                new WitaCBVorgangBuilder().withCarrierRefNr("0200001").withAuftragId(100L).build(),
                new WitaCBVorgangBuilder().withCarrierRefNr("0200002").withAuftragId(101L).build(),
                new WitaCBVorgangBuilder().withCarrierRefNr("0200002").withAuftragId(101L).build()
        );
        when(wbciWitaServiceFacade.findWitaCbVorgaenge(vorabstimmungsId)).thenReturn(cbVorgangList);
        when(wbciDaoMock.findById(wbciGeschaeftsfallId, WbciGeschaeftsfall.class)).thenReturn(wbciGeschaeftsfallKueMrn);

        testling.checkCbVorgangAndMarkForClarification(wbciGeschaeftsfallKueMrn);
        verify(wbciDaoMock).findById(wbciGeschaeftsfallId, WbciGeschaeftsfall.class);
        assertTrue(wbciGeschaeftsfallKueMrn.getKlaerfall());
        String reason = String.format("Klärfall: " + WbciGeschaeftsfallService.REASON_WITA_CB_VORGANG_PRESENT, vorabstimmungsId, "0200001, 0200002");
        verify(wbciCommonService).addComment(eq(vorabstimmungsId), eq(reason), (AKUser) isNull());
    }

    @Test
    public void testCheckCbVorgangAndMarkForClarificationWithoutCbVorgang() {
        final long wbciGeschaeftsfallId = 1L;
        final String vorabstimmungsId = "DEU.MNET.VH12345678";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn =
                new WbciGeschaeftsfallKueMrnTestBuilder()
                        .withVorabstimmungsId(vorabstimmungsId)
                        .buildValid(V1, VA_KUE_MRN);
        wbciGeschaeftsfallKueMrn.setId(wbciGeschaeftsfallId);

        when(wbciWitaServiceFacade.findWitaCbVorgaenge(vorabstimmungsId)).thenReturn(null);

        testling.checkCbVorgangAndMarkForClarification(wbciGeschaeftsfallKueMrn);
        assertFalse(wbciGeschaeftsfallKueMrn.getKlaerfall());
        verify(wbciDaoMock, never()).findById(wbciGeschaeftsfallId, WbciGeschaeftsfall.class);
        verify(wbciCommonService, never()).addComment(anyString(), anyString(), any(AKUser.class));
    }

    @Test
    public void shouldCompleteElapsedPreagreements() {
        final WbciGeschaeftsfall gfWithNoStornoOrTv = setupGfMock("DEU.DTAG.V000000001");
        final WbciGeschaeftsfall gfWithActiveStorno = setupGfMock("DEU.DTAG.V000000002");
        final WbciGeschaeftsfall gfWithCompletedStorno = setupGfMock("DEU.DTAG.V000000003");
        final WbciGeschaeftsfall gfWithActiveTv = setupGfMock("DEU.DTAG.V000000004");
        final WbciGeschaeftsfall gfWithCompletedTv = setupGfMock("DEU.DTAG.V000000005");
        List<WbciGeschaeftsfall> gfs = new ArrayList<>(Arrays.asList(gfWithNoStornoOrTv, gfWithActiveStorno, gfWithCompletedStorno, gfWithActiveTv, gfWithCompletedTv));

        final WbciRequest activeStorno = setupStornoRequestMock(true, gfWithActiveStorno);
        final WbciRequest completedStorno = setupStornoRequestMock(false, gfWithCompletedStorno);
        final WbciRequest activeTv = setupTvRequestMock(true, gfWithActiveTv);
        final WbciRequest completedTv = setupTvRequestMock(false, gfWithCompletedTv);

        when(wbciDaoMock.findElapsedPreagreements(0)).thenReturn(gfs);
        when(wbciDaoMock.findWbciRequestByType(eq(gfWithNoStornoOrTv.getVorabstimmungsId()), eq(WbciRequest.class))).thenReturn(Collections.<WbciRequest>emptyList());
        when(wbciDaoMock.findWbciRequestByType(eq(gfWithActiveStorno.getVorabstimmungsId()), eq(WbciRequest.class))).thenReturn(new ArrayList<>(Arrays.asList(activeStorno)));
        when(wbciDaoMock.findWbciRequestByType(eq(gfWithCompletedStorno.getVorabstimmungsId()), eq(WbciRequest.class))).thenReturn(new ArrayList<>(Arrays.asList(completedStorno)));
        when(wbciDaoMock.findWbciRequestByType(eq(gfWithActiveTv.getVorabstimmungsId()), eq(WbciRequest.class))).thenReturn(new ArrayList<>(Arrays.asList(activeTv)));
        when(wbciDaoMock.findWbciRequestByType(eq(gfWithCompletedTv.getVorabstimmungsId()), eq(WbciRequest.class))).thenReturn(new ArrayList<>(Arrays.asList(completedTv)));

        assertEquals(testling.autoCompleteEligiblePreagreements(), 3);

        verify(wbciDaoMock, times(3)).store(any(WbciEntity.class));
        verify(gfWithNoStornoOrTv).setStatus(WbciGeschaeftsfallStatus.COMPLETE);
        verify(gfWithActiveStorno, never()).setStatus(WbciGeschaeftsfallStatus.COMPLETE);
        verify(gfWithCompletedStorno).setStatus(WbciGeschaeftsfallStatus.COMPLETE);
        verify(gfWithActiveTv, never()).setStatus(WbciGeschaeftsfallStatus.COMPLETE);
        verify(gfWithCompletedTv).setStatus(WbciGeschaeftsfallStatus.COMPLETE);
    }

    @Test
    public void shouldUpdateExpiredPreagreements() {
        final String vaId1 = "DEU.DTAG.V000000001";
        final WbciGeschaeftsfall gf1 = setupGfMock(vaId1);
        final ErledigtmeldungStornoAen erlm1 = setupErledigtmeldungStornoAenMock(gf1, vaId1);

        final String vaId2 = "DEU.DTAG.V000000002";
        final WbciGeschaeftsfall gf2 = setupGfMock(vaId2);
        final ErledigtmeldungStornoAen erlm2 = setupErledigtmeldungStornoAenMock(gf2, vaId2);

        List<WbciGeschaeftsfall> gfResults = new ArrayList<>(Arrays.asList(gf1, gf2));
        LocalDate wtBefore = DateCalculationHelper.getDateInWorkingDaysFromNow(8).toLocalDate();
        when(wbciDaoMock.findPreagreementsWithStatusAndWechselTerminBefore(WbciGeschaeftsfallStatus.NEW_VA, wtBefore)).thenReturn(gfResults);
        when(wbciCommonService.findLastForVaId(vaId1, ErledigtmeldungStornoAen.class)).thenReturn(erlm1);
        when(wbciCommonService.findLastForVaId(vaId2, ErledigtmeldungStornoAen.class)).thenReturn(erlm2);

        assertEquals(testling.updateExpiredPreagreements(), 2);

        verify(gf1).setStatus(WbciGeschaeftsfallStatus.NEW_VA_EXPIRED);
        verify(gf2).setStatus(WbciGeschaeftsfallStatus.NEW_VA_EXPIRED);
        verify(wbciCustomerServiceMock).sendCustomerServiceProtocol(erlm1);
        verify(wbciCustomerServiceMock).sendCustomerServiceProtocol(erlm2);
    }
    
    @DataProvider(name = "isGeschaeftsfallWechselRrnpToMrnOrnDataProvider")
    public Object[][] isGeschaeftsfallWechselRrnpToMrnOrnDataProvider() {
        WbciGeschaeftsfallRrnp rrnp = new WbciGeschaeftsfallRrnpTestBuilder().buildValid(V1, VA_RRNP);
        WbciGeschaeftsfallKueMrn kuemrn = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(V1, VA_KUE_MRN);
        WbciGeschaeftsfallKueOrn kueorn = new WbciGeschaeftsfallKueOrnTestBuilder().buildValid(V1, VA_KUE_ORN);
        
        return new Object[][]{
                { rrnp, kuemrn, true },
                { rrnp, kueorn, true },
                { rrnp, rrnp, false },
                { kuemrn, rrnp, false },
                { kueorn, rrnp, false },
                { kuemrn, kuemrn, false },
                { kueorn, kueorn, false },
        };
    }
    
    @Test(dataProvider = "isGeschaeftsfallWechselRrnpToMrnOrnDataProvider")
    public void isGeschaeftsfallWechselRrnpToMrnOrn(WbciGeschaeftsfall originalGf, WbciGeschaeftsfall actualGf, boolean expected) {
         assertEquals(testling.isGeschaeftsfallWechselRrnpToMrnOrn(originalGf, actualGf), expected);
    }
    

    private WbciGeschaeftsfall setupGfMock(String vaId) {
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);
        when(gfMock.getVorabstimmungsId()).thenReturn(vaId);
        when(gfMock.getId()).thenReturn(1L);
        return gfMock;
    }

    private ErledigtmeldungStornoAen setupErledigtmeldungStornoAenMock(WbciGeschaeftsfall gf, String vaId) {
        ErledigtmeldungStornoAen erlmMock = Mockito.mock(ErledigtmeldungStornoAen.class);
        when(erlmMock.getWbciGeschaeftsfall()).thenReturn(gf);
        when(erlmMock.getVorabstimmungsId()).thenReturn(vaId);
        return erlmMock;
    }

    private WbciRequest setupStornoRequestMock(boolean isActiveStorno, WbciGeschaeftsfall gf) {
        if (isActiveStorno) {
            return setupStornoOrTvRequestMock(RequestTyp.STR_AEN_ABG, WbciRequestStatus.STORNO_VERSENDET, gf);
        }
        else {
            return setupStornoOrTvRequestMock(RequestTyp.STR_AEN_ABG, WbciRequestStatus.STORNO_ERLM_EMPFANGEN, gf);
        }
    }

    private WbciRequest setupTvRequestMock(boolean isActiveTv, WbciGeschaeftsfall gf) {
        if (isActiveTv) {
            return setupStornoOrTvRequestMock(RequestTyp.TV, WbciRequestStatus.TV_VERSENDET, gf);
        }
        else {
            return setupStornoOrTvRequestMock(RequestTyp.TV, WbciRequestStatus.TV_ERLM_EMPFANGEN, gf);
        }
    }

    private WbciRequest setupStornoOrTvRequestMock(RequestTyp requestTyp, WbciRequestStatus requestStatus, WbciGeschaeftsfall gf) {
        WbciRequest reqMock = Mockito.mock(WbciRequest.class);
        when(reqMock.getTyp()).thenReturn(requestTyp);
        when(reqMock.getRequestStatus()).thenReturn(requestStatus);
        when(reqMock.getWbciGeschaeftsfall()).thenReturn(gf);
        return reqMock;
    }

    @Test
    public void createOrUpdateAutomationTaskNewTx() {
        final String vorabstimmungsId = "DEU.MNET.0000001";
        final WbciGeschaeftsfallKueMrn geschaeftsfall = new WbciGeschaeftsfallKueMrnBuilder()
                .withVorabstimmungsId(vorabstimmungsId)
                .build();
        when(wbciDaoMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(geschaeftsfall);
        final TaskName taskName = TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN;
        final AutomationStatus automationStatus = AutomationStatus.COMPLETED;
        final String modifications = "some message";
        testling.createOrUpdateAutomationTaskNewTx(vorabstimmungsId, null, taskName, automationStatus, modifications, user);
        verify(wbciDaoMock).findWbciGeschaeftsfall(vorabstimmungsId);
        verify(testling).createOrUpdateAutomationTask(geschaeftsfall, null, taskName, automationStatus, modifications, user);
    }

    @Test
    public void createOrUpdateAutomationTaskWithElektraResponse() {
        final ElektraResponseDto responseDto = new ElektraResponseDtoBuilder()
                .withModifications("modifications")
                .withStatus(ResponseStatus.OK)
                .build();
        final WbciGeschaeftsfallKueMrn gf = new WbciGeschaeftsfallKueMrn();
        final TaskName taskName = TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN;
        doNothing().when(testling).createOrUpdateAutomationTask(gf, taskName, AutomationStatus.COMPLETED, responseDto.getModifications(), user);
        testling.createOrUpdateAutomationTask(gf, taskName, responseDto, user);
        verify(testling).createOrUpdateAutomationTask(gf, null, taskName, AutomationStatus.COMPLETED, responseDto.getModifications(), user);
    }


    @DataProvider
    public Object[][] createOrUpdateAutomationTaskDataProvider() {
        final AutomationTask automationTask = new AutomationTaskBuilder()
                .withName(TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN)
                .withStatus(AutomationStatus.ERROR)
                .withCreatedAt(LocalDateTime.now())
                .withExecutionLog("some error message")
                .build();
        return new Object[][]{
                { automationTask,   TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN, AutomationStatus.COMPLETED, user },
                { null,             TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN, AutomationStatus.COMPLETED, user },
                { automationTask,   TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN, AutomationStatus.COMPLETED, null },
                { automationTask,   TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN, AutomationStatus.ERROR, user },
        };
    }

    @Test(dataProvider = "createOrUpdateAutomationTaskDataProvider")
    public void createOrUpdateAutomationTask(AutomationTask existingAutomationTask, TaskName taskName, AutomationStatus status, AKUser user) {
        final WbciGeschaeftsfallKueMrnBuilder gfBuilder = new WbciGeschaeftsfallKueMrnBuilder();
        if (existingAutomationTask != null) {
            gfBuilder.addAutomationTask(existingAutomationTask);
            taskName = existingAutomationTask.getName();
        }
        final WbciGeschaeftsfallKueMrn gf = gfBuilder.build();
        final String msg = "modifications";
        testling.createOrUpdateAutomationTask(gf, taskName, status, msg, user);
        assertEquals(gf.getAutomationTasks().size(), 1);
        final AutomationTask automationTask = gf.getAutomationTasks().get(0);
        assertEquals(automationTask.getName(), taskName);
        assertEquals(automationTask.getStatus(), status);
        assertEquals(automationTask.isDone(), automationTask.getStatus() == AutomationStatus.COMPLETED);
        assertTrue(automationTask.getExecutionLog().contains(msg));
        if (user == null) {
            assertNull(automationTask.getUserId());
            assertEquals(automationTask.getUserName(), HurricanConstants.SYSTEM_USER);
        }
        else {
            assertEquals(automationTask.getUserId(), user.getId());
            assertEquals(automationTask.getUserName(), user.getName());
        }
        if (status.equals(AutomationStatus.ERROR)) {
            verify(testling).activateWbciGeschaeftsfallDuringAutomationError(gf, automationTask);
        }
        verify(wbciDaoMock).store(gf);
    }


    @Test
    public void testActivateWbciGeschaeftsfall() throws Exception {
        Long id = 123L;
        WbciGeschaeftsfallKueMrn gf = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(V1, VA_KUE_MRN);
        gf.setId(id);
        AutomationTask automationTask = new AutomationTaskTestBuilder().withStatus(AutomationStatus.ERROR).buildValid(V1, VA_KUE_MRN);

        testling.activateWbciGeschaeftsfallDuringAutomationError(gf, automationTask);
        verify(wbciGeschaeftsfallStatusUpdateServiceMock).updateGeschaeftsfallStatusWithoutCheckAndWithoutStore(gf, WbciGeschaeftsfallStatus.ACTIVE);
        verify(testling, never()).addExecutionLog(any(AutomationTask.class), anyString(), anyString());
    }

    @Test
    public void testActivateWbciGeschaeftsfallException() throws Exception {
        Long id = 123L;
        WbciGeschaeftsfallKueMrn gf = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(V1, VA_KUE_MRN);
        gf.setId(id);
        AutomationTask automationTask = new AutomationTaskTestBuilder().withStatus(AutomationStatus.ERROR).buildValid(V1, VA_KUE_MRN);
        Exception ex = new WbciServiceException("TEST");
        doThrow(ex).when(wbciGeschaeftsfallStatusUpdateServiceMock).updateGeschaeftsfallStatusWithoutCheckAndWithoutStore(gf, WbciGeschaeftsfallStatus.ACTIVE);

        testling.activateWbciGeschaeftsfallDuringAutomationError(gf, automationTask);
        verify(wbciGeschaeftsfallStatusUpdateServiceMock).updateGeschaeftsfallStatusWithoutCheckAndWithoutStore(gf, WbciGeschaeftsfallStatus.ACTIVE);
        verify(testling).addExecutionLog(automationTask, Throwables.getStackTraceAsString(ex), automationTask.getUserName());
    }

    @DataProvider
    public Object[][] automationStatusDataProvider() {
        return new Object[][]{
                { ResponseStatus.OK, AutomationStatus.COMPLETED },
                { ResponseStatus.ERROR, AutomationStatus.ERROR },
        };
    }

    @Test(dataProvider = "automationStatusDataProvider")
    public void getAutomationStatus(ResponseStatus responseStatusType, AutomationStatus expectedAutomationStatus) {
        assertEquals(testling.getAutomationStatus(responseStatusType), expectedAutomationStatus);
    }

    @DataProvider
    public Object[][] addExecutionLogDataProvider() {
        return new Object[][]{
                { null, "bla blub", HurricanConstants.SYSTEM_USER },
                { "", "bla blub", HurricanConstants.SYSTEM_USER },
                { "old execution log", "bla blub", HurricanConstants.SYSTEM_USER },
        };
    }

    @Test(dataProvider = "addExecutionLogDataProvider")
    public void addExecutionLog(String currentExecutionLog, String newExecutionLog, String username) {
        final AutomationTask automationTask =
                new AutomationTaskTestBuilder()
                        .withExecutionLog(currentExecutionLog)
                        .buildValid(V1, VA_KUE_MRN);
        testling.addExecutionLog(automationTask, newExecutionLog, username);
        assertTrue(automationTask.getExecutionLog().contains(username));
        assertTrue(automationTask.getExecutionLog().contains(newExecutionLog));
        String startOfExecutionLog = StringUtils.isEmpty(currentExecutionLog) ? "---------" : currentExecutionLog;
        assertTrue(automationTask.getExecutionLog().startsWith(startOfExecutionLog));
    }

    @DataProvider
    public Object[][] isWechselTerminOffsetInRange() {

        LocalDateTime BASE = LocalDateTime.of(LocalDate.parse("2014-04-04", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN);           // FRI
        LocalDateTime BASE_PLUS_1 = LocalDateTime.of(LocalDate.parse("2014-04-07", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN);    // +MON
        LocalDateTime BASE_PLUS_2 = LocalDateTime.of(LocalDate.parse("2014-04-08", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN);    // +TUE
        LocalDateTime BASE_PLUS_3 = LocalDateTime.of(LocalDate.parse("2014-04-09", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN);    // +WED
        LocalDateTime BASE_MINUS_1 = LocalDateTime.of(LocalDate.parse("2014-04-03", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN);   // -THU

        return new Object[][] {
                { BASE, BASE, true},
                { BASE, BASE_PLUS_1, true},
                { BASE, BASE_PLUS_2, true},
                { BASE, BASE_PLUS_3, false},
                { BASE, BASE_MINUS_1, false},
        };
    }

    @Test(dataProvider = "isWechselTerminOffsetInRange")
    public void testIsWechselTerminOffsetInRange(LocalDateTime kundenWunschtermin, LocalDateTime wechselTermin, boolean inRange) {
        final String vaId = "1";
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);
        when(gfMock.getKundenwunschtermin()).thenReturn(kundenWunschtermin.toLocalDate());
        when(gfMock.getWechseltermin()).thenReturn(wechselTermin.toLocalDate());

        when(wbciDaoMock.findPreagreementsWithAutomatableRuemVa()).thenReturn(Arrays.asList(vaId));
        when(wbciDaoMock.findWbciGeschaeftsfall(vaId)).thenReturn(gfMock);
        when(witaConfigServiceMock.getWbciRequestedAndConfirmedDateOffset()).thenReturn(2);
        Collection<String> vaIds = testling.findPreagreementsWithAutomatableRuemVa();
        if(inRange) {
            Assert.assertEquals(vaIds.size(), 1);
            Assert.assertEquals(vaIds.iterator().next(), vaId);
        }
        else  {
            Assert.assertEquals(vaIds.size(), 0);
        }
    }

}
