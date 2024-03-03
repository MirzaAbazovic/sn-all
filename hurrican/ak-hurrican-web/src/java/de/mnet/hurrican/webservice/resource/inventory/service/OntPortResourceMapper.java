/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2014 15:29
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import javax.inject.*;

import de.augustakom.hurrican.model.cc.view.FTTHOntPortImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 *
 */
@Named
@ObjectsAreNonnullByDefault
public class OntPortResourceMapper extends AbstractOltChildPortResourceMapper<FTTHOntPortImportView> {

    public static final String ONT_PORT_RESOURCE_SPEC_ID = "ontport-1";

    @Override
    protected String getResourceSpecId() {
        return ONT_PORT_RESOURCE_SPEC_ID;
    }

    @Override
    protected FTTHOntPortImportView createOltChild() {
        return new FTTHOntPortImportView();
    }

    @Override
    protected void generateOltChildPort(FTTHOntPortImportView ftthOntPortImportView, Long sessionId) throws StoreException {
        fttxHardwareService.generateFTTHOntPort(ftthOntPortImportView, sessionId);
    }

}
