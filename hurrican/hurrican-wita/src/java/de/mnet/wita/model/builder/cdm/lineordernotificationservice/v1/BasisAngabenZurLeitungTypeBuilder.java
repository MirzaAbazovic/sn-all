/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.BasisAngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.LeitungsbezeichnungType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class BasisAngabenZurLeitungTypeBuilder implements LineOrderNotificationTypeBuilder<BasisAngabenZurLeitungType> {

    private LeitungsbezeichnungType leitungsbezeichnung;

    public BasisAngabenZurLeitungType build() {
        return enrich(new BasisAngabenZurLeitungType());
    }

    protected <AZLT extends BasisAngabenZurLeitungType> AZLT enrich(AZLT angabenZurLeitungType) {
        angabenZurLeitungType.setLeitungsbezeichnung(leitungsbezeichnung);
        return angabenZurLeitungType;
    }

    public BasisAngabenZurLeitungTypeBuilder withLeitungsbezeichnung(LeitungsbezeichnungType value) {
        this.leitungsbezeichnung = value;
        return this;
    }
}
