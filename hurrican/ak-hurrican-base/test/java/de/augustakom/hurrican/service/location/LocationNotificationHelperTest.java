/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2013 16:53:50
 */
package de.augustakom.hurrican.service.location;

import java.util.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoIdCity;
import de.augustakom.hurrican.model.cc.GeoIdCountry;
import de.augustakom.hurrican.model.cc.GeoIdDistrict;
import de.augustakom.hurrican.model.cc.GeoIdLocation;
import de.augustakom.hurrican.model.cc.GeoIdStreetSection;
import de.augustakom.hurrican.model.cc.GeoIdZipCode;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.esb.cdm.resource.location.v1.Building;
import de.mnet.esb.cdm.resource.location.v1.City;
import de.mnet.esb.cdm.resource.location.v1.Country;
import de.mnet.esb.cdm.resource.location.v1.District;
import de.mnet.esb.cdm.resource.location.v1.StreetSection;
import de.mnet.esb.cdm.resource.location.v1.ZipCode;
import de.mnet.esb.cdm.resource.locationnotificationservice.v1.NotifyReplaceLocation;
import de.mnet.esb.cdm.resource.locationnotificationservice.v1.NotifyUpdateLocation;

@Test(groups = { BaseTest.SERVICE })
public class LocationNotificationHelperTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private LocationNotificationHelper cut;

    protected StreetSection streetSection(long id) {
        StreetSection ss = new StreetSection();
        ss.setId(id);
        ss.setModified(new Date());
        ss.setName("Street");
        ss.setZipCode(zipCode(id * 100));
        return ss;
    }

    protected ZipCode zipCode(long id) {
        ZipCode zipCode = new ZipCode();
        zipCode.setId(id);
        zipCode.setModified(new Date());
        zipCode.setZipCode("12345");
        zipCode.setCity(city(id * 100));
        return zipCode;
    }

    protected City city(long id) {
        City city = new City();
        city.setId(id);
        city.setModified(new Date());
        city.setName("City");
        city.setCountry(country(id * 100));
        return city;
    }

    protected Country country(long id) {
        Country country = new Country();
        country.setId(id);
        country.setModified(new Date());
        country.setName("Country");
        return country;
    }

    protected District district(long id) {
        District d = new District();
        d.setId(id);
        d.setModified(new Date());
        d.setName("OT");
        return d;
    }

    protected Building building(long id) {
        Building b = new Building();
        b.setId(id);
        b.setHouseNumber("123");
        b.setAgsn("agsn");
        b.setModified(new Date());
        return b;
    }



    public void testUpdateBuilding() throws StoreException, FindException {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        Building building = building(1);
        building.setHouseNumberExtension("ext");
        in.setBuilding(building);
        StreetSection streetSection = streetSection(55);
        in.getBuilding().setStreet(streetSection);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, true, 1L);

        GeoId result = (GeoId) updateResult.getFirst();
        Assert.assertNotNull(result);
        assertDatesAlmostEqual(result.getModified(), streetSection.getModified());
        Assert.assertEquals(result.getHouseNumExtension(), building.getHouseNumberExtension());
        Assert.assertNotNull(result.getStreetSection());
        Assert.assertEquals(result.getStreetSection().getName(), in.getBuilding().getStreet().getName());
    }

    public void testUpdateBuildingNoCreation() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        Building building = building(-11);
        in.setBuilding(building);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, false, 1L);
        Assert.assertNull(updateResult.getFirst());
    }


    public void testUpdateStreetSection() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        StreetSection streetSection = streetSection(1);
        streetSection.setName(RandomStringUtils.randomAlphabetic(10));
        in.setStreetSection(streetSection);
        District district = district(99);
        district.setName(RandomStringUtils.randomAlphabetic(10));
        in.getStreetSection().setDistrict(district);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, true, 1L);

        GeoIdStreetSection result = (GeoIdStreetSection) updateResult.getFirst();
        Assert.assertNotNull(result);
        assertDatesAlmostEqual(result.getModified(), streetSection.getModified());
        Assert.assertEquals(result.getName(), streetSection.getName());
        Assert.assertNotNull(result.getDistrict());
        Assert.assertEquals(result.getDistrict().getName(), in.getStreetSection().getDistrict().getName());
    }

    public void testUpdateStreetSectionNoCreation() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        StreetSection streetSection = streetSection(-1);
        in.setStreetSection(streetSection);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, false, 1L);
        Assert.assertNull(updateResult.getFirst());
    }

    public void testUpdateZipCode() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        ZipCode zipCode = zipCode(2);
        zipCode.setZipCode(RandomStringUtils.randomNumeric(5));
        in.setZipCode(zipCode);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, true, 1L);

        GeoIdZipCode result = (GeoIdZipCode) updateResult.getFirst();
        Assert.assertNotNull(result);
        assertDatesAlmostEqual(result.getModified(), zipCode.getModified());
        Assert.assertEquals(result.getZipCode(), zipCode.getZipCode());
    }

    public void testUpdateZipCodeNoCreation() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        ZipCode zipCode = zipCode(-1);
        in.setZipCode(zipCode);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, false, 1L);
        Assert.assertNull(updateResult.getFirst());
    }

    public void testUpdateCity() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        City city = city(3);
        city.setName(RandomStringUtils.randomAlphabetic(10));
        in.setCity(city);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, true, 1L);

        GeoIdCity result = (GeoIdCity) updateResult.getFirst();
        Assert.assertNotNull(result);
        assertDatesAlmostEqual(result.getModified(), city.getModified());
        Assert.assertEquals(result.getName(), city.getName());
    }

    public void testUpdateCityNoCreation() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        City city = city(-2);
        in.setCity(city);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, false, 1L);
        Assert.assertNull(updateResult.getFirst());
    }

    public void testUpdateCountry() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        Country country = country(4);
        in.setCountry(country);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, true, 1L);

        GeoIdCountry result = (GeoIdCountry) updateResult.getFirst();
        Assert.assertNotNull(result);
        assertDatesAlmostEqual(result.getModified(), country.getModified());
        Assert.assertEquals(result.getName(), country.getName());
    }

    public void testUpdateCountryNoCreation() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        Country country = country(-4);
        in.setCountry(country);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, false, 1L);
        Assert.assertNull(updateResult.getFirst());
    }

    public void testUpdateDistrict() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        District district = district(5);
        district.setName(RandomStringUtils.randomAlphabetic(10));
        in.setDistrict(district);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, true, 1L);

        GeoIdDistrict result = (GeoIdDistrict) updateResult.getFirst();
        Assert.assertNotNull(result);
        assertDatesAlmostEqual(result.getModified(), district.getModified());
        Assert.assertEquals(result.getName(), district.getName());
    }

    public void testUpdateDistrictNoCreation() throws Exception {
        NotifyUpdateLocation in = new NotifyUpdateLocation();
        District district = district(-1);
        in.setDistrict(district);

        Pair<GeoIdLocation, AKWarnings> updateResult = cut.updateLocation(in, false, 1L);
        Assert.assertNull(updateResult.getFirst());
    }

    public void testReplaceStreetSection() throws Exception {
        NotifyReplaceLocation in = new NotifyReplaceLocation();
        in.setOldStreetSection(streetSection(1000004));
        in.setNewStreetSection(streetSection(2));

        Pair<GeoIdLocation, GeoIdLocation> replaceResult = cut.replaceLocation(in, true, 1L);

        GeoIdStreetSection old = (GeoIdStreetSection) replaceResult.getFirst();
        Assert.assertNotNull(old);
        Assert.assertEquals(old.getReplacedById(), Long.valueOf(in.getNewStreetSection().getId()));
    }

    public void testReplaceStreetSectionNoCreation() throws Exception {
        NotifyReplaceLocation in = new NotifyReplaceLocation();
        in.setOldStreetSection(streetSection(-1));
        in.setNewStreetSection(streetSection(-2));

        Pair<GeoIdLocation, GeoIdLocation> replaceResult = cut.replaceLocation(in, false, 1L);
        Assert.assertNull(replaceResult);
    }

    private static void assertDatesAlmostEqual(Date date1, Date date2) {
        Assert.assertTrue(Math.abs(date1.getTime() - date2.getTime()) < 5000,
                String.format("Dates aren't close enough to each other: date1=%s, date2=%s",
                        date1.getTime(), date2.getTime()));
    }

}
