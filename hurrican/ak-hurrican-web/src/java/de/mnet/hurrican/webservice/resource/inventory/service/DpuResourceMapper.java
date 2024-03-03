/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import java.util.*;
import javax.inject.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.FTTBDpuImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

@Named
public class DpuResourceMapper extends AbstractOltChildResourceMapper<FTTBDpuImportView, HWDpu> implements RackResourceMapper {
    public static final String DPU_RESOURCE_SPEC_ID = "dpu-1";

    public static final String CHARACTERISTIC_REVERSE_POWER = "reversePower";

    @Override
    String getResourceSpecId() {
        return DPU_RESOURCE_SPEC_ID;
    }

    @Override
    FTTBDpuImportView createImportView(Resource resource) {
        FTTBDpuImportView FTTBDpuImportView = new FTTBDpuImportView();
        mapOltChildResource(resource, FTTBDpuImportView);
        Optional<ResourceCharacteristic> characteristicReversePower =
                getResourceCharacteristic(resource, CHARACTERISTIC_REVERSE_POWER);
        if (characteristicReversePower.isPresent()) {
            FTTBDpuImportView.setReversePower(extractBoolean(characteristicReversePower.get().getValue(), 0));
        }
        return FTTBDpuImportView;
    }

    @Override
    Class<HWDpu> getChildHwClass() {
        return HWDpu.class;
    }

    @Override
    boolean isEqual(FTTBDpuImportView oltChildView, HWDpu oltChildHw) throws FindException {
        return fttxHardwareService.isDpuEqual(oltChildView, oltChildHw);
    }

    @Override
    boolean isChildHwComplete(HWDpu oltChildHw) {
        return fttxHardwareService.checkIfDpuFieldsComplete(oltChildHw);
    }

    @Override
    HWDpu createChildHwFromView(FTTBDpuImportView oltChildView, Long sessionId) throws StoreException {
        return fttxHardwareService.generateFTTBDpu(oltChildView, sessionId);
    }

    @Override
    String getOltChildType() {
        return HWRack.RACK_TYPE_DPU;
    }

    @Override
    public boolean isRackSupported(HWRack rack) {
        return rack != null && HWDpu.class.equals(rack.getClass());
    }

    @Override
    public Resource toResource(HWRack rack) throws ResourceProcessException {
        if (!isRackSupported(rack)) {
            throw new ResourceProcessException("Rack Verarbeitung abgelehnt!");
        }

        HWDpu dpu = (HWDpu) rack;

        Resource res = new Resource();
        ResourceSpecId resSpecId = new ResourceSpecId();
        resSpecId.setId(DPU_RESOURCE_SPEC_ID);
        resSpecId.setInventory(COMMAND_INVENTORY);
        res.setResourceSpec(resSpecId);

        res.setName(dpu.getGeraeteBez());
        res.setId(dpu.getGeraeteBez());
        res.setInventory(COMMAND_INVENTORY);

        ResourceCharacteristic freigabe = new ResourceCharacteristic();
        freigabe.setName(CHARACTERISTIC_FREIGABE);
        freigabe.getValue().add(encodeDateTime(DateConverterUtils.asLocalDateTime(dpu.getFreigabe())));
        res.getCharacteristic().add(freigabe);
        return res;
    }

    protected HWDpu updateOltChildFields(final FTTBDpuImportView oltChildView, final HWDpu oltChildHw) throws StoreException,
            ValidationException {
        oltChildHw.setSerialNo(oltChildView.getSeriennummer());
        oltChildHw.setReversePower(oltChildView.getReversePower());
        return hwService.saveHWRack(oltChildHw);
    }

}
