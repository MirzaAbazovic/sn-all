/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2011 08:01:56
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.exceptions.AvailabilityException;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GeoIdCarrierAddress;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.KvzSperreBuilder;
import de.augustakom.hurrican.model.cc.query.GeoIdSearchQuery;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityServiceHelper;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * TestNG UnitTest fuer {@link AvailabilityServiceImpl}
 */
@Test(groups = BaseTest.UNIT)
public class AvailabilityServiceImplTest extends BaseTest {

    @InjectMocks
    @Spy
    private AvailabilityServiceImpl testling;
    @Mock
    private AvailabilityServiceHelper serviceHelper;
    @Mock
    private HVTService hvtService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public void testCorrectAddressWithGeoIdForCb() throws Exception {
        GeoId geoId = new GeoIdBuilder().build();

        GeoIdCarrierAddress carrierAddress = new GeoIdCarrierAddress();
        carrierAddress.setHouseNum("56");
        carrierAddress.setCity("Aystetten-dtag");
        carrierAddress.setStreet("Hauptstrasse-dtag");
        carrierAddress.setZipCode("86482");

        geoId.getCarrierAddresses().put(GeoIdCarrierAddress.CARRIER_DTAG, carrierAddress);

        doReturn(geoId).when(serviceHelper).findGeoId(geoId.getId());

        CCAddress baseAddress = new CCAddress();
        baseAddress.setName("Hans Meiser");
        baseAddress.setStrasse("Hauptstrasse");
        baseAddress.setPlz("86482");
        baseAddress.setOrt("Aystetten");

        AddressModel dtagAddress = testling.getDtagAddressForCb(geoId.getId(), baseAddress);

        assertEquals(dtagAddress.getOrt(), "Aystetten-dtag");
        assertEquals(dtagAddress.getName(), "Hans Meiser");
        assertEquals(dtagAddress.getNummer(), "56");
        assertEquals(dtagAddress.getPlz(), "86482");
        assertEquals(dtagAddress.getStrasse(), "Hauptstrasse-dtag");
    }

    public void testCorrectAddressWithGeoIdForCbWithoutGeoId() throws Exception {
        CCAddress baseAddress = new CCAddress();
        AddressModel dtagAddress = testling.getDtagAddressForCb(null, baseAddress);
        assertEquals(dtagAddress, baseAddress);
    }

    public void testFindOnkzAndAsbForGeoId_success() throws FindException, ServiceNotFoundException {
        Long geoId = RandomTools.createLong();
        Pair<Integer, String> retValExp = new Pair<Integer, String>(RandomTools.createInteger(), "asdf");
        GeoId geoIdEntity = new GeoId();
        geoIdEntity.setAsb(retValExp.getFirst().toString());
        geoIdEntity.setOnkz(retValExp.getSecond());
        doReturn(geoIdEntity).when(serviceHelper).findGeoId(geoId);
        Pair<Integer, String> retVal = testling.findAsbAndOnKzForGeoId(geoId);
        verify(serviceHelper, times(1)).findGeoId(eq(geoId));
        assertEquals(retVal.getFirst(), retValExp.getFirst());
        assertEquals(retVal.getSecond(), retValExp.getSecond());
    }

    @Test(expectedExceptions = { AvailabilityException.class })
    public void testFindOnkzAndAsbForGeoId_WithException() throws ServiceNotFoundException, FindException {
        Long geoId = RandomTools.createLong();
        doThrow(new AvailabilityException()).when(serviceHelper).findGeoId(geoId);
        testling.findAsbAndOnKzForGeoId(geoId);
    }


    @Test
    public void testMoveKvzLocationsToHvt() throws FindException, StoreException {
        HVTStandortBuilder currentHvt = new HVTStandortBuilder().withId(98L);
        HVTStandortBuilder futureHvt = new HVTStandortBuilder().withId(99L);

        KvzSperre kvzSperre = new KvzSperreBuilder().setPersist(false).build();

        GeoIdBuilder geoId1 = new GeoIdBuilder().withId(1L).setPersist(false);
        GeoId2TechLocation geoId2TechLocation1 = new GeoId2TechLocationBuilder()
                .withGeoIdBuilder(geoId1)
                .withHvtStandortBuilder(currentHvt)
                .setPersist(false).build();

        GeoIdBuilder geoId2 = new GeoIdBuilder().withId(2L).setPersist(false);
        GeoId2TechLocation geoId2TechLocation2 = new GeoId2TechLocationBuilder()
                .withGeoIdBuilder(geoId2)
                .withHvtStandortBuilder(currentHvt)
                .setPersist(false).build();

        doReturn(Arrays.asList(geoId1.get(), geoId2.get()))
                .when(testling).findGeoIdsBySearchQuery(any(GeoIdSearchQuery.class));

        doReturn(geoId2TechLocation1).when(testling)
                .findGeoId2TechLocation(geoId1.get().getId(), currentHvt.get().getHvtIdStandort());
        doReturn(geoId2TechLocation2).when(testling)
                .findGeoId2TechLocation(geoId2.get().getId(), currentHvt.get().getHvtIdStandort());

        testling.moveKvzLocationsToHvt(kvzSperre, currentHvt.get().getHvtIdStandort(), 99L, -1L);

        verify(testling).saveGeoId2TechLocation(geoId2TechLocation1, -1L);
        verify(testling).saveGeoId2TechLocation(geoId2TechLocation2, -1L);

        Assert.assertEquals(geoId2TechLocation1.getHvtIdStandort(), futureHvt.get().getHvtIdStandort());
        Assert.assertEquals(geoId2TechLocation2.getHvtIdStandort(), futureHvt.get().getHvtIdStandort());

    }

    @Test
    public void testMoveKvzLocationsToHvtWithExistingFutureHvt() throws FindException, StoreException {
        HVTStandortBuilder currentHvt = new HVTStandortBuilder().withId(98L);
        HVTStandortBuilder futureHvt = new HVTStandortBuilder().withId(99L);

        KvzSperre kvzSperre = new KvzSperreBuilder().setPersist(false).build();

        GeoIdBuilder geoId1 = new GeoIdBuilder().withId(1L).setPersist(false);
        GeoId2TechLocation geoId2TechLocation1 = new GeoId2TechLocationBuilder()
                .withGeoIdBuilder(geoId1)
                .withHvtStandortBuilder(currentHvt)
                .setPersist(false).build();

        GeoId2TechLocation geoId2TechLocation11 = new GeoId2TechLocationBuilder()
                .withGeoIdBuilder(geoId1)
                .withHvtStandortBuilder(futureHvt)
                .setPersist(false).build();

        doReturn(Arrays.asList(geoId1.get()))
                .when(testling).findGeoIdsBySearchQuery(any(GeoIdSearchQuery.class));

        doReturn(geoId2TechLocation1).when(testling)
                .findGeoId2TechLocation(geoId1.get().getId(), currentHvt.get().getHvtIdStandort());
        doReturn(geoId2TechLocation11).when(testling)
                .findGeoId2TechLocation(geoId1.get().getId(), futureHvt.get().getHvtIdStandort());

        testling.moveKvzLocationsToHvt(kvzSperre, currentHvt.get().getHvtIdStandort(), futureHvt.get().getHvtIdStandort(), -1L);

        Assert.assertEquals(geoId2TechLocation1.getHvtIdStandort(), currentHvt.get().getHvtIdStandort());
        Assert.assertEquals(geoId2TechLocation11.getHvtIdStandort(), futureHvt.get().getHvtIdStandort());

    }

}


