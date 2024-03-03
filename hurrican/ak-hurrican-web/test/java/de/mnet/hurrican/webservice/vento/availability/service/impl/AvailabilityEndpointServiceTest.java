/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.13
 */
package de.mnet.hurrican.webservice.vento.availability.service.impl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.AvailabilityServiceHelper;
import de.augustakom.hurrican.service.location.LocationNotificationHelper;

@Test(groups = BaseTest.UNIT)
public class AvailabilityEndpointServiceTest extends BaseTest {

    @Mock
    private AvailabilityService availabilityService;
    @Mock
    private AvailabilityServiceHelper availabilityServiceHelper;
    @Mock
    private LocationNotificationHelper locationNotificationHelper;

    @InjectMocks
    @Spy
    private AvailabilityEndpointServiceImpl cut;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    /**
     * Test lediglich als Unit-Test um zu ueberpruefen, ob vor dem Aufruf von getAvailabilityInformation ein synch der
     * GeoIds durchgefuehrt wird. <br> Die beiden aufgerufenen Methoden (getAvailabilityInformation und
     * synchGeoId2TechLocation) sind bereits ueber eigene Service-Tests abgedeckt.
     */
    public void testGetAvailabilityInformation() throws Exception {
        GeoId geoId = new GeoIdBuilder()
                .withOnkz("0123456")
                .withAsb("1111")
                .setPersist(false).build();

        VentoGetAvailabilityInformationRequest request = new VentoGetAvailabilityInformationRequest();
        request.setGeoId(geoId.getId());

        Mockito.when(availabilityServiceHelper.findOrCreateGeoId(request.getGeoId(), null)).thenReturn(geoId);

        cut.getAvailabilityInformation(request);

        Mockito.verify(availabilityServiceHelper).findOrCreateGeoId(request.getGeoId(), null);
        Mockito.verify(locationNotificationHelper).processExistingGeoId(geoId, -1L, null);
    }
}
