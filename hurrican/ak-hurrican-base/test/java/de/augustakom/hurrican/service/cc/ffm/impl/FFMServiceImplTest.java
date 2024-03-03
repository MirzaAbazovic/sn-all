/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.dao.cc.ffm.FfmDAO;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.billing.KundeBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1.MaterialTestBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.ContactPersonTestBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DescriptionTestBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.WorkforceOrderBuilder;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlassBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.ffm.FfmFeedbackMaterial;
import de.augustakom.hurrican.model.cc.ffm.FfmFeedbackMaterialBuilder;
import de.augustakom.hurrican.model.cc.ffm.FfmOrderState;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMappingBuilder;
import de.augustakom.hurrican.model.cc.ffm.FfmQualification;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationBuilder;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationMappingBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.MailService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderContactsCommand;
import de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderDescription4InterneArbeitCommand;
import de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderIdsCommand;
import de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderLocation4InterneArbeitCommand;
import de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderLocationCommand;
import de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderTimeslotCommand;
import de.augustakom.hurrican.tools.jaxb.XmlSchemaDateTimeBinder;
import de.mnet.common.service.locator.ServiceLocator;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyOrderFeedback;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyUpdateOrder;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.TimeSlot;
import de.mnet.esb.cdm.resource.workforceservice.v1.ContactPerson;
import de.mnet.esb.cdm.resource.workforceservice.v1.CreateOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.DeleteOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.UpdateOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceService;

@Test(groups = BaseTest.UNIT)
public class FFMServiceImplTest extends BaseTest {

    @Mock
    private BAService baService;
    @Mock
    private BAConfigService baConfigService;
    @Mock
    private ReferenceService referenceService;
    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private CCLeistungsService ccLeistungsService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private HVTService hvtService;
    @Mock
    private WorkforceService workforceService;
    @Mock
    private FfmDAO ffmDAO;
    @Mock
    private ServiceLocator serviceLocator;
    @Mock
    private MailService mailService;
    @Mock
    private RegistryService registryService;
    @Mock
    private OEService oeService;
    @Mock
    private KundenService kundenService;

    @InjectMocks
    @Spy
    private FFMServiceImpl testling;

    @BeforeMethod
    public void setUp() {
        testling = new FFMServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateAndSendOrder() throws FindException, ServiceCommandException, StoreException {
        Long auftragId = 1L;
        Long baVerlAnlassId = 2L;
        Verlauf bauauftrag = new Verlauf();
        bauauftrag.setId(99L);
        bauauftrag.setAuftragId(auftragId);
        bauauftrag.setAnlass(baVerlAnlassId);

        when(serviceLocator.getCmdBean(AggregateFfmHeaderIdsCommand.class)).thenReturn(new AggregateFfmHeaderIdsCommand());
        when(serviceLocator.getCmdBean(AggregateFfmHeaderContactsCommand.class)).thenReturn(new AggregateFfmHeaderContactsCommand());
        when(serviceLocator.getCmdBean(AggregateFfmHeaderLocationCommand.class)).thenReturn(new AggregateFfmHeaderLocationCommand());
        when(serviceLocator.getCmdBean(AggregateFfmHeaderTimeslotCommand.class)).thenReturn(new AggregateFfmHeaderTimeslotCommand());
        when(serviceLocator.getCmdBean(AggregateFfmHeaderDescription4InterneArbeitCommand.class)).thenReturn(new AggregateFfmHeaderDescription4InterneArbeitCommand());
        when(serviceLocator.getCmdBean(AggregateFfmHeaderLocation4InterneArbeitCommand.class)).thenReturn(new AggregateFfmHeaderLocation4InterneArbeitCommand());

        when(ccLeistungsService.findTechLeistungen4Verlauf(bauauftrag.getId(), true)).thenReturn(Collections.emptyList());

        doReturn(new HVTStandortBuilder().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT).build()).when(testling).findHvtStandort(auftragId);
        doReturn(new AuftragDatenBuilder().withProdId(Produkt.PROD_ID_SDSL_10000).build()).when(testling).findAuftragDaten(auftragId);
        doReturn(new FfmProductMappingBuilder().build()).when(testling).findFfmProductMapping(auftragId, baVerlAnlassId, HVTStandort.HVT_STANDORT_TYP_HVT, Produkt.PROD_ID_SDSL_10000);
        doNothing().when(testling).executeAggregatorChain(any(AKServiceCommandChain.class));

        testling.createAndSendOrder(bauauftrag);

        verify(testling).findFfmProductMapping(auftragId, baVerlAnlassId, HVTStandort.HVT_STANDORT_TYP_HVT, Produkt.PROD_ID_SDSL_10000);
        verify(workforceService).createOrder(any(CreateOrder.class));
        verify(ccLeistungsService).findTechLeistungen4Verlauf(bauauftrag.getId(), true);
        verify(baService, never()).saveVerlauf(bauauftrag);
    }

    @DataProvider
    public Object[][] updateNotifyOrderDataProvider() {
        // @formatter:off
       return new Object[][] {
                { FfmOrderState.DONE    , true  , false , false , null, "sas-123", null },
                { FfmOrderState.DONE    , true  , false , false , null, null     , null },
                { FfmOrderState.TNFE    , true  , false , true  , 1L,   "sas-123", null },
                { FfmOrderState.CUST    , true  , false , true  , 2L,   "sas-123", null },
                { FfmOrderState.ON_SITE , false , true  , false , null, "sas-123", null },
                { FfmOrderState.SYNC    , false , false  , false , null, "sas-123", VerlaufStatus.STATUS_IM_UMLAUF },
        };
        // @formatter:on
    }


    @Test(dataProvider = "updateNotifyOrderDataProvider")
    public void testNotifyUpdateOrder(FfmOrderState state, boolean finishBaCalled, boolean saveBaCalled,
            Boolean notPossible, Long notPossibleReasonRefId, String resourceId, Long verlaufStatus) throws FindException, StoreException {
        String workforceOrderId = UUID.randomUUID().toString();

        NotifyUpdateOrder notifyUpdateOrder = new NotifyUpdateOrder();
        notifyUpdateOrder.setOrderId(workforceOrderId);
        NotifyUpdateOrder.StateInfo stateInfo = new NotifyUpdateOrder.StateInfo();
        stateInfo.setState(state.name());
        notifyUpdateOrder.setStateInfo(stateInfo);
        notifyUpdateOrder.setModified(LocalDateTime.now().minusMinutes(10));
        NotifyUpdateOrder.Resource resource = new NotifyUpdateOrder.Resource();
        resource.setId(resourceId);
        notifyUpdateOrder.setResource(resource);

        String str = "2015-06-08 10:59:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime realEndTime = LocalDateTime.parse(str, formatter);
        TimeSlot actual = new TimeSlot();
        actual.setEndTime(realEndTime);
        actual.setStartTime(LocalDateTime.now());
        notifyUpdateOrder.setActual(actual);

        Verlauf bauauftrag = new Verlauf();
        bauauftrag.setVerlaufStatusId(VerlaufStatus.BEI_DISPO);
        bauauftrag.setId(1000L);
        bauauftrag.setNotPossible(false);

        VerlaufAbteilung baFFM = new VerlaufAbteilung();
        baFFM.setAbteilungId(Abteilung.FFM);
        baFFM.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
        baFFM.setNotPossible(false);

        VerlaufAbteilung baFieldService = new VerlaufAbteilung();
        baFieldService.setAbteilungId(Abteilung.FIELD_SERVICE);
        baFieldService.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
        baFieldService.setNotPossible(false);
        List<VerlaufAbteilung> vasFieldService = new ArrayList<>();
        vasFieldService.add(baFieldService);

        Reference notPossibleRef = (notPossible) ? new Reference() : null;
        if (notPossible) {
            notPossibleRef.setId(notPossibleReasonRefId);
        }

        when(baService.findVerlaufByWorkforceOrder(workforceOrderId)).thenReturn(bauauftrag);
        when(baService.findVerlaufAbteilung(bauauftrag.getId(), Abteilung.FFM)).thenReturn(baFFM);
        when(baService.findVerlaufAbteilungen(bauauftrag.getId(), new Long[] { Abteilung.FIELD_SERVICE })).thenReturn(vasFieldService);
        when(referenceService.findReference(anyLong())).thenReturn(notPossibleRef);

        testling.notifyUpdateOrder(notifyUpdateOrder, 1L);

        String expectedBearbeiter = resourceId != null ? resourceId : HurricanConstants.UNKNOWN;
        if (finishBaCalled) {
            verify(baService).findVerlaufAbteilungen(bauauftrag.getId(), new Long[] { Abteilung.FIELD_SERVICE });

            verify(baService).finishVerlauf4Abteilung(
                    eq(baFFM),
                    eq(expectedBearbeiter),
                    isNull(String.class),
                    eq(Date.from(notifyUpdateOrder.getActual().getEndTime().atZone(ZoneId.systemDefault()).toInstant())),
                    anyLong(),
                    isNull(Long.class),
                    eq(notPossible),
                    eq(notPossibleReasonRefId));

            verify(baService).finishVerlauf4Abteilung(
                    eq(baFieldService),
                    eq(expectedBearbeiter),
                    isNull(String.class),
                    eq(Date.from(notifyUpdateOrder.getActual().getEndTime().atZone(ZoneId.systemDefault()).toInstant())),
                    anyLong(),
                    isNull(Long.class),
                    eq(notPossible),
                    eq(notPossibleReasonRefId));
        }

        if (saveBaCalled) {
            assertEquals(baFFM.getVerlaufStatusId(), VerlaufStatus.STATUS_IN_BEARBEITUNG);
            assertEquals(baFFM.getBearbeiter(), expectedBearbeiter);
            verify(baService, times(2)).saveVerlaufAbteilung(baFFM);
        }

        if (verlaufStatus != null && verlaufStatus.equals(VerlaufStatus.STATUS_IM_UMLAUF)
                && state == FfmOrderState.SYNC) {
            assertEquals(baFFM.getBearbeiter(), expectedBearbeiter);
            verify(baService).saveVerlaufAbteilung(baFFM);
        }

        verify(baService, never()).saveVerlauf(bauauftrag);
    }

    @Test
    public void testNotifyUpdateOrderIgnore() throws FindException, StoreException {
        String workfordeOrderId = UUID.randomUUID().toString();

        NotifyUpdateOrder notifyUpdateOrder = new NotifyUpdateOrder();
        notifyUpdateOrder.setOrderId(workfordeOrderId);
        NotifyUpdateOrder.StateInfo stateInfo = new NotifyUpdateOrder.StateInfo();
        stateInfo.setState("OTHER");
        notifyUpdateOrder.setStateInfo(stateInfo);

        testling.notifyUpdateOrder(notifyUpdateOrder, 1L);

        verify(baService, never()).saveVerlauf(any(Verlauf.class));
        verify(baService, never()).saveVerlaufAbteilung(any(VerlaufAbteilung.class));
    }

    @Test
    public void testNotifyUpdateOrderVerlaufNotFound() throws FindException, StoreException {
        String workfordeOrderId = UUID.randomUUID().toString();

        NotifyUpdateOrder notifyUpdateOrder = new NotifyUpdateOrder();
        notifyUpdateOrder.setOrderId(workfordeOrderId);
        NotifyUpdateOrder.StateInfo stateInfo = new NotifyUpdateOrder.StateInfo();
        stateInfo.setState(FfmOrderState.DONE.name());
        notifyUpdateOrder.setStateInfo(stateInfo);

        when(baService.findVerlaufByWorkforceOrder(workfordeOrderId)).thenReturn(null);

        testling.notifyUpdateOrder(notifyUpdateOrder, 1L);

        verify(baService, never()).finishVerlauf4Abteilung(
                any(VerlaufAbteilung.class), anyString(), anyString(), any(Date.class), anyLong(), anyLong(), anyBoolean(), anyLong());
        verify(baService, never()).saveVerlauf(any(Verlauf.class));
        verify(baService, never()).saveVerlaufAbteilung(any(VerlaufAbteilung.class));
    }

    @Test
    public void testNotifyUpdateOrderRealEndTime() throws StoreException, FindException {
        String workforceOrderId = UUID.randomUUID().toString();

        Verlauf bauauftrag = new Verlauf();
        bauauftrag.setVerlaufStatusId(VerlaufStatus.BEI_DISPO);
        bauauftrag.setId(1000L);
        bauauftrag.setNotPossible(false);

        VerlaufAbteilung baFFM = new VerlaufAbteilung();
        baFFM.setAbteilungId(Abteilung.FFM);
        baFFM.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
        baFFM.setNotPossible(false);

        NotifyUpdateOrder notifyUpdateOrder = new NotifyUpdateOrder();
        notifyUpdateOrder.setOrderId(workforceOrderId);
        NotifyUpdateOrder.StateInfo stateInfo = new NotifyUpdateOrder.StateInfo();
        stateInfo.setState(FfmOrderState.DONE.name());
        notifyUpdateOrder.setStateInfo(stateInfo);

        String str = "2015-06-08 10:59:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime realEndTime = LocalDateTime.parse(str, formatter);
        TimeSlot actual = new TimeSlot();
        notifyUpdateOrder.setActual(actual);
        notifyUpdateOrder.getActual().setEndTime(realEndTime);

        when(baService.findVerlaufByWorkforceOrder(workforceOrderId)).thenReturn(bauauftrag);
        when(baService.findVerlaufAbteilung(bauauftrag.getId(), Abteilung.FFM)).thenReturn(baFFM);

        testling.notifyUpdateOrder(notifyUpdateOrder, 1L);

        Date realEndTimeAsDate = Date.from(notifyUpdateOrder.getActual().getEndTime().atZone(ZoneId.systemDefault()).toInstant());
        verify(baService).finishVerlauf4Abteilung(
                any(VerlaufAbteilung.class),
                anyString(),
                anyString(),
                eq(realEndTimeAsDate),
                anyLong(), anyLong(),
                anyBoolean(),
                anyLong());

    }

    @Test(expectedExceptions = FFMServiceException.class)
    public void testNotifyUpdateOrderInternalError() throws FindException, StoreException {
        String workfordeOrderId = UUID.randomUUID().toString();

        NotifyUpdateOrder notifyUpdateOrder = new NotifyUpdateOrder();
        notifyUpdateOrder.setOrderId(workfordeOrderId);
        NotifyUpdateOrder.StateInfo stateInfo = new NotifyUpdateOrder.StateInfo();
        stateInfo.setState(FfmOrderState.DONE.name());
        notifyUpdateOrder.setStateInfo(stateInfo);

        Verlauf bauauftrag = new Verlauf();
        bauauftrag.setVerlaufStatusId(VerlaufStatus.BEI_DISPO);
        bauauftrag.setId(1000L);
        bauauftrag.setNotPossible(false);

        when(baService.findVerlaufByWorkforceOrder(workfordeOrderId)).thenReturn(bauauftrag);
        when(baService.findVerlaufAbteilungen(bauauftrag.getId())).thenThrow(new FindException());

        testling.notifyUpdateOrder(notifyUpdateOrder, 1L);

        verify(baService, never()).finishVerlauf4Abteilung(
                any(VerlaufAbteilung.class), anyString(), anyString(), any(Date.class), anyLong(), anyLong(), anyBoolean(), anyLong());
        verify(baService, never()).saveVerlauf(any(Verlauf.class));
        verify(baService, never()).saveVerlaufAbteilung(any(VerlaufAbteilung.class));
    }

    @Test
    public void testGetNotPossibleReasonRef() throws FindException {
        Reference otherReason = new Reference();
        otherReason.setId(1L);
        Reference customerReason = new Reference();
        customerReason.setId(2L);
        Reference technicalReason = new Reference();
        technicalReason.setId(3L);

        when(referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_OTHER)).thenReturn(otherReason);
        when(referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_CUST)).thenReturn(customerReason);
        when(referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_TNFE)).thenReturn(technicalReason);

        assertEquals(testling.getNotPossibleReasonRef(FfmOrderState.ON_SITE), otherReason.getId());
        assertEquals(testling.getNotPossibleReasonRef(FfmOrderState.CUST), customerReason.getId());
        assertEquals(testling.getNotPossibleReasonRef(FfmOrderState.TNFE), technicalReason.getId());
        assertEquals(testling.getNotPossibleReasonRef(FfmOrderState.DONE), null);

        verify(referenceService, times(3)).findReference(anyLong());
    }

    @Test
    public void testGetNotPossibleReasonRefNotFound() throws FindException {
        Reference otherReason = new Reference();
        otherReason.setId(1L);

        when(referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_OTHER)).thenReturn(otherReason);
        when(referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_CUST)).thenThrow(new FindException());

        assertEquals(testling.getNotPossibleReasonRef(FfmOrderState.CUST), otherReason.getId());

        verify(referenceService, times(2)).findReference(anyLong());
    }

    @Test
    public void testGetNotPossibleReasonRefDefaultNotFound() throws FindException {
        Reference otherReason = new Reference();
        otherReason.setId(1L);

        when(referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_OTHER)).thenThrow(new FindException());
        when(referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_CUST)).thenThrow(new FindException());

        assertNull(testling.getNotPossibleReasonRef(FfmOrderState.CUST));

        verify(referenceService, times(2)).findReference(anyLong());
    }


    @Test(expectedExceptions = FFMServiceException.class,
            expectedExceptionsMessageRegExp = "Der Bauauftrags-Anlass mit der ID null ist nicht fuer eine Uebergabe an FFM konfiguriert.")
    public void testFindFfmProductMappingBaAnlassNotConfigured() throws FindException {
        when(baConfigService.findBAVerlaufAnlass(anyLong())).thenReturn(
                new BAVerlaufAnlassBuilder().withFfmTyp(null).setPersist(false).build());
        testling.findFfmProductMapping(null, null, null, null);
    }


    @Test
    public void testFindFfmProductMapping() throws FindException {
        HVTStandortBuilder hvtBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT).setPersist(false);

        when(baConfigService.findBAVerlaufAnlass(anyLong())).thenReturn(
                new BAVerlaufAnlassBuilder().setPersist(false).build());
        when(ccAuftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(
                new AuftragDatenBuilder().withProdId(9999L).setPersist(false).build());
        when(endstellenService.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(
                new EndstelleBuilder().withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                        .withHvtStandortBuilder(hvtBuilder).setPersist(false).build());
        when(hvtService.findHVTStandort(anyLong())).thenReturn(hvtBuilder.build());

        when(ffmDAO.queryByExample(any(FfmProductMapping.class), eq(FfmProductMapping.class))).thenReturn(
                Collections.singletonList(new FfmProductMappingBuilder().setPersist(false).build()));

        assertNotNull(testling.findFfmProductMapping(null, null, null, null));
        verify(ffmDAO).queryByExample(any(FfmProductMapping.class), eq(FfmProductMapping.class));
        verify(ffmDAO, never()).findAll(eq(FfmProductMapping.class));
        verify(testling, never()).filterBestMatch(anyListOf(FfmProductMapping.class), any(FfmProductMapping.class), anyBoolean());
    }

    @Test
    public void testFindFfmProductMappingBestMatch() throws FindException {
        HVTStandortBuilder hvtBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT).setPersist(false);

        when(baConfigService.findBAVerlaufAnlass(anyLong())).thenReturn(
                new BAVerlaufAnlassBuilder().setPersist(false).build());
        when(ccAuftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(
                new AuftragDatenBuilder().withProdId(9999L).setPersist(false).build());
        when(endstellenService.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(
                new EndstelleBuilder().withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                        .withHvtStandortBuilder(hvtBuilder).setPersist(false).build());
        when(hvtService.findHVTStandort(anyLong())).thenReturn(hvtBuilder.build());

        when(ffmDAO.queryByExample(any(FfmProductMapping.class), eq(FfmProductMapping.class))).thenReturn(Collections.emptyList());
        when(ffmDAO.findAll(eq(FfmProductMapping.class))).thenReturn(
                Collections.singletonList(new FfmProductMappingBuilder().setPersist(false).build()));
        doReturn(new FfmProductMappingBuilder().setPersist(false).build())
                .when(testling).filterBestMatch(anyListOf(FfmProductMapping.class), any(FfmProductMapping.class), anyBoolean());

        assertNotNull(testling.findFfmProductMapping(null, null, null, null));
        verify(ffmDAO, times(2)).queryByExample(any(FfmProductMapping.class), eq(FfmProductMapping.class));
        verify(testling).filterBestMatch(anyListOf(FfmProductMapping.class), any(FfmProductMapping.class), anyBoolean());
    }

    @Test(expectedExceptions = FFMServiceException.class)
    public void testFindFfmProductMappingNoResult() throws FindException {
        when(ffmDAO.queryByExample(any(FfmProductMapping.class), eq(FfmProductMapping.class))).thenReturn(null);

        testling.findFfmProductMapping(null, null, null, null);
    }

    @Test
    public void textFeedbackWithEmptyTextInfo() throws StoreException, FindException {
        testling.textFeedback("HUR_123", " ", LocalDateTime.now());
        verify(baService, never()).findVerlaufByWorkforceOrder(anyString());
        verify(baService, never()).saveVerlaufAbteilung(any(VerlaufAbteilung.class));
    }

    @Test
    public void textFeedbackNoBaFoundForWorkforceOrderId() throws FindException, StoreException {
        when(baService.findVerlaufByWorkforceOrder(anyString())).thenReturn(null);

        testling.textFeedback("HUR_123", "info", LocalDateTime.now());

        verify(baService).findVerlaufByWorkforceOrder(anyString());
        verify(baService, never()).saveVerlaufAbteilung(any(VerlaufAbteilung.class));
    }

    @Test
    public void textFeedback() throws FindException, StoreException {
        Verlauf bauauftrag = new VerlaufBuilder().setPersist(false).build();
        VerlaufAbteilung vaFfm = new VerlaufAbteilungBuilder()
                .withBemerkung("existing bemerkung").setPersist(false).build();

        when(baService.findVerlaufByWorkforceOrder(anyString())).thenReturn(bauauftrag);
        when(baService.findVerlaufAbteilung(anyLong(), eq(Abteilung.FFM))).thenReturn(vaFfm);

        LocalDateTime now = LocalDateTime.now();
        testling.textFeedback("HUR_123", "info", now);

        assertTrue(vaFfm.getBemerkung().startsWith("existing bemerkung"));
        assertTrue(vaFfm.getBemerkung().endsWith(
                "(" + now.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DATE_TIME_LONG)) + "): info"));

        verify(baService).findVerlaufByWorkforceOrder(anyString());
        verify(baService).saveVerlaufAbteilung(any(VerlaufAbteilung.class));
    }

    @Test
    public void textFeedbackWithNullAsVaBemerkung() throws FindException, StoreException {
        Verlauf bauauftrag = new VerlaufBuilder().setPersist(false).build();
        VerlaufAbteilung vaFfm = new VerlaufAbteilungBuilder()
                .withBemerkung(null).setPersist(false).build();

        when(baService.findVerlaufByWorkforceOrder(anyString())).thenReturn(bauauftrag);
        when(baService.findVerlaufAbteilung(anyLong(), eq(Abteilung.FFM))).thenReturn(vaFfm);

        LocalDateTime now = LocalDateTime.now();
        testling.textFeedback("HUR_123", "info", now);

        assertEquals(vaFfm.getBemerkung(), "(" + now.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DATE_TIME_LONG)) + "): info");

        verify(baService).findVerlaufByWorkforceOrder(anyString());
        verify(baService).saveVerlaufAbteilung(any(VerlaufAbteilung.class));
    }

    @Test
    public void textFeedbackLocalDateTime() throws FindException, StoreException {
        Verlauf bauauftrag = new VerlaufBuilder().setPersist(false).build();
        VerlaufAbteilung vaFfm = new VerlaufAbteilungBuilder()
                .withBemerkung(null).setPersist(false).build();

        when(baService.findVerlaufByWorkforceOrder(anyString())).thenReturn(bauauftrag);
        when(baService.findVerlaufAbteilung(anyLong(), eq(Abteilung.FFM))).thenReturn(vaFfm);

        final LocalDateTime dateTime = XmlSchemaDateTimeBinder.parseDateTime("2015-04-22T08:01:39.633Z");
        testling.textFeedback("HUR_123", "info", dateTime);

        assertEquals(vaFfm.getBemerkung(), "(22.04.2015 10:01:39): info");
    }

    @Test
    public void testMaterialFeedbackNoBaFoundForWorkforceOrderId() throws FindException {
        when(baService.findVerlaufByWorkforceOrder(anyString())).thenReturn(null);

        testling.materialFeedback("HUR_123", new NotifyOrderFeedback.Material());

        verify(baService).findVerlaufByWorkforceOrder(anyString());
        verify(ffmDAO, never()).store(any(FfmFeedbackMaterial.class));
    }

    @Test
    public void testMaterialFeedback() throws FindException {
        when(baService.findVerlaufByWorkforceOrder(anyString())).thenReturn(new Verlauf());

        NotifyOrderFeedback.Material material = new MaterialTestBuilder().build();
        testling.materialFeedback("HUR_123", material);

        verify(baService).findVerlaufByWorkforceOrder(anyString());
        verify(ffmDAO).store(any(FfmFeedbackMaterial.class));
    }

    @Test
    public void testMarkFfmFeedbackMaterialAsProcessed() {
        FfmFeedbackMaterial material = new FfmFeedbackMaterialBuilder().setPersist(false).build();
        testling.markFfmFeedbackMaterialAsProcessed(material);

        assertTrue(material.getProcessed());
        verify(ffmDAO).store(material);
    }


    @Test
    public void testCreateMailsForFfmFeedbacks() throws FindException {
        FfmFeedbackMaterial material1 = new FfmFeedbackMaterialBuilder().withWorkforceOrderId("HUR-1").setPersist(false).build();
        FfmFeedbackMaterial material2 = new FfmFeedbackMaterialBuilder().withWorkforceOrderId("HUR-1").setPersist(false).build();
        FfmFeedbackMaterial material3 = new FfmFeedbackMaterialBuilder().withWorkforceOrderId("HUR-2").setPersist(false).build();

        Verlauf verlauf1 = new VerlaufBuilder().setPersist(false).build();
        Verlauf verlauf2 = new VerlaufBuilder().setPersist(false).build();

        when(ffmDAO.queryByExample(any(FfmFeedbackMaterial.class), eq(FfmFeedbackMaterial.class)))
                .thenReturn(Arrays.asList(material1, material2, material3));
        when(baService.findVerlaufByWorkforceOrder("HUR-1")).thenReturn(verlauf1);
        when(baService.findVerlaufByWorkforceOrder("HUR-2")).thenReturn(verlauf2);
        when(ccAuftragService.findAuftragById(anyLong())).thenReturn(new AuftragBuilder().setPersist(false).build());
        when(kundenService.findKunde(anyLong())).thenReturn(new KundeBuilder().setPersist(false).build());
        when(oeService.findProduktName4Auftrag(anyLong())).thenReturn("TAI-Produkt");

        when(ccAuftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(
                new AuftragDatenBuilder().setPersist(false).build());

        testling.createMailsForFfmFeedbacks();

        verify(testling).createMailForFfmFeedbacks(eq(verlauf1), eq(Arrays.asList(material1, material2)));
        verify(testling).createMailForFfmFeedbacks(eq(verlauf2), eq(Collections.singletonList(material3)));

        verify(mailService, times(2)).sendMailFromHurricanServer(any(Mail.class));

        verify(ffmDAO).store(material1);
        verify(ffmDAO).store(material2);
        verify(ffmDAO).store(material3);
    }


    @Test
    public void testDeleteOrderForBaWithoutWorkforceOrderId() throws StoreException {
        testling.deleteOrder(new VerlaufBuilder().setPersist(false).build());

        verify(baService, never()).saveVerlauf(any(Verlauf.class));
        verify(workforceService, never()).deleteOrder(any(DeleteOrder.class));
    }

    @Test
    public void testDeleteOrder() throws StoreException {
        Verlauf bauauftrag = new VerlaufBuilder().withWorkforceOrderId("HUR-123").setPersist(false).build();
        testling.deleteOrder(bauauftrag);

        assertNull(bauauftrag.getWorkforceOrderId());

        verify(baService).saveVerlauf(bauauftrag);
        verify(workforceService).deleteOrder(any(DeleteOrder.class));
    }


    @DataProvider(name = "filterBestMatchDP")
    public Object[][] filterBestMatchDP() {
        FfmProductMapping ffmPmHvt = createMapping(null, 11000L);

        FfmProductMapping ffmPmHvtProd1 = createMapping(1L, 11000L);
        FfmProductMapping ffmPmHvtProd2 = createMapping(2L, 11000L);

        FfmProductMapping ffmPmFttc = createMapping(null, 11013L);

        FfmProductMapping ffmPmProdOnly = createMapping(110L, null);

        FfmProductMapping exampleFfmPmHvtProdNotConfigured = createMapping(3L, 11000L);

        List<FfmProductMapping> mappings = Arrays.asList(ffmPmHvt, ffmPmFttc, ffmPmHvtProd1, ffmPmHvtProd2, ffmPmProdOnly);

        return new Object[][] {
                { mappings, ffmPmHvtProd1, ffmPmHvtProd1 },
                { mappings, exampleFfmPmHvtProdNotConfigured, ffmPmHvt },
                { mappings, ffmPmHvtProd1, ffmPmHvtProd1 },
                { mappings, ffmPmProdOnly, ffmPmProdOnly },
        };
    }

    @Test(dataProvider = "filterBestMatchDP")
    public void testFilterBestMatch(List<FfmProductMapping> mappings, FfmProductMapping example, FfmProductMapping expected) {
        FfmProductMapping result = testling.filterBestMatch(mappings, example, false);
        assertEquals(result, expected);
    }

    private FfmProductMapping createMapping(Long produktId, Long standortTypRefId) {
        return new FfmProductMappingBuilder()
                .withProduktId(produktId)
                .withStandortTypRefId(standortTypRefId)
                .setPersist(false)
                .build();
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = ".*zwei identische Konfigurationen gefunden.*")
    public void testFilterBestMatchWithDuplicateConfig() {
        FfmProductMapping config1 = new FfmProductMappingBuilder().withStandortTypRefId(11000L).setPersist(false).build();
        FfmProductMapping config2 = new FfmProductMappingBuilder().withStandortTypRefId(11000L).setPersist(false).build();

        testling.filterBestMatch(Arrays.asList(config1, config2), config1, false);
    }


    @DataProvider(name = "hasActiveFfmRecordDP")
    public Object[][] hasActiveFfmRecordDP() {
        return new Object[][] {
                { new VerlaufAbteilungBuilder().withVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG).build(), true },
                { new VerlaufAbteilungBuilder().withVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF).build(), true },
                { new VerlaufAbteilungBuilder().withVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT).build(), false },
                { new VerlaufAbteilungBuilder().withVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT_CPS).build(), false },
                { new VerlaufAbteilungBuilder().withVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT_SYSTEM).build(), false },
        };
    }


    @Test(dataProvider = "hasActiveFfmRecordDP")
    public void hasActiveFfmRecord(VerlaufAbteilung ffm, boolean expected) throws FindException {
        when(baService.findVerlaufAbteilung(anyLong(), eq(Abteilung.FFM))).thenReturn(ffm);
        assertEquals(testling.hasActiveFfmRecord(new VerlaufBuilder()
                        .withAkt(Boolean.TRUE)
                        .withWorkforceOrderId("1")
                        .build(),
                true), expected);
    }

    @Test
    public void hasActiveFfmRecordBaWithoutWorkforceIdFalse() {
        assertFalse(testling.hasActiveFfmRecord(new VerlaufBuilder()
                        .withAkt(Boolean.TRUE)
                        .withWorkforceOrderId(null)
                        .build(),
                true));
    }

    @Test
    public void hasActiveFfmRecordBaWithoutWorkforceIdTrue() throws FindException {
        when(baService.findVerlaufAbteilung(anyLong(), eq(Abteilung.FFM)))
                .thenReturn(new VerlaufAbteilungBuilder()
                        .withVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG)
                        .build());

        assertTrue(testling.hasActiveFfmRecord(new VerlaufBuilder()
                        .withAkt(Boolean.TRUE)
                        .withWorkforceOrderId(null)
                        .build(),
                false));
    }

    @Test
    public void hasActiveFfmRecordBaNotActive() {
        assertFalse(testling.hasActiveFfmRecord(new VerlaufBuilder()
                        .withAkt(Boolean.FALSE)
                        .withWorkforceOrderId("1")
                        .build(),
                true));
    }

    @Test
    public void updateAndSendOrder() throws ServiceCommandException, FindException {
        doReturn(true).when(testling).hasActiveFfmRecord(any(Verlauf.class), anyBoolean());
        WorkforceOrder.Description description = new DescriptionTestBuilder().build();
        ContactPerson contactPerson = new ContactPersonTestBuilder().build();
        doReturn(new WorkforceOrderBuilder()
                .withId("created.id")
                .withDescription(description)
                .addContactPerson(contactPerson)
                .build()).when(testling).createWorkforceOrder4Bauauftrag(any(Verlauf.class));

        Verlauf verlauf = new VerlaufBuilder().withWorkforceOrderId("workforce.id").build();
        testling.updateAndSendOrder(verlauf);

        ArgumentCaptor<UpdateOrder> argumentCaptor = ArgumentCaptor.forClass(UpdateOrder.class);
        verify(workforceService).updateOrder(argumentCaptor.capture());

        UpdateOrder updateOrder = argumentCaptor.getValue();
        assertEquals(updateOrder.getId(), verlauf.getWorkforceOrderId());
        assertEquals(updateOrder.getDescription().getDetails(), description.getDetails());
        assertEquals(updateOrder.getDescription().getTechParams(), description.getTechParams());
        assertEquals(updateOrder.getContactPerson().iterator().next(), contactPerson);
    }

    @Test
    public void testGetFfmQualificationsForTechLeistungen() {
        List<TechLeistung> techLeistungen = new ArrayList<>();

        techLeistungen.add(new TechLeistungBuilder().withId(1L).build());
        techLeistungen.add(new TechLeistungBuilder().withId(2L).build());
        techLeistungen.add(new TechLeistungBuilder().withId(3L).build());
        techLeistungen.add(new TechLeistungBuilder().withId(4L).build());

        when(ffmDAO.findQualificationsByLeistung(1L)).thenReturn(Collections.singletonList(new FfmQualificationMappingBuilder()
                .withFfmQualification(new FfmQualificationBuilder().build()).build()));
        when(ffmDAO.findQualificationsByLeistung(2L)).thenReturn(Collections.emptyList());
        when(ffmDAO.findQualificationsByLeistung(3L)).thenReturn(Collections.singletonList(new FfmQualificationMappingBuilder()
                .withFfmQualification(new FfmQualificationBuilder().build()).build()));
        when(ffmDAO.findQualificationsByLeistung(4L)).thenReturn(Collections.singletonList(new FfmQualificationMappingBuilder()
                .withFfmQualification(new FfmQualificationBuilder().build()).build()));

        List<FfmQualification> qualifications = testling.getFfmQualifications(techLeistungen);
        Assert.assertEquals(qualifications.size(), 3L);
    }

    @Test
    public void testGetFfmQualificationsForProductMappingWithoutVpn() throws FindException {
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withProdId(Produkt.PROD_ID_SDSL_10000)
                .build();
        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .withVPNBuilder(null)
                .build();

        when(ccAuftragService.findAuftragTechnikByAuftragId(anyLong())).thenReturn(auftragTechnik);
        when(ffmDAO.findQualificationsByProduct(auftragDaten.getProdId())).thenReturn(Collections.singletonList(new FfmQualificationMappingBuilder()
                .withFfmQualification(new FfmQualificationBuilder().build()).build()));

        List<FfmQualification> qualifications = testling.getFfmQualifications(auftragDaten);
        Assert.assertEquals(qualifications.size(), 1L);

        verify(ffmDAO, never()).findQualifications4Vpn();
        verify(ffmDAO).findQualificationsByProduct(anyLong());
    }

    @Test
    public void testGetFfmQualificationsForProductMappingWithVpn() throws FindException {
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withProdId(Produkt.PROD_ID_SDSL_10000)
                .build();
        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .withVPNBuilder(new VPNBuilder())
                .build();

        FfmQualification vpnQualification = new FfmQualificationBuilder().setPersist(false).build();
        FfmQualification otherQualification = new FfmQualificationBuilder().setPersist(false).build();

        when(ccAuftragService.findAuftragTechnikByAuftragId(anyLong())).thenReturn(auftragTechnik);
        when(ffmDAO.findQualificationsByProduct(auftragDaten.getProdId())).thenReturn(
                Arrays.asList(
                        new FfmQualificationMappingBuilder().withFfmQualification(vpnQualification).build(),
                        new FfmQualificationMappingBuilder().withFfmQualification(otherQualification).build()));
        when(ffmDAO.findQualifications4Vpn()).thenReturn(Collections.singletonList(new FfmQualificationMappingBuilder()
                .withFfmQualification(vpnQualification).build()));

        List<FfmQualification> qualifications = testling.getFfmQualifications(auftragDaten);
        Assert.assertEquals(qualifications.size(), 2L);
        Assert.assertTrue(qualifications.contains(vpnQualification));
        Assert.assertTrue(qualifications.contains(otherQualification));

        verify(ffmDAO).findQualifications4Vpn();
        verify(ffmDAO).findQualificationsByProduct(anyLong());
    }

    @Test
    public void testGetFfmQualificationsForHVTStandort() {
        HVTStandort hvtStandort = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
                .build();

        when(ffmDAO.findQualificationsByStandortRef(hvtStandort.getStandortTypRefId())).thenReturn(Collections.singletonList(new FfmQualificationMappingBuilder()
                .withFfmQualification(new FfmQualificationBuilder().build()).build()));

        List<FfmQualification> qualifications = testling.getFfmQualifications(hvtStandort);
        Assert.assertEquals(qualifications.size(), 1L);
    }

    @DataProvider(name = "updateBearbeiterDP")
    public Object[][] updateBearbeiterDP() {
        String bearbeiter = "musterma";
        NotifyUpdateOrder empty = new NotifyUpdateOrder();
        NotifyUpdateOrder bearbeiterMissing = new NotifyUpdateOrder();
        NotifyUpdateOrder.Resource emptyResource = new NotifyUpdateOrder.Resource();
        bearbeiterMissing.setResource(emptyResource);
        NotifyUpdateOrder bearbeiterPresent = new NotifyUpdateOrder();
        NotifyUpdateOrder.Resource bearbeiterResource = new NotifyUpdateOrder.Resource();
        bearbeiterResource.setId(bearbeiter);
        bearbeiterPresent.setResource(bearbeiterResource);
        VerlaufAbteilung verlaufOffen = new VerlaufAbteilung();
        VerlaufAbteilung verlaufWithBearbeiter = new VerlaufAbteilung();
        verlaufWithBearbeiter.setBearbeiter(bearbeiter);
        VerlaufAbteilung verlaufErledigt = new VerlaufAbteilung();
        verlaufErledigt.setDatumErledigt(new Date());

        return new Object[][] {
                { null, null, false, null },
                { empty, null, false, null },
                { null, verlaufErledigt, false, null },
                { empty, verlaufErledigt, false, null },
                { bearbeiterMissing, verlaufErledigt, false, null },
                { bearbeiterPresent, verlaufErledigt, false, null },
                { bearbeiterPresent, verlaufOffen, true, bearbeiter },
                { bearbeiterMissing, verlaufWithBearbeiter, true, null },
        };
    }

    @Test(dataProvider = "updateBearbeiterDP")
    public void testUpdateBearbeiter(NotifyUpdateOrder in, VerlaufAbteilung verlaufAbteilungFfm,
            boolean expectedBearbeiterSet, String expectedBearbeiter) throws StoreException {
        testling.updateBearbeiter(in, verlaufAbteilungFfm);
        if (expectedBearbeiterSet) {
            verify(baService).saveVerlaufAbteilung(verlaufAbteilungFfm);
            assertEquals(expectedBearbeiter, verlaufAbteilungFfm.getBearbeiter());
        }
        else {
            verify(baService, never()).saveVerlaufAbteilung(verlaufAbteilungFfm);
        }
    }

}
