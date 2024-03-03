/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 13:00:45
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer den {@link AuftragspositionAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class AuftragspositionAggregatorTest extends BaseTest {

    @Spy
    @InjectMocks
    private AuftragspositionAggregator cut = new AuftragspositionAggregator();
    @Mock
    private WitaDataService witaDataService;
    @Mock
    private GeschaeftsfallProduktAggregator geschaeftsfallProduktAggregator;
    private AktionsCodeAenderungAggregator aktionsCodeAenderungAggregator = new AktionsCodeAenderungAggregator();


    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cut.aktionsCodeAenderungAggregator = aktionsCodeAenderungAggregator;

        cbVorgang = new WitaCBVorgangBuilder()
                .withWitaGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG)
                .setPersist(false).build();

        Equipment dtagEquipment = new EquipmentBuilder().withRandomId().withCarrier(Carrier.CARRIER_DTAG)
                .withRangSSType(Equipment.RANG_SS_2H).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02")
                .setPersist(false).build();
        when(witaDataService.loadDtagEquipments(cbVorgang)).thenReturn(Arrays.asList(dtagEquipment));
        when(geschaeftsfallProduktAggregator.aggregate(cbVorgang)).thenReturn(new GeschaeftsfallProdukt());
    }

    public void aggregate() {
        Auftragsposition result = cut.aggregate(cbVorgang);

        assertNotNull(result, "Auftragsposition wurde nicht generiert");
        assertEquals(result.getProdukt(), Produkt.TAL, "Produkttyp ist nicht vom erwarteten Typ!");
        assertNull(result.getProduktBezeichner(), "DTAG Produktbezeichner wird nachgelagert aggregiert!");
        assertNotNull(result.getGeschaeftsfallProdukt(), "GeschaeftsfallProdukt ist nicht definiert!");

        verify(geschaeftsfallProduktAggregator).aggregate(cbVorgang);
    }

    @DataProvider
    public Object[][] geschaeftsFaelleWithAktionsCodeAenderung() {
        return new Object[][] {
                { GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, AktionsCode.AENDERUNG },
                { GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG, AktionsCode.AENDERUNG },
                { GeschaeftsfallTyp.BEREITSTELLUNG, null },
                { GeschaeftsfallTyp.KUENDIGUNG_KUNDE, null } };
    }

    @Test(dataProvider = "geschaeftsFaelleWithAktionsCodeAenderung")
    public void aggregateAktionsCode(GeschaeftsfallTyp typ, AktionsCode expectedAktionsCode) {
        cbVorgang.setWitaGeschaeftsfallTyp(typ);

        Auftragsposition result = cut.aggregate(cbVorgang);

        assertEquals(result.getAktionsCode(), expectedAktionsCode);
    }
}
