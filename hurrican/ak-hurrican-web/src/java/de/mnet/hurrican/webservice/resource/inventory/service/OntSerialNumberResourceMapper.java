package de.mnet.hurrican.webservice.resource.inventory.service;

import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

public class OntSerialNumberResourceMapper extends OntResourceMapper {

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

        ResourceCharacteristic sn = new ResourceCharacteristic();
        sn.setName(CHARACTERISTIC_SERIENNUMMER);
        sn.getValue().add(ont.getSerialNo());
        res.getCharacteristic().add(sn);
        return res;
    }

}
