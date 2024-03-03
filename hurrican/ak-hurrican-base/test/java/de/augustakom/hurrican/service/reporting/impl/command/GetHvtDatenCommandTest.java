/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.2009 12:10:22
 */

package de.augustakom.hurrican.service.reporting.impl.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class GetHvtDatenCommandTest extends BaseTest {

    private static final Long TEST_HVT_STANDORT_ID_1 = Long.valueOf(55);
    private static final Long TEST_HVT_STANDORT_ID_2 = Long.valueOf(56);

    public void testReadNoHvtDaten() throws HurricanServiceCommandException {
        GetHvtDatenCommand command = new GetHvtDatenCommand();
        EndstellenService endstellenServiceMock = mock(EndstellenService.class);
        HVTService hvtServiceMock = mock(HVTService.class);
        CarrierService carrierServiceMock = mock(CarrierService.class);

        command.endstellenService = endstellenServiceMock;
        command.hvtService = hvtServiceMock;
        command.carrierService = carrierServiceMock;
        command.map = new HashMap<String, Object>();
        command.readHvtDaten();
        assertNull(command.map.get(GetHvtDatenCommand.KVZNUMMERA));
        assertNull(command.map.get(GetHvtDatenCommand.KVZNUMMERB));
    }

    public void testKvzNummern() throws HurricanServiceCommandException, FindException {
        Long auftragId = Long.MAX_VALUE;
        GetHvtDatenCommand command = new GetHvtDatenCommand();
        EndstellenService endstellenServiceMock = mock(EndstellenService.class);
        RangierungsService rangierungsServiceMock = mock(RangierungsService.class);
        HVTService hvtServiceMock = mock(HVTService.class);
        CarrierService carrierServiceMock = mock(CarrierService.class);

        Equipment equipment1 = new Equipment();
        equipment1.setId(1L);
        equipment1.setHvtIdStandort(TEST_HVT_STANDORT_ID_1);
        equipment1.setKvzNummer("A025");
        equipment1.setRangVerteiler("01K1");

        UEVT uevt1 = new UEVT();
        uevt1.setHvtIdStandort(TEST_HVT_STANDORT_ID_1);
        uevt1.setUevt("01K1");

        Equipment equipment2 = new Equipment();
        equipment2.setId(2L);
        equipment2.setHvtIdStandort(TEST_HVT_STANDORT_ID_2);
        equipment2.setKvzNummer("A031");
        equipment2.setRangVerteiler("01K2");

        UEVT uevt2 = new UEVT();
        uevt2.setHvtIdStandort(TEST_HVT_STANDORT_ID_2);
        uevt2.setUevt("01K2");

        List<Endstelle> endstellenList = new ArrayList<Endstelle>();
        Endstelle es1 = new Endstelle();
        es1.setId(3L);
        es1.setHvtIdStandort(TEST_HVT_STANDORT_ID_1);
        es1.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A);
        endstellenList.add(es1);

        Endstelle es2 = new Endstelle();
        es2.setId(4L);
        es2.setHvtIdStandort(TEST_HVT_STANDORT_ID_2);
        es2.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);
        endstellenList.add(es2);

        when(endstellenServiceMock.findEndstellen4Auftrag(auftragId)).thenReturn(endstellenList);
        when(rangierungsServiceMock.findEquipment4Endstelle(es1, false, true)).thenReturn(equipment1);
        when(rangierungsServiceMock.findEquipment4Endstelle(es2, false, true)).thenReturn(equipment2);

        when(hvtServiceMock.findUEVT(TEST_HVT_STANDORT_ID_1, uevt1.getUevt())).thenReturn(uevt1);
        when(hvtServiceMock.findUEVT(TEST_HVT_STANDORT_ID_2, uevt2.getUevt())).thenReturn(uevt2);

        command.endstellenService = endstellenServiceMock;
        command.hvtService = hvtServiceMock;
        command.carrierService = carrierServiceMock;
        command.rangierungsService = rangierungsServiceMock;
        command.auftragId = auftragId;
        command.map = new HashMap<String, Object>();
        command.readHvtDaten();
        String prefix = command.getPrefix() + AbstractReportCommand.PROPERTY_NAME_SEPARATOR;
        assertEquals(command.map.get(prefix + GetHvtDatenCommand.KVZNUMMERA), "A025");
        assertEquals(command.map.get(prefix + GetHvtDatenCommand.KVZNUMMERB), "A031");
    }

}
