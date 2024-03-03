/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 13:07:16
 */
package de.mnet.wita.service.impl;

import static com.google.common.collect.Lists.*;
import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.model.AkmPvUserTask.AkmPvStatus.*;
import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
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
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Feature.FeatureName;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.aggregator.AnsprechpartnerAmAggregator;
import de.mnet.wita.aggregator.MontageleistungAggregator;
import de.mnet.wita.aggregator.StandortKollokationAggregator;
import de.mnet.wita.bpm.AbgebendPvWorkflowService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.bpm.converter.MwfCbVorgangConverterService;
import de.mnet.wita.dao.WitaCBVorgangDao;
import de.mnet.wita.exceptions.AuftragNotFoundException;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.builder.common.portierung.RufnummernPortierungBuilder;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.attribute.AufnehmenderProviderBuilder;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.AkmPvUserTaskBuilder;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.TamUserTaskBuilder;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.UserTask2AuftragDaten;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.model.validators.RufnummerPortierungCheck;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.TalAenderungstypService;
import de.mnet.wita.service.TalAnbieterwechseltypService;
import de.mnet.wita.service.WitaCheckConditionService;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = UNIT)
public class WitaTalOrderServiceImplTest extends BaseTest {

    private static long NEXT_ID = 0;

    @Mock
    AKUserService userService;
    @Mock
    private
    CarrierService carrierService;
    @Mock
    private
    CBVorgangDAO cbVorgangDAO;
    @Mock
    private
    WitaCBVorgangDao witaCbVorgangDao;
    @Mock
    private
    EndstellenService endstellenService;
    @Mock
    HVTService hvtService;
    @Mock
    private
    CarrierElTALService carrierElTALService;
    @Mock
    private
    TalOrderWorkflowService workflowService;
    @Mock
    private
    CommonWorkflowService commonWorkflowService;
    @Mock
    private
    TalAenderungstypService talAenderungstypService;
    @Mock
    private
    TalAnbieterwechseltypService talAnbieterwechseltypService;
    @Mock
    private
    CCAuftragService auftragService;
    @Mock
    AbgebendPvWorkflowService abgebendPvWorkflowService;
    @Mock
    private
    WitaUsertaskService witaUsertaskService;
    @Mock
    private
    MwfEntityService mwfEntityService;
    @Mock
    private
    DateTimeCalculationService dateTimeCalculationService;
    @Mock
    AnsprechpartnerAmAggregator ansprechpartnerAmAggregator;
    @Mock
    RuntimeService runtimeService;
    @Mock
    private
    PhysikService physikService;
    @Mock
    private
    MontageleistungAggregator montageleistungAggregator;
    @Mock
    private
    StandortKollokationAggregator standortKollokationAggregator;
    @Mock
    private
    WitaCheckConditionService witaCheckConditionService;
    @Mock
    private
    MwfCbVorgangConverterService mwfCbVorgangConverterService;
    @Mock
    private
    RufnummerService rufnummerService;
    @Mock
    private
    RufnummerPortierungService rufnummerPortierungService;
    @Mock
    private
    FeatureService featureService;

    @Captor
    private
    ArgumentCaptor<WitaCBVorgang> witaVorgangCaptor;

    private WitaCBVorgang witaCbVorgang;

    @InjectMocks
    @Spy
    private final WitaTalOrderServiceImpl cut = new WitaTalOrderServiceImpl();

    @BeforeMethod
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        witaCbVorgang = new WitaCBVorgangBuilder().withAuftragId(123L).withId(1234567890L).build();
        when(carrierElTALService.findCBVorgang(witaCbVorgang.getId())).thenReturn(witaCbVorgang);
    }

    @DataProvider
    public Object[][] cbVorgangAndGeschaeftsfallTyp() {
        return new Object[][] { { CBVorgang.TYP_NEU, BEREITSTELLUNG }, { CBVorgang.TYP_KUENDIGUNG, KUENDIGUNG_KUNDE },
                { CBVorgang.TYP_ANBIETERWECHSEL, PROVIDERWECHSEL }, { CBVorgang.TYP_PORTWECHSEL, LEISTUNGS_AENDERUNG } };
    }

    @Test(dataProvider = "cbVorgangAndGeschaeftsfallTyp")
    public void testDetermineGeschaeftsfallTyp(Long cbTyp, GeschaeftsfallTyp expectedGeschaeftsfall)
            throws FindException {
        Carrierbestellung cb = new CarrierbestellungBuilder().withRandomId().setPersist(false).build();

        when(carrierService.findCB(cb.getId())).thenReturn(cb);
        when(talAenderungstypService.determineGeschaeftsfall(cb, witaCbVorgang)).thenReturn(LEISTUNGS_AENDERUNG);
        when(talAnbieterwechseltypService.determineGeschaeftsfall(cb, witaCbVorgang)).thenReturn(PROVIDERWECHSEL);

        GeschaeftsfallTyp determinedGeschaeftsfallTyp = cut
                .determineGeschaeftsfallTyp(cbTyp, cb.getId(), witaCbVorgang);
        if (CBVorgang.TYP_ANBIETERWECHSEL.equals(cbTyp)) {
            verify(talAnbieterwechseltypService).determineGeschaeftsfall(cb, witaCbVorgang);
        }
        if (CBVorgang.TYP_PORTWECHSEL.equals(cbTyp)) {
            verify(talAenderungstypService).determineGeschaeftsfall(cb, witaCbVorgang);
        }

        assertEquals(determinedGeschaeftsfallTyp, expectedGeschaeftsfall);
    }

    @Test
    public void testCreateCBVorgang() throws Exception {
        Carrierbestellung cb1 = createCarrierbestellung();
        Carrierbestellung cb2 = createCarrierbestellung();
        createEndstelle(cb1, 1L);
        createEndstelle(cb2, 2L);

        CbVorgangData cbvData = new CbVorgangData().addAuftragIds(ImmutableList.of(1L, 2L)).withCbId(cb1.getId())
                .withCarrierId(Carrier.ID_DTAG).withVorgabe(new Date()).withCbVorgangTyp(CBVorgang.TYP_NEU)
                .withUser(createAkUser());

        doReturn(new CarrierKennung()).when(cut).findCarrierKennung(any(Long.class), any(String.class));
        doReturn("123").when(cut).getVbz(any(String.class), any(CBVorgang.class));
        Endstelle endstelle = new Endstelle();
        endstelle.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);
        when(endstellenService.findEndstellen4Carrierbestellung(any(Carrierbestellung.class))).thenReturn(
                ImmutableList.of(endstelle));
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(1L);
        when(auftragService.findAuftragDatenByEndstelleTx(any(Long.class))).thenReturn(auftragDaten);

        List<CBVorgang> result = cut.createCBVorgang(cbvData);

        // Speichert vor dem Starten der Workflows
        verify(carrierElTALService, times(2)).saveCBVorgang(witaVorgangCaptor.capture());
        verify(workflowService, times(2)).newProcessInstance(witaVorgangCaptor.capture());

        List<WitaCBVorgang> vorgaenge = witaVorgangCaptor.getAllValues();
        assertThat((WitaCBVorgang) result.get(0), isIn(vorgaenge));

        verify(witaCheckConditionService).checkConditionsForWbciPreagreement(result);
    }

    @Test
    public void testCreateKvzCBVorgang() throws Exception {
        CbVorgangData cbvDataKvz = createCbVorgangData(CBVorgang.TYP_NEU, 1L, 1L, 1L);

        CBVorgang cbVorgangNeu = new CBVorgangBuilder().withId(2L).build();
        doReturn(Arrays.asList(cbVorgangNeu)).when(cut).createCBVorgangWithCarrierbestellung(argThat(new CBVorgangDataMatcher(cbvDataKvz)));

        List<CBVorgang> result = cut.createKvzCBVorgang(cbvDataKvz, 1L);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        verify(cut).createCBVorgangWithCarrierbestellung(argThat(new CBVorgangDataMatcher(cbvDataKvz)));
        verify(cut).startWitaWorkflows(asList(cbVorgangNeu, new CBVorgang[] { }));
    }

    @Test
    public void testCreateHvtAndKvzCBVorgang() throws Exception {
        final long auftragIdHvt = 3L;

        final Endstelle endstelleHvtAuftrag = new EndstelleBuilder().withId(100L).build();
        when(endstellenService.findEndstelle4Auftrag(auftragIdHvt, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelleHvtAuftrag);
        final Carrierbestellung cbHvtAuftrag = new CarrierbestellungBuilder().withId(10L).build();
        when(carrierService.findLastCB4Endstelle(endstelleHvtAuftrag.getId())).thenReturn(cbHvtAuftrag);

        CbVorgangData cbvData = createCbVorgangData(CBVorgang.TYP_HVT_KVZ, 1L, 1L, auftragIdHvt);
        CbVorgangData cbvDataKvz = createCbVorgangData(CBVorgang.TYP_NEU, 1L, cbvData.getCbId(), cbvData.getAuftragId4HvtToKvz());
        CbVorgangData cbvDataHvt = createCbVorgangData(CBVorgang.TYP_KUENDIGUNG, cbvData.getAuftragId4HvtToKvz(), cbHvtAuftrag.getId(), null);

        CBVorgang cbVorgangKuendigung = new WitaCBVorgangBuilder().withId(1L).build();
        doReturn(Arrays.asList(cbVorgangKuendigung)).when(cut).createCBVorgangWithCarrierbestellung(argThat(new CBVorgangDataMatcher(cbvDataHvt)));

        CBVorgang cbVorgangNeu = new CBVorgangBuilder().withId(2L).build();
        doReturn(Arrays.asList(cbVorgangNeu)).when(cut).createCBVorgangWithCarrierbestellung(argThat(new CBVorgangDataMatcher(cbvDataKvz)));

        List<CBVorgang> result = cut.createHvtAndKvzCBVorgang(cbvData);
        assertNotNull(result);
        assertEquals(result.size(), 2);
        verify(cut).createCBVorgangWithCarrierbestellung(argThat(new CBVorgangDataMatcher(cbvDataHvt)));
        verify(cut).startWitaWorkflows(asList(cbVorgangKuendigung, new CBVorgang[] { }));
        verify(cut).createCBVorgangWithCarrierbestellung(argThat(new CBVorgangDataMatcher(cbvDataKvz)));
        verify(cut).startWitaWorkflows(asList(cbVorgangNeu, new CBVorgang[] { }));
    }

    private CbVorgangData createCbVorgangData(Long cbVorgangTyp, Long auftragId, Long cbId, Long auftragIdHvt) {
        return new CbVorgangData()
                .addAuftragIds(auftragId)
                .withCbId(cbId)
                .withAuftragId4HvtToKvz(auftragIdHvt)
                .withCbVorgangTyp(cbVorgangTyp);
    }

    private class CBVorgangDataMatcher extends ArgumentMatcher<CbVorgangData> {

        private CbVorgangData reference;

        private CBVorgangDataMatcher(CbVorgangData cbVorgangData) {
            reference = cbVorgangData;
        }

        public boolean matches(Object object) {
            assert (object instanceof CbVorgangData);
            final CbVorgangData cbVorgangData = (CbVorgangData) object;
            if (reference.getCbId() != cbVorgangData.getCbId()) {
                return false;
            }
            if (!reference.getAuftragIds().containsAll(cbVorgangData.getAuftragIds())
                    || !cbVorgangData.getAuftragIds().containsAll(reference.getAuftragIds())) {
                return false;
            }
            if (reference.getCbVorgangTyp() != cbVorgangData.getCbVorgangTyp()) {
                return false;
            }
            return true;
        }
    }

    @Test
    public void testCreateHvtKvzCBVorgaenge() throws Exception {
        final long auftragIdHvt = 3L;
        CbVorgangData cbvData = createCbVorgangData(CBVorgang.TYP_HVT_KVZ, 1L, 1L, auftragIdHvt);
        when(carrierElTALService.findCBVorgaengeByExample(any(WitaCBVorgang.class))).thenReturn(Collections.emptyList());
        doReturn(Collections.EMPTY_LIST).when(cut).createHvtAndKvzCBVorgang(cbvData);

        cut.createHvtKvzCBVorgaenge(cbvData);
        verify(carrierElTALService).findCBVorgaengeByExample(any(WitaCBVorgang.class));
        verify(cut).createHvtAndKvzCBVorgang(cbvData);
    }

    @Test
    public void testCreateHvtKvzCBVorgaengeHvtAfterABBM() throws Exception {
        final long auftragIdHvt4HvtToKvz = 10L;
        final Long hvtCbVorgangId = 1001L;
        List<WitaCBVorgang> witaCbVorgaenge = Arrays.asList(
                new WitaCBVorgangBuilder()
                        .withId(1L)
                        .withStatus(CBVorgang.STATUS_CLOSED)
                        .build(),
                new WitaCBVorgangBuilder()
                        .withId(2L)
                        .withStatus(CBVorgang.STATUS_TRANSFERRED)
                        .build(),
                new WitaCBVorgangBuilder()
                        .withId(hvtCbVorgangId)
                        .withStatus(CBVorgang.STATUS_ANSWERED)
                        .build()
        );
        when(carrierElTALService.findCBVorgaengeByExample(any(WitaCBVorgang.class))).thenReturn(witaCbVorgaenge);
        CbVorgangData cbvData = new CbVorgangData().withCbVorgangTyp(CBVorgang.TYP_HVT_KVZ)
                .withAuftragId4HvtToKvz(auftragIdHvt4HvtToKvz);
        doReturn(Collections.EMPTY_LIST).when(cut).createKvzCBVorgang(cbvData, hvtCbVorgangId);

        cut.createHvtKvzCBVorgaenge(cbvData);
        verify(carrierElTALService).findCBVorgaengeByExample(any(WitaCBVorgang.class));
        verify(cut).createKvzCBVorgang(cbvData, hvtCbVorgangId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Es wird ein CBVorgang vom Typ HVT_KVZ erwartet.")
    public void testCreateHvtKvzCBVorgaengeNoHvtKvzTyp() throws Exception {
        CbVorgangData cbvData = new CbVorgangData().withCbVorgangTyp(CBVorgang.TYP_NEU);
        cut.createHvtKvzCBVorgaenge(cbvData);
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "Die AuftragsId des zu kündigenden HVt-Auftrags muss gesetzt sein.")
    public void testCreateHvtAndKvzCBVorgaengeNoHvtAuftragId() throws Exception {
        CbVorgangData cbvData = new CbVorgangData().withCbVorgangTyp(CBVorgang.TYP_HVT_KVZ);
        cut.createHvtAndKvzCBVorgang(cbvData);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testCreateRexMkCBVorgangWithCbId() throws Exception {
        CbVorgangData cbvData = new CbVorgangData().withCbId(123L).withCbVorgangTyp(CBVorgang.TYP_REX_MK);
        cut.createCBVorgang(cbvData);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testCreateRexMkCBVorgangForNonWitaCbVorgang() throws Exception {
        CbVorgangData cbvData = new CbVorgangData().addAuftragIds(ImmutableList.of(1L)).withCarrierId(Carrier.ID_MNET)
                .withCbVorgangTyp(CBVorgang.TYP_REX_MK).withUser(createAkUser());

        cut.createCBVorgang(cbvData);
    }

    private Endstelle createEndstelle(Carrierbestellung cb, Long auftragId) throws FindException {
        Endstelle endstelle = new Endstelle();
        endstelle.setCb2EsId(cb.getCb2EsId());
        endstelle.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(ImmutableList.of(endstelle));
        when(endstellenService.findEndstelle4Auftrag(auftragId, endstelle.getEndstelleTyp())).thenReturn(endstelle);
        return endstelle;
    }

    private Carrierbestellung createCarrierbestellung() throws FindException {
        Carrierbestellung cb = new Carrierbestellung();
        cb.setId(NEXT_ID++);
        cb.setCb2EsId(NEXT_ID++);
        when(carrierService.findCB(cb.getId())).thenReturn(cb);
        return cb;
    }

    @Test
    public void checkAllVorgaengeHaveSameWitaGeschaeftsfallTypGutfall() {
        List<CBVorgang> createdCbVorgaenge = Lists.newArrayList();
        cut.checkAllVorgaengeHaveSameWitaGeschaeftsfallTyp(createdCbVorgaenge);
        createdCbVorgaenge.add(createCbVorgang(PROVIDERWECHSEL));
        createdCbVorgaenge.add(createCbVorgang(PROVIDERWECHSEL));
        cut.checkAllVorgaengeHaveSameWitaGeschaeftsfallTyp(createdCbVorgaenge);
    }

    @Test(expectedExceptions = WitaBaseException.class)
    void checkAllVorgaengeHaveSameWitaGeschaeftsfallTypSchlechtfall() {
        List<CBVorgang> createdCbVorgaenge = Lists.newArrayList();
        createdCbVorgaenge.add(createCbVorgang(PROVIDERWECHSEL));
        createdCbVorgaenge.add(createCbVorgang(LEISTUNGS_AENDERUNG));
        cut.checkAllVorgaengeHaveSameWitaGeschaeftsfallTyp(createdCbVorgaenge);
    }

    private CBVorgang createCbVorgang(GeschaeftsfallTyp typ) {
        WitaCBVorgang cbv = new WitaCBVorgang();
        cbv.setWitaGeschaeftsfallTyp(typ);
        return cbv;
    }

    @DataProvider
    public Object[][] sendPositiveRuemPvProvider() {
        // @formatter:off
        return new Object[][] {
                { null, null, null, false },
                { "123", null, null, false },
                { "123", RuemPvAntwortCode.OK, null, true },
                { "123", RuemPvAntwortCode.TERMIN_UNGUELTIG, null, false },
                { "123", RuemPvAntwortCode.SONSTIGES, "text", false },
        };
    }

    @Test(dataProvider = "sendPositiveRuemPvProvider")
    public void sendPositiveRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText, boolean expectOk) {
        try {
            cut.sendPositiveRuemPv(businessKey, antwortCode, antwortText, createAkUser());
        }
        catch (Exception e) {
            if (expectOk) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @DataProvider
    public Object[][] sendNegativeRuemPvProvider() {
        // @formatter:off
        return new Object[][] {
                { null, null, null, false },
                { "123", null, null, false },
                { "123", RuemPvAntwortCode.SONSTIGES, null, false },
                { "123", RuemPvAntwortCode.SONSTIGES, "text", true },
                { "123", RuemPvAntwortCode.OK, null, false },
        };
    }

    @Test(dataProvider = "sendNegativeRuemPvProvider")
    public void sendNegativeRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText, boolean expectOk) {
        try {
            cut.sendNegativeRuemPv(businessKey, antwortCode, antwortText, createAkUser());
        }
        catch (Exception e) {
            if (expectOk) {
                throw e;
            }
        }
    }

    @Test
    public void claimShouldBeCheckedForPositiveRuemPv() {
        AKUser user = createAkUser();
        cut.sendPositiveRuemPv("123", RuemPvAntwortCode.OK, null, user);
        verify(witaUsertaskService).checkUserTaskNotClaimedByOtherUser(any(UserTask.class), eq(user));
    }

    @Test
    public void claimShouldBeCheckedForNegativeRuemPv() {
        AKUser user = createAkUser();
        cut.sendNegativeRuemPv("123", RuemPvAntwortCode.SONSTIGES, "thdnhdt", user);
        verify(witaUsertaskService).checkUserTaskNotClaimedByOtherUser(any(UserTask.class), eq(user));
    }

    @DataProvider
    public Object[][] checkPortierungenStimmenUebereinTestData() {
        // @formatter:off
        return new Object[][] {
                {new RufnummernPortierungBuilder().buildMeldungPortierung(true), new RufnummernPortierungBuilder().buildMeldungPortierung(true), false},
                {null, null, false},
                {new RufnummernPortierungBuilder().buildMeldungPortierung(true), null, true},
                {null, new RufnummernPortierungBuilder().buildMeldungPortierung(true), true},
                {new RufnummernPortierungBuilder().withOnkz("66").buildMeldungPortierung(true), new RufnummernPortierungBuilder().buildMeldungPortierung(true), true},
                {new RufnummernPortierungBuilder().createRufnummernEinzelAnschluss(), new RufnummernPortierungBuilder().createRufnummernEinzelAnschluss(), false},
                {new RufnummernPortierungBuilder().createRufnummernEinzelAnschluss(), new RufnummernPortierungBuilder().createRufnummernAnlagenAnschluss(), true},
        };
        // @formatter:on
    }

    @Test(dataProvider = "checkPortierungenStimmenUebereinTestData")
    public void checkPortierungenStimmenUeberein(RufnummernPortierung bestelltePortierung,
            RufnummernPortierung zurueckgemeldetePortierung,
            boolean expectedBestelltePortierungUngleichZurueckgemeldetePortierung) throws FindException {

        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().withRufnummernPortierung(
                zurueckgemeldetePortierung).build();
        WitaCBVorgang witaCbVorgang = new WitaCBVorgangBuilder().withTyp(CBVorgang.TYP_ANBIETERWECHSEL)
                .withReturnOk(Boolean.TRUE).withCarrierId(12L).withId(12L).build();
        when(mwfEntityService.getLastAbm(witaCbVorgang.getCarrierRefNr())).thenReturn(abm);
        when(carrierElTALService.findCBVorgang(witaCbVorgang.getId())).thenReturn(witaCbVorgang);

        doReturn(Lists.newArrayList()).when(cut).loadRufnummernKommend(witaCbVorgang);
        doReturn(bestelltePortierung).when(cut).loadBestellteRufnummerPortierung(witaCbVorgang.getId());

        RufnummerPortierungCheck result = cut.checkRufnummerPortierungAufnehmend(witaCbVorgang.getId());

        assertEquals(result.bestelltePortierungUngleichZurueckgemeldetePortierung,
                expectedBestelltePortierungUngleichZurueckgemeldetePortierung);
        if (expectedBestelltePortierungUngleichZurueckgemeldetePortierung) {
            assertTrue((result.portierungMnet != null) || (result.portierungAndererCarrier != null));
        }
    }

    @Test
    public void testTerminverschiebungForDifferentDate() throws Exception {
        AKUser user = createAkUser();

        Auftrag auftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        when(mwfEntityService.getAuftragOfCbVorgang(any(Long.class))).thenReturn(auftrag);

        LocalDate termin = DateConverterUtils.asLocalDate(witaCbVorgang.getVorgabeMnet()).plusDays(1);
        ;
        cut.doTerminverschiebung(witaCbVorgang.getId(), termin, user, false, null, TV_60_TAGE);
        verify(mwfCbVorgangConverterService).writeTerminVerschiebung(witaCbVorgang, termin, user);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testTerminverschiebungForSameDate() throws Exception {
        AKUser user = createAkUser();
        user.setId(666L);
        user.setName("John Doe");

        cut.doTerminverschiebung(witaCbVorgang.getId(), DateConverterUtils.asLocalDate(witaCbVorgang.getVorgabeMnet()), user,
                false, null, TV_60_TAGE);
        assertEquals(witaCbVorgang.getUserId(), user.getId());
    }

    @Test
    public void testDoStorno() throws Exception {
        witaCbVorgang.setTamUserTask(new TamUserTaskBuilder().build());

        ProcessInstance processInstance = mock(ProcessInstance.class);
        AKUser user = createAkUser();

        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();
        when(mwfEntityService.getAuftragOfCbVorgang(witaCbVorgang.getId())).thenReturn(auftrag);

        cut.doStorno(witaCbVorgang.getId(), user);

        assertFalse(witaCbVorgang.isRequestOnUnsentRequest());

        verify(mwfEntityService).getAuftragOfCbVorgang(witaCbVorgang.getId());
        verify(witaCheckConditionService).checkConditionsForStorno(witaCbVorgang, auftrag);
        verify(witaUsertaskService).closeUserTask(witaCbVorgang.getTamUserTask(), user);
        verify(mwfCbVorgangConverterService).writeStorno(witaCbVorgang, user);
        verify(mwfEntityService).store(any(Storno.class));
        verify(workflowService).sendTvOrStornoRequest(any(Storno.class));
        verify(witaUsertaskService, never()).resetUserTask(witaCbVorgang.getTamUserTask());
        verify(commonWorkflowService, never()).deleteProcessInstance(processInstance);
    }

    @DataProvider
    public Object[][] dataProviderStornoOnNotSentRequest() {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();
        Storno storno = new StornoBuilder(BEREITSTELLUNG).buildValid();
        TerminVerschiebung tv = new TerminVerschiebungBuilder(BEREITSTELLUNG).buildValid();

        // @formatter:off
        return new Object[][] {
                { auftrag, true, false },
                { storno, false, true },
                { tv, false, true },
            };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderStornoOnNotSentRequest")
    public void testDoStornoOnNotSentRequest(MnetWitaRequest request, boolean deleteProcessInstance,
            boolean resetUserTask) throws Exception {
        witaCbVorgang.setTamUserTask(new TamUserTaskBuilder().build());

        ProcessInstance processInstance = mock(ProcessInstance.class);
        AKUser user = createAkUser();

        when(commonWorkflowService.retrieveProcessInstance(witaCbVorgang.getBusinessKey())).thenReturn(processInstance);
        when(mwfEntityService.findUnsentRequest(witaCbVorgang.getId())).thenReturn(request);

        cut.doStorno(witaCbVorgang.getId(), user);

        assertTrue(request.getRequestWurdeStorniert());
        assertEquals(witaCbVorgang.getUserId(), user.getId());
        assertTrue(witaCbVorgang.isRequestOnUnsentRequest());

        verify(mwfEntityService).store(request);
        verify(carrierElTALService, atLeastOnce()).saveCBVorgang(witaCbVorgang);
        verify(witaUsertaskService, times(resetUserTask ? 1 : 0)).resetUserTask(witaCbVorgang.getTamUserTask());
        verify(commonWorkflowService, times(deleteProcessInstance ? 1 : 0)).deleteProcessInstance(processInstance);
        verify(workflowService, never()).sendTvOrStornoRequest(any(MnetWitaRequest.class));
        verify(mwfCbVorgangConverterService, never()).writeStorno(witaCbVorgang, user);
    }

    @Test
    public void testDoStornoStatusResetOnNotSentTv() throws Exception {
        TerminVerschiebung tv = new TerminVerschiebungBuilder(BEREITSTELLUNG).buildValid();
        when(mwfEntityService.findUnsentRequest(witaCbVorgang.getId())).thenReturn(tv);

        witaCbVorgang.setStatus(CBVorgang.STATUS_SUBMITTED);
        witaCbVorgang.setStatusLast(CBVorgang.STATUS_ANSWERED);
        witaCbVorgang.setLetztesGesendetesAenderungsKennzeichen(AenderungsKennzeichen.STANDARD);
        witaCbVorgang.setAenderungsKennzeichen(AenderungsKennzeichen.TERMINVERSCHIEBUNG);

        cut.doStorno(witaCbVorgang.getId(), null);

        assertTrue(tv.getRequestWurdeStorniert());
        assertTrue(witaCbVorgang.isRequestOnUnsentRequest());

        assertEquals(witaCbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
        assertNull(witaCbVorgang.getStatusLast());
        assertEquals(witaCbVorgang.getAenderungsKennzeichen(), AenderungsKennzeichen.STANDARD);
        assertNull(witaCbVorgang.getLetztesGesendetesAenderungsKennzeichen());
        verify(mwfCbVorgangConverterService, never()).writeStorno(witaCbVorgang, null);
    }

    @Test
    public void testDoTerminverschiebung() throws Exception {
        witaCbVorgang.setTamUserTask(new TamUserTaskBuilder().build());
        AKUser user = createAkUser();
        LocalDate newKwt = LocalDate.now().plusDays(14);

        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();
        when(mwfEntityService.getAuftragOfCbVorgang(witaCbVorgang.getId())).thenReturn(auftrag);

        cut.doTerminverschiebung(witaCbVorgang.getId(), newKwt, user, true, "test - neuer Montagehinweis", TV_60_TAGE);

        assertFalse(witaCbVorgang.isRequestOnUnsentRequest());

        verify(witaUsertaskService).closeUserTask(witaCbVorgang.getTamUserTask(), user);
        verify(witaCheckConditionService).checkConditionsForTv(witaCbVorgang, auftrag, newKwt);
        verify(mwfCbVorgangConverterService).writeTerminVerschiebung(witaCbVorgang, newKwt, user);
        verify(montageleistungAggregator).aggregate(witaCbVorgang);
        verify(mwfEntityService).store(any(TerminVerschiebung.class));
        verify(workflowService).sendTvOrStornoRequest(any(TerminVerschiebung.class));

        verify(commonWorkflowService, never()).deleteProcessInstance(any(ProcessInstance.class));
        verify(witaUsertaskService, never()).resetUserTask(any(TamUserTask.class));
    }

    @Test
    public void testDoTerminverschiebungOnNotSentAuftrag() throws Exception {
        AKUser user = createAkUser();

        LocalDate kwt = DateConverterUtils.asLocalDate(witaCbVorgang.getVorgabeMnet());
        LocalDate newKwt = kwt.plusDays(14);

        Auftrag auftragNotSent = new AuftragBuilder(
                new GeschaeftsfallBuilder(BEREITSTELLUNG).withKundenwunschtermin(kwt)).buildValid();

        when(mwfEntityService.findUnsentRequest(witaCbVorgang.getId())).thenReturn(auftragNotSent);
        when(dateTimeCalculationService.isTerminverschiebungValid(eq(newKwt), any(Zeitfenster.class), eq(false),
                eq(BEREITSTELLUNG), (String) isNull(), eq(false))).thenReturn(Boolean.TRUE);

        cut.doTerminverschiebung(witaCbVorgang.getId(), newKwt, user, false, "test - neuer Montagehinweis", TV_60_TAGE);

        assertEquals(DateConverterUtils.asLocalDate(witaCbVorgang.getVorgabeMnet()), newKwt);
        assertEquals(auftragNotSent.getGeschaeftsfall().getKundenwunschtermin().getDatum(), newKwt);
        assertEquals(witaCbVorgang.getUserId(), user.getId());
        assertEquals(witaCbVorgang.getMontagehinweis(), "test - neuer Montagehinweis");
        assertTrue(witaCbVorgang.isRequestOnUnsentRequest());

        verify(mwfEntityService).store(auftragNotSent);
        verify(carrierElTALService, atLeastOnce()).saveCBVorgang(witaCbVorgang);
        verify(witaUsertaskService).checkUserTaskNotClaimedByOtherUser(any(TamUserTask.class), eq(user));
        verify(montageleistungAggregator).aggregate(witaCbVorgang);
        verify(witaUsertaskService, never()).closeUserTask(any(TamUserTask.class), eq(user));
        verify(workflowService, never()).sendTvOrStornoRequest(any(MnetWitaRequest.class));
        verify(mwfCbVorgangConverterService, never()).writeStorno(witaCbVorgang, user);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Terminverschiebung nicht möglich, da zu diesem Vorgang noch ein nicht an die Telekom gesendeter Storno exisitiert.")
    public void testDoTerminverschiebungOnNotSentStorno() throws Exception {
        AKUser user = createAkUser();

        LocalDate kwt = DateConverterUtils.asLocalDate(witaCbVorgang.getVorgabeMnet());
        LocalDate newKwt = kwt.plusDays(14);

        Storno stornoNotSent = new StornoBuilder(BEREITSTELLUNG).buildValid();
        when(mwfEntityService.findUnsentRequest(witaCbVorgang.getId())).thenReturn(stornoNotSent);
        when(dateTimeCalculationService.isTerminverschiebungValid(eq(newKwt), any(Zeitfenster.class), eq(false),
                eq(BEREITSTELLUNG), (String) isNull(), eq(false))).thenReturn(Boolean.TRUE);

        cut.doTerminverschiebung(witaCbVorgang.getId(), newKwt, user, false, null, TV_60_TAGE);
    }

    @Test
    public void testDoTerminverschiebungOnNotSentTv() throws Exception {
        AKUser user = createAkUser();

        LocalDate kwt = DateConverterUtils.asLocalDate(witaCbVorgang.getVorgabeMnet());
        LocalDate newKwt = kwt.plusDays(14);

        TerminVerschiebung tvNotSent = new TerminVerschiebungBuilder(BEREITSTELLUNG).withTermin(kwt).buildValid();

        when(mwfEntityService.findUnsentRequest(witaCbVorgang.getId())).thenReturn(tvNotSent);
        when(dateTimeCalculationService.isTerminverschiebungValid(eq(newKwt), any(Zeitfenster.class), eq(false),
                eq(BEREITSTELLUNG), (String) isNull(), eq(false))).thenReturn(Boolean.TRUE);

        cut.doTerminverschiebung(witaCbVorgang.getId(), newKwt, user, false, "test2 - neuer Montagehinweis", TV_60_TAGE);

        assertEquals(DateConverterUtils.asLocalDate(witaCbVorgang.getVorgabeMnet()), newKwt);
        assertEquals(tvNotSent.getTermin(), newKwt);
        assertEquals(tvNotSent.getGeschaeftsfall().getKundenwunschtermin().getDatum(), LocalDate.now());
        assertEquals(witaCbVorgang.getUserId(), user.getId());
        assertEquals(witaCbVorgang.getMontagehinweis(), "test2 - neuer Montagehinweis");
        assertTrue(witaCbVorgang.isRequestOnUnsentRequest());

        verify(mwfEntityService).store(tvNotSent);
        verify(carrierElTALService, atLeastOnce()).saveCBVorgang(witaCbVorgang);
        verify(witaUsertaskService).checkUserTaskNotClaimedByOtherUser(any(TamUserTask.class), eq(user));
        verify(montageleistungAggregator).aggregate(witaCbVorgang);
        verify(witaUsertaskService, never()).closeUserTask(any(TamUserTask.class), eq(user));
        verify(workflowService, never()).sendTvOrStornoRequest(any(MnetWitaRequest.class));
        verify(mwfCbVorgangConverterService, never()).writeStorno(witaCbVorgang, user);
    }

    @Test(expectedExceptions = StoreException.class)
    public void checkNeuesVorgabedatumOnRealDate() throws StoreException {
        WitaCBVorgang witaCBVorgang = new WitaCBVorgang();
        witaCBVorgang.setReturnRealDate(new Date());
        cut.checkNeuesVorgabedatumNotSameDay(witaCBVorgang, LocalDate.now());
    }

    @Test(expectedExceptions = StoreException.class)
    public void checkNeuesVorgabedatumOnVorgabeMnet() throws StoreException {
        WitaCBVorgang witaCBVorgang = new WitaCBVorgang();
        witaCBVorgang.setVorgabeMnet(new Date());
        cut.checkNeuesVorgabedatumNotSameDay(witaCBVorgang, LocalDate.now());
    }

    @Test
    public void checkSameVorgabedatumDifferentRealDate() throws StoreException {
        WitaCBVorgang witaCBVorgang = new WitaCBVorgang();
        witaCBVorgang.setVorgabeMnet(new Date());
        witaCBVorgang.setReturnRealDate(Date.from(LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault()).toInstant()));
        cut.checkNeuesVorgabedatumNotSameDay(witaCBVorgang, LocalDate.now());
    }

    @Test
    public void testSendErlmk() throws Exception {
        AKUser user = createAkUser();

        cut.sendErlmk(witaCbVorgang.getId(), user);
        assertEquals(witaCbVorgang.getUserId(), user.getId());
    }

    @Test(expectedExceptions = StoreException.class)
    public void testCloseCbVorgangOnNotFoundVorgang() throws Exception {
        cut.closeCBVorgang(21L, 42L);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testCloseCbVorgangOnNotAnsweredVorgang() throws Exception {
        witaCbVorgang.setStatus(CBVorgang.STATUS_TRANSFERRED);
        cut.closeCBVorgang(witaCbVorgang.getId(), 42L);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testCloseCbVorgangOnClosedVorgang() throws Exception {
        witaCbVorgang.setStatus(CBVorgang.STATUS_CLOSED);
        cut.closeCBVorgang(witaCbVorgang.getId(), 42L);
    }

    @Test
    public void writeDataOnCbForPosNeubestellung() throws Exception {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Carrierbestellung cb = getCarrierbestellungBuilder().build();
        WitaCBVorgang cbv = getWitaCBVorgangBuilder(cb).withTyp(CBVorgang.TYP_NEU).withReturnRealDate(today)
                .withReturnLBZ("astrtda").withReturnVTRNR("arstarst").withReturnAQS("1/567/21")
                .withReturnLL("123/346/89").withReturnKundeVorOrt(true).withReturnMaxBruttoBitrate("2345356")
                .withTalRealisierungsZeitfenster(TalRealisierungsZeitfenster.GANZTAGS)
                .build();
        cbv.answer(true);
        setupCarrierMocks(cb, cbv);

        cut.closeCBVorgang(cbv.getId(), null);

        assertThat(cb.getLbz(), equalTo("astrtda"));
        assertThat(cb.getVtrNr(), equalTo("arstarst"));
        assertThat(cb.getLl(), equalTo("123/346/89"));
        assertThat(cb.getAqs(), equalTo("1/567/21"));
        assertNotNull(cb.getZurueckAm());
        assertThat(cb.getBereitstellungAm(), equalTo(today));
        assertThat(cb.getKundeVorOrt(), equalTo(Boolean.TRUE));
        assertThat(cb.getMaxBruttoBitrate(), equalTo("2345356"));
        assertThat(cb.getTalRealisierungsZeitfenster(), equalTo(TalRealisierungsZeitfenster.GANZTAGS));
    }

    @Test
    public void writeDataOnCbNegRueckmeldungAfterPosRueckmeldung() throws Exception {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Carrierbestellung cb = getCarrierbestellungBuilder().withLbz("astrtda").withVtrNr("arstarst")
                .withLl("123/346/89").withAqs("1/567/21").withZurueckAm(today).withBereitstellungAm(today)
                .withKundeVorOrt(Boolean.TRUE).withMaxBruttoBitrate("2345356").build();
        WitaCBVorgang cbv = getWitaCBVorgangBuilder(cb).withTyp(CBVorgang.TYP_NEU).withReturnRealDate(today)
                .withReturnLBZ("astrtda").withReturnVTRNR("arstarst").withReturnAQS("1/567/21")
                .withReturnLL("123/346/89").withReturnKundeVorOrt(true).withReturnMaxBruttoBitrate("2345356").build();
        cbv.answer(false);
        setupCarrierMocks(cb, cbv);

        cut.closeCBVorgang(cbv.getId(), null);

        assertNull(cb.getLbz());
        assertNull(cb.getVtrNr());
        assertNull(cb.getLl());
        assertNull(cb.getAqs());
        assertNull(cb.getZurueckAm());
        assertNull(cb.getBereitstellungAm());
        assertNull(cb.getKundeVorOrt());
        assertNull(cb.getMaxBruttoBitrate());
        assertNull(cb.getTalRealisierungsZeitfenster());
    }

    @Test
    public void writeDataOnCbForNegAndPosStornoOnNeubestellung() throws Exception {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Carrierbestellung cb = getCarrierbestellungBuilder().withLbz("astrtda").withVtrNr("arstarst")
                .withLl("123/346/89").withAqs("1/567/21").withZurueckAm(today).withBereitstellungAm(today)
                .withKundeVorOrt(Boolean.TRUE).withMaxBruttoBitrate("2345356").build();
        WitaCBVorgang cbv = getWitaCBVorgangBuilder(cb).withTyp(CBVorgang.TYP_NEU)
                .withAenderungsKennzeichen(AenderungsKennzeichen.STORNO).withReturnRealDate(today)
                .withReturnLBZ("astrtda").withReturnVTRNR("arstarst").withReturnAQS("1/567/21")
                .withReturnLL("123/346/89").withReturnKundeVorOrt(true).withReturnMaxBruttoBitrate("2345356")
                .withTalRealisierungsZeitfenster(TalRealisierungsZeitfenster.VORMITTAG)
                .build();
        cbv.answer(true);
        setupCarrierMocks(cb, cbv);

        cut.closeCBVorgang(cbv.getId(), null);

        assertThat(cb.getLbz(), equalTo("astrtda"));
        assertThat(cb.getVtrNr(), equalTo("arstarst"));
        assertThat(cb.getLl(), equalTo("123/346/89"));
        assertThat(cb.getAqs(), equalTo("1/567/21"));
        assertNotNull(cb.getZurueckAm());
        assertThat(cb.getBereitstellungAm(), equalTo(today));
        assertThat(cb.getKundeVorOrt(), equalTo(Boolean.TRUE));
        assertThat(cb.getMaxBruttoBitrate(), equalTo("2345356"));
        assertThat(cb.getTalRealisierungsZeitfenster(), equalTo(TalRealisierungsZeitfenster.VORMITTAG));
    }

    @Test
    public void testWriteDataOntoCarrierbestellungPosKuendigung() throws Exception {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Carrierbestellung cb = getCarrierbestellungBuilder().build();
        WitaCBVorgang cbv = getWitaCBVorgangBuilder(cb).withTyp(CBVorgang.TYP_KUENDIGUNG).build();
        cbv.setTyp(CBVorgang.TYP_KUENDIGUNG);
        cbv.setReturnRealDate(today);
        cbv.answer(true);
        setupCarrierMocks(cb, cbv);

        assertNull(cb.getKuendBestaetigungCarrier());
        cut.closeCBVorgang(cbv.getId(), null);
        assertThat(cb.getKuendBestaetigungCarrier(), equalTo(today));
    }

    @Test
    public void testWriteDataOntoCarrierbestellungNegKuendigung() throws Exception {
        Carrierbestellung cb = getCarrierbestellungBuilder().build();
        WitaCBVorgang cbv = getWitaCBVorgangBuilder(cb).withTyp(CBVorgang.TYP_KUENDIGUNG).build();
        cbv.answer(false);
        setupCarrierMocks(cb, cbv);
        cut.closeCBVorgang(cbv.getId(), null);
        assertNull(cb.getKuendBestaetigungCarrier());
    }

    @Test
    public void testWriteDataOntoCarrierbestellungPosStornoOnKuendigung() throws Exception {
        Carrierbestellung cb = getCarrierbestellungBuilder().withKuendBestaetigungCarrier(new Date())
                .withKuendigungAnCarrier(new Date()).build();
        WitaCBVorgang cbv = getWitaCBVorgangBuilder(cb).withTyp(CBVorgang.TYP_KUENDIGUNG)
                .withAenderungsKennzeichen(AenderungsKennzeichen.STORNO).build();
        cbv.answer(true);
        setupCarrierMocks(cb, cbv);
        cut.closeCBVorgang(cbv.getId(), null);
        assertNull(cb.getKuendBestaetigungCarrier());
        assertNull(cb.getKuendigungAnCarrier());
    }

    @Test
    public void testWriteDataOntoCarrierbestellungNegStornoOnKuendigung() throws Exception {
        Date today = new Date();
        Carrierbestellung cb = getCarrierbestellungBuilder().withKuendBestaetigungCarrier(today)
                .withKuendigungAnCarrier(today).build();
        WitaCBVorgang cbv = getWitaCBVorgangBuilder(cb).withTyp(CBVorgang.TYP_KUENDIGUNG)
                .withAenderungsKennzeichen(AenderungsKennzeichen.STORNO).build();
        cbv.answer(false);
        setupCarrierMocks(cb, cbv);
        cut.closeCBVorgang(cbv.getId(), null);
        assertEquals(cb.getKuendBestaetigungCarrier(), today);
        assertEquals(cb.getKuendigungAnCarrier(), today);
    }

    @Test
    public void testCreateTerminverschiebungForBereiststellungShouldAggregateMontageleistung() {
        LocalDate termin = LocalDate.now();
        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        Montageleistung newMontageleistung = new Montageleistung();
        when(montageleistungAggregator.aggregate(cbVorgang)).thenReturn(newMontageleistung);

        Auftrag auftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        when(mwfEntityService.getAuftragOfCbVorgang(cbVorgang.getId())).thenReturn(auftrag);

        TerminVerschiebung tv = cut.checkAndCreateTv(cbVorgang, termin);

        assertNotNull(tv);
        assertEquals(tv.getTermin(), termin);
        assertEquals(tv.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().getMontageleistung(),
                newMontageleistung);
    }

    @Test
    public void testCreateTerminverschiebungForLmaeShouldNotAggregateMontageleistung() {
        LocalDate termin = LocalDate.now();
        WitaCBVorgang cbVorgang = new WitaCBVorgang();

        Auftrag auftrag = new AuftragBuilder(GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG).buildValid();
        when(mwfEntityService.getAuftragOfCbVorgang(cbVorgang.getId())).thenReturn(auftrag);

        TerminVerschiebung tv = cut.checkAndCreateTv(cbVorgang, termin);

        assertNotNull(tv);
        assertEquals(tv.getTermin(), termin);
        assertNull(tv.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().getMontageleistung());
    }

    @Test(expectedExceptions = WitaUserException.class)
    public void testCreateTerminverschiebungCheckConditionsFailed() {
        LocalDate termin = LocalDate.now();
        WitaCBVorgang cbVorgang = new WitaCBVorgang();

        Auftrag auftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        when(mwfEntityService.getAuftragOfCbVorgang(cbVorgang.getId())).thenReturn(auftrag);

        doThrow(new WitaUserException()).when(witaCheckConditionService).checkConditionsForTv(cbVorgang, auftrag,
                termin);

        cut.checkAndCreateTv(cbVorgang, termin);
    }

    @DataProvider
    public Object[][] dataProviderCheckRufnummerPortierungAbgebend() {
        AkmPvUserTask akmPvUserTaskAbbm = new AkmPvUserTaskBuilder().withAkmPvStatus(ABBM_PV_EMPFANGEN).build();

        AkmPvUserTask akmPvUserTaskWithoutAuftragIds = new AkmPvUserTaskBuilder().withUserTaskAuftragDaten(
                Sets.<UserTask2AuftragDaten>newHashSet()).build();

        UserTask2AuftragDaten userTask2AuftragDaten1 = new UserTask2AuftragDaten();
        userTask2AuftragDaten1.setAuftragId(1L);
        UserTask2AuftragDaten userTask2AuftragDaten2 = new UserTask2AuftragDaten();
        userTask2AuftragDaten2.setAuftragId(2L);

        AkmPvUserTask akmPvUserTaskMultipleAuftragIds = new AkmPvUserTaskBuilder().withUserTaskAuftragDaten(
                Sets.newHashSet(userTask2AuftragDaten1, userTask2AuftragDaten2)).build();

        AkmPvUserTask akmPvUserTask = new AkmPvUserTaskBuilder().withVertragsnummer("9A")
                .withUserTaskAuftragDaten(Sets.newHashSet(userTask2AuftragDaten1)).build();

        AuftragDaten auftragDatenWithoutNoOrig = new AuftragDatenBuilder().withAuftragId(1L).withAuftragNoOrig(null)
                .build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragId(1L).withAuftragNoOrig(1L).build();

        AufnehmenderProvider aufnehmenderProvider = new AufnehmenderProviderBuilder()
                .withUebernahmeDatumGeplant(LocalDate.now()).withUebernahmeDatumVerbindlich(LocalDate.now()).build();
        RufnummernPortierung portierung = new RufnummernPortierungBuilder().buildMeldungPortierung(true);
        RufnummernPortierung noPortierung = new RufnummernPortierungEinzelanschluss();

        AnkuendigungsMeldungPv akmPvWithoutPortierung = new AnkuendigungsMeldungPvBuilder()
                .withAufnehmenderProvider(aufnehmenderProvider).withRufnummernPortierung(null).build();
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .withAufnehmenderProvider(aufnehmenderProvider).withRufnummernPortierung(portierung).build();
        AuftragsBestaetigungsMeldungPv abmPvWithoutPortierung = new AuftragsBestaetigungsMeldungPvBuilder()
                .withAufnehmenderProvider(aufnehmenderProvider).withRufnummernPortierung(null).build();
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder()
                .withAufnehmenderProvider(aufnehmenderProvider).withRufnummernPortierung(portierung).build();

        List<Rufnummer> noRufnummernAbgebend = newArrayList();

        Rufnummer rufnummerToday = new RufnummerBuilder().withRealDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        List<Rufnummer> rufnummerAbgebendToday = newArrayList(rufnummerToday);
        Rufnummer rufnummerNotToday = new RufnummerBuilder().withRealDate(
                Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())).build();
        List<Rufnummer> rufnummerAbgebendNotToday = newArrayList(rufnummerNotToday);

        // @formatter:off
        return new Object[][] {
                { new KueDtUserTask(), null, null, null, null, null, false, false, false, false, 0, false, false },
                { akmPvUserTaskAbbm, null, null,null, null, null, false, false, false, false, 0, false, false },

                { akmPvUserTaskWithoutAuftragIds, null, null, null, null, null, true, true, false, false, 0, false, false },
                { akmPvUserTaskMultipleAuftragIds, null, null, null, null, null, true, true, false, false, 0, false, false },

                { akmPvUserTask, null, null, null, null, null, true, false, true, false, 0, false, false },
                { akmPvUserTask, auftragDatenWithoutNoOrig, null, null, null, null, true, false, true, false, 0, false, false },

                { akmPvUserTask, auftragDaten, null, null, null, null, true, false, false, true, 0, false, false },
                { akmPvUserTask, auftragDaten, noRufnummernAbgebend, null, null, noPortierung, false, false, false, false, 0, false, false },

                { akmPvUserTask, auftragDaten, noRufnummernAbgebend, akmPvWithoutPortierung, null, null, true, false, false, false, 0, true, false },
                { akmPvUserTask, auftragDaten, rufnummerAbgebendNotToday, null, abmPvWithoutPortierung, null, true, false, false, false, 1, true, false },
                { akmPvUserTask, auftragDaten, rufnummerAbgebendToday, akmPvWithoutPortierung, abmPvWithoutPortierung, null, true, false, false, false, 0, true, false },

                { akmPvUserTask, auftragDaten, noRufnummernAbgebend, akmPvWithoutPortierung, null, portierung, true, false, false, false, 0, false, true },
                { akmPvUserTask, auftragDaten, rufnummerAbgebendNotToday, null, abmPvWithoutPortierung, portierung, true, false, false, false, 1, false, true },
                { akmPvUserTask, auftragDaten, rufnummerAbgebendToday, akmPvWithoutPortierung, abmPvWithoutPortierung, portierung, true, false, false, false, 0, false, true },

                { akmPvUserTask, auftragDaten, rufnummerAbgebendNotToday, akmPv, null, portierung, true, false, false, false, 1, false, false },
                { akmPvUserTask, auftragDaten, rufnummerAbgebendNotToday, null, abmPv, portierung, true, false, false, false, 1, false, false },
                { akmPvUserTask, auftragDaten, rufnummerAbgebendNotToday, akmPv, abmPv, portierung, true, false, false, false, 1, false, false },

                { akmPvUserTask, auftragDaten, rufnummerAbgebendToday, akmPv, null, portierung, false, false, false, false, 0, false, false },
                { akmPvUserTask, auftragDaten, rufnummerAbgebendToday, null, abmPv, portierung, false, false, false, false, 0, false, false },
                { akmPvUserTask, auftragDaten, rufnummerAbgebendToday, akmPv, abmPv, portierung, false, false, false, false, 0, false, false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckRufnummerPortierungAbgebend")
    public void testCheckRufnummerPortierungAbgebend(AbgebendeLeitungenUserTask abgebendeLeitungUserTask,
            AuftragDaten auftragDaten, List<Rufnummer> rufnummernAbgebend, AnkuendigungsMeldungPv akmPv,
            AuftragsBestaetigungsMeldungPv abmPv, RufnummernPortierung portierungMnet, boolean expectedResult,
            boolean auftragIdNichtEindeutig, boolean fehlerBeimLadenDesAuftrags, boolean fehlerBeimLadenDerRufnummern,
            int falschesPortierungsdatum, boolean fehlerBeimLadenDerTaifunPortierung,
            boolean taifunPortierungUngleichAkmPvPortierung) throws Exception {

        if (auftragDaten != null) {
            when(auftragService.findAuftragDatenByAuftragIdTx(auftragDaten.getAuftragId())).thenReturn(auftragDaten);
            if (rufnummernAbgebend == null) {
                when(rufnummerService.findDnsAbgehend(auftragDaten.getAuftragNoOrig())).thenThrow(new FindException());
            }
            else {
                when(rufnummerService.findDnsAbgehend(auftragDaten.getAuftragNoOrig())).thenReturn(rufnummernAbgebend);
                if (portierungMnet == null) {
                    when(rufnummerPortierungService.transformToRufnummerPortierung(rufnummernAbgebend, false))
                            .thenThrow(new WitaDataAggregationException());
                }
                else {
                    if (StringUtils.isNotBlank(portierungMnet.getPortierungsKenner())) {
                        when(rufnummerPortierungService.transformToRufnummerPortierung(rufnummernAbgebend, false))
                                .thenReturn(portierungMnet);
                    }
                }
            }
        }

        when(mwfEntityService.getLastAkmPv(abgebendeLeitungUserTask.getVertragsNummer())).thenReturn(akmPv);
        when(mwfEntityService.getLastAbmPv(abgebendeLeitungUserTask.getVertragsNummer())).thenReturn(abmPv);

        RufnummerPortierungCheck check = cut.checkRufnummerPortierungAbgebend(abgebendeLeitungUserTask);
        assertThat(check.hasWarnings(), equalTo(expectedResult));
        assertThat(check.auftragIdNichtEindeutig, equalTo(auftragIdNichtEindeutig));
        assertThat(check.fehlerBeimLadenDesAuftrags, equalTo(fehlerBeimLadenDesAuftrags));
        assertThat(check.fehlerBeimLadenDerRufnummern, equalTo(fehlerBeimLadenDerRufnummern));
        assertThat(check.falschesPortierungsdatum, hasSize(falschesPortierungsdatum));
        assertThat(check.fehlerBeimLadenDerTaifunPortierung, equalTo(fehlerBeimLadenDerTaifunPortierung));
        assertThat(check.taifunPortierungUngleichAkmPvPortierung, equalTo(taifunPortierungUngleichAkmPvPortierung));
    }


    private void setupCarrierMocks(Carrierbestellung cb, WitaCBVorgang cbv) throws FindException {
        when(carrierService.findCB(cb.getId())).thenReturn(cb);
        when(carrierElTALService.findCBVorgang(cbv.getId())).thenReturn(cbv);
    }

    private WitaCBVorgangBuilder getWitaCBVorgangBuilder(Carrierbestellung cb) {
        return new WitaCBVorgangBuilder().withRandomId().withReturnLBZ("12341243").withCbId(cb.getId());
    }

    private CarrierbestellungBuilder getCarrierbestellungBuilder() {
        return new CarrierbestellungBuilder().withRandomId().withLl(null).withAqs(null);
    }

    private AKUser createAkUser() {
        AKUser user = new AKUser();
        user.setId(666L);
        user.setName("John Doe");
        return user;
    }

    public void testFindStandortKollokation4CBVorgang() throws Exception {
        cut.findStandortKollokation4CBVorgang(witaCbVorgang.getId());
        verify(standortKollokationAggregator).aggregate(witaCbVorgang);
    }

    @Test
    public void testIgnoreModifyStandortKollokation() throws Exception {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();
        cut.modifyStandortKollokation(null);
        auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setStandortKollokation(null);
        cut.modifyStandortKollokation(auftrag);
        auftrag.getGeschaeftsfall().getAuftragsPosition().setGeschaeftsfallProdukt(null);
        cut.modifyStandortKollokation(auftrag);
        auftrag.getGeschaeftsfall().setAuftragsPosition(null);
        cut.modifyStandortKollokation(auftrag);
        auftrag.setGeschaeftsfall(null);
        cut.modifyStandortKollokation(auftrag);

        verify(cut, never()).findStandortKollokation4CBVorgang(anyLong());
    }

    @Test
    public void testModifyStandortKollokation() throws Exception {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();
        StandortKollokation standortKollokation = auftrag.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().getStandortKollokation();
        assertNotNull(standortKollokation);
        StandortKollokation compareStandortKollokation = new StandortKollokation();
        standortKollokation.copyKollokation(compareStandortKollokation);

        doReturn(compareStandortKollokation).when(cut).findStandortKollokation4CBVorgang(anyLong());
        cut.modifyStandortKollokation(auftrag);

        verify(mwfEntityService, never()).store(any(StandortKollokation.class));

        final String newHausnummer = "34A";
        compareStandortKollokation.setHausnummer(newHausnummer);
        cut.modifyStandortKollokation(auftrag);
        verify(mwfEntityService).store(standortKollokation);
        assertEquals(standortKollokation.getHausnummer(), newHausnummer);
    }

    @DataProvider(name = "findCBVorgaenge4KlammerDataProvider")
    Object[][] findCBVorgaenge4KlammerDataProvider() {
        // @formatter:off
        return new Object[][]{
                { 999L,   2 }
            };
        // @formatter:on
    }

    @Test(dataProvider = "findCBVorgaenge4KlammerDataProvider")
    public void testFindCBVorgaenge4Klammer(Long klammer, int size) {
        WitaCBVorgang witaCbVorgang1 = new WitaCBVorgangBuilder().withAuftragsKlammer(klammer).setPersist(false).build();
        WitaCBVorgang witaCbVorgang2 = new WitaCBVorgangBuilder().withAuftragsKlammer(klammer).setPersist(false).build();
        List<WitaCBVorgang> witaCbVorgaenge = Arrays.asList(witaCbVorgang1, witaCbVorgang2);

        when(witaCbVorgangDao.findByProperty(eq(WitaCBVorgang.class), any(String.class), any(Long.class))).thenReturn(witaCbVorgaenge);
        List<WitaCBVorgang> result = cut.findCBVorgaenge4Klammer(any(Long.class), any(Long.class));
        verify(witaCbVorgangDao).findByProperty(eq(WitaCBVorgang.class), any(String.class), any(Long.class));
        assertNotNull(result);
        assertEquals(result.size(), size);
    }

    @DataProvider
    Object[][] findCBVorgaengeIds4KlammerDataProvider() {
        return new Object[][] {
                { 123L, 345L, 555L },
                { 123L, 555L, 345L },
                { 345L, 123L, 555L },
                { 345L, 555L, 123L },
                { 555L, 123L, 345L },
                { 555L, 345L, 123L }
        };
    }

    @Test(dataProvider = "findCBVorgaengeIds4KlammerDataProvider")
    public void testFindWitaCBVorgaengIDs4Klammer(Long n1, Long n2, Long n3) throws Exception {
        Long klammerId = 999L;

        when(witaCbVorgangDao.findWitaCBVorgangIDsForKlammerId(klammerId)).thenReturn(Arrays.asList(n1, n2, n3));
        SortedSet<Long> result = cut.findWitaCBVorgaengIDs4Klammer(klammerId);
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.first().longValue(), 123L);
        Assert.assertEquals(result.last().longValue(), 555L);
    }

    @DataProvider(name = "testCheckAutoClosingAllowedDataProvider")
    Object[][] testCheckAutoClosingAllowedDataProvider() {
        // @formatter:off
        return new Object[][]{
                // Kombination erlaubt automatisches abschliessen von pos. Rueckmeldungen
                { Boolean.TRUE,  Boolean.TRUE,  CBVorgang.TYP_KUENDIGUNG,       Carrier.ID_DTAG, Boolean.TRUE },
                { Boolean.TRUE,  Boolean.TRUE,  CBVorgang.TYP_NEU,              Carrier.ID_DTAG, Boolean.TRUE },
                { Boolean.TRUE,  Boolean.TRUE,  CBVorgang.TYP_PORTWECHSEL,      Carrier.ID_DTAG, Boolean.TRUE },
                { Boolean.TRUE,  Boolean.TRUE,  CBVorgang.TYP_ANBIETERWECHSEL,  Carrier.ID_DTAG, Boolean.TRUE },
                { Boolean.TRUE,  Boolean.TRUE,  CBVorgang.TYP_HVT_KVZ,          Carrier.ID_DTAG, Boolean.TRUE },
                // Kombination erlaubt KEIN(!) automatisches abschliessen von pos. Rueckmeldungen
                { Boolean.FALSE, Boolean.TRUE,  CBVorgang.TYP_KUENDIGUNG,       Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.TRUE,  Boolean.FALSE, CBVorgang.TYP_KUENDIGUNG,       Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.FALSE, Boolean.TRUE,  CBVorgang.TYP_NEU,              Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.TRUE,  Boolean.FALSE, CBVorgang.TYP_NEU,              Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.FALSE, Boolean.TRUE,  CBVorgang.TYP_PORTWECHSEL,      Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.TRUE,  Boolean.FALSE, CBVorgang.TYP_PORTWECHSEL,      Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.FALSE, Boolean.TRUE,  CBVorgang.TYP_ANBIETERWECHSEL,  Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.TRUE,  Boolean.FALSE, CBVorgang.TYP_ANBIETERWECHSEL,  Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.TRUE,  Boolean.TRUE,  CBVorgang.TYP_NUTZUNGSAENDERUNG,Carrier.ID_AKOM, Boolean.FALSE },
                { Boolean.TRUE,  Boolean.TRUE,  CBVorgang.TYP_KUENDIGUNG,       Carrier.ID_AKOM, Boolean.FALSE },
                { Boolean.FALSE, Boolean.TRUE,  CBVorgang.TYP_HVT_KVZ,          Carrier.ID_DTAG, Boolean.FALSE },
                { Boolean.TRUE,  Boolean.FALSE, CBVorgang.TYP_HVT_KVZ,          Carrier.ID_DTAG, Boolean.FALSE }
            };
        // @formatter:on
    }

    @Test(dataProvider = "testCheckAutoClosingAllowedDataProvider")
    public void testCheckAutoClosingAllowed(Boolean isFeatureOnline, Boolean isAutomPossible, Long cbVorgangTyp,
            Long carrierId, Boolean expected) {
        AuftragDaten auftragDaten = new AuftragDatenBuilder().setPersist(false).build();

        when(featureService.isFeatureOnline(any(FeatureName.class))).thenReturn(isFeatureOnline);
        when(auftragService.isAutomationPossible(any(AuftragDaten.class), any(Long.class))).thenReturn(isAutomPossible);

        Boolean result = cut.checkAutoClosingAllowed(auftragDaten, carrierId, cbVorgangTyp);
        assertNotNull(result);
        assertEquals(result, expected);
    }

    @DataProvider
    Object[][] isAutomationAllowedDataProvider() {
        // @formatter:off
        return new Object[][]{
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)), true,  null, true,  true,  false, true, true},
                {LocalDateTime.now(),                                                         true,  null, true,  true,  false, true, false},
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)), false, null, true,  true,  false, true, false},
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)), true,  1L,   true,  true,  false, true, false},
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)), true,  null, false, true,  false, true, false},
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)), true,  null, true,  false, false, true, false},
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)), true,  null, true,  true,  null,  true, true},
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)), true,  null, true,  true,  true,  true, false},
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)), true,  null, true,  true,  false, false, false},
                {LocalDateTime.now().plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)),  true,  null, true,  true,  false, true, false},

                {LocalDateTime.now().plusDays(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)), true,  null, true,  true,  false, true, false},
        };
        // @formatter:on
    }

    @Test(dataProvider = "isAutomationAllowedDataProvider")
    public void testIsAutomationAllowed(LocalDateTime realDate, boolean isStandardPositiv, Long auftragsKlammer,
            boolean isFeatureOnline, boolean isAutomPossible, Boolean klaerfall,
            boolean isHvtKvzCheckPositive, boolean expected) throws Exception {
        AuftragDaten auftragDaten = mock(AuftragDaten.class);
        when(auftragService.findAuftragDatenByAuftragId(1L)).thenReturn(auftragDaten);

        WitaCBVorgang cbVorgang = mock(WitaCBVorgang.class);
        when(cbVorgang.getAuftragId()).thenReturn(1L);
        when(cbVorgang.getReturnRealDate()).thenReturn(Date.from(realDate.atZone(ZoneId.systemDefault()).toInstant()));
        when(cbVorgang.isStandardPositiv()).thenReturn(isStandardPositiv);
        when(cbVorgang.getAuftragsKlammer()).thenReturn(auftragsKlammer);
        when(cbVorgang.isKlaerfall()).thenReturn(klaerfall);

        when(featureService.isFeatureOnline(any(FeatureName.class))).thenReturn(isFeatureOnline);
        when(auftragService.isAutomationPossible(any(AuftragDaten.class), any(Long.class))).thenReturn(isAutomPossible);

        doReturn(isHvtKvzCheckPositive).when(cut).checkHvtKvzAndAutomationFeature(cbVorgang);

        assertEquals(cut.isAutomationAllowed(cbVorgang), expected);
    }

    @DataProvider
    Object[][] checkHvtKvzAndAutomationFeatureDP() {
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder().build();
        // @formatter:off
        return new Object[][]{
                {CBVorgang.TYP_NEU,         null, null,          true,  true }, // no HVT_KVZ order
                {CBVorgang.TYP_NEU,         1L,   null,          true,  true }, // HVT_KVZ order and feature is online
                {CBVorgang.TYP_NEU,         1L,   null,          false, false}, // HVT_KVZ order but feature is not online
                {CBVorgang.TYP_KUENDIGUNG,  null, null,          true,  true }, // no HVT_KVZ order
                {CBVorgang.TYP_KUENDIGUNG,  null, witaCBVorgang, true,  true }, // HVT_KVZ order and feature is online
                {CBVorgang.TYP_KUENDIGUNG,  null, witaCBVorgang, false, false}, // HVT_KVZ order but feature is not online
                {CBVorgang.TYP_PORTWECHSEL, null, null,          true,  true }, // no HVT_KVZ order
                {CBVorgang.TYP_PORTWECHSEL, null, null,          false, true }, // no HVT_KVZ order
        };
        // @formatter:on
    }

    @Test(dataProvider = "checkHvtKvzAndAutomationFeatureDP")
    public void testCheckHvtKvzAndAutomationFeature(Long cbVorgangTyp, Long cbVorgangRefId, WitaCBVorgang witaCBVorgangByRefId,
            boolean isFeatureOnline, boolean expected) {
        final WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withId(1L)
                .withTyp(cbVorgangTyp)
                .withCbVorgangRefId(cbVorgangRefId)
                .build();
        when(featureService.isFeatureOnline(FeatureName.HVT_KVZ_RQW_AUTOMATION)).thenReturn(isFeatureOnline);
        doReturn(witaCBVorgangByRefId).when(cut).findWitaCBVorgangByRefId(1L);

        assertEquals(cut.checkHvtKvzAndAutomationFeature(witaCBVorgang), expected);
    }

    @Test
    public void testMarkWitaCbVorgangAsKlearfall() throws Exception {
        String bemerkung = "bemerkung";
        cut.markWitaCBVorgangAsKlaerfall(witaCbVorgang.getId(), bemerkung);
        verify(carrierElTALService).findCBVorgang(witaCbVorgang.getId());
        verify(carrierElTALService).saveCBVorgang(witaCbVorgang);

        assertEquals(witaCbVorgang.isKlaerfall(), Boolean.TRUE);
        assertTrue(witaCbVorgang.getKlaerfallBemerkung().matches(bemerkung + " \\(.*\\)"));

        String bemerkung2 = "bemerkung2";
        cut.markWitaCBVorgangAsKlaerfall(witaCbVorgang.getId(), bemerkung2);
        verify(carrierElTALService, times(2)).findCBVorgang(witaCbVorgang.getId());
        verify(carrierElTALService, times(2)).saveCBVorgang(witaCbVorgang);

        assertEquals(witaCbVorgang.isKlaerfall(), Boolean.TRUE);
        assertTrue(witaCbVorgang.getKlaerfallBemerkung().matches(bemerkung + " \\(.*\\)\\\n" + bemerkung2 + " \\(.*\\)"));
    }

    @Test
    public void testAddKlaerfallBemerkung() throws Exception {
        String bemerkung = "bemerkung";
        cut.addKlaerfallBemerkung(witaCbVorgang.getId(), bemerkung);
        verify(carrierElTALService).findCBVorgang(witaCbVorgang.getId());
        verify(carrierElTALService).saveCBVorgang(witaCbVorgang);

        assertEquals(witaCbVorgang.isKlaerfall(), Boolean.FALSE);
        assertTrue(witaCbVorgang.getKlaerfallBemerkung().matches(bemerkung + " \\(.*\\)"));
    }

    @Test
    public void testGetKlearfallBemerkung() throws Exception {
        Long[] cbIds = new Long[] { 1L, 2L };
        String bemerkung1 = "bemerkung1";
        String bemerkung2 = "bemerkung2";
        CBVorgang cbVorgang1 = mock(CBVorgang.class);
        CBVorgang cbVorgang2 = mock(CBVorgang.class);
        when(cbVorgang1.getCbId()).thenReturn(1L);
        when(cbVorgang1.getKlaerfallBemerkung()).thenReturn(bemerkung1);
        when(cbVorgang2.getCbId()).thenReturn(2L);
        when(cbVorgang2.getKlaerfallBemerkung()).thenReturn(bemerkung2);
        when(carrierElTALService.findCBVorgaenge4CB(cbIds)).thenReturn(Arrays.asList(cbVorgang1, cbVorgang2));

        // no IDs
        Assert.assertNull(cut.getKlaerfallBemerkungen(new HashSet<>()));

        // one Klaerfall, one normal
        when(cbVorgang1.isKlaerfall()).thenReturn(true);
        when(cbVorgang2.isKlaerfall()).thenReturn(false);
        Assert.assertEquals(bemerkung1, cut.getKlaerfallBemerkungen(new HashSet<>(Arrays.asList(cbIds))));

        // two Klaerfaelle
        when(cbVorgang2.isKlaerfall()).thenReturn(true);
        Assert.assertEquals("\n- Carrierbestellung 1: bemerkung1\n- Carrierbestellung 2: bemerkung2",
                cut.getKlaerfallBemerkungen(new HashSet<>(Arrays.asList(cbIds))));
    }

    @Test
    public void shouldNotAdaptAuftragAsBereitstellungNotFound() throws Exception {
        when(witaCbVorgangDao.findWitaCBVorgangByRefId(anyLong())).thenReturn(null);
        boolean adapted = cut.checkAndAdaptHvtToKvzBereitstellung(1L, LocalDateTime.now(), new Date());
        assertFalse(adapted);
    }

    @Test(expectedExceptions = AuftragNotFoundException.class)
    public void shouldNotAdaptAuftragAsAuftragNotFound() throws Exception {
        Long cbId = 1L;
        WitaCBVorgang cbMock = Mockito.mock(WitaCBVorgang.class);

        when(witaCbVorgangDao.findWitaCBVorgangByRefId(anyLong())).thenReturn(cbMock);
        when(cbMock.getId()).thenReturn(cbId);
        when(mwfEntityService.getAuftragOfCbVorgang(cbId)).thenThrow(AuftragNotFoundException.class);

        cut.checkAndAdaptHvtToKvzBereitstellung(1L, LocalDateTime.now(), new Date());
    }

    @Test
    public void shouldNotAdaptAuftragAsItHasAlreadyBeenSent() throws Exception {
        Long cbVorgangId = 1L;
        final Date kwt = new Date();
        WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withId(cbVorgangId)
                .withVorgabeMnet(kwt)
                .build();
        Auftrag aMock = new AuftragBuilder(BEREITSTELLUNG).withSentAt(LocalDateTime.now()).buildValid();

        when(witaCbVorgangDao.findWitaCBVorgangByRefId(anyLong())).thenReturn(witaCBVorgang);
        when(mwfEntityService.getAuftragOfCbVorgang(cbVorgangId)).thenReturn(aMock);

        boolean adapted = cut.checkAndAdaptHvtToKvzBereitstellung(1L, LocalDateTime.now(), kwt);

        assertFalse(adapted);
    }

    @Test
    public void shouldMarkCbVorgangForClarification() throws Exception {
        Long cbVorgangId = 1L;
        final Date kwt = new Date();
        WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withId(cbVorgangId)
                .withVorgabeMnet(kwt)
                .build();
        Auftrag aMock = Mockito.mock(Auftrag.class);

        when(witaCbVorgangDao.findWitaCBVorgangByRefId(anyLong())).thenReturn(witaCBVorgang);
        when(mwfEntityService.getAuftragOfCbVorgang(cbVorgangId)).thenReturn(aMock);
        when(aMock.getSentAt()).thenReturn(new Date());
        doNothing().when(cut).markWitaCBVorgangAsKlaerfall(eq(cbVorgangId), anyString());

        boolean adapted = cut.checkAndAdaptHvtToKvzBereitstellung(1L, LocalDateTime.now(), Date.from(LocalDateTime.from(kwt.toInstant().atZone(ZoneId.systemDefault())).plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));

        verify(cut).markWitaCBVorgangAsKlaerfall(eq(cbVorgangId), anyString());
        assertTrue(adapted);
    }

    @Test
    public void shouldAdaptAuftragByRescheduling() throws Exception {
        Long cbRefId = 1L;
        LocalDateTime earliesSendDate = LocalDateTime.now();
        Date kuendigungsDate = new Date();
        Long cbId = 2L;
        WitaCBVorgang cbMock = Mockito.mock(WitaCBVorgang.class);
        Auftrag aMock = Mockito.mock(Auftrag.class);

        when(witaCbVorgangDao.findWitaCBVorgangByRefId(anyLong())).thenReturn(cbMock);
        when(cbMock.getId()).thenReturn(cbId);
        when(mwfEntityService.getAuftragOfCbVorgang(cbId)).thenReturn(aMock);
        when(aMock.getSentAt()).thenReturn(null);
        when(cbMock.getVorgabeMnet()).thenReturn(kuendigungsDate);

        boolean adapted = cut.checkAndAdaptHvtToKvzBereitstellung(cbRefId, earliesSendDate, kuendigungsDate);

        assertTrue(adapted);

        verify(aMock).setEarliestSendDate(Date.from(earliesSendDate.atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Test
    public void shouldAdaptAuftragByReschedulingAndOverwritingTheKWT() throws Exception {
        Long cbRefId = 1L;
        LocalDateTime earliesSendDate = LocalDateTime.now();
        Date kuendigungsDate = new Date();
        Long cbId = 2L;
        WitaCBVorgang cbMock = Mockito.mock(WitaCBVorgang.class);
        Auftrag aMock = Mockito.mock(Auftrag.class);
        Geschaeftsfall gfMock = Mockito.mock(Geschaeftsfall.class);
        Kundenwunschtermin kwtMock = Mockito.mock(Kundenwunschtermin.class);

        when(witaCbVorgangDao.findWitaCBVorgangByRefId(anyLong())).thenReturn(cbMock);
        when(cbMock.getId()).thenReturn(cbId);
        when(mwfEntityService.getAuftragOfCbVorgang(cbId)).thenReturn(aMock);
        when(aMock.getSentAt()).thenReturn(null);
        when(cbMock.getVorgabeMnet()).thenReturn(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
        when(aMock.getGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getKundenwunschtermin()).thenReturn(kwtMock);
        when(carrierElTALService.findCBVorgang(cbId)).thenReturn(cbMock);

        boolean adapted = cut.checkAndAdaptHvtToKvzBereitstellung(cbRefId, earliesSendDate, kuendigungsDate);

        assertTrue(adapted);

        verify(aMock).setEarliestSendDate(Date.from(earliesSendDate.atZone(ZoneId.systemDefault()).toInstant()));
        verify(kwtMock).setDatum(eq(DateConverterUtils.asLocalDate(kuendigungsDate)));
        verify(cbMock).setKlaerfallBemerkung(anyString());
    }

    @Test
    public void bereitstellungShouldBeCancelled() throws StoreException, ValidationException {
        final Long cbVorgangKueId = 1L;
        final Long cbVorgangNeuId = 2L;
        final WitaCBVorgang bereitstellung = new WitaCBVorgangBuilder().withId(cbVorgangNeuId).build();
        when(witaCbVorgangDao.findWitaCBVorgangByRefId(cbVorgangKueId)).thenReturn(bereitstellung);
        doNothing().when(cut).markWitaCBVorgangAsKlaerfall(eq(cbVorgangKueId), anyString());
        doReturn(bereitstellung).when(cut).doStorno(eq(cbVorgangNeuId), (AKUser) isNull());

        final boolean result = cut.checkHvtKueAndCancelKvzBereitstellung(cbVorgangKueId);

        assertTrue(result);
        verify(witaCbVorgangDao).findWitaCBVorgangByRefId(cbVorgangKueId);
        verify(cut).markWitaCBVorgangAsKlaerfall(eq(cbVorgangKueId), anyString());
        verify(cut).doStorno(eq(cbVorgangNeuId), (AKUser) isNull());
    }

    @Test
    public void bereitstellungShouldNotBeCancelled() throws StoreException, ValidationException {
        Long cbVorgangKueId = 1L;
        when(witaCbVorgangDao.findWitaCBVorgangByRefId(cbVorgangKueId)).thenReturn(null);

        final boolean result = cut.checkHvtKueAndCancelKvzBereitstellung(cbVorgangKueId);

        assertFalse(result);
        verify(witaCbVorgangDao).findWitaCBVorgangByRefId(cbVorgangKueId);
        verify(cut, never()).markWitaCBVorgangAsKlaerfall(eq(2L), anyString());
        verify(cut, never()).doStorno(eq(cbVorgangKueId), (AKUser) isNull());
    }

}
