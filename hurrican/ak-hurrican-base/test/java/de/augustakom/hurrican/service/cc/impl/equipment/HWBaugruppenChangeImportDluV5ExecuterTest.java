/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2010 09:38:57
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;


/**
 * TestNG Klasse fuer {@link HWBaugruppenChangeImportDluV5Executer}
 */
@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeImportDluV5ExecuterTest extends BaseTest {

    private HWBaugruppenChangeImportDluV5Executer cut;

    @BeforeMethod
    public void setUp() {
        cut = new HWBaugruppenChangeImportDluV5Executer();

        List<Equipment> dluEquipments = new ArrayList<Equipment>();
        dluEquipments.add(new EquipmentBuilder().withRandomId().withHwEQN("0000-01-01-01").setPersist(false).build());
        dluEquipments.add(new EquipmentBuilder().withRandomId().withHwEQN("0000-01-01-02").setPersist(false).build());
        dluEquipments.add(new EquipmentBuilder().withRandomId().withHwEQN("0000-01-01-03").setPersist(false).build());
        cut.dluEquipments = dluEquipments;

        Map<String, String> v5Mappings = new HashMap<String, String>();
        v5Mappings.put("0000-01-01-01", "1111-11-11-11");
        v5Mappings.put("0000-01-01-02", "2222-22-22-22");
        v5Mappings.put("0000-01-01-04", "4444-44-44-44");
        cut.hwEqnToV5PortMappings = v5Mappings;
    }


    public void testFindMissingPortsInMappingFile() {
        List<Equipment> result = cut.findMissingPortsInMappingFile();
        assertNotEmpty(result, "es wurde ein Equipment ohne V5-Mapping Referenz erwartet, aber keines gefunden!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getHwEQN(), "0000-01-01-03");
    }


    public void testFindV5MappingWithoutEquipment() {
        Map<String, String> result = cut.findV5MappingWithoutEquipment();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result.keySet().iterator().next(), "0000-01-01-04");
    }


    @Test(expectedExceptions = StoreException.class)
    public void testValidateV5Mappings() throws StoreException {
        try {
            cut.validateV5Mappings();
        }
        catch (StoreException e) {
            String message = e.getMessage();
            assertTrue(StringUtils.contains(message, "0000-01-01-03"));
            assertTrue(StringUtils.contains(message, "0000-01-01-04/4444-44-44-44"));
            throw e;
        }
    }


    public void testReadFile() throws Exception {
        String filename = "rdlu_mapping.xls";
        InputStream fileToImport = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

        cut.setFileToImport(fileToImport);
        cut.readFile();

        assertNotNull(cut.hwEqnToV5PortMappings);
        assertEquals(cut.hwEqnToV5PortMappings.size(), 16, "Anzahl der eingelesenen V5-Port Mappings nicht i.O.");
    }


    public void testCreateAndSaveV5PortMappings() throws StoreException {
        List<Equipment> dluEquipments = new ArrayList<Equipment>();
        dluEquipments.add(new EquipmentBuilder().withRandomId().withHwEQN("0000-01-01-01").setPersist(false).build());
        dluEquipments.add(new EquipmentBuilder().withRandomId().withHwEQN("0000-01-01-02").setPersist(false).build());
        cut.dluEquipments = dluEquipments;

        Map<String, String> v5Mappings = new HashMap<String, String>();
        v5Mappings.put("0000-01-01-01", "1111-11-11-11");
        v5Mappings.put("0000-01-01-02", "2222-22-22-22");
        cut.hwEqnToV5PortMappings = v5Mappings;

        HWBaugruppenChangeService hwBgChangeServiceMock = mock(HWBaugruppenChangeService.class);
        cut.setHwBaugruppenChangeService(hwBgChangeServiceMock);
        cut.setHwBgChangeDlu(new HWBaugruppenChangeDlu());

        cut.createAndSaveV5PortMappings();

        verify(hwBgChangeServiceMock, times(2)).saveHWBaugruppenChangeDluV5(any(HWBaugruppenChangeDluV5.class));
    }

}


