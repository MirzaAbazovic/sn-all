/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2011 08:14:09
 */
package de.mnet.wita.service.impl;

import static de.mnet.wita.AbmMeldungsCode.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Iterables;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.DNTNB;
import de.augustakom.hurrican.model.billing.DNTNBBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangSubOrderBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.CarrierKennungBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.wita.aggregator.KundeAggregator;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.SchaltungKvzTal;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaVorabstimmungService;

/**
 * TestNG Klasse fuer {@link KundeAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class WitaDataServiceTest extends BaseTest {

    @Spy
    @InjectMocks
    private final WitaDataService cut = new WitaDataService();

    @Mock
    private EndstellenService endstellenServiceMock;
    @Mock
    private CarrierService carrierServiceMock;
    @Mock
    private RangierungsService rangierungsServiceMock;
    @Mock
    private RufnummerService rufnummerService;
    @Mock
    private WitaVorabstimmungService witaVorabstimmungService;
    @Mock
    private FeatureService featureService;

    private WitaCBVorgang cbVorgang;
    private Carrierbestellung carrierbestellung;

    private Endstelle endstelle1;
    private Equipment dtagEquipment1;
    private Rangierung rangierung1;

    private Endstelle endstelle2;
    private Rangierung rangierung2;
    private Equipment dtagEquipment2;
    private final Set<Long> rufnummerIds = new HashSet<Long>();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Carrierbestellung2EndstelleBuilder cb2EsBuilder = new Carrierbestellung2EndstelleBuilder().withRandomId()
                .setPersist(false);
        carrierbestellung = new CarrierbestellungBuilder().withCb2EsBuilder(cb2EsBuilder).setPersist(false).build();
        carrierbestellung.setId(13L);

        rufnummerIds.add(new Long(12345L));

        cbVorgang = new WitaCBVorgangBuilder().withRufnummerIds(rufnummerIds).setPersist(false).build();
        cbVorgang.setCbId(carrierbestellung.getId());

        CarrierKennungBuilder carrierKennungBuilder = new CarrierKennungBuilder().withRandomId().setPersist(false);

        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withRandomId()
                .withCarrierKennungId(carrierKennungBuilder.get().getId()).setPersist(false);

        EquipmentBuilder dtagEquipmentBuilder1 = new EquipmentBuilder().withRandomId()
                .withCarrier(Carrier.CARRIER_DTAG).withRangSSType(Equipment.RANG_SS_2H).withRangBucht("0101")
                .withRangLeiste1("01").withRangStift1("02").setPersist(false);
        dtagEquipment1 = dtagEquipmentBuilder1.build();

        RangierungBuilder rangierungBuilder1 = new RangierungBuilder().withRandomId()
                .withEqOutBuilder(dtagEquipmentBuilder1).withEqInBuilder(new EquipmentBuilder().setPersist(false))
                .setPersist(false);
        rangierung1 = rangierungBuilder1.build();

        endstelle1 = new EndstelleBuilder().withRangierungBuilder(rangierungBuilder1).withCb2EsBuilder(cb2EsBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder).setPersist(false).build();

        EquipmentBuilder dtagEquipmentBuilder2 = new EquipmentBuilder().withRandomId()
                .withCarrier(Carrier.CARRIER_DTAG).withRangSSType(Equipment.RANG_SS_2H).withRangBucht("0101")
                .withRangLeiste1("01").withRangStift1("03").setPersist(false);
        dtagEquipment2 = dtagEquipmentBuilder2.build();
        RangierungBuilder rangierungBuilder2 = new RangierungBuilder().withRandomId()
                .withEqOutBuilder(dtagEquipmentBuilder2).withEqInBuilder(new EquipmentBuilder().setPersist(false))
                .setPersist(false);
        rangierung2 = rangierungBuilder2.build();

        endstelle2 = new EndstelleBuilder().withRangierungBuilder(rangierungBuilder2).withCb2EsBuilder(cb2EsBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder).setPersist(false).build();
    }

    public void testLoadEndstellenWithOne() throws FindException {
        Endstelle esA = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).setPersist(false).build();

        when(carrierServiceMock.findCB(cbVorgang.getCbId())).thenReturn(carrierbestellung);
        when(endstellenServiceMock.findEndstellen4Auftrag(cbVorgang.getAuftragId())).thenReturn(
                Arrays.asList(esA, endstelle1));

        List<Endstelle> endstellen = cut.loadEndstellen(cbVorgang);
        assertThat(endstellen, hasSize(1));
        assertEquals(endstelle1, endstellen.get(0), "Ermittelte Endstelle nicht wie erwartet!");

        verify(carrierServiceMock).findCB(cbVorgang.getCbId());
        verify(endstellenServiceMock).findEndstellen4Auftrag(cbVorgang.getAuftragId());
    }

    public void testLoadEndstellenWithTwo() throws FindException {
        Endstelle esA1 = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).setPersist(false).build();
        Endstelle esA2 = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).setPersist(false).build();

        when(carrierServiceMock.findCB(cbVorgang.getCbId())).thenReturn(carrierbestellung);
        when(endstellenServiceMock.findEndstellen4Auftrag(cbVorgang.getAuftragId())).thenReturn(
                Arrays.asList(esA1, endstelle1));

        CBVorgangSubOrder subOrder = (new CBVorgangSubOrderBuilder()).withAuftragId(123456789L).build();
        cbVorgang.setSubOrders(new HashSet<CBVorgangSubOrder>(Arrays.asList(subOrder)));
        cbVorgang.setVierDraht(Boolean.TRUE);

        when(endstellenServiceMock.findEndstellen4Auftrag(123456789L)).thenReturn(Arrays.asList(esA2, endstelle2));

        List<Endstelle> endstellen = cut.loadEndstellen(cbVorgang);
        assertThat(endstellen, hasSize(2));
        assertSame(endstellen.get(0), endstelle1);
        assertSame(endstellen.get(1), endstelle2);

        verify(carrierServiceMock).findCB(cbVorgang.getCbId());
        verify(endstellenServiceMock, times(2)).findEndstellen4Auftrag(any(Long.class));
    }

    public void testLoadEndstellenWithOne2() throws FindException {
        Carrierbestellung2EndstelleBuilder cb2EsBuilder = new Carrierbestellung2EndstelleBuilder().withRandomId()
                .setPersist(false);
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withCb2EsBuilder(cb2EsBuilder)
                .setPersist(false).build();

        Endstelle esA = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).setPersist(false).build();
        Endstelle esB = new EndstelleBuilder().withCb2EsBuilder(cb2EsBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).setPersist(false).build();

        when(carrierServiceMock.findCB(cbVorgang.getCbId())).thenReturn(carrierbestellung);
        when(endstellenServiceMock.findEndstellen4Auftrag(cbVorgang.getAuftragId()))
                .thenReturn(Arrays.asList(esA, esB));

        List<Endstelle> endstellen = cut.loadEndstellen(cbVorgang);
        assertEquals(Iterables.getOnlyElement(endstellen), esB, "Ermittelte Endstelle nicht wie erwartet!");

        verify(carrierServiceMock).findCB(cbVorgang.getCbId());
        verify(endstellenServiceMock).findEndstellen4Auftrag(cbVorgang.getAuftragId());
    }

    public void testLoadEndstellenWithTwo2() throws FindException {
        Carrierbestellung2EndstelleBuilder cb2EsBuilder = new Carrierbestellung2EndstelleBuilder().withRandomId()
                .setPersist(false);
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withCb2EsBuilder(cb2EsBuilder)
                .setPersist(false).build();

        Endstelle esA1 = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).setPersist(false).build();
        Endstelle esB1 = new EndstelleBuilder().withCb2EsBuilder(cb2EsBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).setPersist(false).build();

        Endstelle esA2 = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).setPersist(false).build();
        Endstelle esB2 = new EndstelleBuilder().withCb2EsBuilder(cb2EsBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).setPersist(false).build();

        when(carrierServiceMock.findCB(cbVorgang.getCbId())).thenReturn(carrierbestellung);
        when(endstellenServiceMock.findEndstellen4Auftrag(cbVorgang.getAuftragId())).thenReturn(
                Arrays.asList(esA1, esB1));

        CBVorgangSubOrder subOrder = (new CBVorgangSubOrderBuilder()).withAuftragId(123456789L).build();
        cbVorgang.setSubOrders(new HashSet<CBVorgangSubOrder>(Arrays.asList(subOrder)));
        cbVorgang.setVierDraht(Boolean.TRUE);

        when(endstellenServiceMock.findEndstellen4Auftrag(123456789L)).thenReturn(Arrays.asList(esA2, esB2));

        List<Endstelle> endstellen = cut.loadEndstellen(cbVorgang);
        assertThat(endstellen, hasSize(2));
        assertSame(endstellen.get(0), esB1);
        assertSame(endstellen.get(1), esB2);

        verify(carrierServiceMock).findCB(cbVorgang.getCbId());
        verify(endstellenServiceMock, times(2)).findEndstellen4Auftrag(any(Long.class));
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void testLoadEndstellenForRexMk() throws FindException {
        cbVorgang.setCbId(null);
        Endstelle esB = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).setPersist(false).build();

        when(endstellenServiceMock.findEndstelle4Auftrag(cbVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(esB);

        cut.loadEndstellen(cbVorgang);
    }

    public void testLoadDtagEquipmentsWithOne() throws FindException {

        doReturn(Arrays.asList(rangierung1)).when(cut).loadRangierungen(cbVorgang);
        when(rangierungsServiceMock.findEquipment(rangierung1.getEqOutId())).thenReturn(dtagEquipment1);

        List<Equipment> result = cut.loadDtagEquipments(cbVorgang);
        assertThat(result, hasSize(1));
        assertEquals(result.get(0).getId(), dtagEquipment1.getId());
    }

    public void testLoadDtagEquipmentsWithTwo() throws FindException {
        doReturn(Arrays.asList(rangierung1, rangierung2)).when(cut).loadRangierungen(cbVorgang);
        when(rangierungsServiceMock.findEquipment(rangierung1.getEqOutId())).thenReturn(dtagEquipment1);
        when(rangierungsServiceMock.findEquipment(rangierung2.getEqOutId())).thenReturn(dtagEquipment2);

        List<Equipment> result = cut.loadDtagEquipments(cbVorgang);
        assertThat(result, hasSize(2));
        assertEquals(result.get(0).getId(), dtagEquipment1.getId());
        assertEquals(result.get(1).getId(), dtagEquipment2.getId());
    }

    @Test(expectedExceptions = { WitaDataAggregationException.class })
    public void loadEquipmentExpectExceptionBecauseOfMissingRangierung() {
        doReturn(new ArrayList<Rangierung>()).when(cut).loadRangierungen(cbVorgang);

        cut.loadDtagEquipments(cbVorgang);
    }

    public void isValidDtagEquipmentExpectTrue() {
        Equipment validDtagEq = new EquipmentBuilder().withRandomId().withCarrier(Carrier.CARRIER_DTAG)
                .withRangSSType(Equipment.RANG_SS_2H).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02")
                .setPersist(false).build();
        assertTrue(cut.isValidDtagEquipment(validDtagEq));
    }

    public void isValidDtagEquipmentExpectFalseBecauseOfMissingCarrier() {
        Equipment validDtagEq = new EquipmentBuilder().withRandomId().withRangSSType(Equipment.RANG_SS_2H)
                .withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").setPersist(false).build();
        assertFalse(cut.isValidDtagEquipment(validDtagEq));
    }

    public void isValidDtagEquipmentExpectFalseBecauseOfMissingRangBucht() {
        Equipment validDtagEq = new EquipmentBuilder().withRandomId().withCarrier(Carrier.CARRIER_DTAG)
                .withRangSSType(Equipment.RANG_SS_2H).withRangLeiste1("01").withRangStift1("02").setPersist(false)
                .build();
        assertFalse(cut.isValidDtagEquipment(validDtagEq));
    }

    public void isValidDtagEquipmentExpectFalseBecauseOfMissingRangLeiste() {
        Equipment validDtagEq = new EquipmentBuilder().withRandomId().withCarrier(Carrier.CARRIER_DTAG)
                .withRangSSType(Equipment.RANG_SS_2H).withRangBucht("0101").withRangStift1("02").setPersist(false)
                .build();
        assertFalse(cut.isValidDtagEquipment(validDtagEq));
    }

    public void isValidDtagEquipmentExpectFalseBecauseOfMissingRangStift() {
        Equipment validDtagEq = new EquipmentBuilder().withRandomId().withCarrier(Carrier.CARRIER_DTAG)
                .withRangSSType(Equipment.RANG_SS_2H).withRangBucht("0101").withRangLeiste1("02").setPersist(false)
                .build();
        assertFalse(cut.isValidDtagEquipment(validDtagEq));
    }

    public void isValidDtagEquipmentExpectFalseBecauseOfMissingRangSsType() {
        Equipment validDtagEq = new EquipmentBuilder().withRandomId().withCarrier(Carrier.CARRIER_DTAG)
                .withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").setPersist(false).build();
        assertFalse(cut.isValidDtagEquipment(validDtagEq));
    }

    public void testLoadRangierungenWithOne() throws FindException {
        doReturn(Arrays.asList(endstelle1)).when(cut).loadEndstellen(cbVorgang);
        when(rangierungsServiceMock.findRangierung(endstelle1.getRangierId())).thenReturn(rangierung1);

        List<Rangierung> rangierungen = cut.loadRangierungen(cbVorgang);
        assertThat(rangierungen, hasSize(1));
        assertEquals(rangierungen.get(0).getEqOutId(), dtagEquipment1.getId());
    }

    public void testLoadRangierungenWithTwo() throws FindException {
        doReturn(Arrays.asList(endstelle1, endstelle2)).when(cut).loadEndstellen(cbVorgang);
        when(rangierungsServiceMock.findRangierung(endstelle1.getRangierId())).thenReturn(rangierung1);
        when(rangierungsServiceMock.findRangierung(endstelle2.getRangierId())).thenReturn(rangierung2);

        List<Rangierung> rangierungen = cut.loadRangierungen(cbVorgang);
        assertThat(rangierungen, hasSize(2));
        assertEquals(rangierungen.get(0).getEqOutId(), dtagEquipment1.getId());
        assertEquals(rangierungen.get(1).getEqOutId(), dtagEquipment2.getId());
    }

    public void createSchaltungsList() {
        Equipment eq = new EquipmentBuilder().withUETV(Uebertragungsverfahren.H13).withRangBucht("0202")
                .withRangLeiste1("09").withRangStift1("100").setPersist(false).build();
        SchaltungKupfer result = cut.createSchaltungKupferFor(eq, eq.getUetv());
        assertNotNull(result, "SchaltungKupfer wurde nicht erstellt.");
        assertEquals(result.getUEVT(), eq.getRangBucht());
        assertEquals(result.getEVS(), eq.getRangLeiste1());
        assertEquals(result.getDoppelader(), "00");
        assertEquals(result.getUebertragungsverfahren(), de.mnet.wita.message.common.Uebertragungsverfahren.H13);
    }

    public void getDtagUebertragungsverfahrenH13() {
        de.mnet.wita.message.common.Uebertragungsverfahren result = cut
                .getDtagUebertragungsverfahren(Uebertragungsverfahren.H13);
        assertEquals(result, de.mnet.wita.message.common.Uebertragungsverfahren.H13);
    }

    public void getDtagUebertragungsverfahrenN01() {
        de.mnet.wita.message.common.Uebertragungsverfahren result = cut
                .getDtagUebertragungsverfahren(Uebertragungsverfahren.N01);
        assertTrue(result == null);
    }

    public void testLoadEinzelAnschluss() throws Exception {
        Rufnummer rufnummer = new Rufnummer();
        rufnummer.setDnNoOrig(new Long(12345L));
        rufnummer.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rufnummer.setRealDate(cbVorgang.getVorgabeMnet());
        rufnummer.setOnKz("089");
        rufnummer.setDnBase("123456");
        rufnummer.setActCarrierDnTnb(new DNTNBBuilder().withTnb(TNB.MNET.carrierNameUC).setPersist(false).build());
        testLoadRufnummer(rufnummer);
    }

    public void testLoadAnlagenAnschluss() throws Exception {
        Rufnummer rufnummer = new Rufnummer();
        rufnummer.setDnNoOrig(new Long(12345L));
        rufnummer.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rufnummer.setRealDate(cbVorgang.getVorgabeMnet());
        rufnummer.setOnKz("089");
        rufnummer.setDnBase("13456");
        rufnummer.setDirectDial("1234");
        rufnummer.setRangeFrom("100");
        rufnummer.setRangeTo("299");
        rufnummer.setActCarrierDnTnb(new DNTNBBuilder().withTnb(TNB.NEFKOM.carrierNameUC).setPersist(false).build());
        testLoadRufnummer(rufnummer);
    }

    private void testLoadRufnummer(Rufnummer rufnummer) throws Exception {
        long dnNoOrig = 12345L;
        when(rufnummerService.findLastRN(dnNoOrig)).thenReturn(rufnummer);

        assertThat(Iterables.getOnlyElement(cut.loadRufnummern(cbVorgang)), equalTo(rufnummer));
    }

    public void testLoadVorabstimmung() {
        Carrierbestellung cb = new CarrierbestellungBuilder().setPersist(false).build();
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();
        Vorabstimmung vorabstimmung = new VorabstimmungBuilder().setPersist(false).build();
        doReturn(cb).when(cut).loadCarrierbestellung(cbVorgang);
        doReturn(endstelle).when(cut).loadEndstelle(cbVorgang.getAuftragId(), cb);
        doReturn(vorabstimmung).when(witaVorabstimmungService).findVorabstimmung(endstelle, cbVorgang.getAuftragId());

        assertNotNull(cut.loadVorabstimmung(cbVorgang));

        verify(witaVorabstimmungService).findVorabstimmung(endstelle, cbVorgang.getAuftragId());
        verifyNoMoreInteractions(witaVorabstimmungService);
    }

    public void testLoadVorabstimmungForKlammer() {
        Carrierbestellung cb = new CarrierbestellungBuilder().setPersist(false).build();
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();
        Vorabstimmung vorabstimmung = new VorabstimmungBuilder().setPersist(false).build();

        cbVorgang.setAuftragsKlammer(Long.MAX_VALUE);

        doReturn(cb).when(cut).loadCarrierbestellung(cbVorgang);
        doReturn(endstelle).when(cut).loadEndstelle(cbVorgang.getAuftragId(), cb);
        doReturn(null).when(witaVorabstimmungService).findVorabstimmung(endstelle, cbVorgang.getAuftragId());
        doReturn(Arrays.asList(vorabstimmung)).when(witaVorabstimmungService).findVorabstimmungForAuftragsKlammer(
                cbVorgang.getAuftragsKlammer(), endstelle);

        assertNotNull(cut.loadVorabstimmung(cbVorgang));
        verify(witaVorabstimmungService).findVorabstimmung(endstelle, cbVorgang.getAuftragId());
        verify(witaVorabstimmungService).findVorabstimmungForAuftragsKlammer(cbVorgang.getAuftragsKlammer(), endstelle);
        verifyNoMoreInteractions(witaVorabstimmungService);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class, expectedExceptionsMessageRegExp = "Für die Auftragsklammer wurden.*")
    public void testLoadVorabstimmungForKlammerMultipleResultsExpectError() {
        Carrierbestellung cb = new CarrierbestellungBuilder().setPersist(false).build();
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();
        Vorabstimmung vorabstimmung = new VorabstimmungBuilder().setPersist(false).build();
        Vorabstimmung vorabstimmung2 = new VorabstimmungBuilder().setPersist(false).build();

        cbVorgang.setAuftragsKlammer(Long.MAX_VALUE);

        doReturn(cb).when(cut).loadCarrierbestellung(cbVorgang);
        doReturn(endstelle).when(cut).loadEndstelle(cbVorgang.getAuftragId(), cb);
        doReturn(null).when(witaVorabstimmungService).findVorabstimmung(endstelle, cbVorgang.getAuftragId());
        doReturn(Arrays.asList(vorabstimmung, vorabstimmung2)).when(witaVorabstimmungService)
                .findVorabstimmungForAuftragsKlammer(cbVorgang.getAuftragsKlammer(), endstelle);

        cut.loadVorabstimmung(cbVorgang);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class, expectedExceptionsMessageRegExp = "Konnte keine Provider-Daten.*")
    public void testLoadVorabstimmungForKlammerNoResultExpectError() {
        Carrierbestellung cb = new CarrierbestellungBuilder().setPersist(false).build();
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();

        cbVorgang.setAuftragsKlammer(Long.MAX_VALUE);

        doReturn(cb).when(cut).loadCarrierbestellung(cbVorgang);
        doReturn(endstelle).when(cut).loadEndstelle(cbVorgang.getAuftragId(), cb);
        doReturn(null).when(witaVorabstimmungService).findVorabstimmung(endstelle, cbVorgang.getAuftragId());
        doReturn(null).when(witaVorabstimmungService).findVorabstimmungForAuftragsKlammer(
                cbVorgang.getAuftragsKlammer(), endstelle);

        cut.loadVorabstimmung(cbVorgang);
    }

    public void testLoadVorabstimmungForRexMk() {
        cbVorgang.setCbId(null);
        Vorabstimmung vorabstimmung = new VorabstimmungBuilder().setPersist(false).build();
        doReturn(vorabstimmung).when(witaVorabstimmungService).findVorabstimmungForRexMk(cbVorgang.getAuftragId());

        assertNotNull(cut.loadVorabstimmung(cbVorgang));

        verify(witaVorabstimmungService).findVorabstimmungForRexMk(cbVorgang.getAuftragId());
        verifyNoMoreInteractions(witaVorabstimmungService);
    }

    public void testLoadCarrierKennungForRexMk_Feature_Off() throws FindException {
        Rufnummer dn1 = new RufnummerBuilder().setPersist(false).withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung).build();
        Rufnummer dn2 = new RufnummerBuilder().setPersist(false).withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung).build();

        doReturn(Arrays.asList(dn1, dn2)).when(cut).loadRufnummern(cbVorgang);

        DNTNB dnTnb = new DNTNB();
        dnTnb.setPortKennung(TNB.AKOM.tnbKennung);
        when(rufnummerService.findTNB(TNB.AKOM.carrierName)).thenReturn(dnTnb);
        when(carrierServiceMock.findCarrierKennung(dnTnb.getPortKennung())).thenReturn(
                new CarrierKennungBuilder().withPortierungsKennung(dnTnb.getPortKennung()).setPersist(false).build());
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)).thenReturn(Boolean.FALSE);

        CarrierKennung result = cut.loadCarrierKennungForRexMk(cbVorgang);

        assertNotNull(result);
        assertThat(result.getPortierungsKennung(), equalTo(dnTnb.getPortKennung()));
    }

    public void testLoadCarrierKennungForRexMk_Feature_On_ONKZ() throws FindException {
        final String onKz = "089";
        Rufnummer dn1 = new RufnummerBuilder().setPersist(false).withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung).withOnKz(onKz).build();
        Rufnummer dn2 = new RufnummerBuilder().setPersist(false).withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung).build();

        doReturn(Arrays.asList(dn1, dn2)).when(cut).loadRufnummern(cbVorgang);

        DNTNB dnTnb = new DNTNB();
        dnTnb.setPortKennung(TNB.AKOM.tnbKennung);
        when(rufnummerService.findTNB(TNB.AKOM.carrierName)).thenReturn(dnTnb);
        when(carrierServiceMock.findCarrierKennung(dnTnb.getPortKennung())).thenReturn(
                new CarrierKennungBuilder().withPortierungsKennung(dnTnb.getPortKennung()).setPersist(false).build());
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)).thenReturn(Boolean.TRUE);
        when(rufnummerService.findTnbKennung4Onkz(onKz)).thenReturn("D043");
        when(rufnummerService.findTNB(anyString())).thenReturn(dnTnb);

        CarrierKennung result = cut.loadCarrierKennungForRexMk(cbVorgang);

        assertNotNull(result);
        assertThat(result.getPortierungsKennung(), equalTo(dnTnb.getPortKennung()));
    }

    @Test(expectedExceptions = WitaDataAggregationException.class, expectedExceptionsMessageRegExp = "Aufnehmender .*Carrier konnte für die REX-MK Rufnummern nicht.*")
    public void testLoadCarrierKennungForRexMkMultipleActCarriers_Feature_Off() {
        Rufnummer dn1 = new RufnummerBuilder().setPersist(false).withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung).build();
        Rufnummer dn2 = new RufnummerBuilder().setPersist(false).withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung).build();

        doReturn(Arrays.asList(dn1, dn2)).when(cut).loadRufnummern(cbVorgang);
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)).thenReturn(Boolean.FALSE);

        cut.loadCarrierKennungForRexMk(cbVorgang);
    }


    @DataProvider
    public Object[][] createSchaltangabenDataProvider() {
        Equipment hvtEquipment = new EquipmentBuilder()
                .withRandomId()
                .withCarrier(Carrier.CARRIER_DTAG)
                .withRangSchnittstelle(RangSchnittstelle.H)
                .withRangSSType(Equipment.RANG_SS_2H)
                .withRangBucht("0101")
                .withRangLeiste1("01")
                .withRangStift1("02")
                .setPersist(false)
                .build();

        Equipment kvzEquipment = new EquipmentBuilder()
                .withRandomId()
                .withRangVerteiler("02K1")
                .withCarrier(Carrier.CARRIER_DTAG)
                .withRangSchnittstelle(RangSchnittstelle.H)
                .withKvzNummer("A123")
                .setPersist(false)
                .build();

        return new Object[][] {
                { hvtEquipment, true, false },
                { kvzEquipment, false, true },
        };
    }

    @Test(dataProvider = "createSchaltangabenDataProvider")
    public void testCreateSchaltangaben(Equipment equipment, boolean expectHvt, boolean expectKvz) {
        List<Equipment> equipments = new ArrayList<Equipment>();
        equipments.add(equipment);

        Schaltangaben result = cut.createSchaltangaben(equipments);
        assertNotNull(result);
        if (expectHvt) {
            assertNotEmpty(result.getSchaltungKupfer());
            assertEmpty(result.getSchaltungKvzTal());
        }

        if (expectKvz) {
            assertEmpty(result.getSchaltungKupfer());
            assertNotEmpty(result.getSchaltungKvzTal());
        }
    }


    public void testCreateSchaltungKvzTal() {
        Equipment eq = new EquipmentBuilder()
                .withKvzNummer("A010")
                .withKvzDoppelader("0022")
                .withRangVerteiler("27K1")
                .setPersist(false)
                .build();

        SchaltungKvzTal result = cut.createSchaltungKvzTal(eq, Uebertragungsverfahren.H18);
        assertNotNull(result);
        assertThat(result.getKvz(), equalTo("A10"));  // fuehrende 0 von M-net KVZ Nummer muss fuer WITA entfernt sein!
        assertThat(result.getKvzSchaltnummer(), equalTo("27K10022"));
        assertThat(result.getUebertragungsverfahren(), equalTo(de.mnet.wita.message.common.Uebertragungsverfahren.H18));
    }


    @DataProvider(name = "transformWitaZeitfensterDP")
    public Object[][] transformWitaZeitfensterDP() {
        MnetWitaRequest reqSlot2 = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG)
                .withKundenwunschtermin(LocalDate.now(), SLOT_2)).buildValid();
        MnetWitaRequest reqSlot7 = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG)
                .withKundenwunschtermin(LocalDate.now(), SLOT_7)).buildValid();
        MnetWitaRequest reqSlot9 = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG)
                .withKundenwunschtermin(LocalDate.now(), SLOT_9)).buildValid();

        AnsprechpartnerTelekom ap = new AnsprechpartnerTelekom(null, "Andreas", "Schmid", "0123 456789");
        MeldungsPositionWithAnsprechpartner customerRequired =
                new MeldungsPositionWithAnsprechpartner(CUSTOMER_REQUIRED, ap);
        MeldungsPositionWithAnsprechpartner noChange =
                new MeldungsPositionWithAnsprechpartner(NO_CHANGE, ap);

        AuftragsBestaetigungsMeldung abmMitMontage = new AuftragsBestaetigungsMeldungBuilder()
                .addMeldungsposition(customerRequired).addMeldungsposition(noChange).build();
        AuftragsBestaetigungsMeldung abmOhneMontage = new AuftragsBestaetigungsMeldungBuilder()
                .addMeldungsposition(noChange).build();

        // @formatter:off
        return new Object[][] {
                { null,     null,           TalRealisierungsZeitfenster.VORMITTAG },
                { reqSlot2, null,           TalRealisierungsZeitfenster.VORMITTAG },
                { reqSlot9, null,           TalRealisierungsZeitfenster.GANZTAGS },
                { reqSlot7, null,           TalRealisierungsZeitfenster.NACHMITTAG },
                { reqSlot9, abmMitMontage,  TalRealisierungsZeitfenster.VORMITTAG },
                { reqSlot9, abmOhneMontage, TalRealisierungsZeitfenster.GANZTAGS },
                { reqSlot2, abmOhneMontage, TalRealisierungsZeitfenster.VORMITTAG },  // da SLOT_2 = KUE-KD ZF
        };
        // @formatter:on
    }

    @Test(dataProvider = "transformWitaZeitfensterDP")
    public void testTransformWitaZeitfenster(MnetWitaRequest witaRequest, AuftragsBestaetigungsMeldung abm,
            TalRealisierungsZeitfenster expected) {

        TalRealisierungsZeitfenster result = cut.transformWitaZeitfenster(witaRequest, abm);
        assertEquals(result, expected);
    }

}
