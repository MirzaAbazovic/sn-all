/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2012 13:16:42
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.dao.cc.AuftragAktionDAO;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusServiceHelper;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = { BaseTest.UNIT })
public class CCAuftragStatusServiceImplTest extends BaseTest {

    @Mock
    AuftragDatenDAO auftragDatenDAO;
    @Mock
    CCAuftragDAO auftragDAO;
    @Mock
    AuftragAktionDAO auftragAktionDAO;
    @Mock
    InnenauftragService innenauftragService;
    @Mock
    EndstellenService endstellenService;
    @Mock
    BAService baService;
    @Mock
    KundenService kundenService;
    @Mock
    private CCAuftragService ccAuftragService;
    @SuppressWarnings("unused")
    @Mock
    private CCRufnummernService ccRufnummernService; // dieser Mock wird gebraucht!
    @Mock
    private CarrierService carrierService;
    @Mock
    private CPSService cpsService;
    @Mock
    private AKUserService userService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private CCAuftragStatusServiceHelper auftragStatusServiceHelper;

    @InjectMocks
    @Spy
    private CCAuftragStatusServiceImpl sut;


    final long auftragId = 1234L;
    RangierungBuilder rangierungBuilder;
    EndstelleBuilder esBuilder;
    CarrierbestellungBuilder cbBuilder;
    ProduktBuilder produktBuilder;
    AuftragDatenBuilder adBuilder;
    AuftragTechnikBuilder atBuilder;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * wenn genau eine Carrierbestellung auf dem Auftrag ist, so wird diese autom. gekuendigt
     */
    public void testKuendigeAuftragUndPhysikCBwirdGekuendigt() throws Exception {
        setUpKuendigeAuftragUndPhysikAuftragTest();
        Date kuendigungsDatum = new Date();
        sut.kuendigeAuftragUndPhysik(auftragId, kuendigungsDatum, -1L);
        verify(carrierService).cbKuendigenNewTx(cbBuilder.get().getId());
        verify(auftragStatusServiceHelper).kuendigeAuftragReqNew(eq(auftragId), eq(kuendigungsDatum), any(AKUser.class));

    }

    /**
     * wenn mehrere Carrierbestellungen auf dem Auftrag sind, gibt es eine Warning und die CBs werden nicht
     * autom. gekuendigt
     */
    public void testKuendigeAuftragUndPhysikMitMehrenCbs() throws FindException, StoreException {
        setUpKuendigeAuftragUndPhysikAuftragTest();
        when(carrierService.findCBs4Endstelle(esBuilder.get().getId()))
                .thenReturn(ImmutableList.of(cbBuilder.get(), cbBuilder.get()));
        Date kuendigungsDatum = new Date();
        final AKWarnings warnings = sut.kuendigeAuftragUndPhysik(auftragId, kuendigungsDatum, -1L);
        assertTrue(warnings.isNotEmpty());
        verify(carrierService, never()).cbKuendigen(cbBuilder.get().getId());
        verify(auftragStatusServiceHelper).kuendigeAuftragReqNew(eq(auftragId), eq(kuendigungsDatum), any(AKUser.class));
    }

    /**
     * wenn keine Carrierbestellungen auf dem Auftrag sind, gibt es eine Warning und die CBs werden nicht
     * autom. gekuendigt
     */
    public void testKuendigeAuftragUndPhysikMitKeinerCb() throws FindException, StoreException {
        setUpKuendigeAuftragUndPhysikAuftragTest();
        when(carrierService.findCBs4Endstelle(esBuilder.get().getId()))
                .thenReturn(Collections.<Carrierbestellung>emptyList());
        Date kuendigungsDatum = new Date();
        final AKWarnings warnings = sut.kuendigeAuftragUndPhysik(auftragId, kuendigungsDatum, -1L);
        assertTrue(warnings.isNotEmpty());
        verify(carrierService, never()).cbKuendigen(cbBuilder.get().getId());
        verify(auftragStatusServiceHelper).kuendigeAuftragReqNew(eq(auftragId), eq(kuendigungsDatum), any(AKUser.class));
    }

    /**
     * die Rangierung des Auftrags wird freigegeben
     */
    public void testKuendigeAuftragUndPhysikRangierungWirdFreigegeben() throws Exception {
        setUpKuendigeAuftragUndPhysikAuftragTest();
        final Date kuendigungsDatum = new Date();
        sut.kuendigeAuftragUndPhysik(auftragId, kuendigungsDatum, -1L);
        verify(rangierungsService).rangierungFreigabebereit(esBuilder.get(), kuendigungsDatum);
        verify(auftragStatusServiceHelper).kuendigeAuftragReqNew(eq(auftragId), eq(kuendigungsDatum), any(AKUser.class));
    }

    private void setUpKuendigeAuftragUndPhysikAuftragTest() throws FindException {
        rangierungBuilder = new RangierungBuilder().withRandomId();
        esBuilder = new EndstelleBuilder()
                .withRandomId()
                .withRangierungBuilder(rangierungBuilder);
        cbBuilder = new CarrierbestellungBuilder().withRandomId();
        produktBuilder = new ProduktBuilder().withRandomId();
        adBuilder = new AuftragDatenBuilder()
                .withRandomId()
                .withAuftragId(auftragId)
                .withProdBuilder(produktBuilder);
        atBuilder = new AuftragTechnikBuilder()
                .withRandomId();

        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(adBuilder.get());
        when(ccAuftragService.findAuftragTechnikByAuftragIdTx(auftragId)).thenReturn(atBuilder.get());
        when(endstellenService.findEndstellen4Auftrag(auftragId)).thenReturn(ImmutableList.of(esBuilder.get()));
        when(carrierService.findCBs4Endstelle(esBuilder.get().getId())).thenReturn(ImmutableList.of(cbBuilder.get()));
    }

    /**
     * Auftragstati werden geaendert,
     */
    public void testKuendigeAuftragUndPhysikAuftragStatiWerdenGeaendert() throws Exception {
        setUpKuendigeAuftragUndPhysikAuftragTest();
        Date kuendigungsDatum = new Date();
        sut.kuendigeAuftragUndPhysik(auftragId, kuendigungsDatum, -1L);
        verify(auftragStatusServiceHelper).kuendigeAuftragReqNew(eq(auftragId), eq(kuendigungsDatum), any(AKUser.class));
    }


    @Test
    public void testCancelHurricanOrdersAndCreateBA() throws FindException, StoreException {
        AuftragDaten erfassung = new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.ERFASSUNG).setPersist(false).build();
        AuftragDaten storno = new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.STORNO).setPersist(false).build();
        AuftragDaten absage = new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.ABSAGE).setPersist(false).build();
        AuftragDaten techReal = new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG).setPersist(false).build();
        AuftragDaten inBetrieb = new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.IN_BETRIEB).setPersist(false).build();
        AuftragDaten aenderung = new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.AENDERUNG).setPersist(false).build();
        AuftragDaten kuendigung = new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.KUENDIGUNG_ERFASSEN).setPersist(false).build();

        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(Arrays.asList(
                erfassung, storno, absage, techReal, inBetrieb, aenderung, kuendigung));
        doReturn(new AKWarnings()).when(sut).modifyCancelDate(any(AuftragDaten.class), any(Date.class), any(AKUser.class));
        when(baService.createVerlauf(any(CreateVerlaufParameter.class)))
                .thenReturn(Pair.create(new Verlauf(), null))
                .thenReturn(Pair.create(null, null));

        AKWarnings result = sut.cancelHurricanOrdersAndCreateBA(1L, new Date(), new AKUser(), 1L);

        assertNotNull(result);
        assertTrue(result.isNotEmpty());
        assertTrue(result.getWarningsAsText().contains(String.format("Auftrag %s ist noch nicht in Betrieb", erfassung.getAuftragId())));
        assertTrue(result.getWarningsAsText().contains(String.format("Auftrag %s ist noch nicht in Betrieb", techReal.getAuftragId())));
        assertTrue(result.getWarningsAsText().contains(String.format("Auftrag %s ist in einem unerwarteten Bearbeitungszustand; Kuendigung nicht moeglich", storno.getAuftragId())));
        assertTrue(result.getWarningsAsText().contains(String.format("Auftrag %s ist in einem unerwarteten Bearbeitungszustand; Kuendigung nicht moeglich", absage.getAuftragId())));
        assertTrue(result.getWarningsAsText().contains(String.format("Es wurde kein Kündigungs-Bauauftrag für techn. Auftrag %s erstellt", aenderung.getAuftragId())));

        verify(sut).kuendigeAuftragUndPhysik(eq(inBetrieb.getAuftragId()), any(Date.class), anyLong());
        verify(sut).kuendigeAuftragUndPhysik(eq(aenderung.getAuftragId()), any(Date.class), anyLong());
        verify(baService, times(2)).createVerlauf(any(CreateVerlaufParameter.class));
        verify(sut, never()).kuendigeAuftragUndPhysik(eq(erfassung.getAuftragId()), any(Date.class), anyLong());
        verify(sut, never()).kuendigeAuftragUndPhysik(eq(storno.getAuftragId()), any(Date.class), anyLong());
        verify(sut, never()).kuendigeAuftragUndPhysik(eq(absage.getAuftragId()), any(Date.class), anyLong());
        verify(sut, never()).kuendigeAuftragUndPhysik(eq(techReal.getAuftragId()), any(Date.class), anyLong());
        verify(sut, never()).kuendigeAuftragUndPhysik(eq(kuendigung.getAuftragId()), any(Date.class), anyLong());
        verify(sut).modifyCancelDate(eq(kuendigung), any(Date.class), any(AKUser.class));
    }

    @DataProvider(name = "modifyCancelDateDP")
    public Object[][] modifyCancelDateDP() {
        Date fiveDays = Date.from(LocalDateTime.now().plusDays(5).atZone(ZoneId.systemDefault()).toInstant());
        Date tomorrow = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        VerlaufAbteilung vaFieldService = new VerlaufAbteilungBuilder().withAbteilungId(Abteilung.FIELD_SERVICE)
                .withVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF).setPersist(false).build();
        VerlaufAbteilung vaSTOnlineWorking = new VerlaufAbteilungBuilder().withAbteilungId(Abteilung.ST_ONLINE)
                .withVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG).setPersist(false).build();
        VerlaufAbteilung vaAM = new VerlaufAbteilungBuilder().withAbteilungId(Abteilung.AM)
                .withVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF).setPersist(false).build();

        return new Object[][]{
                { fiveDays, Arrays.asList(vaFieldService, vaAM), null },
                { tomorrow, Arrays.asList(vaFieldService, vaAM), "bisherige Datum zu nahe liegt" },
                { fiveDays, Arrays.asList(vaFieldService, vaAM, vaSTOnlineWorking), "der Bauauftrag von der Technik bereits in Bearbeitung" },
        };
    }


    @Test(dataProvider = "modifyCancelDateDP")
    public void testModifyCancelDate(Date currentRealDate, List<VerlaufAbteilung> verlaufAbteilungen,
            String expectedWarning) throws FindException, StoreException {
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomAuftragId()
                .withKuendigung(currentRealDate).setPersist(false).build();
        Verlauf bauauftrag = new VerlaufBuilder().withRandomId()
                .withRealisierungstermin(currentRealDate)
                .withVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_DISPO)
                .withAnlass(BAVerlaufAnlass.KUENDIGUNG).setPersist(false).build();

        when(baService.findActVerlauf4Auftrag(anyLong(), eq(false))).thenReturn(bauauftrag);
        when(baService.findVerlaufAbteilungen(anyLong())).thenReturn(verlaufAbteilungen);

        Date cancelDate = Date.from(LocalDateTime.now().plusDays(10).atZone(ZoneId.systemDefault()).toInstant());
        AKWarnings result = sut.modifyCancelDate(auftragDaten, cancelDate, new AKUser());
        assertNotNull(result);

        verify(auftragDatenDAO).store(auftragDaten);

        if (expectedWarning == null) {
            assertTrue(result.isEmpty());
            verify(baService).changeRealDate(eq(bauauftrag.getId()), eq(cancelDate), any(AKUser.class));
        }
        else {
            assertTrue(result.isNotEmpty());
            assertTrue(result.getWarningsAsText().contains(expectedWarning));
            verify(baService, never()).changeRealDate(eq(bauauftrag.getId()), eq(cancelDate), any(AKUser.class));
        }
    }


    @Test(expectedExceptions = IllegalStateException.class)
    public void testAuftragAbsageWhenInnenauftragHasOpenBudget() throws Exception {
        final Long auftragNoOrig = RandomTools.createLong();
        final Long auftragId = 1L;
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        auftragDaten.setAuftragId(auftragId);
        auftragDaten.setAuftragNoOrig(auftragNoOrig);

        when(ccAuftragService.findAuftragDatenByAuftragIdTx(any(Long.class))).thenReturn(auftragDaten);
        when(baService.findVerlaeufe4Auftrag(auftragDaten.getAuftragId())).thenReturn(null);
        when(endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId())).thenReturn(null);
        when(innenauftragService.hasOpenBudget(auftragId)).thenReturn(true);

        sut.performAuftragAbsagen(auftragDaten.getAuftragId(), -1L);
    }

    public void testAuftragAbsage() throws Exception {
        final Long auftragNoOrig = RandomTools.createLong();
        final Long auftragId = 1L;
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        auftragDaten.setAuftragId(auftragId);
        auftragDaten.setAuftragNoOrig(auftragNoOrig);

        when(ccAuftragService.findAuftragDatenByAuftragIdTx(any(Long.class))).thenReturn(auftragDaten);
        when(baService.findVerlaeufe4Auftrag(auftragDaten.getAuftragId())).thenReturn(null);
        when(endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId())).thenReturn(null);

        sut.performAuftragAbsagen(auftragDaten.getAuftragId(), -1L);

        assertTrue(NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.ABSAGE),
                "Auftragstatus setht nicht auf ABSAGE!");
    }

    public void testAuftragAbsageWithVerlauf() throws Exception {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        auftragDaten.setAuftragId(2L);

        Verlauf verlauf = new Verlauf();
        verlauf.setAkt(Boolean.TRUE);
        List<Verlauf> verlaeufe = new ArrayList<Verlauf>();
        verlaeufe.add(verlauf);

        when(ccAuftragService.findAuftragDatenByAuftragIdTx(any(Long.class))).thenReturn(auftragDaten);
        when(baService.findVerlaeufe4Auftrag(auftragDaten.getAuftragId())).thenReturn(verlaeufe);
        when(endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId())).thenReturn(null);

        sut.performAuftragAbsagen(auftragDaten.getAuftragId(), -1L);

        assertTrue(NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.ABSAGE),
                "Auftragstatus steht nicht auf ABSAGE!");
        assertTrue(NumberTools.equal(verlauf.getAnlass(), BAVerlaufAnlass.ABSAGE),
                "Verlaufsanlass steht nicht auf ABSAGE!");
        assertTrue(!verlauf.getAkt(), "Verlauf ist immer noch aktiv!");
    }

    @SuppressWarnings("unchecked")
    public void testAuftragAbsageWithSplitVerlauf() throws Exception {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        auftragDaten.setAuftragId(4L);

        Verlauf splitVerlauf = new Verlauf();
        splitVerlauf.setAuftragId(auftragDaten.getAuftragId());

        Verlauf verlauf = new Verlauf();
        verlauf.setAkt(Boolean.TRUE);
        verlauf.setAuftragId(3L);
        Set<Long> subAuftragsIds = new HashSet<Long>();
        subAuftragsIds.add(splitVerlauf.getAuftragId());
        verlauf.setSubAuftragsIds(subAuftragsIds);
        List<Verlauf> verlaeufe = new ArrayList<Verlauf>();
        verlaeufe.add(verlauf);

        List<Verlauf> splitVerlaeufe = new ArrayList<Verlauf>();
        splitVerlaeufe.add(splitVerlauf);

        when(ccAuftragService.findAuftragDatenByAuftragIdTx(any(Long.class))).thenReturn(auftragDaten);
        when(baService.findVerlaeufe4Auftrag(auftragDaten.getAuftragId())).thenReturn(verlaeufe);
        when(endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId())).thenReturn(null);
        when(baService.splitVerlauf(eq(verlauf), any(Set.class), any(Long.class))).thenReturn(splitVerlaeufe);

        sut.performAuftragAbsagen(auftragDaten.getAuftragId(), -1L);

        assertTrue(NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.ABSAGE),
                "Auftragstatus setht nicht auf ABSAGE!");
        assertTrue(NumberTools.equal(splitVerlauf.getAnlass(), BAVerlaufAnlass.ABSAGE),
                "Verlaufsanlass setht nicht auf ABSAGE!");
        assertFalse(splitVerlauf.getAkt(), "Verlauf ist immer noch aktiv!");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCheckAuftragAbsagenMitNichtGekuendigterCb() throws FindException {
        final Long auftragId = 5L;
        final AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(auftragId);
        auftragDaten.setStatusId(AuftragStatus.ERFASSUNG);

        Carrierbestellung cb = new Carrierbestellung();

        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(new Endstelle());
        when(carrierService.findLastCB4Endstelle(any(Long.class))).thenReturn(cb);

        sut.checkAuftragAbsagen(auftragId);
    }

    public void testCheckAuftragAbsagenMitGekuendigterCb() throws FindException {
        final Long auftragId = 6L;
        final AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(auftragId);
        auftragDaten.setStatusId(AuftragStatus.ERFASSUNG);

        Carrierbestellung cb = new Carrierbestellung();
        cb.setKuendBestaetigungCarrier(new Date());

        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(new Endstelle());
        when(carrierService.findLastCB4Endstelle(any(Long.class))).thenReturn(cb);

        sut.checkAuftragAbsagen(auftragId);
    }

    public void testCheckAuftragAbsagenOhneCb() throws FindException {
        final Long auftragId = 7L;
        final AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(auftragId);
        auftragDaten.setStatusId(AuftragStatus.ERFASSUNG);

        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(new Endstelle());
        when(carrierService.findLastCB4Endstelle(any(Long.class))).thenReturn(null);

        sut.checkAuftragAbsagen(auftragId);
    }

    public void testSendAbsageToCpsWhenNoCpsTxExists() throws Exception {
        final CCAuftragStatusService.CpsTxInfosForAuftragAbsage txInfoForAbsage =
                new CCAuftragStatusService.CpsTxInfosForAuftragAbsage(CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType.NONE, null, null);
        sut.sendAbsageToCps(-1L, txInfoForAbsage);

        verify(cpsService, never()).sendCpsTx2CPSAsyncWithoutNewTx(any(CPSTransaction.class), anyLong());
    }

    @DataProvider
    Object[][] testSendAbsageToCpsModifyOrDeleteDataProvider() {
        return new Object[][] {
                { CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType.CANCEL_SUB, CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB },
                { CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType.MODIFY_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB },
        };
    }

    @Test(dataProvider = "testSendAbsageToCpsModifyOrDeleteDataProvider")
    public void testSendAbsageToCpsModifyOrDelete(
            final CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType cpsTxType,
            final Long soTypeExpected) throws Exception {
        final Date date = new Date();
        final CCAuftragStatusService.CpsTxInfosForAuftragAbsage txInfo =
                new CCAuftragStatusService.CpsTxInfosForAuftragAbsage(cpsTxType, date, 123456L);

        final CPSTransactionResult cpsTxResult = new CPSTransactionResult();
        cpsTxResult.setCpsTransactions(Lists.newArrayList(new CPSTransactionBuilder().withRandomId().build()));

        when(cpsService.createCPSTransaction(any(CreateCPSTransactionParameter.class))).thenReturn(cpsTxResult);

        final long sessionId = 1234123L;

        sut.sendAbsageToCps(sessionId, txInfo);

        final ArgumentCaptor<CreateCPSTransactionParameter> cpsTxParamsCaptor = ArgumentCaptor.forClass(CreateCPSTransactionParameter.class);
        verify(cpsService).createCPSTransaction(cpsTxParamsCaptor.capture());
        verify(cpsService).sendCpsTx2CPSAsyncWithoutNewTx(eq(cpsTxResult.getCpsTransactions().get(0)), eq(sessionId));

        final CreateCPSTransactionParameter cpsTxParams = cpsTxParamsCaptor.getValue();
        assertThat(cpsTxParams.getAuftragId(), equalTo(txInfo.getAuftragId()));
        assertThat(cpsTxParams.getSessionId(), equalTo(sessionId));
        assertThat(cpsTxParams.getServiceOrderType(), equalTo(soTypeExpected));
        assertThat(DateTools.isDateEqual(cpsTxParams.getEstimatedExecTime(), date), equalTo(true));
    }


    @Test(expectedExceptions = RuntimeException.class)
    public void testSendAbsageToCpsWithCpsPreparingFailure() throws Exception {
        final CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType cpsTxType =
                CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType.CANCEL_SUB;
        final Date date = new Date();
        final CCAuftragStatusService.CpsTxInfosForAuftragAbsage txInfo =
                new CCAuftragStatusService.CpsTxInfosForAuftragAbsage(cpsTxType, date, 81293827391L);

        final CPSTransactionResult cpsTxResult = new CPSTransactionResult();
        cpsTxResult.setWarnings(new AKWarnings().addAKWarning(this, "test"));
        when(cpsService.createCPSTransaction(any(CreateCPSTransactionParameter.class))).thenReturn(cpsTxResult);

        sut.sendAbsageToCps(1234123L, txInfo);
    }


    public void testDetermineCpsTxInfosForAuftragAbsageWithoutCreateSubTx() throws FindException {
        final Long auftragId = 8L;
        when(cpsService.findSuccessfulCPSTransaction4TechOrder(auftragId))
                .thenReturn(Collections.<CPSTransaction>emptyList());

        final CCAuftragStatusService.CpsTxInfosForAuftragAbsage cpsTxInfos =
                sut.determineCpsTxInfosForAuftragAbsage(auftragId);

        assertEquals(cpsTxInfos.getCpsTxType(), CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType.NONE);
    }

    public void testDetermineCpsTxInfosForAuftragAbsageWithCreateSubTx() throws FindException {
        final Long auftragId = 9L;
        final Date execDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        final CPSTransaction cpsTx = new CPSTransaction();
        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        cpsTx.setEstimatedExecTime(execDate);

        when(cpsService.findSuccessfulCPSTransaction4TechOrder(auftragId))
                .thenReturn(Lists.newArrayList(cpsTx));
        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId))
                .thenReturn(new AuftragDaten());
        when(ccAuftragService.findAuftragDaten4OrderNoOrigTx(any(Long.class)))
                .thenReturn(Collections.<AuftragDaten>emptyList());

        final CCAuftragStatusService.CpsTxInfosForAuftragAbsage cpsTxInfos =
                sut.determineCpsTxInfosForAuftragAbsage(auftragId);

        assertEquals(cpsTxInfos.getCpsTxType(), CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType.CANCEL_SUB);
        assertEquals(cpsTxInfos.getExecutionDateForAbsage(), execDate);
    }

    public void testDetermineCpsTxInfosForAuftragAbsageWithMultipleOrders() throws FindException {
        final Long auftragId = 10L;

        final CPSTransaction cpsTx = new CPSTransaction();
        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        cpsTx.setAuftragId(4312L);

        when(cpsService.findSuccessfulCPSTransaction4TechOrder(auftragId))
                .thenReturn(Lists.newArrayList(cpsTx));
        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId))
                .thenReturn(new AuftragDaten());
        when(ccAuftragService.findAuftragDaten4OrderNoOrigTx(any(Long.class)))
                .thenReturn(Lists.newArrayList(new AuftragDaten(), new AuftragDaten()));

        final CCAuftragStatusService.CpsTxInfosForAuftragAbsage cpsTxInfos =
                sut.determineCpsTxInfosForAuftragAbsage(auftragId);

        assertEquals(cpsTxInfos.getCpsTxType(), CCAuftragStatusService.CpsTxInfosForAuftragAbsage.CpsTxType.MODIFY_SUB);
        assertThat(cpsTxInfos.getAuftragId(), equalTo(cpsTx.getAuftragId()));
    }

    public void testKuendigeCarrierBestellungNoCbDtag() throws FindException {
        Carrier dtag = new CarrierBuilder().setPersist(false).withId(Carrier.ID_DTAG).build();
        when(carrierService.findCBs4Endstelle(anyLong())).thenReturn(null);
        when(carrierService.findCarrier4HVT(anyLong())).thenReturn(dtag);

        AKWarnings warnings = new AKWarnings();
        sut.kuendigeCarrierBestellung(1L, warnings, new EndstelleBuilder().setPersist(false).build());

        Assert.assertTrue(warnings.isNotEmpty());
    }


    public void testKuendigeCarrierBestellungNoCbMnet() throws FindException {
        Carrier dtag = new CarrierBuilder().setPersist(false).withId(Carrier.ID_AKOM).build();
        when(carrierService.findCBs4Endstelle(anyLong())).thenReturn(null);
        when(carrierService.findCarrier4HVT(anyLong())).thenReturn(dtag);

        AKWarnings warnings = new AKWarnings();
        sut.kuendigeCarrierBestellung(1L, warnings, new EndstelleBuilder().setPersist(false).build());

        Assert.assertTrue(warnings.isEmpty());
    }

}
