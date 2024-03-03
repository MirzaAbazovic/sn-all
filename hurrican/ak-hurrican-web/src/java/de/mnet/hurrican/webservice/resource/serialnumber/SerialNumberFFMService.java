package de.mnet.hurrican.webservice.resource.serialnumber;

import de.augustakom.hurrican.service.cc.ICCService;

/**
 * Interface definition serial number transfer from ffm
 *
 */
public interface SerialNumberFFMService extends ICCService {

    /**
     * Performs update of resource characteristics
     *
     * @param resourceId Geraete Bez
     * @param serialNumber serial number
     */
    void updateResourceCharacteristics(String resourceId, String serialNumber, boolean falseRouting);
}
