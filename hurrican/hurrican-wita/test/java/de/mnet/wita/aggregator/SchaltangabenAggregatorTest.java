/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 14:17:17
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link SchaltangabenAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class SchaltangabenAggregatorTest extends BaseTest {

    private SchaltangabenAggregator cut;
    private WitaCBVorgang cbVorgang;

    @Mock
    private WitaDataService witaDataService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cut = new SchaltangabenAggregator();
        cut.witaDataService = witaDataService;
        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();
    }

    public void aggregateHvtTal() {
        Equipment dtagEquipment = new EquipmentBuilder()
                .withRandomId()
                .withCarrier(Carrier.CARRIER_DTAG)
                .withRangSchnittstelle(RangSchnittstelle.H)
                .withRangSSType(Equipment.RANG_SS_2H)
                .withRangBucht("0101")
                .withRangLeiste1("01")
                .withRangStift1("02")
                .setPersist(false)
                .build();

        SchaltangabenAggregator cutSpy = spy(cut);
        doReturn(Arrays.asList(dtagEquipment)).when(witaDataService).loadDtagEquipments(cbVorgang);

        cutSpy.aggregate(cbVorgang);
        verify(witaDataService).createSchaltangaben(Arrays.asList(dtagEquipment));
        verify(witaDataService).loadDtagEquipments(cbVorgang);
        verifyNoMoreInteractions(witaDataService);
    }

    @Test
    public void testAggregateKvzTal() {
        Equipment dtagEquipment = new EquipmentBuilder()
                .withRandomId()
                .withRangVerteiler("02K1")
                .withCarrier(Carrier.CARRIER_DTAG)
                .withRangSchnittstelle(RangSchnittstelle.H)
                .withKvzNummer("123")
                .setPersist(false)
                .build();

        SchaltangabenAggregator cutSpy = spy(cut);
        doReturn(Arrays.asList(dtagEquipment)).when(witaDataService).loadDtagEquipments(cbVorgang);

        cutSpy.aggregate(cbVorgang);
        verify(witaDataService).createSchaltangaben(Arrays.asList(dtagEquipment));
        verify(witaDataService).loadDtagEquipments(cbVorgang);
        verifyNoMoreInteractions(witaDataService);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void testAggregateMnetPort() {
        Equipment dtagEquipment = new EquipmentBuilder()
                .withRandomId()
                .withCarrier(TNB.MNET.carrierNameUC)
                .setPersist(false)
                .build();

        SchaltangabenAggregator cutSpy = spy(cut);
        doReturn(Arrays.asList(dtagEquipment)).when(witaDataService).loadDtagEquipments(cbVorgang);

        cutSpy.aggregate(cbVorgang);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void testAggregateGlasfaserPort() {
        Equipment dtagEquipment = new EquipmentBuilder()
                .withRandomId()
                .withCarrier(Carrier.CARRIER_DTAG)
                .withRangSchnittstelle(RangSchnittstelle.LWL)
                .setPersist(false)
                .build();

        SchaltangabenAggregator cutSpy = spy(cut);
        doReturn(Arrays.asList(dtagEquipment)).when(witaDataService).loadDtagEquipments(cbVorgang);

        cutSpy.aggregate(cbVorgang);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void testAggregateWithHvtAndKvzPortsYieldsException() {
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
                .withKvzNummer("123")
                .setPersist(false)
                .build();

        doReturn(Arrays.asList(hvtEquipment, kvzEquipment)).when(witaDataService).loadDtagEquipments(cbVorgang);
        cut.aggregate(cbVorgang);
    }

}
