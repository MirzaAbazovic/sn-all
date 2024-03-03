/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2011 10:27:14
 */
package de.mnet.wita.model;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.SessionFactoryAware;

@SessionFactoryAware("cc.sessionFactory")
@SuppressWarnings("unused")
public class WitaConfigBuilder extends EntityBuilder<WitaConfigBuilder, WitaConfig> {

    private String key;
    private String value;

    public WitaConfigBuilder withRequestSendDelay(String status) {
        this.key = WitaConfig.DAYS_BEFORE_SENT;
        this.value = status;
        return this;
    }
}
