/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2015
 */
package de.mnet.hurrican.acceptance.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.Assert;

import de.augustakom.hurrican.dao.cc.GeoIdDAO;
import de.augustakom.hurrican.model.cc.GeoId;

/**
 * Verifies that the old GeoID was replaced with the new GeoID by checking the old GeoID's ReplacedById
 */
public class VerifyLocationUpdatedTestAction extends AbstractTestAction {

    private GeoIdDAO geoIdDAO;
    private Long geoId;
    private final String newStreetName;

    public VerifyLocationUpdatedTestAction(GeoIdDAO geoIdDAO, Long geoId, String newStreetName) {
        this.geoIdDAO = geoIdDAO;
        this.geoId = geoId;
        this.newStreetName = newStreetName;
    }

    @Override
    public void doExecute(TestContext testContext) {
        GeoId location = geoIdDAO.findLocation(GeoId.class, this.geoId);
        Assert.assertNotNull(location);
        String streetName = location.getStreet();
        if (!newStreetName.equals(streetName)) {
            throw new CitrusRuntimeException(String.format("StreetName not equal. Expected: %s, but found: %s", newStreetName, streetName));
        }
    }
}
