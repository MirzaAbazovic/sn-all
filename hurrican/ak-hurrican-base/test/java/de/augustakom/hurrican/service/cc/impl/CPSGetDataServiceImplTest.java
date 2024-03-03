/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 10:55:18
 */
package de.augustakom.hurrican.service.cc.impl;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Unit-Tests fuer CPSGetDataService
 */
@Test(groups = { BaseTest.UNIT })
public class CPSGetDataServiceImplTest extends BaseTest {

    @InjectMocks
    private CPSGetDataServiceImpl cut;

    @BeforeMethod
    public void setUp() {
        cut = new CPSGetDataServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    /**
     * Nicht jeder Auftrag an einem FTTB Standort benoetigt ein DSLAM Profil.
     * FTTX Telefon(Taifun: Telefon-Flat) Auftraege benotigen beispielsweise
     * kein Profil.
     */
    public void createFTTBDataWithNoDSLAMProfile() throws FindException {
        HWOltBuilder hwOltBuilder = new HWOltBuilder();
        HWRack olt = hwOltBuilder.setPersist(false).build();
        HWMdu mdu = (new HWMduBuilder()).withHWRackOltBuilder(hwOltBuilder).setPersist(false).build();
        HVTTechnik manufacturer = (new HVTTechnikBuilder()).setPersist(false).build();
        Equipment eqIn = (new EquipmentBuilder()).setPersist(false).build();
        cut.createFTTBData(null, mdu, olt, manufacturer, eqIn, null);
    }


}


