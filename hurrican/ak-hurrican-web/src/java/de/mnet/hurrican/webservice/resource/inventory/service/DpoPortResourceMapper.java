/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import javax.inject.*;

import de.augustakom.hurrican.model.cc.view.FTTBHDpoPortImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;

/**
 *
 */
@Named
@ObjectsAreNonnullByDefault
public class DpoPortResourceMapper extends AbstractOltChildPortResourceMapper<FTTBHDpoPortImportView> {

    public static final String DPO_PORT_RESOURCE_SPEC_ID = "dpoport-1";

    public static final String CHARACTERISTIC_LEISTE = "leiste";
    public static final String CHARACTERISTIC_STIFT = "stift";

    @Override
    protected FTTBHDpoPortImportView mapOltChildPortResource(final Resource in) {
        FTTBHDpoPortImportView out = super.mapOltChildPortResource(in);
        out.setDpo(in.getParentResource().getId());
        out.setPort(in.getName());
        for (ResourceCharacteristic characteristic : in.getCharacteristic()) {
            switch (characteristic.getName().toLowerCase()) {
                case CHARACTERISTIC_SCHNITTSTELLE:
                    out.setSchnittstelle(extractString(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_LEISTE:
                    out.setLeiste(extractString(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_STIFT:
                    out.setStift(extractString(characteristic.getValue(), 0));
                    break;
                default:
                    break;
            }
        }
        return out;
    }

    @Override
    protected String getResourceSpecId() {
        return DPO_PORT_RESOURCE_SPEC_ID;
    }

    @Override
    protected FTTBHDpoPortImportView createOltChild() {
        return new FTTBHDpoPortImportView();
    }

    @Override
    protected void generateOltChildPort(FTTBHDpoPortImportView dpoPortImportView, Long sessionId) throws StoreException {
        fttxHardwareService.generateFTTBHDpoPort(dpoPortImportView, sessionId);
    }

}
