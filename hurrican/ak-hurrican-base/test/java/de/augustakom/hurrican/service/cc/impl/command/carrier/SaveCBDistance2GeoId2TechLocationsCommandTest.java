/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.03.2011 12:12:42
 */

package de.augustakom.hurrican.service.cc.impl.command.carrier;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AvailabilityService;


/**
 * TestNG Klasse fuer {@link SaveCBDistance2GeoId2TechLocationsCommand}
 */
@Test(groups = BaseTest.UNIT)
public class SaveCBDistance2GeoId2TechLocationsCommandTest extends BaseTest {

    private SaveCBDistance2GeoId2TechLocationsCommand cut;
    private Carrierbestellung carrierbestellung;

    // Helper
    private void addEndstelle2List(List<Endstelle> endstellen, Long geoId, Long hvtIdStandort) {
        Endstelle endstelle = new Endstelle();
        endstelle.setGeoId(geoId);
        endstelle.setHvtIdStandort(hvtIdStandort);
        endstellen.add(endstelle);
    }


    // Test
    @BeforeMethod
    public void setUp() {
        cut = new SaveCBDistance2GeoId2TechLocationsCommand();
        carrierbestellung = new Carrierbestellung();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void executeWithoutCarrierbestellung() throws Exception {
        cut.prepare(SaveCBDistance2GeoId2TechLocationsCommand.KEY_SESSION_ID, -1);
        cut.execute();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void executeWithoutSessionId() throws Exception {
        cut.prepare(SaveCBDistance2GeoId2TechLocationsCommand.KEY_CARRIER_ORDER, carrierbestellung);
        cut.execute();
    }

    @Test
    public void writeDistance2EndstellenWithoutValidEndstellen() throws Exception {
        AvailabilityService availabilityServiceMock = mock(AvailabilityService.class);
        cut.setAvailabilityService(availabilityServiceMock);

        List<Endstelle> endstellen = new ArrayList<Endstelle>();
        addEndstelle2List(endstellen, Long.valueOf(1L), null);
        addEndstelle2List(endstellen, null, Long.valueOf(1));

        cut.writeDistance2Endstellen(Long.valueOf(0), endstellen);
        verify(availabilityServiceMock, times(0)).findGeoId2TechLocation(any(Long.class), any(Long.class));
        verify(availabilityServiceMock, times(0))
                .saveGeoId2TechLocation(any(GeoId2TechLocation.class), any(Long.class));
    }

    @Test
    public void writeDistance2EndstellenNotChanged() throws Exception {
        AvailabilityService availabilityServiceMock = mock(AvailabilityService.class);
        cut.setAvailabilityService(availabilityServiceMock);

        Long distance = Long.valueOf(100);
        List<Endstelle> endstellen = new ArrayList<Endstelle>();
        addEndstelle2List(endstellen, Long.valueOf(1L), Long.valueOf(1));
        GeoId2TechLocation geoId2TechLocation = new GeoId2TechLocation();
        geoId2TechLocation.setTalLength(distance.longValue());
        when(availabilityServiceMock.findGeoId2TechLocation(any(Long.class), any(Long.class))).thenReturn(
                geoId2TechLocation);

        cut.writeDistance2Endstellen(distance, endstellen);
        verify(availabilityServiceMock, times(1)).findGeoId2TechLocation(any(Long.class), any(Long.class));
        verify(availabilityServiceMock, times(0))
                .saveGeoId2TechLocation(any(GeoId2TechLocation.class), any(Long.class));
    }

    @Test
    public void writeDistance2EndstellenKeyDouble() throws Exception {
        AvailabilityService availabilityServiceMock = mock(AvailabilityService.class);
        cut.setAvailabilityService(availabilityServiceMock);

        Long distance = Long.valueOf(100);
        List<Endstelle> endstellen = new ArrayList<Endstelle>();
        addEndstelle2List(endstellen, Long.valueOf(1L), Long.valueOf(1));
        addEndstelle2List(endstellen, Long.valueOf(1L), Long.valueOf(1));
        GeoId2TechLocation geoId2TechLocation = new GeoId2TechLocation();
        when(availabilityServiceMock.findGeoId2TechLocation(any(Long.class), any(Long.class))).thenReturn(
                geoId2TechLocation);

        cut.writeDistance2Endstellen(distance, endstellen);
        verify(availabilityServiceMock, times(1)).findGeoId2TechLocation(any(Long.class), any(Long.class));
        verify(availabilityServiceMock, times(1))
                .saveGeoId2TechLocation(any(GeoId2TechLocation.class), any(Long.class));
        assertTrue(NumberTools.equal(geoId2TechLocation.getTalLength(), distance.longValue()), "Distance differ!");
    }

    @Test
    public void writeDistance2EndstellenSuccess() throws Exception {
        AvailabilityService availabilityServiceMock = mock(AvailabilityService.class);
        cut.setAvailabilityService(availabilityServiceMock);

        Long distance = Long.valueOf(100);
        List<Endstelle> endstellen = new ArrayList<Endstelle>();
        addEndstelle2List(endstellen, Long.valueOf(1L), Long.valueOf(1));
        GeoId2TechLocation geoId2TechLocation = new GeoId2TechLocation();
        when(availabilityServiceMock.findGeoId2TechLocation(any(Long.class), any(Long.class))).thenReturn(
                geoId2TechLocation);

        cut.writeDistance2Endstellen(distance, endstellen);
        verify(availabilityServiceMock, times(1)).findGeoId2TechLocation(any(Long.class), any(Long.class));
        verify(availabilityServiceMock, times(1))
                .saveGeoId2TechLocation(any(GeoId2TechLocation.class), any(Long.class));
        assertTrue(NumberTools.equal(geoId2TechLocation.getTalLength(), distance.longValue()), "Distance differ!");
        assertTrue(BooleanTools.nullToFalse(geoId2TechLocation.getTalLengthTrusted()), "The flag 'TAL Length Trusted' ist not set!");
    }

}
