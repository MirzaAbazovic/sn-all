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
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

@Named
public class DpoResourceMapper  extends AbstractOltChildResourceMapper<FTTBHDpoImportView, HWDpo> implements RackResourceMapper {
    public static final String DPO_RESOURCE_SPEC_ID = "dpo-1";

    public static final String CHARACTERISTIC_CHASSISBEZEICHNUNG = "chassisbezeichung";
    public static final String CHARACTERISTIC_POSITION_IN_CHASSIS = "positioninchassis";

    @Override
    String getResourceSpecId() {
        return DPO_RESOURCE_SPEC_ID;
    }

    @Override
    FTTBHDpoImportView createImportView(Resource resource) {
        FTTBHDpoImportView fttbhDpoImportView = new FTTBHDpoImportView();
        mapOltChildResource(resource, fttbhDpoImportView);
        Optional<ResourceCharacteristic> characteristicChassisIdentifier =
                getResourceCharacteristic(resource, CHARACTERISTIC_CHASSISBEZEICHNUNG);
        if (characteristicChassisIdentifier.isPresent()) {
            fttbhDpoImportView.setChassisIdentifier(extractString(characteristicChassisIdentifier.get().getValue(), 0));
        }
        Optional<ResourceCharacteristic> characteristicChassisSlot =
                getResourceCharacteristic(resource, CHARACTERISTIC_POSITION_IN_CHASSIS);
        if (characteristicChassisSlot.isPresent()) {
            fttbhDpoImportView.setChassisSlot(extractString(characteristicChassisSlot.get().getValue(), 0));
        }
        return fttbhDpoImportView;
    }

    @Override
    Class<HWDpo> getChildHwClass() {
        return HWDpo.class;
    }

    @Override
    boolean isEqual(FTTBHDpoImportView oltChildView, HWDpo oltChildHw) throws FindException {
        return fttxHardwareService.isDpoEqual(oltChildView, oltChildHw);
    }

    @Override
    boolean isChildHwComplete(HWDpo oltChildHw) {
        return fttxHardwareService.checkIfDpoFieldsComplete(oltChildHw);
    }

    @Override
    HWDpo createChildHwFromView(FTTBHDpoImportView oltChildView, Long sessionId) throws StoreException {
        return fttxHardwareService.generateFTTBHDpo(oltChildView, sessionId);
    }

    @Override
    String getOltChildType() {
        return HWRack.RACK_TYPE_DPO;
    }

    @Override
    public boolean isRackSupported(HWRack rack) {
        return rack != null && HWDpo.class.equals(rack.getClass());
    }

    @Override
    public Resource toResource(HWRack rack) throws ResourceProcessException {
        if (!isRackSupported(rack)) {
            throw new ResourceProcessException("Rack Verarbeitung abgelehnt!");
        }

        HWDpo dpo = (HWDpo) rack;

        Resource res = new Resource();
        ResourceSpecId resSpecId = new ResourceSpecId();
        resSpecId.setId(DPO_RESOURCE_SPEC_ID);
        resSpecId.setInventory(COMMAND_INVENTORY);
        res.setResourceSpec(resSpecId);

        res.setName(dpo.getGeraeteBez());
        res.setId(dpo.getGeraeteBez());
        res.setInventory(COMMAND_INVENTORY);

        ResourceCharacteristic freigabe = new ResourceCharacteristic();
        freigabe.setName(CHARACTERISTIC_FREIGABE);
        freigabe.getValue().add(encodeDateTime(DateConverterUtils.asLocalDateTime(dpo.getFreigabe())));
        res.getCharacteristic().add(freigabe);
        return res;
    }

    protected HWDpo updateOltChildFields(final FTTBHDpoImportView oltChildView, final HWDpo oltChildHw) throws StoreException,
            ValidationException {
        oltChildHw.setSerialNo(oltChildView.getSeriennummer());
        oltChildHw.setChassisIdentifier(oltChildView.getChassisIdentifier());
        oltChildHw.setChassisSlot(oltChildView.getChassisSlot());
        return hwService.saveHWRack(oltChildHw);
    }

}
