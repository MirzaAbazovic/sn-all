package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoImportView;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTXStandortImportView;
import de.augustakom.hurrican.model.cc.view.OltChildImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * FTTXHardwareServiceImplTest
 */
@Test(groups = BaseTest.UNIT)
public class FTTXHardwareServiceImplTest {

    @InjectMocks
    @Spy
    FTTXHardwareServiceImpl cut;

    @Mock
    HWService hwService;
    @Mock
    private HVTService hvtService;
    @Mock
    CPSService cpsService;
    @Mock
    RangierungFreigabeService rangierungFreigabeService;
    @Mock
    RangierungsService rangierungsService;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
    }

    @DataProvider
    Object[][] testIsOntEqualDP() {
        return new Object[][] {
                {createOntImportView("ONT-000001", "O-124-T", 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei",
                        "FTTH Standort", "OLT-412345"), false, false}, // Modellnummer unterschiedlich -> Fehler
                {createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei",
                        "FTTH Standort", "OLT-412345"), false, true},  // mit SerienNr, alles gleich -> o.k.
        };
    }

    @Test(dataProvider = "testIsOntEqualDP")
    public void testIsOntEqual(FTTHOntImportView importView, boolean isGslam, boolean expectedResult) throws FindException {
        setupIsOltChildEqual(isGslam);
        HWOnt ont = mockCreateHWOnt();
        assertThat(cut.isOntEqual(importView, ont), equalTo(expectedResult));
    }

    @DataProvider
    Object[][] testIsDpoEqualDP() {
        return new Object[][] {
                {createDpoImportView("DPO-000001", "O-124-T", 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei",
                        "FTTH Standort", "OLT-412345", null, null), false, false}, // Modellnummer unterschiedlich -> Fehler
                {createDpoImportView("DPO-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei",
                        "FTTH Standort", "OLT-412345", null, null), false, true},  // mit SerienNr, alles gleich -> o.k.
                {createDpoImportView("DPO-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei",
                        "FTTH Standort", "OLT-412345", "CH-12", "2"), false, true},  // mit SerienNr und Chassis, alles gleich -> o.k.
        };
    }

    @Test(dataProvider = "testIsDpoEqualDP")
    public void testIsDpoEqual(FTTBHDpoImportView importView, boolean isGslam, boolean expectedResult) throws FindException {
        setupIsOltChildEqual(isGslam);
        HWDpo dpo = mockCreateHWDpo();
        assertThat(cut.isDpoEqual(importView, dpo), equalTo(expectedResult));
    }

    @DataProvider
    Object[][] testIsOltChildEqualDP() {
        return new Object[][] {
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // mit SerienNr : alles o.k.
                { createOntImportView(null, "O-123-T", 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // Bezeichnung fehlt: alles o.k.
                { createOntImportView("ONT-000002", "O-123-T", 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, false }, // Gerätebezeichnung neu: Fehler
                { createOntImportView("ONT-000001", null, 1L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // Modellnummer fehlt: alles o.k.
                { createOntImportView("ONT-000001", "O-123-T", null, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // oltRack fehlt: alles o.k.
                { createOntImportView("ONT-000001", "O-123-T", 11L, 2L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, false }, // oltRack neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, null, 3L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // oltSlot fehlt: alles o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 22L, 3L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, false }, // oltSlot neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, null, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // oltSubrack fehlt: alles o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 33L, 4L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, false }, // oltSubrack neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, null, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // oltPort fehlt: alles o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 44L, 5L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, false }, // oltPort neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, null, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // gponid fehlt : alles o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 55L, "123", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, false }, // gponid neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, null, "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // Seriennummer fehlt: o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // Seriennummer neu: o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", null, "Huawei", "FTTH Standort", "OLT-412345"), false, true },  // Raum fehlt: o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "2.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), false, false }, // Raum neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", null, "FTTH Standort", "OLT-412345"), false, true },  // Hersteller fehlt: o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "HUAWEI", "FTTH Standort", "OLT-412345"), false, false }, // Hersteller neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "Huawei", null, "OLT-412345"), false, true },  // Standort fehlt: o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "Huawei", "FTTX Standort", "OLT-412345"), false, false }, // Standort neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "Huawei", "FTTH Standort", null), false, true },  // OLT fehlt: o.k.
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "Huawei", "FTTH Standort", "OLT-41234X"), false, false }, // OLT neu: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "Huawei", "FTTH Standort", "OLT-412345"), true, true }, // GSLAM: ok
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "Huawei", "FTTH Standort", "DSL-412345"), true, false }, // GSLAM, Geraetebez statt AltGeraeteBez: Fehler
                { createOntImportView("ONT-000001", "O-123-T", 1L, 2L, 3L, 4L, 5L, "134", "1.OG, links", "Huawei", "FTTH Standort", "OLT-41234X"), true, false }, // GSLAM, AltGeraeteBez falsch: Fehler
                { createOntImportView("ONT-000001", null, null, null, null, null, null, "134", null, null, null, null), true, true }, // GSLAM, Seriennummer ändert sich: o.k
                { createOntImportView("ONT-000001", null, null, null, null, null, null, "1234", null, null, null, null), true, true }, // GSLAM, Seriennummer die alte: o.k
        };
    }

    @Test(dataProvider = "testIsOltChildEqualDP")
    public void testIsOltChildEqual(OltChildImportView importView, boolean isGslam, boolean expectedResult) throws FindException {
        setupIsOltChildEqual(isGslam);
        HWOltChild oltChild = mockCreateHWOnt();
        assertThat(cut.isOltChildEqual(importView, oltChild), equalTo(expectedResult));
    }

    private void setupIsOltChildEqual(boolean isGslam) throws FindException {
        HVTRaum hvtRaum = new HVTRaum();
        hvtRaum.setRaum("1.OG, links");
        when(hvtService.findHVTRaum(any(Long.class))).thenReturn(hvtRaum);

        HVTTechnik producer = new HVTTechnik();
        producer.setHersteller("Huawei");
        when(hvtService.findHVTTechnik(any(Long.class))).thenReturn(producer);

        HVTGruppe hvtGruppe = new HVTGruppe();
        hvtGruppe.setOrtsteil("FTTH Standort");
        when(hvtService.findHVTGruppe4Standort(any(Long.class))).thenReturn(hvtGruppe);

        if (isGslam) {
            HWDslam hwDslam = new HWDslam();
            hwDslam.setAltGslamBez("OLT-412345");
            when(hwService.findRackById(any(Long.class))).thenReturn(hwDslam);
        }
        else {
            HWOlt hwOlt = new HWOlt();
            hwOlt.setGeraeteBez("OLT-412345");
            when(hwService.findRackById(any(Long.class))).thenReturn(hwOlt);
        }
    }

    private FTTHOntImportView createOntImportView(String bezeichnung, String modellnummer, Long oltRack, Long oltSubrack, Long oltSlot,
            Long oltPort, Long gponId, String seriennummer, String raum, String hersteller, String standort,
            String oltGeraetebezeichnung) {

        FTTHOntImportView importView = new FTTHOntImportView();
        fillOltChildImportView(importView, bezeichnung, modellnummer, oltRack, oltSubrack, oltSlot,
                oltPort, gponId, seriennummer, raum, hersteller, standort,
                oltGeraetebezeichnung);
        return importView;
    }

    private FTTBHDpoImportView createDpoImportView(String bezeichnung, String modellnummer, Long oltRack, Long oltSubrack, Long oltSlot,
            Long oltPort, Long gponId, String seriennummer, String raum, String hersteller, String standort,
            String oltGeraetebezeichnung, String chassisidentifier, String chassisslot) {

        FTTBHDpoImportView importView = new FTTBHDpoImportView();
        fillOltChildImportView(importView, bezeichnung, modellnummer, oltRack, oltSubrack, oltSlot,
                oltPort, gponId, seriennummer, raum, hersteller, standort,
                oltGeraetebezeichnung);
        importView.setChassisIdentifier(chassisidentifier);
        importView.setChassisSlot(chassisslot);

        return importView;
    }

    private void fillOltChildImportView(OltChildImportView importView, String bezeichnung, String modellnummer, Long oltRack, Long oltSubrack, Long oltSlot,
            Long oltPort, Long gponId, String seriennummer, String raum, String hersteller, String standort,
            String oltGeraetebezeichnung) {
        importView.setBezeichnung(bezeichnung);
        importView.setModellnummer(modellnummer);
        importView.setOltRack(oltRack);
        importView.setOltSubrack(oltSubrack);
        importView.setOltSlot(oltSlot);
        importView.setOltPort(oltPort);
        importView.setGponId(gponId);
        importView.setSeriennummer(seriennummer);
        importView.setRaumbezeichung(raum);
        importView.setHersteller(hersteller);
        importView.setStandort(standort);
        importView.setOlt(oltGeraetebezeichnung);
    }

    @DataProvider
    public Object[][] testCheckOntForDeletionAndDeleteDP() {
        return new Object[][] {
                { mockCreateHWOnt() },
                { mockCreateHWDpo() }
        };
    }

    @Test(dataProvider = "testCheckOntForDeletionAndDeleteDP")
    public void testCheckOntForDeletionAndDelete(final HWOltChild hwOltChild)   throws Exception {

        Long sessionId = 10L;
        final CPSTransactionResult cpsTransactionResult = mockCreateCPSTransactionResult();

        when(cut.checkHwOltChildForActiveAuftraege(hwOltChild)).thenReturn(null);
        when(cpsService.isCpsTxServiceOrderTypeExecuteable(any(Long.class), any(Long.class))).thenReturn(true);
        when(cpsService.createCPSTransaction4OltChild(any(Long.class), any(Long.class), any(Long.class))).thenReturn(cpsTransactionResult);
        when(hwService.findBaugruppen4Rack(any(Long.class))).thenReturn(null);

        Long cpsTxId = cut.checkHwOltChildForActiveAuftraegeAndDelete(hwOltChild, true, sessionId);

        assertThat((cpsTxId), equalTo(cpsTransactionResult.getCpsTransactions().get(0).getId()));

    }

    @DataProvider
    public Object[][] checkIfOltChildFieldsCompleteDP() {
        return new Object[][] {
                { createHWOltChildMock("123", "456", 1L), true },
                { createHWOltChildMock(null, "456", 1L), false },
                { createHWOltChildMock("123", null, 1L), false },
                { createHWOltChildMock("123", "456", null), false }
        };
    }

    private HWOltChild createHWOltChildMock(String serialNo, String geraeteBez, Long hvtRaumId) {
        HWOltChild hwOltChild = Mockito.mock(HWOltChild.class);
        when(hwOltChild.getSerialNo()).thenReturn(serialNo);
        when(hwOltChild.getGeraeteBez()).thenReturn(geraeteBez);
        when(hwOltChild.getHvtRaumId()).thenReturn(hvtRaumId);
        return hwOltChild;
    }

    @Test(dataProvider = "checkIfOltChildFieldsCompleteDP")
    public void testCheckIfOltChildFieldsComplete(HWOltChild hwOltChild, boolean expected) {
        Assert.assertEquals(cut.checkIfOltChildFieldsComplete(hwOltChild), expected);
    }

    @DataProvider
    private Object[][] generateFTTBHDpoPortDataProvider() {
        return new Object[][] {
                {"VDSL2", "1-0", "dpo-412345", 1L},
        };
    }

    @Test(dataProvider = "generateFTTBHDpoPortDataProvider")
    public void testGenerateFTTBHDpoPort(String schnittstelle, String port, String gereatebezeichnung, Long sessionId)
            throws Exception {
        AKUser user = mock(AKUser.class);
        doReturn(user).when(cut).getAKUserBySessionId(sessionId);
        HWDpo hwDpo = mockCreateHWDpo();
        when(hwService.findActiveRackByBezeichnung(gereatebezeichnung)).thenReturn(hwDpo);
        HWBaugruppe hwBaugruppe = mock(HWBaugruppe.class);
        FTTBHDpoPortImportView view = createFttbhDpoPortImportView(schnittstelle, port, gereatebezeichnung);
        doReturn(hwBaugruppe).when(cut).getHWBaugruppe(hwDpo, hwDpo.getDpoType(), schnittstelle, "0-1");
        PhysikTyp physikTyp = mock(PhysikTyp.class);
        doReturn(physikTyp).when(cut).getPhysikTyp("FTTB_DPO_" + schnittstelle);
        doReturn(true).when(cut).assertPortDoesNotAlreadyExist(port, hwDpo, hwBaugruppe);
        Equipment equipment = mock(Equipment.class);
        doReturn(equipment).when(cut).getEquipmentDpoDpu(hwDpo.getHvtIdStandort(), view.getLeiste(), view.getStift());
        doNothing().when(cut).definePortValues(equipment, port, schnittstelle, hwBaugruppe, user, EqStatus.rang);
        doNothing().when(cut).createRangierung4Port(eq(equipment), any(Date.class), eq(hwDpo), eq(physikTyp), eq(user));

        cut.generateFTTBHDpoPort(view, sessionId);
        verify(cut).getEquipmentDpoDpu(hwDpo.getHvtIdStandort(), view.getLeiste(),view.getStift());
        verify(cut).definePortValues(equipment, port, view.getSchnittstelle(), hwBaugruppe, user, EqStatus.rang);
        verify(cut).createRangierung4Port(eq(equipment), any(Date.class), eq(hwDpo), eq(physikTyp), eq(user));
    }

    @DataProvider
    private Object[][] generateFTTHOntPortDataProvider() {
        return new Object[][] {
                {"ETH", "1-0", "ont-412345", 1L},
        };
    }

    @Test(dataProvider = "generateFTTHOntPortDataProvider")
    public void testGenerateFTTHOntPort(String schnittstelle, String hweqn, String gereatebezeichnung, Long sessionId)
            throws Exception {
        AKUser user = mock(AKUser.class);
        doReturn(user).when(cut).getAKUserBySessionId(sessionId);
        HWOnt hwOnt = mockCreateHWOnt();
        when(hwService.findActiveRackByBezeichnung(gereatebezeichnung)).thenReturn(hwOnt);
        HWBaugruppe hwBaugruppe = mock(HWBaugruppe.class);
        FTTHOntPortImportView view = createFtthOntPortImportView(schnittstelle, hweqn, gereatebezeichnung);
        doReturn(hwBaugruppe).when(cut).getHWBaugruppe(hwOnt, hwOnt.getOntType(), schnittstelle, "0-1");
        PhysikTyp physikTyp = mock(PhysikTyp.class);
        doReturn(physikTyp).when(cut).getPhysikTyp("FTTH_" + schnittstelle);
        doReturn(true).when(cut).assertPortDoesNotAlreadyExist(hweqn, hwOnt, hwBaugruppe);
        doNothing().when(cut)
                .definePortValues(any(Equipment.class), eq(hweqn), eq(schnittstelle), eq(hwBaugruppe), eq(user),
                        eq(EqStatus.rang));
        doNothing().when(cut)
                .createRangierung4Port(any(Equipment.class), any(Date.class), eq(hwOnt), eq(physikTyp), eq(user));

        cut.generateFTTHOntPort(view, sessionId);
        verify(cut).definePortValues(any(Equipment.class), eq(hweqn), eq(schnittstelle), eq(hwBaugruppe), eq(user),
                eq(EqStatus.rang));
        verify(cut).createRangierung4Port(any(Equipment.class), any(Date.class), eq(hwOnt), eq(physikTyp), eq(user));
    }

    private FTTBHDpoPortImportView createFttbhDpoPortImportView(String schnittstelle, String port, String oltChild) {
        FTTBHDpoPortImportView view = new FTTBHDpoPortImportView();
        view.setSchnittstelle(schnittstelle);
        view.setPort(port);
        view.setOltChild(oltChild);
        return view;
    }

    private FTTHOntPortImportView createFtthOntPortImportView(String schnittstelle, String port, String oltChild) {
        FTTHOntPortImportView view = new FTTHOntPortImportView();
        view.setSchnittstelle(schnittstelle);
        view.setPort(port);
        view.setOltChild(oltChild);
        return view;
    }

    @DataProvider
    private Object[][] getEquipmentDataProvider() {
        return new Object[][] {
                {1L, "L1", "1", "", RangSchnittstelle.FTTB_MNET, false},
                {1L, "L1", "1", "", RangSchnittstelle.FTTB_LVT, false},
                {1L, "L1", "1", "", RangSchnittstelle.FTTB_LZV, false},
                {1L, "L1", "1", null, RangSchnittstelle.FTTB_MNET, false},
                {1L, "L1", "1", null, RangSchnittstelle.LWL, true},
                {1L, "L1", "1", "123", RangSchnittstelle.FTTB_MNET, true},
        };
    }

    @Test(dataProvider = "getEquipmentDataProvider")
    public void getEquipment(Long hvtIdStandort, String leiste, String stift, String hwEqn,
            RangSchnittstelle rangSchnittstelle, boolean expectedException) throws FindException {
        try {
            Equipment equipment = createEquipment(hvtIdStandort, hwEqn, leiste, stift, rangSchnittstelle);
            when(rangierungsService.findEQByLeisteStift(hvtIdStandort, leiste, stift)).thenReturn(equipment);

            assertEquals(cut.getEquipment(hvtIdStandort, leiste, stift), equipment);
        }
        catch (StoreException e) {
            e.printStackTrace();
            assertTrue(expectedException);
        }
    }

    @DataProvider
    private Object[][] getEquipmentNotFoundDataProvider() {
        return new Object[][] {
                {1L, "L1", "1", "01", false},
                {1L, "L1", "01", "01", false},
                {1L, "L1", "1", "01", true},
                {1L, "L1", "01", "01", true},
        };
    }

    @Test(dataProvider = "getEquipmentNotFoundDataProvider")
    public void getEquipmentNotFound(Long hvtIdStandort, String leiste, String stift, String stift2, boolean expectedException) throws FindException {
        try {
            Equipment equipment = null;
            when(rangierungsService.findEQByLeisteStift(hvtIdStandort, leiste, stift)).thenReturn(null);
            if (!expectedException) {
                equipment = createEquipment(hvtIdStandort, null, leiste, stift, RangSchnittstelle.FTTB_MNET);
            }
            when(rangierungsService.findEQByLeisteStift(hvtIdStandort, leiste, stift2)).thenReturn(equipment);

            assertEquals(cut.getEquipment(hvtIdStandort, leiste, stift), equipment);
        }
        catch (StoreException e) {
            e.printStackTrace();
            assertTrue(expectedException);
        }
    }

    @DataProvider
    private Object[][] getDpoEquipmentDataProvider() {
        return new Object[][] {
                {1L, "L1", "1", false},
                {1L, "", "", false},
                {1L, null, null, false},
                {1L, "", "1", true},
                {1L, null, "1", true},
                {1L, "L1", "", true},
                {1L, "L1", null, true},
        };
    }

    @Test(dataProvider = "getDpoEquipmentDataProvider")
    public void getDpoEquipment(Long hvtIdStandort, String leiste, String stift, boolean expectedException) throws FindException {
        try {
            Equipment equipment = createEquipment(hvtIdStandort, null, leiste, stift, RangSchnittstelle.FTTB_MNET);
            doReturn(equipment).when(cut).getEquipment(hvtIdStandort, leiste, stift);

            FTTBHDpoPortImportView view = createDpoPortImportView(leiste, stift);
            Equipment actual = cut.getEquipmentDpoDpu(mockCreateHWDpo().getHvtIdStandort(), view.getLeiste(), view.getStift());
            assertEquals(actual.getHvtIdStandort(), equipment.getHvtIdStandort());
            assertEquals(actual.getRangLeiste1(), "".equals(equipment.getRangLeiste1()) ? null : equipment.getRangLeiste1());
            assertEquals(actual.getRangStift1(), "".equals(equipment.getRangStift1()) ? null : equipment.getRangStift1());
            verify(cut, times(StringUtils.isNotEmpty(leiste) && StringUtils.isNotEmpty(stift) ? 1 : 0))
                    .getEquipment(hvtIdStandort, leiste, stift);
        }
        catch (StoreException e) {
            e.printStackTrace();
            assertTrue(expectedException);
        }
    }

    @DataProvider
    private Object[][] modulnummerDataProvider() {
        return new Object[][] {
                {null, null},
                {"1-0", "0-1"},
                {"1-1", "0-1"},
                {"2-1", "0-2"},
                {"8-1", "0-8"},
        };
    }

    @Test(dataProvider = "modulnummerDataProvider")
    public void getOntModulnummer(String port, String expectedModulnummer) {
        assertEquals(cut.getOntModulnummer(port), expectedModulnummer);
    }

    @DataProvider
    private Object[][] hvtStandortTypDataProvider() {
        return new Object[][] {
                {FTTXStandortImportView.STANDORT_TYP_FTTB, HVTStandort.HVT_STANDORT_TYP_FTTB, false},
                {FTTXStandortImportView.STANDORT_TYP_FTTH, HVTStandort.HVT_STANDORT_TYP_FTTH, false},
                {FTTXStandortImportView.STANDORT_TYP_FTTB_H, HVTStandort.HVT_STANDORT_TYP_FTTB_H, false},
                {FTTXStandortImportView.STANDORT_TYP_FC, HVTStandort.HVT_STANDORT_TYP_FTTX_FC, false},
                {FTTXStandortImportView.STANDORT_TYP_BR, HVTStandort.HVT_STANDORT_TYP_FTTX_BR, false},
                {FTTXStandortImportView.STANDORT_TYP_MVG, null, true},
        };
    }

    @Test(dataProvider = "hvtStandortTypDataProvider")
    public void getHvtStandortTypRefId(String standortTyp, Long expectedStandortRefId, boolean excExpected) {
        FTTXStandortImportView view = new FTTXStandortImportView();
        view.setStandortTyp(standortTyp);
        try {
            assertEquals(cut.getHvtStandortTypRefId(view), expectedStandortRefId);
        }
        catch (StoreException e) {
            if (excExpected) {
                Assert.assertTrue(e.getMessage().contains(standortTyp));
            }
            else {
                e.printStackTrace();
                fail("No exception expected here.");
            }
        }
    }

    private Equipment createEquipment(Long hvtIdStandort, String hwEqn, String leiste, String stift, RangSchnittstelle rangSchnittstelle) {
        Equipment equipment = new Equipment();
        equipment.setHvtIdStandort(hvtIdStandort);
        equipment.setRangSchnittstelle(rangSchnittstelle);
        equipment.setHwEQN(hwEqn);
        equipment.setRangLeiste1(leiste);
        equipment.setRangStift1(stift);
        return equipment;
    }

    private FTTBHDpoPortImportView createDpoPortImportView(String leiste, String stift) {
        FTTBHDpoPortImportView dpoPortImportView = new FTTBHDpoPortImportView();
        dpoPortImportView.setLeiste(leiste);
        dpoPortImportView.setStift(stift);
        return dpoPortImportView;
    }

    private  CPSTransactionResult mockCreateCPSTransactionResult() {

        CPSTransactionResult cpsTransactionResult = new CPSTransactionResult();
        CPSTransaction cpsTransaction = new CPSTransaction();
        cpsTransaction.setId(1234L);
        cpsTransaction.setAuftragId(23456L);
        List<CPSTransaction> cpsTransactions = Lists.newArrayList();
        cpsTransactions.add(cpsTransaction);
        cpsTransactionResult.setCpsTransactions(cpsTransactions);

        return cpsTransactionResult;
    }

    private HWOnt mockCreateHWOnt() {
        HWOnt hwOnt = new HWOnt();
        hwOnt.setGeraeteBez("ONT-000001");
        hwOnt.setOntType("O-123-T");
        fillHWOltChild(hwOnt);
        return hwOnt;
    }

    private HWDpo mockCreateHWDpo() {
        HWDpo hwDpo = new HWDpo();
        hwDpo.setGeraeteBez("DPO-000001");
        hwDpo.setDpoType("O-123-T");
        fillHWOltChild(hwDpo);
        return hwDpo;
    }

    private void fillHWOltChild(HWOltChild hwOltChild) {
        hwOltChild.setOltFrame("1");
        hwOltChild.setOltSubrack("2");
        hwOltChild.setOltSlot("3");
        hwOltChild.setOltGPONPort("4");
        hwOltChild.setOltGPONId("5");
        hwOltChild.setSerialNo("1234");
        hwOltChild.setHvtRaumId(1L);
        hwOltChild.setHwProducer(1L);
        hwOltChild.setHvtIdStandort(1L);
        hwOltChild.setOltRackId(1L);
        hwOltChild.setGueltigBis(DateTools.getHurricanEndDate());
    }
}
