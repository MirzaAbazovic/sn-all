/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 11:48:19
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.BrasPoolBuilder;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.EQCrossConnectionBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;

/**
 * Service-TestNG Klasse fuer {@link EQCrossConnectionService}
 */
@Test(groups = BaseTest.SERVICE)
public class EQCrossConnectionServiceImplTest extends AbstractHurricanBaseServiceTest {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(EQCrossConnectionServiceImplTest.class);

    private EQCrossConnectionService eqCrossConnectionService;

    private static final String MOD_NUMBER_12 = "1-2";

    private static final String VALID_HUAWEI_DSLAM_TYPE = "MA5600v3";
    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        eqCrossConnectionService = getCCService(EQCrossConnectionService.class);
    }

    /**
     * Test fuer {@link EQCrossConnectionService#saveEQCrossConnection(EQCrossConnection)}
     */
    public void testSaveEQCrossConnection() throws StoreException {
        EQCrossConnection crossConnection = getBuilder(EQCrossConnectionBuilder.class)
                .withValidFrom(new Date())
                .setPersist(false)
                .build();

        eqCrossConnectionService.saveEQCrossConnection(crossConnection);
        Assert.assertNotNull(crossConnection.getId(), "ID of CrossConnection not set");
    }

    @Test(expectedExceptions = StoreException.class)
    public void testSaveEQCrossConnectionValidation() throws StoreException {
        EQCrossConnection crossConnection = getBuilder(EQCrossConnectionBuilder.class)
                .withValidFrom(DateTools.getHurricanEndDate())
                .withValidTo(new Date())
                .setPersist(false)
                .build();
        eqCrossConnectionService.saveEQCrossConnection(crossConnection);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testSaveEQCrossConnectionValidationUserWNotNull() throws StoreException {
        EQCrossConnection crossConnection = getBuilder(EQCrossConnectionBuilder.class)
                .withValidFrom(new Date())
                .withUserW(null)
                .setPersist(false)
                .build();
        eqCrossConnectionService.saveEQCrossConnection(crossConnection);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testSaveEQCrossConnectionBrasPoolValidation() throws StoreException {
        EQCrossConnection crossConnection = getBuilder(EQCrossConnectionBuilder.class)
                .withValidFrom(new Date())
                .withBrasPoolBuilder(getBuilder(BrasPoolBuilder.class).setPersist(false))
                .setPersist(false)
                .withLtInner(12)
                .withBrasInner(null)
                .build();
        eqCrossConnectionService.saveEQCrossConnection(crossConnection);
    }

    public void testSaveEQCrossConnections() throws StoreException, ValidationException {
        List<EQCrossConnection> crossConnections = new ArrayList<EQCrossConnection>();
        crossConnections.add(getBuilder(EQCrossConnectionBuilder.class)
                .withBrasPoolBuilder(getBuilder(BrasPoolBuilder.class).setPersist(false))
                .withValidFrom(DateTools.createDate(2000, 12, 12))
                .withValidTo(DateTools.createDate(2000, 12, 13))
                .setPersist(false)
                .build());
        crossConnections.add(getBuilder(EQCrossConnectionBuilder.class)
                .withBrasPoolBuilder(getBuilder(BrasPoolBuilder.class).setPersist(false))
                .withValidFrom(DateTools.createDate(2000, 12, 14))
                .withValidTo(DateTools.createDate(2000, 12, 15))
                .setPersist(false)
                .build());
        crossConnections.add(getBuilder(EQCrossConnectionBuilder.class)
                .withBrasPoolBuilder(getBuilder(BrasPoolBuilder.class).setPersist(false))
                .withValidFrom(DateTools.createDate(2000, 12, 16))
                .withValidTo(DateTools.createDate(2000, 12, 17))
                .setPersist(false)
                .build());

        eqCrossConnectionService.saveEQCrossConnections(crossConnections);
        for (EQCrossConnection cc : crossConnections) {
            Assert.assertNotNull(cc.getId(), "ID of CrossConnection not set");
        }
    }

    public void testSaveEQCrossConnectionsOverlappingWithDifferentTypes() throws StoreException, ValidationException {
        List<EQCrossConnection> crossConnections = new ArrayList<EQCrossConnection>();
        crossConnections.add(getBuilder(EQCrossConnectionBuilder.class)
                .withBrasPoolBuilder(getBuilder(BrasPoolBuilder.class).setPersist(false))
                .withValidFrom(DateTools.createDate(2000, 12, 12))
                .withValidTo(DateTools.createDate(2000, 12, 13))
                .withCrossConnectionTypeRefId(EQCrossConnection.REF_ID_XCONN_IAD_MGM_RB)
                .setPersist(false)
                .build());
        crossConnections.add(getBuilder(EQCrossConnectionBuilder.class)
                .withBrasPoolBuilder(getBuilder(BrasPoolBuilder.class).setPersist(false))
                .withValidFrom(DateTools.createDate(2000, 12, 13))
                .withValidTo(DateTools.createDate(2000, 12, 15))
                .withCrossConnectionTypeRefId(EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB)
                .setPersist(false)
                .build());

        eqCrossConnectionService.saveEQCrossConnections(crossConnections);
        for (EQCrossConnection cc : crossConnections) {
            Assert.assertNotNull(cc.getId(), "ID of CrossConnection not set");
        }
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testSaveEQCrossConnectionsOfSameTypeWithOverlappingValidDate() throws StoreException, ValidationException {
        List<EQCrossConnection> crossConnections = new ArrayList<EQCrossConnection>();
        crossConnections.add(getBuilder(EQCrossConnectionBuilder.class)
                .withBrasPoolBuilder(getBuilder(BrasPoolBuilder.class).setPersist(false))
                .withValidFrom(DateTools.createDate(2000, 12, 12))
                .withValidTo(DateTools.createDate(2000, 12, 13))
                .setPersist(false)
                .build());
        crossConnections.add(getBuilder(EQCrossConnectionBuilder.class)
                .withBrasPoolBuilder(getBuilder(BrasPoolBuilder.class).setPersist(false))
                .withValidFrom(DateTools.createDate(2000, 12, 13))
                .withValidTo(DateTools.createDate(2000, 12, 15))
                .setPersist(false)
                .build());

        eqCrossConnectionService.saveEQCrossConnections(crossConnections);
    }

    public void testFindEQCrossConnections() throws FindException {
        EQCrossConnection crossConnection = getBuilder(EQCrossConnectionBuilder.class)
                .withValidFrom(new Date())
                .build();

        Assert.assertNotNull(crossConnection.getId(), "ID of CrossConnection not set");

        List<EQCrossConnection> result = eqCrossConnectionService.findEQCrossConnections(crossConnection.getEquipmentId(), new Date());
        assertNotEmpty(result, "CrossConnections not found!");
    }

    private EQCrossConnectionBuilder buildEQCrossConnectionBuilder() {
        return new EQCrossConnectionBuilder()
                .withEquipmentBuilder(new EquipmentBuilder())
                .withReferenceBuilder(new ReferenceBuilder());
    }

    public void testAreCrossConnectionsOverwrittenWithDifference() {
        EQCrossConnectionBuilder eqCCBuilder = buildEQCrossConnectionBuilder();

        EQCrossConnection eqCCExisting = eqCCBuilder.build();
        eqCCExisting.setLtInner(NumberTools.add(eqCCExisting.getLtInner(), Integer.valueOf(10)));
        List<EQCrossConnection> existing = new ArrayList<EQCrossConnection>();
        existing.add(eqCCExisting);

        EQCrossConnection eqCCDefault = eqCCBuilder.build();
        List<EQCrossConnection> defaults = new ArrayList<EQCrossConnection>();
        defaults.add(eqCCDefault);

        EQCrossConnectionServiceImpl service = new EQCrossConnectionServiceImpl();
        boolean overwritten = service.areCrossConnectionsOverwritten(defaults, existing);
        Assert.assertTrue(overwritten, "CrossConnections are interpreted as equals, but should not!");
    }

    public void testAreCrossConnectionsOverwrittenWithDifferenceBecauseOfCCTypes() {
        EQCrossConnectionBuilder eqCCBuilder = buildEQCrossConnectionBuilder();

        EQCrossConnection eqCCExisting = eqCCBuilder.build();
        List<EQCrossConnection> existing = new ArrayList<EQCrossConnection>();
        existing.add(eqCCExisting);

        EQCrossConnection eqCCDefault = eqCCBuilder
                .withCrossConnectionTypeRefId(20001L)
                .build();
        List<EQCrossConnection> defaults = new ArrayList<EQCrossConnection>();
        defaults.add(eqCCDefault);

        EQCrossConnectionServiceImpl service = new EQCrossConnectionServiceImpl();
        boolean overwritten = service.areCrossConnectionsOverwritten(defaults, existing);
        Assert.assertTrue(overwritten, "CrossConnections are interpreted as equals, but should not!");
    }

    public void testAreCrossConnectionsOverwrittenNoDifference() {
        EQCrossConnectionBuilder eqCCBuilder = buildEQCrossConnectionBuilder();

        EQCrossConnection eqCCExisting = eqCCBuilder.build();
        List<EQCrossConnection> existing = new ArrayList<EQCrossConnection>();
        existing.add(eqCCExisting);

        EQCrossConnection eqCCDefault = eqCCBuilder.build();
        List<EQCrossConnection> defaults = new ArrayList<EQCrossConnection>();
        defaults.add(eqCCDefault);

        EQCrossConnectionServiceImpl service = new EQCrossConnectionServiceImpl();
        boolean overwritten = service.areCrossConnectionsOverwritten(defaults, existing);
        Assert.assertFalse(overwritten, "CrossConnections are interpreted as overwritten, but should not!");
    }


    public void testCalculateAlcatelCcs() throws FindException {
        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class);
        dslamBuilder.getHwProducerBuilder().toAlcatel();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withModNumber(MOD_NUMBER_12)
                .withRackBuilder(dslamBuilder);

        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(equipmentBuilder.getHwBaugruppenBuilder().getRackBuilder())
                .build();

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(),
                "", new Date(), false, false, false);
        HWDslam dslam = dslamBuilder.get();

        boolean hasHsi = false;
        boolean hasCpe = false;
        int num = 2;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(8));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(65));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagSDSL()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(878));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagSDSL()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(878));
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(11));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(55));
                assertNull(xConnection.getNtOuter());
                assertEquals(xConnection.getNtInner(), Integer.valueOf(dslam.getOuterTagCpeMgmt()));
                assertNull(xConnection.getBrasOuter());
                assertNull(xConnection.getBrasInner());
                hasCpe = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasCpe);
    }

    public void testCalculateHuaweiCcs() throws Exception {
        boolean hasHsi = false;
        boolean hasCpe = false;
        int connectionCount = 2;

        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class).withDslamType(VALID_HUAWEI_DSLAM_TYPE);
        dslamBuilder.getHwProducerBuilder().toHuawei();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withModNumber(MOD_NUMBER_12)
                .withRackBuilder(dslamBuilder);

        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder())
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2);

        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(equipmentBuilder.getHwBaugruppenBuilder().getRackBuilder())
                .build();

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(),
                "", new Date(), false, false, false);
        HWDslam dslam = dslamBuilder.get();

        assertEquals(result.size(), connectionCount);
        for (int i = 0; i < connectionCount; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                checkHsiXConnection(dslam, xConnection);
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB)) {
                checkCpeXConnection(dslam, xConnection);
                hasCpe = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasCpe);
    }

    private void checkCpeXConnection(HWDslam dslam, EQCrossConnection xConnection) {
        assertEquals(xConnection.getLtOuter(), Integer.valueOf(11));
        assertEquals(xConnection.getLtInner(), Integer.valueOf(55));
        assertNull(xConnection.getNtOuter());
        assertEquals(xConnection.getNtInner(), Integer.valueOf(dslam.getOuterTagCpeMgmt()));
        assertNull(xConnection.getBrasOuter());
        assertNull(xConnection.getBrasInner());
    }

    private void checkHsiXConnection(HWDslam dslam, EQCrossConnection xConnection) {
        assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
        assertEquals(xConnection.getLtInner(), Integer.valueOf(40));
        assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagADSL()));
        assertEquals(xConnection.getNtInner(), Integer.valueOf(2311));
        assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagADSL()));
        assertEquals(xConnection.getBrasInner(), Integer.valueOf(2311));
    }


    public void testCalculateCcsWithBrasPool() throws Exception {
        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class)
                .asAtmDslam();
        dslamBuilder.getHwProducerBuilder().toAlcatel();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withModNumber(MOD_NUMBER_12)
                .withRackBuilder(dslamBuilder);

        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_SDSL_OUT)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(equipmentBuilder.getHwBaugruppenBuilder().getRackBuilder())
                .build();

        BrasPool pool = getBuilder(BrasPoolBuilder.class)
                .withName(BrasPool.ATM_SDSL_POOL_PREFIX + " - Test")
                .withVcMin(66)
                .withVcMax(100)
                .withVp(-7)
                .get();

        int myPool = 1000;
        HWDslam dslam = dslamBuilder.get();
        List<EQCrossConnection> result;
        do {
            result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(),
                    "", new Date(), false, false, false);
            for (EQCrossConnection cc : result) {
                if (EQCrossConnection.REF_ID_XCONN_HSI_XCONN.equals(cc.getCrossConnectionTypeRefId()) &&
                        pool.getId().equals(cc.getBrasPoolId())) {
                    myPool = -1;
                }
            }
        }
        while (myPool > 0);
        assertEquals(myPool, -1, "Im Test angelegter Pool wurde nicht genutzt. Das sollte quasi nie vorkommen, " +
                "in ganz seltenen Faellen (P = (2/3)^1000 ~~ 8e-177) koennte dieser Test allerdings fehlschlagen, " +
                "obwohl der Code in Ordnung ist.");

        boolean hasHsi = false;
        boolean hasCpe = false;
        int num = 2;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(8));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(65));
                assertEquals(xConnection.getNtOuter(), dslam.getVpiSDSL());
                assertEquals(xConnection.getNtInner(), Integer.valueOf(968));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(-7));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(66));
                assertEquals(xConnection.getBrasPoolId(), pool.getId());
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(11));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(55));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getVpiCpeMgmt()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(968));
                assertNull(xConnection.getBrasOuter());
                assertNull(xConnection.getBrasInner());
                hasCpe = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasCpe);
    }


    public void testCalculateAlcatelCcsWithVoip() throws Exception {
        Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder = getBuilder(Auftrag2TechLeistungBuilder.class);
        auftrag2TechLeistungBuilder.getTechLeistungBuilder().withTyp(TechLeistung.TYP_VOIP);
        auftrag2TechLeistungBuilder.build();

        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class);
        dslamBuilder.getHwProducerBuilder().toAlcatel();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withModNumber(MOD_NUMBER_12)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_ADSL_OUT)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder().withSubrackBuilder(subrackBuilder);

        getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(equipmentBuilder.getHwBaugruppenBuilder().getRackBuilder())
                .build();

        Long auftragId = auftrag2TechLeistungBuilder.getAuftragBuilder().get().getAuftragId();
        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(),
                "", new Date(), auftragId);
        HWDslam dslam = dslamBuilder.get();

        boolean hasHsi = false;
        boolean hasIad = false;
        boolean hasVoip = false;
        int num = 3;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(1));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(32));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagADSL()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(878));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagADSL()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(878));
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_IAD_MGM_RB)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(35));
                assertNull(xConnection.getNtOuter());
                assertEquals(xConnection.getNtInner(), Integer.valueOf(dslam.getOuterTagIadMgmt()));
                assertNull(xConnection.getBrasOuter());
                assertNull(xConnection.getBrasInner());
                hasIad = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_VOIP_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(7));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(77));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagVoip()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(2878));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagVoip()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(2878));
                hasVoip = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasIad);
        assertTrue(hasVoip);
    }

    public void testCalculateHuaweiCcsWithVoip() throws Exception {
        Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder = getBuilder(Auftrag2TechLeistungBuilder.class);
        auftrag2TechLeistungBuilder.getTechLeistungBuilder().withTyp(TechLeistung.TYP_VOIP);
        auftrag2TechLeistungBuilder.build();

        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class).withDslamType(VALID_HUAWEI_DSLAM_TYPE);
        dslamBuilder.getHwProducerBuilder().toHuawei();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withModNumber(MOD_NUMBER_12)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder().withSubrackBuilder(subrackBuilder);

        getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(equipmentBuilder.getHwBaugruppenBuilder().getRackBuilder())
                .build();

        Long auftragId = auftrag2TechLeistungBuilder.getAuftragBuilder().get().getAuftragId();
        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(), "", new Date(), auftragId);
        HWDslam dslam = dslamBuilder.get();

        boolean hasHsi = false;
        boolean hasIad = false;
        boolean hasVoip = false;
        int num = 3;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                checkHsiXConnection(dslam, xConnection);
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_IAD_MGM_RB)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(3));
                assertNull(xConnection.getNtOuter());
                assertEquals(xConnection.getNtInner(), Integer.valueOf(dslam.getOuterTagIadMgmt()));
                assertNull(xConnection.getBrasOuter());
                assertNull(xConnection.getBrasInner());
                hasIad = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_VOIP_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(200));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagVoip()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(4311));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagVoip()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(4311));
                hasVoip = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasIad);
        assertTrue(hasVoip);
    }


    public void testCalculateAlcatelCcsWithoutVoip() throws Exception {
        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class);
        dslamBuilder.getHwProducerBuilder().toAlcatel();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withModNumber(MOD_NUMBER_12)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_ADSL_OUT)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(equipmentBuilder.getHwBaugruppenBuilder().getRackBuilder())
                .build();

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(), "", new Date(), null);
        HWDslam dslam = dslamBuilder.get();

        boolean hasHsi = false;
        boolean hasIad = false;
        int num = 2;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(1));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(32));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagADSL()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(878));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagADSL()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(878));
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(11));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(55));
                assertNull(xConnection.getNtOuter());
                assertEquals(xConnection.getNtInner(), Integer.valueOf(dslam.getOuterTagCpeMgmt()));
                assertNull(xConnection.getBrasOuter());
                assertNull(xConnection.getBrasInner());
                hasIad = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasIad);
    }

    public void testCalculateHuaweiCcsWithoutVoip() throws Exception {
        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class).withDslamType(VALID_HUAWEI_DSLAM_TYPE);
        dslamBuilder.getHwProducerBuilder().toHuawei();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withModNumber(MOD_NUMBER_12)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(equipmentBuilder.getHwBaugruppenBuilder().getRackBuilder())
                .build();

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(), "", new Date(), null);
        HWDslam dslam = dslamBuilder.get();

        boolean hasHsi = false;
        boolean hasIad = false;
        int num = 2;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection cc = result.get(i);
            if (cc.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                checkHsiXConnection(dslam, cc);
                hasHsi = true;
            }
            else if (cc.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB)) {
                checkCpeXConnection(dslam, cc);
                hasIad = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasIad);
    }


    public void testCalculateAlcatelVdslCcsWithVoip() throws Exception {
        Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder = getBuilder(Auftrag2TechLeistungBuilder.class);
        auftrag2TechLeistungBuilder.getTechLeistungBuilder().withTyp(TechLeistung.TYP_VOIP);
        auftrag2TechLeistungBuilder.build();

        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class)
                .withOuterTagAdsl(Integer.valueOf(405))
                .withOuterTagSdsl(Integer.valueOf(405))
                .withOuterTagVoip(Integer.valueOf(2405))
                .withOuterTagCpeMgmt(Integer.valueOf(4))
                .withOuterTagIadMgmt(Integer.valueOf(3))
                .withBrasOuterTagADSL(Integer.valueOf(405))
                .withBrasOuterTagSDSL(Integer.valueOf(405))
                .withBrasOuterTagVoip(Integer.valueOf(2405))
                .withPhysikArt(HWDslam.ETHERNET)
                .withCcOffset(480);
        dslamBuilder.getHwProducerBuilder().toAlcatel();
        dslamBuilder.build();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwEQN("1-1-1-01")
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        Long auftragId = auftrag2TechLeistungBuilder.getAuftragBuilder().get().getAuftragId();
        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(), "", new Date(), auftragId);
        HWDslam dslam = dslamBuilder.get();

        boolean hasHsi = false;
        boolean hasIad = false;
        boolean hasVoip = false;
        int num = 3;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(40));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagADSL()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(491));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagADSL()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(491));
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_IAD_MGM_RB)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(3));
                assertNull(xConnection.getNtOuter());
                assertEquals(xConnection.getNtInner(), Integer.valueOf(dslam.getOuterTagIadMgmt()));
                assertNull(xConnection.getBrasOuter());
                assertNull(xConnection.getBrasInner());
                hasIad = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_VOIP_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(200));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagVoip()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(2491));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagVoip()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(2491));
                hasVoip = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasIad);
        assertTrue(hasVoip);
    }


    public void testCalculateHuaweiVdslCcsWithVoip() throws Exception {
        Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder = getBuilder(Auftrag2TechLeistungBuilder.class);
        auftrag2TechLeistungBuilder.getTechLeistungBuilder().withTyp(TechLeistung.TYP_VOIP);
        auftrag2TechLeistungBuilder.build();

        Long auftragId = auftrag2TechLeistungBuilder.getAuftragBuilder().get().getAuftragId();

        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class)
                .withOuterTagAdsl(Integer.valueOf(405))
                .withOuterTagSdsl(Integer.valueOf(405))
                .withOuterTagVoip(Integer.valueOf(2405))
                .withOuterTagCpeMgmt(Integer.valueOf(4))
                .withOuterTagIadMgmt(Integer.valueOf(3))
                .withBrasOuterTagADSL(Integer.valueOf(405))
                .withBrasOuterTagSDSL(Integer.valueOf(405))
                .withBrasOuterTagVoip(Integer.valueOf(2405))
                .withPhysikArt(HWDslam.ETHERNET)
                .withCcOffset(480)
                .withDslamType(VALID_HUAWEI_DSLAM_TYPE);
        dslamBuilder.getHwProducerBuilder().toHuawei();
        dslamBuilder.build();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwEQN("1-1-1-01")
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(),
                "", new Date(), auftragId);
        HWDslam dslam = dslamBuilder.get();

        boolean hasHsi = false;
        boolean hasIad = false;
        boolean hasVoip = false;
        int num = 3;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(40));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagADSL()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(2316));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagADSL()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(2316));
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_IAD_MGM_RB)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(3));
                assertNull(xConnection.getNtOuter());
                assertEquals(xConnection.getNtInner(), Integer.valueOf(dslam.getOuterTagIadMgmt()));
                assertNull(xConnection.getBrasOuter());
                assertNull(xConnection.getBrasInner());
                hasIad = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_VOIP_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(200));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagVoip()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(4316));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagVoip()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(4316));
                hasVoip = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasIad);
        assertTrue(hasVoip);
    }


    public void testCalculateHuaweiAsdlCcsWithIpV6() throws Exception {
        Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder = getBuilder(Auftrag2TechLeistungBuilder.class);
        auftrag2TechLeistungBuilder.getTechLeistungBuilder().withTyp(TechLeistung.TYP_VOIP);
        auftrag2TechLeistungBuilder.build();

        Long auftragId = auftrag2TechLeistungBuilder.getAuftragBuilder().get().getAuftragId();

        Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder2 = getBuilder(Auftrag2TechLeistungBuilder.class);
        auftrag2TechLeistungBuilder2.withAuftragId(auftragId);
        auftrag2TechLeistungBuilder2.getTechLeistungBuilder().withTyp(TechLeistung.TYP_VOIP);
        auftrag2TechLeistungBuilder2.withTechLeistungId(TechLeistung.ID_DYNAMIC_IP_V6);
        auftrag2TechLeistungBuilder2.build();

        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class)
                .withOuterTagAdsl(Integer.valueOf(405))
                .withOuterTagSdsl(Integer.valueOf(405))
                .withOuterTagVoip(Integer.valueOf(2405))
                .withOuterTagCpeMgmt(Integer.valueOf(4))
                .withOuterTagIadMgmt(Integer.valueOf(3))
                .withBrasOuterTagADSL(Integer.valueOf(405))
                .withBrasOuterTagSDSL(Integer.valueOf(405))
                .withBrasOuterTagVoip(Integer.valueOf(2405))
                .withPhysikArt(HWDslam.ETHERNET)
                .withCcOffset(480)
                .withDslamType(VALID_HUAWEI_DSLAM_TYPE);
        dslamBuilder.getHwProducerBuilder().toHuawei();
        dslamBuilder.build();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwEQN("1-1-1-01")
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_ADSL_OUT)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(),
                "", new Date(), auftragId);
        HWDslam dslam = dslamBuilder.get();

        boolean hasHsi = false;
        boolean hasIad = false;
        boolean hasVoip = false;
        int num = 3;
        assertEquals(result.size(), num);
        for (int i = 0; i < num; ++i) {
            EQCrossConnection xConnection = result.get(i);
            if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(40));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagADSL()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(2316));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagADSL()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(2316));
                hasHsi = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_IAD_MGM_RB)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(3));
                assertNull(xConnection.getNtOuter());
                assertEquals(xConnection.getNtInner(), Integer.valueOf(dslam.getOuterTagIadMgmt()));
                assertNull(xConnection.getBrasOuter());
                assertNull(xConnection.getBrasInner());
                hasIad = true;
            }
            else if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_VOIP_XCONN)) {
                assertEquals(xConnection.getLtOuter(), Integer.valueOf(0));
                assertEquals(xConnection.getLtInner(), Integer.valueOf(200));
                assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagVoip()));
                assertEquals(xConnection.getNtInner(), Integer.valueOf(4316));
                assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagVoip()));
                assertEquals(xConnection.getBrasInner(), Integer.valueOf(4316));
                hasVoip = true;
            }
            else {
                fail("Wrong CC type");
            }
        }
        assertTrue(hasHsi);
        assertTrue(hasIad);
        assertTrue(hasVoip);
    }

    public void testAlcatelNuernbergSpecialNoCpeCc() throws Exception {
        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class);
        dslamBuilder.getHvtStandortBuilder().getHvtGruppeBuilder().withNiederlassungId(Niederlassung.ID_NUERNBERG);
        dslamBuilder.getHwProducerBuilder().toAlcatel();
        dslamBuilder.withOuterTagCpeMgmt(null);

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class).withHwSchnittstelle(
                Equipment.HW_SCHNITTSTELLE_ADSL_OUT).withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(), "", new Date(), null);
        HWDslam dslam = dslamBuilder.get();

        assertEquals(result.size(), 1);
        EQCrossConnection xConnection = result.get(0);
        if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
            assertEquals(xConnection.getLtOuter(), Integer.valueOf(1));
            assertEquals(xConnection.getLtInner(), Integer.valueOf(32));
            assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagADSL()));
            assertEquals(xConnection.getNtInner(), Integer.valueOf(110)); // offset 10 + bg 3 (* 48) + port 4
            assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagADSL()));
            assertEquals(xConnection.getBrasInner(), Integer.valueOf(110));
        }
        else {
            fail("Wrong CC type");
        }
    }

    public void testNuernbergSpecialNoCcForSiemens() throws Exception {
        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class);
        dslamBuilder.getHvtStandortBuilder().getHvtGruppeBuilder().withNiederlassungId(Niederlassung.ID_NUERNBERG);
        dslamBuilder.getHwProducerBuilder().toSiemens();

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwSchnittstelle("ADSL-OUT")
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder().withSubrackBuilder(subrackBuilder);

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(), "", new Date(), null);

        assertEquals(result.size(), 0);
    }


    public void testHuaweiLandshut() throws Exception {
        HWDslamBuilder dslamBuilder = getBuilder(HWDslamBuilder.class);
        dslamBuilder.getHvtStandortBuilder().getHvtGruppeBuilder().withNiederlassungId(Niederlassung.ID_LANDSHUT);
        dslamBuilder.getHwProducerBuilder().toAlcatel();
        dslamBuilder.withOuterTagCpeMgmt(null);

        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(dslamBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_SDSL_OUT)
                .withHvtStandortBuilder(dslamBuilder.getHvtStandortBuilder());
        equipmentBuilder.getHwBaugruppenBuilder()
                .withSubrackBuilder(subrackBuilder);

        List<EQCrossConnection> result = eqCrossConnectionService.calculateDefaultCcs(equipmentBuilder.get(), "", new Date(), null);
        HWDslam dslam = dslamBuilder.get();

        assertEquals(result.size(), 2);
        // CcStrategyType -CcType HSI
        EQCrossConnection xConnection = result.get(0);
        if (xConnection.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
            assertEquals(xConnection.getLtOuter(), Integer.valueOf(8));
            assertEquals(xConnection.getLtInner(), Integer.valueOf(65));
            assertEquals(xConnection.getNtOuter(), Integer.valueOf(dslam.getOuterTagSDSL()));
            assertEquals(xConnection.getNtInner(), Integer.valueOf(110)); // offset 10 + bg 3 (* 48) + port 4
            assertEquals(xConnection.getBrasOuter(), Integer.valueOf(dslam.getBrasOuterTagSDSL()));
            assertEquals(xConnection.getBrasInner(), Integer.valueOf(110));
        }
        else {
            fail("Wrong CC type");
        }

        // CcStrategyType -CcType CPE
        EQCrossConnection xConnection1 = result.get(1);
        if (xConnection1.getCrossConnectionTypeRefId().equals(EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB)) {
            assertEquals(xConnection1.getLtOuter(), Integer.valueOf(11));
            assertEquals(xConnection1.getLtInner(), Integer.valueOf(55));
            assertNull(xConnection1.getNtOuter());
            assertNull(xConnection1.getNtInner()); // offset 10 + bg 3 (* 48) + port 4
            assertNull(xConnection1.getBrasOuter());
            assertNull(xConnection1.getBrasInner());
        }
        else {
            fail("Wrong CC type");
        }
    }

}
