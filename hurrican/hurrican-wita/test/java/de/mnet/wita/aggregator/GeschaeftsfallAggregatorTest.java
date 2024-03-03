/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 08:41:30
 */
package de.mnet.wita.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.CarrierKennungBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.common.tools.ReflectionTools;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.auftrag.Auftragsmanagement;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallNeu;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallRexMk;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.model.WitaSendLimitBuilder;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link GeschaeftsfallAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class GeschaeftsfallAggregatorTest extends BaseTest {

    @InjectMocks
    @Spy
    private GeschaeftsfallAggregator cut;
    private WitaCBVorgang cbVorgang;

    private VertragsNummerAggregator vertragsNummerAggregator;
    private AnlagenAggregator anlagenAggregator;

    @Mock
    private AnsprechpartnerAmAggregator ansprechpartnerAmAggregatorMock;
    @Mock
    private AnsprechpartnerTechnikAggregator ansprechpartnerTechnikAggregatorMock;
    @Mock
    private KundenwunschterminAggregator terminAggregatorMock;
    @Mock
    private AuftragspositionAggregator auftragspositionAggregatorMock;
    @Mock
    private AuftragspositionLmaeAggregator auftragspositionLmaeAggregatorMock;
    @Mock
    private WitaDataService witaDataService;
    @Mock
    private WitaConfigService witaConfigService;

    private CarrierKennung kennungMuc;

    @BeforeMethod
    public void setUp() {
        cut = new GeschaeftsfallAggregator();

        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().withWitaGeschaeftsfallTyp(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG)
                .setPersist(false).build();

        vertragsNummerAggregator = new VertragsNummerAggregator();
        cut.vertragsNummerAggregator = vertragsNummerAggregator;
        anlagenAggregator = new AnlagenAggregator();
        cut.anlagenAggregator = anlagenAggregator;
        cut.witaDataService = witaDataService;
        setupAggregatorMock(ansprechpartnerAmAggregatorMock);
        setupAggregatorMock(ansprechpartnerTechnikAggregatorMock);
        setupAggregatorMock(terminAggregatorMock);
        setupAggregatorMock(auftragspositionAggregatorMock);
        setupAggregatorMock(auftragspositionLmaeAggregatorMock);
        kennungMuc = new CarrierKennungBuilder().withId(CarrierKennung.ID_MNET_MUENCHEN).setPersist(false).build();
    }

    private <T extends MwfEntity> void setupAggregatorMock(AbstractWitaDataAggregator<T> aggregatorMock) {
        Class<T> aggregationType = ReflectionTools.getTypeArgument(
                AbstractWitaDataAggregator.class,
                aggregatorMock.getClass());
        when(aggregatorMock.getAggregationType()).thenReturn(aggregationType);
    }

    public void aggregate() {
        when(ansprechpartnerAmAggregatorMock.aggregate(cbVorgang)).thenReturn(new Auftragsmanagement());
        when(terminAggregatorMock.aggregate(cbVorgang)).thenReturn(new Kundenwunschtermin());

        VertragsNummerAggregator spyVertragsNummerAggregator = spy(vertragsNummerAggregator);
        when(witaDataService.loadCarrierbestellung(cbVorgang)).thenReturn(new Carrierbestellung());
        when(witaDataService.loadCarrierKennung(cbVorgang)).thenReturn(kennungMuc);
        cut.vertragsNummerAggregator = spyVertragsNummerAggregator;
        spyVertragsNummerAggregator.witaDataService = witaDataService;

        cut.witaDataService = witaDataService;
        Geschaeftsfall result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Geschaeftsfall-Objekt wurde nicht erstellt");
        assertNotNull(result.getGfAnsprechpartner(),
                "Geschaeftsfall-Objekt besitzt keinen Ansprechpartner!");
        assertNotNull(result.getKundenwunschtermin(), "Geschaeftsfall-Objekt besitzt keinen Kundenwunschtermin!");
        assertNull(result.getVertragsNummer());

        verify(ansprechpartnerAmAggregatorMock).aggregate(cbVorgang);
        verify(ansprechpartnerTechnikAggregatorMock).aggregate(cbVorgang);
        verify(terminAggregatorMock).aggregate(cbVorgang);
        verify(auftragspositionAggregatorMock).aggregate(cbVorgang);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void aggregateYieldsExceptionBecauseSendLimitNotAllowedConfigured() {
        doReturn(Boolean.FALSE).when(cut).isGeschaeftsfallAllowed(any(Geschaeftsfall.class), any(WitaCBVorgang.class));
        cut.aggregate(cbVorgang);
    }

    public void aggregateWithReferencingCb() {
        when(ansprechpartnerAmAggregatorMock.aggregate(cbVorgang)).thenReturn(new Auftragsmanagement());
        when(terminAggregatorMock.aggregate(cbVorgang)).thenReturn(new Kundenwunschtermin());

        VertragsNummerAggregator spyVertragsNummerAggregator = spy(vertragsNummerAggregator);
        when(witaDataService.loadCarrierbestellung(cbVorgang)).thenReturn(
                new CarrierbestellungBuilder().withVtrNr("123").setPersist(false).build());
        cut.vertragsNummerAggregator = spyVertragsNummerAggregator;
        spyVertragsNummerAggregator.witaDataService = witaDataService;

        when(witaDataService.loadCarrierKennung(cbVorgang)).thenReturn(new CarrierKennung());
        Geschaeftsfall result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Geschaeftsfall-Objekt wurde nicht erstellt");
        assertNotNull(result.getGfAnsprechpartner(),
                "Geschaeftsfall-Objekt besitzt keinen Ansprechpartner!");
        assertNotNull(result.getKundenwunschtermin(), "Geschaeftsfall-Objekt besitzt keinen Kundenwunschtermin!");
        assertNotNull(result.getVertragsNummer());

        verify(ansprechpartnerAmAggregatorMock).aggregate(cbVorgang);
        verify(terminAggregatorMock).aggregate(cbVorgang);
        verify(auftragspositionAggregatorMock).aggregate(cbVorgang);
    }


    public void testAggregateForRexMk() {
        cbVorgang.setTyp(CBVorgang.TYP_REX_MK);

        CarrierKennung kennungMnetMuc = new CarrierKennungBuilder().withId(CarrierKennung.ID_MNET_MUENCHEN)
                .withKundenNr(CarrierKennung.DTAG_KUNDEN_NR_MNET).setPersist(false).build();
        when(witaDataService.loadCarrierKennungForRexMk(cbVorgang)).thenReturn(kennungMnetMuc);

        VertragsNummerAggregator spyVertragsNummerAggregator = spy(vertragsNummerAggregator);
        when(witaDataService.loadCarrierbestellung(cbVorgang)).thenReturn(
                new CarrierbestellungBuilder().withVtrNr("123").setPersist(false).build());
        cut.vertragsNummerAggregator = spyVertragsNummerAggregator;
        spyVertragsNummerAggregator.witaDataService = witaDataService;

        cut.aggregate(cbVorgang);
        verify(witaDataService).loadCarrierKennungForRexMk(cbVorgang);
        verify(witaDataService, times(0)).loadCarrierKennung(cbVorgang);
    }


    @DataProvider
    public Object[][] isGeschaeftsfallAllowed() {
        Equipment hvtEquipment = new EquipmentBuilder()
                .withCarrier(de.augustakom.hurrican.model.cc.Carrier.CARRIER_DTAG)
                .withRangSchnittstelle(RangSchnittstelle.H)
                .setPersist(false).build();
        List<Equipment> hvtEquipments = Arrays.asList(hvtEquipment);

        // @formatter:off
        return new Object[][] {
                { hvtEquipments, new WitaSendLimitBuilder().withAllowed(Boolean.TRUE).setPersist(false).build(), true },
                { hvtEquipments, new WitaSendLimitBuilder().withAllowed(Boolean.FALSE).setPersist(false).build(), false },
                { hvtEquipments, null, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "isGeschaeftsfallAllowed")
    public void isGeschaeftsfallAllowed(List<Equipment> dtagEquipments, WitaSendLimit sendLimit, boolean expectedResult) {
        when(witaDataService.loadDtagEquipments(any(CBVorgang.class))).thenReturn(dtagEquipments);
        when(witaConfigService.findWitaSendLimit(any(String.class), any(KollokationsTyp.class), (String) Matchers.isNull())).thenReturn(sendLimit);
        boolean result = cut.isGeschaeftsfallAllowed(new GeschaeftsfallNeu(), new WitaCBVorgang());
        assertEquals(result, expectedResult);
    }


    public void isGeschaeftsfallAllowedForRexMkAlways() {
        assertTrue(cut.isGeschaeftsfallAllowed(new GeschaeftsfallRexMk(), null));
    }

}
