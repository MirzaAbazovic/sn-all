/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 14:09:42
 */
package de.mnet.wita.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

/**
 * TestNG Klasse fuer {@link TalAenderungstypServiceImpl}
 */
@Test(groups = BaseTest.UNIT)
public class TalAenderungstypServiceTest extends BaseTest {

    @InjectMocks
    @Spy
    private TalAenderungstypServiceImpl talAenderungstypService;
    @Mock
    private WitaDataService witaDataServiceMock;
    @Mock
    private EndstellenService endstellenServiceMock;
    private WitaCBVorgang witaCbVorgang;

    @BeforeMethod
    public void setUp() {
        talAenderungstypService = new TalAenderungstypServiceImpl();
        MockitoAnnotations.initMocks(this);

        witaCbVorgang = new WitaCBVorgangBuilder().withAuftragId(123L).build();
    }

    @Test(expectedExceptions = WitaBaseException.class)
    public void determineAenderungstypWithoutCb() {
        assertNull(talAenderungstypService.determineGeschaeftsfall(null, witaCbVorgang));
    }

    @Test(expectedExceptions = WitaBaseException.class)
    public void determineAenderungstypWithoutOrder() {
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().setPersist(false).build();
        talAenderungstypService.determineGeschaeftsfall(carrierbestellung, null);
    }

    @Test(expectedExceptions = WitaBaseException.class)
    public void determineAenderungstypWithoutReferencingOrder() {
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().setPersist(false).build();
        talAenderungstypService.determineGeschaeftsfall(carrierbestellung, witaCbVorgang);
    }

    @Test(expectedExceptions = WitaBaseException.class)
    public void determineAenderungstypWithoutEquipmentNew() throws FindException {
        AuftragBuilder auftragId4TalNABuilder = new AuftragBuilder().withRandomId().setPersist(false);
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withAuftrag4TalNaBuilder(auftragId4TalNABuilder).setPersist(false).build();

        Pair<Equipment, Equipment> equipments = Pair.create(new Equipment(), null);
        when(witaDataServiceMock.loadEquipments(carrierbestellung, carrierbestellung.getAuftragId4TalNA())).thenReturn(
                equipments);
        when(endstellenServiceMock.findEndstelle4Auftrag(any(Long.class), any(String.class))).thenReturn(
                new EndstelleBuilder().setPersist(false).build());

        talAenderungstypService.determineGeschaeftsfall(carrierbestellung, new WitaCBVorgangBuilder()
                .withAuftragBuilder(auftragId4TalNABuilder).build());
    }

    @DataProvider
    public Object[][] aenderungstypen() {
        // @formatter:off
        return new Object[][] {
            {
                // nicht DTAG --> kein GeschaeftsfallTyp
                new EquipmentBuilder().withRandomId().setPersist(false).build(),
                new EquipmentBuilder().withRandomId().setPersist(false).build(),
                null
            },
            {
                // Ports identisch --> kein GeschaeftsfallTyp
                new EquipmentBuilder().withId(Long.MAX_VALUE).withRangSchnittstelle(RangSchnittstelle.N).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                new EquipmentBuilder().withId(Long.MAX_VALUE).withRangSchnittstelle(RangSchnittstelle.N).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                null
            },
            {
                // RangSchnittstelle (N) gleich, Ports unterschiedlich --> SER-POW
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.N).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.N).withRangBucht("0101").withRangLeiste1("01").withRangStift1("03").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                GeschaeftsfallTyp.PORTWECHSEL
            },
            {
                // RangSchnittstelle (H) gleich, Ports unterschiedlich --> SER-POW
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.H).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.H).withRangBucht("0101").withRangLeiste1("01").withRangStift1("03").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                GeschaeftsfallTyp.PORTWECHSEL
            },
            {
                // N->H --> LAE
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.N).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.H).withRangBucht("0101").withRangLeiste1("01").withRangStift1("03").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                GeschaeftsfallTyp.LEISTUNGS_AENDERUNG
            },
            {
                // H->N --> LAE
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.H).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.N).withRangBucht("0101").withRangLeiste1("01").withRangStift1("03").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                GeschaeftsfallTyp.LEISTUNGS_AENDERUNG
            },
            {
                // RangSchnittstelle (H) gleich, UETV unterschiedlich --> LMAE
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.H).withUETV(Uebertragungsverfahren.H04).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.H).withUETV(Uebertragungsverfahren.H13).withRangBucht("0101").withRangLeiste1("01").withRangStift1("03").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG
            },
            {
                // RangSchnittstelle (N) gleich, UETV unterschiedlich --> kein Geschaeftsfall (kann es so nicht geben)
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.N).withUETV(Uebertragungsverfahren.N01).withRangBucht("0101").withRangLeiste1("01").withRangStift1("02").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                new EquipmentBuilder().withRandomId().withRangSchnittstelle(RangSchnittstelle.N).withUETV(Uebertragungsverfahren.H01).withRangBucht("0101").withRangLeiste1("01").withRangStift1("03").withCarrier(Carrier.DTAG.toString()).setPersist(false).build(),
                null
            },
        };
        // @formatter:on
    }

    @Test(dataProvider = "aenderungstypen")
    public void determineGeschaeftsfallTypAenderung(Equipment eqOld, Equipment eqNew, GeschaeftsfallTyp expected) {
        GeschaeftsfallTyp result = talAenderungstypService.determineGeschaeftsfallTypAenderung(eqOld, eqNew);
        assertEquals(result, expected);
    }

}
