/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 11:17:37
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import junit.framework.Assert;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.GeoIdClarificationBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationTypeBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.model.cc.query.GeoIdClarificationQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdSearchQuery;
import de.augustakom.hurrican.model.cc.vento.availability.VentoAvailabilityInformationType;
import de.augustakom.hurrican.model.cc.vento.availability.VentoConnectionType;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationResponse;
import de.augustakom.hurrican.model.cc.vento.availability.VentoTechnologyType;
import de.augustakom.hurrican.model.cc.view.GeoIdClarificationView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ReferenceService;


@Test(groups = BaseTest.SERVICE)
public class AvailabilityServiceTest extends AbstractHurricanBaseServiceTest {

    private GeoId geoId;
    private GeoIdBuilder geoIdBuilder;
    private AvailabilityService service;
    private ReferenceService referenceService;

    static class TestGetAvailabilityInformationTypes {
        Long geoId;
        Long hvtStandortId;
        String techTypeString1;
        String techTypeString2;
        VentoConnectionType ct1;
        Long talLength;
        Long bandwidth;
        GeoId2TechLocation geoId2TechLocation;
        VentoGetAvailabilityInformationRequest request;

        AvailabilityServiceImpl availServiceMock;
        HVTService hvtServiceMock;

        HVTStandortTechType techType1;
        HVTStandortTechType techType2;

        List<HVTStandortTechType> hvtStandortTechTypes;
        List<GeoId2TechLocation> geoId2TechLocations;
    }

    @BeforeMethod
    public void initServices() {
        service = getCCService(AvailabilityService.class);
        referenceService = getCCService(ReferenceService.class);
    }


    private void buildGeoIdTestData(boolean persist) {
        geoIdBuilder = getBuilder(GeoIdBuilder.class);
        geoId = geoIdBuilder.setPersist(persist).build();
    }

    public void testFindGeoId() throws FindException {
        buildGeoIdTestData(true);
        GeoId result = service.findGeoId(geoId.getId());

        assertNotNull(result);
        assertEquals(result.getId(), geoId.getId());
        assertEquals(result.getStreet(), geoId.getStreet());
        geoId.getCarrierAddresses();
    }

    public void testFindGeoIdsByQuery() throws FindException {
        buildGeoIdTestData(true);
        GeoIdQuery query = new GeoIdQuery();
        query.setStreet(geoId.getStreet() + "*");
        query.setCity(geoId.getCity() + "*");
        query.setDistrict(geoId.getDistrict() + "*");

        List<GeoId> result = service.findGeoIdsByQuery(query);
        assertNotEmpty(result, "GeoIDs ueber Query nicht gefunden!");

        boolean found = false;
        for (GeoId resultEntry : result) {
            if (NumberTools.equal(resultEntry.getId(), geoId.getId())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Result-List enthaelt nicht das erwartete Objekt!");
    }

    public void testFindGeoIdsBySearchQuery() throws FindException {
        buildGeoIdTestData(true);
        GeoIdSearchQuery query = new GeoIdSearchQuery();
        query.setStreet(geoId.getStreet() + "*");
        query.setCity(geoId.getCity() + "*");
        query.setDistrict(geoId.getDistrict() + "*");
        query.setId(geoId.getId());

        List<GeoId> result = service.findGeoIdsBySearchQuery(query);
        assertNotEmpty(result, "Query liefert leere Liste!");
        assertTrue(result.size() == 1, "Query liefert Liste mit falscher Größe!");
        assertTrue(NumberTools.equal(result.get(0).getId(), geoId.getId()),
                "Query Liste enthaelt nicht das erwartete Objekt!");
    }

    public void testFindGeoIdsBySearchQueryWithOnkzAsb() throws FindException {
        buildGeoIdTestData(true);

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withRandomId()
                .setPersist(true);
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class).withHvtGruppeBuilder(hvtGruppeBuilder)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .setPersist(true)
                .withRandomId();

        getBuilder(GeoId2TechLocationBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withGeoIdBuilder(geoIdBuilder)
                .setPersist(true)
                .build();

        GeoIdSearchQuery query = new GeoIdSearchQuery();
        query.setStreet(geoId.getStreet() + "*");
        query.setCity(geoId.getCity() + "*");
        query.setDistrict(geoId.getDistrict() + "*");
        query.setId(geoId.getId());
        query.setOnkz(hvtGruppeBuilder.getOnkz());
        query.setAsb(hvtStandortBuilder.getAsb());

        List<GeoId> result = service.findGeoIdsBySearchQuery(query);
        assertNotEmpty(result, "Query liefert leere Liste!");
        assertTrue(result.size() == 1, "Query liefert Liste mit falscher Größe!");
        assertTrue(NumberTools.equal(result.get(0).getId(), geoId.getId()),
                "Query Liste enthaelt nicht das erwartete Objekt!");
    }

    @Test(expectedExceptions = FindException.class)
    public void testFindGeoIdsBySearchQueryInvalidQueryParamsOnlyOnkz() throws FindException {
        buildGeoIdTestData(true);
        GeoIdSearchQuery query = new GeoIdSearchQuery();
        query.setStreet(geoId.getStreet() + "*");
        query.setCity(geoId.getCity() + "*");
        query.setDistrict(geoId.getDistrict() + "*");
        query.setId(geoId.getId());
        query.setOnkz("onkz");

        service.findGeoIdsBySearchQuery(query);
    }

    @Test(expectedExceptions = FindException.class)
    public void testFindGeoIdsBySearchQueryInvalidQueryParamsOnlyAsb() throws FindException {
        buildGeoIdTestData(true);
        GeoIdSearchQuery query = new GeoIdSearchQuery();
        query.setStreet(geoId.getStreet() + "*");
        query.setCity(geoId.getCity() + "*");
        query.setDistrict(geoId.getDistrict() + "*");
        query.setId(geoId.getId());
        query.setAsb(0);

        service.findGeoIdsBySearchQuery(query);
    }

    public void testDeleteGeoId2TechLocationView() throws FindException, DeleteException {
        buildGeoIdTestData(true);
        GeoId2TechLocation toDelete = getBuilder(GeoId2TechLocationBuilder.class)
                .withGeoIdBuilder(geoIdBuilder)
                .withVdslAnHvtAvailableSince(null)
                .setPersist(true).build();

        GeoId2TechLocationView view = new GeoId2TechLocationView();
        view.setHvtIdStandort(toDelete.getHvtIdStandort());
        view.setGeoId(geoId.getId());

        service.deleteGeoId2TechLocationView(view);

        GeoId2TechLocation result = service.findGeoId2TechLocation(view.getGeoId(), view.getHvtIdStandort());
        assertNull(result, "Standort ist nicht gelöscht!");
    }

    @DataProvider
    private Object[][] findGeoId2TechLocationViewsDP() {
        return new Object[][] {
                { true },
                { false },
        };
    }

    @Test(dataProvider = "findGeoId2TechLocationViewsDP")
    public void testFindGeoId2TechLocationViews(boolean withHwSwitch) throws FindException, StoreException {
        buildGeoIdTestData(true);
        HWSwitchBuilder hwSwitchBuilder = getBuilder(HWSwitchBuilder.class);
        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class);
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class);

        HWSwitch hwSwitch = withHwSwitch ?
                hwSwitchBuilder.withName("TEST01").withType(HWSwitchType.EWSD).setPersist(true).build() : null;
        hvtGruppeBuilder.withRandomId()
                .withSwitch(hwSwitch)
                .setPersist(true);
        hvtStandortBuilder.withHvtGruppeBuilder(hvtGruppeBuilder)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .setPersist(true)
                .withRandomId();
        GeoId2TechLocation location = getBuilder(GeoId2TechLocationBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withGeoIdBuilder(geoIdBuilder)
                .setPersist(true)
                .build();

        List<GeoId2TechLocationView> views = service.findGeoId2TechLocationViews(geoId.getId());
        assertNotEmpty(views, "Liste der Standorte ist leer!");
        assertTrue(views.size() == 1, "Liste der Standorte weicht von Erwartungswert '1' ab!");
        assertTrue(NumberTools.equal(location.getGeoId(), views.get(0).getGeoId()), "Geo ID nicht identisch!");
        assertTrue(NumberTools.equal(location.getHvtIdStandort(), views.get(0).getHvtIdStandort()),
                "HVT Standort ID nicht identisch!");
        assertTrue(NumberTools.equal(location.getId(), views.get(0).getId()),
                "Standort ID (GeoId2TechLocation) nicht identisch!");
    }

    public void testSaveGeoId() throws StoreException {
        buildGeoIdTestData(false);
        service.saveGeoId(geoId);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testSaveGeoIdWithoutId() throws StoreException {
        buildGeoIdTestData(false);
        //noinspection ConstantConditions
        geoId.setId(null);
        service.saveGeoId(geoId);
    }


    public void testSaveGeoId2TechLocationMapping() throws StoreException {
        Long talLength = 1111L;
        String kvzNumber = "A028";

        buildGeoIdTestData(true);
        GeoId2TechLocation mapping = getBuilder(GeoId2TechLocationBuilder.class)
                .withGeoIdBuilder(geoIdBuilder)
                .withTalLength(talLength)
                .withKvzNumber(kvzNumber)
                .setPersist(false).build();

        service.saveGeoId2TechLocation(mapping, -1L);
        assertNotNull(mapping.getId(), "Object not saved!");
        assertNotNull(mapping.getUserW(), "User not defined!");
        assertEquals(mapping.getTalLength(), talLength);
        assertEquals(mapping.getKvzNumber(), kvzNumber);
    }

    public void testFindGeoId2TechLocations() throws FindException {
        Long talLength = 1111L;
        String kvzNumber = "A028";

        buildGeoIdTestData(true);
        GeoId2TechLocation mapping = getBuilder(GeoId2TechLocationBuilder.class)
                .withGeoIdBuilder(geoIdBuilder)
                .withTalLength(talLength)
                .withKvzNumber(kvzNumber)
                .setPersist(true).build();

        List<GeoId2TechLocation> result = service.findGeoId2TechLocations(geoId.getId());
        assertNotEmpty(result, "Kein Mapping gefunden!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), mapping.getId());
    }

    private void prepareGetAvailabilityInformationTypes(TestGetAvailabilityInformationTypes values, GeoId geoIdRef)
            throws FindException {
        // test params
        values.geoId = 1L;
        values.hvtStandortId = 20L;
        values.techTypeString1 = "ADSL";
        values.techTypeString2 = "SDSL";
        values.ct1 = VentoConnectionType.TAL_HVT;
        values.talLength = 1685L;
        values.bandwidth = 25000L;
        values.geoId2TechLocation = new GeoId2TechLocation();
        values.geoId2TechLocation.setTalLength(values.talLength);
        values.geoId2TechLocation.setHvtIdStandort(values.hvtStandortId);
        values.request = new VentoGetAvailabilityInformationRequest();
        values.request.setGeoId(values.geoId);
        if (geoIdRef != null) {
            geoIdRef.setId(values.geoId);
        }

        values.availServiceMock = Mockito.mock(AvailabilityServiceImpl.class);
        values.hvtServiceMock = Mockito.mock(HVTService.class);
        Mockito.when(values.availServiceMock.getHvtService()).thenReturn(values.hvtServiceMock);

        // mock the HVTStandortTechTypes
        values.techType1 = Mockito.mock(HVTStandortTechType.class);
        values.techType2 = Mockito.mock(HVTStandortTechType.class);
        Mockito.when(values.techType1.getTechnologyTypeName()).thenReturn(values.techTypeString1);
        Mockito.when(values.techType2.getTechnologyTypeName()).thenReturn(values.techTypeString2);
        values.hvtStandortTechTypes = new ArrayList<>();
        values.hvtStandortTechTypes.add(values.techType1);
        values.hvtStandortTechTypes.add(values.techType2);

        // mock GeoId2TechLocations
        values.geoId2TechLocations = new ArrayList<>();
        values.geoId2TechLocations.add(values.geoId2TechLocation);

        // mock helper methods
        Mockito.when(values.hvtServiceMock.findTechTypes4HVTStandort(values.hvtStandortId)).thenReturn(values.hvtStandortTechTypes);
        Mockito.when(values.hvtServiceMock.findHVTStandort(values.hvtStandortId)).thenReturn(new HVTStandort());
        Mockito.when(values.availServiceMock.findGeoId(values.geoId)).thenReturn(geoIdRef);
        Mockito.when(values.availServiceMock.findGeoId2TechLocations(values.geoId)).thenReturn(values.geoId2TechLocations);
        Mockito.when(values.availServiceMock.getConnectionTypeForHVTStandort(Mockito.any(HVTStandort.class))).thenReturn(values.ct1);
        Mockito.when(values.availServiceMock.getBandwidthForTechnology(Mockito.any(VentoTechnologyType.class), Mockito.any(GeoId2TechLocation.class)))
                .thenReturn(values.bandwidth);

        // define real methods
        Mockito.doCallRealMethod().when(values.availServiceMock).getAvailabilityInformation(values.request);
        Mockito.doCallRealMethod().when(values.availServiceMock).getAvailabilityInformationTypes(values.geoId);
        Mockito.doCallRealMethod().when(values.availServiceMock).getTechnologyTypeForTechType(values.techType1);
        Mockito.doCallRealMethod().when(values.availServiceMock).getTechnologyTypeForTechType(values.techType2);
    }

    public void testGetAvailabilityInformation() throws FindException {

        TestGetAvailabilityInformationTypes values = new TestGetAvailabilityInformationTypes();
        prepareGetAvailabilityInformationTypes(values, new GeoId());

        // test
        //@formatter:off
        VentoGetAvailabilityInformationResponse response = values.availServiceMock.getAvailabilityInformation(values.request);
        assertEquals(response.getAvailabilityInformationTypes().size(), 2);

        VentoAvailabilityInformationType ventoAvailabilityInformationType = response.getAvailabilityInformationTypes().get(0);
        assertEquals(ventoAvailabilityInformationType.getTechnology().toString(), values.techTypeString1);
        assertEquals(ventoAvailabilityInformationType.getConnection(), values.ct1);
        assertEquals(ventoAvailabilityInformationType.getDistanceInMeters().intValue(), values.talLength.intValue());
        assertEquals(ventoAvailabilityInformationType.getDistanceApproved(), Boolean.FALSE); // nicht gesicherte Leitungslaenge
        assertEquals(ventoAvailabilityInformationType.getMaxDownstreamBandwidthInKB().intValue(), values.bandwidth.intValue());
        //@formatter:on
    }

    public void testGetAvailabilityInformationTrusted() throws FindException {

        TestGetAvailabilityInformationTypes values = new TestGetAvailabilityInformationTypes();
        prepareGetAvailabilityInformationTypes(values, new GeoId());
        values.geoId2TechLocation.setTalLengthTrusted(Boolean.TRUE);

        // test
        //@formatter:off
        VentoGetAvailabilityInformationResponse response = values.availServiceMock.getAvailabilityInformation(values.request);
        assertEquals(response.getAvailabilityInformationTypes().size(), 2);

        VentoAvailabilityInformationType ventoAvailabilityInformationType = response.getAvailabilityInformationTypes().get(0);
        assertEquals(ventoAvailabilityInformationType.getTechnology().toString(), values.techTypeString1);
        assertEquals(ventoAvailabilityInformationType.getConnection(), values.ct1);
        assertEquals(ventoAvailabilityInformationType.getDistanceInMeters().intValue(), values.talLength.intValue());
        assertEquals(ventoAvailabilityInformationType.getDistanceApproved(), Boolean.TRUE); // gesicherte Leitungslaenge
        assertEquals(ventoAvailabilityInformationType.getMaxDownstreamBandwidthInKB().intValue(), values.bandwidth.intValue());
        //@formatter:on
    }

    @Test
    public void testGetAvailabilityInformationGeoIdNotFound() throws FindException {

        TestGetAvailabilityInformationTypes values = new TestGetAvailabilityInformationTypes();
        prepareGetAvailabilityInformationTypes(values, null);

        // test
        VentoGetAvailabilityInformationResponse availabilityInformation = values.availServiceMock.getAvailabilityInformation(values.request);
        //noinspection deprecation
        Assert.assertTrue(availabilityInformation.getAvailabilityInformationTypes().isEmpty());
    }

    public void testFindGeoIdClarificationsByStatus() throws FindException {
        GeoIdClarificationBuilder geoIdClarificationBuilder = getBuilder(GeoIdClarificationBuilder.class).setPersist(true);

        Reference statusOpen = referenceService.findReference(GeoIdClarification.Status.OPEN.getRefId());
        Reference statusSolved = referenceService.findReference(GeoIdClarification.Status.SOLVED.getRefId());
        Reference statusClosed = referenceService.findReference(GeoIdClarification.Status.CLOSED.getRefId());
        Reference statusInProgress = referenceService.findReference(GeoIdClarification.Status.IN_PROGRESS.getRefId());
        Reference typeOnkzAsb = referenceService.findReference(GeoIdClarification.Type.ONKZ_ASB.getRefId());
        Reference typeKvzDifferent = referenceService.findReference(GeoIdClarification.Type.KVZ_DIFFERENT.getRefId());
        assertNotNull(statusOpen, "Referenz zu Status 'OPEN' konnte nicht ermittelt werden!");
        assertNotNull(statusSolved, "Referenz zu Status 'SOLVED' konnte nicht ermittelt werden!");
        assertNotNull(statusClosed, "Referenz zu Status 'CLOSED' konnte nicht ermittelt werden!");
        assertNotNull(statusInProgress, "Referenz zu Status 'IN_PROGRESS' konnte nicht ermittelt werden!");
        assertNotNull(typeOnkzAsb, "Referenz zu Typ 'ONKZ_ASB' konnte nicht ermittelt werden!");
        assertNotNull(typeKvzDifferent, "Referenz zu Typ 'KVZ_DIFFERENT' konnte nicht ermittelt werden!");

        geoIdClarificationBuilder.withType(typeOnkzAsb);
        GeoIdClarification geoIdClarificationOpen = geoIdClarificationBuilder.withStatus(statusOpen)
                .build();
        geoIdClarificationBuilder.withStatus(statusSolved)
                .build();
        geoIdClarificationBuilder.withStatus(statusClosed)
                .withType(typeKvzDifferent)
                .build();
        geoIdClarificationBuilder.withStatus(statusInProgress)
                .build();

        List<GeoIdClarification.Status> states = new ArrayList<>();
        states.add(GeoIdClarification.Status.OPEN);

        List<GeoIdClarification> result = service.findGeoIdClarificationsByStatus(states, null);

        assertNotNull(result, "Ergebnismenge ist null!");
        boolean openClarificationFound = false;
        for (GeoIdClarification geoIdClarification : result) {
            assertEquals(geoIdClarification.getStatus().getId(), statusOpen.getId(), "Ergebnismenge enthält Klärungen mit"
                    + " falschem Status!");
            if (geoIdClarification.getId().equals(geoIdClarificationOpen.getId())) {
                openClarificationFound = true;
            }
        }
        assertTrue(openClarificationFound, "Offene Klärung ist nicht in Ergebnismenge!");
    }

    public void testFindGeoIdClarificationViewsByStatus() throws FindException {
        GeoIdClarificationBuilder geoIdClarificationBuilder = getBuilder(GeoIdClarificationBuilder.class).setPersist(true);

        Reference statusOpen = referenceService.findReference(GeoIdClarification.Status.OPEN.getRefId());
        Reference statusSolved = referenceService.findReference(GeoIdClarification.Status.SOLVED.getRefId());
        Reference statusClosed = referenceService.findReference(GeoIdClarification.Status.CLOSED.getRefId());
        Reference statusInProgress = referenceService.findReference(GeoIdClarification.Status.IN_PROGRESS.getRefId());
        Reference typeOnkzAsb = referenceService.findReference(GeoIdClarification.Type.ONKZ_ASB.getRefId());
        Reference typeKvzDifferent = referenceService.findReference(GeoIdClarification.Type.KVZ_DIFFERENT.getRefId());
        assertNotNull(statusOpen, "Referenz zu Status 'OPEN' konnte nicht ermittelt werden!");
        assertNotNull(statusSolved, "Referenz zu Status 'SOLVED' konnte nicht ermittelt werden!");
        assertNotNull(statusClosed, "Referenz zu Status 'CLOSED' konnte nicht ermittelt werden!");
        assertNotNull(statusInProgress, "Referenz zu Status 'IN_PROGRESS' konnte nicht ermittelt werden!");
        assertNotNull(typeOnkzAsb, "Referenz zu Typ 'ONKZ_ASB' konnte nicht ermittelt werden!");
        assertNotNull(typeKvzDifferent, "Referenz zu Typ 'KVZ_DIFFERENT' konnte nicht ermittelt werden!");

        geoIdClarificationBuilder.withType(typeOnkzAsb);
        GeoIdClarification geoIdClarificationOpen = geoIdClarificationBuilder.withStatus(statusOpen)
                .build();
        geoIdClarificationBuilder.withStatus(statusSolved)
                .build();
        geoIdClarificationBuilder.withStatus(statusClosed)
                .withType(typeKvzDifferent)
                .build();
        geoIdClarificationBuilder.withStatus(statusInProgress)
                .build();

        List<GeoIdClarification.Status> states = new ArrayList<>();
        states.add(GeoIdClarification.Status.OPEN);

        List<GeoIdClarificationView> result = service.findGeoIdClarificationViewsByStatus(states);

        assertNotNull(result, "Ergebnismenge ist null!");
        boolean openClarificationFound = false;
        for (GeoIdClarificationView geoIdClarificationView : result) {
            assertEquals(geoIdClarificationView.getStatus().getId(), statusOpen.getId(), "Ergebnismenge enthält Klärungen mit"
                    + " falschem Status!");
            if (geoIdClarificationView.getId().equals(geoIdClarificationOpen.getId())) {
                openClarificationFound = true;
            }
        }
        assertTrue(openClarificationFound, "Offene Klärung ist nicht in Ergebnismenge!");
    }

    public void testFindGeoIdClarificationViewsByQuery() throws FindException {
        GeoIdClarificationBuilder geoIdClarificationBuilder = getBuilder(GeoIdClarificationBuilder.class).setPersist(true);

        Reference statusOpen = referenceService.findReference(GeoIdClarification.Status.OPEN.getRefId());
        Reference statusSolved = referenceService.findReference(GeoIdClarification.Status.SOLVED.getRefId());
        Reference statusClosed = referenceService.findReference(GeoIdClarification.Status.CLOSED.getRefId());
        Reference statusInProgress = referenceService.findReference(GeoIdClarification.Status.IN_PROGRESS.getRefId());
        Reference typeOnkzAsb = referenceService.findReference(GeoIdClarification.Type.ONKZ_ASB.getRefId());
        Reference typeKvzDifferent = referenceService.findReference(GeoIdClarification.Type.KVZ_DIFFERENT.getRefId());
        assertNotNull(statusOpen, "Referenz zu Status 'OPEN' konnte nicht ermittelt werden!");
        assertNotNull(statusSolved, "Referenz zu Status 'SOLVED' konnte nicht ermittelt werden!");
        assertNotNull(statusClosed, "Referenz zu Status 'CLOSED' konnte nicht ermittelt werden!");
        assertNotNull(statusInProgress, "Referenz zu Status 'IN_PROGRESS' konnte nicht ermittelt werden!");
        assertNotNull(typeOnkzAsb, "Referenz zu Typ 'ONKZ_ASB' konnte nicht ermittelt werden!");
        assertNotNull(typeKvzDifferent, "Referenz zu Typ 'KVZ_DIFFERENT' konnte nicht ermittelt werden!");

        geoIdClarificationBuilder.withType(typeOnkzAsb);
        GeoIdClarification geoIdClarificationOpen = geoIdClarificationBuilder.withStatus(statusOpen)
                .build();
        geoIdClarificationBuilder.withStatus(statusSolved)
                .build();
        geoIdClarificationBuilder.withStatus(statusClosed)
                .withType(typeKvzDifferent)
                .build();
        geoIdClarificationBuilder.withStatus(statusInProgress)
                .build();

        GeoIdClarificationQuery query = new GeoIdClarificationQuery();
        List<Reference> states = new ArrayList<>();
        states.add(geoIdClarificationOpen.getStatus());
        query.setStatusList(states);
        List<Reference> types = new ArrayList<>();
        types.add(geoIdClarificationOpen.getType());
        query.setTypeList(types);
        query.setGeoId(geoIdClarificationOpen.getGeoId());

        List<GeoIdClarificationView> result = service.findGeoIdClarificationViewsByQuery(query);

        assertNotNull(result, "Ergebnismenge ist null!");
        assertTrue(result.size() == 1, "");
        assertEquals(geoIdClarificationOpen.getId(), result.get(0).getId(), "Offene Klärung ist nicht in Ergebnismenge!");
    }

    public void testfindGeoIdClarificationById() throws FindException {
        GeoIdClarificationBuilder geoIdClarificationBuilder = getBuilder(GeoIdClarificationBuilder.class).setPersist(true);

        Reference statusOpen = referenceService.findReference(GeoIdClarification.Status.OPEN.getRefId());
        Reference typeOnkzAsb = referenceService.findReference(GeoIdClarification.Type.ONKZ_ASB.getRefId());
        assertNotNull(typeOnkzAsb, "Referenz zu Typ 'ONKZ_ASB' konnte nicht ermittelt werden!");

        geoIdClarificationBuilder.withType(typeOnkzAsb);
        GeoIdClarification geoIdClarificationOpen = geoIdClarificationBuilder.withStatus(statusOpen)
                .build();

        GeoIdClarification result = service.findGeoIdClarificationById(geoIdClarificationOpen.getId());

        assertNotNull(result, "Klärfall nicht gefunden!");
    }

    @Test(expectedExceptions = FindException.class)
    public void testFindGeoIdClarificationsByQueryInvalidParamTo() throws FindException {
        GeoIdClarificationQuery query = new GeoIdClarificationQuery();
        query.setFrom(new Date());
        service.findGeoIdClarificationViewsByQuery(query);
    }

    @Test(expectedExceptions = FindException.class)
    public void testFindGeoIdClarificationsByQueryInvalidParamFrom() throws FindException {
        GeoIdClarificationQuery query = new GeoIdClarificationQuery();
        query.setTo(new Date());
        service.findGeoIdClarificationViewsByQuery(query);
    }

    public void testSaveGeoIdClarification() throws FindException, StoreException {
        Reference statusOpen = referenceService.findReference(GeoIdClarification.Status.OPEN.getRefId());
        Reference statusSolved = referenceService.findReference(GeoIdClarification.Status.SOLVED.getRefId());
        Reference statusClosed = referenceService.findReference(GeoIdClarification.Status.CLOSED.getRefId());
        Reference typeOnkzAsb = referenceService.findReference(GeoIdClarification.Type.ONKZ_ASB.getRefId());
        assertNotNull(statusOpen, "Referenz zu Status 'open' konnte nicht ermittelt werden!");
        assertNotNull(statusSolved, "Referenz zu Status 'solved' konnte nicht ermittelt werden!");
        assertNotNull(statusClosed, "Referenz zu Status 'closed' konnte nicht ermittelt werden!");
        assertNotNull(typeOnkzAsb, "Referenz zu Typ 'ONKZ_ASB' konnte nicht ermittelt werden!");

        GeoIdClarification toSave = getBuilder(GeoIdClarificationBuilder.class).setPersist(false)
                .withStatus(statusOpen)
                .withType(typeOnkzAsb)
                .build();

        service.saveGeoIdClarification(toSave, -1L);
        assertNotNull(toSave.getId(), "Insert Statement der Klärung fehlgeschlagen!");

        toSave.setStatus(statusClosed);
        service.saveGeoIdClarification(toSave, -1L);
    }


    public void testFindPossibleGeoId2TechLocations() throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class);
        getBuilder(Produkt2TechLocationTypeBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)
                .withPriority(1)
                .build();
        getBuilder(Produkt2TechLocationTypeBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withPriority(2)
                .build();

        GeoIdBuilder geoIdBuilder = getBuilder(GeoIdBuilder.class);
        GeoId2TechLocation geoIdMapping1 = getBuilder(GeoId2TechLocationBuilder.class)
                .withGeoIdBuilder(geoIdBuilder)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class).withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ))
                .withKvzNumber("A001")
                .build();
        GeoId2TechLocation geoIdMapping2 = getBuilder(GeoId2TechLocationBuilder.class)
                .withGeoIdBuilder(geoIdBuilder)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class).withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT))
                .build();

        List<GeoId2TechLocation> possibleMappings = service.findPossibleGeoId2TechLocations(geoIdBuilder.get(), produktBuilder.get().getId());
        assertNotEmpty(possibleMappings, "kein Result gefunden!");
        assertEquals(possibleMappings.get(0).getHvtIdStandort(), geoIdMapping1.getHvtIdStandort());
        assertEquals(possibleMappings.get(1).getHvtIdStandort(), geoIdMapping2.getHvtIdStandort());
    }


    @DataProvider(name = "dataProviderTestImportStrasseMissingFileName")
    protected Object[][] dataProviderMissingFileName() {
        return new Object[][] {
                { null, 123L }
        };
    }

    @Test(dataProvider = "dataProviderTestImportStrasseMissingFileName", expectedExceptions = FindException.class)
    public void testImportGeoIdsMissingFileName(String filename, Long sessionId) throws Exception {
        service.importGeoIdsAnKVZ(filename, sessionId);
    }

    @Test(expectedExceptions = FindException.class)
    public void testImportGeoIdsMissingSessionId() throws Exception {
        service.importGeoIdsAnKVZ("c:\\TEMP\\strasse-bla.xls", null);
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = Exception.class)
    public void testImportGeoIdsFileNotFound() throws Exception {
        Map<String, Object> result;
        try {
            result = service.importGeoIdsAnKVZ("IRGENDWAS", 123L);
        }
        catch (Exception e) {
            fail("Exception thrown by CUT!");
            return;
        }
        if ((result != null) && (result.size() >= 1)) {
            Exception ex = ((ArrayList<Exception>) result.get(AvailabilityService.EXCEPTION)).get(0);
            throw new Exception(ex);
        }
        fail("Exception in return value missing!");
    }
}
