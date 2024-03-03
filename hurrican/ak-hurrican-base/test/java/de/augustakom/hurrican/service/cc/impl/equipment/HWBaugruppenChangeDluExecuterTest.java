/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2010 08:08:36
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HWDluBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * TestNG Klasse fuer {@link HWBaugruppenChangeDluExecuter}
 */
@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeDluExecuterTest extends BaseTest {

    private HWBaugruppenChangeDluExecuter cut;
    private HWBaugruppenChangeDlu hwBgChangeDlu;
    private HWService hwServiceMock;
    private RangierungsService rangierungsServiceMock;

    private List<HWBaugruppenChangeDluV5> v5Mappings;
    private List<Equipment> dluEquipments;

    @BeforeMethod
    public void setUp() {
        cut = new HWBaugruppenChangeDluExecuter();
        cut.initAKWarnings();

        hwBgChangeDlu = new HWBaugruppenChangeDluBuilder()
                .withDluMediaGatewayNew("MG123456")
                .withDluAccessControllerNew("AC123456")
                .withDluSwitchNew(
                        new HWSwitchBuilder()
                                .withName("MUC123")
                                .build()
                )
                .withDluNumberNew("4567")
                .setPersist(false).build();
        cut.setHwBgChangeDlu(hwBgChangeDlu);

        createSampleData();
        cut.v5Mappings = v5Mappings;
        cut.dluEquipments = dluEquipments;

        hwServiceMock = mock(HWService.class);
        cut.setHwService(hwServiceMock);

        rangierungsServiceMock = mock(RangierungsService.class);
        cut.setRangierungsService(rangierungsServiceMock);
    }

    private void createSampleData() {
        String hwEqnBase = "0000-00-00-{0}";
        v5Mappings = new ArrayList<>();
        dluEquipments = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            String port = StringUtils.leftPad(String.format("%s", i), 2, "0");
            String hwEqn = StringTools.formatString(hwEqnBase, new Object[] { port });

            Equipment eq = new Equipment();
            eq.setId(i);
            eq.setHwEQN(hwEqn);
            dluEquipments.add(eq);

            HWBaugruppenChangeDluV5 v5 = new HWBaugruppenChangeDluV5();
            v5.setDluEquipment(eq);
            v5.setHwEqn(hwEqn);
            v5.setV5Port("v5_" + i);
            v5Mappings.add(v5);
        }
    }

    public void testValidateEquipmentsToV5Mappings() throws FindException {
        cut.validateEquipmentsToV5Mappings();
    }

    @Test(expectedExceptions = FindException.class)
    public void testValidateEquipmentsToV5MappingsErrorBecauseOfDifferentCount() throws FindException {
        cut.dluEquipments.remove(0);
        cut.validateEquipmentsToV5Mappings();
    }

    @Test(expectedExceptions = FindException.class)
    public void testValidateEquipmentsToV5MappingsErrorBecauseOfNoV5Mappings() throws FindException {
        cut.v5Mappings = null;
        cut.validateEquipmentsToV5Mappings();
    }

    @Test(expectedExceptions = FindException.class)
    public void testValidateEquipmentsToV5MappingsErrorBecauseOfNotExpectedV5Mappings() throws FindException {
        cut.hwBgChangeDlu.setDluMediaGatewayNew(null);
        cut.validateEquipmentsToV5Mappings();
    }

    @Test(expectedExceptions = FindException.class)
    public void testValidateEquipmentsToV5MappingsErrorBecauseOfNoDluEquipments() throws FindException {
        cut.dluEquipments = null;
        cut.validateEquipmentsToV5Mappings();
    }

    public void testModifyHwRackDlu() throws StoreException, ValidationException {
        HWDlu dlu = new HWDluBuilder()
                .withDluNumer("1111").withHwSwitchBuilder(new HWSwitchBuilder().withName(HWSwitch.SWITCH_AUG01))
                .setPersist(false).build();

        cut.setDluToChange(dlu);
        cut.modifyHwRackDlu();

        assertEquals(dlu.getDluNumber(), hwBgChangeDlu.getDluNumberNew());
        assertEquals(dlu.getHwSwitch(), hwBgChangeDlu.getDluSwitchNew());
        assertEquals(dlu.getMediaGatewayName(), hwBgChangeDlu.getDluMediaGatewayNew());
        assertEquals(dlu.getAccessController(), hwBgChangeDlu.getDluAccessControllerNew());

        verify(hwServiceMock).saveHWRack(dlu);
    }

    public void testGetV5Mapping() {
        for (Equipment eq : dluEquipments) {
            HWBaugruppenChangeDluV5 v5Match = cut.getV5Mapping(eq);
            assertNotNull(v5Match);
            assertEquals(v5Match.getDluEquipment().getId(), eq.getId());
        }
    }

    public void testChangeDluNumberOfHwEqn() {
        String hwEqn = "0000-00-00-01";
        String dluNumber = "1111";
        String modified = cut.changeDluNumberOfHwEqn(hwEqn, dluNumber);
        assertEquals(modified, "1111-00-00-01");
    }

    public void testChangeDluNumberOfHwEqnWithAutoPaddedDluNumber() {
        String hwEqn = "0000-00-00-01";
        String dluNumber = "111";
        String modified = cut.changeDluNumberOfHwEqn(hwEqn, dluNumber);
        assertEquals(modified, "0111-00-00-01");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testChangeDluNumberOfHwEqnErrorBecauseOfMissingHwEqn() {
        cut.changeDluNumberOfHwEqn(null, "1111");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testChangeDluNumberOfHwEqnErrorBecauseOfMissingDluNumber() {
        cut.changeDluNumberOfHwEqn("0000-00-00-01", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testChangeDluNumberOfHwEqnErrorBecauseOfInvalidHwEqn() {
        cut.changeDluNumberOfHwEqn("0000-00-00-01-1",  "1111");
    }

    public void testModifyEquipments() throws StoreException {
        cut.modifyEquipments();

        int i = 0;
        String newHwEqnBase = "{0}-00-00-{1}";
        for (Equipment eq : dluEquipments) {
            String port = StringUtils.leftPad(String.format("%s", i), 2, "0");
            String dluNumberNew = StringUtils.leftPad(hwBgChangeDlu.getDluNumberNew(), 4, "0");
            String hwEqn = StringTools.formatString(newHwEqnBase, new Object[] { dluNumberNew, port });

            assertEquals(eq.getHwEQN(), hwEqn);
            assertEquals(eq.getV5Port(), v5Mappings.get(i).getV5Port());
            assertEquals(eq.getHwSwitch(), hwBgChangeDlu.getDluSwitchNew());
            i++;
        }
        verify(rangierungsServiceMock, times(dluEquipments.size())).saveEquipment(any(Equipment.class));
    }

    public void testModifyEquipmentsWithoutV5() throws StoreException {
        cut.v5Mappings = null;
        cut.hwBgChangeDlu.setDluMediaGatewayNew(null);
        cut.hwBgChangeDlu.setDluAccessControllerNew(null);

        cut.modifyEquipments();

        int i = 0;
        String newHwEqnBase = "{0}-00-00-{1}";
        for (Equipment eq : dluEquipments) {
            String port = StringUtils.leftPad(String.format("%s", i), 2, "0");
            String dluNumberNew = StringUtils.leftPad(hwBgChangeDlu.getDluNumberNew(), 4, "0");
            String hwEqn = StringTools.formatString(newHwEqnBase, new Object[] { dluNumberNew, port });

            assertEquals(eq.getHwEQN(), hwEqn);
            assertEquals(eq.getV5Port(), null);
            assertEquals(eq.getHwSwitch(), hwBgChangeDlu.getDluSwitchNew());
            i++;
        }
        verify(rangierungsServiceMock, times(dluEquipments.size())).saveEquipment(any(Equipment.class));
    }

}
