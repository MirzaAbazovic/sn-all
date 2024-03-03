/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2011 13:20:38
 */
package de.augustakom.hurrican.service.cc.impl.command.geoid;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Test-Klasse fuer {@link ImportGeoIdKVZExcelCommand}
 */
@Test(groups = BaseTest.UNIT)
public class ImportGeoIdKVZExcelCommandTest extends BaseTest {

    private ImportGeoIdKVZExcelCommand kvzImport;

    private HVTService hvtServiceMock;
    private AvailabilityService availabilityServiceMock;
    private GeoIdImportView importView;

    @BeforeMethod
    public void setUp() throws FindException, StoreException, ServiceNotFoundException {
        kvzImport = new ImportGeoIdKVZExcelCommand();

        hvtServiceMock = mock(HVTService.class);
        kvzImport.hvtService = hvtServiceMock;

        HVTStandort hvtStandort = new HVTStandortBuilder().setPersist(false).build();
        when(hvtServiceMock.findHVTStandortByBezeichnung(any(String.class))).thenReturn(hvtStandort);

        availabilityServiceMock = mock(AvailabilityService.class);
        kvzImport.availabilityService = availabilityServiceMock;

        importView = new GeoIdImportView();
        importView.hvtName = "hvt";
        importView.street = "street";
        importView.houseNum = "houseNum";
        importView.houseNumExt = "houseNumExt";
        importView.zipCode = "zipCode";
        importView.city = "city";
        importView.distance = "123";

        List<GeoId> geoIds = new ArrayList<GeoId>();
        geoIds.add(new GeoIdBuilder().setPersist(false).build());
        when(availabilityServiceMock.mapLocationDataToGeoIds(importView.street,
                importView.houseNum,
                importView.houseNumExt,
                importView.zipCode,
                importView.city,
                null)).thenReturn(geoIds);
    }

    @Test
    public void importGeoIdKVZRowSuccess() throws FindException, StoreException, ServiceNotFoundException {
        when(availabilityServiceMock.findGeoId2TechLocation(any(Long.class), any(Long.class))).thenReturn(null);

        kvzImport.importGeoIdFromRow(importView);
        verify(availabilityServiceMock, times(1))
                .saveGeoId2TechLocation(any(GeoId2TechLocation.class), any(Long.class));
    }

    @Test
    public void importGeoIdKVZRowMappingExists() throws FindException, StoreException, ServiceNotFoundException {
        GeoId2TechLocation mapping = new GeoId2TechLocationBuilder().setPersist(false).build();
        when(availabilityServiceMock.findGeoId2TechLocation(any(Long.class), any(Long.class))).thenReturn(mapping);

        kvzImport.importGeoIdFromRow(importView);
        verify(availabilityServiceMock, times(0))
                .saveGeoId2TechLocation(any(GeoId2TechLocation.class), any(Long.class));
    }

    @Test(expectedExceptions = FindException.class)
    public void importGeoIdKVZRowInvalidHvt() throws FindException, StoreException, ServiceNotFoundException {
        when(hvtServiceMock.findHVTStandortByBezeichnung(importView.hvtName)).thenReturn(null);
        kvzImport.importGeoIdFromRow(importView);
    }

}


