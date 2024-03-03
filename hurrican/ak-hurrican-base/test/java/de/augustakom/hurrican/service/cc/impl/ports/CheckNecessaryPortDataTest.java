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
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;


/**
 * TestNG Klasse fuer {@link CheckNecessaryPortData}
 *
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CheckNecessaryPortDataTest {

    private static final PortGeneratorDetails DUMMY_PORTGENERATOR_DETAILS = new PortGeneratorDetails();
    private static final String SWITCH_NAME_DLU = HWSwitch.SWITCH_MUC03;

    private HWDlu createHWDluRackWithSwitch(String switchName) {
        HWDlu dlu = new HWDlu();
        dlu.setHwSwitch(
                new HWSwitchBuilder().setPersist(false)
                        .withName(switchName)
                        .build()
        );
        return dlu;
    }

    private HVTStandort createHVTStandortWithStandortType(Long standortTypRefId) {
        HVTStandort hvtStandort = new HVTStandort();
        hvtStandort.setStandortTypRefId(standortTypRefId);
        return hvtStandort;
    }

    private HWService getMockedHWServiceAndReturnGivenHardware(HWRack rack) throws FindException {
        HWService hwServiceMock = mock(HWService.class);
        when(hwServiceMock.findRackForBaugruppe(anyLong())).thenReturn(rack);
        return hwServiceMock;
    }

    private HVTService getMockedHVTServiceAndReturnGivenStandort(HVTStandort hvtStandort) throws FindException {
        HVTService hvtServiceMock = mock(HVTService.class);
        when(hvtServiceMock.findHVTStandort(anyLong())).thenReturn(hvtStandort);
        return hvtServiceMock;
    }

    private HWService getMockedHWServiceAndThrowFindException() throws FindException {
        HWService hwServiceMock = mock(HWService.class);
        when(hwServiceMock.findRackForBaugruppe(anyLong())).thenThrow(new FindException());
        return hwServiceMock;
    }

    private List<PortGeneratorImport> createPorts(HWSwitch switchId, int size) {
        List<PortGeneratorImport> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PortGeneratorImport port = new PortGeneratorImport();
            port.setHwEqn("00-00-00-" + i);
            port.setSwitchName(switchId != null ? switchId.getName() : null);
            result.add(port);
        }
        return result;
    }


    public void ExecuteCheck() throws FindException {
        HWDlu dlu = createHWDluRackWithSwitch(SWITCH_NAME_DLU);
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        List<PortGeneratorImport> ports = createPorts(dlu.getHwSwitch(), 16);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(dlu));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertTrue(result.isOk(), "Check-Result is invalid but should be OK!");
    }

    public void ExecuteCheckInvalid() throws FindException {
        HWDlu dlu = createHWDluRackWithSwitch(SWITCH_NAME_DLU);
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        dlu.setMediaGatewayName("000");
        List<PortGeneratorImport> ports = createPorts(dlu.getHwSwitch(), 16);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(dlu));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertFalse(result.isOk(), "Check-Result is Ok but should be invalid!");
    }

    public void ExecuteCheckValidSwitch() throws FindException {
        HWDlu dlu = createHWDluRackWithSwitch(SWITCH_NAME_DLU);
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(dlu));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        List<PortGeneratorImport> ports = createPorts(dlu.getHwSwitch(), 16);
        PortGeneratorImport portThatSwitchIdShouldBeChanged = ports.get(0);
        portThatSwitchIdShouldBeChanged.setSwitchName(HWSwitch.SWITCH_MUC03);
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertTrue(result.isOk(), "Check-Result is invalid but should be OK!");
        assertTrue(portThatSwitchIdShouldBeChanged.getSwitchName().equals(SWITCH_NAME_DLU),
                "Switchkennung des Ports sollte die der DLU sein!");
    }

    public void ExecuteCheckInvalidSwitch() throws FindException {
        HWDlu dlu = createHWDluRackWithSwitch(SWITCH_NAME_DLU);
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(dlu));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        List<PortGeneratorImport> ports = createPorts(dlu.getHwSwitch(), 16);
        PortGeneratorImport portThatSwitchIdShouldBeChanged = ports.get(0);
        portThatSwitchIdShouldBeChanged.setSwitchName(HWSwitch.SWITCH_MUC01);
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertFalse(result.isOk(), "Check-Result is Ok but should be invalid!");
    }

    public void ExecuteCheckMissingSwitch_Null() throws FindException {
        HWDlu dlu = createHWDluRackWithSwitch(SWITCH_NAME_DLU);
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(dlu));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        List<PortGeneratorImport> ports = createPorts(dlu.getHwSwitch(), 16);
        PortGeneratorImport portThatSwitchIdShouldBeChanged = ports.get(0);
        portThatSwitchIdShouldBeChanged.setSwitchName(null);
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertFalse(result.isOk(), "Check-Result is Ok but should be invalid!");
    }

    public void ExecuteCheck_CannotFindBaugruppe() throws FindException {
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndThrowFindException());
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS,
                Collections.<PortGeneratorImport>emptyList());
        assertFalse(result.isOk(), "Check-Result is Ok but should be invalid!");
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
    }

    public void ExecuteCheckMissingSwitch_Empty() throws FindException {
        HWDlu dlu = createHWDluRackWithSwitch(SWITCH_NAME_DLU);
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(dlu));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        List<PortGeneratorImport> ports = createPorts(dlu.getHwSwitch(), 16);
        PortGeneratorImport portThatSwitchIdShouldBeChanged = ports.get(0);
        portThatSwitchIdShouldBeChanged.setSwitchName("");
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertFalse(result.isOk(), "Check-Result is Ok but should be invalid!");
    }

    /**
     * Test method for {@link CheckNecessaryPortData}. The {@link HWRack} that will be found by
     * the test data is not a {@link HWDlu} and so no switch will be checked and the result should be ok.
     *
     * @throws FindException
     */
    public void ExecuteCheck_HardwareIsNoDLU() throws FindException {
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(new HWRack()));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        HWSwitch hwSwitch = new HWSwitchBuilder().setPersist(false)
                .withName("")
                .build();
        List<PortGeneratorImport> ports = createPorts(hwSwitch, 1);
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertTrue(result.isOk(), "Check-Result is invalid but should be OK!");
    }

    @Test
    public void ExecuteCheck_CannotFindStandort() throws FindException {
        HWDlu dlu = createHWDluRackWithSwitch(SWITCH_NAME_DLU);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(dlu));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(null));
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS,
                Collections.<PortGeneratorImport>emptyList());
        assertFalse(result.isOk(), "Check-Resul8t is Ok but should be invalid!");
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
    }

    public void ExecuteCheck_FttCKVZAndNoSwitch() throws FindException {
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);
        List<PortGeneratorImport> ports = createPorts(null, 16);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(new HWRack()));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertFalse(result.isOk(), "Check-Result is Ok but should be invalid!");
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
    }

    public void ExecuteCheck_FttCKVZ() throws FindException {
        HVTStandort hvtStandort = createHVTStandortWithStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);
        HWSwitch hwSwitch = new HWSwitchBuilder().setPersist(false)
                .withName(SWITCH_NAME_DLU)
                .build();
        List<PortGeneratorImport> ports = createPorts(hwSwitch, 16);
        CheckNecessaryPortData check = new CheckNecessaryPortData();
        check.setHwService(getMockedHWServiceAndReturnGivenHardware(new HWRack()));
        check.setHvtService(getMockedHVTServiceAndReturnGivenStandort(hvtStandort));
        ServiceCommandResult result = check.executeCheck(DUMMY_PORTGENERATOR_DETAILS, ports);
        assertTrue(result.isOk(), "Check-Result is invalid but should be OK!");
    }

}
