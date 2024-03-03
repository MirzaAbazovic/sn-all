/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import java.util.*;
import javax.inject.*;

import de.augustakom.hurrican.model.cc.view.FTTBDpuPortImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;

/**
 *
 */
@Named
@ObjectsAreNonnullByDefault
public class DpuPortResourceMapper extends AbstractOltChildPortResourceMapper<FTTBDpuPortImportView> {

    public static final String DPU_PORT1_RESOURCE_SPEC_ID = "dpuport-1";
    public static final String DPU_PORT2_RESOURCE_SPEC_ID = "dpuport-2";

    public static final List<String> resourceSpecs = Arrays.asList(DPU_PORT1_RESOURCE_SPEC_ID, DPU_PORT2_RESOURCE_SPEC_ID);

    public static final String CHARACTERISTIC_LEISTE = "leiste";
    public static final String CHARACTERISTIC_STIFT = "stift";

    @Override
    protected FTTBDpuPortImportView mapOltChildPortResource(final Resource in) {
        FTTBDpuPortImportView out = super.mapOltChildPortResource(in);
        out.setParent(in.getParentResource().getId());
        out.setPort(in.getName());
        out.setResourceSpecId(in.getResourceSpec().getId());
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
        return null;
    }

    @Override
    public boolean isResourceSupported(Resource resource) {
        if (resource.getResourceSpec() == null
                || resource.getResourceSpec().getId() == null
                || resource.getResourceSpec().getInventory() == null) {
            return false;
        }

        return DpuPortResourceMapper.resourceSpecs.contains(resource.getResourceSpec().getId())
                && COMMAND_INVENTORY.equals(resource.getResourceSpec().getInventory());
    }

    @Override
    protected FTTBDpuPortImportView createOltChild() {
        return new FTTBDpuPortImportView();
    }

    @Override
    protected void generateOltChildPort(FTTBDpuPortImportView dpuPortImportView, Long sessionId) throws StoreException {
        fttxHardwareService.generateFTTBDpuPort(dpuPortImportView, sessionId);
    }

}
