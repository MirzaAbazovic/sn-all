/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 15:27:30
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;

/**
 * Builder fuer das einfache Bauen von {@link HWSwitch} Instanzen.
 *
 *
 * @since Release 10
 */
@SuppressWarnings("unused")
public class HWSwitchBuilder extends AbstractCCIDModelBuilder<HWSwitchBuilder, HWSwitch> implements IServiceObject {

    private String name = randomString(30);
    private HWSwitchType type = HWSwitchType.EWSD;

    public HWSwitchBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public HWSwitchBuilder withType(HWSwitchType type) {
        this.type = type;
        return this;
    }

}
