/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2012 09:09:28
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryEntryData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryEntryDataBuilder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryGeneralParamsData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryIdData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * TestNG Klasse fuer {@link CPSQueryAttainableBitrateCommand}.
 */
@Test(groups = BaseTest.UNIT)
public class CPSQueryAttainableBitrateCommandUnitTest extends BaseTest {

    @Spy
    @InjectMocks
    private CPSQueryAttainableBitrateCommand cut;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private HWService hwService;

    @BeforeMethod
    public void setUp() throws FindException {
        cut = new CPSQueryAttainableBitrateCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckValues_withoutAuftragId() throws FindException {
        cut.prepare(CPSQueryAttainableBitrateCommand.KEY_AUFTRAG_ID, null);
        cut.checkValues();
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckValues_withoutUserName() throws FindException {
        cut.prepare(CPSQueryAttainableBitrateCommand.KEY_USER_NAME, null);
        cut.checkValues();
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckValues_withoutSessionId() throws FindException {
        cut.prepare(AbstractCPSCommand.KEY_SESSION_ID, null);
        cut.checkValues();
    }

    @DataProvider
    public Object[][] dataProviderLoadDefaultData() {
        AuftragDaten auftragDaten = new AuftragDaten();
        Equipment equipment = new Equipment();
        equipment.setHwBaugruppenId(Long.valueOf(1));
        HWBaugruppe hwBaugruppe = new HWBaugruppe();
        hwBaugruppe.setRackId(Long.valueOf(1));
        HWRack hwRack = new HWRack();
        hwRack.setRackTyp(HWRack.RACK_TYPE_DSLAM);
        HWBaugruppenTyp hwBaugruppenTyp = new HWBaugruppenTyp();
        HVTTechnik hvtTechnik = new HVTTechnik();
        hwBaugruppenTyp.setHvtTechnik(hvtTechnik);
        hwBaugruppe.setHwBaugruppenTyp(hwBaugruppenTyp);

        // @formatter:off
       return new Object[][] {
               { auftragDaten, equipment, hwBaugruppe, hwRack, false },
               { auftragDaten, equipment, hwBaugruppe, null, true },
               { auftragDaten, equipment, null, hwRack, true },
               { auftragDaten, null, hwBaugruppe, hwRack, true },
               { null, equipment, hwBaugruppe, hwRack, true },
               { null, null, null, null, true },
       };
       // @formatter:on
    }

    @Test(dataProvider = "dataProviderLoadDefaultData")
    public void testLoadDefaultData(AuftragDaten auftragDaten, Equipment equipment, HWBaugruppe hwBaugruppe,
            HWRack hwRack,
            boolean exceptionExpected) throws Exception {
        try {
            Rangierung rangierung = new Rangierung();
            Rangierung[] rangierungen = new Rangierung[] { rangierung };
            when(auftragService.findAuftragDatenByAuftragIdTx(any(Long.class))).thenReturn(auftragDaten);
            when(rangierungsService.findRangierungenTx(any(Long.class), any(String.class))).thenReturn(rangierungen);
            when(rangierungsService.findEquipment(any(Long.class))).thenReturn(equipment);
            when(hwService.findBaugruppe(any(Long.class))).thenReturn(hwBaugruppe);
            when(hwService.findRackById(any(Long.class))).thenReturn(hwRack);
            cut.loadDefaultData();
            if (exceptionExpected == true) {
                fail();
            }
        }
        catch (ServiceCommandException e) {
            if (exceptionExpected == false) {
                throw e;
            }
        }
    }

    @DataProvider
    public Object[][] dataProviderLoadIdData() {
        Equipment equipment = new Equipment();
        equipment.setHwBaugruppenId(Long.valueOf(1));
        HWBaugruppe hwBaugruppe = new HWBaugruppe();
        hwBaugruppe.setRackId(Long.valueOf(1));
        HWRack hwRack = new HWRack();
        hwRack.setGeraeteBez("DSL-400001");
        HWBaugruppenTyp hwBaugruppenTyp = new HWBaugruppenTyp();
        hwBaugruppenTyp.setName("EBLTD");
        HVTTechnik hvtTechnikSNS = new HVTTechnik();
        hvtTechnikSNS.setId(HVTTechnik.SIEMENS);
        HVTTechnik hvtTechnikHuawei = new HVTTechnik();
        hvtTechnikHuawei.setId(HVTTechnik.HUAWEI);
        HVTTechnik hvtTechnikAlcatel = new HVTTechnik();
        hvtTechnikAlcatel.setId(HVTTechnik.ALCATEL);
        hwBaugruppe.setHwBaugruppenTyp(hwBaugruppenTyp);

        // @formatter:off
       return new Object[][] {
               { equipment, hwBaugruppe, hwRack, hvtTechnikSNS, "Ü02/1-302-11", 0, 0, 302, 11 },
               { equipment, hwBaugruppe, hwRack, hvtTechnikHuawei, "U04-3-001-01", 0, 0, 1, 1},
               { equipment, hwBaugruppe, hwRack, hvtTechnikAlcatel, "1-2-14-6", 1, 2, 14, 6 },
       };
       // @formatter:on
    }

    @Test(dataProvider = "dataProviderLoadIdData")
    public void testLoadIdData(Equipment equipment, HWBaugruppe hwBaugruppe, HWRack hwRack, HVTTechnik hvtTechnik,
            String hwEqn, Integer rack, Integer shelf, Integer slot, Integer port) {
        hwBaugruppe.getHwBaugruppenTyp().setHvtTechnik(hvtTechnik);
        equipment.setHwEQN(hwEqn);
        cut.setEquipment(equipment);
        cut.setHwBaugruppe(hwBaugruppe);
        cut.setHwRack(hwRack);
        CPSQueryIdData id = new CPSQueryIdData();
        cut.loadIdData(id);
        assertEquals(id.getDslamName(), hwRack.getGeraeteBez());
        assertEquals(id.getCardType(), hwBaugruppe.getHwBaugruppenTyp().getName());
        assertEquals(id.getDslamType(), CPSQueryIdData.DSLAMType.getById(hvtTechnik.getId()));
        assertEquals(id.getRack(), rack);
        assertEquals(id.getShelf(), shelf);
        assertEquals(id.getSlot(), slot);
        assertEquals(id.getPort(), port);
    }

    @Test
    public void testLoadGeneralParamsData() {
        CPSQueryGeneralParamsData generalParams = new CPSQueryGeneralParamsData();
        AuftragDaten auftragDaten = new AuftragDaten();
        Long auftragNoOrig = Long.valueOf(1);
        auftragDaten.setAuftragNoOrig(auftragNoOrig);
        cut.setUserName("myName");
        cut.setAuftragDaten(auftragDaten);
        cut.loadGeneralParamsData(generalParams);
        assertTrue(NumberTools.equal(generalParams.getTaifunNumber(), auftragNoOrig));
        assertTrue(StringUtils.equals(generalParams.getUserName(), "myName"));
    }

    @Test
    public void testLoadEntryData() {
        CPSQueryEntryData entry = new CPSQueryEntryData();
        cut.loadEntryData(entry);
        assertTrue(StringUtils.equals(entry.getMaxAttBrDn(), " "));
        assertTrue(StringUtils.equals(entry.getMaxAttBrUp(), " "));
    }


    @DataProvider
    public Object[][] dataProviderExecuteAttainableBitrate() {
        // @formatter:off
       return new Object[][] {
               { new CPSQueryEntryDataBuilder().withMaxAttBrDn("1280").withMaxAttBrUp("256").build(), Pair.create(Integer.valueOf(1280), Integer.valueOf(256)) },
               { new CPSQueryEntryDataBuilder().withMaxAttBrDn("0").withMaxAttBrUp("0").build(), null },
               { new CPSQueryEntryDataBuilder().build(), null },
       };
       // @formatter:on
    }

    @Test(dataProvider = "dataProviderExecuteAttainableBitrate")
    public void testExecuteAttainableBitrateQuery(CPSQueryEntryData queryResult, Pair<Integer, Integer> expectedResult) throws FindException, ServiceCommandException, ServiceNotFoundException, XmlException {
        doReturn(queryResult).when(cut).sendQuery();

        Pair<Integer, Integer> result = cut.executeAttainableBitrateQuery();
        assertEquals(result, expectedResult);
    }

}
