/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 15:45:19
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImportBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * TestNG Klasse fuer {@link CheckExistingPorts}
 */
@Test(groups = BaseTest.UNIT)
public class CheckExistingPortsTest extends BaseTest {

    private CheckExistingPorts checkUnderTest;

    private HWService hwServiceMock;
    private RangierungsService rangierungsServiceMock;

    @BeforeMethod
    public void prepareTest() {
        rangierungsServiceMock = mock(RangierungsService.class);
        hwServiceMock = mock(HWService.class);

        checkUnderTest = new CheckExistingPorts();
        checkUnderTest.setHwService(hwServiceMock);
        checkUnderTest.setRangierungsService(rangierungsServiceMock);
    }

    @DataProvider
    public Object[][] dataProviderExecuteCheck() {
        return new Object[][] {
                { "remains empty", array(String.class), false, array(String.class), 8, false, array(String.class) },
                { "fill empty complete", array(String.class), false, array("hwEQN0", "hwEQN1"), 2, true, array(String.class) },
                { "fill empty half", array(String.class), false, array("hwEQN0", "hwEQN1"), 4, false, array(String.class) },
                { "Baugruppe is full", array("", ""), false, array(String.class), 2, false, array(String.class) },
                { "Baugruppe is full", array("", ""), true, array(String.class), 2, false, array(String.class) },
                { "Too less free ports1", array("hwEQN0", "hwEQN1"), false, array("hwEQN2", "hwEQN3"), 3, false, array(String.class) },
                { "Too less free ports2", array("hwEQN0", "hwEQN1"), false, array("hwEQN0", "hwEQN1"), 3, false, array("hwEQN0", "hwEQN1") },
                { "Too less free ports3", array("hwEQN0", "hwEQN1"), true, array("hwEQN0", "hwEQN1"), 3, false, array("hwEQN0", "hwEQN1") },
                { "Too much free ports1", array("hwEQN0", "hwEQN1"), false, array("hwEQN1", "hwEQN2"), 4, false, array("hwEQN1") },
                { "Too much free ports2", array("hwEQN0", "hwEQN1"), true, array("hwEQN1", "hwEQN2"), 4, false, array("hwEQN1") },
                { "import missing ports1", array("hwEQN0", "hwEQN1"), false, array("hwEQN2", "hwEQN3"), 4, true, array(String.class) },
                { "import missing ports2", array("hwEQN0", "hwEQN1"), false, array("hwEQN1", "hwEQN2"), 3, true, array("hwEQN1") },
                { "import missing ports3", array("hwEQN0", "hwEQN1"), true, array("hwEQN2", "hwEQN3"), 4, true, array(String.class) },
        };
    }

    @Test(dataProvider = "dataProviderExecuteCheck")
    public void testExecuteCheck(String testDescription, String[] existingHwEQN, boolean inclPortsForCombiPhysic,
            String[] hwEQNToGenerate, Integer portCount, boolean expectedResult, String[] alreadyExistingHwEQNs) throws Exception {

        List<Equipment> existingPorts = createPorts(inclPortsForCombiPhysic, existingHwEQN);
        List<PortGeneratorImport> portsToGenerate = createPortsToGenerate(hwEQNToGenerate);

        HWBaugruppe hwBaugruppe = createBaugruppe(portCount);

        PortGeneratorDetails portGeneratorDetails = mock(PortGeneratorDetails.class);
        when(portGeneratorDetails.getHwBaugruppenId()).thenReturn(hwBaugruppe.getId());

        when(rangierungsServiceMock.findEquipments4HWBaugruppe(hwBaugruppe.getId())).thenReturn(existingPorts);
        when(hwServiceMock.findBaugruppe(hwBaugruppe.getId())).thenReturn(hwBaugruppe);

        ServiceCommandResult result = checkUnderTest.executeCheck(portGeneratorDetails, portsToGenerate);

        assertEquals(result.isOk(), expectedResult, "Check-Result is incorrect in " + testDescription + "!");
        int marked = 0;
        for (PortGeneratorImport portToGenerate : portsToGenerate) {
            if (portToGenerate.getPortAlreadyExists()) {
                boolean found = false;
                for (String alreadyExistingHwEQN : alreadyExistingHwEQNs) {
                    if (portToGenerate.getHwEqn().equals(alreadyExistingHwEQN)) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "port (hwEQN = " + portToGenerate.getHwEqn() + ") should be marked as alreadyExistingPort");
                marked++;
            }
        }
        assertEquals(alreadyExistingHwEQNs.length, marked, "more or less ports should be marked as already existing ports");

        // dirty hack to get information whether an exception is thrown or not
        if (!result.isOk() && result.getMessage().startsWith("Error while checking for existing ports:")) {
            fail("Exception was thrown");
        }
    }

    private List<Equipment> createPorts(boolean inclPortsForCombiPhysic, String... hwEQNs) {
        List<Equipment> result = new ArrayList<Equipment>();
        for (String hwEQN : hwEQNs) {
            result.add((new EquipmentBuilder()).withHwEQN(hwEQN).withRangSSType(Equipment.HW_SCHNITTSTELLE_ADSL_OUT)
                    .setPersist(false).build());
            if (inclPortsForCombiPhysic) {
                result.add((new EquipmentBuilder()).withHwEQN(hwEQN).withRangSSType(Equipment.HW_SCHNITTSTELLE_ADSL_IN)
                        .setPersist(false).build());
            }
        }
        return result;
    }

    private List<PortGeneratorImport> createPortsToGenerate(String... hwEQNs) {
        List<PortGeneratorImport> portsToGenerate = new ArrayList<PortGeneratorImport>();
        for (String hwEQN : hwEQNs) {
            portsToGenerate.add((new PortGeneratorImportBuilder()).withHwEqn(hwEQN).setPersist(false).build());
        }
        return portsToGenerate;
    }

    private HWBaugruppe createBaugruppe(Integer hwBaugruppenTypPortCount) {
        return (new HWBaugruppeBuilder()).withRandomId()
                .withBaugruppenTypBuilder((new HWBaugruppenTypBuilder()).withPortCount(hwBaugruppenTypPortCount).setPersist(false))
                .setPersist(false).build();
    }
}
