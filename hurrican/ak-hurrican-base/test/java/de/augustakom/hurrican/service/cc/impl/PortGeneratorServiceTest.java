/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 11:55:57
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDluBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.PortGeneratorService;

/**
 * TestNG Klasse fuer {@link PortGeneratorService }
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class PortGeneratorServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Long SESSION_ID = 1L;

    private static final String RANG_LEISTE_SECOND_PORT = "2";
    private static final String RANG_LEISTE_FIRST_PORT = "1";
    private static final String EQN_BASE = "1-1-2-";
    private static final String EQN_BASE_ADSL = "U03-1-000-";
    private static final int PORT_COUNT = 16;

    private PortGeneratorService portGeneratorService;
    private List<PortGeneratorImport> portsToImport;
    private PortGeneratorDetails portGeneratorDetails;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        portGeneratorService = getCCService(PortGeneratorService.class);
    }

    private void buildObjectsForSingleUK0Ports(String hwSchnittstelleName) throws FindException {
        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withName("UK0_TEST")
                .withHwSchnittstelleName(hwSchnittstelleName)
                .withHwTypeName("EWSD_DLU")
                .withPortCount(PORT_COUNT);
        HWDluBuilder hwDlu = getBuilder(HWDluBuilder.class);
        HWBaugruppe hwBG = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(bgTypBuilder)
                .withRackBuilder(hwDlu)
                .build();

        // Testdaten erwarten MUC01
        hwDlu.get().setHwSwitch(getCCService(HWSwitchService.class).findSwitchByName(HWSwitch.SWITCH_MUC01));

        portGeneratorDetails = new PortGeneratorDetails();
        portGeneratorDetails.setCreatePortsForCombiPhysic(Boolean.FALSE);
        portGeneratorDetails.setHwBaugruppenId(hwBG.getId());

        portsToImport = new ArrayList<>();
        String eqnBase = EQN_BASE;
        for (int i = 0; i < PORT_COUNT; i++) {
            PortGeneratorImport pgImport = new PortGeneratorImport();
            pgImport.setHwEqn(eqnBase + i);
            pgImport.setSwitchName(hwDlu.get().getHwSwitch() != null ? hwDlu.get().getHwSwitch().getName() : null);
            portsToImport.add(pgImport);
        }
    }

    private void buildObjectsForAdslPorts() {
        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withName("ADSL_TEST")
                .withHwSchnittstelleName("ADSL")
                .withHwTypeName("XDSL_AGB")
                .withPortCount(PORT_COUNT);
        HWBaugruppe hwBG = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(bgTypBuilder)
                .withRackBuilder(getBuilder(HWDslamBuilder.class))
                .build();

        portGeneratorDetails = new PortGeneratorDetails();
        portGeneratorDetails.setCreatePortsForCombiPhysic(Boolean.TRUE);
        portGeneratorDetails.setHwBaugruppenId(hwBG.getId());

        portsToImport = new ArrayList<>();
        String eqnBase = EQN_BASE_ADSL;
        for (int i = 0; i < PORT_COUNT; i++) {
            PortGeneratorImport pgImport = new PortGeneratorImport();
            pgImport.setHwEqn(eqnBase + i);
            pgImport.setRangLeiste1In(RANG_LEISTE_FIRST_PORT);
            pgImport.setRangLeiste1Out(RANG_LEISTE_SECOND_PORT);
            portsToImport.add(pgImport);
        }
    }

    @Test
    public void testGeneratePorts() throws FindException {
        buildObjectsForSingleUK0Ports("UK0");
        List<Equipment> result = portGeneratorService.generatePorts(portGeneratorDetails, portsToImport, SESSION_ID);
        assertNotEmpty(result, "No equipments created!");
        assertEquals(result.size(), PORT_COUNT, "Count of generated ports is not valid!");
        for (Equipment eq : result) {
            assertNotNull(eq.getHwEQN(), "HW_EQN not defined!");
            assertTrue(eq.getHwEQN().startsWith(EQN_BASE), "HW_EQN is not as expected!");
            assertEquals(eq.getHwSchnittstelle(), Equipment.HW_SCHNITTSTELLE_UK0, "HW-Schnittstelle is not valid");
            assertEquals(eq.getRangSSType(), Equipment.HW_SCHNITTSTELLE_UK0, "Rang-SS-Type not valid!");
            assertNotNull(eq.getHvtIdStandort(), "HVT not defined");
            assertNotNull(eq.getHwBaugruppenId(), "HW-Baugruppe not defined");
        }
    }

    @Test
    public void testGeneratePortsWithCombiPhysic() throws FindException {
        buildObjectsForAdslPorts();
        List<Equipment> result = portGeneratorService.generatePorts(portGeneratorDetails, portsToImport, SESSION_ID);
        assertNotEmpty(result, "No equipments created!");
        assertEquals(result.size(), PORT_COUNT * 2, "Count of generated ports is not valid!");

        int count = 0;
        for (Equipment eq : result) {
            count++;
            assertNotNull(eq.getHwEQN(), "HW_EQN not defined!");
            assertTrue(eq.getHwEQN().startsWith(EQN_BASE_ADSL), "HW_EQN is not as expected!");

            if ((count % 2) != 0) {
                assertEquals(eq.getHwSchnittstelle(), Equipment.HW_SCHNITTSTELLE_ADSL_OUT, "HW-Schnittstelle is not valid");
                assertEquals(eq.getRangSSType(), Equipment.HW_SCHNITTSTELLE_ADSL_OUT, "Rang-SS-Type not valid!");
                assertEquals(eq.getRangLeiste1(), RANG_LEISTE_FIRST_PORT, "RangLeiste1 not valid!");
            }
            else {
                assertEquals(eq.getHwSchnittstelle(), Equipment.HW_SCHNITTSTELLE_ADSL_IN, "HW-Schnittstelle is not valid");
                assertEquals(eq.getRangSSType(), Equipment.HW_SCHNITTSTELLE_ADSL_IN, "Rang-SS-Type not valid!");
                assertEquals(eq.getRangLeiste1(), RANG_LEISTE_SECOND_PORT, "RangLeiste1 not valid!");
            }

            assertNotNull(eq.getHvtIdStandort(), "HVT not defined");
            assertNotNull(eq.getHwBaugruppenId(), "HW-Baugruppe not defined");
        }
    }

    @Test(expectedExceptions = FindException.class)
    public void testGeneratePortsNoStrategy() throws FindException {
        buildObjectsForSingleUK0Ports("dummy");
        portGeneratorService.generatePorts(portGeneratorDetails, portsToImport, SESSION_ID);
    }

    @Test
    public void testGeneratePortsFromFile() throws Exception {
        buildObjectsForSingleUK0Ports("UK0");
        String filename = "equipment_import.xls";
        InputStream fileToImport = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        List<Equipment> result = portGeneratorService.generatePorts(portGeneratorDetails,
                portGeneratorService.retrievePorts(fileToImport, portGeneratorDetails), SESSION_ID);
        assertNotEmpty(result, "No ports generated!");
        assertEquals(result.size(), PORT_COUNT, "count of generated ports not valid!");
    }

}
