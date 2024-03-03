/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.UFAType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class UFATypeBuilder implements LineOrderNotificationTypeBuilder<UFAType> {

    private String ufaNummer;

    @Override
    public UFAType build() {
        UFAType ufaType = new UFAType();
        ufaType.setUFAnummer(ufaNummer);
        return ufaType;
    }

    public UFATypeBuilder withUfaNummer(String ufaNummer) {
        this.ufaNummer = ufaNummer;
        return this;
    }

}
