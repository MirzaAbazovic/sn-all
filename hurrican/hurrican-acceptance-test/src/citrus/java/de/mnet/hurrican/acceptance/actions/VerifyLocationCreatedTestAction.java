/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2015
 */
package de.mnet.hurrican.acceptance.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.augustakom.hurrican.dao.cc.GeoIdDAO;
import de.augustakom.hurrican.model.cc.GeoId;

/**
 * Verifies that the requested GeoID is created and stored in the Hurrican GeoId cache.
 */
public class VerifyLocationCreatedTestAction extends AbstractTestAction {

    private GeoIdDAO geoIdDAO;
    private Long geoId;

    public VerifyLocationCreatedTestAction(GeoIdDAO geoIdDAO, Long geoId) {
        this.geoIdDAO = geoIdDAO;
        this.geoId = geoId;
    }

    @Override
    public void doExecute(TestContext testContext) {
        GeoId location = geoIdDAO.findLocation(GeoId.class, this.geoId);
        if (location == null) {
            throw new CitrusRuntimeException(String.format("GeoId %s not found in Hurrican cache!", geoId));
        }
    }

}
