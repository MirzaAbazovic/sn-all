/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2011
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link SchaltangabenAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class ReferencingSchaltangabenAggregatorTest extends BaseTest {

    private ReferencingSchaltangabenAggregator cut;
    private WitaCBVorgang cbVorgang;
    private Equipment dtagEquipment;

    @Mock
    EndstellenService endstellenServiceMock;
    @Mock
    RangierungsService rangierungsServiceMock;
    @Mock
    private WitaDataService witaDataService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cut = new ReferencingSchaltangabenAggregator();
        cut.endstellenService = endstellenServiceMock;
        cut.rangierungsService = rangierungsServiceMock;
        cut.witaDataService = witaDataService;

        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();

        EquipmentBuilder dtagEquipmentBuilder = new EquipmentBuilder().withRandomId().withCarrier(Carrier.CARRIER_DTAG)
                .withRangSSType(Equipment.RANG_SS_2H).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02")
                .setPersist(false);
        dtagEquipment = dtagEquipmentBuilder.build();
    }

    public void aggregate() {
        ReferencingSchaltangabenAggregator cutSpy = spy(cut);
        doReturn(Arrays.asList(dtagEquipment)).when(cutSpy).loadDtagEquipmentOfReferencingCbVorgang(cbVorgang);

        cutSpy.aggregate(cbVorgang);
        verify(witaDataService).createSchaltangaben(Arrays.asList(dtagEquipment));
        verifyNoMoreInteractions(witaDataService);
    }

    public void aggregateWithPreviousUetv() throws Exception {
        ReferencingSchaltangabenAggregator cutSpy = spy(cut);

        Carrierbestellung actualCarrierbestellung = new Carrierbestellung();
        doReturn(actualCarrierbestellung).when(witaDataService).loadCarrierbestellung(cbVorgang);
        Endstelle endstelleB = new Endstelle();
        endstelleB.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        when(endstellenServiceMock.findEndstellen4Carrierbestellung(actualCarrierbestellung)).thenReturn(
                Arrays.asList(new Endstelle(), endstelleB));

        doReturn(Arrays.asList(dtagEquipment)).when(cutSpy).loadDtagEquipmentOfActualCbVorgang(cbVorgang);
        cbVorgang.setPreviousUebertragungsVerfahren(Uebertragungsverfahren.H13);

        cutSpy.aggregate(cbVorgang);
        verify(witaDataService).createSchaltangaben(Arrays.asList(dtagEquipment), cbVorgang.getPreviousUebertragungsVerfahren());
        verifyNoMoreInteractions(witaDataService);
    }

    public void testLoadDtagEquipmentOfReferencingCbVorgang() throws Exception {
        ReferencingSchaltangabenAggregator cutSpy = spy(cut);
        cutSpy.witaDataService = witaDataService;

        Carrierbestellung actualCarrierbestellung = new Carrierbestellung();
        Carrierbestellung carrierbestellung = new Carrierbestellung();

        doReturn(actualCarrierbestellung).when(witaDataService).loadCarrierbestellung(cbVorgang);
        doReturn(carrierbestellung).when(witaDataService).getReferencingCarrierbestellung(cbVorgang,
                actualCarrierbestellung);

        Endstelle endstelleB = new Endstelle();
        endstelleB.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        when(endstellenServiceMock.findEndstellen4Carrierbestellung(carrierbestellung)).thenReturn(
                Arrays.asList(new Endstelle(), endstelleB));

        Equipment equipment = new Equipment();
        equipment.setRangBucht("0101");
        equipment.setRangLeiste1("11");
        equipment.setRangStift1("22");
        equipment.setUetv(de.augustakom.hurrican.model.cc.Uebertragungsverfahren.H04);

        when(rangierungsServiceMock.findEquipment4Endstelle(endstelleB, false, true)).thenReturn(equipment);

        Collection<Equipment> result = cutSpy.loadDtagEquipmentOfReferencingCbVorgang(cbVorgang);
        assertNotNull(result);
        assertNotEmpty(result);
        Equipment eq = result.iterator().next();
        assertEquals(eq.getRangBucht(), "0101");
        assertEquals(eq.getRangLeiste1(), "11");
        assertEquals(eq.getRangStift1(), "22");
        assertEquals(eq.getUetv(), de.augustakom.hurrican.model.cc.Uebertragungsverfahren.H04);
    }

    public void testLoadDtagEquipmentOfActualCbVorgang() throws Exception {
        ReferencingSchaltangabenAggregator cutSpy = spy(cut);
        cutSpy.witaDataService = witaDataService;

        Carrierbestellung actualCarrierbestellung = new Carrierbestellung();

        doReturn(actualCarrierbestellung).when(witaDataService).loadCarrierbestellung(cbVorgang);

        Endstelle endstelleB = new Endstelle();
        endstelleB.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        when(endstellenServiceMock.findEndstellen4Carrierbestellung(actualCarrierbestellung)).thenReturn(
                Arrays.asList(new Endstelle(), endstelleB));

        Equipment equipment = new Equipment();
        equipment.setRangBucht("0101");
        equipment.setRangLeiste1("11");
        equipment.setRangStift1("22");
        equipment.setUetv(de.augustakom.hurrican.model.cc.Uebertragungsverfahren.H04);

        when(rangierungsServiceMock.findEquipment4Endstelle(endstelleB, false, true)).thenReturn(equipment);

        Collection<Equipment> result = cutSpy.loadDtagEquipmentOfActualCbVorgang(cbVorgang);
        assertNotNull(result);
        assertNotEmpty(result);
        Equipment eq = result.iterator().next();
        assertEquals(eq.getRangBucht(), "0101");
        assertEquals(eq.getRangLeiste1(), "11");
        assertEquals(eq.getRangStift1(), "22");
        assertEquals(eq.getUetv(), de.augustakom.hurrican.model.cc.Uebertragungsverfahren.H04);
    }
}
