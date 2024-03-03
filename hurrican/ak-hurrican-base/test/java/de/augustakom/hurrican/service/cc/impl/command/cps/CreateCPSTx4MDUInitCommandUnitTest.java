/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.11.2010 10:21:17
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMduInitializeData;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;

/**
 * Unit-Test fuer {@link CreateCPSTx4MDUInitCommand}
 */
@Test(groups = { "unit" })
public class CreateCPSTx4MDUInitCommandUnitTest extends BaseTest {

    private static final Long RACK_ID_MDU = Long.valueOf(Integer.MAX_VALUE);
    private static final Long RACK_ID_OLT = Long.valueOf(Integer.MAX_VALUE - 1);

    private CreateCPSTx4MDUInitCommand cut;
    private HWService hwServiceMock;
    private HVTService hvtServiceMock;

    private HWMdu mdu;
    private HWOlt olt;
    private HVTTechnik hvtTechnik;
    private HVTGruppe hvtGruppe;

    @BeforeMethod
    public void setUp() throws FindException {
        cut = new CreateCPSTx4MDUInitCommand();
        cut.mduInitData = new CPSMduInitializeData();

        hwServiceMock = mock(HWService.class);
        hvtServiceMock = mock(HVTService.class);
        cut.setHwService(hwServiceMock);
        cut.setHvtService(hvtServiceMock);
        cut.hwRackId = RACK_ID_MDU;
        cut.sendUpdate = Boolean.FALSE;
        cut.useInitialized = false;

        HWOltBuilder oltBuilder = new HWOltBuilder();
        olt = oltBuilder.setPersist(false).withId(RACK_ID_OLT).build();
        mdu = new HWMduBuilder().setPersist(false).withId(RACK_ID_MDU).withHWRackOltBuilder(oltBuilder).build();
        hvtTechnik = new HVTTechnikBuilder().setPersist(false).build();
        hvtGruppe = new HVTGruppeBuilder().setPersist(false).withOrtsteil("test").build();

        when(hwServiceMock.findRackById(RACK_ID_MDU)).thenReturn(mdu);
        when(hwServiceMock.findRackById(RACK_ID_OLT)).thenReturn(olt);

        when(hvtServiceMock.findHVTTechnik(any(Long.class))).thenReturn(hvtTechnik);
        when(hvtServiceMock.findHVTGruppe4Standort(any(Long.class))).thenReturn(hvtGruppe);
    }

    public void testCreateMIData() throws HurricanServiceCommandException {
        cut.createMIData();
        assertEquals(cut.mduInitData.getGponPort(), "00-01-02-03-04");
    }

    public void testCreateMIDataWithoutSubrack() throws HurricanServiceCommandException {
        mdu.setOltSubrack(null);
        cut.createMIData();
        assertEquals(cut.mduInitData.getGponPort(), "00-02-03-04");
    }

}
