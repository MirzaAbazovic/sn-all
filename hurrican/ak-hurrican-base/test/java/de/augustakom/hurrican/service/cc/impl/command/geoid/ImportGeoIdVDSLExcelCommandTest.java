package de.augustakom.hurrican.service.cc.impl.command.geoid;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Spy;
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
 * Test-Klasse fuer {@link ImportGeoIdVDSLExcelCommand}
 */
@Test(groups = BaseTest.UNIT)
public class ImportGeoIdVDSLExcelCommandTest extends BaseTest {

    @Spy
    private ImportGeoIdVDSLExcelCommand vdslImport;

    @Mock
    private HVTService hvtServiceMock;
    @Mock
    private AvailabilityService availabilityServiceMock;

    private GeoIdImportView importView;

    @BeforeMethod
    public void setUp() throws FindException, StoreException, ServiceNotFoundException {
        initMocks(this);

        vdslImport.hvtService = hvtServiceMock;

        HVTStandort hvtStandort = new HVTStandortBuilder().setPersist(false).build();
        when(hvtServiceMock.findHVTStandortByBezeichnung(any(String.class))).thenReturn(hvtStandort);

        vdslImport.availabilityService = availabilityServiceMock;

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
    public void importGeoIdVDSLRowSuccess() throws FindException, StoreException, ServiceNotFoundException {
        when(availabilityServiceMock.findGeoId2TechLocation(any(Long.class), any(Long.class))).thenReturn(null);
        vdslImport.importGeoIdFromRow(importView);
        verify(availabilityServiceMock, times(1))
                .saveGeoId2TechLocation(any(GeoId2TechLocation.class), any(Long.class));
    }

    private static class GeoId2TechLocationWhereVdslIsAvailable extends ArgumentMatcher<GeoId2TechLocation> {
        @Override
        public boolean matches(Object o) {
            return (((GeoId2TechLocation) o).getVdslAnHvtAvailableSince() != null);
        }
    }

    @Test
    public void importGeoIdVDSLRowMappingExists() throws FindException, StoreException, ServiceNotFoundException {
        GeoId2TechLocation mapping = new GeoId2TechLocationBuilder().build();
        when(availabilityServiceMock.findGeoId2TechLocation(any(Long.class), any(Long.class))).thenReturn(mapping);

        vdslImport.importGeoIdFromRow(importView);
        verify(availabilityServiceMock, times(1)).saveGeoId2TechLocation(
                argThat(new GeoId2TechLocationWhereVdslIsAvailable()), any(Long.class));
    }


    @Test(expectedExceptions = FindException.class)
    public void importGeoIdVDSLRowInvalidHvt() throws FindException, StoreException, ServiceNotFoundException {
        when(hvtServiceMock.findHVTStandortByBezeichnung(importView.hvtName)).thenReturn(null);
        vdslImport.importGeoIdFromRow(importView);
    }

    @Test
    public void testImportGeoIds() throws FindException, StoreException, ServiceNotFoundException {
        doNothing().when(vdslImport).importGeoIdFromRow(any(GeoIdImportView.class));
        vdslImport.fileName = Thread.currentThread().getContextClassLoader()
                .getResource("Strassenimport_fuer_VDSL_an_HVT.xls").getPath();
        vdslImport.importGeoIds();
        assertTrue(vdslImport.successList.size() == 1);
    }
}


