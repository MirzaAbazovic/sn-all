package de.mnet.hurrican.webservice.resource.inventory.service;

import javax.inject.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.FTTHOntImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

@Named
public class OntResourceMapper extends AbstractOltChildResourceMapper<FTTHOntImportView, HWOnt> implements RackResourceMapper {

    public static final String ONT_RESOURCE_SPEC_ID = "ont-1";

    @Override
    String getResourceSpecId() {
        return ONT_RESOURCE_SPEC_ID;
    }

    @Override
    FTTHOntImportView createImportView(Resource resource) {
        FTTHOntImportView ftthOntImportView = new FTTHOntImportView();
        mapOltChildResource(resource, ftthOntImportView);
        return ftthOntImportView;
    }

    @Override
    Class<HWOnt> getChildHwClass() {
        return HWOnt.class;
    }

    @Override
    boolean isEqual(FTTHOntImportView oltChildView, HWOnt oltChildHw) throws FindException {
        return fttxHardwareService.isOntEqual(oltChildView, oltChildHw);
    }

    @Override
    boolean isChildHwComplete(HWOnt hwOltChild) {
        return fttxHardwareService.checkIfOntFieldsComplete(hwOltChild);
    }

    @Override
    HWOnt createChildHwFromView(FTTHOntImportView oltChildView, Long sessionId) throws StoreException {
        return fttxHardwareService.generateFTTHOnt(oltChildView, sessionId);
    }

    @Override
    String getOltChildType() {
        return HWRack.RACK_TYPE_ONT;
    }

    @Override
    public boolean isRackSupported(HWRack rack) {
        return rack != null && HWOnt.class.equals(rack.getClass());
    }

    @Override
    public Resource toResource(HWRack rack) throws ResourceProcessException {
        if (!isRackSupported(rack)) {
            throw new ResourceProcessException("Rack Verarbeitung abgelehnt!");
        }

        HWOnt ont = (HWOnt) rack;

        Resource res = new Resource();
        ResourceSpecId resSpecId = new ResourceSpecId();
        resSpecId.setId(ONT_RESOURCE_SPEC_ID);
        resSpecId.setInventory(COMMAND_INVENTORY);
        res.setResourceSpec(resSpecId);

        res.setName(ont.getGeraeteBez());
        res.setId(ont.getGeraeteBez());
        res.setInventory(COMMAND_INVENTORY);

        ResourceCharacteristic freigabe = new ResourceCharacteristic();
        freigabe.setName(CHARACTERISTIC_FREIGABE);
        freigabe.getValue().add(encodeDateTime(DateConverterUtils.asLocalDateTime(ont.getFreigabe())));
        res.getCharacteristic().add(freigabe);
        return res;
    }

    protected HWOnt updateOltChildFields(final FTTHOntImportView oltChildView, final HWOnt oltChildHw) throws StoreException,
            ValidationException {
        oltChildHw.setSerialNo(oltChildView.getSeriennummer());
        return hwService.saveHWRack(oltChildHw);
    }

}
