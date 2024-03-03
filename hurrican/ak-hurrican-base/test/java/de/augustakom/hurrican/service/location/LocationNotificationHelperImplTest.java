/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2011 10:39:04
 */

package de.augustakom.hurrican.service.location;

import static org.mockito.Mockito.*;

import java.lang.reflect.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.time.DateUtils;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DefaultDAO;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DefaultDeleteDAO;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.dao.cc.GeoIdDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GeoIdCity;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.GeoIdCountry;
import de.augustakom.hurrican.model.cc.GeoIdDistrict;
import de.augustakom.hurrican.model.cc.GeoIdLocation;
import de.augustakom.hurrican.model.cc.GeoIdStreetSection;
import de.augustakom.hurrican.model.cc.GeoIdZipCode;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityServiceHelper;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.GewofagService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HvtUmzugService;
import de.augustakom.hurrican.service.cc.utils.InfasInformationWrapper;
import de.mnet.esb.cdm.resource.location.v1.Building;
import de.mnet.esb.cdm.resource.location.v1.City;
import de.mnet.esb.cdm.resource.location.v1.Country;
import de.mnet.esb.cdm.resource.location.v1.District;
import de.mnet.esb.cdm.resource.location.v1.Location;
import de.mnet.esb.cdm.resource.location.v1.StreetSection;
import de.mnet.esb.cdm.resource.location.v1.ZipCode;

@Test(groups = { BaseTest.UNIT })
public class LocationNotificationHelperImplTest extends BaseTest {

    @InjectMocks
    @Spy
    LocationNotificationHelper cut;

    @Mock
    MnetWebServiceTemplate ventoWebServiceTemplate;
    @Mock
    HVTService hvtService;
    @Mock
    EndstellenService endstellenService;
    @Mock
    GewofagService gewofagService;
    @Mock
    CCAuftragService auftragService;
    @Mock
    AvailabilityServiceHelper availabilityServiceHelper;
    @Mock
    Hibernate4DefaultDeleteDAO defaultDeleteDAO;
    @Mock
    Hibernate4DefaultDAO dao;
    @Mock
    HvtUmzugService hvtUmzugService;
    @Mock
    GeoIdDAO geoIdDAO;

    private List<Pair<Building, GeoId>> modifiedLocations;

    @BeforeMethod
    public void setUp(Method method) throws FindException, StoreException {
        MockitoAnnotations.initMocks(this);

        if (method.getName().equals("testProcessMissingGeoIds")) {
            setUp4testProcessMissingGeoIds();
        }
        else if (method.getName().equals("testProcessExistingGeoIds")) {
            setUp4testProcessExistingGeoIds();
        }
    }

    static class GeoId2TechLocationExampleMatcher extends ArgumentMatcher<GeoId2TechLocation> {
        private final Long id;

        private GeoId2TechLocationExampleMatcher(Long id) {
            this.id = id;
        }

        @Override
        public boolean matches(Object object) {
            if (object instanceof GeoId2TechLocation) {
                GeoId2TechLocation geoId2TechLocation = (GeoId2TechLocation) object;
                if (id.equals(geoId2TechLocation.getGeoId())) {
                    return true;
                }
            }
            return false;
        }
    }

    private InfasInformationWrapper buildInfasInformation(String asb, String ONKZ) {
        InfasInformationWrapper info = new InfasInformationWrapper();
        info.setAsb(asb);
        info.setOnkz(ONKZ);
        return info;
    }

    private Building buildLocation(Long geoId, InfasInformationWrapper info, String kvzNummer) {
        Building location = new Building();
        location.setId(geoId);
        StreetSection ss = new StreetSection();
        ss.setName("Street");
        location.setAgsn("ags_n");
        location.setStreet(ss);
        location.setHouseNumber("1");
        location.setHouseNumberExtension("a");
        ZipCode zipCode = new ZipCode();
        zipCode.setZipCode("12345");
        ss.setZipCode(zipCode);
        City city = new City();
        city.setName("City");
        zipCode.setCity(city);
        Country country = new Country();
        country.setName("Country");
        city.setCountry(country);
        Building.TAL tal = new Building.TAL();
        tal.setAsb(info.getAsb());
        tal.setOnkz(info.getOnkz());
        tal.setKvz(kvzNummer);
        location.setTAL(tal);
        return location;
    }

    private Pair<Building, GeoId> buildPair(Building location, GeoId geoId) {
        return new Pair<>(location, geoId);
    }

    private HVTStandort buildHVTStandort(Integer asb, Long standortTypRefId, Date from, Date to) {
        return new HVTStandortBuilder().withActivation().withAsb(asb).withStandortTypRefId(standortTypRefId)
                .withGueltigVon(from).withGueltigBis(to).withRandomId().setPersist(false).build();
    }

    HVTGruppeStdView buildHVTGruppeStdView(HVTStandort hvtStandort, Integer asb, String ONKZ) {
        HVTGruppeStdView hvtGruppeStdView = new HVTGruppeStdView();
        hvtGruppeStdView.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        hvtGruppeStdView.setAsb(asb);
        hvtGruppeStdView.setOnkz(ONKZ);
        return hvtGruppeStdView;
    }

    private Pair<GeoId2TechLocation, GeoId2TechLocationView> buildGeoId2TechLocation(HVTStandort hvtStandort, GeoId geoId) {
        GeoId2TechLocation geoId2TechLocation = new GeoId2TechLocation();
        geoId2TechLocation.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        geoId2TechLocation.setGeoId(geoId.getId());

        GeoId2TechLocationView view = new GeoId2TechLocationView();
        view.setAsb(hvtStandort.getAsb());
        view.setGeoId(geoId.getId());
        view.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        view.setStandortTypRefId(hvtStandort.getStandortTypRefId());
        return Pair.create(geoId2TechLocation, view);
    }

    private Endstelle buildEndstelle(Long id) {
        Endstelle endstelle = new Endstelle();
        endstelle.setId(id);
        return endstelle;
    }

    private AuftragDaten buildAuftragDaten(Long statusId) {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setStatusId(statusId);
        return auftragDaten;
    }

    private void setUp4testProcessMissingGeoIds() throws FindException {
        // aktiver HVT Standort
        HVTStandort hvtStandortActive = buildHVTStandort(1, HVTStandort.HVT_STANDORT_TYP_HVT,
                DateTools.createDate(2000, 0, 1),
                DateTools.changeDate(new Date(), Calendar.DATE, 1));
        String onkzActive = "089";
        when(hvtService.findHVTStandort4DtagAsb(onkzActive, hvtStandortActive.getAsb(),
                hvtStandortActive.getStandortTypRefId())).thenReturn(ImmutableList.of(hvtStandortActive));
        // nicht aktiver Standort
        HVTStandort hvtStandortNotActive = buildHVTStandort(2, HVTStandort.HVT_STANDORT_TYP_HVT,
                DateTools.createDate(2000, 0, 1), DateTools.changeDate(new Date(), Calendar.DATE, -1));
        String onkzNotActive = "0821";
        when(hvtService.findHVTStandort4DtagAsb(onkzNotActive, hvtStandortNotActive.getAsb(),
                hvtStandortNotActive.getStandortTypRefId())).thenReturn(ImmutableList.of(hvtStandortNotActive));
        // KVZ Standort als zusätzliche Versorgung zum HVT (gleiche ONKZ und DTAG-ASB)
        HVTStandort kvzStandort1 = buildHVTStandort(10001, HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ,
                DateTools.createDate(2000, 0, 1), DateTools.changeDate(new Date(), Calendar.DATE, 1));
        String kvzNummer1 = "A013";
        when(hvtService.findHVTStandort4DtagAsb(onkzActive, kvzStandort1.getDTAGAsb(),
                kvzStandort1.getStandortTypRefId())).thenReturn(ImmutableList.of(kvzStandort1));
        when(hvtService.findKvzAdresse(any(Long.class), Matchers.eq(kvzNummer1))).thenReturn(new KvzAdresse());

        // KVZ Standort ohne HVT
        HVTStandort kvzStandort2 = buildHVTStandort(10085, HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ,
                DateTools.createDate(2000, 0, 1), DateTools.changeDate(new Date(), Calendar.DATE, 1));
        String onkzKvz2 = "09876";
        String kvzNummer2 = "A098";
        when(hvtService.findHVTStandort4DtagAsb(onkzKvz2, kvzStandort2.getDTAGAsb(),
                kvzStandort2.getStandortTypRefId())).thenReturn(ImmutableList.of(kvzStandort2));
        when(hvtService.findKvzAdresse(any(Long.class), Matchers.eq(kvzNummer2))).thenReturn(new KvzAdresse());

        modifiedLocations = new ArrayList<>();
        // [1] Location ohne GeoId, aktivem HVT
        InfasInformationWrapper info_1 = buildInfasInformation(hvtStandortActive.getAsb().toString(), onkzActive);
        Building location_1 = buildLocation(1L, info_1, null);
        modifiedLocations.add(buildPair(location_1, convert2GeoId(location_1)));

        // [2] Location ohne GeoId, nicht aktiver HVT
        InfasInformationWrapper info_3 = buildInfasInformation(hvtStandortNotActive.getAsb().toString(), onkzNotActive);
        Building location_3 = buildLocation(3L, info_3, null);
        modifiedLocations.add(buildPair(location_3, convert2GeoId(location_3)));

        // [3] KVZ Standort ohne GeoId => GeoId2TechLocation für hvtStandortActive und kvzStandort1 wird erzeugt
        InfasInformationWrapper info_4 = buildInfasInformation(kvzStandort1.getAsb().toString(), onkzActive);
        Building location_4 = buildLocation(4L, info_4, kvzNummer1);
        modifiedLocations.add(buildPair(location_4, convert2GeoId(location_4)));

        // [4] KVZ Standort ohne GeoId
        InfasInformationWrapper info_5 = buildInfasInformation(kvzStandort2.getAsb().toString(), onkzKvz2);
        Building location_5 = buildLocation(5L, info_5, kvzNummer2);
        modifiedLocations.add(buildPair(location_5, convert2GeoId(location_5)));
    }

    private void setUp4testProcessExistingGeoIds() throws FindException {
        // HVT Standorte
        // -------------
        HVTStandort hvtStandortActive_1 = buildHVTStandort(1, HVTStandort.HVT_STANDORT_TYP_HVT,
                DateTools.createDate(2000, 0, 1),
                DateTools.changeDate(new Date(), Calendar.DATE, 1));
        String onkzActive_1 = "089";
        when(hvtService.findHVTStandort4DtagAsb(onkzActive_1, hvtStandortActive_1.getAsb(),
                hvtStandortActive_1.getStandortTypRefId())).thenReturn(ImmutableList.of(hvtStandortActive_1));
        // -------------
        HVTStandort hvtStandortActive_2 = buildHVTStandort(2, HVTStandort.HVT_STANDORT_TYP_HVT,
                DateTools.createDate(2000, 0, 1),
                DateTools.changeDate(new Date(), Calendar.DATE, 1));
        String onkzActive_2 = "0821";
        when(hvtService.findHVTStandort4DtagAsb(onkzActive_2, hvtStandortActive_2.getAsb(),
                hvtStandortActive_2.getStandortTypRefId())).thenReturn(ImmutableList.of(hvtStandortActive_2));
        // KVZ Standort als zusätzliche Versorgung zum HVTActive1 (gleiche ONKZ und DTAG-ASB)
        HVTStandort kvzStandort1 = buildHVTStandort(10001, HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ,
                DateTools.createDate(2000, 0, 1), DateTools.changeDate(new Date(), Calendar.DATE, 1));
        // KVZ Standort als zusätzliche Versorgung zum HVTActive2 (gleiche ONKZ und DTAG-ASB)
        HVTStandort kvzStandort3 = buildHVTStandort(10002, HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ,
                DateTools.createDate(2000, 0, 1), DateTools.changeDate(new Date(), Calendar.DATE, 1));
        String kvzNummer3 = "A014";
        when(hvtService.findHVTStandort4DtagAsb(onkzActive_2, kvzStandort3.getDTAGAsb(),
                kvzStandort3.getStandortTypRefId())).thenReturn(ImmutableList.of(kvzStandort3));
        when(hvtService.findKvzAdresse(any(Long.class), Matchers.eq(kvzNummer3))).thenReturn(new KvzAdresse());

        modifiedLocations = new ArrayList<>();

        // [1] Location mit GeoId, HVT_1, Strasse weicht ab
        InfasInformationWrapper info_1 = buildInfasInformation(hvtStandortActive_1.getAsb().toString(), onkzActive_1);
        Building location_1 = buildLocation(1L, info_1, null);
        GeoId geoId_1 = convert2GeoId(location_1);
        StreetSection modified = new StreetSection();
        modified.setName("#modified#");
        modified.setZipCode(location_1.getStreet().getZipCode());
        location_1.setStreet(modified);
        Pair<GeoId2TechLocation, GeoId2TechLocationView> geoId2TechLocation_1 = buildGeoId2TechLocation(
                hvtStandortActive_1, geoId_1);
        List<GeoId2TechLocationView> listGeoId2TechLocation_1 = new ArrayList<>();
        listGeoId2TechLocation_1.add(geoId2TechLocation_1.getSecond());
        when(availabilityServiceHelper.findGeoId2TechLocationViews(geoId_1.getId())).thenReturn(
                listGeoId2TechLocation_1);
        when(availabilityServiceHelper.findGeoId2TechLocation(geoId_1.getId(), hvtStandortActive_1.getHvtIdStandort()))
                .thenReturn(geoId2TechLocation_1.getFirst());
        modifiedLocations.add(buildPair(location_1, geoId_1));

        // [2] Location mit GeoId, Wechsel von HVT_1 nach HVT_2, kein aktiver Auftrag
        InfasInformationWrapper info_3 = buildInfasInformation(hvtStandortActive_2.getAsb().toString(), onkzActive_2);
        Building location_3 = buildLocation(3L, info_3, null);
        GeoId geoId_3 = convert2GeoId(location_3);
        Pair<GeoId2TechLocation, GeoId2TechLocationView> geoId2TechLocation_3 = buildGeoId2TechLocation(
                hvtStandortActive_1, geoId_3);
        List<GeoId2TechLocationView> listGeoId2TechLocation_3 = new ArrayList<>();
        listGeoId2TechLocation_3.add(geoId2TechLocation_3.getSecond());
        when(availabilityServiceHelper.findGeoId2TechLocationViews(geoId_3.getId())).thenReturn(
                listGeoId2TechLocation_3);
        when(availabilityServiceHelper.findGeoId2TechLocation(geoId_3.getId(), hvtStandortActive_1.getHvtIdStandort()))
                .thenReturn(geoId2TechLocation_3.getFirst());
        when(endstellenService.findEndstellenByGeoId(geoId_3.getId())).thenReturn(null);
        modifiedLocations.add(buildPair(location_3, geoId_3));

        // [3] Location mit GeoId, Wechsel von HVT_1 nach HVT_2, Strasse weicht ab, aktiver Auftrag
        InfasInformationWrapper info_4 = buildInfasInformation(hvtStandortActive_2.getAsb().toString(), onkzActive_2);
        Building location_4 = buildLocation(4L, info_4, null);
        GeoId geoId_4 = convert2GeoId(location_4);
        StreetSection modified4 = new StreetSection();
        modified4.setName("#modified#");
        modified4.setZipCode(location_4.getStreet().getZipCode());
        location_4.setStreet(modified4);
        Pair<GeoId2TechLocation, GeoId2TechLocationView> geoId2TechLocation_4 = buildGeoId2TechLocation(
                hvtStandortActive_1, geoId_4);
        List<GeoId2TechLocationView> listGeoId2TechLocation_4 = new ArrayList<>();
        listGeoId2TechLocation_4.add(geoId2TechLocation_4.getSecond());
        when(availabilityServiceHelper.findGeoId2TechLocationViews(geoId_4.getId())).thenReturn(
                listGeoId2TechLocation_4);
        when(availabilityServiceHelper.findGeoId2TechLocation(geoId_4.getId(), hvtStandortActive_1.getHvtIdStandort()))
                .thenReturn(geoId2TechLocation_1.getFirst());
        Endstelle endstelle_4 = buildEndstelle(4L);
        List<Endstelle> listEndstelle_4 = new ArrayList<>();
        listEndstelle_4.add(endstelle_4);
        when(endstellenService.findEndstellenByGeoId(geoId_4.getId())).thenReturn(listEndstelle_4);
        AuftragDaten auftragDaten_4 = buildAuftragDaten(AuftragStatus.IN_BETRIEB);
        when(auftragService.findAuftragDatenByEndstelleTx(endstelle_4.getId())).thenReturn(auftragDaten_4);
        modifiedLocations.add(buildPair(location_4, geoId_4));

        // [4] KVZ Standort mit GeoId, Wechsel von KVZ Standort1 zu HVTActive2 und KvzStandort3, kein aktiver Auftrag
        InfasInformationWrapper info_5 = buildInfasInformation(kvzStandort3.getAsb().toString(), onkzActive_2);
        Building location_5 = buildLocation(5L, info_5, kvzNummer3);
        GeoId geoId_5 = convert2GeoId(location_5);
        Pair<GeoId2TechLocation, GeoId2TechLocationView> geoId2TechLocation_5_1 = buildGeoId2TechLocation(
                kvzStandort1, geoId_5);
        Pair<GeoId2TechLocation, GeoId2TechLocationView> geoId2TechLocation_5_2 = buildGeoId2TechLocation(
                hvtStandortActive_1, geoId_5);
        List<GeoId2TechLocationView> listGeoId2TechLocation_5 = new ArrayList<>();
        listGeoId2TechLocation_5.add(geoId2TechLocation_5_1.getSecond());
        listGeoId2TechLocation_5.add(geoId2TechLocation_5_2.getSecond());
        when(availabilityServiceHelper.findGeoId2TechLocationViews(geoId_5.getId())).thenReturn(
                listGeoId2TechLocation_5);
        when(availabilityServiceHelper.findGeoId2TechLocation(geoId_5.getId(), kvzStandort1.getHvtIdStandort()))
                .thenReturn(geoId2TechLocation_5_1.getFirst());
        when(availabilityServiceHelper.findGeoId2TechLocation(geoId_5.getId(), hvtStandortActive_1.getHvtIdStandort()))
                .thenReturn(geoId2TechLocation_5_2.getFirst());
        when(endstellenService.findEndstellenByGeoId(geoId_5.getId())).thenReturn(null);
        modifiedLocations.add(buildPair(location_5, geoId_5));
    }

    private GeoId convert2GeoId(Building b) {
        GeoId building = new GeoId();
        setBase(b, building);
        building.setHouseNum(b.getHouseNumber());
        building.setHouseNumExtension(b.getHouseNumberExtension());
        if (b.getTAL() != null) {
            building.setOnkz(b.getTAL().getOnkz());
            building.setAsb(b.getTAL().getAsb());
            building.setKvz(b.getTAL().getKvz());
        }
        building.setAgsn(b.getAgsn());
        building.setStreetSection(convert2GeoId(b.getStreet()));
        return building;
    }

    private GeoIdStreetSection convert2GeoId(StreetSection street) {
        GeoIdStreetSection entity = new GeoIdStreetSection();
        setBase(street, entity);
        entity.setName(street.getName());
        entity.setDistrict(convert2GeoId(street.getDistrict()));
        entity.setZipCode(convert2GeoId(street.getZipCode()));
        return entity;
    }

    private GeoIdZipCode convert2GeoId(ZipCode zipCode) {
        GeoIdZipCode entity = new GeoIdZipCode();
        setBase(zipCode, entity);
        entity.setZipCode(zipCode.getZipCode());
        entity.setCity(convert2GeoId(zipCode.getCity()));
        return entity;
    }

    private GeoIdCity convert2GeoId(City city) {
        GeoIdCity entity = new GeoIdCity();
        setBase(city, entity);
        entity.setName(city.getName());
        entity.setCountry(convert2GeoId(city.getCountry()));
        return entity;
    }

    private GeoIdCountry convert2GeoId(Country country) {
        GeoIdCountry entity = new GeoIdCountry();
        setBase(country, entity);
        entity.setName(country.getName());
        return entity;
    }

    private GeoIdDistrict convert2GeoId(District district) {
        if (district == null) {
            return null;
        }
        GeoIdDistrict entity = new GeoIdDistrict();
        setBase(district, entity);
        entity.setName(district.getName());
        return entity;
    }

    private void setBase(Location location, GeoIdLocation entity) {
        entity.setId(location.getId());
        entity.setModified(location.getModified());
        entity.setTechnicalId(location.getTechnicalId());
        entity.setServiceable(location.isServiceable());
    }

    @Test
    public void testProcessMissingGeoIds() throws HurricanServiceCommandException, FindException, StoreException {
        for (Pair<Building, GeoId> location : modifiedLocations) {
            cut.processMissingGeoId(location.getSecond(), -1L, null);
        }

        // Overall invocations
        verify(availabilityServiceHelper, times(4)).saveGeoId2TechLocation(any(GeoId2TechLocation.class),
                any(Long.class));
        // Specific invocations
        verify(availabilityServiceHelper, times(1)).saveGeoId2TechLocation(
                argThat(new GeoId2TechLocationExampleMatcher(1L)), any(Long.class));
    }

    @Test
    public void testProcessExistingGeoIds() throws HurricanServiceCommandException, FindException, StoreException {
        for (Pair<Building, GeoId> location : modifiedLocations) {
            cut.processExistingGeoId(location.getSecond(), -1L, null);
        }
        // Overall invocations
        verify(availabilityServiceHelper, times(3)).saveGeoId2TechLocation(any(GeoId2TechLocation.class),
                any(Long.class));
        verify(availabilityServiceHelper, times(1)).createGeoIdClarification(any(Long.class), any(Long.class),
                any(GeoIdClarification.Type.class), any(String.class));

        // Specific invocations
        verify(availabilityServiceHelper, times(1)).saveGeoId2TechLocation(
                argThat(new GeoId2TechLocationExampleMatcher(3L)), any(Long.class));
    }

    @Test
    public void testHasActiveOrder() throws FindException {
        GeoIdBuilder geoIdBuilder = new GeoIdBuilder().withId(123L).setPersist(false);
        List<Endstelle> endstellen = Arrays.asList(
                new EndstelleBuilder().withGeoIdBuilder(geoIdBuilder).withId(1L).setPersist(false).build(),
                new EndstelleBuilder().withGeoIdBuilder(geoIdBuilder).withId(2L).setPersist(false).build());
        when(endstellenService.findEndstellenByGeoId(any())).thenReturn(endstellen);

        when(auftragService.findAuftragDatenByEndstelleTx(1L)).thenReturn(
                new AuftragDatenBuilder().withStatusId(AuftragStatus.STORNO).setPersist(false).build());
        when(auftragService.findAuftragDatenByEndstelleTx(2L)).thenReturn(
                new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).setPersist(false).build());

        Assert.assertTrue(cut.hasActiveOrder(geoIdBuilder.get()));
    }

    @Test
    /**
     * Dieser Test deckt den Sonderfall ab, wenn ein HVT auf einen neuen umgezogen werden muss und beide HVT noch aktiv
     * sind. Allerdings liegt die Prioritaet bei dem neuen Standort. Sollten also beide Standorte gezogen werden
     * und der entsprechende Kvz wurde schon umgezogen, soll der neue Standort gewaehlt werden,
     * wurde der Kvz noch nicht umgezogen, soll der alte Standort gewaehlt werden.
     */
    public void testFilterHvtStandorteIfUmzug() {
        final Long alteId = 761L;   // Sattlerstrasse
        final Long neueId = 33079L; //Fraunhoferstrasse
        final String kvzNr = "A073";
        HVTStandort alt = new HVTStandortBuilder().withId(alteId).build();
        HVTStandort neu = new HVTStandortBuilder().withId(neueId).build();
        when(hvtUmzugService.findAffectedStandorte4Umzug()).thenReturn(new HashSet<>(Arrays.asList(alteId)));
        when(hvtUmzugService.findKvz4UmzugWithStatusUmgezogen(alt, kvzNr)).thenReturn(new HashSet<>(Arrays.asList(1L)));
        List<HVTStandort> result = cut.filterHvtStandorteIfUmzug(Arrays.asList(alt, neu),kvzNr);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 1);
        Assert.assertEquals(neueId, result.get(0).getId());
    }

    @Test
    public void testFilterHvtStandorteIfNotActive() {
        HVTStandort nichtInBetrieb = new HVTStandortBuilder().withGesichertNichtInBetrieb().build();
        HVTStandort aktiv = new HVTStandortBuilder().withActivation().build();
        HVTStandort beendet = new HVTStandortBuilder().withActivation()
                .withGueltigBis(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
                .build();
        List<HVTStandort> result = cut.filterHvtStandorteIfNotActive(Arrays.asList(nichtInBetrieb, aktiv, beendet));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 1);
        Assert.assertEquals(aktiv.getId(), result.get(0).getId());
    }
}
