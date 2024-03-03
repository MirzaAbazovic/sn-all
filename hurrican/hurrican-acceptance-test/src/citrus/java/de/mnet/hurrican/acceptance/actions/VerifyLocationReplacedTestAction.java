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
public class VerifyLocationReplacedTestAction extends AbstractTestAction {

    private GeoIdDAO geoIdDAO;
    private Long oldGeoId;
    private Long newGeoId;

    public VerifyLocationReplacedTestAction(GeoIdDAO geoIdDAO, Long oldGeoId, Long newGeoId) {
        this.geoIdDAO = geoIdDAO;
        this.oldGeoId = oldGeoId;
        this.newGeoId = newGeoId;
    }

    @Override
    public void doExecute(TestContext testContext) {
        GeoId oldGeoId = geoIdDAO.findLocation(GeoId.class, this.oldGeoId);
        Assert.assertNotNull(oldGeoId);
        Long replacedById = oldGeoId.getReplacedById();
        if (!newGeoId.equals(replacedById)) {
            throw new CitrusRuntimeException(String.format("ReplacedBy GEO_ID not equal. Expected: %s, but found: %s", newGeoId, replacedById));
        }
    }
}
