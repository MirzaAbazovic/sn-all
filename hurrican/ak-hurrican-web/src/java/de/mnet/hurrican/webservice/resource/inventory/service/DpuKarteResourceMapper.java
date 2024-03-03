/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import javax.inject.*;

import de.augustakom.hurrican.model.cc.view.FTTBDpuKarteImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;

/**
 *
 */
@Named
@ObjectsAreNonnullByDefault
public class DpuKarteResourceMapper extends AbstractOltChildKarteResourceMapper<FTTBDpuKarteImportView> {

    public static final String DPU_KARTE_RESOURCE_SPEC_ID = "dpu-karte-1";

    public static final String CHARACTERISTIC_KARTENTYP = "kartentyp";

    @Override
    protected FTTBDpuKarteImportView mapOltChildKarteResource(final Resource in) {
        FTTBDpuKarteImportView out = super.mapOltChildKarteResource(in);
        out.setKarte(in.getName());
        out.setKarteId(in.getId());
        out.setDpu(in.getParentResource().getId());
        for (ResourceCharacteristic characteristic : in.getCharacteristic()) {
            switch (characteristic.getName().toLowerCase()) {
                case CHARACTERISTIC_KARTENTYP:
                    out.setKartentyp(extractString(characteristic.getValue(), 0));
                    break;
                default:
                    break;
            }
        }
        return out;
    }

    @Override
    protected String getResourceSpecId() {
        return DPU_KARTE_RESOURCE_SPEC_ID;
    }

    @Override
    protected FTTBDpuKarteImportView createOltChild() {
        return new FTTBDpuKarteImportView();
    }

    @Override
    protected void generateOltChildKarte(FTTBDpuKarteImportView oltChildPort, Long sessionId) throws StoreException {
        fttxHardwareService.generateFTTBDpuKarte(oltChildPort, sessionId);
    }

}
