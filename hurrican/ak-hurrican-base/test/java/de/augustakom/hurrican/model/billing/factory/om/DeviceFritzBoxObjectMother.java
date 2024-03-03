/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2015
 */
package de.augustakom.hurrican.model.billing.factory.om;


import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceFritzBoxBuilder;

public class DeviceFritzBoxObjectMother extends AbstractTaifunObjectMother {

    public static DeviceFritzBoxBuilder createFromDevice(Device device) {
        return new DeviceFritzBoxBuilder()
                .withDeviceNo(device.getDevNo())
                .withPassphrase(randomString(12))
                .withCwmpId(String.format("00040E-C%s", randomString(11)));
    }

}
