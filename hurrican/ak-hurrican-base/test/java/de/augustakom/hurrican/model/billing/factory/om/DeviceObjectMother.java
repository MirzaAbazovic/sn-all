/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2015
 */
package de.augustakom.hurrican.model.billing.factory.om;


import java.time.*;
import java.util.*;

import de.augustakom.hurrican.model.billing.DeviceBuilder;
import de.mnet.common.tools.DateConverterUtils;

public class DeviceObjectMother extends AbstractTaifunObjectMother {

    public static DeviceBuilder createDefault() {
        return new DeviceBuilder()
                .withDeviceClass("IAD")
                .withDeviceExtension("DEVICE__FRITZBOX")
                .withDevType("AVM Fritz!Box FON WLAN 7360")
                .withManufacturer("AVM")
                .withTechName("FRITZBOX_7360")
                .withSerialNumber(String.format("E%s", randomString(40)))
                .withValidFrom(DateConverterUtils.asDate(LocalDateTime.now().minusDays(100)));
    }

}
