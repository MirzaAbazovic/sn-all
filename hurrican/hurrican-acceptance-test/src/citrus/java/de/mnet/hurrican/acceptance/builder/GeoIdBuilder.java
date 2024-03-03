/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2015
 */
package de.mnet.hurrican.acceptance.builder;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoIdCity;
import de.augustakom.hurrican.model.cc.GeoIdCountry;
import de.augustakom.hurrican.model.cc.GeoIdStreetSection;
import de.augustakom.hurrican.model.cc.GeoIdStreetSectionBuilder;
import de.augustakom.hurrican.model.cc.GeoIdZipCode;

/**
 * Basic builder class for creating GeoIDs
 */
public class GeoIdBuilder {

    public GeoIdBuilderResult build(StoreDAO storeDAO) {
        GeoIdStreetSection streetSection = new GeoIdStreetSectionBuilder().setPersist(false).build();
        GeoIdZipCode zipCode = streetSection.getZipCode();
        GeoIdCity city = zipCode.getCity();
        GeoIdCountry country = city.getCountry();

        storeDAO.store(country);
        storeDAO.store(city);
        storeDAO.store(zipCode);
        streetSection = storeDAO.store(streetSection);

        GeoId geoId = new de.augustakom.hurrican.model.cc.GeoIdBuilder().setPersist(false).build();
        geoId.setStreetSection(streetSection);

        geoId = storeDAO.store(geoId);

        GeoIdBuilderResult geoIdBuilderResult = new GeoIdBuilderResult();
        geoIdBuilderResult.streetSection = streetSection;
        geoIdBuilderResult.zipCode = zipCode;
        geoIdBuilderResult.city = city;
        geoIdBuilderResult.country = country;
        geoIdBuilderResult.geoId = geoId;
        return geoIdBuilderResult;
    }

    public static final class GeoIdBuilderResult {
        public GeoIdStreetSection streetSection;
        public GeoIdZipCode zipCode;
        public GeoIdCity city;
        public GeoIdCountry country;
        public GeoId geoId;
    }
}
