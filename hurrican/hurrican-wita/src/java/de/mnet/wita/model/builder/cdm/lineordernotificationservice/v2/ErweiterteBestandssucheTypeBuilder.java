/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.ErweiterteBestandssucheType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.OnkzRufNrType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class ErweiterteBestandssucheTypeBuilder implements LineOrderNotificationTypeBuilder<ErweiterteBestandssucheType> {

    private OnkzDurchwahlAbfragestelleType anlagenanschluss;
    private OnkzRufNrType einzelanschluss;

    @Override
    public ErweiterteBestandssucheType build() {
        ErweiterteBestandssucheType erweiterteBestandssucheType = new ErweiterteBestandssucheType();
        erweiterteBestandssucheType.setAnlagenanschluss(anlagenanschluss);
        erweiterteBestandssucheType.setEinzelanschluss(einzelanschluss);
        return erweiterteBestandssucheType;
    }

    public ErweiterteBestandssucheTypeBuilder withAnlagenanschluss(OnkzDurchwahlAbfragestelleType anlagenanschluss) {
        this.anlagenanschluss = anlagenanschluss;
        return this;
    }

    public ErweiterteBestandssucheTypeBuilder withEinzelanschluss(OnkzRufNrType einzelanschluss) {
        this.einzelanschluss = einzelanschluss;
        return this;
    }

}
