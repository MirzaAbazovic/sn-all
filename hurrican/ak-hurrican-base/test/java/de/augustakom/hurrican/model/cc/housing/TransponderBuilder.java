/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2011 09:08:26
 */
package de.augustakom.hurrican.model.cc.housing;

import de.augustakom.common.model.EntityBuilder;

@SuppressWarnings("unused")
public class TransponderBuilder extends EntityBuilder<TransponderBuilder, Transponder> {

    private Long transponderId = randomLong(9999999);
    private String customerFirstName = randomString(20);
    private String customerLastName = randomString(20);

    public TransponderBuilder withTransponderId(Long transponderId) {
        this.transponderId = transponderId;
        return this;
    }

}


